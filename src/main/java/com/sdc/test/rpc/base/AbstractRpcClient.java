package com.sdc.test.rpc.base;

import com.rabbitmq.client.*;
import com.sdc.test.rpc.config.RpcClientParam;
import com.sdc.test.util.StringUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class AbstractRpcClient {

    private final Channel channel;

    private final String exchange;

    private final String routingKey;

    private final  String replyTo;

    private DeliverCallback callback;

    private String correlationId ;

    private String consumerTag;

    private  BlockingQueue<Response> response = new ArrayBlockingQueue<>(1);

    public AbstractRpcClient(Channel channel, String exchange, String routingKey) throws IOException {
        this(new RpcClientParam().setChannel(channel)
                                  .setExchange(exchange)
                                  .setReplyTo(null)
                                  .setRoutingKey(routingKey)
        );
    }

    public AbstractRpcClient(RpcClientParam param) throws IOException {
        this.channel = param.getChannel();
        this.exchange = param.getExchange();
        this.routingKey = param.getRoutingKey();
        if(StringUtils.isEmpty(param.getReplyTo()))
            this.replyTo = channel.queueDeclare().getQueue();
        else
            this.replyTo = param.getReplyTo();
        initCallback();
    }


    public abstract void initCallback() throws IOException;

    private Response publish(byte[] req)
            throws IOException, InterruptedException {
        correlationId = UUID.randomUUID().toString();
        AMQP.BasicProperties props =  new AMQP.BasicProperties.Builder()
                    .correlationId(correlationId).replyTo(replyTo).build();
        channel.basicPublish(exchange, routingKey, props, req);
        return response.take();
    }

    public byte [] call(byte [] req) throws IOException, InterruptedException {
        return publish(req).getBody();
    }

    public void close() throws IOException {
        if(!StringUtils.isEmpty(consumerTag))
            channel.basicCancel(consumerTag);
        callback = null;
    }


    public static class Response {
        protected byte[] body;

        public Response( Delivery delivery) {
            this.body = delivery.getBody();
        }


        public byte[] getBody() {
            return body;
        }
    }


    public Channel getChannel() {
        return channel;
    }

    public String getExchange() {
        return exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public DeliverCallback getCallback() {
        return callback;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getConsumerTag() {
        return consumerTag;
    }

    public BlockingQueue<Response> getResponse() {
        return response;
    }

    public void setConsumerTag(String consumerTag) {
        this.consumerTag = consumerTag;
    }
}
