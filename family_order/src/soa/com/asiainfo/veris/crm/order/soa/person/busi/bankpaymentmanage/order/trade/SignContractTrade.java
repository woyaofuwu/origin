/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BankMainSignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.BankPaymentUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.requestdata.BaseSignContractReqData;

/**
 * @CREATED by gongp@2014-6-20 修改历史 Revision 2014-6-20 下午04:11:25
 */
public class SignContractTrade extends BaseTrade implements ITrade
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();
        // 证件类型转换
        IDataset psptTypeInfo = CommparaInfoQry.getCommparaAllColByParser("CSM", "3128", reqData.getUca().getCustomer().getPsptTypeCode(), "");
        if (IDataUtil.isEmpty(psptTypeInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未配置证件类型转换关系，请配置3128参数！");
        }

        String psptTypeId = psptTypeInfo.getData(0).getString("PARA_CODE1");

        IData result = this.dealWithIBoss(btd, psptTypeId);

        this.genBankMainSignInfo(btd, psptTypeId, result.getString("SIGN_ID"), result.getString("BANK_ACCT_TYPE"), result.getString("OPER_FLOW_ID"));

        MainTradeData mainTd = btd.getMainTradeData();

    }

    public IData dealWithIBoss(BusiTradeData btd, String psptTypeId) throws Exception
    {
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();

        IData param = new DataMap();

        param.put("PSPT_TYPE_ID", psptTypeId);
        param.put("PSPT_ID", reqData.getUca().getCustomer().getPsptId());
        param.put("USER_TYPE", "01");
        param.put("USER_VALUE", reqData.getUca().getSerialNumber());
        param.put("USER_NAME", reqData.getUca().getCustomer().getCustName());
        param.put("BANK_ID", reqData.getBankId());
        param.put("BANK_ACCT_ID", reqData.getBankAcctid());
        param.put("BANK_ACCT_TYPE", reqData.getBankAcctType());

        IDataset result = new DatasetList();
        IData rData = new DataMap();
        if (!"99".equals(reqData.getChnlType()))
        {
            try
            {
                param.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
                result = IBossCall.SignBankCheck(param);
                if (IDataUtil.isNotEmpty(result))
                {
                    rData = result.getData(0);
                }
                else
                {
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_201, "获取一级BOSS数据错误!");
                }

            }
            catch (Exception e)
            {
                String errStr = e.toString();
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_201, errStr);
            }
            if (!("0000".equals(rData.getString("X_RSPCODE", ""))))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_201, rData.getString("X_RSPCODE", "") + "," + rData.getString("X_RSPDESC", "") + "," + rData.getString("X_RESULTCODE", "") + "," + rData.getString("X_RESULTINFO", ""));
            }
        }
        String bankAcctType = reqData.getBankAcctType();// rData.getString("BANK_ACCT_TYPE");

        String prepayTag = BankPaymentUtil.convertPrePayTag(reqData.getUca());

        String operFlowId = reqData.getOperFlowId();

        if ("".equals(operFlowId))
        {
            operFlowId = BankPaymentUtil.getOperFlowId();
        }
        String signId = "";

        param.put("DEAL_TYPE", "01");
        param.put("SIGN_ID", reqData.getSignId());
        param.put("USER_TYPE", "01");
        param.put("USER_VALUE", reqData.getUca().getSerialNumber());
        param.put("PREPAY_TYPE", prepayTag);
        param.put("BANK_ID", reqData.getBankId());
        param.put("BANK_ACCT_TYPE", bankAcctType);
        param.put("BANK_ACCT_ID", reqData.getBankAcctid());
        param.put("SUB_TIME", reqData.getAcceptTime().replaceAll(" ", "").replaceAll(":", "").replaceAll("-", ""));

        param.put("PSPT_TYPE_ID", psptTypeId);
        param.put("PSPT_ID", reqData.getUca().getCustomer().getPsptId());
        param.put("USER_NAME", reqData.getUca().getCustomer().getCustName());
        param.put("CHNL_TYPE", reqData.getChnlType());
        param.put("PAY_TYPE", reqData.getPayType());
        param.put("TRANSACTION_ID", operFlowId);

        if ("1".equals(reqData.getPayType()))
        {
            param.put("RECH_THRESHOLD", reqData.getRechThreshold());
            param.put("RECH_AMOUNT", reqData.getRechAmount());
        }

        try
        {
            param.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
            result = IBossCall.MainsignSync(param);
            if (IDataUtil.isNotEmpty(result))
            {
                rData = result.getData(0);
            }
            else
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_201, "获取一级BOSS数据错误!");
            }

        }
        catch (Exception e)
        {
            String errStr = e.toString();
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_53, errStr);
        }
        if (!("0000".equals(rData.getString("X_RSPCODE", ""))))
        {
            CSAppException.apperr(
                    BankPaymentManageException.CRM_BANKPAYMENT_53,
                    rData.getString("X_RSPCODE", "") + "," + rData.getString("X_RSPDESC", "") + ","
                            + rData.getString("X_RESULTCODE", "") + "," + rData.getString("X_RESULTINFO", ""));
        }
        signId = result.getData(0).getString("SIGN_ID", "9999999999999999999999");

        rData.clear();
        if (StringUtils.isBlank(signId))
            signId = "01000401" + SysDateMgr.getSysTime().replaceAll(" ", "").replaceAll(":", "").replaceAll("-", "");
        rData.put("SIGN_ID", signId);
        rData.put("OPER_FLOW_ID", operFlowId);
        rData.put("BANK_ACCT_TYPE", bankAcctType);

        return rData;
    }

    public void genBankMainSignInfo(BusiTradeData btd, String psptTypeId, String signId, String bankAcctType, String operFlowId) throws Exception
    {
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();
        BankMainSignTradeData BankMainsignTD = new BankMainSignTradeData();
        
        if("99".equals(reqData.getChnlType()) && !StringUtils.isBlank(reqData.getPreTradeId())){
            IDataset list = UserBankMainSignInfoQry.qryTradeMainsignInfoByPK(reqData.getPreTradeId());
            if(IDataUtil.isNotEmpty(list)){
                BankMainsignTD = new BankMainSignTradeData(list.getData(0));
            }else{
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_252);
            }
        }
        BankMainsignTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        BankMainsignTD.setInstId(SeqMgr.getInstId());

        BankMainsignTD.setUserType("01");
        BankMainsignTD.setUserValue(reqData.getUca().getSerialNumber());
        BankMainsignTD.setHomeArea(reqData.getUca().getUserEparchyCode());
        BankMainsignTD.setBankAcctId(reqData.getBankAcctid());
        BankMainsignTD.setBankAcctType(reqData.getBankAcctType());
        BankMainsignTD.setBankId(reqData.getBankId());
        BankMainsignTD.setChnlType(reqData.getChnlType());
        BankMainsignTD.setPayType(reqData.getPayType());

        BankMainsignTD.setSignState("0");
        BankMainsignTD.setApplyDate(SysDateMgr.getSysTime());
        BankMainsignTD.setStartDate(SysDateMgr.getSysTime());
        BankMainsignTD.setUpdateDate(SysDateMgr.getSysTime());
        BankMainsignTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
        BankMainsignTD.setUserName(reqData.getUca().getCustomer().getCustName());

        // 自动缴费，且预付费，设定 充值阀值 , 充值额度
        if ("1".equals(reqData.getPayType()))
        {
            BankMainsignTD.setRechThreshold(reqData.getRechThreshold());
            BankMainsignTD.setRechAmount(reqData.getRechAmount());
        }
        else
        {
            BankMainsignTD.setRechThreshold("0");
            BankMainsignTD.setRechAmount("0");
        }

        BankMainsignTD.setIdType(psptTypeId);
        BankMainsignTD.setIdValue(reqData.getUca().getCustomer().getPsptId());
        BankMainsignTD.setRemark(reqData.getRemark());

        // BankMainsignTD.setBankAcctType(bankAcctType);
        BankMainsignTD.setSignId(signId);
        BankMainsignTD.setRsrvStr2(operFlowId);

        btd.add(reqData.getUca().getSerialNumber(), BankMainsignTD);

    }

}
