package com.sdc.test.rpc;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.sdc.test.rpc.base.AbstractRpcClient;
import com.sdc.test.rpc.dto.RpcDto;
import com.sdc.test.util.SerializeUtils;

import java.io.IOException;

public class ObjectRpcClient extends AbstractRpcClient {

    public ObjectRpcClient(Channel channel, String exchange, String routingKey) throws IOException {
        super(channel, exchange, routingKey);
    }

    @Override
    public void initCallback() throws IOException {
       DeliverCallback callback = (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(getCorrelationId())) {
                getResponse().offer(new Response(delivery));
            }
        };
        setConsumerTag(getChannel().basicConsume(getReplyTo(), true, callback , consumerTag -> { }));
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
