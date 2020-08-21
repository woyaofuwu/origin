
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;

public class BatVpnUpgradetoImsTrans implements ITrans
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

        String grpUserId = IDataUtil.chkParam(condData, "USER_ID");

        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(grpUserId);

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_011, grpUserId);
        }

        batData.put("GRP_EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID", "8001"));
        svcData.put(Route.USER_EPARCHY_CODE, batData.getString("GRP_EPARCHY_CODE"));
        svcData.put("ELEMENT_INFO", new DatasetList()); // 集团元素
        svcData.put("GRP_PACKAGE", IDataUtil.getMandaData(condData, "GRP_PACKAGE")); // 集团成员定制
    }

}
