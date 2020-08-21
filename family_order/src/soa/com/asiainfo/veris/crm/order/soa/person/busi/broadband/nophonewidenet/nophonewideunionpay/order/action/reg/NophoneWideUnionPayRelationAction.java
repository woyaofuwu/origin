package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideunionpay.order.action.reg;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewideunionpay.order.requestdata.NophoneWideUnionPayRequestData;

public class NophoneWideUnionPayRelationAction implements ITradeAction{
	 @Override
	    public void executeAction(BusiTradeData btd) throws Exception
	    {
	        // TODO Auto-generated method stub
		 	NophoneWideUnionPayRequestData reqData = (NophoneWideUnionPayRequestData)btd.getRD();
	        String userIdB = reqData.getPayUserId();
	        String userIdA = btd.getRD().getUca().getUserId();
//	        IDataset dataset = RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdB, "58", "1");
//	        if (IDataUtil.isNotEmpty(dataset))
//	        {
	            IDataset allRelations = RelaUUInfoQry.getUserRelationAll(userIdA, "58");
	            if(IDataUtil.isNotEmpty(allRelations)){
	            	IData mainPayRelation = UcaInfoQry.qryDefaultPayRelaByUserId(userIdB);
		            if (IDataUtil.isEmpty(mainPayRelation))
		            {
		                CSAppException.apperr(CrmCommException.CRM_COMM_103, "付费号码无默认付费帐户！");
		            }
		            String mainAcctId = mainPayRelation.getString("ACCT_ID", "-1");

		            IData relations = allRelations.getData(0);

		            RelationTradeData ralationTd = new RelationTradeData(relations);

		            ralationTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
		            ralationTd.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());

		            btd.add(btd.getRD().getUca().getSerialNumber(), ralationTd);

		        }
//	        }
	        IData userPayRelation = new DataMap();
	        userPayRelation = this.getNophonePayRela(userIdA,"").getData(0);
	        if(IDataUtil.isNotEmpty(userPayRelation)){
	        	PayRelationTradeData payTd = new PayRelationTradeData(userPayRelation);

	            payTd.setEndCycleId(SysDateMgr.addMonths(DiversifyAcctUtil.getLastDayThisAcct(userIdB), -1).substring(0, 10).replace("-", ""));
	            payTd.setRemark("无手机宽带统一付费结束当前付费关系");
	            payTd.setModifyTag(BofConst.MODIFY_TAG_DEL);

	            btd.add(btd.getRD().getUca().getSerialNumber(), payTd);
	        }
	    }

	    private IDataset getNophonePayRela(String userId,String payItemCode) throws Exception
	    {
	    	IDataset dataset = new DatasetList();
	    	if("".equals(payItemCode) || null == payItemCode){
	    		dataset = PayRelaInfoQry.getPayRelaByUserId(userId);
	    	}else{
	    		dataset = PayRelaInfoQry.getPayrelaByPayItemCode(userId,payItemCode);
	    	}
	        return dataset;
	    }
}
