package no.powrn.scraper

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient


@Configuration
@EnableScheduling
@ConfigurationPropertiesScan
class TheScraperConfiguration(
    private val dynamoDbConfigProperties: DynamoDbConfiguration
) {
    @Bean
    fun dynamoDbClient(): DynamoDbClient = DynamoDbClient.builder()
        .region(Region.of(dynamoDbConfigProperties.region))
        .credentialsProvider(ProfileCredentialsProvider.create("scraper"))
        .build()

    @Bean
    fun dynamoDbEnhancedClient(): DynamoDbEnhancedClient {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient())
            .build()
    }
}