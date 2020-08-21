
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import com.ailk.biz.bean.impexp.ImpExpBean;
import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.IFileAction;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.BatDealStateUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.BatDealSVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ESOPCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatchTypeInfoQry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BatDataImportByFile extends ImportTaskExecutor
{

    /**
     * ESOP系统调用批量创建任务成功后回调ESOP回馈接口
     * 
     * @param param
     * @throws Exception
     */
    public void callEsopBackIntf(IData param) throws Exception
    {
        String eosString = param.getString("EOS");
        IDataset eos = new DatasetList(eosString);
        if (IDataUtil.isNotEmpty(eos))
        {
            IData eosData = eos.getData(0);

            IData esopData = new DataMap();
            esopData.put("IBSYSID", eosData.getString("IBSYSID", ""));
            esopData.put("PRODUCT_ID", eosData.getString("PRODUCT_ID", ""));
            esopData.put("NODE_ID", eosData.getString("NODE_ID", ""));
            esopData.put("TRADE_ID", eosData.getString("TRADE_ID", ""));
            esopData.put("USER_ID", eosData.getString("USER_ID", ""));
            esopData.put("BPM_TEMPLET_ID", eosData.getString("BPM_TEMPLET_ID", ""));
            esopData.put("MAIN_TEMPLET_ID", eosData.getString("MAIN_TEMPLET_ID", ""));
            esopData.put("CITY_CODE", getVisit().getCityCode());
            esopData.put("DEPART_ID", getVisit().getDepartId());
            esopData.put("DEPART_NAME", getVisit().getDepartName());
            esopData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            esopData.put("STAFF_ID", getVisit().getStaffId());
            esopData.put("STAFF_NAME", getVisit().getStaffName());
            esopData.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_2);
            esopData.put("X_SUBTRANS_CODE", "SaveAndSend");
            esopData.put("OPER_CODE", "01");
            esopData.put("ORIG_DOMAIN", "ECRM"); // 发起方应用域代码
            esopData.put("HOME_DOMAIN", "ECRM"); // 归属方应用域代码
            esopData.put("BIPCODE", "EOS2D011"); // 业务交易代码 这个编码要传进来
            esopData.put("ACTIVITYCODE", "T2011011"); // 交易代码 这个编码也要传进来
            esopData.put("BUSI_SIGN", ""); // 报文类型，BPM要基于此判断 eosData.get("","")
            esopData.put("WORK_TYPE", "00"); // 提交类型 页面提交 08失败通知
            esopData.put("PROCESS_TIME", SysDateMgr.getSysTime()); // 处理时间
            esopData.put("ACCEPT_DATE", SysDateMgr.getSysTime()); // 受理时间
            esopData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 受理地州
            esopData.put("UPDATE_STAFF_ID", getVisit().getStaffId()); // 受理员工
            esopData.put("UPDATE_DEPART_ID", getVisit().getDepartId()); // 受理部门
            esopData.put("TRADE_CITY_CODE", getVisit().getCityCode());
            esopData.put("WORK_ID", eosData.getString("WORK_ID", "")); // BPM工作标识,
            esopData.put("X_RESULTINFO", "TradeOk");
            esopData.put("X_RESULTCODE", "0");

            esopData.put("DEAL_DESC", "esop批量操作");
            ESOPCall.callESOP("ITF_EOS_TcsGrpBusi", esopData);
        }
    }

    /**
     * 创建批量任务
     * 
     * @param param
     * @param resultList
     * @return
     * @throws Exception
     */
    public IDataset createBatchTask(IData param, IDataset resultList) throws Exception
    {
        IData data = param;
        data.put("SMS_FLAG", param.getString("SMS_FLAG", "0"));

        String batTaskId = BatDealBean.createBatTask(data); // 创建批量任务
        param.put("BATCH_TASK_ID", batTaskId);

        return importDataByFile(param, resultList);// 生成批量明细 并返回错误数据

    }

    @Override
    public boolean execute(IData param) throws Exception
    {
        String ftpSite = param.getString("ftpSite");
        String serializeId = param.getString("fileSerializeId");
        String taskLogId = getTaskLogId();
        String fileName = "导入失败文件[" + taskLogId + "].xls";

        String config = param.getString("config");
        List<?> configData = ExcelConfig.getSheets(config);

        IData data = getImportData(param, configData);

        if (IDataUtil.isEmpty(data))
            return false;

        IData[] sheet = (IData[]) data.get("sheet");
        IDataset[] source = (IDataset[]) data.get("right");
        // 发送MQ,更新导入状态为IMPORT_STATUS_PARSEDATA

        IDataset[] error = (IDataset[]) data.get("error");
        int rightCount = data.getInt("rightCount");
        int errorCount = data.getInt("errorCount");

        String errorInfo = "";
        String status = "ok";
        String stack = null;

        try
        {
            for (int i = 0, row = source.length; i < row; i++)
            {
                param.put("sheet", sheet[i]);
                IDataset importError = executeImport(param, source[i]);
                if (IDataUtil.isNotEmpty(importError))
                {
                    error[i].addAll(importError);
                    rightCount = rightCount - importError.size();
                    errorCount = errorCount + importError.size();
                }
            }
            errorInfo = rightCount + "," + errorCount;
        }
        catch (Exception e)
        {
            throw e;
        }

        // 将导入失败的数据生成文件
        param.put("FILE_TYPE", IFileAction.UPLOAD_TYPE_IMPORT_FAIL);
        File errorFile = null;

        try
        {
            errorFile = ImpExpUtil.writeExcelData(error, "/", null, configData, ImpExpUtil.excel_03, 0, 0);

            // 将导入失败的文件上传
            String errorFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(errorFile), ftpSite, "upload/import", fileName);
            String errorUrl = ImpExpUtil.getDownloadPath(errorFileId, fileName);

            // 更新导入任务状态为已完成
            IData logData = new DataMap();
            logData.put("LOG_ID", serializeId);
            logData.put("LOG_PROG", "100");
            logData.put("LOG_STATUS", status);// :已完成
            logData.put("DOWNLOAD_URL", errorUrl);
            logData.put("ERROR_INFO", errorInfo);
            logData.put("ERROR_STACK", stack);

            ImpExpBean bean = BeanManager.createBean(ImpExpBean.class);
            bean.updateImpExpLogById(logData);
            return true;
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            if (null != errorFile)
            {
                errorFile.delete();
            }
        }
    }

    @Override
    public IDataset executeImport(IData inparam, IDataset succeList) throws Exception
    {
       
//REQ202005210027关于优化集团短彩信业务签名设置等相关内容的优化需求
        if("SMSBUSINESSHANDING".equals(inparam.getString("BATCH_OPER_TYPE"))){

            for (int i=0;i<succeList.size();i++){
                IData verfyData= succeList.getData(i);
                String enStr=verfyData.getString("DATA2");
                String zhStr=verfyData.getString("DATA3");
                int zhCount=0;
                String regEx = "[\u4e00-\u9fa5]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(zhStr);
                while(m.find())
                {
                    zhCount++;
                }
                int enCharacter = enStr.length();//英文字符
//                for (int k = 0; k < enStr.length(); k++) {
//                    char tmp = enStr.charAt(k);
//                    if ((tmp >= 'A' && tmp <= 'Z') || (tmp >= 'a' && tmp <= 'z')|| (tmp >= '0' && tmp <= '9')) {
//                        enCharacter++;
//                    }
//                }
                if (enCharacter<4||zhCount<2){
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "中文签名限制不少于2个汉字，英文签名限制不少于4个字符");
                }

            }

        }
        //REQ202005210027关于优化集团短彩信业务签名设置等相关内容的优化需求

        IDataset errorList = createBatchTask(inparam, succeList);

        if (IDataUtil.isEmpty(errorList))
        {
            return null;
        }

        // 返回错误数据给MQ导入组件统计
        return errorList;
    }

    /**
     * 特殊校验写到这个方法里面 过滤成功数据 返回失败数据， sucList需要剔剔除掉失败的数据
     * 
     * @param param
     * @param sucList
     * @throws Exception
     */
    public IDataset filterSucDatasBySpecCheck(IData param, IDataset sucList) throws Exception
    {
    	IDataset err = new DatasetList();
    	String batOperCode = param.getString("BATCH_OPER_CODE");
    	if("BATOPENGROUPMEM".equals(batOperCode)){
    		for (int i = 0, size = sucList.size(); i < size; i++)
            {
                IData sucData = sucList.getData(i);
                String sn = sucData.getString("SERIAL_NUMBER");
                IData verifyParam = new DataMap();
                verifyParam.put("TRADE_TYPE_CODE", "3008");
                verifyParam.put("SERIAL_NUMBER", sn);
                verifyParam.put(Route.ROUTE_EPARCHY_CODE, param.getString("ROUTE_EPARCHY_CODE", "0898"));
                try {
                	CSAppCall.call( "CS.CheckTradeSVC.verifyUnFinishTrade", verifyParam);
				} catch (Exception e) {
					// TODO: handle exception
					sucData.put("IMPORT_ERROR", e.getMessage());
					err.add(sucData);
				}
			}
    		sucList.removeAll(err);
    		if ( IDataUtil.isNotEmpty(err) ) {
    			return err;
    		}
    	}
    	return null;
    }

    /**
     * 获取导入的数据(重写wade的ImportTaskExecutor方法)
     * 
     * @param param
     * @param configData
     * @return
     * @throws Exception
     */
    public IData getImportData(IData param, List<?> configData) throws Exception
    {
        String serializeId = param.getString("fileSerializeId");

        try
        {
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

            ImpExpBean bean = BeanManager.createBean(ImpExpBean.class);
            IData logInfo = bean.queryImpExpLogById(serializeId);

            IData data = ImpExpUtil.beginImport(serializeId, logInfo.getString("FILE_ID"), configData);

            // 文件解析是否成功
            if (IDataUtil.isEmpty(data))
            {
                CSAppException.apperr(BatException.CRM_BAT_89);
            }

            // 判断是否有数据
            if (data.getInt("rightCount") + data.getInt("errorCount") == 0)
            {
                CSAppException.apperr(BatException.CRM_BAT_86);
            }

            return data;

        }
        catch (Exception e)
        {
            throw e;
        }
    }

    public IDataset importDataByFile(IData param, IDataset resultList) throws Exception
    {
        IData InParamData = param;
        // 对MQ返回结果IDataset进行分析
        IDataset suc = new DatasetList();
        IDataset err = new DatasetList();
        for (int i = 0, size = resultList.size(); i < size; i++)
        {
            IData resultData = resultList.getData(i);
            if ("true".equals(resultData.getString("IMPORT_RESULT")))
            {
                suc.add(resultData);
            }
            else
            {
                err.add(resultData);// 实际上MQ回调过来的数据并没有错误数据
            }
        }

        // 特殊校验 过滤不符合要求的成功数据 返回失败数据， sucList需要剔剔除掉失败的数据
        IDataset errList = filterSucDatasBySpecCheck(param, suc);
        if (IDataUtil.isNotEmpty(errList))
        {
            err.addAll(errList);
        }

        String batOperCode = param.getString("BATCH_OPER_CODE");
        int sucCount = suc.size();// 解析成功的数据总条数

        IData queryData = new DataMap();
        queryData.put("BATCH_OPER_CODE", batOperCode);
        /** 查询批量参数表 */
        IData params = BatchTypeInfoQry.queryBatchTypeParamsEx(batOperCode, CSBizBean.getTradeEparchyCode());

        int limit = params.getInt("LIMIT_NUM_BATCH"); // 导入条数限制
        int limit_day = params.getInt("LIMIT_NUM_DAY", 0); // 导入日条数限制
        int limit_month = params.getInt("LIMIT_NUM_MON", 0); // 导入月条数限制

        int priority = params.getInt("PRIORITY"); // 优先级
        String cancelable_flags = params.getString("CANCELABLE_FLAG"); // 可否返销标志

        InParamData.put("PRIORITY", priority);
        InParamData.put("CANCELABLE_FLAG", cancelable_flags);
        InParamData.put("AUDIT_STATE", "0");

        String hint_message = "";
        /** 导入条数上限控制 */
        if (sucCount > limit && limit != 0)
        {
            CSAppException.apperr(BatException.CRM_BAT_45, sucCount, limit);
        }

        BatDealSVC dealBeanSvc = new BatDealSVC();

        // 没有日或者月限制则没必要去查询一次统计sql了
        if (limit_day > 0 || limit_month > 0)
        {

            IData importedCount = dealBeanSvc.getNowDayCount(queryData).getData(0);
            int day_count = Integer.parseInt(importedCount.getString("SUMS", "0"));
            int month_count = Integer.parseInt(importedCount.getString("MONTH_SUM", "0"));

            if (sucCount + day_count > limit_day && limit_day > 0)
            {
                CSAppException.apperr(BatException.CRM_BAT_34, sucCount, limit_day, day_count);
            }
            if (sucCount + month_count > limit_month && limit_month > 0)
            {
                hint_message = "导入条数过多：" + sucCount + ", <br/>" + "本月最大可导入条数为：" + limit_month + ", <br/>" + "本月已导入条数为：" + month_count + "! <br/>" + "请修改条数限制或者下个月导入! <br/>";

                CSAppException.apperr(CrmCommException.CRM_COMM_103, hint_message);
            }
        }
        // 判断是否从ESOP系统调用集团批量
        String esopTag = param.getString("EOS", "");
        if (StringUtils.isNotEmpty(esopTag))
        {
            InParamData.put("ESOP_TAG", "ESOP");// 放置ESOP系统标记位
        }

        // 将正确的插入到批量明细
        if (sucCount != 0)
        {
            IData idata = new DataMap();
            idata.put("DATA_SET", suc);
            idata.put("IN_PARAM", InParamData);
            dealBeanSvc.importData(idata);
        }

        if (StringUtils.isNotEmpty(esopTag))
        {
            callEsopBackIntf(param);
        }

        return err;
    }
}
