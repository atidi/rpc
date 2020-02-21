package com.sdc.test.rpc.config;

import com.rabbitmq.client.Channel;
import com.sdc.test.util.StringUtils;

public class RpcServerParam {

    private Channel channel;

    private String queueName;

    private String exchange = StringUtils.EMPTY;

    private boolean autoAck = false;


    public Channel getChannel() {
        return channel;
    }

    public RpcServerParam setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    public String getQueueName() {
        return queueName;
    }

    public RpcServerParam setQueueName(String queueName) {
        this.queueName = queueName;
        return this;
    }


    public String getExchange() {
        return exchange;
    }

    public RpcServerParam setExchange(String exchange) {
        this.exchange = exchange;
        return this;
    }

    public boolean isAutoAck() {
        return autoAck;
    }

    public RpcServerParam setAutoAck(boolean autoAck) {
        this.autoAck = autoAck;
        return this;
    }
}
