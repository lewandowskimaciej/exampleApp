package pl.lewandowskimaciej.exampleApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.view.View
import android.util.Log
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var TAG: String = "mylog"
    var informacja: String = "call from MainActivity"
    var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
    }

    fun button(v : View) {
        var intent: Intent = Intent(this, SelectActivityMenu::class.java)
        intent.putExtra("name", "value")
        startActivity(intent)
        Log.d(TAG, informacja)
    }
}