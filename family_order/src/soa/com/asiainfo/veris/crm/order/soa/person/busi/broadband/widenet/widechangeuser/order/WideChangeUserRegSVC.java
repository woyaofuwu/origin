
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widechangeuser.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widechangeuser.WideChangeUserCheckBean;

public class WideChangeUserRegSVC extends OrderService
{

    @Override
    public String getOrderTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "640";
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        // TODO Auto-generated method stub
        return "640";
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        orderData.setSubscribeType("300");
    }

    public IDataset tradeReg(IData input) throws Exception
    {
        if (!"KD_".equals(input.getString("SERIAL_NUMBER").substring(0, 3)))
        {
            input.put("SERIAL_NUMBER", "KD_" + input.getString("SERIAL_NUMBER"));
        }
        return super.tradeReg(input);
    }
    
    @SuppressWarnings("unchecked")
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
	{ 
    	String saleActiveTag = input.getString("SALE_ACTIVE_TAG","");
    	if("1".equals(saleActiveTag))
    	{   
    		tradeSaleActiveTrans(input, btd);
    	} 
    	IData tempparam=new DataMap();
        tempparam.put("SERIAL_NUMBER_PRE", input.getString("SERIAL_NUMBER_PRE"));
        tempparam.put("STATUS", "9");
        WideChangeUserCheckBean.updWideChangeUserTempInfo(tempparam);
	}
    
    @SuppressWarnings("unchecked")
    private void tradeSaleActiveTrans(IData input, BusiTradeData btd) throws Exception
    {  
    	String serial_number= input.getString("AUTH_SERIAL_NUMBER"); //OLD NUMBER
    	IDataset user_set = UserInfoQry.getUserinfo(serial_number);
		if (user_set==null && user_set.size()<=0)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"serial_number=["+serial_number+"] info not exist!");
		}
		String userid= user_set.getData(0).getString("USER_ID");//OLD NUMBER user_id
		String targetSN= input.getString("SERIAL_NUMBER_PRE");//NEW NUMBER
		String prodid=input.getString("PRODUCT_ID");
		String packid=input.getString("PACKAGE_ID");
		IDataset active_set = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userid,prodid,packid);
		String relation_tradeid=active_set.getData(0).getString("RELATION_TRADE_ID");
        IData svcParam = new DataMap();
        svcParam.put("SERIAL_NUMBER", serial_number);
        svcParam.put("SOURCE_SN", serial_number);
        svcParam.put("TARGET_SN", targetSN);
        svcParam.put("PRODUCT_ID", prodid);
        svcParam.put("PACKAGE_ID", packid);
        svcParam.put("RELATION_TRADE_ID", relation_tradeid);
        svcParam.put(Route.ROUTE_EPARCHY_CODE, input.getString("EPARCHY_CODE"));
        IDataset saleActives = CSAppCall.call("SS.SaleActiveTransRegSVC.tradeReg", svcParam);
    } 
}
