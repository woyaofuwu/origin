package com.asiainfo.veris.crm.order.soa.group.querygroupinfo;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class QueryGrpBizAuditInfoSVC extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 查询集团业务稽核工单信息
	 * @param input
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-7-10
	 */
	public IDataset queryGrpAuditInfos(IData input) throws Exception {
		QueryGrpBizAuditInfoBean memberFileBean = (QueryGrpBizAuditInfoBean) BeanManager.createBean(QueryGrpBizAuditInfoBean.class);
		if(!input.getString("TRADE_TYPE_CODE_STR", "").equals("")){
			String tradeTypeCodes="";
			String tradeTypeCodeStr=input.getString("TRADE_TYPE_CODE_STR", "");
			IDataset pStaticList = StaticUtil.getList(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE");
			if(IDataUtil.isNotEmpty(pStaticList)){
				for (int i=0,size=pStaticList.size();i<size;i++){
					IData pStatic=pStaticList.getData(i);
					if(IDataUtil.isNotEmpty(pStatic)){
						if(pStatic.getString("TRADE_TYPE", "").indexOf(tradeTypeCodeStr)>=0){
							tradeTypeCodes+="'"+pStatic.getString("TRADE_TYPE_CODE", "")+"',";
						}
					}
				}
				
			}
			if(!tradeTypeCodes.equals("")){
				tradeTypeCodes=tradeTypeCodes.substring(0, tradeTypeCodes.length()-1);
				input.put("TRADE_TYPE_CODES", tradeTypeCodes);
			}else{
				input.put("TRADE_TYPE_CODES", "-1");

			}
			
		}
		
		return memberFileBean.queryGrpAuditInfos(input, getPagination());
	}
	/**
	 * 查询集团业务工单稽核轨迹
	 * @param input
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-8-29
	 */
	public IDataset queryGrpAuditLogDetail(IData input) throws Exception {
		QueryGrpBizAuditInfoBean memberFileBean = (QueryGrpBizAuditInfoBean) BeanManager.createBean(QueryGrpBizAuditInfoBean.class);
		return memberFileBean.queryGrpAuditLogDetail(input, getPagination());
	}

	/**
	 * 稽核集团业务稽核工单
	 * @param input
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-7-10
	 */
	public IDataset dealSubmitAudit(IData input) throws Exception {
		QueryGrpBizAuditInfoBean memberFileBean = (QueryGrpBizAuditInfoBean) BeanManager.createBean(QueryGrpBizAuditInfoBean.class);
		String auditStr = input.getString("AUDIT_INFOS", "[]");
		IDataset auditInfos = new DatasetList(auditStr);
		if(IDataUtil.isNotEmpty(auditInfos)){
			//修改稽核工单
			for(int i=0;i<auditInfos.size();i++){
				IData each = auditInfos.getData(i);
				String pageState = each.getString("STATE");	//页面上传的标识稽核是否通过
				each.put("AUDIT_STAFF_ID", CSBizBean.getVisit().getStaffId());
				IDataset audits = memberFileBean.queryGrpAuditInfoByAuditId(each);
	        	IData auditInfo = audits.getData(0);
	        	String newState = this.getAuditState(pageState, auditInfo.getString("STATE"));
	        	IDataset sainfos = memberFileBean.checkSAinfo(each);
	        	each.put("STATE", newState);
		        int updCnt = memberFileBean.updateGrpAuditInfo(each);
		        //稽核通过不通过都给业务办理员发送短信
		        if(updCnt>0){
		        	String inStaffId = each.getString("IN_STAFF_ID", "");
		        	if(StringUtils.isNotBlank(inStaffId)){
		        		IDataset staffs = StaffInfoQry.queryValidStaffById(inStaffId);
		        		if(IDataUtil.isNotEmpty(staffs)){
		        			IData staff = staffs.getData(0);
		        			String staffSn = staff.getString("SERIAL_NUMBER", "");
		        			if(StringUtils.isNotBlank(staffSn)){
		        				String smsContent = "您办理的业务工单流水号/批次号"+each.getString("AUDIT_ID");
		        				String grpSn = auditInfo.getString("GRP_SN", "");
		        				String tradeTypeCode = auditInfo.getString("TRADE_TYPE_CODE", "");
		        				String tradeType = StaticUtil.getStaticValue(CSBizService.getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
		        				if("1".equals(pageState)){
		        					if(IDataUtil.isNotEmpty(sainfos)){
		        						if("8981".equals(tradeTypeCode) || "8982".equals(tradeTypeCode)){
		        							each.put("REMOVE_TAG", "0");
		        						}
		        						memberFileBean.updateSAinfo(each);
		        					}
		        					smsContent += "已稽核通过！";
		        				}else if("2".equals(pageState)){
		        					if(IDataUtil.isNotEmpty(sainfos)){
		        						if("2".equals(newState)){
		        							each.put("REMOVE_TAG", "3");//修改SA表状态为稽核不通过
		        						}else if("32".equals(newState)){
		        							each.put("REMOVE_TAG", "5");//修改SA表状态为整改不通过
		        						}
		        						memberFileBean.updateSAinfo(each);
		        					}
		        					smsContent += "稽核未通过，请及时修正！";
		        				}
		        				smsContent += "集团产品编码:"+grpSn+",业务类型:"+tradeType+"！";
			        			IData smsdata = new DataMap();
			        	        smsdata.put("EPARCHY_CODE", "0898");
			        	        smsdata.put("RECV_OBJECT", staffSn);// 工号手机号码
			        	        smsdata.put("NOTICE_CONTENT", smsContent);
			        	        smsdata.put("RECV_ID", "-1");
			        	        smsdata.put("SMS_TYPE_CODE", "20");// 用户办理业务通知
			        	        smsdata.put("FORCE_START_TIME", "");
			        	        smsdata.put("FORCE_END_TIME", "");
			        	        smsdata.put("REMARK", "");
			        	        SmsSend.insSms(smsdata);
		        			}
		        		}
		        	}
		        	//记录操作日志
		        	IData auditLog = new DataMap();
		        	auditLog.put("AUDIT_ID", each.getString("AUDIT_ID"));				//稽核单ID
		        	auditLog.put("OLD_STATE", auditInfo.getString("STATE"));			//操作前状态
		        	auditLog.put("NEW_STATE", each.getString("STATE"));					//操作后状态
		        	auditLog.put("OPER_DATE", SysDateMgr.getSysTime());					//操作时间
		        	auditLog.put("OPER_STAFF_ID", CSBizBean.getVisit().getStaffId());	//操作工号
		        	auditLog.put("OPER_DESC", each.getString("AUDIT_DESC"));			//操作描述
		        	Dao.insert("TF_F_GROUP_BASEINFO_AUDIT_LOG", auditLog);
		        }
			}
			
		}
        return new DatasetList();
    }
	/**
	 * 稽核集团业务转派稽核工单
	 * @param input
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-7-10
	 */
	public IDataset submitTransfer(IData input) throws Exception {
		IDataset returnList=new DatasetList();
		IData returnIdata=new DataMap();
		returnIdata.put("returnCode", "9999");
		returnIdata.put("returnStr", "转派失败");
		QueryGrpBizAuditInfoBean memberFileBean = (QueryGrpBizAuditInfoBean) BeanManager.createBean(QueryGrpBizAuditInfoBean.class);
		String auditStr = input.getString("AUDIT_INFOS", "[]");
		IDataset auditInfos = new DatasetList(auditStr);
		String auditStaffId =null;
		String smsContentStr ="工单流水号/批次号:";
		if(IDataUtil.isNotEmpty(auditInfos)){
			//修改稽核工单
			int updCnt =0;
			for(int i=0;i<auditInfos.size();i++){
				IData each = auditInfos.getData(i);
				String pageState = each.getString("STATE");	//页面上传的标识稽核是否通过
				each.put("AUDIT_STAFF_ID", CSBizBean.getVisit().getStaffId());
				IDataset audits = memberFileBean.queryGrpAuditInfoByAuditId(each);
	        	IData auditInfo = audits.getData(0);
	        	auditStaffId = each.getString("TRANSFER_AUDIT_STAFF_ID");
	        	smsContentStr+=each.getString("AUDIT_ID")+"、";
	        	auditInfo.put("AUDIT_STAFF_ID", each.getString("TRANSFER_AUDIT_STAFF_ID"));
	        	auditInfo.put("AUDIT_DESC",CSBizBean.getVisit().getStaffId()+"转派给"+each.getString("TRANSFER_AUDIT_STAFF_ID"));
	        	updCnt= memberFileBean.updateGrpAuditInfo(auditInfo);
		        //稽核通过不通过都给业务办理员发送短信
		        if(updCnt>0){
		        	returnIdata.put("returnCode", "0000");
		    		returnIdata.put("returnStr", "转派成功");
		    		
		        }
			}
			if(updCnt>0){
				
	        	if(StringUtils.isNotBlank(auditStaffId)){
	        		IDataset staffs = StaffInfoQry.queryValidStaffById(auditStaffId);
	        		if(IDataUtil.isNotEmpty(staffs)){
	        			IData staff = staffs.getData(0);
	        			String staffSn = staff.getString("SERIAL_NUMBER", "");
	        			if(StringUtils.isNotBlank(staffSn)){
	        				String smsContent = "工号为:"+CSBizBean.getVisit().getStaffId()+"已给您批量转单,"+smsContentStr.substring(0, (smsContentStr.length()-1))+",请及时查看及稽核!";
		        			IData smsdata = new DataMap();
		        	        smsdata.put("EPARCHY_CODE", "0898");
		        	        smsdata.put("RECV_OBJECT", staffSn);// 工号手机号码
		        	        smsdata.put("NOTICE_CONTENT", smsContent);
		        	        smsdata.put("RECV_ID", "-1");
		        	        smsdata.put("SMS_TYPE_CODE", "20");// 用户办理业务通知
		        	        smsdata.put("FORCE_START_TIME", "");
		        	        smsdata.put("FORCE_END_TIME", "");
		        	        smsdata.put("REMARK", "");
		        	        SmsSend.insSms(smsdata);
	        			}
	        		}
	        	}
			}
			
			
			
		}
		returnList.add(returnIdata);
        return returnList;
    }

	/**
	 * 提交整改处理
	 * @param input
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-8-20
	 */
	public IDataset dealSubmitReAudit(IData input) throws Exception {
		QueryGrpBizAuditInfoBean memberFileBean = (QueryGrpBizAuditInfoBean) BeanManager.createBean(QueryGrpBizAuditInfoBean.class);
		String auditStr = input.getString("AUDIT_INFOS", "[]");
		IDataset auditInfos = new DatasetList(auditStr);
		if(IDataUtil.isNotEmpty(auditInfos)){
			//修改稽核工单记录整改信息
			for(int i=0;i<auditInfos.size();i++){
				IData each = auditInfos.getData(i);
				IDataset audits = memberFileBean.queryGrpAuditInfoByAuditId(each);
	        	IData auditInfo = audits.getData(0);
	        	String reAuditDesc = auditInfo.getString("REAUDIT_DESC","");
	        	if(!"".equals(reAuditDesc)){
	        		each.put("RSRV_STR3", each.getString("REAUDIT_DESC"));
	        		each.remove("REAUDIT_DESC");
	        	}
	        	String newState = this.getAuditState(each.getString("STATE"), auditInfo.getString("STATE"));
	        	each.put("STATE", newState);
		        int updCnt = memberFileBean.updateGrpReAuditInfo(each);
		        //给审核工号发送提醒短信
		        if(updCnt>0){
		        	String auditStaffId = auditInfo.getString("AUDIT_STAFF_ID", "");
		        	if(StringUtils.isNotBlank(auditStaffId)){
		        		IDataset staffs = StaffInfoQry.queryValidStaffById(auditStaffId);
		        		if(IDataUtil.isNotEmpty(staffs)){
		        			IData staff = staffs.getData(0);
		        			String staffSn = staff.getString("SERIAL_NUMBER", "");
		        			if(StringUtils.isNotBlank(staffSn)){
		        				String grpSn = auditInfo.getString("GRP_SN", "");
		        				String tradeTypeCode = auditInfo.getString("TRADE_TYPE_CODE", "");
		        				String tradeType = StaticUtil.getStaticValue(CSBizService.getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", tradeTypeCode);
		        				String smsContent = "集团产品编码:"+grpSn+",业务类型:"+tradeType+",工单流水号/批次号"+each.getString("AUDIT_ID")+"已提交整改,请及时稽核!";
			        			IData smsdata = new DataMap();
			        	        smsdata.put("EPARCHY_CODE", "0898");
			        	        smsdata.put("RECV_OBJECT", staffSn);// 工号手机号码
			        	        smsdata.put("NOTICE_CONTENT", smsContent);
			        	        smsdata.put("RECV_ID", "-1");
			        	        smsdata.put("SMS_TYPE_CODE", "20");// 用户办理业务通知
			        	        smsdata.put("FORCE_START_TIME", "");
			        	        smsdata.put("FORCE_END_TIME", "");
			        	        smsdata.put("REMARK", "");
			        	        SmsSend.insSms(smsdata);
		        			}
		        		}
		        	}
		        	
		        	//记录操作日志
		        	IData auditLog = new DataMap();
		        	auditLog.put("AUDIT_ID", each.getString("AUDIT_ID"));				//稽核单ID
		        	auditLog.put("OLD_STATE", auditInfo.getString("STATE"));			//操作前状态
		        	auditLog.put("NEW_STATE", each.getString("STATE"));					//操作后状态
		        	auditLog.put("OPER_DATE", SysDateMgr.getSysTime());					//操作时间
		        	auditLog.put("OPER_STAFF_ID", CSBizBean.getVisit().getStaffId());	//操作工号
		        	if(!"".equals(each.getString("RSRV_STR3",""))){
		        		auditLog.put("OPER_DESC", each.getString("RSRV_STR3"));			//二次整改操作描述	
		        	}else{
		        		auditLog.put("OPER_DESC", each.getString("REAUDIT_DESC"));			//一次整改操作描述
		        	}
		        	Dao.insert("TF_F_GROUP_BASEINFO_AUDIT_LOG", auditLog);
		        }
			}
			
		}
        return new DatasetList();
    }
	
	/**
	 * 根据页面上的选择判断新的审核状态
	 * @param string
	 * @return
	 * @author chenzg
	 * @date 2018-8-29
	 */
	private String getAuditState(String state, String nowState) {
		String retState = nowState;
		//审核通过
		if("1".equals(state)){
			//当前为待审核，则新状态改为审核通过
			if("0".equals(nowState)){
				retState = "1";
			}
			//当前状态为已整改，则新状态为整改通过
			else if("3".equals(nowState)){
				retState = "31";
			}
			//当前状态为已二次 整改，则新状态为二次整改通过
			else if("4".equals(nowState)){
				retState = "41";
			}
		}
		//审核不通过
		else if("2".equals(state)){
			//当前为待审核，则新状态改为稽核不通过 
			if("0".equals(nowState)){
				retState = "2";
			}
			//当前状态为已整改，则新状态为整改不通过
			else if("3".equals(nowState)){
				retState = "32";
			}
			//当前状态为已二次 整改，则新状态为二次整改不通过
			else if("4".equals(nowState)){
				retState = "42";
			}
		}
		//提交整改
		else if("3".equals(state)){
			//当前为稽核不通过，则新状态改为已整改
			if("2".equals(nowState)){
				retState = "3";
			}
			//当前状态为整改不通过，则新状态为已二次整改
			else if("32".equals(nowState)){
				retState = "4";
			}
		}
		return retState;
	}
	
	/**
	 * 查询集团营销稽核工单的附件
	 * @param input
	 * @return
	 * @throws Exception
	 * @author chenzg
	 * @date 2018-7-10
	 */
	public IDataset queryGrpSaleAuditInfos(IData input) throws Exception {
		QueryGrpBizAuditInfoBean memberFileBean = (QueryGrpBizAuditInfoBean) BeanManager.createBean(QueryGrpBizAuditInfoBean.class);
		return memberFileBean.queryGrpSaleAuditInfos(input, getPagination());
	}
	
}
