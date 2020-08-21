
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.ftthmodermapply;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FTTHModermApply extends PersonBasePage
{ 
    /**
     * FTTH光猫申领
     * @param clcle
     * @throws Exception
     * @author chenxy3
     */ 
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        String routeId = data.getString("EPARCHY_CODE");
        // 客服工号，HAIN, 则默认到0898
        if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
        {
            data.put("EPARCHY_CODE", "0898");
        }
        data.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        data.put("DEPOSIT", data.getInt("DEPOSIT",0)*100);
        IDataset dataset = CSViewCall.call(this, "SS.FTTHModermApplyRegSVC.tradeReg", data);
        setAjax(dataset);
    } 
    
    /**
     * REQ201510270009 FTTH光猫申领押金金额显示优化【2015业务挑刺】
     * 判断用户该收取的押金金额
     * chenxy3 20151029
     * */
    public void checkFTTHdeposit(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData(); 
        IData results = CSViewCall.call(this, "SS.FTTHModermApplySVC.checkFTTHdeposit", pagedata).first(); 
        this.setAjax(results); 
    }  
}
