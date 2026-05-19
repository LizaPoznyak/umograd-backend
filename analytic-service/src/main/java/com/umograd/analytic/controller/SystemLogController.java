package com.umograd.analytic.controller;

import com.umograd.analytic.dto.SessionStatusDto;
import com.umograd.analytic.dto.SystemLogDto;
import com.umograd.analytic.entity.ParentAgeLimitEntity;
import com.umograd.analytic.repository.analytic.ParentAgeLimitRepository;
import com.umograd.analytic.service.SystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    private final ParentAgeLimitRepository limitRepository;

    private static final String REDIS_PREFIX = "active_session:";
    private static final String REDIS_TIME_PREFIX = "heartbeat:time:daily:";

    @GetMapping("/monitoring/errors")
    public List<SystemLogDto> getErrorLogs() {
        return service.findLogs("ERROR");
    }

    @PostMapping("/monitoring/heartbeat/test")
    public SessionStatusDto receiveHeartbeat(
            @RequestHeader("Authorization") String token,
            @RequestParam Long childId,
            @RequestParam Long parentId,
            @RequestParam int age) {

        String cleanToken = token.replace("Bearer ", "");
        String activeKey = REDIS_PREFIX + cleanToken;
        redisTemplate.opsForValue().set(activeKey, "active", 15, TimeUnit.SECONDS);

        String dateSuffix = LocalDate.now().toString();
        String timeKey = REDIS_TIME_PREFIX + childId + ":" + dateSuffix;

        redisTemplate.opsForValue().increment(timeKey, 10);
        redisTemplate.expire(timeKey, 25, TimeUnit.HOURS);

        String rawSeconds = redisTemplate.opsForValue().get(timeKey);
        long secondsUsed = rawSeconds != null ? Long.parseLong(rawSeconds) : 0;
        long minutesUsedToday = secondsUsed / 60;

        int maxMinutes = limitRepository.findByParentIdAndAge(parentId, age)
                .map(ParentAgeLimitEntity::getMaxMinutes)
                .orElse(45);

        if (minutesUsedToday >= maxMinutes) {
            return new SessionStatusDto("BLOCKED", "Время сессии исчерпано согласно настройкам родителя.", 0);
        }

        return new SessionStatusDto("ACTIVE", "Сессия активна", maxMinutes - minutesUsedToday);
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
