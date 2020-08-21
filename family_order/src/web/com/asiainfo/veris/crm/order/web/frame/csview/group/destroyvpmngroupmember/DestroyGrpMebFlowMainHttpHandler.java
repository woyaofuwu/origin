
package com.asiainfo.veris.crm.order.web.frame.csview.group.destroyvpmngroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;

public class DestroyGrpMebFlowMainHttpHandler extends CSBizHttpHandler
{

    /**
     * submit
     * 
     * @param cycle
     * @throws Exception
     */
    public void submit() throws Exception
    {
        IData data = getData();

        IData input = new DataMap();
        input.put("USER_ID", data.getString("GRP_USER_ID"));
        input.put("REMARK", data.getString("parm_REMARK"));
        input.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER"));
        // 是否退出对应的集团资料， 0 不退出 1 退出
        String joinIn = data.getString("parm_JOIN_IN");
        if (StringUtils.isNotBlank(joinIn))
            input.put("JOIN_IN", joinIn);
        // 业务是否预约 true 预约到账期末执行 false 非预约工单
        String ifBooking = data.getString("ifBooking");
        if (StringUtils.isNotBlank(ifBooking))
            input.put("IF_BOOKING", ifBooking);

        String brandCode = data.getString("GRP_BRAND_CODE", "");
        if (brandCode.equals(""))
        {
            brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, data.getString("GRP_PRODUCT_ID", ""));
        }

        if ("BOSG".equals(brandCode))
        {// BBOSS商品
            IDataset result = CSViewCall.call(this, "CS.DestroyBBossMemSVC.dealBBossMebBiz", input);
            setAjax(result);
            return;
        }

        IDataset result = CSViewCall.call(this, "CS.DestroyGroupMemberSvc.destroyGroupMember", input);
        setAjax(result);
    }
}
