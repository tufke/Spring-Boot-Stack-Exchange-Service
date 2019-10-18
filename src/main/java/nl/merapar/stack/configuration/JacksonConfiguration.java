package nl.merapar.stack.configuration;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 
 * set extra jackson properties not available in application.properties
 *
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customJson(){
        return builder -> {
            builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT);
        };
    }	
	
}
