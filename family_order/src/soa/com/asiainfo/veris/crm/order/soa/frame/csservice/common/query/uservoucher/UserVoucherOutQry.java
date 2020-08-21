
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.uservoucher;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserVoucherOutQry
{

    /**
     * 查询用户的所有优惠
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getUserAlldiscnts(IData param) throws Exception
    {
        IDataset dataSet = new DatasetList();
        dataSet = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BY_USERID_NEW", param);
        return dataSet;
    }

    /**
     * 查询产品的信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getUserAllProducts(IData param) throws Exception
    {
        IDataset dataSet = new DatasetList();
        dataSet = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_ALL_EXIST_BY_USERID", param);
        return dataSet;
    }

    /**
     * 查询用户的服务信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getUserAllSvcInfos(IData param) throws Exception
    {
        IDataset dataSet = new DatasetList();
        dataSet = Dao.qryByCode("TF_F_USER_SVC", "SEL_BY_USERID_ALL", param);
        return dataSet;
    }

    /**
     * 用户信息 remove_age in_mode_type servial_number
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserInfoBySn(IData data) throws Exception
    {
        IDataset userInfos = Dao.qryByCode("TF_F_USER", "SEL_BY_SN", data);
        return userInfos;
    }

    /**
     * 查询用户的平台服务
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getUserPlatSvc(IData param) throws Exception
    {
        IDataset dataSet = new DatasetList();
        dataSet = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_NEW", param);
        return dataSet;
    }

    /**
     * 查询用户营销活动
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getUserSeleactiveInfos(IData param) throws Exception
    {
        IDataset dataSet = new DatasetList();
        dataSet = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_NEW", param);
        return dataSet;
    }

    /**
     * 根据传入SESSION_ID和SERIAL_NUMBER查询该用户wap凭证信息
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserVoucherInfo(IData data) throws Exception
    {
        // 根据传入SESSION_ID和SERIAL_NUMBER查询该用户wap凭证信息
        IDataset res = Dao.qryByCode("TF_B_WAP_SESSION", "SEL_CHECK_USER_VOUCHER", data);
        return res;
    }

    /**
     * 修改session时长
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static int updateSessionTime(IData param) throws Exception
    {
        int svc = Dao.executeUpdateByCodeCode("TF_B_WAP_SESSION", "UPD_ACCEPT_END_HALF", param);
        return svc;
    }
}
