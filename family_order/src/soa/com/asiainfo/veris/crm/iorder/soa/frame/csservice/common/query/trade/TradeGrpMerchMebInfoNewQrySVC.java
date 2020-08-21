package com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.query.trade;



import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TradeGrpMerchMebInfoNewQrySVC extends CSBizService
{


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IDataset qryBBossMerchpMeb(IData param) throws Exception
    {

        return TradeGrpMerchMebInfoNewQry.qryBBossMerchpMeb(param.getString("EC_USER_ID", ""), param.getString("SERIAL_NUMBER", ""), param.getString("USER_ID", ""), null);
    }
    
}
