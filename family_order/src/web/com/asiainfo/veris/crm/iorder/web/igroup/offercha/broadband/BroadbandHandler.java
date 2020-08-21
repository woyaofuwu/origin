package com.asiainfo.veris.crm.iorder.web.igroup.offercha.broadband;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class BroadbandHandler extends BizHttpHandler
{
    public void filterAgentDepMgrs() throws Exception
    {

        IData pagedata = this.getData();

        IData param = new DataMap();
        IData resultData = new DataMap();
        param.put("DEPART_NAME", pagedata.getString("AGENT_DEPART_NAME"));
        IDataset managerInfos = CSViewCall.call(this, "CS.StaffInfoQrySVC.qryFilterAgentMgrDepartId", param);
        if (IDataUtil.isNotEmpty(managerInfos))
        {
            int len = managerInfos.size();
            for (int i = 0; i < len; i++)
            {
                IData infoData = managerInfos.getData(i);
                String rsrvStr1 = infoData.getString("DEPART_NAME");
                String rsrvStr2 = infoData.getString("DEPART_CODE");
                infoData.put("AGENT_DEPART_NAME", rsrvStr1 + "|" + rsrvStr2);
            }
        }


        resultData.put("DATA_VAL", managerInfos);

        String ajaxdatastr = resultData.getString("DATA_VAL", "");

        if (StringUtils.isNotBlank(ajaxdatastr))
        {
            this.setAjax(new DatasetList(ajaxdatastr));
        }
    }
   
}
