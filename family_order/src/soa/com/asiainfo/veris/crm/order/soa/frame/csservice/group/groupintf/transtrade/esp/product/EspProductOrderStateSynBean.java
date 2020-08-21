
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp.product;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class EspProductOrderStateSynBean extends GroupBean
{   
	 private String OperCode;

	 private String TradeTypeCode;

	 private String userId;

	 private String brandCode;

	 private String productId;
	
	 private String tradeId;
	 /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }
    
    @Override
	protected void makInit(IData data) throws Exception
    {
       super.makInit(data);
       TradeTypeCode = data.getString("TRADE_TYPE_CODE");
       userId = data.getString("USER_ID");
       OperCode = data.getString("OPER_CODE");
       brandCode = data.getString("BRAND_CODE");
       productId = data.getString("PRODUCT_ID");
       tradeId = data.getString("TRADE_ID");
    }
	
	@Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();

        tradeData.put("SUBSCRIBE_TYPE", "200");

        IDataset userOtherinfos = UserOtherInfoQry.getUserOther(userId,"ESPG");
        String productOrderID = "";
        if(IDataUtil.isEmpty(userOtherinfos)){
        	 CSAppException.apperr(CrmUserException.CRM_USER_1167);
        }
        productOrderID = userOtherinfos.getData(0).getString("RSRV_STR4");
        // 查询集团客户信息
        IData custInfo = UcaInfoQry.qryGrpInfoByCustId(reqData.getUca().getUser().getCustId());
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(CustException.CRM_CUST_35);
        }
        String groupId = custInfo.getString("GROUP_ID");
        String operCode = "";
        if("back".equals(OperCode)){
        	operCode = "2";
        }else if("stop".equals(OperCode)){
        	operCode = "1";
        }else if("destory".equals(OperCode)){
        	operCode = "3";
        }
        tradeData.put("RSRV_STR2", groupId);//集团客户编码
        tradeData.put("RSRV_STR3", productOrderID);//产品订购实例ID
        tradeData.put("RSRV_STR4", operCode);//请求类型
        tradeData.put("RSRV_STR5", tradeId);//记录产品状态同步的订单ID
    }
	
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
    }
    
    @Override
    protected void makUca(IData data) throws Exception
    {
        makUcaForGrpNormal(data);
    } 
    
	@Override
    protected String setTradeTypeCode() throws Exception
    {
        return "6810";
    }
	
	@Override
    protected void regOrder() throws Exception
    {
        super.regOrder();
        IData tradeData = bizData.getTrade();

        tradeData.put("SUBSCRIBE_TYPE", "200");
    }
}