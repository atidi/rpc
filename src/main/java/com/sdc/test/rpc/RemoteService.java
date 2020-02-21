package com.sdc.test.rpc;

import java.util.List;

public interface RemoteService {

    String helloMethod(String value);

    int sum(List<Integer> values);
}
