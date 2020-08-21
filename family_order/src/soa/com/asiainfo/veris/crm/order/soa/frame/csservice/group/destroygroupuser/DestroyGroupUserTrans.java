
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;

public class DestroyGroupUserTrans implements ITrans
{

    // 子类重载
    protected void addSubDataAfter(IData idata) throws Exception
    {
        idata.put("REASON_CODE", idata.getString("REASON_CODE"));
        idata.put("REMARK", idata.getString("REMARK"));
    }

    // 子类重载
    protected void addSubDataBefore(IData idata) throws Exception
    {
        // idata.put(Route.USER_EPARCHY_CODE,idata.getString("TRADE_EPARCHY_CODE"));
        idata.put("OPER_TYPE", BizCtrlType.DestoryUser);// 操作类型
    }

    protected void checkRequestData(IData idata) throws Exception
    {
        String userId = IDataUtil.chkParam(idata, "USER_ID");
        IData userInfos = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (IDataUtil.isEmpty(userInfos))
            CSAppException.apperr(BofException.CRM_BOF_011);

        // String productId = userInfos.getString("PRODUCT_ID");
        // GroupProductUtil.checkProductCanDo(idata.getString("OPER_TYPE"),productId);
    }

    private void transDestroyGroupUserRequestData(IData idata) throws Exception
    {
        checkRequestData(idata);

    }

    @Override
    public final void transRequestData(IData iData) throws Exception
    {
        addSubDataBefore(iData);
        transDestroyGroupUserRequestData(iData);
        addSubDataAfter(iData);
    }

}
