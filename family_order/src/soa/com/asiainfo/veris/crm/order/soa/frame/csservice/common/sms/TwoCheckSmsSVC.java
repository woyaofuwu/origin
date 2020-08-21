
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

/**
 * 二次确认短信
 * 
 * @author huyong
 */
public class TwoCheckSmsSVC extends CSBizService
{

    private static final long serialVersionUID = -8654922184423220971L;

    public IData queryDataMapByOutTradeId(IData input) throws Exception
    {

        String request_id = input.getString("REQUEST_ID");

        return TwoCheckSms.queryDataMapByRequestId(request_id);
    }

    /**
     * 生成二次短信相关信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData twoCheck(IData input) throws Exception
    {

        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");

        IData tradeData = input.getData("TRADE_DATA");

        int amount = Integer.parseInt(input.getString("AMOUNT"));

        // return TwoCheckSms.twoCheck(tradeTypeCode, amount,tradeData);
        return tradeData;// TODO 报错注释
    }

    
    /**
     * 集客大厅生成二次短信相关信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData twoCheckJKDT(IData input) throws Exception
    {

        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");

        IData preData = input.getData("PRE_DATA");

        IData smsData = input.getData("SMS_DATA");
        
        int amount = Integer.parseInt(input.getString("AMOUNT"));
        
        return TwoCheckSms.twoCheck(tradeTypeCode, amount,preData,smsData);
    }
    /**
     * 白名单二次短信回复处理接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset twoCheck2BackByWhiteList(IData input) throws Exception
    {
    	input.put("FORCE_OBJECT", input.getString("RSRV_STR1"));// 检查流水是否存在
    	input.put("DEAL_FLAG", "1");// 调二次短信回复接口传过来的状态
    	input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));// 调二次短信回复接口传过来的号码
    	input.put("ANSWER_CONTENT","00".equals(input.getString("STATUS")) ? "是":"否");
    	//二次确认短信回调
    	IDataset result = TwoCheckSms.twoCheck2Back(input);

    	//登记受限名单记录
    	if ("06".equals(input.getString("STATUS")))
    	{
	    	String inst_id = input.getString("PRODUCT_ID");
	    	input.put("INST_ID", inst_id);
	    	IDataset grp_platInfos = CSAppCall.call("SS.GroupInfoChgSVC.qryGrpPlatInfoByInstID", input);
	    	if (IDataUtil.isNotEmpty(grp_platInfos) && IDataUtil.isNotEmpty(grp_platInfos.getData(0)))
	    	{
	    		IData params = new DataMap();
	    		params.put("BIZ_IN_CODE", grp_platInfos.getData(0).getString("BIZ_IN_CODE"));
	    		params.put("USER_ID_A", grp_platInfos.getData(0).getString("USER_ID"));
	    		params.put("ACCEPT_DATE", input.getString("EFFTT"));
	    		params.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
	    		IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
	    		if(IDataUtil.isNotEmpty(userInfo)){
	    			IData custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));
	    			params.put("CUST_NAME", IDataUtil.isNotEmpty(custInfo) ? custInfo.getString("CUST_NAME") : "");
	    		}
	    		CSAppCall.call("SS.GroupInfoChgSVC.regLimitBlackWhite", params);
	    	}
    	}
    	
    	return result;
    }
    
    /**
     * 二次短信回复处理接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset twoCheck2Back(IData input) throws Exception
    {
        String flowId = input.getString("FLOW_ID", "");
        if (flowId.indexOf("10086502") >= 0 || flowId.indexOf("10086501") >= 0 || flowId.indexOf("10086503") >= 0)
        {
            return IBossCall.ibossTwoCheckBack(input);
        }
        else if (flowId.indexOf("100869778") == 0)// 如果是铃音盒的确认短信
        {
            return IBossCall.ibossTwoCheckBack4LYH(input);
        }
        else
        {
            return TwoCheckSms.twoCheck2Back(input);
        }
    }

    public boolean updateTwoCheckByOutTradeId(IData input) throws Exception
    {

        return TwoCheckSms.updateTwoCheckByOutTradeId(input);

    }
}
