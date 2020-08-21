
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class BatChgWLWMemDiscntTrans implements ITrans
{
  
    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 校验请求参数
        checkRequestDataSub(batData);
        // 构建服务请求数据
        builderSvcData(batData);
        // 构建套餐数据
        this.buildChgDiscountData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
    	
        IData condData = batData.getData("condData", new DataMap());
        
        IDataUtil.chkParam(condData, "PRODUCT_ID");//集团产品 PRODUCT_ID
        IDataUtil.chkParam(condData, "USER_ID");  //集团产品的USER_ID
        IDataUtil.chkParam(batData, "SERIAL_NUMBER");	 //成员号码
        IDataUtil.chkParam(condData, "GRP_SN");	 //集团产品编码
        IDataUtil.chkParam(condData, "GROUP_ID");	 //集团客户GroupID
        
        batData.put("NEED_RULE", true);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());
        IData condData = batData.getData("condData", new DataMap());
        
        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("MEM_ROLE_B", condData.getString("MEM_ROLE_B", "1"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        //svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("ELEMENT_INFO", "[]")));
        svcData.put("RES_INFO", new DatasetList("[]"));
        svcData.put("PRODUCT_PARAM_INFO", new DatasetList("[]"));
        svcData.put("REMARK", condData.getString("REMARK", condData.getString("PRODUCT_ID") + "批量修改成员套餐"));

        // 业务是否预约 true 预约 false 非预约工单
        svcData.put("IF_BOOKING", svcData.getString("IF_BOOKING", "false"));
        svcData.put("NEED_RULE", true);
    }
    
    public void buildChgDiscountData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());
        IData condData = batData.getData("condData", new DataMap());

        String userIdA = condData.getString("USER_ID"); // 集团产品用户user_id
        String groupId = IDataUtil.chkParam(condData, "GROUP_ID");// 集团id
        String productId = IDataUtil.chkParam(condData, "PRODUCT_ID");
        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");//成员号码
        String grpSn = IDataUtil.chkParam(condData, "GRP_SN");//集团产品编码
        
        //获取成员产品信息
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(productId);
        if (StringUtils.isBlank(baseMemProduct.trim()))
        {
            CSAppException.apperr(GrpException.CRM_GRP_185);
        }
        
        //查询集团是否订购对应的集团产品
        if (StringUtils.isBlank(userIdA.trim()))
        {
            IDataset userinfos = UserInfoQry.qryUserByGroupIdAndProductIdForGrp(groupId, productId);
            if (IDataUtil.isEmpty(userinfos))
            {
                CSAppException.apperr(GrpException.CRM_GRP_617, groupId, productId); // 集团未订购
            }
            userIdA = userinfos.getData(0).getString("USER_ID");
        }
        
        IData userMainProd = UcaInfoQry.qryUserMainProdInfoByUserId(userIdA);
        if (IDataUtil.isEmpty(userMainProd))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_45, userIdA);
        }
        String mainProductId = userMainProd.getString("PRODUCT_ID","");
        
       /* 
        * 注释掉拦截 2019-3-29 17:38:09
        * if(!StringUtils.equals("20005013", mainProductId))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该集团产品不是机器卡产品!");
        }*/
        
        //查询成员用户信息
        IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        String userIdB = userInfo.getString("USER_ID",""); //成员的userId
        
        IData relaInfos = RelaUUInfoQry.getRelationUUByPk(userIdA,userIdB, "9A", null);
        
        if(IDataUtil.isEmpty(relaInfos))
        {
        	String errInfo = serialNumber + "该号码" + serialNumber + "不是集团产品" + grpSn + "的成员!";
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, errInfo);
        }

        //要把删除、修改的服务、优惠重新按照成员的userIdB捞取出来
        DatasetList elementInfos = new DatasetList(condData.getString("ELEMENT_INFO", "[]"));
        if(IDataUtil.isNotEmpty(elementInfos)){
            int size = elementInfos.size();
            for (int i = 0; i < size; i++)
            {
                IData elements = elementInfos.getData(i);
                String eleTypeCode = elements.getString("ELEMENT_TYPE_CODE","");
                String modifyTag = elements.getString("MODIFY_TAG","");
                String elementId = elements.getString("ELEMENT_ID","");
                
                if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode) &&
                        BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                {
                	IDataset userDiscnts = UserDiscntInfoQry.getUserDiscntByDiscntCode(userIdB, userIdA, 
                			elementId, Route.CONN_CRM_CG);
                	if (IDataUtil.isEmpty(userDiscnts))
                    {
                		CSAppException.apperr(ElementException.CRM_ELEMENT_222);
                    }
                	IData element = userDiscnts.getData(0);
                	elements.put("INST_ID", element.getString("INST_ID",""));
                }
                else if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode) &&
                        BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                {
                	IDataset userSvcs = UserSvcInfoQry.getSvcUserId(userIdB, userIdA, elementId);
                	if (IDataUtil.isEmpty(userSvcs))
                    {
                		CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户不存在该项服务!");
                    }
                	IData element = userSvcs.getData(0);
                	elements.put("INST_ID", element.getString("INST_ID",""));
                }
                else if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode) &&
                        BofConst.MODIFY_TAG_UPD.equals(modifyTag))
                {
                	IDataset userDiscnts = UserDiscntInfoQry.getUserDiscntByDiscntCode(userIdB, userIdA, 
                			elementId, Route.CONN_CRM_CG);
                	if (IDataUtil.isEmpty(userDiscnts))
                    {
                		CSAppException.apperr(ElementException.CRM_ELEMENT_222);
                    }
                	IData element = userDiscnts.getData(0);
                	elements.put("INST_ID", element.getString("INST_ID",""));
                }
                else if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode) &&
                        BofConst.MODIFY_TAG_UPD.equals(modifyTag))
                {
                	IDataset userSvcs = UserSvcInfoQry.getSvcUserId(userIdB, userIdA, elementId);
                	if (IDataUtil.isEmpty(userSvcs))
                    {
                		CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户不存在该项服务!");
                    }
                	IData element = userSvcs.getData(0);
                	elements.put("INST_ID", element.getString("INST_ID",""));
                }
            }
        }
        
        if("20171214".equals(productId))//NB-IOT产品
        {
        	if(IDataUtil.isNotEmpty(elementInfos))
        	{
        		//svcData.put("RSRV_STR5", "IS_SCHEDULE_TAG");
        		
        		IDataset allDiscntInfos = UserDiscntInfoQry.getAllUserDiscntByUserIdUserIdA(userIdB, userIdA);
                int size = elementInfos.size();
                for (int i = 0; i < size; i++)
                {
                	 IData elements = elementInfos.getData(i);
                     String eleTypeCode = elements.getString("ELEMENT_TYPE_CODE","");
                     String modifyTag = elements.getString("MODIFY_TAG","");
                     String elementId = elements.getString("ELEMENT_ID","");
                     String packageId = elements.getString("PACKAGE_ID","");
                     if(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(eleTypeCode) &&
                             BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                     {
                    	 IData afterData = calDiscnt(elementId,packageId,allDiscntInfos);
                    	 if(IDataUtil.isNotEmpty(afterData))//
                    	 {
                    		 String comTag = afterData.getString("COMPARE_TAG","");
                    		 if("0".equals(comTag))
                    		 {
                    			 //通过旧优惠的结束时间来计算新优惠的开始时间
                    			 String userEndDate = afterData.getString("END_DATE","");
                    			 processValidDate(elements,userEndDate);
                    			 
                    			 String newStartDate = elements.getString("START_DATE","");
                    			 String newEndDate = elements.getString("END_DATE","");
                    			 //查找对应优惠对应的服务,并以优惠的开始、结束时间来替换对应的服务开始、结束时间
                    			 if("41003605".equals(packageId))//NB-IOT流量产品包
                            	 {
                    				 procssNewSvcDate(elementInfos,newStartDate,newEndDate,"1218301","NB-IOT数据通信服务");
                            	 }
                    			 else if("41003606".equals(packageId))//NB-IOT短信产品包
                    			 {
                    				 procssNewSvcDate(elementInfos,newStartDate,newEndDate,"1218302","NB-IOT短信基础通信服务");
                    			 }
                    			 
                    		 }
                    		 else if("1".equals(comTag))
                    		 {
                    			//通过旧优惠的结束时间来计算新优惠的开始时间
                    			 String userEndDate = afterData.getString("END_DATE","");
                    			 processValidDate(elements,userEndDate);
                    			 
                    			 String newStartDate = elements.getString("START_DATE","");
                    			 String newEndDate = elements.getString("END_DATE","");
                    			 //查找对应优惠对应的服务,并以优惠的开始、结束时间来替换对应的服务开始、结束时间
                    			 if("41003605".equals(packageId))//NB-IOT流量产品包
                            	 {
                    				 procssNewSvcDate(elementInfos,newStartDate,newEndDate,"1218301","NB-IOT数据通信服务");
                            	 }
                    			 else if("41003606".equals(packageId))//NB-IOT短信产品包
                    			 {
                    				 procssNewSvcDate(elementInfos,newStartDate,newEndDate,"1218302","NB-IOT短信基础通信服务");
                    			 }
                    		 }
                    	 }
                     }
                }
        	}
        }
        
        svcData.put("ELEMENT_INFO", elementInfos);
        
    }
    
    /**
     * 用户的优惠信息
     * @param elementId
     * @param packageId
     * @param userDiscntInfos
     * @return
     */
    private IData calDiscnt(String elementId,String packageId,IDataset userDiscntInfos)
    {
    	IData discntMap = new DataMap();
    	if(IDataUtil.isNotEmpty(userDiscntInfos))
    	{
    		int size = userDiscntInfos.size();
            for (int i = 0; i < size; i++)
            {
            	 IData elements = userDiscntInfos.getData(i);
                 String userPackageId = elements.getString("PACKAGE_ID","");
                 String discntCode = elements.getString("DISCNT_CODE","");
                 //同一个包,同一个元素时
                 if(elementId.equals(discntCode) && packageId.equals(userPackageId))
                 {
                	 discntMap.put("PACKAGE_ID", userPackageId);
                	 discntMap.put("DISCNT_CODE", discntCode);
                	 discntMap.put("START_DATE", elements.getString("START_DATE",""));
                	 discntMap.put("END_DATE", elements.getString("END_DATE",""));
                	 discntMap.put("COMPARE_TAG", "0");
                	 break;
                 }
                 //同一个包,不同元素
                 else if(!elementId.equals(discntCode) && packageId.equals(userPackageId))
                 {
                	 discntMap.put("PACKAGE_ID", userPackageId);
                	 discntMap.put("DISCNT_CODE", discntCode);
                	 discntMap.put("START_DATE", elements.getString("START_DATE",""));
                	 discntMap.put("END_DATE", elements.getString("END_DATE",""));
                	 discntMap.put("COMPARE_TAG", "1");
                	 break;
                 }
            }
    	}
    	
    	return discntMap;
    }

    
    private static void processValidDate(IData discnt,String endDate) throws Exception 
	{
        if (IDataUtil.isNotEmpty(discnt))
        {
        	IData elementdata = ProductInfoQry.getProductElementByPkForCG(discnt.getString("PACKAGE_ID"), "D",discnt.getString("ELEMENT_ID"));
            String endoffset = "";
            String end_enable_tag = "";
            String end_unit = "";
            if(IDataUtil.isNotEmpty(elementdata))
            {
                endoffset = elementdata.getString("END_OFFSET");
                end_enable_tag = elementdata.getString("END_ENABLE_TAG");
                end_unit = elementdata.getString("END_UNIT");
            }
            if(!"".equals(endoffset) && !"".equals(end_unit) && ("1".equals(end_enable_tag) || "0".equals(end_enable_tag)))
            {
                String startdate = SysDateMgr.addSecond(endDate, 1);
                discnt.put("START_DATE", startdate);//开始时间为测试期的结束时间+2s
                String enddate = SysDateMgr.endDateOffset(startdate, endoffset, end_unit);
                discnt.put("END_DATE", enddate);
            }
        }
	}
    
    /**
     * 
     * @param svcInfos
     * @param startDate
     * @param endDate
     * @param svcId
     * @throws Exception
     */
    private static void procssNewSvcDate(IDataset svcInfos,
    		String startDate,String endDate,String svcId,String name) throws Exception
    {
    	boolean tag = false;
    	if(IDataUtil.isNotEmpty(svcInfos))
    	{
    		int size = svcInfos.size();
            for (int i = 0; i < size; i++)
            {
            	IData elements = svcInfos.getData(i);
                String eleTypeCode = elements.getString("ELEMENT_TYPE_CODE","");
                String modifyTag = elements.getString("MODIFY_TAG","");
                String elementId = elements.getString("ELEMENT_ID","");
                if(BofConst.ELEMENT_TYPE_CODE_SVC.equals(eleTypeCode) &&
                        BofConst.MODIFY_TAG_ADD.equals(modifyTag) &&
                        svcId.equals(elementId))
                {
                	elements.put("START_DATE", startDate);
                	elements.put("END_DATE", endDate);
                	tag = true;
                	break;
                }
            }
    	}
    	
    	//if(!tag)
    	//{
    	//	CSAppException.apperr(CrmCommException.CRM_COMM_103, "请选择" + name + "[" +svcId + "]!");
    	//}
    }
    
}
