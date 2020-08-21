package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustFamilyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyCreateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyMemberData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardBaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.requestdata.SimCardReqData;


public class CheckVolteBusinessAction implements ITradeAction{
	
	

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		UcaData uca= btd.getRD().getUca();
		String userId=uca.getUserId();
		String tradeTypeCode=btd.getTradeTypeCode();
		
		
		boolean isSubscribeWise = false;
		String serviceName = "";
		String resultDesc = "";
		
		String param= "VOLTE_CHECK";
		IDataset CompareServices = StaticInfoQry.getStaticValueByTypeId(param);
		
		
		//开户时，选择办理volte业务，则不能选择智能网、彩印、彩铃业务
		if(tradeTypeCode.equals("10") || tradeTypeCode.equals("500") || tradeTypeCode.equals("700")){		//用户开户
			
			List<SvcTradeData> svcTradeDatas=uca.getUserSvcs();
			if(svcTradeDatas!=null&&svcTradeDatas.size()>0){
				
				boolean isSubscribe190=false;
				isSubscribeWise=false;
				
				for(int i=0,size=svcTradeDatas.size();i<size;i++){
					SvcTradeData svcTradeData=svcTradeDatas.get(i);
					
					String modifyTag=svcTradeData.getModifyTag();
					String serviceId=svcTradeData.getElementId();
					String elementTypecode=svcTradeData.getElementType();
					
					//如果订购了VLOTE服务
					if(elementTypecode.equals("S")&&modifyTag.equals(BofConst.MODIFY_TAG_ADD)
							&&serviceId.equals("190")){
						isSubscribe190=true;
						break;
					}
					
				}
				
				
				
				if(isSubscribe190){		//如果订购190，验证是否订购 智能网、彩印、彩铃业务
					for(int i=0,size=svcTradeDatas.size();i<size;i++){
						SvcTradeData svcTradeData=svcTradeDatas.get(i);
						
						String modifyTag=svcTradeData.getModifyTag();
						String serviceId=svcTradeData.getElementId();
						String elementTypecode=svcTradeData.getElementType();
						
						serviceName = Compare(serviceId, CompareServices);
						//是否订购 智能网、彩印、彩铃业务
						if(!"".equals(serviceName) && elementTypecode.equals("S")&&modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
								
								isSubscribeWise=true;
								if("".equals(resultDesc))
								{
									resultDesc="用户已订购【服务】[190|volte服务]，不能同时订购【服务】["+serviceId + "|" + serviceName + "]";
								}else
								{
									resultDesc += ";" + "【服务】["+serviceId + "|" + serviceName + "]";
								}
						}
						
									
					}
					
					
					if(isSubscribeWise){
						//报错信息
						CSAppException.apperr(CrmCommException.CRM_COMM_103, resultDesc);
					}
					
				}
				
			}
			
		}else if(tradeTypeCode.equals("110") || tradeTypeCode.equals("116")){		//产品变更
			/*
			 * 以下是验证volte与一些服务的互斥
			 */
			List<SvcTradeData> svcTradeDatas=uca.getUserSvcs();
			if(svcTradeDatas!=null&&svcTradeDatas.size()>0){
				
				boolean isSubscribeSvc=false;
				IDataset svcSet=UserSvcInfoQry.getSvcUserIdPf(userId, "190");
				if(IDataUtil.isNotEmpty(svcSet)){
					
					isSubscribeSvc=true;
					
					for(int i=0,size=svcTradeDatas.size();i<size;i++){
						SvcTradeData svcTradeData=svcTradeDatas.get(i);
						
						String modifyTag=svcTradeData.getModifyTag();
						String serviceId=svcTradeData.getElementId();
						String elementTypecode=svcTradeData.getElementType();
						
						//是否订购 智能网、彩印、彩铃业务
						if(elementTypecode.equals("S")&&modifyTag.equals(BofConst.MODIFY_TAG_DEL)
								&&serviceId.equals("190")){
							isSubscribeSvc=false;
							break;
						}
					}
				}
				
				
				if(isSubscribeSvc){
					isSubscribeWise = false;
					for(int i=0,size=svcTradeDatas.size();i<size;i++){
						SvcTradeData svcTradeData=svcTradeDatas.get(i);
						
						String modifyTag=svcTradeData.getModifyTag();
						String serviceId=svcTradeData.getElementId();
						String elementTypecode=svcTradeData.getElementType();
						
						serviceName = Compare(serviceId, CompareServices);
						
						//是否订购 智能网、彩印、彩铃业务
						if( !"".equals(serviceName) && elementTypecode.equals("S")&&modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
								
								isSubscribeWise=true;
								if("".equals(resultDesc))
								{
									resultDesc="用户已订购【服务】[190|volte服务]，不能同时订购【服务】["+ serviceId + "|" + serviceName + "]";
								}else
								{
									resultDesc += ";" + "【服务】["+ serviceId + "|" + serviceName + "]";
								}
						}
						
						
					}
					
					if(isSubscribeWise){
						//报错信息
						CSAppException.apperr(CrmCommException.CRM_COMM_103, resultDesc);
					}
				}
				
				
				for(int i=0,size=svcTradeDatas.size();i<size;i++){
					SvcTradeData svcTradeData=svcTradeDatas.get(i);
					
					String modifyTag=svcTradeData.getModifyTag();
					String serviceId=svcTradeData.getElementId();
					String elementTypecode=svcTradeData.getElementType();
					
					//如果订购了VLOTE服务
					if(elementTypecode.equals("S")&&modifyTag.equals(BofConst.MODIFY_TAG_ADD)
							&&serviceId.equals("190")){
						/*
						 * 如果用户已经拥有了下面了服务：智能网、彩印、彩铃业务，
						 * 就不能订购VOLTE服务
						 */
						//是否订购 智能网、彩印、彩铃业务
						for(int j=0,sizej = CompareServices.size() ;j<sizej;j++){
							IDataset set=UserSvcInfoQry.getSvcUserIdPf(userId, CompareServices.getData(j).getString("DATA_ID"));
							if(IDataUtil.isNotEmpty(set)){
								CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户已订购【服务】[" + CompareServices.getData(j).getString("DATA_ID") + "|" + CompareServices.getData(j).getString("DATA_NAME") + "]" + ",不能同时订购【服务】[190|volte服务]");
								return ;
							}
						}
					}
				}
				
				
				if(svcTradeDatas!=null&&svcTradeDatas.size()>0){
					
					boolean isSubscribe190=false;
					isSubscribeWise=false;
					
					for(int i=0,size=svcTradeDatas.size();i<size;i++){
						SvcTradeData svcTradeData=svcTradeDatas.get(i);
						
						String modifyTag=svcTradeData.getModifyTag();
						String serviceId=svcTradeData.getElementId();
						String elementTypecode=svcTradeData.getElementType();
						
						//如果订购了VLOTE服务
						if(elementTypecode.equals("S")&&modifyTag.equals(BofConst.MODIFY_TAG_ADD)
								&&serviceId.equals("190")){
							isSubscribe190=true;
							break;
						}
						
					}
					
					
					
					if(isSubscribe190){		//如果订购190，验证是否订购 智能网、彩印、彩铃业务
						for(int i=0,size=svcTradeDatas.size();i<size;i++){
							SvcTradeData svcTradeData=svcTradeDatas.get(i);
							
							String modifyTag=svcTradeData.getModifyTag();
							String serviceId=svcTradeData.getElementId();
							String elementTypecode=svcTradeData.getElementType();
							
							serviceName = Compare(serviceId, CompareServices);
							//是否订购 智能网、彩印、彩铃业务
							if( !"".equals(serviceName) && elementTypecode.equals("S")&&modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
									
									isSubscribeWise=true;
									if("".equals(resultDesc))
									{
										resultDesc="用户已订购【服务】[190|volte服务]，不能同时订购【服务】["+serviceId + "|" + serviceName + "]";
									}else
									{
										resultDesc += ";" + "【服务】["+serviceId + "|" + serviceName + "]";
									}
							}
							
						}
						
						
						if(isSubscribeWise){
							//报错信息
							CSAppException.apperr(CrmCommException.CRM_COMM_103, resultDesc);
						}
					}
				}
			}
			
			
			/*
			 * 下面是验证：
			 * V网（全国V网、本地V网）、家庭网、校园卡业务、呼入限制）、（监务通）、VOLTE之间2和1互斥，就是用户如果
			 * 		用户上面三个分类当中任何两个的服务，那么就不允许办理另外一个业务
			 * 因为 监务通 是在集团里办理，所以先验证是否存在 监务通，如果存在的话，用户是否存在或者办理VOLTE和
			 */
			//判断用户是否办理监务通
			IDataset svc5860=UserSvcInfoQry.getSvcUserIdPf(userId, "5860");
			if(IDataUtil.isNotEmpty(svc5860)){
				String limitationSvcsType="VOLTE_CHECK_LIMITATION";
				IDataset limitedSvcs = StaticInfoQry.getStaticValueByTypeId(limitationSvcsType);
				
				//V网（全国V网、本地V网）、家庭网、校园卡业务、呼入限制）配置
				if(IDataUtil.isNotEmpty(limitedSvcs)){
					boolean isHasSvc1=false;
					String svc1Name="";
					boolean isHasSvc2=false;
					String svc2Name="";
					
					//验证本次办理的业务当中是否存在新增的互斥服务
					List<SvcTradeData> tradeSvcDatas=btd.get("TF_B_TRADE_SVC");
					if(tradeSvcDatas!=null&&tradeSvcDatas.size()>0){
						for(int i=0,size=tradeSvcDatas.size();i<size;i++){
							SvcTradeData tradeSvcData=tradeSvcDatas.get(i);
							
							String serviceId=tradeSvcData.getElementId();
							String modifytag=tradeSvcData.getModifyTag();
							
							if(isHasSvc1&&isHasSvc2){
								break;
							}
							
							if(modifytag.equals(BofConst.MODIFY_TAG_ADD)){
								String checkServiceName=Compare(serviceId, limitedSvcs);
								if(checkServiceName!=null&&!checkServiceName.trim().equals("")){
									isHasSvc1=true;
									svc1Name="【"+serviceId+"】"+checkServiceName;
								}else if(serviceId.equals("190")){	//volte
									isHasSvc2=true;
									svc2Name="【"+serviceId+"】VoLTE";
								}
							}
						}
					}
					
					if(isHasSvc1&&isHasSvc2){
						CSAppException.apperr(CrmCommException.CRM_COMM_103, 
													"用户已订购服务【5860】监务通，不能同时订购"+svc1Name+"和"+svc2Name+"。");
					}
					
					//核对用户是否已经办理了相关服务
					IDataset userSvcs=UserSvcInfoQry.queryUserAllSvc(userId);
					if(IDataUtil.isNotEmpty(userSvcs)){
						if(isHasSvc1){		//服务1
							//验证是否办理了volte服务
							for(int i=0,size=userSvcs.size();i<size;i++){
								IData userSvc=userSvcs.getData(i);
								String checkServiceId=userSvc.getString("SERVICE_ID");
								
								if(checkServiceId.equals("190")){
									CSAppException.apperr(CrmCommException.CRM_COMM_103, 
											"用户已订购服务【5860】监务通和【190】VoLTE，不能订购"+svc1Name+"。");
								}
							}
						}
						
						if(isHasSvc2){		//办理volte服务
							//验证是否办理了其他互斥服务
							for(int i=0,size=userSvcs.size();i<size;i++){
								IData userSvc=userSvcs.getData(i);
								String checkServiceId=userSvc.getString("SERVICE_ID");
								String checkServiceName=Compare(checkServiceId, limitedSvcs);
								
								if(checkServiceName!=null&&!checkServiceName.trim().equals("")){
									CSAppException.apperr(CrmCommException.CRM_COMM_103, 
											"用户已订购服务【5860】监务通和【"+checkServiceId+"】"+checkServiceName+"，不能订购服务【190】VoLTE。");
								}
							}
						}
					}
					
				}
			}
			
		}else if(tradeTypeCode.equals("142")){		//补换卡
			//如果订购了VOLTE服务，不能办理2/3G卡业务
			IDataset set=UserSvcInfoQry.getSvcUserIdPf(userId, "190");
			if(IDataUtil.isNotEmpty(set)){
				SimCardReqData simCardRD = (SimCardReqData) btd.getRD();
				SimCardBaseReqData newSimInfo=simCardRD.getNewSimCardInfo();
				
				String is4GCard=newSimInfo.getFlag4G();
				if(!is4GCard.equals("1")){
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户已订购【服务】[190|volte服务]，不能补换2/3G卡, 请先退订VOLTE服务！");
					return ;
				}
			}
			
		}
		else if(tradeTypeCode.equals("283"))
		{
			/*
			 * 对于新增的家庭网成员
			 * 先验证成员信息，然后验证主号
			 */
			boolean isLimited=false;
			StringBuilder limitedPhones=new StringBuilder();
			
			FamilyCreateReqData reqData = (FamilyCreateReqData) btd.getRD();
			List<FamilyMemberData> mebDataList = reqData.getMemberDataList();
			if(mebDataList!=null&&mebDataList.size()>0){
				for (int i = 0, size = mebDataList.size(); i < size; i++)
				{
					FamilyMemberData memberInfo = mebDataList.get(i);
	                UcaData mebUca = memberInfo.getUca();
	                String userIdB = mebUca.getUserId();
	                
	                boolean isLimitedResult=verifyLimitationSvcForFamily(userIdB);
	                if(isLimitedResult){
	                	
	                	if(!isLimited){
	                		isLimited=true;
	                	}
	                	
	                	if(limitedPhones.length()<1){
	                		limitedPhones.append(mebUca.getSerialNumber());
	                	}else{
	                		limitedPhones.append("、");
	                		limitedPhones.append(mebUca.getSerialNumber());
	                	}
	                	
	                }
	                
				}
			}
			
			List<CustFamilyTradeData> custFamily=btd.get("TF_B_TRADE_CUST_FAMILY");		//主号
			if(custFamily!=null&&custFamily.size()>0){
				for(int i=0,size=custFamily.size();i<size;i++){
					CustFamilyTradeData family=custFamily.get(i);
					
					if(family.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
						boolean isLimitedResult=verifyLimitationSvcForFamily(userId);
						
						if(isLimitedResult){	//失败
							
							if(!isLimited){
		                		isLimited=true;
		                	}
							
							if(limitedPhones.length()<1){
		                		limitedPhones.append(uca.getSerialNumber());
		                	}else{
		                		limitedPhones.append("、");
		                		limitedPhones.append(uca.getSerialNumber());
		                	}

			            }
					}
				}
			}
			
			
			if(isLimited){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"号码" + limitedPhones + "已订购服务【190】VoLTE和【5860】监务通，不能办理亲亲网业务！");
        	}
			
		}
		else if(tradeTypeCode.equals("143"))
		{
			//如果订购了VOLTE服务，不能办理改号网业务
			IDataset set=UserSvcInfoQry.getSvcUserIdPf(userId, "190");
			if(IDataUtil.isNotEmpty(set)){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户已订购【服务】[190|volte服务]，不能办理改号业务！");
				return ;
			}
		}
