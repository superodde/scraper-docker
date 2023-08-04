package no.powrn.scraper

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application.dynamo")
class DynamoDbConfiguration(
    val propertyTableName: String,
    val region: String,
    val endpoint: String
)