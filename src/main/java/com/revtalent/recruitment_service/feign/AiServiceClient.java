package com.revtalent.recruitment_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "ai-service")
public interface AiServiceClient {

    @PostMapping("/api/ai/screen-resume")
    Map<String, Object> screenResume(@RequestBody Map<String, String> requestBody);

}
