package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserOfferRelInfoQry
{
	//注意是Ins_Id不是Inst_Id,或者SQL与字段保持一致也行,目前错的都修改过来了
	
	
    public static IDataset qryUserOfferRelInfosByRelOfferInstId(String relOfferInsId) throws Exception
    {
        IData param = new DataMap();

        param.put("REL_OFFER_INS_ID", relOfferInsId);

        return Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_REL_OFFER_INST_ID", param);
    }
    
    public static IDataset queryUserOfferRelInfosByRelOfferInstId(String relOfferInsId) throws Exception
    {
        IData param = new DataMap();

        param.put("REL_OFFER_INS_ID", relOfferInsId);

        return Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_REL_OFFER_INST_ID_NOW", param);
    }
    
    public static IDataset qryUserOfferRelInfosByRelTypeAndRelOfferInstId(String relType, String relOfferInsId) throws Exception
    {
        IData param = new DataMap();

        param.put("REL_TYPE", relType);
        param.put("REL_OFFER_INS_ID", relOfferInsId);

        return Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_REL_TYPE_REL_OFFER_INST_ID", param);
    }
    
    public static IDataset qryUserOfferRelInfosByOfferInstId(String offerInsId) throws Exception
    {
        IData param = new DataMap();

        param.put("OFFER_INS_ID", offerInsId);

        return Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_OFFER_INST_ID", param);
    }
    
    public static IDataset qryUserOfferRelInfosByRelTypeAndOfferInstId(String relType, String offerInsId) throws Exception
    {
        IData param = new DataMap();

        param.put("REL_TYPE", relType);
        param.put("OFFER_INS_ID", offerInsId);

        return Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_REL_TYPE_OFFER_INST_ID", param);
    }
    
    public static IData qryUserOfferRelInfoByOfferInstIdAndRelOfferCode(String offerInstId,String relOfferCode) throws Exception
    {
    	IData param = new DataMap();
    	param.put("OFFER_INS_ID", offerInstId);
    	param.put("REL_OFFER_CODE", relOfferCode);
    	
    	IDataset dataset = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_OFFER_INS_ID_REL_OFFER_CODE", param);
    	if(IDataUtil.isNotEmpty(dataset)){
    		return dataset.first();
    	}else{
    		param.clear();
    		return param;
    	}
    }
    
    public static IData qryUserOfferRelInfoByOfferInsIdAndRelOfferInsId(String offerInsId,String relOfferInsId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("OFFER_INS_ID", offerInsId);
    	param.put("REL_OFFER_INS_ID", relOfferInsId);
    	
    	IDataset dataset = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_OFFER_INS_ID_REL_OFFER_INS_ID", param);
    	return dataset.first();
    }
    
    public static IData qryUserAllOfferRelInfoByOfferInsIdAndRelOfferInsId(String offerInsId,String relOfferInsId) throws Exception
    {
        IData param = new DataMap();
        param.put("OFFER_INS_ID", offerInsId);
        param.put("REL_OFFER_INS_ID", relOfferInsId);
        
        IDataset dataset = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_ALL_BY_OFFER_INS_ID_REL_OFFER_INS_ID", param);
        return dataset.first();
    }
    
    public static IDataset qryUserAllOfferRelByRelOfferInsId(String relOfferInsId) throws Exception{
    	IData param = new DataMap();
    	param.put("REL_OFFER_INS_ID", relOfferInsId);
    	
    	IDataset dataset = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_ALL_BY_REL_OFFER_INST_ID", param);
    	return dataset;
    }
    public static IDataset qryUserAllOfferRelByUserIdDiscntCode(String userId , String discntCode , String relOfferInsId) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DISCNT_CODE", discntCode);
        param.put("REL_OFFER_INS_ID", relOfferInsId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_ALL_BY_USER_ID_DISCNT_CODE", param);
        return dataset;
    }
    public static IDataset qryUserAllOfferRelByUserIdDiscntCode_1(String userId , String discntCode , String relOfferInsId) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DISCNT_CODE", discntCode);
        param.put("REL_OFFER_INS_ID", relOfferInsId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_ALL_BY_USER_ID_DISCNT_CODE_1", param);
        return dataset;
    }



    public static IDataset qryUserAllOfferRelByUserIdAndRlUserId(String userId , String relUserid , String groupId,String relOfferInsId) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("REL_USER_ID", relUserid);
        param.put("GROUP_ID", groupId);
        param.put("REL_OFFER_INS_ID", relOfferInsId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_ALL_BY_USER_ID_AND_REL_USERID_GROUPID", param);
        return dataset;
    }

    /**
     * @Description 根据rel_offer_type 和 inst_id查找offer_rel记录
     * @param: 权益中心
     * @author: liwei29
     * @date: 2020-7-16
     */
    public static IDataset qryUserAllOfferRelByRelOfferTypeAndInstId(String reloffertype , String instId) throws Exception{
        IData param = new DataMap();
        param.put("REL_OFFER_TYPE", reloffertype);
        param.put("REL_OFFER_INS_ID", instId);

        IDataset dataset = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_ALL_BY_REL_OFFER_TYPE_INST_ID", param);
        return dataset;
    }
    public static IDataset qryUserOfferRelByUserIdAndRelOfferType(String userId , String relOfferType) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("REL_OFFER_TYPE", relOfferType);
        IDataset dataset = Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_USER_ID_AND_REL_OFFER_TYPE", param);
        return dataset;
    }
}
