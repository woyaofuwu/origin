package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.wideprereg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WideNetBookQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class WidePreRegBean extends CSBizBean
{
	private static Logger logger = Logger.getLogger(WidePreRegBean.class);

	public IDataset getPreRegInfosByFiveAddr(IData input) throws Exception 
	{
		IData param = new DataMap();
		param.put("HOME_ADDR", input.getString("HOME_ADDR"));
		return Dao.qryByCode("TF_F_WIDENET_BOOK", "SEL_PRE_REG_INFO_BY_FIVEADDR", param);
	}

	/**
	 * AEE自动任务
	 * 宽带需求收集地址汇总，打到预计阀值发送预警信息
	 * @param userInfo
	 * @throws Exception
	 */
	public void checkWidePreRegByAddrCollectEarlyWarning(IData userInfo) throws Exception 
	{
		//获取TF_F_WIDENET_BOOK表未登记的数量
		IDataset widePreRegInfos = WideNetBookQuery.getWidePreRegCollectList();
		
		//获取预警阀值数量
		IDataset paras = CommparaInfoQry.getCommparaAllCol("CSM", "702", "1", "0898");
		String eparchyCode = this.getTradeEparchyCode();
    	String	earlyWarNumsString = paras.getData(0).getString("PARA_CODE1");
    	int earlyWarNums = Integer.parseInt(earlyWarNumsString);
    	
    	IDataset paramsList = new DatasetList(); 	
    	
		if(DataSetUtils.isNotBlank(widePreRegInfos) && widePreRegInfos.size()>0){
			for(int i = 0 ; i < widePreRegInfos.size() ; i++){
				IData widePreRegInfo = widePreRegInfos.getData(i);
				if(Integer.parseInt(widePreRegInfo.getString("PRE_REG_NUMBER")) >= earlyWarNums){//当前地址宽带需求收集数量大于预计阀值，需预警
					paramsList.add(widePreRegInfo);
				}
			}
			
			
			if(DataSetUtils.isNotBlank(paramsList) && paramsList.size()>0){
				Dao.executeBatchByCodeCode("TF_F_WIDENET_BOOK", "UPD_PRE_STATUS_BY_ADDR", paramsList, eparchyCode);
				//发送预警信息
				for(int i = 0 ; i < paramsList.size() ; i++){
					String addr_code = paramsList.getData(i).getString("RSRV_STR1");
					String home_addr = paramsList.getData(i).getString("HOME_ADDR");
					String preRegNums = paramsList.getData(i).getString("PRE_REG_NUMBER");
					if(StringUtils.isNotBlank(addr_code)){
						String[] addr_codes = addr_code.split(",");
						String serialNumber = null;
						String contact_name = null;
						if(addr_codes.length > 0){
							for(int j = addr_codes.length-1 ; j >= 0 ; j--){
								if(StringUtils.isNotBlank(addr_codes[j])){
									IData param = new DataMap();
									param.put("ADDR_CODE", addr_codes[j]);
									IDataset addrContacts = this.getAddrContact(param);
									if(DataSetUtils.isNotBlank(addrContacts)){
										serialNumber = addrContacts.getData(0).getString("CONTACT_SN");
										contact_name = addrContacts.getData(0).getString("CONTACT_NAME");
										break;
									}
								}
							}
						}
						//给施工人员发送预警信息
						if(StringUtils.isNotBlank(serialNumber)){
							IData users = UcaInfoQry.qryUserInfoBySn(serialNumber);
							String noticeContent = "您好！"+home_addr+"宽带需求客户收集数量已达"+preRegNums+"条，可到宽带需求收集查询界面查询详单，请尽快推进该地区的宽带建设工作！";
							IData smsData = new DataMap();
					        smsData.put("RECV_OBJECT", serialNumber);
					        smsData.put("NOTICE_CONTENT", noticeContent);
					        smsData.put("BRAND_CODE", "");
					        if(IDataUtil.isEmpty(users)){
					        	smsData.put("RECV_ID", "0");
					        }else{
					        	smsData.put("RECV_ID", users.getString("USER_ID"));
					        }
					        SmsSend.insSms(smsData);
							
						}
						//给分公司发送预警信息
						if(addr_codes.length > 0){
							IData param = new DataMap();
							param.put("ADDR_CODE", addr_codes[0]);
							IDataset addrContacts = this.getAddrContact(param);
							if(DataSetUtils.isNotBlank(addrContacts)){
								String contactSn = addrContacts.getData(0).getString("CONTACT_SN");
								String contactName = addrContacts.getData(0).getString("CONTACT_NAME");
								if(!StringUtils.equals(contactSn, serialNumber)){//如果前面施工人员预警时是分公司号码，则不再给分公司预警
									IData users = UcaInfoQry.qryUserInfoBySn(contactSn);
									String noticeContent = contactName+ "您好！"+home_addr+"宽带需求客户收集数量已达"+preRegNums+"条，可到宽带需求收集查询界面查询详单，请尽快推进该地区的宽带建设工作！";
									IData smsData = new DataMap();
							        smsData.put("RECV_OBJECT", contactSn);
							        smsData.put("NOTICE_CONTENT", noticeContent);
							        smsData.put("BRAND_CODE", "");
							        if(IDataUtil.isEmpty(users)){
							        	smsData.put("RECV_ID", "0");
							        }else{
							        	smsData.put("RECV_ID", users.getString("USER_ID"));
							        }
							        SmsSend.insSms(smsData);
									
								}
							}
						}
						//给省公司预警
						IData inParam = new DataMap();
						inParam.put("ADDR_CODE", "0898");
						IDataset hainContacts = this.getAddrContact(inParam);
						if(DataSetUtils.isNotBlank(hainContacts)){
							String contactSn = hainContacts.getData(0).getString("CONTACT_SN");
							String contactName = hainContacts.getData(0).getString("CONTACT_NAME");
							IData users = UcaInfoQry.qryUserInfoBySn(contactSn);
							String noticeContent = contactName+ "您好！"+home_addr+"宽带需求客户收集数量已达"+preRegNums+"条，可到宽带需求收集查询界面查询详单，请尽快推进该地区的宽带建设工作！";
							IData smsData = new DataMap();
					        smsData.put("RECV_OBJECT", users.getString("SERIAL_NUMBER"));
					        smsData.put("NOTICE_CONTENT", noticeContent);
					        smsData.put("BRAND_CODE", users.getString("BRAND_CODE"));
					        if(IDataUtil.isEmpty(users)){
					        	smsData.put("RECV_ID", "0");
					        }else{
					        	smsData.put("RECV_ID", users.getString("USER_ID"));
					        }
					        SmsSend.insSms(smsData);
						}
					}
				}
			}
		}
	}
	
	public IDataset getAddrContact(IData param) throws Exception
	{
		IData inParam = new DataMap();
		inParam.put("ADDR_CODE", param.getString("ADDR_CODE"));
		return Dao.qryByCode("TF_F_WIDENET_ADDRCONTACT", "SEL_CONTACT_BY_ADDRCODE", param);
	}

//	/**
//	 * AEE自动任务
//	 * 宽带需求收集提供宽带能力通知
//	 * @param userInfo
//	 * @throws Exception
//	 */
//	public void checkWidePreRegByAddrCollectNotify(IData wideInfo) throws Exception 
//	{
//		String res_status = "4";//登记状态4:已开通
//		IDataset widePreRegInfos = WideNetBookQuery.getWidePreRegDredgeList(res_status);
//		if(DataSetUtils.isNotBlank(widePreRegInfos) && widePreRegInfos.size()>0){
//			for(int i = 0 ; i < widePreRegInfos.size() ; i++){
//				IData widePreRegInfo = widePreRegInfos.getData(i);
//				IData param = new DataMap();
//				param.put("INST_ID", widePreRegInfo.getString("INST_ID"));
//				Dao.executeUpdateByCodeCode("TF_F_WIDENET_BOOK", "UPD_PRE_DREDGE_STATUS", param);
//				String contactSn = widePreRegInfo.getString("CONTACT_SN");
//				IData userInfo = UcaInfoQry.qryUserInfoBySn(contactSn);
//				String noticeContent = "尊敬的客户：您办理的宽带预登记提供的地址，施工人员已安装施工，您可以到营业厅办理宽带业务。";
//				IData smsData = new DataMap();
//		        smsData.put("RECV_OBJECT", contactSn);
//		        smsData.put("NOTICE_CONTENT", noticeContent);
//		        smsData.put("BRAND_CODE", "");
//		        if(IDataUtil.isEmpty(userInfo)){
//		        	smsData.put("RECV_ID", "0");
//		        }else{
//		        	smsData.put("RECV_ID", userInfo.getString("USER_ID"));
//		        }
//		        SmsSend.insSms(smsData);
//			}
//		}
//	}

}
