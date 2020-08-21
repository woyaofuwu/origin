package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetrescheduled.widenetremoveintf;

import org.apache.log4j.Logger;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.WidenetMoveBean;

/**
 * @author zhengkai5 宽带移机接口
 */
public class WideNetMoveIntfSVC extends CSBizService {

	private static final Logger logger = Logger.getLogger(WideNetMoveIntfSVC.class);

	/**
	 * @param input
	 * @return WIDE_INFO :宽带信息 USER_PRO_INFO ：用户产品信息 SALE_ACTIVE_FEE ： 0
	 * @author zhengkai5
	 * @throws Exception
	 */
	public IData loadChildInfo(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		
		//初始化宽带信息
		IData resultData = new DataMap();

		input.put("TRADE_TYPE_CODE", "606");
		
		String kdserialNumber = null;
		if(!input.getString("SERIAL_NUMBER").startsWith("KD_")){
			kdserialNumber = "KD_"+input.getString("SERIAL_NUMBER");
		}
		IData userInfo = UcaInfoQry.qryUserInfoBySn(kdserialNumber);
		if (IDataUtil.isEmpty(userInfo)) {
			CSAppException.appError("-1", "该用户没有办理宽带！");
		}
		
		IData customerInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
		if (IDataUtil.isEmpty(customerInfo)) {
			CSAppException.appError("-1", "通过该服务号码查询不到有效的客户信息！");
		}

		// 用户规则校验
		IData check = new DataMap();
		check.put("SERIAL_NUMBER", kdserialNumber);
		check.putAll(userInfo);
		check.put("IS_REAL_NAME", customerInfo.getString("IS_REAL_NAME"));
		check.put("TRADE_TYPE_CODE", "606");
		check.put("X_CHOICE_TAG", "0");
		check.put(Route.ROUTE_EPARCHY_CODE,input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		
		//将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
		getVisit().setInModeCode("0");
		input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		
		//调用鉴权规则校验
		IDataset infos = CSAppCall.call("CS.CheckTradeSVC.checkBeforeTrade",check);
		CSAppException.breerr(infos.getData(0));
		
		input.put("USER_ID", userInfo.getString("USER_ID"));
		IDataset UserWideInfo = CSAppCall.call("CS.WidenetInfoQuerySVC.getUserWidenetInfo", input);
		if (IDataUtil.isEmpty(UserWideInfo)) {
			CSAppException.apperr(WidenetException.CRM_WIDENET_3);
		} else {
			input.put("MOBILE_USER_ID",(UserWideInfo.getData(0)).getString("USER_ID"));
			input.put("EPARCHY_CODE",UserWideInfo.getData(0).getString("EPARCHY_CODE",getTradeEparchyCode()));
		}
		
		// 1：移动GPON，2：ADSL，3：移动FTTH，4：校园宽带，5：TTADSL海南铁通FTTH，6：海南铁通FTTB
		String widetype = UserWideInfo.getData(0).getString("RSRV_STR2");
		if ("4".equals(widetype)) // 校园宽带
		{
			CSAppException.appError("-1", "校园宽带不能在此办理移机业务！");
		}
		
		//获取用户产品信息
		IData result = getUserProductInfo(input);
		
		IData resultNew = getUserProductInfoNew(input);
		
		// 1：移动GPON，2：ADSL，3：移动FTTH，4：校园宽带，5：TTADSL海南铁通FTTH，6：海南铁通FTTB
		IData wideInfo = UserWideInfo.getData(0);
		// 根据用户宽带信息，重置TRADE_TYPE_CODE和WIDE_TYPE
		resultData.put("TRADE_TYPE_CODE", "606");
		resultData.put("WIDE_TYPE_CODE", widetype);
		
		resultData.put("OLD_STAND_ADDRESS", wideInfo.getString("STAND_ADDRESS"));  //原标准地址
		resultData.put("OLD_DETAIL_ADDRESS", wideInfo.getString("DETAIL_ADDRESS"));//原详细地址
		
		resultData.put("USER_PRODUCT_NAME", result.getString("USER_PRODUCT_NAME")); // 用户宽带产品名称（TF_F_USER_PRODUCT）
		
		resultData.put("SALE_ACTIVE_PRODUCT_NAME", result.getString("SALE_PRODUCT_NAME")); // 营销产品
		resultData.put("SALE_ACTIVE_PACKAGE_NAME", result.getString("SALE_PACKAGE_NAME")); // 营销包
		
		if (StringUtils.isBlank(result.getString("SALE_PRODUCT_NAME"))) {
			resultData.put("SALE_ACTIVE_PRODUCT_NAME", resultNew.getString("SALE_PRODUCT_NAME")); // 营销产品
			resultData.put("SALE_ACTIVE_PACKAGE_NAME", resultNew.getString("SALE_PACKAGE_NAME")); // 营销包
		}
		
		resultData.put("X_RESULTCODE", "0");
		return resultData;
	}


	/**
	 * 选择地址时，所加载的宽带类型   初始化     宽带产品    宽带类型  
	 * @param input
	 * @author zhengkai5
	 * @return  
	 * 		   PRODUCT_MODE_INFO   宽带信息 
	 * 		   PRODUCT_ID_INFO     产品信息
	 *         IS_MODEL_FTTH  为  1 时，调用   modelList  光猫列表接口
	 *         IS_BUSINESS   为 0 时，需调用    营销活动   接口
	 * @throws Exception
	 */
	public IDataset initProductChg(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "OPEN_TYPE");
		IDataUtil.chkParam(input, "AREA_CODE");
		
		//将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
		getVisit().setInModeCode("0");
		input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		
		IDataset resultData = new DatasetList();
		
		String openType = input.getString("OPEN_TYPE","");
		String areaCode = input.getString("AREA_CODE");
		
		//获取 productMode && productModeName
		IData productMode = getProductModeByOpenType(openType);
		String newProductMode = productMode.getString("PRODUCT_MODE");
		
		String SerialNumber = input.getString("SERIAL_NUMBER");
		if (!SerialNumber.startsWith("KD_")) 
		{
			SerialNumber = "KD_"+SerialNumber;
		}
		//获取用户宽带信息（TF_F_USER_WIDENET）
		IDataset userWides = WidenetInfoQry.getUserWidenetInfoBySerialNumber(SerialNumber);
		if ( IDataUtil.isEmpty(userWides) )
		{
			CSAppException.appError("-1", "通过该服务号码查询不到有效的宽带资料信息！");
		}
		
		//获取用户编号
		String userId =  userWides.getData(0).getString("USER_ID");
		input.put("USER_ID", userId);
		input.put("TRADE_TYPE_CODE", "606");
		
		//获取用户wideType
		String oldWideType =  userWides.getData(0).getString("RSRV_STR2");
		
		//获取用户ordProductMode
		String oldProductMode = getProductModeByWideType(oldWideType);
		
		String eparchyCode = input.getString("ROUTE_EPARCHY_CODE",getTradeEparchyCode());
		input.put("EPARCHY_CODE",eparchyCode);
		
		//判断是否可做移机      
		IData chkWideMove = chkWideMoveAndChgProdNum(input);
		String strPriv = chkWideMove.getString("WIDENETMOVE_FIRST");
		if("1".equals(strPriv)) 
		{
			if(oldProductMode != newProductMode)
			{
				CSAppException.appError("-1", "开户首月优惠期间，仅提供同制式、同速率宽带移机!");
			}
		}
		
		//查验  新地址  && 旧productMode  是否 为 ftth && adsl 类型
		IData checkFtth = checkFtthAndAdslAndModelMoney(openType, oldProductMode);
		boolean isNewFtth = checkFtth.getBoolean("IS_NEW_FTTH");
		boolean isUsedFtth = checkFtth.getBoolean("IS_USED_FTTH");
		String isNewAdsl =checkFtth.getString("IS_NEW_ADSL");
		String isOldAdsl = checkFtth.getString("IS_OLD_ADSL");
		
		// 不允许从非ADSL转为ADSL
		if (("0".equals(isOldAdsl)) && ("1".equals(isNewAdsl))) 
		{
			CSAppException.appError("-1", "业务限制：不能从非ADSL宽带转为ADSL宽带，请重新选择标准地址！");
		}
		
		// 判断是否需要做产品变更 
		IData data2 = new DataMap();
		data2.put("PRODUCT_MODE", newProductMode);
		data2.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		IData isNeedChangeData = checkNeedChangeProduct(data2);
		String isNeedChgProd = isNeedChangeData.getString("IS_NEED_CHANGE_PRODUCT");
		
		
		// IS_CHG_OTHER :是否预定了其他产品 (0:无 ， 1：是)
		IData chkProChgRule = chkProductChgRule(input);
		if (("1".equals(chkProChgRule.getString("IS_CHG_OTHER")))) {
			if("1".equals(isNeedChgProd) ){
				CSAppException.appError("-1", "您当前有预约的主产品变更，不能办理跨宽带类型移机，请重新在标准地址中选择您的当前产品对应的宽带类型!");
			}else{
				CSAppException.appError("-1", "您当前有预约的主产品变更，只能办理移机业务，不能变更产品!");
			}
		}
		
		String showInfo = "";
		if("1".equals(isNeedChgProd) )
		{
			showInfo = "您的宽带产品已经发生变化，请重新选择您需要的宽带产品";
		}
		
		//根据SerialNumber查询用户(产品)信息     （userInfo,PRODUCT_ID,PRODUCT_NAME,BRAND_CODE,BRAND_NAME,PRODUCT_MODE）
		String serNumber = input.getString("SERIAL_NUMBER");
		IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serNumber);
		if (IDataUtil.isEmpty(userInfo)) 
		{
			CSAppException.appError("", "通过该服务号码查询不到有效的用户信息！");
		}
		//判断是否集团用户
		String isBusiness = "0";
		if ("BNBD".equals(userInfo.getString("RSRV_STR10")))
		{
			isBusiness = "1";
		}
		
