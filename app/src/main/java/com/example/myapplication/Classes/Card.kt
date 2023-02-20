package com.example.myapplication.Classes
import android.widget.ImageView
import com.example.myapplication.Model.CardState
import com.example.myapplication.R


data class Card(var img:ImageView, var cardState : CardState) {

    companion object{
        const val CARD_BACK = R.drawable.card_back;
    }

    fun show(){
        //Animation
        img.setImageResource(cardState.cardImg)
        cardState.show = true
    }

    fun hide(){
        //Animation
        img.setImageResource(CARD_BACK)
        cardState.show = false
    }

    fun found() {
        cardState.show = true
        cardState.found = true
    }

    fun isEqual(card : Card) : Boolean{
        return this.cardState.cardImg == card.cardState.cardImg;
    }



}
