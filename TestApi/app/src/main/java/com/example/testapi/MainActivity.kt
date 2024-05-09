package com.example.testapi

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), ApiResponse.OnApiResultListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        updateListData(findViewById(android.R.id.content))
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun getDataFromApi(view: View) {

        val apiResponse = ApiResponse()
        val apiUrl = "https://catfact.ninja/fact"

        apiResponse.onApiResultListener = this

        GlobalScope.launch(Dispatchers.Main) {
            apiResponse.fetchAndNotify(apiUrl)
        }
    }

    override fun onResult(result: String?) {
        if (result != null) {
            val gson = Gson()
            val catFact: CatFact = gson.fromJson(result, CatFact::class.java)

            Log.i("USER_LOG", catFact.fact)

            val dbRecord = DBRecord(id = null, fact = catFact.fact, length = catFact.length)
            val db = DBHandler(this)
            db.insertData(dbRecord)
            Log.i("USER_LOG", "Fact saved in database")
            updateListData(findViewById(android.R.id.content))
        } else {
            Log.e("USER_LOG", "Error: Failed to fetch data")
        }
    }

    fun updateListData(view: View) {
        val list = findViewById<ListView>(R.id.listView1)
        val db = DBHandler(this)
        val factsList = db.getAllFacts().map { it.fact }

        try {
            val adapter =
                ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, factsList)
            list.adapter = adapter
            Log.i("USER_LOG", "List updated")
        } catch (e: Exception) {
            Log.e("USER_LOG", e.toString())
        }
    }

    fun deleteData(view: View) {
        val db = DBHandler(this)
        db.clearDatabase()
        updateListData(findViewById(android.R.id.content))
        Log.i("USER_LOG", "Database cleared")
    }
}