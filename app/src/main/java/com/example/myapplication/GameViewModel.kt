package com.example.myapplication

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.Classes.Card
import com.example.myapplication.Model.CardState

val CARD_SOURCES = listOf(
    R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5,
    R.drawable.c6, R.drawable.c7, R.drawable.c8, R.drawable.c9, R.drawable.c10,
    R.drawable.c11, R.drawable.c12, R.drawable.c13, R.drawable.c14, R.drawable.c15,
    R.drawable.c16, R.drawable.c17, R.drawable.c18, R.drawable.c19, R.drawable.c20,
    R.drawable.c21, R.drawable.c22, R.drawable.c23, R.drawable.c24, R.drawable.c25,
    R.drawable.c26, R.drawable.c27, R.drawable.c28, R.drawable.c29, R.drawable.c30,
    R.drawable.c31, R.drawable.c32, R.drawable.c33, R.drawable.c34, R.drawable.c35
)

class GameViewModel() : ViewModel() {

//    private val resultModel = ResultModel(0.0, 0)

    var boardInit = false
    var lastCard : Int? = null;
    var clicks : Int = 0;
    var freeze : Boolean = false;
    var pause : Boolean = false

    private val _cards = MutableLiveData<MutableList<CardState>>()
    val cards : LiveData<MutableList<CardState>> = _cards

    private val _time : MutableLiveData<Double> = MutableLiveData()
    val time : LiveData<Double> = _time

    private val _moves : MutableLiveData<Int> = MutableLiveData()
    val moves : LiveData<Int> = _moves

    private val _gameOver: MutableLiveData<Boolean> = MutableLiveData()
    val gameOver : LiveData<Boolean> = _gameOver


    fun setup(width: Int, height: Int){
        if(!boardInit){
            val resultCards = mutableListOf<CardState>()
            val randomCards = getRandomCards(width * height / 2)

            randomCards.forEach {
                resultCards.add(CardState(it))
            }

            _cards.postValue(resultCards)
        }
        boardInit = true
    }

    fun addClick() {
        clicks = if (clicks + 1 > 2) 1 else clicks + 1
    }

    fun addMove() {
        _moves.postValue((moves.value ?: 0) + 1)
    }

    fun gameOver(){
        _gameOver.postValue(true)
    }

    fun pause(){
        pause = !pause
    }
    fun updateTime(time: Double) {
        _time.postValue(time)
    }

    private fun getRandomCards(pars : Int): List<Int> {
        val result = mutableListOf<Int>()

        val copy = CARD_SOURCES.toMutableList();

        for (i in 0 until pars){
            val random = copy.random()
            val index = copy.indexOf(random)
            result.add(random)
            copy.removeAt(index)
        }

        result.addAll(result)

        return result.shuffled();
    }




}

