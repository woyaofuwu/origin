package com.asiainfo.veris.crm.order.web.person.broadband.widenet.wideprereg;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WidePreReg extends PersonBasePage{
	
	 /**
     * @Function:
     * @Description: 页面初始化方法
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-3-27 下午02:57:59 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-27 chengxf2 v1.0.0 修改原因
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
    	IData request = this.getData();
    	request.put("SERIAL_NUMBER", request.getString("AUTH_SERIAL_NUMBER",""));
    	setInfo(request);
    }
	
	
    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-21 下午04:39:39 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-21 chengxf2 v1.0.0 修改原因
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData request = this.getData();
        request.put("TRADE_TYPE_CODE", "702");
        if (StringUtils.isEmpty(request.getString("SERIAL_NUMBER", "")))
        {
        	request.put("SERIAL_NUMBER", request.getString("AUTH_SERIAL_NUMBER"));
        }
        
        StringBuilder home_addr = new StringBuilder(1000);
        home_addr.append(request.getString("ADDR_NAME1",""));
        home_addr.append(request.getString("ADDR_NAME2",""));
        home_addr.append(request.getString("ADDR_NAME3",""));
        home_addr.append(request.getString("ADDR_NAME4",""));
        request.put("HOME_ADDR", home_addr.toString());
        request.put("SET_ADDR", request.getString("ADDR_NAME"));
        StringBuilder addr_code = new StringBuilder(1000);
        addr_code.append(request.getString("ADDR_ID1",""));
        addr_code.append(",");
        addr_code.append(request.getString("ADDR_ID2",""));
        addr_code.append(",");
        addr_code.append(request.getString("ADDR_ID3",""));
        addr_code.append(",");
        addr_code.append(request.getString("ADDR_ID4",""));
        request.put("ADDR_CODE", addr_code.toString());
        request.put("AREA_CODE", request.getString("ADDR_ID1",""));
        IDataset output = CSViewCall.call(this, "SS.WidePreRegRegSVC.tradeReg", request);
        this.setAjax(output);
    }

    public abstract void setInfo(IData Info);
}
