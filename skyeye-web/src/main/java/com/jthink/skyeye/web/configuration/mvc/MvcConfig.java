package com.jthink.skyeye.web.configuration.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;

/**
 * JThink@JThink
 *
 * @author JThink
 * @version 0.0.1
 * @desc mvc 配置
 * @date 2016-10-08 10:52:10
 */
@Configuration
public class MvcConfig {

    @Bean
    public HttpMessageConverters getJacksonHttpMessageConverters(ObjectMapper objectMapper) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON_UTF8, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN));
        return new HttpMessageConverters(converter);
    }
}
