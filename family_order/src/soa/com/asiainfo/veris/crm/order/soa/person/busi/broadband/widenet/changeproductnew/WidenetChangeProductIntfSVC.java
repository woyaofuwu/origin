
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew;


import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.MergeWideUserCreateSVC;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class WidenetChangeProductIntfSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    /**
     * 宽带产品变更同时办理1+营销活动，提供给短厅用
     * @param param
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset changeWideProductIntf(IData param) throws Exception
    {
        IData busiParam = new DataMap();
        
        //校验服务号码是否传入
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        
        //营销活动产品ID是否传入
        IDataUtil.chkParam(param, "PRODUCT_ID");
        
        //营销活动包ID是否传入
        IDataUtil.chkParam(param, "PACKAGE_ID");
        
        //业务入参转换
        busiParam.put("NEW_SALE_PACKAGE_ID", param.getString("PACKAGE_ID"));
        busiParam.put("NEW_SALE_PRODUCT_ID", param.getString("PRODUCT_ID"));
        busiParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        busiParam.put("AUTH_SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        busiParam.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
        busiParam.put("BOOKING_DATE", SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime()));
        
        IData queryParam = new DataMap();
        queryParam.put("SERIAL_NUMBER_A", param.getString("SERIAL_NUMBER"));
        queryParam.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        queryParam.put("TRADE_TYPE_CODE", "601");
        
        //用户当前的宽带营销活动
        IDataset userActives = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", queryParam);
        
        if (IDataUtil.isNotEmpty(userActives))
        {
            busiParam.put("V_USER_PACKAGE_ID", userActives.getData(0).getString("PACKAGE_ID",""));
            busiParam.put("V_USER_PRODUCT_ID", userActives.getData(0).getString("PRODUCT_ID",""));
        }
        
        //设置宽带用户需要新订购的产品信息
        setWideUserNewProductInfo(param, busiParam);
        
        //设置宽带用户本次业务需要订购和取消的产品元素信息
        setWideUserOfferElementInfos(busiParam);
        
        //设置宽带产品变更升降挡标记
        setChangeProductUpDownTag(param, busiParam);
        
        busiParam.put("TRADE_TYPE_CODE", "601");
        busiParam.put("ORDER_TYPE_CODE", "601");
        
        IDataset result = CSAppCall.call("SS.WidenetChangeProductNewRegSVC.tradeReg", busiParam);
        
        return result;
    }
    
    /**
     * 宽带产品变更同时办理1+营销活动，提供批量宽带产品变更使用
     * @param param
     * @return
     * @throws Exception
     * @author zhangxing3
     */
    public IDataset changeWideProductIntfForBat(IData param) throws Exception
    {
        IData busiParam = new DataMap();
        
        //校验服务号码是否传入
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        
        //宽带产品ID是否传入
        IDataUtil.chkParam(param, "PRODUCT_ID");
        
        String newProductId = param.getString("PRODUCT_ID", "");
        String saleActiveId = param.getString("PACKAGE_ID", "");
        String saleProductId = "";
        
    	UcaData uca = UcaDataFactory.getNormalUca(param.getString("SERIAL_NUMBER",""));
    	String userId = uca.getUserId();
    	IDataset ids = UserDiscntInfoQry.getUserCOMDiscnt(userId,"9711","RHTC",SysDateMgr.getSysTime(),"0898");
    	if ("".equals(saleActiveId) && IDataUtil.isEmpty(ids) && param.getString("SERIAL_NUMBER","").length() == 11)
    	{
    		CSAppException.appError("-1", "用户没有宽带权益主套餐，不能办理批量宽带产品变更！");
    	}
        
        if(!"".equals(saleActiveId))
        {
        	 busiParam.put("NEW_SALE_PACKAGE_ID", param.getString("PACKAGE_ID"));
        	 IData data = new DataMap();
        	 data.put("SUBSYS_CODE", "CSM");
         	 data.put("PARAM_ATTR", "178");
         	 data.put("PARAM_CODE", "601");
         	 data.put("PARA_CODE1", newProductId);
         	 data.put("PARA_CODE5", saleActiveId);
         	 data.put("EPARCHY_CODE", "0898");
        	 IDataset saleActiveIDataset = CommparaInfoQry.getCommparaInfoBy1To6(data);
             
             if (IDataUtil.isNotEmpty(saleActiveIDataset))
             {
                 IData saleActiveData = saleActiveIDataset.first();                   
                 saleProductId = saleActiveData.getString("PARA_CODE4","");
             }
             else
             {
                 CSAppException.appError("-1", "该营销活动配置信息不存在，请联系管理员！");
             }
        	 busiParam.put("NEW_SALE_PRODUCT_ID", saleProductId);

        }
        //业务入参转换
        busiParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER",""));
        busiParam.put("AUTH_SERIAL_NUMBER", param.getString("SERIAL_NUMBER",""));
        busiParam.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
        busiParam.put("BOOKING_DATE", SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime()));
        busiParam.put("BATCH_ID", param.getString("BATCH_ID",""));

        IData queryParam = new DataMap();
        queryParam.put("SERIAL_NUMBER_A", param.getString("SERIAL_NUMBER",""));
        queryParam.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        queryParam.put("TRADE_TYPE_CODE", "601");
        
        //用户当前的宽带营销活动
        IDataset userActives = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", queryParam);
        
        if (IDataUtil.isNotEmpty(userActives))
        {
            if("67220428".equals(userActives.getData(0).getString("PRODUCT_ID","")))
            {
                CSAppException.appError("-1", "用户存在宽带包年活动，不能办理批量宽带产品变更！");
            }
        	busiParam.put("V_USER_PACKAGE_ID", userActives.getData(0).getString("PACKAGE_ID",""));
            busiParam.put("V_USER_PRODUCT_ID", userActives.getData(0).getString("PRODUCT_ID",""));
        }
        
        //设置宽带用户需要新订购的产品信息
        setWideUserNewProductInfoForBat(param, busiParam);
        
        //设置宽带用户本次业务需要订购和取消的产品元素信息
        setWideUserOfferElementInfos(busiParam);
        
        //设置宽带产品变更升降挡标记
        setChangeProductUpDownTagForBat(param, busiParam);
        
        busiParam.put("TRADE_TYPE_CODE", "601");
        busiParam.put("ORDER_TYPE_CODE", "601");
    			
        String oldSaleActiveId = busiParam.getString("V_USER_PACKAGE_ID","");
        if(!"".equals(saleActiveId) && !"".equals(oldSaleActiveId))        	
        {
            busiParam.put("CHANGE_TYPE", "5");//CHANGE_TYPE =5、调用产品变更接口及营销活动终止接口，营销活动受理接口
        }
        else if(!"".equals(saleActiveId) && "".equals(oldSaleActiveId))        	
        {
            busiParam.put("CHANGE_TYPE", "3");//CHANGE_TYPE =3、调用产品变更接口与营销活动受理接口
        }
        else if("".equals(saleActiveId) && !"".equals(oldSaleActiveId))        	
        {
            busiParam.put("CHANGE_TYPE", "4");//CHANGE_TYPE =4、调用产品变更接口及营销活动终止接口
        }
        else
        {
            busiParam.put("CHANGE_TYPE", "1");//CHANGE_TYPE =1、调用产品变更接口
        }
        
        IDataset result = CSAppCall.call("SS.WidenetChangeProductNewRegSVC.tradeReg", busiParam);
        
        return result;
    }
    
    
    /**
     * 设置宽带用户需要新订购的产品信息
     * 
     * @author yuyj3
     * @throws Exception 
     */
    private void setWideUserNewProductInfo(IData param, IData busiParam) throws Exception
    {
        IDataset widenetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_"+param.getString("SERIAL_NUMBER"));
        
        String widenetUserId = "";
        String wideType = "";
        
        if (IDataUtil.isEmpty(widenetInfo))
        {
            CSAppException.appError("-1", "该用户不是宽带用户或宽带开户未完工,不能拨打点播好办理");
        }
        else
        {
            widenetUserId = widenetInfo.getData(0).getString("USER_ID");
            
            
            if (BofConst.WIDENET_TYPE_FTTB.equals(widenetInfo.getData(0).getString("RSRV_STR2")) 
                    || BofConst.WIDENET_TYPE_TTFTTB.equals(widenetInfo.getData(0).getString("RSRV_STR2")) )
            {
                wideType = "FTTB";
            }
            else if (BofConst.WIDENET_TYPE_FTTH.equals(widenetInfo.getData(0).getString("RSRV_STR2")) 
                    || BofConst.WIDENET_TYPE_TTFTTH.equals(widenetInfo.getData(0).getString("RSRV_STR2")) )
            {
                wideType = "FTTH";
            }
        }
        
        //订购新营销活动对应要订购的宽带产品
        IDataset newProductDataset = CommparaInfoQry.getCommparaByCode1to3("CSM", "178", "DIANBO", param.getString("PRODUCT_ID"), param.getString("PACKAGE_ID"), wideType, "0898");
        
        if (IDataUtil.isEmpty(newProductDataset) || StringUtils.isEmpty(newProductDataset.getData(0).getString("PARA_CODE4")))
        {
            CSAppException.appError("-1", "该营销活动["+param.getString("PACKAGE_ID")+"]没有在178参数数据配置对应的宽带产品，请检查参数配置！");
        }
        
        
        String newProductId = newProductDataset.getData(0).getString("PARA_CODE4");
        
        busiParam.put("NEW_PRODUCT_ID", newProductId);
        busiParam.put("WIDENET_USER_ID", widenetUserId);
    }
    
    /**
     * 设置宽带用户需要新订购的产品信息
     * 
     * @author zhangxing3
     * @throws Exception 
     */
    private void setWideUserNewProductInfoForBat(IData param, IData busiParam) throws Exception
    {
    	IDataset widenetInfo = new DatasetList();

    	if(param.getString("SERIAL_NUMBER","").startsWith("KD_"))
        {
        	widenetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(param.getString("SERIAL_NUMBER",""));
        }
        else
        {       	
        	widenetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_"+param.getString("SERIAL_NUMBER",""));
        }
        
        String widenetUserId = "";
        String wideType = "";
        
        if (IDataUtil.isEmpty(widenetInfo))
        {
            CSAppException.appError("-1", "该用户不是宽带用户或宽带开户未完工,不能办理");
        }
        else
        {
            widenetUserId = widenetInfo.getData(0).getString("USER_ID");
            
            
            if (BofConst.WIDENET_TYPE_FTTB.equals(widenetInfo.getData(0).getString("RSRV_STR2")) 
                    || BofConst.WIDENET_TYPE_TTFTTB.equals(widenetInfo.getData(0).getString("RSRV_STR2")) )
            {
                wideType = "FTTB";
                CSAppException.appError("-1", "该用户是FTTB制式,不能办理批量产品变更");
            }
            else if (BofConst.WIDENET_TYPE_FTTH.equals(widenetInfo.getData(0).getString("RSRV_STR2")) 
                    || BofConst.WIDENET_TYPE_TTFTTH.equals(widenetInfo.getData(0).getString("RSRV_STR2")) )
            {
                wideType = "FTTH";
            }
        }
        
        busiParam.put("NEW_PRODUCT_ID", param.getString("PRODUCT_ID",""));
        busiParam.put("WIDENET_USER_ID", widenetUserId);
    }
    
    
    /**
     * 设置宽带用户本次业务需要订购和取消的产品元素信息
     * @param busiParam
     * @throws Exception
     * @author yuyj3
     */
    private void setWideUserOfferElementInfos(IData busiParam) throws Exception
    {
        String widenetUserId = busiParam.getString("WIDENET_USER_ID");
        
        //新增产品必选或者默认的元素
        IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, busiParam.getString("NEW_PRODUCT_ID"), "1", "1");
        
        forceElements = ProductUtils.offerToElement(forceElements, busiParam.getString("NEW_PRODUCT_ID"));
        
        IDataset selectedelements = new DatasetList();
        
        //速率服务
        String rateSvc = "";
        
        for (int i = 0; i < forceElements.size(); i++ )
        {
            IData element = forceElements.getData(i);
            
            if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
            {
                //宽带主服务不变
                if ("1".equals(element.getString("MAIN_TAG")))
                {
                    continue;
                }
                else
                {
                    rateSvc = element.getString("ELEMENT_ID");
                }
                
            }
            
            element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            element.put("START_DATE", SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime()));
            element.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            
            selectedelements.add(element);
        }
        
        IData oldPorductInfo = UcaInfoQry.qryMainProdInfoByUserId(widenetUserId);
        
        if (IDataUtil.isEmpty(oldPorductInfo))
        {
            CSAppException.appError("-1", "该宽带用户主产品信息不存在！");
        }
        
        //用户已有的产品元素
        IDataset userElements = UserSvcInfoQry.getElementFromPackageByUser(widenetUserId, oldPorductInfo.getString("PRODUCT_ID"), null);
        
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
                element.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                element.put("PRODUCT_ID", oldPorductInfo.getString("PRODUCT_ID"));
                
                selectedelements.add(element);
            }
        }
        
        busiParam.put("SELECTED_ELEMENTS", selectedelements.toString());
        
        busiParam.put("WIDE_USER_SELECTED_SERVICEIDS", rateSvc);
    }
    
    /**
     * 设置宽带产品变更升降挡标记
     * @param param
     * @param busiParam
     * @throws Exception
     * @author yuyj3
     */
    private void setChangeProductUpDownTag(IData param, IData busiParam) throws Exception
    {
        String rateSvc = busiParam.getString("WIDE_USER_SELECTED_SERVICEIDS");
        
        //宽带用户速率
        String widenetUserRate = WideNetUtil.getWidenetUserRate(param.getString("SERIAL_NUMBER"));
        
        //新产品宽带速率
        IDataset newWideRateData = CommparaInfoQry.getCommpara("CSM", "4000", rateSvc, "0898");
        
        if (IDataUtil.isEmpty(newWideRateData))
        {
            CSAppException.appError("-1", "新订购的宽带产品没有配置宽带速率，请检查参数配置!");
        }
        
        String newWideRate = newWideRateData.getData(0).getString("PARA_CODE1","0");
        
        if (Integer.valueOf(newWideRate) > Integer.valueOf(widenetUserRate))
        {
            //速率升级标记，0：不变，1：升档，2：降档，3：速率不变，产品变
            busiParam.put("CHANGE_UP_DOWN_TAG", "1");
            busiParam.put("CHANGE_TYPE", "5");
        }
        else
        {
            busiParam.put("CHANGE_UP_DOWN_TAG", "2");
            busiParam.put("CHANGE_TYPE", "6");
        }
    }
    
    /**
     * 设置宽带产品变更升降挡标记
     * @param param
     * @param busiParam
     * @throws Exception
     * @author zhangxing3
     */
    private void setChangeProductUpDownTagForBat(IData param, IData busiParam) throws Exception
    {
        String rateSvc = busiParam.getString("WIDE_USER_SELECTED_SERVICEIDS");
        
        //宽带用户速率
        String widenetUserRate = WideNetUtil.getWidenetUserRate(param.getString("SERIAL_NUMBER"));
        
        //新产品宽带速率
        IDataset newWideRateData = CommparaInfoQry.getCommpara("CSM", "4000", rateSvc, "0898");
        
        if (IDataUtil.isEmpty(newWideRateData))
        {
            CSAppException.appError("-1", "新订购的宽带产品没有配置宽带速率，请检查参数配置!");
        }
        
        String newWideRate = newWideRateData.getData(0).getString("PARA_CODE1","0");
        
        String batchOperType = param.getString("BATCH_OPER_TYPE","");
        if(Integer.parseInt(newWideRate) > 102400 && !"BATMODWIDEPACKAGE".equals(batchOperType))
        {
        	CSAppException.appError("-1", "批量宽带产品变更，变更后产品资费最高为100M！");
        }
        
        if (Integer.valueOf(newWideRate) > Integer.valueOf(widenetUserRate))
        {
            //速率升级标记，0：不变，1：升档，2：降档，3：速率不变，产品变
            busiParam.put("CHANGE_UP_DOWN_TAG", "1");
        }
        else
        {
        	if("BATMODWIDEPACKAGE".equals(batchOperType))
            {
        		//REQ201910310002-关于企业宽带套餐批量变更的开发需求-本次新增的企业宽带批量变更，需求中明确要求：要能任意变更，可以降档升档与平移，请修改
        		if (Integer.valueOf(newWideRate) == Integer.valueOf(widenetUserRate)){
        			busiParam.put("CHANGE_UP_DOWN_TAG", "0");
        		}else{
        			busiParam.put("CHANGE_UP_DOWN_TAG", "2");
        		}
            }else{
            	CSAppException.appError("-1", "批量宽带产品变更，不允许降档！");
            }
        }
    }
    
    
    /**
     * 宽带产品变更用户校验接口
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkWideUserInfoIntf(IData data) throws Exception
    {
    	//校验服务号码是否传入
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        
        String serialNum = data.getString("SERIAL_NUMBER");
        
        String wideSerialNum = "";
        
        if (!serialNum.startsWith("KD_"))
        {
        	wideSerialNum = "KD_"+ serialNum;
        }
    	
    	//初始化宽带类型
        IData resultData = new DataMap();
       
        
        IData checkInputData = new DataMap();
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(wideSerialNum);
        
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.appError("-1", "该用户没有办理宽带！");
        }
        
        IData customerInfo = UcaInfoQry.qryCustomerInfoByCustId(userInfo.getString("CUST_ID"));
        if (IDataUtil.isEmpty(customerInfo))
        {
            CSAppException.appError("-1", "通过该服务号码查询不到有效的客户信息！");
        }
        
        checkInputData.put("SERIAL_NUMBER", wideSerialNum);
        checkInputData.putAll(userInfo);
        checkInputData.put("IS_REAL_NAME", customerInfo.getString("IS_REAL_NAME"));
        checkInputData.put("TRADE_TYPE_CODE", "601");
        checkInputData.put("X_CHOICE_TAG", "0");
        checkInputData.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
       
        //将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");
        
        //调用鉴权规则校验
        IDataset infos = CSAppCall.call( "CS.CheckTradeSVC.checkBeforeTrade", checkInputData);
        
        CSAppException.breerr(infos.getData(0));
        
        
        String userId = userInfo.getString("USER_ID");
        data.put("USER_ID", userId);
        
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isEmpty(widenetInfos))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_3);
        }
        
        String  widetype = widenetInfos.getData(0).getString("RSRV_STR2");
        
        resultData.put("WIDE_TYPE_CODE", widetype);
        String productmode="";//--07：移动GPON宽带，09：ADSL宽带产品，11：移动FTTH宽带，16：海南铁通FTTH，17：海南铁通FTTB，13：校园宽带
    	
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
    	
    	//初始化产品包列表
    	data.put("PROD_MODE", productmode);
        IDataset widenetProductInfos = CSAppCall.call("SS.WidenetChangeProductNewSVC.loadProductInfo", data);
        
        if (IDataUtil.isNotEmpty(widenetProductInfos))
        {
            resultData.put("USER_PRODUCT_NAME", widenetProductInfos.getData(0).getString("USER_PRODUCT_NAME"));
            resultData.put("USER_PRODUCT_ID", widenetProductInfos.getData(0).getString("USER_PRODUCT_ID"));
            resultData.put("USER_PRODUCT_START_DATE", widenetProductInfos.getData(0).getString("USER_PRODUCT_START_DATE"));
        }
        
        data.put("SERIAL_NUMBER_A", serialNum);
        data.put("TRADE_TYPE_CODE", "601");
        
        //宽带营销活动信息
        IDataset widenetActiveInfos = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", data);
        
        resultData.put("PRODUCT_ACTIVE", "0");//宽带营销活动标志
        resultData.put("USER_PACKAGE_ID", "");//保存当前的营销活动id
        
        if (IDataUtil.isNotEmpty(widenetActiveInfos))
        {
        	IData activeData = widenetActiveInfos.getData(0);
            
            resultData.put("PRODUCT_ACTIVE", "1");//有营销活动=1
        	resultData.put("USER_SALE_PACKAGE_ID", activeData.getString("PACKAGE_ID",""));
        	resultData.put("USER_SALE_PRODUCT_ID", activeData.getString("PRODUCT_ID",""));
        	resultData.put("USER_SALE_PACKAGE_NAME", activeData.getString("PACKAGE_NAME",""));
        	resultData.put("USER_SALE_PRODUCT_NAME", activeData.getString("PRODUCT_NAME",""));
        }
        
        return resultData;
        
    }
    
    /**
     * 宽带产品变更获得产品列表接口
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryProductListIntf(IData data) throws Exception
    {
    	//校验服务号码是否传入
        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        
        String serialNum = data.getString("SERIAL_NUMBER");
        
        String wideSerialNum = "";
        
        if (!serialNum.startsWith("KD_"))
        {
        	wideSerialNum = "KD_"+ serialNum;
        }
    	
        IData userInfo = UcaInfoQry.qryUserInfoBySn(wideSerialNum);
        
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.appError("-1", "该用户没有办理宽带！");
        }
        
        String userId = userInfo.getString("USER_ID");
        data.put("USER_ID", userId);
        
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(userId);
        if (IDataUtil.isEmpty(widenetInfos))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_3);
        }
        
        String  widetype = widenetInfos.getData(0).getString("RSRV_STR2");
        
        String productmode="";//--07：移动GPON宽带，09：ADSL宽带产品，11：移动FTTH宽带，16：海南铁通FTTH，17：海南铁通FTTB，13：校园宽带
    	
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
    	
    	//初始化产品包列表
    	data.put("PROD_MODE", productmode);
    	data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
    	
        IDataset widenetProductInfos = CSAppCall.call("SS.WidenetChangeProductNewSVC.loadProductInfo", data);
        
        return widenetProductInfos.getData(0).getDataset("PRODUCT_LIST");
    }

    /**
     * 宽带产品变更查询营销活动列表（提供给APP）
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySaleActiveListIntf(IData input) throws Exception
    {
    	//校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "PRODUCT_ID");
        
        input.put("NEW_PRODUCT_ID", input.getString("PRODUCT_ID"));
        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
        
        //将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");
        
        IDataset retrunSaleActiveList =  new DatasetList();
        
    	IDataset saleActiveList = CSAppCall.call("SS.WidenetChangeProductNewSVC.initActivePackage", input);
    	
        if (IDataUtil.isNotEmpty(saleActiveList))
        {
            IData saleActive = null;
            IData returnSaleActive = null;
            IData inData = null;
            IData saleActiveFeeData = null;
            
            for (int i = 0; i < saleActiveList.size(); i++)
            {
                saleActive = saleActiveList.getData(i);
                
                returnSaleActive = new DataMap();
                inData = new DataMap();
                
                returnSaleActive.put("SALE_ACTIVE_ID", saleActive.getString("PARA_CODE2"));
                returnSaleActive.put("SALE_ACTIVE_NAME", saleActive.getString("PARA_CODE3"));
                returnSaleActive.put("SALE_ACTIVE_EXPLAIN", saleActive.getString("REMARK"));
                
                inData.put("SERIAL_NUMBER", input.getString("serialNumber"));
                //活动标记，1：宽带营销活动，2：魔百和营销活动
                inData.put("ACTIVE_FLAG", "1");
                //营销活动产品ID
                inData.put("PRODUCT_ID", saleActive.getString("PARA_CODE4"));
                //营销活动包ID
                inData.put("PACKAGE_ID", saleActive.getString("PARA_CODE5"));
                //获得营销活动预存费用
                saleActiveFeeData = new MergeWideUserCreateSVC().queryCheckSaleActiveFee(inData);
                
                if (IDataUtil.isNotEmpty(saleActiveFeeData))
                {
                    returnSaleActive.put("SALE_ACTIVE_FEE", saleActiveFeeData.getString("SALE_ACTIVE_FEE", "0"));
                }
                
                retrunSaleActiveList.add(returnSaleActive);
            }
        }
        
        return retrunSaleActiveList;
    }
    
    /**
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkProductInfoIntf(IData input) throws Exception
    {
    	//返回数据
        IData resultData = new DataMap();
    	
        //校验新产品ID是否传入
    	IDataUtil.chkParam(input, "PRODUCT_ID");

    	//校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        
        //校验产品变更预约时间是否传入
        IDataUtil.chkParam(input, "BOOKING_DATE");
        
        //将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");
        
        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
        
        String serialNum = input.getString("SERIAL_NUMBER");
        
        String wideSerialNum = "";
        String newProductId = input.getString("PRODUCT_ID");
        String userProductId = "";
        
        //新产品速率
        String new_rate = WideNetUtil.getWidenetProductRate(newProductId);
      	//老产品速率
        String old_rate = WideNetUtil.getWidenetUserRate(serialNum);
      
        //协议到期标志，0：包年或营销活动协议未到期，1：3个月内到期，或无包年及营销活动
        String bookTag = "";
        //是否包年套餐，1：是，0：否
        String isYearProduct = "0";
        //是否有营销活动，1：是,0:否
        String haveSaleActive = "0";
        //是否最后一个月
        String endFlag = "";
        //用户选择的预约产品生效时间 
        String bookingDate = input.getString("BOOKING_DATE");
        //用户包年套餐或营销活动的结束时间
        String agreementEndDate = ""; 
        
        if (!serialNum.startsWith("KD_"))
        {
        	wideSerialNum = "KD_"+ serialNum;
        }
    	
        IData userInfo = UcaInfoQry.qryUserInfoBySn(wideSerialNum);
        
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.appError("-1", "该用户没有办理宽带！");
        }
        
        String userId = userInfo.getString("USER_ID");
        input.put("USER_ID", userId);
    	
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
        String sysDate = SysDateMgr.getSysTime();

        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                if (userProduct.getString("START_DATE").compareTo(sysDate) < 0)
                {
                	userProductId = userProduct.getString("PRODUCT_ID");
                    break;
                }
            }
        }
        
	   	input.put("TRADE_TYPE_CODE", "601");
		IDataset widenetProductInfos = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetProduct", input);
        
        if (IDataUtil.isNotEmpty(widenetProductInfos))
        {
        	//包年套餐=1
        	isYearProduct = "1";
        	
        	agreementEndDate = widenetProductInfos.getData(0).getString("END_DATE");//包年的直接取结束日期
        	bookTag = widenetProductInfos.getData(0).getString("V_BOOK_TAG","");
        	endFlag = widenetProductInfos.getData(0).getString("FLAG","");
        }
        
        input.put("SERIAL_NUMBER_A", serialNum);
        IDataset checkWidenetActives = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", input);
        
        if (IDataUtil.isNotEmpty(checkWidenetActives))
        {
        	IData activeData = checkWidenetActives.getData(0);
            
        	haveSaleActive = "1";//有营销活动=1
        	
        	bookTag = activeData.getString("V_BOOK_TAG","");
        	agreementEndDate = activeData.getString("V_END_DATE");
        	endFlag = activeData.getString("FLAG","");
        }
		
		
		if(Integer.valueOf(new_rate) <  Integer.valueOf(old_rate))
		{
			//降档操作
			//判断原套餐是否包年或有营销活动
			//包年套餐或营销活动未到期的情况
			if("0".equals(bookTag))
			{
				if("1".equals(isYearProduct))
				{
					CSAppException.appError("-1", "您当前有包年套餐或营销活动还未到期,不能进行降档操作!");
				}

				if("1".equals(haveSaleActive))
				{
					CSAppException.appError("-1", "您当前有营销活动未到期,不能进行降档操作!");
				}
			}
			else
			{
				//包年套餐或营销活动3个月内到期的情况或无包年及营销活动的情况
				//包年或营销活动3个月内到期，预约日期，只能选择协议到期后的次月1日,调用Ajax 计算日期
				//无包年或营销活动，只能选择次月1日生效
				if("1".equals(isYearProduct))
				{
					
					if(SysDateMgr.compareToYYYYMMDD(bookingDate,agreementEndDate) < 0)
					{
						CSAppException.appError("-1", "您的原产品中有包年套餐,还未到期,不能进行降档操作!");
					}
				}
				
				if("1".equals(haveSaleActive))
				{
					//判断是否最后一个月
					
					if(SysDateMgr.compareToYYYYMMDD(bookingDate,agreementEndDate) < 0)
					{
						String tipMsg = "您当前有营销活动未到期，您本次选择的生效时间为[" + bookingDate
							+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
						
						CSAppException.appError("-1", tipMsg);
					}
				}
			}
		}
	   	else if(Integer.valueOf(new_rate) ==  Integer.valueOf(old_rate))
	   	{
	   		//速率不变
	   		//需判断产品是否该变的情况，产品未变，预约日期为次月1日
	   		//需要产品已变，速率不变，需要判断是否有包年或营销活动
	   		if(StringUtils.equals(newProductId, userProductId))
	   		{
	   			//判断原套餐是否包年或有营销活动
				//包年套餐或营销活动未到期的情况
				if("0".equals(bookTag))
				{
					if("1".equals(isYearProduct))
					{
						CSAppException.appError("-1", "您的原产品速率与新选择的产品速率相同,您的原产品中包年套餐未到期,不能进行次操作!");
					}
					
					if("1".equals(haveSaleActive))
					{
						CSAppException.appError("-1", "您的原产品速率与新选择的产品速率相同,您当前有营销活动未到期,不能进行降档操作!");
					}
				}
				else
				{
					//包年套餐或营销活动3个月内到期的情况或无包年及营销活动
					//预约日期，如果有包年及营销活动，只能选择协议到期后的次月1日
					if("1".equals(isYearProduct) &&  "1".equals(endFlag))
					{
						CSAppException.appError("-1", "您的原产品中有包年套餐,还未到期,不能进行降档操作!");
					}
					
					if("1".equals(haveSaleActive))
					{
						//判断是否最后一个月
						if(SysDateMgr.compareToYYYYMMDD(bookingDate,agreementEndDate) < 0)
						{
							String tipMsg = "您当前有营销活动未到期，您本次选择的生效时间为[" + bookingDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
							
							CSAppException.appError("-1", tipMsg);
						}
					}
				}
	   		}
	   	}
		
		
		resultData.put("RESULT_CODE", "0");
		
		return resultData;
    }
    
    /**
     * 宽带产品变更营销活动校验接口
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkWidenetSaleActiveIntf(IData input) throws Exception
    {
    	//返回数据
        IData resultData = new DataMap();
    	
        //校验新产品ID是否传入
    	IDataUtil.chkParam(input, "PRODUCT_ID");
    	
    	//校验新产品ID是否传入
    	IDataUtil.chkParam(input, "SALE_ACTIVE_ID");

    	//校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        
        //校验产品变更预约时间是否传入
        IDataUtil.chkParam(input, "BOOKING_DATE");
        
        //用户所选的宽带服务ＩＤ
        IDataUtil.chkParam(input, "WIDE_USER_SELECTED_SERVICEIDS");
        
        //将inmodeCode设置为0，因为APP接口那边传过来是SD，会跳过规则校验。
        getVisit().setInModeCode("0");
        
        input.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE", getTradeEparchyCode()));
        input.put("EPARCHY_CODE", input.getString("EPARCHY_CODE", getTradeEparchyCode()));
        
        String serialNum = input.getString("SERIAL_NUMBER");
        
        String wideSerialNum = "";
        String newProductId = input.getString("PRODUCT_ID");
        String saleActiveId = input.getString("SALE_ACTIVE_ID");
        

        String changeUpDownTag = "0"; //速率升级标记，0：不变，1：升档，2：降档，3：速率不变，产品变
		
        String upgradeSaleActiveProductIds= ""; //当前营销活动在产品变更升档时可以选择的营销产品ID
		
        String newSalePackageId = "";
		//新选的营销活动类型
        String newSelectActiveType = "";
        String newSaleProductId = "";
        String salePackageIdFlag = "";
        String userSaleProductId = "";//用户当前的活动对应的产品id
		
		
        String userProductId = "";
        //新产品速率
        String new_rate = WideNetUtil.getWidenetProductRate(newProductId);
      	//老产品速率
        String old_rate = WideNetUtil.getWidenetUserRate(serialNum);
      
        //协议到期标志，0：包年或营销活动协议未到期，1：3个月内到期，或无包年及营销活动
        String bookTag = "";
        //是否包年套餐，1：是，0：否
        String isYearProduct = "0";
        //是否有营销活动，1：是,0:否
        String haveSaleActive = "0";
        //是否最后一个月
        String endFlag = "";
        //用户选择的预约产品生效时间 
        String bookingDate = input.getString("BOOKING_DATE");
        //用户包年套餐或营销活动的结束时间
        String agreementEndDate = ""; 
        
        if (!serialNum.startsWith("KD_"))
        {
        	wideSerialNum = "KD_"+ serialNum;
        }
    	
        IData userInfo = UcaInfoQry.qryUserInfoBySn(wideSerialNum);
        
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.appError("-1", "该用户没有办理宽带！");
        }
        
        String userId = userInfo.getString("USER_ID");
        input.put("USER_ID", userId);
    	
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
        String sysDate = SysDateMgr.getSysTime();

        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                if (userProduct.getString("START_DATE").compareTo(sysDate) < 0)
                {
                	userProductId = userProduct.getString("PRODUCT_ID");
                    break;
                }
            }
        }
        
	   	input.put("TRADE_TYPE_CODE", "601");
		IDataset widenetProductInfos = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetProduct", input);
        
        if (IDataUtil.isNotEmpty(widenetProductInfos))
        {
        	//包年套餐=1
        	isYearProduct = "1";
        	
        	agreementEndDate = widenetProductInfos.getData(0).getString("END_DATE");//包年的直接取结束日期
        	bookTag = widenetProductInfos.getData(0).getString("V_BOOK_TAG","");
        	endFlag = widenetProductInfos.getData(0).getString("FLAG","");
        }
        
        input.put("SERIAL_NUMBER_A", serialNum);
        IDataset checkWidenetActives = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", input);
        
        if (IDataUtil.isNotEmpty(checkWidenetActives))
        {
        	IData activeData = checkWidenetActives.getData(0);
            
        	haveSaleActive = "1";//有营销活动=1
        	
        	bookTag = activeData.getString("V_BOOK_TAG","");
        	agreementEndDate = activeData.getString("V_END_DATE");
        	endFlag = activeData.getString("FLAG","");
        	userSaleProductId = activeData.getString("PRODUCT_ID","");
        	
        	//产品变更升档时，可选则的营销活动产品ID
        	upgradeSaleActiveProductIds =  activeData.getString("UPGRADE_SALE_ACTIVE_PRODUCT_IDS","");
        }
		
		
        if(newProductId.equals(userProductId) )
	   	{
	   		changeUpDownTag = "0";
	   		
	   		input.put("WIDE_USER_CREATE_SALE_ACTIVE", "0");
	   		
	   	}
        else
        {
        	input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
        }
        
        
		if(Integer.valueOf(new_rate) >  Integer.valueOf(old_rate))
		{					
			changeUpDownTag = "1";
		}
		else if(Integer.valueOf(new_rate) <  Integer.valueOf(old_rate))
		{	
			changeUpDownTag = "2";
		}
	   	else if(Integer.valueOf(new_rate) ==  Integer.valueOf(old_rate))
	   	{
	   		changeUpDownTag = "3";
		}
        
		input.put("CHANGE_UP_DOWN_TAG", changeUpDownTag);
		
        
        IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "601", newProductId, saleActiveId, "0898");
        
        if (IDataUtil.isNotEmpty(saleActiveIDataset))
        {
            IData saleActiveData = saleActiveIDataset.first();
              
            newSaleProductId = saleActiveData.getString("PARA_CODE4");
            newSalePackageId = saleActiveData.getString("PARA_CODE5");
            newSelectActiveType = saleActiveData.getString("PARA_CODE5");
            salePackageIdFlag = saleActiveData.getString("PARA_CODE8"); 
        }
        else
        {
            CSAppException.appError("-1", "该营销活动配置信息不存在，请联系管理员！");
        }
        
        input.put("NEW_SALE_PRODUCT_ID", newSaleProductId);
        input.put("NEW_PACKAGE_ID", newSalePackageId);
        input.put("USER_SALE_PRODUCT_ID", userSaleProductId);
        
//		if("3"==$("#PACKAGE_VALID")[0].selectedIndex && salePackageIdFlag!= "1"){
//			
//			CSAppException.appError("-1", "特定的活动才能选这个预约时间！");
//		}
		
        //如果没有变更产品
		if("0".equals(changeUpDownTag))
		{
			//产品不变，如之前有包年活动营销活动不允许变更营销活动
			if("1".equals(isYearProduct) && "0".equals(bookTag))
			{
				//办理了特殊的活动可以预约到活动结束
				if("1".equals(salePackageIdFlag) && !"67220429".equals(userSaleProductId) && !"69908016".equals(userSaleProductId) )
				{ 
					//如果用户选择的时间小于协议结束时间，则不允许提交						
					if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
					{
						String tipMsg = "您当前有包年套餐，您本次选择的生效时间为[" + bookingDate
							+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
						
						CSAppException.appError("-1", tipMsg);
					}
				}
				else
				{
					//办理了特殊的活动可以预约到活动结束
					CSAppException.appError("-1", "您当前的产品有包年套餐,未变更产品不能再选择营销活动!");
				}
			}
			
			//包年套餐即将到期
			if("1".equals(isYearProduct) && "1".equals(bookTag)) 
			{
				//判断选择的时间是否大于当前结束时间
				//如果用户选择的时间小于协议结束时间，则不允许提交						
				if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) > 0)
				{
					String tipMsg = "您当前有包年套餐或营销活动未到期，您本次选择的生效时间为[" + bookingDate
						+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
					
					CSAppException.appError("-1", tipMsg);
				}
			}
			
			if("1".equals(haveSaleActive) && "0".equals(bookTag))
			{
				if("1".equals(salePackageIdFlag) && !"67220429".equals(userSaleProductId)  && !"69908016".equals(userSaleProductId))
				{ 
					//办理了特殊的活动可以预约到活动结束
					//如果用户选择的时间小于协议结束时间，则不允许提交						
					if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
					{
						String tipMsg = "您当前有营销活动，您本次选择的生效时间为[" + bookingDate
							+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
						
						CSAppException.appError("-1", tipMsg);
					}
				}
				else
				{  
					////办理了特殊的活动可以预约到活动结束
					CSAppException.appError("-1", "您当前有营销活动,未变更产品不能再选择营销活动!");
				}
			}
			
			if("1".equals(haveSaleActive) && "1".equals(bookTag)) //营销活动即将到期
			{
				//判断选择的时间是否大于当前结束时间
				if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
				{
					String tipMsg = "您当前有包年套餐或营销活动未到期，您本次选择的生效时间为[" + bookingDate
						+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
					
					CSAppException.appError("-1", tipMsg);
				}
			}
		}
		//如果是降档或者速率不变
		else if("3".equals(changeUpDownTag) || "2".equals(changeUpDownTag))
		{
			//有包年或营销活动的情况，如果未到期不能选择营销活动
			if("0".equals(bookTag))
			{
				if("1".equals(isYearProduct))
				{
					CSAppException.appError("-1", "您当前的产品有包年套餐未到期不能办理此业务!");
				}
				
				if("1".equals(haveSaleActive))
				{
					CSAppException.appError("-1", "您当前的营销活动未到期不能办理此业务!");
				}
			}
		}
		//升档
		else if("1".equals(changeUpDownTag))
		{
			//判断当前是否有包年套餐或者包年营销活动
			if("1".equals(isYearProduct))
			{
				//只有包年升级到包年能随意升，其他需要等包年到期
				if (!"WIDE_YEAR_ACTIVE".equals(newSelectActiveType))
				{
					//有包年或营销活动的情况，如果未到期不能选择营销活动
					if ("0".equals(bookTag))
					{
						if("1".equals(salePackageIdFlag) && !"67220429".equals(userSaleProductId) && !"69908016".equals(userSaleProductId))
						{
							//判断选择的时间是否大于当前结束时间
							if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
							{
								String tipMsg = "您当前有包年套餐或营销活动未到期，您本次选择的生效时间为[" + bookingDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
								
								CSAppException.appError("-1", tipMsg);
							}
						}
						else
						{
							CSAppException.appError("-1", "您当前的产品有包年套餐未到期不能办理此业务!");
						}
					}
					else if ("1".equals(bookTag))
					{
						//判断选择的时间是否大于当前结束时间
						//如果用户选择的时间小于协议结束时间，则不允许提交						
						if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
						{
							String tipMsg = "您当前有包年套餐或营销活动未到期，您本次选择的生效时间为[" + bookingDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
							
							CSAppException.appError("-1", tipMsg);
						}
					}
				}
			}
			
			//如果原来有营销活动
			if ("1".equals(haveSaleActive))
			{
				//如果没有则表示协原营销活动协议期内不能升级到新的营销活动
				if (StringUtils.isBlank(upgradeSaleActiveProductIds) 
						|| upgradeSaleActiveProductIds.indexOf(newSaleProductId) < 0)
				{
					//包年未到期不允许变更
					if("0".equals(bookTag))
					{
						if("1".equals(salePackageIdFlag) && !"67220429".equals(userSaleProductId) && !"69908016".equals(userSaleProductId))
						{
							//判断选择的时间是否大于当前结束时间
							if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
							{
								String tipMsg = "您当前有包年套餐或营销活动未到期，您本次选择的生效时间为[" + bookingDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
								
								CSAppException.appError("-1", tipMsg);
							}
						}
						else
						{
							CSAppException.appError("-1", "您的原营销活动未到期，不能选择新的营销活动!");
						}
					}
					
					if("1".equals(bookTag))
					{
						//如果用户选择的时间小于协议结束时间，则不允许提交						
						if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
						{
							String tipMsg = "您当前产品有营销活动套餐，您本次选择的生效时间为[" + bookingDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
								
							CSAppException.appError("-1", tipMsg);
						}
					}
				}
			}
			
		}
		
//		changeWidenetProduct.queryActiveInfo();
    	//调用营销包规则校验
		checkSalePackageInfo(input);
		
		resultData.put("RESULT_CODE", "0");
		
		return resultData;
    }
    
    /**
     * 调用营销包规则校验
     * @param data
     * @throws Exception
     */
    public void checkSalePackageInfo(IData data) throws Exception
    {
    	if(!"67220429".equals(data.getString("USER_SALE_PRODUCT_ID","")) && !"69908016".equals(data.getString("USER_SALE_PRODUCT_ID","")))
    	{
    		if("60012727".equals(data.getString("NEW_PACKAGE_ID",""))||"60012741".equals(data.getString("NEW_PACKAGE_ID","")))
    		{ 
    			//add 20170511
    			data.put("SPECIAL_SALE_FLAG", "1");
    		}
    	}
    	
    	//查询活动详情
        CSAppCall.call("SS.WidenetChangeProductNewSVC.initPackageInfo", data);
        
        data.put("PRODUCT_ID",data.getString("NEW_SALE_PRODUCT_ID"));
        data.put("PACKAGE_ID",data.getString("NEW_PACKAGE_ID"));
        data.put("PRE_TYPE",  "1");// 预受理校验，不写台账
        data.put("ORDER_TYPE_CODE", "601");
        data.put("TRADE_TYPE_CODE", "240");
        
        CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", data);
        
    }
    
    
    /**
     * 宽带产品变更提交前费用校验
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkFeeBeforeSubmitIntf(IData input) throws Exception
    {
    	//校验新产品ID是否传入
    	IDataUtil.chkParam(input, "PRODUCT_ID");
    	
    	//校验新产品ID是否传入
    	IDataUtil.chkParam(input, "SALE_ACTIVE_ID");

    	//校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        
        //校验产品变更预约时间是否传入
        IDataUtil.chkParam(input, "BOOKING_DATE");
    	
        //1.获得用户老的营销活动产品ID、包ID
        input.put("SERIAL_NUMBER_A", input.getString("SERIAL_NUMBER"));
        input.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        input.put("TRADE_TYPE_CODE", "601");
        IDataset checkWidenetActives = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", input);
        if (IDataUtil.isNotEmpty(checkWidenetActives))
        {
        	IData activeData = checkWidenetActives.getData(0);
            
        	input.put("OLD_SALE_ACTIVE_PRODUCT_ID", activeData.getString("PRODUCT_ID",""));
        	input.put("OLD_SALE_ACTIVE_PACKAGE_ID", activeData.getString("PACKAGE_ID",""));
        }
        
        //获得当前选择的营销活动的产品ID、包ID
        IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "601", input.getString("PRODUCT_ID"), input.getString("SALE_ACTIVE_ID"), "0898");
        if (IDataUtil.isNotEmpty(saleActiveIDataset))
        {
            IData saleActiveData = saleActiveIDataset.first();
            
            input.put("NEW_SALE_ACTIVE_PRODUCT_ID", saleActiveData.getString("PARA_CODE4"));
            input.put("NEW_SALE_ACTIVE_PACKAGE_ID", saleActiveData.getString("PARA_CODE5"));
            
        }
        else
        {
            CSAppException.appError("-1", "该营销活动配置信息不存在，请联系管理员！");
        }
        
        //营销活动费用校验
        IData result = CSAppCall.callOne("SS.WidenetChangeProductNewSVC.checkFeeBeforeSubmit", input);
        
        return result;
    }
    
    /**
     * 宽带产品变更校验并获取变更类型
     * @param input
     * @return
     * @throws Exception
     */
    public String getChangeType(IData input) throws Exception
    {
        //校验新产品ID是否传入
    	IDataUtil.chkParam(input, "PRODUCT_ID");

    	//校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        
        //校验产品变更预约时间是否传入
        IDataUtil.chkParam(input, "BOOKING_DATE");
        
        String serialNum = input.getString("SERIAL_NUMBER");
        
        String wideSerialNum = "";
        String newProductId = input.getString("PRODUCT_ID");
        String saleActiveId = input.getString("SALE_ACTIVE_ID");
        

        String changeUpDownTag = "0"; //速率升级标记，0：不变，1：升档，2：降档，3：速率不变，产品变
		
        String upgradeSaleActiveProductIds= ""; //当前营销活动在产品变更升档时可以选择的营销产品ID
		
        String newSalePackageId = "";
		//新选的营销活动类型
        String newSelectActiveType = "";
        String newSaleProductId = "";
        String salePackageIdFlag = "";
        String userSaleProductId = "";//用户当前的活动对应的产品id
        String userSalePackageId = "";//用户当前的活动对应的包id
		
		
        String userProductId = "";
        //新产品速率
        String new_rate = WideNetUtil.getWidenetProductRate(newProductId);
      	//老产品速率
        String old_rate = WideNetUtil.getWidenetUserRate(serialNum);
      
        //协议到期标志，0：包年或营销活动协议未到期，1：3个月内到期，或无包年及营销活动
        String bookTag = "";
        //是否包年套餐，1：是，0：否
        String isYearProduct = "0";
        //是否有营销活动，1：是,0:否
        String haveSaleActive = "0";
        //是否最后一个月
        String endFlag = "";
        //用户选择的预约产品生效时间 
        String bookingDate = input.getString("BOOKING_DATE");
        //用户包年套餐或营销活动的结束时间
        String agreementEndDate = ""; 
        
        if (!serialNum.startsWith("KD_"))
        {
        	wideSerialNum = "KD_"+ serialNum;
        }
    	
        IData userInfo = UcaInfoQry.qryUserInfoBySn(wideSerialNum);
        
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.appError("-1", "该用户没有办理宽带！");
        }
        
        String userId = userInfo.getString("USER_ID");
        input.put("USER_ID", userId);
    	
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
        String sysDate = SysDateMgr.getSysTime();

        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                if (userProduct.getString("START_DATE").compareTo(sysDate) < 0)
                {
                	userProductId = userProduct.getString("PRODUCT_ID");
                    break;
                }
            }
        }
        
        input.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
	   	input.put("TRADE_TYPE_CODE", "601");
		IDataset widenetProductInfos = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetProduct", input);
        
        if (IDataUtil.isNotEmpty(widenetProductInfos))
        {
        	//包年套餐=1
        	isYearProduct = "1";
        	
        	agreementEndDate = widenetProductInfos.getData(0).getString("END_DATE");//包年的直接取结束日期
        	bookTag = widenetProductInfos.getData(0).getString("V_BOOK_TAG","");
        	endFlag = widenetProductInfos.getData(0).getString("FLAG","");
        }
        
        input.put("SERIAL_NUMBER_A", serialNum);
        IDataset checkWidenetActives = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", input);
        
        if (IDataUtil.isNotEmpty(checkWidenetActives))
        {
        	IData activeData = checkWidenetActives.getData(0);
            
        	haveSaleActive = "1";//有营销活动=1
        	
        	bookTag = activeData.getString("V_BOOK_TAG","");
        	agreementEndDate = activeData.getString("V_END_DATE");
        	endFlag = activeData.getString("FLAG","");
        	userSaleProductId = activeData.getString("PRODUCT_ID","");
        	userSalePackageId = activeData.getString("PACKAGE_ID","");
        	
        	//产品变更升档时，可选则的营销活动产品ID
        	upgradeSaleActiveProductIds =  activeData.getString("UPGRADE_SALE_ACTIVE_PRODUCT_IDS","");
        }
		
		
        if(newProductId.equals(userProductId) )
	   	{
	   		changeUpDownTag = "0";
	   		
	   		input.put("WIDE_USER_CREATE_SALE_ACTIVE", "0");
	   		
//	   		changeFlag = true;
	   		
	   	}
        else
        {
        	input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
        }
        
        
		if(Integer.valueOf(new_rate) >  Integer.valueOf(old_rate))
		{					
			changeUpDownTag = "1";
		}
		else if(Integer.valueOf(new_rate) <  Integer.valueOf(old_rate))
		{	
			changeUpDownTag = "2";
		}
	   	else if(Integer.valueOf(new_rate) ==  Integer.valueOf(old_rate))
	   	{
	   		changeUpDownTag = "3";
		}
        
		input.put("CHANGE_UP_DOWN_TAG", changeUpDownTag);
		
        
		if (StringUtils.isNotBlank(saleActiveId))
		{
			IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "601", newProductId, saleActiveId, "0898");
	        
	        if (IDataUtil.isNotEmpty(saleActiveIDataset))
	        {
	            IData saleActiveData = saleActiveIDataset.first();
	              
	            newSaleProductId = saleActiveData.getString("PARA_CODE4");
	            newSalePackageId = saleActiveData.getString("PARA_CODE5");
	            newSelectActiveType = saleActiveData.getString("PARA_CODE5");
	            salePackageIdFlag = saleActiveData.getString("PARA_CODE8"); 
	        }
	        else
	        {
	            CSAppException.appError("-1", "该营销活动配置信息不存在，请联系管理员！");
	        }
		}
    	

		String changeType = "1";
		
