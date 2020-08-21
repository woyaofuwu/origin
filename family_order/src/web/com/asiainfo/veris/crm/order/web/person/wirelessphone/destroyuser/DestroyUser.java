
package com.asiainfo.veris.crm.order.web.person.wirelessphone.destroyuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DestroyUser extends PersonBasePage
{

    /**
     * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.DestroyUserRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setBusiInfo(IData custInfo);

    public abstract void setDestroyInfo(IData destroyInfo);

    /**
     * 认证结束之后，设置用户相关信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void setPageCustInfo(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData();

        IData inParam = new DataMap();
        inParam.put("SERIAL_NUMBER", pagedata.getString("SERIAL_NUMBER", "0"));
        inParam.put("USER_ID", pagedata.getString("USER_ID", "0"));
        inParam.put(Route.ROUTE_EPARCHY_CODE, pagedata.getString("EPARCHY_CODE", ""));
        IDataset busiInfos = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryBusiInfo", inParam);

        setBusiInfo(busiInfos.getData(0));
        // 设置公共值
        IData destroyData = new DataMap();
        destroyData.put("REMARK", "");
        destroyData.put("REMOVE_REASON", "");
        setDestroyInfo(destroyData);
    }

    public abstract void setServices(IDataset services);
}
