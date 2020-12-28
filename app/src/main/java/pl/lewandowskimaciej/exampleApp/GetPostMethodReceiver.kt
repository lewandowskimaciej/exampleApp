package pl.lewandowskimaciej.exampleApp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import java.util.*

class GetPostMethodReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val myRequestQueue = Volley.newRequestQueue(context)
        val url = "https://glozyny.katowice.opoka.org.pl/api/zwrot.php"

        val myStringRequest: StringRequest = object : StringRequest(
            Method.POST, url,
            { response ->

                Log.e("dupa zwrot", "z receivera$response")
            },
            { error ->

            }) {
            override fun getParams(): Map<String, String> {
                val MyData: MutableMap<String, String> = HashMap()
                MyData["name"] = "maciej"
                MyData["job"] = "nauczyciel"
                return MyData
            }
        }

        myRequestQueue.add(myStringRequest)
    }

}
