package com.asiainfo.veris.crm.iorder.web.person.broadband.widenet.enterprisewidepreregaudit;

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

public abstract class EnterpriseWidePreRegAuditNew extends CSBasePage
{

	public void init(IRequestCycle cycle) throws Exception
    {
		String startDate = TimeUtil.decodeTimestamp(TimeUtil.getFirstDayOfThisMonth(), "yyyy-MM-dd");
		String endDate = TimeUtil.decodeTimestamp(TimeUtil.getLastDateThisMonth(), "yyyy-MM-dd");
		IData info = new DataMap();
		info.put("START_DATE", startDate);
		info.put("END_DATE", endDate);
		/*info.put("collect_START_DATE", startDate);
		info.put("collect_END_DATE", endDate);
		info.put("WIDE_PRE_REG_QRY_TYPE", "1");*/
		setInfo(info);
    }
	
    /**
     * @Function:
     * @Description: 企业宽带预约查询
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
    	IDataOutput output = CSViewCall.callPage(this, "SS.EnterpriseWidePreRegAuditService.getEnterpriseWideNetBookList", request,this.getPagination("preregInfoNav"));
        this.setInfos(output.getData());
        setCount(output.getDataCount());
        /*if(StringUtils.equals("4", request.getString("cond_REG_STATUS"))){ //查询用户是否有下发短信权限
    		boolean isLargess = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "NOTIFY_SMS");//下发短信权限
            IData result = new DataMap();
            if(isLargess){//有下发短信权限
            	result.put("RESULT_CODE", "0");
            }else{
            	result.put("RESULT_CODE", "1");
            }
            this.setAjax(result);
    	}*/
    }
    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
    public abstract void setCount(long count);
}
