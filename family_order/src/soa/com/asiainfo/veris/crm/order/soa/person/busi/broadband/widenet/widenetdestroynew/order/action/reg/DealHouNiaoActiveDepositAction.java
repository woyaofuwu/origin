
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

/**
 * 候鸟用户
 * 
 */
public class DealHouNiaoActiveDepositAction implements ITradeAction
{
	
	public void executeAction(BusiTradeData btd) throws Exception 
    {
		String tradeTypeCode = btd.getTradeTypeCode();

		String serialNumberWD = btd.getMainTradeData().getSerialNumber(); //宽带号码
		String userIdWD = btd.getMainTradeData().getUserId(); //宽带USER_ID
		String serialNumber = "";//手机号码
		String seriUserId = "";//手机USER_ID
        

		//宽带拆机/宽带特殊拆机
    	if ("605".equals(tradeTypeCode) || "615".equals(tradeTypeCode))
		{
			if(serialNumberWD.length() > 14)
			{
				return;
			}
	        if(serialNumberWD.startsWith("KD_"))
	        {
	        	serialNumber = serialNumberWD.substring(3);
	        }
	        IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
	        if(IDataUtil.isNotEmpty(userInfos)){
	        	seriUserId = userInfos.getData(0).getString("USER_ID","");
	        }
	        if(!"".equals(seriUserId))
	        {	//1.查询候鸟活动
		    	IDataset saleActives = UserSaleActiveInfoQry.getHouNiaoSaleActiveByUserId(seriUserId);
		    	if (IDataUtil.isNotEmpty(saleActives)){
		    		updUserSaleActive(seriUserId,"66002202");
		    		updUserSaleActiveHis(seriUserId,"66002202");
		    	}
	        }
		}
    	
    	// 2.查询租房套餐
    	IDataset userDiscnts = UserDiscntInfoQry.getAllDiscntByTag3(userIdWD,"84018442");
    	if (IDataUtil.isNotEmpty(userDiscnts)){
    		updUserDiscnt(userIdWD,"84018442");
    	}
		
    }
	
    public int updUserDiscnt(String userId ,String discntCode) throws Exception
    {
            IData param = new DataMap();
            param.put("USER_ID", userId);
            param.put("DISCNT_CODE", discntCode);
                     
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE TF_F_USER_DISCNT A ");
            sql.append(" SET A.END_DATE = sysdate,A.REMARK = '度假宽带承诺在网套餐:已收取装机费',A.RSRV_TAG3= '1' ") ;
            sql.append(" WHERE A.DISCNT_CODE IN (:DISCNT_CODE) ") ;
            sql.append(" AND A.USER_ID = :USER_ID ") ;
            sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
            sql.append(" AND A.END_DATE > A.START_DATE AND A.START_DATE < SYSDATE AND A.END_DATE > SYSDATE ") ;
            return Dao.executeUpdate(sql, param);        
    }
    
    public int updUserSaleActive(String userId ,String productId) throws Exception
    {
            IData param = new DataMap();
            param.put("USER_ID", userId);
            param.put("PRODUCT_ID", productId);
                     
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE TF_F_USER_SALE_ACTIVE A ");
            sql.append(" SET A.REMARK = '度假宽带:已收取装机费',A.RSRV_TAG3= '1' ") ;
            sql.append(" WHERE A.PRODUCT_ID = :PRODUCT_ID ") ;
            sql.append(" AND A.USER_ID = :USER_ID ") ;
            sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
            return Dao.executeUpdate(sql, param);        
    }
    public int updUserSaleActiveHis(String userId ,String productId) throws Exception
    {
            IData param = new DataMap();
            param.put("USER_ID", userId);
            param.put("PRODUCT_ID", productId);
                     
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE TF_FH_USER_SALE_ACTIVE A ");
            sql.append(" SET A.REMARK = '度假宽带:已收取装机费',A.RSRV_TAG3= '1' ") ;
            sql.append(" WHERE A.PRODUCT_ID = :PRODUCT_ID ") ;
            sql.append(" AND A.USER_ID = :USER_ID ") ;
            sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
            return Dao.executeUpdate(sql, param);        
    }
    
    public String getUserIdBySN(String orderId) throws Exception
    {
		String userId = "";
    	StringBuilder sql = new StringBuilder(1000);
		sql.append("select t.user_id from ucr_crm1.tf_bh_trade t ");
		sql.append(" where t.order_id = :ORDER_ID and t.trade_type_code = '10' ");
		sql.append("union select t.user_id from ucr_crm1.tf_b_trade t ");
		sql.append("  where t.order_id = :ORDER_ID and t.trade_type_code = '10' ");
		IData param = new DataMap();
		param.put("ORDER_ID", orderId);
		IDataset ids1 = Dao.qryBySql(sql, param, Route.getJourDb());
		if(IDataUtil.isNotEmpty(ids1))
		{
			userId = ids1.getData(0).getString("USER_ID", "");
		}
		return userId ;
    }
    
    public IDataset getRelationByUIDB(String userIdB) throws Exception
    {
    	StringBuilder sql = new StringBuilder(1000);
		sql.append(" select t.* from tf_f_relation_uu t  ");
		sql.append(" where t.user_id_b=:USER_ID_B ");
		sql.append(" and t.relation_type_code='47' ");
		sql.append(" and sysdate between t.start_date and t.end_date ");
		IData param = new DataMap();
		param.put("USER_ID_B", userIdB);
		return Dao.qryBySql(sql, param);

    }
}
