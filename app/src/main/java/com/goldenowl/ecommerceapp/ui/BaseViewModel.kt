package com.goldenowl.ecommerceapp.ui

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    val toastMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)
    val dismiss = MutableLiveData(false)

    fun validName(nameText: String,validNameLiveData: MutableLiveData<String>): Boolean {
        return when {
            nameText.isBlank() -> {
                validNameLiveData.postValue("Mustn't empty")
                false
            }
            else -> {
                validNameLiveData.postValue("")
                true
            }
        }
    }

    fun validEmail(emailText: String,validNameLiveData: MutableLiveData<String>): Boolean {
        return if (emailText.isBlank()) {
            validNameLiveData.postValue("Mustn't empty")
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            validNameLiveData.postValue("Invalid Email Address")
            false
        } else {
            validNameLiveData.postValue("")
            true
        }
    }

    fun validPassword(passwordText: String,validNameLiveData: MutableLiveData<String>): Boolean {
        return if (passwordText.length < 8) {
            validNameLiveData.postValue("Minimum 8 Character Password")
            false
        } else if (!passwordText.matches(".*[A-Z].*".toRegex())) {
            validNameLiveData.postValue("Must Contain 1 Upper-case Character")
            false
        } else if (!passwordText.matches(".*[a-z].*".toRegex())) {
            validNameLiveData.postValue("Must Contain 1 Lower-case Character")
            false
        } else {
            validNameLiveData.postValue("")
            true
        }
    }
}