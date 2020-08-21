package com.asiainfo.veris.crm.order.soa.person.common.action.finish.widenet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 同步订单状态给APP
 * @author yuyj3
 */
public class SynchTradeStateToAppFinishAction implements ITradeFinishAction
{
    protected static final Logger log = Logger.getLogger(SynchTradeStateToAppFinishAction.class);

    @Override
    public void executeAction(IData mainTrade) throws Exception
    { 
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        
        
        IData inParamData = new DataMap();
        
        
        String serialNumber = "";
        String wideSerialNumber = "";
        String tradeType = "";
        String productName = "";
        
        if (mainTrade.getString("SERIAL_NUMBER").startsWith("KD_"))
        {
        	wideSerialNumber = mainTrade.getString("SERIAL_NUMBER");
        	serialNumber = mainTrade.getString("SERIAL_NUMBER").substring(3);
        }
        else
        {
        	serialNumber = mainTrade.getString("SERIAL_NUMBER");
        	wideSerialNumber = "KD_" + mainTrade.getString("SERIAL_NUMBER");
        }
        
        
        if ("600".equals(tradeTypeCode))
        {
        	tradeType = mainTrade.getString("RSRV_STR10");
        }
        else if ("4800".equals(tradeTypeCode))
        {
        	tradeType = "7";
        }
        
        if (StringUtils.isNotEmpty(mainTrade.getString("PRODUCT_ID")))
        {
        	productName = UProductInfoQry.getProductNameByProductId(mainTrade.getString("PRODUCT_ID"));
        }
        
        inParamData.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
        inParamData.put("SERIAL_NUMBER", wideSerialNumber);
        inParamData.put("WACCTNO", serialNumber);
        inParamData.put("OPER_CODE", "01");
        inParamData.put("TRADE_TYPE", tradeType);
        inParamData.put("PRODUCT_NAME", productName);
        inParamData.put("SUBORDINATE_SYSTEM", "CRM");
        inParamData.put("LINK_NAME", "1010");  //详细：环节状态对应关系文档
        inParamData.put("STATE_OPER_TIME", SysDateMgr.getSysTime());
        inParamData.put("REQUIRE_COMPLETE_TIME", SysDateMgr.getSysTime());
        inParamData.put("STATE_CODE", "9");
        
        inParamData.put("STATE", "完工归档");
        inParamData.put("STATE_DESCRIPTION", "订单完工归档");
        
        inParamData.put("STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(CSBizBean.getVisit().getStaffId()));
        inParamData.put("TELPHONE", UStaffInfoQry.getStaffSnByStaffId(CSBizBean.getVisit().getStaffId()));
        inParamData.put("REMARK", "");
        
        //调用服开接口同步订单状态
        try {
        	 IDataOutput dataOutput = CSAppCall.callNGPf("orderLinkStateSync", inParamData);
        	 log.error("==orderLinkStateSync=====finishi===="+dataOutput.toString());
		} catch (Exception e) {
			 log.error("==SynchTradeStateToAppFinishAction==="+e.toString());
		}
    }
}
