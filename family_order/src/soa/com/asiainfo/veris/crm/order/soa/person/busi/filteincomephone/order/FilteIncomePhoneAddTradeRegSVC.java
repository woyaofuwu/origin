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
 * @CREATED by gongp@2014-5-4 修改历史 Revision 2014-5-4 上午10:57:02
 */
public class FilteIncomePhoneAddTradeRegSVC extends OrderService
{
    private static final long serialVersionUID = 1461280827210505966L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "1301";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "1301";
    }

    @Override
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        String dealDataStr = input.getString("SN_DATASET");

        if (!StringUtils.isBlank(dealDataStr) && dealDataStr.length() > 2)
        {

            IDataset dataset = new DatasetList(dealDataStr);

            for (int i = 1, size = dataset.size(); i < size; i++)
            {
                IData data = dataset.getData(i);

                data.put("OPEN_SMS", input.getString("OPEN_SMS"));
                data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
                data.put("OPER_TYPE", input.getString("OPER_TYPE"));

                CSAppCall.call("SS.FilteIncomePhoneAddTradeRegSVC.tradeReg", data);
            }
        }
    }
}
