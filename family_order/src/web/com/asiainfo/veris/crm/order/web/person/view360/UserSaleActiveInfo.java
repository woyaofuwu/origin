
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

public abstract class UserSaleActiveInfo extends PersonBasePage
{

    /**
     * 客户资料综合查询 - 营销活动信息查询
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
        condition.put("SEND_NUMBER", sn);
        condition.put("SERIAL_NUMBER", sn);
        condition.put("SIM_CHECK", data.getString("SIM_CHECK", ""));
        condition.put("SIM_NUMBER", data.getString("SIM_NUMBER", ""));
        condition.put("NORMAL_USER_CHECK", data.getString("NORMAL_USER_CHECK", ""));
        condition.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        condition.put("USER_ID", userId);
        setCondition(condition);
        data.put("PROCESS_TAG", "9");
        IDataset output = new DatasetList();
        String selectTag = data.getString("SelectTag", "0"); // 默认前台不选择seletTag不选择为0，如果前台选择所有记录seletTag为1
        if (StringUtils.isNotBlank(userId))
        {
            if ("1".equals(selectTag))
            {
                output = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserSaleActiveInfoAll", data);

            }
            else
            {
                output = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserSaleActiveInfo", data);
            }
            if (IDataUtil.isNotEmpty(output))
            {
                setInfos(output);
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

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long infosCount);
}
