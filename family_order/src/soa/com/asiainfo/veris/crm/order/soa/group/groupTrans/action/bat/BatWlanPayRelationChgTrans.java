
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;

public class BatWlanPayRelationChgTrans implements ITrans
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

        // 得到业务参数
        IDataUtil.chkParam(condData, "ACCT_ID");
        IDataUtil.chkParam(condData, "PAYITEM_CODE");
        IDataUtil.chkParam(condData, "USER_ID");
        IDataUtil.chkParam(batData, "SERIAL_NUMBER");
        IDataUtil.chkParam(condData, "OPER_TYPE");
        IDataUtil.chkParam(condData, "LIMIT_TYPE");
        IDataUtil.chkParam(condData, "LIMIT7");
        IDataUtil.chkParam(condData, "START_CYCLE");
        IDataUtil.chkParam(condData, "END_CYCLE");
        IDataUtil.chkParam(condData, "COMPLEMENT_TAG");

    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("GRP_SERIAL_NUMBER", condData.getString("SERIAL_NUMBER"));
        svcData.put("OPER_TYPE", condData.getString("OPER_TYPE"));
        svcData.put("PAYITEM_CODE", condData.getString("PAYITEM_CODE"));
        svcData.put("USER_ID", condData.getString("USER_ID")); // 集团USER_ID
        svcData.put("LIMIT_TYPE", condData.getString("LIMIT_TYPE"));
        svcData.put("LIMIT", condData.getString("LIMIT7"));
        svcData.put("START_CYCLE_ID", condData.getString("START_CYCLE"));
        svcData.put("END_CYCLE_ID", condData.getString("END_CYCLE"));
        svcData.put("COMPLEMENT_TAG", condData.getString("COMPLEMENT_TAG"));
        svcData.put("ACCT_ID", condData.getString("ACCT_ID"));

    }

}
