
package com.asiainfo.veris.crm.order.soa.group.grplocalmgr.action;

import org.apache.log4j.Logger;

import com.ailk.biz.util.TimeUtil;
import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;

public class TradeFinishGrpCityCodeSynAction implements ITradeFinishAction
{
    private final static Logger logger = Logger.getLogger(TradeFinishGrpCityCodeSynAction.class);

    
    public void executeAction(IData mainTrade) throws Exception
    {
        String custId = "";
        custId = mainTrade.getString("CUST_ID");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String tradeId = mainTrade.getString("TRADE_ID");
        String rsrvStr1 = mainTrade.getString("RSRV_STR1");
        String rsrvStr2 = mainTrade.getString("RSRV_STR2");
        
        if(logger.isDebugEnabled())
        {
            logger.debug("<<<<<<<===mainTrade参数===>>>>>>>>>" + mainTrade);
        }
        
        if(("3927".equals(tradeTypeCode)) && StringUtils.isNotBlank(custId))
        {
        	if(StringUtils.isNotBlank(rsrvStr1) && StringUtils.equals("1", rsrvStr1))
        	{
        		IData custInfo = UcaInfoQry.qryGrpInfoByCustId(custId);
                if(IDataUtil.isNotEmpty(custInfo))
                {
                	updateCustGrpByCustId(custId,rsrvStr2);
                	custInfo.put("CITY_CODE", rsrvStr2);
                	updateGrpExt(custId);
                    syncTib(custInfo,tradeId,"测试集团局向调整时同步cust_group表数据给账务");
                }
        	}
            
        }
    }
    
    /**
     * 
     * @param custId
     * @throws Exception
     */
    private void updateCustGrpByCustId(String custId,String cityCode) throws Exception
    {
    	GrpInfoQry.updateCustGrpByCustId(custId,cityCode);
    }
    
    /**
     * 
     * @param custId
     * @throws Exception
     */
    private void updateGrpExt(String custId) throws Exception
    {
    	GrpExtInfoQry.updateGrpExtTestByCustId(custId);
    }
    
    /**
     * 同步资料
     * @param tradeTypeCode
     * @param userId
     * @param remark
     * @throws Exception
     */
    private void syncTib(IData custInfo,String tradeId, String remark) throws Exception
    {
	    //同步用户资料表信息
	    String ivSyncSequence = "";
	    ivSyncSequence = SeqMgr.getSyncIncreId();
	    
	    IData synchInfoData = new DataMap();
	    synchInfoData.put("SYNC_SEQUENCE", ivSyncSequence);
	    String syncDay = StrUtil.getAcceptDayById(ivSyncSequence);
	    synchInfoData.put("SYNC_DAY", syncDay);
	    synchInfoData.put("SYNC_TYPE", "0");
	    synchInfoData.put("TRADE_ID", tradeId);
	    synchInfoData.put("STATE", "0");
	    synchInfoData.put("SYNC_TIME", SysDateMgr.getSysTime());
	    synchInfoData.put("UPDATE_TIME", SysDateMgr.getSysTime());
	    synchInfoData.put("REMARK", remark);
	    Dao.insert("TI_B_SYNCHINFO", synchInfoData);
	    
	    custInfo.put("SYNC_SEQUENCE", ivSyncSequence);
	    custInfo.put("MODIFY_TAG", "2");
	    custInfo.put("SYNC_DAY", TimeUtil.getCurDay());
		Dao.insert("TI_B_CUST_GROUP", custInfo, Route.CONN_CRM_CG);
    }
}
