package luke.com.snoop.beerapp.data.service.api

import luke.com.snoop.beerapp.data.service.response.PunkResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PunkApi {

    @GET("v2/beers")
    fun getBeersList(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<List<PunkResponse>>

    @GET("v2/beers")
    fun getBeersById(
        @Query("ids") id: Int
    ): Call<List<PunkResponse>>

}