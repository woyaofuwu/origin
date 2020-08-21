
package com.asiainfo.veris.crm.order.soa.person.busi.bat;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatchTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPcrfInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;

public class BatDealDispatchSVC extends CSBizService
{

	protected static final Logger log = Logger.getLogger(BatDealDispatchSVC.class);
    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = -1756769134328846009L;

    public IDataset dispatch(IData data) throws Exception
    {
    	System.out.println("BatDealDispatchSVCxxxxxxxxxxxxxxxxxxx50 "+data);
        IData params = null;

        String batchOperType = data.getString("BATCH_OPER_TYPE");
        data.put("BATCH_CODE", data.getString("BATCH_ID"));
        data.put("BATCH_ID", data.getString("OPERATE_ID"));

        IDataset batchDataTypes = BatchTypeInfoQry.queryBatchDataTypes(batchOperType);

        IDataset batchTypes = BatchTypeInfoQry.qryBatchTypeByOperType(batchOperType);
        String cancelAble = "0";
        if(IDataUtil.isNotEmpty(batchTypes)){
        	cancelAble = batchTypes.getData(0).getString("CANCELABLE_FLAG");
        }
        
        String cancelTag = data.getString("CANCEL_TAG","0");
       
        if(!"0".equals(cancelTag)){
        	if(!"1".equals(cancelAble)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该批量业务类型不允许返销");
        	}
        	String orderId = data.getString("TRADE_ID");
        	if(StringUtils.isEmpty(orderId)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "返销订单编号为空");
        	}
        	IDataset tradeinfos = TradeBhQry.queryTradeInfoByOrderId(orderId,null);
        	if(IDataUtil.isNotEmpty(tradeinfos)){
        		params = data;
        		if(IDataUtil.isEmpty(params)){
        			params = new DataMap();
        		}
        		if(StringUtils.isEmpty(params.getString(Route.ROUTE_EPARCHY_CODE))){
        			params.put(Route.ROUTE_EPARCHY_CODE, "0898");
        		}
        		params.put("TRADE_ID", tradeinfos.getData(0).getString("TRADE_ID"));
        		IDataset results = CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", params);
        		return results;
        	}else{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查询到ORDER_ID:"+orderId+"对应的台账信息");
        	}
        }
        
        if ("CREATEPREUSER".equals(batchOperType))
        {// 批量预开户o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "500");
            params.put("ORDER_TYPE_CODE", "500");
            params.put("PRODUCT_ID", data.getString("CONDITION1"));
            params.put("M2M_FLAG", "0");
            data.remove("CODING_STR");
            data.remove("CONDITION1");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.CreatePersonUserBatchIntfSVC.tradeReg", params);
            return results;
        }else if ("CREATEPREUSER_SCHOOL".equals(batchOperType))
        {// 校园营销批量预开户o
        	 String codingstr = data.getString("CODING_STR");
             params = new DataMap(codingstr);
             params.put("TRADE_TYPE_CODE", "500");
             params.put("ORDER_TYPE_CODE", "500");
             params.put("PRODUCT_ID", data.getString("CONDITION1"));
             params.put("M2M_FLAG", "0");
             data.remove("CODING_STR");
             data.remove("CONDITION1");
             params.putAll(data);
             params = transData(params, batchDataTypes);
             IDataset results = CSAppCall.call("SS.CreatePersonUserBatchIntfSVC.tradeReg", params);
             return results;
        }
        else if ("BATACTIVECREATEUSER".equals(batchOperType))
        {// 批量买断开户o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "700");
            params.put("ORDER_TYPE_CODE", "700");
            params.put("PRODUCT_ID", data.getString("CONDITION1"));
            params.put("M2M_FLAG", "0");
            data.remove("CODING_STR");
            data.remove("CONDITION1");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.CreatePersonUserBatchAgentIntfSVC.tradeReg", params);
            return results;
        }
        else if ("CREATEPREUSER_PWLW".equals(batchOperType))
        {// 物联网开户
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "10");
            params.put("ORDER_TYPE_CODE", "10");
            params.put("NET_TYPE_CODE", "07");
            params.put("M2M_FLAG", "1");							//物联网标志	
            params.put("OPEN_TYPE_CODE", PersonConst.IOT_OPEN); 	//物联网标志	
            
            params.put("PRODUCT_ID", data.getString("CONDITION1"));
            
