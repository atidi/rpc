package com.sdc.test.rcp;

import com.rabbitmq.client.*;
import com.sdc.test.util.StringUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class RpcClient {

    private  Channel channel;

    private  String exchange;

    private  String routingKey;

    private  String replyTo;

    private String correlationId ;

    private Consumer defaultConsumer;

    private String consumerTag;

    private final BlockingQueue<Response> response = new LinkedBlockingDeque<>();

    public RpcClient(Channel channel,String exchange, String routingKey) throws IOException {
        this(channel,exchange,routingKey,null);
    }

    public RpcClient(Channel channel,String exchange, String routingKey, String replyTo) throws IOException {
        this.channel = channel;
        this.exchange = exchange;
        this.routingKey = routingKey;
        if(StringUtils.isEmpty(replyTo))
            this.replyTo = channel.queueDeclare().getQueue();
        init();
    }

    private void init() throws IOException {
        defaultConsumer = new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body){

                    if (properties.getCorrelationId().equals(correlationId)) {
                        response.add(new Response(consumerTag, envelope, properties, body));
                    }
            }
        };
        consumerTag = channel.basicConsume(replyTo, true, defaultConsumer);
    }

    private Response publish(byte[] req)
            throws IOException, InterruptedException {
        correlationId = UUID.randomUUID().toString();
        AMQP.BasicProperties props =  new AMQP.BasicProperties.Builder()
                    .correlationId(correlationId).replyTo(replyTo).build();
        channel.basicPublish(exchange, routingKey, false, props, req);
        channel.basicCancel(consumerTag);
        return response.take();
    }

    public byte [] call(byte [] req) throws IOException, InterruptedException {
        return publish(req).getBody();
    }


    public static class Response {
        protected String consumerTag;
        protected Envelope envelope;
        protected AMQP.BasicProperties properties;
        protected byte[] body;

        public Response() {
        }

        public Response(
                final String consumerTag, final Envelope envelope, final AMQP.BasicProperties properties,
                final byte[] body) {
            this.consumerTag = consumerTag;
            this.envelope = envelope;
            this.properties = properties;
            this.body = body;
        }

        public String getConsumerTag() {
            return consumerTag;
        }

        public Envelope getEnvelope() {
            return envelope;
        }

        public AMQP.BasicProperties getProperties() {
            return properties;
        }

        public byte[] getBody() {
            return body;
        }
    }
}
