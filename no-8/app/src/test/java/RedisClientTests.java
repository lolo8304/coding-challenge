import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class RedisClientTests {

    @Test
    public void jediscalls_client_expectpong() {

        try (Jedis jedis = new Jedis("localhost", 6380)) {

            // issue commands using the Jedis object
            jedis.set("key1", "value1");
            jedis.set("key2", "value2");
            jedis.set("key3", "value3");

            var result1 = jedis.get("key1");
            var result2 = jedis.get("key2");
            var result3 = jedis.get("key3");

            // Assert
            assertEquals("value1", result1);
            assertEquals("value2", result2);
            assertEquals("value3", result3);

        }

    }

    @Test
    public void jedispipeline_client_expectmultivalues() {
        try (Jedis jedis = new Jedis("localhost", 6380)) {
            // Create a pipeline object
            Pipeline pipeline = jedis.pipelined();

            // Execute multiple commands using pipeline
            pipeline.set("key1", "value1");
            pipeline.get("key1");
            var map = new HashMap<String, String>();
            map.put("name", "John");
            map.put("age", "30");
            pipeline.hmset("user1", map);
            pipeline.hget("user1", "name");

            // Send commands to Redis server and receive response
            Response<String> getResponse = pipeline.get("key1");
            Response<String> hgetResponse = pipeline.hget("user1", "name");
            pipeline.sync();

            // Assert
            assertEquals("value1", getResponse.get());
            assertEquals("John", hgetResponse.get());
        }
    }

}
