
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.hintinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class HintInfo extends PersonBasePage
{

    /**
     * 小栏框信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void initHintInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData params = new DataMap(data.getString("HINT_INFO")); // 原传入的参数名叫SERIAL_NUMBER

        IDataset infos = CSViewCall.call(this, "CS.HintInfoSVC.getAllHintInfos", params);

        setInfos(infos);

        /*
         * IDataset dataset = new DatasetList(); String hintInfo1 =
         * "开户日期:1998-03-19 08:33:23~~已经开户:5643天~~用户品牌:全球通测试~~当前产品:全球通奥运套餐~~"; IData temp = new DataMap();
         * temp.put("KEY", "HINT_INFO1"); temp.put("VALUE", hintInfo1); dataset.add(temp); String hintInfo2 =
         * "级别：金卡~~使用人：凡人修仙~~卡号：20130813~~客户经理：宋小兵~~客户经理联系电话：13755009543~~"; IData temp2 = new DataMap();
         * temp2.put("KEY", "HINT_INFO2"); temp2.put("VALUE", hintInfo2); dataset.add(temp2); String strR8Msg =
         * "用户目前【是】高级付费关系客户"; IData tmpR10 = new DataMap(); tmpR10.put("KEY", "ROAM_INFO10"); tmpR10.put("VALUE",
         * strR8Msg); dataset.add(tmpR10); setInfos(dataset);
         */
    }

    public abstract void setInfos(IDataset infos);
}
