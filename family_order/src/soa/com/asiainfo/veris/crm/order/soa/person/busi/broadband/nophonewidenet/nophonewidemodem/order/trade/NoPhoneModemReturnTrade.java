package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.trade;

import com.ailk.bizcommon.set.util.DataSetUtils;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.NoPhoneModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.requestdata.NoPhoneModemReturnReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.FTTHBusiModemManageBean;

import com.ailk.org.apache.commons.lang3.StringUtils; 

/**
 * FTTH光猫退还
 * @author Administrator
 *
 */
public class NoPhoneModemReturnTrade extends BaseTrade implements ITrade
{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		NoPhoneModemReturnReqData rd = (NoPhoneModemReturnReqData) btd.getRD();
		String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    	String userid=btd.getRD().getUca().getUser().getUserId();
    	String custid=btd.getRD().getUca().getUser().getCustId();
    	String eparchycode=btd.getRD().getUca().getUser().getEparchyCode();
    	String citycode=btd.getRD().getUca().getUser().getCityCode();
		String instId = rd.getInstId();
		String moderm_id = rd.getModermId();
		IData inParam = new DataMap();
		inParam.put("SERIAL_NUMBER", serialNumber);
		inParam.put("USER_ID", userid);
		inParam.put("INST_ID", instId);
		inParam.put("RSRV_STR1", moderm_id);
		inParam.put("RSRV_VALUE_CODE","FTTH");
		NoPhoneModemManageBean bean= BeanManager.createBean(NoPhoneModemManageBean.class);
		IDataset input = bean.queryModermInfoByInstId(inParam);
		if(DataSetUtils.isNotBlank(input)){
			String modermId = input.first().getString("RSRV_STR1","");//光猫串号
			if(modermId != null && !"".equals(modermId)){
				String kdUserId = "";
				IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
	    		if(IDataUtil.isEmpty(userInfo)){
	    			IData wideUserInfo = FTTHBusiModemManageBean.qryUserInfoBySn(serialNumber).first();
	        		if(IDataUtil.isEmpty(wideUserInfo)){
	        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未办理宽带或宽带开户未完工!");
	        		}else{
	        			kdUserId = wideUserInfo.getString("USER_ID");
	        		}
	    		}else{
	    			kdUserId = userInfo.getString("USER_ID");
	    		}
				IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("606", kdUserId, "0");
				if(DataSetUtils.isNotBlank(outDataset)){//有未完工移机业务
					String tradeId = outDataset.getData(0).getString("TRADE_ID");
					String rsrv_value_code = "FTTH";
					IDataset tradeOtherInfoSet = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,rsrv_value_code);
					if(DataSetUtils.isNotBlank(tradeOtherInfoSet)){
						int size = tradeOtherInfoSet.size();
						boolean isMove = false;
						for(int i = 0 ; i < size ; i++){
							if(StringUtils.equals(BofConst.MODIFY_TAG_UPD, tradeOtherInfoSet.getData(i).getString("MODIFY_TAG")) && StringUtils.equals(modermId, tradeOtherInfoSet.getData(i).getString("RSRV_STR1"))){
								isMove = true;
							}
						}
						if(isMove){
							CSAppException.apperr(CrmCommException.CRM_COMM_103,"该光猫是移机未完工的光猫串号，不能办理光猫退还业务!");
						}
					}
				}
				String apply_type = input.first().getString("RSRV_TAG1","");//申领模式
				if("0".equals(apply_type)){
					String deposit = input.getData(0).getString("RSRV_STR2","0");
					if(Integer.parseInt(deposit) > 0){
						//3、获取默认账户  （acct_id)
				    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
				    	String acctId=accts.getData(0).getString("ACCT_ID");
						//调账务提供的接口将宽带光猫押金存折的钱转到现金存折； 
//			    		IData inparams=new DataMap();
//			    		inparams.put("SERIAL_NUMBER", serialNumber);
//			    		inparams.put("OUTER_TRADE_ID", input.first().getString("RSRV_STR8",""));
//			    		inparams.put("DEPOSIT_CODE_OUT", "9002");
//			    		inparams.put("TRADE_FEE", deposit);
//			    		inparams.put("CHANNEL_ID", "15000");
//			    		inparams.put("SUB_SYS", "RESSERV_TF_RH_SALE_DEAL");
//			    		inparams.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
//			    		inparams.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
//			    		inparams.put("TRADE_DEPART_ID",CSBizBean.getVisit().getDepartId());
//			    		inparams.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());  
//			    		inparams.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
//			    		IData inAcct=AcctCall.transFeeOutADSL(inparams);
			    		
			    		IData params=new DataMap(); 
			    		params.put("ACCT_ID", acctId);
			    		params.put("CHANNEL_ID", "15000");
			    		params.put("PAYMENT_ID", "14");
			    		params.put("PAY_FEE_MODE_CODE", "0");
			    		params.put("FORCE_TAG", "1");//強制清退
			    		params.put("REMARK", "无手机宽带光猫押金余额退还！");
			    		IData depositeInfo=new DataMap();
			    		depositeInfo.put("DEPOSIT_CODE", "9002");
			    		depositeInfo.put("TRANS_FEE", deposit);//清退金额
			    		
			    		IDataset depositeInfos=new DatasetList();
			    		depositeInfos.add(depositeInfo);
			    		params.put("DEPOSIT_INFOS", depositeInfos);
			       		//调用接口，清退金额
			    		IData inAcct =AcctCall.foregiftDeposite(params);
			    		
			    		String callRtnType=inAcct.getString("X_RESULTCODE","");
			    		if(!"".equals(callRtnType)&&"0".equals(callRtnType)){
			    			// 成功！ 处理other表
			    			createOtherTradeInfo(btd,input.getData(0));
			    		}else{
			    			CSAppException.appError("71332", "调用接口AM_CRM_BackFee押金清退错误:"+inAcct.getString("X_RESULTINFO"));
			    		}
					}else{
						createOtherTradeInfo(btd,input.getData(0));
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
					CSAppException.appError("71332",  msg+"的光猫不予做丢失操作！");
				}
			}else{
				CSAppException.appError("71331", "光猫未录入，没有可退还光猫！");
			}
		}else{
			CSAppException.appError("71330", "该光猫信息不能退还！");
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
        otherTradeData.setRsrvStr7("2");//押金状态：0,押金、1,已转移、2已退还、3,已沉淀
        otherTradeData.setRsrvStr8(btd.getTradeId());//BOSS押金转移流水
        otherTradeData.setRsrvTag1(input.getString("RSRV_TAG1",""));//申领模式
        otherTradeData.setRsrvTag2("3");//光猫状态:1:申领，2:更改，3:退还，4:丢失
        otherTradeData.setRsrvStr11(btd.getTradeTypeCode());//业务类型
        otherTradeData.setRsrvDate3(input.getString("RSRV_DATE3",""));//预计归还时间
        otherTradeData.setRsrvStr12(input.getString("RSRV_STR12",""));//光猫终端出库流水
        otherTradeData.setRsrvTag3(input.getString("RSRV_TAG3",""));//优惠租赁标记
        btd.add(serialNumber, otherTradeData);
    }

}
