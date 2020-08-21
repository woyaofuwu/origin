
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew;


import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.util.common.StringTools;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetmove.WidenetMoveBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class WidenetChangeProductSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    //private static Logger logger = Logger.getLogger(WidenetChangeProductSVC.class);
    public IDataset loadProductInfo(IData param) throws Exception
    {
    	String prod_mode = param.getString("PROD_MODE");
    	String userId = param.getString("USER_ID");
        String productMode = "";

        String routeEparchyCode = BizRoute.getRouteId();
        String userProductName = "";
        String userBrandName = "";
        String userProductId = "";
        String userBrandCode = "";
        String userProductStartDate = "";
        String userProductEndDate = "";
        
        String nextProductId = "";
        String nextBrandCode = "";
        String nextBrandName = "";
        String nextProductName = "";
        String nextProductStartDate = "";
        String nextProductEndDate = "";
        // 原本传入的为宽带账号的USER_ID,这里转换为服务号码的USER_ID add by liwei29
        UcaData kduca = UcaDataFactory.getUcaByUserId(userId);
        String serialNumber = kduca.getSerialNumber();
        if (serialNumber.startsWith("KD_")) {
            serialNumber = serialNumber.replace("KD_","");
        }
        IDataset userData=UserInfoQry.getUserInfoBySerailNumber("0",serialNumber);
        
        IDataset saleActiveInfos1 = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
        //---add by zhangxing3 for REQ201704130018优惠期内进行宽带产品变更问题优化---start
        IDataset userInfo = UserInfoQry.getUserInfoByUserIdTag(userId, "0");
        if(IDataUtil.isNotEmpty(userInfo))
        {
	        String inDate = userInfo.getData(0).getString("OPEN_DATE");
			String inMonth = SysDateMgr.getDateForYYYYMMDD(inDate).substring(0, 6);
			String currMonth = SysDateMgr.getNowCycle().substring(0, 6);
			if (inMonth.equals(currMonth)&&IDataUtil.isNotEmpty(saleActiveInfos1))//判断是不是宽带首月免费期内不能办理产品变更
			{
				CSAppException.apperr(CrmUserException.CRM_USER_783,"宽带免费期内不能办理产品变更!");
			}
        }

        // 查询生效的优惠
        IDataset discntInfo = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
    	
        if (IDataUtil.isEmpty(discntInfo)&&IDataUtil.isNotEmpty(saleActiveInfos1))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "宽带免费期内不能办理产品变更!");
        }
        //--add by zhangxing3 for REQ201704130018优惠期内进行宽带产品变更问题优化---end

        
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
                    userBrandCode = userProduct.getString("BRAND_CODE");
                    productMode = userProduct.getString("PRODUCT_MODE");
                    userProductStartDate = userProduct.getString("START_DATE");
                    userProductEndDate = userProduct.getString("END_DATE");
                }
                else
                {
                    nextProductId = userProduct.getString("PRODUCT_ID");
                    nextBrandCode = userProduct.getString("BRAND_CODE");                                        
                    nextProductStartDate = userProduct.getString("START_DATE");
                	nextProductEndDate = userProduct.getString("END_DATE");
                }
            }
        }

        IData result = new DataMap();
        
        //REQ201903080003一机多宽优惠活动开发需求调整
        //IDataset userOtherInfo = UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "ONESN_MANYWIDE");
        IDataset ids = RelaUUInfoQry.getUserRelationRole2(userId,"58","1");
        if (IDataUtil.isNotEmpty(ids))
        {
        	result.put("IS_ONESN_MANYWIDE", "1");
        }
        //REQ201903080003一机多宽优惠活动开发需求调整
        
        // 查询用户当前品牌名称，当前产品名称
        userBrandName = UBrandInfoQry.getBrandNameByBrandCode(userBrandCode);
        userProductName = UProductInfoQry.getProductNameByProductId(userProductId);
        result.put("USER_PRODUCT_NAME", userProductName);
        result.put("USER_PRODUCT_ID", userProductId);
        result.put("USER_BRAND_NAME", userBrandName);
        result.put("USER_PRODUCT_START_DATE", userProductStartDate);
        result.put("USER_PRODUCT_END_DATE", userProductEndDate);

        if (!StringUtils.isBlank(nextProductId))
        {
            nextProductName = UProductInfoQry.getProductNameByProductId(nextProductId);
            nextBrandName = UBrandInfoQry.getBrandNameByBrandCode(nextBrandCode);
            result.put("NEXT_PRODUCT_NAME", nextProductName);
            result.put("NEXT_BRAND_NAME", nextBrandName);
            result.put("NEXT_PRODUCT_ID", nextProductId);
            result.put("NEXT_PRODUCT_START_DATE", nextProductStartDate);
            result.put("NEXT_PRODUCT_END_DATE", nextProductEndDate);
        }
        result.put("EPARCHY_CODE", routeEparchyCode);

        
        
        //查询当前产品的速率，如果未查到说明没有配置，主要是因为有些产品已经不用了，但是用户的产品还是旧的，容易造成错误
