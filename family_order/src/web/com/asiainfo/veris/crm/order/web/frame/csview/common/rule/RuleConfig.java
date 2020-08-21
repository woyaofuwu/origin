
package com.asiainfo.veris.crm.order.web.frame.csview.common.rule;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class RuleConfig extends CSBasePage
{

    /**
     * 保存规则集
     * 
     * @param cycle
     * @throws Exception
     */
    public void saveRuleBusiness(IRequestCycle cycle) throws Exception
    {
        IData pgData = getData();
        IDataset infos = CSViewCall.call(this, "CS.RuleConfigSVC.saveRuleBusiness", pgData);
        setAjax(infos);
    }

    /**
     * 保存规则
     * 
     * @param cycle
     * @throws Exception
     */
    public void saveRuleDifinition(IRequestCycle cycle) throws Exception
    {
        IData pgData = getData();
        IDataset infos = CSViewCall.call(this, "CS.RuleConfigSVC.saveRuleDifinition", pgData);
        setAjax(infos);
    }

    /**
     * 保存规则集和规则的关系
     * 
     * @param cycle
     * @throws Exception
     */
    public void saveRuleRelation(IRequestCycle cycle) throws Exception
    {
        IData pgData = getData();
        IDataset infos = CSViewCall.call(this, "CS.RuleConfigSVC.saveRuleRelation", pgData);
        setAjax(infos);
    }
}
