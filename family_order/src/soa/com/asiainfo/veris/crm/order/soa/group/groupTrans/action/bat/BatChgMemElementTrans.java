
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;

public class BatChgMemElementTrans implements ITrans
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

        IDataUtil.chkParam(condData, "PRODUCT_ID");

        IDataUtil.chkParam(condData, "USER_ID");

        IDataUtil.chkParam(batData, "SERIAL_NUMBER");
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("MEM_ROLE_B", condData.getString("MEM_ROLE_B"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("ELEMENT_INFO", "[]")));
        svcData.put("RES_INFO", new DatasetList("[]"));
        svcData.put("PRODUCT_PARAM_INFO", new DatasetList("[]"));
        svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "false"));
        svcData.put("REMARK", batData.getString("REMARK", condData.getString("PRODUCT_ID") + "批量成员变更"));

        // 业务是否预约 true 预约 false 非预约工单
        svcData.put("IF_BOOKING", svcData.getString("IF_BOOKING", "false"));

    }

}
