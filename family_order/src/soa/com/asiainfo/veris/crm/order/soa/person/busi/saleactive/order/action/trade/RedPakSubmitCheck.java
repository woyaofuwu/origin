package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class RedPakSubmitCheck  implements ITradeAction {
	
	@SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SaleActiveReqData req = (SaleActiveReqData) btd.getRD();
        String tradeTypeCode =  btd.getMainTradeData().getTradeTypeCode();
        String productId = btd.getMainTradeData().getRsrvStr1();
        String packageId = btd.getMainTradeData().getRsrvStr2();
        String userId=btd.getMainTradeData().getUserId();
        
        IDataset comms = CommparaInfoQry.getCommparaInfoByCode("CSM", "6895", productId, packageId, "0898");
        if("240".equals(tradeTypeCode)&&IDataUtil.isNotEmpty(comms))
    	{
        	IData params=new DataMap();
        	params.put("USER_ID", userId);
        	params.put("PACKAGE_ID", packageId);
        	IDataset userOther=Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_RED_PAK_VALUE2", params);
        	if(IDataUtil.isEmpty(userOther)){
        		//如果调接口失败，也报错
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "办理失败！失败信息：不存在用户的使用红包记录，不允许办理！");
        	}
    	}
    }

}
