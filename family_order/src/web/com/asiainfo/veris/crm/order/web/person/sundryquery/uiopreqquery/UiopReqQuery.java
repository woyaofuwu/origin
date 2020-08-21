package com.asiainfo.veris.crm.order.web.person.sundryquery.uiopreqquery;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.event.PageEvent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class UiopReqQuery extends PersonBasePage{
	
	@Override
	public void pageBeginRender(PageEvent event) {
		// TODO Auto-generated method stub
		super.pageBeginRender(event);
		
		init();
	}

	public void init() 
	{
		IData cond = new DataMap();
		try {
			if( hasPriv("UIOP_REQQUERY_EXPORT") ){//页面导出权限
				cond.put("UIOP_REQQUERY_EXPORT", "1");
			}else{
				cond.put("UIOP_REQQUERY_EXPORT", "0");
			}
			
			if( hasPriv("UIOP_REQQUERY_DETAIL") ){
				cond.put("UIOP_REQQUERY_DETAIL", "1");
			}else{
				cond.put("UIOP_REQQUERY_DETAIL", "0");
			}
			
			IData pageData = getData();
			String queryfrom=pageData.getString("QUERY_FROM", "");
	        
	        if( "sms".equals(queryfrom) ){//从短信查询界面跳过来
	        	String receivetime = pageData.getString("RECEIVE_TIME", "");
	        	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        	Date receivedate=df.parse(receivetime);
	        	Date starttime = new Date(receivedate.getTime() - 15 * 60 * 1000);//15分钟前
	        	Date endtime = new Date(receivedate.getTime() + 15 * 60 * 1000);//15分钟后
	        	cond.put("START_TIME",df.format(starttime));
	        	cond.put("END_TIME", df.format(endtime));
	        	cond.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER", ""));
	        }else{
	        	cond.put("START_TIME",pageData.getString("START_TIME", ""));
				cond.put("END_TIME",pageData.getString("END_TIME", ""));
	        }

			setCond(cond);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void queryList(IRequestCycle cycle) throws Exception
	{
		
		
		IData pageData = getData();
        IData inparam = new DataMap();
        
        String queryfrom=pageData.getString("QUERY_FROM", "");
        
        if( "sms".equals(queryfrom) ){//从短信查询界面跳过来
        	String receivetime = pageData.getString("RECEIVE_TIME", "");
        	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        	Date receivedate=df.parse(receivetime);
        	Date starttime = new Date(receivedate.getTime() - 15 * 60 * 1000);//15分钟前
        	Date endtime = new Date(receivedate.getTime() + 15 * 60 * 1000);//15分钟后
        	inparam.put("START_TIME",df.format(starttime));
        	inparam.put("END_TIME", df.format(endtime));
        	inparam.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER", ""));
        	
        }else{
	        inparam.put("SERIAL_NUMBER", pageData.getString("SERIAL_NUMBER", ""));
	        inparam.put("IN_MODE_CODE", pageData.getString("IN_MODE_CODE", ""));
	        inparam.put("START_TIME", pageData.getString("START_TIME", ""));
	        inparam.put("END_TIME", pageData.getString("END_TIME", ""));
        }
        
        IDataOutput out= CSViewCall.callPage(this, "SS.QueryUiopReq.QueryList", inparam, getPagination("page"));

        setInfos(out.getData());
        
        setCond(inparam);
        
        setListCount(out.getDataCount());
	}
	
	public abstract void setCond(IData cond);
	
	public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setListCount(long listCount);

}
