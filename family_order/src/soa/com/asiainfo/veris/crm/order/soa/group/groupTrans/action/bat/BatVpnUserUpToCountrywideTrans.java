
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;

public class BatVpnUserUpToCountrywideTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // SS.VpnUserUpToCountrywideSVC.crtTrade 服务的拦截器
        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        String grpUserId = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(grpUserId);

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_011, grpUserId);
        }

    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());
        IDataset grpPackages = condData.getDataset("GRP_PACKAGE", null);
        svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));
        svcData.put("PRODUCT_ID", IDataUtil.getMandaData(condData, "PRODUCT_ID"));
        svcData.put("productParam", IDataUtil.getMandaData(condData, "productParam"));
        svcData.put("GRP_PACKAGE_INFO", grpPackages); // 集团成员定制
        svcData.put("OLD_801_FLAG", IDataUtil.getMandaData(condData, "OLD_801_FLAG"));
        svcData.put("HAS_DEL_801", IDataUtil.getMandaData(condData, "HAS_DEL_801"));
        svcData.put("VPN_SCARE_CODE", IDataUtil.getMandaData(condData, "VPN_SCARE_CODE"));
        svcData.put("HAS_VPN_SCARE", IDataUtil.getMandaData(condData, "HAS_VPN_SCARE"));
        svcData.put("HAS_ADD_801", IDataUtil.getMandaData(condData, "HAS_ADD_801"));
    }

}
