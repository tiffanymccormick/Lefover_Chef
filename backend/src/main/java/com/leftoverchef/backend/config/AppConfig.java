package com.leftoverchef.backend.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.leftoverchef.backend.controller",
    "com.leftoverchef.backend.service"
})
public class AppConfig {
}
