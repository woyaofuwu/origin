
package com.asiainfo.veris.crm.order.web.person.broadband.nophonewidenet.nophonechangeprod;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import com.asiainfo.veris.crm.order.web.person.broadband.widenet.changeproduct.WidenetChangeProductNew;

public abstract class NoPhoneWideChangeProd extends PersonBasePage
{  
	private static Logger logger = Logger.getLogger(WidenetChangeProductNew.class);
    public void qryUserWideInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
      //初始化宽带类型
        IData pdata1 = new DataMap();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
    	IDataset dataset = CSViewCall.call(this, "CS.WidenetInfoQuerySVC.getUserWidenetInfo", data);
        String  widetype = dataset.getData(0).getString("RSRV_STR2");
        pdata1.put("WIDE_TYPE_CODE", widetype);
        pdata1.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE", ""));
        String productmode="";
    	/*
    	 *  20161140	无手机FTTH宽带产品100M套餐（T）	23
			20161120	无手机FTTH宽带产品20M套餐（T）	23
			20161220	无手机FTTB宽带20M包年套餐（T）	24
			20161130	无手机FTTH宽带产品50M套餐（T）	23
			20161230	无手机FTTB宽带50M包年套餐（T）	24
			20160930	无手机FTTH宽带产品50M套餐        		21
			20161030	无手机FTTB宽带50M包年套餐			22
			20160920	无手机FTTH宽带产品20M套餐			21
			20160940	无手机FTTH宽带产品100M套餐		21
			20161020	无手机FTTB宽带20M包年套餐			22 
    	 * */
        if ("1".equals(widetype))
        {
    		pdata1.put("WIDE_TYPE_NAME", "移动FTTB");
    		productmode="22";
        }else if ("2".equals(widetype))
        {
        	pdata1.put("WIDE_TYPE_NAME", "铁通ADSL");
        	productmode="18";
        }else if ("3".equals(widetype))
        {
        	pdata1.put("WIDE_TYPE_NAME", "移动FTTH");
        	productmode="21";
        }else if ("5".equals(widetype))
        {
        	pdata1.put("WIDE_TYPE_NAME", "铁通FTTH"); 
        	productmode="23";  
        }else if ("6".equals(widetype))
        {
        	pdata1.put("WIDE_TYPE_NAME", "铁通FTTB"); 
        	productmode="24";  
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
        IDataset result2 = CSViewCall.call(this, "SS.NoPhoneWideChangeProdSVC.checkWidenetProduct", data);
        pdata1.put("PRODUCT_YEAR", "0");//包年套餐标志
        pdata1.put("V_END_DATE", "");//保存包年套餐，或者营销活动的结束时间
        String v_end="";
        String booktag="";
        String startDate = "";
        if (IDataUtil.isNotEmpty(result2))
        {
        	pdata1.put("PRODUCT_YEAR", "1");//包年套餐=1
        	v_end = result2.getData(0).getString("END_DATE");//包年的直接取结束日期
        	startDate = result2.getData(0).getString("START_DATE");
        	booktag = result2.getData(0).getString("V_BOOK_TAG","");
        	pdata1.put("V_BOOK_TAG", booktag);
        	pdata1.put("V_END_DATE", v_end);
        	pdata1.put("END_FLAG", result2.getData(0).getString("FLAG","")); 
        	Calendar cl=Calendar.getInstance();
        	cl.set(Calendar.DAY_OF_MONTH, 1);
        	cl.add(Calendar.MONTH, 1); 
        	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        	String nextMonFirstDay=sdf.format(cl.getTime());
        	pdata1.put("NEXT_MON_FISRTDAY", nextMonFirstDay);
        }
        
        //初始化产品变更的预约日期列表，可以预约3个月
        String discntCode = result2.getData(0).getString("DISCNT_CODE","");
        //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
        if(("84014240".equals(discntCode) || "84014241".equals(discntCode) || "84014242".equals(discntCode)) 
        		&& "1".equals(booktag)  ){
        	IData bookTimeThree = new DataMap();    
        	String bookTime = "";
        	
        	Date date = DateUtils.addMonths(SysDateMgr.string2Date(result2.getData(0).getString("END_DATE"), SysDateMgr.PATTERN_STAND_YYYYMMDD), 1);

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.DAY_OF_MONTH, 1);

            bookTime = DateFormatUtils.format(c, SysDateMgr.PATTERN_STAND_YYYYMMDD);
        	
            bookTimeThree.put("DATA_ID", bookTime);     
            bookTimeThree.put("DATA_NAME", bookTime);             
        	setTimeList(IDataUtil.idToIds(bookTimeThree));
        }
        else{
        	onInitBookTimeList(cycle,startDate);
        }
        //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
        setInfo(pdata1);
    }
  //初始化预约拆机时间选择框
    public void onInitBookTimeList(IRequestCycle cycle,String startDate) throws Exception
    { 
    	IData data = new DataMap();
    	data.put("START_DATE", startDate);
    	IDataset iset = CSViewCall.call(this, "SS.NoPhoneWideChangeProdSVC.onInitBookTimeList", data);
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
        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", "681");
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
        if ("1".equals(changeType) || "3".equals(changeType) || "4".equals(changeType) || "5".equals(changeType) || "6".equals(changeType) || "7".equals(changeType))
        {
        	//目前无手机宽带写死类型=1
        	//1、调用产品变更接口
    		//3、调用产品变更接口与营销活动受理接口
    		//4、调用产品变更接口及营销活动终止接口
    		//5、调用产品变更接口及营销活动终止接口，营销活动受理接口
        	result = CSViewCall.call(this, "SS.NoPhoneWideChangeProdRegSVC.tradeReg", data);
        }
//        else if ("2".equals(changeType) || "8".equals(changeType))
//        {
//        	//2、只调用营销活动接口
//        	data.put("TRADE_TYPE_CODE", "240");
//        	data.put("PRODUCT_ID", data.getString("NEW_SALE_PRODUCT_ID"));
//        	data.put("PACKAGE_ID", data.getString("NEW_SALE_PACKAGE_ID"));
//        	String serialNumber = data.getString("SERIAL_NUMBER");
//        	if(serialNumber != null && !"".equals(serialNumber))
//        	{
//        		if(serialNumber.startsWith("KD_"))
//        		{
//        			serialNumber = serialNumber.replace("KD_", "");
//        			data.put("SERIAL_NUMBER", serialNumber);
//        		}
//        	}
//        	result = CSViewCall.call(this, "SS.SaleActiveRegSVC.tradeReg4Intf", data);
//        }
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
     * 获取产品费用
     * 
     * @author yuyj3
     * @param clcle
     * @throws Exception
     */
    public void getProductFeeInfo(IRequestCycle clcle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        data.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        data.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        data.put("PACKAGE_ID", "");
        data.put("ELEMENT_ID", data.getString("ELEMENT_ID"));
        data.put("ELEMENT_TYPE_CODE", "P");
        data.put("TRADE_FEE_TYPE", "3");
        data.put("EPARCHY_CODE", data.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "CS.ProductFeeInfoQrySVC.getProductFeeInfo", data);
        setAjax(dataset);
    } 
     
}
