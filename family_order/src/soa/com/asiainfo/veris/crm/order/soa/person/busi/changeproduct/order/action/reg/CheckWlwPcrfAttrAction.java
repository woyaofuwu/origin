package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;


/**
 * 处理PCRF属性问题ServiceCode, OperType,ServiceUsageState
 * @author MSI-
 *
 */
public class CheckWlwPcrfAttrAction implements ITradeAction {

	private static final Logger logger = Logger.getLogger(CheckWlwPcrfAttrAction.class);
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		// TODO Auto-generated method stub
		UcaData uca = btd.getRD().getUca();
		if(!"PWLW".equals(uca.getBrandCode()))
		{
            return;
        }
		
		AttrTradeData atAudiInfo0898 = null;
		String strAddAudiInfo0898 = "";
		String strDelAudiInfo0898 = "";
		String strOraAudiInfo0898 = "";
		
		String isAddDiscount = "";
		String isDelDiscount = "";
		String isOraDiscount = "";
		
		List<AttrTradeData> lsAudiInfo0898 = uca.getUserAttrsByAttrCode("AudiInfo0898");
		if(lsAudiInfo0898.size() > 0 && lsAudiInfo0898 != null)
		{
			for (int i = 0; i < lsAudiInfo0898.size(); i++) 
			{
				atAudiInfo0898 = lsAudiInfo0898.get(i);
				String strModifyTag = atAudiInfo0898.getModifyTag();
				if("0".equals(strModifyTag))
				{
					strAddAudiInfo0898 = atAudiInfo0898.getAttrValue();
				}
				else if("1".equals(strModifyTag))
				{
					strDelAudiInfo0898 = atAudiInfo0898.getAttrValue();
				}
				else
				{
					strOraAudiInfo0898 = atAudiInfo0898.getAttrValue();
				}
			}
		}
		
		List<AttrTradeData> ls20171211 = uca.getUserAttrsByAttrCode("20171211");
		if(ls20171211.size() > 0 && ls20171211 != null)
		{
			for (int i = 0; i < ls20171211.size(); i++) 
			{
				AttrTradeData at20171211 = ls20171211.get(i);
				String str20171211 = at20171211.getAttrValue();
				String strModifyTag = at20171211.getModifyTag();
				String strElemetID = at20171211.getElementId();
				
				if("0".equals(strModifyTag) || "1".equals(strModifyTag))
				{
					boolean bExtend = StaffPrivUtil.isPriv(CSBizBean.getVisit().getStaffId(), "CHECKWLWPCRFATTR", "1");
					if(!bExtend)
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工号无权限修改本省折扣率。");
					}
				}
				
				int n20171211 = 0;
				try 
				{
					n20171211 = Integer.parseInt(str20171211);
				} 
				catch (Exception e) 
				{
					logger.debug(e);
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "本省折扣率范围请填写0到100，请正确填写。");
				}
				if(n20171211 < 0 || n20171211 > 100)
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "本省折扣率范围请填写0到100，请正确填写。。");
				}
				
				if("0".equals(strModifyTag))
				{
					if(n20171211 < 60)
					{
						if(StringUtils.isBlank(strOraAudiInfo0898) && StringUtils.isBlank(strAddAudiInfo0898))
						{
							CSAppException.apperr(CrmCommException.CRM_COMM_103, "本省折扣率低于6折，请填写审批工单号。");
						}
					}
					List<AttrTradeData> lsDiscount = uca.getUserAttrsByAttrCode("Discount");
					if(lsDiscount.size() > 0 && lsDiscount != null)
					{
						for (int j = 0; j < lsDiscount.size(); j++) 
						{
							AttrTradeData atDiscount = lsDiscount.get(j);
							String strMT = atDiscount.getModifyTag();
							String strDiscountId = atDiscount.getElementId();
							if("0".equals(strMT) && strElemetID.equals(strDiscountId))
							{
								isAddDiscount = atDiscount.getAttrValue();
								atDiscount.setAttrValue("100");
								atDiscount.setModifyTag("0");
							}
							else if("1".equals(strMT)  && strElemetID.equals(strDiscountId))
							{
								isDelDiscount = atDiscount.getAttrValue();
								atDiscount.setAttrValue("100");
								atDiscount.setModifyTag("0");
							}
							else
							{
								isOraDiscount = atDiscount.getAttrValue();
								atDiscount.setAttrValue("100");
								atDiscount.setModifyTag("2");
								btd.add(uca.getSerialNumber(), atDiscount);
							}
						}	
					}
				}
				else if("1".equals(strModifyTag))
				{
					if(StringUtils.isNotBlank(strOraAudiInfo0898) || StringUtils.isNotBlank(strAddAudiInfo0898))
					{
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "清空本省折扣率，同时也要清空审批工单号，请正确操作");
					}
				}
				else
				{
					if(n20171211 < 60)
					{
						if(StringUtils.isBlank(strOraAudiInfo0898) && StringUtils.isBlank(strAddAudiInfo0898))
						{
							CSAppException.apperr(CrmCommException.CRM_COMM_103, "本省折扣率低于6折，请填写审批工单号。。");
						}
					}
					
					List<AttrTradeData> lsDiscount = uca.getUserAttrsByAttrCode("Discount");
					if(lsDiscount.size() > 0 && lsDiscount != null)
					{
						for (int j = 0; j < lsDiscount.size(); j++) 
						{
							AttrTradeData atDiscount = lsDiscount.get(j);
							String strMT = atDiscount.getModifyTag();
							String strDiscountId = atDiscount.getElementId();
							if("0".equals(strMT) && strElemetID.equals(strDiscountId))
							{
								isAddDiscount = atDiscount.getAttrValue();
								atDiscount.setAttrValue("100");
								atDiscount.setModifyTag("0");
							}
							else if("1".equals(strMT)  && strElemetID.equals(strDiscountId))
							{
								isDelDiscount = atDiscount.getAttrValue();
								atDiscount.setAttrValue("100");
								atDiscount.setModifyTag("0");
							}
							else
							{
								isOraDiscount = atDiscount.getAttrValue();
								/*atDiscount.setAttrValue("100");
								atDiscount.setModifyTag("0");
								btd.add(uca.getSerialNumber(), atDiscount);*/
							}
						}	
					}
				}
			}
		}
		else
		{
			if(StringUtils.isNotBlank(strAddAudiInfo0898))
			{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "填写审批工单号，同时也要填写本省折扣率，请正确填写本省折扣率。");
			}
		}
		//QR-20190617-07选择办理通用4G_GRRS服务属性异常问题 wuhao5 20190628 
		//该段代码转至CheckWlwAttrValueAction
