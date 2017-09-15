package com.github.csachs.rioh;

import java.nio.ByteOrder;

import static com.github.csachs.rioh.RiohConstants.ENDIAN_BIG;
import static com.github.csachs.rioh.RiohConstants.ENDIAN_LITTLE;

public final class ByteOrderCompanion {
    public static String convert(ByteOrder byteOrder) {
        if (byteOrder == ByteOrder.LITTLE_ENDIAN)
            return ENDIAN_LITTLE;
        if (byteOrder == ByteOrder.BIG_ENDIAN)
            return ENDIAN_BIG;
        throw new RuntimeException();
    }

    public static ByteOrder convert(String byteOrder) {
        if(byteOrder.equals(ENDIAN_LITTLE))
            return ByteOrder.LITTLE_ENDIAN;
        if (byteOrder.equals(ENDIAN_BIG))
            return ByteOrder.BIG_ENDIAN;

        throw new RuntimeException();
    }
}
