
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.exitgrpmemberbox;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.custinfo.custgroupmember.CustGroupMemberIntfViewUtil;

public class ExitGrpMemberBoxHttpHandler extends CSBizHttpHandler
{

    public void qryExitGrpMemberBoxInfo() throws Exception
    {

        String productId = getData().getString("PRODUCT_ID", "");
        String SerialNumber = getData().getString("SERIAL_NUMBER", "");
        String eparchyCode = getData().getString("EPARCHY_CODE", "");

        IData result = new DataMap();
        if (!productId.equals("6200"))
        {
            result.put("HIDDEN_TAG", "true");
            setAjax(result);
            return;
        }

        // 6200的产品
        IDataset grpMemberInfo = CustGroupMemberIntfViewUtil.qryGrpMebsBySN(this, SerialNumber, eparchyCode);
        if (IDataUtil.isEmpty(grpMemberInfo))
        {
            result.put("HIDDEN_TAG", "true");
            setAjax(result);
            return;
        }

        result.put("HIDDEN_TAG", "false");
        result.put("DISABLED_TAG", "false");
        result.put("CEHCKED_TAG", "true");

        // if (productId.equals("8000") && !StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(),
        // "GROUPMENBER_VPMN_PRV")){
        // // 8000的需要增加权限判断,不允许选择退订
        // result.put("DISABLED_TAG", "true");
        // result.put("CEHCKED_TAG", "false");
        // setAjax(result);
        // return;
        // }

        setAjax(result);
    }

}
