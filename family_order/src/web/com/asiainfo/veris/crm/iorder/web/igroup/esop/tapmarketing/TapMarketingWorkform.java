package com.asiainfo.veris.crm.iorder.web.igroup.esop.tapmarketing;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class TapMarketingWorkform extends EopBasePage {

    public abstract void setPattrs(IDataset pattrs) throws Exception;
    public abstract void setInfoCount(long infoCount) throws Exception;


    public void qryTapMarketing(IRequestCycle cycle) throws Exception {
        IData data = getData("cond");
        queryTapMarketingforCondition(data,true);
    }
    public void qryMyTapMarketing(IRequestCycle cycle) throws Exception {
        IData data = getData("cond");
        queryTapMarketingforCondition(data,false);
    }
    
    private void queryTapMarketingforCondition( IData data,boolean flag)  throws Exception{
    	if(flag){//全部数据
			if(!getVisit().getCityCode().equals("HNSJ")){
				data.put("CITY_ID",getVisit().getCityCode());
			}
		}else{
			data.put("RESPONSIBILITY_ID",getVisit().getStaffId());
		}
    	IDataOutput output = CSViewCall.callPage(this, "SS.TapMarketingSVC.selTapMarketingByCondition", data, this.getPagination("navbar1"));
        setInfoCount(output.getDataCount());
        setPattrs(output.getData());
    }
}
