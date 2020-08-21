
package com.asiainfo.veris.crm.order.web.group.param.desktoptel;

import org.apache.log4j.Logger;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.ProxyParam;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.custinfo.contractinfo.ContractInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

/**
 * @description 多媒体桌面电话参数页面处理类
 * @author yish
 */
public class UserParamInfo extends IProductParamDynamic
{
    private static transient Logger logger = Logger.getLogger(UserParamInfo.class);

    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
            parainfo = result.getData("PARAM_INFO");

        // 查询VPN信息
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(bp, data.getString("USER_ID", ""), false);

        if (IDataUtil.isNotEmpty(userVpnData))
        {
            parainfo.put("VPN_NO", userVpnData.getString("VPN_NO"));
        }

        // 设置变更操作
        parainfo.put("METHOD_NAME", "ChgUs");
        result.put("PARAM_INFO", parainfo);

        return result;
    }

    /**
     * @description 初始化 集团产品受理 参数页面
     * @author yish
     */
    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }

        String cust_id = data.getString("CUST_ID", "");

        IData params = new DataMap();
        params.put("CUST_ID", cust_id);

        // 查集团客户的vpn信息
        //IDataset uservpninfos = CSViewCall.call(bp, "CS.UserVpnInfoQrySVC.getUserVPNInfoByCstId", params);
        IDataset uservpninfos = CSViewCall.call(bp, "CS.UserVpnInfoQrySVC.getUserDesktopVPNInfoByCstId", params);
        if (IDataUtil.isNotEmpty(uservpninfos))
        {
            parainfo.put("VPNS", uservpninfos);
        }

        // 设置为受理操作
        parainfo.put("METHOD_NAME", "CrtUs");
        result.put("PARAM_INFO", parainfo);

        return result;
    }
	/**
	  * 800109资费的规则
	  * 1.集团客户需满足签约两年（及以上）及开通固话数5部（含）以上的条件方可填写功能费（Y值），否则Y值默认为0且不可修改
	  * 
	  * 
	  */
    public IData checkRule800109(IBizCommon bp, IData data) throws Throwable
    {
        IData results = new DataMap();
        IData paramresult = new DataMap();
        boolean result = false;   //是否满足条件（签约两年且有5部及以上固话）
         
        //合同ID
        String selContractId = data.getString("SELE_CONTRACTPRODUCT_CONTRACT_ID", "");
        IData contractInfo = ContractInfoIntf.qryContractByContractIdForGrp(bp, selContractId);
        if(IDataUtil.isNotEmpty(contractInfo)){
        	//判断是否签约两年
            String conSDate = contractInfo.getString("CONTRACT_START_DATE");	//签约开始时间
            String conEDate = contractInfo.getString("CONTRACT_END_DATE");		//签约结束时间
            String date2 = SysDateMgr.addYears(conSDate, 1);	//自定义套餐[800109]签约时长要求由2年改为1年
            int result1 = SysDateMgr.compareTo(conEDate, date2);
            //集团客户需满足签约1年（及以上）
            if(result1 >= 0){
            	result = true;
            }
        }
        paramresult.put("RESULT", result);
        results.put("AJAX_DATA", paramresult);
        return results;
    }
    /**
     * 800109资费
     * 获取月功能费X可录入的最大上限
     */
    public IData getMaxMonthFuncFee(IBizCommon bp, IData data) throws Throwable
    {
    	IData results = new DataMap();
        IData paramresult = new DataMap();
    	IData data1 = new DataMap();
    	data1.put("OFFER_TYPE", "D");
    	data1.put("OFFER_ID", "800109");
    	data1.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());
    	IDataset idataset = CSViewCall.call(bp, "CS.UItemAInfoQrySVC.qryOfferChaSpecsByIdAndIdType", data1);
    	String maxVal = "30000";
    	if(IDataUtil.isNotEmpty(idataset)){
    		 for (int i = 0, size = idataset.size(); i < size; i++)
             {
	    		IData idata = idataset.getData(i);
	    		String attrCode = idata.getString("ATTR_CODE");
	    		if(StringUtils.equals(attrCode, "20000002")){
	    			maxVal = idata.getString("ATTR_FIELD_MAX");
	    			break;
	    		}
             }
    	}
    	//判断是否有放开月功能费上限的权限[NOCHECK_IMS_800109MAXVAL]
    	String staffId = ((ProxyParam) bp).getVisit().getStaffId();
        String dataPriv = "NOCHECK_IMS_800109MAXVAL";
    	boolean hasPriv = StaffPrivUtil.isPriv(staffId, dataPriv, StaffPrivUtil.PRIV_TYPE_FIELD);
    	if(hasPriv){
    		maxVal = "-1";		//最大值为-1则不需要校验月功能费上限
    	}
    	boolean result = true;
        paramresult.put("RESULT", result);
        paramresult.put("MAX_VAL", maxVal);
        results.put("AJAX_DATA", paramresult);
        return results;
    }
}
