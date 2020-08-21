
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DiversifyAcctSVC extends CSBizService
{

    public IDataset getFirstDayNextAcct(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String firstdaynextAcct = DiversifyAcctUtil.getFirstDayNextAcct(userId);
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("FIRST_DAY_NEXTACCT", firstdaynextAcct);
        dataset.add(data);
        return dataset;
    }

    public IDataset getLastTimeThisAcctday(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String theday = DiversifyAcctUtil.getLastTimeThisAcctday(userId, null);
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("LAST_TIME_NOWACCT", theday);
        dataset.add(data);
        return dataset;
    }

    /**
     * 获取用户开户账期数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getNewAcctDayByOpenUser(IData input) throws Exception
    {
        return DiversifyAcctUtil.getNewAcctDayByOpenUser(input);
    }

    /**
     * 获取用户账期变更数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getNewAcctDaysByModi(IData input) throws Exception
    {
        return DiversifyAcctUtil.getNewAcctDaysByModi(input);
    }

    /**
     * 获取用户的账期信息
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public IDataset getUserAcctDay(IData input) throws Exception
    {
        String userid = input.getString("USER_ID");
        IData acctData = DiversifyAcctUtil.getUserAcctDay(userid);
        IDataset ds = new DatasetList();
        ds.add(acctData);
        return ds;
    }

    public IDataset getUserAcctDescMessage(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String desctag = input.getString("DESC_TAG");
        String descmess = DiversifyAcctUtil.getUserAcctDescMessage(userId, desctag);
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("DESC_MESS", descmess);
        dataset.add(data);
        return dataset;
    }
}
