package com.zhixuebanxing.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单（基于内存实现，用于退出登录）
 * 退出登录时，将 token 加入黑名单，过滤器会拒绝黑名单中的 token
 */
@Slf4j
@Component
public class TokenBlacklist {

    /**
     * token -> 过期时间戳（毫秒）
     */
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();

    public TokenBlacklist() {
        // 每 10 分钟清理一次已过期的 token
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "token-blacklist-cleaner");
            t.setDaemon(true);
            return t;
        }).scheduleAtFixedRate(this::cleanExpired, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * 将 token 加入黑名单
     * @param token  要拉黑的 token
     * @param expireAtMs 该 token 原本的过期时间戳（毫秒）
     */
    public void add(String token, long expireAtMs) {
        if (token != null && !token.isEmpty()) {
            blacklist.put(token, expireAtMs);
            log.debug("Token 已加入黑名单, 过期时间: {}", expireAtMs);
        }
    }

    /**
     * 检查 token 是否在黑名单中
     */
    public boolean isBlacklisted(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        Long expireAt = blacklist.get(token);
        if (expireAt == null) {
            return false;
        }
        // 如果已过期，直接移除
        if (System.currentTimeMillis() > expireAt) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    /**
     * 从黑名单中移除 token（手动解除拉黑）
     */
    public void remove(String token) {
        blacklist.remove(token);
    }

    /**
     * 清理所有已过期的 token
     */
    private void cleanExpired() {
        long now = System.currentTimeMillis();
        int removed = 0;
        for (Map.Entry<String, Long> entry : blacklist.entrySet()) {
            if (now > entry.getValue()) {
                blacklist.remove(entry.getKey());
                removed++;
            }
        }
        if (removed > 0) {
            log.debug("清理了 {} 个过期的黑名单 token", removed);
        }
    }
}
