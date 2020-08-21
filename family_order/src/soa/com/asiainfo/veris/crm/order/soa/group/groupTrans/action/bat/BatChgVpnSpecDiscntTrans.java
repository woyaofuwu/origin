
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class BatChgVpnSpecDiscntTrans implements ITrans
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

        IDataUtil.chkParam(condData, "DISCNT_CODE");

        String memSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");

        String relationTypeCode = "20";
        String grpProductId = "8000";
        IDataset dsUu = RelaUUInfoQry.getRelationUusBySnBTypeCode(memSn, relationTypeCode);
        if (IDataUtil.isEmpty(dsUu))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_169, memSn);
        }
        String grpUIdA = "";
        for (int i = 0, size = dsUu.size(); i < size; i++)
        {
            IData dUu = dsUu.getData(i);
            String uIdA = dUu.getString("USER_ID_A");
            IDataset ds = UserProductInfoQry.getUserProductByUserIdProductId(uIdA, grpProductId);
            if (IDataUtil.isNotEmpty(ds))
            {
                grpUIdA = uIdA;
            }
        }
        if (StringUtils.isBlank(grpUIdA))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_169, memSn);
        }

        batData.put("GRP_USER_ID", grpUIdA);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("USER_ID", batData.getString("GRP_USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("PRODUCT_ID", "8000");
        svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "false"));
        svcData.put("DISCNT_CODE", condData.getString("DISCNT_CODE"));
    }

}
