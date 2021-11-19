package luke.com.snoop.beerapp.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import luke.com.snoop.beerapp.R
import luke.com.snoop.beerapp.domain.entities.Beer
import luke.com.snoop.beerapp.ui.adapters.PunkAdapter
import luke.com.snoop.beerapp.ui.utils.Data
import luke.com.snoop.beerapp.ui.utils.Status
import luke.com.snoop.beerapp.ui.viewmodels.PunkViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

const val MINIMUM_LOADING_TIME = 1000L

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<PunkViewModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var llProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        llProgressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recycler_beer_list)
        viewModel.mainStateList.observe(::getLifecycle, ::updateUI)
        viewModel.mainStateDetail.observe(::getLifecycle, ::updateDetailUI)

        callStartService()
    }

    private fun updateUI(beersData: Data<List<Beer>>) {
        when (beersData.responseType) {
            Status.ERROR -> {
                hideLoading()
                beersData.error?.message?.let { showMessage(it) }
                beersData.data?.let { setBeerList(it) }
            }
            Status.LOADING -> {
                showLoading()
            }
            Status.SUCCESSFUL -> {
                beersData.data?.let { setBeerList(it) }
                hideLoading()
            }
        }
    }

    private fun updateDetailUI(beersData: Data<List<Beer>>) {
        when (beersData.responseType) {
            Status.ERROR -> {
                hideLoading()
                beersData.error?.message?.let { showMessage(it) }
            }
            Status.LOADING -> {
                showLoading()
            }
            Status.SUCCESSFUL -> {
                startActivity(Intent(this, DetailActivity::class.java))
                hideLoading()
            }
        }
    }

    private fun callStartService() {
        showLoading()
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.onStartHome(1, 80)
        }, MINIMUM_LOADING_TIME)
    }

    private fun showLoading() {
        llProgressBar.visibility = View.VISIBLE

    }

    private fun hideLoading() {
        llProgressBar.visibility = View.GONE
    }

    private fun setBeerList(beerList: List<Beer>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        val punkAdapter = PunkAdapter(beerList)
        punkAdapter.setOnItemClickListener(itemClickListener())
        recyclerView.adapter = punkAdapter
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun callClickDetailsService(item: Beer) {
        viewModel.onClickToBeerDetails(item.id, this@MainActivity.applicationContext)
    }

    private fun itemClickListener() = object : PunkAdapter.OnItemClickListener {
        override fun onItemClick(item: Beer) {
            callClickDetailsService(item)
        }
    }
}