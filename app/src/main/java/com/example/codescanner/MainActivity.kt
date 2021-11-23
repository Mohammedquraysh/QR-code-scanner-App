package com.example.codescanner

import android.app.PendingIntent.getActivity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import kotlinx.android.synthetic.main.activity_main.*

private const val CAMERA_REQUEST_CODE = 101
class MainActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpPermission()
        codeScanner()
    }

    private fun codeScanner(){
        codeScanner = CodeScanner(this,scanner_view)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_FRONT
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false


            decodeCallback = DecodeCallback {
                runOnUiThread {
                    textview.text = it.text
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.d("Main", "Camera initialization error, ${it.message}")
                }
            }
        }


        // this listener should be called if scanMode is set to anything else other than CONTINUOUS and this make you click the scanner before you scan any code
        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    // when the app is pause and come to onResume state it'll will automatically tell the user to rescan the code
    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        // this will help against memory leak
        codeScanner.releaseResources()
    }

    private fun setUpPermission(){
        val permission = ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
           CAMERA_REQUEST_CODE -> {
               if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                   Toast.makeText(this,"You need the camera permission to be able to use this app", Toast.LENGTH_SHORT).show()
               }else{
                   Toast.makeText(this,"Successful", Toast.LENGTH_SHORT).show()
               }
           }
       }
    }
}