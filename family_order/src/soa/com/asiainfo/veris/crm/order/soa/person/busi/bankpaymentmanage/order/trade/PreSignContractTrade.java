/**
 * 
 */
package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BankMainSignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.BankPaymentUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.requestdata.BaseSignContractReqData;

/**
 *
 * 修改历史
 * Revision  2014-9-22 下午08:24:18
 */
public class PreSignContractTrade extends BaseTrade implements ITrade
{

    /* (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();
        // 证件类型转换
        IDataset psptTypeInfo = CommparaInfoQry.getCommparaAllColByParser("CSM", "3128", reqData.getUca().getCustomer().getPsptTypeCode(), "");
        if (IDataUtil.isEmpty(psptTypeInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未配置证件类型转换关系，请配置3128参数！");
        }
        String psptTypeId = psptTypeInfo.getData(0).getString("PARA_CODE1");
        
        String operFlowId = reqData.getOperFlowId();

        if ("".equals(operFlowId))
        {
            operFlowId = BankPaymentUtil.getOperFlowId();
        }
        
        this.genBankMainSignInfo(btd, psptTypeId,operFlowId);
        
        
        
    }
    
    public void genBankMainSignInfo(BusiTradeData btd, String psptTypeId,String operFlowId) throws Exception
    {
        BaseSignContractReqData reqData = (BaseSignContractReqData) btd.getRD();
        BankMainSignTradeData BankMainsignTD = new BankMainSignTradeData();

        BankMainsignTD.setUserType("01");
        BankMainsignTD.setUserValue(reqData.getUca().getSerialNumber());
        BankMainsignTD.setHomeArea(reqData.getUca().getUserEparchyCode());
        BankMainsignTD.setBankAcctId(reqData.getBankAcctid());
        BankMainsignTD.setBankAcctType(reqData.getBankAcctType());
        BankMainsignTD.setBankId(reqData.getBankId());
        BankMainsignTD.setChnlType(reqData.getChnlType());
        BankMainsignTD.setPayType(reqData.getPayType());
        BankMainsignTD.setInstId(SeqMgr.getInstId());

        BankMainsignTD.setSignState("0");
        BankMainsignTD.setApplyDate(SysDateMgr.getSysTime());
        BankMainsignTD.setStartDate(SysDateMgr.getSysTime());
        BankMainsignTD.setUpdateDate(SysDateMgr.getSysTime());
        BankMainsignTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
        BankMainsignTD.setModifyTag("0");
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
        BankMainsignTD.setRemark("预约工单");

        BankMainsignTD.setSignId(reqData.getSignId());

        BankMainsignTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        BankMainsignTD.setInstId(SeqMgr.getInstId());
        BankMainsignTD.setRsrvStr2(operFlowId);

        btd.add(reqData.getUca().getSerialNumber(), BankMainsignTD);

    }

}
