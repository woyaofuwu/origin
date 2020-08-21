
package com.asiainfo.veris.crm.order.soa.person.busi.custmanagermaintain;

import java.io.File;
import java.io.FileInputStream;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherservQry;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.NpConst;

public class CustManagerMainTainSVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * 根据客户经理号码加用户号码 新增短信停开机
     * 
     * @param serialNumber
     *            用户号码
     * @param rsrvStr1
     *            经理号码
     * @return
     * @throws Exception
     */
    public boolean addOtherSvcTradeData(String serialNumber, String rsrvStr1, int iCount, int iExisted, IData selectData, IData data) throws Exception
    {

        IDataset results = new DatasetList();
        // 判断RSRV_STR1是否是客户经理
        IDataset custManagerInfos = UStaffInfoQry.qryCustManagerStaffBySn(rsrvStr1);
        if (custManagerInfos.size() <= 0)
        {
            // 这里不抛错，但记录错误日志
            String errorString = rsrvStr1 + "不是客户经理号码";
            selectData.put("ERROR_STRING", errorString);
            return false;
        }
        // 现判断需要增加的记录在表中是否已经存在
        IDataset seriRsrvInfoDataset = UserOtherservQry.getUserOtherservBySNandRSRVSTR1(serialNumber, rsrvStr1, "V");
        if (seriRsrvInfoDataset.size() > 0)
        {
            iExisted++;
            // 这里不抛错，但记录错误日志
            String errorString = "客户经理:" + rsrvStr1 + " 用户:" + serialNumber + " 的对应关系已经存在！";
            selectData.put("ERROR_STRING", errorString);
            return false;
        }
        // 根据手机号码获取用户名称
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (userInfo.size() < 1)
        {
            String errorString = "普通用户:" + serialNumber + " 不存在！";
            selectData.put("ERROR_STRING", errorString);
            return false;
        }
        // 根据客户标识获取用户名称
        IDataset custVipInfos = CustVipInfoQry.getCustVipByUserId(userInfo.getString("USER_ID"), "0", "");
        if (custVipInfos.size() < 1)
        {
            String errorString = "个人大客户:" + serialNumber + " 不存在！";
            selectData.put("ERROR_STRING", errorString);
            return false;
        }
        String userId = userInfo.getString("USER_ID");
        data.put("USER_ID", userId);
        data.put("SERVICE_MODE", "V");
        data.put("PARTITION_ID", userId.substring(userId.length() - 4));
        data.put("SERIAL_NUMBER", serialNumber);
        data.put("PROCESS_INFO", "客户经理停开机增加记录");
        data.put("RSRV_NUM1", 0);
        data.put("RSRV_NUM2", 0);
        data.put("INST_ID", SeqMgr.getInstId());
        data.put("RSRV_NUM3", 0);
        data.put("RSRV_STR1", rsrvStr1);
        data.put("RSRV_STR2", custVipInfos.getData(0).getString("CUST_NAME", ""));
        data.put("RSRV_STR3", custManagerInfos.getData(0).getString("CUST_MANAGER_NAME"));
        data.put("RSRV_DATE1", SysDateMgr.getSysDate());
        data.put("PROCESS_TAG", "0");
        data.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("START_DATE", SysDateMgr.getSysDate());
        data.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        data.put("REMARK", "");
        // results.add(data);
        // Dao.insert("TF_F_USER_OTHERSERV", data);
        iCount++;
        return true;
    }

    private IDataset checkImportData(IDataset dataset) throws Exception
    {
        if (IDataUtil.isEmpty(dataset))
        {
            return null;
        }

        IDataset resultSet = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            int count = 0;
            IData importData = dataset.getData(i);
            String custNumber = importData.getString("RSRV_STR1");
            String seriNumber = importData.getString("SERIAL_NUMBER");
            for (int j = 0; j < dataset.size(); j++)
            {
                IData checkData = dataset.getData(j);
                String custNumber2 = checkData.getString("RSRV_STR1");
                String seriNumber2 = checkData.getString("SERIAL_NUMBER");
                if (StringUtils.equals(custNumber, custNumber2) && StringUtils.equals(seriNumber, seriNumber2))
                {
                    count++;
                    if (count > 1)
                    {
                        checkData.put("ERROR_STRING", "重复数据");
                        resultSet.add(checkData);
                        dataset.remove(j);
                    }
                }
            }
        }
        return resultSet;
    }

    // 校验用户是否是携出欠费停机
    public void checkSerialnumber(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        if (serialNumber != "")
        {
            param.put("SERIAL_NUMBER", serialNumber);
            IDataset dataset = UserInfoQry.getAllUserInfoBySn(serialNumber);
            if (dataset == null || dataset.size() == 0)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
            }
            else
            {
                if (dataset.get(0, "USER_STATE_CODESET", "").toString().indexOf(NpConst.USER_STATE_CODE_CARRY_OUT_OWE_FEE_STOP) != -1)
                {
                    CSAppException.apperr(CrmUserNpException.CRM_USER_NP_101);
                }
                else
                {
                    IDataset dataset1 = TradeInfoQry.getMainTradeBySN(serialNumber, "44");
                    if (dataset1 != null && dataset1.size() > 0)
                    {
                        CSAppException.apperr(TradeException.CRM_TRADE_327, serialNumber);
                    }
                }
            }
        }
    }

    public boolean deleteOtherSvcTradeData(String serialNumber, String rsrvStr1, int iCount, IData selectData) throws Exception
    {
        IDataset seriRsrvInfoDataset = UserOtherservQry.getUserOtherservBySNandRSRVSTR1(serialNumber, rsrvStr1, "V");
        if (seriRsrvInfoDataset.size() < 1)
        {
            String errorString = "客户经理:" + rsrvStr1 + " 用户:" + serialNumber + " 的对应关系不存在！";
            selectData.put("ERROR_STRING", errorString);
            return false;
        }
        int num = UserOtherservQry.deleteUserOtherservBySNandRSRVSTR1(serialNumber, rsrvStr1, "V");
        if (num > 0)
        {
            iCount++;
            return true;
        }
        return false;
    }

    public IDataset executeImport(IData data, IDataset dataset) throws Exception
    {

        String fileId = data.getString("cond_STICK_LIST"); // 上传OCS监控excelL文件的编号
        String[] fileIds = fileId.split(",");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        IDataset failds = new DatasetList();
        for (String strfileId : fileIds)
        {
            IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/custmanagermaintain/CustManagerMaintain.xml"));
            IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
            IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
            dataset.addAll(suc[0]);
            failds.addAll(err[0]);
        }
        // 效验导入的数据是否正确
        IData fileCheckData = fileImportCheckForProductChg(dataset);
        ;
        IDataset succds = fileCheckData.getDataset("SUCCDS");
        IDataset failds2 = fileCheckData.getDataset("FAILDS");
        failds.addAll(failds2);

        IData returnInfo = new DataMap();

        // 判断是否重复添加
        IDataset failresultSet = checkImportData(succds);
        if (IDataUtil.isNotEmpty(failresultSet))
        {
            failds.addAll(failresultSet);
        }

        returnInfo.put("DATASET_SIZE", succds.size() + failds.size());
        returnInfo.put("SUCC_SIZE", succds.size());
        returnInfo.put("FAILD_SIZE", failds.size());

        if (IDataUtil.isNotEmpty(failds))
        {
            String fileIdE = ImpExpUtil.getImpExpManager().getFileAction().createFileId();
            String fileName = fileIdE + ".xls";
            File errorFile = ImpExpUtil.writeDataToFile("xls", new IDataset[]
            { failds }, "personserv", fileIdE, null, "import/custmanagermaintain/CustManagerMaintainError.xml");
            String errorFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(errorFile), "personserv", "upload/import", fileName);
            String errorUrl = ImpExpUtil.getDownloadPath(errorFileId, fileName);

            returnInfo.put("FAILED_TYPE", "1");
            returnInfo.put("FILE_ID", errorFileId);
            returnInfo.put("ERROR_URL", errorUrl);
        }

        IDataset resultSet = new DatasetList();
        IData result = new DataMap();
        result.put("SUCCESS", succds);
        result.put("FAILDS", returnInfo);
        resultSet.add(result);

        return resultSet;
    }

    public IDataset exportCustManagerInfos(IData input) throws Exception
    {
        String serialNumber = input.getString("cond_SERIAL_NUMBER");
        String rsrvStr1 = input.getString("cond_CUST_NUMBER");

        IDataset result = null;
        if (!"".equals(serialNumber) && "".equals(rsrvStr1))
        {
            result = UserOtherservQry.getUserOtherservBySN(serialNumber, "V");
        }
        else if ("".equals(serialNumber) && !"".equals(rsrvStr1))
        {
            result = UserOtherservQry.getUserOtherservByRSRVSTR1(rsrvStr1, "V");
        }
        else if (!"".equals(serialNumber) && !"".equals(rsrvStr1))
        {
            result = UserOtherservQry.getUserOtherservBySNandRSRVSTR1(serialNumber, rsrvStr1, "V");
        }
        else
        {
            CSAppException.apperr(CustException.CRM_CUST_4);
        }
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CustException.CRM_CUST_5);
        }

        IDataset resultDataset = new DatasetList();
        IData resultData = new DataMap();
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        if (IDataUtil.isNotEmpty(result))
        {
            String fileIdE = ImpExpUtil.getImpExpManager().getFileAction().createFileId();
            String fileName = fileIdE + ".xls";
            File errorFile = ImpExpUtil.writeDataToFile("xls", new IDataset[]
            { result }, "personserv", fileIdE, null, "import/custmanagermaintain/CustManagerQueryInfos.xml");
            String errorFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(errorFile), "personserv", "upload/import", fileName);
            String errorUrl = ImpExpUtil.getDownloadPath(errorFileId, fileName);

            resultData.put("DATASET_SIZE", result.size()); // 总数
            resultData.put("FILE_ID", errorFileId);
            resultData.put("ERROR_URL", errorUrl);
        }
        resultDataset.add(resultData);
        return resultDataset;
    }

    public IData fileImportCheckForProductChg(IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);

            if (!StringUtils.isNumeric(data.getString("SERIAL_NUMBER")))
            {
                data.put("ERROR_STRING", "服务号码格式不对!");
                failds.add(data);
                continue;
            }

            succds.add(data);
        }

        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        if (succds.size() == 0)
        {
            return returnData;
        }
        else
        {
            String serial_number = (String) succds.get(0, "SERIAL_NUMBER");
            if (serial_number == null || serial_number.equals(""))
            {
                return returnData;
            }
        }

        return returnData;
    }

    public IDataset formatDataset(IDataset dataset, String tag) throws Exception
    {
        IDataset result = new DatasetList();
        if ("0".equals(tag))
        {
            for (int i = 0; i < dataset.size(); i++)
            {

                IData tmpData = dataset.getData(i);
                if ("".equals(tmpData.getString("col_DEAL_FLAG")))
                {
                    tmpData.put("col_DEAL_FLAG", '3');
                    result.add(tmpData);
                }
            }
        }
        else
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData tmpData = dataset.getData(i);
                if ("".equals(tmpData.getString("col_DEAL_FLAG")))
                {
                    tmpData.put("col_DEAL_FLAG", '4');
                    result.add(tmpData);
                }
            }
        }
        return result;
    }

    public IDataset genOtherSvcTradeData(IDataset buildSelectJsonData) throws Exception
    {
        String serialNumber = "";
        String rsrvStr1 = "";
        String dealFlag = "";
        int iCount = 0;// 累加每次增加或删除的条数
        int iNum = buildSelectJsonData.size();// 本次业务需要增加或者删除的记录条数
        int iExisted = 0;// 记录本次增加的记录中已经在表中存在的条数
        IDataset failDataset = new DatasetList();

        IDataset resutls = new DatasetList();

        IData selectData = new DataMap();
        for (int i = 0; i < iNum; i++)
        {
            IData sucessData = new DataMap();
            selectData = buildSelectJsonData.getData(i);
            serialNumber = selectData.getString("col_SERIAL_NUMBER"); // 客户手机号码
            rsrvStr1 = selectData.getString("col_RSRV_STR1"); // 经理手机号码
            dealFlag = selectData.getString("col_DEAL_FLAG"); // 处理标记 3为新增，4为删除
            if (StringUtils.equals(dealFlag, "3"))
            {
                boolean flag = addOtherSvcTradeData(serialNumber, rsrvStr1, iCount, iExisted, selectData, sucessData);
                if (!flag)
                {
                    failDataset.add(selectData);
                    continue;
                }
                else
                {
                    resutls.add(sucessData);
                }
            }
            if (StringUtils.equals(dealFlag, "4"))
            {
                boolean flag = deleteOtherSvcTradeData(serialNumber, rsrvStr1, iCount, selectData);
                if (!flag)
                {
                    failDataset.add(selectData);
                    continue;
                }
            }
        }

        if (IDataUtil.isNotEmpty(resutls))
        {
            Dao.insert("TF_F_USER_OTHERSERV", resutls);
        }

        IDataset resultDataset = new DatasetList();
        IData resultData = new DataMap();
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        if (IDataUtil.isNotEmpty(failDataset))
        {
            String fileIdE = ImpExpUtil.getImpExpManager().getFileAction().createFileId();
            String fileName = fileIdE + ".xls";
            File errorFile = ImpExpUtil.writeDataToFile("xls", new IDataset[]
            { failDataset }, "personserv", fileIdE, null, "import/custmanagermaintain/CustManagerMaintainEmport.xml");
            String errorFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(errorFile), "personserv", "upload/import", fileName);
            String errorUrl = ImpExpUtil.getDownloadPath(errorFileId, fileName);

            resultData.put("FAILED_TYPE", "1");
            resultData.put("FILE_ID", errorFileId);
            resultData.put("ERROR_URL", errorUrl);
        }
        resultData.put("SUCC_SIZE", iNum - failDataset.size()); // 累加每次增加或删除的条数
        resultData.put("DATASET_SIZE", iNum); // 总数
        resultData.put("X_IEXISTED", iExisted); // 记录本次增加的记录中已经在表中存在的条数
        resultData.put("FAILD_SIZE", failDataset.size());
        resultDataset.add(resultData);
        return resultDataset;
    }

    public IDataset importData(IData input) throws Exception
    {
        setUserEparchyCode(CSBizBean.getTradeEparchyCode());
        IDataset set = new DatasetList(); // 上传excel文件内容明细
        return executeImport(input, set);
    }

    public IDataset refreshClick(IData input) throws Exception
    {
        String serialNumber = input.getString("cond_SERIAL_NUMBER");
        String rsrvStr1 = input.getString("cond_CUST_NUMBER");

        IDataset result = new DatasetList();
        if (!"".equals(serialNumber) && "".equals(rsrvStr1))
        {
            result = UserOtherservQry.getUserOtherservBySN(serialNumber, "V", this.getPagination());
        }
        else if ("".equals(serialNumber) && !"".equals(rsrvStr1))
        {
            result = UserOtherservQry.getUserOtherservByRSRVSTR1(rsrvStr1, "V", this.getPagination());
        }
        else if (!"".equals(serialNumber) && !"".equals(rsrvStr1))
        {
            result = UserOtherservQry.getUserOtherservBySNandRSRVSTR1(serialNumber, rsrvStr1, "V", this.getPagination());
        }
        else
        {
            CSAppException.apperr(CustException.CRM_CUST_4);
        }

        return result;
    }

    public IDataset sureClick(IData input) throws Exception
    {
        IDataset dataset = new DatasetList();
        String stopOpenRelationFlag = input.getString("stopOpenRelation");
        IDataset buildSelectJsonData = new DatasetList(input.getString("buildSelectJsonData"));

        if (IDataUtil.isNotEmpty(buildSelectJsonData))
        {
            String dealFlag = ((IData) buildSelectJsonData.get(0)).getString("col_DEAL_FLAG");
            if ("".equals(dealFlag))
            {

                dataset = formatDataset(buildSelectJsonData, stopOpenRelationFlag);
            }
            else
            {
                // begin 携号转网：携出欠费停机状态的用户和有携出欠费停机未完工工单的用户不允许进行停开机
                if ("0".equals(stopOpenRelationFlag))
                {
                    for (int i = 0; i < buildSelectJsonData.size(); i++)
                    {
                        checkSerialnumber(buildSelectJsonData.getData(i).getString("col_SERIAL_NUMBER", "").toString());
                    }
                }
                // end huanggs
                dataset = buildSelectJsonData;
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "客户经理短信停开机：业务异常！");
        }

        return genOtherSvcTradeData(dataset);
    }

}
