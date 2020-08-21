package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.FTTHBusiModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.requestdata.FTTHBusiModemLoseReqData;

/**
 * FTTH光猫丢失
 * @author Administrator
 *
 */
public class FTTHBusiModemLoseTrade extends BaseTrade implements ITrade
{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		FTTHBusiModemLoseReqData rd = (FTTHBusiModemLoseReqData) btd.getRD();
		String instId = rd.getInstId();
		String modermId = rd.getModermId();
		IData inParam = new DataMap();
		inParam.put("INST_ID", instId);
		inParam.put("RSRV_STR1", modermId);
		inParam.put("RSRV_VALUE_CODE","FTTH_GROUP");
		FTTHBusiModemManageBean bean= BeanManager.createBean(FTTHBusiModemManageBean.class);
		IDataset input = bean.queryModermInfoByInstId(inParam);
		if(DataSetUtils.isNotBlank(input)){
			createOtherTradeInfo(btd,input.getData(0));
		}else{
			CSAppException.appError("71340", "该光猫不能丢失处理！");
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
        otherTradeData.setEndDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setDepartId(input.getString("DEPART_ID",""));
        otherTradeData.setStaffId(input.getString("STAFF_ID",""));
        otherTradeData.setInstId(input.getString("INST_ID",""));
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL); 
        otherTradeData.setRemark(input.getString("REMARK","")); 
        otherTradeData.setRsrvStr1(input.getString("RSRV_STR1",""));//光猫串号
        otherTradeData.setRsrvStr6(input.getString("RSRV_STR6",""));//光猫型号
        otherTradeData.setRsrvStr2("0");//押金
        otherTradeData.setRsrvStr7("3");//押金状态:0,押金、1,已转移、2已退还、3,已沉淀
        otherTradeData.setRsrvStr8("");//BOSS押金转移流水
        otherTradeData.setRsrvTag1(input.getString("RSRV_TAG1",""));//申领模式
        otherTradeData.setRsrvTag2("4");//光猫状态：1:申领，2:更改，3:退还，4:丢失
        otherTradeData.setRsrvStr11(input.getString("RSRV_STR11",""));//业务类型
        otherTradeData.setRsrvStr3(input.getString("RSRV_STR3"));//宽带号码
        otherTradeData.setRsrvStr4(input.getString("RSRV_STR4"));//主号号码
        otherTradeData.setRsrvStr5(input.getString("RSRV_STR5"));//开户的TRADE_ID
        otherTradeData.setRsrvStr12(input.getString("RSRV_STR12",""));//光猫终端出库流水
        btd.add(serialNumber, otherTradeData);
    }

}
