package com.goldenowl.ecommerceapp.utilities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.goldenowl.ecommerceapp.ui.general.NoInternetActivity

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            if (!NetworkHelper.isNetworkAvailable(it)) {
                if (it.toString().contains(AUTH_ACTIVITY)) {
                    val intentTemp = Intent(it, NoInternetActivity::class.java)
                    intentTemp.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    it.startActivity(intentTemp)
                }
            }
        }
    }

    companion object {
        const val AUTH_ACTIVITY = "AuthActivity"
    }
}