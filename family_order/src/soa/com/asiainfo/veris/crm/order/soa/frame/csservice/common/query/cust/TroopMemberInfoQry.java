
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class TroopMemberInfoQry
{
    public static boolean isSmsTroopMember(String userId, String productId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_ID", productId);
//        IDataset dataset = Dao.qryByCode("TF_SM_TROOP_MEMBER", "IS_SMS_OBJ_USER", param);
//        String count = dataset.getData(0).getString("CNT");
//        return Integer.parseInt(count) > 0 ? true : false;
//        UpcCall.xxx
        /**
         * SELECT A.CATALOG_ID,
       B.OFFER_CODE,
       B.OFFER_NAME,
       C.FIELD_NAME,
       C.FIELD_VALUE
  FROM PM_OFFER_CATA_REL A, PM_OFER B, PM_EXT_CHA C
 WHERE A.CATALOG_ID = :CATALOG_ID--'68800924'
   AND A.OFFER_ID = B.OFFER_ID
   AND SYSDATE BETWEEN A.VALID_DATE AND A.EXPIRE_DATE
   AND SYSDATE BETWEEN B.VALID_DATE AND B.EXPIRE_DATE
   AND B.STATUS = '5'
   AND C.EXT_CHA_ID(+) = B.OFFER_ID
   AND C.TYPE(+) = 'E'
   AND C.FROM_TABLE_NAME(+) = 'TD_B_PACKAGE_EXT'
   AND c.FIELD_NAME(+) = 'COND_FACTOR1';
         */
        boolean isSmsTroopMember = false;
        IDataset result = UpcCall.qryOfferCatalogInfoByCatalogId(productId, "TD_B_PACKAGE_EXT", "COND_FACTOR1");
        if(IDataUtil.isNotEmpty(result))
        {
        	for(int i=0;i<result.size();i++)
        	{
        		IData data = result.getData(i);
        		String condFactor1 = data.getString("FIELD_VALUE", "0");
        		if(StringUtils.isNotBlank(condFactor1))
        		{
        			param.put("TROOP_ID", condFactor1);
        			IDataset dataset = Dao.qryByCode("TF_SM_TROOP_MEMBER", "SEL_BY_USERID_TROOPID", param);
        			if(IDataUtil.isNotEmpty(dataset))
        			{
        				isSmsTroopMember = true;
        				break;
        			}
        		}
        	}
        }
        
        return isSmsTroopMember;
    }

    public static boolean isTroopMember(String userId, String productStr3) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("PRODUCT_STR3", productStr3);
        IDataset dataset = Dao.qryByCode("TF_SM_TROOP_MEMBER", "IS_TROOP_USER", param);
        String count = dataset.getData(0).getString("CNT");
        return Integer.parseInt(count) > 0 ? true : false;
    }

    public static IDataset queryExistTarget(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);

        return Dao.qryByCode("TF_SM_TROOP_MEMBER", "SEL_IS_EXIST_TARGET_CUST_BY_USERID", param);
    }
    /**
     * 根据客户群Id和客户身份证号查询客户是否存在目标客户群
     * @param troopId
     * @param psptId
     * @return
     * @throws Exception 
     */
	public static IDataset queryCountByTroopIdAndPspt(String troopId,
			String psptId) throws Exception {
		IData param = new DataMap();
	    param.put("TROOP_ID", troopId);
	    param.put("PSPT_ID", psptId);
	    
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT USER_ID FROM TF_SM_TROOP_MEMBER WHERE TROOP_ID= :TROOP_ID AND CUST_CODE=:PSPT_ID AND MEMBER_STATUS='1' ");
        IDataset infos = Dao.qryBySql(sql, param);
        
		return infos;
	}
	/**
	 * 根据客户群Id和客户ID查询客户是否存在目标客户群
	 * @param troopId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryCountByTroopIdAndUserId(String troopId,
			String userId) throws Exception {
		IData param = new DataMap();
	    param.put("TROOP_ID", troopId);
	    param.put("USER_ID", userId);
	    
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT USER_ID FROM TF_SM_TROOP_MEMBER WHERE TROOP_ID= :TROOP_ID AND USER_ID=:USER_ID AND MEMBER_STATUS='1' ");
        IDataset infos = Dao.qryBySql(sql, param);
        
		return infos;
	}
}
