
package com.asiainfo.veris.crm.order.soa.group.largessfluxmgrbean;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;

public class LargessFluxGrpSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(LargessFluxGrpSVC.class);
    
    /**
     * 处理集团客户获得的流量，把满足条件的获得的流量存储到集团流量表中
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset dealGrpLargessFlux(IData inParam) throws Exception
    {
        
        IDataset results = new DatasetList();
        IData result =new DataMap();
        
        IData inData = new DataMap();
        inData.put("PRODUCT_ID", "69901002");
        inData.put("PROCESS_TAG", "0");
        
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULEINFO", "分配集团流量成功!");
        
//        String startDate = "";
//        String endDate = "";
//        IDataset timeInfos = LargessFluxQry.qryUserGrpGfffTagTime(new DataMap());
//        if(IDataUtil.isEmpty(timeInfos)){
//            result.put("X_RESULTCODE", "-1000");
//            result.put("X_RESULEINFO", "查询定时任务的时间为空!");
//        }
//        if(IDataUtil.isNotEmpty(timeInfos)){
//            startDate = timeInfos.getData(0).getString("START_DATE");
//            endDate = SysDateMgr.getSysDate();
//        }
//        System.out.println("startDate=" + startDate + " endDate=" + endDate);
        
        IDataset resultInfos = SaleActiveInfoQry.queryLargessFluxGrpActiveInfo(inData);
        if (IDataUtil.isNotEmpty(resultInfos)){
            
            for (int i = 0; i < resultInfos.size(); i++){
                IData element = resultInfos.getData(i);
                if(IDataUtil.isNotEmpty(element)){
                    String serialNumber = element.getString("SERIAL_NUMBER","");
                    String userId = element.getString("USER_ID","");
                    String packageId = element.getString("PACKAGE_ID","");
                    String acctId = element.getString("RSRV_STR25","");
                    String saleStartDate = element.getString("START_DATE","");
                    
                    
                    String startDate = "";
                    String endDate = "";
                    
                    IData timeData = new DataMap();
                    timeData.put("USER_ID", userId);
                    timeData.put("REMOVE_TAG", "0");
                    IDataset timeInfos = LargessFluxQry.qryUserGrpGfffTagTime(timeData);
                    if(IDataUtil.isEmpty(timeInfos)){//取到空时，用sale_active表里的开始时间
                        startDate = saleStartDate;
                        endDate = SysDateMgr.getSysDate();
                    } else if(IDataUtil.isNotEmpty(timeInfos)){
                        startDate = timeInfos.getData(0).getString("START_DATE");
                        endDate = SysDateMgr.getSysDate();
                    }
                    
                    String custId = "";
                    //获取默认账户acctId
                    //IData defaultPayInfos = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
                    //IDataset payInfos = PayRelaInfoQry.getPayRelaByUserId(userIdA);
                    //if(IDataUtil.isNotEmpty(defaultPayInfos)){
                    //    acctId = defaultPayInfos.getString("ACCT_ID","");
                    //    custId = defaultPayInfos.getString("CUST_ID","");
                    //}
                    //System.out.println("defaultPayInfos=" + defaultPayInfos);
                    
                    
                    //通过默认账户，调用账务提供的接口，查询销账
                    //AM_CRM_QueryGPRSWriteOffFee;
                    int acctFee = 0;//消费金额
                    if(StringUtils.isNotBlank(acctId)){
                        
                        IData param = new DataMap();
                        param.put("ACCT_ID", acctId);
                        param.put("START_DATE", startDate);
                        param.put("END_DATE", endDate);

                        IDataOutput output = CSAppCall.callAcct("AM_CRM_QueryGPRSWriteOffFee", param, false);
                        
                        if ("0".equals(output.getHead().getString("X_RESULTCODE")))
                        {
                            
                            IDataset chkAcctInfos = output.getData();
                            if (IDataUtil.isNotEmpty(chkAcctInfos))
                            {
                                acctFee = chkAcctInfos.getData(0).getInt("FEE",0);// 单位：分
                            }
                        } else {
                            String resultCode = output.getHead().getString("X_RESULTCODE");
                            String reusltInfo = output.getHead().getString("X_RESULEINFO");
                            result.put("X_RESULTCODE", resultCode);
                            result.put("X_RESULEINFO", reusltInfo);
                        }
                        
                    }
                    
                    boolean resultInsert = false;
                    //办理档次
                    if(acctFee > 0){
                        
                        double largessAllFee = 0;//赠送的流量数
                        if(StringUtils.equals("69901549", packageId)){//1档
                            
                            largessAllFee = (acctFee * 0.3) / (50 * 100) * 1024;
                            
                                                       
                            IData param = new DataMap();
                            param.put("PARTITION_ID", userId.substring(userId.length() - 4));
                            param.put("USER_ID", userId);//分配的集团产品编码的user_id
                            param.put("INST_ID", SeqMgr.getInstId("0898"));//分配的集团产品编码
                            param.put("CUST_ID", custId);
                            param.put("SERIAL_NUMBER", serialNumber);//
                            param.put("CUST_NAME", "");
                            param.put("LIMIT_FEE", largessAllFee);
                            param.put("INSERT_TIME", SysDateMgr.getSysTime());
                            param.put("START_DATE", SysDateMgr.getSysTime());
                            param.put("END_DATE", SysDateMgr.firstDayOfMonth(SysDateMgr.getSysTime(),6));
                            param.put("UPDATE_TIME", SysDateMgr.getSysTime());
                            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                            
                            resultInsert = Dao.insert("TF_F_USER_GRP_GFFF", param);
                            if(!resultInsert){
                                result.put("X_RESULTCODE", "-1003");
                                result.put("X_RESULEINFO", "新增分配集团产品" + serialNumber + "流量失败!");
                                CSAppException.apperr(GrpException.CRM_GRP_868,serialNumber);
                            }
                           
                            
                        } else if(StringUtils.equals("69901550", packageId)){//2档
                            
                            largessAllFee = (acctFee * 0.5) / (50 * 100) * 1024;
                            
                            
                            IData param = new DataMap();
                            param.put("PARTITION_ID", userId.substring(userId.length() - 4));
                            param.put("USER_ID", userId);//分配的集团产品编码的user_id
                            param.put("INST_ID", SeqMgr.getInstId("0898"));//分配的集团产品编码
                            param.put("CUST_ID", custId);
                            param.put("SERIAL_NUMBER", serialNumber);//
                            param.put("CUST_NAME", "");
                            param.put("LIMIT_FEE", largessAllFee);
                            param.put("INSERT_TIME", SysDateMgr.getSysTime());
                            param.put("START_DATE", SysDateMgr.getSysTime());
                            param.put("END_DATE", SysDateMgr.firstDayOfMonth(SysDateMgr.getSysTime(),6));
                            param.put("UPDATE_TIME", SysDateMgr.getSysTime());
                            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                            
                            resultInsert = Dao.insert("TF_F_USER_GRP_GFFF", param);
                            if(!resultInsert){
                                result.put("X_RESULTCODE", "-1004");
                                result.put("X_RESULEINFO", "新增分配集团产品" + serialNumber + "流量失败!");
                                CSAppException.apperr(GrpException.CRM_GRP_868,serialNumber);
                            }
                            
                            
                        } else if(StringUtils.equals("69901550", packageId)){//3档
                            
                            largessAllFee = (acctFee * 0.6) / (50 * 100) * 1024;
                            
                            
                            IData param = new DataMap();
                            param.put("PARTITION_ID", userId.substring(userId.length() - 4));
                            param.put("USER_ID", userId);//分配的集团产品编码的user_id
                            param.put("INST_ID", SeqMgr.getInstId("0898"));//分配的集团产品编码
                            param.put("CUST_ID", custId);
                            param.put("SERIAL_NUMBER", serialNumber);//
                            param.put("CUST_NAME", "");
                            param.put("LIMIT_FEE", largessAllFee);
                            param.put("INSERT_TIME", SysDateMgr.getSysTime());
                            param.put("START_DATE", SysDateMgr.getSysTime());
                            param.put("END_DATE", SysDateMgr.firstDayOfMonth(SysDateMgr.getSysTime(),6));
                            param.put("UPDATE_TIME", SysDateMgr.getSysTime());
                            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                            
                            resultInsert = Dao.insert("TF_F_USER_GRP_GFFF", param);
                            
                            if(!resultInsert){
                                result.put("X_RESULTCODE", "-1005");
                                result.put("X_RESULEINFO", "新增分配集团产品" + serialNumber + "流量失败!");
                                CSAppException.apperr(GrpException.CRM_GRP_868,serialNumber);
                            }
                            
                            
                        }
                    }
                  
                    
                    if(acctFee > 0 && resultInsert){
                        
                        if(IDataUtil.isEmpty(timeInfos)){
                            
                            String newStartDate = SysDateMgr.addDays(1);
                            IData param = new DataMap();
                            param.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
                            param.put("USER_ID", userId);
                            param.put("REMOVE_TAG", "0");
                            param.put("RSRV_STR1", acctFee);
                            param.put("START_DATE", newStartDate);
                            
                            resultInsert = Dao.insert("TF_F_USER_GRP_GFFF_TAGTIME", param);
                            
                        } else if(IDataUtil.isNotEmpty(timeInfos)){
                            
                            String newStartDate = SysDateMgr.addDays(1);
                            IData data = new DataMap();
                            data.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
                            data.put("USER_ID", userId);
                            data.put("REMOVE_TAG", "0");
                            data.put("RSRV_STR1", acctFee);
                            data.put("START_DATE", newStartDate);
                            Dao.save("TF_F_USER_GRP_GFFF_TAGTIME", data,new String[]{"PARTITION_ID","USER_ID","REMOVE_TAG"});
                            
                        }
                            
                    }
                    
                }
            }
            
        }
        
//        String isResultCode = result.getString("X_RESULTCODE");
//        System.out.println("result===" + result);
//        if(StringUtils.isNotBlank(isResultCode) && "0".equals(isResultCode)){
//            String newStartDate = SysDateMgr.addDays(1);
//            IData data = new DataMap();
//            data.put("TAGRECORD", "TAGRECORD");
//            data.put("START_DATE", newStartDate);
//            Dao.save("TF_F_USER_GRP_GFFF_TAGTIME", data,new String[]{"TAGRECORD"});
//            
//        }
        
        results.add(result);
        return results;
    }
    
    
    /**
     * 处理集团客户获得的流量，把满足条件的获得的流量存储到集团流量表中
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset dealGrpLargessFluxFee(IData inParam) throws Exception
    {
     
        IDataset results = new DatasetList();
        IData result =new DataMap();
        
        if(logger.isDebugEnabled()){
            logger.debug("<<<<aee传过来的入参 param= " + inParam);
        }
        
        if (IDataUtil.isNotEmpty(inParam)){
            String serialNumber = inParam.getString("SERIAL_NUMBER","");
            String userId = inParam.getString("USER_ID","");
            String packageId = inParam.getString("PACKAGE_ID","");
            String acctId = inParam.getString("RSRV_STR25","");
            String saleStartDate = inParam.getString("START_DATE","");
            
            String startDate = "";
            String endDate = "";
            
            IData timeData = new DataMap();
            timeData.put("USER_ID", userId);
            timeData.put("REMOVE_TAG", "0");
            IDataset timeInfos = LargessFluxQry.qryUserGrpGfffTagTime(timeData);
            if(IDataUtil.isEmpty(timeInfos)){//取到空时，用sale_active表里的开始时间
                startDate = saleStartDate;
                endDate = SysDateMgr.getSysDate();
            } else if(IDataUtil.isNotEmpty(timeInfos)){
                startDate = timeInfos.getData(0).getString("START_DATE");
                endDate = SysDateMgr.getSysDate();
            }
            
            String custId = "";
            
            int acctFee = 0;//消费金额
            if(StringUtils.isNotBlank(acctId)){
                
                IData param = new DataMap();
                param.put("ACCT_ID", acctId);
                param.put("START_DATE", startDate);
                param.put("END_DATE", endDate);

                IDataOutput output = CSAppCall.callAcct("AM_CRM_QueryGPRSWriteOffFee", param, false);
                
                if ("0".equals(output.getHead().getString("X_RESULTCODE")))
                {
                    
                    IDataset chkAcctInfos = output.getData();
                    if (IDataUtil.isNotEmpty(chkAcctInfos))
                    {
                        acctFee = chkAcctInfos.getData(0).getInt("FEE",0);// 单位：分
                    }
                } else {
                    String resultCode = output.getHead().getString("X_RESULTCODE");
                    String reusltInfo = output.getHead().getString("X_RESULEINFO");
                    result.put("X_RESULTCODE", resultCode);
                    result.put("X_RESULEINFO", reusltInfo);
                }
            }
            
            boolean resultInsert = false;
            //办理档次
            if(acctFee > 0){
                
                double largessAllFee = 0;//赠送的流量数
                if(StringUtils.equals("69901549", packageId)){//1档
                    
                    largessAllFee = (acctFee * 0.3) / (50 * 100) * 1024;
                    
                                               
                    IData param = new DataMap();
                    param.put("PARTITION_ID", userId.substring(userId.length() - 4));
                    param.put("USER_ID", userId);//分配的集团产品编码的user_id
                    param.put("INST_ID", SeqMgr.getInstId("0898"));//分配的集团产品编码
                    param.put("CUST_ID", custId);
                    param.put("SERIAL_NUMBER", serialNumber);//
                    param.put("CUST_NAME", "");
                    param.put("LIMIT_FEE", largessAllFee);
                    param.put("INSERT_TIME", SysDateMgr.getSysTime());
                    param.put("START_DATE", SysDateMgr.getSysTime());
                    param.put("END_DATE", SysDateMgr.firstDayOfMonth(SysDateMgr.getSysTime(),6));
                    param.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    
                    resultInsert = Dao.insert("TF_F_USER_GRP_GFFF", param);
                    if(!resultInsert){
                        result.put("X_RESULTCODE", "-1003");
                        result.put("X_RESULEINFO", "新增分配集团产品" + serialNumber + "流量失败!");
                        CSAppException.apperr(GrpException.CRM_GRP_868,serialNumber);
                    }
                   
                    
                } else if(StringUtils.equals("69901550", packageId)){//2档
                    
                    largessAllFee = (acctFee * 0.5) / (50 * 100) * 1024;
                    
                    
                    IData param = new DataMap();
                    param.put("PARTITION_ID", userId.substring(userId.length() - 4));
                    param.put("USER_ID", userId);//分配的集团产品编码的user_id
                    param.put("INST_ID", SeqMgr.getInstId("0898"));//分配的集团产品编码
                    param.put("CUST_ID", custId);
                    param.put("SERIAL_NUMBER", serialNumber);//
                    param.put("CUST_NAME", "");
                    param.put("LIMIT_FEE", largessAllFee);
                    param.put("INSERT_TIME", SysDateMgr.getSysTime());
                    param.put("START_DATE", SysDateMgr.getSysTime());
                    param.put("END_DATE", SysDateMgr.firstDayOfMonth(SysDateMgr.getSysTime(),6));
                    param.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    
                    resultInsert = Dao.insert("TF_F_USER_GRP_GFFF", param);
                    if(!resultInsert){
                        result.put("X_RESULTCODE", "-1004");
                        result.put("X_RESULEINFO", "新增分配集团产品" + serialNumber + "流量失败!");
                        CSAppException.apperr(GrpException.CRM_GRP_868,serialNumber);
                    }
                    
                    
                } else if(StringUtils.equals("69901551", packageId)){//3档
                    
                    largessAllFee = (acctFee * 0.6) / (50 * 100) * 1024;
                    
                    
                    IData param = new DataMap();
                    param.put("PARTITION_ID", userId.substring(userId.length() - 4));
                    param.put("USER_ID", userId);//分配的集团产品编码的user_id
                    param.put("INST_ID", SeqMgr.getInstId("0898"));//分配的集团产品编码
                    param.put("CUST_ID", custId);
                    param.put("SERIAL_NUMBER", serialNumber);//
                    param.put("CUST_NAME", "");
                    param.put("LIMIT_FEE", largessAllFee);
                    param.put("INSERT_TIME", SysDateMgr.getSysTime());
                    param.put("START_DATE", SysDateMgr.getSysTime());
                    param.put("END_DATE", SysDateMgr.firstDayOfMonth(SysDateMgr.getSysTime(),6));
                    param.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    
                    resultInsert = Dao.insert("TF_F_USER_GRP_GFFF", param);
                    
                    if(!resultInsert){
                        result.put("X_RESULTCODE", "-1005");
                        result.put("X_RESULEINFO", "新增分配集团产品" + serialNumber + "流量失败!");
                        CSAppException.apperr(GrpException.CRM_GRP_868,serialNumber);
                    }
                    
                    
                }
            }
            
            if(acctFee > 0 && resultInsert){
                
                if(IDataUtil.isEmpty(timeInfos)){
                    
                    String newStartDate = SysDateMgr.addDays(1);
                    IData param = new DataMap();
                    param.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
                    param.put("USER_ID", userId);
                    param.put("REMOVE_TAG", "0");
                    param.put("RSRV_STR1", acctFee);
                    param.put("START_DATE", newStartDate);
                    
                    resultInsert = Dao.insert("TF_F_USER_GRP_GFFF_TAGTIME", param);
                    
                } else if(IDataUtil.isNotEmpty(timeInfos)){
                    
                    String newStartDate = SysDateMgr.addDays(1);
                    IData data = new DataMap();
                    data.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
                    data.put("USER_ID", userId);
                    data.put("REMOVE_TAG", "0");
                    data.put("RSRV_STR1", acctFee);
                    data.put("START_DATE", newStartDate);
                    Dao.save("TF_F_USER_GRP_GFFF_TAGTIME", data,new String[]{"PARTITION_ID","USER_ID","REMOVE_TAG"});
                    
                }
                
            }
            
        }
        
        results.add(result);
        return results;
    }
}
