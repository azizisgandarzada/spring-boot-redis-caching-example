package com.azizi.app.store.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisCacheKeyConstants {

    public static final String USER_HASH_KEY = "user";
    public static final String APP_HASH_KEY = "app";
    public static final String DOWNLOADERS_LIST_KEY = "downloaders:{appId}";
    public static final String APPS_ZSET_KEY = "apps";

}
