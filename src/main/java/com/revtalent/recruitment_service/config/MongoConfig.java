package com.revtalent.recruitment_service.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/revtalent_mongo}")
    private String mongoUri;

    @Bean
    @Primary
    public MongoClient mongoClient(org.springframework.core.env.Environment env) {
        String mongoUri = env.getProperty("spring.data.mongodb.uri", "mongodb://localhost:27017/revtalent_mongo");
        System.out.println("=========================================================");
        System.out.println("CREATING CUSTOM MONGO CLIENT WITH URI FROM ENV: " + mongoUri);
        System.out.println("=========================================================");
        return MongoClients.create(mongoUri);
    }
    
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "revtalent_mongo");
    }
}