//        IDataset rate_ds = WidenetInfoQry.queryWidenetRateByProductID(userProductId);
        IDataset rate_ds = CommparaInfoQry.getCommpara("CSM", "4000", userProductId, "0898");
        
        if (IDataUtil.isEmpty(rate_ds))
        {
            IDataset userSvcs  =  UserSvcInfoQry.queryUserSvcByUserIdAllNew(userId);
            
            if (IDataUtil.isNotEmpty(userSvcs))
            {
                for (int i = 0; i < userSvcs.size(); i++)
                {
                    if ("0".equals(userSvcs.getData(i).getString("MAIN_TAG")))
                    {
                        rate_ds = CommparaInfoQry.getCommpara("CSM", "4000", userSvcs.getData(i).getString("SERVICE_ID"), "0898");
                        break;
                    }
                }
            }
        }
        
        if (IDataUtil.isNotEmpty(rate_ds))
        {
            result.put("USER_PRODUCT_RATE", rate_ds.getData(0).getString("PARA_CODE1",""));
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查到产品对应的速率，用户当前产品【"+userProductName+"】速率参数配置有误，请检查！");
        }
        
        //
        if ("".equals(prod_mode))
        {
        	prod_mode=productMode;
        }
        //修改原有方法，改为调用新增的查询产品列表的方法，新增的方法有速率信息
        IDataset widenetProductInfos =WidenetInfoQry.getWidenetProduct_RATE(prod_mode, CSBizBean.getTradeEparchyCode());// ProductInfoQry.getWidenetProductInfo(prod_mode, CSBizBean.getTradeEparchyCode());
        
        //add by zhangxing3 for REQ201704110012开发1000M宽带产品资费
        //isGigabit 表示端口是否支持1000M宽带，否-不支持；是-支持
        /*String isGigabit = param.getString("isGigabit","");
        System.out.println("===========WidenetChangeProductSVC==========isGigabit:"+isGigabit);
        if("否".equals(isGigabit) || "".equals(isGigabit))//如果isGigabit = 否 或 空，表示端口不支持1000M宽带，需要从宽带产品中过滤掉
        {
        	//widenetProductInfos = WideNetUtil.filterGigabitProduct(widenetProductInfos);
        }*/
        //add by zhangxing3 for REQ201704110012开发1000M宽带产品资费 end
        
        boolean isBusiness = false;
        if(IDataUtil.isNotEmpty(userInfo))
        {
            if("BNBD".equals(userInfo.getData(0).getString("RSRV_STR10")))
            {
                isBusiness = true;;
            }
        }
        
        if (!isBusiness)
        {
            widenetProductInfos = WideNetUtil.filterBusinessProduct("", widenetProductInfos);
        }
        
        //5.17宽带畅享服务特权活动需求===========================
        
        result.put("KDTS_PRODUCT","");
        if(IDataUtil.isNotEmpty(userData)){
	        IDataset speedUpSaleActives = UserSaleActiveInfoQry.querySaleInfoByUserIdAndCommpara(userData.getData(0).getString("USER_ID"));
	       
	        if (IDataUtil.isNotEmpty(speedUpSaleActives) && speedUpSaleActives.size() > 0){
	      		String productIdKdts = speedUpSaleActives.getData(0).getString("PRODUCT_ID");
	      		String packgeIdKdts = speedUpSaleActives.getData(0).getString("PACKAGE_ID");
	      		String relationTradeIdKdts = speedUpSaleActives.getData(0).getString("RELATION_TRADE_ID");
	      		String KDTS_PRODUCT =productIdKdts+"|"+packgeIdKdts+"|"+relationTradeIdKdts;
	      		result.put("KDTS_PRODUCT",KDTS_PRODUCT);
	      		  
	      	  }
        }
        //5.17宽带畅享服务特权活动需求===========================
        
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), widenetProductInfos);
        result.put("PRODUCT_LIST", widenetProductInfos);
        // 查询用户
        IDataset results = new DatasetList();
        results.add(result);
        return results;
    }

    //判断是否是包年套餐
    public IDataset checkWidenetProduct(IData param) throws Exception
    {
    	//这里用的是宽带用户的user_id
    	//qryParam.put("USER_ID", strUserId);
    	param.put("PARAM_CODE", param.getString("TRADE_TYPE_CODE"));
    	//判断
    	IDataset ds = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_DISCNT2", param);
    	if (IDataUtil.isNotEmpty(ds))
    	{
    		//包年的直接取解释日期
    		String v_end = ds.getData(0).getString("END_DATE");
        	
        	String itag = getFlagForActive(v_end);
        	ds.getData(0).put("V_BOOK_TAG", itag);
    		return ds;
    	}
    	return null;
    }
    //判断是否办理了营销活动
    public IDataset checkWidenetActive(IData param) throws Exception
    {
    	//这里用的是手机号的user_id，需要判断，转换一下
    	String serialnumber = param.getString("SERIAL_NUMBER_A");
    	
    	//长度大于11，表示是集团号码，不需要查询营销活动。
    	if (StringUtils.isNotBlank(serialnumber) && serialnumber.length() > 11)
    	{
    	    return null;
    	}
    	
    	IDataset userinfo = UserInfoQry.getUserinfo(serialnumber);
    	String userid="";
    	if (IDataUtil.isNotEmpty(userinfo))
    	{
    		userid=userinfo.getData(0).getString("USER_ID");
    	}else
    	{
    		 CSAppException.apperr(CrmCommException.CRM_COMM_103,"未找到用户资料！");
    	}
//    	param.put("USER_ID", userid);
//    	param.put("PARAM_CODE", param.getString("TRADE_TYPE_CODE"));
//        IDataset ds = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES4", param);
    	
    	IDataset ds = CParamQry.getLimitActiveDiscnts(userid, param.getString("TRADE_TYPE_CODE"));

        if (IDataUtil.isNotEmpty(ds))
        {
        	//宽带营销活动
            IData wideActive = ds.getData(0);
            
            String v_end = wideActive.getString("START_DATE");
            String v_end2 = wideActive.getString("END_DATE");
        	String s_pakid = wideActive.getString("PACKAGE_ID");
        	String activeProductId = wideActive.getString("PRODUCT_ID");
        	
        	//宽带包年优惠产品ID配置
        	IDataset wideYearActiveCommpara = CommparaInfoQry.getCommpara("CSM", "532", "WIDE_YEAR_ACTIVE",getTradeEparchyCode());
        	
        	String isWideYearActive = "0";
        	
        	if (IDataUtil.isNotEmpty(wideYearActiveCommpara))
        	{
        	    for (int i = 0; i < wideYearActiveCommpara.size(); i++)
        	    {
        	        if (activeProductId.equals(wideYearActiveCommpara.getData(i).getString("PARA_CODE1")))
        	        {
        	            isWideYearActive = "1";
        	            
        	            break;
        	        }
        	    }
        	}
        	
        	//产品变更升档时，可选则的营销活动产品ID
        	String upgradeSaleProductIds = "";
        	
        	//宽带产品变更界面可办理的营销活动配置
            IDataset commparaDataset178 = ParamInfoQry.getProcNameByHandGatherFee("CSM", "178", "601", CSBizBean.getUserEparchyCode());
            
            if (IDataUtil.isNotEmpty(commparaDataset178))
            {
                IData commparaData178 = null;
                
                for (int i = 0; i < commparaDataset178.size(); i++)
                {
                    commparaData178 = commparaDataset178.getData(i);
                    
                    if (activeProductId.equals(commparaData178.getString("PARA_CODE4")))
                    {
                        upgradeSaleProductIds = commparaData178.getString("PARA_CODE20");
                        
                        break;
                    }
                }
            }
            
        	//获取营销活动的周期
        	//IDataset com181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", s_pakid, null);
        	IDataset com181 = CommparaInfoQry.getCommparaInfoByCode("CSM", "181", "-1", s_pakid, "0898");
        	
        	int months=12;//默认12个月
        	if (IDataUtil.isNotEmpty(com181))
        	{
        		months=com181.getData(0).getInt("PARA_CODE4",12);
        	}
        	//计算营销活动的实际结束时间，因为新的规则是活动的结束时间都是2050，所以需要重新计算
        	v_end = SysDateMgr.addMonths(v_end, (months-1));
        	v_end = SysDateMgr.getDateLastMonthSec(v_end);
        	        	
        	if(v_end2.compareTo(v_end)>0){
        		wideActive.put("V_END_DATE", v_end);
        	}else{
        		wideActive.put("V_END_DATE", v_end2);
        		v_end = v_end2;
        	}
        	
        	String itag = getFlagForActive(v_end);
        	wideActive.put("V_BOOK_TAG", itag);
        	
        	wideActive.put("USER_YEAR_ACTIVE", isWideYearActive);
        	
        	wideActive.put("UPGRADE_SALE_ACTIVE_PRODUCT_IDS", upgradeSaleProductIds);
        	
        	return ds;
        }
        return null;
    }
    public String getFlagForActive(String v_end) throws Exception
    {
    	String booktag="";
    	String now_end = SysDateMgr.getSysDate();
    	now_end = SysDateMgr.addMonths(now_end, 2);//因降档只能在结束前3个月的时候可以预约,由于预约是失效后的次月，所以这里取从本月算起3个月的最后一天
    	now_end = SysDateMgr.getDateLastMonthSec(now_end);
    	int iret = SysDateMgr.compareTo(v_end, now_end);
    	if (iret>0)
    	{
    		//活动结束时间还在3个月之后，暂时不能预约
    		booktag= "0";
    	}else if (iret<0)
    	{
    		//活动已接结束，或在3个月内结束，可以预约
    		booktag= "1";
    	}else
    	{
    		//活动在3个月后结束，刚好可以预约
    		booktag= "1";
    	}
    	return booktag;
    }
    
    private boolean checkWidenetUserMFTY(String userId) throws Exception
    {        
        IDataset discntInfo1 = UserDiscntInfoQry.getUserIMSDiscnt(userId,"8523","0898");
        if (IDataUtil.isNotEmpty(discntInfo1))
        {
        	return true;
        }
        else
        {
        	return false;
        }
    }
    
    //初始化预约日期列表
    public IDataset onInitBookTimeList(IData data) throws Exception
    {
    	//默认生效时间为次月1日
    	String firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
    	String firstDayOfNextMonth2 = "";
    	
    	IDataset bookTimeList = new DatasetList();

    	String userId = data.getString("USER_ID", "");

    	if (checkWidenetUserMFTY(userId))
    	{
    		IData bookTime0 = new DataMap();    	
        	bookTime0.put("DATA_ID", SysDateMgr.getSysTime());   	
        	bookTime0.put("DATA_NAME", SysDateMgr.getSysTime()); 
        	
        	bookTimeList.add(bookTime0);
    	}
    	
    	IData bookTime = new DataMap();    	
    	bookTime.put("DATA_ID", firstDayOfNextMonth);   	
    	bookTime.put("DATA_NAME", firstDayOfNextMonth); 
    	
    	bookTimeList.add(bookTime);
    	
    	String firstDayOfNextTwoMonth = SysDateMgr.addMonths(firstDayOfNextMonth, 1);
    	IData bookTimeTwo = new DataMap();    	
    	bookTimeTwo.put("DATA_ID", firstDayOfNextTwoMonth);   	
    	bookTimeTwo.put("DATA_NAME", firstDayOfNextTwoMonth); 
    	
    	bookTimeList.add(bookTimeTwo);
    	
    	String firstDayOfNextThreeMonth = SysDateMgr.addMonths(firstDayOfNextTwoMonth, 1);
    	IData bookTimeThree = new DataMap();    	
    	bookTimeThree.put("DATA_ID", firstDayOfNextThreeMonth);   	
    	bookTimeThree.put("DATA_NAME", firstDayOfNextThreeMonth); 
    	
    	bookTimeList.add(bookTimeThree);
    	
    	if(("1".equals(data.getString("USER_YEAR_ACTIVE", "0")) || "1".equals(data.getString("PRODUCT_YEAR", "0")))&& "0".equals(data.getString("V_BOOK_TAG", ""))){ ////包年套餐标志
            firstDayOfNextMonth2 = SysDateMgr.getDateNextMonthFirstDay(data.getString("V_END_DATE", SysDateMgr.getSysTime()));
            IData bookTimeFour = new DataMap();     
            bookTimeFour.put("DATA_ID", firstDayOfNextMonth2);      
            bookTimeFour.put("DATA_NAME", firstDayOfNextMonth2); 
            bookTimeList.add(bookTimeFour);
        }
    	
    	return bookTimeList;
    	/*
    	String endDate = data.getString("ENDDATE","");
    	if("".equals(endDate))
    	{
    		endDate = SysDateMgr.getDateLastMonthSec(SysDateMgr.getSysDate());
    	}
    	
    	//计算到底有几个月可以预约
    	String end_month = SysDateMgr.addDays(endDate, 1);
    	
    	String next_date1 = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
        String next_date2 = SysDateMgr.addMonths(next_date1, 1);
        String next_date3 = SysDateMgr.addMonths(next_date1, 2);
        IDataset iset = new DatasetList();
        
        int iret = SysDateMgr.compareTo(SysDateMgr.getDateForYYYYMMDD(end_month), SysDateMgr.getDateForYYYYMMDD(next_date1));
        if (iret>=0)
        {
        	IData dt=new DataMap();
            dt.put("DATA_ID", next_date1);
            dt.put("DATA_NAME", next_date1);
            dt.put("DATA_ATTR", SysDateMgr.addDays(next_date1, -1));
            iset.add(dt);
        }
        iret = SysDateMgr.compareTo(SysDateMgr.getDateForYYYYMMDD(end_month), SysDateMgr.getDateForYYYYMMDD(next_date2));
        if (iret>=0)
        {
        	IData dt2=new DataMap();
            dt2.put("DATA_ID", next_date2);
            dt2.put("DATA_NAME", next_date2);
            dt2.put("DATA_ATTR", SysDateMgr.addDays(next_date2, -1));
            iset.add(dt2);
        }
        
        iret = SysDateMgr.compareTo(SysDateMgr.getDateForYYYYMMDD(end_month), SysDateMgr.getDateForYYYYMMDD(next_date3));
        if (iret>=0)
        {
        	IData dt3=new DataMap();
            dt3.put("DATA_ID", next_date3);
            dt3.put("DATA_NAME", next_date3);
            dt3.put("DATA_ATTR", SysDateMgr.addDays(next_date3, -1));
            iset.add(dt3);
        }
        return iset;
        */
    }
    public IDataset resetBookTime_ALL(IData data) throws Exception
    {
    	String next_date1 = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
        String next_date2 = SysDateMgr.addMonths(next_date1, 1);
        String next_date3 = SysDateMgr.addMonths(next_date1, 2);
        IDataset iset = new DatasetList();
        	IData dt=new DataMap();
            dt.put("DATA_ID", next_date1);
            dt.put("DATA_NAME", next_date1);
            dt.put("DATA_ATTR", SysDateMgr.addDays(next_date1, -1));
            iset.add(dt);
        	IData dt2=new DataMap();
            dt2.put("DATA_ID", next_date2);
            dt2.put("DATA_NAME", next_date2);
            dt2.put("DATA_ATTR", SysDateMgr.addDays(next_date2, -1));
            iset.add(dt2);
        	IData dt3=new DataMap();
            dt3.put("DATA_ID", next_date3);
            dt3.put("DATA_NAME", next_date3);
            dt3.put("DATA_ATTR", SysDateMgr.addDays(next_date3, -1));
            iset.add(dt3);
            
        return iset;
    }
    public IDataset resetBookTime_Down(IData data) throws Exception
    {
    	String v_end = data.getString("V_END_DATE");
    	//
    	String end_month = SysDateMgr.addDays(v_end, 1);
    	String next_date1 = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
        String next_date2 = SysDateMgr.addMonths(next_date1, 1);
        String next_date3 = SysDateMgr.addMonths(next_date1, 2);
        
        IDataset iset = new DatasetList();
        
        int iret = SysDateMgr.compareTo(SysDateMgr.getDateForYYYYMMDD(end_month), SysDateMgr.getDateForYYYYMMDD(next_date1));
        if (iret==0)
        {
        	IData dt=new DataMap();
            dt.put("DATA_ID", next_date1);
            dt.put("DATA_NAME", next_date1);
            dt.put("DATA_ATTR", SysDateMgr.addDays(next_date1, -1));
            iset.add(dt);
        }
        iret = SysDateMgr.compareTo(SysDateMgr.getDateForYYYYMMDD(end_month), SysDateMgr.getDateForYYYYMMDD(next_date2));
        if (iret==0)
        {
        	IData dt2=new DataMap();
            dt2.put("DATA_ID", next_date2);
            dt2.put("DATA_NAME", next_date2);
            dt2.put("DATA_ATTR", SysDateMgr.addDays(next_date2, -1));
            iset.add(dt2);
        }
        
        iret = SysDateMgr.compareTo(SysDateMgr.getDateForYYYYMMDD(end_month), SysDateMgr.getDateForYYYYMMDD(next_date3));
        if (iret==0)
        {
        	IData dt3=new DataMap();
            dt3.put("DATA_ID", next_date3);
            dt3.put("DATA_NAME", next_date3);
            dt3.put("DATA_ATTR", SysDateMgr.addDays(next_date3, -1));
            iset.add(dt3);
        }
        return iset;
    }
    //初始化界面上的营销活动列表
    public IDataset initActivePackage(IData data) throws Exception
    {
    	String prodid = data.getString("NEW_PRODUCT_ID");
    	IDataset saleActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "601",prodid);
    	
    	saleActiveList = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), saleActiveList);
    	
    	return saleActiveList;
    }
    //初始化界面上的营销活动内容展示
    public IDataset initPackageInfo(IData data) throws Exception
    {
    	String packid = data.getString("NEW_PACKAGE_ID");
    	String serialNumber = data.getString("SERIAL_NUMBER");
    	IDataset user_set = UserInfoQry.getUserinfo(serialNumber);
		if (IDataUtil.isEmpty(user_set) && user_set.size()<=0)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户资料不存在");
		}
		String userId= user_set.getData(0).getString("USER_ID","");
        String custId = user_set.getData(0).getString("CUST_ID", "");
        String packageId = data.getString("NEW_PACKAGE_ID", "");
        
//        IDataset set = ProductPkgInfoQry.queryProductByPackageID(packageId);
        IDataset set = UpcCall.qryCatalogByOfferId(packageId, BofConst.ELEMENT_TYPE_CODE_PACKAGE);
        
        String productId=set.getData(0).getString("CATALOG_ID","");
        String campnType = set.getData(0).getString("UP_CATALOG_ID","");
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("CUST_ID", custId);
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("CAMPN_TYPE", campnType);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", ""));
        param.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", ""));
        //规则校验
        IDataset results = CSAppCall.call("CS.SaleActiveQuerySVC.choicePackageNode", param);
        //营销活动的规则校验
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("WIDE_USER_CREATE_SALE_ACTIVE", data.getString("WIDE_USER_CREATE_SALE_ACTIVE", "0"));
        param.put("WIDE_USER_SELECTED_SERVICEIDS", data.getString("WIDE_USER_SELECTED_SERVICEIDS", ""));
        param.put("CHANGE_UP_DOWN_TAG", data.getString("CHANGE_UP_DOWN_TAG", ""));
        param.put("BOOKING_DATE", data.getString("BOOKING_DATE", ""));
        param.put("SPECIAL_SALE_FLAG", data.getString("SPECIAL_SALE_FLAG", ""));
       // param.put("SERIAL_NUMBER", serialNumber);
        IDataset ruleResultDataSet = CSAppCall.call( "CS.SaleActiveCheckSVC.checkByPackage", param);
      //查询活动详情
