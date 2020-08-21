package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class TradeBaseBeanUtil
{
	/**
     * 卡单状态为Y
     * @author guonj 
     * @param data
     * @throws Exception
     */
    public static void setWaitState(IData data,String stateCode,String stateValue,String batchId,String tradeTypeCode) throws Exception
    {
    	System.out.println("test_guonj_TradeBaseBeanUtil。star");
    	String inModecodeStr = StaticUtil.getStaticValue("NEED_PAY_CHANNELS", "0");
        //如果有待支付,待打印 TRADE,则改order状态为 X或者Y,
        if(StringUtils.indexOf(inModecodeStr,"|"+CSBizBean.getVisit().getInModeCode()+"|") != -1 && StringUtils.isBlank(batchId)){
			IDataset CommparaParas = CommparaInfoQry.getCommparaAllCol("CSM", "9330", "SETYSTATE", "0898");
			if(IDataUtil.isNotEmpty(CommparaParas))
			{
				for (int i = 0; i < CommparaParas.size(); i++) {
					IData CommparaPara = CommparaParas.getData(i);
					if ( CommparaPara.getString("PARA_CODE1", "").equals(tradeTypeCode) ) {
						data.put(stateCode, stateValue); // 订单状态
						System.out.println("test_guonj_TradeBaseBeanUtil="+stateCode+batchId+tradeTypeCode+inModecodeStr);
					}
				}
			}
        } 
    }
    
    
}
