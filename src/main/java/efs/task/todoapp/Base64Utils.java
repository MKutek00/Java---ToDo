package efs.task.todoapp;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;

public class Base64Utils {
    public static String encode(final String value){

        //Conversion to byte
        final byte[] valueBytes = value.getBytes(StandardCharsets.ISO_8859_1);

        //Get encoder
        final Encoder base64Encoded = Base64.getEncoder();

        //Conversion
        return base64Encoded.encodeToString(valueBytes);
    }

    public static String decode(final String valueInBase64){
        //Get decoder
        final Decoder base64Decoder = Base64.getDecoder();

        //Decode
        final byte[] decodeValuesByte = base64Decoder.decode(valueInBase64);

        //Conversion to text
        return new String(decodeValuesByte, StandardCharsets.ISO_8859_1);
    }
}
