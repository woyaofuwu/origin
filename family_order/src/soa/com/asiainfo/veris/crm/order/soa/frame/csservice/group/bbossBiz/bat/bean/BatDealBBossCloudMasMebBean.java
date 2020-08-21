package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.bean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;

public class BatDealBBossCloudMasMebBean
{
    /** 
    * @Title: createBatForCloudMas
    * @Description: 为行业网关云MAS业务创建批量入口
    * @param data
    * @return
    * @throws Exception    
    * @return boolean
    * @author chenkh
    * @time 2015年4月15日
    */ 
    public static IData createBatForCloudMas(IData data) throws Exception
    {
        // 准备返回数据
        IData retData = new DataMap();

        // 1-1 初始化批量明细表信息
        IData batDealParam = new DataMap();

        // 1-2 初始化批量任务表信息
        IData batTaskParam = new DataMap();

        // 1-3 初始化批量主表信息
        IData batMainParam = new DataMap();

        // 2 处理批量表insert信息，优先处理批量明细表信息，后处理批量任务表和批量主表信息
        dealBatInfo(data, batDealParam, batTaskParam, batMainParam);
        
     // 3-1 插批量明细表信息
        if (IDataUtil.isEmpty(batDealParam))
        {
            CSAppException.apperr(BatException.CRM_BAT_90);
        }
        Dao.insert("TF_B_TRADE_BATDEAL", batDealParam, Route.getJourDb(Route.CONN_CRM_CG));

        // 3-2 插批量任务表信息
        if (IDataUtil.isEmpty(batTaskParam))
        {
            CSAppException.apperr(BatException.CRM_BAT_92);
        }
        Dao.insert("TF_B_TRADE_BAT_TASK", batTaskParam, Route.getJourDb(Route.CONN_CRM_CG));

        // 3-3 插批量主表信息
        if (IDataUtil.isEmpty(batMainParam))
        {
            CSAppException.apperr(BatException.CRM_BAT_91);
        }
        Dao.insert("TF_B_TRADE_BAT", batMainParam, Route.getJourDb(Route.CONN_CRM_CG));
        
        retData.put("BATCH_ID", batDealParam.getString("BATCH_ID", ""));
        retData.put("BATCH_OPER_TYPE", batDealParam.getString("BATCH_OPER_TYPE", ""));

        return retData;
    }
    
    private static void dealBatInfo(IData data, IData batDealParam, IData batTaskParam, IData batMainParam) throws Exception
    {

        // 处理批量明细表信息
        dealBatDealInfo(data, batDealParam);
        // TF_B_TRADE_BAT的BATCH_COUNT加1
        BatDealBean bean = new BatDealBean();
        bean.addBatchCount(batDealParam);
        
        // 处理批量任务表信息
        dealBatTaskInfo(data, batDealParam, batTaskParam);

        // 处理批量主表信息
        dealBatMainInfo(data, batDealParam, batMainParam);
    }
    
