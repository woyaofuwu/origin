package com.asiainfo.veris.crm.iorder.web.person.saleactiveend;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

import java.math.BigDecimal;
import java.util.Iterator;

public abstract class NewSaleActiveEndNew extends PersonBasePage
{
	
	/**
	 * 号码查询后的加载
	 * */
	public void loadBaseTradeInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String userId = data.getString("USER_ID");
        String epachyCode = data.getString("EPARCHY_CODE");
        IDataset returnInfo = new DatasetList();//初始返回的营销活动列表

        //读取1530的是否展示配置
        String allShow = "0";//初始只展示能终止的活动
        IData allShowInParam = new DataMap();
        allShowInParam.put("PARAM_CODE", "ALL_SHOW");
        allShowInParam.put(Route.ROUTE_EPARCHY_CODE, epachyCode);
        IDataset commpara1530AllShow = CSViewCall.call(this, "SS.SaleActiveEndSVC.getCommpara1530", allShowInParam);
        if(commpara1530AllShow != null && commpara1530AllShow.size() > 0)
        {
        	IData data1530AllShow = commpara1530AllShow.getData(0);
        	allShow = data1530AllShow.getString("PARA_CODE1","0");//0-不展示未配置的营销活动  1-展示用户所有有效的营销活动（包含能终止和不能终止的）
        }

