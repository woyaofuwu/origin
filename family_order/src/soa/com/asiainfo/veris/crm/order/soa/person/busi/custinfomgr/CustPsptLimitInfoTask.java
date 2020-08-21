
package com.asiainfo.veris.crm.order.soa.person.busi.custinfomgr;

import org.apache.log4j.Logger;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;

/**
 * REQ201610120016 优化实名制开户阙值调整界面优化需求
 * @author zhuoyingzhi
 * 20161025
 */
public class CustPsptLimitInfoTask extends ExportTaskExecutor
{
	static  Logger logger=Logger.getLogger(CustPsptLimitInfoTask.class);
	
    
	@Override
    public IDataset executeExport(IData param, Pagination pagination) throws Exception
    {
    	try {
    		//获取查询条件
        	IData inputData = param.subData("cond", true);
        	
            inputData.put("DEPART_ID", inputData.getString("DEPART_ID",""));
            inputData.put("EPARCHY_CODE", getTradeEparchyCode());
            inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
            // 用户是否有查看全部权限
            if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_SPECUSEROPENLIMIT") || StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_GROUPUSERLIMITCOUNT"))
            {
                inputData.put("QryLimit", false);
            }
            else
            {
                inputData.put("QryLimit", true);
            }
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
            
        	IDataset  result = CSAppCall.call("SS.CustPsptLimitInfoSVC.queryLimitInfo",inputData);
        	
        	if(IDataUtil.isNotEmpty(result)){
        		for(int i=0;i<result.size();i++){
        			//证件类型转换
        			 String psptTypeCode=result.getData(i).getString("PSPT_TYPE_CODE","");
        			 if(!"".equals(psptTypeCode) && psptTypeCode!=null){
            			 result.getData(i).put("PSPT_TYPE_NAME",
            					 StaticUtil.getStaticValue(getVisit(),
            								"TD_S_PASSPORTTYPE", new String[] {
            										"PSPT_TYPE_CODE", "EPARCHY_CODE" },
            								"PSPT_TYPE", new String[] {psptTypeCode, "0898" }));
        			 }
        			 //调整类型转换
        			 String adjustTypeCode=result.getData(i).getString("RSRV_STR1","");
        			 if(!"".equals(adjustTypeCode) && adjustTypeCode!=null){
            			 result.getData(i).put("ADJUST_TYPE_NAME",
            					 StaticUtil.getStaticValue(getVisit(),
            								"TD_S_STATIC", new String[] {
            										"DATA_ID", "TYPE_ID" },
            								"DATA_NAME", new String[] {adjustTypeCode, "ADJUST_TYPE" }));
        			 }
        		}
        	}
        	return result;
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    } 
}
