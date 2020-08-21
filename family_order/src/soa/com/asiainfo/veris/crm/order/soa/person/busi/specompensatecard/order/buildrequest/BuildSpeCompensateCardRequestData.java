/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.specompensatecard.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.specompensatecard.order.requestdata.SpeCompensateCardRequestData;

/**
 * @CREATED by gongp@2014-9-2 修改历史 Revision 2014-9-2 上午10:58:58
 */
public class BuildSpeCompensateCardRequestData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        SpeCompensateCardRequestData reqData = (SpeCompensateCardRequestData) brd;

        reqData.setSimCardNo(param.getString("SIM_CARD_NO"));
        reqData.setPayMode(param.getString("PAY_MODE"));
        reqData.setCapacityTypeCode(param.getString("CAPACITY_TYPE_CODE", ""));
        reqData.setCardKindCode(param.getString("CARD_KIND_CODE", ""));
        reqData.setRemark(param.getString("REASON", "特殊补偿卡"));
        reqData.setResTypeCode(param.getString("RES_TYPE_CODE"));
        reqData.setResKindCode(param.getString("RES_KIND_CODE"));
        reqData.setFee(param.getString("FEE", "0"));

    }

    @Override
    public UcaData buildUcaData(IData param) throws Exception
    {
        IDataset userInfos = UserInfoQry.getValidUserInfoByResCode(param.getString("SIM_CARD_NO"));

        // 设置三户资料对象
        if (IDataUtil.isNotEmpty(userInfos))
        {
            param.put("SERIAL_NUMBER", userInfos.getData(0).getString("SERIAL_NUMBER"));
            return super.buildUcaData(param);
        }
        else
        {
            param.put("USER_ID", "0");
            param.put("SERIAL_NUMBER", "-1");
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
        IDataset userInfos = UserInfoQry.getValidUserInfoByResCode(input.getString("SIM_CARD_NO"));

        // 设置三户资料对象
        if (IDataUtil.isNotEmpty(userInfos))
        {
            input.put("SERIAL_NUMBER", userInfos.getData(0).getString("SERIAL_NUMBER"));
            super.checkBefore(input, reqData);
        }
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new SpeCompensateCardRequestData();
    }

}
