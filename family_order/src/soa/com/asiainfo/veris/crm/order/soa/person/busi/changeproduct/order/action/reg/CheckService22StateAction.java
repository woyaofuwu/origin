package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

/**
 * BUG20190722105758_上网功能暂停用户办理视频彩铃问题
 * 办理视频彩铃后，由于视频彩铃办理需带出volte服务，而上网功能暂停用户是不能办理volte的，优化产品变更界面，选择视频彩铃提交后，如用户无上网服务，或服务状态为暂停，请拦截并提示需开通上网功能才能办理
 * by mengqx 20190802
 *
 */
public class CheckService22StateAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		ChangeProductReqData changeProductRD=(ChangeProductReqData)btd.getRD();
		
		UcaData uca=changeProductRD.getUca();
		
		//获取服务台账数据
		List<SvcTradeData> svcTrade=btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
		if(svcTrade!=null&&svcTrade.size()>0){
			for(SvcTradeData svc:svcTrade){
				//新增VoLTE服务或视频彩铃服务
				if(("20171201".equals(svc.getElementId())||"190".equals(svc.getElementId())) && BofConst.MODIFY_TAG_ADD.equals(svc.getModifyTag())){
//					List<SvcTradeData> clSvc=uca.getUserSvcBySvcId("22");
//					logger.debug("CheckService22StateActionxxxxxxxxxxxxxmqxxxxxxclSvc=" + clSvc);
//					if(clSvc==null||clSvc.size()==0){
//						//用户无上网服务
//						CSAppException.apperr(CrmCommException.CRM_COMM_103,"办理VoLTE服务必须开通上网功能才能办理");
//					}
					SvcStateTradeData svcState=uca.getUserSvcsStateByServiceId("22");
					if(svcState!=null){
						if("2".equals(svcState.getStateCode())){
							//用户上网服务状态为暂停状态
							CSAppException.apperr(CrmCommException.CRM_COMM_103,"办理VoLTE服务必须开通上网功能才能办理，用户上网服务状态为暂停");
						}
					}
				}
			}
		}
		
	}
	
}
