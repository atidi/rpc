package com.sdc.test;

import java.util.List;

public class TestObject implements RemoteService {

    public String helloMethod(String value) {
        return "Hello " + value;
    }

    public int sum(List<Integer> values) {
        int s = 0;
        for (int i: values) { s += i; }
        return s;
    }
}
