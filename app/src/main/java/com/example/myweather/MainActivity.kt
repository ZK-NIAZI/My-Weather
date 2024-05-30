package com.example.myweather

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myweather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//76a25ad7501621456098922334c2a4f5
class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchweatherData("Lahore")
        searchcity()
      // val searchCityTextView = binding.searchCity
       /* class CitySuggestionAdapter(context: Context, private val cities: List<String>) :
            ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, cities) {

            override fun getFilter(): Filter = object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val filteredCities = if (constraint!!.isNotEmpty()) {
                        cities.filter { it.startsWith(constraint, ignoreCase = true) }
                    } else {
                        cities
                    }
                    return FilterResults().apply {
                        values = filteredCities
                        count = filteredCities.size
                    }
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    // If the results are valid, update the adapter's data set
                    results?.let {
                        if (it.values != null) {
                            //clear()
                            addAll(it.values as List<String>)
                            notifyDataSetChanged()
                        } else {
                            // Handle the case where results.values is null
                        }
                    }
                }
            }
        }

        val citiesList = listOf(
            "Karachi",
            "Lahore",
            "Faisalabad",
            "Rawalpindi",
            "Gujranwala",
            "Multan",
            "Hyderabad",
            "Islamabad",
            "Quetta",
            "Bahawalpur",
            "Sargodha",
            "Sialkot",
            "Peshawar",
            "Sukkar",
            "Rahim Yar Khan",
            "Jhang",
            "Dera Ghazi Khan",
            "Gujrat",
            "Sahiwal",
            "Kasur",
            "Okara",
            "Mardan",
            "Mingora",
            "Kohat",
            "Dera Ismail Khan",
            "Abbottabad",
            "Mansehra",
            "Jhelum",
            "Sheikhupura"
        )*/

        //val citiesAdapter = CitySuggestionAdapter(this, ArrayList(citiesList))

      /*  searchCityTextView.setAdapter(CitySuggestionAdapter(this, ArrayList(citiesList)))
        searchCityTextView.setOnItemClickListener { _, _, position, _ ->
            val selectedCity = searchCityTextView.text.toString()
            fetchweatherData(selectedCity)
        }*/


    }



    private fun searchcity() {
        val searchView=binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchweatherData("$query")

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               // Toast.makeText(this@MainActivity, "No Such City Found.", Toast.LENGTH_SHORT).show()
                return true
            }

        })
    }

    private fun fetchweatherData(cityname1:String) {
        val retrofit=Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response=retrofit.getweatherData(cityname1,"76a25ad7501621456098922334c2a4f5","metric")
        response.enqueue(object :Callback<MyWeather>{
            override fun onResponse(call: Call<MyWeather>, response: Response<MyWeather>) {
                val responseBody =response.body()
                if (response.isSuccessful && responseBody!=null){
                    val temprature=responseBody.main.temp.toString()
                    val humdity=responseBody.main.temp.toString()
                    val windspeed=responseBody.wind.speed.toString()
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val sealevel=responseBody.main.pressure.toString()
                    val maxtemp=responseBody.main.temp_max.toString()
                    val mintemp=responseBody.main.temp_min.toString()
                    val condition =responseBody.weather.firstOrNull()?.main?:"unknown"

                   //Log.d("TAG","onResponse: $temprature")
                    binding.temprature.text="$temprature °C"
                    binding.weather.text=condition
                    binding.maxtemp.text="Max Temp: $maxtemp °C"
                    binding.mintemp.text="Min Temp: $mintemp °C"
                    binding.humdity.text="$humdity"
                    binding.windspeed.text="$windspeed m/s"
                    binding.sunrise.text="${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.sea.text="$sealevel hpa"
                    binding.condition.text=condition
                    binding.day.text=dayname(System.currentTimeMillis())
                    binding.date.text=date()
                    binding.cityname.text=cityname1

                    changeimagescondition(condition)
                }

            }

            override fun onFailure(call: Call<MyWeather>, t: Throwable) {
            }

        })

    }

    private fun changeimagescondition(conditions:String) {
        when(conditions){
            "Clear Sky","Sunny","Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Fog","Smoke" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }else->{
            binding.root.setBackgroundResource(R.drawable.sunny_background)
            binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf =SimpleDateFormat("dd MMMM YYYY",Locale.getDefault())
        return sdf.format(Date())

    }
    private fun time(timestamp: Long): String {
        val sdf =SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format(Date(timestamp*1000))

    }

    fun dayname(timestamp: Long): String{
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}
class CitySuggestionAdapter(context: Context, private val cities: List<String>) :
    ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, cities) {

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredCities = if (constraint!!.isNotEmpty()) {
                cities.filter { it.startsWith(constraint, ignoreCase = true) }
            } else {
                cities
            }
            return FilterResults().apply {
                values = filteredCities
                count = filteredCities.size
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            // If the results are valid, update the adapter's data set
            results?.let {
                clear()
                addAll(it.values as List<String>)
                notifyDataSetChanged()
            }
        }
    }
}
