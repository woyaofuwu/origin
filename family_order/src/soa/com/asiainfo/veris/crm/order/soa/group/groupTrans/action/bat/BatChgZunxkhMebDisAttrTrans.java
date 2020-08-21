
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BatChgZunxkhMebDisAttrTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
    	// 校验请求参数
        //checkRequestDataSub(batData);
        
    	IData svcData = batData.getData("svcData", new DataMap());
        IData condData = batData.getData("condData", new DataMap());
        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");
        String discntCode = IDataUtil.chkParam(condData, "DISCNT_CODE");
        
        //18529 功能费
        String operFee = IDataUtil.chkParam(condData, "OPER_FEE");
        //18530 折扣
        String operDiscount = IDataUtil.chkParam(condData, "OPER_DISCOUNT");
        
        // 查询用户信息
        IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
        
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }
        String mebUserId = userInfo.getString("USER_ID");
        //String eparchyCode = userInfo.getString("EPARCHY_CODE","");

        IDataset uuRelationInfo = RelaUUInfoQry.qryUU("", mebUserId, "UZ", null, null);
        if (IDataUtil.isEmpty(uuRelationInfo))
        {
        	String errMes = "该服务号码" + serialNumber + "不属于任何尊享客户集团产品成员!";
            CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        }
        
        String userIdA = uuRelationInfo.getData(0).getString("USER_ID_A","");
        IData grpUserinfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);
        if (IDataUtil.isEmpty(grpUserinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_197, userIdA);
        }
        
        //IDataset discntInfos = UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(mebUserId, discntCode, eparchyCode);
        IDataset discntInfos = UserDiscntInfoQry.queryVpmnUserDiscntByUserId(mebUserId, discntCode);
        if (IDataUtil.isEmpty(discntInfos))
        {
        	String errMes = "该用户未订购优惠" + discntCode + "!";
            CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        }
        
        IData discntInfo = discntInfos.getData(0);
        
        String relaInstId = discntInfo.getString("INST_ID","");
        if(StringUtils.isNotBlank(relaInstId))
        {
        	//校验对应属性,以免一直做属性变更导致数据问题, 
        	IDataset attrInfos = UserAttrInfoQry.queryUserAttrByUserIdReInstId(mebUserId,relaInstId,"D","18529");
        	if(IDataUtil.isNotEmpty(attrInfos) && attrInfos.size() >=2 )
        	{
        		String errMes = "该用户的优惠" + discntCode + "的属性attr_code=18529有两条数据,不允许再变更,请下个月再做变更!";
                CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        	}
        	//校验对应属性,以免一直做属性变更导致数据问题,
        	IDataset attrInfos2 = UserAttrInfoQry.queryUserAttrByUserIdReInstId(mebUserId,relaInstId,"D","18530");
        	if(IDataUtil.isNotEmpty(attrInfos2) && attrInfos2.size() >=2 )
        	{
        		String errMes = "该用户的优惠" + discntCode + "的属性attr_code=18530有两条数据,不允许再变更,请下个月再做变更!";
                CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        	}
        }       
                
        discntInfo.put("ELEMENT_ID", discntCode);
        discntInfo.put("ELEMENT_TYPE_CODE", "D");
        discntInfo.put("MODIFY_TAG", "2");
    	
        IDataset attrParamList = new DatasetList();
        IData attrParam1 = new DataMap();
        attrParam1.put("ATTR_CODE", "18529");
        attrParam1.put("ATTR_VALUE", operFee);
        attrParamList.add(attrParam1);
        
        IData attrParam2 = new DataMap();
        attrParam2.put("ATTR_CODE", "18530");
        attrParam2.put("ATTR_VALUE", operDiscount);
        attrParamList.add(attrParam2);
        
        discntInfo.put("ATTR_PARAM", attrParamList);
        
        //DatasetList elementList = new DatasetList(condData.getString("ELEMENT_INFO", "[]"));
        
        IDataset elementList = new DatasetList();
        elementList.add(discntInfo);
        
        batData.put("NEED_RULE", true);
                
        // 处理服务服务参数
        svcData.put("IF_CENTRETYPE", condData.getString("IF_CENTRETYPE", ""));
        svcData.put("ELEMENT_INFO", elementList);
        svcData.put("PRODUCT_PARAM_INFO", batData.getDataset("PRODUCT_PARAM_INFO"));
        svcData.put("USER_ID", userIdA);
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put("PRODUCT_ID", grpUserinfo.getString("PRODUCT_ID"));
        svcData.put("MEM_ROLE_B", uuRelationInfo.getData(0).getString("ROLE_CODE_B"));
        svcData.put("NEED_RULE", true);
    }

}
