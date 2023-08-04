package no.powrn.scraper.service

import com.fasterxml.jackson.databind.ObjectMapper
import no.powrn.scraper.domain.ChangeLogEntry
import no.powrn.scraper.domain.Properties
import no.powrn.scraper.domain.SingleProperty
import no.powrn.scraper.repository.PropertyRepository
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class PropertyService(
    val propertyRepository: PropertyRepository,
    val mapper: ObjectMapper
) {
    fun scrapeProperties(): Properties {
        val properties = Properties()

        val url = "https://www.finn.no/realestate/homes/search.html?location=1.22030.20024&sort=PUBLISHED_DESC"
        val doc: Document = Jsoup.connect(url).get()
        val bannerTitle: String = doc.select("nav.breadcrumbs").text()
        println("Banner: $bannerTitle")

        val currentInstant: Instant = Instant.now()
        val currentTimeStamp: Long = currentInstant.toEpochMilli()

        var countScraped = 0
        var countCreated = 0
        var countUpdated = 0

        val propertyBlocks: Elements = doc.select("article.f-card")
        for (i in 0 until propertyBlocks.size) {
            countScraped++
            var link = ""
            try {
                val propertyBlock = propertyBlocks[i]
                link = propertyBlock.select("a")[0].attr("href")
                var id = link.substringAfter("=")
                id = if (id.contains("&")) id.substringBefore("&") else id
                val propertyInfo = propertyBlock.select("div.grid")
                if (propertyInfo.size > 0) {
                    val address = propertyInfo.select("div.sf-realestate-location")
                    val size = propertyInfo.select("div.flex.justify-between")[0].child(0)
                    val price = propertyInfo.select("div.flex.justify-between")[0].child(1)
                    val priceTotal = propertyInfo.select("div.text-gray-500")[0].child(0)
                    val details =
                        propertyInfo.select("div.text-gray-500")[0].child(2).text().split(" ∙ ").toTypedArray()

                    val property = SingleProperty(
                        id = id,
                        url = link,
                        address = address.text(),
                        price = price.text(),
                        priceTotal = priceTotal.text(),
                        sizeProperty = null,
                        sizeHouse = size.text(),
                        ownership = details[0],
                        typeHouse = details[1],
                        rooms = if (details.size > 2) details[2] else null,
                        created = currentTimeStamp,
                        updated = currentTimeStamp,
                        changeLog = listOf()
                    )

                    val storedProperty = getProperty(id)
                    if (storedProperty == null) {
                        countCreated++
                        properties.properties.add(property)
                        propertyRepository.saveProperty(property)
                    } else {
                        if (hasPropertyChanged(property, storedProperty)) {
                            countUpdated++
                            val updatedProperty = updateProperty(property, storedProperty, currentTimeStamp)
                            properties.properties.add(updatedProperty)
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error for $link")
                println(e.message)
            }
        }

        println("Scraped: $countScraped")
        println("Created: $countCreated")
        println("Updated: $countUpdated")

        return properties
    }

    fun getProperty(id: String): SingleProperty? {
        return try {
            val property = propertyRepository.findPropertyById(id)
            property
        } catch (e: Exception) {
            null
        }
    }

    fun getAllProperties(): List<SingleProperty> {
        return propertyRepository.findAllProperties()
    }

    fun hasPropertyChanged(property: SingleProperty, storedProperty: SingleProperty): Boolean {
        val propertyString = mapper.writeValueAsString(property)
        val storedPropertyString = mapper.writeValueAsString(storedProperty)
        return propertyString.equals(storedPropertyString)
    }

    fun updateProperty(
        property: SingleProperty,
        storedProperty: SingleProperty,
        currentTimeStamp: Long
    ): SingleProperty {
        val changeLog = storedProperty.changeLog.toMutableList()

        if (property.address != storedProperty.address) {
            changeLog.add(
                ChangeLogEntry(
                    item = "address",
                    oldValue = storedProperty.address,
                    newValue = property.address,
                    changed = currentTimeStamp
                )
            )
        }

        return SingleProperty(
            id = property.id,
            url = property.url,
            address = property.address,
            price = property.price,
            priceTotal = property.priceTotal,
            sizeProperty = null,
            sizeHouse = property.sizeHouse,
            ownership = property.ownership,
            typeHouse = property.typeHouse,
            rooms = property.rooms,
            created = property.created,
            updated = currentTimeStamp,
            changeLog = changeLog
        )
    }
}