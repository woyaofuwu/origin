/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

/**
 * @CREATED by gongp@2014-5-28 修改历史 Revision 2014-5-28 下午04:32:47
 */
public class FilteIncomePhoneDelTradeRegSVC extends OrderService
{
    private static final long serialVersionUID = 4789727504593969969L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "1302";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "1302";
    }

    @Override
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        String dealDataStr = input.getString("SN_DATASET");

        if ("2".equals(input.getString("OPER_TYPE")))
        {
            return;
        }

        if (!StringUtils.isBlank(dealDataStr) && dealDataStr.length() > 2)
        {

            IDataset dataset = new DatasetList(dealDataStr);

            for (int i = 1, size = dataset.size(); i < size; i++)
            {
                IData data = dataset.getData(i);

                data.put("OPEN_SMS", input.getString("OPEN_SMS"));
                data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
                data.put("OPER_TYPE", input.getString("OPER_TYPE"));

                CSAppCall.call("SS.FilteIncomePhoneDelTradeRegSVC.tradeReg", data);
            }
        }
    }
}
