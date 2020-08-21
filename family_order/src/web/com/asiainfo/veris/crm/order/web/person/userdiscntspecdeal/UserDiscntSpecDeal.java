
package com.asiainfo.veris.crm.order.web.person.userdiscntspecdeal;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: UserDiscntSpecDeal.java
 * @Description: 用户优惠特殊处理View
 * @version: v1.0.0
 * @author: maoke
 * @date: May 26, 2014 2:48:21 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* May 26, 2014 maoke v1.0.0 修改原因
 */
public abstract class UserDiscntSpecDeal extends PersonBasePage
{
    /**
     * @Description: 获取用户优惠列表
     * @param cycle
     * @throws Exception
     * @author: maoke
     * @date: May 26, 2014 3:26:28 PM
     */
    public void getUserDiscntList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();

        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));

        IData discntData = CSViewCall.call(this, "SS.UserDiscntSpecDealSVC.getUserDiscntList", data).getData(0);

        setChooseType(setChooseTypeData());

        setDiscnt(discntData.getDataset("DISCNT_LIST"));

        setAjax(discntData);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        CSViewCall.call(this, "SS.UserDiscntSpecDealSVC.isExistsMainDiscntPriv", data);

        String serialNumber = data.getString("SERIAL_NUMBER");

        IDataset specDiscntList = new DatasetList(data.getString("DISCNT_LIST"));

        IDataset otherDealTrade = new DatasetList();

        if (IDataUtil.isNotEmpty(specDiscntList))
        {
            IData input = specDiscntList.getData(0);
            input.put("SERIAL_NUMBER", serialNumber);

            int size = specDiscntList.size();

            if (size > 1)
            {
                for (int i = 1; i < size; i++)
                {
                    IData otherData = specDiscntList.getData(i);
                    otherData.put("SERIAL_NUMBER", serialNumber);

                    otherDealTrade.add(otherData);
                }

                // otherTradeDeal使用
                input.put("OTHER_DISCNT_LIST", otherDealTrade);
            }

            IDataset result = CSViewCall.call(this, "SS.UserDiscntSpecDealRegSVC.tradeReg", input);

            this.setAjax(result);
        }
    }

    public abstract void setChooseType(IDataset chooseType);

    /**
     * @Description：选择方式
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 26, 2014 7:43:02 PM
     */
    public IDataset setChooseTypeData() throws Exception
    {
        IDataset chooseType = new DatasetList();

        IData data1 = new DataMap();
        data1.put("DATA_ID", "1");
        data1.put("DATA_NAME", "上月底");

        IData data2 = new DataMap();
        data2.put("DATA_ID", "2");
        data2.put("DATA_NAME", "当前");

        IData data3 = new DataMap();
        data3.put("DATA_ID", "3");
        data3.put("DATA_NAME", "本月底");

        IData data4 = new DataMap();
        data4.put("DATA_ID", "4");
        data4.put("DATA_NAME", "永久");

        chooseType.add(data1);
        chooseType.add(data2);
        chooseType.add(data3);
        chooseType.add(data4);

        return chooseType;
    }

    public abstract void setDiscnt(IDataset discntDataset);
}
