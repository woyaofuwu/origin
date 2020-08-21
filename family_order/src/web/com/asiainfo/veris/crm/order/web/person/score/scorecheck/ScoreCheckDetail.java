
package com.asiainfo.veris.crm.order.web.person.score.scorecheck; 
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ScoreCheckDetail extends PersonBasePage
{  
    public void queryScoreCheckDet(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        //Pagination page = getPagination("recordDetNav");
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        //IDataset result = CSViewCall.callPage(this, "SS.ScoreCheckQrySVC.queryScoreTradeDet", data, page); 
        IDataset result = CSViewCall.call(this, "SS.ScoreCheckQrySVC.queryScoreTradeDet", data);
//        long dataCount=result.getDataCount(); 
//        if(dataCount==0){
//        	IDataset ds=result.getData();
//        	dataCount=ds.size();
//        }
        //setRecordDetCount(dataCount);
        setInfos(result);
    }
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRecordDetCount(long recordCount); 
    
}
