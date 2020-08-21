
package com.asiainfo.veris.crm.order.soa.group.internetofthings;


import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpExtInfoQry;

/**
 * 物联网EC同步，服务开通回复接口
 * 
 * @author
 */
public class IotECSyncBackpfSVC extends CSBizService
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 更新同步状态
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset updSyncState(IData data) throws Exception
    {
        String custId = data.getString("CUST_ID");
        String tradeId = data.getString("TRADE_ID");
        IDataset extendLists = GrpExtInfoQry.getExtendLists(custId);
        IDataset tempSync = DataHelper.filter(extendLists, "EXTEND_TAG=SYNSTATE");
        IData param = new DataMap();
        param.put("EXTEND_VALUE", custId);
        param.put("TRADE_ID", tradeId);
        IDataset tradeInfo = GrpExtInfoQry.queryWLWInfoByTradeId(param);
        String rsrvStr34 = null;
        String rsrvTag5 = null;
        if(IDataUtil.isEmpty(tradeInfo)){
        	rsrvStr34 = "物联网EC信息同步成功";
        	rsrvTag5 = "1";
        }else{
        	rsrvStr34 = "物联网EC信息注销成功";
        	rsrvTag5 = "0";
        }
        if (IDataUtil.isEmpty(tempSync))
        {
            String extendId = SeqMgr.getExtendId();
            param.put("EXTEND_ID", extendId);
            param.put("RSRV_DATE15", SysDateMgr.getSysTime());
            param.put("RSRV_STR34", rsrvStr34);
            param.put("RSRV_TAG5", rsrvTag5);
            GrpExtInfoQry.insertIotECExtendSync(param);
        }
        else
        {
        	param.put("RSRV_DATE15", SysDateMgr.getSysTime());
        	param.put("RSRV_STR34", rsrvStr34);
        	param.put("RSRV_TAG5", rsrvTag5);
            GrpExtInfoQry.updateIotECExtendSync(param);
        }
        return null;
    }
    
    /**
     * 更新同步状态
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset updSyncFailState(IData data) throws Exception
    {
        String custId = data.getString("CUST_ID");
        String tradeId = data.getString("TRADE_ID");
        IDataset extendLists = GrpExtInfoQry.getExtendLists(custId);
        IDataset tempSync = DataHelper.filter(extendLists, "EXTEND_TAG=SYNSTATE");
        IData param = new DataMap();
        param.put("EXTEND_VALUE", custId);
        param.put("TRADE_ID", tradeId);
        IDataset tradeInfo = GrpExtInfoQry.queryWLWInfoByTradeId(param);
        String rsrvStr34 = data.getString("FAIL_DESCINFO","");
        if(StringUtils.isBlank(rsrvStr34))
    	{
        	rsrvStr34 = "物联网EC信息同步失败";
    	}
        if(IDataUtil.isEmpty(tradeInfo)){
        	
        }else{
        	rsrvStr34 = "物联网EC信息注销失败";
        }
        if (IDataUtil.isEmpty(tempSync))
        {
            String extendId = SeqMgr.getExtendId();
            param.put("EXTEND_ID", extendId);
            param.put("RSRV_STR34", rsrvStr34);
            param.put("RSRV_DATE15", SysDateMgr.getSysTime());
            GrpExtInfoQry.insertIotECFailExtendSync(param);
        }
        else
        {
        	param.put("RSRV_STR34", rsrvStr34);
            param.put("RSRV_DATE15", SysDateMgr.getSysTime());
            GrpExtInfoQry.updateIotECFailExtendSync(param);
        }
        return null;
    }
}
