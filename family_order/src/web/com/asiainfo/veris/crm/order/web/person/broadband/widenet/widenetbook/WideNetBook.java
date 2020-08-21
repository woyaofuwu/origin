
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.widenetbook;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 
 * @author zyz
 * @version 1.0
 *
 */
public abstract class WideNetBook extends PersonBasePage
{ 

	/**
	 *  宽带预约登记
	 * @param cycle
	 * @throws Exception
	 */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("SERIAL_NUMBER", data.getString("serial_number"));
        //调用台帐
        IDataset dataset = CSViewCall.call(this, "SS.WideNetBookRegSVC.tradeReg", data);
        
        setAjax(dataset);
    } 
    
    /**
     * 校验，验证码使用次和验证码是否存在
     * @param cycle
     * @throws Exception
     */
    public void checkValidCodeNum(IRequestCycle cycle) throws Exception
    {
        IData pagedata = getData(); 
        
        IData result = CSViewCall.call(this, "SS.WideNetBookSVC.checkValidCodeIsExist", pagedata).first(); 
        String stauts=result.getString("stauts");
        if("0".equals(stauts)){
        	//验证码存在
        	IData checkValidCodeNumInfo = CSViewCall.call(this, "SS.WideNetBookSVC.checkValidCodeNum", pagedata).first();
        	result.put("stauts", checkValidCodeNumInfo.getString("stauts"));
        	result.put("msg", checkValidCodeNumInfo.getString("msg"));
        }
        this.setAjax(result); 
    }
    
}
