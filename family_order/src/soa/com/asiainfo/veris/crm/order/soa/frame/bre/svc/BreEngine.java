
package com.asiainfo.veris.crm.order.soa.frame.bre.svc;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.protocol.IBaseService;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.rule.log.BreBizLog;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBizService;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.IGETRuleList;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREDataPrepare;
import com.asiainfo.veris.crm.order.soa.frame.bre.thread.BreThreadPool;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreCacheHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreSuperLimit;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public final class BreEngine extends BreBizService
{
    private static final Logger logger = Logger.getLogger(BreEngine.class);

    /**
     * 业务规则检查
     * 
     * @param databus
     * @return
     * @throws Exception
     */
    public static IData bre4SuperLimit(IData databus) throws Exception
    {
        long lstartTime = BreBizLog.getStartTime();

        if (!databus.containsKey("TIPS_TYPE"))
        {
            databus.put("TIPS_TYPE", "0|1|2|4");
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> bre4SuperLimit start = " + System.currentTimeMillis());
        }

        // 规则并发执行
        ruleFlow4SuperLimitParallel(databus);

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< bre4SuperLimit end = " + System.currentTimeMillis());
        }

        BreTipsHelp.formatInfo(databus);

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("RULE_INFO === " + databus.getDataset("RULE_INFO"));
            logger.debug("TIPS_TYPE_TIP === " + databus.getDataset("TIPS_TYPE_TIP"));
            logger.debug("TIPS_TYPE_ERROR === " + databus.getDataset("TIPS_TYPE_ERROR"));
            logger.debug("TIPS_TYPE_CHOICE === " + databus.getDataset("TIPS_TYPE_CHOICE"));
            logger.debug("-----------------------BreEngineSvc 服务返回信息!!!!!!");
        }

        BreBizLog.log(databus, "BreEngine.bre4SuperLimit", lstartTime);

        return databus;
    }

    /* 构造线程池需要的规则数据结构, 方法已经作废, 合并到 getRuleList() */
    private static void createListRule(String strBizId, IDataset listRule, int idxStart, int idxEnd, IDataset listRuleBiz) throws Exception
    {
        IDataset listDef = new DatasetList();

        for (int idx = idxStart; idx < idxEnd; idx++)
        {
            listDef.add(listRule.getData(idx));
        }

        IData ruleMap = new DataMap();

        ruleMap.put("RULE_BIZ_ID", listDef.get(0, "RULE_BIZ_ID"));
        ruleMap.put("RULE_DEF", listDef);

        listRuleBiz.add(ruleMap);
    }

    /**
     * SuperLimit类校验规则入口 ×
     * 
     * @author gaoyuan3@asiainfo-linkage.com @ 2012-1-16
     * @param pd
     * @param databus
     * @param outData
     * @throws Exception
     */
    private static void ruleFlow4SuperLimitParallel(IData databus) throws Exception
    {
        long lstartTime = BreBizLog.getStartTime();

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("多线程规则判断！");
        }

        /* 先把错误rule_info 准备好 */
        IDataset listRuleInfo = databus.getDataset("RULE_INFO");
        if (listRuleInfo == null)
        {
            listRuleInfo = new DatasetList();
            databus.put("RULE_INFO", listRuleInfo);
        }

        /* 规则结果用map 缓存起来， key = script_id + ruleParam.toString(), value = true|false */
        IData ruleResult = databus.getData("RULE_RESULT");
        if (ruleResult == null)
        {
            ruleResult = new DataMap();
            databus.put("RULE_RESULT", ruleResult);
        }

        /* 把参数转化成 BreDataBus， 再进行规则判断 */
        BreDataBus bus = new BreDataBus(databus);

        /* 并行规则之前调一下缓存， 确保第一次调用并行是正确的 */
        BreCacheHelp.getScriptObjectByScriptId("TD_S_TRADETYPE");
        BreCacheHelp.getRulePerameterMapByRuleId("2009112202440443");

        // IDataset listRule = null;

        /* 数据准备过程 */
        databus.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>> 数据准备过程 start " + System.currentTimeMillis());
        }

        // 针对不同场景，捞取不同的规则。--2929225571
        IDataset listRule = null;
      
        // 将查询出来的规则记录分解成线程池需要的数据组成形式
        IDataset listRuleBiz = null;
        
        // 以下逻辑没用，屏蔽了，luoying 2017/6/2 15:50:33
