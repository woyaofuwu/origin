
package com.asiainfo.veris.crm.order.web.person.CancelAutoBookWN;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CancelAutoBookWN extends PersonBasePage
{

    /**
     * 点击提交按钮
     * 
     * @create_date May 31, 2012
     * @author wenhj
     */
    public void CancelAutoBookInfoSubmit(IRequestCycle cycle) throws Exception
    {

        // 前台传入
        IData inputData = this.getData();
        Pagination page = getPagination("pageNav");
        String encodestr = inputData.getString("edit_table");

        if (encodestr == null || encodestr.length() < 1)
        {
            return;
        }

//        String tempModifyTag = "";
//        int limitCount = 0;
        // 服务输入参数；
        IData dataParam = new DataMap();
        // 将拼串结合串头描述结合生成数据集
        IDataset submitInfoSet = new DatasetList(encodestr); 
        // 权限检查
/*        for (int i = 0; i < submitInfoSet.size(); i++)
        {
            tempModifyTag = submitInfoSet.getData(i).getString("tag", "");
            limitCount = submitInfoSet.getData(i).getInt("LIMIT_COUNT", 5);
            String psptTypeCode = submitInfoSet.getData(i).getString("PSPT_TYPE_CODE", "").trim();
            boolean isgroupPtsp = false;
            if(StringUtils.equals("E",psptTypeCode )||StringUtils.equals("G",psptTypeCode )||StringUtils.equals("M",psptTypeCode )||StringUtils.equals("D",psptTypeCode )||StringUtils.equals("L",psptTypeCode )){
            	isgroupPtsp = true;
            }
            
            //如果是集团证件，并且具有将开户阀值改至200
            if(isgroupPtsp && StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSERLIMITCOUNT"))
            {
            	if(("0".equals(tempModifyTag) || "2".equals(tempModifyTag)) && limitCount > 200)
            	{
            		CSViewException.apperr(ElementException.CRM_ELEMENT_2126);
            	}
            }else if(("0".equals(tempModifyTag) || "2".equals(tempModifyTag)) && limitCount > 30)
            {
            	 if (!StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_SPECUSEROPENLIMIT"))
                 {
                     CSViewException.apperr(ElementException.CRM_ELEMENT_2124);
                 }
            }
           
        }*/

        dataParam.put("edit_table", submitInfoSet);
        dataParam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 执行批量操作逻辑
        IDataset dataSet = CSViewCall.call(this, "SS.CancelAutoBookWNSVC.submitBookWNLimit", dataParam);
        setAjax(dataSet);

        // this.queryCustPsptLimitInfo(cycle);

        // pd.setParameter("resultCounts",
        // ""+dataSet[0]+","+counts[1]+","+counts[2]);

    }

    public void init(IRequestCycle cycle) throws Exception
    {
      /*  if ((StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_SPECUSEROPENLIMIT")) || (StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSERLIMITCOUNT")))
        {
            setPrivateFlag(true);
        }
        else
        {
            setPrivateFlag(false);
        }*/
    }

    public void queryCancelInfo(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData("cond", true);
        Pagination page = getPagination("pageNav");
        inputData.put("DEPART_ID", this.getVisit().getDepartId());
        inputData.put("EPARCHY_CODE", getTradeEparchyCode());
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        // 用户是否有查看全部权限
      /*  if ("CustPsptLimitQuery".equals(this.getPageName()) || StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_SPECUSEROPENLIMIT") || StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSERLIMITCOUNT"))
        {
            inputData.put("QryLimit", false);
        }
        else
        {
            inputData.put("QryLimit", true);
        }*/
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.CancelAutoBookWNSVC.queryLimitInfo", inputData, page);

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

    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

    public abstract void setPrivateFlag(boolean flag);

}