//    	IDataset result3 = PkgInfoQry.getPackageByPackage(packid, "0898");
    	IData result3 = UpcCall.queryOfferByOfferId(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packid);
    	
    	return IDataUtil.idToIds(result3);
    }
    
    //校验界面上已选优惠是否是包年套餐的优惠，用于处理日期
    public IDataset checkPackageYear(IData data) throws Exception
    {
    	String disid = data.getString("NEW_DISCNT_ID");
    	IDataset result3 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "532", "601",disid);
    	if (IDataUtil.isNotEmpty(result3))
    	{
    		String v_end = data.getString("BOOKDATE","");
    		if (!StringUtils.isBlank(v_end))
    		{
    			String e_end = SysDateMgr.addMonths(v_end, 12);
    			e_end = SysDateMgr.addDays(e_end, -1);
            	result3.getData(0).put("BOOKDATE_END", e_end);
    		}
    	}
    	return result3;
    }
    /**
     * 根据产品选择重置生效时间
     * 1、升档，可以选择最近3个月的日期
     * 2、降档，如果有包年或营销活动，只能选择协议到期后的次月1日，无，只能选择次月1日
     * 3、速率不变更，产品变更了，如果有包年或营销活动，只能协议到期后的次月1日，如无包年或营销活动，则为次月1日
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset resetBookTime(IData data) throws Exception
    {
    	String changeUpDownFlag = data.getString("CHANGE_UP_DOWN_TAG","0"); //0：不变，1：升档，2：降档，3：速率不变，产品变
    	String isYearProduct = data.getString("IS_YEAR_PRODUCT","0");
    	String haveSaleActive = data.getString("HAVE_SALE_ACTIVE","0");
    	String bookTag = data.getString("BOOK_TAG","1"); 
    	String vEndDate = data.getString("V_END_DATE",""); 
    	
    	if(vEndDate == null || "".equals(vEndDate))
    	{
    		vEndDate = SysDateMgr.getSysTime();
    	}
    	else 
    	{
    		if(SysDateMgr.compareTo(SysDateMgr.getDateForYYYYMMDD(vEndDate), SysDateMgr.getDateForYYYYMMDD(SysDateMgr.getSysTime())) < 0)
    		{
    			vEndDate = SysDateMgr.getSysTime();
    		}
    	}
    	
    	IDataset bookTimeList = new DatasetList();
    	
    	if("0".equals(changeUpDownFlag))
    	{
    		//取次月1日
    		String firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
        	IData bookTime = new DataMap();    	
        	bookTime.put("DATA_ID", firstDayOfNextMonth);   	
        	bookTime.put("DATA_NAME", firstDayOfNextMonth); 
        	
        	bookTimeList.add(bookTime);
        	
        	String firstDayOfNextMonth2 = SysDateMgr.addMonths(firstDayOfNextMonth, 1);
        	
        	IData bookTime2 = new DataMap();    	
        	bookTime2.put("DATA_ID", firstDayOfNextMonth2);   	
        	bookTime2.put("DATA_NAME", firstDayOfNextMonth2); 
        	
        	bookTimeList.add(bookTime2);
        	
        	String firstDayOfNextMonth3 = SysDateMgr.addMonths(firstDayOfNextMonth, 2);
        	
        	IData bookTime3 = new DataMap();    	
        	bookTime3.put("DATA_ID", firstDayOfNextMonth3);   	
        	bookTime3.put("DATA_NAME", firstDayOfNextMonth3); 
        	
        	bookTimeList.add(bookTime3);
    	}
    	else if("1".equals(changeUpDownFlag))
    	{
    		String firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
        	IData bookTime = new DataMap();    	
        	bookTime.put("DATA_ID", firstDayOfNextMonth);   	
        	bookTime.put("DATA_NAME", firstDayOfNextMonth); 
        	
        	bookTimeList.add(bookTime);
        	
        	String firstDayOfNextMonth2 = SysDateMgr.addMonths(firstDayOfNextMonth, 1);
        	
        	IData bookTime2 = new DataMap();    	
        	bookTime2.put("DATA_ID", firstDayOfNextMonth2);   	
        	bookTime2.put("DATA_NAME", firstDayOfNextMonth2); 
        	
        	bookTimeList.add(bookTime2);
        	
        	String firstDayOfNextMonth3 = SysDateMgr.addMonths(firstDayOfNextMonth, 2);
        	
        	IData bookTime3 = new DataMap();    	
        	bookTime3.put("DATA_ID", firstDayOfNextMonth3);   	
        	bookTime3.put("DATA_NAME", firstDayOfNextMonth3); 
        	
        	bookTimeList.add(bookTime3);
    	}
    	else if("2".equals(changeUpDownFlag) || "3".equals(changeUpDownFlag))
    	{
    		if("1".equals(isYearProduct) || "1".equals(haveSaleActive))
    		{
    			//有包年套餐或营销活动
    			if("0".equals(bookTag))
    			{
    				//协议未到期，报错
    				String errorMsg = "您当前的" + ("1".equals(isYearProduct) ? "包年套餐" : "营销活动") + "还未到期,不允许进行产品变更!";
    				CSAppException.apperr(CrmCommException.CRM_COMM_103,errorMsg);
    			}
    		}
    		
    		String firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
        	IData bookTime = new DataMap();    	
        	bookTime.put("DATA_ID", firstDayOfNextMonth);   	
        	bookTime.put("DATA_NAME", firstDayOfNextMonth); 
        	
        	bookTimeList.add(bookTime);
        	
        	String firstDayOfNextMonth2 = SysDateMgr.addMonths(firstDayOfNextMonth, 1);
        	
        	IData bookTime2 = new DataMap();    	
        	bookTime2.put("DATA_ID", firstDayOfNextMonth2);   	
        	bookTime2.put("DATA_NAME", firstDayOfNextMonth2); 
        	
        	bookTimeList.add(bookTime2);
        	
        	String firstDayOfNextMonth3 = SysDateMgr.addMonths(firstDayOfNextMonth, 2);
        	
        	IData bookTime3 = new DataMap();    	
        	bookTime3.put("DATA_ID", firstDayOfNextMonth3);   	
        	bookTime3.put("DATA_NAME", firstDayOfNextMonth3); 
        	
        	bookTimeList.add(bookTime3);
    	}
        return bookTimeList;
    }
    
    /**
     * 校验所选优惠中是否有包年优惠
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkIsYearDiscnts(IData data) throws Exception
    {
    	boolean isHaveYearDiscnt = false ;
    	String elements = data.getString("SELECTED_ELEMENTS");
    	String addDiscnts = "";
    	if(elements != null && !"".equals(elements))
    	{
    		IDataset selectedElements = new DatasetList(elements); 
    		if(IDataUtil.isNotEmpty(selectedElements) && selectedElements.size() > 0)
    		{
    			for(int i = 0 ;i < selectedElements.size() ; i++)
    			{
    				IData element = selectedElements.getData(i);
    				String elementTypeCode = element.getString("ELEMENT_TYPE_CODE","");
    				String modifyTag = element.getString("MODIFY_TAG","");
    				String elementId = element.getString("ELEMENT_ID","");
    				if("0".equals(modifyTag) && "D".equals(elementTypeCode))
    				{
    					if("".equals(addDiscnts))
    					{
    						addDiscnts = elementId ;
    					}
    					else
    					{
    						addDiscnts += "," + elementId;
    					}
    				}
    			}
    		}
    	}
    	
    	if(!"".equals(addDiscnts))
    	{
    		IDataset commparaInfos532 = CommparaInfoQry.getCommpara("CSM", "532", "601", "0898");
    		if(commparaInfos532 != null && commparaInfos532.size() > 0)
    		{
    			for(int i = 0 ; i < commparaInfos532.size() ; i++)
    			{
    				String paraCode1 = commparaInfos532.getData(i).getString("PARA_CODE1","");
    				if(addDiscnts.indexOf(paraCode1) >= 0)
    				{
    					//是包年套餐
    					isHaveYearDiscnt = true ;
    					break;
    				}
    			}
    		}
    	}
    	
    	IData result = new DataMap();
    	result.put("IS_YEAR_DISCNT", isHaveYearDiscnt);
    	
    	return result;
    }

    /**
     * 判断宽带1+活动是否可平移
     * @param data
     * @return
     * @throws Exception
     * @author tanzheng
     */
    public IData checkIsChangeProduct(IData data) throws Exception
    {
        boolean isHaveYearDiscnt = false ;
        String oldSalePackageId = data.getString("OLD_SALE_PACKAGE_ID");
        String newSalePackageId = data.getString("NEW_SALE_PACKAGE_ID");
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "7980");
        param.put("PARAM_CODE", "ACTIVE_CHANGE");
        param.put("PARA_CODE1", oldSalePackageId);
        param.put("PARA_CODE2", newSalePackageId);
        IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
        if(IDataUtil.isNotEmpty(dataset)){
            isHaveYearDiscnt = true ;
        }
        IData result = new DataMap();
        result.put("IS_CHANGE_PRODUCT", isHaveYearDiscnt);

        return result;
    }
    /**
     * 提交查询出用户需要交纳的费用并进行校验
     * @param cycle
     * @throws Exception
     * @author yuyj3
     */
    public IData checkFeeBeforeSubmit(IData param) throws Exception
    {
        IData result = new DataMap();
        
        //新营销活动包ID
        String newSalePackageId = param.getString("NEW_SALE_ACTIVE_PACKAGE_ID","0");
        
        //新营销活动产品ID
        String newSaleProductId = param.getString("NEW_SALE_ACTIVE_PRODUCT_ID","0");
        
        //老营销活动包ID
        String oldSalePackageId = param.getString("OLD_SALE_ACTIVE_PACKAGE_ID");
        
        //老营销活动产品ID
        String oldSaleProductId = param.getString("OLD_SALE_ACTIVE_PRODUCT_ID");
        
        //服务号码
        String serialNumber = param.getString("SERIAL_NUMBER","");
        
        //预约时间
        String bookingDate = param.getString("BOOKING_DATE","");
        String elements = param.getString("SELECTED_ELEMENTS");
        
        //新营销活动费用
        int newActiveFee = 0;
        
        //用户需要支付的金额
        int payFee = 0;
        
        //CRM计算出的包年套餐到预约生效时剩余的包年余额
        int yearDiscntRemainFee = 0;
        int returnyearDiscntRemainFee = 0;
        //CRM计算出的营销活动到预约生效时剩余的包年余额
        int remainFee = 0;
        
        //根据账务查询的余额计算出预约生效时剩余的余额
        int acctRemainFee = 0;
        
        WidenetMoveBean wb = new WidenetMoveBean();
        IData input = new DataMap();
        input.put("PRODUCT_ID", newSaleProductId);
        input.put("PACKAGE_ID", newSalePackageId);
        
        //获得营销活动费用信息
        IData feeInfo = wb.queryCheckSaleActiveFee(input);
        
        if(IDataUtil.isNotEmpty(feeInfo))
        {
            if (StringUtils.isNotBlank(feeInfo.getString("SALE_ACTIVE_FEE", "0")))
            {
                newActiveFee = Integer.valueOf(feeInfo.getString("SALE_ACTIVE_FEE", "0"));
            }
        }
        
        //新的营销活动需要交纳费用的时候才需要进行费用计算以及校验
        if (newActiveFee > 0)
        {
            //包年优惠费用
            String yearDiscntFee = "0" ;
            String yearDiscntStartDate = "";
            
            if(StringUtils.isNotBlank(elements))
            {
                IDataset selectedElements = new DatasetList(elements); 
                if(IDataUtil.isNotEmpty(selectedElements))
                {
                    //包年优惠配置参数
                    IDataset commparaInfos532 = CommparaInfoQry.getCommpara("CSM", "532", "601", "0898");
                    
                    if(IDataUtil.isNotEmpty(commparaInfos532))
                    {
                        for(int i = 0 ;i < selectedElements.size() ; i++)
                        {
                            IData element = selectedElements.getData(i);
                            String elementTypeCode = element.getString("ELEMENT_TYPE_CODE","");
                            String modifyTag = element.getString("MODIFY_TAG","");
                            
                            //如果是删除的优惠
                            if("1".equals(modifyTag) && "D".equals(elementTypeCode))
                            {
                                String delDiscntId =  element.getString("ELEMENT_ID","");
                                IData commpara532 = null;
                                
                                for(int j = 0 ; j < commparaInfos532.size() ; j++)
                                {
                                    commpara532 = commparaInfos532.getData(j);
                                    
                                    if(StringUtils.equals(delDiscntId, commpara532.getString("PARA_CODE1","")))
                                    {
                                        //是包年套餐
                                        yearDiscntFee = commpara532.getString("PARA_CODE3","0");
                                        yearDiscntStartDate = element.getString("START_DATE");
                                        break;
                                    }
                                }
                           }
                        }
                    }
                }
            }
            
            //如果用户原来订购的是包年套餐
            if (StringUtils.isNotBlank(yearDiscntFee) && StringUtils.isNotBlank(yearDiscntStartDate))
            {
                //包年套餐到预约变更时已使用的月数
                int useMonths = SysDateMgr.monthIntervalYYYYMM(chgFormat(yearDiscntStartDate,"yyyy-MM-dd","yyyyMM"), chgFormat(bookingDate,"yyyy-MM-dd","yyyyMM"));
                
                
                if (useMonths < 0)
                {
                    useMonths = 0;
                }
                else if (useMonths > 12)
                {
                    useMonths = 12;
                }
                
                int yearDiscntFeeInt = Integer.valueOf(yearDiscntFee);
                
                //CRM计算出的包年套餐到预约生效时剩余的包年余额
                yearDiscntRemainFee = yearDiscntFeeInt - (yearDiscntFeeInt/12)*useMonths;
                if(yearDiscntRemainFee<0){
                    yearDiscntRemainFee = 0;
                }

                //实际要支付的钱以账务那边查出的费用为准
                payFee = newActiveFee - yearDiscntRemainFee;
            }
            //如果不是判断用户当前订购的是否是包年类营销活动
            else
            {
                if (StringUtils.isNotBlank(oldSaleProductId) && StringUtils.isNotBlank(oldSalePackageId))
                {
                    //包年费用
                    int oldActiveYearFee = 0;
                    
                    input.put("PRODUCT_ID", oldSaleProductId);
                    input.put("PACKAGE_ID", oldSalePackageId);
                    
                    IData oldSalefeeInfo = wb.queryCheckSaleActiveFee(input);
                    
                    if(IDataUtil.isNotEmpty(oldSalefeeInfo))
                    {
                        if (StringUtils.isNotBlank(feeInfo.getString("SALE_ACTIVE_FEE", "0")))
                        {
                            oldActiveYearFee = Integer.valueOf(oldSalefeeInfo.getString("SALE_ACTIVE_FEE", "0"));
                        }
                    }

                    //如果老的营销活动是存在费用的
                    if (oldActiveYearFee > 0)
                    {
                        IData userInfoData = UcaInfoQry.qryUserInfoBySn(serialNumber);
                        
                        //根据包年优惠，计算当前包年活动优惠使用时间
                        int useMonths = 0;
                        //到下个月初实际使用月份
                        int currentUseMonths = 0;
                        
                        //默认生效时间为次月1日
                    	String firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
                        
                        IDataset useDiscnts = UserDiscntInfoQry.getAllDiscntInfo(userInfoData.getString("USER_ID"));
                        if (IDataUtil.isNotEmpty(useDiscnts))
                        {
                            for(int i=0;i<useDiscnts.size();i++)
                            {
                                if(oldSaleProductId.equals(useDiscnts.getData(i).getString("PRODUCT_ID", ""))
                                        &&oldSalePackageId.equals(useDiscnts.getData(i).getString("PACKAGE_ID", "")))
                                {
                                    String startDate = useDiscnts.getData(i).getString("START_DATE", "");
                                    useMonths = SysDateMgr.monthIntervalYYYYMM(WidenetMoveBean.chgFormat(startDate,"yyyy-MM-dd HH:mm:ss","yyyyMM"),WidenetMoveBean.chgFormat(bookingDate,"yyyy-MM-dd","yyyyMM"));
                                    
                                    currentUseMonths = SysDateMgr.monthIntervalYYYYMM(WidenetMoveBean.chgFormat(startDate,"yyyy-MM-dd HH:mm:ss","yyyyMM"),WidenetMoveBean.chgFormat(firstDayOfNextMonth,"yyyy-MM-dd","yyyyMM"));
                                    
                                    break;
                                }
                            }
                        }
                        
                        //获取营销活动的周期
                        IDataset com181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", oldSalePackageId, null);
                        IDataset newcom181 = CommparaInfoQry.getCommparaByCode1("CSM", "181", "-1", newSalePackageId, null);

                        //妈的脑袋有坑，12月不能整除68180510,68180511,68180512,68180513,68180514
                        //前十一个月32元，最后一个月36元
                        boolean oldmd = false;
                        boolean newmd = false;
                        int oldmd1 = 0;
                        int oldmd2 = 0;
                        int newmd1 = 0;
                        int newmd2 = 0;
                        int months=12;//默认12个月
                        if (IDataUtil.isNotEmpty(com181))
                        {
                            months=com181.getData(0).getInt("PARA_CODE4",12);
                            //老的包年套餐是388、588
                            if("1".equals(com181.getData(0).getString("PARA_CODE7","")))
                            {
                            	oldmd = true;
                            }
                        }
                        
                        if(IDataUtil.isNotEmpty(newcom181))
                        {
                        	//新的包年套餐是388、588
                        	if("1".equals(newcom181.getData(0).getString("PARA_CODE7","")))
                            {
                            	newmd = true;
                            }
                        }
                        
                        if (useMonths < 0)
                        {
                            useMonths = 0;
                        }
                        else if(useMonths > months)
                        {
                            useMonths = months;
                        }

                        remainFee = oldActiveYearFee - (oldActiveYearFee/months)*useMonths;
                        if(oldmd)
                        {
                        	if(useMonths<=11)
                        	{
                        		remainFee = oldActiveYearFee - Integer.parseInt(com181.getData(0).getString("PARA_CODE5","0"))*useMonths;
                        	}else
                        	{
                        		remainFee = oldActiveYearFee;
                        	}
                        	
                        }
                        
                        //如果是包年营销活动终止，此处需要对包年活动剩余的费用（宽带包年活动专项款存折（赠送）9023和 宽带包年活动专项款存折9021）赠送给用户
                        //到（宽带包年活动变更预存回退存折（赠送） 宽带包年活动变更预存回退存折）
                        //查询用户该存折（9021,9023）是否还有剩余的费用
                        //计算专项存折的费用
                        int balance9021 = 0,balance9023 = 0;
                        
                        IDataset allUserMoney = AcctCall.queryAccountDepositBySn(serialNumber);
                       
                        if(IDataUtil.isNotEmpty(allUserMoney))
                        {
                            for(int i=0;i<allUserMoney.size();i++)
                            {
                                String balance1 = allUserMoney.getData(i).getString("DEPOSIT_BALANCE","0");
                                int balance2 = Integer.parseInt(balance1);
                                
                                if("9021".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE")))
                                {
                                    balance9021 = balance9021 + balance2;
                                }
                                
                                if("9023".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE")))
                                {
                                    balance9023 = balance9023 + balance2;
                                }
                            }
                        }
                        
                        
                        //包年套餐到预约变更的月数
                        int bookingMonths = SysDateMgr.monthIntervalYYYYMM(chgFormat(SysDateMgr.getSysDate(),"yyyy-MM-dd","yyyyMM"), chgFormat(bookingDate,"yyyy-MM-dd","yyyyMM"))-1;
                        
                        //营销活动剩余周期
                        int remainMonths = months - currentUseMonths; 
                        
                        //如果预约的月数大于营销活动剩余的周期月数，则以剩余的周期月数为准
                        if (remainMonths < bookingMonths)
                        {
                            bookingMonths = remainMonths;
                        }
                        
                        //根据账务查询出的包年存折的余额计算出到预约生效时剩余的包年余额
                        acctRemainFee = (balance9021+balance9023) - (oldActiveYearFee/months)*(bookingMonths+1); //加账务预销户一个月的钱
                        if(remainMonths == months)
                        {
                        	acctRemainFee = balance9021+balance9023;
                        }
                        
                        if(acctRemainFee<0){
                            acctRemainFee=0;
                        }
                        
                        if(oldmd)
                        {
                        	//add by zhangxing3 for 宽带包年（388、588）本月到期用户，通过宽带产品变更办理度假宽带2019活动，计算费用有误。
                        	//if(remainMonths==1)
                        	if(remainMonths<=1)
                        	//add by zhangxing3 for 宽带包年（388、588）本月到期用户，通过宽带产品变更办理度假宽带2019活动，计算费用有误。
                        	{
                        		acctRemainFee = 0; //加账务预销户一个月的钱
                        	}else
                        	{
                        		acctRemainFee = (balance9021+balance9023) - Integer.parseInt(com181.getData(0).getString("PARA_CODE5","0"))*(bookingMonths+1); //加账务预销户一个月的钱
                        	}
                        	
                        }

                        payFee = newActiveFee - acctRemainFee;
                        
                        if(newmd && payFee < 0)
                        {
                        	returnyearDiscntRemainFee = acctRemainFee - newActiveFee;
                        }
                    }
                    else
                    {
                        payFee = newActiveFee;
                    }
                    
                }
                else
                {
                    payFee = newActiveFee;
                }
                
            }
            
            //如果需要支付的金额为负数，则设置为0
            if (payFee < 0)
            {
                payFee = 0;
            }
            
            String leftFee = WideNetUtil.qryBalanceDepositBySn(serialNumber);
            
            //用余额校验
            if(Integer.parseInt(leftFee)< payFee )
            {
                CSAppException.appError("62816", "您的账户存折可用余额不足，请先办理缴费。本次业务需从现金类存折转出费用：[" + Float.valueOf(payFee)/100 + "元]");
            }
        }
        
        //BUS201907310012关于开发家庭终端调测费的需求
/*        String newSalePackageId2 = param.getString("NEW_SALE_ACTIVE_PACKAGE_ID2","0");        
        String newSaleProductId2 = param.getString("NEW_SALE_ACTIVE_PRODUCT_ID2","0");
        int newActiveFee2 = 0;
        IData input2 = new DataMap();
        input2.put("PRODUCT_ID", newSaleProductId2);
        input2.put("PACKAGE_ID", newSalePackageId2);
        
        //获得营销活动费用信息
        IData feeInfo2 = wb.queryCheckSaleActiveFee(input2);
        
        if(IDataUtil.isNotEmpty(feeInfo2))
        {
            if (StringUtils.isNotBlank(feeInfo2.getString("SALE_ACTIVE_FEE", "0")))
            {
                newActiveFee2 = Integer.valueOf(feeInfo2.getString("SALE_ACTIVE_FEE", "0"));
            }
        }       
        if (newActiveFee2 > 0)
        {
            //用余额校验
            String leftFee = WideNetUtil.qryBalanceDepositBySn(serialNumber);

            if(Integer.parseInt(leftFee)< payFee+newActiveFee2 )
            {
                CSAppException.appError("62816", "您的账户存折可用余额不足，请先办理缴费。本次业务需从现金类存折转出费用：[" + Float.valueOf(payFee)/100 + "元]");
            }
        }
        result.put("WIDE_ACTIVE_PAY_FEE2", newActiveFee2);*/
        //BUS201907310012关于开发家庭终端调测费的需求

        
        result.put("WIDE_ACTIVE_PAY_FEE", payFee);
        result.put("YEAR_DISCNT_REMAIN_FEE", yearDiscntRemainFee);
        result.put("RETURN_YEAR_DISCNT_REMAIN_FEE", returnyearDiscntRemainFee);
        result.put("REMAIN_FEE", remainFee);
        result.put("ACCT_REMAIN_FEE", acctRemainFee);
        result.put("RESULT_CODE", "0");
        
        return result;
    }
    
    public static String chgFormat(String strDate, String oldForm, String newForm) throws Exception{
        if (null == strDate)
        {
            throw new NullPointerException();
        }

        DateFormat oldDf = new SimpleDateFormat(oldForm);
        Date date = oldDf.parse(strDate);

        String newStr = "";
        DateFormat newDf = new SimpleDateFormat(newForm);
        newStr = newDf.format(date);        
        return newStr;
    }

    /**
	 * 校验传入在是否为空
	 * 
	 * @param data
	 * @param keys
	 * @throws Exception
	 */
	public void chkParamNoStr(IData data, String keys, String errorCode) throws Exception 
	{
		String key = data.getString(keys, "");
		if ("".equals(key)) 
		{
			CSAppException.appError(errorCode, "传入在字段" + keys + "值不能为空！");
		}
	}
    
	/**
	 * 宽带产品变更产品查询接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
    public IData getWidenetProductInfo(IData input) throws Exception
    {
    	chkParamNoStr(input, "SERIAL_NUMBER", "201703001"); // 手机号码
    	
    	IDataset idsOutput = new DatasetList();
    	String strSerialNumber = input.getString("SERIAL_NUMBER");
    	/*IData idUser = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
		if(IDataUtil.isEmpty(idUser))
		{
			IData idError = new DataMap();
			idError.put("X_RESULTCODE", "-1");
			idError.put("X_RESULTINFO", "用户信息不存在，请检查");
			idsOutput.add(idError);
			return idsOutput;
		}*/
		//String strUserID = idUser.getString("USER_ID", "");
		/*idUser.put("SERIAL_NUMBER_A", strSerialNumber);
		idUser.put("TRADE_TYPE_CODE", "601");
		idUser.put(Route.ROUTE_EPARCHY_CODE, "0898");
		IDataset result3 = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", idUser);
		if (IDataUtil.isNotEmpty(result3))
        {
			IData activeData = result3.getData(0);
			String strUserYearActive = activeData.getString("USER_YEAR_ACTIVE", "");
			if(!"1".equals(strUserYearActive))
			{
				IData idError = new DataMap();
				idError.put("X_RESULTCODE", "-1");
				idError.put("X_RESULTINFO", "您不是包年营销活动用户，不能办理业务");
				idsOutput.add(idError);
				return idsOutput;
			}
			
			String strVBookTag = activeData.getString("V_BOOK_TAG", "");
			if(!"1".equals(strVBookTag))
			{
				IData idError = new DataMap();
				idError.put("X_RESULTCODE", "-1");
				idError.put("X_RESULTINFO", "您的包年营销活动不是3个月后到期，不能办理业务");
				idsOutput.add(idError);
				return idsOutput;
			}
        }
		else
		{
			IData idError = new DataMap();
			idError.put("X_RESULTCODE", "-1");
			idError.put("X_RESULTINFO", "您不是包年营销活动用户，不能办理业务");
			idsOutput.add(idError);
			return idsOutput;
		}*/
		
		String strKdSerialNumber = "KD_" + strSerialNumber;
		IData idKdUser = UcaInfoQry.qryUserInfoBySn(strKdSerialNumber);
		if(IDataUtil.isEmpty(idKdUser))
		{
			IData idError = new DataMap();
			idError.put("X_RESULTCODE", "-1");
			idError.put("X_RESULTINFO", "用户宽带信息不存在，请检查");
			//idsOutput.add(idError);
			return idError;
		}
		
		String strKdUserID = idKdUser.getString("USER_ID", "");
    	IData data = new DataMap();
    	//初始化宽带类型
    	data.put("USER_ID", strKdUserID);
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	IDataset dataset = CSAppCall.call("CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        String  widetype = dataset.getData(0).getString("RSRV_STR2");
        
        //IData pdata1 = new DataMap();
        //pdata1.put("WIDE_TYPE_CODE", widetype);
        
        String productmode="";//--07：移动GPON宽带，09：ADSL宽带产品，11：移动FTTH宽带，16：海南铁通FTTH，17：海南铁通FTTB，13：校园宽带
    	if ("1".equals(widetype))
        {
    		//pdata1.put("WIDE_TYPE_NAME", "移动FTTB");
    		productmode="07";
        }else if ("2".equals(widetype))
        {
        	//pdata1.put("WIDE_TYPE_NAME", "铁通ADSL");
        	productmode="09";
        }else if ("3".equals(widetype))
        {
        	//pdata1.put("WIDE_TYPE_NAME", "移动FTTH");
        	productmode="11";
        }else if ("5".equals(widetype))
        {
        	//pdata1.put("WIDE_TYPE_NAME", "铁通FTTH");
        	//productmode="16";
        	productmode="11"; //移动FTTH与铁通FTTH合并，使用同一套产品
        }else if ("6".equals(widetype))
        {
        	//pdata1.put("WIDE_TYPE_NAME", "铁通FTTB");
        	//productmode="17";
        	productmode="07"; //移动FTTB与铁通FTTB合并，使用同一套产品
        }
    	IData idResult = new DataMap();
    	//初始化产品包列表
    	data.put("PROD_MODE", productmode);
        IDataset idsResult = CSAppCall.call("SS.WidenetChangeProductNewSVC.loadProductInfo", data);
        if (IDataUtil.isNotEmpty(idsResult))
        {
            //this.setAjax(result);
            //pdata1.put("USER_PRODUCT_NAME", result.getData(0).getString("USER_PRODUCT_NAME"));
            //pdata1.put("USER_PRODUCT_ID", result.getData(0).getString("USER_PRODUCT_ID"));
            //result.first().putAll(pdata1);
        	idResult = idsResult.first();
            IDataset idsWidenetProductInfo = idResult.getDataset("PRODUCT_LIST");
            if(IDataUtil.isNotEmpty(idsWidenetProductInfo))
            {
            	for (int i = 0; i < idsWidenetProductInfo.size(); i++) 
            	{
            		IData idWidenetProductInfo = idsWidenetProductInfo.getData(i);
            		String strNewProductID = idWidenetProductInfo.getString("PRODUCT_ID", "");
            		IDataset idsCompare177 = CommparaInfoQry.getCommparaInfoBy7("CSM", "177", "601", strNewProductID, "WIDE_YEAR_ACTIVE", "0898", null);
            		if(IDataUtil.isNotEmpty(idsCompare177))
            		{
            			idsOutput.add(idWidenetProductInfo);
            		}
            	}
            }
            idResult.put("WIDENET_PRODUCT_LIST", idsOutput);
        }
        idResult.put("X_RESULTCODE", "0");
        idResult.put("X_RESULTINFO", "OK");
        return idResult;
    }
    
    /**
	 * 宽带包年续约校验接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
    public IData checkWidenetProductInfo(IData input) throws Exception
    {
    	chkParamNoStr(input, "SERIAL_NUMBER", "201703001"); // 手机号码
    	
    	//IData idResult = new DataMap();
    	String strSerialNumber = input.getString("SERIAL_NUMBER");
    	
    	IData idUser = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
		if(IDataUtil.isEmpty(idUser))
		{
			IData idError = new DataMap();
			idError.put("X_RESULTCODE", "201703002");
			idError.put("X_RESULTINFO", "用户信息不存在，请检查");
			return idError;
		}
		
		String strKdSerialNumber = "KD_" + strSerialNumber;
		IData idKdUser = UcaInfoQry.qryUserInfoBySn(strKdSerialNumber);
		if(IDataUtil.isEmpty(idKdUser))
		{
			IData idError = new DataMap();
			idError.put("X_RESULTCODE", "201703105");
			idError.put("X_RESULTINFO", "用户宽带信息不存在，请检查");
			return idError;
		}
		
		String strKdUserID = idKdUser.getString("USER_ID", "");
		idUser.put("SERIAL_NUMBER", strSerialNumber);
		idUser.put("TRADE_TYPE_CODE", "601");
		idUser.put(Route.ROUTE_EPARCHY_CODE, "0898");
		idUser.put("USER_ID", strKdUserID);
		
		IData idError = new DataMap();
		idError.put("X_RESULTCODE", "201703011");
		idError.put("X_RESULTINFO", "您不是包年用户，不能办理业务");
		//String firstDayOfNextMonth = "";
		boolean bIsBookTag = false;
		//boolean bIsActive = false;
		IDataset result2 = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetProduct", idUser);
        if (IDataUtil.isNotEmpty(result2))
        {
        	//String strVend = result2.getData(0).getString("END_DATE");//包年的直接取结束日期
        	String strBooktag = result2.getData(0).getString("V_BOOK_TAG","");
        	
        	if("1".equals(strBooktag))
			{
        		bIsBookTag = true;
			}
        	else 
        	{
        		idError.put("X_RESULTCODE", "201703104");
				idError.put("X_RESULTINFO", "您的包年套餐不是3个月后到期，不能办理业务");
			}
        	//firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(strVend);
			//2、只调用营销活动接口
			/*IData data = new DataMap();
			data.put("END_BOOKDATE", firstDayOfNextMonth);
			data.put("BOOKING_DATE", firstDayOfNextMonth);
			data.put("BOOKDATE", firstDayOfNextMonth);
	    	data.put("TRADE_TYPE_CODE", "240");
	    	data.put("PRODUCT_ID", input.getString("NEW_SALE_PRODUCT_ID"));
	    	data.put("PACKAGE_ID", input.getString("NEW_SALE_PACKAGE_ID"));
	    	data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
	    	IData idResult = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", data).first();
	    	return idResult;*/
        }
        
        idUser.put("SERIAL_NUMBER_A", strSerialNumber);
		idUser.put("TRADE_TYPE_CODE", "601");
		idUser.put(Route.ROUTE_EPARCHY_CODE, "0898");
		//idUser.put("SERIAL_NUMBER", strSerialNumber);
		IDataset idsWidenetActive = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", idUser);
		if (IDataUtil.isNotEmpty(idsWidenetActive))
        {
			IData idWidenetActive = idsWidenetActive.first();
			String strUserYearActive = idWidenetActive.getString("USER_YEAR_ACTIVE", "");
			if(!"1".equals(strUserYearActive))
			{
				idError.put("X_RESULTCODE", "201703003");
				idError.put("X_RESULTINFO", "您不是包年营销活动用户，不能办理业务");
			}
			
			String strVBookTag = idWidenetActive.getString("V_BOOK_TAG", "");
			if(!"1".equals(strVBookTag))
			{
				idError.put("X_RESULTCODE", "201703004");
				idError.put("X_RESULTINFO", "您的包年营销活动不是3个月后到期，不能办理业务");
			}
			
			if("1".equals(strUserYearActive) && "1".equals(strVBookTag))
			{
				bIsBookTag = true;
			}
        }
		
		if(bIsBookTag)
		{
			IData idInput = new DataMap();
			idInput.put("SERIAL_NUMBER", strSerialNumber);
			idInput.put("USER_ID", strKdUserID);
			
			IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(strKdUserID);
	        if (IDataUtil.isEmpty(userMainProducts))
	        {
				idError.put("X_RESULTCODE", "201703007");
				idError.put("X_RESULTINFO", "用户宽带主产品信息不存在，请检查");
				return idError;
	        }
	        
	        if(userMainProducts.size() > 1)
	        {
	        	idError.put("X_RESULTCODE", "201703009");
				idError.put("X_RESULTINFO", "您已经有预约生效的主产品，不能再办理宽带预约");
				return idError;
	        }
	        IData userProduct = userMainProducts.first();
	        String strUserProductId = userProduct.getString("PRODUCT_ID");
	    	
	        IDataset idsSaleActive = CommparaInfoQry.getCommparaInfoBy7("CSM", "178", "601", strUserProductId, "WIDE_YEAR_ACTIVE", "0898", null);
			if(IDataUtil.isEmpty(idsSaleActive))
			{
				idError.put("X_RESULTCODE", "-1");
				idError.put("X_RESULTINFO", "不能续约");
			}
			IData idWidenetSaleActive = idsSaleActive.first();
	    	String strNewSaleActiveProductID = idWidenetSaleActive.getString("PARA_CODE4", "");
	    	String strNewSaleActivePackageID = idWidenetSaleActive.getString("PARA_CODE5", "");
	        
	    	IData idElementsParam = new DataMap();
	    	idElementsParam.put("TRADE_TYPE_CODE", "601");
	    	idElementsParam.put("BOOKING_DATE", "");
	    	idElementsParam.put("NEW_PRODUCT_ID", "");
	    	idElementsParam.put("USER_PRODUCT_ID", strUserProductId);
	    	idElementsParam.put("USER_ID", strKdUserID);
	    	idElementsParam.put("EPARCHY_CODE", "0898");
	    	idElementsParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
	    	IDataset dataset = CSAppCall.call("CS.SelectedElementSVC.getUserElements", idElementsParam);
	    	if (IDataUtil.isEmpty(dataset))
	        {
				//IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703008");
				idError.put("X_RESULTINFO", "您选择宽带产品有误，不能办理业务");
				return idError;
	        }
	    	
	    	String strServices = "";
	    	String elements = dataset.first().getString("SELECTED_ELEMENTS");
	    	if(StringUtils.isNotBlank(elements))	  
	    	{  
	    		IDataset idsSelectedElement = new DatasetList(elements);
	    		for (int i = 0; i < idsSelectedElement.size(); i++)
	    		{  
	    			IData idSelectedElement = idsSelectedElement.getData(i);
	    			String strModifyTag = idSelectedElement.getString("MODIFY_TAG", "");
	    			String strEYC = idSelectedElement.getString("ELEMENT_TYPE_CODE", "");
	    			String strEID = idSelectedElement.getString("ELEMENT_ID", "");
	    			
	    			if(!"1".equals(strModifyTag) && "S".equals(strEYC))
	    			{
	    				if ("".equals(strServices))
						{
	    					strServices = strEID;
						}else
						{
							strServices = strServices + "|" + strEID;
						}
	    			}
	    		}
	    	}
	    	
	        /*idInput.put("NEW_PRODUCT_ID", strUserProductId);
	        IDataset idsWidenetSaleActive = getWidenetSaleActiveInfo(idInput);
	    	if(IDataUtil.isEmpty(idsWidenetSaleActive))
			{
				idError.put("X_RESULTCODE", "201703005");
				idError.put("X_RESULTINFO", "仅支持20M及20M以上的宽带用户续约。");
				return idError;
			}
	    	IData idWidenetSaleActive = idsWidenetSaleActive.first();
	    	String strNewSaleActiveProductID = idWidenetSaleActive.getString("NEW_SALE_ACTIVE_PRODUCT_ID", "");
	    	String strNewSaleActivePackageID = idWidenetSaleActive.getString("NEW_SALE_ACTIVE_PACKAGE_ID", "");*/
	    	
	    	IData idParam = new DataMap();
	    	idParam.put("SERIAL_NUMBER", strSerialNumber);
	    	idParam.put("AUTH_SERIAL_NUMBER", strSerialNumber);
	    	idParam.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
	    	idParam.put("WIDE_USER_SELECTED_SERVICEIDS", strServices);
	    	//idParam.put("CHANGE_TYPE", "2");
	    	idParam.put("NEW_SALE_PRODUCT_ID", strNewSaleActiveProductID); 
	    	idParam.put("NEW_SALE_PACKAGE_ID", strNewSaleActivePackageID); 
	    	idParam.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
        	IData idResult = setWidenetSaleActiveInfo(idParam);
        	return idResult;
		}
    	return idError;
    }
    
    /**
	 * 宽带产品变更办理接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
    public IData setWidenetProductInfo(IData input) throws Exception
    {
    	chkParamNoStr(input, "SERIAL_NUMBER", "201703001"); // 手机号码
    	chkParamNoStr(input, "NEW_PRODUCT_ID", "201703001"); // 手机号码
    	//chkParamNoStr(input, "NEW_SALE_ACTIVE_PRODUCT_ID", "2001"); // 手机号码
    	
    	IData idParam = new DataMap();
    	//String strNewSaleActiveProuductID = input.getString("NEW_SALE_ACTIVE_PRODUCT_ID");
    	String strNewProductID = input.getString("NEW_PRODUCT_ID");
    	String strSerialNumber = input.getString("SERIAL_NUMBER");
    	
    	idParam.put("SERIAL_NUMBER", strSerialNumber);
    	idParam.put("NEW_PRODUCT_ID", strNewProductID);

    	idParam.put("TRADE_TYPE_CODE", "601");
		idParam.put("EPARCHY_CODE", "0898");
		idParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
    	
    	IData idUser = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
		if(IDataUtil.isEmpty(idUser))
		{
			IData idError = new DataMap();
			idError.put("X_RESULTCODE", "201703002");
			idError.put("X_RESULTINFO", "用户信息不存在，请检查");
			return idError;
		}
		
		String strKdSerialNumber = "KD_" + strSerialNumber;
		IData idKdUser = UcaInfoQry.qryUserInfoBySn(strKdSerialNumber);
		if(IDataUtil.isEmpty(idKdUser))
		{
			IData idError = new DataMap();
			idError.put("X_RESULTCODE", "201703105");
			idError.put("X_RESULTINFO", "用户宽带信息不存在，请检查");
			return idError;
		}
		
		String strKdUserID = idKdUser.getString("USER_ID", "");
		idUser.put("SERIAL_NUMBER", strSerialNumber);
		idUser.put("TRADE_TYPE_CODE", "601");
		idUser.put(Route.ROUTE_EPARCHY_CODE, "0898");
		idUser.put("USER_ID", strKdUserID);
		
		IData idError = new DataMap();
		idError.put("X_RESULTCODE", "201703011");
		idError.put("X_RESULTINFO", "您不是包年用户，不能办理业务");
		String firstDayOfNextMonth = "";
		boolean bIsBookTag = false;
		//boolean bIsActive = false;
		IDataset result2 = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetProduct", idUser);
        if (IDataUtil.isNotEmpty(result2))
        {
        	String strVend = result2.getData(0).getString("END_DATE");//包年的直接取结束日期
        	String strBooktag = result2.getData(0).getString("V_BOOK_TAG","");
        	
        	if("1".equals(strBooktag))
			{
        		bIsBookTag = true;
				/*IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703104");
				idError.put("X_RESULTINFO", "您的包年活动套餐不是3个月后到期，不能办理业务");
				return idError;*/
			}
        	else 
        	{
        		idError.put("X_RESULTCODE", "201703104");
				idError.put("X_RESULTINFO", "您的包年活动套餐不是3个月后到期，不能办理业务");
			}
        	
        	firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(strVend);
        	
			//2、只调用营销活动接口
			/*IData data = new DataMap();
			
			data.put("END_BOOKDATE", firstDayOfNextMonth);
			data.put("BOOKING_DATE", firstDayOfNextMonth);
			data.put("BOOKDATE", firstDayOfNextMonth);
	    	data.put("TRADE_TYPE_CODE", "240");
	    	data.put("PRODUCT_ID", input.getString("NEW_SALE_PRODUCT_ID"));
	    	data.put("PACKAGE_ID", input.getString("NEW_SALE_PACKAGE_ID"));
	    	data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
	    	IData idResult = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", data).first();
	    	return idResult;*/
        }
        
		
		idUser.put("SERIAL_NUMBER_A", strSerialNumber);
		idUser.put("TRADE_TYPE_CODE", "601");
		idUser.put(Route.ROUTE_EPARCHY_CODE, "0898");
		idUser.put("SERIAL_NUMBER", strSerialNumber);
		IDataset idsWidenetActive = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", idUser);
		if (IDataUtil.isNotEmpty(idsWidenetActive))
        {
			IData idWidenetActive = idsWidenetActive.first();
			String strUserYearActive = idWidenetActive.getString("USER_YEAR_ACTIVE", "");
			if(!"1".equals(strUserYearActive))
			{
				//IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703003");
				idError.put("X_RESULTINFO", "您不是包年营销活动用户，不能办理业务");
				//return idError;
			}
			
			String strVBookTag = idWidenetActive.getString("V_BOOK_TAG", "");
			if(!"1".equals(strVBookTag))
			{
				//IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703004");
				idError.put("X_RESULTINFO", "您的包年营销活动不是3个月后到期，不能办理业务");
				//return idError;
			}
			
			if("1".equals(strUserYearActive) && "1".equals(strVBookTag))
			{
				//bIsActive = true;
				bIsBookTag = true;
				
				String strVEndDate = idWidenetActive.getString("V_END_DATE", "");
				firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(strVEndDate);
				String strOldSaleActiveProuductID = idWidenetActive.getString("PRODUCT_ID", "");
				String strOldSaleActivePackageID = idWidenetActive.getString("PACKAGE_ID", "");
				
				//idParam.put("TRADE_TYPE_CODE", "601");
				//idParam.put("EPARCHY_CODE", "0898");
				//idParam.put("BOOKING_DATE", firstDayOfNextMonth);
				//idParam.put("NEW_PRODUCT_ID", strNewProductID);
				//idParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
		    	idParam.put("OLD_SALE_ACTIVE_PRODUCT_ID", strOldSaleActiveProuductID);
		    	idParam.put("OLD_SALE_ACTIVE_PACKAGE_ID", strOldSaleActivePackageID);
		    	idParam.put("V_USER_PACKAGE_ID", strOldSaleActivePackageID);
		        idParam.put("V_USER_PRODUCT_ID", strOldSaleActiveProuductID);
		        
		        /*IDataset idsWidenetSaleActive = getWidenetSaleActiveInfo(idParam);
		    	if(IDataUtil.isEmpty(idsWidenetSaleActive))
				{
		    		//IData idError = new DataMap();
					idError.put("X_RESULTCODE", "201703005");
					idError.put("X_RESULTINFO", "您办理的包年营销活动有误，不能办理业务");
					return idError;
				}
		    	IData idWidenetSaleActive = idsWidenetSaleActive.first();
		    	String strNewSaleActiveProductID = idWidenetSaleActive.getString("NEW_SALE_ACTIVE_PRODUCT_ID", "");
		    	String strNewSaleActivePackageID = idWidenetSaleActive.getString("NEW_SALE_ACTIVE_PACKAGE_ID", "");
		    	idParam.put("NEW_SALE_ACTIVE_PRODUCT_ID", strNewSaleActiveProductID);
		    	idParam.put("NEW_SALE_ACTIVE_PACKAGE_ID", strNewSaleActivePackageID);
		    	idParam.put("V_NEW_PACKAGE_ID", strNewSaleActivePackageID);*/
			}
        }
		
		if(bIsBookTag)
		{
			/*String strKdSerialNumber = "KD_" + strSerialNumber;
			IData idKdUser = UcaInfoQry.qryUserInfoBySn(strKdSerialNumber);
			if(IDataUtil.isEmpty(idKdUser))
			{
				IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703006");
				idError.put("X_RESULTINFO", "用户宽带信息不存在，请检查");
				return idError;
			}
			String strKdUserID = idKdUser.getString("USER_ID", "");*/
			idParam.put("USER_ID", strKdUserID);
			
			IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(strKdUserID);
	        if (IDataUtil.isEmpty(userMainProducts))
	        {
	        	//IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703007");
				idError.put("X_RESULTINFO", "用户宽带主产品信息不存在，请检查");
				return idError;
	        }
	        IData userProduct = userMainProducts.first();
	        String strUserProductId = userProduct.getString("PRODUCT_ID");
	        idParam.put("USER_PRODUCT_ID", strUserProductId);
	        
	    	IDataset dataset = CSAppCall.call("CS.SelectedElementSVC.getUserElements", idParam);
	    	if (IDataUtil.isEmpty(dataset))
	        {
				//IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703008");
				idError.put("X_RESULTINFO", "您选择宽带产品有误，不能办理业务");
				return idError;
	        }
	    	
	    	String strServices = "";
	    	IDataset idsAddElement = new DatasetList();
	    	String elements = dataset.first().getString("SELECTED_ELEMENTS");
	    	if(StringUtils.isNotBlank(elements))	  
	    	{  
	    		IDataset idsSelectedElement = new DatasetList(elements);
	    		for (int i = 0; i < idsSelectedElement.size(); i++)
	    		{  
	    			IData idSelectedElement = idsSelectedElement.getData(i);
	    			String strModifyTag = idSelectedElement.getString("MODIFY_TAG", "");
	    			String strEYC = idSelectedElement.getString("ELEMENT_TYPE_CODE", "");
	    			String strEID = idSelectedElement.getString("ELEMENT_ID", "");
	    			if("0".equals(strModifyTag))
	    			{
	    				idSelectedElement.put("START_DATE", firstDayOfNextMonth);
	    				idsAddElement.add(idSelectedElement);
	    			}
	    			else if(!"exist".equals(strModifyTag))  
	    			{
	    				idsAddElement.add(idSelectedElement);
	    			}
	    			
	    			if(!"1".equals(strModifyTag) && "S".equals(strEYC))
	    			{
	    				if ("".equals(strServices))
						{
	    					strServices = strEID;
						}else
						{
							strServices = strServices + "|" + strEID;
						}
	    			}
	    		}
	    	}
	    	
	    	IData idAddElement = new DataMap();
	    	if(IDataUtil.isNotEmpty(dataset))
	    	{
	    		idAddElement.put("SELECTED_ELEMENTS", idsAddElement);
	    		idParam.putAll(idAddElement);
	    	}
	    	  
	    	if(StringUtils.isNotBlank(strServices))
	    	{
	    		idParam.put("WIDE_USER_SELECTED_SERVICEIDS", strServices); 
	    	}
	    	
	    	IDataset idsWidenetSaleActive = getWidenetSaleActiveInfo(idParam);
	    	if(IDataUtil.isEmpty(idsWidenetSaleActive))
			{
	    		//IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703005");
				idError.put("X_RESULTINFO", "您办理的包年营销活动有误，不能办理业务");
				return idError;
			}
	    	IData idWidenetSaleActive = idsWidenetSaleActive.first();
	    	String strNewSaleActiveProductID = idWidenetSaleActive.getString("NEW_SALE_ACTIVE_PRODUCT_ID", "");
	    	String strNewSaleActivePackageID = idWidenetSaleActive.getString("NEW_SALE_ACTIVE_PACKAGE_ID", "");
	    	idParam.put("NEW_SALE_ACTIVE_PRODUCT_ID", strNewSaleActiveProductID);
	    	idParam.put("NEW_SALE_ACTIVE_PACKAGE_ID", strNewSaleActivePackageID);
	    	idParam.put("NEW_SALE_PRODUCT_ID", strNewSaleActiveProductID); 
        	idParam.put("NEW_SALE_PACKAGE_ID", strNewSaleActivePackageID); 
	    	idParam.put("V_NEW_PACKAGE_ID", strNewSaleActivePackageID);
	    	
	    	/*if(bIsActive)
	    	{*/
    		idParam.put("WIDE_ACTIVE_PAY_FEE", "0");
    		idParam.put("YEAR_DISCNT_REMAIN_FEE", "0");
    		idParam.put("REMAIN_FEE", "0");
    		idParam.put("ACCT_REMAIN_FEE", "0");
    		idParam.put("BOOKING_DATE", firstDayOfNextMonth);
	    	
	    	IDataset idsWidenetFee = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkFeeBeforeSubmit", idParam);
	    	if (IDataUtil.isNotEmpty(idsWidenetFee))
	        {
	    		IData idWidenetFee = idsWidenetFee.first();
	    		String strPayFee = idWidenetFee.getString("WIDE_ACTIVE_PAY_FEE", "0");
	    		String strYearDiscntRemainFee = idWidenetFee.getString("YEAR_DISCNT_REMAIN_FEE", "0");
	    		String strRemainFee = idWidenetFee.getString("REMAIN_FEE", "0");
	    		String strAcctRemainFee = idWidenetFee.getString("ACCT_REMAIN_FEE", "0");
	    		idParam.put("WIDE_ACTIVE_PAY_FEE", strPayFee);
	    		idParam.put("YEAR_DISCNT_REMAIN_FEE", strYearDiscntRemainFee);
	    		idParam.put("REMAIN_FEE", strRemainFee);
	    		idParam.put("ACCT_REMAIN_FEE", strAcctRemainFee);
	        }
	    	//}	
	    	
			/*IDataset idsElements = CSAppCall.call("CS.SelectedElementSVC.getWidenetUserOpenElements", idParam);
			if(IDataUtil.isEmpty(idsElements))
			{
				IData idError = new DataMap();
				idError.put("X_RESULTCODE", "-1");
				idError.put("X_RESULTINFO", "您选择宽带产品有误，不能办理业务");
				return idError;
			}
			idParam.putAll(idsElements.first());*/
	    	
	    	//idParam.put("EFFECT_NOW", "1");
	    	//idParam.put("CHANGE_TYPE", "1");
	    	
	    	IDataset idsUserWidenetInfo = CSAppCall.call("CS.WidenetInfoQuerySVC.getUserWidenetInfo", idParam);
	        String strWidetype = idsUserWidenetInfo.first().getString("RSRV_STR2");
	        idParam.put("WIDE_TYPE", strWidetype);
	        
	        
	        //idParam.put("NEW_SALE_PRODUCT_ID", strNewSaleActiveProductID);
	        //idParam.put("NEW_SALE_PACKAGE_ID", strNewSaleActivePackageID);
	        //idParam.put("V_NEW_PACKAGE_ID", strNewSaleActivePackageID);
	        //idParam.put("V_USER_PACKAGE_ID", strOldSaleActivePackageID);
	        //idParam.put("V_USER_PRODUCT_ID", strOldSaleActiveProuductID);
	        idParam.put("EFFECT_NOW", "0");
	        idParam.put("V_BOOK_TAG", "1");
	        idParam.put("BOOKDATE", firstDayOfNextMonth);
	        idParam.put("END_BOOKDATE", firstDayOfNextMonth);
	        
	        String strRateDownUpFlag = "0"; //速率升级标记，0：不变，1：升档，2：降档，3：速率不变，产品变
	        idParam.put("WIDE_USER_CREATE_SALE_ACTIVE", "0");
	        if(!strUserProductId.equals(strNewProductID))
	        {
	        	strRateDownUpFlag = "3";
	        	idParam.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");
	        }
	        int strOldWidenetRate = 0;
	        //查询当前产品的速率，如果未查到说明没有配置，主要是因为有些产品已经不用了，但是用户的产品还是旧的，容易造成错误
	        //IDataset idsOldWidenetRate = WidenetInfoQry.queryWidenetRateByProductID(strUserProductId);
	        IDataset idsOldWidenetRate = CommparaInfoQry.getCommpara("CSM", "4000", strUserProductId, "0898");
	        if (IDataUtil.isNotEmpty(idsOldWidenetRate))
	        {
	        	strOldWidenetRate = idsOldWidenetRate.first().getInt("PARA_CODE1");
	        }
	        else
	        {
	        	//IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703009");
				idError.put("X_RESULTINFO", "您原有的宽带产品未配置速率，不能办理业务");
				return idError;
	        }
	        
	        int strNewWidenetRate = 0;
	        //IDataset idsNewWidenetRate = WidenetInfoQry.queryWidenetRateByProductID(strNewProductID);
	        IDataset idsNewWidenetRate = CommparaInfoQry.getCommpara("CSM", "4000", strNewProductID, "0898");
	        if (IDataUtil.isNotEmpty(idsNewWidenetRate))
	        {
	        	strNewWidenetRate = idsNewWidenetRate.first().getInt("PARA_CODE1");
	        }
	        else
	        {
	        	//IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703010");
				idError.put("X_RESULTINFO", "您办理的宽带产品未配置速率，不能办理业务");
				return idError;
	        }
	        
	        if(strOldWidenetRate < strNewWidenetRate)
	        {
	        	strRateDownUpFlag = "1";
	        }
	        else if(strOldWidenetRate > strNewWidenetRate)
	        {
	        	strRateDownUpFlag = "2";
	        }
	        
	        idParam.put("CHANGE_UP_DOWN_TAG", strRateDownUpFlag);
	        
	        if("0".equals(strRateDownUpFlag))
	        {
	        	idParam.put("SERIAL_NUMBER", strSerialNumber);
	        	idParam.put("AUTH_SERIAL_NUMBER", strSerialNumber);
	        	idParam.put("CHANGE_TYPE", "2");
	        	idParam.put("NEW_SALE_PRODUCT_ID", strNewSaleActiveProductID); 
	        	idParam.put("NEW_SALE_PACKAGE_ID", strNewSaleActivePackageID); 
	        	idParam.put("PRE_TYPE", "");
	        	IData idResult = setWidenetSaleActiveInfo(idParam);
	        	idParam.putAll(idResult);
	        }
	        else
	        {
	        	idParam.put("SERIAL_NUMBER", strSerialNumber);
	        	idParam.put("AUTH_SERIAL_NUMBER", strSerialNumber);
	        	idParam.put("CHANGE_TYPE", "3");
	        	idParam.put("NEW_SALE_PRODUCT_ID", strNewSaleActiveProductID); 
	        	idParam.put("NEW_SALE_PACKAGE_ID", strNewSaleActivePackageID);
	        	IDataset idsResult = CSAppCall.call("SS.WidenetChangeProductNewRegSVC.tradeReg", idParam);
		        idParam.putAll(idsResult.first());
	        }
	    	//idParam.put("idsWidenetFee", idsWidenetFee);
	    	return idParam;
			
        }
		else
		{
			//IData idError = new DataMap();
			//idError.put("X_RESULTCODE", "201703011");
			//idError.put("X_RESULTINFO", "您不是包年营销活动用户，不能办理业务");
			return idError;
		}
    }
    
    /**
     * 宽带包年营销活动查询接口
     * @param input
     * @return
     * @throws Exception
     */
	public IDataset getWidenetSaleActiveInfo(IData input) throws Exception
	{
		chkParamNoStr(input, "SERIAL_NUMBER", "201703001"); // 手机号码
		chkParamNoStr(input, "NEW_PRODUCT_ID", "201703001"); // 手机号码
		String strNewProductID = input.getString("NEW_PRODUCT_ID");
		IDataset idsSaleActive = CommparaInfoQry.getCommparaInfoBy7("CSM", "177", "601", strNewProductID, "WIDE_YEAR_ACTIVE", "0898", null);
		if(IDataUtil.isNotEmpty(idsSaleActive))
		{
			for (int i = 0; i < idsSaleActive.size(); i++)
			{
				IData idSaleActive = idsSaleActive.getData(i);
				String strNewSaleActiveProuductID = idSaleActive.getString("PARA_CODE4", "");
				String strNewSaleActivePackageID = idSaleActive.getString("PARA_CODE5", "");
				idSaleActive.put("NEW_SALE_ACTIVE_PRODUCT_ID", strNewSaleActiveProuductID);
				idSaleActive.put("NEW_SALE_ACTIVE_PACKAGE_ID", strNewSaleActivePackageID);
			}
		}
		//saleActiveList = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), saleActiveList);
		return idsSaleActive;
	}
	
	/**
     * 宽带包年营销活动查询接口
     * @param input
     * @return
     * @throws Exception
     */
	public IData setWidenetSaleActiveInfo(IData input) throws Exception
	{
		chkParamNoStr(input, "SERIAL_NUMBER", "201703001"); // 手机号码
		chkParamNoStr(input, "NEW_SALE_PRODUCT_ID", "201703001"); // 
		chkParamNoStr(input, "NEW_SALE_PACKAGE_ID", "201703001"); // 

		String strSerialNumber = input.getString("SERIAL_NUMBER");
		IData idUser = new DataMap();
		idUser.put("SERIAL_NUMBER_A", strSerialNumber);
		idUser.put("TRADE_TYPE_CODE", "601");
		idUser.put(Route.ROUTE_EPARCHY_CODE, "0898");
		
		String strKdSerialNumber = "KD_" + strSerialNumber;
		IData idKdUser = UcaInfoQry.qryUserInfoBySn(strKdSerialNumber);
		if(IDataUtil.isEmpty(idKdUser))
		{
			IData idError = new DataMap();
			idError.put("X_RESULTCODE", "201703105");
			idError.put("X_RESULTINFO", "用户宽带信息不存在，请检查");
			return idError;
		}
		
		String strKdUserID = idKdUser.getString("USER_ID", "");
		idUser.put("USER_ID", strKdUserID);
		
		IDataset result2 = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetProduct", idUser);
        if (IDataUtil.isNotEmpty(result2))
        {
        	String strVend = result2.getData(0).getString("END_DATE");//包年的直接取结束日期
        	String strBooktag = result2.getData(0).getString("V_BOOK_TAG","");
        	
        	if(!"1".equals(strBooktag))
			{
				IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703104");
				idError.put("X_RESULTINFO", "您的包年活动套餐不是3个月后到期，不能办理业务");
				return idError;
			}
        	
        	String firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(strVend);
			//2、只调用营销活动接口
			IData data = new DataMap();
			data.putAll(input);
			data.put("END_BOOKDATE", firstDayOfNextMonth);
			data.put("BOOKING_DATE", firstDayOfNextMonth);
			data.put("BOOKDATE", firstDayOfNextMonth);
	    	data.put("TRADE_TYPE_CODE", "240");
	    	data.put("PRODUCT_ID", input.getString("NEW_SALE_PRODUCT_ID"));
	    	data.put("PACKAGE_ID", input.getString("NEW_SALE_PACKAGE_ID"));
	    	data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
	    	data.put("PRE_TYPE", input.getString("PRE_TYPE", ""));
	    	/*data.put("TRADE_STAFF_ID", "SUPERUSR");
	    	data.put("TRADE_DEPART_ID", "36601");
	    	data.put("TRADE_CITY_CODE", "HNSJ");
	    	data.put("EPARCHY_CODE", "0898");
	    	data.put("WIDE_USER_CREATE_SALE_ACTIVE", input.getString("WIDE_USER_CREATE_SALE_ACTIVE"));
	    	//data.put("WIDE_USER_SELECTED_SERVICEIDS", input.getString("WIDE_USER_SELECTED_SERVICEIDS"));
	    	data.put("CHANGE_UP_DOWN_TAG", input.getString("CHANGE_UP_DOWN_TAG",""));*/
	        
	        //宽带包年营销活动需要补缴的费用
	    	//data.put("WIDE_ACTIVE_PAY_FEE", input.getString("WIDE_ACTIVE_PAY_FEE"));
	        
	        //认证方式
	        //String checkMode = btd.getRD().getCheckMode();
	        //data.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );

	    	try 
			{
	    		IData idResult = new DataMap();
	    		IDataset retnInfo = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", data);
	    		if (IDataUtil.isNotEmpty(retnInfo) && StringUtils.isBlank(retnInfo.first().getString("ORDER_ID"))) 
				{
	    			idResult.put("X_RESULTCODE", "-1");
	    			idResult.put("X_RESULTINFO", "尊敬的客户，您好！您的宽带办理失败。");
				}
				else
				{
					idResult.putAll(retnInfo.first());
					idResult.put("X_RESULTCODE", "0");
					data.put("X_RESULTINFO", "尊敬的客户，您好！您的宽带办理成功。");
				}
		    	return idResult;
			}
			catch (Exception e) 
			{
				IData idError = new DataMap();
				String error =  Utility.parseExceptionMessage(e);
				String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
				if(errorArray.length >= 2)
				{
					String strException = errorArray[0];
					String strExceptionMessage = errorArray[1];
					idError.put("X_RESULTCODE", strException);
					idError.put("X_RESULTINFO", strExceptionMessage);
				}
				else
				{
					idError.put("X_RESULTCODE", "-1");
					idError.put("X_RESULTINFO", "尊敬的客户，您好！您的宽带办理失败。");
				}
				return idError;
			}
	    	/*IData idResult = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", data).first();
	    	return idResult;*/
        }
		
		IDataset idsWidenetActive = CSAppCall.call("SS.WidenetChangeProductNewSVC.checkWidenetActive", idUser);
		if (IDataUtil.isNotEmpty(idsWidenetActive))
        {
			IData idWidenetActive = idsWidenetActive.first();
			String strUserYearActive = idWidenetActive.getString("USER_YEAR_ACTIVE", "");
			if(!"1".equals(strUserYearActive))
			{
				IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703101");
				idError.put("X_RESULTINFO", "您不是包年活动用户，不能办理业务");
				return idError;
			}
			
			String strVBookTag = idWidenetActive.getString("V_BOOK_TAG", "");
			if(!"1".equals(strVBookTag))
			{
				IData idError = new DataMap();
				idError.put("X_RESULTCODE", "201703102");
				idError.put("X_RESULTINFO", "您的包年营销活动不是3个月后到期，不能办理业务");
				return idError;
			}
			//IData activeData = result3.getData(0);
			String strVEndDate = idWidenetActive.getString("V_END_DATE", "");
			String firstDayOfNextMonth = SysDateMgr.getDateNextMonthFirstDay(strVEndDate);
			//2、只调用营销活动接口
			IData data = new DataMap();
			data.putAll(input);
			data.put("END_BOOKDATE", firstDayOfNextMonth);
			data.put("BOOKING_DATE", firstDayOfNextMonth);
			data.put("BOOKDATE", firstDayOfNextMonth);
	    	data.put("TRADE_TYPE_CODE", "240");
	    	data.put("PRODUCT_ID", input.getString("NEW_SALE_PRODUCT_ID"));
	    	data.put("PACKAGE_ID", input.getString("NEW_SALE_PACKAGE_ID"));
	    	data.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
	    	data.put("PRE_TYPE", input.getString("PRE_TYPE", ""));
	    	/*data.put("TRADE_STAFF_ID", "SUPERUSR");
	    	data.put("TRADE_DEPART_ID", "36601");
	    	data.put("TRADE_CITY_CODE", "HNSJ");
	    	data.put("EPARCHY_CODE", "0898");
	    	data.put("WIDE_USER_CREATE_SALE_ACTIVE", input.getString("WIDE_USER_CREATE_SALE_ACTIVE"));
	    	//data.put("WIDE_USER_SELECTED_SERVICEIDS", input.getString("WIDE_USER_SELECTED_SERVICEIDS"));
	    	data.put("CHANGE_UP_DOWN_TAG", input.getString("CHANGE_UP_DOWN_TAG",""));*/
	        
	        //宽带包年营销活动需要补缴的费用
	    	//data.put("WIDE_ACTIVE_PAY_FEE", input.getString("WIDE_ACTIVE_PAY_FEE"));
	    	try 
			{
	    		IData idResult = new DataMap();
	    		IDataset retnInfo = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", data);
	    		if (IDataUtil.isNotEmpty(retnInfo) && StringUtils.isBlank(retnInfo.first().getString("ORDER_ID"))) 
				{
	    			idResult.put("X_RESULTCODE", "-1");
	    			idResult.put("X_RESULTINFO", "尊敬的客户，您好！您的宽带办理失败。");
				}
				else
				{
					idResult.putAll(retnInfo.first());
					idResult.put("X_RESULTCODE", "0");
					idResult.put("X_RESULTINFO", "尊敬的客户，您好！您的宽带办理成功。");
				}
		    	return idResult;
			}
			catch (Exception e) 
			{
				IData idError = new DataMap();
				String error =  Utility.parseExceptionMessage(e);
				String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
				if(errorArray.length >= 2)
				{
					String strException = errorArray[0];
					String strExceptionMessage = errorArray[1];
					idError.put("X_RESULTCODE", strException);
					idError.put("X_RESULTINFO", strExceptionMessage);
				}
				else
				{
					idError.put("X_RESULTCODE", "-1");
					idError.put("X_RESULTINFO", "尊敬的客户，您好！您的宽带办理失败。");
				}
				return idError;
			}
        }
		else
		{
			IData idError = new DataMap();
			idError.put("X_RESULTCODE", "201703103");
			idError.put("X_RESULTINFO", "您不是包年活动用户，不能办理业务");
			return idError;
		}
	}
	/**
	 * 
	 * TODO 校验用户是否办理移动电视营销活动
	 * @author chenfeng9
	 * @date 2018年4月4日
	 * @param param
	 * @return
	 * @throws Exception
	 * @return IDataset
	 */
	public IDataset checkIsHasMobileTV(IData param) throws Exception
    {
    	String serialnumber = param.getString("SERIAL_NUMBER_A");
    	IDataset userinfo = UserInfoQry.getUserinfo(serialnumber);
    	if (!IDataUtil.isNotEmpty(userinfo))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"未找到用户资料！");
    	}
        return UserSaleActiveInfoQry.isHasMobileTV(serialnumber);
    }

	
	/**
	 * 宽带产品变更提交前 优惠校验
	 * @param param
	 * @return
	 * @throws Exception
	 * xuzh5 
	 * 2018-9-21 15:14:00
	 */
	public IData checkDisnctBeforeSubmit(IData param) throws Exception
    {
		IData result=new DataMap();
		result.put("RESULT", "true");
		result.put("STAFF_ID", getVisit().getStaffId());
		String elements = param.getString("SELECTED_ELEMENTS");
		IDataset selectedElements = new DatasetList(elements); 
		for(int i = 0 ;i < selectedElements.size() ; i++)
		{
			IData element = selectedElements.getData(i);
			String modifyTag = element.getString("MODIFY_TAG","");
			String elementId = element.getString("ELEMENT_ID","");
			if("0".equals(modifyTag)&&("84013442".equals(elementId)||"84013443".equals(elementId)||"84013444".equals(elementId))){
				result.put("DISNCT_CODE", elementId);
				IDataset info = Dao.qryByCode("TF_F_ACTIVE_STOCK", "SEL_BY_DISNCT", result);
				if(IDataUtil.isEmpty(info)){
					result.put("RESULT", "false");
					result.put("ELEMENT_ID", elementId);
				}
				
				//add办理之后礼包数据变化
				String RES_KIND_CODE=StringTools.decode(elementId,"84013442","90T","84013443","180T","84013444","360T");
				  IData cond = new DataMap();
		            cond.put("RES_KIND_CODE", RES_KIND_CODE);
		            cond.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
		            cond.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		            cond.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
		            StringBuilder sql = new StringBuilder(200);
		            sql.append(" UPDATE TF_F_ACTIVE_STOCK");
		            sql.append(" SET WARNNING_VALUE_U = WARNNING_VALUE_U + 1");
		            sql.append(" WHERE STAFF_ID = :STAFF_ID");
		            sql.append(" AND EPARCHY_CODE = :EPARCHY_CODE");
		            sql.append(" AND RES_KIND_CODE = :RES_KIND_CODE");
		            sql.append(" AND CITY_CODE = :CITY_CODE");
		            Dao.executeUpdate(sql, cond);
			}
		  }
        return result;
    }
	
	
	public IData getWideRatTips(IData param) throws Exception
    {    
    	 IDataUtil.chkParam(param, "NEW_PRODUCT_ID"); 
    	 IDataUtil.chkParam(param, "SERIAL_NUMBER");
    	 
    	 String newProductId = param.getString("NEW_PRODUCT_ID");
    	 String serialNumber = param.getString("SERIAL_NUMBER");
    	 
    	 String widenetSerialNum = "";
     	 String phoneSerialNum = "";
    	 
    	 if (serialNumber.startsWith("KD_"))
 		 {
 			widenetSerialNum = serialNumber;
 			phoneSerialNum = serialNumber.substring(3);
 		 }
 		 else
 		 {
 			widenetSerialNum = "KD_" + serialNumber;
 			phoneSerialNum = serialNumber;
 		 }
    	 
    	 IData resultData = new DataMap();
    	 resultData.put("X_RESULTCODE", "2998");
 		
 		 IData userInfo = UcaInfoQry.qryUserInfoBySn(phoneSerialNum);
 		 if (IDataUtil.isNotEmpty(userInfo))
         {
 			 String userId = userInfo.getString("USER_ID");
 	 		 
 	 		 //宽带产品速率
 	     	 String productRate = this.getWideRate(newProductId);
 	     	 
 	     	 IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
 			 if (IDataUtil.isNotEmpty(userMainProducts))
 			 {
 				 String productId = userMainProducts.first().getString("PRODUCT_ID");
 				 IDataset commparaInfos778 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","778",productId,null);
 				 if(IDataUtil.isNotEmpty(commparaInfos778)){
 		    		 String maxRate = commparaInfos778.first().getString("PARA_CODE2","0");
 		    		 
 		        	 if(new BigDecimal(productRate).compareTo(new BigDecimal(maxRate))>0){
 		        		 resultData.put("X_RESULTCODE", "0000");
 		    			 resultData.put("X_RESULTINFO", "是否要同期变更融合套餐。");
 		    		 } 
 		    	 }
 			 }
         }
 		 
 		
     	 
    	 return resultData;
    }	
    
    public String getWideRate(String newProductId) throws Exception
    {   
    	//用户宽带速率
    	String userRate = "0";
    	
    	IDataset forceElements = UpcCall.qryAllOffersByOfferIdWithForceTagDefaultTagFilter(BofConst.ELEMENT_TYPE_CODE_PRODUCT, newProductId, "1", "1");
    	if (IDataUtil.isNotEmpty(forceElements))
        {
            IDataset rate_ds = null;
            IData forceElement = null;
            
            for (int j = 0; j < forceElements.size(); j++)
            {
                forceElement = forceElements.getData(j);
                
                if ("S".equals(forceElement.getString("OFFER_TYPE")))
                {
                    //根据产品下的服务ID查询宽带速率
                    rate_ds = CommparaInfoQry.getCommpara("CSM", "4000",forceElement.getString("OFFER_CODE") , "0898");
                    
                    if (IDataUtil.isNotEmpty(rate_ds))
                    {
                        break;
                    }
                }
            }
            
            if (IDataUtil.isNotEmpty(rate_ds))
            {
            	userRate = rate_ds.getData(0).getString("PARA_CODE1","0");
            }
        }
    	
    	return userRate;
    }


}
