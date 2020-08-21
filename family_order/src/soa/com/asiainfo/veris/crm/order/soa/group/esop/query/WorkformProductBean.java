package com.asiainfo.veris.crm.order.soa.group.esop.query;

import com.ailk.biz.service.BizRoute;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;

public class WorkformProductBean
{
	public static IData qryProductByPk(String ibsysid, String recordNum) throws Exception
	{
		IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RECORD_NUM", recordNum);
        IDataset productInfos = Dao.qryByCodeParser("TF_B_EOP_PRODUCT", "SEL_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
        
        if(DataUtils.isNotEmpty(productInfos))
        {
        	return productInfos.first();
        }
        return new DataMap();
	}
	
	public static void updProductByPk(IData param) throws Exception
	{
		Dao.executeUpdateByCodeCode("TF_B_EOP_PRODUCT", "UPD_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
	}
    public static void updProductByTradeidAndUserid(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_B_EOP_PRODUCT", "UPD_BY_TRADEID_USERID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    public static void updProductByRsrvstr2(IData param) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_B_EOP_PRODUCT", "UPD_BY_RSRV_STR2", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	public static void updProductByValid(String ibsysid, String recordNum,String validTag) throws Exception
	{
		IData param = new DataMap();
		param.put("VALID_TAG", validTag);
		param.put("IBSYSID", ibsysid);
		param.put("RECORD_NUM", recordNum);
		Dao.executeUpdateByCodeCode("TF_B_EOP_PRODUCT", "UPD_BY_VALID_TAG", param, Route.getJourDb(BizRoute.getRouteId()));
	}
	
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformProduct(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_PRODUCT", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    /**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static int[] insertWorkformProductSub(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_PRODUCT_SUB", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    public static int[] insertWorkformProductExt(IDataset param) throws Exception
    {
        return Dao.insert("TF_B_EOP_PRODUCT_EXT", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
	/**
     * 新增数据
     * 
     * @param param
     * @return boolean
     * @throws Exception
     */
    public static boolean insertWorkformProduct(IData param) throws Exception
    {
        return Dao.insert("TF_B_EOP_PRODUCT", param, Route.getJourDb(BizRoute.getRouteId()));
    } 
    
    
    public static IDataset qryProductByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	param.put("VALID_TAG", EcEsopConstants.STATE_VALID);
        return Dao.qryByCode("TF_B_EOP_PRODUCT", "SEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryProductSubByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
//    	param.put("RECORD_NUM", EcEsopConstants.STATE_VALID);
        return Dao.qryByCode("TF_B_EOP_PRODUCT_SUB", "SEL_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void delProductByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_PRODUCT", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static void delProductSubByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_PRODUCT_SUB", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    public static void delProductExtByIbsysid(String ibsysid) throws Exception
    {
    	IData param = new DataMap();
    	param.put("IBSYSID", ibsysid);
    	Dao.executeUpdateByCodeCode("TF_B_EOP_PRODUCT_EXT", "DEL_BY_IBSYSID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset qryProductListByIbsysidProductId(String ibsysid, String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("PRODUCT_ID", productId);
        return Dao.qryByCode("TF_B_EOP_PRODUCT", "SEL_BY_IBSYSID_PRODUCTID", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset QueryGroupserialNo(String userId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        return Dao.qryByCode("TF_B_EOP_PRODUCT", "SEL_GROUPPART_BY_GROUP_ID", params,Route.getJourDb(BizRoute.getRouteId()));
    }
    
    public static IDataset queryHisEopProInfoBySn(String serialNumber) throws Exception
    {
    	IData param = new DataMap(); 
        param.put("SERIAL_NUMBER", serialNumber);

        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT * "); 
        sql.append("FROM TF_BH_EOP_PRODUCT ");
        sql.append("WHERE 1=1 ");
        sql.append("AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append("AND rownum < 2 ");
        sql.append("order by IBSYSID desc "); 
        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
    }

	public static IDataset queryMaxSerialMumberByGrpSn(String grp_Sn, int num) throws Exception{
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", grp_Sn);
        param.put("NUM", num);
        StringBuilder sqlMaxSN = new StringBuilder();
        sqlMaxSN.append(" SELECT MAX(SERIAL_NUMBER) MAXSN FROM TF_B_EOP_PRODUCT WHERE  SUBSTR(SERIAL_NUMBER, 1, :NUM) = :SERIAL_NUMBER ");
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL(sqlMaxSN.toString());
        IDataset productDataset = Dao.qryByParse(parser,Route.getJourDb(Route.CONN_CRM_CG));
        
		return productDataset;
	}
	
	
	public static IDataset queryProInfoBySn(String Sn) throws Exception{
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", Sn);
        
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT * "); 
        sql.append("FROM TF_B_EOP_PRODUCT ");
        sql.append("WHERE 1=1 ");
        sql.append("AND SERIAL_NUMBER = :SERIAL_NUMBER ");
        
        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
        
	}
    public static IDataset qryProductExtByIbsysid(String ibsysid) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        return Dao.qryByCode("TF_B_EOP_PRODUCT_EXT", "SEL_BY_PK", param, Route.getJourDb(BizRoute.getRouteId()));
    }
    public static IDataset qryProductByIbsysidAndRsrvstr5(String ibsysid,String rsrvStr5) throws Exception
    {
        IData param = new DataMap();
        param.put("IBSYSID", ibsysid);
        param.put("RSRV_STR5", rsrvStr5);
        StringBuilder sql = new StringBuilder(1000);
        sql.append("SELECT * ");
        sql.append("FROM TF_B_EOP_PRODUCT ");
        sql.append("WHERE 1=1 ");
        sql.append("AND IBSYSID = :IBSYSID ");
        if(StringUtils.isNotEmpty(rsrvStr5))
        {
            sql.append("AND RSRV_STR5 = :RSRV_STR5 ");
        }
        else
        {
            sql.append("AND RSRV_STR5 is null ");
        }
        return Dao.qryBySql(sql, param, Route.getJourDbDefault());
    }
}
