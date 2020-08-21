
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.print;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeCustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceiptInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.KjPrintBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.NotePrintBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;
import com.asiainfo.veris.crm.order.soa.person.busi.unpaidorderdeal.UnpaidOrderDealBean;

public class PrintNoteSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = Logger.getLogger(PrintNoteSVC.class);

    public IDataset getCnoteInfo(IData input) throws Exception
    {
    	IDataset reDs = new DatasetList();
    	if("changeuserinfo.ModifyEPostInfo".equals(input.getString("page", "")))
    	{
    		//modify hefeng  UNION ALL 语句拆分
    		IDataset conteInfos=TradeReceiptInfoQry.getCnoteInfoByTradeId2016_1(input.getString("TRADE_ID"));
    		IDataset custInfo=TradeReceiptInfoQry.getCnoteInfoByTradeId2016_2(conteInfos.getData(0).getString("CUST_ID"));
    		
    		String band_name=UBrandInfoQry.getBrandNameByBrandCode(conteInfos.getData(0).getString("BRAND_CODE"));
    		String  trade_staffname=UStaffInfoQry.getStaffNameByStaffId(conteInfos.getData(0).getString("TRADE_STAFF_ID"));
    		String  depart_name=UDepartInfoQry.getDepartFrameByDepartId(conteInfos.getData(0).getString("ORG_INFO"));
    		conteInfos.getData(0).put("BRAND_NAME", band_name);
    		conteInfos.getData(0).put("TRADE_STAFF_NAME", trade_staffname);
    		conteInfos.getData(0).put("ORG_NAME", depart_name);
    		
    		if(custInfo.size()<1){
    			IDataset custHisInfo=TradeReceiptInfoQry.getCnoteInfoByTradeId2016_3(input.getString("TRADE_ID"),conteInfos.getData(0).getString("CUST_ID"));
    			
    			conteInfos.addAll(custHisInfo);
    		}else{
    			
    			conteInfos.addAll(custInfo);
    		}
    		
    		IDataset conteInfotarde=TradeReceiptInfoQry.getCnoteInfoByTradeId2016_1(input.getString("TRADE_ID"));
    		IDataset custInfos=TradeReceiptInfoQry.getCnoteInfoByTradeId2016_2(conteInfotarde.getData(0).getString("CUST_ID"));
    		if(custInfos.size()>0){
    			String band_names=UBrandInfoQry.getBrandNameByBrandCode(conteInfotarde.getData(0).getString("BRAND_CODE"));
        		String  trade_staffnames=UStaffInfoQry.getStaffNameByStaffId(conteInfotarde.getData(0).getString("TRADE_STAFF_ID"));
        		String  depart_names=UDepartInfoQry.getDepartFrameByDepartId(conteInfotarde.getData(0).getString("ORG_INFO"));
        		conteInfotarde.getData(0).put("BRAND_NAME", band_names);
        		conteInfotarde.getData(0).put("TRADE_STAFF_NAME", trade_staffnames);
        		conteInfotarde.getData(0).put("ORG_NAME", depart_names);
        		conteInfotarde.addAll(custInfos);
        		//如果有值，把两个结果集合并
        		conteInfos.addAll(conteInfotarde);
    		}
    		
    		
//    		return TradeReceiptInfoQry.getCnoteInfoByTradeId2016(input.getString("TRADE_ID")); 
    		reDs.addAll(conteInfos) ;
    	}else{
    		reDs = TradeReceiptInfoQry.getCnoteInfoByTradeId(input.getString("TRADE_ID"));
    		
    		String tradeId = input.getString("TRADE_ID");
			String eparchyCode = input.getString("TRADE_ID");
    		IData tradeInfo = UTradeInfoQry.qryTradeByTradeId(tradeId, "0", eparchyCode);
			if(IDataUtil.isEmpty(tradeInfo))
			{
				tradeInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "0", eparchyCode);
			}
			if(IDataUtil.isNotEmpty(tradeInfo))
			{
				//购物车
	    		if(IDataUtil.isEmpty(reDs))
	    		{
	    			if(StringUtils.equals("5178", tradeInfo.getString("TRADE_TYPE_CODE")))
	    			{
	    				IData data = new DataMap();
	        			data.put("TRADE_ID", tradeId);
	    				data.put("TRADE_TYPE_CODE", tradeInfo.getString("TRADE_TYPE_CODE"));
	    				data.put("NOTE_TYPE", "1");
	    				data.put("ACCEPT_DATE", tradeInfo.getString("ACCEPT_DATE"));
	    				data.put("USER_ID", tradeInfo.getString("USER_ID"));
	    				data.put("SERIAL_NUMBER", tradeInfo.getString("SERIAL_NUMBER"));
	    				data.put("CUST_NAME", tradeInfo.getString("CUST_NAME"));
	    				data.put("BRAND_CODE", tradeInfo.getString("BRAND_CODE"));
	    				data.put("CUST_ID", tradeInfo.getString("CUST_ID"));
	    				data.put("VERIFY_MODE", tradeInfo.getString(""));
	    				data.put("TRADE_STAFF_ID", tradeInfo.getString("TRADE_STAFF_ID"));
	    				data.put("ORG_INFO", tradeInfo.getString("TRADE_DEPART_ID"));
	    				
	    				data.put("BRAND_NAME", UpcCall.queryBrandNameByChaVal(tradeInfo.getString("BRAND_CODE")));
	    				data.put("ORG_NAME", UDepartInfoQry.getDepartFrameByDepartId(tradeInfo.getString("TRADE_DEPART_ID")));
	    				data.put("TRADE_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(tradeInfo.getString("TRADE_STAFF_ID")));
	    				data.put("ORDER_ID", tradeInfo.getString("ORDER_ID"));
	    				
	    				String custId = tradeInfo.getString("CUST_ID");
	                    IDataset customerDataset = CustomerInfoQry.getCustomerByCustID(custId);
	                    if (IDataUtil.isEmpty(customerDataset))
	                    {
	                        customerDataset = TradeCustomerInfoQry.getTradeCustomerByTradeId(tradeId);
	                    }
	                    if (IDataUtil.isNotEmpty(customerDataset))
	                    {
	                    	data.put("ID_CARD", customerDataset.getData(0).getString("PSPT_ID"));
	                    }
	                    
//	                  StringBuilder sb = new StringBuilder();
//	    				sb.append("受理员工：").append(data.getString("TRADE_STAFF_NAME")).append(" ").append(data.getString("TRADE_STAFF_ID")).append(" ");
//	    				sb.append("业务受理时间：").append(data.getString("ACCEPT_DATE")).append(" ");
//	    				sb.append("~~业务类型：购物车").append("   ");
//	    				sb.append("受理方式:未知认证").append(" ");
//	    				data.put("RECEIPT_INFO1", tradeInfo.getString(""));
//	    				data.put("RECEIPT_INFO2", tradeInfo.getString(""));
//	    				data.put("RECEIPT_INFO3", tradeInfo.getString(""));
//	    				data.put("RECEIPT_INFO4", tradeInfo.getString(""));
//	    				data.put("RECEIPT_INFO5", tradeInfo.getString(""));
//	    				data.put("NOTICE_CONTENT", tradeInfo.getString(""));
	                    
	    				reDs.add(data);
	    			}
	    		}else {
	    			reDs.getData(0).put("ORDER_ID", tradeInfo.getString("ORDER_ID"));
	    		}
			}
    	}
    	
    	//2016111610405400202251_REQ201611070014电子化存储新版客户兼容性优化
        IDataset otherTradeDs = TradeReceiptInfoQry.getCnoteOtherTradeInfoByTradeId(input.getString("TRADE_ID"));
        if (otherTradeDs != null && otherTradeDs.size() > 0) {
        	
        	for(int i=0,size=otherTradeDs.size();i<size;i++)
        	{
        		IData temp = otherTradeDs.getData(i);
        		temp.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(temp.getString("BRAND_CODE")));
        		temp.put("TRADE_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(temp.getString("TRADE_STAFF_ID")));
        		temp.put("ORG_NAME", UDepartInfoQry.getDepartNameByDepartId(temp.getString("ORG_INFO")));
        		
        		String tradeId = temp.getString("TRADE_ID");
        		String custId = temp.getString("CUST_ID");
        		
        		IData customer = UcaInfoQry.qryCustomerInfoByCustId(custId);
        		if(IDataUtil.isNotEmpty(customer))
        		{
        			temp.put("ID_CARD", customer.getString("PSPT_ID"));
        		}
        		else
        		{
        			IDataset tradeCustomers = TradeCustomerInfoQry.getTradeCustomerByTradeId(tradeId);
        			if(IDataUtil.isNotEmpty(tradeCustomers))
            		{
        				temp.put("ID_CARD", tradeCustomers.getData(0).getString("PSPT_ID"));
            		}
        			else
        			{
        				temp.put("ID_CARD", "0");
        			}
        		}
        	}
        	
            reDs.add(otherTradeDs);
        }
        
        /**
		 * REQ201805170033_在线公司电子稽核报表优化需求
		 * @author zhuoyingzhi
		 * @date 20180614
		 */
        if(IDataUtil.isNotEmpty(reDs)){
        	IData reDinfo=reDs.getData(0);
    		String  tradeTypeCode=reDinfo.getString("TRADE_TYPE_CODE", "");
    		String  tradeType=UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
    		//往原来的集合里面添加
    		reDs.getData(0).put("TRADE_TYPE", tradeType);//业务类型名称
        }
        logger.debug("----PrintNoteSVC.java-----reDs:"+reDs);
		/****************REQ201805170033_在线公司电子稽核报表优化需求_end**********************************/
		
        return reDs;
    }

    /**
     * 获取普通打印数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getPrintData(IData input) throws Exception
    {
        ReceiptNotePrintMgr notPrint = new ReceiptNotePrintMgr();
        return notPrint.printTradeReceipt(input);
    }

    /**
     * 根据员工ID获取单联票据数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getStaffPrintTicket(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
        returnSet.add(bean.getStaffPrintTicket(input));
        return returnSet;
    }

    /**
     * 登记票据日志
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset regPrintTicketLog(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
        returnSet.add(bean.regPrintTicketLog(input));
        return returnSet;
    }

    /**
     * 更新台账打印标记
     * 
     * @param input
     *            [TRADE_ID,PRT_TAG]
     * @return
     * @throws Exception
     */
    public IDataset updataPrintTag(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        IData data = new DataMap();
        NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
        data.put("PRT_TAG_RESULT", bean.updataPrintTag(input));
        returnSet.add(data);
        return returnSet;
    }
    
    /**
     * 返回生成但是没有打印的记录
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getNotPrintedInfo(IData input) throws Exception
    {
        NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
        return bean.getNotPrintedInfo(input);
    }
    
    /**
     * 记录受理单解析报错日记
     * 
     * @param input
     *            [TRADE_ID,ERROR_DATA]
     * @author wukw3
     * @return
     * @throws Exception
     */
    public IDataset errorLog(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();
        IData data = new DataMap();
        NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
        data.put("RESULT", bean.errorLog(input));
        returnSet.add(data);
        return returnSet;
    }
    
    
    /**
     * 打印开具电子发票接口。
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset printKJ(IData input) throws Exception
    {
 	   if (logger.isDebugEnabled() && null != input) {
 		   logger.debug("----PrintNoteSVC.java#printKJ----in params:----" + input.toString());
 	   }
 	   
 	   KjPrintBean bean = BeanManager.createBean(KjPrintBean.class);
        IDataset rs = bean.printKJ(input);  
        
        if (logger.isDebugEnabled() && null != rs) {
 		   logger.debug("----PrintNoteSVC.java#printKJ----result:----" + rs.toString());
 	   }
        
        return rs;
    }
    
    /** 
     * 校验是否已经开具打印
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset hadPrintedKJ(IData input) throws Exception {
 	   if (logger.isDebugEnabled() && null != input) {
 		   logger.debug("----PrintNoteSVC.java#hadPrintedKJ----in params:----" + input.toString());
 	   }
 	   
 	   KjPrintBean bean = BeanManager.createBean(KjPrintBean.class);
        IDataset rs = bean.hadPrintedKJ(input);  
        
        if (logger.isDebugEnabled() && null != rs) {
 		   logger.debug("----PrintNoteSVC.java#hadPrintedKJ----result:----" + rs.toString());
 	   }
        
        return rs;
    }
    
    /** 获取业务类型是否支持营业电子发票设置 */
    public IDataset getTradeTypeKjConf(IData input) throws Exception {
 	   if (logger.isDebugEnabled() && null != input) {
 		   logger.debug("----PrintNoteSVC.java#getTradeTypeKjConf----in params:----" + input.toString());
 	   }
 	   
 	   KjPrintBean bean = BeanManager.createBean(KjPrintBean.class);
        IDataset rs = bean.isNeedKjSetiByTradeType(input);  
        
        if (logger.isDebugEnabled() && null != rs) {
 		   logger.debug("----PrintNoteSVC.java#getTradeTypeKjConf----result:----" + rs.toString());
 	   }
        
        return rs;
    }
	
	    public IData changePrintPDFlog(IData input) throws Exception
    {
    	IData result = new DataMap();
        NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
        bean.changePrintPDFlog(input);
        
        result.put("X_RESULTCODE", "OK");
        result.put("X_RESULTINFO", "OK");
        
        return result;
    }
	    
	    public IDataset checkReceiptAndSetTradeState(IData inParam)throws Exception
	    {
	    	 NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
	    	 return bean.checkReceiptAndSetTradeState(inParam);
	    }
	    
	    public IDataset updateTradeState(IData inParam)throws Exception
	    {
	    	 NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
	    	 return bean.updateTradeState(inParam);
	    }
	    
	    public IDataset updateOrderState(IData inParam)throws Exception
	    {
	    	 NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
	    	 return bean.updateOrderState(inParam);
	    }
	    
	    public IDataset cancelOrder(IData inParam)throws Exception
	    {
	    	UnpaidOrderDealBean bean = BeanManager.createBean(UnpaidOrderDealBean.class);
	    	 return bean.cancelOrder(inParam);
	    }
	    
	    /**
	     * 获取是否存在照片ID标志
	     * 
	     * @param input
	     *            [TRADE_ID,PRT_TAG]
	     * @return
	     * @throws Exception
	     */
	    public IDataset getPicTag(IData input) throws Exception
	    {
	    	if (logger.isDebugEnabled() && null != input) {
	    		logger.debug("----PrintNoteSVC.java#getPicTag----in params:----" + input.toString());
	    	}
	    	IDataset returnSet = new DatasetList();
	    	NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
	    	IData res = bean.getPicTag(input);	    	

	    	returnSet.add(res);

	    	if (logger.isDebugEnabled() && null != returnSet) {
	    		logger.debug("----PrintNoteSVC.java#getPicTag----result:----" + returnSet.toString());
	    	}
	    	return returnSet;
	    }
	    
	    /**
	     * REQ201805090016_在线公司电子稽核报表优化需求
	     * @author zhuoyingzhi
	     * @date 20180518
	     * @param input
	     * @return
	     * @throws Exception
	     */
	    public IDataset isMainProductChange(IData input) throws Exception
	    {
	    	if (logger.isDebugEnabled() &&  input !=null) {
	    		logger.debug("----PrintNoteSVC.java#isMainProductChange----in params:----" + input.toString());
	    	}
	    	IDataset returnSet = new DatasetList();
	    	NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
	    	IData res = bean.isMainProductChange(input);	    	

	    	returnSet.add(res);

	    	if (logger.isDebugEnabled() && returnSet !=null) {
	    		logger.debug("----PrintNoteSVC.java#isMainProductChange----result:----" + returnSet.toString());
	    	}
	    	return returnSet;
	    }
	    /**
	     * REQ201805090016_在线公司电子稽核报表优化需求
	     * <br/>
	     * IS_HYYYK_BAT_CHOPEN  1:是行业应用卡    0：非行业应用卡
	     * <br/>
	     * 判断是否为行业应用卡
	     * @author zhuoyingzhi
	     * @date 20180523
	     * @param input
	     * @return
	     * @throws Exception
	     */
	    public IDataset isHyyykBatChopen(IData input) throws Exception
	    {
	    	if (logger.isDebugEnabled() && input!=null) {
	    		logger.debug("----PrintNoteSVC.java#isHyyykBatChopen----in params:----" + input);
	    	}
	    	IDataset returnSet = new DatasetList();
	    	NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
	    	IData result = bean.isHyyykBatChopen(input);	    	
	    	returnSet.add(result);
	    	if (logger.isDebugEnabled() && returnSet !=null) {
	    		logger.debug("----PrintNoteSVC.java#isHyyykBatChopen----result:----" + returnSet);
	    	}
	    	return returnSet;
	    }
	    /**
	     * k3
	     * 跨区销户身份证正反面查询
	     * @param input
	     * @return
	     * @throws Exception
	     */
	    public IDataset qryPsptFrontBack(IData input)throws Exception{
	    	IDataset returnSet = new DatasetList();
	    	NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
	    	IData result = bean.qryPsptFrontBack(input);	    	
	    	returnSet.add(result);
	    	if (logger.isInfoEnabled()) {
	    		logger.info("----PrintNoteSVC.java#qryPsptFrontBack----result:----" + returnSet);
	    	}
	    	return returnSet;
	    }

	/**
	 * REQ201910180018押金业务受理及清退电子化存储需求
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset qryForegiftTrade(IData input)throws Exception{
		IDataset returnSet = new DatasetList();
		NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
		IData result = bean.qryForegiftTrade(input);
		returnSet.add(result);
		if (logger.isInfoEnabled()) {
			logger.info("----PrintNoteSVC.java#qryForegiftTrade----result:----" + returnSet);
		}
		return returnSet;
	}
}
