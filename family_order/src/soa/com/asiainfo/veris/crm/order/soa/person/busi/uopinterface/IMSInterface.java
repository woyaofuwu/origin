/**
 * 
 */
package com.asiainfo.veris.crm.order.soa.person.busi.uopinterface;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.bizservice.query.product.UProductInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.codec.binary.Base64;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.rsaEncryptDecrypt.util.Base64Util;
import com.asiainfo.veris.crm.order.soa.person.busi.uopinterface.util.CipherUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.uopinterface.util.RSAUtil;

/**
 * @author wangsc10
 * 和家固话接口,电渠家庭IMS固话开户接口
 *
 */
public class IMSInterface extends CSBizService{
		private static final long serialVersionUID = 1L;
		private static Logger log = Logger.getLogger(IMSInterface.class);
	    /**
	     * 
	     * @Description：1.	查询宽带信息
	     * @param:@param input phone type
	     * @param:@return
	     * @return IData broadband ims码号
	     * @throws Exception 
	     * @throws
	     * @Author :wangsc10
	     * @date :2018-9-13上午10:09:25
	     */
	    public IData queryOpenAccount(IData input) throws Exception
	    {
	    	log.debug("queryOpenAccount()--方法-------------入参-->IData= "+input);
	    	IData resultJson = new DataMap();
	    	try {
	    		IData checkrealnamedata = new DataMap();
	    		
	    		CipherUtil desPlus = new CipherUtil("");
		        String psptIdStr = desPlus.deCiper(input.getString("custCertNo"));
		        String custNameStr = desPlus.deCiper(input.getString("custName"));
		        String psptId = Base64Util.encode(psptIdStr);
		        String custName = Base64Util.encode(custNameStr);
		    	checkrealnamedata.put("SERIAL_NUMBER", input.getString("phone"));
				checkrealnamedata.put("CARD_NUMBER", psptId);
				checkrealnamedata.put("CUST_NAME", custName);
	            IDataset realnameIData = CSAppCall.call("SS.IMSLandLineIntfSVC.checkIMSPhoneCustInfo", checkrealnamedata);
	            if(realnameIData != null && realnameIData.size() > 0){
	            	String RESULT_CODE = realnameIData.getData(0).getString("RESULTCODE","");
	            	if(RESULT_CODE.equals("0")){
	            		resultJson.put("authCode", "1");//1：实名制信息一致
	            	}else{
	            		resultJson.put("authCode", "0");//0：实名制信息不一致
	            		throw new Exception("实名制信息不一致！");
	            	}
	            }
		    	IDataset resultArray = new DatasetList();
		    	IDataset dataset = RelaUUInfoQry.qrySerialNumberBySnBAndRelationType(input.getString("phone"),"47");
		    	if(IDataUtil.isNotEmpty(dataset)){
		    		String userIdA = dataset.first().getString("USER_ID_A"); 
		    		IDataset dataset2 = RelaUUInfoQry.getRelationsByUserIdA("47", userIdA, "2");
	    			if("2".equals(dataset2.first().getString("ROLE_CODE_B"))){
	    				IData temp = new DataMap();
	    				temp.put("broadbandId", dataset2.first().getString("SERIAL_NUMBER_B").substring(3));
	    				//查询宽带地址
	    				String user_id_b = dataset2.first().getString("USER_ID_B");
	    				IDataset dataset3 = WidenetInfoQry.getUserWidenetInfo(user_id_b);
	    				
	    				temp.put("address", dataset3.first().getString("DETAIL_ADDRESS"));
	    				resultArray.add(temp);
	    			}
	    			
		    		resultJson.put("broadbands", resultArray);
			    	resultJson.put("resultCode", "000000");
			    	resultJson.put("desc", "操作成功");
		    	}else{
			    	resultJson.put("resultCode", "000001");
			    	resultJson.put("desc", "查询不到用户宽带信息。");
		    	}
	    	}catch (Exception e) {
	    		log.error("queryOpenAccount()--方法-------错误信息-->" + e.getMessage(), e);
	    		resultJson.put("resultCode", "000001");
		    	resultJson.put("desc", "查询宽带信息: " + e.getMessage());
			}
			return resultJson;
	    	
	    }
	    
