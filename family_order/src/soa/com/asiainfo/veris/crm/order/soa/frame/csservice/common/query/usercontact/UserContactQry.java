
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.usercontact;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqCustContact;

public class UserContactQry
{

    /**
     * 得到序列
     * 
     * @return
     * @throws Exception
     */
    public static String getSeqUserContact() throws Exception
    {
        return Dao.getSequence(SeqCustContact.class);
    }

    /**
     * 查询日志
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserContactLog(IData data) throws Exception
    {

        // 根据号码和最后的sesionID更新
        // IDataset logUserContact = dao.queryListByCodeCode("TF_B_CUST_CONTACT", "SEL_BY_NUM_SESSIONID", data);
        // //查询出用户信息
        IDataset logUserContact = Dao.qryByCode("TF_B_CUST_CONTACT", "SEL_BY_NUM_SESSIONIDNEW", data); // 查询出用户信息add
        // xuyt
        return logUserContact;
    }

    /**
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset getUserInfo(IData data) throws Exception
    {
        IDataset resUser = Dao.qryByCode("TF_F_USER", "SEL_BY_SN", data);
        return resUser;
    }

    /**
     * 保存日志
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean saveLog(IData data) throws Exception
    {
        return Dao.insert("TF_B_CUST_CONTACT", data);
    }

    /**
     * 修改日志
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static int updateUserContactLog(IData data) throws Exception
    {

        // 根据号码和最后的sesionID更新
        // IDataset logUserContact = dao.queryListByCodeCode("TF_B_CUST_CONTACT", "SEL_BY_NUM_SESSIONID", data);
        // //查询出用户信息
        int i = Dao.executeUpdateByCodeCode("TF_B_CUST_CONTACT", "UPD_TF_B_CUST_CONTACT", data);
        return i;
    }
}
