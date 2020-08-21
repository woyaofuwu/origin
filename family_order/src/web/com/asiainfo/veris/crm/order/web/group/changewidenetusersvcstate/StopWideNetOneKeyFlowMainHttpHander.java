
package com.asiainfo.veris.crm.order.web.group.changewidenetusersvcstate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class StopWideNetOneKeyFlowMainHttpHander extends CSBizHttpHandler
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
        svcData.put(Route.USER_EPARCHY_CODE, getData.getString("GRP_USER_EPARCHYCODE"));
        
        String stateFlag = getData.getString("OPER_TYPE");
        svcData.put("STATE_FLAG", stateFlag);
        
        IDataset retDataset;

        // 调用服务
        if("STOP".equals(stateFlag))
        {
             retDataset = CSViewCall.call(this, "SS.ChangeWideNetStateSVC.StopOneKey", svcData);
        }
        else
        {
             retDataset = CSViewCall.call(this, "SS.ChangeWideNetStateSVC.OpenOneKey", svcData);
        }

        // 设置返回值
        setAjax(retDataset);
    }
}
