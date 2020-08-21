
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;

public class ShareInfoQry
{

    /**
     * 根据优惠查询共享优惠记录 @yanwu
     * @param discntCode
     * @return IDataset
     * @throws Exception
     */
    public static IDataset getShareByDiscnt(String discntCode) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("DISCNT_CODE", discntCode);
        return Dao.qryByCode("TD_B_DISCNT_SHARE", "SEL_SHARE_BY_DISCNT", inparams, Route.CONN_CRM_CEN);
    }
    
    /**
     * 查询所有的可以共享优惠相关记录
     * 
     * @return
     */
    public static IDataset getAllShareDiscnt() throws Exception
    {
        IData inparams = new DataMap();

        return Dao.qryByCode("TD_B_DISCNT_SHARE", "SEL_ALLDISCNT", inparams, Route.CONN_CRM_CEN);
    }

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
     * 查询所有资费,包括本月
     * 
     * @param user_id
     * @param role_code
     * @return
     */
    public static IDataset queryAllShareA(String share_id, String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("SHARE_ID", share_id);
        inparams.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_USER_SHARE", "SEL_ALL_PKA", inparams);
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
     * 查询所有资费的共享信息,包括本月
     * @param user_id
     * @return
     * @throws Exception
     */
    public static IDataset queryAllShareInfoA(String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_USER_SHARE", "SEL_ALLINFO_PKA", inparams);
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
        IDataset dataset=Dao.qryByCode("TF_F_USER_SHARE", "SEL_FORSHARE_DISCNT", inparams);
        if(IDataUtil.isNotEmpty(dataset))
        {
        	for(int i=0;i<dataset.size();i++)
        	{
        		IData data = dataset.getData(i);
        		String discntCode = data.getString("DISCNT_CODE");
        		String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
        		data.put("DISCNT_NAME", discntName);
        	}
        }
        return dataset;
    }
    
    /**
     * 查询主卡用户下所有可以共享的4g资费
     * 
     * @param user_id
     * @param
     * @return
     */
    public static IDataset queryDiscnts(String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_SHARE", "SEL_FORSHARE_DISCNTA", inparams);
    }
    
    /**
     * 查询主卡用户下已经生效的所有可以共享的4g资费20190125-wangsc10
     * 
     * @param user_id
     * @param
     * @return
     */
    public static IDataset queryDiscntsOLD(String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_SHARE", "SEL_FORSHARE_DISCNTA_OLD", inparams);
    }
    
    /**
     * 查询主卡用户下预约生效的所有可以共享的4g资费20190125-wangsc10
     * 
     * @param user_id
     * @param
     * @return
     */
    public static IDataset queryDiscntsNEW(String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_SHARE", "SEL_FORSHARE_DISCNTA_NEW", inparams);
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

        return Dao.qryByCode("TF_F_USER_SHARE_RELA", "SEL_BY_SERIALNUMBER_ONLY", inparams);
    }
    
    /**
     * @author yanwu
     * @param user_id
     * @return
     * @throws Exception
     */
    public static IDataset queryMemberRelaAB(String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_USER_SHARE_RELA", "SEL_BY_USERIDB_ROLEAB", inparams);
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
    
    public static IDataset queryMembera(String user_id) throws Exception
    {
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);

        return Dao.qryByCode("TF_F_USER_SHARE_RELA", "SEL_SHARE_MEMBERA", inparams);
    }

}
