package com.asiainfo.veris.crm.order.web.person.broadband.nophonewidenet.widenetmove;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NoPhoneWidenetMove extends PersonBasePage
{
	
	/**
     * 初始化方法
     */
    public void onInitTrade(IRequestCycle clcle) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", "686");//初始TRADE_TYPE_CODE
        setWideInfo(param);
    }
	
    /**
     * 认证后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        //根据USER_ID获取宽带资料，如果无资料，则报错
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        if (IDataUtil.isEmpty(dataset))
        {
            CSViewException.apperr(WidenetException.CRM_WIDENET_4);
        }
        IData wideInfo = dataset.getData(0);
        setWideInfo(wideInfo);//传出了OLD_STAND_ADDRESS-原标准地址 OLD_DETAIL_ADDRESS-原详细地址
 
        //获取宽带主产品信息
        IData result = loadProductInfo(cycle);//调用loadProductInfo
        
        //开户首月优惠期间，仅提供同制式、同速率宽带移机
        //1、宽带开户首月（a.用openDate判断；b.用是否有生效优惠判断）不能移机，如果办理工号有WIDENETMOVE_FIRST权限则WIDENETMOVE_FIRST=1
        //2、判断本月是否做过移机，根据widenet表的start_date与sysdate判断，widenet表的start_date这个字段在移机回单的时候，会进行更新
        IDataset idsPriv = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.judgeIsCanMove", data);
        if(IDataUtil.isNotEmpty(idsPriv))
        {
        	IData idPriv = idsPriv.first();
        	String strPriv = idPriv.getString("WIDENETMOVE_FIRST", "0");
        	result.put("WIDENETMOVE_FIRST", strPriv);
        }
        setUserProdInfo(result);//传出UserProdInfo，貌似result中的值大部分没用，只有用到USER_PRODUCT_NAME和最后的 WIDENETMOVE_FIRST是否允许移机

        IDataset resus = new DatasetList();
        resus.add(result);
        setAjax(resus);
    }
    
    //获取宽带产品信息，有两处调用，一处是认证后loadChildInfo，一处是选择了地址后initProductChg
    public IData loadProductInfo(IRequestCycle cycle)throws Exception
    {
    	IData data = getData();
    	IDataset resus = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getUserProductInfo", data);
        return resus.getData(0);
    }
    
    //测试
    public void init(IRequestCycle cycle) throws Exception{

	}
    
    /**
     * 标准地址查询后设置页面信息
     */
    public void initProductChg(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	String eparchyCode = data.getString("ROUTE_EPARCHY_CODE");
    	String seriNumber = data.getString("SERIAL_NUMBER");
    	data.put("SERIAL_NUMBER", seriNumber);
    	
    	IData result = new DataMap();
        result = loadProductInfo(cycle);//获取宽带主产品信息
        result.put("IS_CHG_OTHER", "0");//初始是否已存在主产品变更，判断依据为主产品的START_DATE是否大于sysdate，大于或等于值则该为1，即存在未生效主产品，否则该值为0
        result.put("IS_ADSL_LIMIT", "0");//初始当该值为1时代表不允许从非ADSL转为ADSL
        
        String productId = result.getString("PRODUCT_ID");
        IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        IData wideInfo = dataset.getData(0);//下面用到wideInfo中的RSRV_STR4，作为oldAreaCode
    	
        
        String openType = data.getString("OPEN_TYPE", "");//宽带类型描述编码  TTADSL-铁通ADSL TTFTTB-铁通FTTB TTFTTH-铁通FTTH GPON-移动FTTB FTTH移动FTTH
        String newWideType = "";//移机后新宽带类型
        String newProductMode = "";//移机选择地址后新的PRODUCT_MODE，仅用于下面判断是否存在主产品变更，且给IS_NEED_CHG_PROD赋值并传出用
        boolean isFtth = false;//是否是FTTH
        String purchaseMoney = "",firstRentMoney = "",secondRendMoney = "",moveFtthMoney = "";
        String isNewAdsl = "0",isOldAdsl = "0";//仅用于adsl转换的判断
        String oldProductMode = result.getString("PRODUCT_MODE");//移机前宽带的PRODUCT_MODE，下面多处用到
        boolean isUsedFtth = false;//初始是否为新老宽带类型都是FTTH，并无变化，默认为否
        boolean isOldFtth = false;

        //获取宽带类型配置，读取的210配置
        IDataset prodModes = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.showProdMode", data);
        for(int i=0;i<prodModes.size();i++){
        	
        	//匹配到与选择地址宽带类型相同的数据，210配置WIDE_TYPE_PROD_MODE中的PARA_CODE3为宽带类型
        	if(prodModes.getData(i).getString("PARA_CODE3", "").equals(openType)){
        		newWideType = prodModes.getData(i).getString("PARA_CODE9", "");//PARA_CODE9  1-移动FTTB 2-铁通ADSL 3-移动FTTH 5-铁通FTTH 6-铁通FTTB 貌似是无手机宽带类型的编码，传出后build中用到
        		newProductMode = prodModes.getData(i).getString("PARA_CODE13", "");//仅用于下面判断是否存在主产品变更，且给IS_NEED_CHG_PROD赋值并传出用   //PARA_CODE13 18-铁通ADSL，21-移动FTTH，22-移动FTTB，23-铁通FTTH，24-铁通FTTB
        		data.put("PRODUCT_MODE", newProductMode);//通过modelInfo传出作为宽带类型下拉框的默认值
        		data.put("NEW_PRODUCT_MODE", newProductMode);//下面的第一次getWidenetProductInfo中用到
        		
        		IDataset prodMode = new DatasetList();
        		prodMode.add(prodModes.getData(i));
                setProductModeList(prodMode);//重置页面中产品变更信息下宽带类型下拉框的选择值，只保留匹配的一条记录
                
                //如果移机后新宽带类型是FTTH
                if(prodModes.getData(i).getString("PARA_CODE4", "").equals("FTTH")){//PARA_CODE4 只有铁通FTTH和移动FTTH的该值才有值，且值为FTTH
                	isFtth = true;//将是否是FTTH标记置为true
                	
                	purchaseMoney = prodModes.getData(i).getString("PARA_CODE5", "");//根据英文翻译得到貌似是购买时的押金
                	firstRentMoney = prodModes.getData(i).getString("PARA_CODE6", "");//根据英文翻译得到貌似是第一次租用的押金
                	secondRendMoney = prodModes.getData(i).getString("PARA_CODE7", "");//根据英文翻译得到貌似是第二次租用的押金
                	moveFtthMoney = prodModes.getData(i).getString("PARA_CODE11", "");//根据英文翻译得到貌似是移机押金
                }
                
                //如果是铁通ADSL
                if(prodModes.getData(i).getString("PARA_CODE8", "").equals("ADSL")){//PARA_CODE8  只有铁通ADSL该值才有值，且值为ADSL
                	isNewAdsl="1";//移机后的新宽带类型是铁通ADSL
                }
        	}

        	//如果宽带的类型没有变化
        	if(newProductMode.equals(oldProductMode)){
                if(prodModes.getData(i).getString("PARA_CODE8", "").equals("ADSL")){
                	isOldAdsl="1";//宽带类型是铁通ADSL，类型未变化
                }
                if(prodModes.getData(i).getString("PARA_CODE4", "").equals("FTTH")){
                    isUsedFtth = true;//新老宽带类型是铁通FTTH和移动FTTH，类型未变化
                }
        	}
        }

        //宽带PRODUCT_MODE类型是否变更
        if(!newProductMode.equals(oldProductMode)){
        	result.put("IS_NEED_CHG_PROD", "1");
        }else{
        	result.put("IS_NEED_CHG_PROD", "0");
        }

        IDataset resus = new DatasetList();
        
        //不允许从非ADSL转为ADSL
        if(("0".equals(isOldAdsl))&&("1".equals(isNewAdsl))){
            result.put("IS_ADSL_LIMIT", "1");
            resus.add(result);
            setAjax(resus);
            return;
        }
        result.put("OLD_WIDE_TYPE", "");
    	//查看用户是否租借过光猫
        IDataset infos = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getModelInfo", data);//获取tf_f_user_other表信息 RSRV_VALUE_CODE='FTTH'
    	for(int i=0;i<infos.size();i++){
    		//rsrv_tag1--申领模式  0租赁，1购买，2赠送，3自备  rsrv_str9--移机、拆机未退光猫标志：1.移机未退光猫  2.拆机未退光猫
    		if(("0".equals(infos.getData(i).getString("RSRV_TAG1"))||"".equals(infos.getData(i).getString("RSRV_TAG1")))
    			&&(!"1".equals(infos.getData(i).getString("RSRV_STR9")))&&(!"2".equals(infos.getData(i).getString("RSRV_STR9"))))
    		{
    			String startDate = infos.getData(i).getString("START_DATE");
    			String threeYYAfter = SysDateMgr.getAddMonthsNowday(36, startDate);//3年后的时间
    			int midMonths = SysDateMgr.monthIntervalYYYYMM(SysDateMgr.getSysDate(),threeYYAfter);
    			
    			//如果还未用到3年
    			if(midMonths>0){
                	isOldFtth = true;//光猫曾经租借过，还未满3年
    			}
    			result.put("OLD_WIDE_TYPE", "3");
    		}
    	}
    	
        IData modelInfo = new DataMap();
        modelInfo.put("IS_FTTH", "0");//初始必须申领光猫 0-不用申领
        modelInfo.put("PRODUCT_MODE", data.getString("PRODUCT_MODE"));//传出宽带类型下拉框的value值，即移机后宽带类型对应的默认值
                
        //如果移机后新宽带类型是FTTH，下面又重新从6131获取并重新赋值了firstRentMoney、secondRendMoney、purchaseMoney不知道出于什么原因，此前已经从210获取过一次
        if(isFtth){
            modelInfo.put("IS_FTTH", "1");//是否必须申领光猫 1-必须申领
            data.put("EPARCHY_CODE", eparchyCode);
            data.put("PARAM_ATTR", "6131");//取commpara表中6131配置作为光猫的押金或购买金额
            
            data.put("PARAM_CODE", "1");
            IDataset results = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getSaleActiveComm", data);
            if(results!=null&&results.size()>0) secondRendMoney = results.getData(0).getString("PARA_CODE1");
            data.put("PARAM_CODE", "2");
            results = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getSaleActiveComm", data);
            if(results!=null&&results.size()>0) firstRentMoney = results.getData(0).getString("PARA_CODE1");
            data.put("PARAM_CODE", "3");
            results = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getSaleActiveComm", data);
            if(results!=null&&results.size()>0) purchaseMoney = results.getData(0).getString("PARA_CODE1");
        	
            modelInfo.put("FIRST_RENT", firstRentMoney);//dealModelMoney中光猫模式选择时，计算押金用到
            modelInfo.put("SECOND_RENT", secondRendMoney);//暂时没有用到
            modelInfo.put("PURCHASE_MONEY", purchaseMoney);//当modelMode=2时，才用到该值记录到tf_b_trade_other用
            modelInfo.put("MOVE_FTTH_MONEY", moveFtthMoney);//dealModelMoney中光猫模式选择时，计算押金用到
        }
        
        //获取产品信息，并传出，同时判断是否存在预约主产品变更IS_CHG_OTHER
        IDataset products = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getWidenetProductInfo", data);
        if(products!=null&&products.size()>0){
        	if(("1".equals(products.getData(0).getString("IS_CHG_OTHER")))){
                result.put("IS_CHG_OTHER", products.getData(0).getString("IS_CHG_OTHER",""));//IS_CHG_OTHER 是否已存在主产品变更，判断依据为主产品的START_DATE是否大于sysdate，大于或等于值为1即存在未生效主产品
                resus.add(result);
                setAjax(resus);
                return;
        	}
        }
        ProductPrivUtil.filterProductListByPriv(this.getVisit().getStaffId(), products);//产品权限控制

        //用移机前老的PRODUCT_MODE，再获取一下宽带信息，主要为了传出OLD_PRODUCT_RATE
        int oldRate = 0;
        IData data2 = new DataMap();
        data2.put("USER_ID", data.getString("USER_ID"));
        data2.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        data2.put("ROUTE_EPARCHY_CODE", data.getString("ROUTE_EPARCHY_CODE"));
        data2.put("NEW_PRODUCT_MODE", oldProductMode);
        IDataset productOlds = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getWidenetProductInfo", data2);
        for(int i=0;i<productOlds.size();i++){
        	if(productId.equals(productOlds.getData(i).getString("PRODUCT_ID"))){
        		oldRate = productOlds.getData(i).getInt("WIDE_RATE",0);
        	}
        }
        result.put("OLD_PRODUCT_RATE", oldRate);//移机前老的宽带速率
        
        //重置宽带产品下拉框内容，不论宽带类型是否变更，都只保留一条，并不允许选择
        boolean oldProductToNewProduct = false;//初始化为没有  老宽带产品是否有对应速率的新宽带产品
        if(products!=null&&products.size()>0){
        	for(int i=0;i<products.size();i++){
        		int tempWideRate = products.getData(i).getInt("WIDE_RATE",0);
        		
        		//如果匹配到相同速率的新产品
        		if(oldRate==tempWideRate){
        			IDataset productListTemp = new DatasetList();
        			productListTemp.add(products.getData(i));
        			setProductList(productListTemp);
        			modelInfo.put("NEW_PRODUCT_ID_VALUE", products.getData(i).getString("PRODUCT_ID"));//将新宽带产品的ID传出
        			result.put("NEW_PRODUCT_ID_VALUE", products.getData(i).getString("PRODUCT_ID"));
        			oldProductToNewProduct = true;//匹配到相同速率的新产品
        		}
        	}
        	
        	//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
        	//没有能够匹配到相同速率的新宽带产品,且原产品为FTTH 100M产品时，可以降档选择FTTB 50M宽带产品
        	boolean houniaoTag = false;
            IData data3 = new DataMap();
            data3.put("USER_ID", data.getString("USER_ID"));          
            data3.put("ROUTE_EPARCHY_CODE", data.getString("ROUTE_EPARCHY_CODE"));
            IDataset userDiscnts = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getUserDiscntInfo", data3);
    		//System.out.println("========NoPhoneWidenetMove======userDiscnts:"+userDiscnts);

            if(userDiscnts!=null&&userDiscnts.size()>0){
            	if(("1".equals(userDiscnts.getData(0).getString("HOUNIAO_TAG")))){
            		houniaoTag = true;
            	}             	
            }
    		//System.out.println("========NoPhoneWidenetMove======oldProductToNewProduct:"+oldProductToNewProduct);
    		//System.out.println("========NoPhoneWidenetMove======isOldFtth:"+isOldFtth);
    		//System.out.println("========NoPhoneWidenetMove======oldRate:"+oldRate);
    		//System.out.println("========NoPhoneWidenetMove======houniaoTag:"+houniaoTag);

        	if (!oldProductToNewProduct && !isUsedFtth && !isFtth && oldRate == 102400 && houniaoTag)
        	{
        		for(int i=0;i<products.size();i++){
            		int tempWideRate = products.getData(i).getInt("WIDE_RATE",0);
            		if(51200==tempWideRate && ("84011644".equals(products.getData(i).getString("PRODUCT_ID","")))){
            			IDataset productListTemp = new DatasetList();
            			productListTemp.add(products.getData(i));
            			setProductList(productListTemp);
            			modelInfo.put("NEW_PRODUCT_ID_VALUE", products.getData(i).getString("PRODUCT_ID"));//将新宽带产品的ID传出
            			result.put("NEW_PRODUCT_ID_VALUE", products.getData(i).getString("PRODUCT_ID"));
            			oldProductToNewProduct = true;//匹配到相同速率的新产品
            		}
            	}
        	}
        	if ( isFtth && !isUsedFtth && oldRate == 51200 && houniaoTag)
        	{
        		for(int i=0;i<products.size();i++){
            		int tempWideRate = products.getData(i).getInt("WIDE_RATE",0);
            		if(102400==tempWideRate && ("84011648".equals(products.getData(i).getString("PRODUCT_ID","")))){
            			IDataset productListTemp = new DatasetList();
            			productListTemp.add(products.getData(i));
            			setProductList(productListTemp);
            			modelInfo.put("NEW_PRODUCT_ID_VALUE", products.getData(i).getString("PRODUCT_ID"));//将新宽带产品的ID传出
            			result.put("NEW_PRODUCT_ID_VALUE", products.getData(i).getString("PRODUCT_ID"));
            			oldProductToNewProduct = true;//匹配到相同速率的新产品
            		}
            	}
        	}
        	//add by zhangxing3 for 候鸟月、季、半年套餐（海南）
        }
        
        //如果没有能够选择的新宽带产品   或者   没有能够匹配到相同速率的新宽带产品，记得测试下如果没有产品变化，会不会有问题！！！！
        if(IDataUtil.isEmpty(products) || !oldProductToNewProduct){
        	result.put("NO_NEW_PRODUCT", "1");
        }else{
        	result.put("NO_NEW_PRODUCT", "0");
        }
        result.put("FIRST_DAY_NEXT_MONTH",SysDateMgr.getFirstDayOfNextMonth4WEB());//下月1日，作为新元素的开始时间
        

        result.put("IS_EXCHANGE_MODEL", "6");//是否需要更换光猫，不知道为什么要传6，貌似是初始化。。。后面又重置了该值

        //isFtth-如果移机后新宽带类型是FTTH 且 isOldFtth=true光猫曾经租借过，还未满3年
        //或isFtth-如果移机后新宽带类型是FTTH 且 isUsedFtth=true新宽带类型还是铁通FTTH或移动FTTH，是FTTH类型未变化
        if((isFtth&&isOldFtth)||(isFtth&&isUsedFtth)){
        	
        	data2.put("TYPE_ID", "WIDENET_COP_MODEL_CODE");
        	
            String oldAreaCode = wideInfo.getString("RSRV_STR4", ""); 
            data2.put("DATA_ID", oldAreaCode);
            IDataset oldAreaModels = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getStaticInfoOnly", data2);
            String oldModelCode = oldAreaModels.getData(0).getString("DATA_VALUE","");//光猫厂家 ZHONGXING或者HUAWEI
            
            String newAreaCode = data.getString("AREA_CODE", "");
            data2.put("DATA_ID", newAreaCode);
            IDataset newAreaModels = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getStaticInfoOnly", data2);
            String newModelCode = newAreaModels.getData(0).getString("DATA_VALUE","");//光猫厂家 ZHONGXING或者HUAWEI
            
            /**
             * cxy2 存量（有光猫记录和无光猫记录）
             * 1、存量（有光猫记录和无光猫记录）用户移机不跨业务区，不可再申领光猫申领。
             * 2、用户移机跨业务区，按NGBOSS中“光猫品牌和业务区”关系判断：   
			（1）若属于不同厂家，则需必选光猫；   
			（2）若属于相同厂家，则不能选择再次申领光猫。  
             * */
            if(newAreaCode.equals(oldAreaCode)){//地区相同
            	result.put("OTHER_AREA_FLAG", "FALSE");//不跨区，为了不影响其他判断。
            	modelInfo.put("IS_FTTH", "0");//不跨区不允许再次申领光猫
            }else{
            	result.put("OTHER_AREA_FLAG", "TRUE");//跨区，为了不影响其他判断。
            	if(newModelCode.equals(oldModelCode)){//地区不同，但光猫厂家相同
            		modelInfo.put("IS_FTTH", "0");
            	}else{//地区不同，光猫厂家也不同，必须重新申领光猫
            		modelInfo.put("IS_FTTH", "1");
            	}
            }
            
            if(newModelCode.equals(oldModelCode)){//光猫厂家相同，如果地区相同则光猫厂家一定相同
            	if(isFtth&&isOldFtth){//isFtth多余，最外层的if已经限定了isFtth一定为真
            		result.put("IS_EXCHANGE_MODEL", "1");//光猫厂家相同，isOldFtth=true光猫还是老光猫
            	}else{
            		result.put("IS_EXCHANGE_MODEL", "5");//光猫厂家相同，无光猫或者光猫已用超过3年
            	}
            }else if(isFtth&&isOldFtth){//isFtth多余，最外层的if已经限定了isFtth一定为真
                result.put("IS_EXCHANGE_MODEL", "0");//光猫厂家不同，isOldFtth=true光猫还是老光猫，需要更换光猫
            }else{//以前的产品是FTTH，但是other表没有光猫记录
            	result.put("IS_EXCHANGE_MODEL", "4");//光猫厂家不同，无光猫或光猫已用超过3年，需要新申请一个光猫，旧的用不了
            }
        }else if(isOldFtth){//isOldFtth=true光猫还是老光猫，但移机后宽带模式不是FTTH，不再需要光猫，需要退还。想到一种情况是FTTH移机到FTTB
            result.put("IS_EXCHANGE_MODEL", "2");
        }else if(isFtth){//isFtth-如果移机后新宽带类型是FTTH，isOldFtth!=true，不知道属于什么情况。想到一种情况是FTTB移机到FTTH
            result.put("IS_EXCHANGE_MODEL", "3");
        }

        /**
         * 是否显示光猫申领条件 即IS_FTTH的梳理
			1、必须是在选择了移机新地址之后，这样才知道新宽带类型是否是FTTH
			2、只有新宽带类型是FTTH的情况下，才【可能】显示申领光猫，此时IS_FTTH=1
			3、是否显示，基于上面两点之后，最后取决于other表没有过申领记录、地区是否变化、厂家是否变化
				如果other表没有过申领记录，宽带类型也变了从FTTB变到FTTH，代表用户此前没有申领过光猫，则需要申领光猫
		
				如果other表有过申领记录，同时之前申领过光猫还不到3年，地区也没变，不管之前是不是FTTH，都不用申领
				如果宽带类型也没变一直是FTTH，地区也没变，不论是否other表有过申领记录，都不用申领
		
				如果other表有过申领记录，同时之前申领过光猫还不到3年，但地区变了，光猫厂家相同，则不用申领
				如果宽带类型也没变一直是FTTH，但地区变了，光猫厂家相同，则不用申领

				如果other表有过申领记录，同时之前申领过光猫还不到3年，但地区变了，光猫厂家也不同，则需要申领
				如果宽带类型也没变一直是FTTH，但地区变了，光猫厂家也不同，则需要申领
         * */
        result.put("IS_FTTH", modelInfo.getString("IS_FTTH"));//是否必须申领光猫 0-不用申领 1-必须申领
        result.put("NEW_WIDE_TYPE", newWideType);//传出移机后的新宽带类型
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
        
        resus.add(result);
        setModelInfo(modelInfo);
        setAjax(resus);
    }
    
    /**
     * 光猫模式选择时调用，需要改造，不走冻结预存款方式，直接收现金
     * */
    public void dealModelMoney(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String moveFtthMoney = data.getString("MOVE_FTTH_MONEY");
        String firstRent = data.getString("FIRST_RENT");
        String isExchangeModel = data.getString("IS_EXCHANGE_MODEL");
        String modelShowInfo = "";
        String modelDeposit = "0";
        if("1".equals(isExchangeModel)||"0".equals(isExchangeModel)){//1 表示“新旧地区使用同厂家光猫”  0表示“新旧地区使用不同厂家光猫”
        	modelDeposit = moveFtthMoney;
        }else{
        	modelDeposit = firstRent;
        }
        modelShowInfo = "您在办理光猫租借，租借押金"+ Integer.parseInt(modelDeposit)/100 +"元！";
        
        int modelNotReturn = 0;
        IDataset infos = CSViewCall.call(this, "SS.NoPhoneWidenetMoveSVC.getModelInfo", data);
    	for(int i=0;i<infos.size();i++){
    		//租借未归还的光猫
    		if(("0".equals(infos.getData(i).getString("RSRV_TAG1"))||"".equals(infos.getData(i).getString("RSRV_TAG1")))
    				&&(("1".equals(infos.getData(i).getString("RSRV_STR9")))||("2".equals(infos.getData(i).getString("RSRV_STR9"))))){
    			modelNotReturn++;
    		}
    	}
        
        IData idata = new DataMap();
		idata.put("MODEL_SHOW_INFO", modelShowInfo);
		idata.put("MODEM_DEPOSIT", modelDeposit);
		idata.put("MODEL_NOT_RETURN", modelNotReturn+"");
        
        IDataset dataset = new DatasetList();
        dataset.add(idata);
        setAjax(dataset);
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

        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneWidenetMoveRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
    public abstract void setWideInfo(IData wideInfo);
    public abstract void setProductList(IDataset productList);
    public abstract void setModelInfo(IData modelInfo);
    public abstract void setUserProdInfo(IData userProdInfo);
    public abstract void setProductModeList(IDataset prodInfo);
}
