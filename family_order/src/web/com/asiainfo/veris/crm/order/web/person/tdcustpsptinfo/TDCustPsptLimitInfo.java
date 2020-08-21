
package com.asiainfo.veris.crm.order.web.person.tdcustpsptinfo;

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
import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

public abstract class TDCustPsptLimitInfo extends PersonBasePage
{

    private static Logger logger = Logger.getLogger(TDCustPsptLimitInfo.class);


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
            if(isgroupPtsp && !StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT"))
            {
            	
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
            }
        }

        dataParam.put("edit_table", submitInfoSet);
        dataParam.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());

        // 执行批量操作逻辑
        IDataset dataSet = CSViewCall.call(this, "SS.TDCustPsptLimitInfoSVC.submitPsptLimit", dataParam);
        setAjax(dataSet);

    }
    // 页面初始化方法
    public void init(IRequestCycle cycle) throws Exception
    {
        logger.debug("TDCustPsptLimitInfo------>init");
        logger.error("CCCCCCCCCCCC"+getTradeEparchyCode());
        System.out.println("sdsahdsahdsa");
        // 获取用户阈值调整权限
        if (StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_GROUPUSEROPENLIMIT"))
        {
            setPrivateFlag(true);
        }
        else
        {
            setPrivateFlag(false);
        }
        // 获取用户导出权限
        if(StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "isCustPspstLimitInfo")){
        	//有权限
        	setExportFlag("true");
        }else{
        	//无权限
        	setExportFlag("false");
        }
        // 获取员工部门编码
        IData param=new DataMap();
        param.put("DEPART_ID", this.getVisit().getDepartId());
        setParam(param);
    }

    public void queryCustPsptLimitInfo(IRequestCycle cycle) throws Exception
    {
        log.debug("-----------TDCustPsptLimitInfo----------queryCustPsptLimitInfo");
        log.debug("-----------TDCustPsptLimitInfo----------getTradeEparchyCode+"+getTradeEparchyCode());
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
        // 获取前台的证件类型标记
        String pstTag= inputData.getString("PSPT_TAG");
        String  qryPsptCode="";
        if("0".equals(pstTag)){
        	//个人证件类型
        	qryPsptCode="('0','1','2','3','A','B','C','F','H','I','J','K','N','O','Z')";
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

        logger.debug("-----------TDCustPsptLimitInfo----------inputData"+inputData);
        
        // 服务返回结果集
        IDataOutput result = CSViewCall.callPage(this, "SS.TDCustPsptLimitInfoSVC.queryLimitInfo", inputData, page);

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
    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

    public abstract void setPrivateFlag(boolean flag);
    
    public abstract void setCustPsptLimtInfoView(IData infoView);
    
    public abstract void setExportFlag(String value);
    
    public abstract void setParam(IData param);
}
