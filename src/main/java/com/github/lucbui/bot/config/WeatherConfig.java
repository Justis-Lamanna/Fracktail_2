//package com.github.lucbui.bot.config;
//
//import com.github.lucbui.openweathermap.client.OpenWeatherMapClient;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class WeatherConfig {
//    @Bean
//    @ConditionalOnProperty("weather.token")
//    public OpenWeatherMapClient openWeatherMapClient(@Value("${weather.token}") String token) {
//        return new OpenWeatherMapClient(token);
//    }
//}
