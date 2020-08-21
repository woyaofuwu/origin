
package com.asiainfo.veris.crm.iorder.web.igroup.esop.busicheck;

import java.util.Date;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.frame.icsview.common.svcutil.datainfo.uca.IUCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
public abstract class BusiCheckVoice extends CSBasePage
{

    /**
     * @Description: 初始化页面方法
     * @author
     * @date
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }
    
    public void queryBusiInfos(IRequestCycle cycle) throws Exception{
    	IData condData = getData();
		IDataset busiInfos = CSViewCall.call(this, "SS.BusiCheckVoiceSVC.queryWorkformInfos", condData);
		for(int i=0;i<busiInfos.size();i++){
			IData busiInfo = busiInfos.getData(i);
			//1、查询地州
			String cityCode = busiInfo.getString("CITY_CODE");
			String cityName =StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", new String[]
			    	{ "TYPE_ID", "DATA_ID"}, "DATA_NAME", new String[]
			    	{ "CUST_CITY_CODE",cityCode }); //查询市县
			busiInfo.put("CITY_NAME", cityName);
			//2、查询客户经理名称
			IData param = new DataMap();
			param.put("STAFF_ID", busiInfo.getString("STAFF_ID"));
			IData staffInfo = CSViewCall.callone(this, "SS.StaffDeptInfoQrySVC.getStaffInfo", param);
		    if(DataUtils.isNotEmpty(staffInfo)){
		    	busiInfo.put("STAFF_NAME", staffInfo.getString("STAFF_NAME"));
		    }
		    //3、存模板
		    busiInfo.put("BUSI_TYPE",  busiInfo.getString("BPM_TEMPLET_ID"));
		   /* //3、查询模板名称
		    IData input = new DataMap();
		    input.put("BPM_TEMPLET_ID", busiInfo.getString("BPM_TEMPLET_ID"));
		    input.put("VALID_TAG", "0");
			IData bpmTempletId = CSViewCall.callone(this, "SS.QryFlowNodeDescSVC.qryFlowDescByTempletId", input);
		    if(DataUtils.isNotEmpty(bpmTempletId)){
		    	busiInfo.put("BUSI_TYPE",  busiInfo.getString("BPM_TEMPLET_ID"));
		    }*/
		    String groupId =  busiInfo.getString("GROUP_ID");
		    IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
		    String custMgrId = group.getString("CUST_MANAGER_ID");
		    busiInfo.put("CUST_MANAGER_ID", custMgrId);
		    String custMgrName =  "";
	        if (StringUtils.isNotEmpty(custMgrId))
	        {
	        	 IData managerInfo = IUCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
	        	 custMgrName = managerInfo.getString("CUST_MANAGER_NAME");
	        }
	        busiInfo.put("CUST_MANAGER_NAME", custMgrName); 
		    
		}
		this.setInfos(busiInfos);
		this.setCondition(getData());
		this.setInfoCount(busiInfos.size());
	}
    public void queryBusiDetailInfo(IRequestCycle cycle) throws Exception{
    	IData condData = getData();
    	IData busiInfos = CSViewCall.callone(this, "SS.BusiCheckVoiceSVC.queryBusiDetailInfo", condData);
    	//检查列表
    	this.setInfo(busiInfos);
    	this.queryCheckRecordInfos(cycle);
    	condData.put("IBSYSID_1", condData.getString("IBSYSID"));
    	this.setCheckRecordInfo2(condData);
    	
    }
    
    //检查列表
  	public void queryCheckRecordInfos(IRequestCycle cycle) throws Exception{
  		IData data = getData();
  		IDataset checkRecordInfos = CSViewCall.call(this, "SS.BusiCheckVoiceSVC.queryCheckRecordInfos", data);
  	     
  		this.setCheckRecordInfos(checkRecordInfos);
  	}

  	public void addCheckRecord(IRequestCycle cycle) throws Exception{
		IData data = getData();
		IData busiInfos = CSViewCall.callone(this, "SS.BusiCheckVoiceSVC.addCheckRecordInfo", data);
		//检查列表
		this.queryCheckRecordInfos(cycle);
		this.setHintInfo("新增成功！");
	}
	
	@SuppressWarnings("unchecked")
	public void updateCheckRecord(IRequestCycle cycle) throws Exception{
		IData data = getData();
		data.put("UPDATE_TIME",DateUtil.formatDate(new Date(),"yyyy-MM-dd"));
		IData busiInfos = CSViewCall.callone(this, "SS.BusiCheckVoiceSVC.updateCheckRecordInfo", data);
		//BusiCheckVoiceQryBean.updateCheckRecordInfo(data);
		//检查列表
		this.queryCheckRecordInfos(cycle);
		this.setHintInfo("修改成功！");
	}
	public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setInfo(IData info);

    public abstract void setCheckRecordInfos(IDataset checkRecordInfos);
    
    public abstract void setCheckRecordInfo2(IData checkRecordInfo2);
}
