
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;

public class BatMebChgRingTrans implements ITrans
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

        condData.put("PRODUCT_ID", "6200"); // j2ee 测试写死

        String memSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 查询用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(memSn);

        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        // 校验成员是否为彩铃成员
        String memUserId = userinfo.getString("USER_ID");
        String relaTypeCode = "26"; // j2ee 测试写死

        IDataset uuInfos = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(memUserId, relaTypeCode);
        if (IDataUtil.isEmpty(uuInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_708, memSn);
        }
        String grpUserId = uuInfos.getData(0).getString("USER_ID_A");

        condData.put("USER_ID", grpUserId);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String productId = condData.getString("PRODUCT_ID");

        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("PRODUCT_ID", productId);
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));

        // 构建产品参数信息
        IData productParam = new DataMap();
        productParam.put("CANCEL_LING", IDataUtil.getMandaData(condData, "cancelLing"));
        IDataset productParamDataset = new DatasetList();

        GroupBatTransUtil.buildProductParam(productId, productParam, productParamDataset);

        svcData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }

}
