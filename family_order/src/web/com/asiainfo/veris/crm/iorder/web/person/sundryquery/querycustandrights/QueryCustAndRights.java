package com.asiainfo.veris.crm.iorder.web.person.sundryquery.querycustandrights;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryCustAndRights extends PersonBasePage{

	
	 public abstract IDataset getInfos();

	    /**
	     * 功能：权益关系查询
	     */
	    public void queryRealNameInfo(IRequestCycle cycle) throws Exception
	    {
	    	 //String inparam = getPage().getParameter("SPEC_TAG");
	    	IData inparam = getData();
	        //权益类型ID，全球通等级查询
	        String userClass = inparam.getString("USER_INFO_CLASS");
	        String paraCode2 = inparam.getString("PARA_CODE2");
	        String paramAttr = "7173";
	        IDataOutput dataCount=null;
	        IData param = new DataMap();
	        param.put("PARA_CODE2", paraCode2);
	        param.put("PARA_CODE1", userClass);
	        param.put("PARAM_ATTR", paramAttr);
	        IDataset results=CSViewCall.call(this, "SS.QueryCustAndRrightsSVC.queryRightIdByClass", param);
	       // IDataset results = Dao.qryByCode("TD_S_COMMPARA", "SEL_RIGHT_BY_CLASS", param);
	        String alertInfo = "";
	        if (IDataUtil.isEmpty(results))
	        {
	            alertInfo = "查询权益信息无记录~~";
	        }
	       for(int i = 0; i < results.size(); i++){
	    	   String rightid = results.getData(i).getString("PARA_CODE3");
	    	   IData iData = new DataMap();
	    	   String paramAttr2 = "7172";
	    	   iData.put("PARAM_CODE", rightid);
	    	   iData.put("PARAM_ATTR", paramAttr2);
	    	   IDataset dataset=CSViewCall.call(this, "SS.QueryCustAndRrightsSVC.queryRightNameByClass", iData);
	    	   //IDataset dataset =Dao.qryByCode("TD_S_COMMPARA", "SEL_RIGHTNAME_BY_CLASS", iData);
	    		   String rightName = dataset.getData(0).getString("PARA_CODE5");
	    		   results.getData(i).put("RIGHT_NAME", rightName);
	    	   
	    	   
	       }
	        this.setAjax("ALERT_INFO", alertInfo);// 传给页面提示
	        //setAjax(results);
	        setInfos(results);
	        setCount(results.size());

	    }

	    public abstract void setCondition(IData inparam);

	    public abstract void setCount(long count);

	    public abstract void setInfos(IDataset infos);
	
	    public abstract void setInfo(IData info);
	
	
	
	
}
