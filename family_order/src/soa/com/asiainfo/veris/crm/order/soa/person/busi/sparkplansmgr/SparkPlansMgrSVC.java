
package com.asiainfo.veris.crm.order.soa.person.busi.sparkplansmgr;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.SparkPlansMgrException;
import com.asiainfo.veris.crm.order.pub.exception.StaffException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockLogInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;

public class SparkPlansMgrSVC extends CSBizService
{

    private static int MAX_SIZE = 500;

    public IDataset querysparkPlans(IData data) throws Exception
    {
        String staffIdS = data.getString("STAFF_ID_S");
        String staffIdE = data.getString("STAFF_ID_E");
        String cityCode = null;
        String departId = null;
        String staffId = null;
        
        String rightLevel = StaffPrivUtil.getFieldPrivClass(getVisit().getStaffId(), "SYS_CRM_QrySparkPlansRange");
		if("4".equals(rightLevel)||"5".equals(rightLevel)){
			
		}else if("3".equals(rightLevel)){
			cityCode = getVisit().getCityCode();
		}else if("2".equals(rightLevel)){
			cityCode = getVisit().getCityCode();
			departId = getVisit().getDepartId();
		}else if("1".equals(rightLevel)){
			cityCode = getVisit().getCityCode();
			departId = getVisit().getDepartId();
			staffId = getVisit().getStaffId();
		} else {
			cityCode = getVisit().getCityCode();
			departId = getVisit().getDepartId();
			staffId = getVisit().getStaffId();
		}
		
        return ActiveStockInfoQry.querysparkPlans(staffIdS, staffIdE, cityCode, departId, staffId, getPagination());
    }

