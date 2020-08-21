package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOfferRelInfoQry;

public class DestroyUserUnEffectOfferAction  implements ITradeAction
{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		// TODO Auto-generated method stub
		UcaData ucaData = btd.getRD().getUca();
        String serialNumber = ucaData.getSerialNumber();
        String sysTime = btd.getRD().getAcceptTime();
        String userId = ucaData.getUserId();
        
        //结束未生效的产品以及优惠
        IDataset unEffectProds = qryUserUnEffectProds(userId);
        if(IDataUtil.isNotEmpty(unEffectProds))
        {
        	for(int i=0,size =unEffectProds.size();i<size;i++)
        	{
        		IData temp = unEffectProds.getData(i);
        		String prodInsId = temp.getString("INST_ID");
        		
        		ProductTradeData ptd = new ProductTradeData(temp);
        		ptd.setEndDate(btd.getRD().getAcceptTime());
        		ptd.setModifyTag(BofConst.MODIFY_TAG_DEL);
        		btd.add(serialNumber, ptd);
        		
        		IDataset offerRels = UserOfferRelInfoQry.qryUserOfferRelInfosByOfferInstId(prodInsId);
        		if(IDataUtil.isNotEmpty(offerRels))
                {
        			for(int j=0,sizej =offerRels.size();j<sizej;j++)
                	{
        				IData tempj = offerRels.getData(j);
                		String startDate = tempj.getString("START_DATE");
                		String relOfferInsId = tempj.getString("REL_OFFER_INS_ID");
                		String relOfferType = tempj.getString("REL_OFFER_TYPE");
                		if(startDate.compareTo(sysTime) >0 )
                		{
                			OfferRelTradeData offerRelTd = new OfferRelTradeData(tempj);
            				offerRelTd.setEndDate(btd.getRD().getAcceptTime());
            				offerRelTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    		btd.add(serialNumber, offerRelTd);
                		}
        				
        				if(StringUtils.equals("D", relOfferType))
        				{
        					DiscntTradeData dtd = ucaData.getUserDiscntByInstId(relOfferInsId);
        					if(dtd!= null && dtd.getEndDate().compareTo(sysTime)>0)
        					{
        						dtd.setEndDate(btd.getRD().getAcceptTime());
        						dtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        		btd.add(serialNumber, dtd);
        					}
        				}
        				if(StringUtils.equals("S", relOfferType))
        				{
        					SvcTradeData svcTd = ucaData.getUserSvcByInstId(relOfferInsId);
        					if(svcTd!= null && svcTd.getEndDate().compareTo(sysTime)>0)
        					{
        						svcTd.setEndDate(btd.getRD().getAcceptTime());
        						svcTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        		btd.add(serialNumber, svcTd);
        					}
        				}
                	}
                }
        	}
        }
        
        //结束未生效的优惠
        IDataset unEffectDiscnts = qryUnEffectDiscnts(userId);
        
        if(IDataUtil.isNotEmpty(unEffectDiscnts))
        {
        	for(int i=0,size =unEffectDiscnts.size();i<size;i++)
        	{
        		IData temp = unEffectDiscnts.getData(i);
        		String instId = temp.getString("INST_ID");
        		
        		DiscntTradeData dtd = ucaData.getUserDiscntByInstId(instId);
        		//QR-20190702-07立即销户报错 dtd.getEndDate().compareTo(dtd.getStartDate())>0 再加上这个条件，属于预约的，并且未处理过的，不然前面已经处理过了，后面再处理会出现2条一样的数据
        		if(dtd!= null && dtd.getEndDate().compareTo(sysTime)>0 && dtd.getEndDate().compareTo(dtd.getStartDate())>0)
				{
					dtd.setEndDate(btd.getRD().getAcceptTime());
					dtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
            		btd.add(serialNumber, dtd);
				}
        	}
        }
        
