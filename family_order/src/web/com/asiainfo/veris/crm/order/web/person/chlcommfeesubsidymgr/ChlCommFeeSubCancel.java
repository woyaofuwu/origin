/**
 * 
 */

package com.asiainfo.veris.crm.order.web.person.chlcommfeesubsidymgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * @CREATED by gongp@2014-4-16 修改历史 Revision 2014-4-16 上午09:38:30
 */
public abstract class ChlCommFeeSubCancel extends ChlCommFeeSubsidyMgr
{

    /**
     * @param cycle
     * @throws Exception
     * @CREATE BY GONGP@2014-4-16
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {

        IData data = getData();

        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }

        IDataset dataset = CSViewCall.call(this, "SS.ChlCommFeeSubCancelRegSVC.tradeReg", data);
        setAjax(dataset);

    }

}
