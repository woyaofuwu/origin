
package com.asiainfo.veris.crm.order.web.person.bat;

import java.net.URLEncoder;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatTaskInfo extends PersonBasePage
{

    /**
     * @Function: queryBatTaskByPK
     * @Description: 根据主键查询批量批次信息
     * @param: @param cycle
     * @param: @throws Exception
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 4:55:32 PM May 25, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* May 25, 2013 tangxy v1.0.0 新建函数
     */
    public void queryBatTask(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData set = null;
        IDataset sets = CSViewCall.call(this, "CS.BatDealSVC.queryBatTask", data);
        if (sets.size() > 0)
        {
            set = sets.getData(0);
            String sBatchOperCode = set.getString("BATCH_OPER_CODE", "");
            IData param = new DataMap();
            param.put("BATCH_OPER_TYPE", sBatchOperCode);
            IDataset batchTypeinfos = CSViewCall.call(this, "CS.BatDealSVC.queryBatTypeByPK", param);

            if (IDataUtil.isNotEmpty(batchTypeinfos))
            {
                String batchOperName = batchTypeinfos.getData(0).getString("BATCH_OPER_NAME");
                String templateUrl = URLEncoder.encode(batchOperName, "utf-8");
                set.put("TEMPLATE_DATA_XLS", templateUrl + ".xls");
                set.put("DOWN_URL", "template/bat/" + sBatchOperCode + ".xls");
                set.put("TEMPLATE_FORMART_XML", "import/bat/" + sBatchOperCode + ".xml");
                set.put("IMPORT_CONTROL_ENABLE", "false");
            }
        }
        setTaskInfo(set);

    }

    public abstract void setTaskInfo(IData taskInfo);

}
