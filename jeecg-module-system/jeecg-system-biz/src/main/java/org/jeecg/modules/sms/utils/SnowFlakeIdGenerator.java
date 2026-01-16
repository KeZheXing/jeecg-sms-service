package org.jeecg.modules.sms.utils;

public class SnowFlakeIdGenerator {
    // ====================== 雪花算法核心参数 ======================
    // 起始的时间戳 (2026-01-15，可自定义，越早越好，能使用更久)
    private final static long START_TIMESTAMP = 1736908800000L;

    // 每一部分的位数
    private final static long SEQUENCE_BIT = 12; // 序列号占用的位数 0-4095
    private final static long MACHINE_BIT = 10;   // 机器标识占用的位数 0-1023

    // 每一部分的最大值
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    // 每一部分向左的位移
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long TIMESTAMP_LEFT = SEQUENCE_BIT + MACHINE_BIT;

    private long machineId = 1;    // 机器ID（0-1023，可配置到配置文件）
    private long sequence = 0L;    // 序列号
    private long lastTimeStamp = -1L; // 上一次的时间戳

    // 单例实例
    private static final SnowFlakeIdGenerator INSTANCE = new SnowFlakeIdGenerator();

    // 私有构造，禁止外部new
    private SnowFlakeIdGenerator(){}

    // 获取单例对象
    public static SnowFlakeIdGenerator getInstance(){
        return INSTANCE;
    }

    // ====================== 核心生成方法 ======================
    public synchronized long nextId(){
        long currTimeStamp = getCurrentTimeStamp();
        // 处理时钟回拨问题：当前时间小于上一次生成ID的时间，抛出异常
        if(currTimeStamp < lastTimeStamp){
            throw new RuntimeException("时钟回拨！拒绝生成雪花ID");
        }

        if(currTimeStamp == lastTimeStamp){
            // 同一毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数耗尽，等待下一毫秒
            if(sequence == 0L){
                currTimeStamp = getNextMill();
            }
        } else {
            // 不同毫秒，序列号重置为0
            sequence = 0L;
        }

        lastTimeStamp = currTimeStamp;

        // 拼接最终ID：时间戳左移 + 机器ID左移 + 序列号
        return (currTimeStamp - START_TIMESTAMP) << TIMESTAMP_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    // 阻塞到下一个毫秒，直到获得新的时间戳
    private long getNextMill(){
        long mill = getCurrentTimeStamp();
        while (mill <= lastTimeStamp) {
            mill = getCurrentTimeStamp();
        }
        return mill;
    }

    // 获取当前毫秒级时间戳
    private long getCurrentTimeStamp(){
        return System.currentTimeMillis();
    }
}