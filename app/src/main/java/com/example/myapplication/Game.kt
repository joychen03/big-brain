package com.example.myapplication

import android.animation.AnimatorInflater
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.Classes.Card
import com.example.myapplication.Classes.Utils
import com.example.myapplication.Enums.BoardSize
import com.example.myapplication.Enums.GameEventType
import com.example.myapplication.Enums.Orientation
import com.example.myapplication.Model.CardState
import com.example.myapplication.Services.MusicService
import com.example.myapplication.Services.TimerService
import com.example.myapplication.databinding.ActivityGameBinding


class Game : AppCompatActivity() {

    private var timerStarted = false

    private lateinit var layout : ActivityGameBinding
    private lateinit var timerService : Intent
    private lateinit var musicService : Intent
    private lateinit var viewModel : GameViewModel
    private lateinit var clickSound : MediaPlayer
    private lateinit var foundSound : MediaPlayer
    private lateinit var failSound : MediaPlayer
    private lateinit var endSound : MediaPlayer
    private lateinit var pauseBtn : ImageButton

    private var cards = mutableListOf<Card>()
    private var boardWidth = 0
    private var boardHeight = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        layout = ActivityGameBinding.inflate(layoutInflater)
        setContentView(layout.root)
        val bundle: Bundle? = intent.extras


        //region Variables
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        clickSound = MediaPlayer.create(this, R.raw.click)
        foundSound = MediaPlayer.create(this, R.raw.wow)
        failSound = MediaPlayer.create(this, R.raw.bruh)
        endSound = MediaPlayer.create(this, R.raw.end_sound)

        pauseBtn = layout.pause

        val difficulty = bundle?.getString("difficulty") ?: Difficulty.HARD.string
        val orientation = this.resources.configuration.orientation
        when(difficulty){
            Difficulty.EASY.string ->{
                boardWidth = if(orientation == Orientation.PORTAIT.value) BoardSize.P_EASY_X.size else BoardSize.L_EASY_X.size
                boardHeight = if(orientation == Orientation.PORTAIT.value) BoardSize.P_EASY_Y.size else BoardSize.L_EASY_Y.size
            }
            Difficulty.NORMAL.string ->{
                boardWidth = if(orientation == Orientation.PORTAIT.value) BoardSize.P_NORMAL_X.size else BoardSize.L_NORMAL_X.size
                boardHeight = if(orientation == Orientation.PORTAIT.value) BoardSize.P_NORMAL_Y.size else BoardSize.L_NORMAL_Y.size
            }
            Difficulty.HARD.string ->{
                boardWidth = if(orientation == Orientation.PORTAIT.value) BoardSize.P_HARD_X.size else BoardSize.L_HARD_X.size
                boardHeight = if(orientation == Orientation.PORTAIT.value) BoardSize.P_HARD_Y.size else BoardSize.L_HARD_Y.size
            }
        }

        //endregion

        musicService = Intent(this, MusicService::class.java)
        startService(musicService)

