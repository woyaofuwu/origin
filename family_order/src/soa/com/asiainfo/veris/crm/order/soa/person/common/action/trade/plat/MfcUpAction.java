
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.MfcCommonUtil;

/**
 * 3.15省内订阅数据上传
 * @author zhuwj
 *
 */

public class MfcUpAction implements ITradeAction
{
    private static Logger log = Logger.getLogger(MfcUpAction.class);

	@Override
	public void executeAction(BusiTradeData btd) throws Exception
	{
		IData map =new DataMap();
		IDataset resultMapList =  new DatasetList();
		map.put("CUSTOMER_PHONE","18789714824");
		map.put("MEM_NUMBER", "18789714824");
		//只上传省内亲情网的数据 a.product_code='MFC999999'，rsrv_str10=1为查询订阅过的数据
		IDataset mfcinfo =MfcCommonUtil.getDataUp(map);
		IData result =new DataMap();
		result.put("BUSINESS_TYPE", "1");
		result.put("PRODUCT_CODE", mfcinfo.getData(0).getString("PRODUCT_CODE"));
		result.put("PRODUCT_OFFERING_ID", mfcinfo.getData(0).getString("PRODUCT_OFFERING_ID"));
		result.put("POID_CODE", mfcinfo.getData(0).getString("POID_CODE"));
		result.put("POID_LABLE", mfcinfo.getData(0).getString("POID_LABLE"));
		result.put("CUSTOMER_PHONE", mfcinfo.getData(0).getString("CUSTOMER_PHONE"));
		result.put("BIZ_VERSION", "1.0.0");
		for(int i=0;mfcinfo.size()>i;i++){
			IData memBerList =new DataMap();
			memBerList.put("MEM_FLAG", mfcinfo.getData(i).getString("ROLE_CODE_B"));//主副号标识
			String memFlag= mfcinfo.getData(i).getString("ROLE_CODE_B");
			if(memFlag.equals("1")){
				memBerList.put("MEM_NUMBER", mfcinfo.getData(i).getString("CUSTOMER_PHONE"));
			}else{
				memBerList.put("MEM_NUMBER", mfcinfo.getData(i).getString("MEM_NUMBER"));
			}
			memBerList.put("MEM_TYPE", "1");//1是移动号码，3是异网号码
			memBerList.put("MEM_LABLE", mfcinfo.getData(i).getString("MEM_LABLE"));
			
			//时间最大长度14位最小长度14位
			SimpleDateFormat format =new SimpleDateFormat("yyyyMMddHHmmss");
			Date date1=format.parse(mfcinfo.getData(i).getString("EFF_TIME"));
			memBerList.put("EFF_TIME", format.format(date1));
			Date date2=format.parse(mfcinfo.getData(i).getString("EXP_TIME"));
			memBerList.put("EXP_TIME", format.format(date2));
			resultMapList.add(memBerList);
			
		}
		result.put("MEMBER_LIST", resultMapList);
		IDataset resultMapList1 =  new DatasetList();
		result.put("KIND_ID", "MFCSubscribeSync_BBOSS_0_0");
		
		IDataset responseInfo =IBossCall.dealInvokeUrl("MFCSubscribeSync_BBOSS_0_0", "IBOSS", result);
		

	
   }
}
