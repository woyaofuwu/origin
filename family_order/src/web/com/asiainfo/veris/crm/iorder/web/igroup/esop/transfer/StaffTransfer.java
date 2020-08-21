package com.asiainfo.veris.crm.iorder.web.igroup.esop.transfer;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class StaffTransfer extends EopBasePage {
    public abstract void setPattr(IData pattr);
	public abstract void setPattrs(IDataset pattrs);
    public abstract void setStaffInfos(IDataset staffInfos) throws Exception;

	
	@Override
    public void initPage(IRequestCycle cycle) throws Exception
    {
    	IData pattr=new DataMap();
    	String oldStaffId = getVisit().getStaffId();
    	String cityCode = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "CITY_CODE", oldStaffId);
    	pattr.put("oldStaffId",oldStaffId);
		if(!cityCode.equals("HNSJ")){
			pattr.put("CITY_CODE",cityCode);
		}
		setPattr(pattr);
    }
    public void qryStaffinfo(IRequestCycle cycle) throws Exception {
        IData pattr = getData("cond");
        IDataset staffinfo = CSViewCall.call(this, "SS.StaffTransferSVC.qryStaffNameForName", pattr);
        setStaffInfos(staffinfo);

    }
    public void qryStaffIdinfo(IRequestCycle cycle) throws Exception {
        IData pattr = getData("cond");
        IDataset staffinfo = CSViewCall.call(this, "SS.StaffTransferSVC.qryStaffNameForId", pattr);
        setStaffInfos(staffinfo);
        
    }
	
    public void submit(IRequestCycle cycle) throws Exception {
        IData pattr = getData("pattr");
        String cityCode = pattr.getString("citycode");
        String oldStaffId = pattr.getString("oldStaffId");
        String newStaffId = pattr.getString("newStaffId");
		System.out.println("submit"+pattr);
		if(newStaffId.equals(oldStaffId)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "转移时工号不能相同！");
		}
		String cityCode2 = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "CITY_CODE", oldStaffId);
		//转派前员工权限校验
		if(StringUtils.isNotEmpty(cityCode) && !cityCode.equals(cityCode2)) {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "非省级工号仅可转派当前地市员工工单！");
		}
		//转派后员工权限校验
		String newCityCode = StaticUtil.getStaticValue(getVisit(), "TD_M_STAFF", "STAFF_ID", "CITY_CODE", newStaffId);
		if(StringUtils.isNotEmpty(cityCode) && StringUtils.isNotBlank(newCityCode) && !newCityCode.equals(cityCode2)) {
		    CSViewException.apperr(CrmCommException.CRM_COMM_103, "仅可转派给当前地市的员工！");
		}
		IDataset newStaffIdList=StaticUtil.getList(getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", new String[]{ "STAFF_ID" }, new String[]{ pattr.getString("newStaffId")});
		if(IDataUtil.isEmpty(newStaffIdList)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "该工号"+pattr.getString("newStaffId")+"未查出工号记录！");
		}else{
	        IDataset pattrs = CSViewCall.call(this, "SS.StaffTransferSVC.updStaffTransfer", pattr);
			setPattrs(pattrs);
		}
		setPattr(pattr);

    }
}
