package luke.com.snoop.beerapp.domain.useCases

import luke.com.snoop.beerapp.domain.repositories.PunkRepository
import org.koin.core.KoinComponent


class GetBeersById (private val punkRepository: PunkRepository) : KoinComponent{
    operator fun invoke(id: Int) = punkRepository.getBeersById(id)
}