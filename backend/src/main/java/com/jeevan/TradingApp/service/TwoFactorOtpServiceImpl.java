package com.jeevan.TradingApp.service;

import com.jeevan.TradingApp.modal.TwoFactorOTP;
import com.jeevan.TradingApp.modal.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class TwoFactorOtpServiceImpl implements TwoFactorOtpService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String OTP_PREFIX_USER = "otp:user:";
    private static final String OTP_PREFIX_ID = "otp:id:";

    @Override
    public TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt) {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        TwoFactorOTP twoFactorOTP = new TwoFactorOTP();
        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setJwt(jwt);
        twoFactorOTP.setId(id);
        twoFactorOTP.setUserId(user.getId());

        // Save to Redis with 5 minutes TTL
        redisTemplate.opsForValue().set(OTP_PREFIX_ID + id, twoFactorOTP, Duration.ofMinutes(5));
        redisTemplate.opsForValue().set(OTP_PREFIX_USER + user.getId(), id, Duration.ofMinutes(5));

        return twoFactorOTP;
    }

    @Override
    public TwoFactorOTP findByUser(Long userId) {
        String id = (String) redisTemplate.opsForValue().get(OTP_PREFIX_USER + userId);
        if (id != null) {
            return findById(id);
        }
        return null;
    }

    @Override
    public TwoFactorOTP findById(String id) {
        return (TwoFactorOTP) redisTemplate.opsForValue().get(OTP_PREFIX_ID + id);
    }

    @Override
    public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP, String otp) {
        return twoFactorOTP.getOtp().equals(otp);
    }

    @Override
    public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP) {
        redisTemplate.delete(OTP_PREFIX_ID + twoFactorOTP.getId());
        redisTemplate.delete(OTP_PREFIX_USER + twoFactorOTP.getUserId());
    }
}
