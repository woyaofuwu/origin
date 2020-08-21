
package com.asiainfo.veris.crm.order.soa.person.busi.goodsapply;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;  
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall; 

public class GoodApplyExpTask extends ExportTaskExecutor
{
    @Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
    	IData input = param.subData("cond", true); 
    	 
    	String AGENT_DEPART_ID1=param.getString("AGENT_DEPART_ID1","");
    	if(!"".equals(AGENT_DEPART_ID1)){
    		String departId=AGENT_DEPART_ID1.substring(0,5);
    		input.put("DEPART_ID", departId);
    	}
        IDataset goodset = CSAppCall.call("SS.GoodsApplySVC.queryGoodsList", input); 
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
        return goodset;
    } 
}
