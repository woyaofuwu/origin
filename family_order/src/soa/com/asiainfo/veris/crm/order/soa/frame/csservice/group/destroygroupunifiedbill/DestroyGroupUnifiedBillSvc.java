
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupunifiedbill;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class DestroyGroupUnifiedBillSvc extends GroupOrderService
{
    private static final long serialVersionUID = 1L;

    public IDataset destroyGroupMember(IData inparam) throws Exception
    {

        inparam.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
        inparam.put("RULE_BIZ_KIND_CODE", "GrpUnifiedBillDestroy");

        String serialNumber = inparam.getString("SERIAL_NUMBER");
        IData userInfos = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, RouteInfoQry.getEparchyCodeBySn(serialNumber));

        if (IDataUtil.isEmpty(userInfos))
        {
            CSAppException.apperr(BofException.CRM_BOF_002);
        }
        IData userInfo = userInfos;

        // 成员用户标识
        inparam.put("USER_ID_B", userInfo.getString("USER_ID"));
        inparam.put("ID", userInfo.getString("USER_ID"));
        inparam.put("BRAND_CODE_B", userInfo.getString("BRAND_CODE"));
        inparam.put("EPARCHY_CODE_B", userInfo.getString("EPARCHY_CODE"));

        IDataset obj = GrpInvoker.ivkProduct(inparam, BizCtrlType.DestroyUnifiedBill, "CreateClass");
        return obj;
    }

    @Override
    public void setIntercept() throws Exception
    {
        super.setIntercept();
        // 设置拦截器,根据不同的in_mode_code trans数据
        setMethodIntercept(DestroyGroupUnifiedBillTransData.class.getName());
    }
}
