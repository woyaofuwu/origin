
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.GrpRegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.template.GroupSmsTemplateBean;

import java.util.List;


/**
 * @description BBOSS某些业务短信需要特殊处理
 * @author chenkh
 * @date 2015年5月20日
 *
 */
public class DealBbossSmsInfoBean extends GroupSmsTemplateBean
{
	
	/**
	 * @description BBOSS短信模板选择
	 * @author xunyl
	 * @date 2015-06-27
	 */
	@SuppressWarnings("unchecked")
	public static boolean chooseUserSms(GrpRegTradeData grpRegTradeData)throws Exception{
		//1- 初始化返回结果
		boolean result = false;
		
		//2- 获取产品编号
		String productId = grpRegTradeData.getGrpUca().getProductId();
		String merchpId = GrpCommonBean.productToMerch(productId, 0);
		
		//3- 自登记的短信获取模板编号
		if(StringUtils.equals("99904", merchpId)){//定向流量统付
			result = dircFlexPaySms(grpRegTradeData);
		}else if(StringUtils.equals("99905", merchpId)){//通用流量统付
			result = genrFlexPaySms(grpRegTradeData);
		}else if(StringUtils.equals("99908", merchpId)){//闲时定向流量统付
            result = dircIdleFlexPaySms(grpRegTradeData);
        }else if(StringUtils.equals("99909", merchpId)){//闲时通用流量统付
            result = genrIdleFlexPaySms(grpRegTradeData);
        }
		
		//4- 返回结果
		return result;
	}
	
	/**
	 * @description 定向流量统付短信模板选择
	 * @author xunyl
	 * @date 2015-06-27
	 */
	@SuppressWarnings("unchecked")
	private static boolean dircFlexPaySms(GrpRegTradeData grpRegTradeData)throws Exception{
		//1- 初始化返回结果
		boolean result = false;
		
		//2- 获取模板编号
		String templateId = grpRegTradeData.getMap().getString("TEMPLATE_ID");
		
		//3- 判断当前模板是成员开通提醒(对应业务类型为4694)还是叠加包开通提醒(对应业务类型为4695)模板
		IData inparam = new DataMap();
		String grpProductUserId = grpRegTradeData.getGrpUca().getUserId();
		String bizType = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999044008");
		inparam.put("BIZ_TYPE", bizType);
		String policyEffRule = getAttrValueByAttrCode("1103",grpRegTradeData);
		inparam.put("POLICY_EFF_RULE", policyEffRule);
			
		String sendSmsTemplateId = "";
		String tradeTypeCode = grpRegTradeData.getMainTradeData().getTradeTypeCode();
		if(StringUtils.equals("4694", tradeTypeCode)){		
			String effMonth = getAttrValueByAttrCode("1104",grpRegTradeData);
			inparam.put("EFF_MONTH", effMonth);			
			sendSmsTemplateId = chooseMebTemplateIdForDirc(inparam);
		}else if(StringUtils.equals("4695", tradeTypeCode)){		
			List tradeMerchMebDisInfoList = grpRegTradeData.get("TF_B_TRADE_GRP_MERCH_MB_DIS");
			if(tradeMerchMebDisInfoList != null && tradeMerchMebDisInfoList.size()>0){			
				return true;
			}
		}
			
		//4- 传入的模板编号和参数验证所得的模板编号一致，则返回true
		result = StringUtils.equals(templateId, sendSmsTemplateId);
		
		//5- 返回结果
		return result;
	}
	
