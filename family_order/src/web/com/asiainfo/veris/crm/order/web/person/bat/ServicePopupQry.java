
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ServicePopupQry extends PersonBasePage
{

    /**
     * @Function: initPageServicePopup
     * @Description:
     * @param: @param cycle
     * @param: @throws Exception
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: Administrator
     * @date: 4:26:27 PM Mar 3, 2013 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* Mar 3, 2013 xiangyc v1.0.0 新建函数
     */
    public void initPageServicePopup(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        String sIsPop = cond.getString("IS_POP", "");
        String specFlag = cond.getString("SPECFLAG", "");
        setSpecFlag(specFlag);
        String tipInfo = "";
        if (StringUtils.isNotBlank(sIsPop) && "SERVICE_CHG".equals(sIsPop))
        {
            setParams("YES");
            tipInfo = "请输入服务编码或服务名称查询，然后双击选定的查询结果回到主界面！";
            setTipInfo(tipInfo);
        }
        else
        {
            tipInfo = "请输入服务编码或服务名称查询！";
            setTipInfo(tipInfo);
        }

    }

    /**
     * @Function: queryServiceInfo
     * @Description:
     * @param：
     * @return：void
     * @throws：
     * @version: v1.0.0
     * @author: xiangyc@asiainfo-linkage.com
     * @date: 下午7:38:22 2014-3-6 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-6 xiangyc v1.0.0
     */
    public void queryServiceInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        long getDataCount = 0;
        IData cond = new DataMap();
        String specFlag = data.getString("SPECFLAG");
        cond.put("SERVICE_NAME", data.getString("SERVICE_NAME"));
        cond.put("SERVICE_ID", data.getString("SERVICE_ID"));
        IDataOutput output = new DataOutput() ;
        if(StringUtils.isNotEmpty(specFlag)){
        	output = CSViewCall.callPage(this, "CS.BatDealSVC.querySerivceSpecInfo", cond, getPagination("taskNav"));
        	for (int i = 0; i < output.getData().size(); i++) {
        		IData idoutput = output.getData().getData(i);
        		String strServiceID = idoutput.getString("DATA_ID", "");
        		String strServiceName = idoutput.getString("DATA_NAME", "");
        		idoutput.put("SERVICE_ID", strServiceID);
        		idoutput.put("SERVICE_NAME", strServiceName);
			}
        }else{
        	output = CSViewCall.callPage(this, "CS.BatDealSVC.querySerivceInfo", cond, getPagination("taskNav"));
        }
        
        if (output.getData() == null || output.getData().size() == 0)
        {
            setTipInfo("没有符合查询条件的数据！");
        }else{
        	setTipInfo("双击任意一条查询结果返回！");
        	getDataCount = Long.parseLong(output.getData().getData(0).getString("TOTAL"));
        }
        setTaskInfos(output.getData());
        setBatchTaskListCount(getDataCount);
    }

    public abstract void setBatchOperTypes(IDataset set);

    public abstract void setBatchTaskListCount(long batchTaskListCount);

    public abstract void setCondition(IData info);

    public abstract void setDetial(IData detail);

    public abstract void setDetials(IDataset detials);

    public abstract void setParams(String params);

    public abstract void setTaskInfos(IDataset task);

    public abstract void setTipInfo(String tipInfo);
    
    public abstract void setSpecFlag(String specFlag);
    
}
