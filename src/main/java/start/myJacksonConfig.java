package start;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
//@AutoConfigureBefore({JacksonAutoConfiguration.class})
public class myJacksonConfig {

    //serialize Java objects into JSON and deserialize JSON into Java objects
    //here: LocalDateTime and ZonedDateTime properties in Template and Application
    @Bean
    @Primary
    public ObjectMapper objectmapper() {
        JavaTimeModule module = new JavaTimeModule();
        return new ObjectMapper()
                .registerModule(module)
                .enable(SerializationFeature.INDENT_OUTPUT);
    }
}