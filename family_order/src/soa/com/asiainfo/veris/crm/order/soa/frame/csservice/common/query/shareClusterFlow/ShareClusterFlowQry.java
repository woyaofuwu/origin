
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.shareClusterFlow;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ShareClusterFlowQry
{

    /**
     * 查询所有资费
     * 
     * @param user_id
     * @param role_code
     * @return
     */
    public static IDataset queryAllShare(String share_id, String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SHARE_ID", share_id);
        inparams.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_USER_SHARE", "SEL_ALL_PK", inparams);
    }

    /**
     * 查询所有资费的共享信息
     * 
     * @param user_id
     * @param role_code
     * @return
     */
    public static IDataset queryAllShareInfo(String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_USER_SHARE", "SEL_ALLINFO_PK", inparams);
    }

    /**
     * 查询主卡用户下所有可以共享的4g资费
     * 
     * @param user_id
     * @param
     * @return
     */
    public static IDataset queryDiscnt(String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);
        inparams.put("SHARE_TYPE", "C-SHARE");
        return Dao.qryByCode("TD_B_DISCNT_SHARE", "SEL_SHARE_DISCNT", inparams);
    }

    /**
     * 查询用户当前的共享成员
     * 
     * @param user_id
     * @param
     * @return
     */
    public static IDataset queryMember(String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_USER_SHARE_RELA", "SEL_SHARE_MEMBER", inparams);
    }

    /**
     * 查询用户是否已经为共享关系
     * 
     * @param user_id
     * @param role_code
     * @return
     */
    public static IDataset queryMemberRela(String user_id, String role_code) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);
        inparams.put("ROLE_CODE", role_code);

        return Dao.qryByCode("TF_F_USER_SHARE_RELA", "SEL_BY_USERID_MAIN", inparams);
    }

    public static IDataset queryRelaByShareIdAndRoleCode(String share_id, String role_code) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SHARE_ID", share_id);
        inparams.put("ROLE_CODE", role_code);

        return Dao.qryByCode("TF_F_USER_SHARE_RELA", "SEL_BY_SHAREID_ROLECODE", inparams);
    }

    /**
     * 根据instId查询相关记录
     * 
     * @param inst_id
     * @return
     */
    public static IDataset queryRelaForInst(String inst_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("INST_ID", inst_id);

        return Dao.qryByCode("TF_F_USER_SHARE_RELA", "SEL_MEMBER_INST", inparams);
    }
    
    /**
     * 查询用户是否是家庭共享关系
     * 
     * @param user_id
     * @param role_code
     * @return
     */
    public static IDataset queryRela(String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_SHARE_RELA", "SEL_BY_USERID", inparams);
    }
    
    /**
     * 根据查询SHARE_TYPE_CODE
     * 
     * @param user_id
     * @param role_code
     * @return
     */
    public static IDataset queryResId(String feepolicy_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("FEEPOLICY_ID", feepolicy_id);
        StringBuilder sql = new StringBuilder();
        sql.append(" select RES_ID,FEEPOLICY_ID,RES_TYPE from TD_B_TP_RES");
        sql.append(" where FEEPOLICY_ID = :FEEPOLICY_ID ");
        IDataset results = Dao.qryBySql(sql, inparams, Route.CONN_CRM_CEN);
        /*if(IDataUtil.isNotEmpty(results)){
        	return results.getData(0).getString("RES_ID");
        }*/
        return results;
    }

}
