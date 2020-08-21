
package com.asiainfo.veris.crm.order.web.person.badness;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BadWebInfoDeal extends PersonBasePage
{

	static  Logger logger=Logger.getLogger(BadWebInfoDeal.class);
    /**
     * 点击提交按钮
     * 
     * @create_date May 31, 2012
     * @author wenhj
     */
    public void BadWebInfoSubmit(IRequestCycle cycle) throws Exception
    {

    	try {
            // 前台传入
            IData inputData = this.getData();
            //批量删除标志
           String del_all_flag=inputData.getString("del_all_flag");
           if(!"".equals(del_all_flag)){
        	   	//批量删除
        	   try {
        		   CSViewCall.call(this, "SS.BadWebInfoDealSVC.updateBadWebInfoByCond", inputData);
        		   IDataset dataset=new DatasetList();
        		   IData data=new DataMap();
        		   data.put("DELETE_COOUNTS", "1");
        		   dataset.add(data);
        		   setAjax(dataset);
				} catch (Exception e) {
					if(logger.isInfoEnabled()) logger.info(e);
					throw e;
				}
        	   
           }else{
        	   
               String encodestr = inputData.getString("edit_table");

               if (encodestr == null || encodestr.length() < 1)
               {
                   return;
               }
	        	  try {
	                   // 服务输入参数；
	                   IData dataParam = new DataMap();
	                   // 将拼串结合串头描述结合生成数据集
	                   IDataset submitInfoSet = new DatasetList(encodestr); 
	
	                   dataParam.put("edit_table", submitInfoSet);
	                   dataParam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
	
	                   // 执行批量操作逻辑
	                   IDataset dataSet = CSViewCall.call(this, "SS.BadWebInfoDealSVC.submitBadWebInfo", dataParam);
	                   setAjax(dataSet);
				} catch (Exception e) {
					if(logger.isInfoEnabled()) logger.info(e.getMessage());
					String errorMsg=e.getMessage();
					if(errorMsg.contains("违反唯一约束条件")){
						CSViewException.apperr(CrmCommException.CRM_COMM_1167);
					}else{
						throw e;
					}
				}

           }
           

		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e.getMessage());
		    throw e;
		}



    }

    public void init(IRequestCycle cycle) throws Exception
    {
        IData staffInfo = new DataMap();
        
        staffInfo.put("NGBOSS_STAFF_ID", this.getVisit().getStaffId());
        staffInfo.put("NGBOSS_STAFF_NAME", this.getVisit().getStaffName());
	    
        setStaffInfo(staffInfo);
    }

    public void queryBadWebInfo(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData("cond", true);
        Pagination page = getPagination("pageNav");
        inputData.put("DEPART_ID", this.getVisit().getDepartId());
        inputData.put("EPARCHY_CODE", getTradeEparchyCode());
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        
        
        
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.BadWebInfoDealSVC.queryBadWebInfo", inputData, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        // 设置页面返回数据
        setInfos(dataset);
        setCondition(inputData);
        setPageCount(result.getDataCount());
    }
    
    /**
     * 
     * @param cycle
     * @throws Exception
     */
    public void importBadwebinfo(IRequestCycle cycle) throws Exception{
    	 try {
	    	IData pageData = getData();
	    	CSViewCall.call(this, "SS.BadWebInfoDealSVC.importBadwebinfo", pageData);
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e.getMessage()+""+e.getLocalizedMessage());
		     throw e;
		}

    }
    
    

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

    public abstract void setStaffInfo(IData staffinfo);

}
