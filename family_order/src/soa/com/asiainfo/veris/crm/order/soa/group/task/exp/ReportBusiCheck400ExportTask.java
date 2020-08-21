package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.workform.WorkFormBean;

public class ReportBusiCheck400ExportTask extends ExportTaskExecutor {

    @Override
    public IDataset executeExport(IData inParam, Pagination arg1) throws Exception {
        // TODO Auto-generated method stub
        IDataset eomsInfos = WorkFormBean.queryBusiInfos(inParam, arg1);
        if (IDataUtil.isNotEmpty(eomsInfos)) {
            for (Object object : eomsInfos) {
                IData busiInfo = (IData) object;
                IData groupInfo = UcaInfoQry.qryGrpInfoByGrpId(busiInfo.getString("GROUP_ID"));
                String custMgrId = groupInfo.getString("CUST_MANAGER_ID");
                if (StringUtils.isNotEmpty(custMgrId)) {
                    IDataset managerInfo = StaffInfoQry.qryCustManagerStaffById(custMgrId);
                    if (IDataUtil.isNotEmpty(managerInfo)) {
                        busiInfo.put("GROUP_MGR_CUST_NAME", managerInfo.getData(0).getString("CUST_MANAGER_NAME"));
                    }
                }
                busiInfo.put("CUST_MANAGER_ID", groupInfo.getString("CUST_MANAGER_ID"));
            }

        }
        return eomsInfos;
    }

}
