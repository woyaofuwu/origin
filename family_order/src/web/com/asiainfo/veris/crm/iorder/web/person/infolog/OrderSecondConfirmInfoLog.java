package com.asiainfo.veris.crm.iorder.web.person.infolog;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class OrderSecondConfirmInfoLog extends PersonBasePage {

	/**
	 * @param cycle
	 */
	public void init(IRequestCycle cycle) {
		IData cond = new DataMap();
		cond.put("cond_BIZ_TYPE_CODE", "1");
		this.setCond(cond);
	}

	/**
	 * 用户业务订购二次确认日志信息 原接口 修改
	 *SS.SecConfirmLogSVC.querySecConfirmLog
	 * */
	public void qryUserOrderInfoLog(IRequestCycle cycle) throws Exception {
		long getDataCount = 0;
		IData param = this.getData("cond", true);
//查询数据
		IDataOutput result = CSViewCall.callPage(this, "SS.SecConfirmLogSVC.querySecConfirmLog", param,
				this.getPagination("pagin"));
		IDataset secConfirmLogDatas = result.getData();

		IDataset channelSourceList= pageutil.getStaticList("OPERATOR_CHANNEL_SOURCE");//渠道来源
		IDataset reWayList= pageutil.getStaticList("OPERATOR_RE_WAY");// 二次确认方式

		if (IDataUtil.isNotEmpty(secConfirmLogDatas)) {//转化 渠道来源  二次确认方式
			for (int i = 0; i < secConfirmLogDatas.size(); i++) {
				IData iData=secConfirmLogDatas.getData(i);
				if (StringUtils.isNotEmpty(iData.getString("CHANNEL_SOURCE"))) {
					IDataset 	sourceFilter=DataHelper.filter(channelSourceList, "DATA_ID=" + iData.getString("CHANNEL_SOURCE"));
					if (IDataUtil.isNotEmpty(sourceFilter)) {//渠道来源
						secConfirmLogDatas.getData(i).put("CHANNEL_SOURCE",sourceFilter.getData(0).getString("DATA_NAME"));
					}
				}
				if (StringUtils.isNotEmpty(iData.getString("RE_WAY"))) {//二次确认方式
					IDataset 	wayFilter=DataHelper.filter(reWayList, "DATA_ID=" + iData.getString("RE_WAY"));
					if (IDataUtil.isNotEmpty(wayFilter)) {//二次确认方式
						secConfirmLogDatas.getData(i).put("RE_WAY",wayFilter.getData(0).getString("DATA_NAME"));

					}
				}

			}
		}

		this.setInfos(secConfirmLogDatas);

	}






	public abstract void setCond(IData cond);

	public abstract void setInfos(IDataset infos);
	public abstract void setPaginCount(long count);
}
