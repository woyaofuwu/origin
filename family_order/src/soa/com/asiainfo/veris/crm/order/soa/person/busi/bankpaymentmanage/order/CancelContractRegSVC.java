/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

/**
 * @CREATED by gongp@2014-6-24 修改历史 Revision 2014-6-24 上午11:11:58
 */
public class CancelContractRegSVC extends OrderService
{
    private static final long serialVersionUID = 3071084419129219674L;

    /*
     * (non-Javadoc)
     */
    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "1392";
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "1392";
    }

    @Override
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        IData inparam = new DataMap();
        String serialNumber = input.getString("SERIAL_NUMBER");
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        IDataset subSignInfos = UserBankMainSignInfoQry.queryUserBankSubCountByUID(serialNumber, ucaData.getUserEparchyCode());

        if (IDataUtil.isNotEmpty(subSignInfos))
        {

            inparam.put("SERIAL_NUMBER", serialNumber);
            inparam.put("CANCEL_DATAS", subSignInfos);

            CSAppCall.call("SS.CancelSubNumRegSVC.tradeReg", inparam);
        }
    }

}
