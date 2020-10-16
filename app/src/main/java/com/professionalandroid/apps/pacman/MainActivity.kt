package com.professionalandroid.apps.pacman


import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var time = 0
    private var isRunning = false
    private var timerTask: Timer? = null
    private var index :Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

    }

//    fun floating_pop_up(){
//        val intent = Intent(this, Popup::class.java)
//        startActivity(intent)
//    }

    fun start_timer(game: Game) {

        timerTask = kotlin.concurrent.timer(period = 10) {	// timer() 호출
            time++	// period=10, 0.01초마다 time을 1씩 증가
            val sec = time / 100	// time/100, 나눗셈의 몫 (초 부분)
            val milli = time % 100	// time%100, 나눗셈의 나머지 (밀리초 부분)

            // UI조작을 위한 메서드
            runOnUiThread {
                game.sectext = "$sec"	// TextView 세팅
                game.millitext = "$milli"	// Textview 세팅
            }
        }
    }

    fun reset() {
        timerTask?.cancel()	// 안전한 호출(?.)로 timerTask가 null이 아니면 cancel() 호출
        time = 0
    }



}