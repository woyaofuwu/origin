
package com.asiainfo.veris.crm.order.web.group.destroyonekey;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class DestroyOneKeyFlowMainHttpHander extends CSBizHttpHandler
{
    /**
     * 页面提交
     * 
     * @throws Exception
     */
    public void submit() throws Exception
    {
        IData getData = getData();

        // 服务数据
        IData svcData = new DataMap();
        svcData.put("GROUP_ID", getData.getString("GROUP_ID"));
        svcData.put("USER_ID", getData.getString("GRP_USER_ID"));
        svcData.put("PRODUCT_ID", getData.getString("GRP_PRODUCT_ID"));
        svcData.put("MEB_VOUCHER_FILE_LIST", getData.getString("MEB_VOUCHER_FILE_LIST", ""));
        svcData.put("AUDIT_STAFF_ID", getData.getString("AUDIT_STAFF_ID", ""));
        svcData.put(Route.USER_EPARCHY_CODE, getData.getString("GRP_USER_EPARCHYCODE"));

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.DestroyOneKeySVC.crtBat", svcData);

        // 设置返回值
        setAjax(retDataset);
    }
}