        //获取该用户所有有效的营销活动
        IData activeParam = new DataMap();
        activeParam.put("USER_ID", userId);
        activeParam.put(Route.ROUTE_EPARCHY_CODE, epachyCode);
        IDataset saleActives = CSViewCall.call(this, "SS.SaleActiveEndSVC.queryCanEndSaleActives", activeParam);
        if(saleActives != null && saleActives.size() > 0)
        {
        	for (Iterator it = saleActives.iterator(); it.hasNext();){
            	IData info = (IData) it.next();
            	String productId = info.getString("PRODUCT_ID","");
            	IData inparam = new DataMap();
    	        inparam.put("PARAM_CODE", productId);
    	        inparam.put(Route.ROUTE_EPARCHY_CODE, epachyCode);
    	        IDataset commparaValue = CSViewCall.call(this, "SS.SaleActiveEndSVC.getCommpara1530", inparam);
    	        if (commparaValue != null && commparaValue.size() > 0)//该活动允许终止
    	        {
    	        	IData commpara = commparaValue.getData(0);
    	        	String paraCode1 = commpara.getString("PARA_CODE1");//约定月份数
    	        	String startData = info.getString("START_DATE","");//该记录营销活动的开始时间
    	        	
    	        	String startDataTemp = SysDateMgr.decodeTimestamp(startData,"yyyyMM");//将活动开始时间转换格式
    	        	String sysDate = SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(),"yyyyMM");//将系统当前时间转换格式
    	        	
    	        	int monthCount = SysDateMgr.monthIntervalYYYYMM(startDataTemp, sysDate);//计算活动月份数

    	        	//继续判断，是否符合约定月份数
    	        	if(monthCount >= Integer.parseInt(paraCode1))//如果符合
    	        	{
    	        		info.put("PRODT_FLAG", "2");//即在1530，又符合约定月份
    	        		returnInfo.add(info);
    	        	}
    	        	else
    	        	{
    	        		info.put("PRODT_FLAG", "1");//在1530，但不符合约定月份
    	        		info.put("CHECK_MONTH", paraCode1);//在1530，但不符合约定月份
    	        		returnInfo.add(info);
    	        	}
    	        }
    	        else//该活动不允许终止
    	        {
    	        	if("1".equals(allShow))//但如果展示
    	        	{
    	        		info.put("PRODT_FLAG", "0");//记录为不在1530，即不允许终止
    	        		returnInfo.add(info);
    	        	}
    	        	
    	        	//如果不允许展示，则不再将该条活动info内容add到returnInfo
    	        }
        	}
        }

        setInfos(returnInfo);
    }
	
	/**
	 * 点击某一个营销活动的校验
	 * */
	public void checkSaleActive(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("TRADE_ID", data.getString("RELATION_TRADE_ID"));
        svcParam.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", data.getString("PACKAGE_ID"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset checkResult = CSViewCall.call(this, "SS.SaleActiveEndCheckSVC.checkSaleActiveEnd", svcParam);//早期的代码中就有，用于判断活动下预存和积分等
        
        /*
         * 核对哪些营销活动需要进行提示
         */
        IDataset checkIsNeedWarmResult = CSViewCall.call(this, "SS.SaleActiveEndCheckSVC.checkIsNeedWarm", svcParam);
        if(IDataUtil.isNotEmpty(checkIsNeedWarmResult)){
        	checkResult.getData(0).putAll(checkIsNeedWarmResult.getData(0));
        }
        
        this.setAjax(checkResult);
    }
	
	/**
	 * 点击某一个营销活动
	 * */
	public void loadActiveDetailInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("USER_ID", data.getString("USER_ID"));
        svcParam.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", data.getString("PACKAGE_ID"));
        svcParam.put("RELATION_TRADE_ID", data.getString("RELATION_TRADE_ID"));
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        IDataset saleActives = CSViewCall.call(this, "SS.SaleActiveEndSVC.querySaleActiveDetialInfos", svcParam);//获取营销活动下的优惠、服务、预存、实物
        if (IDataUtil.isNotEmpty(saleActives))
        {
            IData elementTradeData = saleActives.getData(0);
            setServs(elementTradeData.getDataset("SALE_SERVICE"));
            setDiscnts(elementTradeData.getDataset("SALE_DISCNT"));
            setGoods(elementTradeData.getDataset("SALE_GOODS"));
            setDeposits(elementTradeData.getDataset("SALE_DEPOSIT"));
            
            IData result=new DataMap();
            IDataset saleGoods = elementTradeData.getDataset("SALE_GOODS");
            if(IDataUtil.isEmpty(saleGoods)){
            	saleGoods = new DatasetList();
            }
            result.put("SALE_GOODS", saleGoods);
            
            //获取营销活动应收违约金
            //IDataset refundMoneyData = CSViewCall.call(this, "SS.SaleActiveEndSVC.calculateSaleActiveReturnMoney", svcParam);
            IData refundMoneyData = new DataMap();
            refundMoneyData.put("RESULT_TIP", "0");
            refundMoneyData.put("RESULT_TIP_INFO", "");
            refundMoneyData.put("REFUND_MONEY", "0");
    		
            result.put("REFUND_MONEY", refundMoneyData);
            
            result.put("ALERT_INFO", elementTradeData.getString("ALERT",""));
            
            this.setAjax(result);
        }
    }

	/**
	 * 提交
	 * */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        //如果存在退费，封装操作费用
        String returnFee=data.getString("RETURNFEE","0");
        BigDecimal returnFeeD=new BigDecimal(returnFee);
        int finalReturnFee=returnFeeD.multiply(new BigDecimal(100)).intValue();
        
        IData svcParam = new DataMap();
        svcParam.put("CHECK_MODE", data.getString("CHECK_MODE"));
        svcParam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        svcParam.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        svcParam.put("PACKAGE_ID", data.getString("PACKAGE_ID"));
        svcParam.put("RELATION_TRADE_ID", data.getString("RELATION_TRADE_ID"));
        svcParam.put("CAMPN_TYPE", data.getString("CAMPN_TYPE"));
        svcParam.put("REMARK", data.getString("REMARK"));
        svcParam.put("RETURNFEE",finalReturnFee);
        svcParam.put("INTERFACE", "1");
        svcParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE"));
        svcParam.put("END_DATE_VALUE", "9");//吴丽甘要求，删除终止时间选项，活动时间截止到当前，优惠时间截止到月底，采用一个没有的、无用的END_DATE_VALUE=9即可
        svcParam.put("SRC_PAGE", "1");//营销活动终止查询要求分辨终止页面来源
        
        if(finalReturnFee>0){
        	IData tradeFeeSub=new DataMap();
        	tradeFeeSub.put("TRADE_TYPE_CODE", "237");
        	tradeFeeSub.put("FEE_TYPE_CODE", "602");
        	tradeFeeSub.put("FEE", finalReturnFee);
        	tradeFeeSub.put("OLDFEE", finalReturnFee);
        	tradeFeeSub.put("FEE_MODE", "0");	//营业费用
        	tradeFeeSub.put("ELEMENT_ID", "");
        	
        	IData tradePayMoney=new DataMap();
        	tradePayMoney.put("PAY_MONEY_CODE", "0");
        	tradePayMoney.put("MONEY", finalReturnFee);
        	
        	IDataset tradeFeeSubs=new DatasetList();
        	tradeFeeSubs.add(tradeFeeSub);
        	
        	IDataset tradePayMoneys=new DatasetList();
        	tradePayMoneys.add(tradePayMoney);
        	
        	
        	svcParam.put("X_TRADE_FEESUB", tradeFeeSubs);
        	svcParam.put("X_TRADE_PAYMONEY", tradePayMoneys);
        }
        
        
        IDataset saleActives = CSViewCall.call(this, "SS.SaleActiveEndRegSVC.tradeReg", svcParam);
        setAjax(saleActives);
    }

    public abstract void setCustInfo(IData custInfo);

    public abstract void setDeposits(IDataset deposits);

    public abstract void setDiscnts(IDataset discnts);

    public abstract void setGoods(IDataset goods);

    public abstract void setInfos(IDataset infos);

    public abstract void setServs(IDataset servs);

}
