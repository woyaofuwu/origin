package com.asiainfo.veris.crm.order.web.person.sundryquery.score;
 



import java.net.URLDecoder;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryScoreAssignee extends PersonQueryPage
{

    /**
     * 查询调整积分
     * 
     * @param cycle
     * @throws Exception
     */
    public void getAssigneeInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IDataset outPut = CSViewCall.call(this, "SS.QueryScoreAssigneeSVC.queryAssigneeInfo", param, getPagination("staticNav"));
        IData  result = outPut.getData(0);
        String alertInfo = "";
    	if (result == null || result.size() == 0) { // 未找到记录
    		setStatiCount(0);
    	
    		 
		}
		else if(!"00".equals(result.getString("X_RSPCODE"))){		
			setStatiCount(0);
			alertInfo=result.getString("X_RESULTINFO");
		}
		else{
			
			IDataset dataset1=IDataUtil.getDatasetSpecl("B_MOBILE",result); 
			IDataset dataset2=IDataUtil.getDatasetSpecl("ASSIGNEE_NAME",result);  
			IDataset dataset3=IDataUtil.getDatasetSpecl("VALIDNUM_TYPE",result);
			IDataset dataset4=IDataUtil.getDatasetSpecl("ASSIGNEE_ID",result);
			IDataset dataset5=IDataUtil.getDatasetSpecl("EFFECTIVE_DATE",result);
			IDataset dataset6=IDataUtil.getDatasetSpecl("OPERATOR_TIME",result);
		
			IDataset dataset8=IDataUtil.getDatasetSpecl("ORG_ID",result);
			IDataset dataset9=IDataUtil.getDatasetSpecl("ASSIGNEE_STATUS",result);
			IDataset dataset10=IDataUtil.getDatasetSpecl("USER_ROLE",result);
			IDataset dataset11=IDataUtil.getDatasetSpecl("USER_ID",result);
			IDataset infos = new DatasetList();
			for(int i=0; i<dataset1.size(); i++){						
				IData info = new DataMap();
				info.put("B_MOBILE",dataset1.get(i));
				info.put("ASSIGNEE_NAME",dataset2.get(i));
				info.put("VALIDNUM_TYPE",dataset3.get(i));
				info.put("ASSIGNEE_ID",dataset4.get(i));
				info.put("EFFECTIVE_DATE",dataset5.get(i));
				info.put("OPERATOR_TIME",dataset6.get(i));
				info.put("ORG_ID",dataset8.get(i));
				info.put("ASSIGNEE_STATUS",dataset9.get(i));
				info.put("USER_ROLE",dataset10.get(i));
				info.put("USER_ID",dataset11.get(i));
				infos.add(info);
			}
			setInfos(infos);
			  setStatiCount(infos.size());
		}
    	  this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
    	  setCond(param);
		
      
    }

    /**
     * 页面初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void initPage(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        // 设置办理时间
        String sysDate = SysDateMgr.getSysTime();
        data.put("START_DATE", sysDate);
        data.put("END_DATE", sysDate);
        setCond(data);
    }
    
    public void initAddPage(IRequestCycle cycle) throws Exception
   	{
   		IData pageData = getData();
   		String sn = pageData.getString("L_MOBILE");
   		pageData.put("L_MOBILE", sn);
   		setCond(pageData);
   	}
    
    public void initEditPage(IRequestCycle cycle) throws Exception
   	{
   		IData pageData = getData();
		String name = pageData.getString("ASSIGNEE_NAME");
		name = URLDecoder.decode(URLDecoder.decode(name,"UTF-8"),"UTF-8");
		pageData.put("ASSIGNEE_NAME", name);
		setCond(pageData);
   	}

    public void initReplacePage(IRequestCycle cycle) throws Exception
   	{

   		IData pageData = getData();
		setCond(pageData);
   	}

    
    
    public void AddAssignee(IRequestCycle cycle) throws Exception
	{
		IData pageData = getData();
		pageData.put("OPT_TYPE", "01");
		IDataset infos = CSViewCall.call(this, "SS.QueryScoreAssigneeSVC.updateAssigneeInfo", pageData);
		
		setAjax(infos.getData(0));
	}
    
    public void EditAssignee(IRequestCycle cycle) throws Exception
 	{
 		IData pageData = getData();
 			pageData.put("OPT_TYPE", "03");
 		
 		IDataset infos = CSViewCall.call(this, "SS.QueryScoreAssigneeSVC.updateAssigneeInfo", pageData);
 		setAjax(infos.getData(0));
 	}
    
    
    public void ReplaceAssignee(IRequestCycle cycle) throws Exception
  	{
  		IData pageData = getData();
  			pageData.put("OPT_TYPE", "02");
  		IDataset infos = CSViewCall.call(this, "SS.QueryScoreAssigneeSVC.updateAssigneeInfo", pageData);
  		setAjax(infos.getData(0));
  	}
    
    public void StopAssignee(IRequestCycle cycle) throws Exception
 	{
 		IData pageData = getData();
 		pageData.put("OPT_TYPE", "04");
 		IDataset infos = CSViewCall.call(this, "SS.QueryScoreAssigneeSVC.updateAssigneeInfo", pageData);
 		setAjax(infos.getData(0));
 	}
    
    public void StartAssignee(IRequestCycle cycle) throws Exception
 	{
 		IData pageData = getData();
 		pageData.put("OPT_TYPE", "05");
 		IDataset infos = CSViewCall.call(this, "SS.QueryScoreAssigneeSVC.updateAssigneeInfo", pageData);
 		setAjax(infos.getData(0));
 	}
    
    public abstract void setCond(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

	public abstract void setCount(long count);
	
	public abstract void setStatiCount(long statiCount);

}
