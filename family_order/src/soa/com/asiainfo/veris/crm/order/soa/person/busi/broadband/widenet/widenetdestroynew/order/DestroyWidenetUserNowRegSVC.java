
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class DestroyWidenetUserNowRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(input.getString("SERIAL_NUMBER"));
            if (IDataUtil.isNotEmpty(widenetInfos))
            {
                String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
                if ("4".equals(wideType))
                {
                    input.put("ORDER_TYPE_CODE", "635");
                }
                else if ("3".equals(wideType))
                {
                    input.put("ORDER_TYPE_CODE", "605");
                }
                else if ("2".equals(wideType) || "5".equals(wideType) || "6".equals(wideType))
                {
                    input.put("ORDER_TYPE_CODE", "605");
                }
                else
                {
                    input.put("ORDER_TYPE_CODE", "605");
                }
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
                if ("4".equals(wideType))
                {
                    input.put("TRADE_TYPE_CODE", "635");
                }
                else if ("3".equals(wideType))
                {
                    input.put("TRADE_TYPE_CODE", "605");
                }
                else if ("2".equals(wideType) || "5".equals(wideType) || "6".equals(wideType))
                {
                    input.put("TRADE_TYPE_CODE", "605");
                }
                else
                {
                    input.put("TRADE_TYPE_CODE", "605");
                }

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
        //add on 20160704
        /*String isPreDestroyOrder = btd.getRD().getPageRequestData().getString("IS_PRE_DESTROY_ORDER","");
    	if(isPreDestroyOrder != null && "Y".equals(isPreDestroyOrder))
    	{
    		String destroyOrderTime = btd.getRD().getPageRequestData().getString("DESTROY_ORDER_TIME","");
    		if(destroyOrderTime != null && !"".equals(destroyOrderTime))
    		{
    			orderData.setExecTime(destroyOrderTime);
    		}
    	} */
    }

    public IDataset tradeReg(IData input) throws Exception
    {

        if (!"KD_".equals(input.getString("SERIAL_NUMBER").substring(0, 3)))
        {
            input.put("SERIAL_NUMBER", "KD_" + input.getString("SERIAL_NUMBER"));
        }
        return super.tradeReg(input);
    }
    @SuppressWarnings("null")
	public void otherTradeDeal(IData input, BusiTradeData btd) throws Exception
    {
    	String tradetypecode= btd.getTradeTypeCode();
    	if (!"605".equals(tradetypecode) || input.getString("PHONE_DESTROY_TYPE","").equals("192"))
    	{
    		//这里是宽带拆机完后调用的营销活动终止的地方，特殊拆机调用是用到期执行的方法，不在这里
    		return;
    	}
    	
    	String serial_number= input.getString("AUTH_SERIAL_NUMBER");
    	if(serial_number == null || "".equals(serial_number))
    	{
    		serial_number = input.getString("SERIAL_NUMBER");
    	}
    	if(serial_number.startsWith("KD_"))
    	{
    		serial_number = serial_number.replace("KD_", "");
    	}
    	if(serial_number != null && serial_number.length() > 11)
    		return ;
    	
		IDataset user_set = UserInfoQry.getUserinfo(serial_number);
		if (user_set==null && user_set.size()<=0)
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户资料不存在");
		}
		String userid= user_set.getData(0).getString("USER_ID");
		
		String tradeEparchyCode =btd.getMainTradeData().getEparchyCode();// input.getString("EPARCHY_CODE");
		String cancelDate = SysDateMgr.getLastDateThisMonth();//获取当月月底日期
		//查询要取消的营销活动
		IData params = new DataMap();
        params.put("USER_ID", userid);
        params.put("PARAM_CODE", tradetypecode);
        IDataset cancelSaleActiveList = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES3", params);
        if (IDataUtil.isNotEmpty(cancelSaleActiveList))
		{
        	//如果是预约拆机，则重算拆机时间
        	String isPreDestroyOrder = input.getString("IS_PRE_DESTROY_ORDER","");
        	if(isPreDestroyOrder != null && "Y".equals(isPreDestroyOrder))
        	{
        		String destroyOrderTime = input.getString("DESTROY_ORDER_TIME","");
        		if(destroyOrderTime != null && !"".equals(destroyOrderTime))
        		{
        			//取当前时间的上一秒
        			cancelDate = SysDateMgr.addSecond(destroyOrderTime, -1);
        		}
        	}
        	
			for (int i = 0, size = cancelSaleActiveList.size(); i < size; i++)
			{
				IData cancelSale = cancelSaleActiveList.getData(i);
				
				if(SysDateMgr.compareTo(cancelDate, cancelSale.getString("END_DATE")) >0)
				{
					//如果本月底大于营销活动的结束时间，不用取消营销活动，兼容历史数据
					continue;
				}
				
		    	//add by zhangxing3 for REQ201804280023优化“先装后付，免费体验”
				if(SysDateMgr.compareTo(cancelSale.getString("START_DATE"), cancelSale.getString("END_DATE")) >0)
				{
					//如果营销活动的开始时间大于营销活动的结束时间，不用取消营销活动。
					continue;
				}
				
				IData cancelParam = new DataMap();
				cancelParam.put("SERIAL_NUMBER", serial_number);
				cancelParam.put("PRODUCT_ID", cancelSale.getString("PRODUCT_ID"));
				cancelParam.put("PACKAGE_ID", cancelSale.getString("PACKAGE_ID"));
				cancelParam.put("RELATION_TRADE_ID", cancelSale.getString("RELATION_TRADE_ID"));
				cancelParam.put("FORCE_END_DATE", cancelDate);
				cancelParam.put("END_DATE_VALUE", "7"); //强制终止
//				cancelParam.put("TRADE_STAFF_ID", tradeStaffId);
//				cancelParam.put("TRADE_DEPART_ID", tradeDepartId);
//				cancelParam.put("TRADE_CITY_CODE", tradeCityCode);
				cancelParam.put("TRADE_EPARCHY_CODE", tradeEparchyCode);
				cancelParam.put("TRADE_STAFF_ID", "SUPERUSR");
				cancelParam.put("TRADE_DEPART_ID", "36601");
				cancelParam.put("TRADE_CITY_CODE", "HNSJ");
				cancelParam.put("REMARK", "宽带拆机--营销活动终止");

				IData callData = CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", cancelParam).getData(0);

			}
		}
    }
	
	/**
	 * 业务提交后规则校验，重载父类改方法
	 */
	public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
	{
		String skipRule = btd.getRD().getPageRequestData().getString("SKIP_RULE","");
		//是否跳过规则，如果传入SKIP_RULE=TRUE，则不进行提交后的规则校验
        if(StringUtils.isNotBlank(skipRule) && "TRUE".equals(skipRule))
        {
        	return ;
        }
		super.checkAfterRule(tableData, btd);
	}
}
