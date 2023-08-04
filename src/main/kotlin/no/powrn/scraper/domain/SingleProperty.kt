package no.powrn.scraper.domain

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
data class SingleProperty(
    @get:DynamoDbPartitionKey   //HASH
    var id: String? = null,

    var url: String? = null,
    var address: String? = null,

    var price: String? = null,
    var priceTotal: String? = null,
    var sizeProperty: String? = null,
    var sizeHouse: String? = null,
    var ownership: String? = null,
    var typeHouse: String? = null,
    var rooms: String? = null,

    @get:DynamoDbSortKey    //RANGE
    var updated: Long? = null,
    var created: Long? = null,

    var changeLog: List<ChangeLogEntry> = listOf()
)

@DynamoDbBean
data class ChangeLogEntry(
    var item: String? = null,
    var oldValue: String? = null,
    var newValue: String? = null,
    var changed: Long? = null
)
