package com.github.csachs.rioh;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Image {

    public enum DataType {
        /* bit (1, true, false), */
        uint8 (1, true, false),
        uint16 (2, true, false),
        uint32 (4, true, false),
        uint64 (8, true, false),
        int8 (1, true, true),
        int16 (2, true, true),
        int32 (4, true, true),
        int64 (8, true, true),
        float32 (4, false, false),
        float64 (8, false, false),
        complex64 (8, false, false),
        complex128 (16, false, false);

        final public int storageSize;
        final public boolean isInteger;
        final public boolean isUnsigned;

        DataType(int storageSize, boolean isInteger, boolean isUnsigned) {
            this.storageSize = storageSize;
            this.isInteger = isInteger;
            this.isUnsigned = isUnsigned;
        }
    }

    public enum Dimension {
        X, Y, Z, C, T
    }

    public ByteOrder endian;
    public DataType datatype;
    public Dimension[] dimensions;

    public int[] size;
    public byte[] data;

    public int elements() {
        int elements = 1;
        for(int i = 0; i < size.length; i++) {
            elements *= size[i];
        }
        return elements;
    }

    public void allocate() {
        data = new byte[storageSize()];
    }

    public int storageSize() {
        return elements() * datatype.storageSize;
    }

    public String toString() {
        return "Image<size=" + Arrays.toString(size) + ", dimensions=" + Arrays.toString(dimensions) + ", data=<" + elements() + " " + datatype + " (" + endian + ", " + data.length +" bytes)>>";
    }

    public Object getProperArray() {
        ByteBuffer bb = ByteBuffer.wrap(data).order(endian);

        switch(datatype) {
            /*case bit: {
                byte[] result = new byte[elements()];
                throw new RuntimeException();
            }*/
            case uint8:
            case int8: {
                byte[] result = new byte[elements()];
                bb.get(result);
                return result;
            }
            case uint16:
            case int16: {
                short[] result = new short[elements()];
                bb.asShortBuffer().get(result);
                return result;
            }
            case uint32:
            case int32: {
                int[] result = new int[elements()];
                bb.asIntBuffer().get(result);
                return result;
            }
            case uint64:
            case int64: {
                long[] result = new long[elements()];
                bb.asLongBuffer().get(result);
                return result;
            }
            case float32: {
                float[] result = new float[elements()];
                bb.asFloatBuffer().get(result);
                return result;
            }
            case float64: {
                double[] result = new double[elements()];
                bb.asDoubleBuffer().get(result);
                return result;
            }
            case complex64: {
                float[] result = new float[2*elements()];
                bb.asFloatBuffer().get(result);
                return result;
            }
            case complex128: {
                double[] result = new double[2*elements()];
                bb.asDoubleBuffer().get(result);
                return result;
            }
        }

        throw new RuntimeException("Image did not have any supported type.");
    }

    public Map<String, String> getRiohMapping() {

        StringBuilder dimensionStrBuilder = new StringBuilder();
        for(int i = 0; i < dimensions.length; i++) {
            dimensionStrBuilder.append(dimensions[i].toString());
        }

        StringBuilder sizeStrBuilder = new StringBuilder();
        for(int i = 0; i < size.length; i++) {
            sizeStrBuilder.append(Integer.toString(size[i]));
            sizeStrBuilder.append('x');
        }

        sizeStrBuilder.deleteCharAt(sizeStrBuilder.length() - 1);

        Map<String, String> result = new HashMap<String, String>();
        result.put(RiohConstants.ENDIAN, ByteOrderCompanion.convert(endian));
        result.put(RiohConstants.DATATYPE, datatype.toString());
        result.put(RiohConstants.DIMENSIONS, dimensionStrBuilder.toString());
        result.put(RiohConstants.SIZE, sizeStrBuilder.toString());

        return result;
    }
}