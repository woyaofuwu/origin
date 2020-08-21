
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.flow.FlowInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;


public class GRPFlowPackDiscntFinishAction implements ITradeFinishAction
{

	private static transient Logger logger = Logger.getLogger(GRPFlowPackDiscntFinishAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
    	String tradeId = mainTrade.getString("TRADE_ID");
    	IDataset discntTrades = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
    	if (IDataUtil.isNotEmpty(discntTrades))
        {
    		for (int i=0;i<discntTrades.size();i++)
            {
    			IData each = discntTrades.getData(i);
                String discntCode = each.getString("DISCNT_CODE", "");
                String modifyTag = each.getString("MODIFY_TAG", "");
                String userIda = each.getString("USER_ID_A", "");
                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) && "52000008".equals(discntCode))
                {
                	//对集团用户的账户进行充值缴费
                    IDataset transFees = FlowInfoQry.queryGrpFlowPackTransFee(tradeId, userIda);
                    if(IDataUtil.isNotEmpty(transFees))
                    {
                    	IData input = transFees.getData(0);
                    	input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));//集团用户SERIAL_NUMBER
                    	input.put("USER_ID", input.getString("GRP_USER_ID"));		//集团用户user_id
                    	input.put("CHANNEL_ID", "0");
                    	input.put("PAYMENT_ID", "5566");
                    	input.put("PAY_FEE_MODE_CODE", "151");
                    	input.put("DEPOSIT_CODE", "5566");
                    	input.put("SAIL_VALUE", input.getString("SAIL_VALUE"));		//总销售金额(单位:分)
                    	input.put("FACE_VALUE", input.getString("FACE_VALUE"));		//总卡面金额(单位:分)
                    	input.put("OUTER_TRADE_ID", tradeId);
                    	input.put("TRADE_EPARCHY_CODE", "0898");// 受理地州
                    	input.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());// 路由地州
                    	input.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                    	input.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    	input.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    	input.put("REMARK", "集团电子流量包充值缴费");
                    	input.put("PAYMENT_ID_P", "5567");
                    	input.put("PAY_FEE_MODE_CODE_P", "23");
                    	
                    	try 
                    	{
                    		IDataset result =  AcctCall.grpFlowPackTransFee(input);
						} catch (Exception e) {
							CSAppException.apperr(ProductException.CRM_PRODUCT_522,"调账务缴费接口[AM_ITF_RecvFeeCard]["+tradeId+"]["+userIda+"]失败:"+e.getMessage());
						}
                    }
                }
            }
        }
    }
}
