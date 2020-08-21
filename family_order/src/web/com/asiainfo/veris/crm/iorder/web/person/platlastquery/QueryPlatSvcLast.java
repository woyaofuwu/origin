package com.asiainfo.veris.crm.iorder.web.person.platlastquery;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryPlatSvcLast extends PersonBasePage {

	/**
	 * @param cycle
	 */
	public void init(IRequestCycle cycle) {
		IData cond = new DataMap();
		cond.put("cond_BIZ_TYPE_CODE", "1");
		this.setCond(cond);
		this.setSelectList(new DatasetList());
	}

	/**
	 * 用户平台业务订购关系查询
	 *
	 * @return
	 * @throws Exception
	 */
	public void qryUserPlatSvc(IRequestCycle cycle) throws Exception {
		long getDataCount = 0;
		IData param = this.getData("cond", true);





//查询数据
		IDataOutput result = CSViewCall.callPage(this, "SS.QueryPlatServiceLastSVC.qryUserTransPlatSvcs", param,
				this.getPagination("pagin"));
		IDataset datas = result.getData();
		/*
		 * 获取特殊的服务信息
		 */
		IData comparaData = new DataMap();
		comparaData.put("SUBSYS_CODE", "CSM");
		comparaData.put("PARAM_ATTR", "4121");
		comparaData.put("PARAM_CODE", "PLATSVC_CONTENT_SVC");
		IDataOutput specialSvcData = CSViewCall.callPage(this, "CS.CommparaInfoQrySVC.getCommparaInfoByBizCode",
				comparaData, null);

		if (specialSvcData != null) {
			IDataset specialSvcs = specialSvcData.getData();
			if (IDataUtil.isNotEmpty(specialSvcs)) {
				IData specialSvc = specialSvcs.getData(0);
				String serviceId = specialSvc.getString("PARA_CODE1", "");

				if (IDataUtil.isNotEmpty(datas)) {
					for (int i = 0, size = datas.size(); i < size; i++) {
						IData data = datas.getData(i);
						if (data.getString("SERVICE_ID", "").equals(serviceId)) {
							data.put("PRICE", specialSvc.getString("PARA_CODE2", ""));
							data.put("BIZ_NAME", "");
						}
					}
				}
			}
		}

		if (IDataUtil.isNotEmpty(datas)) {
			for (int i = 0; i < datas.size(); i++) {
				if ("98001901".equals(datas.getData(i).getString("SERVICE_ID"))
						&& "19".equals(datas.getData(i).getString("BIZ_TYPE_CODE"))) {
					IDataset DiscntsCode = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.queryDiscntsCodeByusrid",
							param);
					if (!IDataUtil.isEmpty(DiscntsCode)) {
						String Code = DiscntsCode.getData(0).getString("DISCNT_CODE");
						if ("1237".equals(Code)) {
							datas.getData(i).put("PRICE", "0");
						} else if ("1238".equals(Code)) {
							datas.getData(i).put("PRICE", "5000");
						} else if ("12789".equals(Code)) {
							datas.getData(i).put("PRICE", "6000");
						}
					}
				}
			}
//			getDataCount = Long.parseLong(datas.getData(0).getString("TOTAL"));
		}
//		this.setInfos(datas);
//		this.setPaginCount(getDataCount);
//		this.setCond(this.getData("cond"));
		// 办理业务查询转化选择列表
		IDataset selDataset = new DatasetList();
		for (int i = 0; i < datas.size(); i++) {
			IData data = new DataMap();
			data.put("DATA_NAME",
					datas.getData(i).getString("SERVICE_ID", "") + datas.getData(i).getString("BIZ_NAME", ""));
			data.put("DATA_ID", datas.getData(i).getString("USER_ID", "")// 用户id
					+ "|" + datas.getData(i).getString("SERVICE_ID", "")// 企业代码
					+ "|" + datas.getData(i).getString("SP_CODE", "")// 企业代码
					+ "|" + datas.getData(i).getString("BIZ_CODE", "")// 业务代码
					+ "|" + datas.getData(i).getString("BIZ_NAME", "")// 平台类型
					+ "|" + datas.getData(i).getString("BIZ_TYPE_CODE", "")// 平台类型
					

			);
			selDataset.add(data);

		}

		this.setSelectList(selDataset);
		this.setInfos(new DatasetList());
//		ognl:pageutil.getStaticList('BIZ_TYPE_CODE')
		String[] platStr = param.getString("BIZ_TYPE_CODE").split("\\|");
		if (platStr.length>5){
			getQueryUserPlatSvcLastList(cycle);
			return;

		}
	}

	/**
	 * 平台业务最后办理记录查询
	 *
	 * @return
	 * @throws Exception
	 */
	public void getQueryUserPlatSvcLastList(IRequestCycle cycle) throws Exception {
		long getDataCount = 10;
		IData param = this.getData("cond", true);
		String[] platStr = param.getString("BIZ_TYPE_CODE").split("\\|");
		if (platStr.length<=1){
			this.setInfos(new DatasetList());
			this.setPaginCount(getDataCount);
			return;

		}
		param.put("USER_ID", platStr[0]);
		param.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));//
		param.put("SERVICE_ID", platStr[1]);

		param.put("KIND_ID", "OrderStatusQuaryServ_BBOSS_0_0");

		IDataOutput result = CSViewCall.callPage(this, "SS.QueryPlatServiceLastSVC.qryUserLastTradeSendPlatSvcs", param,
				this.getPagination("pagin"));
		IDataset datas = result.getData();
		if (IDataUtil.isNotEmpty(datas)) {
			for (int i = 0; i < datas.size(); i++) {
				datas.getData(i).put("SP_CODE", platStr[2]);
				datas.getData(i).put("BIZ_CODE", platStr[3]);
				datas.getData(i).put("BIZ_NAME", platStr[4]);

			}
		}
