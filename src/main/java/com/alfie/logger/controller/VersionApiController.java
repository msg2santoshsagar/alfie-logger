package com.alfie.logger.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class VersionApiController {

    @GetMapping("/version")
    public Map<String, String> getVersion() {
        log.info("Version api called");
        return Map.of("status", "UP");
    }

}
