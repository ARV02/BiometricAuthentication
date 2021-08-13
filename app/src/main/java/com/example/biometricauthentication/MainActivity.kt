package com.example.biometricauthentication

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.biometricauthentication.databinding.ActivityMainBinding
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    private val TAG = "MESSAGE"

    private var cancellationSignal:CancellationSignal? = null
    val authenticationCallback:BiometricPrompt.AuthenticationCallback
    get() = @RequiresApi(Build.VERSION_CODES.P)
    object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            notifyUser("Authentication error: $errString")
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            notifyUser("Authentication Success!")
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        biometricSupport()
        biometricAuthentication()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun biometricAuthentication(){
        binding.fingerprint.setOnClickListener {
            val biometricPrompt:BiometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Biometric Authentication")
                .setSubtitle("Fingerprint Authentication")
                .setDescription("Fingerprint Authentication")
                .setNegativeButton("Cancel", this.mainExecutor,
                    DialogInterface.OnClickListener { dialog, which ->  }).build()
            biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
        }
    }

    private fun notifyUser(message:String){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun getCancellationSignal():CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            Log.i(TAG, "Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun biometricSupport():Boolean{
        val keyguardManager:KeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as
                KeyguardManager
        if (!keyguardManager.isKeyguardSecure) {
            notifyUser("Fingerprint hs not been enabled in settings.")
            return false
        }
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint hs not been enabled in settings.")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT))
            true
        else
            true
    }
}