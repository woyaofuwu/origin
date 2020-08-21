
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NpUserInfo extends PersonBasePage
{

    /**
     * 客户资料综合查询 - 携号专网信息查询
     * 
     * @param cycle
     * @throws Exception
     * @author huanghui@asiainfo.com
     * @date 2014-08-15
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData condition = new DataMap();
        String sn = data.getString("SERIAL_NUMBER", "");
        String userId = data.getString("USER_ID", "");
        condition.put("SERIAL_NUMBER", sn);
        condition.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        condition.put("USER_ID", userId);
        setCondition(condition);
        IDataset output = new DatasetList();
        if (StringUtils.isNotBlank(userId))
        {
            output = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryNpUserInfo", data);
            if (IDataUtil.isNotEmpty(output))
            {
                setInfo(output.getData(0));
            }
        }
    }

    public void setCommInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCond(data);
    }

    public abstract void setCond(IData cond);

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
