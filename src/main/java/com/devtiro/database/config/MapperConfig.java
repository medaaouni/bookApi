package com.devtiro.database.config;


import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {


    /*
     This annotation indicates that the method returns a bean that should be managed by the Spring container.
     The bean can then be injected into other components using @Autowired.
     */
    @Bean
    public ModelMapper modelMapper() {
        /*
        the modelMapper() method returns a ModelMapper instance, which will be registered as a Spring bean.
        Using a loose matching strategy makes the ModelMapper more forgiving when mapping objects with slightly different field names.
        For example, it can map authorDto in BookDto to authorEntity in BookEntity without requiring an exact match.
         */
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper;
    }
}
