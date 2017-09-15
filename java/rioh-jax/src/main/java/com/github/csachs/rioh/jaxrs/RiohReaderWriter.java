package com.github.csachs.rioh.jaxrs;

import com.github.csachs.rioh.ImageArray;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static com.github.csachs.rioh.RiohConstants.*;
import static javax.ws.rs.core.HttpHeaders.CONTENT_LENGTH;

@Provider
@Consumes(RIOH_MEDIA_TYPE)
@Produces(RIOH_MEDIA_TYPE)
public class RiohReaderWriter implements MessageBodyReader<ImageArray>, MessageBodyWriter<ImageArray> {

    public boolean isReadable(Class<?> type, Type genericType, Annotation annotations[], MediaType mediaType) {
        if(mediaType.getType().equals(MEDIA_TYPE) && mediaType.getSubtype().equals(MEDIA_SUB_TYPE)) {
            if(mediaType.getParameters().get(VERSION).equals(VERSION_STR)) {
                try {
                    RawImageArrayReaderCompanion.readImages(mediaType.getParameters());
                    return true;
                } catch(RuntimeException e) {
                    return false;
                }
            }
        }

        return false;
    }

    public ImageArray readFrom(Class<ImageArray> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                               MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {

        return RawImageArrayReaderCompanion.readImages(
                mediaType.getParameters(),
                entityStream,
                Integer.parseInt(httpHeaders.getFirst(CONTENT_LENGTH))
        );
    }

    public boolean isWriteable(Class<?> type, Type genericType, Annotation annotations[], MediaType mediaType) {
        return true;
    }

    public long getSize(ImageArray array, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    public void writeTo(ImageArray array, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        RawImageArrayWriterCompanion.writeImages(array, httpHeaders, entityStream);
    }
}
