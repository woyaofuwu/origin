
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CustFamilyInfoQry
{
    /**
     * 查询家庭客户信息
     * 
     * @param param
     *            SERIAL_NUMBER,REMOVE_TAG
     * @return
     * @throws Exception
     */
    public static IDataset getFamilyInfo(IData param) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_FAMILY", "SEL_BY_HEADSN", param);
    }

    /**
     * 根据家庭归属业务区CITY_CODE查询家庭信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getFamilyInfoByCityCode(IData param, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_FAMILY", "SEL_BY_CITYCODE", param, pagination);
    }

    /**
     * 根据家庭客户CUST_ID查询家庭信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getFamilyInfoByCustId(String custId, Pagination page) throws Exception
    {
        IData param = new DataMap();
        param.put("HOME_CUST_ID", custId);
        return Dao.qryByCode("TF_F_CUST_FAMILY", "SEL_BY_CUSTID", param, page);
    }

    /**
     * 根据家庭地址HOME_ADDRESS查询家庭信息（模糊查询）
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getFamilyInfoByHomeAddress(IData param, Pagination page) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_FAMILY", "SEL_BY_HOMEADDRESS", param, page);
    }

    /**
     * 根据家庭名称HOME_NAME查询家庭信息（模糊查询）
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getFamilyInfoByHomeName(IData param, Pagination page) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_FAMILY", "SEL_BY_HOMENAME", param, page);
    }

    /**
     * 根据成员SERIAL_NUMBER，成员角色ROLE查询家庭客户资料
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getFamilyInfoBySnRole(IData param, Pagination page) throws Exception
    {

        return Dao.qryByCode("TF_F_CUST_FAMILY", "SEL_BY_SN_ROLE", param, page);
    }
}
