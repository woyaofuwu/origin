package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import org.apache.log4j.Logger;

import java.util.List;

public class OneNumOneDeviceOpenAction implements ITradeAction{

	private static transient Logger logger = Logger.getLogger(OneNumOneDeviceOpenAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
        logger.debug("========OneNumOneDeviceOpenAction===begin======");
		String remark =  btd.getMainTradeData().getRemark();
		if(remark == null || remark.trim().equals("")){
			return;
		}
		if(remark.endsWith("OneCardOneDevice")){ //关于下发eSIM独立号码服务（一号一终端）支撑方案的通知
			List<ResTradeData> tradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
            if(tradeDatas==null || tradeDatas.size()==0){
                return;
            }


			String newEid = "";
			String newIccid = "";
			String primarymsisdn = "";
			String newImei = "";
			for(int i=0,j=tradeDatas.size();i<j;i++){
                ResTradeData resTradeData = tradeDatas.get(i);
				if("E".equals(resTradeData.getResTypeCode()) && "0".equals(resTradeData.getModifyTag())){
					primarymsisdn = resTradeData.getRsrvStr3();
					String eidImeis = resTradeData.getRsrvStr2();
					String [] eidImei = eidImeis.split("@");
					if(eidImei.length>1){
						newEid = eidImei[0];
					}
					newImei = resTradeData.getRsrvStr4();
					newIccid = resTradeData.getResCode();
				}
			}

			if(StringUtils.isEmpty(newEid)){
				return;
			}

			IData param = new DataMap();
			param.put("msisdn", btd.getMainTradeData().getSerialNumber());
			param.put("deviceType","2");
			if(StringUtils.isNotEmpty(primarymsisdn)){//apple设备
				param.put("primarymsisdn", primarymsisdn);
				param.put("deviceType","1" );
			}
			param.put("eid", newEid);//补换卡
			param.put("imei", newImei);
			param.put("iccid1", newIccid);
			param.put("bizType", "001");//相同代表是eSIM设备新入网
			param.put("biztypeTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

            logger.debug("========OneNumOneDeviceOpenAction===param======"+param);
			//将受理工号保存至缓存，以便稍后接收profile准备结果通知
			insertESIMQRCodeInfo(param,btd.getMainTradeData().getUserId());
		}
        logger.debug("========OneNumOneDeviceOpenAction===end======");
	}

	private void insertESIMQRCodeInfo(IData param, String userId) throws Exception	{
		IData input = new DataMap();

		input.put("QR_CODE_ID", SeqMgr.getInstId());
		input.put("USER_ID", userId);
		input.put("SERIAL_NUMBER", param.getString("msisdn"));
		input.put("BIZ_TYPE", param.getString("bizType"));
		input.put("BIZ_TYPE_TIME", param.getString("biztypeTime"));
		input.put("PRIMARY_MSISDN", param.getString("primarymsisdn"));
		input.put("DEVICE_TYPE", param.getString("deviceType"));
		input.put("EID", param.getString("eid"));
		input.put("IMEI", param.getString("imei"));
		input.put("ICCID1", param.getString("iccid1"));
		input.put("ICCID2", param.getString("iccid2"));
        input.put("ACCEPT_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

		input.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); //保存补换esim设备，营业员工号
		input.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
		input.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		input.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());

		Dao.insert("TF_B_ESIM_QRCODE", input);
	}
}
