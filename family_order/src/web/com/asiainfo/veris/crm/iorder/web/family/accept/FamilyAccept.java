
package com.asiainfo.veris.crm.iorder.web.family.accept;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * @Description 家庭创建
 * @Auther: zhenggang
 * @Date: 2020/7/23 21:03
 * @version: V1.0
 */
public abstract class FamilyAccept extends PersonBasePage
{
    public abstract void setInfo(IData info);

    public abstract void setCustInfo(IData custInfo);

    /**
     * @Description: 初始化
     * @Param: [cycle]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/30 10:22
     */
    public void initFamily(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.FamilyAcceptSVC.initFamily", data);
        setCustInfo(result.getData("CUST_DATA"));
        result.remove("CUST_DATA");
        setAjax(result);
    }

    /**
     * @Description: 提交受理
     * @Param: [cycle]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/30 10:23
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset result = CSViewCall.call(this, "SS.FamilyAcceptSVC.tradeReg", data);
        setAjax(result);
    }
}
