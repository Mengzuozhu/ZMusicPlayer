package com.mzz.zmusicplayer.manage;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TODO
 *
 * @author zuozhu.meng
 * @date 2020/5/24
 **/
public class AdapterManager {
    private static List<BaseQuickAdapter> adapters = new ArrayList<>();

    public static void register(BaseQuickAdapter adapter) {
        adapters.add(adapter);
    }

    public static void unregister(BaseQuickAdapter adapter) {
        adapters.remove(adapter);
    }

    public static void notifyDataSetChanged() {
        adapters.removeIf(Objects::isNull);
        adapters.forEach(BaseQuickAdapter::notifyDataSetChanged);
    }
}
