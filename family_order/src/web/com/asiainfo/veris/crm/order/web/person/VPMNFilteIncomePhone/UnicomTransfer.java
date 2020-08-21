
package com.asiainfo.veris.crm.order.web.person.VPMNFilteIncomePhone;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UnicomTransfer extends PersonBasePage
{
    /**
     * 页面初始化时，设置页面查询参数
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData commInfo = new DataMap();
        // 设置时间
        commInfo.put("START_DATE", SysDateMgr.getSysTime());
        commInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());

        commInfo.put("END_DATE", SysDateMgr.getAddMonthsNowday(3, SysDateMgr.getSysTime()));

        commInfo.put("PHONE_CODE_A", this.getVisit().getSerialNumber());
        setCondition(commInfo);
    }

    /**
     * 输入号码确定后，查询出三户资料外其它资料
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        // 前台参数
        IData inputData = this.getData();
        // 服务返回结果集
        IDataset dataSet = CSViewCall.call(this, "SS.UnicomTransferSVC.loadOtherInfo", inputData);

        if (IDataUtil.isEmpty(dataSet))
        {
            return;
        }
        IData dataMap = (IData) dataSet.get(0);

        IDataset resultList = new DatasetList();

        IData other = dataMap.getData("UNION_PHONES");
        if (IDataUtil.isNotEmpty(other))
        {
            // other.put("STATE", "1");
            resultList.add(other);
        }
        IDataset phoneBeginDs = dataMap.getDataset("PHONE_BEGIN_LIST");
        StringBuilder sb = new StringBuilder();
        // 获取他网号码段，拼接成130，131，135字符串传到前台
        for (int i = 0; i < phoneBeginDs.size(); i++)
        {
            IData data = phoneBeginDs.getData(i);
            String phone = data.getString("PARA_CODE1", "");
            if (phone.length() < 1)
            {
                continue;
            }
            sb.append(phone);
            if (i != phoneBeginDs.size() - 1)
            {
                sb.append(",");
            }
        }

        this.setPhoneBeginList(sb.toString());
        this.setOtherInfos(resultList);
        this.setCondition(inputData);
    }

    public abstract void setCondition(IData condition);

    public abstract void setOtherInfo(IData otherInfo);

    public abstract void setOtherInfos(IDataset otherInfos);

    public abstract void setPhoneBeginList(String phoneBeginList);

    /**
     * 前台提交修改数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitTrade(IRequestCycle cycle) throws Exception
    {
        // 前台参数
        IData inputData = this.getData();
        inputData.put("SERIAL_NUMBER", inputData.getString("AUTH_SERIAL_NUMBER"));
        inputData.putAll(new DataMap(inputData.getString("sumbmit_str")));
        IDataset dataSet = CSViewCall.call(this, "SS.UnicomTransferRegSVC.tradeReg", inputData);
        setAjax(dataSet);
    }

}
