package com.professionalandroid.apps.pacman

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import kotlin.Exception

class Game(context: Context, attr: AttributeSet) : View(context, attr) {
    var scrw: Int? = null
    var scrh: Int? = null
    var xd = 0
    var yd = 0

    var count = 0
    var start = false
    var n = 0
    val p: Paint =  Paint()
    var T: GameThread? = null

    private var dirbutton1:String? = null
    private var dirbutton2:String? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.scrw = w
        this.scrh = h

        if(T == null){
            T = GameThread()
            T?.start()
            Log.d("test", "make thread")
        }

    }

    override fun onDetachedFromWindow() {
        Log.d("test", "detached")
        T?.run = false
        super.onDetachedFromWindow()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {

        val pacman = Array<Bitmap?>(4){null}

        // 팩맨 사진 넣기
        pacman[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.pacman), scrw!!/16, scrh!!/8, true)
        pacman[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.pacman_down), scrw!!/16, scrh!!/8, true)
        pacman[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.pacman_up), scrw!!/16, scrh!!/8, true)
        pacman[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.pacman_left), scrw!!/16, scrh!!/8, true)

        // 캔버스에 팩맨 그리기
        canvas?.drawBitmap(pacman[n]!!, (scrw!! *7/16 + xd).toFloat(), (scrh!!- scrh!!/8 + yd).toFloat(), null )

        val dir = Array(1){ Array<Bitmap?>(4){null} }
        var l = BitmapFactory.decodeResource(resources, R.drawable.dir)
        l = Bitmap.createScaledBitmap(l, scrw!! / 16, scrh!!/ 2, true)

        // dir 그림 자르기
        for(i in 0 until 1){
            for(j in 0 until 4){
                dir[i][j] = Bitmap.createBitmap(l, i* scrw!! / 8, j * scrh!! /8, scrw!! / 16, scrh!! / 8)
            }
        }
        //up
        canvas?.drawBitmap(dir[0][0]!!, (scrw!! / 16).toFloat(), (scrh!! - scrh!!  *3/ 8).toFloat(), null)
        //left
        canvas?.drawBitmap(dir[0][1]!!, 0.0F, (scrh!! - scrh!! / 4).toFloat(), null)
        //right
        canvas?.drawBitmap(dir[0][2]!!, (scrw!! / 8).toFloat(), (scrh!! - scrh!! /4).toFloat(), null)
        //down
        canvas?.drawBitmap(dir[0][3]!!, (scrw!! / 16).toFloat(), (scrh!! - scrh!! * 1/ 8).toFloat(), null)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("test", "touched")
        if(event?.action == MotionEvent.ACTION_DOWN || event?.action == MotionEvent.ACTION_MOVE || event?.action == MotionEvent.ACTION_POINTER_DOWN ){
           if(event.x > scrw!! /8 && event.x < scrw!! * 3 / 16 && event.y < scrh!! * 7 / 8 && event.y > scrh!! *3 /4){
               if(!start && count == 0){
                   start = true
                   dirbutton1 = "Right"
               }
               dirbutton2 = "Right"
           }

            else if(event.x > 0 && event.x < scrw!! * 1 / 16 && event.y < scrh!! * 7 / 8 && event.y > scrh!! *3 /4){
                if(!start && count == 0){
                    start = true
                    dirbutton1 = "Left"
                }
                dirbutton2 = "Left"
            }

            else if(event.x > scrw!! /16 && event.x < scrw!! / 8 && event.y < scrh!! * 3 / 4 && event.y > scrh!! * 5 /8){
                if(!start && count == 0){
                    start = true
                    dirbutton1 = "Up"
                }
                dirbutton2 = "Up"
            }

            else if(event.x > scrw!! /16 && event.x < scrw!! / 8 && event.y < scrh!! && event.y > scrh!! * 7 /8){
                if(!start && count == 0){
                    start = true
                    dirbutton1 = "Down"
                }
                dirbutton2 = "down"
            }
            else{
               start = false
           }
        }

        if(event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_POINTER_UP){
            if(event.x > scrw!! /8 && event.x < scrw!! * 3 / 16 && event.y < scrh!! * 7 / 8 && event.y > scrh!! *3 /4 ){
                start = false
            }
            else if(event.x > 0 && event.x < scrw!! * 1 / 16 && event.y < scrh!! * 7 / 8 && event.y > scrh!! *3 /4){
                start = false
            }
            else if(event.x > scrw!! /16 && event.x < scrw!! / 8 && event.y < scrh!! * 3 / 4 && event.y > scrh!! * 5 /8){
                start = false
            }
            else if(event.x > scrw!! /16 && event.x < scrw!! / 8 && event.y < scrh!! && event.y > scrh!! * 7 /8){
                start = false
            }
        }
        return true
    }

     inner class GameThread: Thread() {
        var run: Boolean = true

        override fun run() {
            while(run){
                try{
                    postInvalidate()
                    if(count == 20){
                        count = 0
                        dirbutton1 = dirbutton2
                    }
                    if(start && count == 20){
                       count += 1
                    }
                    if(count in 1..19){
                        count += 1
                    }

                    if(start && dirbutton1 == "Down" && count != 20 || !start && count in 1..19 && dirbutton1 == "Down"){
                        yd += scrh!! /80
                        n = 1
                    }
                    else if(start && dirbutton1 == "Up" && count != 20 || !start && count in 1..19 && dirbutton1 == "Up") {
                        yd -= scrh!! / 80
                        n = 2
                    }
                    else if(start && dirbutton1 == "Right" && count != 20 || !start && count in 1..19 && dirbutton1 == "Right") {
                        xd += scrw!! / 160
                        n = 0
                    }
                    else if(start && dirbutton1 == "Left" && count != 20 || !start && count in 1..19 && dirbutton1 == "Left") {
                        xd -= scrw!! / 160
                        n = 3
                    }

                    sleep(50)
                }
                catch (e: Exception){
                }
            }
        }
    }

}