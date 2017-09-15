package com.github.csachs.rioh.jaxrs;

import com.github.csachs.rioh.ByteOrderCompanion;
import com.github.csachs.rioh.Image;
import com.github.csachs.rioh.ImageArray;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.github.csachs.rioh.RiohConstants.*;

public class RawImageArrayReaderCompanion {
    static public Image prepareImageFromHeader(String endianness, String datatype, String dimensions, String size) {
        Image image = new Image();

        image.endian = ByteOrderCompanion.convert(endianness.toLowerCase());

        image.datatype = Image.DataType.valueOf(datatype.toLowerCase());

        dimensions = dimensions.toUpperCase();

        image.dimensions = new Image.Dimension[dimensions.length()];
        for (int i = 0; i < dimensions.length(); i++) {
            image.dimensions[i] = Image.Dimension.valueOf(dimensions.substring(i, i + 1));
        }

        String sizeSplit[] = size.toLowerCase().split(MULTI_DIMENSION_SEPARATOR);

        image.size = new int[sizeSplit.length];
        for (int i = 0; i < sizeSplit.length; i++) {
            image.size[i] = Integer.parseInt(sizeSplit[i]);
        }

        return image;
    }


    static public ImageArray readImages(Map<String, String> parameters) {
        try {
            return readImages(parameters, null, 0);
        } catch (IOException e) {
            // will never happen, as entityStream is null and will not be used
            throw new RuntimeException(e);
        }
    }


    static public ImageArray readImages(Map<String, String> parameters, InputStream entityStream, int maxLength) throws IOException {
        String[] endian = new String[] {ENDIAN_LITTLE};
        if(parameters.containsKey(ENDIAN))
            endian = parameters.get(ENDIAN).split(MULTI_IMAGE_SEPARATOR);

        String[] datatype = parameters.get(DATATYPE).split(MULTI_IMAGE_SEPARATOR);
        String[] dimensions = parameters.get(DIMENSIONS).split(MULTI_IMAGE_SEPARATOR);
        String[] size = parameters.get(SIZE).split(MULTI_IMAGE_SEPARATOR);

        int largest = Math.max(endian.length, Math.max(datatype.length, Math.max(dimensions.length, size.length)));

        ImageArray ia = new ImageArray(largest);

        int totalRead = 0;

        for(int i = 0; i < ia.array.length; i++) {
            Image image = prepareImageFromHeader(
                    endian[Math.min(i, endian.length - 1)],
                    datatype[Math.min(i, datatype.length - 1)],
                    dimensions[Math.min(i, dimensions.length - 1)],
                    size[Math.min(i, size.length - 1)]
            );

            if(entityStream != null) {
                image.allocate();
                int offset = 0;
                while (offset < image.data.length) {
                    if((totalRead + image.data.length - offset) > maxLength) {
                        throw new IOException();
                    }
                    totalRead -= offset;
                    offset += entityStream.read(image.data, offset, image.data.length - offset);
                    totalRead += offset;
                }
            }

            ia.array[i] = image;
        }

        return ia;
    }

}
