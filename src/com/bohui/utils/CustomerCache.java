package com.bohui.utils;

import com.bohui.Customer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * 数据缓存
 * Created by liyangli on 17/1/10.
 */
public enum CustomerCache {
    CACHE;
    private  int timeout = 2;
//    private BlockingDeque<List<Customer>> blockingDeque = new LinkedBlockingDeque<>();
    private List<Customer> customers = Collections.synchronizedList(new ArrayList<Customer>());

    public void putCache(List<Customer> list){
        customers.addAll(list);
    }

    /**
     * 获取对应数据.超时时间为3s;
     */

    public List<Customer> findCache(){
        List<Customer> ll = findThreadCache(timeout);
        return ll;
    }

    private List<Customer> findThreadCache(int timeout){
        List<Customer> ll = new ArrayList<>(customers);
        customers.clear();
        if(ll == null || ll.isEmpty()){
            System.out.println("消费者中获取集合列表长度->"+ll.size());
            if(timeout == 0){
                return ll;
            }
            --timeout;
            try {
                TimeUnit.SECONDS.sleep(1);
                ll = findThreadCache(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return ll;
    }

}
