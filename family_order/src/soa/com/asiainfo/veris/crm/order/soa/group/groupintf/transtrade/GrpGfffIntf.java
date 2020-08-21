
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;

public class GrpGfffIntf
{

    private static final long serialVersionUID = 1L;

    protected static final Logger logger = Logger.getLogger(GrpGfffIntf.class);
    
    /**
     * 自由充成员批量新增接口
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset dealGrpGfffAllMem(IData data) throws Exception
    {
        //MODIFY_TAG  0:成员新增;2:成员变更; 1:成员退订
        //OPER_CTRL_TYPE 0:流量自由充(限量统付)产品成员操作;1:流量自由充(全量统付)产品成员操作;2:流量自由充(定额统付)产品成员操作
        
        String serialNumberA = IDataUtil.getMandaData(data, "SERIAL_NUMBER_A");//集团产品编码
        String tradeStaffId = CSBizBean.getVisit().getStaffId();//IDataUtil.getMandaData(data, "TRADE_STAFF_ID");	//业务受理工号
        IDataUtil.getMandaData(data, "MEM_ROLE_B");//成员角色
        //0:流量自由充(限量统付)产品成员操作;
        //1:流量自由充(全量统付)产品成员操作;
        //2:流量自由充(定额统付)产品成员操作
        String operCtrlType = IDataUtil.getMandaData(data, "OPER_CTRL_TYPE");//自由充成员产品操作类型
        String modifyTag = IDataUtil.getMandaData(data, "MODIFY_TAG");//任务名称
        
        IDataset resultInfos = new DatasetList();
        IData batData = new DataMap();
        //IData result = new DataMap();
        
        checkGrpGfffBySerialNumberA(serialNumberA,operCtrlType,data);
        //add by chenzg@20170911 REQ201709050001关于流量自由充流量池的优化需求 校验受理工号和产品编码的对应关系
        checkStaffIdBindGrpSn(tradeStaffId, serialNumberA);
        
        if("0".equals(operCtrlType)){//流量自由充(限量统付)产品成员操作
           
            if("0".equals(modifyTag)){
                data.put("NOTIN_PAY_END_DATE", SysDateMgr.getLastDateThisMonth().substring(0, 10));	//统付的生效截止时间要求改为受理当月月底
                IDataUtil.getMandaData(data, "NOTIN_PAY_LIMIT_FEE");//统付流量大小(MB)
                IDataUtil.getMandaData(data, "NOTIN_PAY_END_DATE");//统付的生效截止时间
                IDataUtil.getMandaData(data, "SERIAL_NUMBER");//成员号码串
                //data.put("SERIAL_NUMBER", "13876711339,18889889773");//测试用的，要删除掉
                
                data.put(Route.USER_EPARCHY_CODE, "0898");
                data.put("PLAN_TYPE", "P");//付费计划
                //data.put("SMS_FLAG", "1");//下发短信
                data.put("SEND_FOR_SMS", "0");//标准短信模板
                
                IDataset batResultSet = CSAppCall.call("SS.GfffGrpMemBatMgrSVC.createBatGfffLimitationMember", data);
                
                if(logger.isDebugEnabled()){
                    if (IDataUtil.isNotEmpty(batResultSet)){
                        logger.debug("<<<<<<<<<<<<<<<<<<<<调用SS.GfffGrpMemBatMgrSVC.createBatGfffLimitationMember服务返回的结果集>>>>>>>>>>>>>>>>>>>>" + batResultSet);
                    }
                }
                
                if(IDataUtil.isNotEmpty(batResultSet)){
                    batData = batResultSet.getData(0);
                }
                batData.put("X_RESULTINFO", "成功!");
                batData.put("X_RESULTCODE", "0");
                resultInfos.add(batData);
                
            } else {
                CSAppException.apperr(GrpException.CRM_GRP_713, "流量自由充(限量统付)产品成员:MODIFY_TAG【" + modifyTag + "】未定义!");
                //result.put("X_RESULTINFO", "流量自由充(限量统付)产品成员：MODIFY_TAG【" + modifyTag + "】未定义!");
                //result.put("X_RESULTCODE", "-80000");
            }
           
            
        } else if("1".equals(operCtrlType)){//流量自由充(全量统付)产品成员操作
            
            if("0".equals(modifyTag)){
                
                IDataUtil.getMandaData(data, "SERIAL_NUMBER");//成员号码串
                //data.put("SERIAL_NUMBER", "13876711339,18889889773");//测试用的，要删除掉
                data.put(Route.USER_EPARCHY_CODE, "0898");
                data.put("PLAN_TYPE", "P");//付费计划
                //data.put("SMS_FLAG", "1");//下发短信
                data.put("SEND_FOR_SMS", "0");//标准短信模板
                data.put("NOTIN_PAY_END_DATE", SysDateMgr.getLastDateThisMonth());	//add by chenzg@20170920业务部门要求接口办理的全量成员新增，失效时间到当月底
                
                IDataset batResultSet = CSAppCall.call("SS.GfffGrpMemBatMgrSVC.createBatGfffQuanLiangMember", data);
                
                if(logger.isDebugEnabled()){
                    if (IDataUtil.isNotEmpty(batResultSet)){
                        logger.debug("<<<<<<<<<<<<<<<<<<<<调用SS.GfffGrpMemBatMgrSVC.createBatGfffQuanLiangMember服务返回的结果集>>>>>>>>>>>>>>>>>>>>" + batResultSet);
                    }
                }
                
                if(IDataUtil.isNotEmpty(batResultSet)){
                    batData = batResultSet.getData(0);
                }
                batData.put("X_RESULTINFO", "成功!");
                batData.put("X_RESULTCODE", "0");
                resultInfos.add(batData);
                
            } else {
                CSAppException.apperr(GrpException.CRM_GRP_713, "流量自由充(全量统付)产品成员:MODIFY_TAG【" + modifyTag + "】未定义!");
                //result.put("X_RESULTINFO", "流量自由充(全量统付)产品成员：MODIFY_TAG【" + modifyTag + "】未定义!");
                //result.put("X_RESULTCODE", "-80001");
            }

        } else if("2".equals(operCtrlType)){//流量自由充(定额统付)产品成员操作
            
            if("0".equals(modifyTag)){
                
                IDataUtil.getMandaData(data, "DISCNT_CODE");//优惠编码
                String startDate = IDataUtil.getMandaData(data, "START_DATE");//优惠开始时间
                String endDate = IDataUtil.getMandaData(data, "END_DATE");//优惠结束时间
                
                IDataUtil.getMandaData(data, "SERIAL_NUMBER");//成员号码串
                //data.put("SERIAL_NUMBER", "13876711339,18889889773");//测试用的，要删除掉
                data.put(Route.USER_EPARCHY_CODE, "0898");
                data.put("PLAN_TYPE", "P");//付费计划
                //data.put("SMS_FLAG", "1");//下发短信
                data.put("SEND_FOR_SMS", "0");//标准短信模板
                
                //校验优惠开始时间和结束时间
                checkDate(startDate,endDate);
                
                IDataset batResultSet = CSAppCall.call("SS.GfffGrpMemBatMgrSVC.createBatGfffDingEMember", data);
                
                if(logger.isDebugEnabled()){
                    if (IDataUtil.isNotEmpty(batResultSet)){
                        logger.debug("<<<<<<<<<<<<<<<<<<<<调用SS.GfffGrpMemBatMgrSVC.createBatGfffDingEMember服务返回的结果集>>>>>>>>>>>>>>>>>>>>" + batResultSet);
                    }
                }
                
                if(IDataUtil.isNotEmpty(batResultSet)){
                    batData = batResultSet.getData(0);
                }
                batData.put("X_RESULTINFO", "成功!");
                batData.put("X_RESULTCODE", "0");
                resultInfos.add(batData);
                
            } else {
                CSAppException.apperr(GrpException.CRM_GRP_713, "流量自由充(定额统付)产品成员:MODIFY_TAG【" + modifyTag + "】未定义!");
                //result.put("X_RESULTINFO", "流量自由充(定额统付)产品成员：MODIFY_TAG【" + modifyTag + "】未定义!");
                //result.put("X_RESULTCODE", "-80002");
            }

        } else {
            CSAppException.apperr(GrpException.CRM_GRP_713, "1:OPER_CTRL_TYPE【" + operCtrlType + "】未定义!");
            //result.put("X_RESULTINFO", "操作类型：OPER_CTRL_TYPE【" + operCtrlType + "】未定义!");
            //result.put("X_RESULTCODE", "-80003");
        }
                
        return resultInfos;
        
    }

    /**
     * 自由充成员变更接口
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset changeGrpGfffAllMem(IData data) throws Exception
    {
        
        //MODIFY_TAG  0:成员新增;2:成员变更; 1:成员退订
        //OPER_CTRL_TYPE 0:流量自由充(限量统付)产品成员操作;1:流量自由充(全量统付)产品成员操作;2:流量自由充(定额统付)产品成员操作
        
        String serialNumberA = IDataUtil.getMandaData(data, "SERIAL_NUMBER_A");//集团产品编码
        IDataUtil.getMandaData(data, "MEM_ROLE_B");//成员角色
        //0:流量自由充(限量统付)产品成员操作;
        //1:流量自由充(全量统付)产品成员操作;
        //2:流量自由充(定额统付)产品成员操作
        String operCtrlType = IDataUtil.getMandaData(data, "OPER_CTRL_TYPE");//自由充成员产品操作类型
        String modifyTag = IDataUtil.getMandaData(data, "MODIFY_TAG");//任务名称
        
        IDataset resultInfos = new DatasetList();
        IData batData = new DataMap();
        
        checkGrpGfffBySerialNumberA(serialNumberA,operCtrlType,data);
       
        if("0".equals(operCtrlType)){//流量自由充(限量统付)产品成员操作
           
          if("2".equals(modifyTag)){
                
                IDataUtil.getMandaData(data, "SERIAL_NUMBER");//成员号码
                IDataUtil.getMandaData(data, "DISCNT_CODE");//叠加包的优惠编码
                
                IDataset batResultSet = CSAppCall.call("SS.GfffGrpMemBatMgrSVC.changeGrpGfffLimitationMember", data);
                
                if(logger.isDebugEnabled()){
                    if (IDataUtil.isNotEmpty(batResultSet)){
                        logger.debug("<<<<<<<<<<<<<<<<<<<<调用SS.GfffGrpMemBatMgrSVC.changeGrpGfffLimitationMember服务返回的结果集>>>>>>>>>>>>>>>>>>>>" + batResultSet);
                    }
                }
                
                if(IDataUtil.isNotEmpty(batResultSet)){
                    batData = batResultSet.getData(0);
                }
                batData.put("X_RESULTINFO", "成功!");
                batData.put("X_RESULTCODE", "0");
                resultInfos.add(batData);
                
            } else {
                CSAppException.apperr(GrpException.CRM_GRP_713, "流量自由充(限量统付)产品成员:MODIFY_TAG【" + modifyTag + "】未定义!");
            }
           
        } else if("2".equals(operCtrlType)){//流量自由充(定额统付)产品成员操作
            
            if("2".equals(modifyTag)){//流量自由充(定额统付)产品成员变更操作
                
                IDataUtil.getMandaData(data, "SERIAL_NUMBER");//成员号码
                //IDataUtil.getMandaData(data, "NEW_DISCNT_CODE");//成员优惠包的优惠，新增的优惠
                //IDataUtil.getMandaData(data, "NEW_END_DATE");//成员优惠包的优惠，新增的优惠
                //IDataUtil.getMandaData(data, "OLD_DISCNT_CODE");//成员优惠包的旧优惠
                
                IDataset batResultSet = CSAppCall.call("SS.GfffGrpMemBatMgrSVC.changeGrpGfffDingEMember", data);
                
                if(logger.isDebugEnabled()){
                    if (IDataUtil.isNotEmpty(batResultSet)){
                        logger.debug("<<<<<<<<<<<<<<<<<<<<调用SS.GfffGrpMemBatMgrSVC.changeGrpGfffDingEMember服务返回的结果集>>>>>>>>>>>>>>>>>>>>" + batResultSet);
                    }
                }
                
                if(IDataUtil.isNotEmpty(batResultSet)){
                    batData = batResultSet.getData(0);
                }
                batData.put("X_RESULTINFO", "成功!");
                batData.put("X_RESULTCODE", "0");
                resultInfos.add(batData);
                
            } else {
                CSAppException.apperr(GrpException.CRM_GRP_713, "流量自由充(定额统付)产品成员:MODIFY_TAG【" + modifyTag + "】未定义!");
            }

        } else {
            CSAppException.apperr(GrpException.CRM_GRP_713, "1:OPER_CTRL_TYPE【" + operCtrlType + "】未定义!");
        }
                
        return resultInfos;
        
    }
    
    /**
     * 校验传入的集团产品编码是否是自由充产品
     *
     * @param data
     * @return
     * @throws Exception
     */
    private static void checkGrpGfffBySerialNumberA(String serialNumber,String operCtrlType,IData info) throws Exception
    {
        if(StringUtils.isNotBlank(serialNumber)){
            IData grpuserInfo = UcaInfoQry.qryUserInfoBySnForGrp(serialNumber);
            
            if (IDataUtil.isEmpty(grpuserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
            }
            String userId = grpuserInfo.getString("USER_ID");
            String custId = grpuserInfo.getString("CUST_ID");
                        
            // 查询集团客户资料
            IData custinfos = UcaInfoQry.qryGrpInfoByCustId(custId);
            if (IDataUtil.isEmpty(custinfos))
            {
                CSAppException.apperr(GrpException.CRM_GRP_190);
            }
            String groupId = custinfos.getString("GROUP_ID");
            
            IData userInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumber);
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BofException.CRM_BOF_017, serialNumber);
            }

