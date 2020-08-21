package com.asiainfo.veris.crm.iorder.soa.group.param.jwtvpn;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;

public class QueryCrtMbJwtVpnAttrParamBean extends QueryAttrParamBean
{
   
    public static IData queryJwtVpnAttrForChgInit(IData param) throws Exception
    {
    	/*IData result = super.initCrtMb(bp, data);
	    IData parainfo = new DataMap();
	    if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
	    {
	        parainfo = result.getData("PARAM_INFO");
	    }*/
    	
    	IData result = new DataMap();
        IDataset dataset =  UItemAInfoQry.queryOfferChaAndValByCond("8060", "P", "1", null);
        
        IData attrItemA = new DataMap();
        
        
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
        }
        else
        {
            attrItemA = new DataMap();
        }
	
	    String grpuserId = param.getString("EC_USER_ID");
	    // 集团用户信息查询
	    //IData grpUserInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(bp, grpuserId, false);
	    IData grpUserparam=new DataMap();
	    grpUserparam.put("USER_ID", grpuserId);
        IData grpUserInfo = CSAppCall.callOne("CS.UcaInfoQrySVC.qryUserMainProdInfoByUserIdForGrp", grpUserparam);
	    if (IDataUtil.isEmpty(grpUserInfo))
	    {
	        // j2ee "集团用户编码【userId】的集团用户信息查询不存在！"
	        CSAppException.apperr(VpmnUserException.VPMN_USER_28);
	    }
	    String userProductCode = "";
	    if (IDataUtil.isNotEmpty(grpUserInfo))
	    {
	        userProductCode = grpUserInfo.getString("SERIAL_NUMBER", "");
	    }
	
	    String staffId = getVisit().getStaffId();
	    String rightCode = "VPN_SHORT_CODE";
	    boolean shortCodeRight = false;
	    IData data1 = new DataMap();
	    data1.put("USER_PRODUCT_CODE", userProductCode);
	    data1.put("RIGHT_CODE", rightCode);
	    // 成员短号操作权限：先判断该VPMN集团是否有短号权限限制
	    IDataset shortCodeRight1 = CSAppCall.call("CS.StaffInfoQrySVC.queryGrpRightByIdCode", data1);
	
	    // 有短号权限限制，判断该员工是否有权限
	    if (IDataUtil.isNotEmpty(shortCodeRight1))
	    {
	        IDataset shortCodeRight2 = DataHelper.filter(shortCodeRight1, "STAFF_ID=" + staffId);
	        if (IDataUtil.isNotEmpty(shortCodeRight2))
	        {
	            shortCodeRight = true;
	        }
	    }
	    else
	    {// 没有短号权限限制就跳过
	        shortCodeRight = true;
	    }
	
	    IData userVpnParam = new DataMap();
	    userVpnParam.put("USER_ID", grpuserId);
	    //userVpnParam.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());
	    //IData vpninfo = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(bp, grpuserId);
        IData vpninfo = CSAppCall.callOne("CS.UserVpnInfoQrySVC.qryUserVpnByUserId", userVpnParam);
	    // IDataset vpninfos = CSViewCall.call(bp, "CS.UserVpnInfoQrySVC.getUserVpn", param);
	    // IData vpninfo = new DataMap();
	    if (IDataUtil.isEmpty(vpninfo))
	        CSAppException.apperr(VpmnUserException.VPMN_USER_6);
	
	    String vpn_scare_code = vpninfo.getString("VPN_SCARE_CODE", "");
	    //IDataset defualtDiscntset = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndEparchyCode(bp, "CGM", "80", Route.getCrmDefaultDb());
	    IData inparam = new DataMap();
        inparam.put("SUBSYS_CODE", "CGM");
        inparam.put("PARAM_ATTR", "80");
        inparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset defualtDiscntset = CSAppCall.call("CS.CommparaInfoQrySVC.getCommparaInfoByAttr", inparam);
        
	    String PROVICE_VPN_DISCNT = "";
	    if (IDataUtil.isNotEmpty(defualtDiscntset))
	    {
	        for (int i = 0; i < defualtDiscntset.size(); i++)
	        {
	            String discntinfo = defualtDiscntset.getData(i).getString("PARA_CODE1", "");
	            if ("".equals(PROVICE_VPN_DISCNT))
	                PROVICE_VPN_DISCNT = discntinfo;
	            else
	                PROVICE_VPN_DISCNT = PROVICE_VPN_DISCNT + "," + discntinfo;
	        }
	    }
	    attrItemA.put("VPN_SCARE_CODE", vpn_scare_code);
	    attrItemA.put("PROVICE_VPN_DISCNT", PROVICE_VPN_DISCNT);
	
	    IData infoParam = new DataMap();
	
	    infoParam.put("USER_ID", grpuserId);
	    // 集团定制信息
	    IDataset userSvcInfos = CSAppCall.call("CS.GrpUserPkgInfoQrySVC.getGrpCustomizeServByUserId", infoParam);
	
	    if (IDataUtil.isNotEmpty(userSvcInfos))
	    {
	        for (int i = 0, iSize = userSvcInfos.size(); i < iSize; i++)
	        {
	            IData svcInfo = (IData) userSvcInfos.get(i);
	            if ("861".equals(svcInfo.getString("ELEMENT_ID", "")))
	            {
	            	attrItemA.put("SERVICE_ID", "861");
	            }
	        }
	    }
	    if (("SUPERUSR".equals(staffId) || shortCodeRight) && "861".equals(attrItemA.getString("SERVICE_ID", "")))
	    {
	    	attrItemA.put("RIGHT_CODE", "yes"); // 自动生成短号
	    }
	    
	    if (IDataUtil.isNotEmpty(attrItemA))
        {
            Set<String> propNames = attrItemA.keySet();
            for (String key : propNames)
            {
                String attrCodeInfo = attrItemA.getString(key);
                IData attrItem = new DataMap();
                if (StringUtils.isNotEmpty(attrCodeInfo))
                {
                    attrItem.put("DATA_VAL", attrCodeInfo);
                    result.put(key, attrItem);
                }
            }
        }
	
	    //result.put("PARAM_INFO", parainfo);
	    return result;
	    }
    
}

