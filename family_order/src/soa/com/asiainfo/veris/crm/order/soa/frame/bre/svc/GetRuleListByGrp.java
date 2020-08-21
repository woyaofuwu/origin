
package com.asiainfo.veris.crm.order.soa.frame.bre.svc;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GetRuleListByGrp extends AbstractIGETRuleListRuleList
{
    private static final Logger logger = Logger.getLogger(GetRuleListByBase.class);

    public IDataset getRuleFlow(IData param) throws Exception
    {
    	IDataset listAllRule=null;
    	//校讯通成员订购，注销，优惠变更，不需要校验其他业务是否有未完工工单 update by zhuwj
    	if(param.getString("TRADE_TYPE_CODE","").equals("3644")
    			||param.getString("TRADE_TYPE_CODE","").equals("3645")
    			||param.getString("TRADE_TYPE_CODE","").equals("3646")
    			||param.getString("TRADE_TYPE_CODE","").equals("3647")){
    		 listAllRule = Dao.qryByCode("TD_S_CPARAM", "SEL_RULE_GRPXXT", param, Route.CONN_CRM_CEN);
    		
    	}else{
    		 listAllRule = Dao.qryByCode("TD_S_CPARAM", "SEL_RULE_GRP", param, Route.CONN_CRM_CEN);
    	}
    	
       

        /* 如果没有找到规则就直接返回 */
        if (listAllRule.size() == 0)
        {
            return listAllRule;
        }
        else
        {
            if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
                logger.debug(" CCCCCCCCCCCCCC 查询到规则数据[" + listAllRule.size() + "]");
        }

        /* 判断产品， 品牌，superusr， 和员工权限 */
        String strRightCode = "";
        String strRuleBrandCode = "";
        /*
         * String strRuleProductId = ""; String strRulePackageId = "";
         */
        IDataset idsRet = new DatasetList();
        boolean priv = false;
        boolean isSupserUser = "SUPERUSR".equals(getVisit().getStaffId());
        String strBrandCode = param.getString("BRAND_CODE");

        for (Iterator iterator = listAllRule.iterator(); iterator.hasNext();)
        {
            IData bizrule = (IData) iterator.next();

            /* 先判断产品和品牌 */

            strRuleBrandCode = bizrule.getString("BRAND_CODE", "");

            if ("ZZZZ".equals(strRuleBrandCode) || strRuleBrandCode.equals(strBrandCode))
            {
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
                }
            }
        }

        if (logger.isDebugEnabled() || BizEnv.getEnvBoolean("bre.isDebugEnabled"))
            logger.debug(" GGGGGGGGGGGGGGGGGGG 过滤产品，品牌，SUPERUSR,和权限后， 剩下规则数据[" + idsRet.size() + "]");

        return idsRet;
    }

    public IDataset getRuleList(IData databus, Object o /* String strRuleTwigCode */) throws Exception
    {
        return super.getRuleList(databus, o);
    }

    @Override
    public void orgDataBus(IData databus, String strRuleTwigCode, IData param)
    {
        // TODO Auto-generated method stub
        String ruleBizKindCode = databus.getString("RULE_BIZ_KIND_CODE", "");

        if (StringUtils.isBlank(ruleBizKindCode))
        {
            ruleBizKindCode = "TradeCheckSuperLimit";
        }
        if (databus.getString("ACTION_TYPE") == null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("规则入口请配置动作类型参数！");
            }
        }

        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        param.put("SPEC_CDTION", databus.getString("SPEC_CDTION"));
        param.put("TRADE_TYPE_CODE", strTradeTypeCode);
        param.put("PRODUCT_ID", databus.getString("PRODUCT_ID"));
        param.put("RULE_BIZ_KIND_CODE", ruleBizKindCode);
    }

}
