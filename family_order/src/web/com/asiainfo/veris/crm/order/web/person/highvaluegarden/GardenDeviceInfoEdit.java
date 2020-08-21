
package com.asiainfo.veris.crm.order.web.person.highvaluegarden;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;


public abstract class GardenDeviceInfoEdit extends GroupBasePage
{
	public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setCondition(IData condition);
    
    public abstract void setInfosCount(long infosCount);

    public abstract void setMarkInfos(IDataset markInfos);
    
    
    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        
        IData condData = getData("cond", true);
        condData.put("START_DATES", SysDateMgr.getSysTime());
        setCondition(condData);

    }
 
   
    /**
	 * 
	 * @param cycle
	 * @throws Exception
	 */
	public void batEditGardenInfo(IRequestCycle cycle) throws Exception {
		IData indata = getData();
		
		boolean hasPriv = StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "ACTIVITY_GARDEN_INFO_EDIT", StaffPrivUtil.PRIV_TYPE_FUNCTION);
        if (!hasPriv) {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "该" + 
        			this.getVisit().getStaffId() + "没有权限不能办理批量覆盖业务!");
        }
        
		// 上传文件处理
		String filePath = indata.getString("FILE_PATH");
		ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
		IData fileData = ImpExpUtil.beginImport(null, filePath, ExcelConfig.getSheets("import/bat/GARDENINFO.xml"));
		
		if (fileData.get("error") != null && ((IDataset[]) fileData.get("error")).length > 0
				&& ((IDataset[]) fileData.get("error"))[0] != null && ((IDataset[]) fileData.get("error"))[0].size() > 0) 
		{
			setAjax("result", (((IDataset[]) fileData.get("error"))[0]).getData(0).getString("IMPORT_ERROR"));
			return;
		}
		
		IDataset fileDataset = ((IDataset[]) fileData.get("right"))[0];
		if ( fileDataset != null && fileDataset.size() > 0 ) {
			String activityCode = fileDataset.getData(0).getString("ACTIVITY_CODE");
			IData paramDelete = new DataMap();
			paramDelete.put("ACTIVITY_CODE", activityCode);
			paramDelete.put("REMOVE_TAG", "1");
			paramDelete.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
			paramDelete.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
			IDataset resDataset = CSViewCall.call(this, "SS.GardenDeviceInfoSVC.gardenInfoDelete", paramDelete, getPagination("ratioNavBar"));
			String res = resDataset.getData(0).getString("result");
		}
		IData param = new DataMap();
		param.put("FILE_PATH", filePath);
		param.put("CREATE_STAFF_ID", this.getVisit().getStaffId());
		param.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
		IDataset resultDataset = CSViewCall.call(this, "SS.GardenDeviceInfoSVC.gardenInfoInsert", param);
		String result = resultDataset.getData(0).getString("result");
		setAjax("result", result);
	}
    
}
