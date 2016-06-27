package com.ai.slp.route.common.util;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class RedisUtil {

    private static Logger logger = LogManager.getLogger(RedisUtil.class);

    private static JedisPool jedisPool;
    private static JedisCluster jedisCluster;
    private static boolean isCluster = false;

    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = RedisUtil.class.getResourceAsStream("/redis.properties");
            properties.load(inputStream);

            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
            config.setMaxIdle(Integer.parseInt(properties.getProperty("redis.maxIdle", "10")));
            config.setMaxTotal(Integer.parseInt(properties.getProperty("redis.maxTotal", "30")));
            config.setMinIdle(Integer.parseInt(properties.getProperty("redis.minIdle", "5")));

            if ("single".equals(properties.getProperty("redis.mode", "single"))) {
                String host = properties.getProperty("redis.host", "127.0.0.1");
                int port = Integer.parseInt(properties.getProperty("redis.port", "6379"));
                jedisPool = new JedisPool(config, host, port);
            } else {
                String hosts = properties.getProperty("redis.host", "127.0.0.1");
                String[] hostArray = hosts.split(";");
                Set<HostAndPort> hostAndPorts = new HashSet<HostAndPort>();
                for (String host : hostArray) {
                    String[] hostAndPort = host.split(":");
                    hostAndPorts.add(new HostAndPort(hostAndPort[0], Integer.parseInt(hostAndPort[1])));
                }
                jedisCluster = new JedisCluster(hostAndPorts, config);
                isCluster = true;
            }
        } catch (Exception e) {
            System.exit(-1);
        }
    }

    public static String load(String key) {
        if (isCluster) {
            return jedisCluster.get(key);
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return jedis.get(key);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("Cannot get resource from redis", e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    public static String hLoad(String key, String field) {
        if (isCluster) {
            return jedisCluster.hget(key, field);
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return jedis.hget(key, field);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("Cannot get resource from redis", e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    public static Map<String, String> hLoads(String key) {
        if (isCluster) {
            return jedisCluster.hgetAll(key);
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return jedis.hgetAll(key);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("Cannot get resource from redis", e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    public static void expire(String key) {
        if (isCluster) {
            jedisCluster.expire(key, 0);
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.expire(key, 0);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("Cannot get resource from redis", e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    public static void put(String key, String value, long expireTime) {
        if (isCluster) {
            jedisCluster.set(key, value);
            jedisCluster.expireAt(key, expireTime);
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.set(key, value);
                jedis.expireAt(key, expireTime);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("Cannot get resource from redis", e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    public static void put(String key, String value) {
        if (isCluster) {
            jedisCluster.set(key, value);
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.set(key, value);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("Cannot get resource from redis", e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }


    public static double atomIncrement(String key, float value) {
        if (isCluster) {
            return jedisCluster.incrByFloat(key, value);
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return jedis.incrByFloat(key, value);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("Cannot get resource from redis", e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    public static double atomDecrement(String key, float value) {
        if (isCluster) {
            return jedisCluster.incrByFloat(key, (-1) * value);
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                return jedis.incrByFloat(key, (-1) * value);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("Cannot get resource from redis", e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    public static void putnx(String key, String value, long expireTime) {
        if (isCluster) {
            jedisCluster.setnx(key, value);
            jedisCluster.expireAt(key, expireTime);
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.setnx(key, value);
                jedis.expireAt(key, expireTime);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("Cannot get resource from redis", e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    public static void hput(String key, Map<String, String> fields) {
        if (isCluster) {
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                jedisCluster.hset(key, entry.getKey(), entry.getValue());
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                for (Map.Entry<String, String> entry : fields.entrySet()) {
                    jedis.hset(key, entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("Cannot get resource from redis", e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    public static void hput(String key, String fields, String value) {
        if (isCluster) {
            jedisCluster.hset(key, fields, value);
        } else {
            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                jedis.hset(key, fields, value);
            } catch (Exception e) {
                logger.error(e);
                throw new RuntimeException("Cannot get resource from redis", e);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }
}
