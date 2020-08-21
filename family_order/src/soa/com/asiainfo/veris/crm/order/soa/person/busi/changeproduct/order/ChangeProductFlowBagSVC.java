package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.proxy.BuilderProxy;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.trade.SpecialChangeProductTrade;

/**    
 * Copyright: Copyright  2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductRegNoRuleSVC.java
 * @Description: 产品变更无规则校验登记服务类
 *
 * @version: v1.0.0
 * @author: maoke
 * @date: Oct 2, 2014 10:34:15 AM 
 *
 * Modification History:
 * Date            Author      Version        Description
 *-------------------------------------------------------*
 * Oct 2, 2014    maoke       v1.0.0           修改原因	
 */
public class ChangeProductFlowBagSVC extends OrderService
{
    @Override
    public String getOrderTypeCode() throws Exception
    {
        return input.getString("ORDER_TYPE_CODE", input.getString("TRADE_TYPE_CODE", "110"));
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return input.getString("TRADE_TYPE_CODE", "110");
    }
    
    /**
     * 重写不校验规则
     */
    public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
    {
        
    }
    
    

	/**
	 * 调用build,trade,action层的处理
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public BusiTradeData trade(IData input) throws Exception
	{
		input.put("TRADE_TYPE_CODE", getTradeTypeCode());
		return acceptOrder(input);
	}
    
	public static BusiTradeData acceptOrder(IData param) throws Exception
	{
		long beginTime = 0;

		OrderDataBus dataBus = DataBusManager.getDataBus();

		String tradeTypeCode = param.getString("TRADE_TYPE_CODE");

		// 构建rd
		IBuilder rdb = BuilderProxy.getInstance(tradeTypeCode, dataBus.getOrderTypeCode(), param);

		beginTime = System.currentTimeMillis();
		if (log.isDebugEnabled())
		{
			log.debug("开始构建请求对象");
		}

		BaseReqData rd = rdb.buildRequestData(param);
		if (log.isDebugEnabled())
		{
			log.debug("构建请求对象 cost time:" + (System.currentTimeMillis() - beginTime) / 1000.0D + "s");
		}

		SpecialChangeProductTrade  tradebean = new SpecialChangeProductTrade();
		
		BusiTradeData bd = tradebean.createTrade(rd);
		//将购买流量包的集团用户的user_id写入user_id_a（便将套餐的功能费收取到user_id_a对应的集团用户上）
		List<DiscntTradeData> discntTradeDatas = bd.get(TradeTableEnum.TRADE_DISCNT.getValue());
		if(discntTradeDatas!=null && discntTradeDatas.size()>0){
			for (DiscntTradeData each : discntTradeDatas) {
				
				each.setUserIdA(param.getString("USER_ID_A", ""));		
			}
		}

		DataBusManager.getDataBus().addBusiTradeData(bd);

		return bd;
	} 
	
}
