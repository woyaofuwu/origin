
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideunionpay.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideunionpay.order.requestdata.NophoneWideUnionPayRequestData;

public class NophoneWideUnionPayTrade extends BaseTrade implements ITrade
{
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
    	NophoneWideUnionPayRequestData reqData = (NophoneWideUnionPayRequestData) btd.getRD();
        
        // 增加统一付费UU关系
    	createTradeRelationUU(btd, reqData);
        // 付费关系
        createTradePayRelation(btd, reqData);
        // 账期处理
        acctDayDeal(btd, reqData);
        
    }


    /**
     * 账期处理
     * @param btd
     * @throws Exception
     */
    public void acctDayDeal(BusiTradeData btd, NophoneWideUnionPayRequestData reqData) throws Exception{
    	if (!StringUtils.isBlank(reqData.getUca().getNextAcctDay()))
        {
            CSAppException.apperr(FamilyException.CRM_FAMILY_830, reqData.getUca().getSerialNumber(), reqData.getUca().getNextAcctDay());
        }
    	if (!"1".equals(reqData.getUca().getAcctDay()))
        {
            btd.addChangeAcctDayData(reqData.getUca().getUserId(), "1");
        }
    }
    
    /**
     * 付费关系
     * 
     * @param btd
     * @throws Exception
     */
    private void createTradePayRelation(BusiTradeData<BaseTradeData> btd, NophoneWideUnionPayRequestData reqData) throws Exception
    {
        PayRelationTradeData payrelationTD = new PayRelationTradeData();
        IData mainPayRelations = UcaInfoQry.qryDefaultPayRelaByUserId(reqData.getPayUserId());
        
        if (IDataUtil.isEmpty(mainPayRelations))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "付费号码无默认付费帐户！");
        }
        IDataset payRela = PayRelaInfoQry.getPayRelaByUserId(reqData.getUca().getUserId());
        String endCycleId = "20501231";
        if(IDataUtil.isNotEmpty(payRela)){
        	endCycleId = payRela.getData(0).getString("END_CYCLE_ID");
        }
        payrelationTD.setUserId(reqData.getUca().getUserId());
        payrelationTD.setAcctId(reqData.getPayAcctId());
//        payrelationTD.setPayitemCode("40001");
        payrelationTD.setPayitemCode(BofConst.NO_PHONE_WIDE_UNION_PAY_CODE);//现网有问题，改造 modify_by_duhj_kd 20200526
        payrelationTD.setAcctPriority("0");
        payrelationTD.setUserPriority("0");
        payrelationTD.setBindType("1");
        String startDate=btd.getRD().getAcceptTime();
        List<DiscntTradeData> discntList = reqData.getUca().getUserDiscnts();
        if(discntList!=null&&discntList.size()>0){
        	for(int i=0;i<discntList.size();i++){
            	DiscntTradeData dtd=discntList.get(i);
            	IDataset commparaInfos = CommparaInfoQry.getCommparaByCodeCode1("CSM","532","685",dtd.getDiscntCode());
            	if(DataSetUtils.isNotBlank(commparaInfos)){
            	    startDate=SysDateMgr.getNextSecond(dtd.getEndDate());
            	    break;
            	}
            }
        }
        payrelationTD.setStartCycleId(startDate.substring(0, 10).replace("-", ""));
        payrelationTD.setEndCycleId(endCycleId);
        payrelationTD.setActTag("1");
        payrelationTD.setDefaultTag("0");
        payrelationTD.setLimitType("0");
        payrelationTD.setLimit("0");
        payrelationTD.setComplementTag("0");
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(btd.getRD().getUca().getSerialNumber(), payrelationTD);
    }
    
    /**
     * 增加统一付费UU关系
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createTradeRelationUU(BusiTradeData<BaseTradeData> btd, NophoneWideUnionPayRequestData reqData) throws Exception
    {
    	NophoneWideUnionPayRequestData rd = (NophoneWideUnionPayRequestData) btd.getRD();
    	RelationTradeData rtd = new RelationTradeData();
    	
        rtd.setUserIdA(reqData.getUca().getUserId());//无手机宽带userId
        rtd.setUserIdB(reqData.getPayUserId());//付费手机号码user_id 
        rtd.setSerialNumberA(rd.getUca().getUser().getSerialNumber()); //无手机宽带帐号
        rtd.setSerialNumberB(reqData.getPaySerialNumber());//付费手机号码
        rtd.setRelationTypeCode("58");
        rtd.setRoleCodeA("0");
        rtd.setRoleCodeB("1");
        
        rtd.setInstId(SeqMgr.getInstId());
        rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        String startDate=btd.getRD().getAcceptTime();
        List<DiscntTradeData> discntList = reqData.getUca().getUserDiscnts();
        if(discntList!=null&&discntList.size()>0){
        	for(int i=0;i<discntList.size();i++){
            	DiscntTradeData dtd=discntList.get(i);
            	IDataset commparaInfos = CommparaInfoQry.getCommparaByCodeCode1("CSM","532","685",dtd.getDiscntCode());
            	if(DataSetUtils.isNotBlank(commparaInfos)){
            	    startDate=SysDateMgr.getNextSecond(dtd.getEndDate());
            	    break;
            	}
            }
        }
        
        rtd.setStartDate(startDate);
        rtd.setEndDate(SysDateMgr.getTheLastTime());
        
        btd.add(reqData.getUca().getUser().getSerialNumber(), rtd);
        
    }
}




