
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class GroupQueryBean
{
    /**
     * vpn短号校验
     * 
     * @param pd
     * @param param
     */
    public static IDataset CheckVUserShortCode(IData param) throws Exception
    {
        return Dao.qryByCodeAllCrm("TD_S_CPARAM", "ShortCodeExist", param, true);
    }

    /**
     * vpn短号校验(校验台帐)
     * 
     * @param pd
     * @param param
     */
    public static IDataset CheckVUserShortCodeByTrade(IData param) throws Exception
    {
        return Dao.qryByCodeAllJour("TD_S_CPARAM", "ShortCodeTradeExist", param, true);
    }

    public IDataset getAddCountMonth(IData dt) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_UU", "SEL_VPMN_MEB_ADDCOUNT", dt);
    }

    public IDataset qryCustgroupById(IData data) throws Exception
    {
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_USERID", data);
    }

    public IDataset qryCustgroupBySn(IData data) throws Exception
    {
        return Dao.qryByCode("TF_F_CUST_GROUP", "SEL_BY_SERIALNUM_AND_USER", data);
    }

    public IDataset qryGrpMebUUinfo(IData data) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_UU", "SEL_SNA_BY_SNB_RTYPE_SX", data);
    }

    public IDataset qryMebProductList() throws Exception
    {
        return Dao.qryByCode("TD_B_PRODUCT", "SEL_MEB_PRODUCT_QUERY", null);
    }

    /*
     * 按用户手机号码
     */
    public IDataset qryPayBySerNum(IData param) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_SPECIALEPAY_2", param);
    }

    /*
     * vpmn名称和用户短号码
     */
    public IDataset qryPayByVpnNameAndShortCode(IData param) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_SPECIALEPAY_1", param);
    }

    /*
     * vpmn编号和用户短号码
     */
    public IDataset qryPayByVpnNoAndShortCode(IData param) throws Exception
    {
        return Dao.qryByCode("TD_S_COMMPARA", "SEL_SPECIALEPAY", param);
    }

    /*
     * 得到 默认vpn信息
     */
    public IDataset qryUserVpmnInfo(IData param) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_VPN", "SEL_BY_USERID", param);
    }

    /*
     * 得到 成员个性vpn信息 前置：USER_ID USER_ID_A
     */
    public IDataset qryVpmnMemInfo(IData dt) throws Exception
    {
        return Dao.qryByCode("TF_F_USER_VPN_MEB", "SEL_BY_USERID", dt);
    }

    /*
     * 查询此成员号码在这个集团客户下的某种UU关系 查询条件：CustId、UserIdB、relationTypeCode
     */
    public static IDataset qryRelationUUByCustIdAndUserIdB(IData data) throws Exception
    {
        return Dao.qryByCode("TF_F_RELATION_UU", "SEL_MEMUU_BY_CUSTID_USERB", data);
    }

    /*
     * 查询此成员号码在这个集团客户下的某种UU关系 查询条件：
     * UserIdB、relationTypeCode && CustId
     */
    public static IDataset qryRelationUUByUserIdB(IData data) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_RELATION_UU", "SEL_MEMUU_BY_CUSTID_USERB", data);
    }
}
