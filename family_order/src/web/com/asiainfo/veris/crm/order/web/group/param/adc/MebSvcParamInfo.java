
package com.asiainfo.veris.crm.order.web.group.param.adc;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.group.param.GroupParamPage;

public abstract class MebSvcParamInfo extends GroupParamPage
{

    /**
     * 作用： 如果页面没有初始化服务参数 通过ajax来查询
     * 
     * @author liaolc 2014-03-18
     * @exception Exception
     */
    public void getServiceParamsByajax(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData serviceInparam = new DataMap();
        serviceInparam.put("USER_ID", data.getString("USER_ID", ""));
        serviceInparam.put("USER_ID_A", data.getString("USER_ID_A", ""));
        serviceInparam.put("SERVICE_ID", data.getString("SERVICE_ID", ""));
        serviceInparam.put("GROUP_ID", data.getString("GROUP_ID", ""));
        serviceInparam.put(Route.ROUTE_EPARCHY_CODE, data.getString("MEB_EPARCHY_CODE", ""));// 服务根据成员EPARCHY_CODE路由
        IDataset serviceparamset = CSViewCall.call(this, "SS.AdcMebParamsSvc.getServiceParam", serviceInparam);
        setAjax(serviceparamset);
    }

    /**
     * http://localhost:8080/groupserv/?service=page/group.param.adc.MebSvcParamInfo&listener=initSvcParamsInfo
     * &ELEMENT_ID=903400&ELEMENT_TYPE_CODE=S&PRODUCT_ID=903401&PACKAGE_ID=90340111&ITEM_INDEX=0&MEB_EPARCHY_CODE=0731
     * 作用： 通过URL传入的参数 初始服务参数界面
     * 
     * @author lioalc 2013-05-18
     * @exception Exception
     */
    public void initSvcParamsInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String itemIndex = data.getString("ITEM_INDEX");
        this.setItemIndex(itemIndex);
        String mebEparchyCode = data.getString("MEB_EPARCHY_CODE");
        this.setMebEparchyCode(mebEparchyCode);
        String serviceId = data.getString("ELEMENT_ID");
        this.setServiceId(serviceId);
        this.setCancleFlag("");

    }

    public abstract void setCancleFlag(String cancleFlag);

    public abstract void setInfo(IData info);

    public abstract void setItemIndex(String itemIndex);

    public abstract void setMebEparchyCode(String mebEparchyCode);

    public abstract void setParamVerifySucc(String paramVerifySucc);

    public abstract void setPlatsvcparam(IData info);

    public abstract void setServiceId(String serviceId);

    public abstract void setSvcotherlists(IDataset svcotherlists);

}
