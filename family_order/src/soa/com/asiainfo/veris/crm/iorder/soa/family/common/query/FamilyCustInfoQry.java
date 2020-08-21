package com.asiainfo.veris.crm.iorder.soa.family.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilySQLEnum;
import com.asiainfo.veris.crm.iorder.soa.family.common.dao.FamilyDao;

/**
 * @auther : lixx9
 * @createDate :  2020/7/21
 * @describe :家庭客户信息查询
 */
public class FamilyCustInfoQry {

    /**
     * @Description: 根据户主手机号码查询家庭客户信息
     * @Param: [input]
     * @return: com.ailk.common.data.IDataset
     * @Author: lixx9
     * @Date: 10:08
     */
    public static IDataset qryFamilyCustInfoBySn(String serialNumber) throws Exception
    {

        IData param = new DataMap();
        param.put(KeyConstants.SERIAL_NUMBER, serialNumber);

        return FamilyDao.qryByCode(FamilySQLEnum.FAMILY_CUST_INFO_BY_SN, param);
    }


    /**
     *
     * @param serialNumber
     * @param removeTag
     * @param headCustId
     * @return
     * @author zhangxi
     * @throws Exception
     */
    public static IDataset qryFamilyCustInfoBySnHeadCustIdRemoveTag(String serialNumber, String headCustId, String removeTag) throws Exception
    {

        IData param = new DataMap();

        param.put(KeyConstants.SERIAL_NUMBER, serialNumber);
        param.put(KeyConstants.HEAD_CUST_ID, headCustId);
        param.put(KeyConstants.REMOVE_TAG, removeTag);

        return FamilyDao.qryByCode(FamilySQLEnum.SEL_HOMECUST_BY_SN_HEAD_CUST_ID_REMOVE_TAG, param);
    }

    /** 
     * @Description: 根据家庭客户编码查询家庭客户资料
     * @Param: [homeCustId]
     * @return: com.ailk.common.data.IDataset
     * @Author: lixx9
     * @Date: 16:55
     */ 
    public static IDataset qryFamilyCustInfoByHomeCustId(String homeCustId) throws Exception{

        IData param = new DataMap();
        param.put(KeyConstants.HOME_CUST_ID,homeCustId);
        return FamilyDao.qryByCode(FamilySQLEnum.SEL_FAMILY_CUSTINFO_BY_HOME_CUST_ID, param);


    }


}
