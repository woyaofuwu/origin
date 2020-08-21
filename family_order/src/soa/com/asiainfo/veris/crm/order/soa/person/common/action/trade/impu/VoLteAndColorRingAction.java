
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.impu;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class VoLteAndColorRingAction implements ITradeAction
{ 
	private static transient Logger logger = Logger.getLogger(VoLteAndColorRingAction.class);
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		logger.debug(">>>>>>>>>>>>>VoLteAndColorRingAction atart>>>>>>>");
		UcaData uca = btd.getRD().getUca();
		Boolean isNeedAction = btd.getRD().isNeedAction();
		if(!isNeedAction){
			logger.debug(">>>>>>>>>>>>>VoLteAndColorRingAction 不需要执行action>>>>>>>");
			return;
		}

		IDataset commData = getCommParam();	
        
		dealColorRingOper(btd,uca,commData.getData(0));	//视频彩铃和VoLTE关联开通
		
		dealMemberPackOper(btd,uca,commData);	//会员包与视频彩铃功能关联开通和退订
		logger.debug(">>>>>>>>>>>>>VoLteAndColorRingAction atart>>>>>>>");
	}
	
	private IDataset getCommParam() throws Exception
    {
      //查询视频彩铃服务配置,PARA_CODE1 配置为视频彩铃服务、PARA_CODE2 配置为VOLTE服务、PARA_CODE3 配置为彩铃服务、PARA_CODE4 配置为视频彩铃基础会员包
        IDataset commList=CommparaInfoQry.getCommpara("CSM","2017","VIDEO_COLORRING_SERV",CSBizBean.getUserEparchyCode());
        if(IDataUtil.isEmpty(commList))
        {
            String errors = "视频彩铃服务静态参数【VIDEO_COLORRING_SERV】未配置，请联系管理员！";
            CSAppException.apperr(CrmCommException.CRM_COMM_103, errors);
        }
        return commList;
    }
	
	//1.开通视频彩铃业务：若是普通用户，需进行VoLTE业务的关联开通。
	private void dealColorRingOper(BusiTradeData btd,UcaData uca,IData commData) throws Exception{
		String ColorRingServiceId = commData.getString("PARA_CODE1");	//视频彩铃服务
		String VolteServiceId = commData.getString("PARA_CODE2");	//VoLTE服务
		
   		List<SvcTradeData> tradeSvcs = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
	    List<SvcTradeData> VolteSvc = uca.getUserSvcBySvcId(VolteServiceId);
   		if(DataUtils.isEmpty(tradeSvcs)){
   			return ;
   		}
   		for(int i = 0; i < tradeSvcs.size(); i++){
   			String modifyTag = tradeSvcs.get(i).getModifyTag();
   			String serviceId = tradeSvcs.get(i).getElementId();
   			if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && serviceId.equals(ColorRingServiceId)){
   				if(DataUtils.isNotEmpty(VolteSvc)){
					String VolteModifyTag = VolteSvc.get(0).getModifyTag();
					if(BofConst.MODIFY_TAG_DEL.equals(VolteModifyTag)){
					      CSAppException.apperr(CrmCommException.CRM_COMM_103, "订购视频彩铃业务，不允许退订VoLTE业务！");					
					}
				}else{
					// 判断用户有没手机上网服务-wuwangfeng
 		    		IDataset onlineSvc = UserSvcInfoQry.getSvcUserId(uca.getUserId(), "22");
 					if (IDataUtil.isEmpty(onlineSvc)) {
 						CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户没有上网功能，不能订购VOLTE服务！");
 						return ;
 					} else {
 						SvcTradeData addVolteTrade = tradeSvcs.get(i).clone();	//新增VoLTE服务开通
 						addVolteTrade.setInstId(SeqMgr.getInstId());
 						addVolteTrade.setElementId(VolteServiceId);
 						btd.add(uca.getSerialNumber(), addVolteTrade);						
 					}
				}
   				//btd.getMainTradeData().setPfWait("1");	//设置闭环     //BUG20190227100857于产品变更服务类预约工单状态为F导致无法完工,需要屏蔽闭环设置
   			}
   		}
	}
	
	
	//2.会员包与视频彩铃功能关联开通:如果用户无视频彩铃功能，申请开通会员包时，实现会员包与视频彩铃功能的关联开通。
	//3.用户申请退订视频彩铃功能，会员包需同时关联退订；允许用户单独退订会员包。
	private void dealMemberPackOper(BusiTradeData btd,UcaData uca,IDataset commData) throws Exception{
		if(IDataUtil.isNotEmpty(commData))
        {
			for(int i = 0; i < commData.size(); i++)
	        {
				String MemberPackServiceId = commData.getData(i).getString("PARA_CODE4");	//视频彩铃基础会员包(平台服务)
				String ColorRingServiceId = commData.getData(i).getString("PARA_CODE1");	//视频彩铃
				String VolteServiceId = commData.getData(i).getString("PARA_CODE2");	//VoLTE服务
				
		   		List<PlatSvcTradeData> tradePlatSvcs = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
			    List<SvcTradeData> ColorRingSvc = uca.getUserSvcBySvcId(ColorRingServiceId);
			    List<SvcTradeData> VolteSvc = uca.getUserSvcBySvcId(VolteServiceId);
		 		if(DataUtils.isNotEmpty(tradePlatSvcs)){
		 			for(int j = 0; j < tradePlatSvcs.size(); j++){
		 		    	String modifyTag = tradePlatSvcs.get(j).getModifyTag();
		 		    	String serviceId = tradePlatSvcs.get(j).getElementId();
		 		    	if(BofConst.MODIFY_TAG_ADD.equals(modifyTag) && serviceId.equals(MemberPackServiceId)){
			 		    	if(DataUtils.isEmpty(ColorRingSvc)){
			 		    		// 判断用户有没手机上网服务-wuwangfeng
			 		    		IDataset onlineSvc = UserSvcInfoQry.getSvcUserId(uca.getUserId(), "22");
			 					if (IDataUtil.isEmpty(onlineSvc)) {
			 						CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户没有上网功能，不能订购VOLTE服务！");
			 						return ;
			 					} else {			 		    		
				 		   			SvcTradeData svcParam = new SvcTradeData();		//关联订购视频彩铃
						    		svcParam.setUserId(uca.getUserId());
						    		svcParam.setUserIdA("-1");
						    		svcParam.setProductId(uca.getProductId());
						    		svcParam.setPackageId("-1");
						    		svcParam.setElementId(ColorRingServiceId);
						    		svcParam.setMainTag("0");
						    		svcParam.setInstId(SeqMgr.getInstId());
						    		svcParam.setStartDate(tradePlatSvcs.get(j).getStartDate());
						    		svcParam.setEndDate(tradePlatSvcs.get(j).getEndDate());
						    		svcParam.setModifyTag(BofConst.MODIFY_TAG_ADD);
						            btd.add(uca.getSerialNumber(), svcParam);
						            
						            if(DataUtils.isEmpty(VolteSvc)){	//关联订购视频彩铃后，volte服务也需要同时绑上
						            	SvcTradeData addVolteTrade = svcParam.clone();
						            	addVolteTrade.setInstId(SeqMgr.getInstId());
										addVolteTrade.setElementId(VolteServiceId);
										btd.add(uca.getSerialNumber(), addVolteTrade);
						            }
			 					}
			 		    	}
		 		    	}
		 			}
		   		}
		
			    List<PlatSvcTradeData> MemberSvc = uca.getUserPlatSvcByServiceId(MemberPackServiceId);
			    logger.debug("VoLteAndColorRingAction ColorRingSvc"+ColorRingSvc.toString());
			    logger.debug("VoLteAndColorRingAction MemberSvc"+MemberSvc.toString());
			    if(DataUtils.isNotEmpty(ColorRingSvc)){
					for(int k=0; k<ColorRingSvc.size(); k++){
						String ColorRingModifyTag = ColorRingSvc.get(k).getModifyTag();
				    	if(BofConst.MODIFY_TAG_DEL.equals(ColorRingModifyTag)){
				    		if(DataUtils.isNotEmpty(MemberSvc)){
								PlatSvcTradeData delMemberSvc = MemberSvc.get(0).clone();	//关联退订会员包
								delMemberSvc.setEndDate(btd.getRD().getAcceptTime());
								delMemberSvc.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
								delMemberSvc.setBizStateCode(PlatConstants.STATE_CANCEL);
								delMemberSvc.setOprSource("08");
								delMemberSvc.setIsNeedPf("1");
								delMemberSvc.setOperTime(btd.getRD().getAcceptTime());
								delMemberSvc.setModifyTag(BofConst.MODIFY_TAG_DEL);
								btd.add(uca.getSerialNumber(), delMemberSvc);
				    		}
				    	}
				    }
			    }
	    
	        }
	
        }
	}
}
