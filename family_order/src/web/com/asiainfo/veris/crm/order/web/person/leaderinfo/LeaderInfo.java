package com.asiainfo.veris.crm.order.web.person.leaderinfo;

import java.net.URLDecoder;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

/**
 * 领导信息
 * @author mengqx
 *
 */
public abstract class LeaderInfo extends PersonQueryPage
{

    public void getLeaderInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        
        
        IDataOutput outPut = CSViewCall.callPage(this, "SS.LeaderInfoSVC.queryLeaderInfo", param, getPagination("navt"));
        IDataset  results = outPut.getData();
        String alertInfo = "";
        if (IDataUtil.isEmpty(results))
        {
            alertInfo = "没有符合查询条件的【领导信息结果】数据~";
        }
    	/*if (result == null || result.size() == 0) { // 未找到记录
    		setStatiCount(0);
    	
		}*/
		else{
			setInfos(results);
		}
        setCount(outPut.getDataCount());
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

    }
    
    public void initAddPage(IRequestCycle cycle) throws Exception
   	{
   		IData pageData = getData();
   		String sn = pageData.getString("SERIAL_NUMBER");
   		pageData.put("SERIAL_NUMBER", sn);
   		setCond(pageData);
   	}
    
    public void initEditPage(IRequestCycle cycle) throws Exception
   	{
   		IData pageData = getData();
		String name = pageData.getString("LEADER_NAME");
		String position = pageData.getString("POSITION");
		name = URLDecoder.decode(URLDecoder.decode(name,"UTF-8"),"UTF-8");
		position = URLDecoder.decode(URLDecoder.decode(position,"UTF-8"),"UTF-8");
		pageData.put("LEADER_NAME", name);
		pageData.put("POSITION", position);
		setCond(pageData);
   	}
    
    public void initDeletePage(IRequestCycle cycle) throws Exception
   	{
   		IData pageData = getData();
		String name = pageData.getString("LEADER_NAME");
		String position = pageData.getString("POSITION");
		name = URLDecoder.decode(URLDecoder.decode(name,"UTF-8"),"UTF-8");
		position = URLDecoder.decode(URLDecoder.decode(position,"UTF-8"),"UTF-8");
		pageData.put("LEADER_NAME", name);
		pageData.put("POSITION", position);
		setCond(pageData);
   	}
    
    public void insertLeaderInfo(IRequestCycle cycle) throws Exception
    {
    	IData pageData = getData();
    	IDataset result = CSViewCall.call(this, "SS.LeaderInfoSVC.insertLeaderInfo", pageData);
    	this.setAjax(result);
    }
    
    public void updateLeaderInfo(IRequestCycle cycle) throws Exception
    {
    	IData pageData = getData();
    	IDataset result = CSViewCall.call(this, "SS.LeaderInfoSVC.updateLeaderInfo", pageData);
    	this.setAjax(result);
    }
    
    public void deleteLeaderInfo(IRequestCycle cycle) throws Exception
    {
    	IData pageData = getData();
    	IDataset result = CSViewCall.call(this, "SS.LeaderInfoSVC.deleteLeaderInfo", pageData);
    	this.setAjax(result);
    }


    public abstract void setCond(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

	public abstract void setCount(long count);
	
}

