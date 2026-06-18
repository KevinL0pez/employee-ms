package com.parameta.rrhh.employee.config;

import com.parameta.rrhh.employee.soap.EmployeeType;
import com.parameta.rrhh.employee.soap.SaveEmployeeRequest;
import com.parameta.rrhh.employee.soap.SaveEmployeeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * JAXB and {@link WebServiceTemplate} beans for the SOAP client.
 */
@Configuration
public class SoapClientConfig {

    @Value("${soap.service.url}")
    private String soapServiceUrl;

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(
                SaveEmployeeRequest.class,
                SaveEmployeeResponse.class,
                EmployeeType.class
        );
        return marshaller;
    }

    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate(marshaller);
        webServiceTemplate.setDefaultUri(soapServiceUrl);
        return webServiceTemplate;
    }
}
