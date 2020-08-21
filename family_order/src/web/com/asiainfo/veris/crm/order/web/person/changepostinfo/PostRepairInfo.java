
package com.asiainfo.veris.crm.order.web.person.changepostinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class PostRepairInfo extends PersonBasePage
{
    private void getPostInfo(IData allInfo) throws Exception
    {
        IData data = CSViewCall.callone(this, "SS.ModifyPostInfoSVC.getPostInfo", allInfo);
        if (StringUtils.equals(data.getString("IS_POST"), "1"))
        {
            setOldPostInfo(data);
        }
        else
        {
            CSViewException.apperr(CrmUserException.CRM_USER_724);// 用户未办理邮寄业务!
        }
    }

    private void getRepairInfo(IData userInfo) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userInfo.getString("USER_ID"));
        params.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        IDataset data = CSViewCall.call(this, "SS.ModifyPostInfoSVC.getPostRepairInfo", params);
        setPostRepairInfos(data);
    }

    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        IData dataset = new DataMap();

        IData userInfo = new DataMap(data.getString("USER_INFO", ""));

        dataset.put("USER_INFO", userInfo);
        dataset.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
        // 查询子业务信息
        getPostInfo(dataset);
        getRepairInfo(userInfo);

    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData("postinfo", true);
        IData data2 = getData();

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", getData().getString("AUTH_SERIAL_NUMBER"));
        param.putAll(data);
        param.putAll(data2);

        IDataset dataset = CSViewCall.call(this, "SS.PostRepairInfoRegSVC.tradeReg", param);
        setAjax(dataset);
    }

    public abstract void setOldPostInfo(IData oldPostInfo);

    public abstract void setPostRepairInfos(IDataset postRepairInfos);

}
