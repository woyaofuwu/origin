
package com.asiainfo.veris.crm.order.web.person.internetofthings;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class TestTranSilence extends PersonBasePage
{

    /**
     * 初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData ajax = new DataMap();
        IDataset testDiscntEndList = new DatasetList();// 保存可选择的测试期结束时间
        IData param = this.getData();
        IDataset result = CSViewCall.call(this, "SS.IOTQuerySVC.queryUserTestValidDiscnt", param);

        if (result != null && !result.isEmpty())
        {
            IData testDiscnt = result.getData(0);
            testDiscnt.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));

            IData testingLastMonth1 = new DataMap();
            testingLastMonth1.put("DATA_ID", SysDateMgr.getSysDate("yyyy-MM-dd hh:mm:ss"));
            testingLastMonth1.put("DATA_NAME", "测试期立即截止");
            testDiscntEndList.add(testingLastMonth1);

            setInfos(testDiscntEndList);
            setInfo(testDiscnt);

        }
        else
        {
            ajax.put("ERROR_DESC", "该用户没有订购测试期优惠，不能办理主动测试期转沉默业务");
            this.setAjax(ajax);
        }

    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    /**
     * 提交生成订单
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitTrade(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        IDataset result = CSViewCall.call(this, "SS.TestTransSilenceSVC.tradeReg", param);
        this.setAjax(result);
    }
}
