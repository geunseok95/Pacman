package com.professionalandroid.apps.pacman

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class Popup: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup)


    }

    fun retry(view:View){
        finish()
    }

    fun mCancle(view: View){
        finish()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_OUTSIDE){
            return false
        }
        return true
    }

    override fun onBackPressed() {

    }

}