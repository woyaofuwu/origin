
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.trade;

import com.ailk.bizcommon.set.util.DataSetUtils;
import com.ailk.bizcommon.util.SysDateMgr; 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;  
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.NoPhoneModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.requestdata.NoPhoneModemChangeReqData;

public class NoPhoneModemChangeTrade extends BaseTrade implements ITrade
{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		NoPhoneModemChangeReqData rd = (NoPhoneModemChangeReqData) btd.getRD();
		String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
		String userid=btd.getRD().getUca().getUser().getUserId();
		String instId = rd.getInstId();
		String oldModermId = rd.getOldModermId();
		String newModermId = rd.getNewModermId();
		String newModermType = rd.getNewModermType();
		IData inParam = new DataMap();
		inParam.put("SERIAL_NUMBER", serialNumber);
		inParam.put("USER_ID", userid);
		inParam.put("INST_ID", instId);
		inParam.put("RSRV_STR1", oldModermId);
		inParam.put("RSRV_VALUE_CODE","FTTH");
		NoPhoneModemManageBean bean= BeanManager.createBean(NoPhoneModemManageBean.class);
		IDataset input = bean.queryModermInfoByInstId(inParam);
		if(DataSetUtils.isNotBlank(input)){
			String modermId = input.first().getString("RSRV_STR1","");//光猫串号
			if(modermId != null && !"".equals(modermId)){
				String apply_type = input.first().getString("RSRV_TAG1","");//申领模式
				if(!"3".equals(apply_type)){//自备
					IData param = input.getData(0);
					String deposit = param.getString("RSRV_STR2","");
					String depositStatus = param.getString("RSRV_STR7","");
					String end_date = param.getString("END_DATE","");
					//先删除老光猫信息
					String modifyTag = BofConst.MODIFY_TAG_DEL;
					param.put("RSRV_STR2","0"); //押金金额:清零
					param.put("RSRV_STR7","1"); //押金状态：0,押金、1,已转移、2已退还、3,已沉淀
					param.put("RSRV_TAG2","2"); //光猫状态 :1:申领，2:更改，3:退还，4:丢失
					param.put("END_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
					param.put("MODIFY_TAG",modifyTag);
					createOtherTradeInfo(btd,param);
					modifyTag = BofConst.MODIFY_TAG_ADD;
					param.put("RSRV_STR1", newModermId);
					param.put("RSRV_STR2",deposit);
					param.put("RSRV_STR6",newModermType);
					param.put("RSRV_STR7",depositStatus);
					param.put("END_DATE",end_date);
					param.put("RSRV_TAG2","1");
					param.put("MODIFY_TAG",modifyTag);
					param.put("INST_ID", SeqMgr.getInstId());
					param.put("RSRV_STR12", btd.getTradeId());
					createOtherTradeInfo(btd,param);
				}else{
					CSAppException.appError("71322", "自备的光猫不予更换光猫！");
				}
			}else{
				CSAppException.appError("71321", "光猫未录入,不能更换光猫！");
			}
		}else{
			CSAppException.appError("71320", "该光猫不能更换！");
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
        otherTradeData.setInstId(input.getString("INST_ID",""));// 新增 则需要新增inst_id值 
        otherTradeData.setModifyTag(input.getString("MODIFY_TAG","")); 
        otherTradeData.setRemark(input.getString("REMARK","")); 
        otherTradeData.setRsrvStr1(input.getString("RSRV_STR1",""));//光猫串号
        otherTradeData.setRsrvStr6(input.getString("RSRV_STR6",""));//光猫型号
        otherTradeData.setRsrvStr2(input.getString("RSRV_STR2",""));//押金
        otherTradeData.setRsrvStr7(input.getString("RSRV_STR7",""));//押金状态
        otherTradeData.setRsrvStr8(input.getString("RSRV_STR8",""));//BOSS押金转移流水
        otherTradeData.setRsrvTag1(input.getString("RSRV_TAG1",""));//申领模式
        otherTradeData.setRsrvTag2(input.getString("RSRV_TAG2",""));//光猫状态
        otherTradeData.setRsrvStr11(btd.getTradeTypeCode());//业务类型
        otherTradeData.setRsrvDate3(input.getString("RSRV_DATE3",""));//预计归还时间
        otherTradeData.setRsrvStr12(input.getString("RSRV_STR12",""));//光猫终端出库流水
        otherTradeData.setRsrvTag3(input.getString("RSRV_TAG3",""));//优惠租赁标记
        btd.add(serialNumber, otherTradeData);
    }

}
