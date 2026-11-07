package com.pharmacy.drugs.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Serializes every {@link BigDecimal} (all monetary values in this service) with
 * a fixed scale of 2, so amounts always render as e.g. {@code 28.90} in JSON.
 */
@Configuration
public class JacksonConfig {
    
    @Bean
    JsonMapperBuilderCustomizer moneyScaleCustomizer() {
        SimpleModule moneyModule = new SimpleModule("money-scale")
                                       .addSerializer(BigDecimal.class, new ValueSerializer<BigDecimal>() {
                                           @Override
                                           public void serialize(BigDecimal value, JsonGenerator gen,
                                                                 SerializationContext ctxt) {
                                               gen.writeNumber(value.setScale(2, RoundingMode.HALF_UP));
                                           }
                                       });
        return builder -> builder.addModule(moneyModule);
    }
}
