package com.revtalent.recruitment_service.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            String message = "Feign client error";
            if (response.body() != null) {
                InputStream inputStream = response.body().asInputStream();
                message = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
            HttpStatus status = HttpStatus.valueOf(response.status());
            return new ResponseStatusException(status, message);
        } catch (Exception e) {
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
