package com.phongtro247backend.entity;

import com.phongtro247backend.entity.enums.RoomType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoomTypeTest {

    @Test
    void testRoomTypeValues() {
        // Test all enum values exist
        assertEquals(6, RoomType.values().length);
        
        // Test specific values
        assertEquals("Phòng trọ", RoomType.PHONG_TRO.getDisplayName());
        assertEquals("Chung cư", RoomType.CHUNG_CU.getDisplayName());
        assertEquals("Nhà nguyên căn", RoomType.NHA_NGUYEN_CAN.getDisplayName());
        assertEquals("Căn hộ dịch vụ", RoomType.CAN_HO_DICH_VU.getDisplayName());
        assertEquals("Nhà mặt tiền", RoomType.NHA_MAT_TIEN.getDisplayName());
        assertEquals("Studio", RoomType.STUDIO.getDisplayName());
    }

    @Test
    void testRoomTypeFromString() {
        // Test valueOf works correctly
        assertEquals(RoomType.PHONG_TRO, RoomType.valueOf("PHONG_TRO"));
        assertEquals(RoomType.CHUNG_CU, RoomType.valueOf("CHUNG_CU"));
        assertEquals(RoomType.NHA_NGUYEN_CAN, RoomType.valueOf("NHA_NGUYEN_CAN"));
        assertEquals(RoomType.CAN_HO_DICH_VU, RoomType.valueOf("CAN_HO_DICH_VU"));
        assertEquals(RoomType.NHA_MAT_TIEN, RoomType.valueOf("NHA_MAT_TIEN"));
        assertEquals(RoomType.STUDIO, RoomType.valueOf("STUDIO"));
    }

    @Test
    void testInvalidRoomType() {
        // Test invalid enum value throws exception
        assertThrows(IllegalArgumentException.class, () -> {
            RoomType.valueOf("INVALID_TYPE");
        });
    }
}
