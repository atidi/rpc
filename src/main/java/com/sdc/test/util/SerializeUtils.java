package com.sdc.test.util;

import com.sdc.test.rpc.dto.RpcDto;

import java.io.*;

public class SerializeUtils {

    public static byte [] serialize(RpcDto dto) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(dto);
        return byteOut.toByteArray();
    }

    public static RpcDto deserialize(byte [] byteArray) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteArray);
        ObjectInputStream in = new ObjectInputStream(byteIn);
        return  (RpcDto) in.readObject();
    }
}
