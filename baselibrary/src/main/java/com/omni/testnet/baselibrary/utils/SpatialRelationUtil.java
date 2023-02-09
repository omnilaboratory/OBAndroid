package com.omni.testnet.baselibrary.utils;


import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;

/**
 * 空间关系的工具类
 * 判断一个点是否在多边形区域内和多边形边界
 */

public class SpatialRelationUtil {
    private static Region region = new Region();
    private static Region paramsRegion = new Region();
    private static RectF rectF = new RectF();


    /**
     * 判断一个点是否在多边形区域中
     *
     * @param path 多边形的Path
     * @param x    点的X坐标
     * @param y    点的Y坐标
     */
    public static boolean pointInBound(Path path, int x, int y) {
//        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        paramsRegion.set((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        region.setPath(path, paramsRegion);
        if (region.contains(x, y)) {
            return true;
        }
        return false;
    }
}
