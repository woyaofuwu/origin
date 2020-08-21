
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;
import com.asiainfo.veris.crm.order.soa.group.custmanager.CustManagerTJNumBean;

public class BatCustManagerTJNum extends GroupBatService
{
    public static final long serialVersionUID = 1L;

    public static final String SERVICE_NAME = "SS.CustManagerTJNumSVC.custManagerTJNum";

    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;

    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
        String codingStr = IDataUtil.getMandaData(batData, "CODING_STR");

        IData param = new DataMap(codingStr);

        String activeId = param.getString("ACTIVE_ID");
        String activeName = param.getString("ACTIVE_NAME");
        String tjNum = IDataUtil.getMandaData(batData, "SERIAL_NUMBER");
        String custManagerStaffid = IDataUtil.getMandaData(batData, "DATA1");

        IData Param = new DataMap();
        Param.put("MANAGER_STAFF_ID", custManagerStaffid);
        Param.put("TJNUMBER", tjNum);
        Param.put("ACTIVE_ID", activeId);
        Param.put("ACTIVE_NAME", activeName);
        Param.put("REMARK", "批量导入");

        // 验证客户经理
        IDataset custMgrInfos = CustManagerTJNumBean.checkCustManagerStaff(Param);
        IData custMgrData = custMgrInfos.getData(0);

        String staffName = custMgrData.getString("MANGER_NAME", "");
        Param.put("MANGER_NAME", staffName);
        // 验证号码是否为某一集团成员
        IDataset result = CustManagerTJNumBean.checkGbmBySerialNumber(Param);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CustException.CRM_CUST_999, tjNum);
        }

        // 验证号码推荐次数 当月同一号码系统限制只能录入2次机会
        IDataset countDataset = CustManagerTJNumBean.checkSerialNumberCont(Param);

        int size = countDataset.size();
        if (size >= 2)
        {
            CSAppException.apperr(CustException.CRM_CUST_1000, tjNum);
        }

        batData.put("TJ_INFO", Param);
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("TJ_INFO", batData.getData("TJ_INFO"));
    }

}
