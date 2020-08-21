
package com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log.SimCardLogQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import org.apache.log4j.Logger;

/**
 * sim卡占用
 */
public class SimCardFinishAction implements ITradeFinishAction
{
    protected static Logger log = Logger.getLogger(SimCardFinishAction.class);

    public void executeAction(IData mainTrade) throws Exception
    {
        log.error("========SimCardFinishAction===begin======");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String simCardNo = mainTrade.getString("RSRV_STR10");
        String tradeId = mainTrade.getString("TRADE_ID");
        String netTypeCode = mainTrade.getString("NET_TYPE_CODE");
        String tradeTypeCode = mainTrade.getString("NET_TYPE_CODE");
        String userId = mainTrade.getString("USER_ID");
        String tradeType = mainTrade.getString("TRADE_TYPE_CODE");
        String simNoOccupyTag  = mainTrade.getString("RSRV_STR4");
        String remark = mainTrade.getString("REMARK");
        boolean isNoOccupy = ("142".equals(tradeType) && "1".equals(simNoOccupyTag));//跨区补区功能不占用卡

        log.error("========SimCardFinishAction===isNoOccupy="+isNoOccupy);

        if (!isNoOccupy)  {
            log.error("========SimCardFinishAction===remark="+remark);

            if(StringUtils.isNotBlank(remark) && "OneNoOneTerminal".equals(remark)){
            	//向一级能力开放平台发起补换eSIM成功通知，并发起Profile准确的请求
            	oneNoOneTerminal(mainTrade);
            }
            	// 资源信息占用
            ResCall.changeSimCardFinish("0", serialNumber, simCardNo, "0", tradeId, tradeTypeCode, "1", userId, "0", "0", mainTrade.getString("PRODUCT_ID"), netTypeCode);
            // 修改操作日子TL_B_ABNORMAL_OPER
            SimCardLogQry.updRecordBySerialNumber(serialNumber, mainTrade.getString("UPDATE_STAFF_ID"), "142");
            SimCardLogQry.insRecordBySerialNumber(serialNumber, mainTrade.getString("UPDATE_STAFF_ID"), "142", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND), mainTrade.getString("TRADE_EPARCHY_CODE"));
        }
        log.error("========SimCardFinishAction===end======");

    }

	private void oneNoOneTerminal(IData mainTrade) throws Exception {
        log.error("=======private=oneNoOneTerminal===begin=");

		String oldIccid = mainTrade.getString("RSRV_STR9");
		String newEid = "";
		String newIccid = mainTrade.getString("RSRV_STR10");
		String primarymsisdn = "";
		String imei = "";
		String oldEid="";
		IDataset resTrades = TradeResInfoQry.queryAllTradeResByTradeId(mainTrade.getString("TRADE_ID"));
		for(int i=0,j=resTrades.size();i<j;i++){
			IData resTrade = resTrades.getData(i);
			if("E".equals(resTrade.getString("RES_TYPE_CODE")) && "OneNoOneTerminal".equals(resTrade.getString("RSRV_STR1"))){
				String [] newEids = resTrade.getString("RSRV_STR2").split("@");
				newEid = newEids[0];
                primarymsisdn = resTrade.getString("RSRV_STR3");
				imei = resTrade.getString("RSRV_STR5");
                oldEid = resTrade.getString("RSRV_STR4");
			}
		}
		IData param = new DataMap();
		param.put("msisdn", mainTrade.getString("SERIAL_NUMBER"));
        log.error("=======private=oneNoOneTerminal===primarymsisdn="+primarymsisdn);
		if(StringUtils.isNotEmpty(primarymsisdn)){
            param.put("primarymsisdn", primarymsisdn);
            param.put("deviceType", "1");
        }else{
            param.put("deviceType", "2");
        }
		param.put("eid", newEid);
        param.put("imei", imei);
		param.put("iccid1", newIccid);
		log.error("=======private=oneNoOneTerminal===oldEid="+oldEid+";newEid="+newEid);
		if(StringUtils.equals(oldEid,newEid)){
            param.put("bizType", "002");//补写卡
        }else {
            param.put("bizType", "003");//补换设备
        }
//		param.put("eid2", newEid);
		param.put("iccid2", oldIccid);
		param.put("biztypeTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		log.error("=======private=oneNoOneTerminal===param="+param);
		
		IData paramurl = new DataMap();
        paramurl.put("PARAM_NAME", "crm.ABILITY.CIP127");
        IDataset urls = Dao.qryBySql(AbilityEncrypting.getInterFaceSQL, paramurl, "cen");
        String url = "";
        log.error("=======private=oneNoOneTerminal===urls="+urls);

        if (urls != null && urls.size() > 0)
        {
           url = urls.getData(0).getString("PARAM_VALUE", "");
         }
         else
         {
             CSAppException.appError("-1", "crm.feedback接口地址未在TD_S_BIZENV表中配置");
         }
        
        String apiAddress = url;

		AbilityEncrypting.callAbilityPlatCommon(apiAddress,param);

        log.error("=======private=oneNoOneTerminal===end=");

	}

}
