
package com.asiainfo.veris.crm.order.soa.person.busi.terminalAfterSales;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.SmsVerifyCodeBean;
/**
 * 售后服务短信发送服务
 * @author zyz
 * @version 1.0
 *
 */
public class AfterSalesSendSmsSVC extends CSBizService
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1814212176503925400L;
	protected static Logger log = Logger.getLogger(AfterSalesSendSmsSVC.class);
	
	
	/**
	 * 
	 * 随机码5分钟内有效
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData  sendSmsVerifyCode(IData input) throws Exception{
		IData result=new DataMap();
		try {
			String serialNumber = input.getString("SERIAL_NUMBER").trim();
			
			if(!validPhoneNum("0", serialNumber)){
		        result.put("X_RESULTCODE", "2998");
		        result.put("X_RESULTINFO", "手机号码格式不正确");
		        return result;
			}
			
			//生成短信验证码
			String verifyCode = RandomStringUtils.randomNumeric(6);
			
			//短信内容 
			IData param=new DataMap();
			param.put("VERIFY_CODE", verifyCode);
			String msg = getSmsContent(this.getTemplateInfo("CRM_SMS_AFTER_SALES_9"),param);
			
			//发送短信通知
			IData inparam = new DataMap();
	        inparam.put("NOTICE_CONTENT", msg);
	        inparam.put("RECV_OBJECT", serialNumber);
	        inparam.put("RECV_ID", serialNumber);
	        inparam.put("REFER_STAFF_ID", getVisit().getStaffId());
	        inparam.put("REFER_DEPART_ID", getVisit().getDepartId());
	        inparam.put("REMARK", "终端售后_售后退机短信随机码发送");
	        SmsSend.insSms(inparam);
	        //保存短信验证码(5分钟)
	        SharedCache.set(SmsVerifyCodeBean.VERIFY_CODE_CACHE_KEY+serialNumber, verifyCode, 60*5);
	        result.put("X_RESULTCODE", "0");
	        result.put("X_RESULTINFO", "随机码发送,成功！");
	        result.put("validcode", verifyCode);
		} catch (Exception e) {
			//log.info("(e);
	        result.put("X_RESULTCODE", "2998");
	        result.put("X_RESULTINFO", e.getMessage());
		}
		return result;
	}
	

	/**
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData  sendSmsInfoByType(IData input) throws Exception{
		IData result=new DataMap();
		try {
			int type=input.getInt("TYPE");
			String serialNumber=input.getString("SERIAL_NUMBER").trim();
			
			if(!validPhoneNum("0", serialNumber)){
		        result.put("X_RESULTCODE", "2998");
		        result.put("X_RESULTINFO", "手机号码格式不正确");
		        return result;
			}
			
			
			IData smsData = new DataMap();
			
			smsData.put("RECV_OBJECT", serialNumber);
			smsData.put("RECV_ID", serialNumber);
			smsData.put("REFER_STAFF_ID", getVisit().getStaffId());
			smsData.put("REFER_DEPART_ID", getVisit().getDepartId());
	        
			
            String templateId = "";
           
            IData template =new DataMap();
            //模版替换参数
            IData param= new DataMap();
            String  content="";
			
			switch (type) {
				case 1:
					 //本地受理完毕{接机受理短信发送}
					templateId="CRM_SMS_AFTER_SALES_1";
					template =this.getTemplateInfo(templateId);
					//读取模版直接发送
					content = getSmsContent(template,param);
					smsData.put("NOTICE_CONTENT", content);
					smsData.put("REMARK", "接机受理短信发送");
					SmsSend.insSms(smsData);
			        result.put("X_RESULTCODE", "0");
			        result.put("X_RESULTINFO", "ok");
					break;
				case 2 :
					//本地维修完毕待取机｛售后维修完毕点击发货短信提示 ｝
					templateId="CRM_SMS_AFTER_SALES_2";
					template =this.getTemplateInfo(templateId);
					//受理网点名称
					String  accenptWd=input.getString("ACCEPT_WD");
					param.put("ACCEPT_WD", accenptWd);
					//寄送网点名称
					param.put("SEND_WD", input.getString("SEND_WD"));
					
					content = getSmsContent(template,param);
					smsData.put("NOTICE_CONTENT", content);
					smsData.put("REMARK", "售后维修完毕点击发货短信发送");
					SmsSend.insSms(smsData);
			        result.put("X_RESULTCODE", "0");
			        result.put("X_RESULTINFO", "ok");
				break;
				case 3 :
					//终端售后_售后取机短信满意度调查发送
					templateId="CRM_SMS_AFTER_SALES_3";
					template =this.getTemplateInfo(templateId);
					//读取模版直接发送
					content = getSmsContent(template,param);
					smsData.put("NOTICE_CONTENT", content);
					smsData.put("REMARK", "终端售后_售后取机短信满意度调查发送");
					SmsSend.insSms(smsData);
			        result.put("X_RESULTCODE", "0");
			        result.put("X_RESULTINFO", "ok");
					break;
				case 4 :
					//
					result=sendSmsVerifyCode(input);
					break;
				case 5 :
					//接修方（海口）收到待修机{售后配送收货待维修短信发送}
					templateId="CRM_SMS_AFTER_SALES_5";
					template =this.getTemplateInfo(templateId);
					//受理网点名称
					param.put("ACCEPT_WD", input.getString("ACCEPT_WD"));
					
					content = getSmsContent(template,param);
					smsData.put("NOTICE_CONTENT", content);
					smsData.put("REMARK", "接修方（海口）收到待修机短信发送");
					SmsSend.insSms(smsData);
			        result.put("X_RESULTCODE", "0");
			        result.put("X_RESULTINFO", "ok");
					break;
				case 6 :
					//接修方（海口）返回送修方{售后退机短信发送通知}
					templateId="CRM_SMS_AFTER_SALES_6";
					template =this.getTemplateInfo(templateId);
					//寄送网点名称
					param.put("SEND_WD", input.getString("SEND_WD"));
					
					content = getSmsContent(template,param);
					smsData.put("NOTICE_CONTENT", content);
					smsData.put("REMARK", "接修方（海口）返回送修方短信发送");
					SmsSend.insSms(smsData);
			        result.put("X_RESULTCODE", "0");
			        result.put("X_RESULTINFO", "ok");
					break;
				case 7 :
					//送修方收到，通知最终用户取机｛终端售后_售后取机短信发送｝
					templateId="CRM_SMS_AFTER_SALES_7";
					template =this.getTemplateInfo(templateId);
					//手机号码
					param.put("MOBILEPHONE_NUMBER", input.getString("SERIAL_NUMBER"));
					//品牌
					param.put("MOBILEPHONE_BRAND", input.getString("BRAND"));
					//型号
					param.put("MOBILEPHONE_IMEI", input.getString("IMEI"));
					//受理网点名称
					param.put("ACCEPT_WD", input.getString("ACCEPT_WD"));
					
					content = getSmsContent(template,param);
					
					smsData.put("NOTICE_CONTENT", content);
					smsData.put("REMARK", "送修方收到，通知最终用户取机短信发送");
					SmsSend.insSms(smsData);
			        result.put("X_RESULTCODE", "0");
			        result.put("X_RESULTINFO", "ok");
					break;
				case 8 :
					//售后退机星级次数提醒{售后退机星级次数提醒}
					templateId="CRM_SMS_AFTER_SALES_8";
					template =this.getTemplateInfo(templateId);
					
					String fee_cnt1=input.getString("FEE_CNT1");
					
					String fee_cnt2=input.getString("FEE_CNT2");
					
					String dev_cnt1=input.getString("DEV_CNT1");
					
					String dev_cnt2=input.getString("DEV_CNT2");
					
					if(!"".equals(fee_cnt1)&&fee_cnt1!=null
						&&!"".equals(fee_cnt2)&&fee_cnt2!=null
						&&!"".equals(dev_cnt1)&&dev_cnt1!=null
						&&!"".equals(dev_cnt2)&&dev_cnt2!=null){
						
						//人工费优惠已使用次数
						param.put("FEE_CNT1", fee_cnt1);
						//人工费优惠未使用次数
						param.put("FEE_CNT2", fee_cnt2);
						//配件优惠已使用次数
						param.put("DEV_CNT1", dev_cnt1);
						//配件优惠未使用次数
						param.put("DEV_CNT2", dev_cnt2);
						
						content = getSmsContent(template,param);
						smsData.put("NOTICE_CONTENT", content);
						smsData.put("REMARK", "售后退机星级次数提醒短信发送");
						SmsSend.insSms(smsData);
				        result.put("X_RESULTCODE", "0");
				        result.put("X_RESULTINFO", "ok");
					}else{
				        result.put("X_RESULTCODE", "2998");
				        result.put("X_RESULTINFO", "人工费优惠已使用次数或人工费优惠未使用次数或配件优惠已使用次数或配件优惠未使用次数  有空值存在");
					}
					break;
				default:
				   result.put("X_RESULTCODE", "2998");
			       result.put("X_RESULTINFO", "模版代码错误");
					break;
			}
		} catch (Exception e) {
			//log.info("(e);
	        result.put("X_RESULTCODE", "2998");
	        result.put("X_RESULTINFO", e.getMessage());
		}
		return result;
		
	}

	
	 public IData getTemplateInfo(String templateId) throws Exception
	    {
	        IData templateInfo = TemplateQry.qryTemplateContentByTempateId(templateId);
	        return templateInfo;
	    }
	 
	    public String getSmsContent(IData templateInfo, IData params) throws Exception
	    {
	        if (IDataUtil.isEmpty(templateInfo))
	        {
	            CSAppException.apperr(BizException.CRM_BIZ_3);
	        }
	        MVELExecutor exector = new MVELExecutor();
	        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());

	        StringBuilder sb = new StringBuilder();
	        sb.append(templateInfo.getString("TEMPLATE_CONTENT1", ""));
	        sb.append(templateInfo.getString("TEMPLATE_CONTENT2", ""));
	        sb.append(templateInfo.getString("TEMPLATE_CONTENT3", ""));
	        sb.append(templateInfo.getString("TEMPLATE_CONTENT4", ""));
	        sb.append(templateInfo.getString("TEMPLATE_CONTENT5", ""));

	        String templateContent = sb.toString();
	        if (IDataUtil.isNotEmpty(params))
	        {
	            exector.prepare(params);// 模板变量解析
	            templateContent = exector.applyTemplate(templateContent);
	        }
	        return templateContent;
	    }
		/**
		 * @param checkType 校验类型：0校验手机号码，1校验座机号码，2两者都校验满足其一就可
		 * @param phoneNum
		 * */
		public static boolean validPhoneNum(String checkType,String phoneNum){
			boolean flag=false;
			Pattern p1 = null;
			Pattern p2 = null;
			Matcher m = null;
			p1 = Pattern.compile("^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(17[0-9]{1}))+\\d{8})?$");
			p2 = Pattern.compile("^(0[0-9]{2,3}\\-)?([1-9][0-9]{6,7})$");
			if("0".equals(checkType)){
				if(phoneNum.length()!=11){
					return false;
				}else{
					m = p1.matcher(phoneNum);
					flag = m.matches();
				}
			}else if("1".equals(checkType)){
				if(phoneNum.length()<11||phoneNum.length()>=16){
					return false;
				}else{
					m = p2.matcher(phoneNum);
					flag = m.matches();
				}
			}else if("2".equals(checkType)){
				if(!((phoneNum.length() == 11 && p1.matcher(phoneNum).matches())||(phoneNum.length()<16&&p2.matcher(phoneNum).matches()))){
					return false;
				}else{
					flag = true;
				}
			}
			return flag;
		}
}
