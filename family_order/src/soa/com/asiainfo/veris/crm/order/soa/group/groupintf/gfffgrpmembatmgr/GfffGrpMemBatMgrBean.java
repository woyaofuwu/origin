
package com.asiainfo.veris.crm.order.soa.group.groupintf.gfffgrpmembatmgr;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class GfffGrpMemBatMgrBean extends CSBizBean
{

    protected static final Logger logger = Logger.getLogger(GfffGrpMemBatMgrBean.class);
    
    /**
     * 创建流量自由充(限量统付)产品成员新增的批量任务
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset createBatGfffLimitationMember(IData inParam) throws Exception
    {
        StringBuilder builder = new StringBuilder(50);
        IDataset relaList = new DatasetList();
        
        String serialNumber = inParam.getString("SERIAL_NUMBER");
        String grpSn = inParam.getString("SERIAL_NUMBER_A");
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<SERIAL_NUMBER的参数值>>>>>>>>>>>>>>>>>>>>" + serialNumber);
        }
        
        if(StringUtils.isNotBlank(serialNumber)){
            String[] strs = StringUtils.split(serialNumber, ",");
            for (int i = 0, size = strs.length; i < size; i++)
            {
                IData data = new DataMap();
                data.put("SERIAL_NUMBER", strs[i].trim());
                relaList.add(data);
            }
        }
        
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<打印relaList的参数值>>>>>>>>>>>>>>>>>>>>");
            if (IDataUtil.isNotEmpty(relaList)){
                logger.debug("<<<<<<<<<<<<<<<<<<<<relaList的参数值>>>>>>>>>>>>>>>>>>>>" + relaList);
            } else if(IDataUtil.isEmpty(relaList)){
                logger.debug("<<<<<<<<<<<<<<<<<<<<relaList的参数值为空>>>>>>>>>>>>>>>>>>>>"); 
            } 
        }
        
        String groupId = inParam.getString("GROUP_ID");
        String productId = inParam.getString("PRODUCT_ID");
        String userId = inParam.getString("USER_ID");
        String custId = inParam.getString("CUST_ID");
        String planType = inParam.getString("PLAN_TYPE");
        String memRoleB = inParam.getString("MEM_ROLE_B");
        String payLimitFee = inParam.getString("NOTIN_PAY_LIMIT_FEE");
        String payEnDate = inParam.getString("NOTIN_PAY_END_DATE");
        String inmodecode = inParam.getString("IN_MODE_CODE","0");
        String systime = SysDateMgr.getSysTime();
        
        // 创建集团成员的批量任务
        IData mebBatData = new DataMap();
        
        IData mebCondStrData = new DataMap();
        mebCondStrData.put("GROUP_ID", groupId);
        mebCondStrData.put("PRODUCT_ID", productId);
        mebCondStrData.put("USER_ID", userId);
        mebCondStrData.put("CUST_ID", custId);
        mebCondStrData.put("GRP_SN", grpSn);
        mebCondStrData.put("NOTIN_PAY_LIMIT_FEE", payLimitFee);
        mebCondStrData.put("NOTIN_PAY_END_DATE", payEnDate);
        mebCondStrData.put("NEED_RULE", true);
        mebCondStrData.put("PLAN_TYPE", planType);
        mebCondStrData.put("MEM_ROLE_B", memRoleB);
        mebCondStrData.put("ELEMENT_INFO", new DatasetList());
        
        //默认下发标准短信        
        String smsFlag = inParam.getString("SMS_FLAG");
        String sendForSms = inParam.getString("SEND_FOR_SMS");
        if(StringUtils.isNotBlank(smsFlag) && StringUtils.isNotBlank(sendForSms)
                && "1".equals(smsFlag) && "0".equals(sendForSms)){
            mebCondStrData.put("NOTIN_SMS_FLAG", smsFlag);
            mebCondStrData.put("NOTIN_sendForSms", sendForSms);
        }
        
        
        mebBatData.put("BATCH_OPER_TYPE", "ADDGFFFLIMITAIONMEM");
        mebBatData.put("BATCH_TASK_NAME", "流量自由充(限量统付)产品成员批量订购-接口");
        //mebBatData.put("SMS_FLAG", "0");
        mebBatData.put("CREATE_TIME", systime);
        mebBatData.put("ACTIVE_FLAG", "1");
        mebBatData.put("ACTIVE_TIME", systime);
        mebBatData.put("DEAL_TIME", systime);
        mebBatData.put("DEAL_STATE", "1");
        mebBatData.put("IN_MODE_CODE",inmodecode);
        mebBatData.put("CODING_STR", mebCondStrData.toString());

        String mebBatchId = "";
        mebBatchId = BatDealBean.createBat(mebBatData, relaList);
        builder.append(mebBatchId);
        
        IData retData = new DataMap();
        retData.put("BATCH_ID", builder.toString());

        return IDataUtil.idToIds(retData);
    }
    
    /**
     * 创建流量自由充(全量统付)产品成员新增的批量任务
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset createBatGfffQuanLiangMember(IData inParam) throws Exception
    {
        StringBuilder builder = new StringBuilder(50);
        IDataset relaList = new DatasetList();
        
        String serialNumber = inParam.getString("SERIAL_NUMBER");
        String grpSn = inParam.getString("SERIAL_NUMBER_A");
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<SERIAL_NUMBER的参数值>>>>>>>>>>>>>>>>>>>>" + serialNumber);
        }
        
        if(StringUtils.isNotBlank(serialNumber)){
            String[] strs = StringUtils.split(serialNumber, ",");
            for (int i = 0, size = strs.length; i < size; i++)
            {
                IData data = new DataMap();
                data.put("SERIAL_NUMBER", strs[i].trim());
                relaList.add(data);
            }
        }
        
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<打印relaList的参数值>>>>>>>>>>>>>>>>>>>>");
            if (IDataUtil.isNotEmpty(relaList)){
                logger.debug("<<<<<<<<<<<<<<<<<<<<relaList的参数值>>>>>>>>>>>>>>>>>>>>" + relaList);
            } else if(IDataUtil.isEmpty(relaList)){
                logger.debug("<<<<<<<<<<<<<<<<<<<<relaList的参数值为空>>>>>>>>>>>>>>>>>>>>"); 
            } 
        }
        
        String groupId = inParam.getString("GROUP_ID");
        String productId = inParam.getString("PRODUCT_ID");
        String userId = inParam.getString("USER_ID");
        String custId = inParam.getString("CUST_ID");
        String planType = inParam.getString("PLAN_TYPE");
        String memRoleB = inParam.getString("MEM_ROLE_B");
        String inmodecode = inParam.getString("IN_MODE_CODE","0");
        String notiEndDate = inParam.getString("NOTIN_PAY_END_DATE", "");
        String systime = SysDateMgr.getSysTime();
        
        // 创建集团成员的批量任务
        IData mebBatData = new DataMap();
        
        IData mebCondStrData = new DataMap();
        mebCondStrData.put("GROUP_ID", groupId);
        mebCondStrData.put("PRODUCT_ID", productId);
        mebCondStrData.put("USER_ID", userId);
        mebCondStrData.put("CUST_ID", custId);
        mebCondStrData.put("GRP_SN", grpSn);
        mebCondStrData.put("NEED_RULE", true);
        mebCondStrData.put("PLAN_TYPE", planType);
        mebCondStrData.put("MEM_ROLE_B", memRoleB);
        mebCondStrData.put("NOTIN_PAY_END_DATE", notiEndDate);
        mebCondStrData.put("ELEMENT_INFO", new DatasetList());
        
        //默认下发标准短信
        String smsFlag = inParam.getString("SMS_FLAG");
        String sendForSms = inParam.getString("SEND_FOR_SMS");
        if(StringUtils.isNotBlank(smsFlag) && StringUtils.isNotBlank(sendForSms)
                && "1".equals(smsFlag) && "0".equals(sendForSms)){
            mebCondStrData.put("NOTIN_SMS_FLAG", smsFlag);
            mebCondStrData.put("NOTIN_sendForSms", sendForSms);
        }
        
        mebBatData.put("BATCH_OPER_TYPE", "ADDGFFFQLMEMBER");
        mebBatData.put("BATCH_TASK_NAME", "流量自由充(全量统付)产品成员批量订购-接口");
        //mebBatData.put("SMS_FLAG", "0");
        mebBatData.put("CREATE_TIME", systime);
        mebBatData.put("ACTIVE_FLAG", "1");
        mebBatData.put("ACTIVE_TIME", systime);
        mebBatData.put("DEAL_TIME", systime);
        mebBatData.put("DEAL_STATE", "1");
        mebBatData.put("IN_MODE_CODE",inmodecode);
        mebBatData.put("CODING_STR", mebCondStrData.toString());

        String mebBatchId = "";
        mebBatchId = BatDealBean.createBat(mebBatData, relaList);
        builder.append(mebBatchId);
        
        IData retData = new DataMap();
        retData.put("BATCH_ID", builder.toString());

        return IDataUtil.idToIds(retData);
    }
    
    
    /**
     * 创建流量自由充(定额统付)产品成员新增的批量任务
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset createBatGfffDingEMember(IData inParam) throws Exception
    {
        StringBuilder builder = new StringBuilder(50);
        IDataset relaList = new DatasetList();
        
        String serialNumber = inParam.getString("SERIAL_NUMBER");
        String grpSn = inParam.getString("SERIAL_NUMBER_A");
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<SERIAL_NUMBER的参数值>>>>>>>>>>>>>>>>>>>>" + serialNumber);
        }
        
        if(StringUtils.isNotBlank(serialNumber)){
            String[] strs = StringUtils.split(serialNumber, ",");
            for (int i = 0, size = strs.length; i < size; i++)
            {
                IData data = new DataMap();
                data.put("SERIAL_NUMBER", strs[i].trim());
                relaList.add(data);
            }
        }
        
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<打印relaList的参数值>>>>>>>>>>>>>>>>>>>>");
            if (IDataUtil.isNotEmpty(relaList)){
                logger.debug("<<<<<<<<<<<<<<<<<<<<relaList的参数值>>>>>>>>>>>>>>>>>>>>" + relaList);
            } else if(IDataUtil.isEmpty(relaList)){
                logger.debug("<<<<<<<<<<<<<<<<<<<<relaList的参数值为空>>>>>>>>>>>>>>>>>>>>"); 
            } 
        }
        
        IDataset discntList = new DatasetList();
        String discntCode = inParam.getString("DISCNT_CODE");//优惠编码
        String startDate = inParam.getString("START_DATE");//优惠开始时间
        String endDate = inParam.getString("END_DATE");//优惠结束时间
        if(StringUtils.isNotBlank(discntCode)){
            IData discntData = new DataMap();
            discntData.put("INST_ID", "");
            discntData.put("PRODUCT_ID", "734301");
            discntData.put("PACKAGE_ID", "73430002");
            discntData.put("ELEMENT_ID", discntCode);
            discntData.put("ELEMENT_TYPE_CODE", "D");
            discntData.put("START_DATE", startDate);
            discntData.put("END_DATE", endDate);
            discntData.put("MODIFY_TAG", "0");
            discntList.add(discntData);
        }
        
        
        String groupId = inParam.getString("GROUP_ID");
        String productId = inParam.getString("PRODUCT_ID");
        String userId = inParam.getString("USER_ID");
        String custId = inParam.getString("CUST_ID");
        String planType = inParam.getString("PLAN_TYPE");
        String memRoleB = inParam.getString("MEM_ROLE_B");
        String inmodecode = inParam.getString("IN_MODE_CODE","0");
        String systime = SysDateMgr.getSysTime();
        
        // 创建集团成员的批量任务
        IData mebBatData = new DataMap();
        
        IData mebCondStrData = new DataMap();
        mebCondStrData.put("GROUP_ID", groupId);
        mebCondStrData.put("PRODUCT_ID", productId);
        mebCondStrData.put("USER_ID", userId);
        mebCondStrData.put("CUST_ID", custId);
        mebCondStrData.put("GRP_SN", grpSn);
        mebCondStrData.put("NEED_RULE", true);
        mebCondStrData.put("PLAN_TYPE", planType);
        mebCondStrData.put("MEM_ROLE_B", memRoleB);
        mebCondStrData.put("ELEMENT_INFO", discntList);
        
        //默认下发标准短信
        String smsFlag = inParam.getString("SMS_FLAG");
        String sendForSms = inParam.getString("SEND_FOR_SMS");
        if(StringUtils.isNotBlank(smsFlag) && StringUtils.isNotBlank(sendForSms)
                && "1".equals(smsFlag) && "0".equals(sendForSms)){
            mebCondStrData.put("NOTIN_SMS_FLAG", smsFlag);
            mebCondStrData.put("NOTIN_sendForSms", sendForSms);
        }
        
        mebBatData.put("BATCH_OPER_TYPE", "ADDGFFFDINGEMEMBER");
        mebBatData.put("BATCH_TASK_NAME", "流量自由充(定额统付)产品成员批量订购-接口");
        //mebBatData.put("SMS_FLAG", "0");
        mebBatData.put("CREATE_TIME", systime);
        mebBatData.put("ACTIVE_FLAG", "1");
        mebBatData.put("ACTIVE_TIME", systime);
        mebBatData.put("DEAL_TIME", systime);
        mebBatData.put("DEAL_STATE", "1");
        mebBatData.put("IN_MODE_CODE",inmodecode);
        mebBatData.put("CODING_STR", mebCondStrData.toString());

        String mebBatchId = "";
        mebBatchId = BatDealBean.createBat(mebBatData, relaList);
        builder.append(mebBatchId);
        
        IData retData = new DataMap();
        retData.put("BATCH_ID", builder.toString());

        return IDataUtil.idToIds(retData);
    }
    
    /**
     * 流量自由充(限量统付)产品成员变更
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset changeGrpGfffLimitationMember(IData inParam) throws Exception
    {
        String serialNumber = inParam.getString("SERIAL_NUMBER");//成员手机号
        String grpSn = inParam.getString("SERIAL_NUMBER_A");//集团产品编码
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<SERIAL_NUMBER的参数值>>>>>>>>>>>>>>>>>>>>" + serialNumber);
        }
        
        // 查询集团用户信息
        IData grpuserInfo = UcaInfoQry.qryUserInfoBySnForGrp(grpSn);
        
        if (IDataUtil.isEmpty(grpuserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, grpSn);
        }
        
        String grpUserId = grpuserInfo.getString("USER_ID");
        String grpCustId = grpuserInfo.getString("CUST_ID");
        // 查询集团客户资料
        IData custinfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
        if (IDataUtil.isEmpty(custinfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_190);
        }
        
        IData grpProductInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(grpSn);
        if (IDataUtil.isEmpty(grpProductInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_017, grpSn);
        }
        
        String relaTypeCode = "";
        String productId = grpProductInfo.getString("PRODUCT_ID","");
        
        if("7344".equals(productId)){//流量自由充(限量统付)产品
            relaTypeCode = "T7";
        } else {
            CSAppException.apperr(GrpException.CRM_GRP_872,grpSn);
        }
        
        
        // 查询号码用户信息
        IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }
        
        String userId = userInfo.getString("USER_ID");
        String userCustId = userInfo.getString("CUST_ID");
        IData custInfo = UcaInfoQry.qryPerInfoByCustId(userCustId);
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_716);
        }
        
        //checkMemRelaByUserIdb;
        //getRelationUUByPk
        //getSEL_BY_PK1
        //getUUInfoByUserIdAB
        IDataset uuInfos = RelaUUInfoQry.getUUInfoByUserIdAB(userId,grpUserId,relaTypeCode);
        //IDataset uuInfos = RelaUUInfoQry.qryRelaByUserIdbAndRelaTypeCode(userId,relaTypeCode);
        if(IDataUtil.isEmpty(uuInfos)){
            //获取不到限量统付成员关系
            CSAppException.apperr(GrpException.CRM_GRP_875,serialNumber,grpSn);
        }
        
        
        //优惠处理
        String discntCode = inParam.getString("DISCNT_CODE");//优惠
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<DISCNT_CODE的参数值>>>>>>>>>>>>>>>>>>>>" + discntCode);
        }
        IDataset productElements = new DatasetList();
        if(StringUtils.isNotBlank(discntCode)){
            String[] strs = StringUtils.split(discntCode, ",");
            for (int i = 0, size = strs.length; i < size; i++)
            {
                IData elementInfo = new DataMap();
                elementInfo.put("DISCNT_CODE", strs[i].trim());
                elementInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                productElements.add(elementInfo);
            }
        }
        
        String effectNow = "true"; //立即生效
        
        IDataset processIds = new DatasetList();
        IData callSvcParam = new DataMap();
        IData result = new DataMap();
        
        callSvcParam.put("MEM_ROLE_B", inParam.getString("MEM_ROLE_B","")); // 普通成员角色
        callSvcParam.put("USER_ID", grpUserId);
        callSvcParam.put("GRP_SERIAL_NUMBER", grpSn);
        callSvcParam.put("SERIAL_NUMBER", serialNumber);
        callSvcParam.put("PRODUCT_ID", productId);// 集团产品id
        callSvcParam.put("REMARK", "流量自由充(限量统付)产品成员变更处理接口 ");
        callSvcParam.put("EFFECT_NOW", effectNow);
        callSvcParam.put("LIST_INFOS", productElements);
        
        IDataset resultCallSvc = CSAppCall.call("CS.ChangeMemElementSvc.changeMemElement", callSvcParam);
        if (IDataUtil.isEmpty(resultCallSvc))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "-1:Trade ERROR!");
        }
        else
        {
            result.put("X_LAST_RESULTINFO", "Trade OK!");
            result.put("X_RESULTCODE", "0");
            result.put("ORDER_ID", resultCallSvc.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", resultCallSvc.getData(0).getString("TRADE_ID"));
        }

        processIds.add(result);
        
        return processIds;
    }
    
    
    /**
     * 流量自由充(定额统付)产品成员变更
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset changeGrpGfffDingEMember(IData inParam) throws Exception
    {
        String serialNumber = inParam.getString("SERIAL_NUMBER");//成员手机号
        String grpSn = inParam.getString("SERIAL_NUMBER_A");//集团产品编码
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<SERIAL_NUMBER的参数值>>>>>>>>>>>>>>>>>>>>" + serialNumber);
        }
        
        // 查询集团用户信息
        IData grpuserInfo = UcaInfoQry.qryUserInfoBySnForGrp(grpSn);
        
        if (IDataUtil.isEmpty(grpuserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, grpSn);
        }
        
        String grpUserId = grpuserInfo.getString("USER_ID");
        String grpCustId = grpuserInfo.getString("CUST_ID");
        // 查询集团客户资料
        IData custinfos = UcaInfoQry.qryGrpInfoByCustId(grpCustId);
        if (IDataUtil.isEmpty(custinfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_190);
        }
        
        IData grpProductInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(grpSn);
        if (IDataUtil.isEmpty(grpProductInfo))
        {
            CSAppException.apperr(BofException.CRM_BOF_017, grpSn);
        }
        
        String relaTypeCode = "";
        String productId = grpProductInfo.getString("PRODUCT_ID","");
        
        if("7343".equals(productId)){//流量自由充(定额统付)产品
            relaTypeCode = "T6";
        } else {
            CSAppException.apperr(GrpException.CRM_GRP_872,grpSn);
        }
        
        
        // 查询号码用户信息
        IData userInfo = UserInfoQry.getUserInfoBySN(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }
        
        String userId = userInfo.getString("USER_ID");
        String userCustId = userInfo.getString("CUST_ID");
        IData custInfo = UcaInfoQry.qryPerInfoByCustId(userCustId);
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_716);
        }
        
        IDataset uuInfos = RelaUUInfoQry.getUUInfoByUserIdAB(userId,grpUserId,relaTypeCode);
        //IDataset uuInfos = RelaUUInfoQry.qryRelaByUserIdbAndRelaTypeCode(userId,relaTypeCode);
        if(IDataUtil.isEmpty(uuInfos)){
            //获取不到限量统付成员关系
            CSAppException.apperr(GrpException.CRM_GRP_875,serialNumber,grpSn);
        }
        
        //叠加包的优惠处理
        String discntCode = inParam.getString("DISCNT_CODE");//优惠
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<DISCNT_CODE的参数值>>>>>>>>>>>>>>>>>>>>" + discntCode);
        }
        IDataset productElements = new DatasetList();
        if(StringUtils.isNotBlank(discntCode)){
            String[] strs = StringUtils.split(discntCode, ",");
            for (int i = 0, size = strs.length; i < size; i++)
            {
                IData elementInfo = new DataMap();
                elementInfo.put("DISCNT_CODE", strs[i].trim());
                elementInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                productElements.add(elementInfo);
            }
        }
        
        //成员包的优惠处理
        String newDiscntCode = inParam.getString("NEW_DISCNT_CODE");//新优惠
        String oldDiscntCode = inParam.getString("OLD_DISCNT_CODE");//旧优惠
        if(logger.isDebugEnabled()){
            logger.debug("<<<<<<<<<<<<<<<<<<<<NEW_DISCNT_CODE的参数值>>>>>>>>>>>>>>>>>>>>" + newDiscntCode);
            logger.debug("<<<<<<<<<<<<<<<<<<<<OLD_DISCNT_CODE的参数值>>>>>>>>>>>>>>>>>>>>" + oldDiscntCode);
        }
        if(StringUtils.isNotBlank(newDiscntCode) && StringUtils.isNotBlank(oldDiscntCode)){
            IData newElementInfo = new DataMap();
            newElementInfo.put("DISCNT_CODE", newDiscntCode);
            newElementInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            newElementInfo.put("END_DATE", inParam.getString("NEW_END_DATE"));
            productElements.add(newElementInfo);
            
            IData oldElementInfo = new DataMap();
            oldElementInfo.put("DISCNT_CODE", oldDiscntCode);
            oldElementInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            productElements.add(oldElementInfo);
            
        }
        
        String effectNow = "false"; //立即生效
        
        IDataset processIds = new DatasetList();
        IData callSvcParam = new DataMap();
        IData result = new DataMap();
        
        callSvcParam.put("MEM_ROLE_B", inParam.getString("MEM_ROLE_B","")); // 普通成员角色
        callSvcParam.put("USER_ID", grpUserId);
        callSvcParam.put("GRP_SERIAL_NUMBER", grpSn);
        callSvcParam.put("SERIAL_NUMBER", serialNumber);
        callSvcParam.put("PRODUCT_ID", productId);// 集团产品id
        callSvcParam.put("REMARK", "流量自由充(限量统付)产品成员变更处理接口 ");
        callSvcParam.put("EFFECT_NOW", effectNow);
        callSvcParam.put("LIST_INFOS", productElements);
        
        IDataset resultCallSvc = CSAppCall.call("CS.ChangeMemElementSvc.changeMemElement", callSvcParam);
        if (IDataUtil.isEmpty(resultCallSvc))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "-1:Trade ERROR!");
        }
        else
        {
            result.put("X_LAST_RESULTINFO", "Trade OK!");
            result.put("X_RESULTCODE", "0");
            result.put("ORDER_ID", resultCallSvc.getData(0).getString("ORDER_ID"));
            result.put("TRADE_ID", resultCallSvc.getData(0).getString("TRADE_ID"));
        }

        processIds.add(result);
        
        return processIds;
    }
    
}
