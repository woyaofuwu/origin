package com.asiainfo.veris.crm.order.soa.person.busi.interroamday;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class InterRoamPriorityHisQryBean extends CSBizBean{

	private final static transient Logger logger = Logger.getLogger(InterRoamPriorityHisQryBean.class);

	/**
	 * 订购关系历史优先级查询
	 * @param input
	 * @param roamInfoList
	 * @return
	 * @throws Exception
	 */
	public IDataset interRoamPriorityHisQry(IData input) throws Exception
	{
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		String beginTimsi = IDataUtil.chkParam(input, "BEGIN_TIMSI");
		String endTimsi = IDataUtil.chkParam(input, "END_TIMSI");
		
		IDataset prioInfos = IBossCall.interRoamPriorityHisQry(serialNumber, beginTimsi, endTimsi);
		logger.debug("===InterRoamPriorityHisQryBean====prioInfos="+prioInfos);
		
		IDataset prioInfoList = new DatasetList();
		if(DataUtils.isNotEmpty(prioInfos))
		{
			IDataset commparaSet = CommparaInfoQry.getCommByParaAttr("CSM", "2742", "ZZZZ");
			if(DataSetUtils.isNotBlank(commparaSet))
			{  
				//获取省侧资费编码
				for(int j=0;j<prioInfos.size();j++)
				{
					IData prioInfo = prioInfos.getData(j);
					IDataset prodInfos = prioInfo.getDataset("BILLING_PRIORITY_INFO");
					String oprTmsi = prioInfo.getString("OPR_TIMSI");
					for(int k=0;k<prodInfos.size();k++)
					{
						IData prodInfo = prodInfos.getData(k);
						String prodId = prodInfo.getString("PRODUCT_ID");
						for(int i=0 ;i< commparaSet.size() ;i++)
						{
							IData data = commparaSet.getData(i);
							if(prodId.equals(data.getString("PARA_CODE2")))
							{//获取省内编码及名称
								prodInfo.put("DISCNT_CODE",data.getString("PARAM_CODE"));
								prodInfo.put("DISCNT_NAME",data.getString("PARAM_NAME"));
								prodInfo.put("OPR_TIMSI",oprTmsi);
								prioInfoList.add(prodInfo);
								break;
							}
						}
					}
				}	
			}
			
		}
		
		if(DataUtils.isNotEmpty(prioInfoList))
		{
			DataHelper.sort(prioInfoList, "OPR_TIMSI", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
		}
		return prioInfoList;
	}
}
