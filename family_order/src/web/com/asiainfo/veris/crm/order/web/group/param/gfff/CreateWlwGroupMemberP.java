
package com.asiainfo.veris.crm.order.web.group.param.gfff;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;

public class CreateWlwGroupMemberP extends IProductParamDynamic
{
    /**
     * 成员变更初始化 
     * @author sht
     */
    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        
        IData parainfo = result.getData("PARAM_INFO");
        String groupType = "0";
        String grpUserId = data.getString("GRP_USER_ID","");
        if(StringUtils.isNotBlank(grpUserId)){
            IData param = new DataMap();
            param.put("USER_ID", grpUserId);
            param.put("SERVICE_ID", "99011015");
            param.put("ATTR_CODE", "GroupType");
            IDataset attrDataSet = CSViewCall.call(bp, "CS.UserAttrInfoQrySVC.queryGrpUserAttrGroupTypeByUserId", param);
            if (IDataUtil.isNotEmpty(attrDataSet))
            {
            	groupType = attrDataSet.getData(0).getString("ATTR_VALUE","0");
            }
        }
        parainfo.put("GROUP_TYPE", groupType);
        
        initApplyTypeA(bp, result);
        return result;
    }
    
    /**
     * initApplyTypea初始化函数
     * @author yanwu
     * @date 2015-08-05
     * @param bp
     * @param data
     * @throws Exception 
     * @throws Throwable
     */
    public void initApplyTypeA(IBizCommon bp, IData result) throws Exception
    {
        IData parainfo = result.getData("PARAM_INFO");
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "9980");
        param.put("PARAM_CODE", "1");
        param.put("EPARCHY_CODE", "0898");
        IDataset applyTypeas = CSViewCall.call(bp, "CS.CommparaInfoQrySVC.getCommpara", param);
        
        parainfo.put("APPLY_TYPE_A_LIST", applyTypeas);
    } 
    
    /**
     * @author yanwu
     * @date 2015-08-05
     * @param bp
     * @param data
     * @return
     * @throws Throwable
     */
    public IData queryApplyTypebs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = new DataMap();
        //String strPARAM_INFO = data.getString("PARAM_INFO", "");strPARAM_INFOdata.toString()
        IData output = new DataMap();
        String strTA = data.getString("APPLY_TYPE_A", "");
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "9980");
        param.put("PARAM_CODE", strTA);
        param.put("EPARCHY_CODE", "0898");
        IDataset applyTypebs = CSViewCall.call(bp, "CS.CommparaInfoQrySVC.getCommpara", param);
        output.put("APPLY_TYPE_B_LIST", applyTypebs);
        
        result.put("AJAX_DATA", output);
        return result;
    }

	@Override
	public IData initChgMb(IBizCommon bp, IData data) throws Throwable {
		IData result =  super.initChgMb(bp, data);
		
		IData parainfo = result.getData("PARAM_INFO");
        String groupType = "0";
        String grpUserId = data.getString("GRP_USER_ID","");
        if(StringUtils.isNotBlank(grpUserId)){
            IData param = new DataMap();
            param.put("USER_ID", grpUserId);
            param.put("SERVICE_ID", "99011015");
            param.put("ATTR_CODE", "GroupType");
            IDataset attrDataSet = CSViewCall.call(bp, "CS.UserAttrInfoQrySVC.queryGrpUserAttrGroupTypeByUserId", param);
            if (IDataUtil.isNotEmpty(attrDataSet))
            {
            	groupType = attrDataSet.getData(0).getString("ATTR_VALUE","0");
            }
        }
        parainfo.put("GROUP_TYPE", groupType);
        
        return result;
	}
	
	  /**
     * @author wangzx3
     * 获取通用APN对应的节电参数模板
     * @date 2015-08-05
     * @param cycle
     * @throws Exception
     */
    public IData queryCommonApnTemplate(IBizCommon bp, IData result) throws Exception
    {
        
        String apnName = result.getString("APNNAME", "");
       
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
        param.put("PARAM_ATTR", "1511");
        param.put("PARAM_CODE", apnName);
        param.put("EPARCHY_CODE", "0898");
        IDataset apnTemplates = CSViewCall.call(bp, "CS.CommparaInfoQrySVC.getCommpara", param);
        IData apnTemplate=apnTemplates.getData(0);
       
        //para_code1对应低功耗模式，para_code2对应RAU/TAU定时器是否需填
        apnTemplate.put("LOWPOWERMODE", apnTemplate.getString("PARA_CODE1"));
        apnTemplate.put("RAUTAUTIMER", apnTemplate.getString("PARA_CODE2"));
        result.put("AJAX_DATA", apnTemplate);
        return result;
    }
    
}
