package com.saverio.wordoftheday_en

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.core.view.isGone
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadWord()
    }

    fun loadWord() {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://www.saveriomorelli.com/api/word-of-the-day/")
            .build()

        val jsonAPI = retrofit.create(jsonAPI::class.java)
        val mcall: Call<Model> = jsonAPI.getInfo()
        try {
            mcall.enqueue(
                object : Callback<Model> {
                    override fun onFailure(call: Call<Model>, t: Throwable) {
                        Log.e("Error", t.message.toString())
                    }

                    override fun onResponse(
                        call: Call<Model>,
                        response: Response<Model>
                    ) {
                        val model: Model? = response.body()

                        val dateElement: TextView = findViewById(R.id.dateElement)
                        val wordElement: TextView = findViewById(R.id.wordElement)

                        val definitionTitle: TextView = findViewById(R.id.titleDefinition)
                        val definitionElement: TextView = findViewById(R.id.definitionElement)

                        val separator: TextView = findViewById(R.id.separator)
                        val typeElement: TextView = findViewById(R.id.typeElement)
                        val phoneticsElement: TextView = findViewById(R.id.phoneticsElement)

                        val etymologyTitle: TextView = findViewById(R.id.titleEtymology)
                        val etymologyElement: TextView = findViewById(R.id.etymologyElement)


                        if (model != null && model.date != "null") {
                            dateElement.text = model.date
                            wordElement.text = model.word
                            definitionElement.text = model.definition

                            if (model.definition != "") {
                                definitionElement.text = model.definition
                                definitionElement.isGone = false
                                definitionTitle.isGone = false
                            }


                            if (model.word_type != "" && model.phonetics != "") {
                                separator.isGone = false
                            }
                            typeElement.text = model.word_type
                            phoneticsElement.text = "/${model.phonetics}/"

                            if (model.etymology != "") {
                                etymologyElement.text = model.etymology
                                etymologyElement.isGone = false
                                etymologyTitle.isGone = false
                            }

                        } else if (model != null && model.date == "null") {
                            //no word
                            println("no word")
                        } else {
                            //null
                            println("null")
                        }
                    }
                })
        } catch (e: Exception) {
            Log.e("Exception", e.message.toString())
        }
    }
}