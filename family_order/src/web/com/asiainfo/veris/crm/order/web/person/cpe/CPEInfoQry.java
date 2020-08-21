/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.cpe;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * CPE号码解锁
 */
public abstract class CPEInfoQry extends PersonBasePage
{

    public void cpeInfoQry(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData(); 
        IDataset results = CSViewCall.call(this, "SS.CPEActiveSVC.cpeInfoQry", pagedata); 
        IData rtnData=new DataMap();
        if(results!=null && results.size()>0){
        	rtnData=results.getData(0);
        	//rtnData.put("RTN_CODE", "0"); 
        }else{
        	rtnData.put("RTN_CODE", "9");
        	rtnData.put("RTN_MSG", "没有找到用户的CPE信息。");
        }
        this.setAjax(rtnData);

    } 
     
}
