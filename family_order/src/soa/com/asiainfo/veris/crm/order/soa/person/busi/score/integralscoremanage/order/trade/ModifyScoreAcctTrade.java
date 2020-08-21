
package com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.order.trade;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.IntegralAcctTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.score.ScoreAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.integralscoremanage.order.requestdata.ModifyScoreAcctRegData;

public class ModifyScoreAcctTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        ModifyScoreAcctRegData reqData = (ModifyScoreAcctRegData) bd.getRD();
        
        String inType = reqData.getIN_TYPE();
        
        if(StringUtils.isEmpty(inType) || inType.equals("MOD")){
        	createModifyTradeIntegralAcct(bd);
        }else if(inType.equals("ADD")){
        	createAddTradeIntegralAcct(bd);
        }

    }

    public void createModifyTradeIntegralAcct(BusiTradeData bd) throws Exception
    {

        ModifyScoreAcctRegData reqData = (ModifyScoreAcctRegData) bd.getRD();

        IDataset acctinfos = ScoreAcctInfoQry.queryScoreAcctInfoByUserId(reqData.getUca().getUserId(), "10A", getUserEparchyCode());
        if (IDataUtil.isEmpty(acctinfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户积分账户资料无数据！");
        }

        IntegralAcctTradeData integralTD = new IntegralAcctTradeData(acctinfos.getData(0));
        
        if(StringUtils.isNotEmpty(reqData.getPSPT_TYPE_CODE()))
        	integralTD.setPsptTypeCode(reqData.getPSPT_TYPE_CODE());
        if(StringUtils.isNotEmpty(reqData.getPSPT_ID()) && !reqData.getPSPT_ID().contains("*"))
	        integralTD.setPsptId(reqData.getPSPT_ID());
        if(StringUtils.isNotEmpty(reqData.getCONTRACT_PHONE()))
	        integralTD.setContractPhone(reqData.getCONTRACT_PHONE());
        if(StringUtils.isNotEmpty(reqData.getEMAIL()) && !reqData.getEMAIL().contains("*") )
	        integralTD.setEmail(reqData.getEMAIL());
        if(StringUtils.isNotEmpty(reqData.getNAME()) && !reqData.getNAME().contains("*"))
	        integralTD.setName(reqData.getNAME());
        if(StringUtils.isNotEmpty(reqData.getADDRESSE()) && !reqData.getADDRESSE().contains("*"))
	        integralTD.setAddress(reqData.getADDRESSE());
        if(StringUtils.isNotEmpty(reqData.getSTART_DATE()))
	        integralTD.setStartDate(reqData.getSTART_DATE());
        if(StringUtils.isNotEmpty(reqData.getEND_DATE()))
	        integralTD.setEndDate(reqData.getEND_DATE());
	        
        integralTD.setModifyTag(BofConst.MODIFY_TAG_UPD);

        bd.add(reqData.getUca().getSerialNumber(), integralTD);
    }
    
    /**
     * 接口新增积分账户信息
     * @param bd
     * @throws Exception
     */
    public void createAddTradeIntegralAcct(BusiTradeData bd) throws Exception
    {

        ModifyScoreAcctRegData reqData = (ModifyScoreAcctRegData) bd.getRD();

        IntegralAcctTradeData integralTD = new IntegralAcctTradeData();
        
        integralTD.setUserId(reqData.getUca().getUserId());
        integralTD.setIntegralAcctId(reqData.getINTEGRAL_ACCOUNT_ID());
        integralTD.setIntegralAccountType(reqData.getINTEGRAL_ACCOUNT_TYPE());
        integralTD.setPsptTypeCode(reqData.getPSPT_TYPE_CODE());
        integralTD.setPsptId(reqData.getPSPT_ID());
        integralTD.setContractPhone(reqData.getCONTRACT_PHONE());
        integralTD.setEmail(reqData.getEMAIL());
        integralTD.setName(reqData.getNAME());
        integralTD.setAddress(reqData.getADDRESSE());
        integralTD.setStartDate(reqData.getSTART_DATE());
        integralTD.setEndDate(reqData.getEND_DATE());
        integralTD.setStatus(reqData.getSTATUS());
        integralTD.setModifyTag(BofConst.MODIFY_TAG_ADD);

        bd.add(reqData.getUca().getSerialNumber(), integralTD);

    }

}
