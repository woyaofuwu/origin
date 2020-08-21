
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class GroupBatVpmnUserExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        String serialNumber = inParam.getString("cond_SERIAL_NUMBER");

        if (StringUtils.isBlank(serialNumber))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_115);
        }

        // 查询集团用户信息
        IData userData = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);

        if (IDataUtil.isEmpty(userData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_315, serialNumber);
        }

        // 非VPMN集团不能办理此业务
        if (!"VPMN".equals(userData.getString("BRAND_CODE")) || !"8000".equals(userData.getString("PRODUCT_ID")))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_15, serialNumber);
        }

        String custId = userData.getString("CUST_ID", "");
        String userId = userData.getString("USER_ID", "");

        // 查询集团客户信息
        IData custData = UcaInfoQry.qryGrpInfoByCustId(custId);

        if (IDataUtil.isEmpty(custData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_791, custId);
        }

        // 查询UU信息
        IDataset relaList = RelaUUInfoQry.getRelaUUInfoByRolForGrp(userId, "40", pg);

        if (IDataUtil.isEmpty(relaList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_48);
        }

        String userIdA = relaList.getData(0).getString("USER_ID_A", "");

        IDataset subRelaList = RelaUUInfoQry.getRelaUUInfoByUserIdaForGrp(userIdA, "40", pg);

        if (IDataUtil.isEmpty(subRelaList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_119);
        }

        return subRelaList;
    }

}
