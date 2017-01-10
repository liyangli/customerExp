package com.bohui.thread;

import com.bohui.Customer;
import com.bohui.utils.*;

import java.util.List;
import java.util.concurrent.BlockingDeque;

/**
 * 读取数据库线程
 * 理论上含有多个现场进行处理
 * 该现场会进行读取指定文件,指定文件需要进行锁定,防止脏读
 *
 * Created by liyangli on 17/1/10.
 */
public class ReadDBTask implements Runnable {
    private DBConnPool dbConnPool;

    private int start;
    private int end;
    public ReadDBTask(DBConnPool dbConnPool, int start,int end) {
        this.dbConnPool = dbConnPool;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {

        String name = Thread.currentThread().getName();
        //真正执行的方法
        System.out.println("当前线程名称->"+name+";类->"+this.hashCode()+";start->"+start+",end->"+end);
        ReadDB readDB = dbConnPool.findReadDB();
        List<Customer> list = readDB.findObj(start,end);
        //获取所有数据后需要放到缓存中.进行交给具体线程再处理
        System.out.println("list->"+list.size());
        dbConnPool.putDB(readDB);
        CustomerCache.CACHE.putCache(list);
        System.out.println("线程执行完毕->"+name);

    }
}
