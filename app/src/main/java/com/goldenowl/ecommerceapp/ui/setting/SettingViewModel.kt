package com.goldenowl.ecommerceapp.ui.setting

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.goldenowl.ecommerceapp.core.BaseViewModel
import com.goldenowl.ecommerceapp.data.UserManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    val userManager: UserManager,
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : BaseViewModel() {
    val validNameLiveData: MutableLiveData<String> = MutableLiveData()
    val flat = MutableLiveData(0)
    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference
    fun updateName(nameText: String): MutableLiveData<Int> {
        if (!validName(nameText)) return MutableLiveData(-1)
        val user = userManager.getUser()
        user.name = nameText
        userManager.setName(nameText)
        return userManager.writeProfile(db, user)
    }

    fun updateDOB(dobText: String): MutableLiveData<Int> {
        val user = userManager.getUser()
        user.dob = dobText
        userManager.setDOB(dobText)
        return userManager.writeProfile(db, user)
    }

    private fun updateAvatar(avatarURL: String) {
        val user = userManager.getUser()
        user.avatar = avatarURL
        userManager.setAvatar(avatarURL)
        userManager.writeProfile(db, user)
    }

    fun uploadImage(filePath: Uri?, token: String) {
        if (filePath != null) {
            val ref = storageReference.child("avatar/$token")
            val uploadTask = ref.putFile(filePath)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    updateAvatar(downloadUri.toString())
                }
            }
        } else {
            toastMessage.postValue("Please Upload an Image")
        }
    }

    fun checkLoginWithFacebookOrGoogle(): Int {
        firebaseAuth.currentUser?.providerData?.forEach {
            if (it.providerId == FACEBOOK_COM) {
                return FACEBOOK

            } else if (it.providerId == GOOGLE_COM) {
                return GOOGLE
            }
        }
        return 0
    }

    private fun validName(nameText: String): Boolean {
        return validName(nameText, validNameLiveData)
    }

    companion object{
        const val FACEBOOK_COM = "facebook.com"
        const val GOOGLE_COM = "google.com"
        const val FACEBOOK = 1
        const val GOOGLE = 2
    }
}