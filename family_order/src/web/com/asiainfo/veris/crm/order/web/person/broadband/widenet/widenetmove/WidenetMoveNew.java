package com.asiainfo.veris.crm.order.web.person.broadband.widenet.widenetmove;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WidenetMoveNew extends PersonBasePage
{
	private static final Logger log = Logger.getLogger(WidenetMoveNew.class);
    public void checkSerialNumber(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put("TRADE_TYPE_CODE", "606");
        CSViewCall.call(this, "SS.WidenetMoveSVC.judgeIsCanMove", data);
        IDataset dataset = CSViewCall.call(this, "SS.WidenetMoveSVC.checkSerialNumber", data);
        IDataset acctInfos = dataset.getData(0).getDataset("ALL_ACCT");
        setAllAcct(acctInfos);
        setSaleActiveFee("0");
        setAjax(dataset);
        setNewWideInfo(dataset.getData(0));
    }
    
    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData(); 
        
        //有IMS固话信息，需先做IMS固话拆机
        IData param = new DataMap();
        String serialNumber = data.getString("SERIAL_NUMBER");
        if(StringUtils.isEmpty(serialNumber))
        {
        	serialNumber = data.getString("AUTH_SERIAL_NUMBER");
        }
        if(serialNumber.startsWith("KD_"))
        {
        	serialNumber = serialNumber.replace("KD_", "");
        }
        param.put("SERIAL_NUMBER", serialNumber);
        param.put(Route.ROUTE_EPARCHY_CODE, "0898");
        param.put("EPARCHY_CODE", "0898");
    	IData imsInfo = null;
        try {
        	imsInfo = CSViewCall.callone(this,"SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", param);
		} catch (Exception e) {
		}
		if (IDataUtil.isNotEmpty(imsInfo)) {
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "该用户存在IMS固话，请先办理IMS固话拆机！");
		}
			
        
        
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        if (IDataUtil.isEmpty(dataset))
        {
            CSViewException.apperr(WidenetException.CRM_WIDENET_4);
        }
        
        //1：移动GPON，2：ADSL，3：移动FTTH，4：校园宽带，5：TTADSL海南铁通FTTH，6：海南铁通FTTB
        String wideTypeCode = dataset.getData(0).getString("RSRV_STR2");
        
        if ("4".equals(wideTypeCode))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "校园宽带不能在此办理移机业务！");
        }
        
        
        IDataset idsPriv = CSViewCall.call(this, "SS.WidenetMoveSVC.judgeIsCanMove", data);        
        String isBusiness = "0";
        IDataset userInfos = CSViewCall.call(this, "SS.WidenetMoveSVC.getUserInfoBySerial", data);
        if(userInfos!=null&&userInfos.size()>0){
        	if("BNBD".equals(userInfos.getData(0).getString("RSRV_STR10"))){
        		isBusiness = "1";
        	}
        }
        
        IData result = loadProductInfo(cycle);
        String tradeTypeCode = "606",wideType="GPON";
        //1：移动GPON，2：ADSL，3：移动FTTH，4：校园宽带，5：TTADSL海南铁通FTTH，6：海南铁通FTTB

        IData wideInfo = dataset.getData(0);
        //根据用户宽带信息，重置TRADE_TYPE_CODE和WIDE_TYPE
        wideInfo.put("TRADE_TYPE_CODE", tradeTypeCode);
        wideInfo.put("WIDE_TYPE", wideType);
        setWideInfo(wideInfo);
        result.put("IS_BUSINESS_WIDE", isBusiness);
        result.put("USER_PRODUCT_NAME", result.getString("USER_PRODUCT_NAME"));
        result.put("SALE_PRODUCT_NAME", result.getString("SALE_PRODUCT_NAME"));
        result.put("SALE_PACKAGE_NAME", result.getString("SALE_PACKAGE_NAME"));
        if(IDataUtil.isNotEmpty(idsPriv))
        {
        	IData idPriv = idsPriv.first();
        	String strPriv = idPriv.getString("WIDENETMOVE_FIRST", "0");
        	result.put("WIDENETMOVE_FIRST", strPriv);
        }
        
        // 预约施工时间只能选择48小时之后
		String minDate = SysDateMgr.getAddHoursDate(
				SysDateMgr.getSysTime(), 48); // SysDateMgr.addDays(2);
		result.put("MIN", minDate);
        
        setUserProdInfo(result);
        /*String productId = result.getString("PRODUCT_ID");
        String eparchyCode = data.getString("ROUTE_EPARCHY_CODE");
        IDataset sales = new DatasetList(); 
        if(!"1".equals(isBusiness)){
        	sales = getSaleActiveByProdId(cycle, productId, eparchyCode);
        }
        setSaleActiveList(sales);*/
        
        IDataset resus = new DatasetList();
        setSaleActiveFee("0");
        resus.add(result);
        setAjax(resus);
    }
    
    public IData loadProductInfo(IRequestCycle cycle)throws Exception
    {
    	IData data = getData();
    	IDataset resus = CSViewCall.call(this, "SS.WidenetMoveSVC.getUserProductInfo", data);
        return resus.getData(0);
    }

    /**
     * 查询后设置页面信息
     */
    public void initProductChg(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset resus = new DatasetList();
        IData result = new DataMap();
        result.put("IS_SAME_DEVICEID", "0");
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        
        String allStrNum = data.getString("SERIAL_NUMBER");
    	String seriNumber = data.getString("SERIAL_NUMBER");
    	if(seriNumber.startsWith("KD_")){
    		seriNumber = seriNumber.substring(3);
    	}
    	data.put("SERIAL_NUMBER", seriNumber);
    	/*
        //检验选择标准地址后的设备ID和当前使用的设备ID是否一致
        String deviceId = data.getString("DEVICE_ID");
    	IDataset userDeviceIdInfos = CSViewCall.call(this, "PB.AddressManageSvc.queryDeviceByAccount", data);
        if(userDeviceIdInfos!=null&&userDeviceIdInfos.size()>0){
        	for(int i=0;i<userDeviceIdInfos.size();i++){
        		IData deviceInfo = userDeviceIdInfos.getData(i);
        		if(deviceId.equals(deviceInfo.getString("DEVICE_ID"))){
        			result.put("IS_SAME_DEVICEID", "1");
                    resus.add(result);
                    setAjax(resus);
                    return ;
        		}
        	}
        }*/
    	data.put("SERIAL_NUMBER", allStrNum);
        
        result = loadProductInfo(cycle);
        result.put("IS_SAME_DEVICEID", "0");
        result.put("IS_CHG_OTHER", "0");
        result.put("IS_HAS_YEAR_DISCNT", "0");
        result.put("IS_ADSL_LIMIT", "0");
        String oldProductMode = result.getString("PRODUCT_MODE");
        String productId = result.getString("PRODUCT_ID");
        String eparchyCode = data.getString("ROUTE_EPARCHY_CODE");
        data.put("PRODUCT_ID", productId);
        String newProductMode = "";
        String tradeTypeCode = "606",wideType="GPON";
        IData userWidenet = dataset.getData(0);
        //1：移动GPON，2：ADSL，3：移动FTTH，4：校园宽带，5：TTADSL海南铁通FTTH，6：海南铁通FTTB

        IData wideInfo = dataset.getData(0);
        //根据用户宽带信息，重置TRADE_TYPE_CODE和WIDE_TYPE
        wideInfo.put("TRADE_TYPE_CODE", tradeTypeCode);
        wideInfo.put("WIDE_TYPE", wideType);
        setWideInfo(wideInfo);
        
    	String isBusiness = data.getString("IS_BUSINESS_WIDE","");
        IDataset prodModes = CSViewCall.call(this, "SS.WidenetMoveSVC.showProdMode", data);
        setProductModeList(prodModes);
        boolean isFtth = false;
        boolean isOldFtth = false;
        boolean isUsedFtth = false;
        String firstRentMoney="",secondRendMoney="",purchaseMoney="",moveFtthMoney="";
        String isNewAdsl="0",isOldAdsl="0",newWideType="";
        
        String openType = data.getString("OPEN_TYPE", "");
        for(int i=0;i<prodModes.size();i++){
        	if(prodModes.getData(i).getString("PARA_CODE3", "").equals(openType)){
        		data.put("PRODUCT_MODE", prodModes.getData(i).getString("PARA_CODE2", ""));
        		data.put("NEW_PRODUCT_MODE", prodModes.getData(i).getString("PARA_CODE2", ""));
        		prodModes.getData(i).put("PRODUCT_MODE", prodModes.getData(i).getString("PARA_CODE2", ""));
        		newProductMode = prodModes.getData(i).getString("PARA_CODE2", "");
            	newWideType = prodModes.getData(i).getString("PARA_CODE9", "");
        		IDataset prodMode = new DatasetList();
        		prodMode.add(prodModes.getData(i));
                setProductModeList(prodMode);
                if(prodModes.getData(i).getString("PARA_CODE4", "").equals("FTTH")){
                	purchaseMoney = prodModes.getData(i).getString("PARA_CODE5", "");
                	firstRentMoney = prodModes.getData(i).getString("PARA_CODE6", "");
                	secondRendMoney = prodModes.getData(i).getString("PARA_CODE7", "");
                	moveFtthMoney = prodModes.getData(i).getString("PARA_CODE11", "");
                	isFtth = true;
                }
                if(prodModes.getData(i).getString("PARA_CODE8", "").equals("ADSL")){
                	isNewAdsl="1";
                }
        	}

        	if(prodModes.getData(i).getString("PARA_CODE2", "").equals(oldProductMode)){
                if(prodModes.getData(i).getString("PARA_CODE8", "").equals("ADSL")){
                	isOldAdsl="1";
                }
                if(prodModes.getData(i).getString("PARA_CODE4", "").equals("FTTH")){
                    isUsedFtth = true;
                }
        	}
        	
        	/*if("1".equals(isBusiness)&&"BNBD".equals(prodModes.getData(i).getString("PARA_CODE3", ""))){
        		prodBnbdMode.add(prodModes.getData(i));
        	}*/
        }
        /*if("1".equals(isBusiness)){
        	setProductModeList(prodBnbdMode);
        }*/

        if(!newProductMode.equals(oldProductMode)){
        	result.put("IS_NEED_CHG_PROD", "1");
        }else{
        	result.put("IS_NEED_CHG_PROD", "0");
        }

        //不允许从非ADSL转为ADSL
        if(("0".equals(isOldAdsl))&&("1".equals(isNewAdsl))){
            result.put("IS_ADSL_LIMIT", "1");
            resus.add(result);
            setAjax(resus);
            return ;
        }

    	//查看用户是否租借过光猫
        int modelCnt = 0,modelNotReturn = 0;
        result.put("IS_DISCNT_MODEL_NOW", "0");
        result.put("IS_USE_MODEL_NOW", "0");
        result.put("DISCNT_MODEL_NOW_MONEY", "0");
        result.put("IS_DISCNT_MODEL_BEFORE", "0");
        result.put("NUM_DISCNT_MODEL_BEFORE", "0");
        IDataset infos = CSViewCall.call(this, "SS.WidenetMoveSVC.getModelInfo", data);
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
    	
        IData modelInfo = new DataMap();
        modelInfo.put("IS_FTTH", "0");
        modelInfo.put("IS_PRE_EFF", "1");
        modelInfo.put("MODEL_CNT", modelCnt);
        modelInfo.put("MODEL_NOT_RETURN", modelNotReturn);
        modelInfo.put("PRODUCT_MODE", data.getString("PRODUCT_MODE"));
        modelInfo.put("OLD_PROD_MODE", oldProductMode);
        if(isFtth){
            modelInfo.put("IS_FTTH", "1");
            //取commpara表中6131配置作为光猫的押金和购买金额
            data.put("EPARCHY_CODE", eparchyCode);
            data.put("PARAM_ATTR", "6131");
            data.put("PARAM_CODE", "1");
            IDataset results = CSViewCall.call(this, "SS.WidenetMoveSVC.getSaleActiveComm", data);
            if(results!=null&&results.size()>0) secondRendMoney = results.getData(0).getString("PARA_CODE1");
            data.put("PARAM_CODE", "2");
            results = CSViewCall.call(this, "SS.WidenetMoveSVC.getSaleActiveComm", data);
            if(results!=null&&results.size()>0) firstRentMoney = results.getData(0).getString("PARA_CODE1");
            data.put("PARAM_CODE", "3");
            results = CSViewCall.call(this, "SS.WidenetMoveSVC.getSaleActiveComm", data);
            if(results!=null&&results.size()>0) purchaseMoney = results.getData(0).getString("PARA_CODE1");

        	
        	String cash = "0";
        	if(!"1".equals(isBusiness)){
            	String serialNumber = data.getString("SERIAL_NUMBER");
        		IData param=new DataMap();
            	if(serialNumber.startsWith("KD_")){
            		serialNumber = serialNumber.substring(3);
            	}
        		param.put("SERIAL_NUMBER", serialNumber);
                IDataset accts = CSViewCall.call(this, "SS.WidenetMoveSVC.getUserBalance", data);
    	    	cash=accts.getData(0).getString("CASH_BALANCE");
        	}else{
        		modelCnt = 0;cash = "0";firstRentMoney = "0";secondRendMoney = "0";purchaseMoney = "0";
        	}
        	//获取用户现金预存金额
            modelInfo.put("MODEL_CNT", modelCnt);
            modelInfo.put("CASH_BALANCE", cash);
            modelInfo.put("FIRST_RENT", firstRentMoney);
            modelInfo.put("SECOND_RENT", secondRendMoney);
            modelInfo.put("PURCHASE_MONEY", purchaseMoney);
            modelInfo.put("MOVE_FTTH_MONEY", moveFtthMoney);
            /**
             * 光猫模式 权限控制
             * */  
            String freeRight = "";//赠送光猫权限查询
            if(StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT")){
            	freeRight="1";
            }else{
            	freeRight="0";
            }
            
            String selfRight = "";//自备模式权限
            if(StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "WIDE_MODEM_STYLE_3")){
            	selfRight="1";
            }else{
            	selfRight="0";
            }
            result.put("MODEM_FREE_RIGHT", freeRight);
            result.put("MODEM_SELF_RIGHT", selfRight);
        }
        
        //是否曾经有过光猫优惠
        IDataset disModelBef = CSViewCall.call(this, "SS.WidenetMoveSVC.getDiscntModelBef", data);
        if(disModelBef!=null&&disModelBef.size()>0){
            result.put("IS_DISCNT_MODEL_BEFORE", "1");
            result.put("NUM_DISCNT_MODEL_BEFORE", "" + disModelBef.size());
        }
        
        /*if("1".equals(isBusiness)){
        	//商务宽带处理
            modelInfo.put("PRODUCT_MODE", "18");
        }*/
        
        /*if("1".equals(data.getString("IS_BUSINESS_WIDE",""))){
        	//商务宽带处理，PRODCT_MODE写死为18
            modelInfo.put("NEW_PRODUCT_MODE", "18");
            data.put("NEW_PRODUCT_MODE", "18");
        }*/
        IDataset products = CSViewCall.call(this, "SS.WidenetMoveSVC.getWidenetProductInfo", data);
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("PARAM_ATTR", "210");
        data.put("PARAM_CODE", "IS_JUDGE_YEAR_DISCNT");
        //IDataset results = CSViewCall.call(this, "SS.WidenetMoveSVC.getSaleActiveComm", data);
        //if (IDataUtil.isNotEmpty(results)) isJudgeYearDiscnt = results.getData(0).getString("PARA_CODE1","");
        if(products!=null&&products.size()>0){
        	if(("1".equals(products.getData(0).getString("IS_CHG_OTHER")))){
                result.put("IS_CHG_OTHER", products.getData(0).getString("IS_CHG_OTHER",""));
                //if("1".equals(isJudgeYearDiscnt))
                //if("1".equals(products.getData(0).getString("IS_HAS_YEAR_DISCNT")))
                result.put("IS_HAS_YEAR_DISCNT", products.getData(0).getString("IS_HAS_YEAR_DISCNT","0"));
                result.put("HAS_YEAR_ACTIVE", products.getData(0).getString("HAS_YEAR_ACTIVE","0"));
                result.put("HAS_End_YEAR_ACTIVE", products.getData(0).getString("HAS_End_YEAR_ACTIVE","0"));
                
                resus.add(result);
                setAjax(resus);
                return ;
        	}
            result.put("IS_HAS_YEAR_DISCNT", products.getData(0).getString("IS_HAS_YEAR_DISCNT","0"));
            result.put("HAS_YEAR_ACTIVE", products.getData(0).getString("HAS_YEAR_ACTIVE","0"));
            result.put("HAS_End_YEAR_ACTIVE", products.getData(0).getString("HAS_End_YEAR_ACTIVE","0"));
            result.put("HAS_MONTH_DISCNT", products.getData(0).getString("HAS_MONTH_DISCNT","0"));
        }
        //log.info("("******CXY******2*products="+products);
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), products);
        //log.info("("******CXY******3*products="+products);
        /**
         * 1000M宽带 chenxy3
         * 去除地址中不适合装1000M的产品
         * */
        String tag1000=data.getString("TAG1000","");
        if(tag1000==null || "".equals(tag1000) || !"1000".equals(tag1000)){ 
        	for(int k=0;k<products.size();k++){
        		String prodId=products.getData(k).getString("PRODUCT_ID");
        		if("20171000".equals(prodId)){
        			dataset.remove(k);
        			break;
        		}
        	} 
        }
        setProductList(products);
        if("1".equals(isBusiness)){
        	//商务宽带处理
            modelInfo.put("NEW_PRODUCT_MODE", newProductMode);
        }

        int oldRate = 0;
        IData data2 = new DataMap();
        data2.put("USER_ID", data.getString("USER_ID"));
        data2.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        data2.put("ROUTE_EPARCHY_CODE", data.getString("ROUTE_EPARCHY_CODE"));
        data2.put("NEW_PRODUCT_MODE", oldProductMode);
        IDataset productOlds = CSViewCall.call(this, "SS.WidenetMoveSVC.getWidenetProductInfo", data2);
        for(int i=0;i<productOlds.size();i++){
        	if(productId.equals(productOlds.getData(i).getString("PRODUCT_ID")))
        		oldRate = productOlds.getData(i).getInt("WIDE_RATE",0);
        }

        result.put("IS_FTTH", modelInfo.getString("IS_FTTH"));
        result.put("NEW_WIDE_TYPE", newWideType);
        result.put("IS_EXCHANGE_MODEL", "6");
        result.put("HAS_EFF_ACTIVE", products.getData(0).getString("HAS_EFF_ACTIVE",""));
        result.put("HAS_EFF_YEAR", products.getData(0).getString("HAS_EFF_YEAR",""));
        result.put("HAS_YEAR_ACTIVE", products.getData(0).getString("HAS_YEAR_ACTIVE","0"));
        result.put("HAS_End_YEAR_ACTIVE", products.getData(0).getString("HAS_End_YEAR_ACTIVE","0"));
        result.put("OLD_PRODUCT_RATE", oldRate);
        if((isFtth&&isOldFtth)||(isFtth&&isUsedFtth)){
            String newAreaCode = data.getString("AREA_CODE", "");
            String oldAreaCode = userWidenet.getString("RSRV_STR4", ""); 
            String newModelCode = "";
            String oldMOdelCode = "";
            data2.put("DATA_ID", newAreaCode);
            data2.put("TYPE_ID", "WIDENET_COP_MODEL_CODE");
            IDataset oldAreaModels = CSViewCall.call(this, "SS.WidenetMoveSVC.getStaticInfoOnly", data2);
            data2.put("DATA_ID", oldAreaCode);
            IDataset newAreaModels = CSViewCall.call(this, "SS.WidenetMoveSVC.getStaticInfoOnly", data2);
            
            newModelCode = newAreaModels.getData(0).getString("DATA_VALUE","");
            oldMOdelCode = oldAreaModels.getData(0).getString("DATA_VALUE","");
            
            /**
             * cxy2 存量（有光猫记录和无光猫记录）
             * 1、存量（有光猫记录和无光猫记录）用户移机不跨业务区，不可再申领光猫申领。
             * 2、用户移机跨业务区，按NGBOSS中“光猫品牌和业务区”关系判断：   
			（1）若属于不同厂家，则需必选光猫；   
			（2）若属于相同厂家，则不能选择再次申领光猫。  
             * */
            if(newAreaCode.equals(oldAreaCode)){
            	result.put("OTHER_AREA_FLAG", "FALSE");//不跨区，为了不影响其他判断。
            	modelInfo.put("IS_FTTH", "0");//不跨区不允许再次申领光猫
            }else{
            	result.put("OTHER_AREA_FLAG", "TRUE");//跨区，为了不影响其他判断。
            	if(newModelCode.equals(oldMOdelCode)){  
            		modelInfo.put("IS_FTTH", "0");
            	}else{
            		modelInfo.put("IS_FTTH", "1");
            	}
            }
            
            if(newModelCode.equals(oldMOdelCode)){ 
            	if(isFtth&&isOldFtth)            	
            		result.put("IS_EXCHANGE_MODEL", "1");
            	else
            		result.put("IS_EXCHANGE_MODEL", "5");
            }else if(isFtth&&isOldFtth){
                result.put("IS_EXCHANGE_MODEL", "0");
            }else{//以前的产品是FTTH，但是other表没有光猫记录
            	result.put("IS_EXCHANGE_MODEL", "4");
            }
        }else if(isOldFtth){
            result.put("IS_EXCHANGE_MODEL", "2");
        }else if(isFtth){
            result.put("IS_EXCHANGE_MODEL", "3");
        }

        /*IDataset sales = new DatasetList(); 
        if(!"1".equals(isBusiness)){
        	sales = getSaleActiveByProdId(cycle, productId, eparchyCode);
        }
        setSaleActiveList(sales);*/

        setSaleActiveFee("0");
        result.put("IS_FTTH", modelInfo.getString("IS_FTTH"));
        //log.info("("******CXY*******result="+result);
        resus.add(result);
        setModelInfo(modelInfo);
        setAjax(resus);
    }

    public void getSaleActiveList(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String newProductId = data.getString("NEW_PRODUCT_ID");
        String userProductId = data.getString("USER_PRODUCT_ID");
        String eparchyCode = data.getString("EPARCHY_CODE");
        String hasYearActive = data.getString("HAS_YEAR_ACTIVE","");
        String hasEndYearActive = data.getString("HAS_End_YEAR_ACTIVE","");
        String hasYearDiscnt = data.getString("IS_HAS_YEAR_DISCNT","");
        
        IDataset resus = getSaleActiveByProdId(cycle, newProductId, eparchyCode, hasYearActive, hasEndYearActive, hasYearDiscnt);
        setSaleActiveList(resus);
        
        //查看用户是否已经办理过营销活动，如果已经办理过，此处需要提示用户重新办理
        IDataset resu = CSViewCall.call(this, "SS.WidenetMoveSVC.isUserSaleActive", data);
        boolean isDeal = resu.getData(0).getBoolean("IS_DEAL");
        if(isDeal&&(!(newProductId.equals(userProductId)))) {
        	resu.getData(0).put("IS_HAS","1");
        }else{
        	resu.getData(0).put("IS_HAS","0");
        }

        setSaleActiveFee("0");
        setAjax(resu);
    }
    
    public IDataset getSaleActiveByProdId(IRequestCycle cycle,String prodId,String eparchyCode,String hasYearActive,String hasEndYearActive,String hasYearDiscnt) throws Exception
    {
        IData data = getData();
        IDataset actives = new DatasetList();
        String newProductId = prodId;
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("PARAM_ATTR", "178");
        data.put("PARAM_CODE", "600");
        IDataset results = CSViewCall.call(this, "SS.WidenetMoveSVC.getSaleActiveComm", data);
        
        //新增包年活动
        /*data.put("PARAM_CODE", "WIDE_YEAR_ACTIVE");
        IDataset yearActives = CSViewCall.call(this, "SS.WidenetMoveSVC.getSaleActiveComm", data);*/
        
        //包年套餐只可以办理包年营销活动
        if("1".equals(hasYearDiscnt)){
        	for(int i=0;i<results.size();i++){
        		IData active = results.getData(i);
        		if("WIDE_YEAR_ACTIVE".equals(active.getString("PARA_CODE7",""))){
        			actives.add(active);
        		}
        	}
        }else{
        	String productId = "",endMonth = "";
            data.put("DEAL_TYPE", "JUDGE_ACTIVE_INFO");
            IDataset dataset = CSViewCall.call(this, "SS.WidenetMoveSVC.getWideNetMoveInfo", data);
            if(IDataUtil.isNotEmpty(dataset)){
            	productId = dataset.getData(0).getString("PRODUCT_ID");
            	endMonth = dataset.getData(0).getString("IS_END_MONTH");
            }
        	if("1".equals(endMonth)){
        		String canProd = "";
                data.put("DEAL_TYPE", "JUDGE_ACTIVE_IS_CAN_CHG");
                IDataset activeChg = CSViewCall.call(this, "SS.WidenetMoveSVC.getWideNetMoveInfo", data);
        		for(int i=0;i<activeChg.size();i++){
        			if(productId.equals(activeChg.getData(i).getString("PARA_CODE4"))){
        				canProd = activeChg.getData(i).getString("PARA_CODE20");
        				break;
        			}
        		}
        		
        		if(canProd!=null&&!"".equals(canProd)){
        			String[] prods = canProd.split("\\|");
        			for(int i=0;i<prods.length;i++){
        				String newProd = prods[i];
        				for(int j=0;j<results.size();j++){
        					if(newProd.equals(results.getData(j).getString("PARA_CODE4"))){
        						actives.add(results.getData(j));
        					}
        				}
        			}
        		}else{
        			actives = results;
        		}
        	}else{
        		actives = results;
        	}
        }
        
        
        /*//包年活动最后一个月和包年套餐最后一个月可以办理所有营销活动（包年活动、宽带1+活动）（此处hasYearDiscnt为1表示包年套餐有并且不在最后一个月）
        //没有包年优惠和包年套餐的用户，可以办理所有的营销活动
        if(("1".equals(hasYearActive)&&"1".equals(hasEndYearActive)))
        
        if((!"1".equals(hasYearDiscnt)&&)
        		||(!"1".equals(hasYearDiscnt)&&(!"1".equals(hasYearActive)))){
        	actives = results;
        }*/
        
        IDataset resus = new DatasetList();
        for(int i=0;i<actives.size();i++){
        	IData result = actives.getData(i);
        	if(newProductId.equals(result.getString("PARA_CODE1"))){
        		resus.add(result);
        	}
        }
        
        return resus;
    }

    public void dealModelMoney(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        //String isDMB = data.getString("IS_DISCNT_MODEL_BEFORE");
        //String isDMN = data.getString("IS_DISCNT_MODEL_NOW");
        //String isUMN = data.getString("IS_USE_MODEL_NOW");
        //String dMNM = data.getString("DISCNT_MODEL_NOW_MONEY");
        //String ndmb = data.getString("NUM_DISCNT_MODEL_BEFORE");
        String packageId = data.getString("PACKAGE_ID");
        String isChgS = data.getString("IS_CHG_SALE");
        String firstRent = data.getString("FIRST_RENT");
        String secondRent = data.getString("SECOND_RENT");
        String moveFtthMoney = data.getString("MOVE_FTTH_MONEY");
        String activeName = data.getString("OLD_SALE_PACKAGE_NAME");
        //String oldProdId = data.getString("OLD_PROD_ID","");
        String newProdId = data.getString("NEW_PROD_ID","");
        String hasEndYearActive = data.getString("HAS_End_YEAR_ACTIVE","");
        
        //获取用户当前现金账户余额
        IDataset accts = CSViewCall.call(this, "SS.WidenetMoveSVC.getUserBalance", data);
    	String cash=accts.getData(0).getString("CASH_BALANCE");
        
        String modelShowInfo = "";
        modelShowInfo = "您在办理光猫租借，租借押金"+ Integer.parseInt(firstRent)/100 +"元，请先缴纳预存，以免押金金额不足！";
        /*if(!"1".equals(isDMB)){//以前没有享受过光猫优惠
        	modelShowInfo = "您在办理光猫租借，租借押金"+ Integer.parseInt(firstRent)/100 +"元，如果您办理营销活动，优惠减免后押金"+Integer.parseInt(secondRent)/100+"元，请先缴纳预存，以免押金金额不足！";
        	if("TRUE".equals(isChgS)&&packageId!=null&&!"".equals(packageId)){
        		modelShowInfo = "您在办理光猫租借，您已经选择营销活动，优惠减免后押金"+Integer.parseInt(secondRent)/100+"元，请先缴纳预存，以免押金金额不足！";
        		if(Integer.parseInt(secondRent)>Integer.parseInt(cash)){
        			modelShowInfo = "您在办理光猫租借，您已经选择营销活动，优惠减免后押金"+Integer.parseInt(secondRent)/100+"元，您当前账户余额为"+Integer.parseInt(cash)/100+"元，不足押金扣减，请先缴纳预存";
        		}
        	}
        	if(Integer.parseInt(firstRent)>Integer.parseInt(cash)){
        		modelShowInfo = "您在办理光猫租借，租借押金"+ Integer.parseInt(firstRent)/100 +"元,，您当前账户余额为"+Integer.parseInt(cash)/100+"元，不足押金扣减，请先缴纳预存；办理营销活动可以减免押金，减免后押金为"+Integer.parseInt(secondRent)/100+"元";
        	}
        }else if("1".equals(isDMN)&&Integer.parseInt(ndmb)<2){//当前正在享受过光猫优惠
        	modelShowInfo = "您在办理光猫租借，租借押金"+Integer.parseInt(dMNM)/100+"元！";
        	if(Integer.parseInt(dMNM)>Integer.parseInt(cash)){
        		modelShowInfo = "您在办理光猫租借，租借押金"+Integer.parseInt(dMNM)/100+"元！您当前账户余额为"+Integer.parseInt(cash)/100+"元，不足押金扣减，请先缴纳预存";
        	}
        }else{//以前享受过光猫优惠，或者移机情况下已经享受过两次光猫优惠
        	modelShowInfo = "您在办理光猫租借，租借押金"+Integer.parseInt(firstRent)/100+"元！";
        	if(Integer.parseInt(firstRent)>Integer.parseInt(cash)){
        		modelShowInfo = "您在办理光猫租借，租借押金"+Integer.parseInt(firstRent)/100+"元！您当前账户余额为"+Integer.parseInt(cash)/100+"元，不足押金扣减，请先缴纳预存";
        	}
        }*/
        
        /**根据局方要求，此处再写一次
         * a、非FTTH变FTTH，一定领光猫。光猫押金如已优惠申领过，则200。没优惠申领过，有活动则100，无则200.
         * b、FTTH变FTTH，新旧地区使用不同厂家光猫，一定领光猫。光猫押金100，不算优惠申领。
         * c、FTTH变FTTH，新旧地区使用同厂家光猫，可领可不领。领时光猫押金100，不算优惠申领。
         * d、是否领光猫务必正确通知到服开，服开在施工单务必正确展示给施工人员，推送给APP也要推送对。
         * **/
        String isExchangeModel = data.getString("IS_EXCHANGE_MODEL");
        String modelDeposit = "0";
        String isDependActive = "0";
        modelShowInfo = "您在办理光猫租借，需要冻结预存款"+ Integer.parseInt(firstRent)/100 +"元，请提前缴纳预存，以免预存款金额不足！";
        if("1".equals(isExchangeModel)||"0".equals(isExchangeModel)){//1 表示“新旧地区使用同厂家光猫”  0表示“新旧地区使用不同厂家光猫”
        	modelDeposit = moveFtthMoney;
        	modelShowInfo = "您在办理光猫租借，需要冻结预存款"+Integer.parseInt(moveFtthMoney)/100+"元！";
        	if(Integer.parseInt(moveFtthMoney)>Integer.parseInt(cash)){
    			modelShowInfo = "您在办理光猫租借，需要冻结预存款"+Integer.parseInt(moveFtthMoney)/100+"元，您当前账户余额为"+Integer.parseInt(cash)/100+"元，不足冻结预存款扣减，请提前缴纳预存！";
    		}
        }else{
        	//查看用户当前有没有生效的营销活动，营销活动对光猫押金进行优惠
            boolean hasActive = false;
            if(activeName!=null&&!"".equals(activeName)&&!"1".equals(hasEndYearActive))
            	hasActive = true;
        	
        	//if(!"1".equals(isDMB)){//以前没有享受过光猫优惠
            isDependActive = "1";
        	modelDeposit = firstRent;
        	modelShowInfo = "您在办理光猫租借，需要冻结预存款"+ Integer.parseInt(firstRent)/100 +"元，请提前缴纳预存，以免预存款金额不足！";
        	if(Integer.parseInt(firstRent)>Integer.parseInt(cash)){
        		modelShowInfo = "您在办理光猫租借，需要冻结预存款"+ Integer.parseInt(firstRent)/100 +"元,，您当前账户余额为"+Integer.parseInt(cash)/100+"元，不足冻结预存款扣减，请提前缴纳预存；办理营销活动可以减免冻结预存款金额，减免后冻结金额为"+Integer.parseInt(secondRent)/100+"元！";
        	}
        	if(("TRUE".equals(isChgS)&&packageId!=null&&!"".equals(packageId))||(hasActive&&"".equals(newProdId))){
            	modelDeposit = secondRent;
        		modelShowInfo = "您在办理光猫租借，您已经选择营销活动，优惠减免后需要冻结预存款"+Integer.parseInt(secondRent)/100+"元，请提前缴纳预存，以免预存款金额不足！";
        		if(Integer.parseInt(secondRent)>Integer.parseInt(cash)){
        			modelShowInfo = "您在办理光猫租借，您已经选择营销活动，优惠减免后需要冻结预存款"+Integer.parseInt(secondRent)/100+"元，您当前账户余额为"+Integer.parseInt(cash)/100+"元，不足冻结预存款扣减，请提前缴纳预存！";
        		}
        	}
            //}
        	/*else if("1".equals(isDMN)&&Integer.parseInt(ndmb)<2){//当前正在享受过光猫优惠
            	modelShowInfo = "您在办理光猫租借，租借押金"+Integer.parseInt(dMNM)/100+"元！";
            	if(Integer.parseInt(dMNM)>Integer.parseInt(cash)){
            		modelShowInfo = "您在办理光猫租借，租借押金"+Integer.parseInt(dMNM)/100+"元！您当前账户余额为"+Integer.parseInt(cash)/100+"元，不足押金扣减，请先缴纳预存";
            	}
            }
        	else{//以前享受过光猫优惠，或者移机情况下已经享受过两次光猫优惠
            	modelShowInfo = "您正在办理光猫租借，需要冻结预存款"+Integer.parseInt(firstRent)/100+"元！";
            	modelDeposit = firstRent;
            	if(Integer.parseInt(firstRent)>Integer.parseInt(cash)){
            		modelShowInfo = "您正在办理光猫租借，需要冻结预存款"+Integer.parseInt(firstRent)/100+"元！您当前账户余额为"+Integer.parseInt(cash)/100+"元，不足冻结预存款扣减，请提前缴纳预存！";
            	}
            }*/
        }
        
        int modelNotReturn = 0;
        IDataset infos = CSViewCall.call(this, "SS.WidenetMoveSVC.getModelInfo", data);
    	for(int i=0;i<infos.size();i++){
    		//租借未归还的光猫
    		if(("0".equals(infos.getData(i).getString("RSRV_TAG1"))||"".equals(infos.getData(i).getString("RSRV_TAG1")))
    				&&(("1".equals(infos.getData(i).getString("RSRV_STR9")))||("2".equals(infos.getData(i).getString("RSRV_STR9"))))){
    			modelNotReturn++;
    		}
    	}
        
        IData idata = new DataMap();
		idata.put("MODEL_SHOW_INFO", modelShowInfo);
		//modelDeposit = "" + Integer.parseInt(modelDeposit)/100;
		idata.put("MODEM_DEPOSIT", modelDeposit);
		idata.put("IS_DEPEND_ACTIVE", isDependActive);
		idata.put("MODEL_NOT_RETURN", modelNotReturn+"");
        
        IDataset dataset = new DatasetList();
        dataset.add(idata);
        setAjax(dataset);
    }
    
    public void getSaleActiveDesc(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String saleActiveFee = "0";
        String newProductId = data.getString("NEW_PRODUCT_ID");
        String packageId = data.getString("PACKAGE_ID");
        String eparchyCode = data.getString("EPARCHY_CODE");
        
        data.put("EPARCHY_CODE", eparchyCode);
        data.put("PARAM_ATTR", "178");
        data.put("PARAM_CODE", "600");
        IDataset results = CSViewCall.call(this, "SS.WidenetMoveSVC.getSaleActiveComm", data);
        //IDataset results = CommparaInfoQry.getCommpara("CSM", "178", "600", "0898");
        IData idata = new DataMap();
        IDataset resus = new DatasetList();
        for(int i=0;i<results.size();i++){
        	IData result = results.getData(i);
        	if((newProductId.equals(result.getString("PARA_CODE1")))&&(packageId.equals(result.getString("PARA_CODE5")))){
        		resus.add(result);
        		idata.put("ACTIVE_DESC", result.getString("PARA_CODE24"));
                idata.put("PRODUCT_ID", result.getString("PARA_CODE4"));
                idata.put("CAPM_TYPE", result.getString("PARA_CODE6"));
                idata.put("SALE_STAFF_ID", getVisit().getStaffId());

                data.put("PACKAGE_ID", packageId);
                data.put("PRODUCT_ID", result.getString("PARA_CODE4"));
                data.put("ACTIVE_FLAG", "1");
                IDataset activeFee = CSViewCall.call(this, "SS.WidenetMoveSVC.queryCheckSaleActiveFee", data);
                if (IDataUtil.isNotEmpty(activeFee)){
                	int depositFee = Integer.parseInt(activeFee.getData(0).getString("SALE_ACTIVE_FEE", "0"));
                    idata.put("SALE_ACTIVE_FEE", depositFee);
                    
                    if(depositFee>0){
                        //获取用户当前现金账户余额
                        IDataset accts = CSViewCall.call(this, "SS.WidenetMoveSVC.getUserBalance", data);
                    	String cash=accts.getData(0).getString("CASH_BALANCE","0");
                    	String hintInfo = "";
                    	if(depositFee>Integer.parseInt(cash)){
                    		hintInfo = "您选择办理宽带营销活动，需要冻结预存"+depositFee/100+"元，您当前余额为"+(Integer.parseInt(cash))/100+"，请提前缴纳预存！";
                    	}else{
                    		hintInfo = "您选择办理宽带营销活动，需要冻结预存"+depositFee/100+"元";
                    	}
                        idata.put("HINT_INFO", hintInfo);
                    }
                    saleActiveFee = "" + Integer.parseInt(activeFee.getData(0).getString("SALE_ACTIVE_FEE", "0"))/100;
                }else{
                    idata.put("SALE_ACTIVE_FEE", "0");
                    saleActiveFee = "0";
                }
                
                
                
                setSaleActiveDesc(result.getString("PARA_CODE24"));
        	}
        }
        setSaleActiveFee(saleActiveFee);
        IDataset dataset = new DatasetList();
        dataset.add(idata);
        setAjax(dataset);
    }
    
    /**
     * 初始化方法
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData param = new DataMap();
        //此处TRADE_TYPE_CODE和WIDE_TYPE先写死，呆用户输入号码之后，根据号码重新设置
        param.put("TRADE_TYPE_CODE", "606");
        param.put("WIDE_TYPE", "ADSL");
        setWideInfo(param);
    }

    /**
     * 业务提交（onTradeSubmit cssubmit组件中默认的提交action方法）
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if ("".equals(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        /**
         * REQ201609280017_家客资源管理-九级地址BOSS侧改造需求 
         * @author zhuoyingzhi
         * 20161102
         */
        if("1".equals(data.getString("FLOOR_AND_ROOM_NUM_FLAG"))){
            data.put("DETAIL_ADDRESS", data.getString("DETAIL_ADDRESS_1"));
            data.put("NEW_DETAIL_ADDRESS", data.getString("DETAIL_ADDRESS_1"));
        }else{
            data.put("DETAIL_ADDRESS", data.getString("DETAIL_ADDRESS_1")+","+data.getString("ADDRESS_BUILDING_NUM"));
            data.put("NEW_DETAIL_ADDRESS", data.getString("DETAIL_ADDRESS_1")+","+data.getString("ADDRESS_BUILDING_NUM"));
        }

        IDataset dataset = CSViewCall.call(this, "SS.WidenetMoveRegSVC.tradeReg", data);
        setAjax(dataset);
    }

    public void chgProdShowInit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WidenetMoveSVC.showProdMode", data);
        setProductModeList(dataset);
        setAjax(dataset);
    }

    public void chgSaleShowInit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WidenetMoveSVC.checkSerialNumber", data);
        IDataset acctInfos = dataset.getData(0).getDataset("ALL_ACCT");
        setAllAcct(acctInfos);
        setAjax(dataset);
        setNewWideInfo(dataset.getData(0));
    }
    
    public void loadProductListByMode(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IDataset dataset = CSViewCall.call(this, "SS.WidenetMoveSVC.getWidenetProductInfo", data);
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), dataset);
        setProductList(dataset);
        setAjax(dataset);
    }
    

    public void checkSaleBook(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IData returnData = new DataMap();
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        
        IDataset infos = CSViewCall.call(this, "SS.SaleActiveSVC.checkSaleBook", params);
        if (IDataUtil.isNotEmpty(infos))
        {
            returnData = infos.getData(0);
        }
        else
        {
            returnData.put("AUTH_BOOK_SALE", 0);
        }

        setAjax(returnData);
    }
    
    public abstract void setAllAcct(IDataset datas);
    public abstract void setNewWideInfo(IData userWideInfo);
    public abstract void setWideInfo(IData wideInfo);
    public abstract void setModelInfo(IData modelInfo);
    public abstract void setUserProdInfo(IData userProdInfo);
    public abstract void setProductModeList(IDataset prodInfo);
    public abstract void setProductList(IDataset productList);
    public abstract void setSaleActiveList(IDataset saleActiveList);
    public abstract void setSaleActiveDesc(String saleActiveDesc);
    public abstract void setSaleActiveFee(String saleActiveDesc);

}
