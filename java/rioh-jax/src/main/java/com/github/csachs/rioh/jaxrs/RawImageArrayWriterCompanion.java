package com.github.csachs.rioh.jaxrs;

import com.github.csachs.rioh.ImageArray;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static com.github.csachs.rioh.RiohConstants.*;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;

public class RawImageArrayWriterCompanion {
    static public void writeImages(ImageArray imageArray, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        Map<String, String> params = null;
        for(int i = 0; i < imageArray.array.length; i++) {
            Map<String, String> currentParams = imageArray.array[i].getRiohMapping();
            if(params == null) {
                params = currentParams;
            } else {
                for(Map.Entry<String, String> entry : currentParams.entrySet()) {
                    if(params.containsKey(entry.getKey())) {
                        params.put(entry.getKey(), params.get(entry.getKey()) + MULTI_IMAGE_SEPARATOR + entry.getValue());
                    } else {
                        params.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }

        params.put(VERSION, VERSION_STR);

        MediaType mt = new MediaType(MEDIA_TYPE, MEDIA_SUB_TYPE, params);

        httpHeaders.get(CONTENT_TYPE).set(0, mt);

        for(int i = 0; i < imageArray.array.length; i++) {
            entityStream.write(imageArray.array[i].data);
        }

    }
}
