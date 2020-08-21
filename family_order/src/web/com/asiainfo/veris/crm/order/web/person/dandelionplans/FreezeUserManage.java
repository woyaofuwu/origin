/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.dandelionplans;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FreezeUserManage extends PersonBasePage
{
    public void createPhone(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData();
        String msisdn = condParams.getString("SERIAL_NUMBER");
        String numberType = condParams.getString("NUMBER_TYPE");
        String startDate = condParams.getString("START_DATE");
        String endDate = condParams.getString("END_DATE");
        IData params = new DataMap();
        int ret = 999;
        IDataset results = CSViewCall.call(this, "SS.FreezeUserManageSVC.createPhone", condParams);
        if (IDataUtil.isNotEmpty(results))
        {
            ret = Integer.parseInt(results.getData(0).getString("STATUS", "999"));
        }
        if (ret == 1)
        {
            setTipInfo("服务号码【" + msisdn + "】已经销户，不能再新增！");
            this.setAjax("1111", "111111111");
        }
        else if (ret == 2)
        {
            setTipInfo("服务号码【" + msisdn + "】已经存在生效的记录，不能再新增！");
            this.setAjax("2222", "22222");
        }
        else if (ret == 3)
        {
            setTipInfo("服务号码【" + msisdn + "】冻结名单当天已经新增过，不能再新增，请选别的开始时间再试！");
            this.setAjax("3333", "33333");
        }
        else if (ret == 0)
        {
            setTipInfo("新增冻结或者黑名单成功！服务号码：[" + msisdn + "]");
            this.setAjax("4444", "444444");
        }
        else
        {
            setTipInfo("新增失败！");
            this.setAjax("5555", "55555");
        }
    }

    public void queryUserFreeze(IRequestCycle cycle) throws Exception
    {
        IData condParams = getData("cond", true);
        IDataOutput infos = CSViewCall.callPage(this, "SS.FreezeUserManageSVC.queryUserFreeze", condParams, getPagination("page"));
        this.setInfos(infos.getData());
        // this.setAjax(infos.getData());
        this.setListCount(infos.getDataCount());
    }

    public abstract void setCommInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setListCount(long count);

    public abstract void setTipInfo(String tipInfo);
}
