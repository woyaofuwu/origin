package com.asiainfo.veris.crm.order.web.person.oceanfront;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;


/**
 * REQ201805160025_2018年海洋通业务办理开发需求
 * <br/>
 * 海洋通，服务状态变更
 * @author zhuoyingzhi
 * @date 20180601
 *
 */
public abstract class OceanFrontOpenStopTelephone extends PersonBasePage
{

	/**
	 * 界面初始化
	 * @param cycle
	 * @throws Exception
	 */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData pgData = this.getData();
        String tradeTypeCode = pgData.getString("TRADE_TYPE_CODE","9830");//默认是报停
        String authType = pgData.getString("authType", "00");
        IData info = new DataMap();
        info.put("TRADE_TYPE_CODE", tradeTypeCode);
        info.put("authType", authType);
        this.setInfo(info);
    }
       
    
    /**
     * @param requestCycle
     * @description:服务状态变更业务提交类
     * @author: zhuoyingzhi
     * @date 20180601
     */
    public void onTradeSubmit(IRequestCycle requestCycle) throws Exception
    {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
        {
        	data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        String openStopType=data.getString("OPEN_STOP_TYPE", "");
        if("0".equals(openStopType)){
        	//0标识报停
        	data.put("TRADE_TYPE_CODE", "9830");
        }else if("1".equals(openStopType)){
        	//1标识报开
        	data.put("TRADE_TYPE_CODE", "9831");
        }
        
        IDataset dataset = CSViewCall.call(this, "SS.OceanFrontRegSVC.tradeReg", data);
        setAjax(dataset);
    }
   
    public abstract void setInfo(IData info);
}
