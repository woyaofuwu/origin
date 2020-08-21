package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetrescheduled;

import org.apache.log4j.Logger;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

/**
 * @author zhengkai
 *  宽带改期
 */
public class WideNetRescheduledBean extends CSBizBean {

	public static Logger log = Logger.getLogger(WideNetRescheduledBean.class);
	
	public IDataset queryWideNetInfo(IData input) throws Exception {
		//拼凑宽带帐号
		if (!"KD_".equals(input.getString("SERIAL_NUMBER").substring(0, 3)))
        {
            input.put("SERIAL_NUMBER", "KD_" + input.getString("SERIAL_NUMBER"));
        }
        
		SQLParser parser = new SQLParser(input);
		parser.addSQL(" select  *   ");
		parser.addSQL(" from tf_b_trade_widenet ");
		parser.addSQL(" where trade_id in (");
		parser.addSQL(" select trade_id  ");
		parser.addSQL(" from tf_b_trade ");
		parser.addSQL(" where SERIAL_NUMBER = :SERIAL_NUMBER ");
		parser.addSQL(" and trade_type_code = '600' ");
		parser.addSQL(" and cancel_tag = '0') ");
		return Dao.qryByParse(parser,Route.getJourDb());
	}

	public IDataset onTradeSubmit(IData input) throws Exception {
		
		String serial_number = input.getString("SERIAL_NUMBER");
		//拼凑宽带帐号
		if (!"KD_".equals(serial_number.substring(0, 3)))
		{
			serial_number = "KD_" + serial_number;
		}
		
		IData inParam = new DataMap();
		inParam.put("TRADE_STAFF_ID", input.getString("TRADE_STAFF_ID"));
		inParam.put("ACCEPT_DATE", input.getString("ACCEPT_DATE"));
		inParam.put("SUBSCRIBE_ID", input.getString("SUBSCRIBE_ID"));
		inParam.put("SUGGEST_DATE", input.getString("SUGGEST_DATE"));
		inParam.put("REASON_FLAG", input.getString("REASON_FLAG"));
		inParam.put("REASON", input.getString("REASON"));
		
		
    	IDataOutput dataOutput = CSAppCall.callNGPf("PBOSS_ORDER_ModifyBookingTime", inParam);
    	log.error("====================="+dataOutput.toString());
    	
    	IData head = dataOutput.getHead();//服开返回报文头
       
    	String resultCode = head.getString("X_RESULTCODE", "-1");
        
        if (!"0".equals(resultCode))
        {
            String resultInfo = head.getString("X_RESULTINFO");
            CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
        }
        
        IDataset result = dataOutput.getData();
        if (IDataUtil.isNotEmpty(result))
        {
            IData tmpData = result.getData(0);
            String xResultCode = tmpData.getString("X_RESULTTYPE", "-1");
            
            if (!"0".equals(xResultCode))
            {
            	String resultInfo = tmpData.getString("X_RESULTINFO");
            	CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
            }
        }
        
        StringBuilder sql = new StringBuilder(200);
        sql.append(" update tf_b_trade_widenet ");
        sql.append(" set SUGGEST_DATE = to_date(:SUGGEST_DATE , 'yyyy-mm-dd hh24:mi:ss') ");
        sql.append(" where trade_id = :TRADE_ID ");
        
        Dao.executeUpdate(sql, input , Route.getJourDb());
        return IDataUtil.idToIds(result.getData(0));
	}
}
