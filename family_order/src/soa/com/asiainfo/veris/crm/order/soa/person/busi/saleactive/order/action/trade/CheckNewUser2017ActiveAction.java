package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveCheckSnBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;


public class CheckNewUser2017ActiveAction  implements ITradeAction {
	
	@SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SaleActiveReqData req = (SaleActiveReqData) btd.getRD();
        String tradeTypeCode =  btd.getMainTradeData().getTradeTypeCode();
        String userFinalProd=req.getProductId();
    	String serialNum=btd.getRD().getUca().getSerialNumber();
    	String userId=btd.getRD().getUca().getUserId();
    	String toDay=SysDateMgr.getSysDateYYYYMMDD();
    	String preType=req.getPreType();
    	
        if("240".endsWith(tradeTypeCode)&&"66000206".equals(userFinalProd)&&!"1".equals(preType))
    	{
        	IData input = new DataMap();
        	input.put("SERIAL_NUMBER", serialNum);
        	input.put("USER_ID", userId);
        	input.put("TRADE_TYPE_CODE", tradeTypeCode);
        	input.put("TO_DAY", toDay);
        	input.put("PRODUCT_ID", userFinalProd);
        	
        	SaleActiveCheckSnBean checkBean = BeanManager.createBean(SaleActiveCheckSnBean.class);
        	//查询是否存在临时表数据
        	IDataset oldCustInfos=checkBean.qryNewTempInfo(input);
        	if(oldCustInfos==null || oldCustInfos.size()<1){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "检查到号码【"+serialNum+"】办理66000206活动不存在新号码信息，如果在选择活动后没有弹窗或者弹窗显示“老用户”字样均为错误的，可能为缓存导致。请关闭浏览器重新打开办理。");
        	}
    	}
        
        //根据活动产品ID，判断是否属于以老带新活动
        IDataset productInfos = CommparaInfoQry.getCommpara("CSM", "9957", userFinalProd, "0898");
        //如果属于特殊活动，才进行处理
        
        if("240".endsWith(tradeTypeCode) && IDataUtil.isNotEmpty(productInfos) && !"1".equals(preType))
    	{
        	IData input = new DataMap();
        	input.put("SERIAL_NUMBER", serialNum);
        	input.put("USER_ID", userId);
        	input.put("TRADE_TYPE_CODE", tradeTypeCode);
        	input.put("PRODUCT_ID", userFinalProd);

        	SaleActiveCheckSnBean checkBean = BeanManager.createBean(SaleActiveCheckSnBean.class);
        	//查询是否存在临时表数据
        	IDataset oldCustInfos=checkBean.qryNewTempInfo(input);

        	if(IDataUtil.isEmpty(oldCustInfos)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "检查到号码【"+serialNum+"】办理"+userFinalProd+"活动不存在新号码信息，如果在选择活动后没有弹窗，可能为缓存导致。请关闭浏览器重新打开办理。");
        	}
    	}
        //用户办理红海行动时,校验老用户是否有未完工的大派送活动.
        if("240".endsWith(tradeTypeCode)&&("6600231".equals(userFinalProd)||"6600232".equals(userFinalProd)
        		||"6600233".equals(userFinalProd)||"6600234".equals(userFinalProd)))
    	{
        	String serialNumberOld="";
        	String productId="66000238";

        	IData input = new DataMap();
        	input.put("CHECK_SERIAL_NUMBER", serialNum);
        	input.put("CHECK_USER_ID", userId);
        	input.put("TRADE_TYPE_CODE", tradeTypeCode);
        	input.put("PRODUCT_ID", productId);
        	SaleActiveCheckSnBean checkBean = BeanManager.createBean(SaleActiveCheckSnBean.class);
        	//查询是否存在临时表数据
        	IDataset oldCustInfos=checkBean.qryNewTempInfo2(input);

        	if(oldCustInfos!=null && oldCustInfos.size()>=1){
        		serialNumberOld=oldCustInfos.getData(0).getString("SERIAL_NUMBER", "");
        		IDataset ids = TradeInfoQry.queryTradeinfoBySN_RSRVSTR1(serialNumberOld,productId);

        		if(ids!=null || ids.size()>=1){
            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "检查到老号码【"+serialNumberOld+"】已经登记办理“2018老客户感恩大派送活动|66000238”活动，新号码不能再办理红海活动！");
            	}
        	}
    	}
    }
}
