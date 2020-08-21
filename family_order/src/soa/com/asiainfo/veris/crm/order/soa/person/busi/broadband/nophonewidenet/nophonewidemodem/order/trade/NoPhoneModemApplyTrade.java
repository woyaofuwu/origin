
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.trade;

import com.ailk.bizcommon.set.util.DataSetUtils;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr; 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.ailk.org.apache.commons.lang3.StringUtils; 
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.NoPhoneModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.requestdata.NoPhoneModemApplyReqData;

/**
 * REQ201505210004 FTTH光猫申领
 * 
 * @author chenxy3 2015-6-1
 */
public class NoPhoneModemApplyTrade extends BaseTrade implements ITrade
{
	String deposit="";//押金
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    { 
    	NoPhoneModemApplyReqData rd = (NoPhoneModemApplyReqData) btd.getRD();
    	String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    	
    	//1、先判断用户存在那种产品，只开通FTTH宽带押金200；开通宽带且办理宽带1+押金100    		
		NoPhoneModemManageBean bean= BeanManager.createBean(NoPhoneModemManageBean.class);
		//无手机宽带光猫申领不走调用接口，前台feemgr组件已经处理。
		String applyType = rd.getApply_type();
		deposit=rd.getDeposit();
		createOtherTradeInfo(btd,rd); 
//		if("1".equals(applyType) || "2".equals(applyType)){
//			deposit = "0";
//			createOtherTradeInfo(btd,rd); 
//		}else{
//			//已存在宽带产品，需要判断用户的现金是否足够
//	    	//4、调接口判断用户的现金是否足够，不够则提示缴费，不登记台账；调用接口
//	    	IDataset checkCash= AcctCall.queryAccountDepositBySn(serialNumber);
//	    	int cash = 0;
//	    	if(DataSetUtils.isNotBlank(checkCash)){
//	    		for(int i = 0 ; i < checkCash.size() ; i++){
//	    			cash = cash + Integer.parseInt(checkCash.getData(i).getString("DEPOSIT_BALANCE","0"));
//	    		}
//	    	}
//	    	if(cash<Integer.parseInt(deposit)){
//	    		CSAppException.appError("61311", "账户存折可用余额不足，请先办理缴费。账户余额："+Double.parseDouble(String.valueOf(cash))/100+"元，押金金额："+Integer.parseInt(deposit)/100+"元");
//	    	}else{
//	    		//5、调账务提供的接口将现金存折的钱转到宽带光猫押金存折； 
//	    		IData inparams=new DataMap();
//	    		//获取转账存折
//				StringBuffer depositeNotes=new StringBuffer();
//				IDataset noteDatas=CommparaInfoQry.getCommNetInfo("CSM", "1627", "TOP_SET_BOX_NOTES");
//				if(IDataUtil.isNotEmpty(noteDatas)){
//					
//					for(int i=0,size=noteDatas.size();i<size;i++){
//						IData noteData=noteDatas.getData(i);
//						
//						depositeNotes.append(noteData.getString("PARA_CODE1"));
//						if(i<size-1){
//							depositeNotes.append("|");
//						}
//					}
//				}else{
//					CSAppException.appError("39102", "取转账存折错误！");
//				}
//	    		inparams.put("SERIAL_NUMBER", serialNumber);
//	    		inparams.put("OUTER_TRADE_ID", btd.getRD().getTradeId());
//	    		inparams.put("DEPOSIT_CODE_OUT", depositeNotes.toString());
//	    		inparams.put("DEPOSIT_CODE_IN", "9002"); 
//	    		inparams.put("TRADE_FEE", deposit);
//	    		inparams.put("CHANNEL_ID", "15000");
//	    		inparams.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
//	    		inparams.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId()); 
//	    		inparams.put("TRADE_DEPART_ID",CSBizBean.getVisit().getDepartId());
//	    		inparams.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()); 
//	    		inparams.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
//	    		IData inAcct=AcctCall.transFeeInADSL(inparams);
//	    		String result=inAcct.getString("RESULT_CODE","");
//	    		if(!"".equals(result) && "0".equals(result)){
//	    			// 成功！ 处理other表
//	    			rd.setDepositTradeId(btd.getTradeId());
//	    	        createOtherTradeInfo(btd,rd); 
//	    		}else{
//	    			CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金错误:"+inAcct.getString("RESULT_INFO"));
//	    		}
//	    	}
//		}  	
    } 

    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd,NoPhoneModemApplyReqData rd) throws Exception
    {
        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();

        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("FTTH");
        otherTradeData.setRsrvValue("FTTH光猫申领");
        otherTradeData.setUserId(rd.getUca().getUser().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        otherTradeData.setRemark(rd.getRemark()); 
        otherTradeData.setRsrvStr2(deposit);//押金
        otherTradeData.setRsrvStr7("0");//押金状态:0,押金、1,已转移、2已退还、3,已沉淀
        otherTradeData.setRsrvStr8(rd.getDepositTradeId());//BOSS押金转移流水
        otherTradeData.setRsrvTag1(rd.getApply_type());//申领模式  0租赁，1购买，2赠送
        otherTradeData.setRsrvTag2("1");//光猫状态：1:申领，2:更改，3:退还，4:丢失
        otherTradeData.setRsrvStr11(btd.getTradeTypeCode());//业务类型
        otherTradeData.setRsrvStr1(rd.getModermId());//光猫串号
        otherTradeData.setRsrvStr6(rd.getModermType());//光猫型号
        otherTradeData.setRsrvDate3(rd.getReturnDate());//预计归还时间
        otherTradeData.setRsrvStr12(btd.getTradeId());//光猫终端出库流水
        IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "6131", "2", "0898");
		String money =paras.getData(0).getString("PARA_CODE1");
        if(StringUtils.equals("0", rd.getApply_type()) && Integer.parseInt(deposit) < Integer.parseInt(money)){
        	otherTradeData.setRsrvTag3("1");//优惠租赁标记
        }
        btd.add(serialNumber, otherTradeData);
    }
}
