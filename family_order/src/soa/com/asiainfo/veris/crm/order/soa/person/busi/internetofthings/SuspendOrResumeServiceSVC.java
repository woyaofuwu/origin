package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;

public class SuspendOrResumeServiceSVC extends CSBizService {

	private static final long serialVersionUID = 5256161209975981629L;

	/**
	 * 暂停或恢复物联网数据通信功能“GPRS基础服务”或“数据通信服务”
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData operSvcState(IData input) throws Exception 
	{
		IData result = new DataMap();
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");// 手机号
		//IDataUtil.chkParam(input, "OPR_CODE");// 操作代码
		String strOprCode = input.getString("OPR_CODE", "");
		String strDealCond = input.getString("DEAL_COND", "");
		String stType = input.getString("ST_TYPE", "");// 关停类型
		String dcType = input.getString("DCTYPE", "");// 信控类型
		String strDealSvcId = "";
		String strDealSvcInstId = "";
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		IData param = new DataMap();
		if("".equals(strOprCode) && !"".equals(strDealCond))
		{
			IData dtDealCond = new DataMap(strDealCond);
			if(IDataUtil.isNotEmpty(dtDealCond))
			{
				strOprCode = dtDealCond.getString("OPR_CODE", "");
				strDealSvcId = dtDealCond.getString("SERVICE_ID", "");
				if(strDealSvcId != null && !"".equals(strDealSvcId))
				{
					List<SvcTradeData> dtsSvcs = uca.getUserSvcBySvcId(strDealSvcId);
					if(CollectionUtils.isNotEmpty(dtsSvcs))
					{
						SvcTradeData dtSvc = dtsSvcs.get(0);
						strDealSvcInstId = dtSvc.getInstId();
					}
				}
			}
			
		}
		if("01".equals(strOprCode))//恢复
		{
			param.put("USER_ID", uca.getUserId());
			param.put("SERIAL_NUMBER", serialNumber);
			param.put("RESUME_SERVICE", strDealSvcId + "&" + strDealSvcInstId);
			param.put("OPR_CODE", strOprCode);
			
		}
		else if("02".equals(strOprCode))
		{//暂停
			stType = IDataUtil.chkParam(input, "ST_TYPE");// 关停类型
			dcType = IDataUtil.chkParam(input, "DCTYPE");// 信控类型
			String apnName = input.getString("APNNAME");//要关停的APN名称
			if("1".equals(stType))//双封顶关停
			{        
				param.put("MORE_OPER", "双封顶接口受理的标记");	
				param.put("USER_ID", uca.getUserId());
				param.put("SERIAL_NUMBER", serialNumber);
				param.put("OPR_CODE", strOprCode);
				IDataset compare3996 = CommparaInfoQry.getCommparaInfos("CSM", "3996", "IoTGprsSVC");
				if(IDataUtil.isEmpty(compare3996))
				{
					result.put("RESULT_CODE", "-1");
					result.put("RESULT_INFO", "数据库没有配置物联网双封顶对应服务");
					return result;
				}
				else
				{
					if("1".equals(dcType))//原有GPRS中小流量套餐，APN名称字段不出现	
					{		
						String strElementId = "";
						String strElementInstId = "";
						for (int i = 0; i < compare3996.size(); i++) 
						{
							IData dtCompare = compare3996.getData(i);
							String strServiceID = dtCompare.getString("PARA_CODE1", "");
							List<SvcTradeData> dtsSvcs = uca.getUserSvcBySvcId(strServiceID);
							if(CollectionUtils.isNotEmpty(dtsSvcs))
							{
								SvcTradeData dtSvc = dtsSvcs.get(0);
								strElementId = dtSvc.getElementId();
								strElementInstId = dtSvc.getInstId();
							}
						}
						if(!"".equals(strElementId))
						{
							param.put("SUSPEND_SERVICE", strElementId + "&" + strElementInstId);
						}
						else
						{
							result.put("RESULT_CODE", "-1");
							result.put("RESULT_INFO", "该用户没有找到GPRS服务");
							return result;
						}
					}
					else if("2".equals(dcType))//新资费带APNNAME属性
					{	
						result = dealApnDiscnt(input,result,param,compare3996);
						if(IDataUtil.isNotEmpty(result))
						{	
							return result;
						}
					}
				}
			}
			else if("2".equals(stType))//流量用尽关停
			{
				param.put("CLOSE_OPER", "流量用尽关停接口受理的标记");
				param.put("USER_ID", uca.getUserId());
				param.put("SERIAL_NUMBER", serialNumber);
				param.put("OPR_CODE", strOprCode);
				IDataset closesvcs = CommparaInfoQry.getCommPkInfo("CSM", "1115", "IoTGprsCloseSVC", "0898");
				if(closesvcs.isEmpty())
				{
					result.put("RESULT_CODE", "-1");
					result.put("RESULT_INFO", "数据库没有配置物联网APN对应服务");
					return result;
				}
				if("1".equals(dcType))//APN老资费套餐APNNAME不出现
				{                 
					String suspendservice  = "";
					String suspendserviceInstId = "";
					for(int i=0;i<closesvcs.size();i++)
					{
						String svcid = closesvcs.getData(i).getString("PARA_CODE1");	
						List<SvcTradeData> svcTradeData = uca.getUserSvcBySvcId(svcid);
					    if(!svcTradeData.isEmpty())
					    {
					    	//suspendservice = svcid;
					    	SvcTradeData dtSvc = svcTradeData.get(0);
					    	suspendservice = dtSvc.getElementId();
					    	suspendserviceInstId = dtSvc.getInstId();
					    	break;
					    }
					}
					if("".equals(suspendservice))
					{
						result.put("RESULT_CODE", "-1");
						result.put("RESULT_INFO", "用户没有订购物联网流量用尽关停对应的服务！");
						return result;
					}
					if ("02".equals(strOprCode))//暂停 
					{
					    param.put("SUSPEND_SERVICE", suspendservice  + "&" + suspendserviceInstId);
						//param.put("CLOSE_OPER", "流量用尽关停接口受理的标记");
					} 
				}
				else if("2".equals(dcType))
				{            //停止APN上网功能
					result = dealApnDiscnt(input, result, param, closesvcs);
					if(IDataUtil.isNotEmpty(result))
					{
						return result;
					}
				}
			}
		}
		IData value = new DataMap();
		value.put("SERIAL_NUMBER", serialNumber);
		value.put("USER_ID", uca.getUserId());
		//海南区分集团和个人的relation_type_code并不是很确定
		//2017-11-29 relation_type_code 9A
		
		String susserviceId = "";
		if("01".equals(strOprCode))
		{
			String resumeService = param.getString("RESUME_SERVICE","");
			if(StringUtils.isNotBlank(resumeService))
			{
				String susserviceIds[] = resumeService.split("&");
				susserviceId = susserviceIds[0];
			}
		}
		else if("02".equals(strOprCode))
		{
			String suspendService = param.getString("SUSPEND_SERVICE","");
			if(StringUtils.isNotBlank(suspendService))
			{
				String susserviceIds[] = suspendService.split("&");
				susserviceId = susserviceIds[0];
			}
		}
	        
        IDataset infos = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(uca.getUserId(), "9A",null);
        IDataset results = new DatasetList();
        boolean isPerson = false;
        if(IDataUtil.isNotEmpty(infos) && StringUtils.isNotEmpty(susserviceId)){
            IDataset service = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(uca.getUserId(), susserviceId);
            if(IDataUtil.isNotEmpty(service)){
                String userida = service.getData(0).getString("USER_ID_A");
                if("-1".equals(userida)){
                    isPerson = true;
                }
            }
        }
		
		//是集团成员的服务则调用集团成员的变更接口
		if(!isPerson && IDataUtil.isNotEmpty(infos))
		{
			//获取用户订购的集团成员服务
			IDataset result1 = CSAppCall.call("SS.WlwQuerySVC.qryUserServiceState", value);
			String suspendServiceId = param.getString("SUSPEND_SERVICE");
            String resumeServiceId = param.getString("RESUME_SERVICE");
            if (StringUtils.isNotEmpty(suspendServiceId))
            {
            	if(StringUtils.isNotBlank(suspendServiceId)){
    				String susserviceIds[] = suspendServiceId.split("&");
    				suspendServiceId = susserviceIds[0];
    			}
            	
                result1 = DataHelper.filter(result1, "SERVICE_ID="+suspendServiceId); 
                if (IDataUtil.isEmpty(result1))
                {
                    result.put("RESULT_CODE", "-1");
                    result.put("RESULT_INFO", "集团成员没有订购需要暂停的服务："+suspendServiceId);
                    return result;
                }
            }
            else if (StringUtils.isNotEmpty(resumeServiceId))
            {
            	if(StringUtils.isNotBlank(resumeServiceId)){
    				String susserviceIds[] = resumeServiceId.split("&");
    				resumeServiceId = susserviceIds[0];
    			}
                result1 =  DataHelper.filter(result1, "SERVICE_ID="+resumeServiceId);
                if(IDataUtil.isEmpty(result1))
                {
                    result.put("RESULT_CODE", "-1");
                    result.put("RESULT_INFO", "集团成员没有订购需要恢复的服务："+resumeServiceId);
                    return result;
                }
            }
            else
            {
                result.put("RESULT_CODE", "-1");
                result.put("RESULT_INFO", "此用户需要处理的服务不存在");
                return result;
            }
            param.put("ST_TYPE", stType);
            param.put("DCTYPE", dcType);
            param.put("USER_ID_A", result1.getData(0).getString("USER_ID_A")); 
            
            if(StringUtils.isNotBlank(suspendServiceId))
            {
            	String suspendService = "";
                IData service = result1.getData(0);
                suspendService = suspendService + service.getString("SERVICE_ID") + "," +service.getString("INST_ID") + "," +service.getString("START_DATE") + "," +service.getString("USER_ID_A") + ";";
                suspendService = suspendService.substring(0, suspendService.length()-1);
                param.put("SUSPEND_SERVICE", suspendService);
            }
            else if(StringUtils.isNotBlank(resumeServiceId))
            {
            	String resumeService = "";
                IData service = result1.getData(0);
                resumeService = resumeService + service.getString("SERVICE_ID") + "," +service.getString("INST_ID") + "," +service.getString("START_DATE") + "," +service.getString("USER_ID_A") + ";";
                resumeService = resumeService.substring(0, resumeService.length()-1);
                param.put("RESUME_SERVICE", resumeService);
            }
            
            results = CSAppCall.call("SS.SuspendResumeWlwServiceSVC.crtTrade", param);
			
		}
		else
		{	
			results = CSAppCall.call("SS.SuspendResumeServiceRegSVC.tradeReg", param);
		}
		if (IDataUtil.isNotEmpty(results)) 
		{
			IData operResult = results.getData(0);
			if (!"".equals(operResult.getString("TRADE_ID"))) 
			{
				result.put("RESULT_CODE", "0");
				result.put("RESULT_INFO", "trade ok");
			} 
			else 
			{
				result.put("RESULT_CODE", "-1");
				result.put("RESULT_INFO", operResult.getString("X_RESULTINFO"));
			}
		}
		return result;
	}
	
	public static IData dealApnDiscnt(IData input,IData result,IData param,IDataset closesvcs) throws Exception
	{
		String serialNumber = input.getString("SERIAL_NUMBER");
		String apnName = IDataUtil.chkParam(input, "APNNAME");
        //停止APN上网功能
		//1判断订购的APN
		//对于未开通PCRF控制策略的用户，发送基础通信服务的APN暂停请求；对于开通了PCRF控制策略的用户，发送基础通信服务中APN控制策略的关停请求（ServiceUsageState=2表示关停操作）		
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		IDataset usersvcs = UserSvcInfoQry.queryUserAllSvc(uca.getUserId());
		IDataset svcsfilter = new DatasetList();
		IDataset svcs = new DatasetList();
		for(int i=0; i<closesvcs.size(); i++)
		{
			IData idClosesvc = closesvcs.getData(i);
			String strClosesvc = idClosesvc.getString("PARA_CODE1", "");
			svcsfilter = DataHelper.filter(usersvcs, "SERVICE_ID=" + strClosesvc);
			if(IDataUtil.isNotEmpty(svcsfilter))
			{
				svcs.addAll(svcsfilter);
			}
		}
		if(IDataUtil.isEmpty(svcs))
		{
			result.put("RESULT_CODE", "-1");
			result.put("RESULT_INFO", "用户没有订购物联网APN对应服务");
			return result;
		}
		String suspandAttrservice = "";
		boolean flag = false;
		for(int i = 0; i < svcs.size(); i++)
		{
			IData svc = svcs.getData(i);
			//查询用户已订购的APNNAME属性
			IDataset usersvcAttrs = UserAttrInfoQry.getuserAttrByUserIdSvcId(uca.getUserId(), svc.getString("SERVICE_ID"));
			IDataset userattrsfilter = DataHelper.filter(usersvcAttrs, "ATTR_CODE=APNNAME,ATTR_VALUE=" + apnName);	
            if(IDataUtil.isNotEmpty(userattrsfilter))
            {
                suspandAttrservice = svc.getString("SERVICE_ID");
                suspandAttrservice = suspandAttrservice + "&" + svc.getString("INST_ID");
                flag = true;
                break;
            }
		}
		if(!flag)
		{
			result.put("RESULT_CODE", "-1");
			result.put("RESULT_INFO", "用户没有订购该APN："+apnName);
			return result;
		}	
		param.put("SUSPEND_SERVICE", suspandAttrservice);	
		return result;
	}
	
	/**
	 * 国际漫游用尽关停接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData IotInternationalRoaming(IData input) throws Exception {
		IData result = new DataMap();
		super.setRoute(input);
		input.put("OPR_CODE", "02");
		IDataUtil.chkParam(input, "OPR_SEQ");
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");// 手机号
		IDataUtil.chkParam(input, "PROVINCE_ID");
		IDataUtil.chkParam(input, "DCTYPE");
		IDataUtil.chkParam(input, "OPR_TIME");
		String stType = IDataUtil.chkParam(input, "ST_TYPE");// 关停类型
		String dcType = input.getString("DCTYPE");// 信控类型
		String instID = "";
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		if (input.getString("OPR_CODE").equals("02")) {// 暂停
			if ("1".equals(stType)) { // 国漫流量用尽关停
				param.put("CLOSE_OPER", "流量用尽关停接口受理的标记");
				IDataset closesvcs = CommparaInfoQry.getCommparaByAttrCode1("CSM", "9014", "I00010100421", "ZZZZ", null);// 通过全网编码获取到COMMPARA表中的记录
				if (closesvcs.isEmpty()) {
					CSAppException.apperr(BizException.CRM_BIZ_5,"数据库没有配置国漫流量用尽关停对应服务");
					return result;
				}
				if ("1".equals(dcType)) {// 关停国漫流量上网功能
					// 查询用户订购的服务
					String suspendservice = "";
					for (int j = 0; j < closesvcs.size(); j++) {
						String svcid = closesvcs.getData(j).getString("PARAM_CODE");
						List<SvcTradeData> svcTradeData = uca.getUserSvcBySvcId(svcid);
						if (!svcTradeData.isEmpty()) {
							suspendservice = svcid;
							instID = svcTradeData.get(0).getInstId();
							break;
						}
					}
					if ("".equals(suspendservice)) {
						CSAppException.apperr(BizException.CRM_BIZ_5, "用户没有订购国漫流量用尽关停对应的服务！");
						return result;
					}
					if (input.getString("OPR_CODE").equals("02")) {// 暂停
						param.put("SUSPEND_SERVICE", suspendservice + "&" + instID);
					}
				}
			}
		}
		IData value = new DataMap();
		value.put("SERIAL_NUMBER", serialNumber);
		value.put("USER_ID", uca.getUserId());
		//海南区分集团和个人的relation_type_code并不是很确定
		//2017-11-29 relation_type_code 9A
		String suspendServiceId = "";
		String suspendService = param.getString("SUSPEND_SERVICE","");
		if(StringUtils.isNotBlank(suspendService))
		{
			String susserviceIds[] = suspendService.split("&");
			suspendServiceId = susserviceIds[0];
		}
        IDataset infos = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(uca.getUserId(), "9A",null);
        IDataset results = new DatasetList();
        boolean isPerson = false;
        if(IDataUtil.isNotEmpty(infos) && StringUtils.isNotEmpty(suspendServiceId)){
            IDataset service = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(uca.getUserId(), suspendServiceId);
            if(IDataUtil.isNotEmpty(service)){
                String userida = service.getData(0).getString("USER_ID_A");
                if("-1".equals(userida)){
                    isPerson = true;
                }
            }
        }
		//是集团成员的服务则调用集团成员的变更接口
		if(!isPerson && IDataUtil.isNotEmpty(infos))
		{
			//获取用户订购的集团成员服务
			IDataset result1 = CSAppCall.call("SS.WlwQuerySVC.qryUserServiceState", value);
            result1 = DataHelper.filter(result1, "SERVICE_ID="+suspendServiceId); 
            if (IDataUtil.isEmpty(result1)){
                result.put("RESULT_CODE", "-1");
                result.put("RESULT_INFO", "集团成员没有订购需要暂停的服务："+suspendServiceId);
                return result;
            }
            param.put("ST_TYPE", stType);
            param.put("DCTYPE", dcType);
            param.put("OPR_CODE", "02");
            param.put("USER_ID_A", result1.getData(0).getString("USER_ID_A")); 
            if(StringUtils.isNotBlank(suspendServiceId))
            {
                IData service = result1.getData(0);
                suspendService = suspendService + service.getString("SERVICE_ID") + "," +service.getString("INST_ID") + "," +service.getString("START_DATE") + "," +service.getString("USER_ID_A") + ";";
                suspendService = suspendService.substring(0, suspendService.length()-1);
                param.put("SUSPEND_SERVICE", suspendService);
            }
            results = CSAppCall.call("SS.SuspendResumeWlwServiceSVC.crtTrade", param);
		}
		else
		{	
			results = CSAppCall.call("SS.SuspendResumeServiceRegSVC.tradeReg", param);
		}
		if (IDataUtil.isNotEmpty(results)) 
		{
			IData operResult = results.getData(0);
			if (!"".equals(operResult.getString("TRADE_ID"))) 
			{
				result.put("RESULT_CODE", "0");
				result.put("RESULT_INFO", "trade ok");
			} 
			else 
			{
				result.put("RESULT_CODE", "-1");
				result.put("RESULT_INFO", operResult.getString("X_RESULTINFO"));
			}
		}
		return result;
	}
}
