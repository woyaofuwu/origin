package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.MfcCommonUtil;

/**
 * 家庭网成员资费处理
 */
public class DealMemDiscntFinishAction implements ITradeFinishAction
{
    private static transient Logger log = Logger.getLogger(DealMemDiscntFinishAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
    	String tradeId = mainTrade.getString("TRADE_ID");
    	String routeSn = mainTrade.getString("SERIAL_NUMBER");
    	IDataset relaUUDatas = MfcCommonUtil.getUserRelationByTradeId(tradeId);
    	
    	if (log.isDebugEnabled())
    	{
    		log.debug("11111111111111111111111111DealMemDiscntFinishAction11111111111111relaUUDatas="+relaUUDatas);
    	}
    	
    	String custSn = mainTrade.getString("RSRV_STR3");
    	String productCode = mainTrade.getString("RSRV_STR1");
    	IDataset moffice = ResCall.getMphonecodeInfo(custSn);
		if(!MfcCommonUtil.PRODUCT_CODE_ZF.equals(productCode))
		{//统付产品不考虑本省成员处理
			return;
		}
		for(int i=0;i<relaUUDatas.size();i++)
		{
			IData relation = relaUUDatas.getData(i);
			if("2".equals(relation.getString("ROLE_CODE_B")) && "1".equals(relation.getString("RSRV_STR1")))
			{//本省副号
				String action = mainTrade.getString("RSRV_STR2");
				String modifyTag = BofConst.MODIFY_TAG_ADD;
				if("51".equals(action))
				{
					modifyTag = BofConst.MODIFY_TAG_DEL;
				}
				if (log.isDebugEnabled())
				{
					log.debug("11111111111111111111111111DealMemDiscntFinishAction11111111111111modifyTag="+modifyTag);
				}

				if(DataUtils.isNotEmpty(moffice) || !routeSn.equals(relation.getString("SERIAL_NUMBER_B")))
				{//主号省的成员号码和成员省的非路由号码需要给本省成员号码绑定资费
					newDiscntTradeData(relation,modifyTag,productCode);
				}
			}
		}
    }
    
    /**
     * 创建新的优惠信息
     *
     * @param relationTradeData
     * @throws Exception
     */
    public static void newDiscntTradeData(IData relation,String modifyTag,String productCode) throws Exception 
    {
    	DataMap data=new DataMap();
        data.put("SERIAL_NUMBER", relation.getString("SERIAL_NUMBER_B"));
        data.put("MODIFY_TAG", modifyTag);
        data.put("USER_ID_A", relation.getString("USER_ID_A"));
        data.put("USER_ID_B", relation.getString("USER_ID_B"));
        data.put("ROLE_CODE_B", relation.getString("ROLE_CODE_B"));
        data.put("PRODUCT_CODE", productCode);
        data.put("PRODUCT_OFFERING_ID", relation.getString("RSRV_STR2"));
        data.put("START_DATE", SysDateMgr.decodeTimestamp(relation.getString("FINISH_TIME",SysDateMgr.getSysTime()),SysDateMgr.PATTERN_STAND));
        data.put("END_DATE", SysDateMgr.decodeTimestamp(relation.getString("EXP_TIME",SysDateMgr.END_DATE_FOREVER),SysDateMgr.PATTERN_STAND));
        CSAppCall.call("SS.MfcSubDiscntTradeSVC.tradeReg",data);
    }
}
