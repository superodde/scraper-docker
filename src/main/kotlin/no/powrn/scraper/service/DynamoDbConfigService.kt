package no.powrn.scraper.service

import no.powrn.scraper.DynamoDbConfiguration
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*


@Service
class DynamoDbConfigService(
    private val dynamoDbClient: DynamoDbClient,
    private val dynamoDbConfiguration: DynamoDbConfiguration
) {

    @EventListener(ApplicationReadyEvent::class)
    fun generateTables() {
        val listOfTables = dynamoDbClient.listTables().tableNames()
        if (!listOfTables.contains(dynamoDbConfiguration.propertyTableName)) {
            createTableProperties()
        }
    }
    
    fun createTableProperties() {
        // id : hash
        val attributeId = buildAttributeDefinition("id", ScalarAttributeType.S)
        val keyId = buildKeySchemaElement("id", KeyType.HASH)

        // updated : range
        val attributeUpdated = buildAttributeDefinition("updated", ScalarAttributeType.N)
        val keyUpdated = buildKeySchemaElement("updated", KeyType.RANGE)

        // Read and write
        val provisionedVal = ProvisionedThroughput.builder()
            .readCapacityUnits(5)
            .writeCapacityUnits(5)
            .build()

        // Create table
        val request = CreateTableRequest.builder()
            .attributeDefinitions(listOf(attributeId, attributeUpdated))
            .keySchema(listOf(keyId, keyUpdated))
            .provisionedThroughput(provisionedVal)
            .tableName(dynamoDbConfiguration.propertyTableName)
            .build()

        dynamoDbClient.createTable(request)
    }

    private fun buildAttributeDefinition(attributeName: String, scalarType: ScalarAttributeType): AttributeDefinition {
        return AttributeDefinition.builder()
            .attributeName(attributeName)
            .attributeType(scalarType)
            .build()
    }

    private fun buildKeySchemaElement(attributeName: String, keyType: KeyType): KeySchemaElement {
        return KeySchemaElement.builder()
            .attributeName(attributeName)
            .keyType(keyType)
            .build()
    }
}