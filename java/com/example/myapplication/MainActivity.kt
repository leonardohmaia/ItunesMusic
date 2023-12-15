import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var searchBox: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var labelProducts: TextView
    private lateinit var recyclerViewProducts: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBox = findViewById(R.id.searchBox)
        searchButton = findViewById(R.id.searchButton)
        labelProducts = findViewById(R.id.labelProducts)
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts)

        productList = mutableListOf()
        productAdapter = ProductAdapter(productList)
        recyclerViewProducts.layoutManager = LinearLayoutManager(this)
        recyclerViewProducts.adapter = productAdapter

        searchButton.setOnClickListener {
            val artistName = searchBox.text.toString()
            if (artistName.isEmpty()) {
                Toast.makeText(this, "Please enter an artist name", Toast.LENGTH_SHORT).show()
            } else {
                fetchProducts(artistName)
            }
        }
    }

    private fun fetchProducts(artistName: String) {
        val url = "https://itunes.apple.com/search?term=$artistName&entity=musicVideo"
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val results = response.getJSONArray("results")
                    for (i in 0 until results.length()) {
                        val result = results.getJSONObject(i)
                        val trackName = result.getString("trackName")
                        val releaseDate = result.getString("releaseDate")
                        val collectionName = result.getString("collectionName")
                        val artworkUrl = result.getString("artworkUrl100")
                        productList.add(Product(trackName, releaseDate, collectionName, artworkUrl))
                    }
                    productAdapter.notifyDataSetChanged()
                    labelProducts.text = "Produtos"
                } catch (e: JSONException) {
                    Log.e("Error", "Error parsing JSON response", e)
                }
            }, { error ->
                Log.e("Error", "Error fetching products", error)
            })
        queue.add(jsonObjectRequest)
    }
}
