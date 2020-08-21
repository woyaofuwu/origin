
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

public abstract class NewUserCreditClass extends PersonBasePage
{

    /**
     * 客户资料综合查询 - 用户信用等级信息查询
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
        setCond(condition);
        condition.put("IDTYPE", "0");
        IDataset output = new DatasetList();
        IData userInfo = new DataMap();
        if (StringUtils.isNotBlank(userId))
        {
            output = CSViewCall.call(this, "SS.GetUser360ViewSVC.getCreditInfo", condition);
            if (IDataUtil.isNotEmpty(output))
            {
                if (!"-1".equals(output.getData(0).getString("CREDIT_CLASS", "")) && !"".equals(output.getData(0).getString("CREDIT_CLASS", "")) && output.getData(0).getString("CREDIT_CLASS", "") != null)
                {
                	/**
                	 * REQ201608160006 将NGBOSS界面涉及“五星普”全改为“五星银”的需求 
                	 * @author zhuoyingzhi
                	 * 20160909
                	 * 修改为直接读取账务接口,显示星级名称
                	 */
                	userInfo.put("CREDIT_CLASS", output.getData(0).getString("CREDIT_CLASS_NAME", ""));
                }
                else
                {
                    userInfo.put("CREDIT_CLASS", "未评级");
                }
                userInfo.put("CREDIT_VALUE", output.getData(0).getString("CREDIT_VALUE", "0"));

            }
            setInfo(userInfo);
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
