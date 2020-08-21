
package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.changeproduct;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class WidenetChangeProductNew extends PersonBasePage
{
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
      //初始化宽带类型
        IData pdata1 = new DataMap();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
    	IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        String  widetype = dataset.getData(0).getString("RSRV_STR2");
        pdata1.put("WIDE_TYPE_CODE", widetype);
        String productmode="";//--07：移动GPON宽带，09：ADSL宽带产品，11：移动FTTH宽带，16：海南铁通FTTH，17：海南铁通FTTB，13：校园宽带
    	if ("1".equals(widetype))
        {
    		pdata1.put("WIDE_TYPE_NAME", "移动FTTB");
    		productmode="07";
        }else if ("2".equals(widetype))
        {
        	pdata1.put("WIDE_TYPE_NAME", "铁通ADSL");
        	productmode="09";
        }else if ("3".equals(widetype))
        {
        	pdata1.put("WIDE_TYPE_NAME", "移动FTTH");
        	productmode="11";
        }else if ("5".equals(widetype))
        {
        	pdata1.put("WIDE_TYPE_NAME", "铁通FTTH");
        	//productmode="16";
        	productmode="11"; //移动FTTH与铁通FTTH合并，使用同一套产品
        }else if ("6".equals(widetype))
        {
        	pdata1.put("WIDE_TYPE_NAME", "铁通FTTB");
        	//productmode="17";
        	productmode="07"; //移动FTTB与铁通FTTB合并，使用同一套产品
        }
    	
    	//初始化产品包列表
    	data.put("PROD_MODE", productmode);
        IDataset result = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.loadProductInfo", data);
        if (IDataUtil.isNotEmpty(result))
        {
            this.setAjax(result);
            pdata1.put("USER_PRODUCT_NAME", result.getData(0).getString("USER_PRODUCT_NAME"));
            pdata1.put("USER_PRODUCT_ID", result.getData(0).getString("USER_PRODUCT_ID"));
        }
        String productList = result.getData(0).getString("PRODUCT_LIST");
        setProductList(new DatasetList(productList));
        
//        String seril_num = data.getString("SERIAL_NUMBER","");
//        String seril_num2 = data.getString("SERIAL_NUMBER_A","");
        //判断办理的是包年套餐还是营销活动
        IDataset result2 = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.checkWidenetProduct", data);
        pdata1.put("PRODUCT_YEAR", "0");//包年套餐标志
        pdata1.put("V_END_DATE", "");//保存包年套餐，或者营销活动的结束时间
        String v_end="";
        String booktag="";
        if (IDataUtil.isNotEmpty(result2))
        {
        	pdata1.put("PRODUCT_YEAR", "1");//包年套餐=1
        	v_end = result2.getData(0).getString("END_DATE");//包年的直接取结束日期
        	booktag = result2.getData(0).getString("V_BOOK_TAG","");
        	pdata1.put("V_BOOK_TAG", booktag);
        	pdata1.put("V_END_DATE", v_end);
        	pdata1.put("END_FLAG", result2.getData(0).getString("FLAG",""));
        }
        IDataset result3 = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.checkWidenetActive", data);
        pdata1.put("PRODUCT_ACTIVE", "0");//宽带营销活动标志
        pdata1.put("USER_PACKAGE_ID", "");//保存当前的营销活动id
        
        if (IDataUtil.isNotEmpty(result3))
        {
        	IData activeData = result3.getData(0);
            
            pdata1.put("PRODUCT_ACTIVE", "1");//有营销活动=1
        	pdata1.put("USER_SALE_PACKAGE_ID", activeData.getString("PACKAGE_ID",""));
        	pdata1.put("USER_SALE_PRODUCT_ID", activeData.getString("PRODUCT_ID",""));
        	pdata1.put("USER_SALE_PACKAGE_NAME", activeData.getString("PACKAGE_NAME",""));
        	pdata1.put("USER_SALE_PRODUCT_NAME", activeData.getString("PRODUCT_NAME",""));
        	
        	//1.包年营销活动   0：非包年营销活动
        	pdata1.put("USER_YEAR_ACTIVE", activeData.getString("USER_YEAR_ACTIVE",""));
        	
        	//产品变更升档时，可选则的营销活动产品ID
            pdata1.put("UPGRADE_SALE_ACTIVE_PRODUCT_IDS", activeData.getString("UPGRADE_SALE_ACTIVE_PRODUCT_IDS",""));
        	
        	pdata1.put("V_BOOK_TAG", activeData.getString("V_BOOK_TAG",""));
        	pdata1.put("V_END_DATE", activeData.getString("V_END_DATE"));
        	pdata1.put("END_FLAG", activeData.getString("FLAG",""));
        }
        //REQ201706200008    移动电视尝鲜活动办理条件优化 --校验用户时候办理过移动电视尝鲜活动
        IDataset result4 = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.checkIsHasMobileTV", data);
        if (IDataUtil.isNotEmpty(result4))
        {
        	pdata1.put("HAS_MOBILE_TV","1");
        	pdata1.put("MOBILE_TV_END_DATE",result4.getData(0).getString("END_DATE"));
        }else{
        	pdata1.put("HAS_MOBILE_TV","0");
        }
        
//        if (!"".equals(v_end))
//        {
//        	//
//        }else
//        {
//            	pdata1.put("V_BOOK_TAG", "1");
//        }
        
        //初始化产品变更的预约日期列表，可以预约3个月
        onInitBookTimeList(cycle,v_end,pdata1);
        
        
        setInfo(pdata1);
    }
  //初始化预约拆机时间选择框
    public void onInitBookTimeList(IRequestCycle cycle,String endDate,IData pdata1) throws Exception
    { 
    	IData data = new DataMap();
    	data.put("USER_ID", this.getData().getString("USER_ID", ""));
    	data.put("ENDDATE", endDate);
    	data.putAll(pdata1);
    	IDataset iset = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.onInitBookTimeList", data);
        setTimeList(iset);
    }
    //当用户之前没有办理过包年套餐，也没有办理过营销活动，则直接初始化3个月的预约时间
    public void resetBookTime_ALL(IRequestCycle cycle) throws Exception
    {
    	IData data = new DataMap();
    	data.put("ENDDATE", "");
        
    	IDataset iset = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.resetBookTime_ALL", data);
        setTimeList(iset);
    }
    //套餐升档下的预约时间重置
    public void resetBookTime_UP(IRequestCycle cycle) throws Exception
    {
    	IData data = this.getData();
    	String v_end = data.getString("V_END_DATE");
    	if ("".equals(v_end))
    	{
    		resetBookTime_ALL(cycle);
    	}else
    	{
    		onInitBookTimeList(cycle,v_end,data);
    	}
    	
    }
    //套餐降档下的预约时间重置
    public void resetBookTime_Down(IRequestCycle cycle) throws Exception
    {
    	IData data = this.getData();
    	String v_end = data.getString("V_END_DATE");
    	if ("".equals(v_end))
    	{
    		resetBookTime_ALL(cycle);
    		return;
    	}
    	IDataset iset = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.resetBookTime_Down", data);
        
        setTimeList(iset);
    }
    /**
     * 初始化方法
     * 
     * @author chenzm
     * @param clcle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        //记录从热门和推荐点击过来的
        IData param = new DataMap();
        
        //从热门和推荐过来的连接，只对第一次用户生效
        param.put("PRE_PRODUCT_ID", data.getString("offerCode", ""));
        param.put("TRADE_TYPE_CODE", "601");
        setInfo(param);
    }

    public abstract void setInfo(IData info);
    public abstract void setProductList(IDataset productList);
    public abstract void setActivePackage(IDataset activePackage);
    public abstract void setTimeList(IDataset timeList);
    
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
    	submitChangeProduct(cycle);
    }
    public void submitChangeProduct(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        
        String changeType = data.getString("CHANGE_TYPE","");
        IDataset result = new DatasetList();
        if("60012727".equals(data.getString("NEW_SALE_PACKAGE_ID",""))||"60012741".equals(data.getString("NEW_SALE_PACKAGE_ID",""))){
        	data.put("SPECIAL_SALE_FLAG", "1");
        }
        if ("1".equals(changeType) || "3".equals(changeType) || "4".equals(changeType) || "5".equals(changeType) || "6".equals(changeType) || "7".equals(changeType))
        {
        	//1、调用产品变更接口
    		//3、调用产品变更接口与营销活动受理接口
    		//4、调用产品变更接口及营销活动终止接口
    		//5、调用产品变更接口及营销活动终止接口，营销活动受理接口
        	result = CSViewCall.call(this, "SS.WidenetChangeProductNewRegSVC.tradeReg", data);
        }
        else if ("2".equals(changeType))
        {
        	//2、只调用营销活动接口
        	data.put("TRADE_TYPE_CODE", "240");
        	data.put("PRODUCT_ID", data.getString("NEW_SALE_PRODUCT_ID"));
        	data.put("PACKAGE_ID", data.getString("NEW_SALE_PACKAGE_ID"));
        	String serialNumber = data.getString("SERIAL_NUMBER");
        	if(serialNumber != null && !"".equals(serialNumber))
        	{
        		if(serialNumber.startsWith("KD_"))
        		{
        			serialNumber = serialNumber.replace("KD_", "");
        			data.put("SERIAL_NUMBER", serialNumber);
        		}
        	}
        	result = CSViewCall.call(this, "SS.SaleActiveRegSVC.tradeReg4Intf", data);
        	}else if("8".equals(changeType)){
        	//REQ202001030018增加宽带1+多人约消档次的开发需求--NoChangeProductEndSaleActiveAction中会终止原活动，这里不需要了。
        	//CSViewCall.call(this, "SS.WidenetChangeProductNewRegSVC.saleactiveandEnd", data);
        	//REQ202001030018增加宽带1+多人约消档次的开发需求
        		
        	data.put("TRADE_TYPE_CODE", "240");
        	data.put("PRODUCT_ID", data.getString("NEW_SALE_PRODUCT_ID"));
        	data.put("PACKAGE_ID", data.getString("NEW_SALE_PACKAGE_ID"));
        	String serialNumber = data.getString("SERIAL_NUMBER");
        	if(serialNumber != null && !"".equals(serialNumber))
        	{
        		if(serialNumber.startsWith("KD_"))
        		{
        			serialNumber = serialNumber.replace("KD_", "");
        			data.put("SERIAL_NUMBER", serialNumber);
        		}
        	}
        	result = CSViewCall.call(this, "SS.SaleActiveRegSVC.tradeReg4Intf", data);
        }
        else
        {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "未知的操作类型CHANGE_TYPE：" + changeType);
        }
        this.setAjax(result);
    }
    
    public void initActivePackage(IRequestCycle cycle) throws Exception
    {
    	IData data = this.getData();
    	IDataset result3 = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.initActivePackage", data);
    	setActivePackage(result3);
    	this.setAjax(result3);
    }
    public void initPackageInfo(IRequestCycle cycle) throws Exception
    {
    	IData data = this.getData();
    	if(!"67220429".equals(data.getString("USER_SALE_PRODUCT_ID","")) && !"69908016".equals(data.getString("USER_SALE_PRODUCT_ID",""))){
    		if("60012727".equals(data.getString("NEW_PACKAGE_ID",""))||"60012741".equals(data.getString("NEW_PACKAGE_ID","")) || 
    				"68180511".equals(data.getString("NEW_PACKAGE_ID","")) || "68180521".equals(data.getString("NEW_PACKAGE_ID","")) || "85300201".equals(data.getString("NEW_PACKAGE_ID","")) || 
    				"85300501".equals(data.getString("NEW_PACKAGE_ID","")) || "85301001".equals(data.getString("NEW_PACKAGE_ID","")) || "60180528".equals(data.getString("NEW_PACKAGE_ID",""))){ //add 20170511
    			data.put("SPECIAL_SALE_FLAG", "1");
    		}
    	}
    	
    	//查询活动详情
        IDataset result3 = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.initPackageInfo", data);
        
        data.put("PRODUCT_ID",data.getString("NEW_SALE_PRODUCT_ID"));
        data.put("PACKAGE_ID",data.getString("NEW_PACKAGE_ID"));
        data.put("PRE_TYPE",  "1");// 预受理校验，不写台账
        data.put("ORDER_TYPE_CODE", "601");
        CSViewCall.call(this,"SS.SaleActiveRegSVC.tradeReg4Intf", data);
        
    	this.setAjax(result3);
    }
    //校验该优惠是否是包年套餐的优惠
    public void checkPackageYear(IRequestCycle cycle) throws Exception
    {
    	IData data = this.getData();
    	IDataset result3 = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.checkPackageYear", data);
    	this.setAjax(result3);
    }
    /**
     * 根据产品选择重置生效时间
     * 1、升档，可以选择最近3个月的日期
     * 2、降档，如果有包年或营销活动，只能选择协议到期后的次月1日
     * 3、不变，生效日期为次月1日
     * 4、速率不变更，产品变更了，如果有包年或营销活动，只能协议到期后的次月1日，如无包年或营销活动，则为次月1日
     * 
     * @param cycle
     * @throws Exception
     */
    public void resetBookTime(IRequestCycle cycle) throws Exception
    {
    	IData data = this.getData();
    	
    	IDataset bookTimeList  = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.resetBookTime", data);
    	
    	setTimeList(bookTimeList);
    }

    /**
     * 校验所选优惠中是否有包年优惠
     * @param cycle
     * @throws Exception
     */
    public void checkIsYearDiscnts(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IData result = CSViewCall.callone(this, "SS.WidenetChangeProductNewSVC.checkIsYearDiscnts", data);
        this.setAjax(result);
    }
    /**
     * 判断宽带1+活动是否可平移
     * @param cycle
     * @throws Exception
     */
    public void checkIsChangeProduct(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        IData result = CSViewCall.callone(this, "SS.WidenetChangeProductNewSVC.checkIsChangeProduct", data);
        this.setAjax(result);
    }
    
    
    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkFeeBeforeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IData result = CSViewCall.callone(this, "SS.WidenetChangeProductNewSVC.checkFeeBeforeSubmit", data);
        this.setAjax(result);
    }
    
    /**
     * 提交前优惠校验
     * @param cycle
     * @throws Exception
     * xuzh5 
     * 2018-9-21 15:41:13
     */
    public void checkDisnctBeforeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        IData result = CSViewCall.callone(this, "SS.WidenetChangeProductNewSVC.checkDisnctBeforeSubmit", data);
        this.setAjax(result);
    }
    
    public void getNewProductTips(IRequestCycle cycle) throws Exception
    {
    	IData userData = this.getData();
        IDataset results = CSViewCall.call(this, "SS.WidenetChangeProductNewSVC.getWideRatTips", userData);
        setAjax(results.first());
    }
}
