
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserSaleGoodsInfoQry
{

	public static IDataset getByRelationTradeIdComm(IData cond) throws Exception
    {
        IDataset dataset = Dao.qryByCode("TF_F_USER_SALE_GOODS", "SEL_BY_RELATIONTRADEID_COMPARA", cond);

        return dataset;
    }
	
    public static IDataset getByRelationTradeId(String relationTradeId) throws Exception
    {
        IData cond = new DataMap();
        cond.put("RELATION_TRADE_ID", relationTradeId);

        IDataset dataset = Dao.qryByCode("TF_F_USER_SALE_GOODS", "SEL_BY_RELATION_TRADE_ID", cond);

        return dataset;
    }
    
    public static IDataset getByRelationTradeId(String relationTradeId,String eparchyCode) throws Exception
    {
        IData cond = new DataMap();
        cond.put("RELATION_TRADE_ID", relationTradeId);

        IDataset dataset = Dao.qryByCode("TF_F_USER_SALE_GOODS", "SEL_BY_RELATION_TRADE_ID", cond,eparchyCode);

        return dataset;
    }

    public static IDataset querySaleGoodsInfoByResCode(String userId, String imei) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RES_CODE", imei);

        return Dao.qryByCodeParser("TF_F_USER_SALE_GOODS", "SEL_BY_RESCODE", param);
    }

    public static IDataset querySaleGoodsInfoOnlyByResCode(String imei) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_CODE", imei);
        param.put("GOODS_STATE", "0");

        return Dao.qryByCodeParser("TF_F_USER_SALE_GOODS", "SEL_ONLYBY_RESCODE", param);
    }
    
    public static IDataset querySaleGoodsInfoByDeviceModelCode(String userId, String deviceModelCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DEVICE_MODEL_CODE", deviceModelCode);

        return Dao.qryByCodeParser("TF_F_USER_SALE_GOODS", "SEL_BY_DEVICE_MODEL_CODE", param);
    }
    
    public static IDataset querySaleGoodsInfo(String userId, String relationTradeId, String deviceModelCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RELATION_TRADE_ID", relationTradeId);
        param.put("DEVICE_MODEL_CODE", deviceModelCode);

        return Dao.qryByCodeParser("TF_F_USER_SALE_GOODS", "SEL_BY_RELATIONTRADEID_DEVICEMODELCODE", param);
    }
    
    public static void insertIMSTopsetboxOnline(IData param)throws Exception{
    	Dao.executeUpdateByCodeCode("TF_F_USER_SALE_GOODS", "INS_SALE_GOODS", param);
    }
}
