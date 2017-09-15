package com.github.csachs.rioh;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.*;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.*;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import java.nio.ByteBuffer;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static com.github.csachs.rioh.Image.DataType.*;

@SuppressWarnings("unchecked")
public class ImageImgLib2Companion {
    static public Img convert(Image image) {

        Object array = image.getProperArray();

        long[] size = new long[image.size.length];
        for(int i = 0; i < image.size.length; i++) {
            if(image.dimensions[0] == Image.Dimension.Y && image.dimensions[1] == Image.Dimension.X) {
                size[image.size.length - i - 1] = image.size[i];
            } else if(image.dimensions[0] == Image.Dimension.X && image.dimensions[1] == Image.Dimension.Y) {
                size[i] = image.size[i];
            } else {
                size[i] = image.size[i]; // TODO manage better
            }
        }


        switch(image.datatype) {
            case uint8:
                return ArrayImgs.unsignedBytes((byte[]) array, size);
            case uint16:
                return ArrayImgs.unsignedShorts((short[]) array, size);
            case uint32:
                return ArrayImgs.unsignedInts((int[]) array, size);
            case uint64:
                return ArrayImgs.longs((long[]) array, size); /* TODO unsigned? */
            case int8:
                return ArrayImgs.bytes((byte[]) array, size);
            case int16:
                return ArrayImgs.shorts((short[]) array, size);
            case int32:
                return ArrayImgs.ints((int[]) array, size);
            case int64:
                return ArrayImgs.longs((long[]) array, size);
            case float32:
                return ArrayImgs.floats((float[]) array, size);
            case float64:
                return ArrayImgs.doubles((double[]) array, size);
            case complex64:
                return ArrayImgs.complexFloats((float[]) array, size);
            case complex128:
                return ArrayImgs.complexDoubles((double[]) array, size);
        }

        throw new RuntimeException("Unsupported Image");
    }

    static public Image convert(Img img_) {
        ArrayImg img = (ArrayImg)img_; // if this cast fails, just crash

        Image image = new Image();

        image.endian = BIG_ENDIAN;
        image.dimensions = new Image.Dimension[img.numDimensions()];
        image.dimensions[0] = Image.Dimension.X;
        image.dimensions[1] = Image.Dimension.Y;

        image.size = new int[img.numDimensions()];

        for(int i = 0; i < img.numDimensions(); i++) {
            image.size[i] = (int)img.dimension(i);
        }

        Object backing = img.update(null);
        Object value = img.localizingCursor().get();


        if(value instanceof UnsignedByteType) {
            image.datatype = uint8;
        } else if(value instanceof UnsignedShortType) {
            image.datatype = uint16;
        } else if(value instanceof UnsignedIntType) {
            image.datatype = uint32;
        } else if(value instanceof UnsignedLongType) {
            image.datatype = uint64;
        } else if(value instanceof ByteType) {
            image.datatype = int8;
        } else if(value instanceof ShortType) {
            image.datatype = int16;
        } else if(value instanceof IntType) {
            image.datatype = int32;
        } else if(value instanceof LongType) {
            image.datatype = int64;
        } else if(value instanceof FloatType) {
            image.datatype = float32;
        } else if(value instanceof DoubleType) {
            image.datatype = float64;
        } else if(value instanceof ComplexFloatType) {
            image.datatype = complex64;
        } else if(value instanceof ComplexDoubleType) {
            image.datatype = uint8;
        } else if(value instanceof BitType) {
            image.datatype = uint8;
        } else {
            throw new RuntimeException("Unsupported type");
        }


        image.allocate();

        ByteBuffer bb = ByteBuffer.wrap(image.data).order(image.endian);
        if(value instanceof UnsignedByteType && backing instanceof ByteArray) {
            image.datatype = uint8;
            bb.put(((ByteArray)backing).getCurrentStorageArray());
        } else if(value instanceof UnsignedShortType && backing instanceof ShortArray) {
            image.datatype = uint16;
            bb.asShortBuffer().put(((ShortArray)backing).getCurrentStorageArray());
        } else if(value instanceof UnsignedIntType && backing instanceof IntArray) {
            image.datatype = uint32;
            bb.asIntBuffer().put(((IntArray)backing).getCurrentStorageArray());
        } else if(value instanceof UnsignedLongType && backing instanceof LongArray) {
            image.datatype = uint64;
            bb.asLongBuffer().put(((LongArray)backing).getCurrentStorageArray());
        } else if(value instanceof ByteType && backing instanceof ByteArray) {
            image.datatype = int8;
            bb.put(((ByteArray)backing).getCurrentStorageArray());
        } else if(value instanceof ShortType && backing instanceof ShortArray) {
            image.datatype = int16;
            bb.asShortBuffer().put(((ShortArray)backing).getCurrentStorageArray());
        } else if(value instanceof IntType && backing instanceof IntArray) {
            image.datatype = int32;
            bb.asIntBuffer().put(((IntArray)backing).getCurrentStorageArray());
        } else if(value instanceof LongType && backing instanceof LongArray) {
            image.datatype = int64;
            bb.asLongBuffer().put(((LongArray)backing).getCurrentStorageArray());
        } else if(value instanceof FloatType && backing instanceof FloatArray) {
            image.datatype = float32;
            bb.asFloatBuffer().put(((FloatArray)backing).getCurrentStorageArray());
        } else if(value instanceof DoubleType && backing instanceof DoubleArray) {
            image.datatype = float64;
            bb.asDoubleBuffer().put(((DoubleArray)backing).getCurrentStorageArray());
        } else if(value instanceof ComplexFloatType && backing instanceof FloatArray) {
            image.datatype = complex64;
            bb.asFloatBuffer().put(((FloatArray)backing).getCurrentStorageArray());
        } else if(value instanceof ComplexDoubleType && backing instanceof DoubleArray) {
            image.datatype = uint8;
            bb.asDoubleBuffer().put(((DoubleArray)backing).getCurrentStorageArray());
        } else if(value instanceof BitType) {
            image.datatype = uint8;
            int i = 0;
            Cursor<BitType> cursor = img.cursor();
            while(cursor.hasNext()) {
                cursor.fwd();
                image.data[i++] = cursor.get().get() ? (byte)1 : (byte)0;
            }
        } else {
            throw new RuntimeException("Unsupported type");
        }

        return image;
    }
}