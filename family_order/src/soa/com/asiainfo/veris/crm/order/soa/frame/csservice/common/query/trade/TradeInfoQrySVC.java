 
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.service.route.impl.RouteByStaffId;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.PrintAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.NotePrintBean;

public class TradeInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 根据ATTR_VALUE查询TF_B_TRADE_EXT信息 chenyi
     */
    public static IDataset getTradeBysubscribeIdForEsop(IData input) throws Exception
    {

        return TradeInfoQry.getTradeBysubscribeIdForEsop(input.getString("ATTR_VALUE"));

    }

    /**
     * chenyi 根据order_id查询tradeInfo
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getTradeInfobyOrd(IData input) throws Exception
    {

        return TradeInfoQry.getTradeInfobyOrd(input.getString("ORDER_ID"));

    }

    /**
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset delTradeRsrvTag10(IData input) throws Exception
    {
        TradeInfoQry.delTradeRsrvTag10(input.getString("ORDER_ID"), input.getString("RSRV_STR10"));
        return null;
    }

    public IDataset getMainTrade(IData input) throws Exception
    {
        String userId = input.getString("UESR_ID");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        IDataset data = TradeInfoQry.getMainTradeByUserIdTypeCode(userId, tradeTypeCode);
        return data;
    }

    /**
     * 根据userId、trade_type_code查询用户trade信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getMainTradeByCond(IData input) throws Exception
    {
        String userIdA = input.getString("USER_ID_A");
        String userIdB = input.getString("USER_ID_B");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        IDataset data = TradeInfoQry.getMainTradeByCond(userIdA, userIdB, tradeTypeCode);
        return data;
    }

    public IDataset getMainTradeByRG(IData input) throws Exception
    {
        String userID = input.getString("USER_ID");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        IDataset dataset = TradeInfoQry.getMainTradeByUserIdTypeCode(userID, tradeTypeCode);
        return dataset;
    }

    public IDataset getMainTradeBySN(IData inparam) throws Exception
    {
        return TradeInfoQry.getMainTradeBySN(inparam.getString("SERIAL_NUMBER"), inparam.getString("TRADE_TYPE_CODE"));
    }

    /**
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset getMainTradeForGrp(IData input) throws Exception
    {
        IDataset data = TradeInfoQry.getMainTradeByUserIdTypeCode(input.getString("USER_ID"), input.getString("TRADE_TYPE_CODE"));
        return data;
    }

    public IDataset getNoTrade(IData input) throws Exception
    {
        IDataset data = TradeInfoQry.getNoTrade(input);
        return data;
    }

    /**
     * 根据userId、trade_type_code查询用户trade信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getTradeByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");
        IDataset data = TradeInfoQry.getMainTradeByUserIdTypeCode(userId, tradeTypeCode);
        return data;
    }

    /**
     * 查询ESOP侧的台账信息 chenyi
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getTradeForGrpBBoss(IData input) throws Exception
    {
        IDataset data = TradeInfoQry.getTradeForGrpBBoss(input.getString("TRADE_ID"));
        return data;
    }

    /**
     * 更加orderId查询trade表记录
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryTradeInfoByOrderId(IData input) throws Exception
    {
        String orderId = input.getString("ORDER_ID");
        String routeId = input.getString("EPARCHY_CODE");

        return UTradeInfoQry.qryTradeByOrderId(orderId, routeId);
    }

    /**
     * chenyi 查看预受理阶段的trade数据
     * 
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset queryBbossTradeByEsop(IData input) throws Exception
    {

        String product_id = input.getString("PRODUCT_ID");
        String group_id = input.getString("GROUP_ID");
        String uids = input.getString("UIDS");
        return TradeInfoQry.queryBbossTradeByEsop(product_id, group_id, uids);

    }

    public IDataset queryCancelTradeByTradeId(IData input) throws Exception
    {
        String tradeId = input.getString("TRADE_ID");
        String cancelTag = input.getString("CANCEL_TAG");

        IDataset dataset = IDataUtil.idToIds(UTradeInfoQry.qryTradeByTradeId(tradeId, cancelTag, null));

        return dataset;
    }

    public IDataset queryInfosBhTrade(IData input) throws Exception
    {
        IDataset data = TradeInfoQry.queryInfosBhTrade(input, getPagination());

        if (IDataUtil.isNotEmpty(data))
        {
            int sz = data.size();
            IData iData = new DataMap();
            for (int i = 0; i < sz; i++)
            {
                iData = data.getData(i);
                iData.put("TRADE_TYPE_NAME", UTradeTypeInfoQry.getTradeTypeName(iData.getString("TRADE_TYPE_CODE")));
                iData.put("STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(iData.getString("TRADE_STAFF_ID")));
            }
        }

        return data;
    }

    public IDataset queryInfosTrade(IData input) throws Exception
    {
        IDataset dataset = TradeInfoQry.queryInfosTrade(input, getPagination());

        if (IDataUtil.isNotEmpty(dataset))
        {
            int sz = dataset.size();

            IData iData = new DataMap();

            for (int i = 0; i < sz; i++)
            {
                iData = dataset.getData(i);
                iData.put("TRADE_TYPE_NAME", UTradeTypeInfoQry.getTradeTypeName(iData.getString("TRADE_TYPE_CODE")));
                iData.put("STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(iData.getString("TRADE_STAFF_ID")));
            }
        }

        return dataset;
    }

    /**
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset queryTradeByOrderProduct(IData input) throws Exception
    {
        return TradeInfoQry.queryTradeByOrderProduct(input.getString("ORDER_ID"), input.getString("PRODUCT_ID"), input.getString("CANCEL_TAG"));
    }

    /**
     * 根据集团号码查询订单
     * 
     * @author fengsl
     * @date 2013-04-19
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryTradeBySnGrp(IData input) throws Exception
    {
        String trade_type_code = input.getString("TRADE_TYPE_CODE");
        String serial_number = input.getString("SERIAL_NUMBER");
        IDataset data = TradeInfoQry.queryTradeBySnGrp(trade_type_code, serial_number, null, getRouteId());
        return data;
    }

    /**
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset queryTradeSet(IData input) throws Exception
    {
        String trade_id = input.getString("TRADE_ID");
        IDataset data = TradeInfoQry.queryTradeSet(trade_id, null);
        return data;
    }

    /**
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset updateTradeRsrvStr10(IData input) throws Exception
    {
        TradeInfoQry.updateTradeRsrvStr10(input);
        return null;
    }

    /**
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset updateTradeState(IData input) throws Exception
    {
        TradeInfoQry.updateTradeState(input.getString("TRADE_ID"), input.getString("SUBSCRIBE_TYPE"));
        return null;
    }

    /**
     * @param inparams
     * @param page
     * @author weixb3
     * @return
     * @throws Exception
     */
    public IDataset updateTradeStateByPK(IData input) throws Exception
    {
        String tradeId = input.getString("ORDER_ID");
        String subscribe_state = input.getString("SUBSCRIBE_STATE");

        TradeInfoQry.updateTradeStateByPK(tradeId, subscribe_state);
        return null;
    }
    
    public IData checkAndUpdateCnoteTagForENote(IData input)throws Exception{
    	IData result=new DataMap();
    	
    	String tradeId=input.getString("TRADE_ID","");
    	
    	if(tradeId==null||tradeId.equals("")){
    		result.put("X_RESULTECODE", "2999");
    		result.put("RESULT_INFO", "订单ID不能为空！");
    		
    		return result;
    	}
    	IDataset tradeInfo = TradeInfoQry.queryTradeByTradeId(tradeId, Route.getJourDb());
    	if(IDataUtil.isEmpty(tradeInfo))
    	{
    		tradeInfo = TradeHistoryInfoQry.queryByTradeId(tradeId, Route.getJourDb());
    	}
    	if(IDataUtil.isEmpty(tradeInfo))
    	{
    		/*
    		 * REQ201806190020_新增行业应用卡批量开户人像比对功能
    		 * by mqx 20181023
    		 * 批开导入数据后打印保存电子工单，把TF_B_TRADE_CNOTE_INFO表RSRV_TAG2字段改为1
    		 */
    		IDataset dataset = BatDealInfoQry.qryBatDealByBatchId(tradeId ,null);
        	if((!IDataUtil.isEmpty(dataset))&&dataset.size()>0){
        		TradeReceiptInfoQry.updatePrintNotByTradeId(tradeId,"1");
        		result.put("X_RESULTECODE", "0");
        		result.put("RESULT_INFO", "修改成功！");
        		
        		return result;
        	}
        	
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据TRADE_ID=["+tradeId+"]未找到订单状态！");
    	}
    	//更新整个order为已打印状态
    	String orderId = tradeInfo.getData(0).getString("ORDER_ID");
    	IData inParam = new DataMap();
    	inParam.put("ORDER_ID", orderId);
    	NotePrintBean bean = BeanManager.createBean(NotePrintBean.class);
    	bean.updateOrderState(inParam);
    	
    	IDataset allTrades = TradeInfoQry.queryTradeByOrerId(orderId, "0");
    	IDataset allHTrades = TradeHistoryInfoQry.qryTradeByOrderId(orderId, "0", BizRoute.getRouteId());
    	allTrades.addAll(allHTrades);
    	if(IDataUtil.isNotEmpty(allTrades))
    	{
    		for(int i=0;i<allTrades.size();i++)
    		{
    			IData temp = allTrades.getData(i);
    			String temTradeId = temp.getString("TRADE_ID");
    			
    			IDataset trades=TradeReceiptInfoQry.queryPrintNotByTradeId(temTradeId);
            	if(IDataUtil.isNotEmpty(trades)){
            		IData trade=trades.getData(0);
            		String rsrvTag2=trade.getString("RSRV_TAG2","");
            		
            		if(!(rsrvTag2!=null&&rsrvTag2.equals("1"))){
            			TradeReceiptInfoQry.updatePrintNotByTradeId(temTradeId,"1");
            		}
            		PrintAction.action(temp);
            	}
    		}
    	}
    	else
    	{
    		result.put("X_RESULTECODE", "2999");
    		result.put("RESULT_INFO", "根据订单ID无法查询到订单信息！");
    		
    		return result;
    	}
    	result.put("X_RESULTECODE", "0");
		result.put("RESULT_INFO", "修改成功！");
		
		return result;
    }
    
    public static IDataset queryTradeByRsrvstr1(IData input) throws Exception
    {

        return TradeInfoQry.queryTradeByRsrvstr1(input);

    }
    
}
