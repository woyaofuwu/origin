
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductBean;


/**
 * 用户办理活动绑定服务（包括平台服务） ，当用户取消掉绑定的服务时，活动也立即终止。这个请新增公共参数表，
 * 指定哪些活动，哪些营销包对应什么服务，当配置的服务终止时，
 * 用户所对应的活动以及营销包也要终止
 * @author tz
 *
 */
public class EndSVCCancelActiveAction implements ITradeAction
{
	private static Logger logger = Logger.getLogger(EndSVCCancelActiveAction.class);
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	UcaData uca =btd.getRD().getUca();
    	String userId = uca.getUserId();
        String serialNumber = uca.getSerialNumber();
        String tradeTypeCode = btd.getTradeTypeCode();
        String offerType="";
		List<String> delSvcArray = new ArrayList<String>();
		//如果开户时间不在当月就返回
		logger.debug("ChangeSVCCancelActiveAction>>>>>" + serialNumber +"---");
		if("110".equals(tradeTypeCode)){
			//如果有服务变更，将删除的服务id放到delSvcArray list中
			List<SvcTradeData> changeSvcs=btd.get("TF_B_TRADE_SVC"); 
			if(changeSvcs!=null&&changeSvcs.size()>0){
				for(SvcTradeData svcData : changeSvcs){
					if(BofConst.MODIFY_TAG_DEL.equals(svcData.getModifyTag())){
						delSvcArray.add(svcData.getElementId());
					}
				}
			}
			offerType = "S";
		}
		
		if(delSvcArray.size()==0){
			return;
		}
		logger.debug("ChangeSVCCancelActiveAction>>>>>offerType=" + offerType +"---");
		logger.debug("ChangeSVCCancelActiveAction>>>>>delSvcArray=" + delSvcArray +"---");
		
		ChangeProductBean bean = (ChangeProductBean) BeanManager.createBean(ChangeProductBean.class);
		bean.dealActiveBySvcEnd(userId,delSvcArray,offerType,btd.getRD().getCheckMode());
        
		
		/*
		 * 本需求中的平台服务取消截至营销活动在PlatRelaSaleAction中实现
		 * param_attr=1220 param_code=1221
		 * P1=服务编码；P2=活动编码；P3=营销包编码
		 */
		
		
		
		
		/*if("3700".equals(tradeTypeCode)){
			//如果有平台服务变更，将删除的平台服务id放到delSvcArray list中
			List<PlatSvcTradeData> changeSvcs=btd.get("TF_B_TRADE_PLATSVC"); 
			if(changeSvcs!=null&&changeSvcs.size()>0){
				for(PlatSvcTradeData svcData : changeSvcs){
					if(BofConst.MODIFY_TAG_DEL.equals(svcData.getModifyTag())){
						delSvcArray.add(svcData.getElementId());
					}
				}
			}
			
			offerType = "Z";
		}*/
		
    }
}
