package com.asiainfo.veris.crm.iorder.web.igroup.esop.quickopen;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;


public abstract class QuickOpenLineAddr extends CSBasePage
{

	 public abstract void setInfos(IDataset infos);
	 public abstract void setInfo(IData info);
	 public abstract void setCondition(IData condition);
	 public abstract void setCount(long count);
	 
	    /**
	     * 查询快速标准开通地址
	     * @param cycle
	     * @throws Exception
	     */
	    
	    public void queryLineAddr(IRequestCycle cycle) throws Exception
	    {
	        IData data = new DataMap();
	        IData pagedata= getData("cond",true);
	        data.put("STANDARD_ADDR", pagedata.getString("STANDARD_ADDR"));
	        data.put("CITY", pagedata.getString("CITY"));
	        data.put("AREA", pagedata.getString("AREA"));
	        IDataOutput output = CSViewCall.callPage(this, "SS.LineAddrQrySVC.qryQuickOpenLineAddr", data, getPagination("queryNav"));
	        setCount(output.getDataCount());
	        setInfos(output.getData());
	    }
	    
	    public void changeAreaByCity(IRequestCycle cycle) throws Exception
	    {
	        IData pagedata= getData();
	        setCondition(pagedata);
	        IData condition = new DataMap();
	        String city = pagedata.getString("CITY");
	        IDataset areaList = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[]{ "TYPE_ID", "DATA_NAME" }, new String[]{ "CHANGE_AREA_BY_CITY", city});
	        IDataset area = new DatasetList();
	        for (int i = 0; i < areaList.size(); i++) {
                IData temp = areaList.getData(i);
                temp.put("DATA_ID", temp.getString("DATA_ID", ""));
                temp.put("DATA_NAME", temp.getString("DATA_ID", ""));
                area.add(temp);
			}
	        condition.put("AREAS", area);
	        setInfo(condition);
	    }
}
