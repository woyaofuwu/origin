
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.requestdata;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class SuspendAndResumeReqData extends BaseReqData
{

    private List<String> resumeList = new ArrayList<String>();// 要恢复的服务列表

    private List<String> suspendList = new ArrayList<String>(); // 要暂停的服务列表

    private List<String> svcInstIdList = new ArrayList<String>(); // 要暂停的服务实例id列表
    
	private String more_oper = "";

    private String close_oper = ""; //流量用尽关停接口受理标志
    
    public String getMore_oper() {
		return more_oper;
	}

	public void setMore_oper(String more_oper) {
		this.more_oper = more_oper;
	}
	
	public String getClose_oper() {
		return close_oper;
	}

	public void setClose_oper(String close_oper) {
		this.close_oper = close_oper;
	}
    public List<String> getResumeList()
    {
        return resumeList;
    }

    public List<String> getSuspendList()
    {
        return suspendList;
    }

    public void setResumeList(List<String> resumeList)
    {
        this.resumeList = resumeList;
    }

    public void setSuspendList(List<String> suspendList)
    {
        this.suspendList = suspendList;
    }

	public List<String> getSvcInstIdList() {
		return svcInstIdList;
	}

	public void setSvcInstIdList(List<String> svcInstIdList) {
		this.svcInstIdList = svcInstIdList;
	}

}
