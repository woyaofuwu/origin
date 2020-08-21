
package com.asiainfo.veris.crm.order.web.person.portraitComparison;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


/**
 * REQ201705270006_关于人像比对业务优化需求
 * @author zhuoyingzhi
 * @date  20170623
 *
 */
public abstract class PortraitComparison extends PersonBasePage
{


    /**
     * 页面初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setTradeTypeCode(data.getString("TRADE_TYPE_CODE"));
        //证件类型
        IDataset  ds =  pageutil.getStaticList("TD_S_PASSPORTTYPE2");
        
        setPsptTypeSource(ds);
        setCustInfo(null);
    }
    
    /**
     * 人像信息比对
     * 
     * @author dengyi
     * @param clcle
     * @throws Exception
     */
    public void cmpPicInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        IData param = new DataMap();
        
        param.putAll(data);
        param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
 
        IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.cmpPicInfo", param);
        setAjax(dataset.getData(0));
    }
    
    public abstract void setCustInfo(IData custInfo);

    public abstract void setTradeTypeCode(String tradeTypeCode);
    
    public abstract void setPsptTypeSource(IDataset psptTypeSource);
}
