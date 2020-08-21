
package com.asiainfo.veris.crm.order.soa.group.bindbroadbandmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class BindBroadBandForDeskMemQry
{
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryBroadBandInfoByNumber(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.SERIAL_NUMBER FTTH_ACOUNT,");
        parser.addSQL("		   W.STAND_ADDRESS,");
        parser.addSQL("		   W.CONTACT,");
        parser.addSQL("		   W.CONTACT_PHONE,");
        parser.addSQL("		   W.PHONE, ");
        parser.addSQL("		   W.RSRV_STR2, ");
        parser.addSQL("		   T.USER_ID ");
        parser.addSQL("	  FROM TF_F_USER T, TF_F_USER_WIDENET W");
        parser.addSQL("	 WHERE T.SERIAL_NUMBER = :SERIAL_NUMBER");
        parser.addSQL("	  AND T.REMOVE_TAG = '0'");
        parser.addSQL("	  AND T.PARTITION_ID = W.PARTITION_ID");
        parser.addSQL("	  AND T.USER_ID = W.USER_ID");
        parser.addSQL("	  AND W.END_DATE > SYSDATE");
        return Dao.qryByParse(parser);
    }
	
	/**
	 * 校验未完工的工单
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static IDataset checkTradeBroadBand(IData param) throws Exception
	{
		String serialNumber = param.getString("SERIAL_NUMBER");
		String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
		return TradeInfoQry.getMainTradeBySN(serialNumber, tradeTypeCode);
	}
	
}
