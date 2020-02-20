package com.sdc.test.rcp;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.sdc.test.util.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ObjectRpcServer<E> extends RpcServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectRpcServer.class);

    private E interfaceInstance;

    private Class<?> interfaceClass;

    private Map<String, Method> interfaceDescription;

    public ObjectRpcServer(Channel channel,Class<?> interfaceClass, E interfaceInstance) throws Exception {
        super(channel);
        init(interfaceClass,interfaceInstance);

    }

    public ObjectRpcServer(Channel channel, String queueName,Class<?> interfaceClass, E interfaceInstance) throws Exception {
        super(channel, queueName);
        init(interfaceClass,interfaceInstance);
    }

    private void init(Class<?> interfaceClass, E interfaceInstance) throws Exception {
        this.interfaceClass = interfaceClass;
        this.interfaceInstance = interfaceInstance;
        interfaceDescription  = new HashMap<>();
        for (Method method : interfaceClass.getMethods()) {
          Method m =  interfaceDescription.put(method.getName(),method);
            if( m != null)
            throw new Exception("Different methods with some name not allowed ");
        }
    }

    @Override
    public byte[] handleCall(byte[] requestBody, AMQP.BasicProperties replyProperties) throws IOException {
        return call(requestBody);
    }

    private byte[] call(byte[] requestBody) throws IOException {
        byte [] res;
        RpcDto dto = null;
            try{
                 dto = SerializeUtils.deserialize(requestBody);
                Method method = interfaceDescription.get(dto.getMethodName());
                dto.setResult(method.invoke(interfaceInstance,dto.getParams()));
                res =  SerializeUtils.serialize(dto);
            } catch (Exception e) {
                LOGGER.error("Error while invoke method" ,e);
                if (dto == null)
                    dto = new RpcDto();
                dto.setError(true);
                dto.setErrorMessage("Error while invoke method");
                res = SerializeUtils.serialize(dto);
            }
        return res;
    }


    @Override
    public void handleCast(byte[] requestBody) {
      //TODO implementing
    }


}
