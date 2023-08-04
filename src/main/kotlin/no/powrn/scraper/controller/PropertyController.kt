package no.powrn.scraper.controller

import no.powrn.scraper.domain.SingleProperty
import no.powrn.scraper.service.PropertyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/scraper")
class PropertyController(
    val propertyService: PropertyService
) {

    @GetMapping("/properties")
    fun getPropertData(): List<SingleProperty> {
        return propertyService.getAllProperties()
    }

}