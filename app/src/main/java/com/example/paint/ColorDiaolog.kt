package com.example.paint

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import java.lang.Exception
import java.lang.IllegalStateException

class ColorDiaolog : DialogFragment(), SeekBar.OnSeekBarChangeListener {

    lateinit var alphaSeekBar: SeekBar
    lateinit var redSeekBar: SeekBar
    lateinit var greenSeekBar: SeekBar
    lateinit var blueSeekBar: SeekBar
    lateinit var setColorButton: Button
    lateinit var colorView: View
    val sharedPreferences: SharedPreferences? = null
    var alpha: Int = 255
    var red: Int = 0
    var green: Int = 0
    var blue: Int = 0
    var listener: setColor? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.palete_dialog, null)
            builder.setView(view)
            alphaSeekBar = view.findViewById(R.id.alphaSeekBar)
            redSeekBar = view.findViewById(R.id.redSeekBar)
            greenSeekBar = view.findViewById(R.id.GreenSeekBar)
            blueSeekBar = view.findViewById(R.id.blueSeekBar)
            setColorButton = view.findViewById(R.id.setColorButton)
            colorView = view.findViewById(R.id.colorview)
            setSeekbarColor()
            alphaSeekBar.setOnSeekBarChangeListener(this)
            redSeekBar.setOnSeekBarChangeListener(this)
            greenSeekBar.setOnSeekBarChangeListener(this)
            blueSeekBar.setOnSeekBarChangeListener(this)
            setColorButton.setOnClickListener {

                listener?.setColor(
                    alpha,
                    red,
                    green,
                    blue,
                )
                dismiss()
            }
            builder.create()
        } ?: throw IllegalStateException("sasasasa")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        alpha = alphaSeekBar.progress
        red = redSeekBar.progress
        green = greenSeekBar.progress
        blue = blueSeekBar.progress
        colorView.setBackgroundColor(
            Color.argb(
                alphaSeekBar.progress,
                redSeekBar.progress,
                greenSeekBar.progress,
                blueSeekBar.progress
            )
        )
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as setColor
        } catch (e: Exception) {
        }
    }

    interface setColor {
        fun setColor(alpha: Int?, red: Int?, green: Int?, blue: Int?)
    }

    fun setSeekbarColor() {
        alphaSeekBar.progress = arguments!!.getInt("alpha")
        alpha = arguments!!.getInt("alpha")
        redSeekBar.progress = arguments!!.getInt("red")
        red = arguments!!.getInt("red")
        greenSeekBar.progress = arguments!!.getInt("green")
        green = arguments!!.getInt("green")
        blueSeekBar.progress = arguments!!.getInt("blue")
        blue = arguments!!.getInt("blue")
        colorView.setBackgroundColor(Color.argb(alpha, red, green, blue))
    }
}