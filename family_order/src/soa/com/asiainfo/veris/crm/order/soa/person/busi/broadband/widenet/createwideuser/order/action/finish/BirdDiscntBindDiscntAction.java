
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class BirdDiscntBindDiscntAction implements ITradeFinishAction
{

	public void executeAction(IData mainTrade) throws Exception
    {
		String tradeId = mainTrade.getString("TRADE_ID","");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE","");
		String serialNumberWD = mainTrade.getString("SERIAL_NUMBER",""); //宽带号码
		String userIdWD = mainTrade.getString("USER_ID",""); //宽带USER_ID
		String serialNumber = "";//手机号码
		String userId = "";//手机USER_ID
        
		if  ("680".equals(tradeTypeCode) )
		{
			if (!"".equals(mainTrade.getString("RSRV_STR6","")))
			{
				serialNumber = mainTrade.getString("RSRV_STR6","");
				String orderId = mainTrade.getString("ORDER_ID","");
				userId = getUserIdBySN(orderId);
			}
		}

		else if  ( "681".equals(tradeTypeCode) || "682".equals(tradeTypeCode))
		{
			IDataset ids = getRelationByUIDB(userIdWD);
			if(IDataUtil.isNotEmpty(ids)){
				serialNumber = ids.getData(0).getString("SERIAL_NUMBER_A", "");
				userId = ids.getData(0).getString("USER_ID_A", "");
			}
		}
		//System.out.println("===========BirdDiscntBindDiscntAction===========serialNumber:"+serialNumber);
		//System.out.println("===========BirdDiscntBindDiscntAction===========userId:"+userId);

        
        IDataset discntTradeInfos = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
        //System.out.println("===========BirdDiscntBindDiscntAction=========discntTradeInfos:"+discntTradeInfos);

        if(( "680".equals(tradeTypeCode) || "681".equals(tradeTypeCode) || "682".equals(tradeTypeCode)) 
        		&& IDataUtil.isNotEmpty(discntTradeInfos) && !"".equals(userId) )
        {
	        for (int i = 0; i < discntTradeInfos.size(); i++)
	        {
	            String modifyTag = discntTradeInfos.getData(i).getString("MODIFY_TAG", "");
	            String discntCode = discntTradeInfos.getData(i).getString("DISCNT_CODE", "");
	            String startDate = discntTradeInfos.getData(i).getString("START_DATE", "");
	            String endDate = discntTradeInfos.getData(i).getString("END_DATE", "");
	
	            //新增 度假宽带季度套餐（无手机）、度假宽带半年套餐（无手机）时，给手机用户绑定：度假宽带减免魔百和功能费套餐（无手机）
	            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) && ("84071448".equals(discntCode) || "84071449".equals(discntCode)
	            		|| "84074442".equals(discntCode)))
	            {
	                    String iv_sync_sequence1 = SeqMgr.getSyncIncreId();
	                    //ADD BY zhangxing3解决无手机魔百和立即扣费故障问题
	                    String newStartDate = SysDateMgr.getFirstDayOfThisMonth();
				        int w = insUserDiscnt(userId,"84071643",newStartDate, endDate,iv_sync_sequence1);
	                    //ADD BY zhangxing3解决无手机魔百和立即扣费故障问题
				        //System.out.println("===========BirdDiscntBindDiscntAction===========w:"+w);
				        if (w > 0){
					        insTibDiscnt(iv_sync_sequence1, userId);
					        insTiSync(iv_sync_sequence1);
				        }                                      
	
	             }
	             //删除 度假宽带季度套餐（无手机）、度假宽带半年套餐（无手机）时，删除：度假宽带减免魔百和功能费套餐（无手机)
	             else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag) && ("84071448".equals(discntCode) || "84071449".equals(discntCode)
	            		 || "84074442".equals(discntCode)))
	             {
	            	//是否用户已经订购了该优惠
	             	String delDiscnts="84071643";
	             	UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
	                 List<DiscntTradeData> userDiscnts = ucaData.getUserDiscntsByDiscntCodeArray(delDiscnts);
	                 if (userDiscnts.size() == 0)
	                 {
	                     continue;
	                 }
	                 String iv_sync_sequence = SeqMgr.getSyncIncreId();
	                 int m = updUserDiscnt(userId,delDiscnts,endDate,iv_sync_sequence);
				     //System.out.println("===========BirdDiscntBindDiscntAction===========m:"+m);
	                 if ( m > 0 ){
					        insTibDiscnt(iv_sync_sequence, userId);
					        insTiSync(iv_sync_sequence);
				     }
	             }
	        }
        }
        if( "4900".equals(tradeTypeCode))
        {
            IDataset otherTradeInfos = TradeOtherInfoQry.getTradeOtherByTradeId(tradeId,"TOPSETBOX",userIdWD);
	        //System.out.println("===========BirdDiscntBindDiscntAction=========otherTradeInfos:"+otherTradeInfos);

            if (IDataUtil.isNotEmpty(otherTradeInfos))
            {
            	userId = otherTradeInfos.getData(0).getString("RSRV_STR2", "");
            }
            String startDate = "";String endDate = "";
            String duDiscnts="84071448,84071449,84074442";
         	UcaData ucaData = UcaDataFactory.getNormalUca(serialNumberWD);
            List<DiscntTradeData> userDiscnts = ucaData.getUserDiscntsByDiscntCodeArray(duDiscnts);
            
            if ( userDiscnts.size() > 0 )
            {
            	 startDate = userDiscnts.get(0).getStartDate();
 	             endDate = userDiscnts.get(0).getEndDate();
 	        }
	        //System.out.println("===========BirdDiscntBindDiscntAction=========startDate:"+startDate+",endDate:"+endDate+",userId:"+userId);

            if(!"".equals(userId) && !"".equals(startDate) && !"".equals(endDate))
            {
	            String iv_sync_sequence1 = SeqMgr.getSyncIncreId();
		        int w = insUserDiscnt(userId,"84071643",startDate, endDate,iv_sync_sequence1);
		        //System.out.println("===========BirdDiscntBindDiscntAction===========w:"+w);
		        if (w > 0){
			        insTibDiscnt(iv_sync_sequence1, userId);
			        insTiSync(iv_sync_sequence1);
		        }
            }
        }

    }
	
	   public void insTibDiscnt(String iv_sync_sequence, String userId) throws Exception
	    {
	        IDataset UserDiscntInfos=UserDiscntInfoQry.getSpecDiscnt(userId);
	        if(IDataUtil.isNotEmpty(UserDiscntInfos)){
	        	for(int i=0;i<UserDiscntInfos.size();i++){
	        		IData param =UserDiscntInfos.getData(i);
	                param.put("PRODUCT_ID", param.getString("PRODUCT_ID","-1"));
	                param.put("PACKAGE_ID", param.getString("PACKAGE_ID","-1"));
	                param.put("SYNC_SEQUENCE", iv_sync_sequence);
	                param.put("USER_ID", userId);
	        	    Dao.executeUpdateByCodeCode("TI_B_USER_DISCNT", "INS_TIBDISCNT_FROM_USERDISCNT", param,Route.getJourDb(BizRoute.getRouteId()));
	        	}
	        } 
	    }
	    
	    public void insTiSync(String iv_sync_sequence) throws Exception
	    {
	        IData synchInfoData = new DataMap();
	        synchInfoData.put("SYNC_SEQUENCE", iv_sync_sequence);
	        String syncDay = StrUtil.getAcceptDayById(iv_sync_sequence);
	        synchInfoData.put("SYNC_DAY", syncDay);
	        synchInfoData.put("SYNC_TYPE", "0");
	        synchInfoData.put("TRADE_ID", "0");
	        synchInfoData.put("STATE", "0");
	        synchInfoData.put("SYNC_TIME", SysDateMgr.getSysTime());
	        synchInfoData.put("UPDATE_TIME", SysDateMgr.getSysTime());
	        Dao.insert("TI_B_SYNCHINFO", synchInfoData,Route.getJourDb(BizRoute.getRouteId()));
	    }
	    
	    public int insUserDiscnt(String userId ,String discntCode,String startDate, String endDate,String iv_sync_sequence) throws Exception
	    {

	    	String instId = SeqMgr.getInstId();
			IData cond = new DataMap();
			cond.put("USER_ID",userId);
			cond.put("USER_ID_A","-1");
			cond.put("PRODUCT_ID","-1");
			cond.put("PACKAGE_ID","-1");
			cond.put("DISCNT_CODE",discntCode);
			cond.put("SPEC_TAG","0");
			cond.put("RELATION_TYPE_CODE","");
			cond.put("INST_ID",instId);
			cond.put("CAMPN_ID","");
			cond.put("START_DATE",startDate);
			cond.put("END_DATE",endDate);
			cond.put("UPDATE_TIME",SysDateMgr.getSysTime());
			cond.put("RSRV_STR1",iv_sync_sequence);
			cond.put("REMARK","度假宽带2019，绑定减免魔百和功能费套餐");
			cond.put("SPEC_TAG","0");
			cond.put("UPDATE_STAFF_ID","");
			cond.put("UPDATE_DEPART_ID","");
			return Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "INS_ALL_FINISH", cond);           
	       
	        
	    }
	    
	    public int updUserDiscnt(String userId ,String discntCode,String endDate,String iv_sync_sequence) throws Exception
	    {
	            IData param = new DataMap();
	            param.put("USER_ID", userId);
	            param.put("DISCNT_CODE", discntCode);
	            param.put("SYNC_SEQUENCE", iv_sync_sequence);
	            param.put("END_DATE", endDate);
	            
	            StringBuilder sql = new StringBuilder();
	            sql.append(" UPDATE TF_F_USER_DISCNT A ");
	            sql.append(" SET A.END_DATE =  to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),A.REMARK = '度假宽带2019，取消绑定减免魔百和功能费套餐！',A.RSRV_STR1= :SYNC_SEQUENCE ") ;
	            sql.append(" WHERE A.DISCNT_CODE IN (:DISCNT_CODE) ") ;
	            sql.append(" AND A.USER_ID = :USER_ID ") ;
	            sql.append(" AND A.PARTITION_ID = MOD(:USER_ID, 10000) ") ;
	            sql.append(" AND A.END_DATE > A.START_DATE AND A.START_DATE < SYSDATE AND A.END_DATE > SYSDATE ") ;
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
