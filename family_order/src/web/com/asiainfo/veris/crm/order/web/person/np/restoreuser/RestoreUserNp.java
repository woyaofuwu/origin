
package com.asiainfo.veris.crm.order.web.person.np.restoreuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RestoreUserNp extends PersonBasePage
{

    /**
     * @Function: checkResource
     * @Description: 如果换sim卡那么要校验
     * @param:参数描述
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014-4-23 下午3:00:28 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-23 lijm3 v1.0.0 修改原因
     */
    public void checkResource(IRequestCycle cycle) throws Exception
    {
        IData input = getData();
        IDataset ids = CSViewCall.call(this, "SS.RestoreUserNpSVC.checkResource", input);
        setAjax(ids);

    }

    /**
     * 获取产品费用
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void getProductFeeInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.RestoreUserNpSVC.getProductFeeInfo", data);
        setAjax(dataset);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        IDataset result = CSViewCall.call(this, "SS.RestoreUserNpTradeRegSVC.tradeReg", data);
        setAjax(result);
    }

    public void queryDestroyedUserInfo(IRequestCycle cycle) throws Exception
    {

        IData input = getData();

        IData custInfo = new DataMap(input.getString("CUST_INFO"));

        IData userInfo = new DataMap(input.getString("USER_INFO"));

        IData acctInfo = new DataMap(input.getString("ACCT_INFO"));

        setCustInfo(custInfo);

        setUserInfo(userInfo);

        setAcctInfo(acctInfo);

        IDataset ids = CSViewCall.call(this, "SS.RestoreUserNpSVC.getUserNpInfos", userInfo);
        if (IDataUtil.isNotEmpty(ids))
        {
            IData ajaxData = new DataMap();
            setUserNpInfo(ids.getData(0).getData("info"));
            ajaxData.put("info", ids.getData(0).getData("info"));
            ajaxData.put("resparam", ids.getData(0).getData("resparam"));
            setAjax(ajaxData);
            setProductTypeList(ids.getData(0).getDataset("productTypeList"));
            setResInfos(ids.getData(0).getDataset("resInfos"));
        }

    }

    public abstract void setAcctInfo(IData info);

    public abstract void setCustInfo(IData info);

    public abstract void setProductTypeList(IDataset infos);

    public abstract void setResInfos(IDataset infos);

    public abstract void setUserInfo(IData info);

    public abstract void setUserNpInfo(IData info);
}
