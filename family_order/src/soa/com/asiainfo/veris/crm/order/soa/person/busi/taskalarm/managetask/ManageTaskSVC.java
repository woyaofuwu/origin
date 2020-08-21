
package com.asiainfo.veris.crm.order.soa.person.busi.taskalarm.managetask;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class ManageTaskSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    public IDataset delConfiguredTask(IData input) throws Exception
    {
        ManageTaskBean bean = (ManageTaskBean) BeanManager.createBean(ManageTaskBean.class);
        IData data = new DataMap();
        IDataset dataset = new DatasetList();
        int deleteSuccessFlag = bean.delConfiguredTask(input, getPagination());
        data.put("DELETE_SUCCESS_FLAG", deleteSuccessFlag);
        dataset.add(data);
        return dataset;
    }

    public IDataset insertTaskConfiguration(IData input) throws Exception
    {
        ManageTaskBean bean = (ManageTaskBean) BeanManager.createBean(ManageTaskBean.class);
        IData data = new DataMap();
        IDataset dataset = new DatasetList();
        boolean addSuccessFlag = bean.insertTaskConfiguration(input, getPagination());
        if (addSuccessFlag == true)
        {
            data.put("ADD_SUCCESS_FLAG", 1);
        }
        else
        {
            data.put("ADD_SUCCESS_FLAG", 0);
        }
        dataset.add(data);
        return dataset;
    }

    public IDataset isTaskConfigured(IData input) throws Exception
    {
        ManageTaskBean bean = (ManageTaskBean) BeanManager.createBean(ManageTaskBean.class);
        return bean.isTaskConfigured(input, null);
    }

    public IDataset queryConfiguredTask(IData input) throws Exception
    {
        ManageTaskBean bean = (ManageTaskBean) BeanManager.createBean(ManageTaskBean.class);
        return bean.queryConfiguredTask(input, getPagination());
    }

    public IDataset queryTaskByCon(IData input) throws Exception
    {
        ManageTaskBean bean = (ManageTaskBean) BeanManager.createBean(ManageTaskBean.class);
        return bean.queryTaskByCon(input, null);
    }

    public IDataset updateConfiguredTask(IData input) throws Exception
    {
        ManageTaskBean bean = (ManageTaskBean) BeanManager.createBean(ManageTaskBean.class);
        IData data = new DataMap();
        IDataset dataset = new DatasetList();
        int updateSuccessFlag = bean.updateConfiguredTask(input, getPagination());
        data.put("UPDATE_SUCCESS_FLAG", updateSuccessFlag);
        dataset.add(data);
        return dataset;
    }
}
