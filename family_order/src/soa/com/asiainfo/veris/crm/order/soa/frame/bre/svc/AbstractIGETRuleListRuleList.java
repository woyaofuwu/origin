
package com.asiainfo.veris.crm.order.soa.frame.bre.svc;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.rule.log.BreBizLog;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.IGETRuleList;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage *
 * 
 * @ClassName: AbstractIGETRuleListRuleList
 * @Description: 重构获取规则列表
 * @version: v1.0.0
 * @author: xiaocl
 */
public abstract class AbstractIGETRuleListRuleList extends BreBase implements IGETRuleList
{
    private static final Logger logger = Logger.getLogger(AbstractIGETRuleListRuleList.class);

    /* 获取规则之后的相关过滤,权限控制为公用，可以重载这部分方法 */
    public IDataset filterObject(IDataset listAllRule, IData param) throws Exception
    {

        /* 判断产品， 品牌，superusr， 和员工权限 */
        String strRightCode = "";
        /*
         * String strRuleProductId = ""; String strRuleBrandCode = ""; String strRulePackageId = "";
         */
        IDataset idsRet = new DatasetList();
        boolean priv = false;
        boolean isSupserUser = "SUPERUSR".equals(getVisit().getStaffId());
        String strInModeCode = param.getString("IN_MODE_CODE");

        for (Iterator iterator = listAllRule.iterator(); iterator.hasNext();)
        {
            IData bizrule = (IData) iterator.next();
            
            /* 渠道过滤 */
            boolean bTagMode = false;
            String orginStr = bizrule.getString("IN_MODE_CODE", "-1");
            if (!StringUtils.equals(orginStr, "-1"))
            {
            	//兼容现有配置，现有配置是没有|分隔符的
            	if(orginStr.indexOf(",") == -1)
            	{
            		if(orginStr.indexOf(strInModeCode) == -1)
            		{
            			continue;
            		}
            	}
            	else
            	{
            		String strInmodeCodeSplit = "";
                    StringTokenizer token = new StringTokenizer(orginStr, ",");
                    while (token.hasMoreTokens())
                    {
                        strInmodeCodeSplit = token.nextToken();
                        if (StringUtils.equals(strInmodeCodeSplit, strInModeCode))
                        {
                            bTagMode = true;
                            break;
                        }
                        else
                            continue;
                    }
                    if (!bTagMode)
                        continue;
            	}
            }

            strRightCode = bizrule.getString("RIGHT_CODE", "");

            /* SUPERUSR 工号的特殊处理 */
            if (isSupserUser)
            {
                if (bizrule.getString("RIGHT_CODE") == null || StringUtils.isBlank(bizrule.getString("RIGHT_CODE")))
                {
                    idsRet.add(bizrule);
                }
            }
            else
            {
                if (StringUtils.isBlank(strRightCode))
                {
                    idsRet.add(bizrule);
                    continue;
                }

                /* 判断数据权限 */
                priv = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), strRightCode);

                /* 员工有权限就跳过，不判断规则 */
                if (!priv)
                {
                    idsRet.add(bizrule);
                }
                else
                {
                    if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                        logger.debug(" QQQQQQQQQQQQQQQQQQQQQQQQQQQQQQ [" + bizrule.getString("RULE_BIZ_ID") + "][" + strRightCode + "]");
                }

                if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                    logger.debug(" GGGGGGGGGGGGGGGGGGG 过滤产品，品牌，SUPERUSR,和权限后， 剩下规则数据[" + idsRet.size() + "]");
            }
        }
        return idsRet;

    }

    /* 组织好获取规则的条件后，具体获取规则 */
    public abstract IDataset getRuleFlow(IData param) throws Exception;

    /**
     * 获取规则数据
     * 
     * @param databus
     * @param strRuleTwigCode
     * @return
     * @throws Exception
     */
    public IDataset getRuleList(IData databus, Object o /* String strRuleTwigCode */) throws Exception
    {

        long lstartTime = BreBizLog.getStartTime();
        IData param = new DataMap();
        String strActionType = databus.getString("ACTION_TYPE");
        if ("TRADEALL.TradeCheckAfter".equals(strActionType))
        {
            strActionType = "TradeCheckAfter";
        }
        else if ("TRADEALL.TradeCheckBefore".equals(strActionType))
        {
            strActionType = "TradeCheckBefore";
        }
        param.put("RULE_BIZ_TWIG_CODE", String.valueOf(o));// 业务规则 0 业务规则 1数据加载规则
        param.put("ACTION_TYPE", strActionType);// 针对不同的动作类型,比如点击查询时,比如提交,比如选择某一优惠/某一服务,自定义
        param.put("IN_MODE_CODE", databus.getString("IN_MODE_CODE"));
        param.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("TIPS_TYPE", databus.getString("TIPS_TYPE"));
        param.put("EPARCHY_CODE", databus.getString("EPARCHY_CODE"));
        param.put("ORDER_TYPE_CODE", databus.getString("ORDER_TYPE_CODE", "*"));
        param.put("ACTION_ID", databus.getString("ACTION_ID"));
        param.put("BRAND_CODE", databus.getString("BRAND_CODE"));
        this.orgDataBus(databus, String.valueOf(o), param);
        // IDataset ids = this.getRuleFlow(param);
        IDataset ids = filterObject(this.getRuleFlow(param),param);
        // 特殊RULE_BIZ_ID,保留,其他RULE_BIZ_ID直接过滤掉
        if (databus.containsKey("LIST_BIZ_ID") && databus.getDataset("LIST_BIZ_ID").size() > 0)
        {
            ids = BreQueryHelp.specListBizId(ids, databus.getDataset("LIST_BIZ_ID"));
        }
        BreBizLog.log(databus, "BreEngine.getRuleList", lstartTime);

        return ids;
    }

    /* 获取时规则的 条件组织 */
    public abstract void orgDataBus(IData databus, String strRuleTwigCode, IData param);
}
