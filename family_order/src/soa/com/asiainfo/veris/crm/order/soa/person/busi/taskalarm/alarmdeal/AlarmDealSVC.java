
package com.asiainfo.veris.crm.order.soa.person.busi.taskalarm.alarmdeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AlarmDealSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    public IDataset queryAlarmByCond(IData input) throws Exception
    {
        AlarmDealBean bean = (AlarmDealBean) BeanManager.createBean(AlarmDealBean.class);
        return bean.queryAlarmByCond(input, null);
    }

    public IDataset queryAlarmByMonth(IData input) throws Exception
    {
        AlarmDealBean bean = (AlarmDealBean) BeanManager.createBean(AlarmDealBean.class);
        return bean.queryAlarmByMonth(input, null);
    }

    public IDataset queryChart(IData input) throws Exception
    {
        AlarmDealBean bean = (AlarmDealBean) BeanManager.createBean(AlarmDealBean.class);
        return bean.queryChart(input);
    }

    public IDataset updAlarmClose(IData input) throws Exception
    {
        AlarmDealBean bean = (AlarmDealBean) BeanManager.createBean(AlarmDealBean.class);
        IData data = new DataMap();
        IDataset dataset = new DatasetList();
        int updateSuccessFlag = bean.updAlarmClose(input, getPagination());
        data.put("UPDATE_SUCCESS_FLAG", updateSuccessFlag);
        dataset.add(data);
        return dataset;
    }

    public IDataset updAlarmState(IData input) throws Exception
    {
        AlarmDealBean bean = (AlarmDealBean) BeanManager.createBean(AlarmDealBean.class);
        IData data = new DataMap();
        IDataset dataset = new DatasetList();
        int dealSuccessFlag = bean.updAlarmState(input, getPagination());
        data.put("DEAL_SUCCESS_FLAG", dealSuccessFlag);
        dataset.add(data);
        return dataset;
    }

    public IDataset updAlarmStateBatch(IData input) throws Exception
    {
        AlarmDealBean bean = (AlarmDealBean) BeanManager.createBean(AlarmDealBean.class);
        IData data = new DataMap();
        IDataset dataset = new DatasetList();
        int dealSuccessFlag = bean.updAlarmStateBatch(input, getPagination());
        data.put("DEAL_SUCCESS_FLAG", dealSuccessFlag);
        dataset.add(data);
        return dataset;
    }

}