//		if("3"==$("#PACKAGE_VALID")[0].selectedIndex && salePackageIdFlag!= "1")
//		{
//			MessageBox.alert("提示","特定的活动才能选这个预约时间");
//		}
		
		//如果产品没有变更
		if("0".equals(changeUpDownTag))
		{
			//产品未变的情况
			//未做任何改变，不能提交
			if(StringUtils.isBlank(newSalePackageId))
			{
				CSAppException.appError("-1", "您没有进行任何操作，不能提交!");
			}
			
			//原无营销活动
			if(StringUtils.isEmpty(userSaleProductId)) 
			{
				//原套餐有包年的情况
				if("1".equals(isYearProduct))
				{
					//如果包年套餐当前不是最后一个月
					if ("1".equals(endFlag))
					{
						//新选的营销活动是特殊营销活动
						if("1".equals(salePackageIdFlag))
						{
							//办理了特殊的活动可以预约到活动结束
							//如果用户选择的时间小于协议结束时间，则不允许提交						
							if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
							{
								String tipMsg = "您当前有包年套餐，您本次选择的生效时间为[" + bookingDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
							
								CSAppException.appError("-1", tipMsg);
							}
							else
							{
								changeType = "2";    //原产品没有营销活动，新营销活动，新产品未变，优惠未变，新增营销活动
								return changeType;
							}
						}
						else
						{
							CSAppException.appError("-1", "您的当前产品有包年套餐，且您未进行产品变更,不能办理营销活动!");
						}
					}
					else 
					{
						changeType = "2";    
						return changeType;
					}
				}
				else 
				{
					changeType = "2";    //原产品没有营销活动，新营销活动也无，新产品未变，优惠未变，新增营销活动
					return changeType;
				}
			}
			else
			{
				//原有营销活动的情况，不变产品的情况下，不管原活动有没有结束都不允许再办理营销活动，因为原营销活动可能是2050年结束
				if(newSalePackageId.equals(userSalePackageId))
				{
					//原营销活动非本月底结束
					CSAppException.appError("-1", "您当前已经有营销活动，未变更产品，不能再选择营销活动!");
				}
				else
				{
					//判断原活动是否到期，不到期不能办理
					//办理了特殊的活动可以预约到活动结束
					if("1".equals(bookTag) || "0".equals(salePackageIdFlag))
					{
						//如果用户选择的时间小于协议结束时间，则不允许提交						
						if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
						{
							String tipMsg = "您当前有营销活动未到期，您本次选择的生效时间为[" + bookingDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
								
							CSAppException.appError("-1", tipMsg);
						}
					}
					else
					{
						//办理了特殊的活动可以预约到活动结束
						CSAppException.appError("-1", "您当前有营销活动未到期，不能再办理营销活动!");
					}
					
					changeType = "8";     //终止原活动，新增新活动
					return changeType;
				}
			}
			
		} 
		//产品升档开始
		else if("1".equals(changeUpDownTag))	
		{
			//原包年套餐
			if("1".equals(isYearProduct))
			{
				//只有包年升级到包年能随意升，其他需要等包年到期
				if (!"WIDE_YEAR_ACTIVE".equals(newSelectActiveType))
				{
					//判断原活动是否三个月内到期，不到期不能办理或者是否办理了特殊营销活动
					if("1".equals(bookTag) || "1".equals(salePackageIdFlag))
					{
						//如果用户选择的时间小于协议结束时间，则不允许提交						
						if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
						{
							String tipMsg = "您当前产品有包年套餐，您本次选择的生效时间为[" + bookingDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
								
							CSAppException.appError("-1", tipMsg);
						}
					}
					else
					{   //办理了特殊的活动可以预约到活动结束
						//包年未到期不允许变更
						CSAppException.appError("-1", "您的原产品中有包年优惠未到期，不能进行产品变更!");
					}
				}
				
				if(StringUtils.isEmpty(newSalePackageId))
				{
					changeType = "1";     //升档，包年转包年的情况
					return changeType ;
				}
				else
				{
					changeType = "3";     //升档，包年转活动的情况，需要同时调用产品变更接口及营销活动受理接口
					return changeType ;
				}
			}
			else if("1".equals(haveSaleActive))  //原有营销活动的情况
			{
				//新选没有营销活动
				if(StringUtils.isEmpty(newSalePackageId))
				{
					CSAppException.appError("-1", "您之前有营销活动未到期，要变更产品需要选营销活动!");
					
					changeType = "4";     //升档，活动转包年的情况，需要同时调用产品变更接口及营销活动终止接口
					return changeType;
				}
				else
				{
					//如果没有则表示协原营销活动协议期内不能升级到新的营销活动
					if (StringUtils.isEmpty(upgradeSaleActiveProductIds)
							|| upgradeSaleActiveProductIds.indexOf(newSaleProductId) < 0)
					{
						//营销活动在预约期内到期或 办理了特殊营销活动
						if("1".equals(bookTag) || "1".equals(salePackageIdFlag))
						{
							//如果用户选择的时间小于协议结束时间，则不允许提交						
							if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
							{
								String tipMsg = "您当前产品有营销活动套餐，您本次选择的生效时间为[" + bookingDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请重新选择生效时间!";
									
								CSAppException.appError("-1", tipMsg);
							}
						}
						else
						{  
							//办理了特殊的活动可以预约到活动结束
							CSAppException.appError("-1", "您的原营销活动未到期，不能选择新的营销活动!");
						}
					}
					
					changeType = "5";     //升档，活动转活动的情况，需要同时调用产品变更接口及营销活动终止接口和营销活动受理接口
					return changeType ;
				}
			}
			else 
			{
				//原产品无包年套餐和营销活动
				if(StringUtils.isEmpty(newSalePackageId))
				{
					changeType = "1";  
					return changeType ;
				}
				else
				{
					changeType = "3";      //调用调用产品变更接口和营销活动受理接口
					return changeType ;
				}
			}
		}
		//产品降档开始
		else if("2".equals(changeUpDownTag))	
		{
			if("1".equals(isYearProduct))
			{
				//原产品有包年套餐
				if("0".equals(bookTag))
				{
					CSAppException.appError("-1", "您当前产品有包年套餐未到期，不能办理产品降档变更!");
				}
				
				
				//包年套餐 也不允许进行营销活动受理
				if(StringUtils.isNotBlank(newSalePackageId) && "1".equals(endFlag))
				{
					//如果用户选择的时间小于协议结束时间，则不允许提交						
					if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
					{
						String tipMsg = "您当前产品有包年套餐，产品降档变更不允许受理营销活动，您本次选择的生效时间为[" + bookingDate
							+ "]您的协议期截至到[" + agreementEndDate + "],请取消营销活动或进行产品升档变更或重新选择生效时间!";
							
						CSAppException.appError("-1", tipMsg);
					}
					else
					{
						changeType = "3";  
						return changeType ;
					}
				}
				else if(StringUtils.isNotBlank(newSalePackageId) && "0".equals(endFlag))
				{
					//包年套餐的最后一个月，也是允许营销活动受理的
					
					changeType = "3";  
					return changeType ;
				}
				else if(StringUtils.isBlank(newSalePackageId) && "0".equals(endFlag))
				{
					changeType = "1";  
					return changeType ;
				}
				else if(StringUtils.isBlank(newSalePackageId) && "1".equals(endFlag))
				{
					if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
					{
						String tipMsg = "您当前产品有包年套餐未到期，不能办理产品降档变更，您本次选择的生效时间为[" + bookingDate
							+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
							
						CSAppException.appError("-1", tipMsg);
					}
					else
					{
						changeType = "1";  
						return changeType ;
					}
				}
			}
			else if("1".equals(haveSaleActive))
			{
				//原产品有营销活动
				if("0".equals(bookTag))
				{
					CSAppException.appError("-1", "您当前有营销活动未到期，不能办理产品降档变更!");
				}
				
				if(StringUtils.isNotBlank(newSalePackageId) && "1".equals(endFlag))
				{
					if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
					{
						String tipMsg = "您当前有营销活动未到期，不能办理产品降档变更，您本次选择的生效时间为[" + bookingDate
							+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
							
						CSAppException.appError("-1", tipMsg);
					}
					else
					{
						changeType = "6";   //降档，原有营销活动，又新选了营销活动，则需要判断预约时间是否大于原活动时间，小于则需要终止原活动
						return changeType ;
					}
				}
				else if(StringUtils.isNotBlank(newSalePackageId) && "0".equals(endFlag))
				{
					changeType = "6";      //降档，原有营销活动，又新选了营销活动，则需要判断预约时间是否大于原活动时间，小于则需要终止原活动
					return changeType ;
				}
				else if(StringUtils.isBlank(newSalePackageId) && "0".equals(endFlag))
				{
					changeType = "7";     //降档，原有营销活动，无新选营销活动，则需要判断预约时间是否大于原活动时间，小于则需要终止原活动
					return changeType ;
				}
				else if(StringUtils.isBlank(newSalePackageId) && "1".equals(endFlag))
				{
					if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
					{
						String tipMsg = "您当前有营销活动未到期，不能办理产品降档变更，您本次选择的生效时间为[" + bookingDate
							+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
							
						CSAppException.appError("-1", tipMsg);
					}
					else
					{
						changeType = "7";  //降档，原有营销活动，无新选营销活动，则需要判断预约时间是否大于原活动时间，小于则需要终止原活动
						return changeType ;
					}
				}
			}
			else
			{
				//无包年和营销活动，允许自由降档
				if(StringUtils.isNotBlank(newSalePackageId))
				{
					changeType = "3";  
					return changeType ;
				}
				else
				{
					changeType = "1";  
					return changeType ;
				}
			}
		}//产品降档结束
		else if("3".equals(changeUpDownTag))	//产品同档变更开始（产品变，速率不变）
		{
			if("1".equals(isYearProduct))
			{
				if("0".equals(bookTag))
				{
					CSAppException.appError("-1", "您当前产品有包年套餐未到期，不能办理产品变更!");
				}
				else if("1".equals(bookTag))
				{
					if(StringUtils.isNotBlank(newSalePackageId))
					{
						if("0".equals(endFlag))
						{
							changeType = "3";   //同时调用产品变更接口和营销活动受理接口
							return changeType ;
						}
						else
						{
							if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
							{
								String tipMsg = "您当前包年套餐未到期，不能办理产品变更，您本次选择的生效时间为[" + bookingDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
									
								CSAppException.appError("-1", tipMsg);
							}
							
							changeType = "3";   //同时调用产品变更接口和营销活动受理接口
							return changeType ;
						}
					}
					else
					{
						if("1".equals(endFlag))
						{
							if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
							{
								String tipMsg = "您当前包年套餐未到期，不能办理产品变更，您本次选择的生效时间为[" + bookingDate
									+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
									
								CSAppException.appError("-1", tipMsg);
							}
						}
						
						changeType = "1"; 
						return changeType ;
					}
				}
			}
			else if("1".equals(haveSaleActive))
			{
				if("0".equals(bookTag))
				{
					CSAppException.appError("-1", "您当前有营销活动未到期，不能办理产品变更!");
				}
				else if("1".equals(bookTag))
				{
					if("1".equals(endFlag))
					{
						if(SysDateMgr.compareToYYYYMMDD(bookingDate, agreementEndDate) < 0)
						{
							String tipMsg = "您当前有营销活动未到期，不能办理产品变更，您本次选择的生效时间为[" + bookingDate
								+ "]您的协议期截至到[" + agreementEndDate + "],请进行产品升档变更或重新选择生效时间!";
								
							CSAppException.appError("-1", tipMsg);
						}
					}
						
					if(StringUtils.isNotBlank(newSalePackageId))
					{
						changeType = "6";   //同时调用产品变更接口和营销活动受理接口,生效日期必须为协议到期的次月
						return changeType ;
					}
					else
					{
						changeType = "7";   //调用产品变更接口生效日期必须为协议到期的次月
						return changeType ;
					}
				}
			}
			else 
			{
				//无包年和营销活动，允许同档变更
				if(StringUtils.isNotBlank(newSalePackageId))
				{
					changeType = "3";  
					return changeType ;
				}
				else
				{
					changeType = "1";  
					return changeType ;
				}
			}
		} //产品同档变更结束
		else
		{
			CSAppException.appError("-1", "未知操作类型，不能提交!UP_DOWN_TAG=" + changeUpDownTag + ";BOOK_TAG=" + bookTag + ";IS_YEAR_PRODUCT=" + isYearProduct + ";END_FALG=" + endFlag);
			
		}
		
		return changeType ;
    }
    
    
    /**
     * 宽带产品变更业务受理接口（）
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset changeWidenetProductIntf(IData input) throws Exception
    {
    	IDataset result = null;
    	
    	//校验服务号码是否传入
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	
    	//校验新产品ID是否传入
    	IDataUtil.chkParam(input, "PRODUCT_ID");
    	
        //校验产品变更预约时间是否传入
        IDataUtil.chkParam(input, "BOOKING_DATE");
        
        
        input.put("AUTH_SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        input.put("NEW_PRODUCT_ID", input.getString("PRODUCT_ID"));
        
        
        String saleActiveId = input.getString("SALE_ACTIVE_ID");
    	
        if (StringUtils.isNotBlank(saleActiveId))
        {
        	IData feeData = checkFeeBeforeSubmitIntf(input);
        	
        	input.put("WIDE_ACTIVE_PAY_FEE", feeData.getString("WIDE_ACTIVE_PAY_FEE"));
        	input.put("YEAR_DISCNT_REMAIN_FEE", feeData.getString("YEAR_DISCNT_REMAIN_FEE"));
        	input.put("REMAIN_FEE", feeData.getString("REMAIN_FEE"));
        	input.put("ACCT_REMAIN_FEE", feeData.getString("ACCT_REMAIN_FEE"));
        	
        	
        	input.put("V_USER_PRODUCT_ID", input.getString("OLD_SALE_ACTIVE_PRODUCT_ID",""));
        	input.put("V_USER_PACKAGE_ID", input.getString("OLD_SALE_ACTIVE_PACKAGE_ID",""));
        	        	
        	input.put("NEW_SALE_PRODUCT_ID", input.getString("NEW_SALE_ACTIVE_PRODUCT_ID"));
        	input.put("NEW_SALE_PACKAGE_ID", input.getString("NEW_SALE_ACTIVE_PACKAGE_ID"));
        }
        
        //宽带产品变更校验并获取变更类型
        String changeType = getChangeType(input);
        
        input.put("CHANGE_TYPE", changeType);
        
        if("60012727".equals(input.getString("NEW_SALE_PACKAGE_ID",""))||"60012741".equals(input.getString("NEW_SALE_PACKAGE_ID","")))
        {
        	//标记是否是特殊营销活动
        	input.put("SPECIAL_SALE_FLAG", "1");
        }
        
        
        if ("1".equals(changeType) || "3".equals(changeType) || "4".equals(changeType) || "5".equals(changeType) || "6".equals(changeType) || "7".equals(changeType))
        {
        	//校验新服务ID是否传入
        	IDataUtil.chkParam(input, "SERVICE_ID");
        	
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
    	        element.put("START_DATE", input.getString("BOOKING_DATE"));
    	        element.put("END_DATE", "2050-12-31");
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
    	        element.put("START_DATE", input.getString("BOOKING_DATE"));
    	        element.put("END_DATE", "2050-12-31");
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
                    element.put("END_DATE", SysDateMgr.getLastSecond(input.getString("BOOKING_DATE")));
                    element.put("PRODUCT_ID", oldPorductInfo.getString("PRODUCT_ID"));
                    
                    selectedelements.add(element);
                }
            }
            
            input.put("WIDE_USER_SELECTED_SERVICEIDS", rateSvc);
            input.put("SELECTED_ELEMENTS", selectedelements.toString());
        	
        	
        	//1、调用产品变更接口
    		//3、调用产品变更接口与营销活动受理接口
    		//4、调用产品变更接口及营销活动终止接口
    		//5、调用产品变更接口及营销活动终止接口，营销活动受理接口
        	result = CSAppCall.call("SS.WidenetChangeProductNewRegSVC.tradeReg", input);
        }
        else if ("2".equals(changeType) || "8".equals(changeType))
        {
        	//2、只调用营销活动接口
        	input.put("TRADE_TYPE_CODE", "240");
        	input.put("PRODUCT_ID", input.getString("NEW_SALE_PRODUCT_ID"));
        	input.put("PACKAGE_ID", input.getString("NEW_SALE_PACKAGE_ID"));
        	String serialNumber = input.getString("SERIAL_NUMBER");
        	if(serialNumber != null && !"".equals(serialNumber))
        	{
        		if(serialNumber.startsWith("KD_"))
        		{
        			serialNumber = serialNumber.replace("KD_", "");
        			input.put("SERIAL_NUMBER", serialNumber);
        		}
        	}
        	result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);
        }
        else
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "未知的操作类型CHANGE_TYPE：" + changeType);
        }
        
    	return result;
    }
    
    
    /**
     * BUS202003180003 关于家宽云平台联调5个嵌套页面底层接口的需求
     * 查询融合宽带
     * @param data
     * @return
     * @throws Exception
     */
    public IData getRHProudctInfo(IData data) throws Exception
    {
    	IDataUtil.chkParam(data, "USER_PRODUCT_ID");
    	
    	IDataset pruodctList = CommparaInfoQry.getCommpara("CSM", "6190", data.getString("USER_PRODUCT_ID"), "0898");
    	
    	IData resultData = new DataMap();
    	
    	if (IDataUtil.isNotEmpty(pruodctList)) 
    	{
    		resultData.put("PARAM_CODE", pruodctList.getData(0).getString("PARAM_CODE"));
    		resultData.put("PARAM_NAME", pruodctList.getData(0).getString("PARAM_NAME"));
		}
    	
    	
    	return resultData;
    }
    
    
}