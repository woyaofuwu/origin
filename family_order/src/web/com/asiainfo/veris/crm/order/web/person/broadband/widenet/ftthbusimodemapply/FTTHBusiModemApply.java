
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.ftthbusimodemapply;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage; 

public abstract class FTTHBusiModemApply extends PersonBasePage
{ 
    /**
     * FTTH商务宽带光猫申领
     * @param clcle
     * @throws Exception
     * @author chenxy3
     */ 
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = new DatasetList(data.getString("FTTH_DATASET"));
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode()); 
        IDataset tradeSet = CSViewCall.call(this, "SS.FTTHBusiModemApplyRegSVC.tradeReg", data);
        setAjax(tradeSet);
    } 
    
    /**
     * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
     * 判断用户规则
     * chenxy3 20151208
     * */
    public void checkFTTHBusi(IRequestCycle cycle) throws Exception
    { 
        IData pagedata = getData(); 
        IData checks=new DataMap();
        IDataset results = CSViewCall.call(this, "SS.FTTHBusiModemApplySVC.checkFTTHBusi", pagedata); 
        if(results==null || results.size()==0){
        	checks.put("RTNCODE", "9");
        	checks.put("RTNMSG", "该用户不存在7341 集团商务宽带产品，不允许办理该业务！"); 
        }
        this.setAjax(checks); 
    }  
    
    /**
     * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
     * 查询录入的宽带号码是否满足条件
     * chenxy3 20151208
     * */
    public void checkKDNumber(IRequestCycle cycle) throws Exception
    { 
        IData pagedata = getData(); 
        IData checks=new DataMap();
        IData results = CSViewCall.call(this, "SS.FTTHBusiModemApplySVC.checkKDNumber", pagedata).first();
       
        this.setAjax(results); 
    }  
    
    
}