	    /**
	     * 
	     * @Description：2.	查询可以复用的固话号码
	     * @param:@param input phone type
	     * @param:@return
	     * @return IData broadband ims码号
	     * @throws Exception 
	     * @throws
	     * @Author :wangsc10
	     * @date :2018-9-13上午10:09:25
	     */
	    public IData queryReuseIMSAccount(IData input) throws Exception
	    {
	    	String serialNumber = input.getString("phone");
	    	IData data = UcaInfoQry.qryUserInfoBySn(serialNumber);
	    	IDataset callset=ResCall.getTenImsiPhoneByCityCode("0", "0G", "0", data.getString("CITY_CODE"));
	    	IDataset dataset = pooting(callset);
	    	IData resultJson = new DataMap();
	    	if(IDataUtil.isNotEmpty(dataset)){
	    		resultJson.put("imsNum", dataset);
		    	resultJson.put("resultCode", "000000");
		    	resultJson.put("desc", "操作成功");
	    	}else{
		    	resultJson.put("resultCode", "000001");
		    	resultJson.put("desc", "查询不到用户可以复用的固话号码。");
	    	}
	    	return resultJson;
		}
	    /**
	     * 
	     * @Description：3.	查询已开通的账号信息
	     * @param:phone	  必选	String	开通和家固话的手机号
				  imsNum 可选	String	和家固话账号
				  type	   可选	String	传固定值ims

	     * @return imsNum     必选	String	ims码号
				   imsAccount 必选	String	ims鉴权账号
				   password	      必选	String	ims密码
	     * @throws Exception 

	     * @throws
	     * @Author :wangsc10
	     * @date :2018-9-20下午03:11:32
	     */
	    public IData queryIMSAccountInfo(IData input) throws Exception {
	    	IData resultJson = new DataMap();
	    	String IMSNumber = input.getString("imsNum");
	    	String sn =  input.getString("phone");
    		try {
    		   String userIdB="";
	    	   IData userInfoData = UcaInfoQry.qryUserInfoBySn(sn); 
	    	   if(IDataUtil.isNotEmpty(userInfoData)){
	    		   userIdB =  userInfoData.getString("USER_ID","").trim();
	    	   }else{
	    		   throw new Exception("查询不到用户信息！");
	    	   }
	    	   //获取主号信息
	           IDataset iDataset=RelaUUInfoQry.getRelaByUserIdAndRelaTypeCode(userIdB, "MS", "1");
	           if(IDataUtil.isNotEmpty(iDataset)){
	        	 //获取虚拟号
	        	 String userIdA=iDataset.getData(0).getString("USER_ID_A", "");
	        	 //通过虚拟号获取关联的IMS家庭固话号码信息
	        	 IDataset userBInfo=RelaUUInfoQry.getRelationsByUserIdA("MS", userIdA, "2");
	        	 
	        	 if(IDataUtil.isNotEmpty(userBInfo)){
	        		 String imsNumber = userBInfo.first().getString("SERIAL_NUMBER_B");
	    	    		String USER_ID_B = userBInfo.first().getString("USER_ID_B");
	    	    		IDataset USER_WIDENET = WidenetInfoQry.getUserWidenetInfo(USER_ID_B);
	    	    		if(IDataUtil.isEmpty(USER_WIDENET)){
	        				throw new Exception("用户还未开通宽带！");
	        			}
	    	    		resultJson.put("password", RSAUtil.encrypt(USER_WIDENET.first().getString("ACCT_PASSWD")));//加密
	    	    		resultJson.put("imsNum", imsNumber);
	    	    		resultJson.put("imsAccount", imsNumber);
	    	    		resultJson.put("resultCode", "000000");
	    		    	resultJson.put("desc", "操作成功");
	        	 }else{
	        		 throw new Exception("用户不存在有效的IMS固话号码！");
	        	 }
	           }else{
	        	   throw new Exception("该用户未办理IMS固话业务！");
	           }
			} catch (Exception e) {
				resultJson.put("resultCode", "000001");
		    	resultJson.put("desc", "查询已开通的账号信息:"+e.getMessage());
			}
			return resultJson;
		}
	    /**
	     * 
	     * @Description：4.	开户选号
	     * @param:@param input phone	 可选	String	用户手机号
							   type	            可选	String	传固定值ims
	     * @param:@return      imsList
	     * @param:@throws Exception
	     * @return IData
	     * @throws
	     * @Author :wangsc10
	     * @date :2018-9-20下午05:12:28
	     */
	    public IData queryAbleIMSAccount(IData input) throws Exception {
	    	IData resultJson = new DataMap();
	    	String serialNumber = input.getString("phone");
	    	String wSerialNumber = "KD_" + serialNumber;
	    	try {
	    		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
	    		if(IDataUtil.isNotEmpty(userInfo)){
	    			String USER_STATE_CODESET = userInfo.getString("USER_STATE_CODESET");
	    			if(null != USER_STATE_CODESET && !"0".equals(USER_STATE_CODESET)){
	    				throw new Exception("用户的号码非正常状态，无法办理！");
	    			}
	    		}else{
	    			throw new Exception("查询不到用户信息！");
	    		}
		        String userId = userInfo.getString("USER_ID");
		    	// .1是否办理过宽带
	            IData wUserInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
	            if (IDataUtil.isEmpty(wUserInfo))
	            {
	                throw new Exception("用户没有开通宽带, 不能办理该业务！");
	            }
	            
	            // .2用户宽带FTTH类型宽带
	            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
	            
	            if (IDataUtil.isEmpty(wideNetInfo))
	            {
	                throw new Exception("该用户宽带资料信息不存在！");
	            }
	            
	            if(!(StringUtils.equals("3", wideNetInfo.getString("RSRV_STR2", "")) || StringUtils.equals("5", wideNetInfo.getString("RSRV_STR2", ""))))
	            {
	            	throw new Exception("用户宽带非FTTH类型宽带！");
	            }
	            
	            // .3用户是否已经办理家庭IMS固话
	            IDataset uuInfo = RelaUUInfoQry.getRelationUUInfoByDeputySn(userId, "MS",null);
	            
	            if (IDataUtil.isNotEmpty(uuInfo))
	            {
	                throw new Exception("该用户已经办理过家庭IMS固话业务！");
	            }
	            
		    	String citycode = "";
		    	//查出手机号码开的宽带地址，业务归属区
		    	IDataset dataset1 = RelaUUInfoQry.qrySerialNumberBySnBAndRelationType(serialNumber,"47");
		    	if(IDataUtil.isNotEmpty(dataset1)){
		    		String userIdA = dataset1.first().getString("USER_ID_A"); 
		    		IDataset dataset2 = RelaUUInfoQry.getRelationsByUserIdA("47", userIdA, "2");
	    			if("2".equals(dataset2.first().getString("ROLE_CODE_B"))){
	    				String user_id_b = dataset2.first().getString("USER_ID_B");
	    				IDataset dataset3 = WidenetInfoQry.getUserWidenetInfo(user_id_b);
	    				citycode = dataset3.first().getString("RSRV_STR4");
	    			}
		    	}else{
			    	throw new Exception("查询不到用户宽带信息！");
		    	}
		    	
		    	IDataset callset=ResCall.getTenImsiPhoneByCityCode("0", "0G", "0", citycode);
		    	IDataset dataset4 = pooting(callset);
		    	
		    	if(IDataUtil.isEmpty(dataset4)){
			    	throw new Exception("查询不到用户的开户号码！");
		    	}
		    	resultJson.put("imsList", dataset4);
		    	resultJson.put("resultCode", "000000");
		    	resultJson.put("desc", "操作成功");
			} catch (Exception e) {
				resultJson.put("resultCode", "000001");
		    	resultJson.put("desc", "开户选号:"+e.getMessage());
			}
	    	return resultJson;
		}
	    /**
		 * @Description：组装返回参数
		 * @param:@param callset
		 * @param:@return
		 * @return IDataset
		 * @throws
		 * @Author :wangsc10
		 * @date :2018-9-25下午03:21:01
		 */
		private IDataset pooting(IDataset callset) {
			
			IDataset resultList = new DatasetList();
			IDataset tempDataset = callset.first().getDataset("OUTDATA");
			
			if(!IDataUtil.isEmpty(tempDataset)){
				for (int i = 0; i < tempDataset.size(); i++){
					IData ele = tempDataset.getData(i);
					resultList.add( ele.getString("ACCESS_NUMBER"));
				}
			}
			
			return resultList;
		}
		
