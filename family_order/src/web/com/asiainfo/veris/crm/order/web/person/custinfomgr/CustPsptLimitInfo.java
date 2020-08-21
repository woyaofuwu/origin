
package com.asiainfo.veris.crm.order.web.person.custinfomgr;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
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
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CustPsptLimitInfo extends PersonBasePage
{

	static Logger log=Logger.getLogger(CustPsptLimitInfo.class);
	
    /**
     * 点击提交按钮
     * 
     * @create_date May 31, 2012
     * @author wenhj
     */
    public void custPsptLimitInfoSubmit(IRequestCycle cycle) throws Exception
    {

        // 前台传入
        IData inputData = this.getData();
        Pagination page = getPagination("pageNav");
        String encodestr = inputData.getString("edit_table");

        if (encodestr == null || encodestr.length() < 1)
        {
            return;
        }

        String tempModifyTag = "";
        int limitCount = 0;
        // 服务输入参数；
        IData dataParam = new DataMap();
        // 将拼串结合串头描述结合生成数据集
        IDataset submitInfoSet = new DatasetList(encodestr); 
        // 权限检查
        for (int i = 0; i < submitInfoSet.size(); i++)
        {
            tempModifyTag = submitInfoSet.getData(i).getString("tag", "");
            limitCount = submitInfoSet.getData(i).getInt("LIMIT_COUNT", 5);
            String psptTypeCode = submitInfoSet.getData(i).getString("PSPT_TYPE_CODE", "").trim();
            boolean isgroupPtsp = false;
            if(StringUtils.equals("E",psptTypeCode )||StringUtils.equals("G",psptTypeCode )||StringUtils.equals("M",psptTypeCode )||StringUtils.equals("D",psptTypeCode )||StringUtils.equals("L",psptTypeCode )){
            	isgroupPtsp = true;
            }
            
            //如果是集团证件，并且没有集团用户实名制开户数目限制解除权限
            if(isgroupPtsp && (!StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT") || !StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT_50") || !StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT_100")))
            {
            	if(!StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT")){
            		if(!StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT_100")){
            			if(!StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT_50")){
            				if(("0".equals(tempModifyTag) || "2".equals(tempModifyTag)))
                        	{
                        		/*
                            	 * 如选择调整类型为：0移动电话，则分公司的开户阀值调整权限由200变成20个；如选择类型为：1物联网卡（含IMS、行业应用卡），分公司开户阀值调整权限保持与现状一直（200个）  
                            	 */
                        		String adjustTypeCode = submitInfoSet.getData(i).getString("ADJUST_TYPE_CODE", "");
                        		if("1".equals(adjustTypeCode) && limitCount > 200){
                        			CSViewException.apperr(ElementException.CRM_ELEMENT_2126);
                        		}else if("0".equals(adjustTypeCode) && limitCount > 20){
                        			CSViewException.apperr(ElementException.CRM_ELEMENT_2127);
                        		}
                        	}
                    	}else{
                    		//有50的权限则为0-50的限制
                    		if(("0".equals(tempModifyTag) || "2".equals(tempModifyTag)))
                        	{
                        		/*
                            	 * 如选择调整类型为：0移动电话，则分公司的开户阀值调整权限为50个；如选择类型为：1物联网卡（含IMS、行业应用卡），分公司开户阀值调整权限保持与现状一直（200个）  
                            	 */
                        		String adjustTypeCode = submitInfoSet.getData(i).getString("ADJUST_TYPE_CODE", "");
                        		if("1".equals(adjustTypeCode) && limitCount > 200){
                        			CSViewException.apperr(ElementException.CRM_ELEMENT_2126);
                        		}else if("0".equals(adjustTypeCode) && limitCount > 50){
                        			CSViewException.apperr(ElementException.CRM_ELEMENT_2128);
                        		}
                        	}
                    	}
                	}else{
                		//有100的权限则为0-100的限制
                		if(("0".equals(tempModifyTag) || "2".equals(tempModifyTag)))
                    	{
                    		/*
                        	 * 如选择调整类型为：0移动电话，则分公司的开户阀值调整权限为100个；如选择类型为：1物联网卡（含IMS、行业应用卡），分公司开户阀值调整权限保持与现状一直（200个）  
                        	 */
                    		String adjustTypeCode = submitInfoSet.getData(i).getString("ADJUST_TYPE_CODE", "");
                    		if("1".equals(adjustTypeCode) && limitCount > 200){
                    			CSViewException.apperr(ElementException.CRM_ELEMENT_2126);
                    		}else if("0".equals(adjustTypeCode) && limitCount > 100){
                    			CSViewException.apperr(ElementException.CRM_ELEMENT_2129);
                    		}
                    	}
                	}
            	}
            }
        }

        dataParam.put("edit_table", submitInfoSet);
        dataParam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 执行批量操作逻辑
        IDataset dataSet = CSViewCall.call(this, "SS.CustPsptLimitInfoSVC.submitPsptLimit", dataParam);
        setAjax(dataSet);

        // this.queryCustPsptLimitInfo(cycle);

        // pd.setParameter("resultCounts",
        // ""+dataSet[0]+","+counts[1]+","+counts[2]);

    }

    public void init(IRequestCycle cycle) throws Exception
    {
        if (StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT"))
        {
            setPrivateFlag(true);
        }else
        {
            setPrivateFlag(false);
        }
          if (StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT_50"))
        {
            setPrivateFlag_50(true);
        }else
        {
            setPrivateFlag_50(false);
        }
          if (StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT_100"))
        {
            setPrivateFlag_100(true);
        }else
        {
            setPrivateFlag_100(false);
        }
        
        /**
         * REQ201610120016 优化实名制开户阙值调整界面优化需求
         * @author zhuoyingzhi
         * 20161027
         * 判断是否有权限导出
         * isCustPspstLimitInfo
         * <br/>
         * 由于测试环境无法刷新，员工缓存
         * HNHKB008  StatDataRight 来做测试
         * 
         */
        if(StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "isCustPspstLimitInfo")){
        	//有权限
        	setExportFlag("true");
        }else{
        	//无权限
        	setExportFlag("false");
        }
        
        //员工部门
        IData param=new DataMap();
        param.put("DEPART_ID", this.getVisit().getDepartId());
        setParam(param);
    }

    public void queryCustPsptLimitInfo(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData("cond", true);
        Pagination page = getPagination("pageNav");
        inputData.put("DEPART_ID", this.getVisit().getDepartId());
        inputData.put("EPARCHY_CODE", getTradeEparchyCode());
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        // 用户是否有查看全部权限
        if ("CustPsptLimitQuery".equals(this.getPageName()) || StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT") || StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_SPECUSEROPENLIMIT") || StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSERLIMITCOUNT"))
        {
            inputData.put("QryLimit", false);
        }
        else
        {
            inputData.put("QryLimit", true);
        }
        /**
         * REQ201610120016 优化实名制开户阙值调整界面优化需求
         * @author zhuoyingzhi
         * 20161025
         */
        String pstTag= inputData.getString("PSPT_TAG");
        String  qryPsptCode="";
        if("0".equals(pstTag)){
        	//个人证件类型
        	qryPsptCode="('0','1','2','A','B','C','F','H','I','J','K','N','O','Z')";
        }else if("1".equals(pstTag)){
        	//单位证件类型
   	       /*1、营业执照、
			2、组织机构代码证
			3、事业单位法人证书
			4、社会团体法人登记证书
			5、单位证明*/
        	qryPsptCode="('D','E','G','L','M')";
        }
        inputData.put("QRY_PSPT_CODE", qryPsptCode);
        
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.CustPsptLimitInfoSVC.queryLimitInfo", inputData, page);

        IDataset dataset = result.getData();
        if (IDataUtil.isEmpty(dataset))
        {
            this.setAjax("ALERT_INFO", "无数据");
        }
        // 设置页面返回数据
        setInfos(dataset);
        setCondition(getData("cond", true));
        setPageCount(result.getDataCount());
    }
    /**
     * REQ201610120016 优化实名制开户阙值调整界面优化需求
     * @author zhuoyingzhi
     * 20161021
     * @param cycle
     * @throws Throwable
     */
    public void queryGroupCusts(IRequestCycle cycle) throws Throwable
    {
        IData result = new DataMap();
        try {
            IData conParams = getData("cond", true);
            //集团客户编码
            String id = conParams.getString("RSRV_STR4");
            /**
             * UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId
             * 这个已经存在
             */
            result=UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, id, false);
            //获取客户经理工号
            String custManagerId= result.getString("CUST_MANAGER_ID","");
            //获取客户经理(名称)
            String  custManagerName=StaticUtil.getStaticValue(getVisit(),"TD_M_STAFF","STAFF_ID","STAFF_NAME", custManagerId);
            result.put("CUST_MANAGER_NAME", custManagerName);
            
		} catch (Exception e) {
			   //log.info("(e.getMessage());
			   throw e;
		}
		setCustPsptLimtInfoView(result);
		IData resultAjax=new DataMap();
		  if(result == null){
			  //没有值
			  resultAjax.put("resultNum", 0);
		  }else{
			  resultAjax.put("resultNum", 1);
		  }
		setAjax(resultAjax);
    }
    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

    public abstract void setPrivateFlag(boolean flag);
    
    public abstract void setPrivateFlag_50(boolean flag);
    
    public abstract void setPrivateFlag_100(boolean flag);
    
    public abstract void setCustPsptLimtInfoView(IData infoView);
    
    public abstract void setExportFlag(String value);
    
    public abstract void setParam(IData param);
}
