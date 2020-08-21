
package com.asiainfo.veris.crm.order.web.person.multisnonecard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class SearchMultiSNOneCard extends PersonBasePage
{
    /**
     * popup初始化，获取开通国漫一卡多号地区列表
     * 
     * @param cycle
     * @throws Exception
     * @author wangww
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        IData tempdata = new DataMap();

        IDataset dataset = CSViewCall.call(this, "SS.SearchMultiSNOneCardSVC.getdeployState", tempdata);

        setDeployList(dataset);
        setCondition(pagedata);
    }

    /**
     * 获取副卡号码列表
     * 
     * @param cycle
     * @throws Exception
     * @author wangww
     */
    public void queryCard(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();
        IData tempdata = new DataMap();
        tempdata.put("USERCARD", pagedata.getString("USERCARD"));
        IDataset datasetCard = CSViewCall.call(this, "SS.SearchMultiSNOneCardSVC.getPreforOneCardMutiNumber", tempdata);

        tempdata.clear();
        tempdata.put("PARA_VALUE1", datasetCard.getData(0).getString("DATA_NAME"));
        tempdata.put("USER_EPARCHY_CODE", pagedata.getString("USER_EPARCHY_CODE"));
        tempdata.put("EPARCHY_CODE", "ZZZZ");
        tempdata.put("PARA_ATTR", "9100");
        tempdata.put("PARA_CODE1", "ONECARD_MANYNUM_DATA");
        IDataset dataset = CSViewCall.call(this, "SS.SearchMultiSNOneCardSVC.querySearchCard", tempdata);

        tempdata.clear();
        IDataset deploy = CSViewCall.call(this, "SS.SearchMultiSNOneCardSVC.getdeployState", tempdata);

        setDeployList(deploy);
        setCopList(dataset);
        setCondition(pagedata);
    }

    public abstract void setCondition(IData data);

    public abstract void setCopList(IDataset list);

    public abstract void setDeployList(IDataset dataset);
}
