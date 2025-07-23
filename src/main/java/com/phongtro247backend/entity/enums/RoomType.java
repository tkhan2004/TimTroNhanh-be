package com.phongtro247backend.entity.enums;

public enum RoomType {
    PHONG_TRO("Phòng trọ"),
    CHUNG_CU("Chung cư"),
    NHA_NGUYEN_CAN("Nhà nguyên căn"),
    CAN_HO_DICH_VU("Căn hộ dịch vụ"),
    NHA_MAT_TIEN("Nhà mặt tiền"),
    STUDIO("Studio");

    private final String displayName;

    RoomType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}