package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.widepreregaudit;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: PreRegAudit.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-3-27 下午02:57:18 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-27 chengxf2 v1.0.0 修改原因
 */

public abstract class WidePreRegAuditNew extends CSBasePage
{

	public void init(IRequestCycle cycle) throws Exception
    {
		String startDate = TimeUtil.decodeTimestamp(TimeUtil.getFirstDayOfThisMonth(), "yyyy-MM-dd");
		String endDate = TimeUtil.decodeTimestamp(TimeUtil.getLastDateThisMonth(), "yyyy-MM-dd");
		IData info = new DataMap();
		info.put("START_DATE", startDate);
		info.put("END_DATE", endDate);
		info.put("collect_START_DATE", startDate);
		info.put("collect_END_DATE", endDate);
		info.put("WIDE_PRE_REG_QRY_TYPE", "1");
		setInfo(info);
    }
    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-4-3 上午08:14:01 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-4-3 chengxf2 v1.0.0 修改原因
     */
    public void getPreRegAuditStatusList(IRequestCycle cycle) throws Exception
    {
        IData request = this.getData();
        String typeId = request.getString("TYPE_ID");
        IDataset auditStatusList = pageutil.getStaticList(typeId);
        this.setAjax(auditStatusList);
    }
    
    /**
     * 下发短信
     * @param cycle
     * @throws Exception
     */
    public void notifySms(IRequestCycle cycle) throws Exception
    {
    	IData request = this.getData();
        request.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset output = CSViewCall.call(this, "SS.WidePreRegAuditService.notifySms", request);
        this.setAjax(output);
    }
    
    /**
     * @Function:
     * @Description: 宽带需求收集清单查询
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijun17
     * @date: 2016-5-21 下午04:40:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-25 lijun17 v1.0.0 修改原因
     */
    public void queryWidePreRegInfo(IRequestCycle cycle) throws Exception
    {
    	IData request = this.getData();
    	IDataOutput output = CSViewCall.callPage(this, "SS.WidePreRegAuditService.getWideNetBookList", request,this.getPagination("preregInfoNav"));
        this.setInfos(output.getData());
        setCount(output.getDataCount());
        if(StringUtils.equals("4", request.getString("cond_REG_STATUS"))){ //查询用户是否有下发短信权限
    		boolean isLargess = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "NOTIFY_SMS");//下发短信权限
            IData result = new DataMap();
            if(isLargess){//有下发短信权限
            	result.put("RESULT_CODE", "0");
            }else{
            	result.put("RESULT_CODE", "1");
            }
            this.setAjax(result);
    	}
    }
    
    /**
     * @Function:
     * @Description: 宽带需求收集清单汇总查询
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijun17
     * @date: 2016-5-21 下午04:40:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-25 lijun17 v1.0.0 修改原因
     */
    public void queryCollectInfo(IRequestCycle cycle) throws Exception
    {
    	IData request = this.getData();
    	IDataOutput output = CSViewCall.callPage(this, "SS.WidePreRegAuditService.getWideNetBookCollectList", request,this.getPagination("preregCollectInfoNav"));
        this.setInfos(output.getData());
        setCount(output.getDataCount());
    }
    

    /**
     * @Function:
     * @Description: 宽带需求收集清单登记状态维护提交
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: lijun17
     * @date: 2016-5-21 下午04:40:50 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-25 lijun17 v1.0.0 修改原因
     */
    public void saveContent(IRequestCycle cycle) throws Exception
    {
        IData request = this.getData();
        request.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset output = CSViewCall.call(this, "SS.WidePreRegAuditService.dealService", request);
        this.setAjax(output);
    }
    
    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
    public abstract void setCount(long count);
}