	/**
     * @description 闲时定向流量统付短信模板选择
     * @author xunyl
     * @date 2015-07-29
     */
    @SuppressWarnings("unchecked")
    private static boolean dircIdleFlexPaySms(GrpRegTradeData grpRegTradeData)throws Exception{
        //1- 初始化返回结果
        boolean result = false;
        
        //2- 获取模板编号
        String templateId = grpRegTradeData.getMap().getString("TEMPLATE_ID");
        
        //3- 判断当前模板是成员开通提醒(对应业务类型为4694)还是叠加包开通提醒(对应业务类型为4695)模板
        IData inparam = new DataMap();
        String grpProductUserId = grpRegTradeData.getGrpUca().getUserId();
        String bizType = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999084008");
        inparam.put("BIZ_TYPE", bizType);
        String policyEffRule = getAttrValueByAttrCode("1103",grpRegTradeData);
        inparam.put("POLICY_EFF_RULE", policyEffRule);
            
        String sendSmsTemplateId = "";
        String tradeTypeCode = grpRegTradeData.getMainTradeData().getTradeTypeCode();
        if(StringUtils.equals("4694", tradeTypeCode)){      
            String effMonth = getAttrValueByAttrCode("1104",grpRegTradeData);
            inparam.put("EFF_MONTH", effMonth);         
            sendSmsTemplateId = chooseMebTemplateIdForIdleDirc(inparam);
        }else if(StringUtils.equals("4695", tradeTypeCode)){        
            List tradeMerchMebDisInfoList = grpRegTradeData.get("TF_B_TRADE_GRP_MERCH_MB_DIS");
            if(tradeMerchMebDisInfoList != null && tradeMerchMebDisInfoList.size()>0){          
                return true;
            }
        }
            
        //4- 传入的模板编号和参数验证所得的模板编号一致，则返回true
        result = StringUtils.equals(templateId, sendSmsTemplateId);
        
        //5- 返回结果
        return result;
    }
	
	/**
	 * @description 根据条件筛选定向流量统付成员开通短信模板编号
	 * @author xunyl
	 * @date 2015-06-29
	 */
	private static String chooseMebTemplateIdForDirc(IData inparam)throws Exception{
		//1- 初始化模板编号
		String templateId = "";
		
		//2- 获取业务模式(1:不限用户,全量统付 2:不限用户，限量统付 3:指定用户,全量统付 4:指定用户,定额统付 5:指定用户,限量统付)
		String bizType = inparam.getString("BIZ_TYPE");
		
		//3- 获取套餐生效规则(1-立即生效 2-下账期生效)
		String policyEffRule = inparam.getString("POLICY_EFF_RULE");
		
		//4- 获取赠送有效期(00代表无限期)
		String effMonth = inparam.getString("EFF_MONTH");
		
		//5- 根据业务规范，筛选满足条件的模板
		//5-1  全量统付、立即生效、无限期
		if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
				StringUtils.equals("1", policyEffRule) && StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00001";
		}
		
