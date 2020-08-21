package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.IdentcardInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.IdentCodeAuthSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.FamilyCreateBean;

/**
 * 家庭网操作接口
 * @author chenwy
 * @date 2017-07-18
 * @time 14:17
 */

public class FamilyOperPreSVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private void checkParam(IData input,IData result) throws Exception{
		for(String key:input.keySet()){
			String value = input.getString(key);
			if(StringUtils.isBlank(value)){
				result.put("X_RESULTCODE", "9999");
				result.put("X_RESULTINFO", "参数"+key+"不能为空");
				result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
				return;
			}
		}
		result.put("X_RESULTCODE", "0000");
		result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	}
	/**
	 * 家庭网的各个操作，根据opr_code来选择不同的服务
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData Operate(IData input) throws Exception{
		
		IData returnData = new DataMap();
		IData inputData = new DataMap();
		
		inputData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		inputData.put("IDENT_CODE", input.getString("IDENT_CODE"));
		inputData.put("BIZ_TYPE", input.getString("BIZ_TYPE"));
		inputData.put("OPR_CODE", input.getString("OPR_CODE"));
		checkParam(inputData, returnData);
		
		if(!"0000".equals(returnData.get("X_RESULTCODE"))){
			return returnData;
		}
		
		if(!("NOCHECK".equals(input.getString("IDENT_CODE")))){
			IData res = IdentcardInfoQry.checkIdentInfoByIdent(input.getString("IDENT_CODE", ""), input.getString("BUSINESS_CODE", ""), input.getString("SERIAL_NUMBER", ""), input.getString("IDENT_CODE_TYPE", ""), input.getString("IDENT_CODE_LEVEL", ""));
	
			if (IDataUtil.isEmpty(res))
	        {
				returnData.put("X_RESULTCODE", "3018");
				returnData.put("X_RESULTINFO", "身份凭证失败");
				return returnData;
	        }
		}
		
		String op = input.getString("OPR_CODE");
		IData exData = new DataMap();
		if("01".equals(op)){
			//组网、开户操作
			exData.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
			exData.put("SHORT_CODE_B", input.getString("MEMBER_SHORT_NO_NEW"));
			//海南省组建家庭网必须传入副号码
			exData.put("SERIAL_NUMBER_B", input.getString("MEMBER_PHONE_NO"));		
			
			if(StringUtils.isBlank(exData.getString("SERIAL_NUMBER_B"))){
				returnData.put("X_RESULTCODE", "5010");
				returnData.put("X_RESULTINFO", "省BOSS未开通单卡组建家庭网业务，不支持业务受理");
				return returnData;
			}
			
			checkParam(exData, returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			
			
			//若不写则获取默认的短号
			exData.put("SHORT_CODE", input.getString("ORDERNO","520"));
			exData.put("SUBMIT_TYPE", "submit");
			exData.put("CANCEL_TAG", "0");
			exData.put("SUBSCRIBE_TYPE", "0");
			inputData.putAll(exData);
			
			String SERIAL_NUMBER = inputData.getString("SERIAL_NUMBER");
			//查询号码是否有未完工的台账
			IDataset unfinishTrade = TradeInfoQry.getTradeInfoBySn(SERIAL_NUMBER);
			if(IDataUtil.isNotEmpty(unfinishTrade)){
				returnData.put("X_RESULTCODE", "9999");
				returnData.put("X_RESULTINFO", "该用户有未完工的台账");
				return returnData;
			}
			//查询副卡是否有未完工的台账
			String SERIAL_NUMBER_B = inputData.getString("SERIAL_NUMBER_B");
            IDataset unfinishVice = TradeInfoQry.getTradeInfoBySn(SERIAL_NUMBER_B);
            if(IDataUtil.isNotEmpty(unfinishVice)){
                returnData.put("X_RESULTCODE", "9996");
                returnData.put("X_RESULTINFO", "该副卡有未完工的台账");
                return returnData;
            }
			//通过手机号码搜索用户信息
			IData user = UcaInfoQry.qryUserInfoBySn(SERIAL_NUMBER);
			String USER_ID = user.getString("USER_ID");
			//先查看该号码是否开通了家庭网
			IDataset familyNet = checkFamily(USER_ID);
			if(IDataUtil.isNotEmpty(familyNet)){
				returnData.put("X_RESULTCODE", "9998");
				returnData.put("X_RESULTINFO", "该用户已经开通家庭网");
				return returnData;
			}
			if(!"0".equals(user.getString("USER_STATE_CODESET"))){
				returnData.put("X_RESULTCODE", "2005");
				returnData.put("X_RESULTINFO", "该用户已经停机");
				return returnData;
			}
			
			//通过副卡手机号搜副卡信息
            IData vice = UcaInfoQry.qryUserInfoBySn(SERIAL_NUMBER_B);
            String VICE_ID = vice.getString("USER_ID");
            //检查副卡是否开通了家庭网
            IDataset viceNet = checkFamily(VICE_ID);
            if(IDataUtil.isNotEmpty(viceNet)){
                returnData.put("X_RESULTCODE", "2000");
                returnData.put("X_RESULTINFO", "该副卡已开通家庭网");
                return returnData;
            }
            if(!"0".equals(vice.getString("USER_STATE_CODESET"))){
				returnData.put("X_RESULTCODE", "2005");
				returnData.put("X_RESULTINFO", "该副卡用户已经停机");
				return returnData;
			}
            
			//黑名单查询
			checkBlack(SERIAL_NUMBER,returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			
			
			//查询主卡优惠
			String product_id = inputData.getString("PRODUCT_ID");
			boolean flag = true;
			IDataset discntInfos = CommparaInfoQry.getCommNetInfo("CSM", "5", "VPCN");
			IData discnt_real = new DataMap();
			for(Object discntInfo : discntInfos){
				IData realInfo = (IData) discntInfo;
				if(product_id.equals(realInfo.getString("PARA_CODE1"))){
					flag = false;
					discnt_real = realInfo;
					break;
				}
			}
			if(flag){
				returnData.put("X_RESULTCODE", "9997");
				returnData.put("X_RESULTINFO", "家庭网产品有误");
				return returnData;
			}
			//主卡优惠
			String discnt_code = discnt_real.getString("PARA_CODE2");
			String discnt_code_b = discnt_real.getString("PARA_CODE3");
			//检验短号的合法性
            String shortCode = inputData.getString("SHORT_CODE_B");
            IDataset dataset = new DatasetList();
            dataset = CommparaInfoQry.getCommpara("CSM", "112", "QQWLIMIT","ZZZZ");  //根据套餐代码查询本省套餐
            if(IDataUtil.isEmpty(dataset)){
                Pattern p = Pattern.compile("52\\d|53\\d");
                Matcher m = p.matcher(shortCode);
                boolean b = m.matches();
                if (!b)
                {
                    // 短号非法,短号必须为【520-539】
                    CSAppException.apperr(FamilyException.CRM_FAMILY_716,shortCode);
                }   
            }else{
                Pattern p = Pattern.compile("52\\d");
                Matcher m = p.matcher(shortCode);
                boolean b = m.matches();
                if (!b)
                {
                    // 短号非法,短号必须为【520-529】
                    CSAppException.apperr(FamilyException.CRM_FAMILY_833,shortCode);
                }   
            }
			
			
			//其他参数准备
			String end_date = "2050-12-31 23:59:59";
			String startDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			String start_date = startDate.substring(0, 4)+"-"+startDate.substring(4, 6)+"-"+startDate.substring(6, 8)+" "+startDate.substring(8, 10)+":"+startDate.substring(10, 12)+":"+startDate.substring(12);
			
			IDataset vices = new DatasetList();
            IData viceInfo = new DataMap();
            viceInfo.put("SERIAL_NUMBER_B", SERIAL_NUMBER_B);
            viceInfo.put("DISCNT_CODE_B", discnt_code_b);
            viceInfo.put("DISCNT_NAME_B", DiscntInfoQry.getDiscntInfoByDisCode(discnt_code_b).getData(0).getString("DISCNT_NAME"));
            viceInfo.put("SHORT_CODE_B", inputData.getString("SHORT_CODE_B"));
            viceInfo.put("START_DATE", "立即");
            viceInfo.put("END_DATE", end_date);
            viceInfo.put("MEMBER_ROLE_B", "");
            viceInfo.put("MEMBER_KIND_B", "");
            viceInfo.put("tag", "0");
            vices.add(viceInfo);
			
			//参数拼接
			inputData.put("FMY_DISCNT_CODE", discnt_code);
			inputData.put("DISCNT_CODE", discnt_code);
			inputData.put("CHECK_MODE", "0");
			inputData.put("FMY_VERIFY_MODE", "2");
			inputData.put("FMY_PRODUCT_ID", product_id);
			inputData.put("START_DATE", start_date);
			inputData.put("END_DATE", end_date);
			inputData.put("FMY_START_DATE", start_date);
			inputData.put("FMY_END_DATE", end_date);
			inputData.put("VALID_MEMBER_NUMBER", "0");
			inputData.put("VERIFY_MODE", "2");
			inputData.put("ADD_MEMBER_NUMBER", "1");
			inputData.put("MEB_LIST", vices);
			//走台账开户
			IDataset result = CSAppCall.call("SS.FamilyCreateRegSVC.tradeReg", inputData);
			if(IDataUtil.isEmpty(result)){
				returnData.put("X_RESULTCODE", "9996");
				returnData.put("X_RESULTINFO", "操作失误请稍后再试");
				return returnData;
			}
			
			return returnData;
			
		}else if("02".equals(op)){
			
			//退网操作
			exData.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
			checkParam(exData, returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			
			inputData.putAll(exData);
			
			//获取主卡的用户信息
			String serial_number = inputData.getString("SERIAL_NUMBER");
			IData mainCard = UcaInfoQry.qryUserInfoBySn(serial_number);
			String user_id = mainCard.getString("USER_ID");
			//判断是否开通家庭网
			IDataset mainFamily = checkFamily(user_id);
			if(IDataUtil.isEmpty(mainFamily)){
				returnData.put("X_RESULTCODE", "2001");
				returnData.put("X_RESULTINFO", "订购关系不存在（或已销户）");
				return returnData;
			}
			//检验黑名单
			checkBlack(serial_number, returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			
			
			//检验是否有台账
			IDataset unfinishTrade = TradeInfoQry.getTradeInfoBySn(serial_number);
			if(IDataUtil.isNotEmpty(unfinishTrade)){
				returnData.put("X_RESULTCODE", "9999");
				returnData.put("X_RESULTINFO", "该用户有未完工的台账");
				return returnData;
			}
			//参数准备
			if("1".equals(mainFamily.getData(0).getString("ROLE_CODE_B"))){
				inputData.put("IN_TAG", "0");
				inputData.put("CHECK_MODE", "0");
				inputData.put("SUBMIT_SOURCE", "CRM_PAGE");
				
				IDataset result = CSAppCall.call("SS.DestroyFamilyRegSVC.tradeReg", inputData);
			}else if("2".equals(mainFamily.getData(0).getString("ROLE_CODE_B"))){
				returnData.put("X_RESULTCODE", "5010");
				returnData.put("X_RESULTINFO", "不支持副卡退网操作");
				return returnData;
			}
			
			
			return returnData;
			
		}else if("03".equals(op)){
			//添加副卡
			exData.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
			exData.put("SERIAL_NUMBER_B", input.getString("MEMBER_PHONE_NO"));
			exData.put("SHORT_CODE_B",input.getString("MEMBER_SHORT_NO_NEW"));
			checkParam(exData, returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			inputData.putAll(exData);
			
			String SERIAL_NUMBER = inputData.getString("SERIAL_NUMBER");
			String SERIAL_NUMBER_B = inputData.getString("SERIAL_NUMBER_B");
			//查询号码是否有未完工的台账
			IDataset unfinishTrade = TradeInfoQry.getTradeInfoBySn(SERIAL_NUMBER);
			if(IDataUtil.isNotEmpty(unfinishTrade)){
				returnData.put("X_RESULTCODE", "9999");
				returnData.put("X_RESULTINFO", "该用户有未完工的台账");
				return returnData;
			}
			//查询副卡是否有未完工的台账
			IDataset unfinishVice = TradeInfoQry.getTradeInfoBySn(SERIAL_NUMBER_B);
			if(IDataUtil.isNotEmpty(unfinishVice)){
				returnData.put("X_RESULTCODE", "9996");
				returnData.put("X_RESULTINFO", "该副卡有未完工的台账");
				return returnData;
			}
			//通过手机号码搜索用户信息
			IData user = UcaInfoQry.qryUserInfoBySn(SERIAL_NUMBER);
			String USER_ID = user.getString("USER_ID");
			//先查看该号码是否开通了家庭网
			IDataset familyNet = checkFamily(USER_ID);
			if(IDataUtil.isEmpty(familyNet)){
				returnData.put("X_RESULTCODE", "2001");
				returnData.put("X_RESULTINFO", "订购关系不存在（或已销户）");
				return returnData;
			}
			if(!"0".equals(user.getString("USER_STATE_CODESET"))){
				returnData.put("X_RESULTCODE", "2005");
				returnData.put("X_RESULTINFO", "该用户已经停机");
				return returnData;
			}
			
			
			String user_id_a = familyNet.getData(0).getString("USER_ID_A");
			//通过副卡手机号搜副卡信息
			IData vice = UcaInfoQry.qryUserInfoBySn(SERIAL_NUMBER_B);
			String VICE_ID = vice.getString("USER_ID");
			//检查副卡是否开通了家庭网
			IDataset viceNet = checkFamily(VICE_ID);
			if(IDataUtil.isNotEmpty(viceNet)){
				
				if(!user_id_a.equals(viceNet.getData(0).getString("USER_ID_A"))){
					returnData.put("X_RESULTCODE", "3004");
					returnData.put("X_RESULTINFO", "存在互斥关系，不允许用户订购");
					return returnData;
				}
				returnData.put("X_RESULTCODE", "2000");
				returnData.put("X_RESULTINFO", "该副卡已开通家庭网");
				return returnData;
			}
			
			if(!"0".equals(vice.getString("USER_STATE_CODESET"))){
				returnData.put("X_RESULTCODE", "2005");
				returnData.put("X_RESULTINFO", "该用户已经停机");
				return returnData;
			}
			
			//黑名单查询
			checkBlack(SERIAL_NUMBER,returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			//查询主卡优惠
			String product_id = inputData.getString("PRODUCT_ID");
			boolean flag = true;
			IDataset discntInfos = CommparaInfoQry.getCommNetInfo("CSM", "5", "VPCN");
			IData discnt_real = new DataMap();
			for(Object discntInfo : discntInfos){
				IData realInfo = (IData) discntInfo;
				if(product_id.equals(realInfo.getString("PARA_CODE1"))){
					flag = false;
					discnt_real = realInfo;
					product_id = realInfo.getString("PARA_CODE1");
					break;
				}
			}
			if(flag){
				returnData.put("X_RESULTCODE", "9997");
				returnData.put("X_RESULTINFO", "家庭网产品有误");
				return returnData;
			}
			//主卡优惠
			String discnt_code = discnt_real.getString("PARA_CODE2");
			String discnt_code_b = discnt_real.getString("PARA_CODE3");
			//检验短号的合法性
			String shortCode = inputData.getString("SHORT_CODE_B");
			IDataset dataset = new DatasetList();
	        dataset = CommparaInfoQry.getCommpara("CSM", "112", "QQWLIMIT","ZZZZ");  //根据套餐代码查询本省套餐
			if(IDataUtil.isEmpty(dataset)){
		        Pattern p = Pattern.compile("52\\d|53\\d");
		        Matcher m = p.matcher(shortCode);
		        boolean b = m.matches();
		        if (!b)
		        {
		            // 短号非法,短号必须为【520-539】
		            CSAppException.apperr(FamilyException.CRM_FAMILY_716,shortCode);
		        }	
			}else{
		        Pattern p = Pattern.compile("52\\d");
		        Matcher m = p.matcher(shortCode);
		        boolean b = m.matches();
		        if (!b)
		        {
		            // 短号非法,短号必须为【520-529】
		            CSAppException.apperr(FamilyException.CRM_FAMILY_833,shortCode);
		        }	
			}
			//副卡数量
			IDataset members = countMembers(user_id_a);
			int size = members.size();
			//准备数据
			String start_date = familyNet.getData(0).getString("START_DATE");
			String end_date = familyNet.getData(0).getString("END_DATE");
			
			inputData.put("VALID_MEMBER_NUMBER", size);
			inputData.put("FMY_DISCNT_CODE", discnt_code);
			inputData.put("DISCNT_CODE", discnt_code);
			inputData.put("SERIAL_NUMBER", SERIAL_NUMBER);
			inputData.put("FMY_VERIFY_MODE", "2");
			inputData.put("VERIFY_MODE", "2");
			inputData.put("START_DATE", start_date);
			inputData.put("END_DATE", end_date);
			inputData.put("FMY_START_DATE", start_date);
			inputData.put("FMY_END_DATE", end_date);
			inputData.put("PRODUCT_ID", product_id);
			inputData.put("FMY_PRODUCT_ID", product_id);
			inputData.put("CHECK_MODE", input.getString("CHECK_MODE","0"));
			inputData.put("ADD_MEMBER_NUMBER", input.getString("ADD_MEMBER_NUMBER","1"));
			
			IDataset vices = new DatasetList();
			IData viceInfo = new DataMap();
			viceInfo.put("SERIAL_NUMBER_B", SERIAL_NUMBER_B);
			viceInfo.put("DISCNT_CODE_B", discnt_code_b);
			viceInfo.put("DISCNT_NAME_B", DiscntInfoQry.getDiscntInfoByDisCode(discnt_code_b).getData(0).getString("DISCNT_NAME"));
			viceInfo.put("SHORT_CODE_B", inputData.getString("SHORT_CODE_B"));
			viceInfo.put("START_DATE", "立即");
			viceInfo.put("END_DATE", end_date);
//			viceInfo.put("MEMBER_ROLE_B", "10");
//			viceInfo.put("MEMBER_KIND_B", "0");
			viceInfo.put("tag", "0");
			vices.add(viceInfo);
			
			inputData.put("MEB_LIST", vices);
			//数据准备完就可以走台账了
			IDataset result = CSAppCall.call("SS.FamilyCreateRegSVC.tradeReg", inputData);
			if(IDataUtil.isEmpty(result)){
				returnData.put("X_RESULTCODE", "9996");
				returnData.put("X_RESULTINFO", "办理业务有误");
			}
			
			return returnData;
			
		}else if("04".equals(op)){
			//删除副卡
			exData.put("SERIAL_NUMBER_B", input.getString("MEMBER_PHONE_NO"));
			checkParam(exData, returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			inputData.putAll(exData);
			//获取主卡的三户资料
			String serial_number = inputData.getString("SERIAL_NUMBER");
			IData mainCard = UcaInfoQry.qryUserInfoBySn(serial_number);
			String user_id = mainCard.getString("USER_ID");
			//获取副卡的三户资料
			String serial_number_b = inputData.getString("SERIAL_NUMBER_B");
			IData viceCard = UcaInfoQry.qryUserInfoBySn(serial_number_b);
			String user_id_b = viceCard.getString("USER_ID");
			//判断主副卡是否是黑名单用户
			checkBlack(serial_number, returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			checkBlack(serial_number_b, returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			//查询号码是否有未完工的台账
			IDataset unfinishTrade = TradeInfoQry.getTradeInfoBySn(serial_number);
			if(IDataUtil.isNotEmpty(unfinishTrade)){
				returnData.put("X_RESULTCODE", "9999");
				returnData.put("X_RESULTINFO", "该用户有未完工的台账");
				return returnData;
			}
			//查询副卡是否有未完工的台账
			IDataset unfinishVice = TradeInfoQry.getTradeInfoBySn(serial_number_b);
			if(IDataUtil.isNotEmpty(unfinishVice)){
				returnData.put("X_RESULTCODE", "9996");
				returnData.put("X_RESULTINFO", "该副卡有未完工的台账");
				return returnData;
			}
			//主副卡家庭网校验
			IDataset mainFamilys = checkFamily(user_id);
			IDataset viceFamilys = checkFamily(user_id_b);
			if(IDataUtil.isEmpty(mainFamilys)){
				returnData.put("X_RESULTCODE", "2001");
				returnData.put("X_RESULTINFO", "订购关系不存在（或已销户）");
				return returnData;
			}
			if(IDataUtil.isEmpty(viceFamilys)){
				returnData.put("X_RESULTCODE", "2001");
				returnData.put("X_RESULTINFO", "订购关系不存在（或已销户）");
				return returnData;
			}
			IData mainFamily = mainFamilys.getData(0);
			IData viceFamily = viceFamilys.getData(0);
			if(!mainFamily.getString("USER_ID_A").equals(viceFamily.getString("USER_ID_A"))){
				returnData.put("X_RESULTCODE", "9993");
				returnData.put("X_RESULTINFO", "这两张卡不在同一个家庭网中");
				return returnData;
			}
			//获得副卡短号
			String short_code_b = viceFamily.getString("SHORT_CODE");
			String start_date = viceFamily.getString("START_DATE");
			String endDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
			String end_date = endDate.substring(0, 4)+"-"+endDate.substring(4, 6)+"-"+endDate.substring(6, 8)+" "+endDate.substring(8, 10)+":"+endDate.substring(10, 12)+":"+endDate.substring(12);
			String last_date = SysDateMgr.getLastDateThisMonth();
			//获取副卡优惠
			IDataset discnt_vice = CommparaInfoQry.getCommNetInfo("CSM", "5", "VPCN");
			String discnt_b = discnt_vice.getData(0).getString("PARA_CODE3");
			String product_id = discnt_vice.getData(0).getString("PARA_CODE1");
			
			//根据优惠id搜产品信息
			String offer_name = DiscntInfoQry.getDiscntInfoByDisCode(discnt_b).getData(0).getString("DISCNT_NAME");
			//获取inst_id*
			IData inputInfo = new DataMap();
			inputInfo.put("SERIAL_NUMBER", serial_number);
			FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
			IData rsd = bean.getViceMebList(inputInfo);
			IDataset members = rsd.getDataset("MEB_LIST");
			String inst_id = null;
			for(Object member : members){
				IData mem = (IData)member;
				if(serial_number_b.equals(mem.getString("SERIAL_NUMBER_B"))){
					inst_id = mem.getString("U_INST_ID");
					break;
				}
			}
			if(StringUtils.isBlank(inst_id)){
				returnData.put("X_RESULTCODE", "9992");
				returnData.put("X_RESULTINFO", "信息有误");
				return returnData;
			}
			//获取家庭网中多少号码
			String user_id_a = mainFamily.getString("USER_ID_A");
			int num = countMembers(user_id_a).size();
			//准备参数
			inputData.put("CHECK_MODE", "0");
			inputData.put("DELETE_MEMBER_NUMBER", "1");
			inputData.put("SUBMIT_SOURCE", "CRM_PAGE");
			inputData.put("VALID_MEMBER_NUMBER", num+"");
			IDataset memberInfo = new DatasetList();
			IData memInfo = new DataMap();
			memInfo.put("INST_ID_B", inst_id);
			memInfo.put("SERIAL_NUMBER_B", serial_number_b);
			memInfo.put("DISCNT_CODE_B", discnt_b);
			memInfo.put("DISCNT_NAME_B", offer_name);
			memInfo.put("SHORT_CODE_B", short_code_b);
			memInfo.put("START_DATE", start_date);
			memInfo.put("END_DATE", end_date);
			memInfo.put("LAST_DAY_THIS_ACCT", last_date);
			memInfo.put("EFFECT_NOW", "YES");
			memInfo.put("tag", "2");
			memberInfo.add(memInfo);
			inputData.put("MEB_LIST", memberInfo);
			inputData.put("PRODUCT_ID", product_id);
			
			//台账系统
			IDataset result = CSAppCall.call("SS.DelFamilyNetMemberRegSVC.tradeReg", inputData);
			if(IDataUtil.isEmpty(result)){
				returnData.put("X_RESULTCODE", "9996");
				returnData.put("X_RESULTINFO", "办理业务有误");
			}
			
			return returnData;
			
		}else if("05".equals(op)){
			//变更短号
			exData.put("SERIAL_NUMBER_B", input.getString("MEMBER_PHONE_NO"));
			exData.put("SHORT_CODE_OLD", input.getString("MEMBER_SHORT_NO_OLD"));
			exData.put("SHORT_CODE", input.getString("MEMBER_SHORT_NO_NEW"));
			checkParam(exData, returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			inputData.putAll(exData);
			//获取主卡的用户信息
			String serial_number = inputData.getString("SERIAL_NUMBER");
			IData mainCard = UcaInfoQry.qryUserInfoBySn(serial_number);
			String user_id = mainCard.getString("USER_ID");
			//获取副卡的用户信息
			String serial_number_b = inputData.getString("SERIAL_NUMBER_B");
			IData viceCard = UcaInfoQry.qryUserInfoBySn(serial_number_b);
			String user_id_b = viceCard.getString("USER_ID");
			//查询号码是否有未完工的台账
			IDataset unfinishTrade = TradeInfoQry.getTradeInfoBySn(serial_number);
			if(IDataUtil.isNotEmpty(unfinishTrade)){
				returnData.put("X_RESULTCODE", "9999");
				returnData.put("X_RESULTINFO", "该用户有未完工的台账");
				return returnData;
			}
			//查询副卡是否有未完工的台账
			IDataset unfinishVice = TradeInfoQry.getTradeInfoBySn(serial_number_b);
			if(IDataUtil.isNotEmpty(unfinishVice)){
				returnData.put("X_RESULTCODE", "9996");
				returnData.put("X_RESULTINFO", "该副卡有未完工的台账");
				return returnData;
			}
			
			//检验主副卡的家庭网状况
			IData mainFamily = checkFamily(user_id).getData(0);
			IData viceFamily = checkFamily(user_id_b).getData(0);
			if(IDataUtil.isEmpty(mainFamily)){
				returnData.put("X_RESULTCODE", "2001");
				returnData.put("X_RESULTINFO", "订购关系不存在（或已销户）");
				return returnData;
			}
			if(IDataUtil.isEmpty(viceFamily)){
				returnData.put("X_RESULTCODE", "2001");
				returnData.put("X_RESULTINFO", "订购关系不存在（或已销户）");
				return returnData;
			}
			if(!mainFamily.getString("USER_ID_A").equals(viceFamily.getString("USER_ID_A"))){
				returnData.put("X_RESULTCODE", "3004");
				returnData.put("X_RESULTINFO", "这两张卡不在同一个家庭网中");
				return returnData;
			}
			
			if(serial_number.equals(serial_number_b)){
				returnData.put("X_RESULTCODE", "5010");
				returnData.put("X_RESULTINFO", "主卡不能变更短号");
				return returnData;
			}
			
			//获取inst_id*
			IData inputInfo = new DataMap();
			inputInfo.put("SERIAL_NUMBER", serial_number);
			FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
			IData rsd = bean.getViceMebList(inputInfo);
			IDataset members = rsd.getDataset("MEB_LIST");
			String inst_id = null;
			for(Object member : members){
				IData mem = (IData)member;
				if(serial_number_b.equals(mem.getString("SERIAL_NUMBER_B"))){
					inst_id = mem.getString("U_INST_ID");
					break;
				}
			}
			if(StringUtils.isBlank(inst_id)){
				returnData.put("X_RESULTCODE", "9992");
				returnData.put("X_RESULTINFO", "信息有误");
				return returnData;
			}
			//副卡信息准备
			String start_date = viceFamily.getString("START_DATE");
			String end_date = viceFamily.getString("END_DATE");
			//获取家庭网一共多少号码
			String user_id_a = mainFamily.getString("USER_ID_A");
			int num = countMembers(user_id_a).size();
			//校验新短号是否合法
			String shortCode = inputData.getString("SHORT_CODE");
			IDataset dataset = new DatasetList();
	        dataset = CommparaInfoQry.getCommpara("CSM", "112", "QQWLIMIT","ZZZZ");  //根据套餐代码查询本省套餐
			if(IDataUtil.isEmpty(dataset)){
		        Pattern p = Pattern.compile("52\\d|53\\d");
		        Matcher m = p.matcher(shortCode);
		        boolean b = m.matches();
		        if (!b)
		        {
		            // 短号非法,短号必须为【520-539】
		            CSAppException.apperr(FamilyException.CRM_FAMILY_716,shortCode);
		        }	
			}else{
		        Pattern p = Pattern.compile("52\\d");
		        Matcher m = p.matcher(shortCode);
		        boolean b = m.matches();
		        if (!b)
		        {
		            // 短号非法,短号必须为【520-529】
		            CSAppException.apperr(FamilyException.CRM_FAMILY_833,shortCode);
		        }	
			}
			//判断是否为黑名单
			checkBlack(serial_number, returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			checkBlack(serial_number_b, returnData);
			if(!"0000".equals(returnData.get("X_RESULTCODE"))){
				return returnData;
			}
			//准备参数
			inputData.put("CHECK_MODE", "0");
			inputData.put("SUBMIT_SOURCE", "CRM_PAGE");
			inputData.put("VALID_MEMBER_NUMBER", num+"");
			IDataset vicelist = new DatasetList();
			IData memInfo = new DataMap();
			memInfo.put("INST_ID_B", inst_id);
			memInfo.put("SERIAL_NUMBER_B", serial_number_b);
			memInfo.put("SHORT_CODE_B", shortCode);
			memInfo.put("START_DATE", start_date);
			memInfo.put("END_DATE", end_date);
			memInfo.put("tag", "2");
			vicelist.add(memInfo);
			inputData.put("MEB_LIST", vicelist);
			
			IDataset result = CSAppCall.call("SS.FamilyShortCodeBusiRegSVC.tradeReg", inputData);
			if(IDataUtil.isEmpty(result)){
				returnData.put("X_RESULTCODE", "9996");
				returnData.put("X_RESULTINFO", "办理业务有误");
			}
			
			return returnData;
			
		}else if("06".equals(op)){
			returnData.put("X_RESULTCODE", "5010");
			returnData.put("X_RESULTINFO", "暂不支持该功能");
		}else if("07".equals(op)){
			//变更家庭网资费
			returnData.put("X_RESULTCODE", "5010");
			returnData.put("X_RESULTINFO", "暂不支持该功能");
		}else{
			returnData.put("X_RESULTCODE", "8888");
			returnData.put("X_RESULTINFO", "操作码有误!");
		}
		
		
		return returnData;
	}
	/**
	 * 家庭网查询
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public IData qryFamilyNet(IData input) throws Exception{
		IData returnData = new DataMap();
		returnData = checkParam(input);
		if(!"0000".equals(returnData.getString("X_RESULTCODE"))){
			return returnData;
		}
		IData res = IdentcardInfoQry.checkIdentInfoByIdent(input.getString("IDENT_CODE", ""), input.getString("BUSINESS_CODE", ""), input.getString("SERIAL_NUMBER", ""), input.getString("IDENT_CODE_TYPE", ""), input.getString("IDENT_CODE_LEVEL", ""));

		if (IDataUtil.isEmpty(res))
        {
			returnData.put("X_RESULTCODE", "3018");
			returnData.put("X_RESULTINFO", "身份凭证失败");
			return returnData;
        }

		//查询该号码是否开通了家庭网
		String serial_number = input.getString("SERIAL_NUMBER");
		IData mainCard = UcaInfoQry.qryUserInfoBySn(serial_number);
		String user_id = mainCard.getString("USER_ID");
		IDataset family = checkFamily(user_id);
		if(IDataUtil.isEmpty(family)){
			returnData.put("X_RESULTCODE", "0000");
			returnData.put("X_RESULTINFO", "ok");
			returnData.put("STATUS", "0");
			return returnData;
		}
		String role_code = family.getData(0).getString("ROLE_CODE_B");
		if("1".equals(role_code)){
			//该卡为主卡
			mainCardSearch(serial_number,returnData);
			
		}else if("2".equals(role_code)){
			//该卡为副卡
			returnData.put("STATUS", "3");
			viceCardSearch(serial_number,returnData);
			
		}else{
			returnData.put("X_RESULTCODE", "9994");
			returnData.put("X_RESULTINFO", "信息有误");
			return returnData;
		}
		
		return returnData;
	}
	//主卡查询
	private void mainCardSearch(String serial_number,IData returnData) throws Exception{
		IData main = UcaInfoQry.qryUserInfoBySn(serial_number);
		String user_id = main.getString("USER_ID");
		//获取家庭网信息
		IData mainFamily = checkFamily(user_id).getData(0);
		String user_id_a = mainFamily.getString("USER_ID_A");
		//获取产品信息
		IDataset product = FamilyOperPreBean.getUserProduct(user_id_a);
		if(IDataUtil.isEmpty(product)){
			returnData.put("X_RESULTCODE", "8000");
			returnData.put("X_RESULTINFO", "产品信息有误");
			return ;
		}
		String product_id = product.getData(0).getString("PRODUCT_ID");
		IDataset discntInfos = CommparaInfoQry.getCommNetInfo("CSM", "5", "VPCN");
		if(!product_id.equals(discntInfos.getData(0).getString("PARA_CODE1"))){
			returnData.put("X_RESULTCODE", "7999");
			returnData.put("X_RESULTINFO", "产品信息有误");
			return ;
		}
		String mainDis = discntInfos.getData(0).getString("PARA_CODE2");
		String product_name = DiscntInfoQry.getDiscntInfoByDisCode(mainDis).getData(0).getString("DISCNT_NAME");
		int size = product_name.length();
		String price = product_name.substring(size-3, size-1)+"/月";
		//通过虚拟家庭网号，搜所有家庭网信息
		IDataset familyInfos = FamilyOperPreBean.getAllUserByUserIdA(user_id_a);
		IDataset viceInfos = new DatasetList();
		IData allInfo = new DataMap();
		for(Object info:familyInfos){
			IData realInfo = (IData) info;
			String role_code_b = realInfo.getString("ROLE_CODE_B");
			if("1".equals(role_code_b)){
				String short_code = realInfo.getString("SHORT_CODE");
				allInfo.put("MASTER_PHONE_NO", serial_number);
				allInfo.put("MASTER_SHORT_NO", short_code);
				IDataset res = new DatasetList();
				allInfo.put("RESOURCE_INFO_LIST", res);
				String start_date = realInfo.getString("START_DATE");
				String end_date = realInfo.getString("END_DATE");
				allInfo.put("ORDER_TIME", start_date);
				allInfo.put("VALID_DATE", start_date);
				allInfo.put("EXPIRE_DATE", end_date);
				allInfo.put("FEE_TYPE", "包月");
			}else if("2".equals(role_code_b)){
			    IData vice = new DataMap();
				String short_code = realInfo.getString("SHORT_CODE");
				String serial_number_b = realInfo.getString("SERIAL_NUMBER_B");
				vice.put("MEMBER_PHONE_NO", serial_number_b);
				vice.put("MEMBER_SHORT_NO", short_code);
				vice.put("MEMBER_STATUS", "3");
				viceInfos.add(vice);
			}
			
		}
		allInfo.put("MEMBER_INFO_LIST", viceInfos);
		allInfo.put("PRODUCT_ID", product_id);
		allInfo.put("BUSI_NAME", product_name);
		allInfo.put("BUSI_FEE", price);		
		
		returnData.putAll(allInfo);
	}
	//副卡查询
	private void viceCardSearch(String serial_number,IData returnData) throws Exception{
		IData main = UcaInfoQry.qryUserInfoBySn(serial_number);
		String user_id = main.getString("USER_ID");
		//获取家庭网信息
		IData mainFamily = checkFamily(user_id).getData(0);
		String user_id_a = mainFamily.getString("USER_ID_A");
		//获取产品信息
		IDataset product = FamilyOperPreBean.getUserProduct(user_id_a);
		if(IDataUtil.isEmpty(product)){
			returnData.put("X_RESULTCODE", "8000");
			returnData.put("X_RESULTINFO", "产品信息有误");
			return ;
		}
		String product_id = product.getData(0).getString("PRODUCT_ID");
		IDataset discntInfos = CommparaInfoQry.getCommNetInfo("CSM", "5", "VPCN");
		if(!product_id.equals(discntInfos.getData(0).getString("PARA_CODE1"))){
			returnData.put("X_RESULTCODE", "7999"); 
			returnData.put("X_RESULTINFO", "产品信息有误");
			return ;
		}
		String mainDis = discntInfos.getData(0).getString("PARA_CODE2");
		String product_name = DiscntInfoQry.getDiscntInfoByDisCode(mainDis).getData(0).getString("DISCNT_NAME");
		int size = product_name.length();
		String price = product_name.substring(size-3, size-1)+"/月";
		//通过虚拟家庭网号，搜所有家庭网信息
		IDataset familyInfos = FamilyOperPreBean.getAllUserByUserIdA(user_id_a);
		IDataset viceInfos = new DatasetList();
		IData vice = new DataMap();
		IData allInfo = new DataMap();
		for(Object info:familyInfos){
			IData realInfo = (IData) info;
			String role_code_b = realInfo.getString("ROLE_CODE_B");
			String serial_number_b = realInfo.getString("SERIAL_NUMBER_B");
			if("1".equals(role_code_b)){
				String short_code = realInfo.getString("SHORT_CODE");
				allInfo.put("MASTER_PHONE_NO", serial_number_b);
				allInfo.put("MASTER_SHORT_NO", short_code);
				IDataset res = new DatasetList();
				allInfo.put("RESOURCE_INFO_LIST", res);
				String start_date = realInfo.getString("START_DATE");
				String end_date = realInfo.getString("END_DATE");
				allInfo.put("ORDER_TIME", start_date);
				allInfo.put("VALID_DATE", start_date);
				allInfo.put("EXPIRE_DATE", end_date);
				allInfo.put("FEE_TYPE", "包月");
			}else if(serial_number.equals(serial_number_b)){
				String short_code = realInfo.getString("SHORT_CODE");
				vice.put("MEMBER_PHONE_NO", serial_number_b);
				vice.put("MEMBER_SHORT_NO", short_code);
				vice.put("MEMBER_STATUS", "3");
				viceInfos.add(vice);
			}
		}
		allInfo.put("MEMBER_INFO_LIST", viceInfos);
		allInfo.put("PRODUCT_ID", product_id);
		allInfo.put("BUSI_NAME", product_name);
		allInfo.put("BUSI_FEE", price);		
		
		returnData.putAll(allInfo);
	}
	//家庭网查询
	private IDataset checkFamily(String userId) throws Exception{
		IData checkFamily = new DataMap();
		checkFamily.put("USER_ID_B", userId);
		checkFamily.put("RELATION_TYPE_CODE", "45");
		return FamilyOperPreBean.getUserRelationByUserIdB(checkFamily);
	}
	//家庭网查询
	private IDataset countMembers(String userId) throws Exception{
		IData checkFamily = new DataMap();
		checkFamily.put("USER_ID_A", userId);
		checkFamily.put("RELATION_TYPE_CODE", "45");
		return FamilyOperPreBean.getAllRoleB(checkFamily);
	}
	//家庭网产品详情
//	private IData getOfferInfo(String product_id,String code) throws Exception{
//		
//		IData offerInfo = new DataMap();
//		offerInfo.put("OFFER_CODE", product_id);
//		offerInfo.put("OFFER_TYPE", code);
//		return FamilyOperPreBean.getOfferInfo(offerInfo).getData(0);
//	}
	//黑名单查询
	private void checkBlack(String serial_number,IData returnData) throws Exception{
		
		String psptTypeCode= "";
        String psptId = "";
        IData info = new DataMap();
        info.put("SERIAL_NUMBER", serial_number);
    	IDataset custInfo = CustomerInfoQry.getCustInfoByAllSn(info, null);
    	if(IDataUtil.isNotEmpty(custInfo))
    	{
    		psptTypeCode = custInfo.getData(0).getString("PSPT_TYPE_CODE");
    		psptId = custInfo.getData(0).getString("PSPT_ID");
    	}
    	if (UCustBlackInfoQry.isBlackCust(psptTypeCode, psptId))
        {
        	returnData.put("X_RESULTCODE", "8993");
			returnData.put("X_RESULTINFO", "该卡为黑名单用户");
			return;
        }
	}
	//查询接口的参数校验
	private IData checkParam(IData input) throws Exception{
		IData response = new DataMap();
		response.put("X_RESULTCODE", "0000");
		response.put("X_RESULTINFO", "OK");
		response.put("STATUS", "1");
		response.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		String number = input.getString("SERIAL_NUMBER");
		String ident_code = input.getString("IDENT_CODE");
		String biz_type = input.getString("BIZ_TYPE");
		if(StringUtils.isBlank(number)||StringUtils.isBlank(ident_code)||StringUtils.isBlank(biz_type)){
			response.put("X_RESULTCODE", "9999");
			response.put("X_RESULTINFO", "传入参数有误");
		}
		
		return response;
	}
}