//		List<SvcTradeData> tradesvclist  = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
//		System.out.print("wuhao5>>checkwlw" + tradesvclist);
//		if(tradesvclist.size() > 0 && tradesvclist != null)
//		{	
//			for(SvcTradeData svcTrade: tradesvclist)
//			{
//				String strSvcID = svcTrade.getElementId();
//				List<AttrTradeData> tradeAttrlist = btd.getTradeDatas(TradeTableEnum.TRADE_ATTR);
//				System.out.print("wuhao5>>checkwlw2" + tradeAttrlist);
//				if(tradeAttrlist.size() > 0 && tradeAttrlist != null)
//				{	
//					IDataset DataCommunicationServices = CommparaInfoQry.getCommparaByAttrCode1("CSM","9014","I00010100085","ZZZZ",null);
//					IDataset CustomizedDataCommunicationServices = CommparaInfoQry.getCommparaByAttrCode1("CSM","9014","I00010100092","ZZZZ",null);
//
//					//String DataCommunicationService = CommparaInfoQry.getCommparaByAttrCode1("CSM","9014","I00010100085","ZZZZ",null).first().getString("PARAM_CODE");
//					//String CustomizedDataCommunicationService = CommparaInfoQry.getCommparaByAttrCode1("CSM","9014","I00010100092","ZZZZ",null).first().getString("PARAM_CODE");
//					String tradeSvcId = svcTrade.getElementId();
//					boolean notInComm = true;
//					System.out.print("wuhao5>>checkwlw3 tradeSvcId:" + tradeSvcId + "DataCommunicationServices" + DataCommunicationServices + "CustomizedDataCommunicationServices" + CustomizedDataCommunicationServices);
//					if(IDataUtil.isNotEmpty(DataCommunicationServices) && DataCommunicationServices.size()>0)
//					{
//						for(int i = 0 ; i < DataCommunicationServices.size() ; i++)
//						{
//							String commSvcId = DataCommunicationServices.getData(i).getString("PARAM_CODE");
//							if(tradeSvcId.equals(commSvcId))
//							{
//								notInComm = false;
//								break;
//							}
//						}
//					}
//					if(IDataUtil.isNotEmpty(CustomizedDataCommunicationServices) && CustomizedDataCommunicationServices.size()>0){
//						for(int i = 0 ; i < CustomizedDataCommunicationServices.size() ; i++)
//						{
//							String commSvcId = CustomizedDataCommunicationServices.getData(i).getString("PARAM_CODE");
//							if(tradeSvcId.equals(commSvcId))
//							{
//								notInComm = false;
//								break;
//							}
//						}
//					}
//					if(notInComm)
//					{
//						continue;//非数据通信服务、专用数据通信服务没有此规则
//					}
//					
//					boolean servcodeFlag = false;
//					boolean opertypeFlag = false;
//					boolean stateFlag = false;
//					for(AttrTradeData attrTrade: tradeAttrlist)
//					{
//						String strAttrModifyTag = attrTrade.getModifyTag();
//						String strAttrCode = attrTrade.getAttrCode();
//						String strAttrValue = attrTrade.getAttrValue();
//						if("0".equals(strAttrModifyTag) || "1".equals(strAttrModifyTag))
//						{
//							System.out.print("wuhao5>>checkwlw3.1 attrTrade : "+ attrTrade + "strSvcID:" + strSvcID + "ElementId:" + attrTrade.getElementId());
//
//							if(strSvcID.equals(attrTrade.getElementId()))
//							{
//								if("ServiceCode".equals(strAttrCode) && StringUtils.isNotBlank(strAttrValue))
//								{
//									servcodeFlag = true;
//								}
//								if("OperType".equals(strAttrCode) && StringUtils.isNotBlank(strAttrValue))
//								{
//									opertypeFlag = true;
//								}
//								if("ServiceUsageState".equals(strAttrCode) && StringUtils.isNotBlank(strAttrValue))
//								{
//									stateFlag = true;
//								}
//							}
//						}
//					}
//					/*if((servcodeFlag || opertypeFlag || stateFlag) && !(servcodeFlag && opertypeFlag && stateFlag))
//					{				
//						CSAppException.apperr(CrmCommException.CRM_COMM_103,"如果需要订购或退订控制策略，用户订购业务唯一代码、操作类型、业务配额状态只能全部不为空或者全部为空！");
//					}*/
//					System.out.print("wuhao5>>checkwlw4 servcodeFlag:" + servcodeFlag + "opertypeFlag" + opertypeFlag + "stateFlag" + stateFlag);
//					if(servcodeFlag || opertypeFlag || stateFlag)
//					{
//						int nCount = 0;
//						List<AttrTradeData> lsServiceCode = uca.getUserAttrsByAttrCode("ServiceCode");
//						if(lsServiceCode.size() > 0 && lsServiceCode != null)
//						{
//							for (int i = 0; i < lsServiceCode.size(); i++) 
//							{
//								AttrTradeData atServiceCode = lsServiceCode.get(i);
//								String strMT = atServiceCode.getModifyTag();
//								String strAttrValue = atServiceCode.getAttrValue();
//								String strElementId = atServiceCode.getElementId();
//								if("0".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
//								{
//									servcodeFlag = true;
//									break;
//								}
//								else if("1".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
//								{
//									servcodeFlag = false;
//									nCount++;
//								}
//								else if(strSvcID.equals(strElementId))
//								{
//									servcodeFlag = true;
//								}
//							}
//						}
//						List<AttrTradeData> lsOperType = uca.getUserAttrsByAttrCode("OperType");
//						if(lsOperType.size() > 0 && lsOperType != null)
//						{
//							for (int i = 0; i < lsOperType.size(); i++) 
//							{
//								AttrTradeData atOperType = lsOperType.get(i);
//								String strMT = atOperType.getModifyTag();
//								String strAttrValue = atOperType.getAttrValue();
//								String strElementId = atOperType.getElementId();
//								if("0".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
//								{
//									opertypeFlag = true;
//									break;
//								}
//								else if("1".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
//								{
//									opertypeFlag = false;
//									nCount++;
//								}
//								else if(strSvcID.equals(strElementId))
//								{
//									opertypeFlag = true;
//								}
//							}
//						}
//						List<AttrTradeData> lsServiceUsageState = uca.getUserAttrsByAttrCode("ServiceUsageState");
//						if(lsServiceUsageState.size() > 0 && lsServiceUsageState != null)
//						{
//							for (int i = 0; i < lsServiceUsageState.size(); i++) 
//							{
//								AttrTradeData atServiceUsageState = lsServiceUsageState.get(i);
//								String strMT = atServiceUsageState.getModifyTag();
//								String strAttrValue = atServiceUsageState.getAttrValue();
//								String strElementId = atServiceUsageState.getElementId();
//								if("0".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
//								{
//									stateFlag = true;
//									break;
//								}
//								else if("1".equals(strMT) && StringUtils.isNotBlank(strAttrValue) && strSvcID.equals(strElementId))
//								{
//									stateFlag = false;
//									nCount++;
//								}
//								else if(strSvcID.equals(strElementId))
//								{
//									stateFlag = true;
//								}
//							}
//						}
//						System.out.print("wuhao5>>checkwlw5 servcodeFlag:" + servcodeFlag + "opertypeFlag" + opertypeFlag + "stateFlag" + stateFlag + "nCount" + nCount);
//						if((!servcodeFlag || !opertypeFlag || !stateFlag) && (nCount != 3))
//						{
//							CSAppException.apperr(CrmCommException.CRM_COMM_103,"如果需要订购或退订控制策略，用户订购业务唯一代码、操作类型、业务配额状态只能全部不为空或者全部为空！");
//						}
//					}
//				}
//			}
//		}	
	}
}
