package com.sdc.test.rcp;

import com.rabbitmq.client.Channel;
import com.sdc.test.util.SerializeUtils;

import java.io.IOException;

public class ObjectRpcClient extends RpcClient {


    public ObjectRpcClient(Channel channel, String exchange, String routingKey) throws IOException {
        super(channel, exchange, routingKey);
    }

    public ObjectRpcClient(Channel channel, String exchange, String routingKey, String replyTo) throws IOException {
        super(channel, exchange, routingKey, replyTo);
    }


    public Object call(String method, Object[] params) throws Exception {
        RpcDto dto = new RpcDto(method,(params == null) ? new Object[0] : params);
           byte []  req = SerializeUtils.serialize(dto);
         RpcDto res = SerializeUtils.deserialize(this.call(req));
         if(res == null || res.isError())
             throw new Exception( res != null ? res.getErrorMessage() : "internal error" );
         else
             return res.getResult();
    }
}
