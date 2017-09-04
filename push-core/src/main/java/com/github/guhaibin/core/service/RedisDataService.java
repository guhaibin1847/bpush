package com.github.guhaibin.core.service;

import com.github.guhaibin.api.Config;
import com.github.guhaibin.api.push.User;
import com.github.guhaibin.api.push.UserType;
import com.github.guhaibin.api.spi.common.CacheManager;
import com.github.guhaibin.api.spi.service.DataService;
import com.github.guhaibin.utils.common.IdGen;
import org.apache.commons.lang3.StringUtils;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Project : bpush
 * Author  : Bean
 * Contact : guhaibin1847@gmail.com
 */
public class RedisDataService implements DataService {

    private static final String PREFIX = Config.RedisConf.prefix;
    private static final String U2C = PREFIX + "u2c";//map
    private static final String U2C_STORE = U2C + ":store";
    private static final String U2C_ADMIN = U2C + ":admin";
    private static final String U2C_WX = U2C + ":wx";

    private static final String C2U = PREFIX + "c2u";//map
    private static final String C2U_STORE = C2U + ":store";
    private static final String C2U_ADMIN = C2U + ":admin";
    private static final String C2U_WX = C2U + ":wx";

    private static final String USERS = PREFIX + "users";//zset
    private static final String USERS_STORE = USERS + ":store";
    private static final String USERS_ADMIN = USERS + ":admin";
    private static final String USERS_WX = USERS + ":wx";

    private static final String TAG = PREFIX + "tag";//set

    private static final String U2T = PREFIX + "u2t";//set
    private static final String U2T_STORE = U2T + ":store";
    private static final String U2T_ADMIN = U2T + ":admin";
    private static final String U2T_WX = U2T + ":wx";

    private static RedisDataService INSTANCE;
    private CacheManager cacheManager;


    private RedisDataService(CacheManager cacheManager){
        this.cacheManager = cacheManager;
    }

    public static synchronized RedisDataService me(CacheManager cacheManager){
        if (INSTANCE == null){
            INSTANCE = new RedisDataService(cacheManager);
        }
        return INSTANCE;
    }


    @Override
    public void addUser(User user, String channelId, String tag) {

        cacheManager.hset(U2C, IdGen.getUserId(user), channelId);
        cacheManager.hset(C2U, channelId, user);

        if (StringUtils.isNotBlank(tag)){
            cacheManager.sadd(TAG + ":" + tag, user);
            cacheManager.sadd(U2T + ":" + IdGen.getUserId(user), tag);
        }
        long current = System.currentTimeMillis();
        cacheManager.zadd(USERS, user, (double)current);
    }

    @Override
    public String findChannelId(User user) {
        return cacheManager.hget(U2C, IdGen.getUserId(user), String.class);
    }

    @Override
    public User findUser(String channelId) {
        return cacheManager.hget(C2U, channelId, User.class);
    }

    @Override
    public void removeUser(User user) {
        String channelId = findChannelId(user);
        cacheManager.hrem(U2C, IdGen.getUserId(user));
        if (channelId != null) {
            cacheManager.hrem(C2U, channelId);
        }
        Set<String> tags = cacheManager.sget(U2T + ":" + IdGen.getUserId(user));
        if (tags != null && !tags.isEmpty()){
            for (String tag : tags) {
                cacheManager.srem(TAG + ":" + tag, user);
            }
        }
        cacheManager.del(U2T + ":" + IdGen.getUserId(user));
        cacheManager.zrem(USERS, user);
    }

    @Override
    public Set<String> findChannelIdsByTag(String tag) {
        return cacheManager.sget(tag);
    }

    @Override
    public void removeTag(String tag) {
        Set<String> userIds = cacheManager.sget(TAG + ":" + tag);
        if (userIds != null && !userIds.isEmpty()){
            for (String userId : userIds){
                cacheManager.srem(U2T + ":" + userId, tag);
            }
        }
        cacheManager.del(TAG + ":" + tag);
    }

    @Override
    public boolean online(User user) {
        return cacheManager.zisMember(USERS, user);
    }

    @Override
    public void clearAll() {
        cacheManager.del(U2T);
        cacheManager.del(U2C);
        cacheManager.del(C2U);
        cacheManager.del(USERS);
    }


}