//		
		this.setInfos(datas);
		this.setPaginCount(getDataCount);
//		this.setCond(this.getData("cond"));

	}
	
	
	/**
	 * 数据 重发操作
	 * 重新发送用户的该条平台业务的当前状态至数指平台 并更新状态 实现重跑数据指令
	 * @param cycle
	 * @throws Exception
	 */
	public void anewSendPlatsDataForDataCom(IRequestCycle cycle) throws Exception {
		long getDataCount = 10;
		IData param = this.getData("cond", true);
		IData data = getData();
		
		
		
		String[] platStr = param.getString("BIZ_TYPE_CODE").split("\\|");
		param.put("USER_ID", platStr[0]);
		param.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));//
		param.put("SERVICE_ID", platStr[1]);
		param.put("KIND_ID", "anewSendPlatsDataForDataCom_DATACOM_0_0");
		param.put("PLAT_SYN_ID",  data.getString("PLAT_SYN_ID"));
		param.put("SUBSCRIBE_ID",  data.getString("SUBSCRIBE_ID"));
		param.put("BUSI_SIGN",  data.getString("BUSI_SIGN"));
		IDataOutput result = CSViewCall.callPage(this, "SS.QueryPlatServiceLastSVC.anewSendPlatsDataForDataComSvcs", param,
				this.getPagination("pagin"));
		
	
		IDataset datas = result.getData();
		IData resultinfo = datas.getData(0);
	    setAjax(resultinfo);
	    
	    getQueryUserPlatSvcLastList(cycle);
		
//		if (IDataUtil.isNotEmpty(datas)) {
//			for (int i = 0; i < datas.size(); i++) {
//				datas.getData(i).put("SP_CODE", platStr[2]);
//				datas.getData(i).put("BIZ_CODE", platStr[3]);
//				datas.getData(i).put("BIZ_NAME", platStr[4]);
//
//			}
//		}
////		
//		this.setInfos(datas);
//		this.setPaginCount(getDataCount);
//		this.setCond(this.getData("cond"));

	}

	public abstract void setCond(IData cond);

	public abstract void setInfos(IDataset infos);

	public abstract void setSelectList(IDataset selectInfos);

	public abstract void setPaginCount(long count);
}
