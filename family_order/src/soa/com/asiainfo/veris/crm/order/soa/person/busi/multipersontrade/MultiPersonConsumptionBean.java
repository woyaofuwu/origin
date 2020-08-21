package com.asiainfo.veris.crm.order.soa.person.busi.multipersontrade;

import java.util.List;

import org.omg.PortableServer.IdAssignmentPolicy;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

public class MultiPersonConsumptionBean extends CSBizBean{
	
	public IData QryMemberInfo(IData input) throws Exception
    {
        IData rtData = new DataMap();
        String userId = input.getString("USER_ID");
        String mainSn = input.getString("SERIAL_NUMBER");

        IData disData = new DataMap();
        disData.put("SUBSYS_CODE", "CSM");
        disData.put("PARAM_ATTR", "8383");
        disData.put("PARAM_CODE", "DR_DISCNT");
        disData.put("USER_ID", userId);
        IDataset discntList = RelaUUInfoQry.qryPrincipalDiscnt(disData);//主号需要有参与【宽带1+多人约消活动】才可以组网
        if (IDataUtil.isEmpty(discntList)) {
        	IDataset snRelationSet = RelaUUInfoQry.queryRelaUUBySnb(mainSn,"61");
			if (IDataUtil.isEmpty(snRelationSet)) {
				rtData.put("RESULT_CODE", "2998");
				rtData.put("RESULT_INFO", "主号需要办理【宽带1+多人约消活动】才可以组网！");
				return rtData;
			}
		}
        
        /*IData params = new DataMap();
		params.put("SUBSYS_CODE", "CSM");
		params.put("PARAM_ATTR", "8383");
		params.put("PARAM_CODE", "DR_DEPLOY_PRODUCT");
		params.put("USER_ID", userId);
		IDataset productList = RelaUUInfoQry.qryPrincipalDiscnt(params);
		if (IDataUtil.isNotEmpty(productList)) 
		{
			rtData.put("RESULT_CODE", "2998");
			rtData.put("RESULT_INFO", "办理企业尊享套餐不允许办理多人约消用户！");
			return rtData;
		}
		
		//一卡付多号校验
		IDataset payMoreCards = RelaUUInfoQry.queryRelaUUBySnb(mainSn, "97");
		if(IDataUtil.isNotEmpty(payMoreCards))
		{
			rtData.put("RESULT_CODE", "2998");
			rtData.put("RESULT_INFO", "该号码存在【一卡付多号】关系，不允许办理！");
			return rtData;
		}
		
		//一卡双号校验
		IDataset payDoubleCards = RelaUUInfoQry.queryRelaUUBySnb(mainSn, "30");
		if(IDataUtil.isNotEmpty(payDoubleCards))
		{
			rtData.put("RESULT_CODE", "2998");
			rtData.put("RESULT_INFO", "该号码存在【一卡双号】关系，不允许办理！");
			return rtData;
		}
		
		UcaData uca = UcaDataFactory.getNormalUca(mainSn);
		//剔除TD固话（TDYD）、移动公话B（G009）、移动公话C（G014）、随e行（G005）等品牌。移动公话存在两个品牌
		if("TDYD".equals(uca.getBrandCode()) || "G005".equals(uca.getBrandCode()) 
			|| "G009".equals(uca.getBrandCode()) || "G014".equals(uca.getBrandCode()))
		{
			rtData.put("RESULT_CODE", "2998");
			rtData.put("RESULT_INFO", "该号码不满足参与条件，建议更换其他号码！");
			return rtData;
		}*/
		
		if (mainSn.startsWith("0898"))
        {
			rtData.put("RESULT_CODE", "2998");
			rtData.put("RESULT_INFO", "0898用户不允许办理！");
			return rtData;
        }
		
		/*IDataset snRelationSet = RelaUUInfoQry.queryRelaUUBySnb(mainSn,"56");
		if (IDataUtil.isNotEmpty(snRelationSet)) 
		{
			rtData.put("RESULT_CODE", "2998");
			rtData.put("RESULT_INFO", "该号码存在统一付费关系，不允许办理！");
			return rtData;
		}*/
        
        IDataset mainRelationSet = RelaUUInfoQry.queryRelaUUBySnb(mainSn,"61");
    	if (IDataUtil.isNotEmpty(mainRelationSet)) {
    		String rolecodeb = mainRelationSet.getData(0).getString("ROLE_CODE_B");
    		if ("2".equals(rolecodeb)) 
    		{
    			IDataset memberList = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("61", mainRelationSet.getData(0).getString("USER_ID_B"), "2");
	    		rtData.put("SNB_LIST", memberList);
	    		
	    		System.out.println("memberList>>>>>cnc=="+memberList);
			}else 
			{
				String userIdA = mainRelationSet.getData(0).getString("USER_ID_A");
	    		IDataset snData = RelaUUInfoQry.qryRoleCodeB(userIdA);
	    		rtData.put("SNB_LIST", snData);
			}
    		
		}
    	System.out.println("mainRelationSet>>>>>cnc=="+mainRelationSet);
    	System.out.println("rtData>>>>>cnc=="+rtData);
    	
        return rtData;
    }
	
}