//		else if(tradeTypeCode.equals("3034"))
//		{
//			//如果订购了VOLTE服务，不能办理VPMN业务
//			IDataset set=UserSvcInfoQry.getSvcUserIdPf(userId, "190");
//			if(IDataUtil.isNotEmpty(set)){
//				CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户已订购【服务】[190|volte服务]，不能同时订购【服务】[860|VPMN服务]！");
//				return ;
//			}
//		}
		else if(tradeTypeCode.equals("3700"))
		{
			//如果订购了VOLTE服务，平台业务不能订购智能网 业务
			IDataset set=UserSvcInfoQry.getSvcUserIdPf(userId, "190");
			
			if(IDataUtil.isNotEmpty(set))
			{
				
				List<SvcTradeData> svcTradeDatas=uca.getUserSvcs();
				if(svcTradeDatas!=null&&svcTradeDatas.size()>0)
				{
					for(int i=0,size=svcTradeDatas.size();i<size;i++)
					{
						SvcTradeData svcTradeData=svcTradeDatas.get(i);
						
						String modifyTag=svcTradeData.getModifyTag();
						String serviceId=svcTradeData.getElementId();
						String elementTypecode=svcTradeData.getElementType();
						
						serviceName = Compare(serviceId, CompareServices);
						
						//是否订购 智能网、彩印、彩铃业务
						if( !"".equals(serviceName) && elementTypecode.equals("S")&&modifyTag.equals(BofConst.MODIFY_TAG_ADD))
						{
							CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户已订购【服务】[190|volte服务]，不能同时订购【服务】["+ serviceId + "|" + serviceName + "]");
						}
					}
				}
				
				
			}
		}
		
	}
	
	public String Compare(String service_id, IDataset CompareServices) throws Exception {
		if("".equals(service_id) || (CompareServices.size() == 0))
		{
			return "";
		}
		
		for(int index = 0; index < CompareServices.size(); index++)
		{
			if(service_id.equals(CompareServices.getData(index).getString("DATA_ID", "")))
			{
				return CompareServices.getData(index).getString("DATA_NAME", "");
			}
		}
		
		return "";
	}
	
	/**
	 * 验证相关互斥规则：
	 * 	 互斥内容是：
	 * 		（V网（全国V网、本地V网）、家庭网、校园卡业务、呼入限制）、（监务通）、VOLTE之间2和1互斥，就是用户如果
	 * 		用户上面三个分类当中任何两个的服务，那么就不允许办理另外一个业务
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public boolean verifyLimitationSvcForFamily(String userId)throws Exception{
		boolean result=false;
		
		//核对用户是否已经办理了相关服务
		IDataset userSvcs=UserSvcInfoQry.queryUserAllSvc(userId);
		if(IDataUtil.isNotEmpty(userSvcs)){
			boolean isHasSvc1=false;
			boolean isHasSvc2=false;
			
			for(int i=0,size=userSvcs.size();i<size;i++){
				IData userSvc=userSvcs.getData(i);
				String checkServiceId=userSvc.getString("SERVICE_ID");
				
				if(isHasSvc1&&isHasSvc2){
					break;
				}
				
				if(checkServiceId.equals("190")){	//VoLTE
					isHasSvc1=true;
				}else if(checkServiceId.equals("5860")){	//监务通
					isHasSvc2=true;
				}
			}
			
			if(isHasSvc1&&isHasSvc2){
				result=true;
			}
		}
		
		return result;
	}
	
}
