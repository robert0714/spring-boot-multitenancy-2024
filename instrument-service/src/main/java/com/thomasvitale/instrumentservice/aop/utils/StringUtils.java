package com.thomasvitale.instrumentservice.aop.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.util.AntPathMatcher;
 
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static final String NULLSTR = "";

    private static final char SEPARATOR = '_';

    // 使返回值不為空
    public static <T> T nvl(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    // 判斷集合是否為null或者空集合
    public static boolean isEmpty(Collection<?> coll) {
        return isNull(coll) || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    // 判斷array是否為null或者空array
    public static boolean isEmpty(Object[] objects) {
        return isNull(objects) || (objects.length == 0);
    }

    public static boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }

    // 判斷map是否為null或者空map
    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    // 判斷字串是否為null或者空字串
    public static boolean isEmpty(String str) {
        return isNull(str) || NULLSTR.equals(str.trim());
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    // 判斷物件是否為null
    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }

    // 判斷物件是否為陣列
    public static boolean isArray(Object object) {
        return isNotNull(object) && object.getClass().isArray();
    }

    // 去掉空格
    public static String trim(String str) {
        return (str == null ? NULLSTR : str.trim());
    }

    // 截取字串
    public static String substring(final String str, int start) {
        if (str == null) {
            return NULLSTR;
        }

        if (start < 0) {
            start = str.length() + start;
        }
        if (start < 0) {
            start = 0;
        }

        if (start > str.length()) {
            return NULLSTR;
        }

        return str.substring(start);
    }

    // 截取字串
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return NULLSTR;
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return NULLSTR;
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    // 判斷是否為空且不是空字串
    public static boolean hasText(String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    // 格式化文本，將占位符{}替換為指定的參數
    // 轉譯{} : format("this is \\{} for {}", "a", "b") =》 this is \{} for a
    // 轉譯\ : format("this is \\\\{} for {}", "a", "b") =》 this is \a for b
    public static String format(String template, Object... params) {
        if (isEmpty(params) || isEmpty(template)) {
            return template;
        }
        return StrFormatter.format(template, params);
    }

    // 是否http(s)://開頭
    public static boolean ishttp(String link) {
        return StringUtils.startsWithAny(link, Constants.HTTP, Constants.HTTPS);
    }

    // 判斷collection中是否包含array 判斷array中是否包含value
    public static boolean containsAny(Collection<String> collection, String... array) {
        if (isEmpty(collection) || isEmpty(array)) {
            return false;
        }

        for (String str : array) {
            if (collection.contains(str)) {
                return true;
            }
        }
        return false;
    }

    // 駝峰轉下劃線命名
    public static String toUnderScoreCase(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // 前一個字元是否大寫
        boolean preCharIsUpperCase = true;
        // 當前字元是否大寫
        boolean curreCharIsUpperCase = true;
        // 下一個字元是否大寫
        boolean nexteCharIsUpperCase = true;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i > 0) {
                preCharIsUpperCase = Character.isUpperCase(str.charAt(i - 1));
            } else {
                preCharIsUpperCase = false;
            }

            curreCharIsUpperCase = Character.isUpperCase(c);

            if (i < (str.length() - 1)) {
                nexteCharIsUpperCase = Character.isUpperCase(str.charAt(i + 1));
            }

            if (preCharIsUpperCase && curreCharIsUpperCase && !nexteCharIsUpperCase) {
                sb.append(SEPARATOR);
            } else if ((i != 0 && !preCharIsUpperCase) && curreCharIsUpperCase) {
                sb.append(SEPARATOR);
            }
            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    // 忽略大小寫比較字串
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    // 下劃線命名轉為開頭大寫駝峰命名
    public static String convertToCamelCase(String name) {
        if (name == null || name.isEmpty()) {
            return NULLSTR;
        } else if (!name.contains("_")) {
            return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }

        StringBuilder result = new StringBuilder();
        String[] camels = name.split("_");
        for (String camel : camels) {
            if (camel.isEmpty()) {
                continue;
            }

            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    // 下劃線命名轉為開頭小寫駝峰命名
    public static String toCamelCase(String s) {
        if (s == null || s.isEmpty()) {
            return NULLSTR;
        } else if (!s.contains("_")) {
            return s.toLowerCase();
        }

        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // 檢查字串是否匹配pattern
    public static boolean matches(String str, List<String> patterns) {
        if (isEmpty(str) || isEmpty(patterns)) {
            return false;
        }

        for (String pattern : patterns) {
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    // 判斷URL是否匹配
    // ? 表示單個字元
    // * 表示一層路徑內的任意字串，不可跨層級
    // ** 表示任意層路徑
    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    // 數字左邊補0轉字串，若字串長度大於size，則只保留最後size個字元
    public static final String padl(final Number num, final int size) {
        return padl(num.toString(), size, '0');
    }

    // 字串補左邊字元，若字串長度大於size，則只保留最後size個字元
    public static final String padl(final String s, final int size, final char c) {
        final StringBuilder sb = new StringBuilder(size);
        if (s != null) {
            final int len = s.length();
            if (s.length() <= size) {
                for (int i = size - len; i > 0; i--) {
                    sb.append(c);
                }
                sb.append(s);
            } else {
                return s.substring(len - size, len);
            }
        } else {
            for (int i = size; i > 0; i--) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}