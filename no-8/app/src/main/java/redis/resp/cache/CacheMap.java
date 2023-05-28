package redis.resp.cache;

import java.util.TreeMap;

import redis.resp.IRespBuilder;
import redis.resp.RespException;
import redis.resp.cache.RedisCache.RedisCacheContext;
import redis.resp.types.RespArray;
import redis.resp.types.RespSortedMap;
import redis.resp.types.RespType;

public class CacheMap extends TreeMap<String, RedisCacheContext> implements IRespBuilder {

    private RedisCache cache;

    public CacheMap(RedisCache cache, RespArray data) throws RespException {
        this.cache = cache;
        loadFrom(data);
    }

    public CacheMap(RedisCache cache) {
        this.cache = cache;
    }

    @Override
    public void toRespString(StringBuilder buffer) {
        this.toRespType().toRespString(buffer);
    }

    @Override
    public RespType toRespType() {
        var map = new RespSortedMap();
        forEach((key, value) -> {
            map.put(key, value.toRespType());
        });
        return map;
    }

    @Override
    public void loadFrom(RespArray data) throws RespException {
        var map = data.arrayToMap();
        map.forEach((key, value) -> {
            try {
                this.put(key, new RedisCacheContext(this.cache, (RespArray) value));
            } catch (RespException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

}
