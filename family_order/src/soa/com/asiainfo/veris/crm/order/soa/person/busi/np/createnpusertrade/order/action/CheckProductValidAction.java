package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;

public class CheckProductValidAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		String tradeTypeCode=btd.getTradeTypeCode();
				
		UcaData uca= btd.getRD().getUca();
		String userId=uca.getUserId();
		
		if(tradeTypeCode.equals("40")){		//携入用户开户
			List<SvcTradeData> svcs=uca.getUserSvcs();
			if(svcs!=null&&svcs.size()>0){
				
				for(int i=0,size=svcs.size();i<size;i++){
					SvcTradeData data=svcs.get(i);
					
					String modifyTag=data.getModifyTag();
					String svcId=data.getElementId();
					
					if((svcId.equals("190")||svcId.equals("20171201"))&&modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"携入用户不能办理volte业务及视频彩铃业务！");
						return ;
					}
				}
			}
		}else if(tradeTypeCode.equals("110")){		//产品变更
			//查询携转信息
			IDataset npUsers=UserNpInfoQry.qryUserNpInfosByUserId(userId);
			if(IDataUtil.isNotEmpty(npUsers)){
				IData npUser=npUsers.getData(0);
				String npTag=npUser.getString("NP_TAG","");
				
				if(npTag.equals("1")){		//如果是携入用户
					List<SvcTradeData> svcs=uca.getUserSvcs();
					if(svcs!=null&&svcs.size()>0){
						
						for(int i=0,size=svcs.size();i<size;i++){
							SvcTradeData data=svcs.get(i);
							
							String modifyTag=data.getModifyTag();
							String svcId=data.getElementId();
							
							if((svcId.equals("190")||svcId.equals("20171201"))&&modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
								CSAppException.apperr(CrmCommException.CRM_COMM_103,"携入用户不能办理volte业务及视频彩铃业务！");
								return ;
							}
						}
					}
					
				}
				
			}
		}
	}
}
