package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatchTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class GrpGfffMemSuperpositionIntf
{
    protected static final Logger log = Logger.getLogger(GrpGfffMemSuperpositionIntf.class);
    
    public static IDataset batGrpOrderGfffMemSuperposition(IData inparam) throws Exception
    {
        IDataset result = new DatasetList();
        
        IDataUtil.chkParam(inparam, "GROUP_ID");
        IDataUtil.chkParam(inparam, "OPER_CTRL_TYPE");
        IDataUtil.chkParam(inparam, "PACKAGE_ID");
        IDataUtil.chkParam(inparam, "ELEMENT_ID");
        IDataUtil.chkParam(inparam, "SERIAL_NUMBER");
        String groupId = inparam.getString("GROUP_ID");
        String operType = inparam.getString("OPER_CTRL_TYPE");
        String tradeStaffId = CSBizBean.getVisit().getStaffId();//IDataUtil.getMandaData(inparam, "TRADE_STAFF_ID");	//受理工号
        
        if(!("0".equals(operType) || "1".equals(operType) || "2".equals(operType)))
        {
            IData info = new DataMap();
            info.put("X_RESULTINFO", "OPER_CTRL_TYPE 参数值错误，只能为0或2，0:限量统付叠加包，2：定额统付叠加包");
            info.put("X_RESULTCODE", "50808001");
            result.add(info);
            return result;
        }
        
        // 查询批量类型信息
        String batchOperType = "";
        if("0".equals(operType))
        {
            batchOperType = "DJBGFFFLIMITAIONMEM";
        }
        else if("2".equals(operType))
        {
            batchOperType = "DJBGFFFDINGEMEMBER";
        }
        
        IDataset batchTypeList = BatchTypeInfoQry.qryBatchTypeByOperType(batchOperType);

        if (IDataUtil.isEmpty(batchTypeList))
        {
            CSAppException.apperr(BatException.CRM_BAT_37);
        }
        
        int maxLimitNum = 500;
        
        IData batchTypeParam = BatchTypeInfoQry.queryBatchTypeParamsEx(batchOperType,"0898");
        
        if(IDataUtil.isNotEmpty(batchTypeParam))
        {
            int limitNum = batchTypeParam.getInt("LIMIT_NUM_BATCH", 500);
            if(limitNum > 0)
                maxLimitNum = limitNum;
        }

        //校验号码
        String [] serialnumbers = inparam.getString("SERIAL_NUMBER").split(",");
        if(serialnumbers.length > maxLimitNum)
        {
            IData info = new DataMap();
            info.put("X_RESULTINFO", "SERIAL_NUMBER 成员号码单次最大支持" + maxLimitNum + "个");
            info.put("X_RESULTCODE", "50808002");
            result.add(info);
            return result;
        }
        
        String grpCustId = "",grpUserId = "" ,grpProductId = "",grpSerialNumber = "",grpEparchyCode = "";
        IDataset grpUserList = new DatasetList() ;
        IDataset groupInfos = CSAppCall.call("CS.UcaInfoQrySVC.qryGrpInfoByGrpId", inparam);
        if(groupInfos != null && groupInfos.size() >0)
        {
            grpCustId = groupInfos.getData(0).getString("CUST_ID");
            //根据集团客户信息查询集团已订购产品用户信息
            IData param = new DataMap();
            param.put("CUST_ID", grpCustId);
            grpUserList = CSAppCall.call("CS.UserInfoQrySVC.getUserInfoByCstIdForGrp", param);
        }
        else
        {
            IData info = new DataMap();
            info.put("X_RESULTINFO", "根据集团ID["+ inparam.getString("GROUP_ID") +"]查询集团信息不存在");
            info.put("X_RESULTCODE", "50808003");
            result.add(info);
            return result;
        }
        
        if("0".equals(operType))
        {
            grpProductId = "7344";
        }
        else if("1".equals(operType))
        {
            grpProductId = "7342";
        }
        else if("2".equals(operType))
        {
            grpProductId = "7343";
        }
        
        //判断集团是否订购了流量自由充产品
        boolean isOrderProduct = false ;
        for(int i = 0 ; i < grpUserList.size() ; i++)
        {
            IData user = grpUserList.getData(i);
            String productId = user.getString("PRODUCT_ID","");
            if(grpProductId.equals(productId))
            {
                isOrderProduct = true;
                grpUserId = user.getString("USER_ID");
                grpSerialNumber = user.getString("SERIAL_NUMBER");
                grpEparchyCode = user.getString("EPARCHY_CODE");
                break;
            }
        }
        
        if(!isOrderProduct)
        {
            IData info = new DataMap();
            String productName = UProductInfoQry.getProductNameByProductId(grpProductId);
            info.put("X_RESULTINFO", "该集团未订购" + productName + "产品，不能办理成员叠加包订购");
            info.put("X_RESULTCODE", "50808004");
            result.add(info);
            return result;
        }
        
        //add by chenzg@20170911 REQ201709050001关于流量自由充流量池的优化需求 校验受理工号和产品编码的对应关系
        checkStaffIdBindGrpSn(tradeStaffId, grpSerialNumber);
        
        //判断集团是否订购了叠加包
        /*IData pakParam = new DataMap();
        pakParam.put("GRP_USER_ID", grpUserId);
        pakParam.put("PRODUCT_ID", grpProductId);
        pakParam.put("USER_EPARCHY_CODE", grpEparchyCode);*/
        IDataset grpOrderPackages = UserGrpPkgInfoQry.getUserGrpPackageIdForGrp(grpUserId);
        if(IDataUtil.isEmpty(grpOrderPackages))
        {
            IData info = new DataMap();
            info.put("X_RESULTINFO", "该集团暂未订购叠加包，不能为成员办理叠加包订购");
            info.put("X_RESULTCODE", "50808005");
            result.add(info);
            return result;
        }
        else
        {
            if(grpProductId.equals("7343"))
            {
                boolean isExistsDjb = false;
                for(int i = 0 ; i < grpOrderPackages.size() ; i++)
                {
                    String packageId = grpOrderPackages.getData(i).getString("PACKAGE_ID");
                    if("73430003".equals(packageId))
                    {
                        isExistsDjb = true;
                        continue;
                    }
                    else if("73430002".equals(packageId))
                    {
                        grpOrderPackages.remove(i);
                        i--;
                    }
                }
                
                if(!isExistsDjb)
                {
                    IData info = new DataMap();
                    info.put("X_RESULTINFO", "该集团暂未订购叠加包，不能为成员办理叠加包订购");
                    info.put("X_RESULTCODE", "50808005");
                    result.add(info);
                    return result;
                }
            }
        }
        
        String strPackageId = inparam.getString("PACKAGE_ID");
        boolean isGrpOrderPackageId = false ;
        for(int i = 0 ; i < grpOrderPackages.size(); i++ )
        {
            IData packages = grpOrderPackages.getData(i);
            String packageId = packages.getString("PACKAGE_ID");
            if(strPackageId.equals(packageId))
            {
                isGrpOrderPackageId = true;
                break;
            }
        }
        //判断传入的包ID是否集团已订购，如传入的包ID集团未订购，则判断集团订购的包有几个，只有一个就取默认，有多个否则提示包传入错误
        if(!isGrpOrderPackageId)
        {
            if(grpOrderPackages.size() == 1)
            {
                strPackageId = grpOrderPackages.getData(0).getString("PACKAGE_ID");
            }
            else
            {
                IData info = new DataMap();
                info.put("X_RESULTINFO", "该集团订购的叠加包中未找到【PACKAGE_ID=" + strPackageId + "】,请确认是否传入的PACKAGE_ID是否正确");
                info.put("X_RESULTCODE", "50808006");
                result.add(info);
                return result;
            }
        }
        
        //查询集团已订购的叠加优惠
        /*IData elemParam = new DataMap();
        elemParam.put("GRP_USER_ID", grpUserId);
        elemParam.put("PRODUCT_ID", grpProductId);
        elemParam.put("PACKAGE_ID", strPackageId);
        elemParam.put("PRIV_FOR_PACKAGE", inparam.getString("PRIV_FOR_PACKAGE","true"));*/
        IDataset grpOrderElements = UserGrpPkgInfoQry.getUserGrpPkgInfoByPk(grpUserId, null, strPackageId, null, null);
        if(IDataUtil.isEmpty(grpOrderElements))
        {
            IData info = new DataMap();
            info.put("X_RESULTINFO", "该集团暂未订购叠加优惠或您无权限办理叠加优惠包");
            info.put("X_RESULTCODE", "50808007");
            result.add(info);
            return result;
        }
        
        String strElementId = inparam.getString("ELEMENT_ID","");
        String elemArray [] = strElementId.split("\\|");
        String noOrderElementIds = "";
        for(int i = 0 ; i < elemArray.length ; i++)
        {
            String elemId = elemArray[i];
            boolean isOrderFlag = false;
            for(int j = 0 ; j < grpOrderElements.size() ; j++)
            {
                IData orderElement = grpOrderElements.getData(j);
                String elementId = orderElement.getString("ELEMENT_ID");
                if(elemId.equals(elementId))
                {
                    isOrderFlag = true;
                    break;
                }
            }
            
            if(!isOrderFlag)
            {
                noOrderElementIds += elemId + ",";
            }
        }
        
        if(!"".equals(noOrderElementIds))
        {
            IData info = new DataMap();
            info.put("X_RESULTINFO", "该集团暂未订购叠加优惠[" + noOrderElementIds + "],请确认ELEMENT_ID是否正确!");
            info.put("X_RESULTCODE", "50808008");
            result.add(info);
            return result;
        }
        
        IDataset elementInfo = new DatasetList();
        for(int i = 0 ; i < elemArray.length ; i++ )
        {
            String tempElementId = elemArray[i];
            IData element = new DataMap();
            element.put("ELEMENT_ID", tempElementId);
            element.put("PACKAGE_ID", strPackageId);
            element.put("PRODUCT_ID", grpProductId + "01");
            element.put("MODIFY_TAG", "0");
            element.put("ELEMENT_TYPE_CODE", "D");
            element.put("START_DATE", SysDateMgr.getSysTime());
            element.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            element.put("INST_ID", "");
            elementInfo.add(element);
        }
        
        //校验成员号码,号码正常未销户，号码已订购了集团产品
        IDataset okUsers = new DatasetList(),failUsers = new DatasetList();
        for(int i = 0 ; i < serialnumbers.length ; i++)
        {
            String sn = serialnumbers[i];
            
            //校验号码是否存在
            IData checkUser = UserInfoQry.checkMebUserInfoBySn(sn);
           
            if(!"0".equals(checkUser.getString("USER_RESULT_CODE","")))
            {
                IData errorUser = new DataMap();
                errorUser.put("SERIAL_NUMBER", sn);
                errorUser.put("ERROR_INFO", "该号码非正常用户");
                failUsers.add(errorUser);
                continue;
            }
          
            //号码已订购集团产品
            String userId = checkUser.getData("USER_INFO").getString("USER_ID");
            IData userParam = new DataMap();
            userParam.put("CUST_ID", grpCustId);
            userParam.put("USER_ID_A", grpUserId);
            userParam.put("USER_ID_B", userId);
            userParam.put(Route.ROUTE_EPARCHY_CODE, checkUser.getData("USER_INFO").getString("EPARCHY_CODE"));
            IDataset userRelation = CSAppCall.call("CS.UserInfoQrySVC.getRelaUserInfoByCstId",userParam);

            if(IDataUtil.isEmpty(userRelation))
            {
                IData errorUser = new DataMap();
                errorUser.put("SERIAL_NUMBER", sn);
                errorUser.put("ERROR_INFO", "号码【" + sn + "】非该集团[" + inparam.getString("GROUP_ID") + "]产品成员号码");
                failUsers.add(errorUser);
                continue;
            }
            else
            {
                boolean isInGrp = false ;
                for(int k = 0 ; k < userRelation.size() ; k++ )
                {
                    IData relation = userRelation.getData(k);
                    String userIdA = relation.getString("USER_ID_A");
                    if(grpUserId.equals(userIdA))
                    {
                        isInGrp = true;
                        break;
                    }
                }
                
                if(!isInGrp)
                {
                    IData errorUser = new DataMap();
                    errorUser.put("SERIAL_NUMBER", sn);
                    errorUser.put("ERROR_INFO", "号码【" + sn + "】非该集团[" + inparam.getString("GROUP_ID") + "]流量自由充产品成员号码");
                    failUsers.add(errorUser);
                    continue;
                }
            }
            //校验重复号码
            boolean isDuplicateSn = false;
            for(int m = 0 ; m < okUsers.size(); m++)
            {
                if(sn.equals(okUsers.getData(m).getString("SERIAL_NUMBER","")))
                {
                    isDuplicateSn = true;
                }
            }
            
            if(isDuplicateSn)
            {
                IData errorUser = new DataMap();
                errorUser.put("SERIAL_NUMBER", sn);
                errorUser.put("ERROR_INFO", "重复号码，该号码在列表中已存在");
                failUsers.add(errorUser);
                continue;
            }
            
            IData normalUser = new DataMap();
            normalUser.put("SERIAL_NUMBER", sn);
            okUsers.add(normalUser);
        }
        
        if(IDataUtil.isEmpty(okUsers))
        {
            IData info = new DataMap();
            info.put("X_RESULTINFO", "导入号码都不符合条件,请检查");
            info.put("X_RESULTCODE", "50808009");
            info.put("FAIL_COUNT", failUsers.size());
            if(failUsers != null && failUsers.size() >0)
            {
                info.put("FAIL_LIST", failUsers);
            }
            result.add(info);
            return result;
        }
        
       
        
        
        
      //创建批量任务,创建 TF_B_TRADE_BAT_TASK
        IData batTaskData = new DataMap();

        String batTaskName = inparam.getString("BAT_TASK_NAME","");
        if("".equals(batTaskName))
            batTaskName = "网厅流量自由充成员叠加包批量办理(" + ("0".equals(operType) ? "限量统付" :( "1".equals(operType) ? "全量统付" : "定额统付"))  + ")";
        
        batTaskData.put("BATCH_OPER_TYPE", batchOperType);
        batTaskData.put("BATCH_OPER_CODE", batchOperType);
        batTaskData.put("BATCH_OPER_NAME", batchTypeList.getData(0).getString("BATCH_OPER_NAME"));
        batTaskData.put("BATCH_TASK_NAME", batTaskName);
        batTaskData.put("REMARK", batTaskName);
        batTaskData.put("START_DATE", SysDateMgr.getSysTime());
        batTaskData.put("END_DATE", SysDateMgr.getLastDateThisMonth().substring(0, 10));
        
        String codingStr = "{" + "\"GROUP_ID\":\"" + groupId + "\",\"USER_EPARCHY_CODE\":\"" + grpEparchyCode + "\",\"PRODUCT_ID\":\"" + grpProductId + "\"" 
           + ",\"USER_ID\":\"" + grpUserId + "\",\"OPER_TYPE\":\"0\",\"CUST_ID\":\"" + grpCustId + "\",\"GRP_SN\":\"" + grpSerialNumber +"\"" 
           + ",\"ELEMENT_INFO\":" + elementInfo.toString()  + "}";
        batTaskData.put("CODEINGSTR", codingStr);
        IDataset bastTasks = CSAppCall.call("CS.BatDealSVC.submitBatTask", batTaskData);
        
        String batTaskId = bastTasks.getData(0).getString("BATCH_TASK_ID");
        
        //导入批量数据，创建TF_B_TRADE_BAT,TF_B_TRADE_BATDEAL
        IData batData = new DataMap();
        IData inParam = new DataMap();

        inParam.put("BATCH_OPER_TYPE", batchOperType);
        inParam.put("BATCH_TASK_ID", batTaskId);
        inParam.put("BATCH_TASK_NAME", batTaskName);
        inParam.put("AUDIT_STATE", "0");
        inParam.put("ACTIVE_FLAG", "0"); //0:未激活，1：已激活
        inParam.put("DEAL_STATE", "0"); //0: 未启动，1：等待预处理
        
        batData.put("IN_PARAM", inParam);
        batData.put("DATA_SET", okUsers);
        batData.put("USER_EPARCHY_CODE", grpEparchyCode);
 
        CSAppCall.call("CS.BatDealSVC.importData", batData);
        
        IData qryParam = new DataMap();
        qryParam.put("BATCH_TASK_ID", batTaskId);
        
        IDataset taskDetials = CSAppCall.call("CS.BatDealSVC.taskDetialQuery", qryParam);
        
        String batchId = "";
        if(IDataUtil.isNotEmpty(taskDetials))
        {
            batchId = taskDetials.getData(0).getString("BATCH_ID");
            //启动批量任务
            qryParam.put("BATCH_ID", batchId);
            qryParam.put("BATCH_OPER_TYPE", batchOperType);
            CSAppCall.call("CS.BatDealSVC.batTaskNowRun", qryParam);
        }
        
        IData returnData = new DataMap();
        returnData.put("BATCH_ID", batchId);
        returnData.put("SUC_COUNT", okUsers.size());
        returnData.put("FAIL_COUNT", failUsers.size());
        if(failUsers != null && failUsers.size() >0)
        {
            returnData.put("FAIL_LIST", failUsers);
        }
        if(okUsers != null && okUsers.size() >0)
        {
            returnData.put("SUC_LIST", okUsers);
        }
        returnData.put("X_RESULTCODE", "0");
        returnData.put("X_RESULTINFO", "OK");
        
        result.add(returnData);
        return result;
    }
    
    /**
     * 校验业务受理工号和集团产品编码的绑定关系
     * @param info
     * @throws Exception
     * @Author:chenzg
     * @Date:2017-9-11
     */
    private static void checkStaffIdBindGrpSn(String tradeStaffId, String grpSn) throws Exception
    {
    	IData param = new DataMap();							
    	param.put("TRADE_STAFF_ID", tradeStaffId);				//业务受理工号	
    	param.put("SERIAL_NUMBER", grpSn);						//集团产品编码
    	IDataset rs = CSAppCall.call("SS.GfffUserMaxGprsSetSVC.qryGfffStaffIdBindInfosForChk", param);
    	if(IDataUtil.isEmpty(rs)){
    		CSAppException.apperr(GrpException.CRM_GRP_713, "根据业务受理工号["+tradeStaffId+"]和产品编码["+grpSn+"]查询不到绑定关系！");
    	}
    }
}
