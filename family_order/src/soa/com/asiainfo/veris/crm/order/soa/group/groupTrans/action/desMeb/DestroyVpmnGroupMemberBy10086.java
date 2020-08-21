
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.desMeb;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMemberTrans;

public class DestroyVpmnGroupMemberBy10086 extends DestroyGroupMemberTrans
{
    protected void addSubDataBefore(IData idata) throws Exception
    {
        super.addSubDataBefore(idata);

        transDestroyVpmnGroupMemberRequestData(idata);
    }

    private void transDestroyVpmnGroupMemberRequestData(IData idata) throws Exception
    {
        checkRequestData(idata);

        String groupId = idata.getString("GROUP_ID");
        String sn = IDataUtil.chkParam(idata, "SERIAL_NUMBER");
        if (!"DistroyGroupIdVPMN".equals(groupId))
            CSAppException.apperr(GrpException.CRM_GRP_713, "GROUP_ID值不等于DistroyGroupIdVPMN,请检查输入参数!");
        IDataset idatas = RelaUUInfoQry.queryVpmnRelaBySn(sn);
        if (IDataUtil.isEmpty(idatas))
            CSAppException.apperr(GrpException.CRM_GRP_713, "该手机用户号码 " + sn + " 不是集团VPMN用户 ！");
        IData vpmnData = idatas.getData(0);
        String userIdA = vpmnData.getString("USER_ID_A");
        idata.put("USER_ID", userIdA);
    }

    public void checkRequestData(IData idata) throws Exception
    {
        IDataUtil.chkParam(idata, "GROUP_ID");// 客服必须传GROUP_ID

    }
}
