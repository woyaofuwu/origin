package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserOfferRelQry {
	 /**
     * 根据rel_offer_ins_id查询tf_f_user_offer_rel关联订购关系
     * @param REL_OFFER_INS_ID
     * @return
     * @throws Exception
     */
    public static IDataset getOfferRelByRelOfferInsId(String relOfferInsId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("REL_OFFER_INS_ID", relOfferInsId);
    	
    	return Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_REL_OFFER_INST_ID", param);
    }
    
    /**
     * 根据rel_offer_ins_id查询tf_f_user_offer_rel关联订购关系
     * @param REL_OFFER_INS_ID
     * @return
     * @throws Exception
     */
    public static IDataset getOfferRelByOfferInsIdAndRelOfferInsId(String offerInsId, String relOfferInsId) throws Exception
    {
    	IData param = new DataMap();
    	param.put("OFFER_INS_ID", offerInsId);
    	param.put("REL_OFFER_INS_ID", relOfferInsId);
    	
    	return Dao.qryByCode("TF_F_USER_OFFER_REL", "SEL_BY_OID_AND_RELOID.sql", param);
    }
}
