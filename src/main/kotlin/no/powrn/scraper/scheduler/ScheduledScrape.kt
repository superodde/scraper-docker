package no.powrn.scraper.scheduler

import com.fasterxml.jackson.databind.ObjectMapper
import no.powrn.scraper.constant.FIVE_MINUTES
import no.powrn.scraper.domain.Properties
import no.powrn.scraper.service.PropertyService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class ScheduledScrape(
    val propertyService: PropertyService,
    val objectMapper: ObjectMapper
) {
    @Scheduled(fixedRate = FIVE_MINUTES)
    fun scrapePropertyData() {
        println("Start scraping")
        val properties: Properties = propertyService.scrapeProperties()
        println(objectMapper.writeValueAsString(properties))
    }
}