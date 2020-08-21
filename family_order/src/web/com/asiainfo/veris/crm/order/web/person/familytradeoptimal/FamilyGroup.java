
package com.asiainfo.veris.crm.order.web.person.familytradeoptimal;

import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FamilyGroup extends PersonBasePage
{
	/**
	 * 省BOSS查询BBOSS群组信息查询接口
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public void bossGroupInfo(IRequestCycle cycle) throws Exception{
		IData pageData = getData();
		IDataset rtDataset =CSViewCall.call(this,"SS.FamilyGroupSVC.qryBossGroupInfo", pageData);//省侧查boss群组信息
		IDataset return_data =new DatasetList();//群组信息列表
		IData result = new DataMap();//返回结果
		if (IDataUtil.isNotEmpty(rtDataset)){
			String rsp_code = rtDataset.getData(0).getString("RSP_CODE");
			String rsp_desc = rtDataset.getData(0).getString("RSP_DESC");
			if(StringUtils.isNotBlank(rsp_code)){
				if("00".equals(rsp_code)){//RSP_CODE="00"显示数据,否则页面报错
					IDataset rsp_result = rtDataset.getData(0).getDataset("RSP_RESULT");//查询结果
					if(IDataUtil.isNotEmpty(rsp_result)){
						for(int k=0;k<rsp_result.size();k++){
							IData data =new DataMap();
							data.put("PRODUCT_CODE", rsp_result.getData(k).getString("PRODUCT_CODE"));
							data.put("POID_CODE", rsp_result.getData(k).getString("POID_CODE"));
							data.put("POID_LABLE", rsp_result.getData(k).getString("POID_LABLE"));
							data.put("CUSTOMER_PHONE", rsp_result.getData(k).getString("CUSTOMER_PHONE"));
							data.put("PRODUCT_OFFERING_ID", rsp_result.getData(k).getString("PRODUCT_OFFERING_ID"));
							data.put("MEM_COUNT", rsp_result.getData(k).getString("MEM_COUNT"));
							data.put("EFF_TIME", rsp_result.getData(k).getString("EFF_TIME"));
							data.put("EXP_TIME", rsp_result.getData(k).getString("EXP_TIME"));
							return_data.add(data);
						}
					}
				}
			}else{
				rsp_desc="网络异常";
			}
			result.put("RSP_CODE",rsp_code);
			result.put("RSP_DESC",rsp_desc);
		}
		log.debug("web层java的返回"+return_data);
		setAjax(result);
		setMebInfos(return_data);
	}
	public abstract void setMebTranInfos(IDataset mebInfos);

	public abstract void setMebInfos(IDataset mebInfos);


}
