package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.realname;
 
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;

public class RealNameJudgeSVC extends CSBizService {
	private static final Logger log = Logger.getLogger(RealNameJudgeSVC.class);
	
   	/**
   	 * 补录号码校验接口
   	 * @param input
   	 * @return
   	 * @throws Exception
   	 */
	public IData checkRealNameState(IData input) throws Exception{
		IData result = new DataMap();
		
		String serialNum = input.getString("SERIAL_NUMBER");
		if (StringUtils.isEmpty(serialNum)) {
			result.put("RESULTCODE", "2999");
			result.put("RETURN_MESSAGE", "手机号码不能为空！");
			result.put("IS_REG", "0");
			return result;
		}
		
		RealNameJudgeBean bean =  BeanManager.createBean(RealNameJudgeBean.class);
		return bean.checkRealNameState(input);
	}
	
	
	/**
	 * 自助卡校验接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData checkSimCardState(IData input) throws Exception{
		IData result = new DataMap();
	       
		String serialNum = input.getString("SERIAL_NUMBER");
		if (StringUtils.isEmpty(serialNum)) {
			result.put("RESULTCODE", "2999");
			result.put("RETURN_MESSAGE", "手机号码不能为空！");
			
			return result;
		}
				
		String sim_card_no = input.getString("SIM_CARD_NO");
		if (StringUtils.isEmpty(sim_card_no)) {
			result.put("RESULTCODE", "2999");
			result.put("RETURN_MESSAGE", "SIM卡号不能为空！");
		
			return result;
		}
		
		RealNameJudgeBean bean =  BeanManager.createBean(RealNameJudgeBean.class);
		return bean.checkSimCardState(input);
	}
	
	/**
	 * 密码校验接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData verifyUserNumber(IData input) throws Exception{
		
		IData result = new DataMap();

		if ("".equals(input.getString("SERIAL_NUMBER",""))) {// 没有输入手机号
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "手机号码为空");
			return result;
		}

		if ("".equals(input.getString("PASS_WORD",""))) {// 没有输入密码
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "服务密码为空");
			return result;
		}

		if (input.getString("SERIAL_NUMBER").length() != 11)// 手机号长度不正确
		{
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "手机号码长度不正确");
			return result;
		}

		if (input.getString("PASS_WORD").length() != 6)// 密码长度不正确
		{
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "服务密码长度不正确");
			return result;
		}
		RealNameJudgeBean bean =  BeanManager.createBean(RealNameJudgeBean.class);
		return bean.verifyUserNumber(input);
	}
	
	/**
	 * 补登记接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData realNameRegiste(IData input) throws Exception{
		
		IData result = new DataMap();			    
				        				
	    if (StringUtils.isEmpty(input.getString("BUSI_TYPE"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "业务类型不能为空！");
			return result;
		}
	    if (StringUtils.isEmpty(input.getString("TRANSACTION_ID"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "操作流水号不能为空！");
			return result;
		}
	    if (StringUtils.isEmpty(input.getString("SERIAL_NUMBER"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "手机号码不能为空！");
			return result;
		}
	    if (StringUtils.isEmpty(input.getString("CHANNEL_ID"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "渠道不能为空！");
			return result;
		}
	    if (StringUtils.isEmpty(input.getString("CUST_NAME"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "客户姓名不能为空！");
			return result;
		}
	    if (StringUtils.isEmpty(input.getString("CUST_CERT_NO"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "证件号码不能为空！");
			return result;
		}
	    if (StringUtils.isEmpty(input.getString("CUST_CERT_ADDR"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "证件地址不能为空！");
			return result;
		}
	    if (StringUtils.isEmpty(input.getString("GENDER"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "性别不能为空！");
			return result;
		}
	    if (StringUtils.isEmpty(input.getString("NATION"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "民族不能为空！");
			return result;
		}
	    if (StringUtils.isEmpty(input.getString("BIRTHDAY"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "生日不能为空！");
			return result;
		}
	    if (StringUtils.isEmpty(input.getString("ISSUING_AUTHORITY"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "签证机关不能为空！");
			return result;
		}
		
	    if (StringUtils.isEmpty(input.getString("CERT_VALIDDATE"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "生效日期不能为空！");
			return result;
		}
	    if (StringUtils.isEmpty(input.getString("CERT_EXPDATE"))) {
			result.put("RETURN_CODE", "2999");
			result.put("RETURN_MESSAGE", "失效日期不能为空！");
			return result;
		}	
	    
	  //检查身份证出生日期是否在12(含)-120岁(含)之间
        try {            
            String psptId = input.getString("CUST_CERT_NO", "").trim();
            if (psptId.length() == 15 || psptId.length() == 18) {
                if (psptId.length() == 15 ) {
                    psptId = IdcardUtils.conver15CardTo18(psptId);
                    if (psptId == null || psptId.equals("")) {
                        result.put("RETURN_CODE", "2999");
                        result.put("RETURN_MESSAGE", "身份证不合法！");
                        return result;
                    }
                }
                int age = IdcardUtils.getAgeByIdCard(psptId);
                if (age < 12) {
                    result.put("RETURN_CODE", "2999");
                    result.put("RETURN_MESSAGE", "身份证年龄小于12岁，不能办理该业务！");
                    return result;  
                } else if (age > 120) {
                    result.put("RETURN_CODE", "2999");
                    result.put("RETURN_MESSAGE", "身份证年龄大于120岁，不能办理该业务！");
                    return result;
                }
            } else {
                result.put("RETURN_CODE", "2999");
                result.put("RETURN_MESSAGE", "身份证不合法！");
                return result;
            }
        } catch (Exception e) {
            log.equals(e);
            result.put("RETURN_CODE", "1001");
            result.put("RETURN_MESSAGE", "办理失败！");
            return result;
        }
	    
	    /**
		 * REQ201609280015 关于开户用户名优化的需求
		 * 姓名去空格 前后中间 chenxy3
		 * */
		String custName=input.getString("CUST_NAME"); 
		custName=custName.replaceAll(" +", "");
		//log.info("("***********cxy***realNameRegiste*******custName=|"+custName+"|");
		input.put("CUST_NAME", custName);
	    
	    input.put("PSPT_TYPE_CODE_NEW", "0");
	    input.put("PSPT_ID_NEW", input.getString("CUST_CERT_NO"));
	    input.put("PSPT_ENDDATE_NEW", input.getString("CERT_EXPDATE"));
	    input.put("PSPT_ADDR_NEW", input.getString("CUST_CERT_ADDR"));
	    input.put("CUST_NAME_NEW", input.getString("CUST_NAME"));
        
		RealNameJudgeBean bean =  BeanManager.createBean(RealNameJudgeBean.class);
		return bean.realNameRegiste(input);
	}
}
