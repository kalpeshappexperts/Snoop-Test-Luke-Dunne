package luke.com.snoop.beerapp.data.repositories

import luke.com.snoop.beerapp.data.service.PunkService
import luke.com.snoop.beerapp.domain.entities.Beer
import luke.com.snoop.beerapp.domain.repositories.PunkRepository
import luke.com.snoop.beerapp.domain.utils.Result

class PunkRepositoryImpl(
    private val punkService: PunkService
) : PunkRepository {

    override fun getBeersList(page: Int, perPage: Int): Result<List<Beer>> {
        return punkService.getBeersList(page, perPage)
    }
    override fun getBeersById(id: Int): Result<List<Beer>> {
        return punkService.getBeersById(id)
    }

}