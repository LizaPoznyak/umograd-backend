package com.umograd.content.security;

import java.util.List;

/**
 * Port to decode/validate tokens. Implement against your auth-service.
 */
public interface TokenDecoder {
    Claims decode(String token);

    record Claims(String username, List<String> roles, Long childId, Long userId, String email) {}
}
