
package com.asiainfo.veris.crm.iorder.web.person.sundryquery.esimqrcode;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;
import org.apache.tapestry.IRequestCycle;

public abstract class ESimQRCode extends PersonQueryPage
{	
    /**
     * 一号一终端二维码查询
     * @param cycle
     * @throws Exception
     */
    public void queryESimQrCode(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        IData param = new DataMap();
        param.put("BIZ_TYPE", cond.getString("BIZ_TYPE"));
        param.put("SERIAL_NUMBER", cond.getString("SERIAL_NUMBER"));
        IDataset results = CSViewCall.call(this, "SS.ESimQrCodeSVC.queryQRCodeInfo", param);

        setAjax(results);
        setQrCodeInfos(results);
        setNumberCount(results.size());
    }
    
    public abstract void setNumberCount(long count);

    public abstract void setQrCodeInfos(IDataset infos);
}
