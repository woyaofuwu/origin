
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;

public class BatChgDeskTopMebInfoTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());
        
        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");

        String operType = IDataUtil.chkParam(condData, "OPER_TYPE");
        
        //operType,0操作服务,1操作优惠
        if(!"0".equals(operType) && !"1".equals(operType))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "操作类型值不正确,请联系管理员核查!");
        }
        
        String svcCode = "";
        String svcOper = "";
        String discntCode = "";
        String discntOper = "";
        if("0".equals(operType))//处理服务
        {
        	svcCode = IDataUtil.chkParam(condData, "SVC_CODE");
        	svcOper = IDataUtil.chkParam(condData, "SVC_OPER");
        	
        	//0是新增服务,1是删除服务
        	if(!"0".equals(svcOper) && !"1".equals(svcOper))
        	{
        		CSAppException.apperr(GrpException.CRM_GRP_713, "服务操作方式不正确,只能是新增或删除!");
        	}
        }
        
        if("1".equals(operType))//处理优惠
        {
        	discntCode = IDataUtil.chkParam(condData, "DISCNT_CODE");
        	discntOper = IDataUtil.chkParam(condData, "DISCNT_OPER");
        	//0是新增优惠,1是删除优惠
        	if(!"0".equals(discntOper) && !"1".equals(discntOper))
        	{
        		CSAppException.apperr(GrpException.CRM_GRP_713, "优惠操作方式不正确,只能是新增或删除!");
        	}
        }
        
        // 查询用户信息
        IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
        //IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }
        String mebUserId = userInfo.getString("USER_ID");

        IDataset uuRelationInfo = RelaUUInfoQry.qryUU("", mebUserId, "S1", null, null);
        if (IDataUtil.isEmpty(uuRelationInfo))
        {
        	String errMes = "该服务号码" + serialNumber + "不属于任何多媒体桌面电话集团成员!";
            CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        }
        
        String userIdA = uuRelationInfo.getData(0).getString("USER_ID_A");
        IData grpUserinfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);
        if (IDataUtil.isEmpty(grpUserinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_197, userIdA);
        }
        
        
        IDataset elementList = new DatasetList();
        
        if("0".equals(operType))
        {
        	if("0".equals(svcOper))//新增服务
        	{
        		IDataset svcInfos = GrpUserPkgInfoQry.getGrpPkgSvcElementByUserId(userIdA,svcCode);
        		if(IDataUtil.isEmpty(svcInfos))
        		{
        			String errMes = "该集团用户" + userIdA + "未订购该服务" + svcCode + "!";
        			CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        		}
        		
        		IDataset svcUserInfos = UserSvcInfoQry.getSvcUserId(mebUserId,userIdA,svcCode);
        		if(IDataUtil.isNotEmpty(svcUserInfos))
        		{
        			String errMes = "该用户" + mebUserId + "已经订购该服务" + svcCode + ",不可再订购!";
        			CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        		}
        		
        		IData element = new DataMap();
        		element.put("ELEMENT_TYPE_CODE", "S");
        		element.put("MODIFY_TAG", "0");
        		element.put("PRODUCT_ID", "222201");
        		element.put("PACKAGE_ID", "10122801");
        		element.put("ELEMENT_ID", svcCode);
        		
        		ElementUtil.dealSelectedElementStartDateAndEndDate(element, "", true, "0898", null);
        		
        		addMemberElement(svcCode,"S",mebUserId,userIdA,
        				element.getString("START_DATE",""),element.getString("END_DATE",""),elementList);
        	}
        	else if("1".equals(svcOper))//删除服务
        	{
        		IDataset svcInfos = UserSvcInfoQry.getSvcUserId(mebUserId,userIdA,svcCode);
        		if(IDataUtil.isEmpty(svcInfos))
        		{
        			String errMes = "该用户" + mebUserId + "未订购该服务" + svcCode + "!";
        			CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        		}
        		IData svcInfo = svcInfos.getData(0);
        		IData element = new DataMap();
        		element.put("ELEMENT_TYPE_CODE", "S");
        		element.put("MODIFY_TAG", "1");
        		element.put("PRODUCT_ID", "222201");
        		element.put("PACKAGE_ID", "10122801");
        		element.put("ELEMENT_ID", svcCode);
        		
        		ElementUtil.dealSelectedElementStartDateAndEndDate(element, "", false, "0898", svcInfos);
        		
        		delMemberElement(svcInfo,svcCode,"S",element.getString("END_DATE",""),elementList);
        	}
        }
        
        if("1".equals(operType))
        {
        	if("0".equals(discntOper))//新增优惠
        	{
        		//IDataset discntUserInfos = UserDiscntInfoQry.getUserDiscntByDiscntCode(mebUserId, userIdA, 
        		//		discntCode, Route.CONN_CRM_CG);
        		IDataset discntUserInfos = UserDiscntInfoQry.getUserDiscntByUserIdBAndA(mebUserId, userIdA, 
        				discntCode, Route.CONN_CRM_CG);
            	if (IDataUtil.isNotEmpty(discntUserInfos))
                {
            		String errMes = "该用户" + mebUserId + "已经订购该优惠" + discntCode + ",不可再订购!";
            		CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
                }
            	
            	IDataset grpDiscntInfos = GrpUserPkgInfoQry.getGrpPkgDiscntElementByUserId(userIdA,discntCode);
        		if(IDataUtil.isEmpty(grpDiscntInfos))
        		{
        			String errMes = "该集团用户" + userIdA + "未订购该优惠" + discntCode + "!";
        			CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        		}
        		
        		//要订购的新优惠所在的包ID
        		IData pckInfos = grpDiscntInfos.getData(0);
        		String packageId = pckInfos.getString("PACKAGE_ID","");
        		boolean isSame = false;//订购的新优惠跟已经订购的优惠在同一个包
        		
        		IDataset oldDiscntList = new DatasetList();
        		IData oldDiscntData = new DataMap();
        		
        		//成员已经订购的优惠
        		IDataset grpMebDiscnts = GrpUserPkgInfoQry.getUserGrpPkgDiscntElementByUserId(mebUserId,userIdA);
        		
        		if(IDataUtil.isNotEmpty(grpMebDiscnts))
        		{
        			for (int i = 0, row = grpMebDiscnts.size(); i < row; i++)
                    {
        				IData discntData = grpMebDiscnts.getData(i);
        				String oldDiscntCode = discntData.getString("DISCNT_CODE","");
        				String oldPackageId = discntData.getString("PACKAGE_ID","");
        				if(StringUtils.isNotBlank(packageId) && StringUtils.isNotBlank(oldPackageId)
        						&& StringUtils.equals(packageId, oldPackageId))
        				{
        					//订购的新优惠跟已经订购的优惠在同一个包下
        					isSame = true;
        					oldDiscntData = discntData;
        					oldDiscntList.add(discntData);
        					break;
        				}
                    }
        		}
        		
        		if(isSame) //新增新的优惠,截止掉老的优惠
        		{
        			if(IDataUtil.isNotEmpty(oldDiscntData))
        			{
        				IData delElement = new DataMap();
        				delElement.put("ELEMENT_TYPE_CODE", "D");
        				delElement.put("MODIFY_TAG", "1");
        				delElement.put("PRODUCT_ID", "222201");
        				delElement.put("PACKAGE_ID", packageId);
        				delElement.put("ELEMENT_ID", oldDiscntData.getString("DISCNT_CODE",""));
        				
        				ElementUtil.dealSelectedElementStartDateAndEndDate(delElement, "", false, "0898", oldDiscntList);
                		
                		delMemberElement(oldDiscntData,oldDiscntData.getString("DISCNT_CODE",""),"D",
                				delElement.getString("END_DATE",""),elementList);
        			}
        			
        			//直接新增优惠
        			IData element = new DataMap();
            		element.put("ELEMENT_TYPE_CODE", "D");
            		element.put("MODIFY_TAG", "0");
            		element.put("PRODUCT_ID", "222201");
            		element.put("PACKAGE_ID", packageId);
            		element.put("ELEMENT_ID", discntCode);
            		
            		ElementUtil.dealSelectedElementStartDateAndEndDate(element, "", true, "0898", oldDiscntList);
            		
            		addMemberElement(discntCode,"D",mebUserId,userIdA,
            				element.getString("START_DATE",""),element.getString("END_DATE",""),elementList);
            		
        		}
        		else 
        		{
        			//直接新增优惠
        			IData element = new DataMap();
            		element.put("ELEMENT_TYPE_CODE", "D");
            		element.put("MODIFY_TAG", "0");
            		element.put("PRODUCT_ID", "222201");
            		element.put("PACKAGE_ID", packageId);
            		element.put("ELEMENT_ID", discntCode);
            		
            		ElementUtil.dealSelectedElementStartDateAndEndDate(element, "", false, "0898", null);
            		
            		//String startDateNextMonth = SysDateMgr.getFirstDayOfNextMonth();
            		//addMemberElement(discntCode,"D",mebUserId,userIdA,
            		//		startDateNextMonth,element.getString("END_DATE",""),elementList);
            		addMemberElement(discntCode,"D",mebUserId,userIdA,
            				element.getString("START_DATE",""),element.getString("END_DATE",""),elementList);
        		}
            	
        	}
        	else if("1".equals(discntOper))//删除优惠
        	{
        		
        		//IDataset discntInfos = UserDiscntInfoQry.getUserDiscntByDiscntCode(mebUserId, userIdA, 
        		//		discntCode, Route.CONN_CRM_CG);
        		IDataset discntInfos = UserDiscntInfoQry.getUserDiscntByUserIdBAndA(mebUserId, userIdA, 
        				discntCode, Route.CONN_CRM_CG);
            	if (IDataUtil.isEmpty(discntInfos))
                {
            		String errMes = "该用户" + mebUserId + "未订购该优惠" + discntCode + "!";
            		CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
                }
            	
            	String packageId = "";
            	IDataset grpPkgDiscnts = GrpUserPkgInfoQry.getGrpPkgDiscntElementByUserId(userIdA,discntCode);
            	if(IDataUtil.isNotEmpty(grpPkgDiscnts))
            	{
            		IData grpPkg = grpPkgDiscnts.getData(0);
            		String pkgId = grpPkg.getString("PACKAGE_ID","");
            		if(StringUtils.isNotBlank(pkgId))
            		{
            			packageId = pkgId;
            		}
            	}
            	
        		IData discntInfo = discntInfos.getData(0);
        		IData element = new DataMap();
        		element.put("ELEMENT_TYPE_CODE", "D");
        		element.put("MODIFY_TAG", "1");
        		element.put("PRODUCT_ID", "222201");
        		element.put("PACKAGE_ID", packageId);
        		element.put("ELEMENT_ID", discntCode);
        		
        		ElementUtil.dealSelectedElementStartDateAndEndDate(element, "", false, "0898", discntInfos);
        		
        		//String endDate = SysDateMgr.getSysTime();
        		//delMemberElement(discntInfo,discntCode,"D",endDate,elementList);
        		delMemberElement(discntInfo,discntCode,"D",element.getString("END_DATE",""),elementList);
        	}
        }
        
        // 处理服务服务参数
        svcData.put("IF_CENTRETYPE", condData.getString("IF_CENTRETYPE", ""));
        svcData.put("ELEMENT_INFO", elementList);
        svcData.put("PRODUCT_PARAM_INFO", batData.getDataset("PRODUCT_PARAM_INFO"));
        svcData.put("USER_ID", userIdA);
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put("PRODUCT_ID", grpUserinfo.getString("PRODUCT_ID"));
        svcData.put("MEM_ROLE_B", uuRelationInfo.getData(0).getString("ROLE_CODE_B"));
    }

    /**
     * 
     * @param elementId
     * @param typeCode
     * @param mebUserId
     * @param userIdA
     * @param startDate
     * @param endDate
     * @param elementList
     * @throws Exception
     */
    private void addMemberElement(String elementId,String typeCode,
    		String mebUserId,String userIdA,String startDate ,
    		String endDate, IDataset elementList) throws Exception
    {
    	IData data = new DataMap();
    	if(StringUtils.isBlank(startDate))
    	{
    		startDate = SysDateMgr.getSysTime();
    	}
    	if(StringUtils.isBlank(endDate))
    	{
    		endDate = SysDateMgr.getTheLastTime();
    	}
    	
    	data.put("ELEMENT_ID", elementId);
    	data.put("ELEMENT_TYPE_CODE", typeCode);
    	data.put("USER_ID", mebUserId);
    	data.put("USER_ID_A", userIdA);
    	//data.put("STATE", "ADD");
        data.put("MODIFY_TAG", "0");
        data.put("START_DATE", startDate);
        data.put("END_DATE", endDate);
        data.put("INST_ID", SeqMgr.getInstId());
        
        elementList.add(data);
    }
    
    /**
     * 
     * @param svcInfo
     * @param svcCode
     * @param typeCode
     * @param endDate
     * @param elementList
     * @throws Exception
     */
    private void delMemberElement(IData svcInfo,String svcCode,String typeCode,
    		String endDate,IDataset elementList) throws Exception
    {
    	if(StringUtils.isBlank(endDate))
    	{
    		endDate = SysDateMgr.getSysTime();
    	}
    	svcInfo.put("ELEMENT_ID", svcCode);
    	svcInfo.put("ELEMENT_TYPE_CODE", typeCode);
    	svcInfo.put("MODIFY_TAG", "1");
    	svcInfo.put("END_DATE", endDate);
    	
    	elementList.add(svcInfo);
    }

}
