/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
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
 * @CREATED by gongp@2014-6-24 修改历史 Revision 2014-6-24 上午11:16:28
 */
public class CancelContractTrade extends BaseTrade implements ITrade
{

    public void cancelBankSignInfo(BusiTradeData btd) throws Exception
    {

        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();

        IDataset mainSignInfo = UserBankMainSignInfoQry.queryUserBankMainSignByUID("01", reqData.getUca().getSerialNumber());

        if (IDataUtil.isEmpty(mainSignInfo))
        {
            // common.error("1202:该用户当前无有效的总对总缴费签约记录！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_233);
        }

        IData signInfo = mainSignInfo.getData(0);

        if (reqData.getUca().getAcctDay().equals(SysDateMgr.getCurDay()))
        {
            // common.error("1209:用户月结日不允许办理解约！");
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_234);
        }
        // 预付费类型转换
        String prepayTag = BankPaymentUtil.convertPrePayTag(reqData.getUca());

        // 2位交易方式类型（10：消息，11：文件）+3位省编码+17位精确到毫秒时间+10位流水。流水号从0000000001开始，步长为1
        String operFlowId = reqData.getOperFlowId();

        if ("".equals(operFlowId) || operFlowId == null)
        {
            operFlowId = BankPaymentUtil.getOperFlowId();
        }

        BankMainSignTradeData BankMainsignTD = new BankMainSignTradeData(signInfo);

        BankMainsignTD.setEndDate(reqData.getAcceptTime());
        BankMainsignTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
        BankMainsignTD.setSignState("1");
        BankMainsignTD.setRsrvStr2(operFlowId);

        btd.add(reqData.getUca().getSerialNumber(), BankMainsignTD);

        // ----//关联副号码解约
        // 改在ohertradeDeal里处理
        // -------------------

        // 签约信息同步
        IData param = new DataMap();
        param.put("DEAL_TYPE", "02");
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
        param.put("PAY_TYPE", signInfo.getString("PAY_TYPE", ""));
        param.put("TRANSACTION_ID", operFlowId);

        String chnlType = reqData.getChnlType();

        if (!"99".equals(chnlType) && !"50".equals(chnlType) && !"51".equals(chnlType) && !"52".equals(chnlType) && !"53".equals(chnlType) && !"54".equals(chnlType) && !"55".equals(chnlType))
        {
            if ("1".equals(reqData.getPayType()))
            {
                param.put("RECH_THRESHOLD", reqData.getRechThreshold());
                param.put("RECH_AMOUNT", reqData.getRechAmount());
            }
            IData rData = new DataMap();
            try
            {
                param.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());

                String kindId = "";
                if ("00".equals(chnlType))
                {
                    kindId = "BIP1A157_T1000153_0_0";
                }
                else
                {
                    kindId = "BIP1A155_T1000153_0_0";
                }
                param.put("X_TRANS_CODE", "T1000153");
                param.put("KIND_ID", kindId);

                rData = IBossCall.dealInvokeUrl(kindId, "IBOSS2", param).getData(0);
            }
            catch (Exception e)
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_53, e.getMessage());
            }

            if (!("0000".equals(rData.getString("X_RSPCODE", ""))))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_53, rData.getString("X_RSPCODE", "") + "," + rData.getString("X_RSPDESC", "") + "," + rData.getString("X_RESULTCODE", "") + "," + rData.getString("X_RESULTINFO", ""));
            }

        }

    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();

        this.cancelBankSignInfo(btd);

        MainTradeData mainTD = btd.getMainTradeData();
        mainTD.setRsrvStr1("CancelSignInfo");
    }

}
