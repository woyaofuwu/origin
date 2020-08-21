
package com.asiainfo.veris.crm.order.web.person.badness;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryInfectLogInfo extends PersonQueryPage
{
	public abstract void setBadnessForbiddenInfoCount(long str);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

	
	
	/**
	 * 
	 * 查询列表
	 * @param cycle
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void queryInfo(IRequestCycle cycle) throws Exception
	{

		IData condParams = getData("cond", true);
        IData param = new DataMap();
        String serialNum = condParams.getString("PHONE_NUM", ""); // 手机号码
        param.put("SERIAL_NUMBER", serialNum);
		
		IDataOutput output = CSViewCall.callPage(this, "SS.BadnessQuerySVC.queryInfectLogInfo", param, getPagination("forbiddenPoint"));
	    IData idata = output.getData().getData(0);
        IDataset idatas = idata.getDataset("RET");
        setInfos(idatas);
        setAjax(idatas);
        setBadnessForbiddenInfoCount(output.getDataCount());
	}

}
