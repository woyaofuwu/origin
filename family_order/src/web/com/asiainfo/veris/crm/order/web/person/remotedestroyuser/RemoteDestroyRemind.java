package com.asiainfo.veris.crm.order.web.person.remotedestroyuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class RemoteDestroyRemind extends PersonBasePage {
	public abstract void setCondition(IData condition);
	public abstract void setRowIndex(int rowIndex);
	public abstract void setCountTrade(long countTrade);
	public abstract void setDestroyInfoList(IDataset tradeInfoList);
	public abstract void setDestroyInfo(IData destroyInfo);
	public abstract void setCount(long count);
	public void queryRemindOrder(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
		IDataset destroyInfoList = new DatasetList();
		String destroyRemindTag = pageData.getString("DESTROY_REMIND_TAG");
		String serialNO = pageData.getString("REMOTE_SERIAL_NUMBER");
		pageData.put("DEAL_TAG", "1");
		//调iboss查未催单的工单
		if(StringUtils.isNotBlank(serialNO)&&"0".equals(destroyRemindTag)){
			//调后台服务
			IDataOutput out = CSViewCall.callPage(this, "SS.RemoteDestroyUserSVC.queryDestroyOrder", pageData, getPagination("nav"));
			IDataset result = out.getData();
			if(IDataUtil.isNotEmpty(result)){
				IData sendOrderInfo = new DataMap();
				for(int k=0;k<result.size();k++){
					if(StringUtils.isBlank(result.getData(k).getString("RSRV_STR8"))){
						sendOrderInfo=result.getData(k);
						break;
					}
				}
				if(IDataUtil.isNotEmpty(sendOrderInfo)){
					sendOrderInfo.put("OPR_TYPE", "2");//查询操作
					sendOrderInfo.put("BIZ_TYPE", "1013");
					IDataset resultInfo = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.queryRemindOrder", sendOrderInfo);
					String rtnCode = resultInfo.getData(0).getString("BIZ_ORDER_RESULT");
					if("0000".equals(rtnCode)){
						sendOrderInfo.put("RSRV_STR6", resultInfo.getData(0).getString("CD_NAME"));//销户处理人
						sendOrderInfo.put("RSRV_STR7", resultInfo.getData(0).getString("CD_TEL"));//销户处理人联系方式
						sendOrderInfo.put("XH_STATUS", resultInfo.getData(0).getString("XH_STATUS"));//销户工单当前环节
						destroyInfoList.add(sendOrderInfo);
					}else{
						CSViewException.apperr(CrmCommException.CRM_COMM_103, "查询失败：" + resultInfo.getData(0).getString("RESULT_DESC"));
					}
				}
			}
			
		}else{
			IDataOutput out2 = CSViewCall.callPage(this, "SS.RemoteDestroyUserSVC.queryDestroyLocalOrder", pageData, getPagination("nav"));
			IDataset orderInfos = out2.getData();
			setCount(out2.getDataCount());
			if(IDataUtil.isNotEmpty(orderInfos)){
				destroyInfoList=orderInfos;
			}
		}
		setDestroyInfoList(destroyInfoList);
		setAjax(destroyInfoList);
	}

	public void onTradeSubmit(IRequestCycle cycle) throws Exception
	{
		IData data = getData();
		data.put("OPR_TYPE", "1");//催单操作
		data.put("BIZ_TYPE", "1013");
	    IDataset resultInfo = CSViewCall.call(this, "SS.RemoteDestroyUserSVC.queryRemindOrder", data);
	    String rtnCode = resultInfo.getData(0).getString("BIZ_ORDER_RESULT");
		if(!"0000".equals(rtnCode)){
			CSViewException.apperr(CrmCommException.CRM_COMM_103, "催单失败：" + resultInfo.getData(0).getString("RESULT_DESC"));
		}
	}
}
