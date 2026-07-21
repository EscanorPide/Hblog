package com.hehaoran.hblog.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author:
 * @url:
 * @date: 2026/7/21
 * @description: 敏感词 DFA 过滤器
 **/
public class SensitiveWordUtil {

    /** 最少匹配字数：单字词库极易误伤正常汉字，默认忽略长度 < 2 的词 */
    private static final int MIN_WORD_LENGTH = 2;

    // 敏感词 Trie 树根节点
    private static final Map<Character, Object> sensitiveWordMap = new HashMap<>();

    // 标识是否为敏感词的最后一个字符
    private static final String IS_END = "isEnd";

    /**
     * 初始化敏感词库（将敏感词构建成 Trie 树）
     *
     * @param sensitiveWords 敏感词集合
     */
    public static synchronized void init(Set<String> sensitiveWords) {
        sensitiveWordMap.clear();
        if (sensitiveWords == null || sensitiveWords.isEmpty()) {
            return;
        }
        for (String word : sensitiveWords) {
            if (word == null) {
                continue;
            }
            String w = word.trim();
            // 跳过空词、单字，避免「真/日/人」等正常用字被整段打码
            if (w.length() < MIN_WORD_LENGTH) {
                continue;
            }
            Map currentMap = sensitiveWordMap;
            for (int i = 0; i < w.length(); i++) {
                char c = w.charAt(i);
                Map subMap = (Map) currentMap.get(c);
                if (subMap == null) {
                    subMap = new HashMap<>();
                    subMap.put(IS_END, "0");
                    currentMap.put(c, subMap);
                }
                currentMap = subMap;
                if (i == w.length() - 1) {
                    currentMap.put(IS_END, "1"); // 标记词语结束
                }
            }
        }
    }

    /** 当前词库是否已初始化（根节点非空） */
    public static boolean isReady() {
        return !sensitiveWordMap.isEmpty();
    }

    /**
     * 检查文本中从 beginIndex 开始的敏感词长度（最长匹配）
     *
     * @param text       待检查文本
     * @param beginIndex 起始索引
     * @return 匹配到的敏感词长度，未匹配到返回 0
     */
    private static int checkSensitiveWord(String text, int beginIndex) {
        Map currentMap = sensitiveWordMap;
        int matchLen = 0;
        int tempLen = 0;

        for (int i = beginIndex; i < text.length(); i++) {
            char c = text.charAt(i);
            currentMap = (Map) currentMap.get(c);

            if (currentMap != null) {
                tempLen++;
                if ("1".equals(currentMap.get(IS_END)) && tempLen >= MIN_WORD_LENGTH) {
                    matchLen = tempLen; // 匹配到完整敏感词，记录最长长度
                }
            } else {
                break;
            }
        }
        return matchLen;
    }

    /**
     * 纯英文/数字敏感词要求独立成词，避免 Java 中的 av、class 中的 ass 等被误伤
     */
    private static boolean isStandaloneMatch(String text, int start, int length) {
        boolean asciiWord = true;
        for (int i = start; i < start + length; i++) {
            if (!isAsciiLetterOrDigit(text.charAt(i))) {
                asciiWord = false;
                break;
            }
        }
        if (!asciiWord) {
            return true;
        }
        char before = start > 0 ? text.charAt(start - 1) : 0;
        char after = start + length < text.length() ? text.charAt(start + length) : 0;
        return !isAsciiLetterOrDigit(before) && !isAsciiLetterOrDigit(after);
    }

    private static boolean isAsciiLetterOrDigit(char c) {
        return (c >= 'a' && c <= 'z')
                || (c >= 'A' && c <= 'Z')
                || (c >= '0' && c <= '9');
    }

    /**
     * 替换文本中的敏感词
     *
     * @param text        待过滤文本
     * @param replaceChar 替换字符，如 '*'
     * @return 过滤后的文本
     */
    public static String filter(String text, char replaceChar) {
        if (text == null || text.isEmpty() || sensitiveWordMap.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder(text);
        int i = 0;
        while (i < text.length()) {
            int length = checkSensitiveWord(text, i);
            if (length > 0 && isStandaloneMatch(text, i, length)) {
                for (int j = i; j < i + length; j++) {
                    result.setCharAt(j, replaceChar);
                }
                i += length;
            } else {
                i++;
            }
        }
        return result.toString();
    }
}
