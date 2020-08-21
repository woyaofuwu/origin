package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyUnionPayReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.UnionPayMemberData;

public class FamilyUnionPayCheckDRConsume implements ITradeAction
{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		String sn = btd.getRD().getUca().getSerialNumber();//获取到的是主号号码
		System.out.println("sn>>>>>sn=="+sn);
		IDataset snRelationSet = RelaUUInfoQry.queryRelaUUBySnb(sn,"61");
		if (IDataUtil.isNotEmpty(snRelationSet) && "2".equals(snRelationSet.getData(0).getString("ROLE_CODE_B"))) 
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务检查：多人约消用户不允许办理统一付费业务");
			return;
			
		}else
		{
			FamilyUnionPayReqData reqData = (FamilyUnionPayReqData) btd.getRD();
	        List<UnionPayMemberData> memberList = reqData.getMemberList();
	        System.out.println("memberList>>>>>memberList=="+memberList);
	        for (int i = 0, size = memberList.size(); i < size; i++)
	        {
	            UnionPayMemberData member = memberList.get(i);
	            IDataset snMebList = RelaUUInfoQry.queryRelaUUBySnb(member.getMemberSn(),"61");
	            if (IDataUtil.isNotEmpty(snMebList) && "2".equals(snMebList.getData(0).getString("ROLE_CODE_B"))) 
	            {
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务检查：多人约消用户不允许办理统一付费业务");
					return;
				}
	        }
		}
        
		
	}

}
