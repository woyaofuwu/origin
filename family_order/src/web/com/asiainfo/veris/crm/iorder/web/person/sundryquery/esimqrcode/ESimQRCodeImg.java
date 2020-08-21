
package com.asiainfo.veris.crm.iorder.web.person.sundryquery.esimqrcode;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;
import org.apache.tapestry.IRequestCycle;

public abstract class ESimQRCodeImg extends PersonQueryPage
{	
    public void imgInit(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        IData baseData = new DataMap();
        baseData.put("ACTIVATION_CODE", input.getString("ACTIVATION_CODE"));
        baseData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        baseData.put("BIZ_TYPE_CODE", input.getString("BIZ_TYPE_CODE"));
        // 数据回填
        this.setBase(baseData);
    }
    public abstract void setBase(IData cond);
}
