package com.sdc.test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sdc.test.rpc.ObjectRpcClient;


public class ClientMain {
    public static void main(String[] args) throws Exception {



        ConnectionFactory connFactoryC = new ConnectionFactory();
        Connection connection = connFactoryC.newConnection();
      Channel  channelC = connection.createChannel();
        ObjectRpcClient client = new ObjectRpcClient(channelC, "", "TestDefault");
        String mes = (String) client.call("helloMethod",  new Object[]{"hello"});
        System.out.println(mes);
        connection.close();
    }
}
