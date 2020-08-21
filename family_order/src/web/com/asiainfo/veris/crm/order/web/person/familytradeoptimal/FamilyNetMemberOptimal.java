
package com.asiainfo.veris.crm.order.web.person.familytradeoptimal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FamilyNetMemberOptimal extends PersonBasePage
{

    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IData rtData = CSViewCall.call(this, "SS.FamilyCreateSVC.getViceMebList", pageData).getData(0);
        String sn = pageData.getString("SERIAL_NUMBER");

        IDataset mebList = rtData.getDataset("MEB_LIST");
        String mainFlag = rtData.getString("MAIN_FLAG");
        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData meb = mebList.getData(i);
            String isDelete = meb.getString("IS_DELETE");// 成员是否已经被删除
            if ("true".equals(mainFlag))
            {
                // 如果是主号并且副号已经被删除了，则副号不能再被删除
                /*if ("true".equals(isDelete))
                    meb.put("DISABLED", true);
                else
                    meb.put("DISABLED", false);*/
            }
            else
            {
                if (sn.equals(meb.getString("SERIAL_NUMBER_B")))
                {
                    meb.put("DISABLED", false);
                    // 如果是副号并且已经被删除了，则不能再被删除
                    /*if ("true".equals(isDelete))
                    {
                        meb.put("DISABLED", true);
                    }*/
                }
                else
                {
                    meb.put("DISABLED", true);
                }
            }
        }

        setViceInfos(mebList);
        
        if(IDataUtil.isNotEmpty(mebList)){
        	this.setAjax("VALIDE_MEBMER_NUMBER",String.valueOf(mebList.size()+1));
        }else{
        	this.setAjax("VALIDE_MEBMER_NUMBER","-1");
        }
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset rtDataset = CSViewCall.call(this, "SS.DelFamilyNetMemberRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }

    public abstract void setViceInfo(IData viceInfo);

    public abstract void setViceInfos(IDataset viceInfos);
}
