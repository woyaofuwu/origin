
package com.asiainfo.veris.crm.order.web.person.internetofthings;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 物联网测试期变更
 * 
 * @author xiekl
 */
public abstract class ChgTestPeriod extends PersonBasePage
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
            String startDate = testDiscnt.getString("START_DATE");
            String sysDate = SysDateMgr.getSysTime();

            String endTime1 = SysDateMgr.getAddMonthsLastDayNoEnv(1,startDate);
            String endTime2 = SysDateMgr.getAddMonthsLastDayNoEnv(2,startDate);
            String endTime3 = SysDateMgr.getAddMonthsLastDayNoEnv(3,startDate);

            if (endTime1.compareTo(sysDate) > 0)
            {
                IData testingLastMonth1 = new DataMap();
                testingLastMonth1.put("DATA_ID", endTime1);
                testingLastMonth1.put("DATA_NAME", "一个月测试期，结束时间：" + endTime1);
                testDiscntEndList.add(testingLastMonth1);
            }

            if (endTime2.compareTo(sysDate) > 0)
            {
                IData testingLastMonth2 = new DataMap();
                testingLastMonth2.put("DATA_ID", endTime2);
                testingLastMonth2.put("DATA_NAME", "二个月测试期，结束时间：" + endTime2);
                testDiscntEndList.add(testingLastMonth2);
            }

            if (endTime3.compareTo(sysDate) > 0)
            {
                IData testingLastMonth3 = new DataMap();
                testingLastMonth3.put("DATA_ID", endTime3);
                testingLastMonth3.put("DATA_NAME", "三个月测试期，结束时间：" + endTime3);
                testDiscntEndList.add(testingLastMonth3);
            }
            if(testDiscntEndList.size()<1)
            {
            	ajax.put("ERROR_DESC", "该用户订购的测试期优惠已经超过三个月，不能办理测试期变更业务");
                this.setAjax(ajax);
                return;
            }

            setInfos(testDiscntEndList);
            setInfo(testDiscnt);

        }
        else
        {
            ajax.put("ERROR_DESC", "该用户没有订购测试期优惠，不能办理测试期变更业务");
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
        IDataset result = CSViewCall.call(this, "SS.ChgTestPeriodRegSVC.tradeReg", param);
        this.setAjax(result);
    }
}
