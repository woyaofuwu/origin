package com.asiainfo.veris.crm.order.soa.person.busi.pccbusiness;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.pccbusiness.PCCBusinessQry;

public class PCCBusinessBean extends CSBizBean {
	
	public static final String BUSINESS_TYPE_1 = "1";// 业务类型
	public static final String BUSINESS_TYPE_2 = "2";// 业务类型
	public static final String BUSINESS_TYPE_3 = "3";// 业务类型

	public IDataset qryPCCBatTaskStatus(IData input) throws Exception {

		IDataset PCCBusinessList = new DatasetList();
		// 用户属性签约信息
		if ("1".equals(input.getString("SERVICE_TYPE"))) {
			PCCBusinessList = PCCBusinessQry.qryPCCBusinessListForSubscriber(input);
		} else if ("2".equals(input.getString("SERVICE_TYPE"))) {
			PCCBusinessList = PCCBusinessQry.qryPCCBusinessListForService(input);
		} else if ("3".equals(input.getString("SERVICE_TYPE"))) {
			PCCBusinessList = PCCBusinessQry.qryPCCBusinessListForPolocy(input);
		} else {
			PCCBusinessList = PCCBusinessQry.qryPCCBusinessList(input);
		}

		return PCCBusinessList;
	}

	@SuppressWarnings( { "unchecked", "unused" })
	public IData qryPCCBatTaskToIboss(IData param) throws Exception {
		IData resData = new DataMap();
		// data输入数据调用IBOSS接口
		IData interfaceDate = new DataMap();

		buildKindId(param);

		// 调用igboss接口
		param.put("ROUTETYPE", "00");
		param.put("ROUTEVALUE", "000");

		IDataset bossSet = IBossCall.dealInvokeUrl(param.getString("KIND_ID"),"IBOSS3", param);
		if (IDataUtil.isNotEmpty(bossSet)) {
			interfaceDate = bossSet.getData(0);
		}
		/*
		 * RESULT_CODE 任务执行状态 必选 TASK_ID 任务ID 必选 EP_SN PCRF/SPR网元名称 必选
		 * START_TIME 任务执行开始时间。STATUS为ABORTED时可不带。 可选 END_TIME
		 * 任务执行结束时间。STATUS为COMPLETED时才携带。 可选 STATUS 任务执行状态 必选 TOTAL 执行的总命令数
		 * STATUS为COMPLETED时才携带。 可选 SUCCESS 执行成功的命令数 STATUS为COMPLETED时才携带。 可选
		 * ACK_FILE 日志文件名 STATUS为COMPLETED时才携带。 可选
		 * interfaceDate.put("RESULT_CODE", "110008");
		 * interfaceDate.put("TASK_ID", "20141214142001");
		 * interfaceDate.put("START_TIME", "20140226140725");
		 * interfaceDate.put("EP_SN", "1"); //interfaceDate.put("END_TIME",
		 * "20140226140729"); interfaceDate.put("STATUS", "COMPLETED");
		 * interfaceDate.put("TOTAL", "5"); interfaceDate.put("SUCCESS", "2");
		 * interfaceDate.put("ACK_FILE", "23weewe.plt");
		 */
		if (interfaceDate != null) {
			if(
				StringUtils.isNotBlank(interfaceDate.getString("RESULT_CODE","")) && StringUtils.isNotBlank(interfaceDate.getString("TASK_ID",""))
				&& StringUtils.isNotBlank(interfaceDate.getString("EP_SN","")) && StringUtils.isNotBlank(interfaceDate.getString("STATUS",""))
				)
			{
				int i = PCCBusinessQry.updPCCBusiness(interfaceDate);
				if (i <= 0) {
					resData.put("RESULT", "1");// 处理失败
				} else {
					resData.put("RESULT", "0");
				}
			}
		} else {
			resData.put("RESULT", "1");
		}
		return resData;
	}

    private void buildKindId(IData param)
    {
        //新增参数系统类型 12-华为  34-诺西
	    String sysType = param.getString("SYSTEM_TYPE","12");

	    if (sysType.equals("12"))
	    {
	        param.put("KIND_ID", "CheckBatQry_PCRF_0_0");
	    }
	    else
	    {
	        param.put("KIND_ID", "CheckBatQryNX_PCRF_0_0");
	    }
    }
	public IData pccDateInsert(IData input) throws Exception {
		IData reData = new DataMap();
		try {
			IDataset pccDatas = input.getDataset("PCC_DATA");
			String serviceType = input.getString("SERVICE_TYPE");
			// 用户属性签约信息
			if ("1".equals(serviceType)) {
				PCCBusinessQry.insertPCCBusinessListForSubscriber(pccDatas);
			} else if ("2".equals(serviceType)) {
				PCCBusinessQry.insertPCCBusinessListForService(pccDatas);
			} else if ("3".equals(serviceType)) {
				PCCBusinessQry.insertPCCBusinessListForPolocy(pccDatas);
			}
			reData.put("result", "添加数据成功");
		} catch (Exception e) {
			reData.put("result", "添加数据的时候出现问题:" + e.getMessage());
		}
		return reData;
	}

}