
package com.asiainfo.veris.crm.order.web.person.goodsapply;

import org.apache.tapestry.IRequestCycle;
 
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class GoodsApplyList extends PersonBasePage
{
    
	 public void init(IRequestCycle cycle) throws Exception
	 { 
	    IDataset expTagList=StaticUtil.getStaticList("EXP_SHOW_TAG");
	    String expTag=expTagList.getData(0).getString("DATA_ID","0");
	    setExpTag(Integer.parseInt(expTag));
	 }
	
    /**
	 * 查询用户礼品信息
	 * */
    public void queryUserScoreGoodsList(IRequestCycle cycle) throws Exception
    {
    	IData data = getData("cond", true);
    	IData data2=getData();
    	String AGENT_DEPART_ID1=data2.getString("AGENT_DEPART_ID1","");
    	if(!"".equals(AGENT_DEPART_ID1)){
    		String departId=AGENT_DEPART_ID1.substring(0,5);
    		data.put("DEPART_ID", departId);
    	}
        Pagination page = getPagination("recordNav"); 
        //inparam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        //取该用户的礼品信息TL_B_USER_SCORE_GOODS  
        IDataOutput result = CSViewCall.callPage(this, "SS.GoodsApplySVC.queryGoodsList", data, page); 
        
        //回传礼品信息
        long dataCount=result.getDataCount();
        setRecordCount(dataCount);
//      ////////输出兑换zhuangta//////////////////
        IDataset goodset=result.getData();
        if(goodset!=null &&goodset.size()>0){
        	for(int k=0;k<goodset.size();k++){
        		String state=goodset.getData(k).getString("STATE");
        		if("0".equals(state)){
        			state="未领取";
        		}else if("1".equals(state)){
        			state="已领取部分";
        		}else if("2".equals(state)){
        			state="全部领取完成";
        		}else if("3".equals(state)){
        			state="已返销";
        		}
        		
        		goodset.getData(k).put("GOODS_STATE", state);

        	}
        }
//      //////////////////////////
       //setRecordCount(dataCount);
         
        setCond(data);
        setInfos(result.getData());
    }
    
    
    /**
     * 礼品领取延期批量导入
     * chenxy3 20161111
     * */
    public void importDelayData(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset importResults=CSViewCall.call(this, "SS.GoodsApplySVC.importDelayData", pageData);
        setBatInfos(importResults);
    }
    
    public abstract void setCond(IData info);
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRecordCount(long recordCount);  
    
    public abstract void setExpTag(int expTag);
    public abstract void setBatInfos(IDataset batInfos);
}
