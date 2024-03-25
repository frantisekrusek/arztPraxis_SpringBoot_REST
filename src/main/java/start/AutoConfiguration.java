package start;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
//@AutoConfigureBefore({JacksonAutoConfiguration.class})
public class AutoConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectmapper() {
        JavaTimeModule module = new JavaTimeModule();
        return new ObjectMapper()
                .registerModule(module)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

}