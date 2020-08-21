/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.cpe;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * CPE号码绑定
 */
public abstract class CPEActive extends PersonBasePage
{

    public void checkBySerialNumber(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();

        pagedata.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.CPEActiveSVC.checkBySerialNumber", pagedata);

        this.setAjax(results.getData(0));

    }

    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData();
        String sn=pagedata.getString("SERIAL_NUMBER");
        pagedata.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());

        IDataset results = CSViewCall.call(this, "SS.CPEActiveSVC.loadChildInfo", pagedata); 
        //this.setInfos(results); 
        this.setCommInfo(results.getData(0).getData("FAM_PARA"));

        this.setInfos(results.getData(0).getDataset("QRY_MEMBER_LIST"));
    }
    
    public void checkCPENumber(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData(); 
        pagedata.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset results = CSViewCall.call(this, "SS.CPEActiveSVC.checkCEPNumber", pagedata); 
         
        this.setAjax(results.getData(0));
    }
 

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode()); 
        IDataset dataset = CSViewCall.call(this, "SS.CPEActiveRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public abstract void setCommInfo(IData info);// 

    public abstract void setInfos(IDataset infos);// 

}
