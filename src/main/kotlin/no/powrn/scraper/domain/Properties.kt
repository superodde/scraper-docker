package no.powrn.scraper.domain

data class Properties(
    val id: String? = null,
    val properties: MutableList<SingleProperty> = mutableListOf()
)
