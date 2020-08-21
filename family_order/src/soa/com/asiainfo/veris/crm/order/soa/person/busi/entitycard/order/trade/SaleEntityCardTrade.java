/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.entitycard.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DeviceTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.entitycard.order.requestdata.SaleEntityCardRequestData;

/**
 * @CREATED by gongp@2014-6-4 修改历史 Revision 2014-6-4 上午09:37:35
 */
public class SaleEntityCardTrade extends BaseTrade implements ITrade
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub

        SaleEntityCardRequestData reqData = (SaleEntityCardRequestData) bd.getRD();

        MainTradeData maintd = bd.getMainTradeData();
        if ("".equals(reqData.getUca().getSerialNumber()))
        {
            maintd.setUserId(bd.getTradeId());
            maintd.setCustName(reqData.getCustName());
        }

        if ("1".equals(reqData.getSaleTypeRadio()))
        {
            maintd.setRsrvStr1("1"); // 没折时，主台账RSRV_STR1传"1"
        }
        else
        {
            maintd.setRsrvStr1("2"); // 打折时，主台账RSRV_STR1传"2"
        }
        maintd.setRsrvStr3("1");
        maintd.setRsrvStr4(" ");

        IDataset dataset2 = reqData.getCardList();

        Float deviceTotalFee = new Float(0); // 总面值
        Float shouldPayFee = new Float(0);

        /*
         * String radio = reqData.getSaleTypeRadio(); int totalCount = 0; if (IDataUtil.isNotEmpty(dataset2)) { for (int
         * i = 0; i < dataset2.size(); i++) { IData cardData = dataset2.getData(i); // 获取实缴费用总额 shouldPayFee +=
         * Float.valueOf(cardData.getString("totalPrice")) * 100; totalCount +=
         * Integer.parseInt(cardData.getString("rowCount")); if ("a".equals(radio)) { String devicePrice =
         * cardData.getString("devicePrice"); deviceTotalFee += Integer.parseInt(devicePrice) *
         * Integer.parseInt(cardData.getString("rowCount")); } } } dealTradeFeeSub(bd,shouldPayFee, deviceTotalFee);
         * float balance = 0; float totalBalance = 0; if(Float.parseFloat(bd.getOperFee()) < shouldPayFee) {
         * totalBalance = shouldPayFee - Float.parseFloat(bd.getOperFee()); balance = totalBalance; } float[] theBalance
         * = new float[dataset2.size()]; //分摊到各号段的减免差价，单位：（分） if(totalBalance != 0) { IData balanceData = null; for(int
         * i = 0; i < dataset2.size(); i++) { balanceData = (IData) dataset2.get(i); int tempSalePrice = (int)
         * (totalBalance * (Float.parseFloat(balanceData.getString("totalPrice")) * 100 / shouldPayFee) / 100); if(i ==
         * dataset2.size() - 1) { theBalance[i] = balance; } else { theBalance[i] = tempSalePrice * 100; } balance =
         * balance - tempSalePrice * 100; } }
         */
        long allSellFee = 0;
        String resKindcode = "";
        String devicePrice = "";
        for (int i = 0; i < dataset2.size(); i++)
        {
            IData tradeFeeDeviceData = dataset2.getData(i);

            if ("0".equals(tradeFeeDeviceData.getString("activeFlag")))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_159);
            }
            resKindcode = tradeFeeDeviceData.getString("resKindCode");
            devicePrice = tradeFeeDeviceData.getString("advise_price");

            dealTradeFeeDevice(bd, tradeFeeDeviceData);

            allSellFee += (int) (Double.parseDouble(tradeFeeDeviceData.get("totalPrice").toString()) * 100);

            this.saleEntityCard(tradeFeeDeviceData);
        }

        maintd.setRsrvStr5(resKindcode);
        maintd.setRsrvStr6(FeeUtils.Fen2Yuan(devicePrice));
        maintd.setRsrvStr9(FeeUtils.Fen2Yuan(String.valueOf(allSellFee)));
        
        String operFee = bd.getOperFee();
        if(!StringUtils.isBlank(operFee)){
           if(!operFee.equals(String.valueOf(allSellFee))){
               CSAppException.apperr(CrmCommException.CRM_COMM_103,"台帐费用稽核:费用子表中的销售费用和台账表中的数据不符!("+allSellFee+","+operFee+")"); 
           }
        }
        
    }

    public void dealTradeFeeDevice(BusiTradeData bd, IData param) throws Exception
    {
        SaleEntityCardRequestData reqData = (SaleEntityCardRequestData) bd.getRD();

        DeviceTradeData deviceTd = new DeviceTradeData();

        deviceTd.setDeviceTypeCode(param.getString("resKindCode"));// cardType
        deviceTd.setFeeTypeCode("22");// 22——实体卡购卡费
        deviceTd.setDeviceNoS(param.getString("startCardNo"));
        deviceTd.setDeviceNoE(param.getString("endCardNo"));
        deviceTd.setDeviceNum(param.getString("rowCount"));
        deviceTd.setDevicePrice(param.getString("advise_price"));
        deviceTd.setRemark("");
        deviceTd.setRsrvStr1(param.getString("resTypeCode"));

        deviceTd.setSalePrice(String.valueOf((int) (Double.parseDouble(param.get("totalPrice").toString()) * 100)));

        bd.add(reqData.getUca().getSerialNumber(), deviceTd);
    }

    public void dealTradeFeeSub(BusiTradeData bd, float shouldPayFee, float deviceTotalFee) throws Exception
    {
        SaleEntityCardRequestData reqData = (SaleEntityCardRequestData) bd.getRD();

        List feeTradeDatas = bd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);

        if (feeTradeDatas != null && feeTradeDatas.size() > 0)
        {
            for (int i = 0; i < feeTradeDatas.size(); i++)
            {
                FeeTradeData feeTradeData = (FeeTradeData) feeTradeDatas.get(i);
                if ("b".equals(reqData.getSaleTypeRadio()))
                {
                    feeTradeData.setOldfee(String.valueOf((int) shouldPayFee));
                }
                else
                {
                    feeTradeData.setOldfee(String.valueOf((int) deviceTotalFee));
                }
            }
        }
    }

    public void saleEntityCard(IData data) throws Exception
    {

        IDataset resSet = ResCall.iEntityCardModifyState(data.getString("startCardNo"), data.getString("endCardNo"), CSBizBean.getVisit().getDepartId(), "3", String.valueOf(Double.parseDouble(data.get("totalPrice").toString()) * 100), "", "0", "3");

        if (resSet.getData(0).getInt("X_RESULTCODE") != 0)
        {
            // THROW_C(216201, "调用资源更新接口出错！");
            CSAppException.apperr(CrmUserException.CRM_USER_867);
        }
    }

}
