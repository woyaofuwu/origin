package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class THTradeSmsBoomInfoDAO
{
    /**
     * 短信炸弹受理信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryTradeSmsBoomInfo(IData param) throws Exception
    {
        String trade_id = param.getString("TRADE_ID", "");
        if ("".equals(trade_id))
        {
            return new DatasetList();
        }
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" Select A.RECV_ID,A.ACCESS_NO,A.SERIAL_NO,A.ACCEPT_TIME,A.EXPIRE_DATE,A.MODIFY_TAG,A.STATUS,A.CHANNEL_ID,A.CREATE_STAFF_ID ");
        parser.addSQL(" From TF_B_TRADE_SMS_PROTEGE A Where 1=1 ");
        parser.addSQL(" And A.trade_id=:TRADE_ID ");
        parser.addSQL(" And A.ACCEPT_MONTH = :ACCEPT_MONTH ");
        return Dao.qryByParse(parser, Route.getJourDb());
    }
    
    /**
     * 短信炸弹受理信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryTradeSmsBoomInfoNew(IData param) throws Exception
    {
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" Select A.RECV_ID,A.ACCESS_NO,A.SERIAL_NO,A.ACCEPT_TIME,A.EXPIRE_DATE,A.MODIFY_TAG,A.STATUS,A.CHANNEL_ID,A.CREATE_STAFF_ID ");
        parser.addSQL(" From TF_B_TRADE_SMS_PROTEGE A Where 1=1 ");
        if(!"".equals(param.getString("ACCESS_NO" ,""))){
        	parser.addSQL(" And A.ACCESS_NO=:ACCESS_NO ");
        }
        if(!"".equals(param.getString("SERIAL_NO", ""))){
        	parser.addSQL(" And A.SERIAL_NO = :SERIAL_NO ");
        }       
        return Dao.qryByParse(parser, Route.getJourDb());
    }
    
    public IDataset qrySMSBombStatHisInf(IData param,Pagination pagination) throws Exception
    {        
    	param.put("ACCESS_NO", param.get("userMobile"));
    	param.put("START_TIME", param.get("startTime"));
    	param.put("END_TIME", param.get("endTime"));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" Select A.RECV_ID,A.ACCESS_NO,A.SERIAL_NO,A.ACCEPT_TIME,A.EXPIRE_DATE,A.MODIFY_TAG,A.STATUS,A.CHANNEL_ID,A.CREATE_STAFF_ID,'' REMARK ");
        parser.addSQL(" From TF_B_TRADE_SMS_PROTEGE A Where 1=1 And A.ACCESS_NO = :ACCESS_NO");
        if(!"".equals(param.getString("startTime" ,"")) && !"".equals(param.getString("endTime" ,""))){
        	parser.addSQL(" And TO_DATE(A.ACCEPT_TIME,'YYYYMMDDHH24MISS') BETWEEN TO_DATE(:START_TIME,'YYYYMMDDHH24MISS') AND TO_DATE(:END_TIME,'YYYYMMDDHH24MISS') ");
        }
        parser.addSQL(" ORDER BY A.ACCEPT_TIME DESC ");
        return Dao.qryByParse(parser, pagination , Route.getJourDb());
    }

}
