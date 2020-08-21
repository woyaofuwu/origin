package com.asiainfo.veris.crm.order.web.person.sundryquery.score;
 


import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryScoreTransferor extends PersonQueryPage
{

    /**
     * 查询调整积分
     * 
     * @param cycle
     * @throws Exception
     */
    public void getTransferorInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IDataset outPut = CSViewCall.call(this, "SS.QueryScoreTransferorSVC.queryTransferorInfo", param, getPagination("staticNav"));
        setCond(param);
        IData result=outPut.getData(0);
        String alertInfo = "";
        if (result == null || result.size() == 0) { // 未找到记录
        	setStatiCount(0);
   		 alertInfo = "没有符合条件的数据";
		}
		else if(!"00".equals(result.getString("X_RSPCODE"))){				
			setStatiCount(0);
   		 alertInfo = "没有符合条件的数据";
		}
		else{

			IDataset dataset1=IDataUtil.getDatasetSpecl("L_MOBILE",result); 
			IDataset dataset2=IDataUtil.getDatasetSpecl("EFFECTIVE_DATE",result); 
			IDataset dataset3=IDataUtil.getDatasetSpecl("OPERATOR_TIME",result);
			IDataset dataset4=IDataUtil.getDatasetSpecl("ORG_ID",result); 
			IDataset dataset5=IDataUtil.getDatasetSpecl("ASSIGNEE_STATUS",result); 
			IDataset dataset6=IDataUtil.getDatasetSpecl("USER_ROLE",result); 
			IDataset dataset7=IDataUtil.getDatasetSpecl("USER_ID",result);
			IDataset infos = new DatasetList();
			for(int i=0; i<dataset1.size(); i++){						
				IData info = new DataMap();
				info.put("L_MOBILE",dataset1.get(i));
				info.put("EFFECTIVE_DATE",dataset2.get(i));
				info.put("OPERATOR_TIME",dataset3.get(i));
				info.put("ORG_ID",dataset4.get(i));
				info.put("ASSIGNEE_STATUS",dataset5.get(i));
				info.put("USER_ROLE",dataset6.get(i));
				info.put("USER_ID",dataset7.get(i));
				infos.add(info);
			}
			setInfos(infos);
			   setStatiCount(infos.size());
		}
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
        
    }

    
    /**
     * 受让人历史维护信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void getTransferorInfoHis(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IDataset outPut = CSViewCall.call(this, "SS.QueryScoreTransferorSVC.queryTransferorHis", param, getPagination("staticNav"));
        IData result=outPut.getData(0);
        String alertInfo = "";
        if (result == null || result.size() == 0) { // 未找到记录
        	setStatiCount(0);
   		 alertInfo = "没有符合条件的数据";
		}
		else if(!"00".equals(result.getString("X_RSPCODE"))){				
			setStatiCount(0);
   		 alertInfo = "没有符合条件的数据";
		}
		else{

			IDataset dataset1=IDataUtil.getDatasetSpecl("B_MOBILE",result); 
			IDataset dataset2=IDataUtil.getDatasetSpecl("ASSIGNEE_NAME",result);  
			IDataset dataset3=IDataUtil.getDatasetSpecl("VALIDNUM_TYPE",result);
			IDataset dataset4=IDataUtil.getDatasetSpecl("ASSIGNEE_ID",result);
			IDataset dataset5=IDataUtil.getDatasetSpecl("EFFECTIVE_DATE",result);
			IDataset dataset6=IDataUtil.getDatasetSpecl("OPERATOR_TIME",result);
			IDataset dataset7=IDataUtil.getDatasetSpecl("OPT_TYPE",result);
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
				info.put("OPT_TYPE",dataset7.get(i));
				info.put("ORG_ID",dataset8.get(i));
				info.put("ASSIGNEE_STATUS",dataset9.get(i));
				info.put("USER_ROLE",dataset10.get(i));
				info.put("USER_ID",dataset11.get(i));
				infos.add(info);
			}
			setInfos(infos);
			   setStatiCount(infos.size());
		}
        setCond(param);
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
     
    }

    
    
  
    public void getTransferPoint(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IDataset outPut = CSViewCall.call(this, "SS.QueryScoreTransferorSVC.queryTransferorPoint", param, getPagination("staticNav"));
        IData result=outPut.getData(0);
        String alertInfo = "";
        if (result == null || result.size() == 0) { // 未找到记录
        	setStatiCount(0);
   		 alertInfo = "没有符合条件的数据";
		}
		else if(!"00".equals(result.getString("X_RSPCODE"))){				
			setStatiCount(0);
   		 alertInfo = "没有符合条件的数据";
		}
		else{

			IDataset dataset1=IDataUtil.getDatasetSpecl("TRADE_SEQ",result); 
			IDataset dataset2=IDataUtil.getDatasetSpecl("TRADE_TIME",result); 
			IDataset dataset3=IDataUtil.getDatasetSpecl("ORG_ID",result); 
			IDataset dataset4=IDataUtil.getDatasetSpecl("L_MOBILE",result); 
			IDataset dataset5=IDataUtil.getDatasetSpecl("B_MOBILE",result); 
			IDataset dataset6=IDataUtil.getDatasetSpecl("TRANSFER_POINT",result); 
			IDataset dataset7=IDataUtil.getDatasetSpecl("TRADE_STATUS",result); 
			IDataset dataset8=IDataUtil.getDatasetSpecl("RES_DESC",result); 
			IDataset infos = new DatasetList();
			for(int i=0; i<dataset1.size(); i++){						
				IData info = new DataMap();
				info.put("TRADE_SEQ",dataset1.get(i));
				info.put("TRADE_TIME",dataset2.get(i));
				info.put("ORG_ID",dataset3.get(i));
				info.put("L_MOBILE",dataset4.get(i));
				info.put("B_MOBILE",dataset5.get(i));
				info.put("TRANSFER_POINT",dataset6.get(i));
				info.put("TRADE_STATUS",dataset7.get(i));
				info.put("RES_DESC",dataset8.get(i));
				infos.add(info);
			}
			setInfos(infos);
			   setStatiCount(infos.size());
		}
        setCond(param);
        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
    }
    
    public abstract void setCond(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

	public abstract void setCount(long count);
	
	public abstract void setStatiCount(long statiCount);

}
