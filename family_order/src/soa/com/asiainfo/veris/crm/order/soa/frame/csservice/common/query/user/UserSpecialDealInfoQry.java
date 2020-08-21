
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserSpecialDealInfoQry
{
    /**
     * 短信黑白名单查询
     * 
     * @author xieh
     * @param 查询所需参数
     * @param eparchyCode
     *            地州编码
     * @param Pagination
     * @return IDataset 短信黑白名单资料列表
     * @throws Exception
     */
    public static IDataset getTrashMsgUser(String serial_number, String id_type, String cust_type, String state_code, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serial_number);
        param.put("ID_TYPE", id_type);
        param.put("CUST_TYPE", cust_type);
        param.put("STATE_CODE", state_code);
        return Dao.qryByCode("TF_O_TRASHMSGUSER", "SEL_BY_ALL", param, pagination);
    }

    /**
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public IDataset getTrashMsgUserBySerialNumber(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_O_TRASHMSGUSER", "SEL_BY_VALID", param);
    }
}
