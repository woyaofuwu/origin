
package com.asiainfo.veris.crm.order.web.person.highvaluegarden;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;


public abstract class GardenDeviceInfoDel extends GroupBasePage
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
	public void batGardenInfo(IRequestCycle cycle) throws Exception {
		IData indata = getData();
		
		boolean hasPriv = StaffPrivUtil.isPriv(this.getVisit().getStaffId(), "ACTIVITY_GARDEN_INFO_DEL", StaffPrivUtil.PRIV_TYPE_FUNCTION);
        if (!hasPriv) {
        	CSViewException.apperr(CrmCommException.CRM_COMM_103, "该" + 
        			this.getVisit().getStaffId() + "没有权限不能办理批量删除业务!");
        }
        
		// 上传文件处理
		String filePath = indata.getString("FILE_PATH");
		
		IData param = new DataMap();
		param.put("FILE_PATH", filePath);
		param.put("CREATE_STAFF_ID", this.getVisit().getStaffId());
		param.put("UPDATE_STAFF_ID", this.getVisit().getStaffId());
		param.put("UPDATE_DEPART_ID", this.getVisit().getDepartId());
		IDataset resultDataset = CSViewCall.call(this, "SS.GardenDeviceInfoSVC.deleteGardenInfos", param);
		String result = resultDataset.getData(0).getString("result");
		setAjax("result", result);
	}
	
    /**
     * 查询
     * @param cycle
     * @throws Throwable
     */
    public void qryGardenDeviceInfo(IRequestCycle cycle) throws Throwable
    {
    	 IData data = getData();
         IData inparam = new DataMap();
         String activityCode = data.getString("ACTIVITY_CODE");
         String gardenName = data.getString("GARDEN_NAME");
         String deviceCode = data.getString("DEVICE_CODE");
         inparam.put("GARDEN_NAME", gardenName);
         inparam.put("ACTIVITY_CODE", activityCode);
         inparam.put("DEVICE_CODE", deviceCode);
         
         IDataOutput markOutput = CSViewCall.callPage(this, 
        		 "SS.GardenDeviceInfoSVC.qryGardenDeviceInfo", 
        		 inparam, getPagination("ratioNavBar"));
         
         IDataset dataList = new DatasetList();
         dataList = markOutput.getData();
         long infosCount = markOutput.getDataCount();
         
         setMarkInfos(dataList);
         setInfosCount(infosCount);
         
         setCondition(data);
    }
    
    
    
}
