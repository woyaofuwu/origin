
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.group.custmanager.CustManagerTJNumBean;

public class BatCustManagerTJNumTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {

        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        String tjNum = IDataUtil.getMandaData(batData, "SERIAL_NUMBER");
        String custManagerStaffid = IDataUtil.getMandaData(batData, "DATA1");

        IData Param = new DataMap();
        Param.put("MANAGER_STAFF_ID", custManagerStaffid);
        Param.put("TJNUMBER", tjNum);
        Param.put("ACTIVE_ID", condData.getString("ACTIVE_ID"));
        Param.put("ACTIVE_NAME", condData.getString("ACTIVE_NAME"));
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

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        svcData.put("TJ_INFO", batData.getData("TJ_INFO"));
    }

}
