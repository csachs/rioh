
# RIOH - Raw Images over HTTP

Microformat/convention to transfer (raw) image data over HTTP.

A proper format definition text will be (hopefully) added in the future.

## Aim

Simple transfer of nD image data without unnecessary encoding/decoding.

## Motivation

JPEG and PNG support commonly only single frames.
JPEG is lossy, unsuitable for many scientific use. PNG only supports 8 or 16 bit.
While TIFF supports everything, it is complicated to parse, and to properly use it, both sender
and receiver need to follow conventions.

## Contents

This is a work in progress.

This repository currently includes code to use it in the following constellations:

- Java
    - Server, JAX-RS
- Python
    - Server, WSGI
    - Server, Flask
    - Client, Requests

## License
BSD License.