        //结束未生效的服务
        IDataset unEffectSvcs = qryUnEffectSvcs(userId);
        if(IDataUtil.isNotEmpty(unEffectSvcs))
        {
        	for(int i=0,size =unEffectSvcs.size();i<size;i++)
        	{
        		IData temp = unEffectSvcs.getData(i);
        		String instId = temp.getString("INST_ID");
        		
        		SvcTradeData svcTd = ucaData.getUserSvcByInstId(instId);
				if(svcTd!= null && svcTd.getEndDate().compareTo(sysTime)>0)
				{
					svcTd.setEndDate(btd.getRD().getAcceptTime());
					svcTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
            		btd.add(serialNumber, svcTd);
				}
        	}
        }
	}
	
	public static IDataset qryUnEffectSvcs(String userId) throws Exception
	{
		IData param = new DataMap();
        param.put("USER_ID", userId);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT  ");
        sql.append("US.PARTITION_ID,US.USER_ID,US.USER_ID_A, ");
        sql.append("US.SERVICE_ID,US.MAIN_TAG,US.INST_ID,US.CAMPN_ID,TO_CHAR(US.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE, ");
        sql.append("TO_CHAR(US.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(US.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME,US.UPDATE_STAFF_ID,US.UPDATE_DEPART_ID,US.REMARK, ");
        sql.append("US.RSRV_NUM1,US.RSRV_NUM2,US.RSRV_NUM3,US.RSRV_NUM4,US.RSRV_NUM5, ");
        sql.append("US.RSRV_STR1,US.RSRV_STR2,US.RSRV_STR3,US.RSRV_STR4,US.RSRV_STR5, ");
        sql.append("TO_CHAR(US.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(US.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2,TO_CHAR(US.RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,US.RSRV_TAG1,US.RSRV_TAG2, ");
        sql.append("US.RSRV_TAG3,US.RSRV_STR6,US.RSRV_STR7,US.RSRV_STR8,US.RSRV_STR9, ");
        sql.append("US.RSRV_STR10 ");
        sql.append("FROM TF_F_USER_SVC US ");
        sql.append("WHERE US.PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000) ");
        sql.append("AND US.USER_ID=TO_NUMBER(:USER_ID) ");
        sql.append("AND US.START_DATE > SYSDATE ");
        sql.append("  AND US.END_DATE > US.START_DATE ");
        IDataset ids = Dao.qryBySql(sql, param);
        return ids;
		
		
	}
	
	public static IDataset qryUnEffectDiscnts(String userId) throws Exception
	{

		IData param = new DataMap();
		param.put("USER_ID", userId);

		StringBuilder sql = new StringBuilder(1000);
		sql.append("SELECT UD.PARTITION_ID,UD.USER_ID,UD.USER_ID_A,  ");
		sql.append("       UD.DISCNT_CODE,UD.SPEC_TAG,UD.RELATION_TYPE_CODE,UD.INST_ID,UD.CAMPN_ID, ");
		sql.append("       TO_CHAR(UD.START_DATE,'YYYY-MM-DD HH24:MI:SS') START_DATE,TO_CHAR(UD.END_DATE,'YYYY-MM-DD HH24:MI:SS') END_DATE,TO_CHAR(UD.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UPDATE_TIME, ");
		sql.append("       UD.UPDATE_STAFF_ID,UD.UPDATE_DEPART_ID,UD.REMARK,UD.RSRV_NUM1,UD.RSRV_NUM2,UD.RSRV_NUM3,UD.RSRV_NUM4,UD.RSRV_NUM5,UD.RSRV_STR1,UD.RSRV_STR2, ");
		sql.append("       UD.RSRV_STR3,UD.RSRV_STR4,UD.RSRV_STR5,TO_CHAR(UD.RSRV_DATE1,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE1,TO_CHAR(UD.RSRV_DATE2,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE2, ");
		sql.append("       TO_CHAR(UD.RSRV_DATE3,'YYYY-MM-DD HH24:MI:SS') RSRV_DATE3,UD.RSRV_TAG1,UD.RSRV_TAG2,UD.RSRV_TAG3 ");
		sql.append("  FROM TF_F_USER_DISCNT UD ");
		sql.append(" WHERE UD.USER_ID = TO_NUMBER(:USER_ID) ");
		sql.append("   AND UD.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
		sql.append("   AND UD.START_DATE > SYSDATE ");
		sql.append("  AND UD.END_DATE > UD.START_DATE ");

		IDataset ids = Dao.qryBySql(sql, param);

		return ids;

	}
	
	 public static IDataset qryUserUnEffectProds(String userId) throws Exception
	 {
	        IData param = new DataMap();
	        param.put("USER_ID", userId);

	        StringBuilder sql = new StringBuilder(1000);

	        sql.append("SELECT PARTITION_ID, TO_CHAR(USER_ID) USER_ID, TO_CHAR(USER_ID_A) USER_ID_A, ");
	        sql.append("PRODUCT_ID, PRODUCT_MODE, BRAND_CODE, TO_CHAR(INST_ID) INST_ID, ");
	        sql.append("TO_CHAR(CAMPN_ID) CAMPN_ID, ");
	        sql.append("TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE, ");
	        sql.append("TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE, ");
	        sql.append("TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, ");
	        sql.append("UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, ");
	        sql.append("RSRV_NUM3, TO_CHAR(RSRV_NUM4) RSRV_NUM4, TO_CHAR(RSRV_NUM5) RSRV_NUM5, ");
	        sql.append("RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, ");
	        sql.append("TO_CHAR(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, ");
	        sql.append("TO_CHAR(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, ");
	        sql.append("TO_CHAR(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, ");
	        sql.append("RSRV_TAG2, RSRV_TAG3, MAIN_TAG ");
	        sql.append("FROM TF_F_USER_PRODUCT T ");
	        sql.append("WHERE T.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000) ");
	        sql.append("AND T.USER_ID = :USER_ID ");
	        sql.append("AND T.END_DATE > T.START_DATE ");
	        sql.append("AND T.START_DATE > SYSDATE ");
	   
	        return  Dao.qryBySql(sql, param);
	  }

}
