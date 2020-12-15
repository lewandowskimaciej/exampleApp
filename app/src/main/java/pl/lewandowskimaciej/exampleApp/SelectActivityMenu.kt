package pl.lewandowskimaciej.exampleApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SelectActivityMenu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_menu)
    }

    fun location(v: View) {
        var intent : Intent = Intent(this, Location::class.java)
        startActivity(intent)
    }
}