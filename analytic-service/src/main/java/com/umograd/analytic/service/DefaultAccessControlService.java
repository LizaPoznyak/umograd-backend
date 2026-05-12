package com.umograd.analytic.service;

import com.umograd.analytic.dto.AccessResponse;
import com.umograd.analytic.repository.UserAnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultAccessControlService implements AccessControlService {

    private final UserAnalyticsRepository userRepository;

    public AccessResponse checkAccess(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    boolean isUnderage = user.getAge() < 16;
                    boolean noConsent = !user.isParentConsent();
                    if (isUnderage && noConsent) {
                        return new AccessResponse(false,
                                "Пользователям младше 16 лет требуется согласие родителя.");
                    }
                    return new AccessResponse(true, "Доступ разрешен");
                })
                .orElse(new AccessResponse(false, "Пользователь не найден"));
    }
}
