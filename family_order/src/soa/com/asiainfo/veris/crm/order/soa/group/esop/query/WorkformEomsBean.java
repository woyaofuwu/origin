package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformEomsBean 
{
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static boolean insertWorkformEoms(IData param) throws Exception
    {
        return Dao.insert("TF_B_EOP_EOMS", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static int[] insertWorkformEoms(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_EOMS", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static int[] insertWorkformEomsSub(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_EOMS_SUB", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryEomsByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_EOMS", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void delEomsByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_EOMS", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryNewWorkSheetByIbsysid(String ibsysid,String recordNum) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("RECORD_NUM", recordNum);
        return Dao.qryByCodeParser("TF_B_EOP_EOMS", "SEL_NEW_WORKSHEET_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryNewWorkSheetRecvByIbsysid(String ibsysid) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        return Dao.qryByCodeParser("TF_B_EOP_EOMS", "SEL_NEW_WORKSHEET_RECV_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryworkformEOMSBySerialNo(String serialNo) throws Exception
    {
    	IData param = new DataMap();
    	param.put("SERIALNO", serialNo);
        return Dao.qryByCode("TF_B_EOP_EOMS", "SEL_EOMS_BY_SERIALNO", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    

    public static IData queryBySubIbsysid(String subIbsysid) throws Exception
    {
    	IData params = new DataMap();
    	params.put("SUB_IBSYSID", subIbsysid);
    	IDataset eomsInfos = Dao.qryByCode("TF_B_EOP_EOMS", "SEL_BY_SUBIBSYSID", params,Route.getJourDb(BizRoute.getRouteId()));
    	if(DataUtils.isNotEmpty(eomsInfos))
    	{
    		return eomsInfos.first();
    	}
    	else
    	{
    		return new DataMap();
    	}
    }

    public static IData queryBySubIbsysIdAndOperTypeDesc(String subIbsysid, String operType) throws Exception
    {
        IData params = new DataMap();
        params.put("SUB_IBSYSID", subIbsysid);
        params.put("OPER_TYPE", operType);

        IDataset eomsInfos = Dao.qryByCode("TF_B_EOP_EOMS", "SEL_BY_SUBIBSYSIDOPERTYPE_DESC", params,
                Route.getJourDb(BizRoute.getRouteId()));
        if (DataUtils.isNotEmpty(eomsInfos))
        {
            return eomsInfos.first();
        }
        else
        {
            return null;
        }
    }
  
    public static IDataset queryworkformEOMSVOList(String ibsysId) throws Exception
    {
        IData params = new DataMap();
        params.put("IBSYSID", ibsysId);
        return Dao.qryByCode("TF_B_EOP_EOMS", "SEL_BY_EOMS_SVO_LIST", params,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IData querySerial(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
        IDataset eomsInfos = Dao.qryByCode("TF_B_EOP_EOMS", "SELSERIALNO_BY_IBSYSID", param, Route.getJourDbDefault());
        if(DataUtils.isNotEmpty(eomsInfos))
    	{
    		return eomsInfos.first();
    	}
    	else
    	{
    		return new DataMap();
    	}
    }
    
    public static IDataset qryworkformEOMSByIbsysidRecordNum(String ibsysid,String recordNum) throws Exception{
    	
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("RECORD_NUM", recordNum);
    	
    	return Dao.qryByCodeParser("TF_B_EOP_EOMS", "SEL_BY_IBSYSID_RECORDNUM", param, Route.getJourDb(BizRoute.getRouteId()));
    	
    }
    
    public static IDataset queryHisEopEOMSByIbsysId(String ibsysId) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysId); 

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT E.* FROM TF_BH_EOP_EOMS E,"); 
        sql.append("(SELECT BUSI_ID, IBSYSID, MAX(SUB_IBSYSID) AS SUB_IBSYSID ");
        sql.append(" FROM TF_B_EOP_EOMS T ");
        sql.append(" WHERE IBSYSID = :IBSYSID ");
        sql.append(" AND T.NODE_ID IN ('newWorkSheet', 'renewWorkSheet') ");
        sql.append(" GROUP BY BUSI_ID, IBSYSID) A "); 
        sql.append(" WHERE E.BUSI_ID = A.BUSI_ID ");
        sql.append(" AND E.SUB_IBSYSID = A.SUB_IBSYSID "); 
        sql.append(" AND E.TRADE_DRIECT ='0' ");
        sql.append(" AND E.IBSYSID=:IBSYSID "); 
        
        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
    }
    
    public static IDataset queryHisEopAttrByIbsysIdAndRecordNum(String ibsysId,String recordNum) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysId); 
        param.put("RECORD_NUM", recordNum);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT t.* FROM TF_BH_EOP_ATTR t ");  
        sql.append(" WHERE 1=1 ");
        sql.append(" AND IBSYSID = :IBSYSID "); 
        sql.append(" AND RECORD_NUM = :RECORD_NUM "); 
        sql.append(" order by SUB_IBSYSID ");
        
        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
    }
    
    public static IDataset queryEopAttrByIbsysIdAndRecordNum(String ibsysId,String recordNum) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysId); 
        param.put("RECORD_NUM", recordNum);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT t.* FROM TF_B_EOP_ATTR t ");  
        sql.append(" WHERE 1=1 ");
        sql.append(" AND IBSYSID = :IBSYSID "); 
        sql.append(" AND RECORD_NUM = :RECORD_NUM "); 
        sql.append(" order by SUB_IBSYSID ");
        
        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
    }
    
	public static IDataset qryworkformEOMSByIbsysidRecordNumSheettype(String ibsysid, String recordNum,String sheettype) throws Exception {
		IData param = new DataMap();
        param.put("IBSYSID", ibsysid); 
        param.put("RECORD_NUM", recordNum); 
        param.put("SHEETTYPE", sheettype); 


        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT t.* FROM TF_B_EOP_EOMS t ");  
        sql.append(" WHERE 1=1 ");
        sql.append(" AND t.IBSYSID = :IBSYSID "); 
        sql.append(" AND t.RECORD_NUM = :RECORD_NUM "); 
        sql.append(" AND t.SHEETTYPE = :SHEETTYPE "); 
        
        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
	}
	
	public static IDataset qryworkformEOMSByIbsysidSheettype(String ibsysid,String sheettype) throws Exception {
		IData param = new DataMap();
        param.put("IBSYSID", ibsysid); 
        param.put("SHEETTYPE", sheettype); 


        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT distinct t.SERIALNO FROM TF_B_EOP_EOMS t ");  
        sql.append(" WHERE 1=1 ");
        sql.append(" AND t.IBSYSID = :IBSYSID "); 
        sql.append(" AND t.SHEETTYPE = :SHEETTYPE "); 
        
        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
	}

	public static IDataset qryFinishEOMSByIbsysidRecordNumSheettype(String ibsysid, String recordNum,String sheettype) throws Exception {
		IData param = new DataMap();
        param.put("IBSYSID", ibsysid); 
        param.put("RECORD_NUM", recordNum); 
        param.put("SHEETTYPE", sheettype); 


        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT t.* FROM TF_BH_EOP_EOMS t ");  
        sql.append(" WHERE 1=1 ");
        sql.append(" AND t.IBSYSID = :IBSYSID "); 
        sql.append(" AND t.RECORD_NUM = :RECORD_NUM "); 
        sql.append(" AND t.SHEETTYPE = :SHEETTYPE "); 
        
        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
	}
	  public static IDataset querySpecialLineDetail(String ibsysid,Pagination page) throws Exception
	  {
	      IData params = new DataMap();
	      params.put("IBSYSID", ibsysid);
	      return Dao.qryByCode("TF_B_EOP_EOMS", "SEL_BY_EOMS_IBSYSID", params,page,Route.getJourDb(BizRoute.getRouteId()));
	  }
  
	public static IDataset querySpecialLineDetail(String ibsysId) throws Exception
    { 
		IData param = new DataMap();
        param.put("IBSYSID", ibsysId); 

        StringBuilder sql = new StringBuilder(1000);
        sql.append(" select we.sub_ibsysid,we.ibsysid,we.busi_id, we.product_id,we.sheettype,wed.busi_state,"); 
        sql.append(" wed.product_no,wed.trade_id,decode(we.deal_state,'0','未处理','1','已发送','2','处理成功','处理失败') deal_state, ");
        sql.append(" decode(we.oper_type,'newWorkSheet','派单','renewWorkSheet','重派单','replyWorkSheet','回复','withdrawWorkSheet','驳回','untreadWorkSheet','退回','confirmWorkSheet','受理','checkinWorkSheet','归档','suggestWorkSheet','阶段通知','notifyWorkSheet','阶段回复','replyError','程序性错误',we.oper_type) oper_type, ");
        sql.append(" decode(we.product_id,'7010','VOIP专线（专网专线）','7011','互联网专线接入（专网专线）','7012','数据专线（专网专线）',we.product_id) product_name, ");
        sql.append(" nvl2(we.eoms_order_code,'完工','在途') order_type,WE.RECORD_NUM ");
        sql.append(" from TF_B_EOP_EOMS we,TF_B_EOP_EOMS_DETAIL wed "); 
        sql.append(" where we.ibsysid=wed.ibsysid and we.RECORD_NUM=wed.RECORD_NUM ");
        sql.append(" and  (we.RECORD_NUM,we.group_seq) in ( "); 
        sql.append(" select  wed1.RECORD_NUM, max(we1.group_seq) group_seq ");
        sql.append(" from TF_B_EOP_EOMS we1,TF_B_EOP_EOMS_DETAIL wed1 "); 
        sql.append(" where we1.ibsysid=wed1.ibsysid and we1.RECORD_NUM=wed1.RECORD_NUM and we1.ibsysid=we.ibsysid "); 
        sql.append(" group by wed1.RECORD_NUM) "); 
        sql.append(" and we.ibsysid=:IBSYSID ");   
        
        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
    }

	public static IDataset getEomsDatasetByIbsysidRecordNum(String ibsysid,String recordNum) throws Exception
	{
		IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RECORD_NUM", recordNum); 

        StringBuilder sql = new StringBuilder(1000);
        sql.append(" SELECT * FROM TF_B_EOP_EOMS T" );
        sql.append(" WHERE T.IBSYSID = :IBSYSID ");
        sql.append(" AND T.RECORD_NUM = :RECORD_NUM ");
        sql.append(" ORDER BY T.INSERT_TIME DESC,T.GROUP_SEQ DESC ");
//        sql.append(" AND T.SHEETTYPE IN ('32','33','34') ");
//        sql.append(" AND T.OPER_TYPE='replyWorkSheet' ");
        
        return Dao.qryBySql(sql, param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset qryEomsByIbsysIdOperType(String ibsysid, String recordNum, String operType) throws Exception
	{
		 IData params = new DataMap();
		 params.put("IBSYSID", ibsysid);
		 params.put("RECORD_NUM", recordNum);
		 params.put("OPER_TYPE", operType);

	     return Dao.qryByCode("TF_B_EOP_EOMS", "SEL_BY_IBSYSID_OPERTYPE", params,Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset qryHisEomsByIbsysIdOperTypeGroupSeq(IData param) throws Exception {
        SQLParser parser  = new SQLParser(param);
        parser.addSQL("SELECT t.* FROM TF_BH_EOP_EOMS t ");  
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND t.IBSYSID = :IBSYSID "); 
        parser.addSQL(" AND t.OPER_TYPE IN ('newWorkSheet','renewWorkSheet')"); 
        parser.addSQL(" AND  t.group_seq =(SELECT max(a.group_seq) FROM TF_BH_EOP_EOMS a where a.IBSYSID = :IBSYSID  and  a.OPER_TYPE IN ('newWorkSheet','renewWorkSheet'))"); 
        parser.addSQL(" AND t.RECORD_NUM = :RECORD_NUM "); 
        
        return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset qryBulletinReplyYes(String sysTag, String beginDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("SYS_TAG", sysTag);
		param.put("BEGIN_DATE", beginDate);
		param.put("END_DATE", endDate);
		
		return Dao.qryByCode("TF_B_EOP_EOMS", "SEL_FOR_BULLETIN_REPLY_YES", param,Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset qryBulletinReplyNo(String sysTag, String beginDate, String endDate) throws Exception
	{
		IData param = new DataMap();
		param.put("SYS_TAG", sysTag);
		param.put("BEGIN_DATE", beginDate);
		param.put("END_DATE", endDate);
		
		return Dao.qryByCode("TF_B_EOP_EOMS", "SEL_FOR_BULLETIN_REPLY_NO", param,Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static IDataset qryEomsByIbsysIdOperTypeGroupSeq(IData param) throws Exception {
        SQLParser parser  = new SQLParser(param);
        parser.addSQL("SELECT t.* FROM TF_B_EOP_EOMS t ");  
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND t.IBSYSID = :IBSYSID "); 
        parser.addSQL(" AND t.OPER_TYPE IN ('newWorkSheet','renewWorkSheet')"); 
        parser.addSQL(" AND  t.group_seq =(SELECT max(a.group_seq) FROM TF_B_EOP_EOMS a where a.IBSYSID = :IBSYSID  and  a.OPER_TYPE IN ('newWorkSheet','renewWorkSheet'))"); 
        parser.addSQL(" AND t.RECORD_NUM = :RECORD_NUM "); 
        
        return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getRouteId()));
	}
}
