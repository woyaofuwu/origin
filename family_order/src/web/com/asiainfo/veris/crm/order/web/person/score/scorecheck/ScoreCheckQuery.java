
package com.asiainfo.veris.crm.order.web.person.score.scorecheck; 
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ScoreCheckQuery extends PersonBasePage
{  
    public void queryScoreCheck(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        Pagination page = getPagination("recordNav");
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        IDataOutput result = CSViewCall.callPage(this, "SS.ScoreCheckQrySVC.queryScoreTrade", data, page);
	      long dataCount=result.getDataCount(); 
	      if(dataCount==0){
	      	IDataset ds=result.getData();
	      	dataCount=ds.size();
	      }
        setRecordCount(dataCount);
        setInfos(result.getData());
    }
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRecordCount(long recordCount); 
    
}
