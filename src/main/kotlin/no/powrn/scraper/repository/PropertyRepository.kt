package no.powrn.scraper.repository

import no.powrn.scraper.DynamoDbConfiguration
import no.powrn.scraper.domain.SingleProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.pagination.sync.SdkIterable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest


@Service
class PropertyRepository(
    dynamoDbClientEnhanced: DynamoDbEnhancedClient,
    dynamoDbConfigProperties: DynamoDbConfiguration
) {
    private val logger: Logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private val tableSchema = TableSchema.fromBean(SingleProperty::class.java)
    }

    private val table = dynamoDbClientEnhanced.table(dynamoDbConfigProperties.propertyTableName, tableSchema)

    fun saveProperty(property: SingleProperty): SingleProperty {
        logger.debug("Saving property with id '${property.id}'")
        table.putItem(property)
        return property
    }

    fun findPropertyById(propertyId: String): SingleProperty {
        logger.debug("Finding property with id '$propertyId'")

        val queryConditional: QueryConditional = QueryConditional
            .keyEqualTo(
                Key.builder().partitionValue(propertyId)
                    .build()
            )
        val singleProperties: SdkIterable<SingleProperty> = table.query { r: QueryEnhancedRequest.Builder ->
            r.queryConditional(
                queryConditional
            )
        }.items()

        return singleProperties.first()

    }

    fun findAllProperties(): List<SingleProperty> {
        logger.debug("Finding all properties")
        val propertList = mutableListOf<SingleProperty>()
        val scanResult = table.scan()
        scanResult.items().forEach { property ->
            propertList.add(property)
        }
        return propertList
    }
}