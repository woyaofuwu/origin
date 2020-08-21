package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;
/**
 * 未激活用户变更产品-把原来的老套餐截止到上个月底，新套餐立即生效。  
 * @author Administrator
 *
 */
public class NoActiveChangeAction implements ITradeAction{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		
		ChangeProductReqData changeProductRD=(ChangeProductReqData)btd.getRD();
		UcaData uca=changeProductRD.getUca();
		
		IData idRequestData = btd.getRD().getPageRequestData();
		String strBatChId = btd.getRD().getBatchId();
    	String strBatchOperType = idRequestData.getString("BATCH_OPER_TYPE", "");
    	
    	if(StringUtils.isNotBlank(strBatChId) && "MODIFYPRODUCT_NAME".equals(strBatchOperType))//批量主套餐办理(未实名制)
    	{
    		if(uca!=null&&uca.getUser()!=null
    				&&(!("0".equals(uca.getUser().getAcctTag())))){//未激活用户
    			
    			ProductData pd=changeProductRD.getNewMainProduct();
    			if((pd!=null)&&(!changeProductRD.isBookingTag())){//变更主套餐 且是非预约的
    				
    				//处理产品
    				List<ProductTradeData> productTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
    				if(productTrades!=null){
    					for(ProductTradeData products:productTrades){
    						if(BofConst.MODIFY_TAG_ADD.equals(products.getModifyTag())){
    							products.setStartDate(SysDateMgr.getSysTime());//新产品立即生效
    						}else if(BofConst.MODIFY_TAG_DEL.equals(products.getModifyTag())){
    							products.setEndDate(getAddMonthsLastDay(-1));//旧产品截止上个月底
    						}
    					}
    				}
    				
    				//处理优惠
    				List<DiscntTradeData> discntTradeList = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
    				if(discntTradeList!=null){
    					for(DiscntTradeData discnt:discntTradeList){
    						if(BofConst.MODIFY_TAG_ADD.equals(discnt.getModifyTag())){
    							discnt.setStartDate(SysDateMgr.getSysTime());//新优惠立即生效
    						}else if(BofConst.MODIFY_TAG_DEL.equals(discnt.getModifyTag())){
    							discnt.setEndDate(getAddMonthsLastDay(-1));//旧优惠截止上个月底
    						}
    					}
    				}
    				
    			}
    		}else{//已激活的报错
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该号码为激活用户，不允许变更!");
    		}
    	}
		
	}
	 /**
     * 获取 N个月后的最后一天
     * 
     * @author lizj 
     * @return
     * @throws Exception
     */
    public static String getAddMonthsLastDay(int offset) throws Exception
    {
    	SimpleDateFormat format =new SimpleDateFormat("yyyy-MM-dd 23:59:59");
    	Calendar cale = null;
    	cale = Calendar.getInstance();
    	cale.add(Calendar.MONTH, offset+1);
    	cale.set(Calendar.DAY_OF_MONTH, 0);
        return format.format(cale.getTime());
    }
    public static void main(String args[]){
    	try {
			System.out.println(getAddMonthsLastDay(-1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
