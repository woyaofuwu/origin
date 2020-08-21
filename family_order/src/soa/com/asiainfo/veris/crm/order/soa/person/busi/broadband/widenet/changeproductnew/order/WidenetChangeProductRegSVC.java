/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class WidenetChangeProductRegSVC extends OrderService
{

    private static final long serialVersionUID = 2563107001595275438L;

    public String getOrderTypeCode() throws Exception
    {
        if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(input.getString("SERIAL_NUMBER"));
            if (IDataUtil.isNotEmpty(widenetInfos))
            {
                String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
                
                    input.put("ORDER_TYPE_CODE", "601");// gpon
                

            }
            else
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_4);
            }
        }
        else
        {
            return this.input.getString("TRADE_TYPE_CODE", "");
        }
        return this.input.getString("ORDER_TYPE_CODE", "");
    }

    public String getTradeTypeCode() throws Exception
    {
        if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(input.getString("SERIAL_NUMBER"));
            if (IDataUtil.isNotEmpty(widenetInfos))
            {
                String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
               
                    input.put("TRADE_TYPE_CODE", "601");// gpon
                

            }
            else
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_4);
            }
        }
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {

        orderData.setSubscribeType("300");
    }

    /**
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset tradeReg(IData input) throws Exception
    {

        if (!"KD_".equals(input.getString("SERIAL_NUMBER").substring(0, 3)))
        {
        	input.put("SERIAL_NUMBER_A", input.getString("SERIAL_NUMBER"));
            input.put("SERIAL_NUMBER", "KD_" + input.getString("SERIAL_NUMBER"));
        }
        //add by zhangxing3 for REQ201804280023优化“先装后付，免费体验”
		String endDate = checkWidenetUserMFTY(input.getString("SERIAL_NUMBER", ""));
		if(!"".equals(endDate))
		{
			input.put("BOOKING_DATE", SysDateMgr.getSysTime());
			input.put("EFFECT_NOW","1");
		}
        return super.tradeReg(input);

    }
    @SuppressWarnings("unchecked")
    public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
	{
    	//1、调用产品变更接口
		//3、调用产品变更接口与营销活动受理接口
		//4、调用产品变更接口及营销活动终止接口
		//5、调用产品变更接口及营销活动终止接口，营销活动受理接口
    	//多笔工单处理
    	String changeType = input.getString("CHANGE_TYPE","");
    	if("1".equals(changeType))
    	{
    		return ;
    	}
    	else if("3".equals(changeType))
    	{
    		//调用营销活动受理接口
    		tradeSaleActiveCreate(input, btd);
    		//BUS201907310012关于开发家庭终端调测费的需求
/*    		if(!"".equals(input.getString("NEW_SALE_PRODUCT_ID2","")) && !"".equals(input.getString("NEW_SALE_PACKAGE_ID2","")))
    		{
    			tradeSaleActiveCreate2(input, btd);
    		}*/
    		//BUS201907310012关于开发家庭终端调测费的需求
    	}
    	else if("4".equals(changeType) || "7".equals(changeType))
    	{
    		//调用营销活动终止接口
    		tradeSaleActiveEnd(input, btd);
    	}
    	else if("5".equals(changeType) || "6".equals(changeType))
    	{
    		String oldProudctId = input.getString("V_USER_PRODUCT_ID","");
    		String newProudctId = input.getString("NEW_SALE_PRODUCT_ID","");
    		String serialNumber = input.getString("SERIAL_NUMBER","");
    		String endDate = checkWidenetUserMFTY(serialNumber);
    		if("69908001".equals(newProudctId) && "67220428".equals(oldProudctId) )
    		{
    			//1、宽带包年转为宽带1+活动，不在这里调用终止接口 	
    		}
    		else{
    			//1、先调用营销活动终止接口 			
	    		tradeSaleActiveEnd(input, btd);
    		}
    		//2、再调用营销活动受理接口
    		if (!"".equals(endDate))
    		{
    			input.put("BOOKING_DATE", SysDateMgr.getFirstDayOfNextMonth(endDate));
    		}
    		tradeSaleActiveCreate(input, btd);
    		//BUS201907310012关于开发家庭终端调测费的需求
/*    		if(!"".equals(input.getString("NEW_SALE_PRODUCT_ID2","")) && !"".equals(input.getString("NEW_SALE_PACKAGE_ID2","")))
    		{
    			tradeSaleActiveCreate2(input, btd);
    		}*/
    		//BUS201907310012关于开发家庭终端调测费的需求
    	}
	}
    @SuppressWarnings("unchecked")
    private void tradeSaleActiveEnd(IData input, BusiTradeData btd) throws Exception
    {
		String serial_number= input.getString("AUTH_SERIAL_NUMBER");
		IDataset user_set = UserInfoQry.getUserinfo(serial_number);
		if (user_set==null && user_set.size()<=0)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户资料不存在");
		}
		String userid= user_set.getData(0).getString("USER_ID");
		String packid=input.getString("V_USER_PACKAGE_ID");
		String prodid=input.getString("V_USER_PRODUCT_ID");
		
		IDataset active_set = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userid,prodid,packid);
		//不存在则不需要截止
		if(IDataUtil.isEmpty(active_set)){
			return;
		}
		String relation_tradeid=active_set.getData(0).getString("RELATION_TRADE_ID");
		String bookDate = input.getString("BOOKING_DATE","");
		if(bookDate != null && !"".equals(bookDate))
		{
			bookDate = SysDateMgr.addDays(bookDate,-1);
			bookDate = SysDateMgr.getDateLastMonthSec(bookDate);
		}
		else
		{
			bookDate = SysDateMgr.getLastDateThisMonth();
		}
		
		String oldSaleActiveEndDate = active_set.getData(0).getString("END_DATE");
		//如果预约时间结束时间小于等于原营销活动的结束时间，则不需要终止
		if(SysDateMgr.compareTo(oldSaleActiveEndDate, bookDate) <= 0)
		{
			return ;
		}
		
		IData endActiveParam = new DataMap();

        endActiveParam.put("SERIAL_NUMBER", serial_number);
        endActiveParam.put("PRODUCT_ID", prodid);
        endActiveParam.put("PACKAGE_ID", packid);
        endActiveParam.put("RELATION_TRADE_ID", relation_tradeid);
        endActiveParam.put("IS_RETURN", "0");
        endActiveParam.put("FORCE_END_DATE", bookDate);
        endActiveParam.put("END_DATE_VALUE", "7"); //强制终止
        endActiveParam.put("EPARCHY_CODE",input.getString("EPARCHY_CODE"));
        
        endActiveParam.put("WIDE_YEAR_ACTIVE_BACK_FEE","1"); //标记为1，如果营销活动有预存不进行清退
        
        //endActiveParam.put("WIDE_USER_CREATE_SALE_ACTIVE", input.getString("WIDE_USER_CREATE_SALE_ACTIVE"));
        //endActiveParam.put("WIDE_USER_SELECTED_SERVICEIDS", input.getString("WIDE_USER_SELECTED_SERVICEIDS"));
        //认证方式
        String checkMode = btd.getRD().getCheckMode();
        endActiveParam.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
        endActiveParam.put("BATCH_ID",btd.getRD().getBatchId());

        CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
    }
    @SuppressWarnings("unchecked")
    private void tradeSaleActiveCreate(IData input, BusiTradeData btd) throws Exception
    {
    	String serialNumber= input.getString("AUTH_SERIAL_NUMBER");
		String packid=input.getString("NEW_SALE_PACKAGE_ID");
		
    	IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("PRODUCT_ID", input.getString("NEW_SALE_PRODUCT_ID"));
        param.put("PACKAGE_ID", packid);
        param.put("TRADE_STAFF_ID", "SUPERUSR");
        param.put("TRADE_DEPART_ID", "36601");
        param.put("TRADE_CITY_CODE", "HNSJ");
        
        param.put("EPARCHY_CODE",input.getString("EPARCHY_CODE"));
        param.put("WIDE_USER_CREATE_SALE_ACTIVE", input.getString("WIDE_USER_CREATE_SALE_ACTIVE"));
        param.put("WIDE_USER_SELECTED_SERVICEIDS", input.getString("WIDE_USER_SELECTED_SERVICEIDS"));
        param.put("BOOKING_DATE", input.getString("BOOKING_DATE",""));
        param.put("CHANGE_UP_DOWN_TAG", input.getString("CHANGE_UP_DOWN_TAG",""));
        param.put("SPECIAL_SALE_FLAG", input.getString("SPECIAL_SALE_FLAG", "")); // add 20170511
        //宽带包年营销活动需要补缴的费用
        param.put("WIDE_ACTIVE_PAY_FEE", input.getString("WIDE_ACTIVE_PAY_FEE"));
        param.put("RETURN_YEAR_DISCNT_REMAIN_FEE", input.getString("RETURN_YEAR_DISCNT_REMAIN_FEE"));
        param.put("RETURN_YEAR_DISCNT_REMAIN_FEEFLAG", "JZF");
        //认证方式
        String checkMode = btd.getRD().getCheckMode();
        param.put("BATCH_ID",btd.getRD().getBatchId());
        param.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
    	
        CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param);
    }
    
    /*private void tradeSaleActiveCreate2(IData input, BusiTradeData btd) throws Exception
    {
    	String serialNumber= input.getString("AUTH_SERIAL_NUMBER");
		String packid=input.getString("NEW_SALE_PACKAGE_ID2","");
		
    	IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("PRODUCT_ID", input.getString("NEW_SALE_PRODUCT_ID2",""));
        param.put("PACKAGE_ID", packid);
        param.put("TRADE_STAFF_ID", "SUPERUSR");
        param.put("TRADE_DEPART_ID", "36601");
        param.put("TRADE_CITY_CODE", "HNSJ");
        
        param.put("EPARCHY_CODE",input.getString("EPARCHY_CODE"));
        param.put("WIDE_USER_CREATE_SALE_ACTIVE", input.getString("WIDE_USER_CREATE_SALE_ACTIVE"));
        param.put("WIDE_USER_SELECTED_SERVICEIDS", input.getString("WIDE_USER_SELECTED_SERVICEIDS"));
        param.put("BOOKING_DATE", input.getString("BOOKING_DATE",""));
        param.put("CHANGE_UP_DOWN_TAG", input.getString("CHANGE_UP_DOWN_TAG",""));
        param.put("SPECIAL_SALE_FLAG", input.getString("SPECIAL_SALE_FLAG", "")); // add 20170511
        //认证方式
        String checkMode = btd.getRD().getCheckMode();
        param.put("BATCH_ID",btd.getRD().getBatchId());
        param.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
    	
        CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param);
    }*/
    
    
    private String checkWidenetUserMFTY(String serialNumber) throws Exception
    {
    	IDataset user_set = UserInfoQry.getUserinfo(serialNumber);
		if (user_set==null && user_set.size()<=0)
		{
			return "";
		}
		String userid= user_set.getData(0).getString("USER_ID");
        
        IDataset discntInfo1 = UserDiscntInfoQry.getUserIMSDiscnt(userid,"8523","0898");
        if (IDataUtil.isNotEmpty(discntInfo1))
        {
        	return discntInfo1.getData(0).getString("END_DATE", "");
        }
        else
        {
        	return "";
        }
    }
	
	public void saleactiveandEnd(IData input) throws Exception
    {
    	String serial_number= input.getString("AUTH_SERIAL_NUMBER");
		IDataset user_set = UserInfoQry.getUserinfo(serial_number);
		if (user_set==null && user_set.size()<=0)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户资料不存在");
		}
		String userid= user_set.getData(0).getString("USER_ID");
		String packid=input.getString("V_USER_PACKAGE_ID");
		String prodid=input.getString("V_USER_PRODUCT_ID");
		
		IDataset active_set = UserSaleActiveInfoQry.querySaleActiveByPPIDuserId(userid,prodid,packid);
		//不存在则不需要截止
		if(IDataUtil.isEmpty(active_set)){
			return;
		}
		String relation_tradeid=active_set.getData(0).getString("RELATION_TRADE_ID");
		String bookDate = input.getString("BOOKING_DATE","");
		if(bookDate != null && !"".equals(bookDate))
		{
			bookDate = SysDateMgr.addDays(bookDate,-1);
			bookDate = SysDateMgr.getDateLastMonthSec(bookDate);
		}
		else
		{
			bookDate = SysDateMgr.getLastDateThisMonth();
		}
		
		String oldSaleActiveEndDate = active_set.getData(0).getString("END_DATE");
		//如果预约时间结束时间小于等于原营销活动的结束时间，则不需要终止
		if(SysDateMgr.compareTo(oldSaleActiveEndDate, bookDate) <= 0)
		{
			return ;
		}
		
		IData endActiveParam = new DataMap();

        endActiveParam.put("SERIAL_NUMBER", serial_number);
        endActiveParam.put("PRODUCT_ID", prodid);
        endActiveParam.put("PACKAGE_ID", packid);
        endActiveParam.put("RELATION_TRADE_ID", relation_tradeid);
        endActiveParam.put("IS_RETURN", "0");
        endActiveParam.put("FORCE_END_DATE", bookDate);
        endActiveParam.put("END_DATE_VALUE", "7"); //强制终止
        endActiveParam.put("EPARCHY_CODE",input.getString("EPARCHY_CODE"));
        
        endActiveParam.put("WIDE_YEAR_ACTIVE_BACK_FEE","1"); //标记为1，如果营销活动有预存不进行清退
        
        //endActiveParam.put("WIDE_USER_CREATE_SALE_ACTIVE", input.getString("WIDE_USER_CREATE_SALE_ACTIVE"));
        //endActiveParam.put("WIDE_USER_SELECTED_SERVICEIDS", input.getString("WIDE_USER_SELECTED_SERVICEIDS"));
        //认证方式
        String checkMode = input.getString("CHECK_MODE","");
        endActiveParam.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );
    	
        CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
    }
	
	/**
	 * BUS202002180007关于优化FTTH迁移活动的需求
	 * 到期任务处理:宽带产品变更生效后，终止FTTB用户迁移提速活动
	 * @param input
	 * @throws Exception
	 */
	public void widenetFTTBActiveEnd(IData input) throws Exception
    {
		IData endActiveParam = new DataMap(input.getString("DEAL_COND"));
		CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
    }
	
}
