package com.professionalandroid.apps.pacman

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Window
import android.widget.Button
import android.widget.TextView

class Popup(context : Context, mlistener: MyDialogOKClickedListener) {
    private val dlg = Dialog(context)   //부모 액티비티의 context 가 들어감
    private lateinit var lblDesc : TextView
    private lateinit var scoretext : TextView
    private lateinit var btnOK : Button
    private lateinit var btnCancel : Button
    private var listener : MyDialogOKClickedListener = mlistener

    fun start(score:String, message : String) {
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE)   //타이틀바 제거
        dlg.setContentView(R.layout.popup)     //다이얼로그에 사용할 xml 파일을 불러옴
        dlg.setCancelable(false)    //다이얼로그의 바깥 화면을 눌렀을 때 다이얼로그가 닫히지 않도록 함
        lblDesc = dlg.findViewById(R.id.message)
        lblDesc.text = message
        scoretext = dlg.findViewById(R.id.score)
        scoretext.text = score


        btnOK = dlg.findViewById(R.id.ok)
        btnOK.setOnClickListener {
            dlg.dismiss()

            listener.onOKClicked()
        }


        btnCancel = dlg.findViewById(R.id.cancel)
        btnCancel.setOnClickListener {
            dlg.dismiss()
        }
        Log.d("test", "show")
        dlg.show()
    }



    interface MyDialogOKClickedListener {
        fun onOKClicked()
    }
}