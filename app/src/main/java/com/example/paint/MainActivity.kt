package com.example.paint

import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.paint.view.PicassoView
import com.example.paint.view.bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), ColorDiaolog.setColor {

    lateinit var picasso: PicassoView
    var currentAlertDialog: AlertDialog.Builder? = null
    var dialogLineWidth: AlertDialog? = null
    var sharedPreferences: SharedPreferences? = null
    var imageView: ImageView? = null

    var alpha: Int = 255
    var red: Int = 0
    var green: Int = 0
    var blue: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        picasso = findViewById(R.id.view)
        retrieveColor()
        val stroke = sharedPreferences!!.getInt("brushWidth", 0)
        picasso.setLineWidth(stroke)
        picasso.setDrawingColor(Color.argb(alpha, red, green, blue))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.colorid -> {
                val dialog = ColorDiaolog()
                val b = Bundle()
                b.putInt("alpha", this.alpha)
                b.putInt("red", this.red)
                b.putInt("green", this.green)
                b.putInt("blue", this.blue)
                dialog.arguments = b
                dialog.show(supportFragmentManager, "Dialog")

            }
            R.id.eraseid -> {
                picasso.clear()

            }
            R.id.lineWidthid -> {
                showLineWidthDialog()
            }
            R.id.saveid -> {
                if (checkPermision()){
                saveImage()
                }
                else{
                    requestPermission()
                }
            }
            R.id.clearid -> {
                picasso.clear()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showLineWidthDialog() {
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        currentAlertDialog = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.width_dialog, null)
        val seekbar: SeekBar = view.findViewById(R.id.seekbar)
        imageView = view.findViewById(R.id.imageView)
        val stroke = sharedPreferences!!.getInt("brushWidth", 0)
        seekbar.progress = stroke
        strokeWidth(stroke)
        val btnSetWidth: Button = view.findViewById(R.id.btnSetWidth)
        btnSetWidth.setOnClickListener {
            Toast.makeText(this, "Set Width", Toast.LENGTH_SHORT).show()
            picasso.setLineWidth(seekbar.progress)
            val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
            editor.putInt("brushWidth", seekbar.progress)
            editor.apply()
            dialogLineWidth?.dismiss()
        }

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            val bitmap: Bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888)
            val canvas: Canvas = Canvas(bitmap)
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val p: Paint = Paint()
                p.setColor(picasso.getDrawingColor())
                p.strokeWidth = p1.toFloat()
                p.strokeCap = Paint.Cap.ROUND

                bitmap.eraseColor(Color.WHITE)
                canvas.drawLine(30F, 50F, 370F, 50F, p)
                imageView!!.setImageBitmap(bitmap)

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }
        })

        currentAlertDialog!!.setView(view)
        dialogLineWidth = currentAlertDialog!!.create()
        dialogLineWidth!!.show()
    }

    override fun setColor(alpha: Int?, red: Int?, green: Int?, blue: Int?) {
        picasso.setDrawingColor(Color.argb(alpha!!, red!!, green!!, blue!!))
        this.alpha = alpha
        this.red = red
        this.green = green
        this.blue = blue
        saveColor()
    }

    fun saveColor() {
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
        editor.putInt("alpha", this.alpha)
        editor.putInt("red", this.red)
        editor.putInt("green", this.green)
        editor.putInt("blue", this.blue)
        editor.apply()
    }

    fun retrieveColor() {
        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE)
        this.alpha = sharedPreferences!!.getInt("alpha", 255)
        this.red = sharedPreferences!!.getInt("red", 0)
        this.green = sharedPreferences!!.getInt("green", 0)
        this.blue = sharedPreferences!!.getInt("blue", 0)
    }

    fun strokeWidth(p1: Int) {
        val bitmap: Bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(bitmap)
        val p: Paint = Paint()
        p.setColor(picasso.getDrawingColor())
        p.strokeWidth = p1.toFloat()
        p.strokeCap = Paint.Cap.ROUND
        picasso.setLineWidth(p1)
        bitmap.eraseColor(Color.WHITE)
        canvas.drawLine(30F, 50F, 370F, 50F, p)
        imageView!!.setImageBitmap(bitmap)
    }

    fun saveImage() {
        val fileName: String = "Picasso" + System.currentTimeMillis()
        val values: ContentValues = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, fileName)
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpg")

        val uri: Uri =
            baseContext.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )!!
        val outputStream: OutputStream? = baseContext.contentResolver.openOutputStream(uri)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream?.flush()
        outputStream?.close()
        Toast.makeText(this, "Image Saved", Toast.LENGTH_SHORT).show()
    }

    fun saveToInternalStorage() {
        val cw: ContextWrapper = ContextWrapper(baseContext)
        val fileName: String = "Picasso" + System.currentTimeMillis()
        val directory: File = cw.getDir("imageDir", Context.MODE_PRIVATE)
        val mypath: File = File(directory, fileName + "jpg")
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.flush()
                fos?.close()
                Toast.makeText(this, directory.absolutePath, Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun checkPermision(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            100
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage()
                } else {
                    requestPermission()
                }
            }
        }
    }
}