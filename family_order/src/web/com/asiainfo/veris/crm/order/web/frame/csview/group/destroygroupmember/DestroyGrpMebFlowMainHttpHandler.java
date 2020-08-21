
package com.asiainfo.veris.crm.order.web.frame.csview.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
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
        //add by chenzg@20180711 REQ201804280001集团合同管理界面优化需求
        input.put("MEB_VOUCHER_FILE_LIST", data.getString("MEB_VOUCHER_FILE_LIST", ""));
        input.put("AUDIT_STAFF_ID", data.getString("AUDIT_STAFF_ID", ""));
        // 是否退出对应的集团资料， 0 不退出 1 退出
        String joinIn = data.getString("parm_JOIN_IN");
        if (StringUtils.isNotBlank(joinIn))
            input.put("JOIN_IN", joinIn);
        // 业务是否预约 true 预约到账期末执行 false 非预约工单
        String ifBooking = data.getString("IF_BOOKING", "false");
        if (ifBooking.equals("true"))
            input.put("IF_BOOKING", ifBooking);

        String checkModeString = getData().getString("cond_CHECK_MODE");
        if (StringUtils.isNotEmpty(checkModeString))
        {
            input.put("CHECK_MODE", checkModeString);
        }
        
        //附件列表
        String mebFileShow = getData().getString("MEB_FILE_SHOW");
        if (StringUtils.isNotEmpty(mebFileShow) && StringUtils.equals("true", mebFileShow)){
        	input.put("MEB_FILE_SHOW", mebFileShow);
        	input.put("MEB_FILE_LIST", getData().getString("MEB_FILE_LIST",""));
        }
        
        String brandCode = data.getString("GRP_BRAND_CODE", "");
        if (brandCode.equals(""))
        {
            brandCode = ProductInfoIntfViewUtil.qryBrandCodeStrByProductId(this, data.getString("GRP_PRODUCT_ID"));
        }

        if ("BOSG".equals(brandCode))
        {// BBOSS商品
            input.put(Route.ROUTE_EPARCHY_CODE, getData().getString("MEB_EPARCHY_CODE"));
            IDataset result = CSViewCall.call(this, "CS.DestroyBBossMemSVC.dealBBossMebBiz", input);
            setAjax(result);
            return;
        }

        IDataset result = CSViewCall.call(this, "CS.DestroyGroupMemberSvc.destroyGroupMember", input);
        setAjax(result);
    }
}
