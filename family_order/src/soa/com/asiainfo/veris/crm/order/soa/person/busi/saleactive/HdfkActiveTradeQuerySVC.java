   
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.ticket.TicketInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.StateTaxUtil;

public class HdfkActiveTradeQuerySVC extends CSBizService
{

    public void dealHdfkActiveTrade(IData input) throws Exception
    {      
        String serialNumber = input.getString("SERIAL_NUMBER");
        String relationTradeId = input.getString("RELATION_TRADE_ID");
        String operTypeCode = input.getString("OPER_TYPE_CODE");

        IDataset result = UserSaleActiveInfoQry.queryHdfkSaleActive(serialNumber, null, null, null, relationTradeId, null, null);
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_45, serialNumber, relationTradeId);
        }

        IData hdfkActiveTrade = result.getData(0);
        if (!"0".equals(hdfkActiveTrade.getString("DEAL_STATE_CODE")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_46, relationTradeId);
        }

        if ("1".equals(operTypeCode))
        {
            // 撤销
            IData cond = new DataMap();
            cond.put("SERIAL_NUMBER", serialNumber);
            cond.put("RELATION_TRADE_ID", relationTradeId);
            cond.put("STAFF_ID", getVisit().getStaffId());
            Dao.executeUpdateByCodeCode("TF_F_USER_SALEACTIVE_BOOK", "UPD_CANCEL_HDFKACTIVE_TRADE", cond);
            
            String needCHReceipt = StateTaxUtil.needCHReceipt(relationTradeId);
            if(StringUtils.isNotEmpty(needCHReceipt))
            {
            	CSAppException.apperr(TicketException.CRM_TICKET_14, needCHReceipt);
            }
            
            //撤销票据
            IDataset ticketsInfo = TicketInfoQry.qryTradeTickets(relationTradeId);
            IData param = new DataMap();
    		for (int i = 0, size = ticketsInfo.size(); i < size; ++i) {//对于当前流水中有效的票据进行作废处理
    			// 调用票据作废服务
    			param.put("PRINT_ID", ticketsInfo.getData(i).getString("PRINT_ID",""));
    			param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getVisit().getStaffEparchyCode());
    			CSAppCall.call("SS.ReceiptZFSVC.submitZFReceipt", param);
    		}
        }
        else if ("2".equals(operTypeCode))
        {
            // 签收
            IData cond = new DataMap();
            String productId = hdfkActiveTrade.getString("PRODUCT_ID", "");
            String packageId = hdfkActiveTrade.getString("PACKAGE_ID", "");
            String resCode = hdfkActiveTrade.getString("RES_CODE", "-1"); // 终端串号
            String campnType = hdfkActiveTrade.getString("CAMPN_TYPE");

            if ("YX03".equals(campnType) || "YX07".equals(campnType) || "YX08".equals(campnType) || "YX09".equals(campnType))
            {
                if (StringUtils.isBlank(resCode) || resCode.equals("-1"))
                {
                    CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_47);
                }
            }
            else
            {
                resCode = "";
            }
            
            
            /*
             * 如果是网厅货到付款的方式，需要从比表TF_B_WT_HDFK_ELEMENTS里获取用户自定义的元素
             */
            String remark=hdfkActiveTrade.getString("REMARK","");
            if(!remark.equals("")){
            	if(remark.length()>10){
            		if(remark.substring(0, 6).equals("WTHDFK")){	//货到付款
            			String isHasElements=remark.substring(0,10);
            			String elementIds=remark.substring(10);
            			
            			IData personalElementsParam=new DataMap();
        	            personalElementsParam.put("HDFK_ELEMENTS_ID", elementIds);
        	            IDataset personalElementsSet=Dao.qryByCode("TF_B_WT_HDFK_ELEMENTS", 
        	            			"QRY_WT_HDFK_ELEMENTS_BY_ID", personalElementsParam, Route.CONN_CRM_CG);
            			
        	            if(personalElementsSet!=null&&personalElementsSet.size()>0){
        	            	if(isHasElements.equals("WTHDFK_YE_")){	//有自定义元素
        	            		String personalelements=personalElementsSet.getData(0).getString("ELEMENT_IDS","");
        	            		if(!personalelements.equals("")){
            	            		cond.put("ELEMENT_IDS", personalelements);
            	            	}
	            			}
        	            	cond.put("NET_ORDER_ID", personalElementsSet.getData(0).getString("ORDER_ID",""));	//货到付款处理的预约订单号
        	            }
        	            cond.put("WTSC_HDFK_SIGNUP", "1");		
            		}
            	}
            }
            
            
            // 调营销活动接口
            cond.put("SERIAL_NUMBER", serialNumber);
            cond.put("PRODUCT_ID", productId);
            cond.put("PACKAGE_ID", packageId);
            cond.put("TERMINAL_ID", resCode);
            cond.put("ACTION_TYPE", "3");
            IData intfResult = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", cond).getData(0);

            cond.clear();
            cond.put("SERIAL_NUMBER", serialNumber);
            cond.put("RELATION_TRADE_ID", relationTradeId);
            cond.put("STAFF_ID", getVisit().getStaffId());
            cond.put("DEPART_ID", getVisit().getDepartId());
            cond.put("TRADE_ID", intfResult.getString("TRADE_ID"));
            Dao.executeUpdateByCodeCode("TF_F_USER_SALEACTIVE_BOOK", "UPD_ACCEPT_HDFKACTIVE_TRADE", cond);
            
            //往TF_B_TICKET表插条记录，补打、作废、冲红界面都不能查到这个流水的记录
            String tradeId = intfResult.getString("TRADE_ID");
            IData ticketInsertData = new DataMap();
            IDataset feeSubList = TradefeeSubInfoQry.getFeeListByTrade(tradeId);
            for(int i = 0, size = feeSubList.size(); i < size; i++)
            {
            	IData feeSub = feeSubList.getData(i);
            	int fee = feeSub.getInt("FEE");
            	if(fee > 0)
            	{
            		String feeMode = feeSub.getString("FEE_MODE");
            		String key = "FEE_MODE_" + feeMode;
            		if(ticketInsertData.containsKey(key))
            		{
            			IData ticketData = ticketInsertData.getData(key);
            			ticketData.put("FEE", ticketData.getInt("FEE") + fee);
            		}
            		else
            		{
            			String instId = SeqMgr.getPrintId();
                		IData inparams = new DataMap();
                    	inparams.put("PRINT_ID", instId);
                        inparams.put("PARTITION_ID", tradeId.substring(tradeId.length() - 4));
                        inparams.put("TRADE_ID", tradeId);
                        inparams.put("ACCEPT_MONTH", tradeId.substring(5, 7));
                        inparams.put("ACCEPT_TIME", SysDateMgr.getSysTime());
                        inparams.put("FEE", fee);
                        inparams.put("TICKET_ID", "-1"); // 虚拟的
                        inparams.put("TAX_NO", "-1"); // 虚拟的
                        inparams.put("FEE_MODE", feeMode);
                        inparams.put("TRADE_TYPE_CODE", "240");
                        inparams.put("TRADE_STAFF_ID", getVisit().getStaffId());
                        inparams.put("TRADE_DEPART_ID", getVisit().getDepartId());
                        inparams.put("TRADE_CITY_CODE", getVisit().getCityCode());
                        inparams.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                        inparams.put("TICKET_TYPE_CODE", "D"); // 票据类别
                        inparams.put("SERIAL_NUMBER", serialNumber);
                        inparams.put("USER_ID", feeSub.getString("USER_ID", ""));
                        inparams.put("TICKET_STATE_CODE", "0"); // 发票状态
                        inparams.put("RSRV_STR1", relationTradeId);
                        inparams.put("REMARK", "货到付款签收虚拟打票记录");
                        
                        ticketInsertData.put(key, inparams);
            		}
            	}
            }
            
            if(IDataUtil.isNotEmpty(ticketInsertData))
            {
            	Iterator lter = ticketInsertData.keySet().iterator();

                while (lter.hasNext())
                {
                	String key = (String) lter.next();
                	Dao.insert("TF_B_TICKET", ticketInsertData.getData(key), Route.getJourDb());
                }
            }
        }
    }

    public IDataset queryTrade(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String startDate = input.getString("START_DATE");
        String endDate = input.getString("END_DATE");
        String tradeStaffId = input.getString("QUERY_STAFF_ID");
        String tradeId = input.getString("TRADE_ID");
        String dealStateCode = input.getString("DEAL_STATE_CODE");

        IDataset result = UserSaleActiveInfoQry.queryHdfkSaleActive(serialNumber, startDate, endDate, tradeStaffId, tradeId, dealStateCode, getPagination());

        return result;
    }
}
