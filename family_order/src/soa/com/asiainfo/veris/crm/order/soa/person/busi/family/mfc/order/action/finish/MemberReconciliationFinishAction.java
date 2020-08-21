package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.order.action.finish;

import org.apache.log4j.Logger;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.MfcCommonUtil;

public class MemberReconciliationFinishAction implements ITradeFinishAction
{
    private static transient Logger log = Logger.getLogger(MemberReconciliationFinishAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
    	String tradeType = mainTrade.getString("TRADE_TYPE_CODE");
        if (log.isDebugEnabled())
        {
            log.debug("11111111111111111111111111MemberReconciliationFinishAction11111111111111tradeType="+tradeType);
        }
        if("2584".equals(tradeType) || "2585".equals(tradeType))
        {//停复机登记对账
        	changeMfcState(tradeType,mainTrade);
        }
        else
        {//成员操作对账处理
        	dealMemberSynInfo(mainTrade);
        }
    }
    
    private void dealMemberSynInfo(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset relaUUDatas = MfcCommonUtil.getUserRelationByTradeId(tradeId);
        if(DataUtils.isNotEmpty(relaUUDatas))
        {
        	if (log.isDebugEnabled())
        	{
        		log.debug("11111111111111111111111111MemberReconciliationFinishAction11111111111111relaUUDatas="+relaUUDatas);
        	}
        	IData param = new DataMap();
        	param.put("PRODUCT_CODE", mainTrade.getString("RSRV_STR1", ""));//产品编码
        	param.put("ACTION", mainTrade.getString("RSRV_STR2", ""));//操作类型
        	param.put("CUSTOMER_PHONE", mainTrade.getString("RSRV_STR3", ""));//主号号码
        	param.put("BUSINESS_TYPE", mainTrade.getString("RSRV_STR4", ""));//业务类型
        	param.put("BIZ_VERSION", mainTrade.getString("RSRV_STR5", ""));//业务版本号
        	String sysTime = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        	String oprTime = "";
        	if("1".equals(mainTrade.getString("RSRV_STR6")))//对账标记
			{
				String lastTime = SysDateMgr.decodeTimestamp(SysDateMgr.addDays(-1),SysDateMgr.PATTERN_TIME_YYYYMMDD);
				oprTime = lastTime+sysTime.substring(8, sysTime.length());
			}
        	for(int i=0;i<relaUUDatas.size();i++)
        	{
        		IData relation = relaUUDatas.getData(i);
        		if("51".equals(mainTrade.getString("RSRV_STR2", "")))
        		{//删除操作，获取新增对账记录，有则更新失效时间，无则不处理
        			IData input = new DataMap();
        			input.put("MEM_NUMBER", relation.getString("SERIAL_NUMBER_B", ""));
        			input.put("CUSTOMER_PHONE", mainTrade.getString("RSRV_STR3", ""));
        			input.put("PRODUCT_OFFERING_ID", relation.getString("RSRV_STR2", ""));
        			IDataset membList = Dao.qryByCodeParser("TI_B_MFC_SYNC", "SEL_MFC_BYMEMNUM", input,Route.CONN_CRM_CEN);
        			if(DataUtils.isNotEmpty(membList))
        			{
        				input.put("OPR_TIME", SysDateMgr.decodeTimestamp(relation.getString("RSRV_DATE3",""),SysDateMgr.PATTERN_STAND_SHORT));
        				if("1".equals(mainTrade.getString("RSRV_STR6")))//对账标记
        				{//对账需要将操作时间改为前一天时间
        					input.put("OPR_TIME", oprTime);
        					input.put("RSRV_STR1", "ACCT");//对账标记
        				}
        				input.put("SEQ_ID", membList.getData(0).getString("SEQ_ID"));
        				param.put("FINISH_TIME", SysDateMgr.decodeTimestamp(relation.getString("RSRV_DATE3",""),SysDateMgr.PATTERN_STAND_SHORT));//归档时间
        				input.put("EXP_TIME", SysDateMgr.decodeTimestamp(relation.getString("RSRV_DATE3"),SysDateMgr.PATTERN_STAND_SHORT));
        				input.put("ACTION", "51");
        				if("02".equals(relation.getString("RSRV_STR5")))
        				{//取消业务保存操作类型为52
        					input.put("ACTION", "52");
        				}
        				MfcCommonUtil.updateSync(input);
        			}
        		}
        		else
        		{
        			param.put("PARTITION_ID", SysDateMgr.getCurMonth());
        			param.put("SEQ_ID", SeqMgr.getInstId());
        			param.put("PRODUCT_OFFERING_ID", relation.getString("RSRV_STR2", ""));//业务订购实例ID
        			param.put("MEM_TYPE", relation.getString("RSRV_STR3", ""));//成员类型
        			param.put("MEM_NUMBER", relation.getString("SERIAL_NUMBER_B", ""));//成员号码
        			param.put("MEM_AREA_CODE", relation.getString("RSRV_STR4", ""));//成员区号
        			param.put("EFF_TIME", SysDateMgr.decodeTimestamp(relation.getString("RSRV_DATE1"),SysDateMgr.PATTERN_STAND_SHORT));//生效时间
        			param.put("EXP_TIME", SysDateMgr.decodeTimestamp(relation.getString("RSRV_DATE2"),SysDateMgr.PATTERN_STAND_SHORT));//失效时间
        			param.put("FINISH_TIME", SysDateMgr.decodeTimestamp(relation.getString("RSRV_DATE3",""),SysDateMgr.PATTERN_STAND_SHORT));//归档时间
        			param.put("ACT_TAG", "0");//初始状态
        			param.put("OPR_TIME",SysDateMgr.decodeTimestamp(relation.getString("RSRV_DATE3",""),SysDateMgr.PATTERN_STAND_SHORT));//初始状态
        			if("1".equals(mainTrade.getString("RSRV_STR6")))//对账标记
        			{
        				param.put("OPR_TIME", oprTime);
        				param.put("RSRV_STR1", "ACCT");//对账标记
        			}
        			Dao.insert("TI_B_MFC_SYNC", param,Route.CONN_CRM_CEN);
        		}
        	}
        }
	}

