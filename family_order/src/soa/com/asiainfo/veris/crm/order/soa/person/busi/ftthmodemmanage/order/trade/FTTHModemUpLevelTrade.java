package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.trade;

import java.math.BigDecimal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.FTTHModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.requestdata.FTTHModemChangeReqData;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class FTTHModemUpLevelTrade extends BaseTrade implements ITrade
{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		FTTHModemChangeReqData rd = (FTTHModemChangeReqData) btd.getRD();
		String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
		String userid=btd.getRD().getUca().getUser().getUserId();
		String instId = rd.getInstId();
		String oldModermId = rd.getOldModermId();
		String newModermId = rd.getNewModermId();
		String newModermType = rd.getNewModermType();
		
		String depositFee = WideNetUtil.qryBalanceDepositBySn(serialNumber);
		if(new BigDecimal("10000").compareTo(new BigDecimal(depositFee))>0)
        {  
			CSAppException.appError("71354", "升级光猫需支付100元光猫调测费，余额不足！");
        }
		
		IData inParam = new DataMap();
		inParam.put("SERIAL_NUMBER", serialNumber);
		inParam.put("USER_ID", userid);
		inParam.put("INST_ID", instId);
		inParam.put("RSRV_STR1", oldModermId);
		inParam.put("RSRV_VALUE_CODE","FTTH");
		FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
		IDataset input = bean.queryModermInfoByInstId(inParam);
		if(DataSetUtils.isNotBlank(input)){
			String modermId = input.first().getString("RSRV_STR1","");//光猫串号
			if(modermId != null && !"".equals(modermId)){
				String apply_type = input.first().getString("RSRV_TAG1","");//申领模式
				if(!"3".equals(apply_type)){//自备
					IData param = input.getData(0);
					String deposit = param.getString("RSRV_STR2","");
					String depositStatus = param.getString("RSRV_STR7","");
					param.put("END_DATE", SysDateMgr.getSysTime());
					//先删除老光猫信息
					String modifyTag = BofConst.MODIFY_TAG_DEL;
					param.put("MODIFY_TAG",modifyTag);
					createOtherTradeInfo(btd,param);
					
					modifyTag = BofConst.MODIFY_TAG_ADD;
					param.put("RSRV_STR2","0"); //押金金额:清零
					param.put("RSRV_STR7","2"); //押金状态：0,押金、1,已转移、2已退还、3,已沉淀
					param.put("RSRV_TAG2","5"); //光猫状态 :1:申领，2:更改，3:退还，4:丢失 ，5:升级
					param.put("RSRV_STR1", newModermId);
					param.put("RSRV_STR6",newModermType);
					//param.put("RSRV_STR2",deposit);
					//param.put("RSRV_STR7",depositStatus);
					param.put("START_DATE",SysDateMgr.getSysTime());
					param.put("END_DATE",SysDateMgr.END_TIME_FOREVER);
					param.put("MODIFY_TAG",modifyTag);
					param.put("INST_ID", SeqMgr.getInstId());
					param.put("RSRV_STR12", btd.getTradeId());
					createOtherTradeInfo(btd,param);
					
					//绑定调测费优惠
					createDiscntTradeInfo(btd,param);
					
					this.payModermFee(btd, param);
					
				}else{
					CSAppException.appError("71352", "自备的光猫不予升级光猫！");
				}
			}else{
				CSAppException.appError("71351", "光猫未录入,不能升级光猫！");
			}
		}else{
			CSAppException.appError("71350", "该光猫不能升级！");
		}
	}
	 /**
     * 处理discnt台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createDiscntTradeInfo(BusiTradeData btd,IData input) throws Exception
    {
    	String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    	String userId = btd.getRD().getUca().getUser().getUserId();
    	DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(userId);
        newDiscnt.setProductId("-1");
        newDiscnt.setPackageId("-1");
        newDiscnt.setElementId("19082809");
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newDiscnt.setStartDate(SysDateMgr.getSysTime());
        newDiscnt.setEndDate(SysDateMgr.getAddMonthsLastDay(1));
        newDiscnt.setRemark("光猫升级绑定调测费优惠");
        btd.add(serialNumber, newDiscnt);
    	
    }
    
    /**
     * 收取100元光猫调测费，转入9440存折
     * 
     * @param btd
     * @throws Exception
     */
    private void payModermFee(BusiTradeData btd,IData input) throws Exception
    {
    	 String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    	 IData params=new DataMap(); 
		 params.put("SERIAL_NUMBER", serialNumber);
		 params.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
         params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
         params.put("TRADE_CITY_CODE",  CSBizBean.getVisit().getCityCode());
         params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
         params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
         params.put("OUTER_TRADE_ID", SeqMgr.getTradeId());
         params.put("DEPOSIT_CODE_OUT",WideNetUtil.getOutDepositCode());//账务给存折后修改
         params.put("DEPOSIT_CODE_IN", "9440");//账务给存折后修改
         params.put("TRADE_FEE", "10000");//转存金额（单位分）
         params.put("CHANNEL_ID", "15000");
        
         if(StringUtils.isNotBlank(params.getString("TRADE_FEE"))){
        	 //调用接口，将【现金类】——>【押金】
            IData resultData = AcctCall.transFeeInADSL(params);
            String result=resultData.getString("RESULT_CODE","");
            
            if("".equals(result) || !"0".equals(result))
            {
                CSAppException.appError("71352", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + params + "错误:" + resultData.getString("RESULT_INFO"));
            }
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
        otherTradeData.setRsrvStr11(input.getString("RSRV_STR11",""));//业务类型
        otherTradeData.setRsrvDate3(input.getString("RSRV_DATE3",""));//预计归还时间
        otherTradeData.setRsrvStr12(input.getString("RSRV_STR12",""));//光猫终端出库流水
        otherTradeData.setRsrvTag3(input.getString("RSRV_TAG3",""));//优惠租赁标记
        btd.add(serialNumber, otherTradeData);
    }

}
