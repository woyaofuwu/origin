package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class DatalineOrderDAO
{
    public static IDataset queryDatalineInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM  TF_F_WORKFORM_EOS_LINE_TEMP  T");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.IBSYSID = :IBSYSID");
        return Dao.qryByParse(parser);
    }
    
    public static IDataset queryCommonDataInfo(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM  TF_F_WORKFORM_EOS_COMMON_TEMP T ");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND T.IBSYSID = :IBSYSID");
        parser.addSQL(" AND T.GROUP_ID = :GROUP_ID");
        parser.addSQL(" AND T.STATE = :STATE");
        return Dao.qryByParse(parser);
    }
    
    public static IDataset queryDatalineOrder(IData param) throws Exception
    {
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT DISTINCT T.IBSYSID ,T.GROUP_ID ,T.STATE ");
        parser.addSQL(" FROM  TF_F_WORKFORM_EOS_COMMON_TEMP  T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.IBSYSID = :IBSYSID");
        parser.addSQL(" AND T.GROUP_ID = :GROUP_ID");
        parser.addSQL(" AND T.STATE = :STATE");
        
        return Dao.qryByParse(parser);
    }
    
    public int updateDalineOrderState(IData param) throws Exception{
        
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE TF_F_WORKFORM_EOS_COMMON_TEMP T SET T.STATE = '1' WHERE T.IBSYSID = :IBSYSID ");
        
        return Dao.executeUpdate(sql, param);
    }
    
    public void createTradeExt(IData param) throws Exception{
        
        Dao.insert("TF_B_TRADE_EXT", param, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    public static IData queryDataline(String userId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", userId);
    	SQLParser parser = new SQLParser(param);
    	parser.addSQL(" SELECT  * ");
        parser.addSQL(" FROM  TF_F_USER_DATALINE");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND USER_ID = :USER_ID ");

        IDataset dataset =  Dao.qryByParse(parser);
        IData data = new DataMap();
        if(IDataUtil.isNotEmpty(dataset)){
        	data = dataset.first();
        }
        return data;
    }
    
    public static IData queryDataDetailline(String sheetType, String productNo) throws Exception
    {
    	IData param = new DataMap();
    	param.put("SHEET_TYPE", sheetType);
    	param.put("PRODUCT_NO", productNo);
    	SQLParser parser = new SQLParser(param);
    	parser.addSQL(" SELECT  * ");
        parser.addSQL(" FROM  TF_F_USER_DATALINE");
        parser.addSQL(" WHERE 1=1");
        parser.addSQL(" AND SHEET_TYPE = :SHEET_TYPE ");
        parser.addSQL(" AND PRODUCT_NO = :PRODUCT_NO ");

        IDataset dataset =  Dao.qryByParse(parser);
        IData data = new DataMap();
        if(IDataUtil.isNotEmpty(dataset)){
        	data = dataset.first();
        }
        return data;
    }
}
