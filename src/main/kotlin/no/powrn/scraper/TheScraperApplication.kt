package no.powrn.scraper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TheScraperApplication

fun main(args: Array<String>) {
    runApplication<TheScraperApplication>(*args)
}
