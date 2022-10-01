package com.omni.wallet.baselibrary.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * List的筛选工具类
 */

public final class ListUtils {
    public static <T> List<T> filter(List<T> list, String text, ListUtilsHook<T> hook) {
        ArrayList<T> r = new ArrayList<>();
        for (T t : list) {
            if (hook.compare(t, text)) {
                r.add(t);
            }
        }
        r.trimToSize();
        return r;
    }

    public interface ListUtilsHook<T> {
        boolean compare(T t, String text);
    }
}
