
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.group.common.query.StaffTicketInfoQry;

public class StaffTicketQryIntf
{

    /**
     * 查询票据信息接口
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset qryStaffTicket(IData inParam) throws Exception
    {
        String staffId = IDataUtil.chkParam(inParam, "STAFF_ID");
        String ticketTypeCode = IDataUtil.chkParam(inParam, "TICKET_TYPE_CODE");

        return StaffTicketInfoQry.qryStaffTicket(staffId, ticketTypeCode);
    }

    /**
     * 更新票据信息接口
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset updateStaffTicket(IData inParam) throws Exception
    {
        String staffId = IDataUtil.chkParam(inParam, "STAFF_ID");
        String ticketTypeCode = IDataUtil.chkParam(inParam, "TICKET_TYPE_CODE");
        String ticketId = IDataUtil.chkParam(inParam, "TICKET_ID");
        String taxNo = IDataUtil.chkParam(inParam, "TAX_NO");

        IData retData = new DataMap();

        StringBuilder sBuilder = new StringBuilder(50);
        sBuilder.append("[STAFF_ID=" + staffId + "]");
        sBuilder.append("[TICKET_TYPE_CODE=" + ticketTypeCode + "]");
        sBuilder.append("[TICKET_ID=" + ticketId + "]");
        sBuilder.append("[TAX_NO=" + taxNo + "]");

        IDataset staffTicketList = StaffTicketInfoQry.qryStaffTicket(staffId, ticketTypeCode);

        if (IDataUtil.isEmpty(staffTicketList)) // 插入票据信息
        {
            boolean isSuccess = StaffTicketInfoQry.insertStaffTicket(inParam);

            if (isSuccess)
            {
                retData.put("X_RESULT_CODE", "0");
                retData.put("X_RESULT_INFO", "插入票据信息成功" + sBuilder.toString());
            }
            else
            {
                retData.put("X_RESULT_CODE", "-1");
                retData.put("X_RESULT_INFO", "插入票据信息失败" + sBuilder.toString());
            }

        }
        else
        // 更新票据信息
        {
            boolean isSuccess = StaffTicketInfoQry.updateStaffTicket(inParam);
            if (isSuccess)
            {
                retData.put("X_RESULT_CODE", "0");
                retData.put("X_RESULT_INFO", "更新票据信息成功" + sBuilder.toString());
            }
            else
            {
                retData.put("X_RESULT_CODE", "-1");
                retData.put("X_RESULT_INFO", "更新票据信息失败" + sBuilder.toString());
            }
        }

        return IDataUtil.idToIds(retData);
    }

}
