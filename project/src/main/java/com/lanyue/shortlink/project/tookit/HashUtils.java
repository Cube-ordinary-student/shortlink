package com.lanyue.shortlink.project.tookit;

import cn.hutool.core.util.HashUtil;

public class HashUtils {
    private static final char[] base62 = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D','E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N','O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X','Y', 'Z'
    };

    public static String convert62Base(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % 62);
            sb.append(base62[remainder]);
            num /= 62;
        }
        return sb.reverse().toString();
    }

    public static String generateShortLinkSuffix(String originUrl) {
        byte[] bytes = originUrl.getBytes();
        int hash = HashUtil.murmur32(bytes);
        long murmur32 = hash < 0 ? Integer.MAX_VALUE - (long) hash : hash;
        return convert62Base(murmur32);
    }

    public static void main(String[] args) {
        System.out.println(generateShortLinkSuffix("https://www.baidu.com"));
    }
}
