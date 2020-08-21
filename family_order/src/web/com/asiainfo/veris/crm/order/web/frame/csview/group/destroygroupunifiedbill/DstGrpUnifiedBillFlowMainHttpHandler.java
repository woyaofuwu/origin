
package com.asiainfo.veris.crm.order.web.frame.csview.group.destroygroupunifiedbill;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class DstGrpUnifiedBillFlowMainHttpHandler extends CSBizHttpHandler
{
    /**
     * 提交方法
     * 
     * @throws Exception
     */
    public void submit() throws Exception
    {
        IData data = getData();
        IData input = new DataMap();
        input.put("USER_ID", data.getString("GRP_USER_ID"));
        input.put("REMARK", data.getString("pam_REMARK"));
        input.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER"));
        input.put("IF_CENTRETYPE", data.getString("IF_CENTRETYPE", "")); // 融合标识

        IDataset result = CSViewCall.call(this, "CS.DestroyGroupUnifiedBillSvc.destroyGroupMember", input);
        setAjax(result);
    }
}
