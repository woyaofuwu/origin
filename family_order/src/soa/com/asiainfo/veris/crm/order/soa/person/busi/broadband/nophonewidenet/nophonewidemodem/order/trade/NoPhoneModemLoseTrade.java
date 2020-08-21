package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.trade;

import com.ailk.bizcommon.set.util.DataSetUtils;
import com.ailk.bizcommon.util.SysDateMgr; 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;  
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.NoPhoneModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.requestdata.NoPhoneModemLoseReqData;

/**
 * FTTH光猫丢失
 * @author Administrator
 *
 */
public class NoPhoneModemLoseTrade extends BaseTrade implements ITrade
{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		NoPhoneModemLoseReqData rd = (NoPhoneModemLoseReqData) btd.getRD();
		String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    	String userid=btd.getRD().getUca().getUser().getUserId();
    	String custid=btd.getRD().getUca().getUser().getCustId();
    	String eparchycode=btd.getRD().getUca().getUser().getEparchyCode();
    	String citycode=btd.getRD().getUca().getUser().getCityCode();
		String instId = rd.getInstId();
		String modermId = rd.getModermId();
		IData inParam = new DataMap();
		inParam.put("SERIAL_NUMBER", serialNumber);
		inParam.put("USER_ID", userid);
		inParam.put("INST_ID", instId);
		inParam.put("RSRV_STR1", modermId);
		inParam.put("RSRV_VALUE_CODE","FTTH");
		NoPhoneModemManageBean bean= BeanManager.createBean(NoPhoneModemManageBean.class);
		IDataset input = bean.queryModermInfoByInstId(inParam);
		if(DataSetUtils.isNotBlank(input)){
			String apply_type = input.first().getString("RSRV_TAG1","");//申领模式
			if("0".equals(apply_type)){
				String deposit = input.getData(0).getString("RSRV_STR2","0");
				if(Integer.parseInt(deposit) > 0){//有押金才做押金处理
					String sysdate=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
					String modemStartDate = input.getData(0).getString("START_DATE",SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
					int years=SysDateMgr.yearInterval(modemStartDate, sysdate)+1;
					if(years < 3){//用户申领光猫小于三年丢失，押金沉淀处理
						//3、获取默认账户  （acct_id)
				    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
				    	String acctId=accts.getData(0).getString("ACCT_ID");
				    	IData inparams=new DataMap();
			    		inparams.put("USER_ID", userid);
			    		inparams.put("ACCT_ID", acctId);
			    		inparams.put("SERIAL_NUMBER", serialNumber); 
			    		inparams.put("TRADE_FEE", deposit);
			    		IData inAcct=AcctCall.AMBackFee(inparams);
			    		String result=inAcct.getString("RESULT_CODE","");
			    		if(!"".equals(result) && "0".equals(result)){
			    			// 成功！ 处理other表
			    			createOtherTradeInfo(btd,input.getData(0));
			    		}else{
			    			CSAppException.appError("61312", "调用接口AM_CRM_GMFeeDeposit沉淀押金错误:"+inAcct.getString("RESULT_INFO"));
			    		}
					}else{//大于三年丢失，押金退还处理
						IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
				    	String acctId=accts.getData(0).getString("ACCT_ID");  
				    	IData params=new DataMap(); 
						params.put("ACCT_ID", acctId);
						params.put("CHANNEL_ID", "15000");
						params.put("PAYMENT_ID", "100021");
						params.put("PAY_FEE_MODE_CODE", "0");
						params.put("REMARK", "无手机宽带退还光猫押金退费！");
						IData depositeInfo=new DataMap();
						depositeInfo.put("DEPOSIT_CODE", "9002");
						depositeInfo.put("TRANS_FEE", deposit);
						
						IDataset depositeInfos=new DatasetList();
						depositeInfos.add(depositeInfo);
						params.put("DEPOSIT_INFOS", depositeInfos);
				        CSBizBean.getVisit().setStaffEparchyCode("0898");
				   		//调用接口，将【押金】退费
						IData inAcct =AcctCall.foregiftDeposite(params);
						String callRtnType=inAcct.getString("X_RESULTCODE","");
						if(!"".equals(callRtnType)&&"0".equals(callRtnType)){ 
						}else{  
			            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用账务接口退订光猫押金失败:" + inAcct.getString("X_RESULTINFO",""));
						} 
					}
				}
			}else{
				String msg = "";
				if("1".equals(apply_type)){
					msg = "购买";
				}else if("2".equals(apply_type)){
					msg = "赠送";
				}
				else if("3".equals(apply_type)){
					msg = "自备";
				}else{
					msg = "非租赁";
				}
				CSAppException.appError("71332", msg+"的光猫不予做丢失操作！");
			}
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
        otherTradeData.setRsrvStr8(btd.getTradeId());//BOSS押金转移流水
        otherTradeData.setRsrvTag1(input.getString("RSRV_TAG1",""));//申领模式
        otherTradeData.setRsrvTag2("4");//光猫状态：1:申领，2:更改，3:退还，4:丢失
        otherTradeData.setRsrvStr11(btd.getTradeTypeCode());//业务类型
        otherTradeData.setRsrvDate3(input.getString("RSRV_DATE3",""));//预计归还时间
        otherTradeData.setRsrvStr12(input.getString("RSRV_STR12",""));//光猫终端出库流水
        otherTradeData.setRsrvTag3(input.getString("RSRV_TAG3",""));//优惠租赁标记
        btd.add(serialNumber, otherTradeData);
    }

}
