package com.riverisland.app.automation.enums;

/**
 * Created by Prashant Ramcharan on 25/01/2018
 */
public enum DevicePoint {
    IPHONEX_SHOP_TOP_Y_POINT(44, 79),
    IPHONEX_ONBOARDING_BOTTOM_Y_POINT(740, 707);

    int point;
    int pointOffset;

    DevicePoint(int point, int pointOffset) {
        this.point = point;
        this.pointOffset = pointOffset;
    }

    public int get() {
        return point;
    }

    public int getOffset() {
        return pointOffset;
    }
}
