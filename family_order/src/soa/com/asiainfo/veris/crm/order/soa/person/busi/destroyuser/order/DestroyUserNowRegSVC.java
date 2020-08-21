
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class DestroyUserNowRegSVC extends OrderService
{

    private static final long serialVersionUID = 1L;
    
    public IDataset tradeReg(IData input) throws Exception
    {
    	if(StringUtils.isNotEmpty(input.getString("CREATE_DEPART_ID"))
        		&&StringUtils.isNotEmpty(input.getString("CREATE_CITY_CODE"))
        		&&StringUtils.isNotEmpty(input.getString("CREATE_STAFF_ID"))){
        	getVisit().setStaffId(input.getString("CREATE_STAFF_ID"));
        	getVisit().setDepartId(input.getString("CREATE_DEPART_ID"));
        	getVisit().setCityCode(input.getString("CREATE_CITY_CODE"));
        }
    	return super.tradeReg(input);
    }

    @Override
    public void checkAfterRule(IData tableData, BusiTradeData btd) throws Exception
    {
        // 销户不再做校验了
    }

    @Override
    public String getOrderTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    @Override
    public String getTradeTypeCode() throws Exception
    {
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        String tradeTypeCode = btd.getTradeTypeCode();
        if (StringUtils.equals("47", tradeTypeCode) || StringUtils.equals("48", tradeTypeCode))
        {
            orderData.setExecTime(SysDateMgr.END_DATE_FOREVER);
            String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);
            orderData.setRemark(tradeTypeName);
            orderData.setSubscribeType("200");
        }
        else if (StringUtils.equals("7240", tradeTypeCode))
        {
            orderData.setSubscribeType("200");
        }
    }
}
