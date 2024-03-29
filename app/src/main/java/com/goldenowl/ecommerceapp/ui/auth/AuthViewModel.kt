package com.goldenowl.ecommerceapp.ui.auth

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.core.BaseViewModel
import com.goldenowl.ecommerceapp.core.OnSignInStartedListener
import com.goldenowl.ecommerceapp.data.User
import com.goldenowl.ecommerceapp.data.UserManager
import com.goldenowl.ecommerceapp.utilities.BLOCK_DEVICE
import com.goldenowl.ecommerceapp.utilities.DateFormat
import com.goldenowl.ecommerceapp.utilities.Hash
import com.goldenowl.ecommerceapp.utilities.USER_FIREBASE
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userManager: UserManager,
    private val googleSignInClient: GoogleSignInClient,
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) :
    BaseViewModel() {
    val userLiveData: MutableLiveData<FirebaseUser?> = MutableLiveData()
    val validNameLiveData: MutableLiveData<String> = MutableLiveData()
    val validEmailLiveData: MutableLiveData<String> = MutableLiveData()
    val validPasswordLiveData: MutableLiveData<String> = MutableLiveData()
    var remain = 3
    private val tokenDevice = MutableLiveData("")
    val isBlock = MutableLiveData(false)

    init {
        if (firebaseAuth.currentUser != null) {
            userLiveData.postValue(firebaseAuth.currentUser)
        }
    }

    fun initLogin() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            tokenDevice.postValue(it)
            getTimeBlock(it)
        }
    }

    private fun getTimeBlock(token: String) {
        db.collection(BLOCK_DEVICE)
            .document(token)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.data != null) {
                    val time = document.get(TIME) as Long - Date().time
                    if (time < 0) {
                        resetBlock()
                    } else {
                        toastMessage.postValue(
                            "$WARNING_BLOCK ${time % 60000 / 1000} second"
                        )
                        blockLogin(time)
                    }
                } else {
                    resetBlock()
                }
            }
    }

    private fun setTimeBlock() {
        if (!tokenDevice.value.isNullOrBlank()) {
            db.collection(BLOCK_DEVICE)
                .document(tokenDevice.value ?: "")
                .set(hashMapOf(TIME to (Date().time + TIME_BLOCK)))
        }
    }

    private fun deleteTimeBlock() {
        if (!tokenDevice.value.isNullOrBlank()) {
            db.collection(BLOCK_DEVICE)
                .document(tokenDevice.value ?: "")
                .delete()
        }
    }

    fun resetBlock() {
        isBlock.postValue(false)
        remain = 3
        deleteTimeBlock()
    }

    private fun blockLogin(time: Long = TIME_BLOCK.toLong()) {
        object : CountDownTimer(time, COUNT_DOWN.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                isBlock.postValue(true)
            }

            override fun onFinish() {
                resetBlock()
            }
        }.start()
    }

    fun signUp(name: String, email: String, password: String) {
        if (!validName(name) ||
            !validEmail(email) ||
            !validPassword(password)
        ) {
            return
        }
        isLoading.postValue(true)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        userLiveData.postValue(user)
                        val dob = Date()
                        val account = User(
                            name,
                            email,
                            Hash.hashSHA256(password),
                            user.uid,
                            DateFormat.dob.format(dob),
                            ""
                        )
                        userManager.addAccount(account)
                        userManager.writeProfile(db, account)
                        toastMessage.postValue(REGISTRATION_SUCCESS)
                    }
                } else {
                    checkFail(task.exception?.message.toString())
                }
                isLoading.postValue(false)
            }
    }

    fun forgotPassword(emailText: String) {
        if (!validEmail(emailText)) {
            return
        }
        if (emailText.isBlank()) {
            toastMessage.postValue("Please enter email")
        } else {
            isLoading.postValue(true)
            firebaseAuth.sendPasswordResetEmail(emailText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toastMessage.postValue("Email sent.")
                    } else {
                        toastMessage.postValue("Email invalid")
                    }
                    isLoading.postValue(false)
                }
        }
    }

    fun logIn(email: String, password: String) {
        if (!validEmail(email, validEmailLiveData) || !validPasswordLogin(password)) {
            return
        }
        isLoading.postValue(true)
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let {
                        actionLoginOrCreateFirebase(user, password)
                    }
                } else {
                    checkFail(task.exception?.message.toString())
                }
                isLoading.postValue(false)
            }
    }

    //If function hasn't password parameter mean login with social
    private fun actionLoginOrCreateFirebase(user: FirebaseUser, password: String?) {
        db.collection(USER_FIREBASE).document(user.uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documentSnapshot = task.result
                if (documentSnapshot.exists()) {
                    val account = documentSnapshot.toObject<User>()
                    account?.let {
                        if (password != null && account.password != Hash.hashSHA256(password)) {
                            account.password = Hash.hashSHA256(password)
                            userManager.writeProfile(db, account)
                        }
                        userManager.addAccount(account)
                    }
                } else {
                    if (password == null) {
                        createNewUserManagerForLoginSocial(user)
                    }
                }
                userLiveData.postValue(user)
                isLoading.postValue(false)
                toastMessage.postValue(LOGIN_SUCCESS)
            } else {
                checkFail(task.exception?.message.toString())
                isLoading.postValue(false)
            }
        }

    }

    fun isLogged(): Boolean {
        return userManager.isLogged()
    }


    fun signInWithGoogle(listener: OnSignInStartedListener) {
        listener.onSignInStarted(googleSignInClient)
    }

    // GOOGLE
    fun firebaseAuthWithGoogle(idToken: String) {
        isLoading.postValue(true)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.let {
                    actionLoginOrCreateFirebase(user, null)
                }
            } else {
                checkFail(it.exception?.message.toString())
                isLoading.postValue(false)
            }
        }
    }

    private fun createNewUserManagerForLoginSocial(user: FirebaseUser) {
        val dob = Date()
        val account = User(
            user.displayName.toString(),
            user.email.toString(),
            "",
            user.uid,
            DateFormat.dob.format(dob),
            user.photoUrl.toString()
        )
        userManager.addAccount(account)
        userManager.writeProfile(db, account)
    }

    private fun checkFail(string: String) {
        remain -= 1
        if (remain == 0) {
            setTimeBlock()
            blockLogin()
        }
        if (string.contains(KEY_NO_USER)) {
            toastMessage.postValue(WARNING_NO_USER)
        } else if (string.contains(KEY_INVALID_USER)) {
            toastMessage.postValue(WARNING_INVALID_USER)
        } else if (string.contains(KEY_BLOCK)) {
            setTimeBlock()
            blockLogin()
            toastMessage.postValue("$WARNING_BLOCK ${TIME_BLOCK / 1000} second")
        } else if (string.contains(KEY_ALREADY_EMAIL)) {
            toastMessage.postValue(WARNING_ALREADY_EMAIL)
        } else {
            toastMessage.postValue("Login Failure: $string")
        }
    }

    fun validName(nameText: String): Boolean {
        return validName(nameText,validNameLiveData)
    }

    fun validEmail(emailText: String): Boolean {
        return validEmail(emailText,validEmailLiveData)
    }

    fun validPassword(passwordText: String): Boolean {
        return validPassword(passwordText,validPasswordLiveData)
    }

    private fun validPasswordLogin(passwordText: String): Boolean {
        return if (passwordText.isEmpty()) {
            validPasswordLiveData.postValue("Mustn't empty")
            false
        } else {
            validPasswordLiveData.postValue("")
            true
        }
    }

    companion object {
        const val KEY_NO_USER = "user may have been deleted"
        const val WARNING_NO_USER = "Email does not exist"
        const val KEY_INVALID_USER = "password is invalid"
        const val WARNING_INVALID_USER = "Wrong password"
        const val KEY_BLOCK = "due to many failed login attempts"
        const val WARNING_BLOCK = "The device was blocked login about"
        const val KEY_ALREADY_EMAIL = "The email address is already"
        const val WARNING_ALREADY_EMAIL = "The email address is already"
        const val LOGIN_SUCCESS = "Login Success"
        const val REGISTRATION_SUCCESS = "Registration Success"
        const val EMAIL = "email"
        const val PUBLIC_PROFILE = "public_profile"
        const val USER_FRIEND = "user_friends"
        const val TAG = "Authentication"
        const val TIME_BLOCK = 30000
        const val COUNT_DOWN = 1000
        const val TIME = "time"
    }
}