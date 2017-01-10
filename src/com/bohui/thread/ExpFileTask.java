package com.bohui.thread;

import com.bohui.Customer;
import com.bohui.utils.CustomerCache;
import com.bohui.utils.ExpFile;

import java.util.List;

/**
 * Created by liyangli on 17/1/10.
 */
public class ExpFileTask implements Runnable {
    private boolean flag = true;
    private ExpFile expFile = new ExpFile();
    @Override
    public void run() {
        //真正执行对应expFile的任务
        System.out.println("i am expFile in .......");
        long start  = System.currentTimeMillis();
        while(flag){
            List<Customer> list = CustomerCache.CACHE.findCache();
            if(list == null || list.size() == 0){
                //如果list为null表明执行完毕.需要进行跳出当前的线程;
                System.out.println("执行完成导出文件=========获取内容为null;");
                flag = false;
                expFile.close();
                break;
            }
            //开始执行对应写入文件中从左
            expFile.export(list);
        }
        System.out.println("执行完成文件导出消耗时间为->"+(System.currentTimeMillis()-start));
    }

    public void removeFile() {
       expFile.removeFile();
    }
}
