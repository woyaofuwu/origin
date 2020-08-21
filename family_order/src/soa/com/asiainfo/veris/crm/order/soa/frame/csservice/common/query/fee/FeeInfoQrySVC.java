
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.fee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;

public class FeeInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    /**
     * 查询集团一次性费用(非移动云)
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryOneFeeList(IData data) throws Exception
    {
        String groupId = data.getString("GROUP_ID");
        String startTime = data.getString("START_TIME");
        String endTime = data.getString("END_TIME");

        // 查询集团客户信息
        IData custGrpData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(custGrpData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_131, groupId);
        }

        // 查询费用信息
        IDataset feeList = FeeInfoQry.qryOneOffFeeList(groupId, startTime, endTime);

        if (IDataUtil.isEmpty(data))
            return feeList;

        String custName = custGrpData.getString("CUST_NAME");

        String custManagerId = custGrpData.getString("CUST_MANAGER_ID");

        String custManagerName = ""; // 客户经理名称
        String serialNumber = ""; // 客户经理电话

        // 查询客户经理信息
        if (StringUtils.isNotBlank(custManagerId))
        {
            IDataset managerList = UStaffInfoQry.qryCustManagerStaffById(custManagerId);

            if (IDataUtil.isNotEmpty(managerList))
            {
                IData managerData = managerList.getData(0);

                custManagerName = managerData.getString("CUST_MANAGER_NAME");
                serialNumber = managerData.getString("SERIAL_NUMBER");
            }
        }

        for (int i = 0, row = feeList.size(); i < row; i++)
        {
            IData feeData = feeList.getData(i);

            feeData.put("GROUP_ID", groupId);
            feeData.put("CUST_NAME", custName);
            feeData.put("CUST_MANAGER_ID", custManagerId);
            feeData.put("CUST_MANAGER_NAME", custManagerName);
            feeData.put("SERIAL_NUMBER", serialNumber);
        }

        return feeList;
    }
    /**
     * 查询集团一次性费用(移动云)
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset qryOneFeeCloudList(IData data) throws Exception
    {
        String groupId = data.getString("GROUP_ID");
        String startTime = data.getString("START_TIME");
        String endTime = data.getString("END_TIME");

        // 查询集团客户信息
        IData custGrpData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(custGrpData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_131, groupId);
        }

        // 查询费用信息
        IDataset feeList = FeeInfoQry.qryOneOffFeeCloudList(groupId, startTime, endTime);

        if (IDataUtil.isEmpty(data))
            return feeList;

        String custName = custGrpData.getString("CUST_NAME");

        String custManagerId = custGrpData.getString("CUST_MANAGER_ID");

        String custManagerName = ""; // 客户经理名称
        String serialNumber = ""; // 客户经理电话

        // 查询客户经理信息
        if (StringUtils.isNotBlank(custManagerId))
        {
            IDataset managerList = UStaffInfoQry.qryCustManagerStaffById(custManagerId);

            if (IDataUtil.isNotEmpty(managerList))
            {
                IData managerData = managerList.getData(0);

                custManagerName = managerData.getString("CUST_MANAGER_NAME");
                serialNumber = managerData.getString("SERIAL_NUMBER");
            }
        }

        for (int i = 0, row = feeList.size(); i < row; i++)
        {
            IData feeData = feeList.getData(i);

            feeData.put("GROUP_ID", groupId);
            feeData.put("CUST_NAME", custName);
            feeData.put("CUST_MANAGER_ID", custManagerId);
            feeData.put("CUST_MANAGER_NAME", custManagerName);
            feeData.put("SERIAL_NUMBER", serialNumber);
        }

        return feeList;
    }
}
