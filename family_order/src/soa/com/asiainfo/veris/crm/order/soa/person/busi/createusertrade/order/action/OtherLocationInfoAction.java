
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

/**
 * 如果其他元素不为空则拼其他元素串 记录到other表
 * 
 * @author fusr
 */
public class OtherLocationInfoAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
    	MainTradeData main=btd.getMainTradeData();
  
    	        List<ProductTradeData> product = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
    	String productid ="";
    	if(productid!= null){
    		for (int i = 0, size = product.size(); i < size; i++){
    	        ProductTradeData productTD = (ProductTradeData) product.get(i);
                productid = productTD.getProductId();
		 }
    	}
    	String productname = "";
    	IDataset productinfo = UpcCall.queryOfferInfoByOfferCodeAndOfferType(productid, "P");
    	if(productinfo != null){
        	productname = productinfo.first().getString("OFFER_NAME");
    	}
    	String longitude=btd.getRD().getPageRequestData().getString("MOP_LONGITUDE");
    	String latitude=btd.getRD().getPageRequestData().getString("MOP_LATITUDE");
    	String locationinfo=btd.getRD().getPageRequestData().getString("MOP_LOCATIONINFO");
    	System.out.println("longitude="+ longitude);
    	System.out.println("latitude="+ latitude);
    	if(longitude != null && !("".equals(longitude))){
    		   OtherTradeData otherTradeData = new OtherTradeData();
               otherTradeData.setUserId(main.getUserId());
               otherTradeData.setRsrvValueCode("MOP_LOCATIONINFO");
               otherTradeData.setRsrvValue("10");//
               otherTradeData.setRsrvStr2(main.getTradeTypeCode());//业务类型编码trade_code_type
               otherTradeData.setRsrvStr3(main.getSerialNumber());//用户号码
               otherTradeData.setRsrvStr4(CSBizBean.getVisit().getStaffId());//办理工号
               otherTradeData.setRsrvStr5(CSBizBean.getVisit().getDepartId());//办理渠道
               otherTradeData.setRsrvStr6(SysDateMgr.getSysTime());//办理时间
               otherTradeData.setRsrvStr7(longitude);//经度
               otherTradeData.setRsrvStr8(latitude);//纬度
               otherTradeData.setRsrvStr9(locationinfo);//办理地点位置名称
               otherTradeData.setRsrvStr10(productname);//新开户时办理主套餐的名称
               otherTradeData.setStartDate(SysDateMgr.getSysTime());
               otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
               otherTradeData.setInstId(SeqMgr.getInstId());
               otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
               otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());

               otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
               btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
    	}
    	
    	
  

    }

}
