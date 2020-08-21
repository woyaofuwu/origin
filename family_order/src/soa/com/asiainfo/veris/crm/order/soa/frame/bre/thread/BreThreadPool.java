
package com.asiainfo.veris.crm.order.soa.frame.bre.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.BizVisit;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.service.protocol.IBaseService;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.rule.log.BreBizLog;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreSuperLimit;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @className: BreThreadPool
 * @description: 规则线程池
 * @version: v1.0.0
 * @author: 高原
 * @author: 罗颖
 * @author: 周麟
 * @date: 2013-10-17
 */
public class BreThreadPool extends BreBase
{

    /**
     * 任务类
     */
    final class BreTask extends BreBase implements Runnable
    {

        private IDataset listDef = null;

        private IBaseService service = null;

        /* 把规则ID传过来, 方便定位问题在哪里 */
        private String strRuleBizId = null;

        private BizVisit visit = null;

        private CountDownLatch latch;

        public BreTask(IData ruleData, BizVisit visit, IBaseService service, CountDownLatch latch)
        {
            this.visit = visit;
            this.strRuleBizId = ruleData.getString("RULE_BIZ_ID");
            this.listDef = ruleData.getDataset("RULE_DEF");
            this.service = service;
            this.latch = latch;
        }

        /**
         * 获取线程池当前状态信息
         * 
         * @param pool
         * @return
         */
        public String getPoolInfo(ThreadPoolExecutor pool)
        {
            int poolSize = pool.getPoolSize(); // 线程池大小 当前工作线程数量
            int activeCount = pool.getActiveCount(); // 正在运行的任务
            long completedTaskCount = pool.getCompletedTaskCount(); // 运行结束的任务
            long taskCount = pool.getTaskCount(); // 任务总数
            long taskWaitCount = taskCount - completedTaskCount - activeCount; // 等待运行的 任务队列里的任务数量

            StringBuilder sbuff = new StringBuilder(200);
            sbuff.append("poolSize=").append(poolSize);
            sbuff.append(",activeCount=").append(activeCount);
            sbuff.append(",completedTaskCount=").append(completedTaskCount);
            sbuff.append(",taskCount=").append(taskCount);
            sbuff.append(",taskWaitCount=").append(taskWaitCount);
            return sbuff.toString();
        }

        @Override
        public void run()
        {

            try
            {

                /* 如果碰到强制退出标记 FORCE_EXIT, 剩下的线程都不作事情, 直接退出 */
                if (databus.containsKey("FORCE_EXIT"))
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("find FORCE_EXIT! strRuleBizId = " + strRuleBizId);
                    }
                }

                try
                {
                    SessionManager.getInstance().start();
                    SessionManager.getInstance().setContext(service, visit);
                    BreSuperLimit.jSuperList(databus, listDef); // 调用规则判断
                    SessionManager.getInstance().commit();
                }
                catch (Exception e)
                {
                    try
                    {
                        SessionManager.getInstance().rollback();
                        logger.error("connection error！", e);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
                finally
                {
                    try
                    {
                        SessionManager.getInstance().destroy();
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
            finally
            {
                latch.countDown();
            }

        }
    }

    private static final Logger logger = Logger.getLogger(BreThreadPool.class);

    private IData databus = null;

    public BreThreadPool(IData databus)
    {
        this.databus = databus;
    }

    /**
     * 并发执行规则集
     * 
     * @param databus
     * @param listRuleBiz
     * @param service
     * @throws Exception
     */
    public void executeRule(IData databus, IDataset listRuleBiz, IBaseService service) throws Exception
    {

        long startTime = BreBizLog.getStartTime();

        BreTask task = null;
        IData ruleData = null;

        int iMaxTaskSize = listRuleBiz.size();
        if (0 == iMaxTaskSize)
        {
            return;
        }

        /* 任务队列 */
        BlockingQueue<Runnable> blockQueue = new LinkedBlockingQueue<Runnable>(iMaxTaskSize);

        /* 创建线程池 */
        ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, blockQueue);
        CountDownLatch latch = new CountDownLatch(iMaxTaskSize);

        /* 开始规则校验 */
        for (int i = 0; i < iMaxTaskSize; i++)
        {
            ruleData = listRuleBiz.getData(i);
            task = new BreTask(ruleData, getVisit(), service, latch);
            try
            {
                pool.execute(task);
            }
            catch (Exception e)
            {
                logger.error("往规则线程池投放任务发生异常!", e);
            }
        }

        latch.await(20, TimeUnit.SECONDS); // 等待子线程全部处理完毕
        pool.shutdown();
        listRuleBiz.clear(); // 清空规则内存

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("RuleThreadPool execute time(ms):" + (System.currentTimeMillis() - startTime));
        }

        BreBizLog.log(databus, "BreThreadPool.executeRule", startTime);
    }
}
