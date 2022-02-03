package com.example.xml_api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.xml_api.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val baseUrl = "https://raw.github.uio.no/kmbjorem/Test_xlm/main/dogs.xml?"
        val token = "token=AAABT3TL6VGN7DU6ALS76NTCAIZXQ"

        val tekst = binding.tekst


        fun getData():String{
            val requestUrl = "$baseUrl$token"
            return khttp.get(requestUrl).text
        }

        CoroutineScope(Dispatchers.IO).launch {
            val response = getData()
            //Log.d("Response",response)

            runOnUiThread{
                val inputStream : InputStream = response.byteInputStream()
                val listOfDogs = XmlParser().parse(inputStream)

                Log.d("List of Dogs:", listOfDogs.toString())
                Log.d("Size of Dogs:",listOfDogs.size.toString())
                var skriv = ""
                for(item in listOfDogs){
                    val denne = item as Dog
                    val navn = denne.name
                    val ar = denne.age.toString()
                    skriv += "$navn, $ar\n"
                }

                tekst.text = skriv
            }

        }

    }
}