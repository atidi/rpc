package com.sdc.test.rpc.config;

import com.rabbitmq.client.Channel;

public class RpcClientParam {

    private Channel channel;

    private  String exchange;

    private  String routingKey;

    private  String replyTo;


    public Channel getChannel() {
        return channel;
    }

    public RpcClientParam setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    public String getExchange() {
        return exchange;
    }

    public RpcClientParam setExchange(String exchange) {
        this.exchange = exchange;
        return this;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public RpcClientParam setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
        return this;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public RpcClientParam setReplyTo(String replyTo) {
        this.replyTo = replyTo;
        return this;
    }
}
