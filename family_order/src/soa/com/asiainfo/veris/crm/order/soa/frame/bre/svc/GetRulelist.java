
package com.asiainfo.veris.crm.order.soa.frame.bre.svc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.AbstractFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.IGETRuleList;

/**
 * Copyright: Copyright 2014/6/16 Asiainfo-Linkage
 * 
 * @ClassName: GetRulelist
 * @Description: 区分数据类规则，业务规则，或者其他规则 集合
 * @version: v1.0.0
 * @author: xiaocl
 */
public class GetRulelist implements IGETRuleList
{
    @Override
    public IDataset getRuleList(IData databus, Object o) throws Exception
    {
        IDataset listRule = new DatasetList();
        AbstractFactory object = null;
        if (o instanceof FactoryGetRulelist)
        {
            object = (FactoryGetRulelist) o;
        }
        // 定义：AUTH，SUBMIT,提交BUILDER，点包，点产品，实物校验，预存赠送校验等等
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String strPageRuleTag = "";
        if (databus.containsKey("PAGE_RULE"))
        {
            strPageRuleTag = databus.getString("PAGE_RULE", "");
        }
        String strActionType = databus.getString("ACTION_TYPE");
        // 查询和SUBMMIT时 提交登记服务时
        if ("".equals(strPageRuleTag))
        {
            listRule = ((IGETRuleList) object.GetInstance(BreConst.RuleSet.BASE, true)).getRuleList(databus, "0");
        }
        /*
         * if("0".equals(xChoiceTag) || "1".equals(xChoiceTag)){ listRule = ((IGETRuleList)
         * object.Creator(BreConst.RuleSet.BASE,false)).getRuleList(databus, "0"); } //提交登记服务时 if(
         * StringUtils.isBlank(xChoiceTag) && ( strActionType.equals(BreConst.RuleSet.RG_TD_AFTER) ||
         * strActionType.equals(BreConst.RuleSet.RG_TD_BEFORE) )){ listRule = ((IGETRuleList)
         * object.Creator(BreConst.RuleSet.BASE,false)).getRuleList(databus, "0"); }
         */

        // 页面操作时或登记服务时候 暂时先这么做，下个省这个地方再把界限划合理一点
        if ((strTradeTypeCode.equals("110") && strPageRuleTag.equals(BreConst.RuleSet.PAGE_RULE)) || (strTradeTypeCode.equals("110") && (strActionType.equals(BreConst.RuleSet.RG_TD_BEFORE) || strActionType.equals(BreConst.RuleSet.RG_TD_AFTER))))
        {
            if (((IGETRuleList) object.GetInstance(BreConst.RuleSet.CHANGEELEMENT, false)).getRuleList(databus, "0") == null)
            {

            }
            else
                listRule.addAll(((IGETRuleList) object.GetInstance(BreConst.RuleSet.CHANGEELEMENT, false)).getRuleList(databus, "0"));
        }
        if (strTradeTypeCode.equals("3700"))
        {
            if (((IGETRuleList) object.GetInstance(BreConst.RuleSet.SP, false)).getRuleList(databus, "0") == null)
            {

            }
            else
                listRule.addAll(((IGETRuleList) object.GetInstance(BreConst.RuleSet.SP, false)).getRuleList(databus, "0"));
        }
        if (strTradeTypeCode.equals("240"))
        {
            if (((IGETRuleList) object.GetInstance(BreConst.RuleSet.SALEACTIVE, false)).getRuleList(databus, "0") == null)
            {
            }
            else
                listRule.addAll(((IGETRuleList) object.GetInstance("SALEACTIVE", false)).getRuleList(databus, "0"));
        }
        if (strActionType.equals(BreConst.RuleSet.GRP))
        {
            listRule.addAll(((IGETRuleList) object.GetInstance(BreConst.RuleSet.GRP, false)).getRuleList(databus, "0") == null ? new DatasetList() : ((IGETRuleList) object.GetInstance(BreConst.RuleSet.GRP, false)).getRuleList(databus, "0"));
        }

        return listRule;
    }

    void GetRulelist()
    {

    }
}
