package com.asiainfo.veris.crm.order.soa.person.busi.cmonline.selfterminal;

import java.util.List;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityPlatBean;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityPlatOrderBean;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.FamilyOperPreBean;

/**
 * 新型自助终端查询类接口
 * @author Administrator
 *
 */
public class SelfTerminalQrySVC extends CSBizService{
	/**
	 * 实名制查询
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData checkUserRealName(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		String serialNumber=input.getString("serviceNum");
		IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isNotEmpty(userInfo)){
			String custId=userInfo.getString("CUST_ID", "");
            IData custInfo=UcaInfoQry.qryCustomerInfoByCustId(custId);
            if(IDataUtil.isNotEmpty(custInfo)){
                //实名制标识
                String isRealName=custInfo.getString("IS_REAL_NAME", "");
                if("1".equals(isRealName)){
                    output.put("checkResult", "01"); 
                    output.put("checkMsg", "实名制"); 
                }else{
                	output.put("checkResult", "Y"); 
                	output.put("checkMsg", "可以实名制"); 
                }
                return SelfTerminalUtil.responseSuccess(output);
            }else{
            	return SelfTerminalUtil.responseFail("客户资料不存在",null);
            }
		}else{
			return SelfTerminalUtil.responseFail("用户资料不存在",null);
		}
		
	}
	/**
	 * 停开机状态查询接口
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData qryUserStatus(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		output.put("oprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceType");
			IDataUtil.chkParam(input, "serviceNum");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		String serialNumber=input.getString("serviceNum");
		IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
		AbilityPlatBean apBean=BeanManager.createBean(AbilityPlatBean.class);
		if(IDataUtil.isEmpty(userInfo)){
			IData users=apBean.queryAllUserInfo(serialNumber);//所有状态的号码
			if(IDataUtil.isNotEmpty(users)){
				String removaTag=users.getString("REMOVE_TAG");
        		if("1".equals(removaTag)||"3".equals(removaTag)){//预销号
        			output.put("status", "03");
    				output.put("statusDesc", "预销户");
        		}else if("2".equals(removaTag)||"4".equals(removaTag)||"5".equals(removaTag)||"6".equals(removaTag)){
        			output.put("status", "99");
    				output.put("statusDesc", "此号码不存在");
        		}       		
			}else{
				output.put("status", "99");
				output.put("statusDesc", "此号码不存在");
			}
		}else{
			String stauts = userInfo.getString("USER_STATE_CODESET");
        	String acctTag= userInfo.getString("ACCT_TAG");
        	if("0".equals(stauts))
        	{
        		if("2".equals(acctTag)){
        			return SelfTerminalUtil.responseFail("用户未激活",null);
        		}else{
        			output.put("status", "00");
    				//output.put("statusDesc", "正常开机状态");
        		}        		 
        	}else{
        		output.put("status", SelfTerminalUtil.getUserStatus(stauts));
				output.put("statusDesc", SelfTerminalUtil.getUserStatusName(stauts));
        	}
			
		}
		return SelfTerminalUtil.responseSuccess(output);
	}
	/**
	 * 家庭网查询接口
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData qryFamilyNet(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		output.put("oprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "channelType");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}

		String serialNumber=input.getString("serviceNum");
		String channelType=input.getString("channelType");
		String homeNetQryReq=input.getString("homeNetQryReq");//用户身份凭证
		String productId=input.getString("productId");
		
		IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isEmpty(userInfo)){
			output.put("bizCode", "2998");
			return SelfTerminalUtil.responseFail("用户资料不存在",output);
		}
		
		//查询该号码是否开通了家庭网
		String user_id = userInfo.getString("USER_ID");
		IDataset family = checkFamily(user_id);
		if(IDataUtil.isEmpty(family)){
			output.put("status", "0");
			output.put("masterPhoneNO", serialNumber);
			return SelfTerminalUtil.responseFail("用户未开通家庭网",output);
		}
		String role_code = family.getData(0).getString("ROLE_CODE_B");
		if("1".equals(role_code)){
			//该卡为主卡
			output.put("status", "1");
			return mainCardSearch(serialNumber,output);
			
		}else if("2".equals(role_code)){
			//该卡为副卡
			output.put("status", "3");
			return viceCardSearch(serialNumber,output);
			
		}else{
			return SelfTerminalUtil.responseFail("用户家庭网数据异常",null);
		}
	}
	//主卡查询
	private IData mainCardSearch(String serial_number,IData returnData) throws Exception{
		IData main = UcaInfoQry.qryUserInfoBySn(serial_number);
		String user_id = main.getString("USER_ID");
		//获取家庭网信息
		IData mainFamily = checkFamily(user_id).getData(0);
		String user_id_a = mainFamily.getString("USER_ID_A");
		//获取产品信息
		IDataset product = FamilyOperPreBean.getUserProduct(user_id_a);
		if(IDataUtil.isEmpty(product)){
			return SelfTerminalUtil.responseFail("查无家庭网产品",null);
		}
		String product_id = product.getData(0).getString("PRODUCT_ID");
		/*IDataset discntInfos = CommparaInfoQry.getCommNetInfo("CSM", "5", "VPCN");
		if(!product_id.equals(discntInfos.getData(0).getString("PARA_CODE1"))){
			return SelfTerminalUtil.responseFail("产品信息有误",null);
		}
		String mainDis = discntInfos.getData(0).getString("PARA_CODE2");
		String product_name = DiscntInfoQry.getDiscntInfoByDisCode(mainDis).getData(0).getString("DISCNT_NAME");
		int size = product_name.length();
		String price = product_name.substring(size-3, size-1)+"/月";*/
		IDataset discntList = UpcCall.queryMebOffersByTopOfferIdRole(product_id, "1","D");
		UcaData mebUca = UcaDataFactory.getNormalUca(serial_number);
		// 处理优惠
		boolean isOld=false;
		List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
		for (int j = 0; j < userDiscntList.size(); j++) {
			DiscntTradeData userDiscnt = userDiscntList.get(j);
			if ("3403".equals(userDiscnt.getDiscntCode())) {
				isOld = true;
				break;
			}
		}
		IDataset tmpist1 = new DatasetList();
		tmpist1.addAll(discntList);
		if(isOld){
			for (Object a : tmpist1) {
				IData data = (IData) a;
				if (!"3403".equals(data.getString("DISCNT_CODE"))) {
					discntList.remove(a);
				}
			}
		}else{
			for (Object a : tmpist1) {
				IData data = (IData) a;
				if ("3403".equals(data.getString("DISCNT_CODE"))) {
					discntList.remove(a);
				}
			}
		}
		String product_name = DiscntInfoQry.getDiscntInfoByDisCode(discntList.getData(0).getString("DISCNT_CODE")).getData(0).getString("DISCNT_NAME");
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
				allInfo.put("masterPhoneNO", serial_number);
				allInfo.put("masterShortNO", short_code);
				IDataset res = new DatasetList();
				allInfo.put("resourcesInfoList", res);
				IDataset markInfos = new DatasetList();
				allInfo.put("marketIDInfoList", markInfos);
				String start_date = realInfo.getString("START_DATE");
				String end_date = realInfo.getString("END_DATE");
				allInfo.put("orderTime", SysDateMgr.getDateForYYYYMMDD(start_date));
				allInfo.put("validDate", SysDateMgr.getDateForYYYYMMDD(start_date));
				allInfo.put("expireDate", SysDateMgr.getDateForYYYYMMDD(end_date));
				allInfo.put("feeType", "包月");
			}else if("2".equals(role_code_b)){
				//副号只查有效的
				IDataset userInfo=UserInfoQry.selUserInfo(realInfo.getString("USER_ID_B","USER_ID_B"));
				if(IDataUtil.isNotEmpty(userInfo)
						&&(!("0".equals(userInfo.getData(0).getString("REMOVE_TAG"))))){
						continue;
				}
				IData memberInfo = new DataMap();
			    IData vice = new DataMap();
				String short_code = realInfo.getString("SHORT_CODE");
				String serial_number_b = realInfo.getString("SERIAL_NUMBER_B");
				vice.put("memberPhoneNo", serial_number_b);
				vice.put("memberShortNo", short_code);
				vice.put("memberStatus", "3");
				vice.put("resourcesInfoList", new DatasetList());
				memberInfo.put("memberInfo", vice);
				
				viceInfos.add(memberInfo);
			}
			
		}
		allInfo.put("memberInfoList", viceInfos);
		allInfo.put("productId", product_id);
		allInfo.put("busiName", product_name);
		allInfo.put("busiFee", price);		
		
		returnData.putAll(allInfo);
		
		return SelfTerminalUtil.responseSuccess(returnData);
	}
	//副卡查询
	private IData viceCardSearch(String serial_number,IData returnData) throws Exception{
		IData main = UcaInfoQry.qryUserInfoBySn(serial_number);
		String user_id = main.getString("USER_ID");
		//获取家庭网信息
		IData mainFamily = checkFamily(user_id).getData(0);
		String user_id_a = mainFamily.getString("USER_ID_A");
		//获取产品信息
		IDataset product = FamilyOperPreBean.getUserProduct(user_id_a);
		if(IDataUtil.isEmpty(product)){
			return SelfTerminalUtil.responseFail("查无家庭网产品",null);
		}
		String product_id = product.getData(0).getString("PRODUCT_ID");
		/*IDataset discntInfos = CommparaInfoQry.getCommNetInfo("CSM", "5", "VPCN");
		if(!product_id.equals(discntInfos.getData(0).getString("PARA_CODE1"))){
			return SelfTerminalUtil.responseFail("产品信息有误",null);
		}
		String mainDis = discntInfos.getData(0).getString("PARA_CODE2");
		String product_name = DiscntInfoQry.getDiscntInfoByDisCode(mainDis).getData(0).getString("DISCNT_NAME");
		int size = product_name.length();
		String price = product_name.substring(size-3, size-1)+"/月";*/
		String product_name="";
		String price="";
		
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
				allInfo.put("masterPhoneNO", serial_number_b);
				allInfo.put("masterShortNO", short_code);
				IDataset res = new DatasetList();
				allInfo.put("resourcesInfoList", res);
				IDataset markInfos = new DatasetList();
				allInfo.put("marketIDInfoList", markInfos);
				String start_date = realInfo.getString("START_DATE");
				String end_date = realInfo.getString("END_DATE");
				allInfo.put("orderTime", SysDateMgr.getDateForYYYYMMDD(start_date));
				allInfo.put("validDate", SysDateMgr.getDateForYYYYMMDD(start_date));
				allInfo.put("expireDate", SysDateMgr.getDateForYYYYMMDD(end_date));
				allInfo.put("feeType", "包月");
				
				//主卡产品放在这里查询================
				IDataset discntList = UpcCall.queryMebOffersByTopOfferIdRole(product_id, "1","D");
				UcaData mebUca = UcaDataFactory.getNormalUca(serial_number_b);
				// 处理优惠
				boolean isOld=false;
				List<DiscntTradeData> userDiscntList = mebUca.getUserDiscnts();
				for (int j = 0; j < userDiscntList.size(); j++) {
					DiscntTradeData userDiscnt = userDiscntList.get(j);
					if ("3403".equals(userDiscnt.getDiscntCode())) {
						isOld = true;
						break;
					}
				}
				IDataset tmpist1 = new DatasetList();
				tmpist1.addAll(discntList);
				if(isOld){
					for (Object a : tmpist1) {
						IData data = (IData) a;
						if (!"3403".equals(data.getString("DISCNT_CODE"))) {
							discntList.remove(a);
						}
					}
				}else{
					for (Object a : tmpist1) {
						IData data = (IData) a;
						if ("3403".equals(data.getString("DISCNT_CODE"))) {
							discntList.remove(a);
						}
					}
				}
				product_name = DiscntInfoQry.getDiscntInfoByDisCode(discntList.getData(0).getString("DISCNT_CODE")).getData(0).getString("DISCNT_NAME");
				int size = product_name.length();
				price = product_name.substring(size-3, size-1)+"/月";
				//主卡产品放在这里查询================
				
			}else if(serial_number.equals(serial_number_b)){
				String short_code = realInfo.getString("SHORT_CODE");
				vice.put("memberPhoneNo", serial_number_b);
				vice.put("memberShortNo", short_code);
				vice.put("memberStatus", "3");
				viceInfos.add(vice);
			}
		}
		allInfo.put("memberInfoList", viceInfos);
		allInfo.put("productId", product_id);
		allInfo.put("busiName", product_name);
		allInfo.put("busiFee", price);		
		
		returnData.putAll(allInfo);
		
		return SelfTerminalUtil.responseSuccess(returnData);
	}
	//家庭网查询
	private IDataset checkFamily(String userId) throws Exception{
		IData checkFamily = new DataMap();
		checkFamily.put("USER_ID_B", userId);
		checkFamily.put("RELATION_TYPE_CODE", "45");
		return FamilyOperPreBean.getUserRelationByUserIdB(checkFamily);
	}
	/**
	 * 家庭网产品查询
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData qryFamilyNetProduct(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceNum");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		String serialNumber=input.getString("serviceNum");
		
		IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
		if(IDataUtil.isEmpty(userInfo)){
			return SelfTerminalUtil.responseFail("用户资料不存在",null);
		}
		//查询该号码是否开通了家庭网
		String user_id = userInfo.getString("USER_ID");
		IDataset family = checkFamily(user_id);
		if(IDataUtil.isEmpty(family)){
			return SelfTerminalUtil.responseFail("用户没有开通家庭网业务",null);
		}
		String user_id_a = family.getData(0).getString("USER_ID_A");
		//获取产品信息
		IDataset product = FamilyOperPreBean.getUserProduct(user_id_a);
		if(IDataUtil.isEmpty(product)){
			return SelfTerminalUtil.responseFail("查无家庭网产品",null);
		}
		String product_id = product.getData(0).getString("PRODUCT_ID");
		IDataset discntInfos = CommparaInfoQry.getCommNetInfo("CSM", "5", "VPCN");
		if(!product_id.equals(discntInfos.getData(0).getString("PARA_CODE1"))){
			return SelfTerminalUtil.responseFail("产品信息有误",null);
		}
		String mainDis = discntInfos.getData(0).getString("PARA_CODE2");
		String product_name = DiscntInfoQry.getDiscntInfoByDisCode(mainDis).getData(0).getString("DISCNT_NAME");
		
		//通过主副卡来判断产品名称
		String role_code = family.getData(0).getString("ROLE_CODE_B");
		if("1".equals(role_code)){
			product_name="亲亲网套餐(主号码)";
		}else if("2".equals(role_code)){
			product_name="亲亲网套餐(副号码)";
		}
		
		IDataset productList=new DatasetList();
		IData productData=new DataMap();
		productData.put("productId", product_id);
		productData.put("busiName", product_name);
		productList.add(productData);
		output.put("productList", productList);
		
		return SelfTerminalUtil.responseSuccess(output);
	}
	/**
	 * 已订购业务查询接口
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public IData qryOrderedSvc(IData inParam) throws Exception {
		IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		output.put("oprTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		//非空判断
		try{
			IDataUtil.chkParam(input, "serviceType");
			IDataUtil.chkParam(input, "serviceNum");
			IDataUtil.chkParam(input, "busiType");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		String serviceType=input.getString("serviceType");
		String serviceNum=input.getString("serviceNum");
		String busiType=input.getString("busiType");
		
		if(!("01".equals(input.getString("serviceType")))){
			return SelfTerminalUtil.responseFail("暂不支持serviceType="+serviceType,null);
		}
		IData userInfo=UcaInfoQry.qryUserInfoBySn(serviceNum);
		if(IDataUtil.isEmpty(userInfo)){
			return SelfTerminalUtil.responseFail("用户资料不存在",null);
		}
		
		IDataset bizInfoList=new DatasetList();
		output.put("bizInfoList", bizInfoList);
		IData data=new DataMap();
		IDataset infos=new DatasetList();
		if("01".equals(busiType)){//套餐类
			data.put("productType", "01");
			infos=this.getDicntSvc(userInfo.getString("USER_ID"));
		}else if("02".equals(busiType)){//增值业务类
			data.put("productType", "02");
			infos=this.getPlatSvc(userInfo.getString("USER_ID"),userInfo.getString("EPARCHY_CODE"));
		}else if("03".equals(busiType)){//服务功能类
			data.put("productType", "03");
			infos=this.getNormalSvc(userInfo.getString("USER_ID"));
		}else if("04".equals(busiType)){//营销活动等其他类
			data.put("productType", "04");
			infos=this.getActiveSvc(userInfo.getString("USER_ID"));
		}
		data.put("productInfo", infos);
		bizInfoList.add(data);
		return SelfTerminalUtil.responseSuccess(output);
	}
	/**
    * 套餐类
    * @param userId
    * @return
    * @throws Exception
    */
   private IDataset getDicntSvc(String userId) throws Exception{
	   IDataset productInfoList=new DatasetList();
  	   IDataset queryInfos = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
  	   if(IDataUtil.isNotEmpty(queryInfos)){
    	  for(int i=0;i<queryInfos.size();i++ ){
    		 IData data=new DataMap();
     		 IDataset productList = new DatasetList();
     		 IData result=new DataMap();	        		
     		 IData info=queryInfos.getData(i);
     		 result.put("productEffDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));//生效时间
    		 result.put("productExpDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("END_DATE")));//失效时间
    		 String discntCode=info.getString("DISCNT_CODE","");
    		 OfferCfg offercfg = OfferCfg.getInstance(discntCode, BofConst.ELEMENT_TYPE_CODE_DISCNT);
    		 if(offercfg != null){
        		 result.put("productId", discntCode);//产品编码
        		 result.put("productName", offercfg.getOfferName());//产品名称
        		 result.put("productDesc", offercfg.getDescription());//产品描述	        		 
    		 }
    		 productList.add(result);//产品名称PRODUCT_LIST
    		 data.put("validDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));	   
    		 data.put("expireDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("END_DATE")));
    		 data.put("productList", productList);
    		 productInfoList.add(data);//产品信息PRODUCT_INF
    	  }
    	  //进行排序操作
          if(!productInfoList.isEmpty()){
              DataHelper.sort(productInfoList, "validDate", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
             
          } 
  	   } 
  	   return productInfoList;
   }
   /**
    * 增值类
    * @param userId
    * @return
    * @throws Exception
    */
   private IDataset getPlatSvc(String userId,String eparchyCode) throws Exception{
	    IDataset ret=new DatasetList();	    
	   	IDataset queryInfos=UserPlatSvcInfoQry.queryPlatSvcInfoByUserIdForAbility(userId);
	   	if(IDataUtil.isNotEmpty(queryInfos)){
	   		for(int i=0;i<queryInfos.size();i++){	
	       		IData data=new DataMap();    			
	   			IData info=queryInfos.getData(i);
	   			String serviceId=info.getString("SERVICE_ID");
	   			IDataset mountOfferInfos = UpcCall.qryMountOfferByOfferId(serviceId,BofConst.ELEMENT_TYPE_CODE_PLATSVC);
	   			String productId = "";
	   			if(IDataUtil.isNotEmpty(mountOfferInfos)){
	   				productId = mountOfferInfos.getData(0).getString("PRODUCT_ID","");
	   			}
	   			if(StringUtils.isNotEmpty(productId)){
	   				data.put("bizType", "01");
	   				data.put("productCode", productId);
	   			}else{
	   				data.put("bizType", "02");
	   				data.put("spCode", info.getString("SP_CODE"));
		    			data.put("bizCode", info.getString("BIZ_CODE"));
	   			}    		
	   			data.put("validDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));	
	   			data.put("expireDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("END_DATE")));	
	
	      		ret.add(data);//产品信息PRODUCT_INF
	   		}
	       	//进行排序操作
            if(!ret.isEmpty()){
                DataHelper.sort(ret, "validDate", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
               
            } 
	   	}	    	
	   	return ret;
   }
   /**
    * 服务功能类
    * @param userId
    * @return
    * @throws Exception
    */
   private IDataset getNormalSvc(String userId) throws Exception{
	   	IDataset ret=new DatasetList();	 
	   	IDataset queryInfos=UserSvcInfoQry.queryUserAllSvcForAbility(userId);
	   	if(IDataUtil.isNotEmpty(queryInfos)){
	   		for(int i=0;i<queryInfos.size();i++){
	       		IData data=new DataMap();
	   			IData info=queryInfos.getData(i);
	   			data.put("servFunId", info.getString("SERVICE_ID"));
	   			data.put("servFunName", USvcInfoQry.getSvcNameBySvcId(info.getString("SERVICE_ID","")));
	   			data.put("validDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));
	   			data.put("expireDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("END_DATE")));
	      		ret.add(data);//产品信息PRODUCT_INF   			
	   		}	
	       	 //进行排序操作
            if(!ret.isEmpty()){
                DataHelper.sort(ret, "validDate", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
            } 
	   	}
	   	return ret;
   }
   /**
    * 营销活动类
    * @param userId
    * @return
    * @throws Exception
    */
   public IDataset getActiveSvc(String userId) throws Exception{
	   	IDataset ret=new DatasetList();	 
	   	IDataset queryInfos=UserSaleActiveInfoQry.queryUserSaleActiveByTag(userId);//.queryUserSaleActiveByUserId(userId);//正常状态下的
	   	if(IDataUtil.isNotEmpty(queryInfos)){
	   		IDataset dataset=new DatasetList();	 
	   		for(int i=0;i<queryInfos.size();i++){
	       		IData data=new DataMap();
	       		IData result=new DataMap();
	   			IData info=queryInfos.getData(i);
	   			result.put("actionId", info.getString("PRODUCT_ID"));//营销活动id
	   			result.put("actionName",info.getString("PRODUCT_NAME") + "-" + info.getString("PACKAGE_NAME"));//营销活动名称
	   			result.put("actionDesc", info.getString("PRODUCT_NAME"));//营销活动描述
	   			result.put("actionEffDate",Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));//营销活动生效时间
	   			result.put("actionExpDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("END_DATE")));//营销活动失效时间
	   			dataset.add(result);
	   			data.put("actionList", dataset);//营销活动列表
	   			data.put("validDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("START_DATE")));//生效时间
	   			data.put("expireDate", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, info.getString("END_DATE")));//生效时间
	      		ret.add(data);//产品信息PRODUCT_INF
	   		}
	       	//进行排序操作
            if(!ret.isEmpty()){
                DataHelper.sort(ret, "validDate", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
            } 
	   	}
	   	 
	   	return ret;
	}
   /**
    * 查询可选号码
    * @param userId
    * @return
    * @throws Exception
    */
    public IData qrySelectNum(IData inParam) throws Exception{
    	IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
	
		//非空判断
		try{
			IDataUtil.chkParam(input, "pageCount");
			IDataUtil.chkParam(input, "pageSize");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		String serviceNum=input.getString("serviceNumber");
		String offerId=input.getString("offerId");
		String segmentTag=input.getString("segmentTag");//号段
		String reserveFee=input.getString("reserveFee");//预存款
		String characterTag=input.getString("characterTag");//特殊标签
		String numMode=input.getString("numMode");//号码模式
		String containFour=input.getString("containFour");//是否包含4
		String isLuckyNum=input.getString("isLuckyNum");//是否吉祥号码
		String startNum=input.getString("startNum");//已什么开头
		String endNum=input.getString("endNum");//以什么结尾
		String pageNum=input.getString("pageCount");//当前页数
		String pageSize=input.getString("pageSize");//页数
		String areaCode=input.getString("areaCode");//地市
		
		IData params=new DataMap();
		params.put("IS_NEW_SELFTERM", "1");
		params.put("SEL_TAG", "1");
		params.put("CODE_AREA_CODE", segmentTag);
		if(StringUtils.isNotEmpty(serviceNum)){
			params.put("START_NUM", serviceNum);
		}
		/*1：预存款0-2W
		2：预存款2-5W
		3：预存款5-10W
		4：预存款10-20W
		5：预存款20-50W
		6：预存款50-100W
		7：预存款大于100W
		单位：分*/
		if("1".equals(reserveFee)){
			params.put("RESERVE_FEE_START", 0);
			params.put("RESERVE_FEE_END", 20000*100);
		}else if("2".equals(reserveFee)){
			params.put("RESERVE_FEE_START", 20000*100);
			params.put("RESERVE_FEE_END", 50000*100);
		}else if("3".equals(reserveFee)){
			params.put("RESERVE_FEE_START", 50000*100);
			params.put("RESERVE_FEE_END", 100000*100);
		}else if("4".equals(reserveFee)){
			params.put("RESERVE_FEE_START", 100000*100);
			params.put("RESERVE_FEE_END", 200000*100);
		}else if("5".equals(reserveFee)){
			params.put("RESERVE_FEE_START", 200000*100);
			params.put("RESERVE_FEE_END", 500000*100);
		}else if("6".equals(reserveFee)){
			params.put("RESERVE_FEE_START", 500000*100);
			params.put("RESERVE_FEE_END", 1000000*100);
		}else if("7".equals(reserveFee)){
			params.put("RESERVE_FEE_START", 1000000*100);
		}
		
		if(StringUtils.isNotEmpty(numMode)){
			if("88".equals(numMode)){
				params.put("SEL_PHONE_QUERY_TYPE", "4");
				params.put("PHONE_SPECIAL_NUMBERIC", numMode);
			}else{
				params.put("NUM_MODE", numMode);
			}
		}
		
		//0：不包含，1：包含
		if("0".equals(containFour)){
			params.put("PARAM_NAME_4", "2");
		}else if("1".equals(containFour)){
			params.put("PARAM_NAME_4", "1");
		}
		//1：是吉祥号
		//0：普通号码
		if("1".equals(isLuckyNum)){
			params.put("BEAUTIFUL_TAG", "1");
		}
		if(StringUtils.isNotEmpty(startNum)){
			params.put("START_NUM", startNum);
		}
		//以什么结尾
		if(StringUtils.isNotEmpty(endNum)){
			int length=endNum.length();
			int index=12;
			for(int i=length-1;i>=0;i--){
				char num=endNum.charAt(i);
				params.put("RES_NO_"+index, ""+num);
				index--;
			}
		}
		
		params.put("PAGE_NUM", pageNum);
		params.put("PAGE_SIZE", pageSize);
		params.put("MGMT_COUNTY", areaCode);
		
		IDataset datas= ResCall.queryNumInfo4AreaSel(params);
		IDataset outDatas=datas.getData(0).getDataset("OUTDATA");
		IDataset results=new DatasetList();
		if(IDataUtil.isNotEmpty(outDatas)){
			for(int i=0;i<outDatas.size();i++){
				IData data1=new DataMap();
				IData data=new DataMap();
				data.put("serviceNumber", outDatas.getData(i).getString("ACCESS_NUMBER",""));
				if("1".equals(outDatas.getData(i).getString("PRECODE_TAG"))){
					data.put("status", "0");
				}else{
					data.put("status", "2");
				}
				data.put("prdName", "");
				data.put("offerId", outDatas.getData(i).getString("OFFER_ID",""));
				data.put("offerName", "");
				data.put("costValue", outDatas.getData(i).getString("RESERVE_FEE",""));
				data.put("miniCharge", "");
				data.put("miniMonth", outDatas.getData(i).getString("DEPOSIT_MONTH",""));
				data1.put("assetDetailInfo", data);
				results.add(data1);
			}
			output.put("returnNumberContent", results);
			output.put("totalNumber", outDatas.getData(0).getString("PAGE_COUNT",results.size()+""));
		}else{
			output.put("totalNumber", "0");
		}
		//output.put("totalNumber", results.size()+"");
		return SelfTerminalUtil.responseSuccess(output);
    }
    /**
     * 获取受理单内容
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData qryElecWorkInfo(IData inParam) throws Exception{
    	IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
	
		//非空判断
		try{
			IDataUtil.chkParam(input, "transactionId");
			IDataUtil.chkParam(input, "phoneNo");
			//IDataUtil.chkParam(input, "busiCode");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),null);
		}
		
		SelfTerminalBean selfBean=BeanManager.createBean(SelfTerminalBean.class);
		IData result=selfBean.qrySelfLog(input);
		if(IDataUtil.isNotEmpty(result)){
			String tradeId=result.getString("CRM_TRADE_ID");
			IData params=new DataMap();
			params.put("TRADE_ID", tradeId);
			IDataset results=CSAppCall.call("SS.UipInfoSVC.getEFormInfo", params);
			if(IDataUtil.isNotEmpty(results)){
				
				IData userCheckInfo=selfBean.qryUserCheckInfo(input);
				String cardImg="";
				String handImg="";
				
				if(IDataUtil.isNotEmpty(userCheckInfo)){
					cardImg=userCheckInfo.getString("IDCARD_IMG");
					if(StringUtils.isNotEmpty(userCheckInfo.getString("USERCOLOR_PIC"))){
						handImg=userCheckInfo.getString("USERCOLOR_PIC");
					}else if(StringUtils.isNotEmpty(userCheckInfo.getString("USERGAY_PIC"))){
						handImg=userCheckInfo.getString("USERGAY_PIC");
					}
				}
				
				results.getData(0).put("cardImg", cardImg);
				results.getData(0).put("handImg", handImg);
				
				output.putAll(results.getData(0));
				return SelfTerminalUtil.responseSuccess(output);
			}else{
				return SelfTerminalUtil.responseFail("没有查询到受理单记录",null);
			}
		}else if("002".equals(input.getString("busiCode"))){
			return getElecWorkInfoByCrossChangeCard(input);
		}else{
			return SelfTerminalUtil.responseFail("没有查询到工单信息",null);
		}
    }
    /**
     * 获取跨区补换卡受理单信息
     * @return
     * @throws Exception
     */
    private IData getElecWorkInfoByCrossChangeCard(IData input)throws Exception{
    	SelfTerminalBean selfBean=BeanManager.createBean(SelfTerminalBean.class);
    	IData result = new DataMap();
    	IData tradeInfo=selfBean.qryTradeInfo(input.getString("phoneNo"), "142", false);
    	if(IDataUtil.isEmpty(tradeInfo)){
    		 tradeInfo=selfBean.qryTradeInfo(input.getString("phoneNo"), "142", true);
    	}
    	if(IDataUtil.isNotEmpty(tradeInfo)){
    		 result.put("billid", tradeInfo.getString("TRADE_ID", ""));
             result.put("brand_name", tradeInfo.getString("BRAND_NAME", ""));
             result.put("brand_code", tradeInfo.getString("BRAND_CODE", ""));
             result.put("work_name", tradeInfo.getString("TRADE_STAFF_NAME", ""));
             result.put("work_no", tradeInfo.getString("TRADE_STAFF_ID", ""));
             result.put("org_info", tradeInfo.getString("ORG_INFO", ""));
             result.put("org_name", tradeInfo.getString("ORG_NAME", ""));
             result.put("phone", tradeInfo.getString("SERIAL_NUMBER", ""));
             result.put("serv_id", tradeInfo.getString("USER_ID", ""));
             result.put("op_time", tradeInfo.getString("ACCEPT_DATE", ""));
            
             IData busi_info = new DataMap();

             busi_info.put("op_code", tradeInfo.getString("TRADE_TYPE_CODE", ""));
             busi_info.put("sys_accept", tradeInfo.getString("TRADE_ID", ""));
             busi_info.put("busi_detail","无");

             result.putAll(busi_info);

             result.put("verify_mode",tradeInfo.getString("VERIFY_MODE", ""));
             result.put("id_card", tradeInfo.getString("ID_CARD", ""));
             result.put("cust_name", tradeInfo.getString("CUST_NAME", ""));
             result.put("copy_flag", "");
             result.put("agm_flag", "");
             
             //查询手持照
             IData userCheckInfo=selfBean.qryUserCheckInfo(input);
			 String cardImg="";
			 String handImg="";
			
			 if(IDataUtil.isNotEmpty(userCheckInfo)){
				cardImg=userCheckInfo.getString("IDCARD_IMG");
				if(StringUtils.isNotEmpty(userCheckInfo.getString("USERCOLOR_PIC"))){
					handImg=userCheckInfo.getString("USERCOLOR_PIC");
				}else if(StringUtils.isNotEmpty(userCheckInfo.getString("USERGAY_PIC"))){
					handImg=userCheckInfo.getString("USERGAY_PIC");
				}
			 }
			
			 result.put("cardImg", cardImg);
			 result.put("handImg", handImg);
			 
			 return SelfTerminalUtil.responseSuccess(result);
    	}else{
    		 return SelfTerminalUtil.responseFail("没有查询到补换卡工单信息",null);
    	}
    }
    /**
     * 获取写卡数据
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData qryWriteCardData(IData inParam) throws Exception{
    	IData input = new DataMap(inParam.toString());
		IData output=new DataMap();
		
		
		//非空判断
		try{
			IDataUtil.chkParam(input, "mobileNum");
			IDataUtil.chkParam(input, "bizCode");
			IDataUtil.chkParam(input, "cardType");
			IDataUtil.chkParam(input, "orderId");
		}catch(Exception e){
			return SelfTerminalUtil.responseFail(e.getMessage(),output);
		}
		//设置返回参数
		output.put("orderId", input.getString("orderId",""));
		output.put("extOrderId",input.getString("extOrderId",""));
		output.put("bizType",input.getString("bizCode",""));
		output.put("mobileNum", input.getString("mobileNum",""));
		output.put("cardSn", input.getString("cardSn",""));
		output.put("cardType", input.getInt("cardType"));
		output.put("eid", input.getString("eid",""));
		
		String serialNumber=input.getString("mobileNum");
		String emptyCardId=input.getString("cardSn");
		String bizCode=input.getString("bizCode");
		String cardType=input.getString("cardType");
		
		//集中写卡自助激活增加
		boolean isSelfTerm=true;
//		if(StringUtils.isNotEmpty(input.getString("channelId"))){
//			String channelId=input.getString("channelId");
//			//是否在渠道里
//			IDataset channelList=CommparaInfoQry.getCommParas("CSM", "7979", "JZXK_CHANNEL", channelId, "0898");
//			if(IDataUtil.isNotEmpty(channelList)){
//				isSelfTerm=false;
//			}
//		}
		IDataset orderlist = AbilityPlatOrderBean.queryOrderInfo(input.getString("orderId",""));
		if(IDataUtil.isNotEmpty(orderlist)){
			isSelfTerm=false;
		}
		IDataset writeResult=null;
		//新型自助终端分支
		if(isSelfTerm){
			if("2".equals(cardType)||"3".equals(cardType)){
				try{
					IDataUtil.chkParam(input, "cardSn");
				}catch(Exception e){
					return SelfTerminalUtil.responseFail(e.getMessage(),output);
				}
			}else{
				return SelfTerminalUtil.responseFail("目前不支持的cardType="+cardType,output);
			}
			
			String isNp = "0";
	        IData uData = MsisdnInfoQry.getCrmMsisonBySerialnumber(serialNumber);
	        if(IDataUtil.isEmpty(uData)){
	        	uData = MsisdnInfoQry.getCrmMsisonBySerialnumberNew(serialNumber);
	        }
	        if (IDataUtil.isNotEmpty(uData))
	        {
	            String asp = uData.getString("ASP", "");
	            if (!"1".equals(asp))
	            {
	            	isNp = "1";
	            }
	        }
			
			//获取开户写卡数据
			if("002".equals(bizCode)){
				 writeResult= ResCall.queryWriteCardInfo(serialNumber, emptyCardId, "2", "2", isNp);
			//获取补换卡写卡数据	
			}else if("001".equals(bizCode)){
				writeResult = ResCall.queryWriteCardBasicData(serialNumber, emptyCardId, "2", isNp);
			}else{
				return SelfTerminalUtil.responseFail("不支持的bizCode="+bizCode,output);
			}
			
		//集中写卡自助激活分支	
		}else{
			//查询子订单信息
			IDataset OAOorderInfos= AbilityPlatOrderBean.querySuborderInfoByNumberOperType(null, orderlist.getData(0).getString("ORDER_ID"), null);
			String numberOprType = "";
			if(IDataUtil.isNotEmpty(OAOorderInfos)){
				numberOprType = OAOorderInfos.getData(0).getString("NUMBER_OPRTYPE");
			}

			String channelId=input.getString("channelId");
			//办理工号
			IDataset staffList=CommparaInfoQry.getCommParas("CSM", "7979", "JZXK_CHANNEL_STAFF", channelId, "0898");
			if(IDataUtil.isNotEmpty(staffList)){
				//赋值登录工号
				if(StringUtils.isNotEmpty(staffList.getData(0).getString("PARA_CODE2"))){
					getVisit().setStaffId(staffList.getData(0).getString("PARA_CODE2"));
				}
				//赋值登录部门ID
				if(StringUtils.isNotEmpty(staffList.getData(0).getString("PARA_CODE3"))){
					getVisit().setDepartId(staffList.getData(0).getString("PARA_CODE3"));
				}
				//赋值部门代码
				if(StringUtils.isNotEmpty(staffList.getData(0).getString("PARA_CODE4"))){
					getVisit().setDepartCode(staffList.getData(0).getString("PARA_CODE4"));
				}
				//赋值业务区
				if(StringUtils.isNotEmpty(staffList.getData(0).getString("PARA_CODE5"))){
					getVisit().setCityCode(staffList.getData(0).getString("PARA_CODE5"));
				}
			}
			String isTurnnet="0";
			//携转
			if ("24".equals(numberOprType)) {
				isTurnnet = "1";
			}
			writeResult= ResCall.queryWriteCardInfoByCreditPay(serialNumber, emptyCardId, "1", "2", isTurnnet);
		}
		
		if(IDataUtil.isEmpty(writeResult)){
			return SelfTerminalUtil.responseFail("获取写卡基础数据失败",output);
		}
		
		IData cardData=new DataMap();
		cardData.put("imsi", writeResult.getData(0).getString("IMSI",""));
		cardData.put("iccid", writeResult.getData(0).getString("ICC_ID",""));
		cardData.put("smsp", writeResult.getData(0).getString("SMSP",""));
		cardData.put("pin1", writeResult.getData(0).getString("PIN1",""));
		cardData.put("pin2", writeResult.getData(0).getString("PIN2",""));
		cardData.put("puk1", writeResult.getData(0).getString("PUK1",""));
		cardData.put("puk2", writeResult.getData(0).getString("PUK2",""));
		output.put("cardData", cardData);
		
		return SelfTerminalUtil.responseSuccess(output);
    }
}
