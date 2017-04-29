package com.paulvarry.intra42.utils;

import java.util.List;

public class Pagination {

    public static int getPage(int size) {
        return (int) (size / 30.0 + 1);
    }

    public static int getPage(List<?> list) {
        if (list != null) {
            return (int) (list.size() / 30.0 + 1);
        } else
            return 1;
    }

    public static int getPage(List<?> list, int pageSize) {
        if (list != null) {
            return (int) (list.size() / (float) pageSize + 1);
        } else
            return 1;
    }

    public static boolean canAdd(List<?> list) {
        return list != null && list.size() % 30 == 0;
    }

    public static boolean canAdd(List<?> list, int pageSize) {
        return list != null && list.size() % pageSize == 0;
    }
}
