package com.sdc.test.rpc;

import com.rabbitmq.client.*;
import com.sdc.test.util.StringUtils;

import java.io.IOException;

public abstract class RpcServer {

    private Channel channel;

    private String queueName;

    private Consumer defaultConsumer;


    public RpcServer(Channel channel) throws IOException {
        this(channel, null);
    }

    public RpcServer(Channel channel, String queueName) throws IOException {
        this.channel = channel;
        if (StringUtils.isEmpty(queueName))
            this.queueName = channel.queueDeclare().getQueue();
        else
            this.queueName = queueName;
        initConsumer();
    }

    private void initConsumer() throws IOException {
        defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {

                String correlationId = properties.getCorrelationId();
                String replyTo = properties.getReplyTo();
                if (correlationId != null && replyTo != null) {
                    AMQP.BasicProperties replyProperties
                            = new AMQP.BasicProperties.Builder().correlationId(correlationId).build();
                    byte[] replyBody = handleCall(body, replyProperties);
                    channel.basicPublish("", replyTo, replyProperties, replyBody);
                } else {
                    handleCast(body);
                }

                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };


        channel.basicConsume(queueName, defaultConsumer);
    }

    public abstract byte [] handleCall(byte[] requestBody, AMQP.BasicProperties replyProperties) throws IOException;

    public abstract void handleCast(byte [] requestBody);


}