    /** 
    * @Title: dealBatMainInfo
    * @Description: 处理批量主表信息
    * @param data
    * @param batDealParam
    * @param batMainParam
    * @throws Exception    
    * @return void
    * @author chenkh
    * @time 2015年4月15日
    */ 
    private static void dealBatMainInfo(IData data, IData batDealParam, IData batMainParam) throws Exception 
    {
        // 1-1 初始化用户信息
        String tradeStaffId = data.getString("UPDATESTAFFID", "IAGWIntf");
        String tradeDepartId = data.getString("TRADE_DEPART_ID", "IAGW");
        String tradeCityCode = "IAGW";  //城市代码没有传，故放行业网关缩写
        String tradeEparchyCode = IDataUtil.getMandaData(data, "TRADE_EPARCHY_CODE");

        // 处理批量主表信息
        batMainParam.put("BATCH_TASK_ID", batDealParam.getString("BATCH_TASK_ID"));
        batMainParam.put("BATCH_ID", batDealParam.getString("BATCH_ID"));
        batMainParam.put("BATCH_OPER_TYPE", batDealParam.getString("BATCH_OPER_TYPE"));
        batMainParam.put("BATCH_COUNT", "1");
        batMainParam.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        batMainParam.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        batMainParam.put("STAFF_ID", tradeStaffId);
        batMainParam.put("DEPART_ID", tradeDepartId);
        batMainParam.put("CITY_CODE", tradeCityCode);
        batMainParam.put("EPARCHY_CODE", tradeEparchyCode);
        batMainParam.put("TERM_IP", "127.0.0.1");
        batMainParam.put("IN_MODE_CODE", "v");
        batMainParam.put("REMOVE_TAG", "0");
        batMainParam.put("ACTIVE_FLAG", "0");
        batMainParam.put("AUDIT_STATE", "0");
        batMainParam.put("AUDIT_REMARK", "批量任务名称");
        batMainParam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batDealParam.getString("BATCH_TASK_ID")));
    }
    
    /** 
    * @Title: dealBatTaskInfo
    * @Description: 处理批量任务表
    * @param data
    * @param batDealParam
    * @param batTaskParam
    * @throws Exception    
    * @return void
    * @author chenkh
    * @time 2015年4月15日
    */ 
    private static void dealBatTaskInfo(IData data, IData batDealParam, IData batTaskParam) throws Exception
    {
        // 1-1 初始化用户信息
        String tradeStaffId = data.getString("UPDATESTAFFID", "IAGWIntf");
        String tradeDepartId = data.getString("TRADE_DEPART_ID", "IAGW");
        String tradeCityCode = "IAGW";  //城市代码没有传，故放行业网关缩写
        String tradeEparchyCode = IDataUtil.getMandaData(data, "TRADE_EPARCHY_CODE");
        
        // 处理批量任务表信息
        batTaskParam.put("BATCH_TASK_ID", batDealParam.getString("BATCH_TASK_ID"));
        batTaskParam.put("BATCH_TASK_NAME", batDealParam.getString("BATCH_TASK_NAME"));
        batTaskParam.put("BATCH_OPER_CODE", batDealParam.getString("BATCH_OPER_TYPE"));
        batTaskParam.put("BATCH_OPER_NAME", batDealParam.getString("BATCH_OPER_NAME"));
        batTaskParam.put("START_DATE", SysDateMgr.getSysTime());
        batTaskParam.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        batTaskParam.put("CREATE_TIME", SysDateMgr.getSysDate());
        batTaskParam.put("CREATE_STAFF_ID", tradeStaffId);
        batTaskParam.put("CREATE_DEPART_ID", tradeDepartId);
        batTaskParam.put("CREATE_CITY_CODE", tradeCityCode);
        batTaskParam.put("CREATE_EPARCHY_CODE", tradeEparchyCode);
        batTaskParam.put("SMS_FLAG", "0");
        batTaskParam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batDealParam.getString("BATCH_TASK_ID")));
    }
    
    /** 
    * @Title: dealBatDealInfo
    * @Description: 处理批量详细信息表
    * @param data
    * @param batDealParam
    * @throws Exception    
    * @return void
    * @author chenkh
    * @time 2015年4月15日
    */ 
    private static void dealBatDealInfo(IData data, IData batDealParam) throws Exception
    {
        String sysDate = SysDateMgr.getSysTime();
        String acceptMonth = SysDateMgr.getCurMonth();
        String operCode = IDataUtil.getMandaData(data, "OPR_CODE");

        batDealParam.put("PRIORITY", "10");
        batDealParam.put("REFER_TIME", sysDate);
        batDealParam.put("EXEC_TIME", sysDate);
        batDealParam.put("ACCEPT_MONTH", acceptMonth);
        batDealParam.put("CANCEL_TAG", "0");
        batDealParam.put("DEAL_STATE", "0");
        batDealParam.put("SERIAL_NUMBER", data.getString("MOB_NUM", ""));
        batDealParam.put("DATA19", operCode);
        
        //处理新增和注销业务
        if ("01".equals(operCode))
        {
            batDealParam.put("BATCH_OPER_TYPE", "BATADDBBOSSMEMBER");
            batDealParam.put("BATCH_OPER_NAME", "集团BBOSS成员批量新增");
        }
        else if ("02".equals(operCode)) 
        {
            batDealParam.put("BATCH_OPER_TYPE", "BATDELBBOSSMEMBER");
            batDealParam.put("BATCH_OPER_NAME", "集团BBOSS成员批量注销");
        }
        
        batDealParam.put("BATCH_TASK_NAME", "行业网关订购业务批量");
        batDealParam.put("BATCH_TASK_ID", SeqMgr.getBatchId());
        batDealParam.put("BATCH_ID", SeqMgr.getBatchId());
        batDealParam.put("OPERATE_ID", SeqMgr.getBatchId());
    }
}
