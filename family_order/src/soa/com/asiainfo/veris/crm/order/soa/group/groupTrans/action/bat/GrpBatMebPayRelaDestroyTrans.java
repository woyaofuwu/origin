
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class GrpBatMebPayRelaDestroyTrans implements ITrans
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

        String serialNumber = IDataUtil.getMandaData(batData, "SERIAL_NUMBER"); // 成员服务号码

        IDataUtil.getMandaData(batData, "DATA1"); // 集团账户ID

        IDataUtil.getMandaData(batData, "DATA2"); // 集团用户ID

        IDataUtil.getMandaData(batData, "DATA3"); // 付费账目

        // 查询成员用户信息
        IData memuserinfo = UserInfoQry.getUserInfoBySN(serialNumber);

        if (IDataUtil.isEmpty(memuserinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());
        svcData.put("USER_ID", IDataUtil.getMandaData(batData, "DATA2")); // 集团用户ID
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER")); // 成员服务号码
        svcData.put("ACTION_FLAG", batData.getString("DATA4", "2"));// 默认本月底失效
        svcData.put("ACCT_ID", IDataUtil.getMandaData(batData, "DATA1")); // 集团账户ID
        svcData.put("PAY_ITEM_CODE", IDataUtil.getMandaData(batData, "DATA3")); // 付费项
    }

}
