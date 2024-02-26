package com.company.orderservice.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "order.service")
@Getter
@Setter
public class ApplicationProperties {

    private String inventoryServiceBaseURL;
}
