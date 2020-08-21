package com.asiainfo.veris.crm.order.web.person.evaluecard;

import org.apache.tapestry.IRequestCycle;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class DealEValueCard extends PersonBasePage
{
	public abstract void setCondition(IData condition);
	
	public abstract void setEditInfo(IData a);
	
	public abstract void setCardInfos(IDataset infos);


    /**
     * 查询后设置页面信息
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData ajaxData = new DataMap();
        IData data = getData();

        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));

        IData editInfo = new DataMap();
        editInfo.put("CUST_NAME", custInfo.getString("CUST_NAME"));
        editInfo.put("PSPT_ADDR", custInfo.getString("PSPT_ADDR"));
        editInfo.put("PSPT_TYPE_CODE", custInfo.getString("PSPT_TYPE_CODE"));
        editInfo.put("PSPT_ID", custInfo.getString("PSPT_ID"));
        setEditInfo(editInfo);
        setAjax(ajaxData); 
    }
    
    /**
     * 业务提交
     * 
     * @param clcle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData inparam = getData("AUTH", true);
        data.putAll(inparam);
        //有价卡销售
        if ("0".equals(data.getString("OPER_TYPE"))) {
        	data.put("CARD_MONEY", Integer.parseInt(data.getString("CARD_MONEY"))*100);
        	CSViewCall.call(this,"SS.TelValueCardSVC.sellStoreEValueCard",data);
		}
        //有价卡返销
        if ("1".equals(data.getString("OPER_TYPE"))) {
        	CSViewCall.call(this,"SS.TelValueCardSVC.cancelEValueCard",data);
        }
        
        IDataset result = CSViewCall.call(this, "SS.EValueCardRegSVC.tradeReg", data);
        setAjax(result);
    }
	
	/**
	 * 按条件查询要返销的已售的有价卡
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void queryCardInfo(IRequestCycle cycle) throws Exception {
		
		IData pageParam = getData();
		String cardNo = null;
		String transId = null;
		if ("1".equals(pageParam.getString("QRY_FLAG"))) {  //表示按流水号来返销
			transId = pageParam.getString("TRANSACTION_ID");
		}else {
			cardNo = pageParam.getString("CARD_NO");
		}  
				
		IData param = new DataMap();
		param.put("IDVALUE", pageParam.getString("AUTH_SERIAL_NUMBER"));
		param.put("CARD_NO", cardNo);
		param.put("TRANSACTIONID", transId);
		param.put("CHANNEL_TYPE", "01");
		IDataset cardInfos= CSViewCall.call(this,"SS.TelValueCardSVC.getCanCancelCardInfo",param);
		setCardInfos(cardInfos);
		
		if (cardInfos != null && !cardInfos.isEmpty()) {
			pageParam.put("IS_SUCCESS", "0");
			float totalFee = -(Float.valueOf(cardInfos.getData(0).getString("CARD_MONEY","0")))*cardInfos.size();
			pageParam.put("TOTAL_FEE", totalFee);
			
		}else {
			pageParam.put("IS_SUCCESS", "1");
			pageParam.put("TOTAL_FEE", "0");
		}
		
    	setAjax(pageParam);
	}
	
	 /**
     * 电子卡锁定、解锁、延期操作
     * add by huping 20161009
     * @param clcle
     * @throws Exception
     */
    public void lockOrUnlockEValueCard(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.TelValueCardSVC.LockOrUnlockEValueCard", data);
        if (result != null && !result.isEmpty()) {
        	if("0000".equals(result.getData(0).getString("X_RSPCODE"))){
        		data.put("IS_SUCCESS", "0");
        		data.put("RESULT_INFO", result.getData(0).getString("RESERVED"));
        	}else{
        		data.put("IS_SUCCESS", "1");
        		data.put("RESULT_INFO", result.getData(0).getString("X_RESULTINFO"));
        	}
        }else{
        	data.put("IS_SUCCESS", "2");
        }
        setCondition(data);
        setAjax(data);
    }
	public String getTransactionID() throws Exception{
		return "731"+SysDateMgr.getSysDate("yyyyMMddHHmmss")+"000001"; 
	}
}
