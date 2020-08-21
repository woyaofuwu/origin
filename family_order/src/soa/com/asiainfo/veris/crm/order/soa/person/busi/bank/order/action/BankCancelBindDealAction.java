
package com.asiainfo.veris.crm.order.soa.person.busi.bank.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.bank.BankBean;

public class BankCancelBindDealAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {

    	String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String tradeTypeCode = btd.getTradeTypeCode();
        String eparchyCode = btd.getRD().getUca().getUser().getEparchyCode();
        
    	IData inparam = new DataMap();
    	inparam.put("USER_ID", userId);
    	
    	IDataset userOtherset = BankBean.qryBankBindInfo2(inparam);
    	if(IDataUtil.isNotEmpty(userOtherset)){
    		for(int i =0; i < userOtherset.size(); i++){
    			
    	        IData params = new DataMap();
    	        params.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
    	        params.put("USER_ID", userId);
    	        params.put("SERIAL_NUMBER", serialNumber);
    	        params.put("TRADE_TYPE_CODE", "512");
    	        params.put("ACCEPT_TIME", btd.getRD().getAcceptTime());
    	        params.put("RSRV_STR1", userOtherset.getData(i).getString("RSRV_STR1"));
    	        IDataset bankResults = CSAppCall.call("SS.BankIntfConnectSVC.bankCancelBind", params);
    	        
    			OtherTradeData otherTD = new OtherTradeData(userOtherset.getData(i));
    	        
    	        otherTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
    	        //otherTD.setStartDate(btd.getRD().getAcceptTime());
    	        otherTD.setEndDate(btd.getRD().getAcceptTime());
    	        String remark;
    	        if("192".equals(tradeTypeCode)){
    	        	remark = "立即销户银联解绑";
    	        }else if("7240".equals(tradeTypeCode)){
    	        	remark = "欠费销户银联解绑";
    	        }else if("143".equals(tradeTypeCode)){
    	        	remark = "改号银联解绑";
    	        }else if("100".equals(tradeTypeCode)){
    	        	remark = "过户银联解绑";
    	        }else{
    	        	remark = "其他银联解绑";
    	        }
    	        otherTD.setRemark(remark);

    	        btd.add(serialNumber, otherTD);
    		}
    	}
    }
}