            data.remove("CODING_STR");
            data.remove("CONDITION1");
            
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.CreateUserPwlwIntfSVC.tradeReg", params);
            if( log.isDebugEnabled() ){
            	log.debug("<<<<<<<<<svc>>>>>>>>>>yanwu CSAppCall: SS.CreateUserPwlwIntfSVC.tradeReg <<<<<<<<<svc>>>>>>>>>>");
            }
            //IDataset results = CSAppCall.call("SS.CreatePersonUserBatchIntfSVC.tradeReg", params);
            return results;
        }
        else if ("CREATEPREUSER_M2M".equals(batchOperType))
        {// 行业应用卡批量开户
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "10");
            params.put("ORDER_TYPE_CODE", "10");
            params.put("NET_TYPE_CODE", "00");
            params.put("M2M_FLAG", "0");							 
            params.put("OPEN_TYPE_CODE", ""); 						 
            params.put("PERSON_BATCH_TYPE", "CREATEPREUSER_M2M"); 						 
            
            params.put("PRODUCT_ID", data.getString("CONDITION1"));
            
            data.remove("CODING_STR");
            data.remove("CONDITION1");
            
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.CreateUserM2MIntfSVC.tradeReg", params);
            if( log.isDebugEnabled() ){
            	log.debug("<<<<<<<<<svc>>>>>>>>>>yanwu CSAppCall: SS.CreateUserM2MIntfSVC.tradeReg <<<<<<<<<svc>>>>>>>>>>");
            }

            return results;
        }
        else if ("CREATEPREUSER_BNBD".equals(batchOperType))
        {// 商务宽带批量开户
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "600");
            params.put("ORDER_TYPE_CODE", "600");
            params.put("FLOOR_AND_ROOM_NUM", "0");
            params.put("MODEM_DEPOSIT", "0");
            
            String strSn = params.getString("SERIAL_NUMBER");
            String strPhone = params.getString("PHONE");
            String strCP = params.getString("CONTACT_PHONE");
            String strC = params.getString("CONTACT");
            
            data.remove("CODING_STR");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            params.put("SERIAL_NUMBER", strSn);
            params.put("PHONE", strPhone);
            params.put("CONTACT_PHONE", strCP);
            params.put("CONTACT", strC);
            
            IDataset results = CSAppCall.call("SS.MergeWideUserCreateIntfSVC.tradeReg", params);
            if( log.isDebugEnabled() ){
            	log.debug("<<<<<<<<<svc>>>>>>>>>>yanwu CSAppCall: SS.MergeWideUserCreateIntfSVC.tradeReg <<<<<<<<<svc>>>>>>>>>>");
            }

            return results;
        }
        else if ("BLACKSMSCHG".equals(batchOperType))
        {// 不良信息号码批量处理o
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "880");
            params.put("ORDER_TYPE_CODE", "880");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.CloseSmsTradeSVC.tradeReg", params);
            return results;
        }
        else if ("OFFICESTOPOPEN".equals(batchOperType))
        {// 批量局方开机o
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "126");
            params.put("ORDER_TYPE_CODE", "126");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.ChangeSvcStateIntfSVC.createOfficeStopOpenReg", params);
            return results;
        }
        else if ("TD_FIXED_PHONE_STOP".equals(batchOperType))
        {// TD二代无线固话批量停机
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "3808");
            params.put("ORDER_TYPE_CODE", "3808");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", params);
            return results;
        }
        else if ("TT_FIXED_PHONE_STOP".equals(batchOperType))
        {// 铁通固话批量停机
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "9734");
            params.put("ORDER_TYPE_CODE", "9734");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.FixTelChangeSvcStateRegSVC.tradeReg", params);
            return results;
        }
        else if ("BATPLATFORM".equals(batchOperType))
        {// 批量业务平台业务o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "3700");
            params.put("ORDER_TYPE_CODE", "3700");
            data.remove("CODING_STR");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", params);
            return results;
        }
        else if ("BATHBPAY".equals(batchOperType))
        {// 批量业务手机支付业务o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "3700");
            params.put("ORDER_TYPE_CODE", "3700");
            data.remove("CODING_STR");
            params.putAll(data);

            IDataset svc = PlatSvcInfoQry.qryPlatSvc4All(params.getString("SP_CODE"), params.getString("BIZ_CODE"), params.getString("BIZ_TYPE_CODE"));
       
            IData tmp  = new DataMap();
            IDataset attrs = new DatasetList();
            IDataset tmps = new DatasetList(); 
            if(IDataUtil.isNotEmpty(params))
            {
                IData attr = new DataMap();
	            attr.put("ATTR_VALUE", params.getString("DATA1"));
	            attr.put("ATTR_CODE", "101");
	            attrs.add(0,attr);
	            IData attr1 = new DataMap();
	            attr1.put("ATTR_VALUE", params.getString("DATA2"));
	            attr1.put("ATTR_CODE", "104");
	            attrs.add(1,attr1);
	            IData attr2 = new DataMap();
	            attr2.put("ATTR_VALUE", params.getString("DATA3"));
	            attr2.put("ATTR_CODE", "126");
	            attrs.add(2,attr2);
	            IData attr3 = new DataMap();
	            attr3.put("ATTR_VALUE", params.getString("DATA4"));
	            attr3.put("ATTR_CODE", "103");
	            attrs.add(3,attr3);
            }

            tmp.put("ATTR_PARAM", attrs);
            tmp.put("SERVICE_ID", svc.getData(0).getString("SERVICE_ID"));
            tmp.put("BIZ_STATE_CODE", svc.getData(0).getString("BIZ_STATE_CODE"));
            tmp.put("MODIFY_TAG", "0");
            tmp.put("OPER_CODE","06");

           // tmp.put("IS_WRITE_ATTR","true");
            tmps.add(tmp);
            params.put("SELECTED_ELEMENTS", tmps.toString());
            IDataset results = CSAppCall.call("SS.PlatRegSVC.tradeReg", params);
            return results;
        }
        else if ("MODIFYACYCINFO".equals(batchOperType))
        {// 批量修改帐户银行资料o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "80");
            params.put("ORDER_TYPE_CODE", "80");
            data.remove("CODING_STR");
            data.remove("CONDITION2");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.ModifyAcctInfoIntfRegSVC.tradeReg", params);
            return results;
        }
        else if("BATPCRFCHG".equals(batchOperType)){
        	if(log.isDebugEnabled()){
        		log.debug("~~~~~~~~~~~~~~~~~入参是~~~~~~~~~~~~~~~~~~~~~："+data.toString());
        	}
        	String codingstr = data.getString("CODING_STR");
        	IDataset attrs = new DatasetList();
        	IData param = new DataMap(codingstr);

        	String tradeTypeTag = "279";
        	String tempTradeType = param.getString("TRADE_TYPE_TAG","");
        	if(StringUtils.isNotBlank(tempTradeType) && StringUtils.equals("280", tempTradeType)){
        		tradeTypeTag = "280";
        	}
        	
        	params = new DataMap();

        	params.put("TRADE_TYPE_CODE", tradeTypeTag);
        	params.put("ORDER_TYPE_CODE", tradeTypeTag);
        	data.remove("CODING_STR");
        	params.putAll(data);
        	
        	String serial_number = params.getString("SERIAL_NUMBER");
        	
        	IData user = UcaInfoQry.qryUserInfoBySn(serial_number);
    		String user_id = user.getString("USER_ID");
    		IData param_in = new DataMap();
    		param_in.put("USER_ID", user_id);
    		param_in.put("TIME_POINT", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
    		IDataset user_pro = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_BY_UID_DATE", param_in);
    		if(IDataUtil.isEmpty(user_pro)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,serial_number+"不是物联网号码!");
    		}
    		if(!"PWLW".equals(user_pro.getData(0).getString("BRAND_CODE"))){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,serial_number+"不是物联网号码!");
    		}
    		
    		params.put("USER_ID", user_id);
    		
    		
    		
    		String strPcrfReq= param.getString("X_BATPCRFREQ_STR");
        	IDataset list = new DatasetList(strPcrfReq);
        	String service_id = list.getData(0).getString("SERVICE_ID");
        	
        	IDataset infos =UserSvcInfoQry.qrySvcInfoByUserIdSvcId(user_id, service_id);
    		IDataset pcrfs = UserPcrfInfoQry.getUserPcrfsByUserId(user_id,tradeTypeTag, null);
        	
    		for(int i = 0 ; i<list.size();i++){
    			IData data1 = list.getData(i);
    			String service_code = data1.getString("SERVICE_CODE");
    			String rela_inst_id = data1.getString("RELA_INST_ID");
    			if(StringUtils.isEmpty(rela_inst_id)){
    				if(IDataUtil.isEmpty(infos)){
    					
						CSAppException.apperr(CrmCommException.CRM_COMM_103,serial_number+"没有订购过策略服务,不允许变更操作!");
						
    				}else{
    					
    					IData real = infos.getData(0);
    					
						data1.put("RELA_INST_ID", real.getString("INST_ID"));
						String inst_id = real.getString("INST_ID");                                	
						for(int l=0;l<pcrfs.size();l++){
							IData pcrf = pcrfs.getData(l);
							if(inst_id.equals(pcrf.getString("RELA_INST_ID"))&&service_code.equals(pcrf.getString("SERVICE_CODE"))){
								data1.put("INST_ID", pcrf.getString("INST_ID"));
								if("1".equals(data1.getString("MODIFY_TAG"))){
									data1.put("BILLING_TYPE", pcrf.getString("BILLING_TYPE"));
									data1.put("USAGE_STATE", pcrf.getString("USAGE_STATE"));
								}
								if("0".equals(data1.getString("MODIFY_TAG"))){
									CSAppException.apperr(CrmCommException.CRM_COMM_103,serial_number+"已开通策略服务,不允许再做新增操作!");
								}
                            }
						}
						attrs.add(data1);	
    				}
    				
    			}	
    		}
    		
    		params.put("X_BATPCRFREQ_STR", attrs);
    		if(log.isDebugEnabled()){
        		log.debug("~~~~~~~~~~~~~~~~~修改之后的参数~~~~~~~~~~~~~~~~~~~~~："+params.toString());
        	}
        	
        	IDataset results = CSAppCall.call("SS.DealPcrfTacticsRegSVC.tradeReg", params);
            return results;
        	
        }
        else if ("SERVICECHG".equals(batchOperType))
        {// 批量服务变更o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "110");
            params.put("ORDER_TYPE_CODE", "110");
            params.put("ELEMENT_ID", params.getString("SERVICE_ID"));
            params.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SVC);
            String startDate = params.getString("START_DATE", "");
            String bookingTag = "0";
            if (StringUtils.isNotEmpty(startDate))
            {
                String curDate = SysDateMgr.getSysDate();
                if (startDate.compareTo(curDate) > 0)
                {
                    bookingTag = "1";
                }
            }
            params.put("BOOKING_TAG", bookingTag);
            IDataset commparaInfo = CommparaInfoQry.getCommNetInfo("CSM","680",params.getString("ELEMENT_ID"));
            if(BizEnv.getEnvBoolean("BATCHSECONDCONFIRM_USERSVC")&&IDataUtil.isEmpty(commparaInfo))
            {
            	params.put("PRE_TYPE", BofConst.SVC_SEC);
            }
            
            data.remove("CODING_STR");
            params.remove("SERVICE_ID");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", params);
            if(BizEnv.getEnvBoolean("BATCHSECONDCONFIRM_USERSVC")&&IDataUtil.isEmpty(commparaInfo))
            {
	            IData inputData = new DataMap();
	            inputData = params;
	            inputData.put("PRE_TYPE", BofConst.SVC_SEC);
	            inputData.put("SERIAL_NUMBER", params.getString("SERIAL_NUMBER"));
	            inputData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
	            inputData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
	            inputData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
	            inputData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	            inputData.put("SVC_NAME", "SS.ChangeProductRegSVC.ChangeProduct");
	            
	            
	            String serviceName = "服务";
	            if (StringUtils.isNotBlank(params.getString("ELEMENT_ID")))
	            {
	                serviceName = USvcInfoQry.getSvcNameBySvcId(params.getString("ELEMENT_ID"));
	            }
	            String smsContent = "订购确认：您好！感谢您对中国移动的大力支持，您即将订购由中国移动为您提供的"+ serviceName +"，请在24小时内回复“是”确认订购，回复其他内容或不回复，则不订购。我们一直努力，为您十分满意。【中国移动】您好";            
	            IData twoCheckSms = new DataMap();
	            twoCheckSms.put("TEMPLATE_ID", "");// 海南需要传TEMPLATE_ID
	            twoCheckSms.put("SERIAL_NUMBER", params.getString("SERIAL_NUMBER"));
	            twoCheckSms.put("SMS_CONTENT", smsContent);
	            twoCheckSms.put("SMS_TYPE", BofConst.SVC_SEC);
	            twoCheckSms.put("OPR_SOURCE", "1");
	            twoCheckSms.put("REMARK", "批量服务变更");
	            TwoCheckSms.twoCheck("110", 0, params, twoCheckSms);
            }
            return results;
        }
        else if ("SUSPENDSERVICE".equals(batchOperType))
        {// 批量服务暂停o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "272");
            params.put("ORDER_TYPE_CODE", "272");
            params.put("SUSPEND_SERVICE", params.getString("SERVICE_ID"));
            data.remove("CODING_STR");
            params.remove("SERVICE_ID");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.SuspendResumeServiceRegSVC.tradeReg", params);
            return results;
        }
        else if ("RESUMESERVICE".equals(batchOperType))
        {// 批量服务恢复o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "272");
            params.put("ORDER_TYPE_CODE", "272");
            params.put("RESUME_SERVICE", params.getString("SERVICE_ID"));
            data.remove("CODING_STR");
            params.remove("SERVICE_ID");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.SuspendResumeServiceRegSVC.tradeReg", params);
            return results;
        }
        else if ("DISCNTCHG".equals(batchOperType))
        {// 批量办理套餐o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            String discntCode = params.getString("DISCNT_CODE");
            String startDate = params.getString("START_DATE", "");
            String modifyTag = params.getString("MODIFY_TAG", "");
            String serialNumber = data.getString("SERIAL_NUMBER");
            UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
            IData param = new DataMap();
            param.put("SUBSYS_CODE", "CSM");
    		param.put("PARAM_ATTR", "96");
    		param.put("PARAM_CODE", discntCode);
            IDataset commparaInfo = CommparaInfoQry.getCommparaInfoByPara(param);
            IData compara = commparaInfo.first();
            String packageId = compara.getString("PARA_CODE2");
            String productId = compara.getString("PARA_CODE3");
            String endMode = compara.getString("PARA_CODE4");//1截至月底，0立即截至
            String endDate = "";
            if("1".equals(endMode)){
            	endDate = SysDateMgr.getDateLastMonthSec(startDate);
            	startDate = SysDateMgr.addSecond(endDate,1);
            }else{
            	endDate = SysDateMgr.addSecond(startDate,-1);
            }
            IData serviceMap = new DataMap();
            IDataset elementArray = new DatasetList();
            IData addElement = new DataMap();
            addElement.put("START_DATE", startDate);
            addElement.put("ELEMENT_ID", discntCode);
            addElement.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            addElement.put("MODIFY_TAG", modifyTag);
            elementArray.add(addElement);
           
            //如果packageId不为空，补充老的优惠截至参数
            if(StringUtils.isNotBlank(packageId)){
            	IData delElement = new DataMap();
            	param.clear();
                param.put("SUBSYS_CODE", "CSM");
        		param.put("PARAM_ATTR", "96");
        		param.put("PARA_CODE2", packageId);
                IDataset packageInfo = CommparaInfoQry.getCommparaInfoByPara(param);
                for(Object data4 :packageInfo){
    				IData inparams = new DataMap();
    				inparams.put("USER_ID", ucaData.getUserId());
    				inparams.put("DISCNT_CODE", ((IData)data4).getString("PARAM_CODE"));
    				IDataset discntList = UserDiscntInfoQry.getUserDiscntByUserID(inparams);
    				//如果不为空，说明用户需要将这个优惠截至掉
    				if(IDataUtil.isNotEmpty(discntList)){
    					for(Object data5 :discntList){
    						if(((IData)data5).getString("END_DATE").startsWith("2050")){
    							String oldDiscntCode = ((IData)data5).getString("DISCNT_CODE");
    							String oldInstId = ((IData)data5).getString("INST_ID"); 
    							startDate = ((IData)data5).getString("START_DATE");
    							log.debug("新办理的优惠"+discntCode+"是流量包中的优惠，对应的老优惠："+oldDiscntCode);
    							delElement.put("ELEMENT_ID", oldDiscntCode);
    							delElement.put("ELEMENT_TYPE_CODE", "D");
    							delElement.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
    							delElement.put("INST_ID", oldInstId);
    							delElement.put("PRODUCT_ID", productId);
    							delElement.put("PACKAGE_ID", packageId);
    							delElement.put("START_DATE", startDate);
    							delElement.put("END_DATE", endDate);
    							break;
    						}
    					}
    					
    				}
                }
            	elementArray.add(delElement);
            }
            serviceMap.put("TRADE_TYPE_CODE", "110");
            serviceMap.put("ORDER_TYPE_CODE", "110");

            String bookingTag = "0";
            if (StringUtils.isNotEmpty(startDate))
            {
                String curDate = SysDateMgr.getSysDate();
                if (startDate.compareTo(curDate) > 0)
                {
                    bookingTag = "1";
                }
            }
            serviceMap.put("BOOKING_TAG", bookingTag);
            serviceMap.put("ELEMENTS", elementArray);
            serviceMap.put("SERIAL_NUMBER", serialNumber);
            
            serviceMap.putAll(data);
            params = transData(serviceMap, batchDataTypes);
            //处理4g自选套餐老套餐（语音、流量）截止@tanzheng
            IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", serviceMap);
            return results;
        }
        else if ("CampusBroadband".equals(batchOperType))
        {// 批量办理校园网宽带套餐o
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "165");
            params.put("ORDER_TYPE_CODE", "165");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.WidenetChangeDiscntRegSVC.tradeReg", params);
            return results;
        }
        else if ("BATCREATEFIXEDUSER".equals(batchOperType))
        {// 固话批量装机o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "9750");
            params.put("ORDER_TYPE_CODE", "9750");
            params.put("PRODUCT_ID", data.getString("CONDITION1"));
            data.remove("CODING_STR");
            data.remove("CONDITION1");
            params.put("IS_BAT", "1");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.CreateFixTelUserSVC.tradeReg", params);
            return results;
        }
        else if ("BATCREATETRUNKUSER".equals(batchOperType))
        {// 千群百号装机o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "9751");
            params.put("ORDER_TYPE_CODE", "9751");
            params.put("PRODUCT_ID", data.getString("CONDITION1"));
            data.remove("CODING_STR");
            data.remove("CONDITION1");
            params.put("IS_BAT", "1");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.CreateFixTelUserSVC.tradeReg", params);
            return results;
        }
        else if ("BATAPPENDTRUNKUSER".equals(batchOperType))
        {// 追加千群百号
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "9752");
            params.put("ORDER_TYPE_CODE", "9752");
            params.put("PRODUCT_ID", data.getString("CONDITION1"));
            data.remove("CODING_STR");
            data.remove("CONDITION1");
            params.put("IS_BAT", "1");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.CreateFixTelUserSVC.tradeReg", params);
            return results;
        }
        else if ("OPENUSER".equals(batchOperType))
        {// 物联网批量开机o
            params = new DataMap();
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.ChangeSvcStateIntfSVC.createOpenReg", params);
            return results;
        }
        else if ("STOPUSER".equals(batchOperType))
        {// 物联网批量停机o
            params = new DataMap();
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.ChangeSvcStateIntfSVC.createStopReg", params);
            return results;
        }
        else if ("SALEACTIVE".equals(batchOperType))
        {// 批量办理营销活动o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            data.remove("CODING_STR");
            params.putAll(data);
            params.put("IS_BAT", "1");
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", params);
            return results;
        }
        else if ("SALEACTIVEEND".equals(batchOperType))
        {// 批量办理营销活动终止
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            data.remove("CODING_STR");
            params.putAll(data);
            params.put("IS_BAT", "1");
            params = transData(params, batchDataTypes);
            
            String serial_number= params.getString("SERIAL_NUMBER");
    		IDataset user_set = UserInfoQry.getUserinfo(serial_number);
    		if (user_set==null && user_set.size()<=0)
    		{
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户资料不存在");
    		}
    		String userid= user_set.getData(0).getString("USER_ID");
    		String packid=params.getString("PACKAGE_ID");
    		String prodid=params.getString("PRODUCT_ID");
    		
    	    IDataset active_set = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userid,prodid,packid);
    		//不存在则不需要截止
    		if(IDataUtil.isEmpty(active_set)){
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"无终止营销活动");
    		}
    		String relation_tradeid=active_set.getData(0).getString("RELATION_TRADE_ID");
    		
    		String bookDate = params.getString("BOOKING_DATE","");
    		if(bookDate != null && !"".equals(bookDate))
    		{
    			bookDate = SysDateMgr.addDays(bookDate,-1);
    			bookDate = SysDateMgr.getDateLastMonthSec(bookDate);
    		}
    		else
    		{
    			bookDate = SysDateMgr.getAddMonthsLastDay(-1);
    		}
    		
    		String oldSaleActiveEndDate = active_set.getData(0).getString("END_DATE");
    		//如果预约时间结束时间小于等于原营销活动的结束时间，则不需要终止
    		if(SysDateMgr.compareTo(oldSaleActiveEndDate, bookDate) <= 0)
    		{
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"预约时间结束时间小于等于原营销活动的结束时间不需要终止");
    		}


    		params.put("RELATION_TRADE_ID", relation_tradeid);
    		params.put("IS_RETURN", "0");
    		params.put("FORCE_END_DATE", bookDate);
    		params.put("END_DATE_VALUE", "7"); //批量终止
    		params.put("EPARCHY_CODE",params.getString("TRADE_EPARCHY_CODE"));
            
    		//params.put("WIDE_YEAR_ACTIVE_BACK_FEE","1"); //标记为1，如果营销活动有预存不进行清退
        	
            IDataset results = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", params);
            return results;
        }
        else if ("SPARKPLAN".equals(batchOperType))
        {// 营销活动（星火计划）批量办理o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            data.remove("CODING_STR");
            params.putAll(data);
            params.put("IS_BAT", "1");
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", params);
            return results;
        }
        else if ("DESTROYUSER".equals(batchOperType))
        {// 物联网批量立即销号o
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "192");
            params.put("ORDER_TYPE_CODE", "192");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", params);
            return results;
        }
        else if ("BATACTIVEUSER".equals(batchOperType))
        {// 批量买断开户手工激活
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "18");
            params.put("ORDER_TYPE_CODE", "18");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.ActiveSaleCardOpenIntfSVC.tradeReg", params);
            return results;
        }
        else if ("REDMEMBER".equals(batchOperType))
        {// 短信白名单批量处理 ITF_CRM_RedMember
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            data.remove("CODING_STR");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.QueryInfoSVC.redMemberDeal", params);
            return results;
        }
        else if ("INFOMANAGE".equals(batchOperType))
        {// 目标用户批量导入 ITF_CRM_InfoManage
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            data.remove("CODING_STR");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.InformationManageSVC.infoManageDeal", params);
            return results;
        }
        else if ("CONSTRUCTIONADDR".equals(batchOperType))
        {// 批量修改宽带施工地址
        	params = new DataMap();
            params.put("TRADE_TYPE_CODE", "6666");
            params.put("ORDER_TYPE_CODE", "6666");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.ConstructionAddrSVC.constructionAddrDeal", params);
            return results;
        }
        else if ("CREATEPRETDUSER".equals(batchOperType))
        {// 无线固话批量预开户 TCS_CreateTDUserRegBat
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "3822");
            params.put("ORDER_TYPE_CODE", "3822");
            params.put("PRODUCT_ID", data.getString("CONDITION1"));
            params.put("M2M_FLAG", "0");
            data.remove("CODING_STR");
            data.remove("CONDITION1");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.CreateTDPersonUserRegSVC.tradeReg", params);
            return results;
        }
        else if ("STOPTESTCARD".equals(batchOperType))
        {// 测试卡批量停机
            params = new DataMap();
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.TestCardSVC.testCardDeal", params);
            return results;
        }
        else if ("BATTESTCARD".equals(batchOperType))
        {// 测试卡批量操作
            params = new DataMap();
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.TestCardSVC.testCardDeal", params);
            return results;
        }
        else if ("CREATEUSIMPREUSER".equals(batchOperType))
        {// 批量USIM卡预开
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "502");
            params.put("ORDER_TYPE_CODE", "502");
            data.remove("CODING_STR");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.PhoneReturnRegSVC.tradeReg", params);
            return results;
        }
        else if ("TEMPPHONERETURN".equals(batchOperType))
        {// 资源临时号码销户
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "503");
            params.put("ORDER_TYPE_CODE", "503");
            data.remove("CODING_STR");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.PhoneReturnRegSVC.tradeReg", params);
            return results;
        }
        else if ("CLEARKI".equals(batchOperType))
        {// 资源清KI
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "800");
            params.put("ORDER_TYPE_CODE", "800");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.ClearKISVC.clearKI", params);
            return results;
        }
        else if ("SALECARDOPEN".equals(batchOperType))
        {// 资源售卡
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "14");
            params.put("ORDER_TYPE_CODE", "14");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.SaleCardOpenRegSVC.tradeReg", params);
            return results;
        }
        else if ("BACKCARDOPEN".equals(batchOperType))
        {// 套卡退卡(返销)
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "14");
            params.put("ORDER_TYPE_CODE", "14");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.UndoSaleCardOpenRegSVC.UndoSaleCardOpenReg", params);
            return results;
        }
        
        else if ("DISCNTCHGSPEC".equals(batchOperType))
        {// 批量办理特殊套餐o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "110");
            params.put("ORDER_TYPE_CODE", "110");
            params.put("ELEMENT_ID", params.getString("DISCNT_CODE"));
            params.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            String startDate = params.getString("START_DATE", "");
            String bookingTag = "0";
            if (StringUtils.isNotEmpty(startDate))
            {
                String curDate = SysDateMgr.getSysDate();
                if (startDate.compareTo(curDate) > 0)
                {
                    bookingTag = "1";
                }
            }
            params.put("BOOKING_TAG", bookingTag);
            data.remove("CODING_STR");
            params.remove("DISCNT_CODE");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.ChangeProductRegNoRuleSVC.ChangeProduct", params);
            return results;
        }else if ("GRPDISCNTCHGSPEC".equals(batchOperType))
        {// 集团产品特殊优惠变更
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "1522");
            params.put("ORDER_TYPE_CODE", "1522");
            data.remove("CODING_STR");
            params.putAll(data);
            
            UcaData uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
        	IData map = new DataMap();
        	map.put("USER_ID", uca.getUserId());
        	map.put("ROUTE_EPARCHY_CODE", "0898");
        	map.put("EPARCHY_CODE", "0898");
        	IDataset discntData = CSAppCall.call("SS.UserGrpDiscntSpecDealSVC.getUserDiscntList", map);
        	IDataset OTHER_DISCNT_LIST = new DatasetList();
            if(IDataUtil.isNotEmpty(discntData))
            {
            	IDataset specDiscntList = new DatasetList(discntData.getData(0).getDataset("DISCNT_LIST")); 
            	for(int i=0;i<specDiscntList.size();i++)
            	{
            		IData dis = specDiscntList.getData(i);
            		dis.put("END_DATE1", params.getString("END_DATE1",""));
            		dis.put("DISCNT_REMARK", "批量修改");
            		dis.put("TEMP_START_DATE", dis.getString("START_DATE"));
            		dis.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            		dis.put("tag", "2");
            		String endTime = SysDateMgr.getDateLastMonthSec(params.getString("END_DATE1",""));
            		String oldTime = dis.getString("END_DATE");
            		dis.put("END_DATE", endTime);
            		dis.put("OLD_END_DATE", oldTime);
            		dis.put("OLD_START_DATE", dis.getString("START_DATE"));
            		OTHER_DISCNT_LIST.add(dis);
            	}
            }
            params.put("OTHER_DISCNT_LIST", OTHER_DISCNT_LIST);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.UserGrpDiscntSpecDealRegSVC.tradeReg", params);
            return results;
        }
        else if ("SERVICECHGSPEC".equals(batchOperType))
        {// 批量服务变更o
            String codingstr = data.getString("CODING_STR");
            params = new DataMap(codingstr);
            params.put("TRADE_TYPE_CODE", "110");
            params.put("ORDER_TYPE_CODE", "110");
            params.put("ELEMENT_ID", params.getString("SERVICE_ID"));
            params.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SVC);
            String startDate = params.getString("START_DATE", "");
            String bookingTag = "0";
            if (StringUtils.isNotEmpty(startDate))
            {
                String curDate = SysDateMgr.getSysDate();
                if (startDate.compareTo(curDate) > 0)
                {
                    bookingTag = "1";
                }
            }
            params.put("BOOKING_TAG", bookingTag);
            data.remove("CODING_STR");
            params.remove("SERVICE_ID");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.ChangeProductRegNoRuleSVC.ChangeProduct", params);
            return results;
        }
        else if ("BATACTIVECANCEL".equals(batchOperType))
        {// 批量返销营销活动
        	String serialNum=data.getString("SERIAL_NUMBER");
            params = new DataMap();
            String codingstr = data.getString("CODING_STR");
            IData codStr1=new DataMap(codingstr);
            String tradeIdList="";
            if(!"".equals(codStr1)){
        		String campnType=codStr1.getString("SALE_CAMPN_TYPE");
            	String prodId=codStr1.getString("SALE_PRODUCT_ID");
            	String packId=codStr1.getString("SALE_PACKAGE_ID");
            	IData saleData=new DataMap();
            	saleData.put("SERIAL_NUMBER", serialNum); 
            	saleData.put("CAMPN_TYPE", campnType); 
            	saleData.put("PRODUCT_ID", prodId); 
            	saleData.put("PACKAGE_ID", packId); 
            	IDataset userSaleActiveInfo=CSAppCall.call("SS.BatActiveCancelSVC.queryUserSaleActiveInfo", saleData);
            	if(userSaleActiveInfo!=null && userSaleActiveInfo.size()>0){
            		tradeIdList=userSaleActiveInfo.getData(0).getString("TRADE_ID");
            	}
            }
            params.put("SERIAL_NUMBER", serialNum);
            params.put("ORDER_TYPE_CODE", "240"); 
            params.put("TRADEID_LIST", tradeIdList+","); 
            params.put("ROUTE_EPARCHY_CODE", "0898");  
            params.putAll(data); 
            
            //查询 {"TRADE_TYPE_CODE":"240","START_DATE":"2016-01-27","SERIAL_NUMBER":"15203611628","END_DATE":"2016-01-27"}
            
            //返销
            IDataset results = CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", params);
            return results;
        }
        else if("BATMOBILESTOP".equals(batchOperType))
        {//批量停机/销户       	   
            params = new DataMap();
            params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            params.put("TRADE_TYPE_CODE", data.getString("DATA1"));
            params.put("ORDER_OA", data.getString("DATA2"));
            params.put("TOP_REASON", data.getString("DATA3"));
            params.put("REMARK", data.getString("DATA4"));
            params.putAll(data);
            params = transData(params, batchDataTypes); 
            System.out.println("BATMOBILESTOP销户");
            IDataset commparaInfos9688 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9688",data.getString("DATA1"),null);
            String paraCode2 = commparaInfos9688.getData(0).getString("PARA_CODE2");
            IDataset results =null;
        	if("0".equals(paraCode2)){
        		 results = CSAppCall.call("SS.ChangeSvcStateRegSVC.tradeReg", params);
        	}else{
        		 params.put(Route.ROUTE_EPARCHY_CODE,  "0898"); 
        		 results = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", params);
        	}
        	
            return results;
        
        }
        else if("BATREALNAME".equals(batchOperType))
        {//批量实名制  
            UcaData uca = null;
            IData inputData = new DataMap();
        	uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
        	inputData.put("USER_ID", uca.getUserId());
        	inputData.put("CUST_ID", uca.getCustId());
        	inputData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        	
            
            //Begin: REQ201506150011 批量实名制登记业务优化--激活用户不能通过批量方式登记实名制
            String acctTag = uca.getUser().getAcctTag();
            if("0".equals(acctTag))
            {
            	//已激活
                /**
                 * REQ201612010002_关于2016年下半年测试卡规则优化需求（一）
                 * @author zhuoyingzhi
                 * 20170109
                 * 2）如果操作工号有此权限“批量修改测试号码客户资料”，可以批量修改已激活的测试号码的客户资料；
                 */
                String city_code = uca.getUser().getCityCode();
                if ("HNSJ".equals(city_code) || "HNHN".equals(city_code))
                {
               	    //测试号
                    boolean isBatRealNameRight = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "isBatRealNameRight");
	               	 if(!isBatRealNameRight){
	               		 //无权限
	                 	CSAppException.apperr(CrmCommException.CRM_COMM_103,"您无权限修改已激活的测试号码.");
	               	  } 
                }else{
                	//非测试号码
                	CSAppException.apperr(CrmCommException.CRM_COMM_103, data.getString("SERIAL_NUMBER") + ",激活用户不能通过批量方式登记实名制!");
                }
                /**************结束**********************/

            }
          //End: REQ201506150011 批量实名制登记业务优化--激活用户不能通过批量方式登记实名制
        	
            IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", inputData);
            
            params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
            params.put("IS_REAL_NAME", "1");
            params.put("REAL_NAME", "true");
            params.put("CHECK_MODE", data.getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
            params.putAll(data);
            params = transData(params, batchDataTypes); 

            if(uca.getBrandCode().equals("TDYD")){
            	params.put("TRADE_TYPE_CODE","3811");//无线固话实名制登记
        	}else{
                params.put("TRADE_TYPE_CODE", "60");
        	}
            
            IDataset results = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);
            
            return results;
        }
        else if("BATUPDATEPSW".equals(batchOperType))//add for fufn
        {//批量修改密码
        	
        	params = new DataMap();
        	params.put("NEW_PASSWD", data.getString("DATA1"));
        	params.put("PASSWD_TYPE", "2");
        	params.put("X_MANAGEMODE", "");        	
        	
            params.putAll(data);
            params = transData(params, batchDataTypes); 
        
            UcaData uca = UcaDataFactory.getNormalUca(params.getString("SERIAL_NUMBER",""));
            //System.out.println("BatDealDispatchSVCxxxxxxxxxxxxx949 "+params);
            //System.out.println("BatDealDispatchSVCxxxxxxxxxxxxx950 "+ uca.getBrandCode());
            
        	if(uca.getBrandCode().equals("TDYD")){
            	params.put("TRADE_TYPE_CODE","3810");//无线固话密码变更
        	}else{
        		params.put("TRADE_TYPE_CODE","71");	
        	}
        	
            IDataset results = CSAppCall.call("SS.ModifyUserPwdInfoRegSVC.tradeReg", params);
        	log.error("BatDealDispatchSVCxxxxxxxxxxxx1022 "+results);

            return results;
        }
        else if("BATDESTROYUSER".equals(batchOperType))//add for fufn
        {//批量销户
        	
        	params = new DataMap();
        	params.put("X_MANAGEMODE", "");
        	params.put("TRADE_TYPE_CODE","192");
        	params.put("REMOVE_REASON_CODE", "10");
            params.putAll(data);
            params = transData(params, batchDataTypes); 
            
            IDataset results = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", params);
            
            return results;
        }
        else if("BATBNBDWIDENETDELETE".equals(batchOperType))//add for fufn
        {//批量商务宽带拆机
        	
        	params = new DataMap();
        	params.put("AUTH_SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        	params.put("MODEM_FEE","0");
        	params.put("MODEM_RETUAN","0");//是否退光猫
        	params.put("TRADE_TYPE_CODE","605");
            params.putAll(data);
            params = transData(params, batchDataTypes); 
            IDataset results = CSAppCall.call("SS.WidenetDestroyNewRegSVC.tradeReg", params);
            return results;
        }
        else if("BATCREATEM2MTAG".equals(batchOperType))
        {//批量增加行业应用卡标识
        	IData ups = new DataMap();
        	UcaData uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
        	ups.put("USER_ID", uca.getUserId());
        	ups.put("CUST_ID", uca.getCustId());
        	ups.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        	
        	IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", ups);
            
            params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
            params.put("IS_REAL_NAME", "1");
            params.put("REAL_NAME", "true");
            params.put("TRADE_TYPE_CODE", "7814");
            params.putAll(data);
            params = transData(params, batchDataTypes); 
            
            IDataset results = CSAppCall.call("SS.CreateM2MTagRegSVC.tradeReg", params);
            
            return results;
        }
        
        //add zhangxing3 for REQ201904180053新增批量宽带产品变更界面
        else if ("BATWIDEPRODUCTCHANGE".equals(batchOperType))
        {
            params = new DataMap();
            params.putAll(data);            
            params.put("PRODUCT_ID", data.getString("DATA1"));
            if(!"".equals(data.getString("DATA2","")))
            {
            	String salePackageId = data.getString("DATA2","");
            	params.put("PACKAGE_ID", salePackageId);
            }
            IDataset results = CSAppCall.call("SS.WidenetChangeProductIntfSVC.changeWideProductIntfForBat", params);
            return results;
        }
        //add zhangxing3 for REQ201904180053新增批量宽带产品变更界面

        //add guonj for REQ201910310002 关于企业宽带套餐批量变更的开发需求 
        else if ("BATMODWIDEPACKAGE".equals(batchOperType))
        {
        	params = new DataMap();
            params.putAll(data);            
            params.put("PRODUCT_ID", data.getString("DATA1"));
            //无营销活动
            params.put("PACKAGE_ID", "");
            IDataset results = CSAppCall.call("SS.WidenetChangeProductIntfSVC.changeWideProductIntfForBat", params);
            return results;
        }
        //add guonj for REQ201910310002 关于企业宽带套餐批量变更的开发需求 
        
        else if ("MODIFYPRODUCT_MAIN".equals(batchOperType) || "MODIFYPRODUCT_NAME".equals(batchOperType))
        {// 批量主套餐办理  @yanwu
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "110");
            params.put("ORDER_TYPE_CODE", "110");
            params.putAll(data);
            
            String strBookingTag = data.getString("DATA3");
            
            params.put("ELEMENT_ID", data.getString("DATA1"));	
            params.put("ELEMENT_TYPE_CODE", "P");		
            params.put("MODIFY_TAG", "2");
            if("0".equals(strBookingTag))
            {
            	params.put("BOOKING_TAG", "0");
            }
            else
            {
            	params.put("BOOKING_TAG", "1");
                params.put("START_DATE", data.getString("DATA2"));	
			}
            
            String strData = "DATA";
            String strDataKey = "";
            String strDiscntStr = "DISCNT_STR";
            String strDiscntStrKey = "";
            String strValue = "";
            
            for(int i = 1; i <= 17; i++)
            {
            	int n = i + 3;
            	strDataKey = strData + n;
            	strValue = params.getString(strDataKey, "");
            	if(StringUtils.isBlank(strValue))
            	{
            		continue;	//为空跳过
            	}
            	strDiscntStrKey = strDiscntStr + i;
            	params.put(strDiscntStrKey, strValue);
            }
            //params = transData(params, batchDataTypes);	//该函数不适用此批量业务
            IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", params);
            return results;
        }
        else if("SILENCECALLDEAL_PWLW".equals(batchOperType))
        {//物联网沉默用户批量激活
            String strSn = data.getString("SERIAL_NUMBER");
            params = new DataMap();
            params.putAll(data);
            params.put("SERIAL_NUMBER", strSn);
            IDataset results = CSAppCall.call("SS.SilenceTransNormalSVC.tradeReg", params);
            
            return results;
        }
        else if("SVCQSUPERVISOR".equals(batchOperType))
        {   //服务质量监督员
        	log.error("BatDealDispatchSVCxxxxxxxxxxxx1127 "+data);
        	
        	//"ORDER_ID":"1119061003734497","USER_ID":"1119031839765659","ORDER_TYPE_CODE":"3810","TRADE_ID":"1119061013023049"
        	
        	
            String strSn = data.getString("SERIAL_NUMBER");
            UcaData uca = UcaDataFactory.getNormalUca(strSn); 
            String userId = uca.getUserId();
            
            IDataset results = new DatasetList();
        	IData redata = new DataMap();
        	redata.put("ORDER_ID",  SeqMgr.getOrderId());
        	redata.put("TRADE_ID",  SeqMgr.getTradeId());
        	redata.put("USER_ID", userId);
        	
        	results.add(redata);
        	
            params = new DataMap();
            params.putAll(data);
            params.put("SERIAL_NUMBER", strSn);                       
            
            //先查下是否有记录，如有则不插入
            IData cond=new DataMap();
            cond.put("USER_ID", userId);
            cond.put("RSRV_VALUE_CODE", "SVCQ_SUPERVISOR");             
        	log.error("BatDealDispatchSVCxxxxxxxxxxxx1142 "+cond);
            
            IDataset otherds = Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USERID_ENDDATE", cond);
        	log.error("BatDealDispatchSVCxxxxxxxxxxxx1145 "+otherds);

			if (IDataUtil.isEmpty(otherds)) {
				IData inparams = new DataMap();
				String sysdate = SysDateMgr.getSysTime();
				String instId = SeqMgr.getInstId();
				String endDate = SysDateMgr.END_DATE_FOREVER;
				inparams.put("PARTITION_ID", new Long(userId) % 10000);
				inparams.put("USER_ID", userId);
				inparams.put("INST_ID", instId);
				inparams.put("RSRV_VALUE_CODE", "SVCQ_SUPERVISOR");
				inparams.put("RSRV_VALUE", strSn);
				inparams.put("START_DATE", sysdate);  
				inparams.put("END_DATE", endDate);
				inparams.put("STAFF_ID", getVisit().getStaffId());
				inparams.put("DEPART_ID", getVisit().getDepartId());
				inparams.put("MODIFY_TAG", "0");
				inparams.put("UPDATE_TIME", sysdate);
				inparams.put("UPDATE_STAFF_ID", getVisit().getStaffId());
				inparams.put("UPDATE_DEPART_ID", getVisit().getDepartId());
				inparams.put("REMARK", "服务质量监督员");
	        	log.error("BatDealDispatchSVCxxxxxxxxxxxx1166 "+inparams);

//				boolean flag = Dao.insert("TF_F_USER_OTHER", inparams);
	        	IDataset reInfos = CSAppCall.call("SS.UserOtherSVC.insertUserOther", inparams);

	        	log.error("BatDealDispatchSVCxxxxxxxxxxxx1171 "+reInfos);

				/*if (IDataUtil.isNotEmpty(reInfos)) {
					results = reInfos;
				}*/
			}
            return results;
        }
        else if("MODIFYCUSTINFO_M2M".equals(batchOperType))
        {//行业应用卡类用户资料变更 @yanwu
        	IData ups = new DataMap();
        	UcaData uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
        	ups.put("USER_ID", uca.getUserId());
        	ups.put("CUST_ID", uca.getCustId());
        	ups.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        	
        	IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", ups);
            
            params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
            params.put("IS_REAL_NAME", "1");
            params.put("REAL_NAME", "true");
            params.put("CHECK_MODE", "F");// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
            params.put("TRADE_TYPE_CODE", "60");
            params.put("USER_ID", uca.getUserId());
            params.putAll(data);
            params = transData(params, batchDataTypes); 
            params.put("BATCH_OPER_TYPE", batchOperType);
            IDataset results = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);
            
            return results;
        }else if("MODIFYGROUPPSPTINFO".equals(batchOperType))
        {//以单位证件开户的开户证件变更  liquan
        	//System.out.println("BatDealDispatchSVCxxxxxxxxxxxxxxxxxxx1088 "+data);
            IData ups = new DataMap();
            UcaData uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
            ups.put("USER_ID", uca.getUserId());
            ups.put("CUST_ID", uca.getCustId());
            ups.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            
            IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", ups);
            
            params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
            params.put("IS_REAL_NAME", "1");
            params.put("REAL_NAME", "true");
            params.put("CHECK_MODE", "F");// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
            //REQ201808230031新增固话批量资料变更功能
            String serialNumber = data.getString("SERIAL_NUMBER");
            if(serialNumber.startsWith("898") || serialNumber.startsWith("0898")){
				params.put("TRADE_TYPE_CODE", "3811");
			} else {
				params.put("TRADE_TYPE_CODE", "60");
			}
            params.putAll(data);
            params = transData(params, batchDataTypes); 
            
            params.put("AGENT_CUST_NAME", params.getString("RSRV_STR7","").trim());
            params.put("AGENT_PSPT_TYPE_CODE", params.getString("RSRV_STR8","").trim());
            params.put("AGENT_PSPT_ID", params.getString("RSRV_STR9","").trim());
            params.put("AGENT_PSPT_ADDR", params.getString("RSRV_STR10","").trim());            

            params.put("USE",data.getString("DATA13","").trim());
            params.put("USE_PSPT_TYPE_CODE", data.getString("DATA14","").trim());
            params.put("USE_PSPT_ID", data.getString("DATA15","").trim());
            params.put("USE_PSPT_ADDR", data.getString("DATA16","").trim());   
        	
            IDataset results = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);
            
            return results;
        }
        else if("MODIFYTDPSPTINFO".equals(batchOperType))
        {//无线固话单位证件实名制登记  liquan
        	//System.out.println("BatDealDispatchSVCxxxxxxxxxxxxxxxxxxx1146 "+data);
        	//System.out.println("BatDealDispatchSVCxxxxxxxxxxxxxxxxxxx1147 "+params);
        	//System.out.println("BatDealDispatchSVCxxxxxxxxxxxxxxxxxxx1148 "+batchDataTypes);        	

            IData ups = new DataMap();
            UcaData uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
            ups.put("USER_ID", uca.getUserId());
            ups.put("CUST_ID", uca.getCustId());
            ups.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            
            IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", ups);
        	//System.out.println("BatDealDispatchSVCxxxxxxxxxxxxxxxxxxx1157 "+custInfos);        	

            params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
            params.put("IS_REAL_NAME", "1");
            params.put("REAL_NAME", "true");
            params.put("CHECK_MODE", "F");// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
            //REQ201808230031新增固话批量资料变更功能
            String serialNumber = data.getString("SERIAL_NUMBER");
			if (serialNumber.startsWith("898") || serialNumber.startsWith("0898") || uca.getBrandCode().equals("TDYD")) {
				params.put("TRADE_TYPE_CODE", "3811");
			} 
            params.putAll(data);
            params = transData(params, batchDataTypes); 
        	//System.out.println("BatDealDispatchSVCxxxxxxxxxxxxxxxxxxx1170 "+params);        	
        	
            params.put("AGENT_CUST_NAME", params.getString("AGENT_CUST_NAME","").trim());
            params.put("AGENT_PSPT_TYPE_CODE", params.getString("AGENT_PSPT_TYPE_CODE","").trim());
            params.put("AGENT_PSPT_ID", params.getString("AGENT_PSPT_ID","").trim());
            params.put("AGENT_PSPT_ADDR", params.getString("AGENT_PSPT_ADDR","").trim());                         

            params.put("USE", params.getString("USE","").trim());
            params.put("USE_PSPT_TYPE_CODE", params.getString("USE_PSPT_TYPE_CODE","").trim());
            params.put("USE_PSPT_ID", params.getString("USE_PSPT_ID","").trim());
            params.put("USE_PSPT_ADDR", params.getString("USE_PSPT_ADDR","").trim());  
            
        	//System.out.println("BatDealDispatchSVCxxxxxxxxxxxxxxxxxxx1182 "+params);        	
            
            IDataset results = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);
        	//System.out.println("BatDealDispatchSVCxxxxxxxxxxxxxxxxxxx1176 "+results);        	

            return results;
        }
        else if ("BATQQNET".equals(batchOperType))
        {// 批量办理亲亲网业务  @yanwu
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "283");
            params.put("ORDER_TYPE_CODE", "283");
            params.putAll(data);
            
            //主号信息
            params.put("PRODUCT_ID", "99000001");	//这里写死，如产品ID变更，这里也要跟着改！
            params.put("DISCNT_CODE", "3410");		//这里写死，如亲亲网主号优惠再次变更，这里也要跟着改！
            params.put("SHORT_CODE", "520");
            params.put("FMY_VERIFY_MODE", "1");		//短信邀请校验
            
            //副号信息
            IDataset mebList = new DatasetList();
            String strData = "DATA";
            String strValue = "";
            String strKey = "";
            String strShortCodeB = "52";
            for(int i=1; i<=9; i++){
            	strKey = strData + i;
            	strValue = params.getString(strKey, "");
            	if( "".equals(strValue) ){
            		continue;	//副号为空跳过
            	}
            	IData meb = new DataMap();
                meb.put("SERIAL_NUMBER_B", strValue);
                meb.put("DISCNT_CODE_B", "3411");		//这里写死，如亲亲网副号优惠再次变更，这里也要跟着改！
                meb.put("tag", "0");
                meb.put("SHORT_CODE_B", strShortCodeB + i);
                meb.put("DISCNT_NAME_B", "亲亲网套餐畅享版(副号码)");
                meb.put("START_DATE", SysDateMgr.getSysTime());
                meb.put("END_DATE", SysDateMgr.getTheLastTime());
                params.remove(strKey);
                mebList.add(meb);
            }
            params.put("MEB_LIST", mebList);
            //params = transData(params, batchDataTypes);	//该函数不适用此批量业务
            IDataset results = CSAppCall.call("SS.FamilyCreateRegSVC.tradeReg", params);
            return results;
        }else if("MODIFY_GROUPMEMBER".equals(batchOperType))
        {
          	/**
          	 * 
          	 * REQ201608150016_新增“以单位证件开户集团成员实名资料维护界面”需求
          	 * @zhuoyz_mtw  
          	 * 20160826
          	 * 
          	 */
              UcaData uca = null;
              IData inputData = new DataMap();
          	uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
          	inputData.put("USER_ID", uca.getUserId());
          	inputData.put("CUST_ID", uca.getCustId());
          	inputData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
          	           
              IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", inputData);
              
              params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
              params.put("CHECK_MODE", data.getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱
              /**
              * REQ201610310009_优化“以单位证件开户集团成员实名资料变更”界面 
              * @author zhuoyingzhi
              * 紧急需求
              * 20161101
              * 
              */
              params.put("IS_REAL_NAME", "1");//1为实名制
              params.putAll(data);
            //REQ201808230031新增固话批量资料变更功能
            String serialNumber = data.getString("SERIAL_NUMBER");
            if(serialNumber.startsWith("898") || serialNumber.startsWith("0898")){
                params.put("TRADE_TYPE_CODE", "3811");
            }
              params = transData(params, batchDataTypes); 
       
              IDataset results = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);
              
              return results;
          }else if("TESTCARDUSER".equals(batchOperType)){
	          /**	 	 
	           * REQ201609060001_2016年下半年测试卡功能优化（二） 	 
	           * @author zhuoyingzhi	 	 
	           * 20160930	 	 
	           */	 	 
		        params = new DataMap();	 	 
		        params.put("CHECK_MODE", data.getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱	 	 
		        params.putAll(data);	 	 
		        params = transData(params, batchDataTypes);	 	 
		       	 	 
		        IDataset results = CSAppCall.call("SS.TestCardUserManageRegSVC.tradeReg", params);	 	 
		       	 	 
	           return results;	 
         }else if("TESTCARDCUSTNAME".equals(batchOperType)){
        	 /**
        	  * REQ201610200010_关于测试卡管理的三点优化
        	  * @author zhuoyingzhi
        	  * 批量修改测试卡客户名称
        	  */
	        params = new DataMap();
            UcaData uca = null;
            IData inputData = new DataMap();
        	uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
        	inputData.put("USER_ID", uca.getUserId());
        	inputData.put("CUST_ID", uca.getCustId());
        	inputData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));

        	           
            IDataset custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", inputData);
            
            params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
	        params.put("CHECK_MODE", data.getString("CHECK_MODE"));// getData()如果没有传入后台，则需要取CHECK_MODE传入，否则认证方式会错乱	 	 
        	/**
        	 * 实名制标志
        	 * 由于在CheckIsExistsProducts.java中判断newIsRealName.equals("1")语法错误,
        	 * 正确判断应该是"1".equals(newIsRealName)
        	 * 给它实名制
        	 */
	        params.put("IS_REAL_NAME", "1");//1为实名制
	        params.putAll(data);	 	 
	        params = transData(params, batchDataTypes);	 	 
	       	 	 
	        IDataset results = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", params);	 	 
	       	 	 
           return results;	
        	 
        	 
         }else if("TESTCARDMAXFLOWVALUE".equals(batchOperType)){
        	 /**
        	  * REQ201610200010_关于测试卡管理的三点优化
        	  * @author zhuoyingzhi
        	  * 批量修改测试卡流量封顶值
        	  */
             params = new DataMap();
             params.put("TRADE_TYPE_CODE", "110");
             params.put("ORDER_TYPE_CODE", "110");
             params.put("ELEMENT_ID", "9501");
             params.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SVC);
             
             UcaData uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
             //判断手机号码是否有 流量封顶服务
             IDataset userSvcInfo=BatDataImportBean.getUserSVCInfoByEnd(uca.getUserId(), "9501");
             if(IDataUtil.isEmpty(userSvcInfo)){
            	  //没有流量封顶服务(新增流量封顶服务)
            	 params.put("MODIFY_TAG", "0");
             }else{
            	 //存在修改
            	 params.put("MODIFY_TAG", "2");
             }
             
             params.put("BOOKING_TAG", "0");
             params.put("ATTR_STR1", "FD_CODE");
             params.putAll(data);
             params = transData(params, batchDataTypes);
             IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", params);
             return results;
         }else if ("TESTCARDOFFICEOPEN".equals(batchOperType))
         {   
        	 /**
        	  * REQ201612010002_关于2016年下半年测试卡规则优化需求（一）
        	  * @author zhuyingzhi
        	  * 20170110
        	  * <br/>
        	  * 测试号码批量局开
        	  */
             params = new DataMap();
             params.put("TRADE_TYPE_CODE", "126");
             params.put("ORDER_TYPE_CODE", "126");
             params.putAll(data);
             params = transData(params, batchDataTypes);
             IDataset results = CSAppCall.call("SS.ChangeSvcStateIntfSVC.createOfficeStopOpenReg", params);
             return results;

         }else if ("TESTPHONERETURN".equals(batchOperType))
         {// 测试号码批量销号
             params = new DataMap();
             params.put("TRADE_TYPE_CODE", "192");
             params.put("ORDER_TYPE_CODE", "192");
             params.putAll(data);
             params = transData(params, batchDataTypes);
             IDataset results = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", params);
             return results;
         }else if ("BATOTHERCREATEUSER".equals(batchOperType))
         { //REQ201806190026 关于和校园业务异网号码批量开户的需求 
             params = new DataMap();
             params.put("TRADE_TYPE_CODE", "7510");
             params.put("ORDER_TYPE_CODE", "7510");
             params.put("ROUTE_EPARCHY_CODE", "0898");
             
             params.putAll(data);
             params = transData(params, batchDataTypes);
             pottingPara(params);
             IDataset results = CSAppCall.call("SS.CreateHPersonUserRegSVC.tradeReg", params);
             return results;
         }else if("BATBENEFITADDUSENUM".equals(batchOperType)){
            params = new DataMap();
            params.put("TRADE_TYPE_CODE", "714");
            params.put("ORDER_TYPE_CODE", "714");
            params.putAll(data);
            params = transData(params, batchDataTypes);
            IDataset results = CSAppCall.call("SS.BenefitCenterIntfSVC.batAddRigntUseNum", params);
            return results;
        }else if("BATOPENSTOP".equals(batchOperType)) {

            params = new DataMap();
            params.putAll(data);
            params = transData(params, batchDataTypes);

            String netTypeCode = params.getString("NET_TYPE_CODE");
            String eparchyCode = params.getString("ROUTE_EPARCHY_CODE");
            String beautyMark = params.getString("IS_BEAUTY");

            String tradeTypeCode = "192";
            if (org.apache.commons.lang.StringUtils.equals(netTypeCode, "18")) {
                tradeTypeCode = "3804";
            }
            params.put("TRADE_TYPE_CODE", tradeTypeCode);

            if (tradeTypeCode.equals("192") && beautyMark != null && beautyMark.equals("1")) {
                //如果是吉祥号销户
                params.put("IS_LUCKY_NUMBER", "1");
            }
            params.put("ROUTE_EPARCHY_CODE", eparchyCode);
            params.put("ACTIVE_TAG", "1");
            params.put("SKIP_RULE", "TRUE");

            log.info("买断批开到期未激活销户批量业务入参: " + params);
            IDataset results = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", params);

            StringBuilder sql = new StringBuilder(1000);
            IData map = new DataMap();
            map.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            sql.append(" UPDATE TS_S_USER_BACK SET TAG = '1' WHERE SERIAL_NUMBER = :SERIAL_NUMBER ");
            Dao.executeUpdate(sql, map);

            return results;
        }
        /*
         * else if("FAMILYNETDISCNT".equals(batchOperType)){//批量办理家庭网套餐D }else
         * if("TERMINALACTIVATION".equals(batchOperType)){//集团下发终端激活D }else
         * if("TERMINALPRESENTFEE".equals(batchOperType)){//集团下发终端赠费D }else
         * if("TERMINALRETURN".equals(batchOperType)){//集团下发终端退货D }else if("ACCTDAYCHG".equals(batchOperType)){//批量账期变更D
         * }else
         * if("COUNTRYNETACTIVE".equals(batchOperType)){//营销活动（入乡情网送农信通）批量办理D String codingstr =
         * data.getString("CODING_STR"); params = new DataMap(codingstr); data.remove("CODING_STR");
         * params.putAll(data); params = transData(params,batchDataTypes); IDataset results =
         * CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", params); return results ; }
         */

        return null;
    }

    /**
	 * @Description：组装和校园异网开户需要的参数
	 * @param:@param params
	 * @return void
     * @throws Exception 
	 * @Author :tanzheng
	 * @date :2018-8-27下午06:27:29
	 */
	private void pottingPara(IData params) throws Exception {
		params.put("PAY_NAME", params.getString("CUST_NAME"));
		
		IData data = new DataMap();
		data.put("ELEMENT_ID", "84007636");
		data.put("ELEMENT_TYPE_CODE", "S");
		data.put("PRODUCT_ID", params.getString("DATA10"));
		data.put("PACKAGE_ID", "0");
		data.put("MODIFY_TAG", "0");
		data.put("START_DATE", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
		data.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
		data.put("INST_ID", "");
		IDataset dataset = new DatasetList();
		dataset.add(data);
		params.put("SELECTED_ELEMENTS", dataset);
		params.put("PAY_MODE_CODE", "0");
		params.put("PRODUCT_ID", params.getString("DATA10"));
		params.put("X_TRANS_CODE", "SS.CreateHPersonUserRegSVC.tradeReg");
		
		
	}

	public IData transData(IData params, IDataset batchDataTypes) throws Exception
    {
		//System.out.println("BatDealDispatchSVCxxxxxxxxxxxxx1361 "+params);
		//System.out.println("BatDealDispatchSVCxxxxxxxxxxxxx1362 "+batchDataTypes);
		
        if (IDataUtil.isNotEmpty(batchDataTypes))
        {
            for (int i = 0; i < batchDataTypes.size(); i++)
            {
                IData batchDataType = batchDataTypes.getData(i);
                String dataNo = batchDataType.getString("DATA_NO");
                String dataCode = batchDataType.getString("DATA_CODE");
                params.put(dataCode, params.getString("DATA" + dataNo));
                params.remove("DATA" + dataNo);
            }
        }
        return params;
    }
    
}