		//5-2  全量统付、立即生效、非无限期
		if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
				StringUtils.equals("1", policyEffRule) && !StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00002";
		}
		
		//5-3  全量统付、下账期生效、无限期
		if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
				StringUtils.equals("2", policyEffRule) && StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00003";
		}
		
		//5-4  全量统付、下账期生效、非无限期
		if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
				StringUtils.equals("2", policyEffRule) && !StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00004";
		}
		
		//5-5 (定额/限量)统付、立即生效、无限期
		if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
				StringUtils.equals("5", bizType)) && StringUtils.equals("1", policyEffRule) && 
				StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00005";
		}
		
		//5-6 (定额/限量)统付、立即生效、非无限期
		if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
				StringUtils.equals("5", bizType)) && StringUtils.equals("1", policyEffRule) && 
				!StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00006";
		}
		
		//5-7 (定额/限量)统付、下账期生效、无限期
		if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
				StringUtils.equals("5", bizType)) && StringUtils.equals("2", policyEffRule) && 
				StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00007";
		}
		
		//5-8 (定额/限量)统付、下账期生效、非无限期
		if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
				StringUtils.equals("5", bizType)) && StringUtils.equals("2", policyEffRule) && 
				!StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00008";
		}
		
		//6- 返回模板编号 
		return templateId;
	}	
	
	/**
     * @description 根据条件筛选定向流量统付成员开通短信模板编号
     * @author xunyl
     * @date 2015-07-29
     */
    private static String chooseMebTemplateIdForIdleDirc(IData inparam)throws Exception{
        //1- 初始化模板编号
        String templateId = "";
        
        //2- 获取业务模式(1:不限用户,全量统付 2:不限用户，限量统付 3:指定用户,全量统付 4:指定用户,定额统付 5:指定用户,限量统付)
        String bizType = inparam.getString("BIZ_TYPE");
        
        //3- 获取套餐生效规则(1-立即生效 2-下账期生效)
        String policyEffRule = inparam.getString("POLICY_EFF_RULE");
        
        //4- 获取赠送有效期(00代表无限期)
        String effMonth = inparam.getString("EFF_MONTH");
        
        //5- 根据业务规范，筛选满足条件的模板
        //5-1  全量统付、立即生效、无限期
        if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
                StringUtils.equals("1", policyEffRule) && StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00019";
        }
        
        //5-2  全量统付、立即生效、非无限期
        if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
                StringUtils.equals("1", policyEffRule) && !StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00020";
        }
        
        //5-3  全量统付、下账期生效、无限期
        if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
                StringUtils.equals("2", policyEffRule) && StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00021";
        }
        
        //5-4  全量统付、下账期生效、非无限期
        if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
                StringUtils.equals("2", policyEffRule) && !StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00022";
        }
        
        //5-5 (定额/限量)统付、立即生效、无限期
        if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
                StringUtils.equals("5", bizType)) && StringUtils.equals("1", policyEffRule) && 
                StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00023";
        }
        
        //5-6 (定额/限量)统付、立即生效、非无限期
        if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
                StringUtils.equals("5", bizType)) && StringUtils.equals("1", policyEffRule) && 
                !StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00024";
        }
        
        //5-7 (定额/限量)统付、下账期生效、无限期
        if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
                StringUtils.equals("5", bizType)) && StringUtils.equals("2", policyEffRule) && 
                StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00025";
        }
        
        //5-8 (定额/限量)统付、下账期生效、非无限期
        if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
                StringUtils.equals("5", bizType)) && StringUtils.equals("2", policyEffRule) && 
                !StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00026";
        }
        
        //6- 返回模板编号 
        return templateId;
    }
	
	/**
	 * @description 通用流量统付短信模板选择
	 * author xunyl
	 * @date 2015-06-27
	 */
	@SuppressWarnings("unchecked")
	private static boolean genrFlexPaySms(GrpRegTradeData grpRegTradeData)throws Exception{
		//1- 初始化返回结果
		boolean result = false;
		
		//2- 获取模板编号
		String templateId = grpRegTradeData.getMap().getString("TEMPLATE_ID");
		
		//3- 判断当前模板是成员开通提醒(对应业务类型为4694)还是叠加包开通提醒(对应业务类型为4695)模板
		IData inparam = new DataMap();
		String grpProductUserId = grpRegTradeData.getGrpUca().getUserId();
		String bizType = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999054008");
		inparam.put("BIZ_TYPE", bizType);
		String policyEffRule = getAttrValueByAttrCode("1103",grpRegTradeData);
		inparam.put("POLICY_EFF_RULE", policyEffRule);
			
		String sendSmsTemplateId = "";
		String tradeTypeCode = grpRegTradeData.getMainTradeData().getTradeTypeCode();
		if(StringUtils.equals("4694", tradeTypeCode)){		
			String effMonth = getAttrValueByAttrCode("1104",grpRegTradeData);
			inparam.put("EFF_MONTH", effMonth);			
			sendSmsTemplateId = chooseMebTemplateIdForGenr(inparam);
		}else if(StringUtils.equals("4695", tradeTypeCode)){		
		    List tradeMerchMebDisInfoList = grpRegTradeData.get("TF_B_TRADE_GRP_MERCH_MB_DIS");
            if(tradeMerchMebDisInfoList != null && tradeMerchMebDisInfoList.size()>0){      
				return true;
			}
		}
			
		//4- 传入的模板编号和参数验证所得的模板编号一致，则返回true
		result = StringUtils.equals(templateId, sendSmsTemplateId);
		
		//5- 返回结果
		return result;
	}
	
	/**
     * @description 闲时通用流量统付短信模板选择
     * author xunyl
     * @date 2015-07-29
     */
    @SuppressWarnings("unchecked")
    private static boolean genrIdleFlexPaySms(GrpRegTradeData grpRegTradeData)throws Exception{
        //1- 初始化返回结果
        boolean result = false;
        
        //2- 获取模板编号
        String templateId = grpRegTradeData.getMap().getString("TEMPLATE_ID");
        
        //3- 判断当前模板是成员开通提醒(对应业务类型为4694)还是叠加包开通提醒(对应业务类型为4695)模板
        IData inparam = new DataMap();
        String grpProductUserId = grpRegTradeData.getGrpUca().getUserId();
        String bizType = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999094008");
        inparam.put("BIZ_TYPE", bizType);
        String policyEffRule = getAttrValueByAttrCode("1103",grpRegTradeData);
        inparam.put("POLICY_EFF_RULE", policyEffRule);
            
        String sendSmsTemplateId = "";
        String tradeTypeCode = grpRegTradeData.getMainTradeData().getTradeTypeCode();
        if(StringUtils.equals("4694", tradeTypeCode)){      
            String effMonth = getAttrValueByAttrCode("1104",grpRegTradeData);
            inparam.put("EFF_MONTH", effMonth);         
            sendSmsTemplateId = chooseMebTemplateIdForIdleGenr(inparam);
        }else if(StringUtils.equals("4695", tradeTypeCode)){        
            List tradeMerchMebDisInfoList = grpRegTradeData.get("TF_B_TRADE_GRP_MERCH_MB_DIS");
            if(tradeMerchMebDisInfoList != null && tradeMerchMebDisInfoList.size()>0){      
                return true;
            }
        }
            
        //4- 传入的模板编号和参数验证所得的模板编号一致，则返回true
        result = StringUtils.equals(templateId, sendSmsTemplateId);
        
        //5- 返回结果
        return result;
    }
	
	/**
	 * @description 根据条件筛选通用流量统付成员开通短信模板编号
	 * @author xunyl
	 * @date 2015-06-29
	 */
	private static String chooseMebTemplateIdForGenr(IData inparam)throws Exception{
		//1- 初始化模板编号
		String templateId = "";
		
		//2- 获取业务模式(1:不限用户,全量统付 2:不限用户，限量统付 3:指定用户,全量统付 4:指定用户,定额统付 5:指定用户,限量统付)
		String bizType = inparam.getString("BIZ_TYPE");
		
		//3- 获取套餐生效规则(1-立即生效 2-下账期生效)
		String policyEffRule = inparam.getString("POLICY_EFF_RULE");
		
		//4- 获取赠送有效期(00代表无限期)
		String effMonth = inparam.getString("EFF_MONTH");
		
		//5- 根据业务规范，筛选满足条件的模板
		//5-1  全量统付、立即生效、无限期
		if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
				StringUtils.equals("1", policyEffRule) && StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00010";
		}
		
		//5-2  全量统付、立即生效、非无限期
		if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
				StringUtils.equals("1", policyEffRule) && !StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00011";
		}
		
		//5-3  全量统付、下账期生效、无限期
		if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
				StringUtils.equals("2", policyEffRule) && StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00012";
		}
		
		//5-4  全量统付、下账期生效、非无限期
		if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
				StringUtils.equals("2", policyEffRule) && !StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00013";
		}
		
		//5-5 (定额/限量)统付、立即生效、无限期
		if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
				StringUtils.equals("5", bizType)) && StringUtils.equals("1", policyEffRule) && 
				StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00014";
		}
		
		//5-6 (定额/限量)统付、立即生效、非无限期
		if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
				StringUtils.equals("5", bizType)) && StringUtils.equals("1", policyEffRule) && 
				!StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00015";
		}
		
		//5-7 (定额/限量)统付、下账期生效、无限期
		if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
				StringUtils.equals("5", bizType)) && StringUtils.equals("2", policyEffRule) && 
				StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00016";
		}
		
		//5-8 (定额/限量)统付、下账期生效、非无限期
		if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
				StringUtils.equals("5", bizType)) && StringUtils.equals("2", policyEffRule) && 
				!StringUtils.equals("00", effMonth)){
			templateId = "CRM_SMS_GRP_BBOSS_00017";
		}
		
		//6- 返回模板编号 
		return templateId;
	}
		
	/**
     * @description 根据条件筛选通用流量统付成员开通短信模板编号
     * @author xunyl
     * @date 2015-07-29
     */
    private static String chooseMebTemplateIdForIdleGenr(IData inparam)throws Exception{
        //1- 初始化模板编号
        String templateId = "";
        
        //2- 获取业务模式(1:不限用户,全量统付 2:不限用户，限量统付 3:指定用户,全量统付 4:指定用户,定额统付 5:指定用户,限量统付)
        String bizType = inparam.getString("BIZ_TYPE");
        
        //3- 获取套餐生效规则(1-立即生效 2-下账期生效)
        String policyEffRule = inparam.getString("POLICY_EFF_RULE");
        
        //4- 获取赠送有效期(00代表无限期)
        String effMonth = inparam.getString("EFF_MONTH");
        
        //5- 根据业务规范，筛选满足条件的模板
        //5-1  全量统付、立即生效、无限期
        if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
                StringUtils.equals("1", policyEffRule) && StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00028";
        }
        
        //5-2  全量统付、立即生效、非无限期
        if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
                StringUtils.equals("1", policyEffRule) && !StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00029";
        }
        
        //5-3  全量统付、下账期生效、无限期
        if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
                StringUtils.equals("2", policyEffRule) && StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00030";
        }
        
        //5-4  全量统付、下账期生效、非无限期
        if((StringUtils.equals("1", bizType) || StringUtils.equals("3", bizType)) &&
                StringUtils.equals("2", policyEffRule) && !StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00031";
        }
        
        //5-5 (定额/限量)统付、立即生效、无限期
        if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
                StringUtils.equals("5", bizType)) && StringUtils.equals("1", policyEffRule) && 
                StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00032";
        }
        
        //5-6 (定额/限量)统付、立即生效、非无限期
        if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
                StringUtils.equals("5", bizType)) && StringUtils.equals("1", policyEffRule) && 
                !StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00033";
        }
        
        //5-7 (定额/限量)统付、下账期生效、无限期
        if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
                StringUtils.equals("5", bizType)) && StringUtils.equals("2", policyEffRule) && 
                StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00034";
        }
        
        //5-8 (定额/限量)统付、下账期生效、非无限期
        if((StringUtils.equals("2", bizType) || StringUtils.equals("4", bizType) ||
                StringUtils.equals("5", bizType)) && StringUtils.equals("2", policyEffRule) && 
                !StringUtils.equals("00", effMonth)){
            templateId = "CRM_SMS_GRP_BBOSS_00035";
        }
        
        //6- 返回模板编号 
        return templateId;
    }
	/**
	 * @description 根据各自业务判断是否采用配置模板下发短信
	 * @author xunyl
	 * @date 2015-06-29
	 */
	public static boolean isSendSmsByModel(String productSpecCode,String grpProductUserId,String mebSerailNumber)throws Exception{
		//1- 定义返回值
		boolean isSendSmsByModel = false;
		
		//2- 定向流量统付，判断是否需要采用配置模板下发短信
		if (StringUtils.equals("99904", productSpecCode)){
			isSendSmsByModel = isSendSmsForDirc(grpProductUserId,mebSerailNumber);
		}
		 
		//3- 通用流量统付，判断是否需要采用配置模板下发短信
		if(StringUtils.equals("99905", productSpecCode)){
			isSendSmsByModel = isSendSmsForGenr(grpProductUserId,mebSerailNumber);
		}
		
		//4- 闲时定向流量统付
        if (StringUtils.equals("99908", productSpecCode)){
            isSendSmsByModel = isSendSmsForIdleDirc(grpProductUserId,mebSerailNumber);
        }
        
        //5- 闲时通用流量统付
        if (StringUtils.equals("99909", productSpecCode)){
            isSendSmsByModel = isSendSmsForIdleGenr(grpProductUserId,mebSerailNumber);      
        }
        
        //6- 国际流量统付
  		if (StringUtils.equals("99910", productSpecCode)){
  			isSendSmsByModel = isSendSmsForInternationalGenr(grpProductUserId,mebSerailNumber);		
  		}

        if (StringUtils.equals("9101101", productSpecCode) || StringUtils.equals("1101011", productSpecCode)) {
            isSendSmsByModel = true;
        }

		if (StringUtils.equals("5001301", productSpecCode) || StringUtils.equals("50013", productSpecCode)) {
			isSendSmsByModel = true;
		}
		 
		//6- 返回处理结果
		return isSendSmsByModel;
	}
	
	/**
	 * @description 根据否需要向个人发送短信及是否采用标准模板发短信判断定向流量统付是否采用配置模板下发短信
	 * @author xunyl
	 * @date 2015-06-29
	 */
	private static boolean isSendSmsForDirc(String grpProductUserId,String mebSerailNumber)throws Exception{
		//1- 定义返回值
		boolean isSendSmsByModel = false;
		
		//2- 判断是否需要向个人发送短信，如果选择否，则直接返回false
		String isSendSms = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999044044");
		if(StringUtils.equals("0", isSendSms)){
			return isSendSmsByModel;
		}
		
		//3- 判断是否采用标准模板发短信，如果采用定制短信，则需要自己登记短息表，并且返回false
		String isSendStandardSms = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999044061");
		if(StringUtils.equals("0", isSendStandardSms)){
			String smsContent = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999044062");
			combinCommonDate(smsContent,mebSerailNumber);
			return isSendSmsByModel;
		}
		
		//4- 返回处理结果
		isSendSmsByModel = true;
		return isSendSmsByModel;
	}
	
	/**
     * @description 根据否需要向个人发送短信及是否采用标准模板发短信判断定向流量统付是否采用配置模板下发短信
     * @author xunyl
     * @date 2015-07-29
     */
    private static boolean isSendSmsForIdleDirc(String grpProductUserId,String mebSerailNumber)throws Exception{
        //1- 定义返回值
        boolean isSendSmsByModel = false;
        
        //2- 判断是否需要向个人发送短信，如果选择否，则直接返回false
        String isSendSms = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999084044");
        if(StringUtils.equals("0", isSendSms)){
            return isSendSmsByModel;
        }
        
        //3- 判断是否采用标准模板发短信，如果采用定制短信，则需要自己登记短息表，并且返回false
        String isSendStandardSms = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999084061");
        if(StringUtils.equals("0", isSendStandardSms)){
            String smsContent = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999084062");
            combinCommonDate(smsContent,mebSerailNumber);
            return isSendSmsByModel;
        }
        
        //4- 返回处理结果
        isSendSmsByModel = true;
        return isSendSmsByModel;
    }
	
	/**
	 * @description 根据否需要向个人发送短信判断通用流量统付是否采用配置模板下发短信
	 * @author xunyl
	 * @date 2015-06-29
	 */
	private static boolean isSendSmsForGenr(String grpProductUserId, String mebSerailNumber)throws Exception{
		//1- 定义返回值
		boolean isSendSmsByModel = false;
		
		//2- 判断是否需要向个人发送短信，如果选择否，则直接返回false
		String isSendSms = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999054044");
		if(StringUtils.equals("0", isSendSms)){
			return isSendSmsByModel;
		}
		 //3- 判断是否采用标准模板发短信，如果采用定制短信，则需要自己登记短息表，并且返回false
        String isSendStandardSms = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999054061");
        if(StringUtils.equals("0", isSendStandardSms)){
            String smsContent = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999054062");
            combinCommonDate(smsContent,mebSerailNumber);
            return isSendSmsByModel;
        }
		
		//4- 返回处理结果
		isSendSmsByModel = true;
		return isSendSmsByModel;
	}
	
	/**
	 * @author chenmw
	 * @date 2016-11-22
	 * @description 根据否需要向个人发送短信判断国际通用流量统付是否采用配置模板下发短信
	 */
	private static boolean isSendSmsForInternationalGenr(String grpProductUserId,String mebSerailNumber)throws Exception{
		//1- 定义返回值
		boolean isSendSmsByModel = false;
		//2- 判断是否需要向个人发送短信，如果选择否，则直接返回false
		String isSendSms = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999104044");
		if(StringUtils.equals("0", isSendSms))
		{
			return isSendSmsByModel;
		}
		//3- 返回处理结果
		isSendSmsByModel = true;
		return isSendSmsByModel;
	}

	
	/**
     * @description 根据否需要向个人发送短信判断通用流量统付是否采用配置模板下发短信
     * @author xunyl
     * @date 2015-07-29
     */
    private static boolean isSendSmsForIdleGenr(String grpProductUserId,String mebSerailNumber)throws Exception{
        //1- 定义返回值
        boolean isSendSmsByModel = false;
        
        //2- 判断是否需要向个人发送短信，如果选择否，则直接返回false
        String isSendSms = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999094044");
        if(StringUtils.equals("0", isSendSms)){
            return isSendSmsByModel;
        }
        
        //3- 判断是否采用标准模板发短信，如果采用定制短信，则需要自己登记短息表，并且返回false
        String isSendStandardSms = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999094061");
        if(StringUtils.equals("0", isSendStandardSms)){
            String smsContent = GrpCommonBean.getAttrValueByAttrCode(grpProductUserId,"999094062");
            combinCommonDate(smsContent,mebSerailNumber);
            return isSendSmsByModel;
        }
        
        //4- 返回处理结果
        isSendSmsByModel = true;
        return isSendSmsByModel;
    }

    /** 
    * @Title: combinCommonDate
    * @Description: 拼装短信数据中公用部分数据
    * @param smsTemplateDate
    * @param serialNumber
    * @return
    * @throws Exception    
    * @return IData
    * @author chenkh
    * @time 2015年5月20日
    */ 
    public static void combinCommonDate(String smsTemplateDate, String serialNumber) throws Exception
    {
    	IData inparam = new DataMap();
    	inparam.put("RECV_OBJECT", serialNumber);
    	inparam.put("NOTICE_CONTENT", smsTemplateDate);
    	SmsSend.insSms(inparam);    	
    }
    
    /**
     * @description 根据属性编号获取模板中的属性值
     * @author xunyl
     * @date 2015-06-29
     */
    @SuppressWarnings("unchecked")
	private static String getAttrValueByAttrCode(String attrCode,GrpRegTradeData grpRegTradeData)throws Exception{
    	//1- 定义参数的初始化值
    	String attrValue = "";
    	
    	//2- 获取模板中的属性集
    	List tradeAttrInfoList =grpRegTradeData.get("TF_B_TRADE_ATTR");
    	if(tradeAttrInfoList==null || tradeAttrInfoList.size()==0){
    		return attrValue;
    	}
    	
    	//3- 遍历属性集，获取属性值
    	for(int i=0;i<tradeAttrInfoList.size();i++){
    		AttrTradeData tradeAttrInfo = (AttrTradeData)tradeAttrInfoList.get(i);
    		String currAttrCode = tradeAttrInfo.getAttrCode();
    		if(StringUtils.equals(attrCode, currAttrCode)){
    			attrValue = tradeAttrInfo.getAttrValue();
    			break;
    		}
    	}
    	
    	//4- 返回参数值
    	return attrValue;
    }

}
