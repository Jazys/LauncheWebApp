package com.welie.blessed;

import org.jetbrains.annotations.NotNull;

public enum PhyType {
    /**
     * A Physical Layer (PHY) connection of 1 mbit. Compatible with Bluetooth 4.0, 4.1, 4.2 and 5.0
     */
    LE_1M(1,1),

    /**
     * A Physical Layer (PHY) connection of 2 mbit. Requires Bluetooth 5
     */
    LE_2M (2,2),

    /**
     * A Physical Layer (PHY) connection with long range. Requires Bluetooth 5
     */
    LE_CODED(3, 4),

    /**
     * Unknown Phy Type. Not to be used.
     */
    UNKNOWN_PHY_TYPE(-1,-1);

    PhyType(final int value, final int mask) {
        this.value = value;
        this.mask = mask;
    }
    private final int value;
    private final int mask;

    int getValue() {
        return value;
    }
    int getMask() {
        return mask;
    }

    @NotNull
    public static PhyType fromValue(int value) {
        for (PhyType type : values()) {
            if (type.getValue() == value)
                return type;
        }
        return UNKNOWN_PHY_TYPE;
    }
}
