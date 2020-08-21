
package com.asiainfo.veris.crm.order.web.person.plat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class QueryPlatSvc extends PersonBasePage
{
	/**
     * @param cycle
     */
    public void init(IRequestCycle cycle)
    {
        IData cond = new DataMap();
        cond.put("cond_BIZ_TYPE_CODE", "1");
        this.setCond(cond);
    }

    /**
     * 用户平台业务订购关系查询
     * 
     * @param pd
     * @param userId
     * @return
     * @throws Exception
     */
    public void qryUserPlatSvc(IRequestCycle cycle) throws Exception
    {
    	long getDataCount = 0;
        IData param = this.getData("cond", true);

        IDataOutput result = CSViewCall.callPage(this, "SS.QueryPlatServiceSVC.qryUserPlatSvcs", param, this.getPagination("pagin"));

        IDataset datas=result.getData();
        
        /*
         * 获取特殊的服务信息
         */
        IData comparaData=new DataMap();
        comparaData.put("SUBSYS_CODE", "CSM");
        comparaData.put("PARAM_ATTR", "4121");
        comparaData.put("PARAM_CODE", "PLATSVC_CONTENT_SVC");
        IDataOutput specialSvcData = CSViewCall.callPage(this, "CS.CommparaInfoQrySVC.getCommparaInfoByBizCode", comparaData,null);
        if(specialSvcData!=null){
        	IDataset specialSvcs=specialSvcData.getData();
        	if(IDataUtil.isNotEmpty(specialSvcs)){
        		
        		IData specialSvc=specialSvcs.getData(0);
        		String serviceId=specialSvc.getString("PARA_CODE1","");
        		
        		if(IDataUtil.isNotEmpty(datas)){
            		for(int i=0,size=datas.size();i<size;i++){
            			IData data=datas.getData(i);
            			if(data.getString("SERVICE_ID","").equals(serviceId)){
            				data.put("PRICE", specialSvc.getString("PARA_CODE2",""));
            				data.put("BIZ_NAME", "");
            			}
            		}
            	}
        		
        	}
        }
        if (datas != null && datas.size() > 0){
        	 for(int i=0;i<datas.size();i++){
             	if("98001901".equals(datas.getData(i).getString("SERVICE_ID")) && "19".equals(datas.getData(i).getString("BIZ_TYPE_CODE"))){          		
             		IDataset DiscntsCode = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.queryDiscntsCodeByusrid", param);
             		if(!IDataUtil.isEmpty(DiscntsCode)){
                 		String Code = DiscntsCode.getData(0).getString("DISCNT_CODE");
                 		if("1237".equals(Code)){
                 			datas.getData(i).put("PRICE", "0"); 
                 		}else if("1238".equals(Code)){
                 			datas.getData(i).put("PRICE", "5000");
                 		}else if("12789".equals(Code)){
                 			datas.getData(i).put("PRICE", "6000");
                 		}
                 	}
                 }
             }
        }
       
        
        if (datas == null )
        {
        }else{
        	getDataCount = Long.parseLong(datas.getData(0).getString("TOTAL"));
        }
        this.setInfos(datas);
        this.setPaginCount(getDataCount);
        this.setCond(this.getData("cond"));
    }

    public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);

    public abstract void setPaginCount(long count);
}
