package no.jmheiberg.jonma.note2self.util

import android.app.Activity
import android.os.AsyncTask
import com.bumptech.glide.Glide
import no.jmheiberg.jonma.note2self.BuildConfig
import no.jmheiberg.jonma.note2self.R
import no.jmheiberg.jonma.note2self.database.Note
import no.jmheiberg.jonma.note2self.database.Weather
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

class WeatherUtil(private val activity: Activity) {

    private val apikey = BuildConfig.ApiKey


    fun getWeatherForecast(note: Note): Weather {
        val url =
            "http://api.openweathermap.org/data/2.5/weather?lat=${note.lat}&lon=${note.lng}&APPID=$apikey&units=metric"
        return RequestWeatherAsync().execute(url).get()
    }


    inner class RequestWeatherAsync : AsyncTask<String, String, Weather>() {
        override fun doInBackground(vararg params: String?): Weather? {
            var connectionUrl: HttpURLConnection? = null
            var inputString = ""
            var tempWeatherObject: Weather? = null
            try {
                val url = URL(params[0])
                connectionUrl = url.openConnection() as HttpURLConnection
                connectionUrl.connectTimeout = 60000
                connectionUrl.readTimeout = 60000

                inputString = streamToString(connectionUrl.inputStream)

                publishProgress(inputString)
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                connectionUrl?.disconnect()
            }

            try {
                val json = JSONObject(inputString)
                val weather = json.getJSONArray("weather").getJSONObject(0)
                val main = json.getJSONObject("main")

                val temp = main.getString("temp").toDouble().roundToInt()

                val icon = weather.getString("icon")


                tempWeatherObject = Weather(temp, icon)


            } catch (ex: Exception) {
                ex.printStackTrace()
            }



            return tempWeatherObject
        }


    }


    fun setWeatherFromDatabase(weather: Weather) {
        Glide.with(activity).load("http://openweathermap.org/img/w/${weather.icon}.png")
            .into(activity.findViewById(R.id.img_weather_big))
    }

    private fun streamToString(inputStream: InputStream): String {

        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line: String
        var result = ""

        try {
            do {
                line = bufferReader.readLine()
                if (line != null) {
                    result += line
                }
            } while (line != null)
            inputStream.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }
}