package com.ekdorn.silentium.morse

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.reduce


class MorseListener (context: Context) {
    var MESSAGE_END_DURATION: Long = 0
    var MESSAGE_SPACE_DURATION = 0.0
    var MESSAGE_LETTER_DURATION = 0.0

    var timeDown: Long = 0
    var timeUp: Long = 0
    var flgDown = false
    var message: Message = Message()
    var symbol = 0
    // var timer: Timer? = null

    /*fun MorseListener(context: Context) {
        this.context = context
        try {
            MESSAGE_LETTER_DURATION = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("short_morse", "750")!!
                .toDouble()
            MESSAGE_SPACE_DURATION = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("long_morse", "3000")!!
                .toDouble()
            MESSAGE_END_DURATION = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("frustration_morse", "5000")!!
                .toLong()
        } catch (nfe: NumberFormatException) {
            Toast.makeText(
                context,
                context.getString(R.string.wrong_morse_numerals_stayed),
                Toast.LENGTH_SHORT
            ).show()
            MESSAGE_LETTER_DURATION = 750.0
            MESSAGE_SPACE_DURATION = 3000.0
            MESSAGE_END_DURATION = 5000
        }
        Log.e("TAG", "MorseListener: $MESSAGE_LETTER_DURATION")
        Log.e("TAG", "MorseListener: $MESSAGE_SPACE_DURATION")
        Log.e("TAG", "MorseListener: $MESSAGE_END_DURATION")
        Log.e("TAG", "MorseListener: <--- end of the message --->")
    }*/

    fun decode(event: MotionEvent?): Boolean = true

    /*fun decode(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (timer != null) {
                    timer.cancel()
                }
                if (!flgDown) {
                    Log.d("TM_UP", (System.currentTimeMillis() - timeUp).toString() + "")
                    timeDown = System.currentTimeMillis()
                    flgDown = true
                    Clicker1()
                    class Sent : TimerTask() {
                        fun run() {
                            Log.e("TAG", "run: $symbol")
                            Obtainer1(message, symbol)
                            Sender(message)
                            symbol = 0
                            Obtainer3(message)
                            timeDown = 0
                            timeUp = 0
                        }
                    }
                    timer = Timer()
                    timer.schedule(Sent(), MESSAGE_END_DURATION)
                    if (timeDown - timeUp > MESSAGE_LETTER_DURATION && timeUp != 0L) {
                        Log.d("Pack", "PACKED ")
                        Obtainer1(message, symbol)
                        symbol = 0
                        if (timeDown - timeUp > MESSAGE_SPACE_DURATION) {
                            Log.d("SWAG", "lol")
                            Obtainer2(message)
                        }
                        timeUp = 0
                    }
                    symbol *= 2
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (flgDown) {
                    Clicker2()
                    Log.d("TM_DOWN", (System.currentTimeMillis() - timeDown).toString() + "")
                    if (System.currentTimeMillis() - timeDown < MESSAGE_LETTER_DURATION) {
                        symbol *= 2
                        symbol++
                    } else {
                        symbol *= 2
                        symbol++
                        symbol *= 2
                        symbol++
                    }
                    timeUp = System.currentTimeMillis()
                    flgDown = false
                }
                return true
            }
        }
        return false
    }

    fun Sender(message: Message?) {}

    fun Obtainer1(message: Message, symbol: Int) {
        message.addSymb(symbol)
    }

    fun Obtainer2(message: Message) {
        message.addSymb(-1)
    }

    fun Obtainer3(message: Message) {
        message.clear()
    }

    fun Clicker1() {}

    fun Clicker2() {}

    suspend fun accept (flow: Flow<Byte>) {
        flow.reduce(suspend{ accumulator: String, value: Byte ->
            return accumulator
        })
    }*/
}
