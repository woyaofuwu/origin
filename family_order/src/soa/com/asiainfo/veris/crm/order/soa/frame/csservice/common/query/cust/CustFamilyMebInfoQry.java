
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class CustFamilyMebInfoQry
{

    /**
     * 查询某家庭的所有成员信息
     * 
     * @param param
     *            HOME_CUST_ID
     * @return
     * @throws Exception
     */
    public static IDataset getAllFamilyMember(String homeCustId) throws Exception
    {
        IData param = new DataMap();
        param.put("HOME_CUST_ID", homeCustId);
        return Dao.qryByCode("TF_F_CUST_FAMILYMEB", "SEL_BY_HOMECUSTID", param);
    }

    /**
     * 查询某家庭的所有成员信息
     * 
     * @param param
     *            HOME_CUST_ID
     * @return
     * @throws Exception
     */
    public static IDataset getFamilyMem(String memberCUstId, String removeTag) throws Exception
    {
        IData param = new DataMap();
        param.put("MEMBER_CUST_ID", memberCUstId);
        param.put("REMOVE_TAG", removeTag);
        return Dao.qryByCode("TF_F_CUST_FAMILYMEB", "SEL_BY_CUSTID", param);
    }
}
