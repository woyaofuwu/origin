
package com.asiainfo.veris.crm.order.soa.person.busi.taskalarm.managerule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ManageRuleSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    public IDataset checkClassIsAdd(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        return bean.checkClassIsAdd(data, getPagination());
    }

    public IDataset checkClassIsUpdate(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        return bean.checkClassIsUpdate(data, getPagination());
    }

    public IDataset delRuleBatch(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        IData Returndata = new DataMap();
        IDataset dataset = new DatasetList();
        int dealSuccessFlag = bean.delRuleBatch(data, getPagination());
        Returndata.put("DELETE_SUCCESS_FLAG", dealSuccessFlag);
        dataset.add(Returndata);
        return dataset;
    }

    public IDataset disenableRuleBatch(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        IData Returndata = new DataMap();
        IDataset dataset = new DatasetList();
        int dealSuccessFlag = bean.disenableRule(data, getPagination());
        Returndata.put("DISENABLE_SUCCESS_FLAG", dealSuccessFlag);
        dataset.add(Returndata);
        return dataset;
    }

    public IDataset enableRuleBatch(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        IData Returndata = new DataMap();
        IDataset dataset = new DatasetList();
        int dealSuccessFlag = bean.enableRule(data, getPagination());
        Returndata.put("ENABLE_SUCCESS_FLAG", dealSuccessFlag);
        dataset.add(Returndata);
        return dataset;
    }

    public IDataset getRuleID(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        IData Returndata = new DataMap();
        IDataset dataset = new DatasetList();
        String ruleId = bean.getRuleID(data, getPagination());
        Returndata.put("RULE_ID", ruleId);
        dataset.add(Returndata);
        return dataset;
    }

    public IDataset insertRuleToAlarm(IData input) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        IData Returndata = new DataMap();
        IDataset dataset = new DatasetList();
        boolean addSuccessFlag = bean.insertRuleToAlarm(input, getPagination());
        if (addSuccessFlag == true)
        {
            Returndata.put("ADD_SUCCESS_FLAG", 1);
        }
        else
        {
            Returndata.put("ADD_SUCCESS_FLAG", 0);
        }
        dataset.add(Returndata);
        return dataset;
    }

    public IDataset isRunning(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        return bean.isRunning(data, getPagination());
    }

    /**
     * 功能：查询ClassNames 作者：GongGuang
     */
    public IDataset queryClassNames(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        return bean.queryClassNames(data, getPagination());
    }

    public IDataset queryExecTime(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        return bean.queryExecTime(data, getPagination());
    }

    /**
     * 功能：查询Levels 作者：GongGuang
     */
    public IDataset queryLevels(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        return bean.queryLevels(data, getPagination());
    }

    /**
     * 功能：查询EditRule初始化内容 作者：GongGuang
     */
    public IDataset queryRuleByPK(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        return bean.queryRuleByPK(data, getPagination());
    }

    /**
     * 功能：查询AddRule内容 作者：GongGuang
     */
    public IDataset queryRuleByPKSpecial(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        return bean.queryRuleByPKSpecial(data, getPagination());
    }

    /**
     * 功能：查询RulesByCond 作者：GongGuang
     */
    public IDataset queryRulesByCond(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        return bean.queryRulesByCond(data, getPagination());
    }

    public void updateRule(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        bean.updateRule(data, getPagination());
    }

    public void updateTaskClassName(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        bean.updateTaskClassName(data, getPagination());
    }

    public void updateTaskPlan(IData data) throws Exception
    {
        ManageRuleBean bean = (ManageRuleBean) BeanManager.createBean(ManageRuleBean.class);
        bean.updateTaskPlan(data, getPagination());
    }

}
