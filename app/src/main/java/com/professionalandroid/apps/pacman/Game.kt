package com.professionalandroid.apps.pacman

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.Exception
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

class Game(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    var scrw: Int? = null
    var scrh: Int? = null

    var sectext = "00"
    var millitext = "00"


    // pacman의 좌표
    var xd:Float = 0.0F
    var yd:Float = 0.0F
    // pacman의 이동을 위한 카운트 수를 저장할 정수형 변수
    var count = 0
    // pacman의 방향키 버튼 클릭 유무
    var start = false
    // pacman의 현재 위치
    var pacman_present_location = FloatArray(2){0.0F}
    // pacman life
    var life = 3
    // monster의 x 좌표 값을 저장할 실수형 변수
    val rxd = FloatArray(4){0.0F}
    // monster의 y 좌표 값을 저장할 실수형 변수
    val ryd = FloatArray(4){0.0F}
    // monster의 이동을 위한 카운트 수를 저장할 정수형 변수
    val count2 = IntArray(4){0}
    // monster의 이동 방향을 저장할 문자열 변수
    private val monsterdirbutton = Array<String?>(4){null}
    // monster의 현재 위치
    var monster_present_location = Array(4){FloatArray(2){0.0F}}


    var n = 0
    val p: Paint =  Paint()
    var pacmanThread: PacmanThread? = null
    var monsterThread: MonsterThread? = null

    // 캐릭터가 정지 상태에 있는 동안 어떤 방향키를 클릭했는지 저장할 문자열 변수
    private var dirbutton1:String? = null
    // 캐릭터가 이동하고 있는 상태에서 어떤 방향키를 클릭했는지 저장할 문자열 변수
    private var dirbutton2:String? = null

    var mp: MediaPlayer = MediaPlayer.create(context, R.raw.pacman_coin)

    // pacman 사진을 담을 Bitmap 배열
    val pacman = Array<Bitmap?>(4){null}
    // monster 사진을 담을 Bitmap 변수
    val monster = Array<Bitmap?>(4){null}

    // 화살표 사진을 담을 배열
    val dir = Array(1){ Array<Bitmap?>(4){null} }

    init {
        mp.start()
        mp.isLooping = true
    }

    val paint = Paint()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.scrw = w
        this.scrh = h

        paint.strokeWidth = 5f
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK

        // pacman 사진 넣기
        pacman[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.pacman), scrw!!/16, scrh!!/8, true)
        pacman[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.pacman_down), scrw!!/16, scrh!!/8, true)
        pacman[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.pacman_up), scrw!!/16, scrh!!/8, true)
        pacman[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.pacman_left), scrw!!/16, scrh!!/8, true)
        // monster 사진 넣기
        monster[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.blue_monster), scrw!!/16, scrh!!/8, true)
        monster[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.millitary_monster), scrw!!/16, scrh!!/8, true)
        monster[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.yellow_monster), scrw!!/16, scrh!!/8, true)
        monster[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.red_monster), scrw!!/16, scrh!!/8, true)

        // 화살표 사진 넣기
        var l = BitmapFactory.decodeResource(resources, R.drawable.dir)
        l = Bitmap.createScaledBitmap(l, scrw!! / 16, scrh!!/ 2, true)

        // dir 그림 자르기
        for(i in 0 until 1){
            for(j in 0 until 4){
                dir[i][j] = Bitmap.createBitmap(l, i* scrw!! / 8, j * scrh!! /8, scrw!! / 16, scrh!! / 8)
            }
        }

        if(pacmanThread == null){
            pacmanThread = PacmanThread()
            pacmanThread?.start()
            Log.d("test", "make thread")
        }

        if(monsterThread == null){
            monsterThread = MonsterThread()
            monsterThread?.start()
        }
    }

    override fun onDetachedFromWindow() {
        Log.d("test", "detached")
        pacmanThread?.run = false
        monsterThread?.run = false
        super.onDetachedFromWindow()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {

        //경계선 그리기
        canvas?.drawLine(scrw!! * 7 / 32.0F, scrh!!.toFloat(), scrw!!* 7 / 32.0F, scrh!! / 8.0F, paint )
        canvas?.drawLine(scrw!! * 7 / 32.0F, scrh!! / 8.0F, scrw!!.toFloat(), scrh!! / 8.0F, paint )

        p.color = Color.BLACK
        p.textSize = scrh!! / 16.0F
        canvas?.drawText("$sectext : $millitext",scrw!! / 2.0F ,scrh!! / 16.0F, p)

        // life 그리기
        for(i in 0 until life){
            canvas?.drawBitmap(pacman[0]!!, i * scrw!! / 16.0F , 0.0F, null )
        }

        // 캔버스에 pacman 그리기
        canvas?.drawBitmap(pacman[n]!!, scrw!! *7 / 16 + xd, scrh!!- scrh!! / 8 + yd, null )
        // pacman의 현재 위치 저장
        pacman_present_location[0] = scrw!! *7 / 16 + xd + scrw!! / 32
        pacman_present_location[1] = scrh!!- scrh!! / 8 + yd + scrh!! / 16

        // 캔버스에 monster 그리기
        for(i in monster.indices) {
            canvas?.drawBitmap(monster[i]!!, scrw!! * 7 / 16 + rxd[i], scrh!! - scrh!! / 2 + ryd[i], null)
            // monster의 현재 위치 저장
            monster_present_location[i][0] = scrw!! * 7 / 16 + rxd[i] + scrw!! / 32
            monster_present_location[i][1] = scrh!! - scrh!! / 2 + ryd[i] + scrh!! / 16

            // 게임 종료 조건
            if(life != 0 && distance_beween_pacman_monster(pacman_present_location[0], pacman_present_location[1], monster_present_location[i][0], monster_present_location[i][1]) <= scrw!! / 16){
                life--
                pacmanThread?.run = false
                monsterThread?.run = false

                xd = 0.0F
                yd = 0.0F
                for(j in 0 until 4){
                    rxd[j] = 0.0F
                    ryd[j] = 0.0F
                }
                pacmanThread = PacmanThread()
                pacmanThread?.start()
                monsterThread = MonsterThread()
                monsterThread?.start()

                Log.d("test", "thread have to be stopped")
            }
            if(life == 0){
                pacmanThread?.run = false
                monsterThread?.run = false
            }
        }

        //up
        canvas?.drawBitmap(dir[0][0]!!, (scrw!! / 16).toFloat(), (scrh!! - scrh!! *3 / 8).toFloat(), null)
        //left
        canvas?.drawBitmap(dir[0][1]!!, 0.0F, (scrh!! - scrh!! / 4).toFloat(), null)
        //right
        canvas?.drawBitmap(dir[0][2]!!, (scrw!! / 8).toFloat(), (scrh!! - scrh!! / 4).toFloat(), null)
        //down
        canvas?.drawBitmap(dir[0][3]!!, (scrw!! / 16).toFloat(), (scrh!! - scrh!! * 1 / 8).toFloat(), null)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("test", "touched")
        if(event?.action == MotionEvent.ACTION_DOWN || event?.action == MotionEvent.ACTION_MOVE || event?.action == MotionEvent.ACTION_POINTER_DOWN ){
           if(event.x > scrw!! /8 && event.x < scrw!! * 3 / 16 && event.y < scrh!! * 7 / 8 && event.y > scrh!! *3 / 4){
               if(!start && count == 0){
                   start = true
                   dirbutton1 = "Right"
               }
               dirbutton2 = "Right"
           }

            else if(event.x > 0 && event.x < scrw!! * 1 / 16 && event.y < scrh!! * 7 / 8 && event.y > scrh!! *3 / 4){
                if(!start && count == 0){
                    start = true
                    dirbutton1 = "Left"
                }
                dirbutton2 = "Left"
            }

            else if(event.x > scrw!! /16 && event.x < scrw!! / 8 && event.y < scrh!! * 3 / 4 && event.y > scrh!! * 5 / 8){
                if(!start && count == 0){
                    start = true
                    dirbutton1 = "Up"
                }
                dirbutton2 = "Up"
            }

            else if(event.x > scrw!! /16 && event.x < scrw!! / 8 && event.y < scrh!! && event.y > scrh!! * 7 / 8){
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

     // pacman Thread
     inner class PacmanThread: Thread() {
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
                        yd += (scrh!! /80).toFloat()
                        n = 1
                    }
                    else if(start && dirbutton1 == "Up" && count != 20 || !start && count in 1..19 && dirbutton1 == "Up") {
                        yd -= (scrh!! / 80).toFloat()
                        n = 2
                    }
                    else if(start && dirbutton1 == "Right" && count != 20 || !start && count in 1..19 && dirbutton1 == "Right") {
                        xd += (scrw!! / 160).toFloat()
                        n = 0
                    }
                    else if(start && dirbutton1 == "Left" && count != 20 || !start && count in 1..19 && dirbutton1 == "Left") {
                        xd -= (scrw!! / 160).toFloat()
                        n = 3
                    }

                    sleep(50)
                }
                catch (e: Exception){
                }
            }
        }
    }

    // monster Thread
    inner class MonsterThread: Thread() {
        var run: Boolean = true
        override fun run() {

            while (run){
                try{
                    for(i in monster.indices) {
                        postInvalidate()
                        count2[i] += 1
                        if (count2[i] == 10) {
                            count2[i] = 0
                        }
                        if (count2[i] == 0) {
                            when(Random.nextInt(4) + 1){
                                1 -> monsterdirbutton[i] = "Left"
                                2 -> monsterdirbutton[i] = "Right"
                                3 -> monsterdirbutton[i] = "Up"
                                4 -> monsterdirbutton[i] = "Down"
                            }
                        }
                        // 경계선 밖으로 나가지 않는다
                        if(monsterdirbutton[i] == "Down"){
                            if(scrh!! / 2 + ryd[i] < scrh!! - scrh!! / 8 - (scrh!! % 32) / 2){
                                ryd[i] += (scrh!! / 32).toFloat()
                            }
                        }
                        if(monsterdirbutton[i] == "Up"){
                            if(scrh!! / 2 + ryd[i] > scrh!! / 7){
                                ryd[i] -= (scrh!! / 32).toFloat()
                            }
                        }
                        if(monsterdirbutton[i] == "Left"){
                                if(scrw!! / 2 + rxd[i] > scrw!! * 5 / 16){
                                rxd[i] -= (scrw!! / 64).toFloat()
                            }
                        }
                        if(monsterdirbutton[i] == "Right"){
                            if(scrw!! / 2 + rxd[i] < scrw!! - scrw!! / 8 - (scrw!! % 64) / 2){
                                rxd[i] += (scrw!! / 64).toFloat()
                            }
                        }
                    }
                    sleep(100)
                }
                catch (e: java.lang.Exception){

                }
            }
        }
    }





    fun distance_beween_pacman_monster(pacmanx: Float, pacmany:Float, monsterx: Float, monstery: Float): Float = sqrt((pacmanx - monsterx).pow(2) + (pacmany - monstery).pow(2))


}