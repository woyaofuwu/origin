
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class DestroyGroupMemberTrans implements ITrans
{
    // 子类重载
    protected void addSubDataAfter(IData idata) throws Exception
    {

    }

    // 子类重载
    protected void addSubDataBefore(IData idata) throws Exception
    {
        idata.put("USER_ID", idata.getString("USER_ID_A", idata.getString("USER_ID")));
        idata.remove("USER_ID_A");
    }

    protected void checkRequestData(IData idata) throws Exception
    {
        IDataUtil.chkParam(idata, "SERIAL_NUMBER");// 成员手机号码
        IDataUtil.chkParam(idata, "USER_ID");
    }

    private void transDestroyGroupMemberRequestData(IData idata) throws Exception
    {
        // 根据集团服务号码查询集团用户ID
        String serialNumberA = idata.getString("SERIAL_NUMBER_A", "");

        if (StringUtils.isNotBlank(serialNumberA))
        {
            IData grpUserData = UcaInfoQry.qryUserInfoBySnForGrp(serialNumberA);

            if (IDataUtil.isEmpty(grpUserData))
            {
                CSAppException.apperr(GrpException.CRM_GRP_197, serialNumberA);
            }

            idata.put("USER_ID", grpUserData.getString("USER_ID"));
        }

        checkRequestData(idata);
    }

    @Override
    public final void transRequestData(IData iData) throws Exception
    {
        addSubDataBefore(iData);
        transDestroyGroupMemberRequestData(iData);
        addSubDataAfter(iData);
    }
}
