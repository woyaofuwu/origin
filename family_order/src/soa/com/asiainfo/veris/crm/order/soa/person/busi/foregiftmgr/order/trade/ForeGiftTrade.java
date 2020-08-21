/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.foregiftmgr.order.trade;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import org.apache.commons.lang.StringUtils;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.foregiftmgr.order.requestdata.ForeGiftReqData;

/**
 * @CREATED by gongp@2014-4-10 修改历史 Revision 2014-4-10 上午10:07:39
 */
public class ForeGiftTrade extends BaseTrade implements ITrade
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub

        ForeGiftReqData reqData = (ForeGiftReqData) bd.getRD();

        MainTradeData mainTD = bd.getMainTradeData();

        IDataset dataset = reqData.getUserForeGifts();

    

        //add by liangdg3 at 20191028 for REQ201910180018押金业务受理及清退电子化存储需求 start
        //主台账表记录押金业务类型
        mainTD.setRsrvStr7(reqData.getOperType());
        //add by liangdg3 at 20191028 for REQ201910180018押金业务受理及清退电子化存储需求 start
        //主台账表记录押金业务类型

        if (dataset.size() > 0)
        {
            IData foreGift = dataset.getData(0);
            mainTD.setInvoiceNo(reqData.getInvoiceNo());// 发票号码
            mainTD.setRsrvStr3(foreGift.getString("FOREGIFT_PSPT_ID"));
            mainTD.setRsrvStr5(reqData.getInvoiceNo());// 发票号码
            mainTD.setRsrvStr8(foreGift.getString("FOREGIFT_CUST_NAME"));

            //add by liangdg3 at 20191028 for REQ201910180018押金业务受理及清退电子化存储需求 start
            //主台账表记录押金类型名称和金额
            String foreGiftCode=foreGift.getString("FOREGIFT_CODE");
            IDataset foregiftCodes = StaticUtil.getStaticList("TD_S_FOREGIFT");
            if(StringUtils.isNotBlank(foreGiftCode)&& IDataUtil.isNotEmpty(foregiftCodes)){
                for (int i = 0; i < foregiftCodes.size(); i++) {
                    IData fc = foregiftCodes.getData(i);
                    if(foreGiftCode.equals(fc.getString("DATA_ID"))){
                        mainTD.setRsrvStr9(fc.getString("DATA_NAME"));
                    }
                }
            }
            String payMoney=foreGift.getString("PAY_MONEY");
            if("0".equals(reqData.getOperType())){
                mainTD.setRsrvStr10(payMoney);
            }else{
                if(StringUtils.isNotBlank(payMoney)&&payMoney.length()>1) {
                    mainTD.setRsrvStr10(payMoney.substring(1));
                }
            }

            //add by liangdg3 at 20191028 for REQ201910180018押金业务受理及清退电子化存储需求 start
            //主台账表记录押金业务类型
        }
        bd.setMainTradeProcessTagSet(5, "1");

        if ("2".equals(reqData.getOperType()))
        {
            this.dealNonCustomerForeGift(bd, reqData);
        }else{
            if("0".equals(reqData.getUca().getUser().getRemoveTag()))
            {
                this.genCreditTradeData(bd); 
            }
        }

    }
    
    /**
     * 交押金送信用度
     * @param bd
     * @param reqData
     * @throws Exception
     */
    public void genCreditTradeData(BusiTradeData btd) throws Exception
    {
       
        CreditTradeData creditTradeData = new CreditTradeData();
        creditTradeData.setUserId(btd.getRD().getUca().getUserId());
        creditTradeData.setCreditValue("" + btd.getForeGift());
        creditTradeData.setCreditMode("addCredit");// 信用度类型标识'addCredit-增加扣减信用度;callCredit-触发信控';
        creditTradeData.setCreditGiftMonths("12");// 赠送信用度月份数
        creditTradeData.setStartDate(btd.getRD().getAcceptTime());
        creditTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        creditTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(btd.getRD().getUca().getSerialNumber(), creditTradeData);
        
    }

    /**
     * 无主押金userID改为押金表中的userID
     * 
     * @param bd
     * @param reqData
     * @throws Exception
     * @CREATE BY GONGP@2014-5-23
     */
    public void dealNonCustomerForeGift(BusiTradeData bd, ForeGiftReqData reqData) throws Exception
    {

        List feeTradeDatas = bd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);

        if (feeTradeDatas != null && feeTradeDatas.size() > 0)
        {
            for (int i = 0; i < feeTradeDatas.size(); i++)
            {
                FeeTradeData feeTradeData = (FeeTradeData) feeTradeDatas.get(i);
                if (feeTradeData.getFeeMode().equals(BofConst.FEE_MODE_FOREGIFT))
                {
                    feeTradeData.setUserId(reqData.getNonCustomerUserId());
                }
            }
        }

    }

}
