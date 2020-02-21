package com.sdc.test.rpc.base;

import com.rabbitmq.client.*;
import com.sdc.test.rpc.config.RpcServerParam;
import com.sdc.test.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class AbstractRpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRpcServer.class);

    private Channel channel;

    private String queueName;

    private String  exchange;

    private volatile  boolean isRunning  = true;

    private DeliverCallback callback;

    private String consumerTag;

    private boolean autoAck = false;

    public AbstractRpcServer(Channel channel) throws IOException {
        this(channel, StringUtils.EMPTY);
    }

    public AbstractRpcServer(Channel channel, String queueName) throws IOException {
        this(new RpcServerParam()
                .setChannel(channel)
                .setQueueName(StringUtils.EMPTY)
                .setAutoAck(false)
                .setQueueName(queueName));
    }


    public AbstractRpcServer(RpcServerParam param) throws IOException {
        this.channel = param.getChannel();
        this.autoAck = param.isAutoAck();
        this.exchange = param.getExchange();
        if (StringUtils.isEmpty(param.getQueueName()))
            this.queueName = channel.queueDeclare().getQueue();
        else
            this.queueName = param.getQueueName();
        initDriverCallBack();

    }

    private void initDriverCallBack() throws IOException {
        callback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();
            byte[] replyBody = null;
            String correlationId = delivery.getProperties().getCorrelationId();
            String replyTo = delivery.getProperties().getReplyTo();
            if (correlationId != null && replyTo != null) {
                AMQP.BasicProperties replyProperties
                        = new AMQP.BasicProperties.Builder().correlationId(correlationId).build();
                replyBody = handleCall(delivery.getBody(), replyProperties);
            } else {
                handleCast(delivery.getBody());
            }
            channel.basicPublish(exchange, delivery.getProperties().getReplyTo(), replyProps, replyBody);
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            synchronized (this) {
                this.notify();
            }
        };
        consumerTag  =  channel.basicConsume(queueName, autoAck, callback, (consumerTag -> {}));
    }

    public void startLoop(){
        while (isRunning) {
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    isRunning = false;
                    LOGGER.error("Thread caused exception ",e);
                }
            }
        }
    }

    public void stopLoop(){
        isRunning = false;
    }

    public void close() throws IOException {
        if(!StringUtils.isEmpty(consumerTag))
            channel.basicCancel(consumerTag);
             stopLoop();

             callback = null;
    }

    public abstract byte [] handleCall(byte[] requestBody, AMQP.BasicProperties replyProperties) throws IOException;

    public abstract void handleCast(byte [] requestBody);


}