	private void changeMfcState(String tradeType,IData mainTrade) throws Exception
	{
		String custPhone = mainTrade.getString("RSRV_STR1");//停机主号码
		String sysTime = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
		IData input = new DataMap();
		input.put("CUSTOMER_PHONE", custPhone);
		input.put("OPR_TIME", sysTime);
		input.put("FINISH_TIME", sysTime);
		input.put("PRODUCT_OFFERING_ID", mainTrade.getString("RSRV_STR3"));
		if("2584".equals(tradeType))
		{//停机,需要根据失效时间判断
			if("1".equals(mainTrade.getString("RSRV_STR6")))//对账标记
			{
				String oprTime = SysDateMgr.decodeTimestamp(SysDateMgr.addDays(-1),SysDateMgr.PATTERN_TIME_YYYYMMDD);
				oprTime += sysTime.substring(8, sysTime.length());
				input.put("OPR_TIME", oprTime);
				input.put("RSRV_STR1", "ACCT");//对账标记
			}
			input.put("EXP_TIME", SysDateMgr.decodeTimestamp(SysDateMgr.getLastDateThisMonth(),SysDateMgr.PATTERN_STAND_SHORT));
			input.put("ACTION", "53");
			input.put("SYS_DATE", SysDateMgr.getSysTime());//有效成员数据修改操作类型
		}
		else
		{//复机
			input.put("EXP_TIME", "20991231235959");
			input.put("ACTION", "50");//复机后为新增状态
			input.put("BEFORE_ACTION", "53");//停机号码做复机
		}
		MfcCommonUtil.updateSync(input);
	}
}
