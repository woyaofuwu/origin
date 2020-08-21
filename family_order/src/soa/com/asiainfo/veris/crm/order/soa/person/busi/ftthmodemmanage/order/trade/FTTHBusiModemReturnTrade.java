package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.FTTHBusiModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.requestdata.FTTHBusiModemReturnReqData;

/**
 * FTTH光猫退还
 * @author Administrator
 *
 */
public class FTTHBusiModemReturnTrade extends BaseTrade implements ITrade
{

	@Override
	public void createBusiTradeData(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		FTTHBusiModemReturnReqData rd = (FTTHBusiModemReturnReqData) btd.getRD();
    	String custid=btd.getRD().getUca().getUser().getCustId();
    	String eparchycode=btd.getRD().getUca().getUser().getEparchyCode();
    	String citycode=btd.getRD().getUca().getUser().getCityCode();
		String instId = rd.getInstId();
		String moderm_id = rd.getModermId();
		IData inParam = new DataMap();
		inParam.put("INST_ID", instId);
		inParam.put("RSRV_STR1", moderm_id);
		inParam.put("RSRV_VALUE_CODE","FTTH_GROUP");
		FTTHBusiModemManageBean bean= BeanManager.createBean(FTTHBusiModemManageBean.class);
		IDataset input = bean.queryModermInfoByInstId(inParam);
		if(DataSetUtils.isNotBlank(input)){
			String modermId = input.first().getString("RSRV_STR1","");//光猫串号
			if(modermId != null && !"".equals(modermId)){
				String kdUserId = "";
				String kdSerialNumber = input.first().getString("RSRV_STR3");
				if(StringUtils.isNotBlank(kdSerialNumber)){
					IData userInfo = UcaInfoQry.qryUserInfoBySn(kdSerialNumber);
		    		if(IDataUtil.isEmpty(userInfo)){
		    			IData wideUserInfo = FTTHBusiModemManageBean.qryUserInfoBySn(kdSerialNumber).first();
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
				}
				createOtherTradeInfo(btd,input.getData(0));
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
        otherTradeData.setRsrvStr8("");//BOSS押金转移流水
        otherTradeData.setRsrvTag1(input.getString("RSRV_TAG1",""));//申领模式
        otherTradeData.setRsrvTag2("3");//光猫状态:1:申领，2:更改，3:退还，4:丢失
        otherTradeData.setRsrvStr11(input.getString("RSRV_STR11",""));//业务类型
        otherTradeData.setRsrvStr3(input.getString("RSRV_STR3"));//宽带号码
        otherTradeData.setRsrvStr4(input.getString("RSRV_STR4"));//主号号码
        otherTradeData.setRsrvStr5(input.getString("RSRV_STR5"));//开户的TRADE_ID
        otherTradeData.setRsrvStr12(input.getString("RSRV_STR12",""));//光猫终端出库流水
        btd.add(serialNumber, otherTradeData);
    }

}
