package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class CheckMessageVolteBusinessAction implements ITradeAction{
	
	
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		UcaData uca= btd.getRD().getUca();
		String userId=uca.getUserId();
		/*
		 * REQ201906030013volte用户修改上网服务状态拦截优化
		 * volte用户如果把上网服务截止或者暂停后，将会导致无法接受volte被叫。容易引起投诉，
		 * 目前用户可以通过短信侧办理上网服务暂停，请优化接口，判断用户如存在volte服务，则拦截办理上网服务状态变更为暂停。
		 */
		List<SvcTradeData> svcTradeDatas=btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
		if(svcTradeDatas!=null&&svcTradeDatas.size()>0){
			for(int i=0,size=svcTradeDatas.size();i<size;i++){
				SvcTradeData svcTradeData=svcTradeDatas.get(i);
				String modifyTag=svcTradeData.getModifyTag();
				String serviceId=svcTradeData.getElementId();
				String elementTypecode=svcTradeData.getElementType();
				//判断是否有退订上网服务操作，如有，对volte进行判断
				if(elementTypecode.equals("S")&&modifyTag.equals(BofConst.MODIFY_TAG_DEL)
								&&serviceId.equals("22")){
					IDataset svc190 = UserSvcInfoQry.getSvcUserIdPf(userId, "190");
					//判断是否退订190服务
					boolean delsvc190=false;							
					//判断是否订购190服务
					boolean addsvc190=false;
					for(int j=0,length=svcTradeDatas.size();j<length;j++){
						SvcTradeData svcTradeData_190=svcTradeDatas.get(j);
						String modifyTag_190=svcTradeData_190.getModifyTag();
						String serviceId_190=svcTradeData_190.getElementId();
						String elementTypecode_190=svcTradeData_190.getElementType();
						
						if(elementTypecode_190.equals("S")&&modifyTag_190.equals(BofConst.MODIFY_TAG_DEL)
							&&serviceId_190.equals("190")){
							delsvc190=true;	
						}
								
						if(elementTypecode_190.equals("S")&&modifyTag_190.equals(BofConst.MODIFY_TAG_ADD)
							&&serviceId_190.equals("190")){
							addsvc190=true;
						}
					}						

					if(IDataUtil.isNotEmpty(svc190)){
						if(!delsvc190){
							CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户存在volte服务，不能办理暂停或截止上网服务");
						}
					}else{
						if(addsvc190){ //本次做订购操作
							CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户正在办理volte服务，不能办理暂停或截止上网服务");
						}
					}
							
				}
			}
		}
	}
}
