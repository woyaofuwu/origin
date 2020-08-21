/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BankMainSignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.BankPaymentUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.requestdata.BaseSignContractReqData;

/**
 * @CREATED by gongp@2014-6-24 修改历史 Revision 2014-6-24 上午11:15:06
 */
public class ChangeContractTrade extends BaseTrade implements ITrade
{

    public void changeBankSignInfo(BusiTradeData btd) throws Exception
    {

        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();

        IDataset mainSignInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", reqData.getUca().getSerialNumber());

        if (IDataUtil.isEmpty(mainSignInfo))
        {
            // common.error("1202:该用户当前无有效的总对总缴费签约记录！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_233);
        }
        IData signInfo = mainSignInfo.getData(0);

        String prepayTag = BankPaymentUtil.convertPrePayTag(reqData.getUca());

        // 2位交易方式类型（10：消息，11：文件）+3位省编码+17位精确到毫秒时间+10位流水。流水号从0000000001开始，步长为1
        String operFlowId = reqData.getOperFlowId();

        if ("".equals(operFlowId))
        {
            operFlowId = BankPaymentUtil.getOperFlowId();
        }

        String payType = reqData.getPayType();

        BankMainSignTradeData BankMainsignTD = new BankMainSignTradeData(signInfo);

        BankMainsignTD.setChnlType(reqData.getChnlType());

        if ("1".equals(reqData.getPayType()))
        {
            if ("0".equals(BankMainsignTD.getPayType()))
            {
                BankMainsignTD.setRsrvStr1("1");// SMS USE;
            }
            else
            {
                if (!BankMainsignTD.getRechAmount().equals(reqData.getRechAmount()) || !BankMainsignTD.getRechThreshold().equals(reqData.getRechThreshold()))
                {
                    BankMainsignTD.setRsrvStr1("2");// SMS USE;
                }
            }

            BankMainsignTD.setRechThreshold(reqData.getRechThreshold());
            BankMainsignTD.setRechAmount(reqData.getRechAmount());
        }
        if ("0".equals(reqData.getPayType()))
        {
            if ("1".equals(BankMainsignTD.getPayType()))
            {
                BankMainsignTD.setRsrvStr1("3");// SMS USE;
            }
        }

        BankMainsignTD.setPayType(payType);
        BankMainsignTD.setRemark(reqData.getRemark());
        BankMainsignTD.setModifyTag(BofConst.MODIFY_TAG_UPD);

        btd.add(reqData.getUca().getSerialNumber(), BankMainsignTD);

        if ("1".equals(payType))
        {

            // 开通确认
            if ("0".equals(signInfo.getString("PAY_TYPE")))
            {
                BankMainsignTD.setRsrvStr1("1");
            }
            else if (!reqData.getRechAmount().equals(signInfo.getString("RECH_AMOUNT", "")) || !reqData.getRechThreshold().equals(signInfo.getString("RECH_THRESHOLD", "")))
            {
                BankMainsignTD.setRsrvStr1("2");
            }
        }
        if ("0".equals(payType))
        {
            // 开通确认
            if ("1".equals(signInfo.getString("PAY_TYPE")))
            {
                BankMainsignTD.setRsrvStr1("3");
            }
        }
        // 签约信息同步
        if (!"99".equals(reqData.getChnlType()))
        {
            IData param = new DataMap();
            String errStr = "";
            param.put("DEAL_TYPE", "03");
            param.put("SIGN_ID", signInfo.getString("SIGN_ID", ""));
            param.put("USER_TYPE", signInfo.getString("USER_TYPE"));
            param.put("USER_VALUE", reqData.getUca().getSerialNumber());
            param.put("PREPAY_TYPE", prepayTag);
            param.put("BANK_ID", signInfo.getString("BANK_ID", ""));
            param.put("BANK_ACCT_TYPE", signInfo.getString("BANK_ACCT_TYPE", ""));
            param.put("BANK_ACCT_ID", signInfo.getString("BANK_ACCT_ID", ""));
            param.put("SUB_TIME", signInfo.getString("APPLY_DATE").replaceAll(" ", "").replaceAll(":", "").replaceAll("-", ""));
            param.put("PSPT_TYPE_ID", signInfo.getString("ID_TYPE", ""));
            param.put("PSPT_ID", signInfo.getString("ID_VALUE", ""));
            param.put("USER_NAME", reqData.getUca().getCustomer().getCustName());
            param.put("CHNL_TYPE", reqData.getChnlType());
            param.put("PAY_TYPE", payType);
            param.put("TRANSACTION_ID", operFlowId);
            if ("1".equals(payType))
            {
                param.put("RECH_THRESHOLD", reqData.getRechThreshold());
                param.put("RECH_AMOUNT", reqData.getRechAmount());
            }
            IData rData = new DataMap();

            try
            {
                param.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
                param.put("KIND_ID", "BIP1A154_T1000153_0_0");
                param.put("X_TRANS_CODE", "T1000153");

                rData = IBossCall.dealInvokeUrl("BIP1A154_T1000153_0_0", "IBOSS2", param).getData(0);
            }
            catch (Exception e)
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_235, e.getMessage());
            }

            if (rData == null)
            {
                // .error("1302:调用一级接口主号签约信息变更同步出错:返回为空！");
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_235, "返回为空！");
            }
            if (rData != null && !rData.getString("X_RSPCODE", "").equals("0000"))
            {
                // common.error("1302:调用一级接口主号签约信息变更同步出错:"+rData.getString("X_RSPDESC",""));
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_235, rData.getString("X_RSPDESC", ""));
            }

        }

    }

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();

        this.changeBankSignInfo(btd);

        MainTradeData mainTD = btd.getMainTradeData();
        mainTD.setRsrvStr1("ChangeSignInfo");
    }
}
