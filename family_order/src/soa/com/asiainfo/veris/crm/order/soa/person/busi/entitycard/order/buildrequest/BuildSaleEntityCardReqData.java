/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.entitycard.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.entitycard.order.requestdata.SaleEntityCardRequestData;

/**
 * @CREATED by gongp@2014-6-4 修改历史 Revision 2014-6-4 上午09:40:25
 */
public class BuildSaleEntityCardReqData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        SaleEntityCardRequestData reqData = (SaleEntityCardRequestData) brd;

        reqData.setSaleTypeRadio(param.getString("baseinfo_radio"));

        String cardListStr = param.getString("X_CODING_STR", "[]");

        reqData.setCardList(new DatasetList(cardListStr));

        reqData.setCustName(param.getString("CUST_NAME"));
    }

    @Override
    public UcaData buildUcaData(IData param) throws Exception
    {
        // 设置三户资料对象
        String sn = param.getString("SERIAL_NUMBER");
        if (!StringUtils.isBlank(sn))
        {
            return super.buildUcaData(param);
        }
        else
        {
            param.put("USER_ID", SeqMgr.getUserId());
            param.put("SERIAL_NUMBER", "");
            param.put("CUST_ID", "0");
            param.put("ACCT_ID", "0");
            param.put("NET_TYPE_CODE", "00");

            String routeId = param.getString("EPARCHY_CODE");
            if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
            {
                param.put("EPARCHY_CODE", "0898");
            }

            UcaData ucaData = new UcaData();
            ucaData.setUser(new UserTradeData(param));
            ucaData.setCustomer(new CustomerTradeData(param));
            ucaData.setCustPerson(new CustPersonTradeData(param));
            ucaData.setAccount(new AccountTradeData(param));
            ucaData.setLastOweFee("0");
            ucaData.setRealFee("0");
            ucaData.setAcctBlance("0");

            DataBusManager.getDataBus().setUca(ucaData);
            return ucaData;
        }
    }

    @Override
    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {

    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new SaleEntityCardRequestData();
    }

}