//        listRule = new DatasetList();
//        
//        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
//        {
//            logger.debug(" 查询到规则 --------------------------------------------------" + listRule.size());
//        }
//
//        listRuleBiz = splitRuleList(databus, listRule);
//
//        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
//        {
//            logger.debug(">>>>>>>>>>>>>> 数据准备过程规则list准备完成 and  list.size() = " + listRuleBiz.size());
//        }
//
//        String scriptId = "";
//        IData scriptMap = null;
//
//        /* 执行数据准备规则 */
//        int listRuleSize = listRule.size();
//        for (int idx = 0; idx < listRuleSize; idx++)
//        {
//            scriptMap = listRule.getData(idx);
//            scriptId = scriptMap.getString("SCRIPT_ID");
//
//            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
//            {
//                logger.debug("－－－－－－－－－－－－调用数据准备脚本 script_id = " + scriptId);
//            }
//
//            IBREDataPrepare rule = (IBREDataPrepare) BreCacheHelp.getScriptObjectByScriptId(scriptId);
//            rule.run(bus);
//        }
//
//        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
//        {
//            logger.debug("<<<<<<<<<<<<<<<<<<<< 数据准备过程 end   " + System.currentTimeMillis());
//        }
//
//        /* 规则运算需要的数据准备完成, 清空数据加载规则内存, 准备加载规则计算数据 */
//
//        /* 构造规则list */
//        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
//        {
//            logger.debug(">>>>>>>>>>>>>>>>>>>> 构造规则数据 start " + System.currentTimeMillis());
//        }

        // listRule = getRuleList(databus, "0");
        /* 重构获取规则列表，各业务侧负责各自的特有的业务规则模型：未来演进，规则配置随各自业务和产品走。modify by xiaocl */
        IGETRuleList getRulelist = new GetRulelist();
        listRule = getRulelist.getRuleList(databus, new FactoryGetRulelist());
        /* 重构获取规则列表，各业务侧负责各自的特有的业务规则模型：未来演进，规则配置随各自业务和产品走。modify by xiaocl */

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug(" 查询到规则 --------------------------------------------------" + listRule.size());
        }

        /* 将数据按照 rule_biz_id 分类， 方便后面并行计算 */

        listRuleBiz = splitRuleList(databus, listRule);

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("<<<<<<<<<<<<<<<<<<<< 规则list准备完成 end  list.size() = " + listRuleBiz.size());
            logger.debug("<<<<<<<<<<<<<<<<<<<< 构造规则数据 end " + System.currentTimeMillis());
        }

        if (BreQueryHelp.isParallel())
        {
            /* 并行执行规则 */
            // 得到服务接口
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            {
                logger.debug(" ----------------------- threadpool rule --------------------");
            }

            IBaseService service = (IBaseService) SessionManager.getInstance().peek();
            BreThreadPool tpt = new BreThreadPool(bus);

            tpt.executeRule(bus, listRuleBiz, service);
        }
        else
        {
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            {
                logger.debug(" ----------------------- single rule --------------------");
            }

            for (Iterator iterRuleBiz = listRuleBiz.iterator(); iterRuleBiz.hasNext();)
            {
                IData rule = (IData) iterRuleBiz.next();
                IDataset listRuleDef = rule.getDataset("RULE_DEF");

                BreSuperLimit.jSuperList(bus, listRuleDef);
            }
        }

        databus.putAll(bus);
        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug("RULE_INFO === " + databus.getDataset("RULE_INFO"));
            logger.debug("TIPS_TYPE_TIP === " + databus.getDataset("TIPS_TYPE_TIP"));
            logger.debug("TIPS_TYPE_ERROR === " + databus.getDataset("TIPS_TYPE_ERROR"));
            logger.debug("TIPS_TYPE_CHOICE === " + databus.getDataset("TIPS_TYPE_CHOICE"));
            logger.debug("-----------------------end lto!!!!!!");
        }

        /* UDP 日志 结束时间 开始 是否监控chk */
        BreBizLog.log(databus, "BreEngine.ruleFlow4SuperLimitParallel", lstartTime);
        /* UDP 日志 结束时间 结束 是否监控chk */

    }

    /**
     * 将查询出来的规则记录分解成线程池需要的数据组成形式
     * 
     * @param listRule
     * @throws Exception
     */
    public static IDataset splitRuleList(IData databus, IDataset listRule) throws Exception
    {
        IDataset listRuleBiz = new DatasetList();

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            logger.debug(">>>>>>>>>>>>>> getRuleList start");
        }

        /* UDP 日志 开始时间 开始 是否监控chk */
        long lstartTime = BreBizLog.getStartTime();
        /* UDP 日志 开始时间 结束 是否监控chk */

        String strBizId = "";
        String strPrivBizId = "";
        int idxStart = 0;
        int idxEnd = 0;

        /* 用于构造业务线程池需要的规则数据结构 */

        /* 只有一条记录的时候需要特殊处理 */
        if (listRule.size() == 1)
        {
            IData ruleMap = new DataMap();
            ruleMap.put("RULE_BIZ_ID", listRule.get(0, "RULE_BIZ_ID"));
            ruleMap.put("RULE_DEF", listRule);

            listRuleBiz.add(ruleMap);

            return listRuleBiz;
        }

        /* 多条规则时候, 走这里的逻辑构造线程池需要的规则数据结构 */

        IDataset listDef = null;

        int listRuleSize = listRule.size();
        for (int idx = 0; idx < listRuleSize; idx++)
        {
            strBizId = (String) listRule.get(idx, "RULE_BIZ_ID");
            idxEnd = idx;

            /* 构造出线程池需要的规则数据结构 */
            if (!strBizId.equals(strPrivBizId) && !strPrivBizId.equals(""))
            {
                strPrivBizId = strBizId;

                /* 下面注释的一句话需要结合下面废弃的 createListRule 使用 */
                // this.createListRule(strPrivBizId, listRule, idxStart, idxEnd);

                listDef = new DatasetList();

                for (int iDef = idxStart; iDef < idxEnd; iDef++)
                {
                    listDef.add(listRule.getData(iDef));
                }

                IData ruleMap = new DataMap();
                ruleMap.put("RULE_BIZ_ID", listDef.get(0, "RULE_BIZ_ID"));
                ruleMap.put("RULE_DEF", listDef);

                listRuleBiz.add(ruleMap);

                idxStart = idxEnd;
            }

            strPrivBizId = strBizId;
        }

        /* 执行最后一组 */
        if (idxEnd > 0 && strPrivBizId.equals(strBizId) && !"".equals(strPrivBizId))
        {
            createListRule(strBizId, listRule, idxStart, idxEnd + 1, listRuleBiz);
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
        {
            for (int idx = 0; idx < listRuleBiz.size(); idx++)
            {
                logger.debug(" -!-!-!-!-!-!-!-!-!-!-!-!-!-!-! listRuleBiz[" + idx + "].RULE_BIZ_ID = " + listRuleBiz.get(idx, "RULE_BIZ_ID"));
            }

            logger.debug(">>>>>>>>>>>>>> getRuleList end");
        }

        /* UDP 日志 结束时间 开始 是否监控chk */
        BreBizLog.log(databus, "BreEngine.SplitRuleList", lstartTime);
        /* UDP 日志 结束时间 结束 是否监控chk */

        return listRuleBiz;
    }

}
