
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;

/**
 * @ClassName: DealBookTradeAction
 * @Description: 无手机宽带开户登记成功时，更新tf_b_trade_book为正常处理。
 * @version: v1.0.0
 */
public class DealBookTradeAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String psptId = btd.getRD().getPageRequestData().getString("PSPT_ID","");
        //System.out.println("-------------DealBookTradeAction------------PSPT_ID:"+psptId);
        IDataset userBookTrade = queryUserTradeBook(psptId);
        if (IDataUtil.isNotEmpty(userBookTrade))
        {
        	updUserTradeBook(psptId);
        }
    }
	/* 
	 * 无手机宽带开户-判断是否有线上预约工单
	 * REQ201809300014新增线上无手机宽带开户功能的需求—BOSS新增界面 
	 * zhangxing3
	 */
	public IDataset queryUserTradeBook(String pstpId) throws Exception
	{
		if (null == pstpId || "".equals(pstpId))
		{
			return null;
		}
		IData iData = new DataMap();
		iData.put("PSPT_ID", pstpId);
		SQLParser dctparser = new SQLParser(iData);

		dctparser.addSQL(" select * from tf_b_trade_book a where a.pspt_id = :PSPT_ID ");
		dctparser.addSQL("    and a.book_status='0' and a.book_end_date > sysdate and a.rsrv_tag1='0' ");
		IDataset resultset = Dao.qryByParse(dctparser,Route.CONN_CRM_CEN);
		return resultset;
	}
	
    public int updUserTradeBook(String pstpId) throws Exception
    {
            IData param = new DataMap();
            param.put("PSPT_ID", pstpId);
                     
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE tf_b_trade_book a ");
            sql.append(" SET a.book_status='1' ") ;
            sql.append(" WHERE a.pspt_id = :PSPT_ID ") ;
            sql.append(" and a.book_status='0' and a.rsrv_tag1='0' ") ;
            sql.append(" and a.book_end_date > sysdate ") ;
            return Dao.executeUpdate(sql, param,Route.CONN_CRM_CEN);        
    }
}