		//wangsc10-20181012-strat
		/**
	     * 
	     * @Description：5.	开通业务
	     * @param:@param phone	必选	String	用户手机号
						 broadband	可选	String	宽带账号，1个手机号仅能开通1个固话、1个宽带的省份可不传
						 imsNum	必选	Stirng	选择的固话号码
						 comboId	必选	String	套餐id
						 type	可选	String	传固定值ims
						 
	     * @param:@return imsNum	必选	String	ims固话号码
						  imsAccount	必选	String	ims账号信息
						  password	必选	String	ims密码

	     * @param:@throws Exception
	     * @return IDataset
	     * @throws
	     * @Author :wangsc10
	     * @date :2018-9-20上午09:23:07
	     */
	    public IData openAccount(IData input) throws Exception
	    {
	    	/**
	    	 * PRODUCT_ID
	    	 * NORMAL_USER_ID
	    	 * SERIAL_NUMBER
	    	 * WIDE_SERIAL_NUMBER ims号码
	    	 * PRODUCT_NAME 家庭IMS固话
	    	 * WIDE_PRODUCT_NAME FTTH宽带产品50M套餐(2018)
	    	 * IS_MERGE_WIDE_USER_CREATE 
	    	 * MO_PRODUCT_ID 营销活动的产品编码
	    	 * MO_PACKAGE_ID 营销活动的包编码
	    	 * TOP_SET_BOX_SALE_ACTIVE_FEE 营销活动费用
	    	 * SELECTED_ELEMENTS
	    	 * [{\"ELEMENT_ID\":\"84003861\",\"ELEMENT_TYPE_CODE\":\"D\",\"PRODUCT_ID\":\"84004439\",\"PACKAGE_ID\":\"0\",\"MODIFY_TAG\":\"0\",\"START_DATE\":\"2018-09-21 18:05:13\",\"END_DATE\":\"2050-12-31 23:59:59.0\",\"INST_ID\":\"\"},
	    	 * {\"ELEMENT_ID\":\"84004236\",\"ELEMENT_TYPE_CODE\":\"S\",\"PRODUCT_ID\":\"84004439\",\"PACKAGE_ID\":\"0\",\"MODIFY_TAG\":\"0\",\"START_DATE\":\"2018-09-21 18:05:13\",\"END_DATE\":\"2050-12-31 23:59:59.0\",\"INST_ID\":\"\"},
	    	 * {\"ELEMENT_ID\":\"84004237\",\"ELEMENT_TYPE_CODE\":\"S\",\"PRODUCT_ID\":\"84004439\",\"PACKAGE_ID\":\"41002606\",\"MODIFY_TAG\":\"0\",\"START_DATE\":\"2018-09-21 18:05:13\",\"END_DATE\":\"2050-12-31 23:59:59\",\"INST_ID\":\"\"},
			   {\"ELEMENT_ID\":\"84004238\",\"ELEMENT_TYPE_CODE\":\"S\",\"PRODUCT_ID\":\"84004439\",\"PACKAGE_ID\":\"41002606\",\"MODIFY_TAG\":\"0\",\"START_DATE\":\"2018-09-21 18:05:13\",\"END_DATE\":\"2050-12-31 23:59:59\",\"INST_ID\":\"\"},
			   {\"ELEMENT_ID\":\"84004239\",\"ELEMENT_TYPE_CODE\":\"S\",\"PRODUCT_ID\":\"84004439\",\"PACKAGE_ID\":\"41002606\",\"MODIFY_TAG\":\"0\",\"START_DATE\":\"2018-09-21 18:05:13\",\"END_DATE\":\"2050-12-31 23:59:59\",\"INST_ID\":\"\"}]
	    	 * 
	    	 */
	    	log.debug("openAccount()--方法-------------入参-->IData= "+input);
	    	IData resultJson = new DataMap();
	    	IData data = new DataMap();
	    	//公共参数构造 写死的--吴坚
			getVisit().setStaffId("ITFZYC35");
			getVisit().setDepartId("00300");
			getVisit().setCityCode("HNHK");
			getVisit().setLoginEparchyCode("0898");
			getVisit().setStaffEparchyCode("0898");
			
	    	String phone = input.getString("phone");//必选 用户手机号
	    	String serialNumberB = input.getString("imsNum");//必选  选择的固话号码
	    	String comboId = input.getString("comboId");//必选 套餐id
	    	String broadband = input.getString("broadband");//可选 宽带账号，1个手机号仅能开通1个固话、1个宽带的省份可不传
	    	String type = input.getString("type");//可选 传固定值ims
	    	
	    	try {
		    	IData checkrealnamedata = new DataMap();
		    	CipherUtil desPlus = new CipherUtil("");
		        String psptIdStr = desPlus.deCiper(input.getString("custCertNo"));
		        String custNameStr = desPlus.deCiper(input.getString("custName"));
		        String psptId = Base64Util.encode(psptIdStr);
		        String custName = Base64Util.encode(custNameStr);
		    	checkrealnamedata.put("SERIAL_NUMBER", input.getString("phone"));
				checkrealnamedata.put("CARD_NUMBER", psptId);
				checkrealnamedata.put("CUST_NAME", custName);
	            IDataset realnameIData = CSAppCall.call("SS.IMSLandLineIntfSVC.checkIMSPhoneCustInfo", checkrealnamedata);
	            if(realnameIData != null && realnameIData.size() > 0){
	            	String RESULT_CODE = realnameIData.getData(0).getString("RESULTCODE","");
	            	if(RESULT_CODE.equals("0")){
	            		resultJson.put("authCode", "1");//1：实名制信息一致
	            	}else{
	            		resultJson.put("authCode", "0");//0：实名制信息不一致
	            		throw new Exception("实名制信息不一致！");
	            	}
	            }
            
	    		IData userInfo = UcaInfoQry.qryUserInfoBySn(phone);
	    		if(IDataUtil.isNotEmpty(userInfo)){
	    			String USER_STATE_CODESET = userInfo.getString("USER_STATE_CODESET");
	    			if(null != USER_STATE_CODESET && !"0".equals(USER_STATE_CODESET)){
	    				throw new Exception("用户的号码非正常状态，无法办理！");
	    			}
	    		}else{
	    			throw new Exception("查询不到用户信息！");
	    		}
	    		
	    		IDataset productElements = ProductElementsCache.getProductElements(comboId);//根据product_id获取该产品下的所有元素
		    	
	            IData checkAuthSerialNumdata = new DataMap();
	            checkAuthSerialNumdata.put("AUTH_SERIAL_NUMBER", phone);
	            IDataset IData = CSAppCall.call("SS.IMSLandLineSVC.checkAuthSerialNum", checkAuthSerialNumdata);
	            if(IData != null && IData.size() > 0){
	            	String RESULT_CODE = IData.getData(0).getString("RESULT_CODE","");
	            	String RESULT_INFO = IData.getData(0).getString("RESULT_INFO","");
	            	if(RESULT_CODE.equals("0")){
	            		throw new Exception(RESULT_INFO);
	            	}
	            }
	    		String citycode = "";
		    	//查出手机号码开的宽带地址，业务归属区
		    	IDataset dataset1 = RelaUUInfoQry.qrySerialNumberBySnBAndRelationType(phone,"47");
		    	if(IDataUtil.isNotEmpty(dataset1)){
		    		String userIdA = dataset1.first().getString("USER_ID_A"); 
		    		IDataset dataset2 = RelaUUInfoQry.getRelationsByUserIdA("47", userIdA, "2");
	    			if("2".equals(dataset2.first().getString("ROLE_CODE_B"))){
	    				String user_id_b = dataset2.first().getString("USER_ID_B");
	    				IDataset dataset3 = WidenetInfoQry.getUserWidenetInfo(user_id_b);
	    				citycode = dataset3.first().getString("RSRV_STR4");
	    			}
		    	}else{
		    		throw new Exception("查询不到用户宽带信息！");
		    	}
		    	
	    		//选占
	    		IData userData = new DataMap();
	            userData.put("FIX_NUMBER", serialNumberB);
	            userData.put("CITYCODE_RSRVSTR4", citycode);
	            
	            IDataset IDatacheckFixPhoneNum = CSAppCall.call("SS.IMSLandLineSVC.checkFixPhoneNum", userData);
	            if(IDatacheckFixPhoneNum != null && IDatacheckFixPhoneNum.size() > 0){
	            	String RESULT_CODE = IDatacheckFixPhoneNum.getData(0).getString("RESULT_CODE","");
	            	String RESULT_INFO = IDatacheckFixPhoneNum.getData(0).getString("RESULT_INFO","");
	            	if(RESULT_CODE.equals("-1")){
	            		throw new Exception(RESULT_INFO);
	            	}
	            }
	            //开户
	        	data.put("SERIAL_NUMBER", phone);
		        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
		        data.put("WIDE_SERIAL_NUMBER",serialNumberB);
		        data.put("PRODUCT_ID", comboId);
		        data.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(comboId));
		        data.put("SELECTED_ELEMENTS", productElements);
		        data.put("OPEN_IMS", "OPEN_IMS");//在主台账中给预留字段赋值，服开根据此字段来判断是否自动完工，不需要走外线工单
		        
		        IDataset result = CSAppCall.call("SS.IMSLandLineRegSVC.tradeReg", data);
		        String passwd = "";
		        if(result != null && IDataUtil.isNotEmpty(result)){
		        	String trade_id = result.first().getString("TRADE_ID");
		        	passwd =  TradeWideNetInfoQry.queryTradeWideNet(trade_id).first().getString("ACCT_PASSWD");//获取明文密码
		        }else{
		        	throw new Exception("开户失败！");
		        }
	    		resultJson.put("imsNum", serialNumberB);//ims固话号码
	    		resultJson.put("imsAccount", serialNumberB);//ims账号信息
	    		resultJson.put("password", RSAUtil.encrypt(passwd));//加密
	    		resultJson.put("resultCode", "000000");
		    	resultJson.put("desc", "操作成功");
			} catch (Exception e) {
				log.error("openAccount()--方法-------错误信息-->" + e.getMessage(), e);
				resultJson.put("resultCode", "000001");
		    	resultJson.put("desc", "开通业务: " + e.getMessage());
			}
			return resultJson;
	    }
	    //end
	    /**
	     * 
	     * @Description：6.	退订业务
	     * @param:@param input imsNum	必选	String	IMS码号
								status	必选	String	0暂停
								type	必选	String	传固定值ims

	     * @param:@return 		code	必选	String	0失败，1成功
	     * @param:@throws Exception
	     * @return IDataset
	     * @throws Exception 
	     * @throws
	     * @Author :wangsc10
	     * @date :2018-9-20下午06:19:39
	     */
	    public IData stopAccount(IData input) throws Exception
	    {
	    	IData resultJson = new DataMap();
	    	IData data = new DataMap();
	    	String serialNumberB = input.getString("imsNum");
	    	
	    	//公共参数构造 写死的--吴坚
			getVisit().setStaffId("ITFZYC35");
			getVisit().setDepartId("00300");
			getVisit().setCityCode("HNHK");
			getVisit().setLoginEparchyCode("0898");
			getVisit().setStaffEparchyCode("0898");
	    		
    		data.put(Route.ROUTE_EPARCHY_CODE, "0898");
    		data.put("EPARCHY_CODE", "0898");
	    	if (StringUtils.isNotEmpty(serialNumberB))
	        {
	    		data.put("SERIAL_NUMBER", serialNumberB);
	        }
	    	try {
		        //通过固话号码获取手机号码
				IDataset ImsInfo = CSAppCall.call("SS.ChangeSvcStateSVC.getSerialNumberInfoByIMSInfo", data);
				if(IDataUtil.isEmpty(ImsInfo))
				{
					throw new Exception("该用户未办理IMS固话业务！");
				}else {
					data.put("WIDE_SERIAL_NUMBER", ImsInfo.first().getString("SERIAL_NUMBER_B"));
					data.put("SERIAL_NUMBER", serialNumberB);
				}
				
				String status = input.getString("status");
	        	if("0".equals(status)){
	        		data.put("TRADE_TYPE_CODE", "6805");
	        	}
        		CSAppCall.call("SS.IMSDestroyRegSVC.tradeReg", data);
				resultJson.put("code", "1");
				resultJson.put("resultCode", "000000");
		    	resultJson.put("desc", "操作成功");
			} catch (Exception e) {
				resultJson.put("code", "0");
				resultJson.put("resultCode", "000001");
		    	resultJson.put("desc", "退订业务:"+e.getMessage());
			}
			return resultJson;
	    	
	    }
	    /**
	     * 用于1个手机号，能开通多个固话号码的省份，如果1个手机号只有1个固话号码，直接复用反向开通流程中的接口“3.	查询已开通的账号信息”
	     * @Description：查询固话号码
	     * @param:phone	  必选	String	开通和家固话的手机号
				  type	   可选	String	传固定值ims

	     * @return imsNum     必选	Array	固话号码
	     * @throws Exception 

	     * @throws
	     * @Author :wangsc10
	     * @date :2018-11-22
	     */
	    public IData queryOpenIMSAccountInfos(IData input) throws Exception {
	    	IData resultJson = new DataMap();
	    	String sn =  input.getString("phone");
    		IData data = UcaInfoQry.qryUserInfoBySn(sn);
    		IDataset dataset = RelaUUInfoQry.getEnableRelationUusByUserIdBTypeCode(data.getString("USER_ID"), "MS");
    		if(IDataUtil.isNotEmpty(dataset)){
    			IDataset dataset1 = RelaUUInfoQry.getSEL_USER_ROLEA(dataset.first().getString("USER_ID_A"), "2", "MS");
    			if(IDataUtil.isEmpty(dataset1)){
    				throw new Exception("用户不存在有效的IMS固话号码！");
    			}
    			IDataset resultList = new DatasetList();
    			
    			if(IDataUtil.isNotEmpty(dataset1)){
    				for (int i = 0; i < dataset1.size(); i++){
    					IData ele = dataset1.getData(i);
    					resultList.add(ele.getString("SERIAL_NUMBER_B"));
    				}
    			}
	    		resultJson.put("imsNum", resultList);
	    		resultJson.put("resultCode", "000000");
		    	resultJson.put("desc", "操作成功");
    		}else{
    			resultJson.put("resultCode", "000001");
		    	resultJson.put("desc", "查询不到用户的固话号码。");
    		}
			return resultJson;
		}
	 
	    private static final String PRIVATEKEY = "f2fsdx5g9nm4bctz";//海南
	    private static final String PROVINCE = "46";//海南
	    public IData openIMSAccountInfos(IData input) throws Exception {
	    	//从家庭IMS开户（新）页面传过来的的复选框如果选择了杭研软终端开户才需要走这个Action
	        String phone = input.getString("RSRV_STR1","");//手机号码
	        String serialNumber = input.getString("SERIAL_NUMBER","");//固话码号
	        //String tradeId = input.getString("TRADE_ID");
	        String password = input.getString("PASS_WORD");
	      
	    	String url = "https://devhjgh.komect.com:30443/fy/receiver/api/public/ims/synData/v2";
	    	String bizType = input.getString("BIZ_TYPE");//必选	String	1 开户2 销户3 修改密码
	    	String province = "";//必选	String	省码46
	    	String sign = "";//必选	String	签名
	    	String content = "";//必选	String	业务参数（加密）
	    	
	    	String region = "";//可选	String	区号，不带0
	    	String msisdn = phone;//必选	String	手机号
	    	String idcard = "";//可选	String	身份证
	    	String broadband = "KD_" + phone;//必选	String	宽带账号
	    	String imsNum = serialNumber;//必选	String	ims码号
	    	String imsAccount = serialNumber;//必选	String	Ims账号
	    	//String password = TradeWideNetInfoQry.queryTradeWideNet(tradeId).first().getString("ACCT_PASSWD");//获取明文密码  必选	String	ims密码，bizType为 1002 销户时可不填
	    	String comboId = "84018046";//必选	String	云固话套餐id，用户前台展示套餐介绍、资费等信息（套餐介绍、资费等可由线下提供），bizType为 1002 销户、1003 修改密码时可不填
	    	String type = "ims";//可选	String	固定填：ims

	    	JSONObject jsonObject = new JSONObject();
	    	jsonObject.put("region", region);
	    	jsonObject.put("msisdn", msisdn);
	    	jsonObject.put("idcard", idcard);
	    	jsonObject.put("broadband", broadband);
	    	jsonObject.put("imsNum", imsNum);
	    	jsonObject.put("imsAccount", imsAccount);
	        jsonObject.put("password", RSAUtil.encrypt(password));
	        jsonObject.put("comboId", comboId);
	        jsonObject.put("type", type);//固定值
	        String jsonString = jsonObject.toJSONString();
	         
	        String paramContent = enCiper(PRIVATEKEY, jsonString);//aes 加密
	        //进行md5加密生成sign
	        String baowen = new StringBuilder()
	                .append("bizType").append(bizType)
	                .append("content").append(paramContent)
	                .append("province").append(PROVINCE)
	                .append(PRIVATEKEY)
	                .toString();

	        String signString = DigestUtils.md5Hex(getContentBytes(baowen, "utf-8"));

	        IData postBody = new  DataMap();
	        postBody.put("bizType", bizType);
	        postBody.put("province", PROVINCE);
	        postBody.put("content", paramContent);
	        postBody.put("sign", signString);
	        System.out.println("WANGSC:");
	        System.out.println(postBody.toString());
	        
	        imsToAbility(postBody);//调用一级能力平台，同步家庭IMS开户信息到杭研
			return postBody;
		}
	    
	    
	    private void imsToAbility(IData data) throws Exception {
			
			IData paramurl = new DataMap();
	        paramurl.put("PARAM_NAME", "crm.ABILITY.TORZD");
	        IDataset urls = Dao.qryBySql(AbilityEncrypting.getInterFaceSQL, paramurl, "cen");
	        String url = "";
	        
	        if (urls != null && urls.size() > 0)
	        {
	           url = urls.getData(0).getString("PARAM_VALUE", "");
	        }
	        else
	        {
	           CSAppException.appError("-1", "软终端同步接口地址未在TD_S_BIZENV表中配置");
	        }
	        
	        String apiAddress = url;
			AbilityEncrypting.callAbilityPlatCommon(apiAddress,data);
		}
	 
	    private static byte[] getContentBytes(String content, String charset) {
	        if (charset == null || "".equals(charset)) {
	            return content.getBytes();
	        }
	        try {
	            return content.getBytes(charset);
	        } catch (UnsupportedEncodingException e) {
	            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
	        }
	    }


	    private static String enCiper(String key, String data) throws Exception {
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        byte[] raw = key.getBytes("utf-8");
	        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
	        byte[] original = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
	        return new Base64().encodeAsString(original);
	    }
	    
		/**
	     * @Description：REQ201903010002+关于掌厅与网厅办理家庭IMS固话的需求
	     * @Author :wangsc10
	     * @date :2019-4-16
	     */
	    public IData openElectricCanalAccount(IData input) throws Exception
	    {
	    	IData resultJson = new DataMap();
	    	IData data = new DataMap();
			String staffId = IDataUtil.chkParam(input, "STAFF_ID"); //必选 受理员工
			String departId = IDataUtil.chkParam(input, "DEPART_ID"); //必选 受理渠道
			String cityCode = IDataUtil.chkParam(input, "CITY_CODE"); //必选 受理业务区
			getVisit().setStaffId(staffId);
			getVisit().setDepartId(departId);
			getVisit().setCityCode(cityCode);
			getVisit().setLoginEparchyCode("0898");
			getVisit().setStaffEparchyCode("0898");
			
	    	String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");//必选 用户手机号
	    	String wideSerialNumber = IDataUtil.chkParam(input, "WIDE_SERIAL_NUMBE");//必选  选择的固话号码
	    	String productId = IDataUtil.chkParam(input, "PRODUCT_ID");//必选 套餐id
	    	String kdCityCode = IDataUtil.chkParam(input, "KD_CITY_CODE");//必选 宽带市县
	    	try {
	    		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
	    		if(IDataUtil.isNotEmpty(userInfo)){
	    			String USER_STATE_CODESET = userInfo.getString("USER_STATE_CODESET");
	    			if(null != USER_STATE_CODESET && !"0".equals(USER_STATE_CODESET)){
	    				throw new Exception("用户的号码非正常状态，无法办理！");
	    			}
	    		}else{
	    			throw new Exception("查询不到用户信息！");
	    		}
	    		
	            IData checkAuthSerialNumdata = new DataMap();
	            checkAuthSerialNumdata.put("AUTH_SERIAL_NUMBER", serialNumber);
	            IDataset IData = CSAppCall.call("SS.IMSLandLineSVC.checkAuthSerialNum", checkAuthSerialNumdata);
	            if(IData != null && IData.size() > 0){
	            	String RESULT_CODE = IData.getData(0).getString("RESULT_CODE","");
	            	String RESULT_INFO = IData.getData(0).getString("RESULT_INFO","");
	            	if(RESULT_CODE.equals("0")){
	            		throw new Exception(RESULT_INFO);
	            	}
	            }
	            
		    	//查出手机号码开的宽带地址，业务归属区
		    	IDataset dataset1 = RelaUUInfoQry.qrySerialNumberBySnBAndRelationType(serialNumber,"47");
		    	if(IDataUtil.isNotEmpty(dataset1)){
		    		String userIdA = dataset1.first().getString("USER_ID_A"); 
		    		IDataset dataset2 = RelaUUInfoQry.getRelationsByUserIdA("47", userIdA, "2");
	    			if("2".equals(dataset2.first().getString("ROLE_CODE_B"))){
	    				String user_id_b = dataset2.first().getString("USER_ID_B");
	    				IDataset dataset3 = WidenetInfoQry.getUserWidenetInfo(user_id_b);
	    				String citycode = dataset3.first().getString("RSRV_STR4");
	    				if(!citycode.equals(kdCityCode)){
	    					throw new Exception("用户手机号码所办理宽带的业务归属区和实际的业务归属区不相符！");
	    				}
	    			}
		    	}else{
		    		throw new Exception("查询不到用户宽带信息！");
		    	}
		    	
	    		//选占
	    		IData userData = new DataMap();
	            userData.put("FIX_NUMBER", wideSerialNumber);
	            userData.put("CITYCODE_RSRVSTR4", kdCityCode);
	            
	            IDataset IDatacheckFixPhoneNum = CSAppCall.call("SS.IMSLandLineSVC.checkFixPhoneNum", userData);
	            if(IDatacheckFixPhoneNum != null && IDatacheckFixPhoneNum.size() > 0){
	            	String RESULT_CODE = IDatacheckFixPhoneNum.getData(0).getString("RESULT_CODE","");
	            	String RESULT_INFO = IDatacheckFixPhoneNum.getData(0).getString("RESULT_INFO","");
	            	if(RESULT_CODE.equals("-1")){
	            		throw new Exception(RESULT_INFO);
	            	}
	            }
	            
	            String MO_PRODUCT_ID = input.getString("MO_PRODUCT_ID","");//营销活动产品ID
	            String MO_PACKAGE_ID = input.getString("MO_PACKAGE_ID","");//营销活动包ID
	            if((MO_PRODUCT_ID != null && !"".equals(MO_PRODUCT_ID)) && (MO_PACKAGE_ID != null && !"".equals(MO_PACKAGE_ID))){
	            	//IMS固话产品与IMS营销活动规则校验
//	            	IData checkSaleActiveAndProductdata = new DataMap();
//	            	checkSaleActiveAndProductdata.put("PRODUCT_ID", productId);
//	            	IDataset checkSaleActiveAndProductresult = CSAppCall.call("SS.IMSLandLineSVC.checkSaleActiveAndProduct", checkSaleActiveAndProductdata);
//	            	
//	            	if(IDataUtil.isNotEmpty(checkSaleActiveAndProductresult))
//	                {
//	            		String paraCode1 = checkSaleActiveAndProductresult.getData(0).getString("PARA_CODE1", "");
//	            		String activeNames = checkSaleActiveAndProductresult.getData(0).getString("PARA_CODE3", "");
//	            		if(!MO_PACKAGE_ID.equals(paraCode1)){
//	            			throw new Exception("该固话产品必须选择如下IMS营销活动:【"+activeNames+"】");
//                		}
//	                }
	            	//营销活动校验
	            	IData checkSaleActivedata = new DataMap();
	            	checkSaleActivedata.put("SERIAL_NUMBER", serialNumber);
	            	checkSaleActivedata.put("PRODUCT_ID", MO_PRODUCT_ID);
	            	checkSaleActivedata.put("PACKAGE_ID", MO_PACKAGE_ID);
	            	IDataset checkSaleActiveresult = CSAppCall.call("SS.IMSLandLineSVC.checkSaleActive", checkSaleActivedata);
	            	if(checkSaleActiveresult != null && IDataUtil.isNotEmpty(checkSaleActiveresult)){
	            		
			        }else{
			        	throw new Exception("家庭IMS固话开户营销活动校验失败！");
			        }
	            	//魔百和宽带营销活动费用校验
	            	IData queryCheckSaleActiveFeedata = new DataMap();
	            	queryCheckSaleActiveFeedata.put("SERIAL_NUMBER", serialNumber);
	            	queryCheckSaleActiveFeedata.put("PRODUCT_ID", MO_PRODUCT_ID);
	            	queryCheckSaleActiveFeedata.put("PACKAGE_ID", MO_PACKAGE_ID);
	            	queryCheckSaleActiveFeedata.put("ACTIVE_FLAG","2");
	            	IDataset queryCheckSaleActiveFeeresult = CSAppCall.call("SS.IMSLandLineSVC.queryCheckSaleActiveFee", queryCheckSaleActiveFeedata);
	            	String SALE_ACTIVE_FEE = "0";
	            	if(queryCheckSaleActiveFeeresult != null && IDataUtil.isNotEmpty(queryCheckSaleActiveFeeresult)){
	            		//String X_RESULTCODE = queryCheckSaleActiveFeeresult.first().getString("X_RESULTCODE");
	            		SALE_ACTIVE_FEE = queryCheckSaleActiveFeeresult.first().getString("SALE_ACTIVE_FEE");
			        }else{
			        	throw new Exception("营销活动费用校验失败！");
			        }
	            	
	            	//提交前费用校验
	            	IData checkFeeBeforeSubmitdata = new DataMap();
	            	checkFeeBeforeSubmitdata.put("SERIAL_NUMBER", serialNumber);
	            	checkFeeBeforeSubmitdata.put("TOPSETBOX_SALE_ACTIVE_FEE", SALE_ACTIVE_FEE);
	            	IDataset checkFeeBeforeSubmitresult = CSAppCall.call("SS.IMSLandLineSVC.checkFeeBeforeSubmit", checkFeeBeforeSubmitdata);
	            	if(checkFeeBeforeSubmitresult != null && IDataUtil.isNotEmpty(checkFeeBeforeSubmitresult)){
	            		String X_RESULTCODE = checkFeeBeforeSubmitresult.first().getString("X_RESULTCODE");
			        }else{
			        	throw new Exception("您的账户存折可用余额不足["+SALE_ACTIVE_FEE+"]，请先办理缴费。");
			        }
	            	//计算营销活动费用
		            String ImsSaleActiveFee = fee(MO_PRODUCT_ID,MO_PACKAGE_ID,serialNumber);
		        	//IMS固话营销活动产品ID
		            data.put("MO_PRODUCT_ID", MO_PRODUCT_ID);
		        	//IMS固话营销活动包ID
		            data.put("MO_PACKAGE_ID", MO_PACKAGE_ID);
		        	//IMS固话营销活动预存费用
		            data.put("TOP_SET_BOX_SALE_ACTIVE_FEE", ImsSaleActiveFee);
	            }
	            
	            IDataset productElements = ProductElementsCache.getProductElements(productId);//根据product_id获取该产品下的所有元素
	            //开户
	        	data.put("SERIAL_NUMBER", serialNumber);
		        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
		        data.put("WIDE_SERIAL_NUMBER",wideSerialNumber);
		        data.put("PRODUCT_ID", productId);
		        data.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
		        data.put("SELECTED_ELEMENTS", productElements);
		        data.put("ZTWT_OPEN_IMS", "ZTWT_OPEN_IMS");
		        
		        IDataset result = CSAppCall.call("SS.IMSLandLineRegSVC.tradeReg", data);
		        if(result != null && IDataUtil.isNotEmpty(result)){
		        	String trade_id = result.first().getString("TRADE_ID");
		        	resultJson.put("TRADE_ID", trade_id);//订单编码
		    		resultJson.put("RESULTCODE", "0");
			    	resultJson.put("DESC", "操作成功");
		        }else{
		        	throw new Exception("开户失败！");
		        }
			} catch (Exception e) {
				resultJson.put("RESULTCODE", "1");
		    	resultJson.put("DESC", "掌厅与网厅开通业务:"+e.getMessage());
			}
			return resultJson;
	    }
	    
	    /**
	     * 营销活动费用计算
	     * */
		public String fee (String productId , String packageId, String serialNumber) throws Exception
		{
			IData feeData = new DataMap();
			String SaleActiveFee = "";
			feeData.put("SERIAL_NUMBER", serialNumber);
	        feeData.put("PACKAGE_ID", packageId);
	        feeData.put("PRODUCT_ID", productId);
	        IData SaleActiveFeeData = CSAppCall.callOne("SS.MergeWideUserCreateSVC.queryCheckSaleActiveFee", feeData);
	        if(IDataUtil.isNotEmpty(SaleActiveFeeData))
	        {
	        	SaleActiveFee = SaleActiveFeeData.getString("SALE_ACTIVE_FEE");
	        }
	        
	        return SaleActiveFee ;
		}
}
