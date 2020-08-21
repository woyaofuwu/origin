
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BatVpnMemUpToCountrywideTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // SS.VpnMemUpToCountrywideSVC.crtTrade服务的拦截器
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

        String addFlag = IDataUtil.chkParam(condData, "ADD_FLAG"); // 判断集团用户资料是否已存在801短号漫游服务，1：存在
        if ("1".equals(addFlag))
        {
            batData.put("TRADE_TYPE_CODE", "3035");
        }
        else
        {
            batData.put("TRADE_TYPE_CODE", "3038");
        }

        // 查询用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(serialNumber);

        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID")); // 集团userid
        svcData.put("PRODUCT_ID", IDataUtil.getMandaData(condData, "PRODUCT_ID")); // 集团产品ID
        svcData.put("SERIAL_NUMBER", IDataUtil.getMandaData(batData, "SERIAL_NUMBER")); // 成员sn
        svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("DISCNT", "[]"))); // 成员 选定跨省资费
        svcData.put("TRADE_TYPE_CODE", batData.getString("TRADE_TYPE_CODE"));
    }

}