        timerService = Intent(applicationContext, TimerService::class.java)
        registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))
        startStopTimer()

        viewModel.setup(boardWidth,boardHeight)

        viewModel.cards.observe(this, Observer{ cardState ->
            initBoard(cardState)
            startGame(cards)
        })

        viewModel.time.observe(this, Observer { time ->
            layout.time.text = Utils.getTimeStringFromDoble(time)
        })

        viewModel.moves.observe(this, Observer { moves ->
            layout.moves.text = moves.toString()
        })

        viewModel.gameOver.observe(this, Observer { isGameOver ->
            if(isGameOver){
                stopService(timerService)
                playSound(GameEventType.END)

                val intent = Intent(this, Result::class.java)
                intent.putExtra("time", viewModel.time.value)
                intent.putExtra("moves", viewModel.moves.value)
                intent.putExtra("difficulty", difficulty)
                this.startActivity(intent);
            }
        })

    }
    
    override fun onStop() {
        stopTimer()
        stopService(musicService)
        super.onStop()
    }

    override fun onStart() {
        startTimer()
        startService(musicService)
        super.onStart()
    }


    //region Functions

    private fun initBoard(cardStates: List<CardState>) {

        checkPasue()

        cardStates.forEach { cardState ->
            cards.add(Card(ImageView(this), cardState))
        }

        val trowLayout = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT,1f)
        val cardLayout = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1f).apply {
            setMargins(10)
        }
        val scale : Float = this.resources.displayMetrics.density;

        var count = 0
        for(i in 0 until boardHeight){
            val row = TableRow(this).apply {
                layoutParams = trowLayout
                gravity = Gravity.CENTER
            }

            for(j in 0 until boardWidth){
                val card = cards[count]

                card.img.apply {
                    layoutParams = cardLayout
                    setPadding(0)
                    background = null
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setBackgroundResource(card.cardState.cardImg)
                    cameraDistance = 8000 * scale

                    if(card.cardState.show || card.cardState.found){
                        setImageResource(card.cardState.cardImg)
                    }else{
                        setImageResource(Card.CARD_BACK)
                    }

                }

                row.addView(card.img)

                count ++
            }

            layout.board.addView(row)

        }
    }

    private fun startGame(cards : MutableList<Card>) {

        pauseBtn.setOnClickListener {
            viewModel.pause()
            checkPasue()
        }
        cards.forEachIndexed { index, card ->
            card.img.setOnClickListener{

                if(isSameCard(card) || viewModel.freeze || card.cardState.found || viewModel.pause) return@setOnClickListener
                playSound(GameEventType.CLICK)

                viewModel.addClick()
                flip(card)

                if(isSecondMove() && viewModel.lastCard != null){
                    if(isfound(card)){
                        playSound(GameEventType.FOUND)
                        card.found()
                        cards[viewModel.lastCard!!].found()
                    }else{
                        viewModel.freeze = true
                        playSound(GameEventType.FAIL)
                        Handler(Looper.getMainLooper()).postDelayed({
                            flip(cards[viewModel.lastCard!!], true)
                            flip(card, true)
                            viewModel.lastCard = null
                            viewModel.freeze = false
                        }, 1000)
                    }
                    viewModel.addMove()
                }else{
                    viewModel.lastCard = index
                }

                if(allFound()){
                    viewModel.gameOver()
                }
            }

        }
    }

    private fun checkPasue(){
        if(viewModel.pause){
            pauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
            stopTimer()
        }else{
            pauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            startTimer()
        }
    }

    private fun flip(card : Card, hide : Boolean = false){
        val flip = AnimatorInflater.loadAnimator(this, R.animator.flip)
        val flip180 = AnimatorInflater.loadAnimator(this, R.animator.flip180)
        flip.setTarget(card.img)
        flip180.setTarget(card.img)

        flip180.start()
        flip.start()

        Handler(Looper.getMainLooper()).postDelayed({
            if(hide){
                card.hide()
            }else{
                card.show()
            }
        }, 150)

    }

    private fun playSound(type : GameEventType){
        when(type){
            GameEventType.CLICK -> clickSound.start()
            GameEventType.FOUND ->{
                //For long sound effect
                foundSound.release()
                foundSound = MediaPlayer.create(this, R.raw.wow)
                foundSound.start()
            }
            GameEventType.FAIL -> failSound.start()
            GameEventType.END -> endSound.start()
            else -> {}
        }
    }

    private fun allFound(): Boolean {
        return cards.all { it.cardState.found }
    }

    private fun isSecondMove() : Boolean {
        return viewModel.clicks == 2
    }

    private fun isSameCard(currentCard: Card) : Boolean{
        return if(viewModel.lastCard != null){
            currentCard == cards[viewModel.lastCard!!]
        }else{
            false
        }
    }

    private fun isfound(currentCard : Card): Boolean {
        return if(isSecondMove()){
            currentCard.isEqual(cards[viewModel.lastCard!!]);
        }else{
            false;
        }
    }

    //region Timer

    private fun startStopTimer() {
        if(timerStarted){
            stopTimer()
        }else{
            startTimer()
        }
    }

    private fun startTimer() {
        timerService.putExtra(TimerService.TIMER_EXTRA, viewModel.time.value)
        startService(timerService)
        timerStarted = true
    }

    private fun stopTimer() {
        stopService(timerService)
        timerStarted = false
    }

    private val updateTime : BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.updateTime(intent.getDoubleExtra(TimerService.TIMER_EXTRA, 0.0))
        }
    }



    //endregion

    //endregion

}