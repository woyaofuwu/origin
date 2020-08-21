package com.asiainfo.veris.crm.order.soa.person.busi.bat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.rowset.serial.SerialException;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;

public class BatDataImportBean extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static Logger log = Logger.getLogger(BatDataImportBean.class);

	/**
	 * 通过手机号码判断是否为集团成员 <br/>
	 * 
	 * @param phoneNumber
	 * @return
	 * @throws Exception
	 */
	public static boolean is89custGroupmember(String phoneNumber) throws Exception {
		try {
			IData param = new DataMap();
			param.put("SERIAL_NUMBER", phoneNumber);
			IDataset dataset = Dao.qryByCode("TF_F_CUST_GROUPMEMBER", "SEL_BY_SN", param, Route.CONN_CRM_CG);
			if (IDataUtil.isNotEmpty(dataset)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}
	}

	/**
	 * 判断是否为个人证件类型
	 * 
	 * @param objValue
	 * @return
	 * @throws Exception
	 */
	public static boolean isPersonCertificate(String objValue) throws Exception {
		try {
			if ("D".equals(objValue) || "E".equals(objValue) || "G".equals(objValue) || "L".equals(objValue) || "M".equals(objValue)) {
				// 单位
				return false;
			} else {
				// 个人
				return true;
			}
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}
	}

	/**
	 * 根据编码获取名称
	 * 
	 * @param typeCodeValue
	 * @param key
	 *            要取的值
	 * @return
	 * @throws Exception
	 */
	public static String getTypeNameByTypeCode(String typeCodeValue) throws Exception {
		try {
			// 类型转换
			String[] keys = new String[3];
			keys[0] = "TYPE_ID";
			keys[1] = "SUBSYS_CODE";
			keys[2] = "DATA_ID";
			String[] value = new String[3];
			value[0] = "TD_S_PASSPORTTYPE2";
			value[1] = "CUSTMGR";
			value[2] = typeCodeValue;
			return StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", keys, "DATA_NAME", value);
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}
	}

	/**
	 * 验证名称
	 * 
	 * @param desc
	 *            提示输入框提示信息
	 * @param custName
	 * @param psptTypeCode
	 *            证件类型
	 * @return
	 * @throws Exception
	 * @author zhuoyingzhi 返回0000 表示验证通过 , 否则返回错误提示信息
	 */
	public static String checkCustName(String desc, String custName, String psptTypeCode) throws Exception {
		try {
			/**
			 * //查找以Java开头,任意结尾的字符串 Pattern pattern =
			 * Pattern.compile("^Java.*"); Matcher matcher =
			 * pattern.matcher("Java不是人"); boolean b= matcher.matches();
			 * //当条件满足时，将返回true，否则返回false 
			 */
			String re2 = "^(全球通|动感地带|套餐|大灵通|乡镇通|无权户|无档户|代办|代理)*$";
			String specialStr = "`￥#$~!@%^&*(),;'\"?><[]{}\\|:/=+―“”‘’，《》";

			for (int i = 0; i < specialStr.length(); i++) {
				if (custName.indexOf(specialStr.charAt(i)) > -1) {
					return desc + "包含特殊字符，请检查！";
				}
			}

			if (Pattern.compile(re2).matcher(custName).find()) {
				return desc + "包含非法关键字！";
			}

			if (!"A".equals(psptTypeCode) && !"D".equals(psptTypeCode)) {
				if (isInDigitChinese(custName)) {
					return "不是护照, " + desc + "不能包含数字和字母！";
				}
				if (custName.length() < 2 && isInChinese(custName)) {
					return "不是护照," + desc + "不能少于2个中文和字符！";
				}
			} else if ("A".equals(psptTypeCode)) {
				/* 护照：客户名称须大于三个字符，不能全为阿拉伯数字 */
				if (custName.length() < 3 || StringUtils.isNumeric(custName)) {
					return "是护照," + desc + "须大于三个字符，且不能全为阿拉伯数字！";
				}
			}
			/*if (custName.indexOf("校园") > -1 || custName.indexOf("海南通") > -1 || custName.indexOf("神州行") > -1 || custName.indexOf("动感地带") > -1 || custName.indexOf("套餐") > -1) {

				return desc + "不能包含【校园、海南通、神州行、动感地带、套餐】，请重新输入！";
			}*/

			return "0000";// 验证通过
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}
	}

	public static boolean isInChinese(String strName) {
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (!isChinese(c)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isChinese(char c) {

		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

		|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

		|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

		|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION

		|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

		|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

			return true;

		}

		return false;

	}

	/**
	 * 验证地址
	 * 
	 * @param desc
	 * @param addr
	 * @return
	 * @throws Exception
	 * @author zhuoyingzhi
	 */
	public static String checkAddr(String desc, String addr) throws Exception {
		try {
			if (addr.length() < 8) {
				return desc + "文字需大于8位";
			}
			if (StringUtils.isNumeric(addr)) {
				return desc + "不能全部为数字";
			}
			return "0000";
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}
	}

	/**
	 * 验证手机号码在本次导入内是否存在重复的
	 * 
	 * @param SERIAL_NUMBER
	 * @return
	 * @throws Exception
	 */
	public static String checkSerialNumberCount(String SERIAL_NUMBER, IDataset serialNumberList) throws Exception {
		try {
			boolean flag = false;
			if (IDataUtil.isNotEmpty(serialNumberList)) {
				for (int i = 0; i < serialNumberList.size(); i++) {
					if (SERIAL_NUMBER.equals(serialNumberList.getData(i).getString("SERIAL_NUMBER"))) {
						flag = true;
						break;
					}
				}
				if (flag) {
					return "本次导入中,手机号码：" + SERIAL_NUMBER + "出现重复,请更换手机号码";
				}
			}
			IData data = new DataMap();
			data.put("SERIAL_NUMBER", SERIAL_NUMBER);
			serialNumberList.add(data);
			return "0000";
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}

	}

	/**
	 * 
	 * @author
	 * @param strName
	 * @return 是否含有数字或者字母
	 */
	public static boolean isInDigitChinese(String strName) {
		if (Pattern.compile("(?i)[a-z]").matcher(strName).find()) {
			return true;
		}
		char[] ch = strName.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isDigit(c)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	private static boolean isDigit(char c) {
		if (!Character.isDigit(c)) {
			return false;
		}
		return true;
	}

	/**
	 * 验证最大长度
	 * 
	 * @param desc
	 * @param str
	 * @param maxCount
	 * @return
	 * @throws Exception
	 */
	public static String maxLen(String desc, String str, int maxCount) throws Exception {
		try {
			if (getWordCount(str) > maxCount) {
				return desc + "超过最大长度,最大长度为" + maxCount + ".";
			}
			return "0000";
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}
	}

	/**
	 * 获得以字节为单位的字符串长度 <br/>
	 * 由于Java是基于Unicode编码的，因此，一个汉字的长度为1，而不是2。 但有时需要以字节单位获得字符串的长度。
	 * 例如，“123abc长城”按字节长度计算是10，而按Unicode计算长度是8。
	 * 为了获得10，需要从头扫描根据字符的Ascii来获得具体的长度。如果是标准的字符
	 * ，Ascii的范围是0至255，如果是汉字或其他全角字符，Ascii会大于255。
	 * 
	 * @param str
	 * @return
	 */
	public static int getWordCount(String str) {
		int length = 0;
		for (int i = 0; i < str.length(); i++) {
			int ascii = Character.codePointAt(str, i);
			if (ascii >= 0 && ascii <= 255)
				length++;
			else
				length += 2;
		}
		return length;

	}

	/**
	 * 初始化本次导入中使用人证件号码可用个数
	 * 
	 * @param cidList
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	public static void initCanUseCount(List<Map<String, Object>> cidList, IDataset dataset) throws Exception {
		try {

			// 记录没有重复的信息
			IDataset notRepeatData = new DatasetList();

			for (int i = 0; i < dataset.size(); i++) {
				IData data = dataset.getData(i);
				Map<String, Object> cidMap = new HashMap<String, Object>();

				String use = data.getString("DATA1");
				String usePspt = data.getString("DATA3");
				String  serialNumber=data.getString("SERIAL_NUMBER");

				// 使用人证件类型
				String use_pspt_type_code = data.getString("DATA2");
				if (!"".equals(use_pspt_type_code) && use_pspt_type_code != null) {
					// 使用人个人证件
					if (isPersonCertificate(use_pspt_type_code)) {
						// 使用人
						if (!"".equals(usePspt) && usePspt != null && !"".equals(use) && use != null) {

							/**
							 * 检查此证件号码之前验证过没
							 */
							IData obj = new DataMap();
							boolean checkMark = false;// 验证标识 true表示已经验证过
							if (IDataUtil.isNotEmpty(notRepeatData)) {
								for (int k = 0; k < notRepeatData.size(); k++) {
									if (usePspt.equals(notRepeatData.getData(k).getString("DATA3"))) {
										// usePspt 此证件号码已经验证过
										checkMark = true;
										break;
									}
								}
								if (!checkMark) {
									obj.put("DATA3", usePspt);
									notRepeatData.add(obj);
								}
							} else {
								obj.put("DATA3", usePspt);
								notRepeatData.add(obj);
							}

							if (!checkMark) {
								// 此证件号码没有验证过
								IData param = new DataMap();
								param.put("CUST_NAME", use.trim());
								param.put("PSPT_ID", usePspt.trim());
								param.put("SERIAL_NUMBER", serialNumber);
								IDataset rs = CSAppCall.call("SS.CreatePersonUserSVC.checkImportRealNameLimitByUsePspt", param);
								if (IDataUtil.isNotEmpty(rs)) {
									IData r = rs.getData(0);
									String strrCount = r.getString("rCount");
									String strrLimit = r.getString("rLimit");
									int rCount = Integer.parseInt(strrCount); // 已办理证件办理数
									int rLimit = Integer.parseInt(strrLimit); // 证件办理最大数
									// 剩余可办理个数
									int canCount = rLimit - rCount;
									cidMap.put(usePspt, canCount);// 使用人证件号码可使用个数
									cidList.add(cidMap);
								}
							}

						}
					}
				}
			}
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}
	}

	/**
	 * 修改剩余个数
	 * 
	 * @param cidList
	 * @param psptNmuber
	 *            证件号码
	 * @throws Exception
	 */
	public static boolean updateCanCount(List<Map<String, Object>> cidList, String psptNmuber) throws Exception {
		try {
			boolean flag = false;
			// 验证标志
			boolean checkMark = false;
			for (Map<String, Object> obj : cidList) {
				for (String k : obj.keySet()) {
					if (psptNmuber.equals(k)) {
						// 找到对应的证件号码
						int canNum = (Integer) obj.get(k);// 可用个数
						if (canNum > 0) {
							// 还可以使用
							obj.put(k, (canNum - 1));
							checkMark = true;
						} else {
							// 已使用完
							obj.put(k, 0);
							checkMark = false;
						}
						flag = true;
						break;
					}
				}
				if (flag) {
					break;
				}
			}
			return checkMark;
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}
	}

	/**
	 * 校验:不允许一证多名(使用人)
	 * 
	 * @param psptType
	 * @param psptid
	 * @param psptList
	 * @return
	 * @throws Exception
	 */
	public static String checkPsptIdCount(String useName, String psptType, String psptid, IDataset psptList) throws Exception {
		try {
			// 使用人证件类型（身份证相关检查）
			if ("0".equals(psptType) || "1".equals(psptType) || "2".equals(psptType)) {

				boolean flag = false;
				if (IDataUtil.isNotEmpty(psptList)) {
					for (int i = 0; i < psptList.size(); i++) {
						if (psptid.equals(psptList.getData(i).getString("PSPT_ID"))) {
							if (!useName.equals(psptList.getData(i).getString("USE_NAME"))) {
								// 名称不相同
								flag = true;
								break;
							}
						}
					}
					if (flag) {
						return "本次导入中,不允许一证多名(使用人)";
					}
				}
				IData data = new DataMap();
				data.put("PSPT_ID", psptid);// 使用人证件号码
				data.put("USE_NAME", useName);// 使用人名称
				psptList.add(data);
				return "0000";
			} else {
				return "0000";
			}
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}
	}

	/**
	 * 通过tf_f_user_other表判断是否为测试卡， 不能使用tf_f_user 来判断，因为在有存量的测试存在。 <br/>
	 * SEL_BY_OTHERSTR1 已经存在
	 * 
	 * @param serialNumber
	 * @return
	 * @throws Exception
	 */
	public static boolean IsTestCradUser(String serialNumber, String userId) throws Exception {
		try {
			IData param = new DataMap();
			param.put("RSRV_STR1", serialNumber);
			param.put("USER_ID", userId);
			param.put("RSRV_VALUE_CODE", "TEST_CARD_USER");
			IDataset dataset = Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_OTHERSTR1", param, Route.CONN_CRM_CG);
			if (IDataUtil.isNotEmpty(dataset)) {
				// 是测试卡
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}

	}

	/**
	 * @param checkType
	 *            校验类型：0校验手机号码，1校验座机号码，2两者都校验满足其一就可
	 * @param phoneNum
	 * */
	public static boolean validPhoneNum(String checkType, String phoneNum) {
		boolean flag = false;
		Pattern p1 = null;
		Pattern p2 = null;
		Matcher m = null;
		p1 = Pattern.compile("^1[2||3||4||5||7||8||9]{1}[0-9]{9}$");
		p2 = Pattern.compile("^(0[0-9]{2,3}\\-)?([1-9][0-9]{6,7})$");
		if ("0".equals(checkType)) {
			if (phoneNum.length() != 11) {
				return false;
			} else {
				m = p1.matcher(phoneNum);
				flag = m.matches();
			}
		} else if ("1".equals(checkType)) {
			if (phoneNum.length() < 11 || phoneNum.length() >= 16) {
				return false;
			} else {
				m = p2.matcher(phoneNum);
				flag = m.matches();
			}
		} else if ("2".equals(checkType)) {
			if (!((phoneNum.length() == 11 && p1.matcher(phoneNum).matches()) || (phoneNum.length() < 16 && p2.matcher(phoneNum).matches()))) {
				return false;
			} else {
				flag = true;
			}
		}
		return flag;
	}

	/**
	 * REQ201610200010_关于测试卡管理的三点优化
	 * 
	 * @author zhuoyingzhi 20161111 <br/>
	 * @param attrFieldName
	 * @return
	 * @throws Exception
	 */
	public static IDataset getAttrItembByAttrFieldName(String attrFieldName) throws Exception {
		try {
			/*
			 * IData param=new DataMap(); param.put("ATTR_FIELD_NAME",
			 * attrFieldName); param.put("ID", "9501"); param.put("ATTR_CODE",
			 * "FD_CODE"); param.put("ID_TYPE", "S"); param.put("EPARCHY_CODE",
			 * CSBizBean.getTradeEparchyCode());
			 */
			IDataset dataset = UpcCallIntf.qryOfferChaValByCond("9501", "S", "FD_CODE", attrFieldName, CSBizBean.getTradeEparchyCode());

			// IDataset dataset=Dao.qryByCode("TD_B_ATTR_ITEMB",
			// "SEL_ATTR_ITEMB_BY_ATTR_FIELD_NAME", param,Route.CONN_CRM_CEN);
			return dataset;
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}
	}

	/**
	 * REQ201610200010_关于测试卡管理的三点优化
	 * 
	 * @author zhuoyingzhi 20161111 <br/>
	 *         查询用户的有效流量封顶服务 <br/>
	 *         SEL_BY_USER_ID_SERVICE_ID_END 已经存在
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserSVCInfoByEnd(String userId, String serviceId) throws Exception {
		try {
			IData param = new DataMap();
			param.put("USER_ID", userId);
			param.put("SERVICE_ID", serviceId);
			IDataset dataset = Dao.qryByCodeParser("TF_F_USER_SVC", "SEL_BY_USER_ID_SERVICE_ID_END", param, Route.CONN_CRM_CG);
			return dataset;
		} catch (Exception e) {
			//log.info("(e.getMessage());
			throw e;
		}
	}
	
	/**
	 * 物联网批量开户打印电子工单优化
	 * REQ201806190020_新增行业应用卡批量开户人像比对功能
	 * @param returnInfos
	 * @param batOperType
	 * @return
	 * @throws Exception
	 * @author zhuoyingzhi
	 * @date 20180720
	 */
	public static void  insertIntoTradeCnoteInfoBat(IData returnInfos,String batOperType) throws Exception{
		
        IData param = new DataMap();
        
        //获取批次号
        String batchId=returnInfos.getString("BATCH_ID", "");
        
        if(batchId==null || "".equals(batchId)){
        	return ;
        }
        //在打印电子受理单的时候billid传给东软的值也是  批量次号
        param.put("TRADE_ID", batchId);
        
        
        String acceptTime = SysDateMgr.getSysTime();
        String acceptMonth = acceptTime.substring(5, 7);
        
        param.put("ACCEPT_MONTH", acceptMonth);
        
        param.put("NOTE_TYPE", "1");//票类型
        
        
        String receiptInfo1 = "受理员工："+CSBizBean.getVisit().getStaffId()+"   业务受理时间："+acceptTime;
        String receiptInfo2="";
        
        String remark="";
        if("CREATEPREUSER_PWLW".equals(batOperType)){
        	//物联网批量开户
        	remark="物联网批量开户,打印电子工单";
        	
        	receiptInfo1 =receiptInfo1 + "~~业务类型：物联网批量开户 ";
        	
        }else if("CREATEPREUSER_M2M".equals(batOperType)){
        	//行业应用卡批量开户
        	remark="行业应用卡批量开户,打印电子工单";
        	
        	receiptInfo1 =receiptInfo1 + "~~业务类型：行业应用卡批量开户 ";
        }else if("TD_FIXED_PHONE_STOP".equals(batOperType)){
			// TD二代无线固话批量停机
			remark="TD二代无线固话批量停机,打印电子工单";

			receiptInfo1 =receiptInfo1 + "~~业务类型：TD二代无线固话批量停机 ";
		}else if ("TT_FIXED_PHONE_STOP".equals(batOperType)){
        	// 铁通固话批量停机
			remark="铁通固话批量停机,打印电子工单";

			receiptInfo1 =receiptInfo1 + "~~业务类型：铁通固话批量停机 ";
		}else if ("BATREALNAME".equals(batOperType)){
			// 批量无线固话登记实名制
			remark="批量无线固话登记实名制,打印电子工单";

			receiptInfo1 =receiptInfo1 + "~~业务类型：批量无线固话登记实名制 ";
		}else if ("BATUPDATEPSW".equals(batOperType)){
			// 批量无线固话修改密码
			remark="批量无线固话修改密码,打印电子工单";

			receiptInfo1 =receiptInfo1 + "~~业务类型：批量无线固话修改密码 ";
		}else if ("MODIFYTDPSPTINFO".equals(batOperType)){
			// 批量无线固话单位证件实名制登记
			remark="批量无线固话单位证件实名制登记,打印电子工单";

			receiptInfo1 =receiptInfo1 + "~~业务类型：批量无线固话单位证件实名制登记 ";
		}
        
        param.put("RECEIPT_INFO1", receiptInfo1);
        param.put("RECEIPT_INFO2", receiptInfo2);
        
        
        param.put("UPDATE_TIME", acceptTime);
        param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        param.put("ACCEPT_DATE", acceptTime);
        param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        param.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        
        param.put("REMARK", remark);
        
        Dao.executeUpdateByCodeCode("TF_B_TRADE_CNOTE_INFO", "INSERTINTO_TRADECNOTEINFO_BAT", param,Route.getCrmDefaultDb());		
	}
	
	public static boolean IsUserGrpDiscnt(String serialNumber, String userId) throws Exception {
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset dataset = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_BY_USERID_ALLGRP", param, Route.CONN_CRM_CG);
		if (IDataUtil.isEmpty(dataset)) {
			return true;
		} else {
			return false;
		}
	}


	public static String getSerialNumberBySimCardNo(String simCardNo) throws Exception {
		String serialNumber = "";
		//System.out.println("============getSerialNumberBySimCardNo=========simCardNo:"+simCardNo);
		IDataset dataSet = ResCall.getSimCardInfo("0",simCardNo,"",null);
		//System.out.println("============getSerialNumberBySimCardNo=========dataSet:"+dataSet);

 		if (IDataUtil.isNotEmpty(dataSet))
 		{
 			IData simCardInfo = dataSet.first();
 			serialNumber = simCardInfo.getString("ACCESS_NUMBER","");
 		}
 		return serialNumber ;
	}
}