		//获取宽带产品信息
		IData wideNetData = new DataMap();
		wideNetData.put("NEW_PRODUCT_MODE", newProductMode);
		wideNetData.put("USER_ID", userId);
		wideNetData.put("SERIAL_NUMBER", serNumber);
		wideNetData.put("IS_BUSINESS_WIDE", isBusiness);
		wideNetData.put("TRADE_TYPE_CODE", "606");
		wideNetData.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset products = CSAppCall.call("SS.WidenetMoveSVC.getWidenetProductInfo", wideNetData);
		// 产品权限控制
		ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(),products);

		/**
		 * 1000M宽带 去除地址中不适合装1000M的产品
		 * */
		String tag1000 = input.getString("TAG1000", "");
		if (tag1000 == null || "".equals(tag1000) || !"1000".equals(tag1000)) 
		{
			for (int k = 0; k < products.size(); k++) {
				String prodId = products.getData(k).getString("PRODUCT_ID");
				if ("20171000".equals(prodId)) 
				{
					products.remove(k);
					break;
				}
			}
		}
		
		//验证用户当前 是否为 ftth  by serialNumber
		IData userModel = getUserModelInfo(input);
		boolean isOldFtth = userModel.getBoolean("IS_OLD_FTTH");
		
		IData iEdata = new DataMap();
		iEdata.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		iEdata.put("IS_USED_FTTH", isUsedFtth);
		iEdata.put("IS_NEW_FTTH", isNewFtth);
		iEdata.put("IS_OLD_FTTH", isOldFtth);
		iEdata.put("AREA_CODE", areaCode);
		IData isExchangeModelData = checkExchangeModel(iEdata);
		
		String isExchangeModel = isExchangeModelData.getString("IS_EXCHANGE_MODEL");
		String isModelFtth = isExchangeModelData.getString("IS_MODEL_FTTH");
		//String otherAreaFlag = isExchangeModelData.getString("OTHER_AREA_FLAG");
		String showModelInfo = isExchangeModelData.getString("showModelInfo");
		
		//返回提示信息 && isFtth
		IData paraData = new DataMap();
		paraData.put("IS_FTTH", "0");
		if(isNewFtth)
		{
			paraData.put("IS_MODEL_FTTH", "1"); // 如果为 1 ，需调用  光猫列表接口
		}
		if( isModelFtth!=null|| !"".equals(isModelFtth))
		{
			paraData.put("IS_MODEL_FTTH", isModelFtth); 
		}
		
		paraData.put("SHOW_INFO", showInfo);  //提示信息
		paraData.put("IS_BUSINESS_WIDE", isBusiness);  // 不为 1 ，需调用营销活动接口
		paraData.put("IS_EXCHANGE_MODEL", isExchangeModel);  
		paraData.put("SHOW_MODEL_INFO", showModelInfo);
		resultData.add(paraData); 

		//宽带类型 productMode & productModeName
		resultData.add(productMode);
		
		//获取宽带产品列表
		for(int k = 0; k < products.size(); k++)
		{
			IData datai = new DataMap();
			datai.put("PRODUCT_ID", products.getData(k).getString("PRODUCT_ID"));
			datai.put("PRODUCT_NAME", products.getData(k).getString("PRODUCT_NAME"));
			resultData.add(datai);
		}
		return resultData;
	}

	/**
	 * 宽带产品列表下拉条 初始化 营销活动   && 光猫费用  
	 * @param input
	 * @author zhengkai5
	 * @return IS_EXCHANGE_MODEL
	 * 		   MODEM_DEPOSIT
	 * 		   IS_BUSINESS_WIDE 		   
	 * @throws Exception
	 */
	public IData checkProductMode(IData input) throws Exception 
	{
		IData resultData = new DataMap();
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "OPEN_TYPE");
		IDataUtil.chkParam(input, "IS_EXCHANGE_MODEL");
		IDataUtil.chkParam(input, "PRODUCT_MODE");
		IDataUtil.chkParam(input, "PRODUCT_ID");
		
		//将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
		getVisit().setInModeCode("0");
		input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		
		String serialNumber = input.getString("SERIAL_NUMBER","");  
		String openType = input.getString("OPEN_TYPE");
		String isExchangeModel = input.getString("IS_EXCHANGE_MODEL");  // 兑换光猫
		String newProductMode = input.getString("PRODUCT_MODE");
		String newProductId = input.getString("PRODUCT_ID"); // 用户选择的宽带产品（产品id）
		String modelMode = input.getString("MODEL_MODE");
		String oldSaleActiveName = input.getString("SALE_ACTIVE_PACKAGE_NAME","");
		
        String wideSerialNum = "";
        
        if (!serialNumber.startsWith("KD_"))
        {
        	wideSerialNum = "KD_"+ serialNumber;
        }
    	
        IData userInfo = UcaInfoQry.qryUserInfoBySn(wideSerialNum);
        
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.appError("-1", "该用户没有办理宽带！");
        }
        
        String userId = userInfo.getString("USER_ID");
        input.put("USER_ID", userId);
        
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isEmpty(widenetInfos))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_3);
        }
        
        String  widetype = widenetInfos.getData(0).getString("RSRV_STR2");
		
        String oldProductMode = getProductModeByWideType(widetype);
		
		IData checkFtth = checkFtthAndAdslAndModelMoney(openType,oldProductMode);
		
		String fristRent = checkFtth.getString("FIRST_RENT" ); //第一次押金
		String secondRent =checkFtth.getString("SECOND_RENT"); //第二次押金
		
		String isDependActive = "0";
		if( !"1".equals(isExchangeModel)|| !"0".equals(isExchangeModel))
		{
			isDependActive= "1";
		}
		
		// 判断是否需要做产品变更 
		IData data1 = new DataMap();
		data1.put("SERIAL_NUMBER", serialNumber);
		data1.put("PRODUCT_MODE", newProductMode);
		IData isNeedChangeData = checkNeedChangeProduct(data1);
		String isNeedChgProd = isNeedChangeData.getString("IS_NEED_CHANGE_PRODUCT");
		
		if(newProductId==null && "".equals(newProductId)){
			if("3".equals(modelMode) && "1".equals(isDependActive)){
				resultData.put("MODEM_DEPOSIT", fristRent);
				if(oldSaleActiveName != null && !"".equals(oldSaleActiveName) && !"1".equals(isNeedChgProd))
					resultData.put("MODEM_DEPOSIT", secondRent);
			}
		}else{
			if("3".equals(modelMode) && "1".equals(isDependActive)){
				resultData.put("MODEM_DEPOSIT", fristRent);
			}
		}
		
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);
		data.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset userInfos = CSAppCall.call("SS.WidenetMoveSVC.getUserInfoBySerial", data);
		if (IDataUtil.isEmpty(userInfos)) 
		{
			CSAppException.appError("", "通过该服务号码查询不到有效的用户信息！");
		}
		
		String isBusiness = "0";
		if(userInfos != null&& userInfos.size()>0){
        	if("BNBD".equals(userInfos.getData(0).getString("RSRV_STR10"))){
        		isBusiness = "1";
        	}
        }
		
		resultData.put("IS_BUSINESS_WIDE",isBusiness); //如果IS_BUSINESS = 0，需加载营销活动列表
		resultData.put("X_RESULTCODE", "0");
		return resultData;
	}

	/**
	 * 根据ProdId 获取 SaleActive
	 * @param data
	 *            prodId eparchyCode hasYearActive hasEndYearActive
	 *            hasYearDiscnt 包年套餐
	 * @author zhengkai5
	 * @return
	 * @throws Exception
	 */
	public IDataset getSaleActiveByProdId(IData input, String prodId,
			String eparchyCode, String hasYearActive, String hasEndYearActive,
			String hasYearDiscnt) throws Exception {
		IDataset actives = new DatasetList();
		String newProductId = prodId;
		input.put("EPARCHY_CODE", eparchyCode);
		input.put("PARAM_ATTR", "178");
		input.put("PARAM_CODE", "600");
		input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		/**
		 * 获取所有的营销活动
		 * @param data   EPARCHY_CODE,PARAM_ATTR,PARAM_CODE,PARAM_CODE
		 * @return td_s_commpara
		 * @throws Exception
		 */
		IDataset results = CSAppCall.call("SS.WidenetMoveSVC.getSaleActiveComm", input);

		// 包年套餐只可以办理包年营销活动
		if ("1".equals(hasYearDiscnt)) {
			for (int i = 0; i < results.size(); i++) {
				IData active = results.getData(i);
				if ("WIDE_YEAR_ACTIVE".equals(active.getString("PARA_CODE7", ""))) {
					actives.add(active);
				}
			}
		} else {
			String productId = "", endMonth = "";
			input.put("DEAL_TYPE", "JUDGE_ACTIVE_INFO");
			
			/**
			 * 移机公共方法新增
			 * @param DEAL_TYPE, SERIAL_NUMBER
			 * @return PRODUCT_ID: 产品ID IS_END_MONTH: 是否是最后一个月 0 || 1
			 */
			IDataset dataset = CSAppCall.call("SS.WidenetMoveSVC.getWideNetMoveInfo", input);
			if (IDataUtil.isNotEmpty(dataset)) {
				productId = dataset.getData(0).getString("PRODUCT_ID");
				endMonth = dataset.getData(0).getString("IS_END_MONTH");
			}
			if ("1".equals(endMonth)) {
				String canProd = "";
				input.put("DEAL_TYPE", "JUDGE_ACTIVE_IS_CAN_CHG");
				/**
				 * 移机公共方法新增
				 * @param "CSM", "178", "600", code_code:TD_S_COMMPARA,SEL_PK_TD_S_COMMPARA
				 */
				IDataset activeChg = CSAppCall.call("SS.WidenetMoveSVC.getWideNetMoveInfo", input);
				for (int i = 0; i < activeChg.size(); i++) {
					if (productId.equals(activeChg.getData(i).getString("PARA_CODE4"))) {
						canProd = activeChg.getData(i).getString("PARA_CODE20");
						break;
					}
				}

				if (canProd != null && !"".equals(canProd)) {
					String[] prods = canProd.split("\\|");
					for (int i = 0; i < prods.length; i++) {
						String newProd = prods[i];
						for (int j = 0; j < results.size(); j++) {
							if (newProd.equals(results.getData(j).getString(
									"PARA_CODE4"))) {
								actives.add(results.getData(j));
							}
						}
					}
				} else {
					actives = results;
				}
			} else {
				actives = results;
			}
		}

		IDataset resus = new DatasetList();
		for (int i = 0; i < actives.size(); i++) {
			IData result = actives.getData(i);
			if (newProductId.equals(result.getString("PARA_CODE1"))) {
				resus.add(result);
			}
		}
		return resus;
	}

	
	/**
	 * 处理光猫的费用（页面光猫模式下拉条）
	 * @param SERIAL_NUMBER,OPEN_TYPE,MODEL_MODE,IS_BUSINESS,IS_EXCHANGE_MODEL,NEW_PRODUCT_MODE,
	 *            USER_ID,
	 *            
	 * @return  MODEL_SHOW_INFO 
	 * 			MODEM_DEPOSIT  押金信息
	 * 			IS_DEPEND_ACTIVE 
	 * 			MODEL_NOT_RETURN
	 * @author zhengkai5
	 * @throws Exception
	 */
	public IData dealModelMoney(IData input) throws Exception {
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "OPEN_TYPE");
		IDataUtil.chkParam(input, "MODEL_MODE");
		IDataUtil.chkParam(input, "IS_BUSINESS_WIDE");
		IDataUtil.chkParam(input, "IS_EXCHANGE_MODEL");

		//将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
		getVisit().setInModeCode("0");
		input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		
		IData resultData = new DataMap();
		
		String eparchyCode = input.getString("EPARCHY_CODE", getTradeEparchyCode());
		String modelMode = input.getString("MODEL_MODE","");
		String isBusiness = input.getString("IS_BUSINESS_WIDE");
		String isExchangeModel = input.getString("IS_EXCHANGE_MODEL");
		String openType = input.getString("OPEN_TYPE","");
		
		//拼凑宽带服务号
		String SerialNumber = input.getString("SERIAL_NUMBER");
		if(!SerialNumber.startsWith("KD_"))
		{
			SerialNumber = "KD_"+SerialNumber;
		}
		
		//获取用户宽带信息（TF_F_USER_WIDENET）
		IDataset userWides = WidenetInfoQry.getUserWidenetInfoBySerialNumber(SerialNumber);
		
		//获取用户编号
		String userId =  userWides.getData(0).getString("USER_ID");
		
		input.put("USER_ID", userId);
		
		//获取用户wideType
		String oldWideType =  userWides.getData(0).getString("RSRV_STR2");
		
		//查验用户当前wideType
		IData oldcheckWide = checkWideType(oldWideType);
		
		//获取用户当前productMode
		String oldProductMode = oldcheckWide.getString("PRODUCT_MODE");
		
		input.put("NEW_PRODUCT_MODE", oldProductMode);
		String showModelInfo = "";
		String showInfo = "";
		if("0".equals(modelMode)){
			WidenetMoveBean widenetMoveBean = BeanManager.createBean(WidenetMoveBean.class);
			
			//获取用户光猫信息
			IData userModel = getUserModelInfo(input);
			int modelNotReturn = userModel.getInt("MODEL_NOT_RETURN");
			if(modelNotReturn > 0 )
			{
				String errInfo = "您有租借的尚未退还的光猫，请先退还或办理丢失之后，再办理光猫租借！";
				CSAppException.appError("-1", errInfo);
			}
			
			//获取宽带产品信息
			IDataset products = getWidenetProductInfo(input);
			IData product = products.getData(0);
			
			String firstRent = null;
			String secondRent = null;
			String moveFtthMoney = null;
			boolean isFtth = false;
			/**
			 * 查询td_s_commpara表，WIDE_TYPE_PROD_MODE ，获取所有的宽带类型配置(宽带移机宽带类型配置)： 有6条
			 * IDataset results = CommparaInfoQry.getCommpara("CSM", "210","WIDE_TYPE_PROD_MODE", "0898");
			 * @param 无
			 */
			IDataset prodModes = CSAppCall.call("SS.WidenetMoveSVC.showProdMode",input);
	
			//获取光猫费用
			for (int i = 0; i < prodModes.size(); i++) {
				if (prodModes.getData(i).getString("PARA_CODE3", "").equals(openType)) {
					// 查看宽带类型是否为FTTH
					if (prodModes.getData(i).getString("PARA_CODE4", "").equals("FTTH")) {
						firstRent = prodModes.getData(i).getString("PARA_CODE6", "");
						secondRent = prodModes.getData(i).getString("PARA_CODE7","");
						moveFtthMoney = prodModes.getData(i).getString("PARA_CODE11", "");
						isFtth = true;
					}
				}
			}
			
			/**
			 * 获取手机用户的营销活动
			 * @Parameter: input SERIAL_NUMBER
			 * @Return: 该手机用户营销活动中productId所对应的 用户优惠信息（TF_F_USER_DISCNT） 以及
			 *          PRODUCE_ID && PACKAGE_ID
			 */
			IDataset produceList = widenetMoveBean.getUserSaleActive(input);
			String packageId = "";
			if(!IDataUtil.isEmpty(produceList))
			{
				packageId = produceList.getData(0).getString("PACKAGE_ID");
			}
	
			boolean isChgS = false;
			if( input.getString("SALE_ACTIVE_PACKAGE_NAME")!= null)
			{	
				isChgS= true;
			}
			//营销包  name
			String activeName = input.getString("SALE_ACTIVE_PACKAGE_NAME","");
			String newProdId = input.getString("PRODUCT_ID", "");
			String hasEndYearActive = product.getString("HAS_End_YEAR_ACTIVE", ""); // 查看用户是否已经办理过该地州所有营销活动 且 是否过期
	
			String cash = null;
			
			if (isFtth) 
			{
				/**
				 * 6131配置作为光猫的押金和购买金额
				 * @return td_s_commpara
				 */
				IData data = new DataMap();
				data.put("EPARCHY_CODE", eparchyCode);
				data.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
				data.put("PARAM_ATTR", "6131");
				IDataset results = CSAppCall.call("SS.WidenetMoveSVC.getSaleActiveComm", data);
				if (results != null && results.size() > 0)
					for(int i = 0;i<results.size();i++)
					{
						if(results.getData(i).getString("PARAM_CODE")== "1"||"1".equals(results.getData(i).getString("PARAM_CODE")))
						{
							secondRent = results.getData(i).getString("PARA_CODE1");
						}
						if(results.getData(i).getString("PARAM_CODE")== "2"||"2".equals(results.getData(i).getString("PARAM_CODE")))
						{
							firstRent = results.getData(i).getString("PARA_CODE1");
						}
					}
			}
	
			// 非集团用户
			if (!"1".equals(isBusiness)) 
			{
				String serialNumber = input.getString("SERIAL_NUMBER");
				IData param = new DataMap();
				if (serialNumber.startsWith("KD_")) 
				{
					serialNumber = serialNumber.substring(3);
				}
				param.put("SERIAL_NUMBER", serialNumber);
				param.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
				/**
				 * 查询账号，用户等信息
				 * @param SERIAL_NUMBER
				 */
				IDataset accts = CSAppCall.call("SS.WidenetMoveSVC.getUserBalance",param);
				cash = accts.getData(0).getString("CASH_BALANCE"); // 获取该用户的现金余额
			} else {
				// 集团用户
				cash = "0";
				firstRent = "0";
				secondRent = "0";
			}
			/**
			 * 根据局方要求，此处再写一次
			 * a、非FTTH变FTTH，一定领光猫。光猫押金如已优惠申领过，则200。没优惠申领过，有活动则100，无则200.
			 * b、FTTH变FTTH，新旧地区使用不同厂家光猫，一定领光猫。光猫押金100，不算优惠申领。
			 * c、FTTH变FTTH，新旧地区使用同厂家光猫，可领可不领。领时光猫押金100，不算优惠申领。
			 * d、是否领光猫务必正确通知到服开，服开在施工单务必正确展示给施工人员，推送给APP也要推送对。
			 **/
			String modelDeposit = "0";
			if ("1".equals(isExchangeModel) || "0".equals(isExchangeModel)) 
			{  
				// 1表示“新旧地区使用同厂家光猫”
				modelDeposit = moveFtthMoney;
				showInfo = "您在办理光猫租借，需要冻结预存款"+Integer.parseInt(moveFtthMoney)/100+"元！";
				
				if (Integer.parseInt(moveFtthMoney) > Integer.parseInt(cash)) 
				{
					showInfo = "您在办理光猫租借，需要冻结预存款"
							+ Integer.parseInt(moveFtthMoney) / 100 + "元，您当前账户余额为"
							+ Integer.parseInt(cash) / 100 + "元，不足冻结预存款扣减，请提前缴纳预存！";
					CSAppException.appError("-1", showInfo);
				}
			} else {
				// 查看用户当前有没有生效的营销活动，营销活动对光猫押金进行优惠
				boolean hasActive = false;
				if (activeName != null && !"".equals(activeName) && !"1".equals(hasEndYearActive))
					hasActive = true;
	
				modelDeposit = firstRent; // 第一次的租金
				showInfo = "您在办理光猫租借，需要冻结预存款"+ Integer.parseInt(firstRent)/100 +"元，请提前缴纳预存，以免预存款金额不足！";
				if (Integer.parseInt(firstRent) > Integer.parseInt(cash)) 
				{
					showInfo = "您在办理光猫租借，需要冻结预存款"
							+ Integer.parseInt(firstRent) / 100 + "元,，您当前账户余额为"
							+ Integer.parseInt(cash) / 100
							+ "元，不足冻结预存款扣减，请提前缴纳预存；办理营销活动可以减免冻结预存款金额，减免后冻结金额为"
							+ Integer.parseInt(secondRent) / 100 + "元！";
					CSAppException.appError("-1", showInfo);
				}
				if (( isChgS && packageId != null && !"".equals(packageId)) || (hasActive && "".equals(newProdId))) 
				{
					modelDeposit = secondRent;
					showInfo = "您在办理光猫租借，您已经选择营销活动，优惠减免后需要冻结预存款"+Integer.parseInt(secondRent)/100+"元，请提前缴纳预存，以免预存款金额不足！";
					if (Integer.parseInt(secondRent) > Integer.parseInt(cash)) 
					{
						showInfo = "您在办理光猫租借，您已经选择营销活动，优惠减免后需要冻结预存款"
								+ Integer.parseInt(secondRent) / 100 + "元，您当前账户余额为"
								+ Integer.parseInt(cash) / 100
								+ "元，不足冻结预存款扣减，请提前缴纳预存！";
						CSAppException.appError("-1", showInfo);
					}
				}
			}
			
			resultData.put("MODEM_DEPOSIT", modelDeposit); // 光猫押金
			resultData.put("SHOW_MODEL_INFO",showModelInfo);    //光猫信息提示
			resultData.put("SHOW_INFO",showInfo);               //提示信息
			resultData.put("X_RESULTCODE", "0");
			return resultData;
		}else if("1".equals(modelMode))
		{
			resultData.put("MODEM_DEPOSIT", "30000"); // 光猫押金
			resultData.put("X_RESULTCODE", "0");
			return resultData;
		}
		resultData.put("MODEM_DEPOSIT", "0"); 
		resultData.put("X_RESULTCODE", "0");
		return resultData;
		
	}

	
	/**
	 * 营销活动列表下拉条,获取包年活动费用 && 营销活动描述  && 提示信息
	 * @param input
	 * @throws Exception
	 */
	public IData getSaleActiveDesc(IData input) throws Exception {
		
		//校验产品ID是否传入
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	
    	//校验产品ID是否传入
    	IDataUtil.chkParam(input, "PACKAGE_ID");
		
    	//校验产品ID是否传入
    	IDataUtil.chkParam(input, "PRODUCT_ID");
		
    	//将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
		getVisit().setInModeCode("0");
		input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
    	
		IData resultData = new DataMap();
		
		String saleActiveFee = "0";
		String hintInfo = "";  //提示信息
		
		resultData.put("SHOW_INFO",hintInfo);               //提示信息
		resultData.put("SALE_ACTIVE_FEE", saleActiveFee);   //营销活动费用
		resultData.put("SALE_ACTIVE_DESC", "");             //营销活动描述 
		
		String newProductId = input.getString("PRODUCT_ID");
		String packageId = input.getString("PACKAGE_ID");
		String eparchyCode = input.getString("EPARCHY_CODE",getTradeEparchyCode());

		input.put("EPARCHY_CODE", eparchyCode);
		input.put("PARAM_ATTR", "178");
		input.put("PARAM_CODE", "600");
		//获取所有的优惠活动
		IDataset results = CSAppCall.call("SS.WidenetMoveSVC.getSaleActiveComm", input);
		for (int i = 0; i < results.size() ; i++ ) 
		{
			IData result = results.getData(i);
			if ((newProductId.equals(result.getString("PARA_CODE1"))) && (packageId.equals(result.getString("PARA_CODE5")))) 
			{
				resultData.put("SALE_ACTIVE_DESC", result.getString("PARA_CODE24"));  //营销活动描述 
				
				input.put("PRODUCT_ID", result.getString("PARA_CODE4"));
				input.put("ACTIVE_FLAG", "1");
				IDataset activeFee = CSAppCall.call("SS.WidenetMoveSVC.queryCheckSaleActiveFee", input);
				if (IDataUtil.isNotEmpty(activeFee)) {
					
					int depositFee = Integer.parseInt(activeFee.getData(0).getString("SALE_ACTIVE_FEE", "0"));
					resultData.put("SALE_ACTIVE_FEE", depositFee);

					if (depositFee > 0) 
					{
						// 获取用户当前现金账户余额
						IDataset accts = CSAppCall.call("SS.WidenetMoveSVC.getUserBalance", input);
						String cash = accts.getData(0).getString("CASH_BALANCE", "0");
						
						if (depositFee > Integer.parseInt(cash)) {
							hintInfo = "您选择办理宽带营销活动，需要冻结预存" + depositFee / 100
									+ "元，您当前余额为" + (Integer.parseInt(cash))
									/ 100 + "，请提前缴纳预存！";
						} else {
							hintInfo = "您选择办理宽带营销活动，需要冻结预存" + depositFee / 100
									+ "元";
						}
						resultData.put("HINT_INFO", hintInfo);
					}
					saleActiveFee = ""
							+ Integer.parseInt(activeFee.getData(0).getString(
									"SALE_ACTIVE_FEE", "0")) / 100;
				} else {
					saleActiveFee = "0";
					resultData.put("SALE_ACTIVE_FEE", saleActiveFee);
				}
				break;
			}
		}
		resultData.put("X_RESULTCODE", "0");
		return resultData;
	}

	/**
	 * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
	 * @param cycle
	 * @throws Exception
	 */
	public IDataset onTradeSubmit(IData input) throws Exception {
		
		//校验新服务ID是否传入
    	IDataUtil.chkParam(input, "SERVICE_ID");
    	
    	//校验  宽带类型   是否传入
    	IDataUtil.chkParam(input, "OPEN_TYPE");
    	
    	//校验服务号是否传入
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	
    	//校验新产品ID是否传入
    	IDataUtil.chkParam(input, "PRODUCT_ID");
    	
    	//校验新地址是否传入
    	IDataUtil.chkParam(input, "STAND_ADDRESS");
    	String standAddress = input.getString("STAND_ADDRESS","");
    	
    	//校验新地区是否传入
    	IDataUtil.chkParam(input, "AREA_CODE");
    	
    	//校验楼层  和  房号 是否传入
    	String addressBuildingNum = input.getString("ADDRESS_BUILDING_NUM","");
    	
    	//校验  集团用户  是否传入
    	IDataUtil.chkParam(input, "IS_BUSINESS_WIDE");
    	
    	//将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
		getVisit().setInModeCode("0");
		input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
    	
    	String productId = input.getString("PRODUCT_ID","");
    	input.put("NEW_PRODUCT_ID", productId);
    	String isModelFtth = input.getString("IS_MODEL_FTTH");
    	
    	//获取  光猫 
    	String modelMode = input.getString("MODEL_MODE");
    	input.put("MODEL_MODE", modelMode);
    	
    	//获取光猫押金
    	String modelDeposit = input.getString("MODEM_DEPOSIT");
    	input.put("MODEM_DEPOSIT", modelDeposit);
    	
    	//获取营销活动金额
    	String saleActiveFee = input.getString("SALE_ACTIVE_FEE");
    	input.put("SALE_ACTIVE_FEE", saleActiveFee);
    	
    	//获取营销活动  包 
    	String newPackageId = input.getString("PACKAGE_ID","");
		input.put("SALEACTIVE_PACKAGE_ID", newPackageId);
    	
		//获取opentype
		String openType = input.getString("OPEN_TYPE","");
		
		//获取新的productMode
		IData productModeData = getProductModeByOpenType(openType); 
		String productMode = productModeData.getString("PRODUCT_MODE","");
		input.put("PRODUCT_MODE", productMode);
		
		//拼凑宽带服务号
		String SerialNumber = input.getString("SERIAL_NUMBER");
		if(!SerialNumber.startsWith("KD_"))
		{
			SerialNumber = "KD_"+SerialNumber;
		}
		
		//获取用户宽带信息（TF_F_USER_WIDENET）
		IDataset userWides = WidenetInfoQry.getUserWidenetInfoBySerialNumber(SerialNumber);
		if(IDataUtil.isEmpty(userWides))
		{
			CSAppException.appError("-1", "该用户没有办理宽带业务！");
		}
		
		//获取用户编号
		String userId =  userWides.getData(0).getString("USER_ID");
		
		input.put("USER_ID", userId);
		input.put("AUTH_SERIAL_NUMBER",input.getString("SERIAL_NUMBER"));

		String isChgProd = "FALSE";
		
		// 判断是否需要做产品变更 
		IData checkNeedChangeProduct = checkNeedChangeProduct(input);
		String isNeedChgProd = checkNeedChangeProduct.getString("IS_NEED_CHANGE_PRODUCT","");
		
		//判断是否做了产品变更
		if(productId != null && !"".equals(productId))
		{
			isChgProd = "TRUE";
		}
		
		if("1".equals(isNeedChgProd))
	   	{
	     	if("TRUE".equals(isChgProd))
	     	{
	     		CSAppException.appError("-1", "请选择您要办理的新产品！");
	     	}
	   	}
		
		input.put("IS_CHG_PROD", isChgProd);
		input.put("IS_CHG_SALE", "FALSE");
		if(newPackageId != null && "".equals(newPackageId) )
		{
			input.put("IS_CHG_SALE", "TRUE");
		}
		
		//总费用
		double totalFee = Double.parseDouble(modelDeposit)+ Double.parseDouble(saleActiveFee);
    	
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
		// 查询账号，用户等信息 SERIAL_NUMBER 现金存折
		param.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset accts = CSAppCall.call("SS.WidenetMoveSVC.getUserBalance",param);
		String cash = accts.getData(0).getString("CASH_BALANCE"); // 获取该用户的现金余额
		double Cash = Double.parseDouble(cash);
		
		if(totalFee > Cash)
		{
			double fee = totalFee - Cash;
			String showInfo = "您总共需要从现金存折中转出："+totalFee+"元，" +
							  "其中宽带营销活动预存："+saleActiveFee+"元，" +
							  "光猫押金："+modelDeposit+"元，" +
							  "您当前现金余额为" +cash+"元，余额不足，" +
							  		"请您先缴费:"+fee+"元，再来办理该业务！";
			CSAppException.appError("-1", showInfo);
		}
		
		//是否需要光猫验证
		String isExchangeModel = input.getString("IS_EXCHANGE_MODEL");
		
		//是否需要光猫  
		if("1".equals(isModelFtth))
		{
			String errInfo = "";
	   		if("0".equals(isExchangeModel)||"4".equals(isExchangeModel)){
	   			errInfo = "您的宽带类型已经发生变化，当前的光猫在新地址下无法使用。请选择新的光猫";
	   			CSAppException.appError("-1", errInfo);
	   		}
	   		if("3".equals(isExchangeModel)){
	   			errInfo = "您的新装宽带地址需要光猫，请选择您的光猫";
	   			CSAppException.appError("-1", errInfo);
	   		}
		}
		
    	//校验新优惠ID是否传入
        IDataUtil.chkParam(input, "DISCNT_CODE");
    	
    	IDataset selectedelements = new DatasetList();
        String[] services = input.getString("SERVICE_ID").split(",");

        //营销活动校验会用到速率服务
        String rateSvc = "";
        
        String packageId = "-1";
        
        for(int i=0; i<services.length; i++)
        {
	        IData element = new DataMap();
	        element.put("ELEMENT_ID", services[i]);
	        element.put("ELEMENT_TYPE_CODE", "S");
	        element.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
	        
	        IData elementCfg = ProductElementsCache.getElement(input.getString("PRODUCT_ID"), services[i], BofConst.ELEMENT_TYPE_CODE_SVC);
            
	        //主服务从老的产品继承，不需要新订购
	        if ("1".equals(elementCfg.getString("IS_MAIN")))
	        {
	        	continue;
	        }
	        
	        rateSvc = services[i];
	        
	        if (IDataUtil.isNotEmpty(elementCfg))
            {
                packageId = elementCfg.getString("GROUP_ID","-1");
            }
	        
	        element.put("PACKAGE_ID", packageId);
	        element.put("MODIFY_TAG", "0");
	        selectedelements.add(element);
        }
        
        String[] discnts = input.getString("DISCNT_CODE").split(",");
        for(int i=0; i<discnts.length; i++)
        {
	        IData element = new DataMap();
	        element.put("ELEMENT_ID", discnts[i]);
	        element.put("ELEMENT_TYPE_CODE", "D");
	        element.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
	        element.put("MODIFY_TAG", "0");
	        
	        IData elementCfg = ProductElementsCache.getElement(input.getString("PRODUCT_ID"), discnts[i], BofConst.ELEMENT_TYPE_CODE_DISCNT);
            if (IDataUtil.isNotEmpty(elementCfg))
            {
                packageId = elementCfg.getString("GROUP_ID","-1");
            }
	        
	        element.put("PACKAGE_ID", packageId);
	        selectedelements.add(element);
        }
        
        IData oldPorductInfo = UcaInfoQry.qryMainProdInfoByUserId(input.getString("USER_ID"));
        
        if (IDataUtil.isEmpty(oldPorductInfo))
        {
            CSAppException.appError("-1", "该宽带用户主产品信息不存在！");
        }
        
        //用户已有的产品元素
        IDataset userElements = UserSvcInfoQry.getElementFromPackageByUser(input.getString("USER_ID"), oldPorductInfo.getString("PRODUCT_ID"), null);
        
        if (IDataUtil.isNotEmpty(userElements))
        {
            for (int j = 0; j < userElements.size(); j++ )
            {
                IData element = userElements.getData(j);
                
                //宽带主服务不变
                if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")) && "1".equals(element.getString("MAIN_TAG")))
                {
                    continue;
                }
                element.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                element.put("PRODUCT_ID", oldPorductInfo.getString("PRODUCT_ID"));
                
                selectedelements.add(element);
            }
        }
        
        input.put("WIDE_USER_SELECTED_SERVICEIDS", rateSvc);
        input.put("SELECTED_ELEMENTS", selectedelements.toString());
    	
		/**
		 * REQ201609280017_家客资源管理-九级地址BOSS侧改造需求
		 * @author zhengkai5
		 */
		input.put("DETAIL_ADDRESS", standAddress+addressBuildingNum); // 房层 和 楼号
		input.put("NEW_DETAIL_ADDRESS",standAddress+addressBuildingNum);
		
		//调用移机业务受理服务
		IDataset dataset = CSAppCall.call("SS.WidenetMoveRegSVC.tradeReg", input);
		return dataset;
	}
	
	/**
	 * 根据widetype  查验  ProductMode && WideTypeName
	 * @param widetype
	 * @return WIDE_TYPE_NAME
	 * 		   PRODUCT_MODE
	 */
	public IData checkWideType (String widetype){
		IData resultData = new DataMap();
		String productmode = "";
		if ("1".equals(widetype))
        {
    		resultData.put("WIDE_TYPE_NAME", "移动FTTB");
    		productmode="07";
        }
    	else if ("2".equals(widetype))
        {
        	resultData.put("WIDE_TYPE_NAME", "铁通ADSL");
        	productmode="09";
        }
    	else if ("3".equals(widetype))
        {
        	resultData.put("WIDE_TYPE_NAME", "移动FTTH");
        	productmode="11";
        }
    	else if ("5".equals(widetype))
        {
        	resultData.put("WIDE_TYPE_NAME", "铁通FTTH");
        	productmode="11"; //移动FTTH与铁通FTTH合并，使用同一套产品
        }
        else if ("6".equals(widetype))
        {
        	resultData.put("WIDE_TYPE_NAME", "铁通FTTB");
        	productmode="07"; //移动FTTB与铁通FTTB合并，使用同一套产品
        }
		resultData.put("PRODUCT_MODE", productmode);
		return resultData;
	}
	
	
	/**
	 * 检查  是否 为 ftth && adsl  类型  (查询td_s_commpara表，)
	 * @param wideType
	 * @param oldProductMode
	 * @return  IS_NEW_ADSL  
				IS_OLD_ADSL
				IS_NEW_FTTH
				IS_USED_FTTH
	 * WIDE_TYPE_PROD_MODE ，获取所有的宽带类型配置(宽带移机宽带类型配置)： 有6条
	 * IDataset results = CommparaInfoQry.getCommpara("CSM", "210","WIDE_TYPE_PROD_MODE", "0898");
	 * @param 无
	 */
	public IData checkFtthAndAdslAndModelMoney(String openType,String oldProductMode) throws Exception{
		IData result = new DataMap();
		boolean isNewFtth = false;
		boolean isUsedFtth = false;
		String isNewAdsl = "0";
		String isOldAdsl = "0";
		String firstRentMoney = "0";
		String secondRendMoney = "0";
		result.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IDataset prodModes = CSAppCall.call("SS.WidenetMoveSVC.showProdMode",result);
		// 获取用户需要移机后所在地址的宽带类型
		for (int i = 0; i < prodModes.size(); i++) {
			if (prodModes.getData(i).getString("PARA_CODE3", "").equals(openType)) {
				// 查看宽带类型是否为FTTH
				if (prodModes.getData(i).getString("PARA_CODE4", "").equals("FTTH")) {
					//purchaseMoney = prodModes.getData(i).getString("PARA_CODE5", "");
                	firstRentMoney = prodModes.getData(i).getString("PARA_CODE6", "");
                	secondRendMoney = prodModes.getData(i).getString("PARA_CODE7", "");
                	//moveFtthMoney = prodModes.getData(i).getString("PARA_CODE11", "");
					isNewFtth = true;
				}
				// 判断是否为ADSL
				if (prodModes.getData(i).getString("PARA_CODE8", "").equals("ADSL")) {
					isNewAdsl = "1";
				}
			}
			if (prodModes.getData(i).getString("PARA_CODE2", "").equals(oldProductMode)) {// ：当前用户产品模型
				if (prodModes.getData(i).getString("PARA_CODE8", "").equals("ADSL")) {
					isOldAdsl = "1";
				}
				if (prodModes.getData(i).getString("PARA_CODE4", "").equals("FTTH")) { 
					isUsedFtth = true;  
				}
			}
		}
		result.put("IS_NEW_ADSL", isNewAdsl);
		result.put("IS_OLD_ADSL", isOldAdsl);
		result.put("IS_NEW_FTTH", isNewFtth);
		result.put("IS_USED_FTTH", isUsedFtth);
		result.put("FIRST_RENT", firstRentMoney);
		result.put("SECOND_RENT", secondRendMoney);
		return result;
	}
	
	/**
	 * 获取宽带产品信息
	 * @param 参数说明：
	 *            NEW_PRODUCT_MODE,
	 *            USER_ID,
	 *            SERIAL_NUMBER,
	 *            IS_BUSINESS_WIDE（是否集团客户）
	 * @return wideNetProduct      宽带产品信息 
	 * 		   WIDE_RATE           宽带速录 
	 * 		   IS_CHG_OTHER :      是否预定了其他产品(0:无 ， 1：是) 
	 *         IS_HAS_YEAR_DISCNT  是否有包年活动：0：没有 ，1：有
	 *         HAS_EFF_ACTIVE      判断 该用户的营销活动有效 且 不是最后一个月 
	 *         HAS_EFF_YEAR        是否办理了当前业务类型的宽带优惠活动 且 不是最后一月 
	 *         HAS_YEAR_ACTIVE     是否已经办理过包年营销活动
	 *         HAS_End_YEAR_ACTIVE 查看用户是否已经办理过该地州所有营销活动 且 是否过期
	 */
	public IDataset getWidenetProductInfo(IData input) throws Exception{
		input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset products = CSAppCall.call("SS.WidenetMoveSVC.getWidenetProductInfo", input);
		return products;
	}
	
	/**
	 * 获取该用户产品信息
	 * @param USER_ID
	 *            ： 用户id, EPARCHY_CODE： 归属地 MOBILE_USER_ID = USER_ID：用户id
	 *            SERIAL_NUMBER： 服务号码
	 * @return SALE_PRODUCT_NAME(String): 营销活动产品名称
	 *         SALE_PACKAGE_NAME(String): 营销活动包名称 
	 *         PRODUCT_LIST (IDataset):所有的宽带产品列表（根据当前产品模型，业务交易地址得出） 
	 *         PRODUCT_MODE (String): 产品包
	 *         PRODUCT_ID (String): 产品id 
	 *         EPARCHY_CODE (String) = 入参中的EPARCHY_CODE 
	 *         USER_PRODUCT_NAME(String): 产品名称
	 *         USER_PRODUCT_ID (String)： 产品编号 
	 *         USER_BRAND_NAME (String)： 品牌名称
	 * @throws Exception 
	 */
	public IData getUserProductInfo(IData input) throws Exception{
		input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset resus = CSAppCall.call("SS.WidenetMoveSVC.getUserProductInfo",input);
		IData result = resus.getData(0);
		return result;
	}
	
	public IData getUserProductInfoNew(IData input) throws Exception{
		input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		IDataset resus = CSAppCall.call("SS.WidenetMoveSVC.getUserProductInfoNew",input);
		IData result = resus.getData(0);
		return result;
	}
	
	
	/**
	 * 当月是否已经做过移机和产品变更
	 * @param USER_ID   SERIAL_NUMBER
	 * @return WIDENETMOVE_FIRST = "0"/"1"
	 * @throws Exception 
	 */
	public IData chkWideMoveAndChgProdNum(IData input) throws Exception{
		IData resultData = new DataMap();
		input.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IDataset idsPriv = CSAppCall.call("SS.WidenetMoveSVC.judgeIsCanMove",input);
		resultData.put("WIDENETMOVE_FIRST", "0");
		if (IDataUtil.isNotEmpty(idsPriv)) {
			IData idPriv = idsPriv.first();
			String strPriv = idPriv.getString("WIDENETMOVE_FIRST", "0");
			resultData.put("WIDENETMOVE_FIRST", strPriv);
		}
		return resultData;
	}
	
	
	/**
	 * 根据服务号码，获取用户光猫信息
	 * @param SERIAL_NUMBER
	 * @return  DISCNT_MODEL_NOW_MONEY   光猫优惠的钱
	 * 			IS_DISCNT_MODEL_NOW     是否有光猫优惠
	 * 			IS_USE_MODEL_NOW    是否有光猫
	 * 			MODEL_NOT_RETURN    未返还光猫数量
	 * 			IS_OLD_FTTH      是否old_ftth
	 * 			MODEL_CNT
	 * @throws Exception
	 */
	public IData getUserModelInfo (IData input) throws Exception
	{
		input.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IData result = new DataMap();
		boolean isOldFtth = false;
		int modelCnt = 0;
		int modelNotReturn = 0;
		result.put("IS_USE_MODEL_NOW", "0");
		result.put("IS_DISCNT_MODEL_NOW", "0");
		result.put("DISCNT_MODEL_NOW_MONEY","0");
		IDataset infos = CSAppCall.call("SS.WidenetMoveSVC.getModelInfo", input);
    	for(int i=0;i<infos.size();i++){
    		//租借未归还的光猫
    		if(("0".equals(infos.getData(i).getString("RSRV_TAG1"))||"".equals(infos.getData(i).getString("RSRV_TAG1")))
    			&&(!"1".equals(infos.getData(i).getString("RSRV_STR9")))&&(!"2".equals(infos.getData(i).getString("RSRV_STR9"))))	{
    			String startDate = infos.getData(i).getString("START_DATE");
    			String threeYYAfter = SysDateMgr.getAddMonthsNowday(36, startDate);
    			int midMonths = SysDateMgr.monthIntervalYYYYMM(SysDateMgr.getSysDate(),threeYYAfter);
    			if(midMonths>0){
    				modelCnt++;
                	isOldFtth = true;
                	result.put("DISCNT_MODEL_NOW_MONEY", infos.getData(i).getString("RSRV_STR2"));
    			}
        		if("1".equals(infos.getData(i).getString("RSRV_TAG3"))){
        			result.put("IS_DISCNT_MODEL_NOW", "1");
        		}
        		result.put("IS_USE_MODEL_NOW", "1");
    		}else if("0".equals(infos.getData(i).getString("RSRV_TAG1"))||"".equals(infos.getData(i).getString("RSRV_TAG1"))
    				&&(("1".equals(infos.getData(i).getString("RSRV_STR9")))||("2".equals(infos.getData(i).getString("RSRV_STR9"))))){
    			modelNotReturn ++;
    		}
    	}
    	result.put("MODEL_CNT", modelCnt);
    	result.put("IS_OLD_FTTH", isOldFtth);
    	result.put("MODEL_NOT_RETURN", modelNotReturn);
    	return result;
	}

	
	/**是否预约了其他产品
     * @param  USER_ID  , TRADE_TYPE_CODE
     * @return IS_CHG_OTHER   :  是否预定了其他产品  (0:无 ， 1：是)
     *         IS_HAS_YEAR_DISCNT:  是否有包年活动   (0:无 ， 1：是)
     */
	public IData chkProductChgRule (IData input) throws Exception
	{
		WidenetMoveBean Bean = BeanManager.createBean(WidenetMoveBean.class);
		IDataset results =Bean.chkProductChgRule(input); 
		return results.getData(0);
	}
	
	/**
	 * 条件判断  当 IS_MODEL_FTTH = 1 时  
	 * 获取光猫列表接口
	 */
	public IDataset getModelList(IData input) throws Exception
	{
		boolean freeRight = false;// 赠送光猫权限查询
		boolean selfRight = false;// 自备模式权限
		//光猫模式 权限控制
		if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(),"FTTH_FREE_RIGHT")) 
		{
			freeRight = true;
		}
		
		if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(),"WIDE_MODEM_STYLE_3")) {
			selfRight = true;
		} 
		
		IDataset resultData = StaticUtil.getStaticList("WIDE_MODEM_STYLE");
	
		for(int i = 0 ; i<resultData.size() ; i++)
		{
			IData result = resultData.getData(i);
			
			//2 表示赠送光猫
			if("2".equals(result.getString("DATA_ID")))
			{
				//如果没有赠送光猫权限
				if(!freeRight)
				{
					resultData.remove(result);
					i--;
				}
			}
			//3 表示自备光猫
			else if("3".equals(result.getString("DATA_ID")))
			{
				//如果没有自备光猫权限
				if(!selfRight)
				{
					resultData.remove(result);
					i--;
				}
			}
		}
		
		return resultData;
	}
	
	/**
	 * 获取宽带类型   by openType
	 * @param openType
	 * @return productmode ，PRODUCT_MODE_NAME
	 * @throws Exception 
	 */
	public IData getProductModeByOpenType(String openType) throws Exception
	{
		IData data = new DataMap();
		data.put(Route.ROUTE_EPARCHY_CODE,getTradeEparchyCode());
		IDataset prodModes = CSAppCall.call("SS.WidenetMoveSVC.showProdMode", data);
        for(int i=0;i<prodModes.size();i++){
        	if(prodModes.getData(i).getString("PARA_CODE3", "").equals(openType))
        	{
        		data.put("PRODUCT_MODE", prodModes.getData(i).getString("PARA_CODE2", ""));
        		data.put("PRODUCT_MODE_NAME", prodModes.getData(i).getString("PARA_CODE3", ""));
        		break;
        	}
        }
        return data;
        
	}
	
	/**
	 * 获取宽带类型   by widetype
	 * @param widetype
	 * @return productmode
	 * @throws Exception 
	 */
	public String getProductModeByWideType(String widetype)
	{
		String productmode="";
		if ("1".equals(widetype))
        {
    		productmode="07";
        }
    	else if ("2".equals(widetype))
        {
        	productmode="09";
        }
    	else if ("3".equals(widetype))
        {
        	productmode="11";
        }
    	else if ("5".equals(widetype))
        {
        	productmode="11"; //移动FTTH与铁通FTTH合并，使用同一套产品
        }
        else if ("6".equals(widetype))
        {
        	productmode="07"; //移动FTTB与铁通FTTB合并，使用同一套产品
        }
		return productmode;
	}
	
	/**
	 * 营销活动列表接口
	 * @return
	 * @throws Exception 
	 */
	public IDataset getSaleActiveList(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER");
		IDataUtil.chkParam(input, "PRODUCT_ID");
		IDataUtil.chkParam(input, "PRODUCT_MODE");
		
		//将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
		getVisit().setInModeCode("0");
		input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
		
		String newProductMode = input.getString("PRODUCT_MODE");
		String newProductId= input.getString("PRODUCT_ID");
		String serialNumber= input.getString("SERIAL_NUMBER");
		
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);
		data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
		IDataset userInfos = CSAppCall.call("SS.WidenetMoveSVC.getUserInfoBySerial", data);
		if (IDataUtil.isEmpty(userInfos)) 
		{
			CSAppException.appError("", "通过该服务号码查询不到有效的用户信息！");
		}
		
		IDataset resultData = new DatasetList();
		String eparchyCode = userInfos.getData(0).getString("EPARCHY_CODE");
		
		String userId =  userInfos.getData(0).getString("USER_ID");
		input.put("NEW_PRODUCT_MODE", newProductMode);
		input.put("USER_ID",userId);
		
		//查询用户当前主产品信息
		IData oldPorductInfo = UcaInfoQry.qryMainProdInfoByUserId(input.getString("USER_ID"));
		String oldProductId = oldPorductInfo.getString("PRODUCT_ID");
		
		//获取宽带产品信息
		IData wideNetData = new DataMap();
		wideNetData.put("NEW_PRODUCT_MODE", newProductMode);
		wideNetData.put("USER_ID", userId);
		wideNetData.put("SERIAL_NUMBER", serialNumber);
		wideNetData.put("IS_BUSINESS_WIDE", "1");
		wideNetData.put("TRADE_TYPE_CODE", "606");
		IDataset products = getWidenetProductInfo(wideNetData);
		IData product = products.getData(0);
		
		int newRate = 0; 
		int oldRate = 0 ;
		for(int i=0;i<products.size();i++){
        	if(oldProductId.equals(products.getData(i).getString("PRODUCT_ID")))
        	{	
        		oldRate = products.getData(i).getInt("WIDE_RATE",0);
        	}
        	if(newProductId.equals(products.getData(i).getString("PRODUCT_ID")))
        	{
        		newRate = products.getData(i).getInt("WIDE_RATE",0);
        	}
        }
		
		String hasYearActive = product.getString("HAS_YEAR_ACTIVE", ""); // 包年营销活动
		String hasEndYearActive = product.getString("HAS_End_YEAR_ACTIVE", "");
		String hasYearDiscnt = product.getString("IS_HAS_YEAR_DISCNT", ""); // 包年套餐(包年套餐只可以办理包年营销活动)
		String hasEffActive = product.getString("HAS_EFF_ACTIVE", "");
		String hasEffYear = product.getString("HAS_EFF_YEAR", "");
		
		if(  ("1".equals(hasEffActive)||"1".equals(hasEffYear)||("1".equals(hasYearActive)&&"1".equals(hasEndYearActive)) )&&
				( oldRate != 0 )&& ( newRate != 0 )&& oldRate > newRate )
		{	
			String errorInfo = "您已经办理过包年套餐或者未到期的营销活动，不能选择低档套餐，请重新选择同档或更高档的套餐！";
			CSAppException.appError("-1", errorInfo);
		}
		// 获取  SaleActive 营销活动列表
		IDataset resus = getSaleActiveByProdId(input, newProductId,eparchyCode, hasYearActive, hasEndYearActive, hasYearDiscnt);
		for(int i = 0 ;i<resus.size();i++)
		{
			IData dataSale = new DataMap();
			dataSale.put("PACKAGE_NAME", resus.getData(i).getString("PARA_CODE3"));
			dataSale.put("PACKAGE_ID", resus.getData(i).getString("PARA_CODE5"));
			resultData.add(dataSale);
		}
		return resultData;
	}
	
	/**
	 * 判断是否需要做产品变更
	 * @param SERIAL_NUMBER,PRODUCT_MODE
	 * @throws Exception 
	 */
	public IData checkNeedChangeProduct(IData input) throws Exception
	{
		String newProductMode = input.getString("PRODUCT_MODE");
		String SerialNumber = input.getString("SERIAL_NUMBER");
		if (!SerialNumber.startsWith("KD_")) 
		{
			SerialNumber =  "KD_"+SerialNumber;
		}
		//获取用户宽带信息（TF_F_USER_WIDENET）
		IDataset userWides = WidenetInfoQry.getUserWidenetInfoBySerialNumber(SerialNumber);
		if ( IDataUtil.isEmpty(userWides) )
		{
			CSAppException.appError("-1", "通过该服务号码查询不到有效的宽带资料信息！");
		}
		IData resultData = new DataMap();
		
		//获取用户wideType
		String oldWideType =  userWides.getData(0).getString("RSRV_STR2");
		
		//获取用户ordProductMode
		String oldProductMode = getProductModeByWideType(oldWideType);
		String isNeedChgProd = "";
		if (!oldProductMode.equals(newProductMode)) 
		{
			isNeedChgProd =  "1";  //需要做产品变更
		} 
		else 
		{
			isNeedChgProd = "0";  //不需要
		}
		resultData.put("IS_NEED_CHANGE_PRODUCT", isNeedChgProd);
		return resultData;
	}
	
	 /**
	 * 查验光猫
	 * @param IS_NEW_FTTH,IS_OLD_FTTH ,IS_USED_FTTH ,AREA_CODE,SERIAL_NUMBER
	 * @return
	 * @throws Exception 
	 */
	public IData checkExchangeModel(IData input) throws Exception
	{	
		IData resultData = new DataMap();
		boolean isNewFtth = input.getBoolean("IS_NEW_FTTH");
		boolean isOldFtth = input.getBoolean("IS_OLD_FTTH");
		boolean isUsedFtth = input.getBoolean("IS_USED_FTTH");
		
		String SerialNumber = input.getString("SERIAL_NUMBER");
		if (!SerialNumber.startsWith("KD_")) 
		{
			SerialNumber = "KD_"+ SerialNumber;
		}
		/**
		 * 获取用户宽带信息（TF_F_USER_WIDENET）
		 * @param SERIAL_NUMBER
		 */
		IDataset userWides = WidenetInfoQry.getUserWidenetInfoBySerialNumber(SerialNumber);
		if (IDataUtil.isEmpty(userWides)) 
		{
			CSAppException.appError("-1", "通过该服务号码查询不到有效的宽带资料信息！");
		}
		
		String showModelInfo = "";
		String isExchangeModel = "6";
		String isModelFtth = "";
		String otherAreaFlag  = "";  
		if((isNewFtth&&isOldFtth)||(isNewFtth&&isUsedFtth)){
            String newAreaCode = input.getString("AREA_CODE", "");
            String oldAreaCode = userWides.getData(0).getString("RSRV_STR4", ""); 
            String newModelCode = "";
            String oldModelCode = "";
            IData data2 = new DataMap();
            data2.put("DATA_ID", newAreaCode);
            data2.put("TYPE_ID", "WIDENET_COP_MODEL_CODE");
            data2.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
            IDataset oldAreaModels = CSAppCall.call("SS.WidenetMoveSVC.getStaticInfoOnly", data2);
            data2.put("DATA_ID", oldAreaCode);
            IDataset newAreaModels = CSAppCall.call("SS.WidenetMoveSVC.getStaticInfoOnly", data2);
            
            newModelCode = newAreaModels.getData(0).getString("DATA_VALUE","");
            oldModelCode = oldAreaModels.getData(0).getString("DATA_VALUE","");
            
            /**
             * cxy2 存量（有光猫记录和无光猫记录）
             * 1、存量（有光猫记录和无光猫记录）用户移机不跨业务区，不可再申领光猫申领。
             * 2、用户移机跨业务区，按NGBOSS中“光猫品牌和业务区”关系判断：   
			（1）若属于不同厂家，则需必选光猫；   
			（2）若属于相同厂家，则不能选择再次申领光猫。  
             * */
            if(newAreaCode.equals(oldAreaCode)){
            	otherAreaFlag =  "FALSE" ;//不跨区，为了不影响其他判断。
            	isModelFtth = "0";//不跨区不允许再次申领光猫
            }else{
            	otherAreaFlag = "TRUE" ;//跨区，为了不影响其他判断。
            	if(newModelCode.equals(oldModelCode)){  
            		isModelFtth = "0";
            	}else{
            		isModelFtth = "1";
            	}
            }
            if(newModelCode.equals(oldModelCode)){ 
            	if(isNewFtth&&isOldFtth)            	
            		isExchangeModel = "1";
            	else
            		isExchangeModel = "5";
            }else if(isNewFtth&&isOldFtth){
            	isExchangeModel = "0";
            }else{//以前的产品是FTTH，但是other表没有光猫记录
            	isExchangeModel = "4";
            }
        }else if(isOldFtth){
        	isExchangeModel = "2";
        }else if(isNewFtth){
        	isExchangeModel = "3";
        }
		resultData.put("IS_MODEL_FTTH", isModelFtth);
		resultData.put("OTHER_AREA_FLAG", otherAreaFlag);
		resultData.put("IS_EXCHANGE_MODEL", isExchangeModel);
		resultData.put("SHOW_MODEL_INFO", showModelInfo);
		return resultData;
	}
}
