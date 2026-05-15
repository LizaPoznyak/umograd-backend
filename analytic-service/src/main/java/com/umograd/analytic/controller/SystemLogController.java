package com.umograd.analytic.controller;

import com.umograd.analytic.dto.SystemLogDto;
import com.umograd.analytic.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/analytics/logs")
public class SystemLogController {

    private final StringRedisTemplate redisTemplate;

    private final SystemLogService service;

    private static final String REDIS_PREFIX = "active_session:";

    @GetMapping("/monitoring/errors")
    public List<SystemLogDto> getErrorLogs() {
        return service.findLogs("ERROR");
    }

    @PostMapping("/monitoring/heartbeat")
    public void receiveHeartbeat(@RequestHeader("Authorization") String token) {
        if (token != null) {
            String key = REDIS_PREFIX + token.replace("Bearer ", "");
            redisTemplate.opsForValue().set(key, "active", 15, TimeUnit.SECONDS);
        }
    }

    @GetMapping("/monitoring/active-sessions")
    public Map<String, Integer> getActiveSessionsCount() {
        Set<String> keys = redisTemplate.keys(REDIS_PREFIX + "*");
        int count = keys.size();

        if (count == 0) count = 1;

        return Map.of("count", count);
    }
}
