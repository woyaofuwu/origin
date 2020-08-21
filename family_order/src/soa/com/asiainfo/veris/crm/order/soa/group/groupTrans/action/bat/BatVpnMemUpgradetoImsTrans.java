
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BatVpnMemUpgradetoImsTrans implements ITrans
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

        IDataUtil.chkParam(condData, "USER_ID");
        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 查询用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(serialNumber);

        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        // 校验成员是否为VPMN成员
        String memUserId = userinfo.getString("USER_ID");

    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID")); // 集团userid
        svcData.put("PRODUCT_ID", IDataUtil.getMandaData(condData, "PRODUCT_ID")); // 集团产品ID
        svcData.put("SERIAL_NUMBER", IDataUtil.getMandaData(batData, "SERIAL_NUMBER")); // 成员sn
        svcData.put("MEM_DISCNT_CODE", IDataUtil.getMandaData(condData, "DISCNT")); // 成员 选定资费

    }

}
