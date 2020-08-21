
package com.asiainfo.veris.crm.iorder.web.person.familytradeoptimal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FamilyRejectRemindSvcBusiNew extends PersonBasePage
{

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IData rtData = CSViewCall.call(this, "SS.FamilyCreateSVC.getAllMebList", pageData).getData(0);
        String sn = pageData.getString("SERIAL_NUMBER");

        IDataset mebList = rtData.getDataset("MEB_LIST");
        String mainFlag = rtData.getString("MAIN_FLAG");
        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData meb = mebList.getData(i);
            if ("true".equals(mainFlag))
            {
                meb.put("DISABLED", true);
            }
            else
            {
                if (sn.equals(meb.getString("SERIAL_NUMBER_B")))
                {
                    meb.put("DISABLED", false);
                }
                else
                {
                    meb.put("DISABLED", true);
                }
            }
        }

        // 如果是主号，则拒收类型为1（全网拒收）；如果是副号，则拒收类型为2（成员拒收）
        IData rejectMode = new DataMap();
        if ("true".equals(mainFlag))
        {
            rejectMode.put("MAIN_FLAG", false);
            rejectMode.put("REJECT_MODE", "1");
        }
        else
        {
            rejectMode.put("MAIN_FLAG", true);
            rejectMode.put("REJECT_MODE", "2");
        }

        setRejectMode(rejectMode);
        setViceInfos(mebList);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        pageData.put("IN_TAG", "0");// 0表示前台办理
        IDataset rtDataset = CSViewCall.call(this, "SS.FamilyRejectRemindSvcBusiRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setRejectMode(IData rejectMode);

    public abstract void setViceInfo(IData viceInfo);

    public abstract void setViceInfos(IDataset viceInfos);
}
