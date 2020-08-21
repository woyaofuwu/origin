
package com.asiainfo.veris.crm.order.web.group.param.workphone;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class MemParamInfo extends IProductParamDynamic
{

    /**
     * 成员变更初始化
     * 
     * @author sht
     */
    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);
        IData parainfo = result.getData("PARAM_INFO");

        IDataset otherDataset = CSViewCall.call(bp, "CS.UserDiscntInfoQrySVC.queryUserDiscntToCommparaByUID", data);

        if (IDataUtil.isNotEmpty(otherDataset))
        {
            IDataset dataset = new DatasetList();
            for (int i = 0; i < otherDataset.size(); i++)
            {
                IData otherInfo = otherDataset.getData(i);
                IData otherData = new DataMap();
                otherData.put("pam_DISCNT_CODE", otherInfo.getString("DISCNT_CODE", ""));
                otherData.put("pam_DISCNT_NAME", otherInfo.getString("DISCNT_NAME", ""));
                otherData.put("pam_CDISCNT_CODE", otherInfo.getString("PARA_CODE1", ""));
                otherData.put("pam_START_DATE", otherInfo.getString("START_DATE", ""));
                otherData.put("pam_END_DATE", otherInfo.getString("END_DATE", ""));
                dataset.add(otherData);
            }
            parainfo.put("userParamInfos", dataset);
        }

        return result;
    }
    
    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        IData parainfo = result.getData("PARAM_INFO");

        IDataset otherDataset = CSViewCall.call(bp, "CS.UserDiscntInfoQrySVC.queryUserDiscntToCommparaByUID", data);

        if (IDataUtil.isNotEmpty(otherDataset))
        {
            IDataset dataset = new DatasetList();
            for (int i = 0; i < otherDataset.size(); i++)
            {
                IData otherInfo = otherDataset.getData(i);
                IData otherData = new DataMap();
                otherData.put("pam_DISCNT_CODE", otherInfo.getString("DISCNT_CODE", ""));
                otherData.put("pam_DISCNT_NAME", otherInfo.getString("DISCNT_NAME", ""));
                otherData.put("pam_CDISCNT_CODE", otherInfo.getString("PARA_CODE1", ""));
                otherData.put("pam_START_DATE", otherInfo.getString("START_DATE", ""));
                otherData.put("pam_END_DATE", otherInfo.getString("END_DATE", ""));
                dataset.add(otherData);
            }
            parainfo.put("userParamInfos", dataset);
        }

        return result;
    }
}
