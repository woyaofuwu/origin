package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.FTTHModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.requestdata.SupplementModemCodeReqData;

/**
 * FTTH光猫补录
 * @author Administrator
 *
 */
public class SupplementModemCodeTrade extends BaseTrade implements ITrade
{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		SupplementModemCodeReqData rd = (SupplementModemCodeReqData) btd.getRD();
		String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    	String userid=btd.getRD().getUca().getUser().getUserId();
		String instId = rd.getInstId();
		String modem_id = rd.getModemId();
		IData inParam = new DataMap();
		inParam.put("SERIAL_NUMBER", serialNumber);
		inParam.put("USER_ID", userid);
		inParam.put("INST_ID", instId);
		inParam.put("RSRV_STR1", modem_id);
		if(StringUtils.equals(rd.getOperType(), "1")){
			inParam.put("RSRV_VALUE_CODE","FTTH");
		}else if(StringUtils.equals(rd.getOperType(), "2")){
			inParam.put("RSRV_VALUE_CODE","FTTH_GROUP");
		}
		FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
		IDataset input = bean.queryModermInfoByInstId(inParam);
		if(DataSetUtils.isNotBlank(input)){
			IData param = new DataMap(input.getData(0));
			param.put("RSRV_STR1", modem_id);
			param.put("RSRV_TAG4", "1");
			param.put("RSRV_TAG5", rd.getSupplementType());
			if(StringUtils.isNotBlank(rd.getModemType())){
				param.put("RSRV_STR6", rd.getModemType());
				param.put("RSRV_STR12", btd.getTradeId());
				//清空补录标记
				param.put("RSRV_TAG4", "");
			}
			createOtherTradeInfo(btd,param);
		}else{
			CSAppException.appError("71330", "该光猫信息不能补录！");
		}
	}
	
	 /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd,IData input) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();

        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode(input.getString("RSRV_VALUE_CODE",""));
        otherTradeData.setRsrvValue(input.getString("RSRV_VALUE",""));
        otherTradeData.setUserId(input.getString("USER_ID",""));
        otherTradeData.setStartDate(input.getString("START_DATE",""));
        otherTradeData.setEndDate(input.getString("END_DATE",""));
        otherTradeData.setDepartId(input.getString("DEPART_ID",""));
        otherTradeData.setStaffId(input.getString("STAFF_ID",""));
        otherTradeData.setInstId(input.getString("INST_ID",""));
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD); 
        otherTradeData.setRemark(input.getString("REMARK","")); 
        otherTradeData.setRsrvStr1(input.getString("RSRV_STR1",""));//光猫串号
        otherTradeData.setRsrvStr6(input.getString("RSRV_STR6",""));//光猫型号
        otherTradeData.setRsrvStr2(input.getString("RSRV_STR2",""));//押金
        otherTradeData.setRsrvStr7(input.getString("RSRV_STR7",""));//押金状态：0,押金、1,已转移、2已退还、3,已沉淀
        otherTradeData.setRsrvStr8(input.getString("RSRV_STR8",""));//BOSS押金转移流水
        otherTradeData.setRsrvTag1(input.getString("RSRV_TAG1",""));//申领模式
        otherTradeData.setRsrvTag2(input.getString("RSRV_TAG2",""));//光猫状态:1:申领，2:更改，3:退还，4:丢失
        otherTradeData.setRsrvStr11(input.getString("RSRV_STR11",""));//业务类型
        otherTradeData.setRsrvDate3(input.getString("RSRV_DATE3",""));//预计归还时间
        otherTradeData.setRsrvStr12(input.getString("RSRV_STR12",""));//光猫终端出库流水
        otherTradeData.setRsrvTag3(input.getString("RSRV_TAG3",""));//优惠租赁标记
        otherTradeData.setRsrvTag4(input.getString("RSRV_TAG4",""));//补录标记
        otherTradeData.setRsrvTag5(input.getString("RSRV_TAG5",""));//补录方式
        //商务光猫补录字段
        otherTradeData.setRsrvStr3(input.getString("RSRV_STR3"));//宽带号码
        otherTradeData.setRsrvStr4(input.getString("RSRV_STR4"));//主号号码
        otherTradeData.setRsrvStr5(input.getString("RSRV_STR5"));//开户的TRADE_ID
        btd.add(serialNumber, otherTradeData);
    }

}
