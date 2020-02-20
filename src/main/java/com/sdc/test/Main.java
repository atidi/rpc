package com.sdc.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sdc.test.rpc.ObjectRpcClient;
import com.sdc.test.rpc.ObjectRpcServer;


public class Main {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connFactory = new ConnectionFactory();
        Connection conn = connFactory.newConnection();
        Channel channel = conn.createChannel();
        channel.queueDeclare("TestDefault", false, false, false, null);
         new ObjectRpcServer<>(channel,"TestDefault",RemoteService.class,new TestObject());


        ConnectionFactory connFactoryC = new ConnectionFactory();
        Connection connection = connFactoryC.newConnection();
      Channel  channelC = connection.createChannel();
        ObjectRpcClient client = new ObjectRpcClient(channelC, "", "TestDefault");
        client.call("helloMethod",  new Object[]{"hello"});
    }
}