    public void assignSparkPlans(IData data) throws Exception
    {
        String eparchyCode = data.getString("EPARCHY_CODE");
        String staffId = data.getString("STAFF_ID");
        String number = data.getString("NUMBER");
        String resKindCode = data.getString("RES_KIND_CODE");
        String staffIdF = data.getString("STAFF_ID_F");

        if (StringUtils.isBlank(eparchyCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "地州编码");
        }
        if (StringUtils.isBlank(staffId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "归属工号");
        }
        if (StringUtils.isBlank(number))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "调出数量");
        }
        if (StringUtils.isBlank(resKindCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "种类");
        }
        if (StringUtils.isBlank(staffIdF))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "领用员工");
        }
        if (staffId.equals(staffIdF))
        {
            CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_6, staffIdF, staffId);
        }

        IData updatedt = new DataMap();
        boolean flag = false;
        int operNum = 0;

        // 判断入参有没有

        IDataset results = ActiveStockInfoQry.querySparkPlans(eparchyCode, staffId, resKindCode);
        if (IDataUtil.isEmpty(results))
        {
            CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_1);
        }
        IData insertDt = results.getData(0);

        IDataset staff = StaffInfoQry.queryValidStaffById(staffIdF);
        if (IDataUtil.isEmpty(staff))
        {
            CSAppException.apperr(StaffException.CRM_STAFF_1, "报错", staffIdF);
        }
        String cityCode = staff.getData(0).getString("CITY_CODE");

        if (Integer.parseInt(number) > insertDt.getInt("SURPLUS_VALUE"))
        {
            CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_2);
        }

        operNum = insertDt.getInt("WARNNING_VALUE_D") - Integer.parseInt(number);
        updatedt.put("WARNNING_VALUE_D", operNum);
        //
        flag = Dao.save("TF_F_ACTIVE_STOCK", updatedt, new String[]
        { "EPARCHY_CODE", "STAFF_ID", "RES_KIND_CODE" }, new String[]
        { eparchyCode, staffId, resKindCode });

        if (!flag)
        {
            CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_3);
        }

        results = ActiveStockInfoQry.querySparkPlans(eparchyCode, staffIdF, resKindCode);
        if (IDataUtil.isEmpty(results))
        {
            // 插表
            insertDt.put("CITY_CODE", cityCode);
            insertDt.put("STAFF_ID", staffIdF);
            insertDt.put("WARNNING_VALUE_D", number);
            insertDt.put("WARNNING_VALUE_U", "0");
            insertDt.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            insertDt.put("UPDATE_DEPART_ID", getVisit().getDepartId());

            flag = Dao.insert("TF_F_ACTIVE_STOCK", insertDt);
            if (!flag)
            {
                CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_4);
            }
        }
        else
        {
            // 更新
            updatedt.clear();
            operNum = Integer.parseInt(number) + results.getData(0).getInt("WARNNING_VALUE_D");
            updatedt.put("WARNNING_VALUE_D", operNum);
            flag = Dao.save("TF_F_ACTIVE_STOCK", updatedt, new String[]
            { "EPARCHY_CODE", "STAFF_ID", "RES_KIND_CODE" }, new String[]
            { eparchyCode, staffIdF, resKindCode });

            if (!flag)
            {
                CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_5);
            }
        }

        if (flag)
        {
            // 插调拨日志表
            IData param = new DataMap();
            param.put("LOG_ID", SeqMgr.getLogIdForCrm());
            param.put("EPARCHY_CODE", eparchyCode);
            param.put("STAFF_ID_IN", staffIdF);
            param.put("STAFF_ID_OUT", staffId);
            param.put("RES_KIND_CODE", resKindCode);
            param.put("PRODUCT_NAME", data.getString("PRODUCT_NAME"));
            param.put("PACKAGE_NAME", data.getString("PACKAGE_NAME"));
            param.put("CITY_CODE", data.getString("CITY_CODE"));
            param.put("DEPART_ID", data.getString("DEPART_ID"));
            param.put("ASSIGN_COUNT", number);
            param.put("RSRV_TAG1", "0");
            param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            param.put("UPDATE_TIME", SysDateMgr.getSysTime());
            Dao.insert("TF_F_ACTIVE_STOCK_LOG", param);
        }
    }

    public void backSparkPlans(IData data) throws Exception
    {
        String eparchyCode = data.getString("EPARCHY_CODE");
        String staffId = data.getString("STAFF_ID");
        String number = data.getString("NUMBER");
        String resKindCode = data.getString("RES_KIND_CODE");
        String staffIdF = data.getString("STAFF_ID_F");

        if (StringUtils.isBlank(eparchyCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "地州编码");
        }
        if (StringUtils.isBlank(staffId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "归属工号");
        }
        if (StringUtils.isBlank(number))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "调出数量");
        }
        if (StringUtils.isBlank(resKindCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "种类");
        }
        if (StringUtils.isBlank(staffIdF))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "回收员工");
        }
        if (staffId.equals(staffIdF))
        {
            CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_7, staffIdF, staffId);
        }

        IData updatedt = new DataMap();
        boolean flag = false;
        int operNum = 0;

        // 判断入参有没有
        IDataset results = ActiveStockInfoQry.querySparkPlans(eparchyCode, staffIdF, resKindCode);
        if (IDataUtil.isEmpty(results))
        {
            CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_1);
        }
        IData insertDt = results.getData(0);

        IDataset staff = StaffInfoQry.queryValidStaffById(staffIdF);
        if (IDataUtil.isEmpty(staff))
        {
            CSAppException.apperr(StaffException.CRM_STAFF_1, "报错", staffIdF);
        }

        if (Integer.parseInt(number) > insertDt.getInt("SURPLUS_VALUE"))
        {
            CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_2);
        }

        operNum = insertDt.getInt("WARNNING_VALUE_D") - Integer.parseInt(number);
        updatedt.put("WARNNING_VALUE_D", operNum);
        //
        flag = Dao.save("TF_F_ACTIVE_STOCK", updatedt, new String[]
        { "EPARCHY_CODE", "STAFF_ID", "RES_KIND_CODE" }, new String[]
        { eparchyCode, staffIdF, resKindCode });

        if (!flag)
        {
            CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_3);
        }

        results = ActiveStockInfoQry.querySparkPlans(eparchyCode, staffId, resKindCode);
        if (IDataUtil.isEmpty(results))
        {
            CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_1);
        }
        else
        {
            // 更新
            updatedt.clear();
            operNum = Integer.parseInt(number) + results.getData(0).getInt("WARNNING_VALUE_D");
            updatedt.put("WARNNING_VALUE_D", operNum);
            flag = Dao.save("TF_F_ACTIVE_STOCK", updatedt, new String[]
            { "EPARCHY_CODE", "STAFF_ID", "RES_KIND_CODE" }, new String[]
            { eparchyCode, staffId, resKindCode });

            if (!flag)
            {
                CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_5);
            }
        }

        if (flag)
        {
            // 插调拨日志表
            IData param = new DataMap();
            param.put("LOG_ID", SeqMgr.getLogIdForCrm());
            param.put("EPARCHY_CODE", eparchyCode);
            param.put("STAFF_ID_IN", staffId);
            param.put("STAFF_ID_OUT", staffIdF);
            param.put("RES_KIND_CODE", resKindCode);
            param.put("PRODUCT_NAME", data.getString("PRODUCT_NAME"));
            param.put("PACKAGE_NAME", data.getString("PACKAGE_NAME"));
            param.put("CITY_CODE", data.getString("CITY_CODE"));
            param.put("DEPART_ID", data.getString("DEPART_ID"));
            param.put("ASSIGN_COUNT", number);
            param.put("RSRV_TAG1", "1");
            param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            param.put("UPDATE_TIME", SysDateMgr.getSysTime());
            Dao.insert("TF_F_ACTIVE_STOCK_LOG", param);
        }
    }

    public IData importAssignInfo(IData data) throws Exception
    {
        IData rtData = new DataMap();
        IDataset succList = new DatasetList();
        IDataset failList = new DatasetList();
        IDataset importDataList = data.getDataset("IMPORT_DATA_LIST");
        String eparchyCode = data.getString("EPARCHY_CODE");
        String staffId = data.getString("STAFF_ID");
        String resKindCode = data.getString("RES_KIND_CODE");

        if (StringUtils.isBlank(eparchyCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "地州编码");
        }
        if (StringUtils.isBlank(staffId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "归属工号");
        }
        if (StringUtils.isBlank(resKindCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "种类");
        }
        if (IDataUtil.isEmpty(importDataList))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "导入数据");
        }
        if (importDataList.size() > SparkPlansMgrSVC.MAX_SIZE)
        {
            CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_8, SparkPlansMgrSVC.MAX_SIZE);
        }

        for (int i = 0, size = importDataList.size(); i < size; i++)
        {
            IData importData = importDataList.getData(i);
            data.remove("IMPORT_DATA_LIST");
            importData.putAll(data);
            try
            {
                CSAppCall.call("SS.SparkPlansMgrSVC.assignSparkPlans", importData);
                succList.add(importData);
            }
            catch (BaseException e)
            {
                String errInfo = e.getInfo();
                importData.put("ERRINFO", errInfo);
                failList.add(importData);
            }
            catch (Exception e)
            {
                importData.put("ERRINFO", e.getMessage());
                failList.add(importData);
            }
        }

        rtData.put("SUCCLIST", succList);
        rtData.put("FAILLIST", failList);

        return rtData;
    }

    public IData importBackInfo(IData data) throws Exception
    {
        IData rtData = new DataMap();
        IDataset succList = new DatasetList();
        IDataset failList = new DatasetList();
        IDataset importDataList = data.getDataset("IMPORT_DATA_LIST");
        String eparchyCode = data.getString("EPARCHY_CODE");
        String staffId = data.getString("STAFF_ID");
        String resKindCode = data.getString("RES_KIND_CODE");

        if (StringUtils.isBlank(eparchyCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "地州编码");
        }
        if (StringUtils.isBlank(staffId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "归属工号");
        }
        if (StringUtils.isBlank(resKindCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "种类");
        }
        if (IDataUtil.isEmpty(importDataList))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "导入数据");
        }
        if (importDataList.size() > SparkPlansMgrSVC.MAX_SIZE)
        {
            CSAppException.apperr(SparkPlansMgrException.CRM_SPARKPLANS_8, SparkPlansMgrSVC.MAX_SIZE);
        }

        for (int i = 0, size = importDataList.size(); i < size; i++)
        {
            IData importData = importDataList.getData(i);
            data.remove("IMPORT_DATA_LIST");
            importData.putAll(data);
            try
            {
                CSAppCall.call("SS.SparkPlansMgrSVC.backSparkPlans", importData);
                succList.add(importData);
            }
            catch (BaseException e)
            {
                String errInfo = e.getInfo();
                importData.put("ERRINFO", errInfo);
                failList.add(importData);
            }
            catch (Exception e)
            {
                importData.put("ERRINFO", e.getMessage());
                failList.add(importData);
            }
        }

        rtData.put("SUCCLIST", succList);
        rtData.put("FAILLIST", failList);

        return rtData;
    }

    public IDataset queryAssignLog(IData data) throws Exception
    {
        String cityCode = data.getString("CITY_CODE");
        String departId = data.getString("DEPART_ID");
        String staffId = data.getString("UPDATE_STAFF_ID");
        String productName = data.getString("PRODUCT_NAME");
        String packageName = data.getString("PACKAGE_NAME");
        String startDate = data.getString("START_DATE");
        String endDate = data.getString("END_DATE");

        IDataset results = ActiveStockLogInfoQry.queryLog(cityCode, departId, staffId, productName, packageName, startDate, endDate, getPagination());
        for (int i = 0, size = results.size(); i < size; i++)
        {
            IData logData = results.getData(i);
            String operType = logData.getString("RSRV_TAG1");
            logData.put("OPER_TYPE_DESC", "1".equals(operType) ? "回收" : "调拨");
        }

        return results;
    }
}
