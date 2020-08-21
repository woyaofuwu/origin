
package com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge.order.requestdata.SupplierChargeReqData;

public class SupplierChargeTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        SupplierChargeReqData reqData = (SupplierChargeReqData) btd.getRD();
        btd.getMainTradeData().setCustName(reqData.getCustName());
        btd.getMainTradeData().setRemark(reqData.getRemark());
        
        updateFeeShopper(reqData);
    }
    
    public void updateFeeShopper(SupplierChargeReqData reqData) throws Exception{
    	IData param = new DataMap();
    	
    	param.put("UPDATE_STAFF", getVisit().getStaffId());
    	param.put("CHNL_ID", reqData.getChnlId());
    	param.put("FACTORY_CODE", reqData.getFactoryCode());
    	param.put("YEAR", reqData.getYear());
    	param.put("ACCEPT_MONTH", reqData.getAcceptMonth());
    	param.put("RSRV_STR3", reqData.getsetRsrvStr3());
    	param.put("RSRV_STR2", reqData.getTradeId());
    	
    	Dao.executeUpdateByCodeCode("TD_M_RES_CORP", "UPD_FEE_SHIPPER", param);
    }

}
