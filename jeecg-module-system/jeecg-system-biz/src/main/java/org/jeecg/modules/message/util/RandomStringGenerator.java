package org.jeecg.modules.message.util;

import java.util.Random;

public class RandomStringGenerator {
    // 定义字符集（可根据需求调整，如仅数字、仅字母等）
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LENGTH = 10; // 目标长度

    public static String generateRandomString() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            // 随机从字符集中获取一个字符
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(generateRandomString()); // 示例输出：xY3pQ7sT2k
    }
}