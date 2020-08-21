
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.widenet;

import java.util.List;
import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;

public class SynchTradeStateToAppRegAction implements ITradeAction
{
	protected static final Logger log = Logger.getLogger(SynchTradeStateToAppRegAction.class);
    /**
     *同步订单状态给APP
     * 
     * @author yuyj3
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {
        IData inParamData = new DataMap();
        
        
        String serialNumber = "";
        String wideSerialNumber = "";
        String tradeType = "";
        String productName = "";
        
        if (btd.getRD().getUca().getSerialNumber().startsWith("KD_"))
        {
        	wideSerialNumber = btd.getRD().getUca().getSerialNumber();
        	serialNumber = btd.getRD().getUca().getSerialNumber().substring(3);
        }
        else
        {
        	serialNumber = btd.getRD().getUca().getSerialNumber();
        	wideSerialNumber = "KD_" + btd.getRD().getUca().getSerialNumber();
        }
        
        
        if ("600".equals(btd.getTradeTypeCode()))
        {
        	tradeType = btd.getMainTradeData().getRsrvStr10();
        }
        else if ("4800".equals(btd.getTradeTypeCode()))
        {
        	tradeType = "7";
        }
        
        
        List<ProductTradeData> productTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        for (ProductTradeData productTradeData : productTradeDatas)
        {
            productName = UProductInfoQry.getProductNameByProductId(productTradeData.getProductId());
        }
        
        inParamData.put("TRADE_ID", btd.getTradeId());
        inParamData.put("SERIAL_NUMBER", wideSerialNumber);
        inParamData.put("WACCTNO", serialNumber);
        inParamData.put("OPER_CODE", "01");
        inParamData.put("TRADE_TYPE", tradeType);
        inParamData.put("PRODUCT_NAME", productName);
        inParamData.put("SUBORDINATE_SYSTEM", "CRM");
        inParamData.put("LINK_NAME", "1000");
        inParamData.put("STATE_OPER_TIME", SysDateMgr.getSysTime());
        inParamData.put("REQUIRE_COMPLETE_TIME", SysDateMgr.getSysTime());
        inParamData.put("STATE_CODE", "0");
        
        inParamData.put("STATE", "初始状态");
        inParamData.put("STATE_DESCRIPTION", "订单登记初始状态");
        
        inParamData.put("STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(CSBizBean.getVisit().getStaffId()));
        inParamData.put("TELPHONE", UStaffInfoQry.getStaffSnByStaffId(CSBizBean.getVisit().getStaffId()));
        inParamData.put("REMARK", btd.getTradeId());
        
        //调用服开接口同步订单状态
        try {
        	IDataOutput dataOutput = CSAppCall.callNGPf("orderLinkStateSync", inParamData);
        	log.error("==orderLinkStateSync==reg==="+dataOutput.toString());
		} catch (Exception e) {
			log.error("=SynchTradeStateToAppRegAction===="+e.toString());
			// TODO: handle exception
		}
        
    	
    }
}