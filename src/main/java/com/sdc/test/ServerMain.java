package com.sdc.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sdc.test.rpc.ObjectRpcServer;
import com.sdc.test.rpc.RemoteService;
import com.sdc.test.rpc.TestObject;

public class ServerMain {

    public static void main(String[] args) throws Exception {
        ConnectionFactory connFactory = new ConnectionFactory();
        Connection conn = connFactory.newConnection();
        Channel channel = conn.createChannel();
        channel.queueDeclare("TestDefault", false, false, false, null);
        ObjectRpcServer<RemoteService> s = new ObjectRpcServer<>(channel,"TestDefault",RemoteService.class,new TestObject());
        s.startLoop();
    }
}