            String productId = userInfo.getString("PRODUCT_ID","");
           
            if("0".equals(operCtrlType)){//0:流量自由充(限量统付)产品成员操作;
                if(!"7344".equals(productId)){
                    CSAppException.apperr(GrpException.CRM_GRP_885,serialNumber);
                }
            } else if("1".equals(operCtrlType)){//1:流量自由充(全量统付)产品成员操作;
                if(!"7342".equals(productId)){
                    CSAppException.apperr(GrpException.CRM_GRP_871,serialNumber);
                    
                }
            } else if("2".equals(operCtrlType)){//2:流量自由充(定额统付)产品成员操作
                if(!"7343".equals(productId)){
                    CSAppException.apperr(GrpException.CRM_GRP_872,serialNumber);
                }
            }
                        
            info.put("USER_ID", userId);
            info.put("CUST_ID", custId);
            info.put("PRODUCT_ID", productId);
            info.put("GROUP_ID", groupId);
        }
    }
    
    /**
     * 
     * @param startDate
     * @param endDate
     * @throws Exception
     */
    private static void checkDate(String startDate,String endDate) throws Exception
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = null;
        Date endTime = null;
        
        boolean checkFlag = true;
        checkFlag = checkDateEquals(startDate);
        if(!checkFlag)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "优惠的开始时间格式不正确!格式应为:yyyy-MM-dd HH:mm:ss");
        }
        
        checkFlag = checkDateEquals(endDate);
        if(!checkFlag)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "优惠的结束时间格式不正确!格式应为:yyyy-MM-dd HH:mm:ss");
        }
        
        try
        {
            startTime = sdf.parse(startDate);
        }
        catch (ParseException e)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("优惠的开始时间格式不正确!格式应为:yyyy-MM-dd HH:mm:ss");
            }
            CSAppException.apperr(GrpException.CRM_GRP_713, "优惠的开始时间格式不正确!格式应为:yyyy-MM-dd HH:mm:ss");
        }
        
        //校验优惠开始时间的年月是否是工单的当月
        Date nowDate = new Date();
        int nowYear = nowDate.getYear();
        int nowMonth = nowDate.getMonth();
        int startYear = startTime.getYear();
        int startMonth = startTime.getMonth();
        if(nowYear != startYear || nowMonth != startMonth)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "优惠的开始时间应为业务受理的当前年份、月份.");
        }
        
        //优惠结束时间的当月月底的校验
        String lastDate = SysDateMgr.getLastDateThisMonth();
        if(!endDate.equals(lastDate))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "优惠的结束时间应该为业务受理的当月月底,例如:2016-08-31 23:59:59");
        }
        
    }
    
    private static boolean checkDateEquals(String datetime)
    {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            
            Date date = formatter.parse(datetime);
            return   datetime.equals(formatter.format(date));
            
        }
        catch(Exception   e)
        {
            if(logger.isDebugEnabled())
            {
                logger.debug("时间格式不正确!格式应为:yyyy-MM-dd HH:mm:ss");
            }
            return   false;
        }
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


