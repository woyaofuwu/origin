
package com.asiainfo.veris.crm.order.soa.group.common.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class StaffTicketInfoQry
{

    /**
     * 查询票据信息
     * 
     * @param staffId
     * @param ticketTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset qryStaffTicket(String staffId, String ticketTypeCode) throws Exception
    {
        IData param = new DataMap();

        param.put("STAFF_ID", staffId);
        param.put("TICKET_TYPE_CODE", ticketTypeCode);

        return Dao.qryByCode("TD_B_STAFF_TICKET", "SEL_BY_STAFFID", param);
    }

    /**
     * 更新票据信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static boolean updateStaffTicket(IData inParam) throws Exception
    {
        return Dao.update("TD_B_STAFF_TICKET", inParam);
    }

    /**
     * 插入票据信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static boolean insertStaffTicket(IData inParam) throws Exception
    {

        return Dao.insert("TD_B_STAFF_TICKET", inParam);
    }
}
