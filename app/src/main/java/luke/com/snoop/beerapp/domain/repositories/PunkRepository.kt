package luke.com.snoop.beerapp.domain.repositories

import luke.com.snoop.beerapp.domain.entities.Beer
import luke.com.snoop.beerapp.domain.utils.Result

interface PunkRepository {

    fun getBeersList(page: Int, perPage: Int)
            : Result<List<Beer>>

    fun getBeersById(id: Int)
            : Result<List<Beer>>

}