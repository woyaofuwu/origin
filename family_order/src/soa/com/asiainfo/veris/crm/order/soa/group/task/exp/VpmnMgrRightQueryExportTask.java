
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custmanager.CustManagerInfoQry;

public class VpmnMgrRightQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        String custManagerId = inParam.getString("cond_STAFF_ID");
        String userProdCode = inParam.getString("cond_USER_PRODUCT_CODE");
        IData inparam = new DataMap();
        inparam.put("STAFF_ID", custManagerId);
        inparam.put("USER_PRODUCT_CODE", userProdCode);
        IDataset dataset = CustManagerInfoQry.qryVpmnStaffList(custManagerId, userProdCode, pg);
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_129);
        }
        else
        {
            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                IData data = dataset.getData(i);
                String staffId = data.getString("STAFF_ID", "");
                String rightCode = data.getString("RIGHT_CODE", "");
                String areaCode = data.getString("AREA_CODE", "");
                String startDate = data.getString("START_DATE", "");
                String endDate = data.getString("END_DATE", "");
                String departId = data.getString("DEPART_ID", "");
                String updateTime = data.getString("UPDATE_TIME", "");

                data.put("STAFF_NAME", !"".equals(staffId) ? UStaffInfoQry.getStaffNameByStaffId(staffId) : "");
                data.put("RIGHT_CODE", !"".equals(rightCode) ? StaticUtil.getStaticValue("VPN_MEMBER_STAFFRIGHT", rightCode) : "");
                data.put("AREA_NAME", !"".equals(areaCode) ? StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", areaCode) : "");
                data.put("START_DATE", !"".equals(startDate) ? SysDateMgr.decodeTimestamp(startDate, SysDateMgr.PATTERN_STAND) : "");
                data.put("END_DATE", !"".equals(endDate) ? SysDateMgr.decodeTimestamp(endDate, SysDateMgr.PATTERN_STAND) : "");
                data.put("UPDATE_TIME", !"".equals(updateTime) ? SysDateMgr.decodeTimestamp(updateTime, SysDateMgr.PATTERN_STAND) : "");
                data.put("DEPART_NAME", !"".equals(departId) ? UDepartInfoQry.getDepartNameByDepartId(departId) : "");

            }
        }
        return dataset;
    }

}
