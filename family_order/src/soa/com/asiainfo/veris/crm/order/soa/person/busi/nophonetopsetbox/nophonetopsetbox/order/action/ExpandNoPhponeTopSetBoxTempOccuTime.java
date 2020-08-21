package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetbox.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetbox.order.requestdata.NoPhoneTopSetBoxRequestData;

public class ExpandNoPhponeTopSetBoxTempOccuTime implements ITradeAction {

	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		IData requestData=btd.getRD().getPageRequestData();
		
		String tradeTypeCode=btd.getTradeTypeCode();
		
		
		if("4910".equals(tradeTypeCode)){		
			if(IDataUtil.isNotEmpty(requestData)){
				String requestSource=requestData.getString("INTERNET_TV_SOURCE","");
				boolean isExpendTempOccuTime=false;
				
				
				if(requestSource.equals("OPEN_TOPSETBOX")){	//如果是魔百和开户
					isExpendTempOccuTime=true;
				}else if(requestSource.equals("CHANGE_TOPSETBOX")){	//如果是机顶盒变更
					isExpendTempOccuTime=true;
				}
				
				
				//如果需要验证机顶盒预占时间
				if(isExpendTempOccuTime){
					
					List<ResTradeData> tradeRes=btd.get("TF_B_TRADE_RES");
					if(tradeRes!=null&&tradeRes.size()>0){
						
						String imsi=null;
						
						for(int i=0,size=tradeRes.size();i<size;i++){
							ResTradeData res=tradeRes.get(i);
							
							if(res.getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
								imsi=res.getImsi();
								break;
							}
						}
						
						if(imsi!=null&&!imsi.trim().equals("")){
							IData param=new DataMap();
							param.put("RES_NO", imsi);
							param.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
							param.put("MONTH_PICK", "1");
							
							//由于当前会话的登录信息是4900工单中营业员的登录信息，而华为的预占记录是施工人员的登录信息，所以调华为的时候，不能再用营业员信息了，置为施工人员信息
							NoPhoneTopSetBoxRequestData tsbReqData = (NoPhoneTopSetBoxRequestData) btd.getRD();
							String workStaffId = tsbReqData.getWorkStaffId();
							String workDepatrId = tsbReqData.getWorkDepatrId();
							String workCityCode = tsbReqData.getWorkCityCode();
							if(StringUtils.isNotBlank(workStaffId) && StringUtils.isNotBlank(workDepatrId) && StringUtils.isNotBlank(workCityCode))
							{
								param.put("INTERNETTV_OPEN_TAG", "1");//标记为魔百和开户
								param.put("WORK_STAFF_ID", workStaffId);
								param.put("WORK_DEPART_ID", workDepatrId);
								param.put("WORK_CITY_CODE", workCityCode);
							}
							
							//机顶盒查询
							IDataset retDataset =HwTerminalCall.expandSetTopBoxOccupyTime(param);
							if (!(DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0")))
					        {
								String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为延长预占终端时间接口调用异常！");
					            CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
					        }

						}

					}

				}
	
			}
		}
	}
	
}
