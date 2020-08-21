
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BatGrpOutNetMemCreateTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("MEM_ROLE_B", condData.getString("MEM_ROLE_B"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("ELEMENT_INFO")));
        svcData.put("RES_INFO", new DatasetList("[]"));
        svcData.put("PRODUCT_PARAM_INFO", new DatasetList("[]"));
        svcData.put("PLAN_TYPE_CODE", condData.getString("PLAN_TYPE"));
        svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "true"));
        svcData.put("REMARK", batData.getString("REMARK", "集团成员批量新增（专网网外号码)"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE"));
        svcData.put("IS_OUT_NET", batData.getBoolean("IS_OUT_NET", false)); // 网外号码标识
        svcData.put("MEB_USER_INFO", batData.getData("MEB_USER_INFO"));
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        IDataUtil.chkParam(condData, "MEM_ROLE_B");

        IDataUtil.chkParam(condData, "PRODUCT_ID");

        IDataUtil.chkParam(condData, "ELEMENT_INFO");

        IDataUtil.chkParam(condData, "USER_ID");

        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        if (serialNumber.length() != 12)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1081, serialNumber); // 网外服务号码【"+serialNumber+"】网外号码必须为12位！
        }

        if (!serialNumber.startsWith("0898"))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1080, serialNumber); // 网外服务号码【"+serialNumber+"】不是以0898开头，业务不能继续！
        }

        IData userInfoData = UserInfoQry.getMebUserInfoBySN(serialNumber);

        if (IDataUtil.isEmpty(userInfoData))
        {

            // 网外号码校验先不加
            batData.put("IS_OUT_NET", true);

            IData meb_user_info = new DataMap();

            meb_user_info.put("PRODUCT_ID", "4444"); // 默认开4444产品
            meb_user_info.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
            meb_user_info.put("EPARCHY_CODE", batData.getString("EPARCHY_CODE"));

            batData.put("MEB_USER_INFO", meb_user_info);
        }
        else
        {

            CSAppException.apperr(CrmUserException.CRM_USER_1098, serialNumber); // 用户资料表里已存在相同的网外号码！
        }
    }

}
