/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.agent;

import java.io.File;
import java.io.FileInputStream;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.AgentsException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.agent.AgentBankAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.DepartInfoQry;

public class AgentPayManageBean extends CSBizBean
{

    /**
     * 校验是否存在用户号码
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    private boolean checkAgentSn(String serialNumber) throws Exception
    {
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            return true;
        }
        return false;
    }

    /**
     * 校验导入数据格式
     * 
     * @param data
     * @param checkData
     * @throws Exception
     */
    private void checkImportAgentPayInfo(IData data, IData checkData) throws Exception
    {
        checkData.put("FLAG", false);
        if (IDataUtil.isEmpty(data))
        {
            checkData.put("IMPORT_ERROR", "记录行数据为空!");
            return;
        }

        String cityCode = data.getString("CITY_CODE", "").trim();
        String agentCode = data.getString("AGENT_CODE", "").trim();

        if (StringUtils.isBlank(cityCode))
        {
            checkData.put("IMPORT_ERROR", "分公司不能为空!");
            return;
        }

        if (cityCode.length() != 4)
        {
            checkData.put("IMPORT_ERROR", "分公司只能为4位!");
            return;
        }

        if (StringUtils.isBlank(agentCode))
        {
            checkData.put("IMPORT_ERROR", "代理商编码不能为空!");
            return;
        }

        if (agentCode.length() != 8)
        {
            checkData.put("IMPORT_ERROR", "代理商编码只能为8位!");
            return;
        }

        // 效验分公司和代理商是否相符
        if (!StringUtils.equals(cityCode, agentCode.substring(0, 4)))
        {
            checkData.put("IMPORT_ERROR", "您输入的分公司与代理商编码不符!");
            return;
        }

        // 效验手机号码是否有效
        String serialNumber = data.getString("SERIAL_NUMBER", "").trim();
        if (StringUtils.isBlank(serialNumber))
        {
            checkData.put("IMPORT_ERROR", "填写的手机号码不能为空!");
            return;
        }
        if (!checkAgentSn(serialNumber))
        {
            checkData.put("IMPORT_ERROR", "填写的手机号码无效!");
            return;
        }

        IDataset departs = DepartInfoQry.getAgentDepart(agentCode, "0");
        if (IDataUtil.isEmpty(departs))
        {
            checkData.put("IMPORT_ERROR", "填写的代理商编码不正确!");
            return;
        }
        String departId = departs.getData(0).getString("DEPART_ID");

        // 效验银行名称是否正确
        String bankName = data.getString("BANK_NAME", "").trim();
        if (StringUtils.isBlank(bankName))
        {
            checkData.put("IMPORT_ERROR", "填写的银行名称不能为空!");
            return;
        }

        IDataset bankDatas = queryAgentBankByName(bankName);
        if (IDataUtil.isEmpty(bankDatas))
        {
            data.put("IMPORT_ERROR", "填写的银行名称不正确!");
            return;
        }
        String bankCode = bankDatas.getData(0).getString("PARA_CODE1", "");
        if (StringUtils.isBlank(bankCode))
        {
            data.put("IMPORT_ERROR", "获取的银行代码不正确!");
            return;
        }

        // 判断回缴渠道中填写的是否是0或1
        String payChannel = data.getString("PAY_CHANNEL", "").trim();
        if (!StringUtils.equals(payChannel, "0") && !StringUtils.equals(payChannel, "1"))
        {
            checkData.put("IMPORT_ERROR", "回缴渠道只能填写0或1!");
            return;
        }
        
        String acctype = data.getString("RSRV_STR1", "").trim();
        if (!StringUtils.equals(acctype, "0") && !StringUtils.equals(acctype, "1"))
        {
            checkData.put("IMPORT_ERROR", "账户类型只能填写0或1!");
            return;
        }

        if ((!StringUtils.equals(payChannel, "403") || !StringUtils.equals(payChannel, "104")) && StringUtils.equals(payChannel, "1"))
        {
            checkData.put("IMPORT_ERROR", "不是中国邮政储蓄银行的，回缴渠道不能填写1[邮储]!");
            return;
        }

        // 效验代理商是否已经存在,true已经存在
        IDataset agentPays = AgentBankAcctInfoQry.queryAgentPayByAgentCode(agentCode, "0");
        if (IDataUtil.isNotEmpty(agentPays))
        {
            checkData.put("IMPORT_ERROR", "代理商编码已经存在!");
            return;
        }
        checkData.put("FLAG", true);
        checkData.put("BANK_CODE", bankCode);
        checkData.put("DEPART_ID", departId);
    }

    /**
     * 生成导入错误数据文件
     * 
     * @param data
     * @param checkData
     * @throws Exception
     */
    private String createImportErrDataFile(IDataset errDataset) throws Exception
    {
        String fileSerializeId = ImpExpUtil.getImpExpManager().getFileAction().createFileId();
        String fileName = "代理商银行账户信息导入错误数据(" + SysDateMgr.getSysDate() + ").xls";

        String cfgFile = "export/agent/AgentPayInfoExport.xml";
        // 生成失败的文件
        File File = ImpExpUtil.writeDataToFile("xls", new IDataset[]
        { errDataset }, "personserv", fileSerializeId, null, cfgFile);
        // 将导入失败的文件上传
        String errFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(File), "personserv", "upload/import", fileName);

        // ImpExpUtil.beginExport(fileSerializeId, null, fileName, new IDataset[]{errDataset}, cfgData);
        // 生成文件下载的URL
        return ImpExpUtil.getDownloadPath(errFileId, fileName);
    }

    /**
     * 删除代理商银行账户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData deleteAgentInfo(IData input) throws Exception
    {
        IData returnData = new DataMap();
        returnData.put("RESULT_CODE", 1);
        String agentId = input.getString("AGENT_ID", "");
        if (StringUtils.equals(agentId, ""))
        {
            return returnData;
        }
        String[] agents = agentId.split(",");

        for (int i = 0; i < agents.length; i++)
        {
            IData data = new DataMap();
            data.put("AGENT_ID", agentId);
            data.put("REMOVE_TAG", "1"); // 注销标识：0-正常、1-注销

            // 不进行物理删除，将END_DATE设置为系统当前时间。
            data.put("DEL_DATE", SysDateMgr.getSysTime());
            data.put("MODIFY_STAFF_ID", getVisit().getStaffId());
            data.put("MODIFY_STAFF_NAME", getVisit().getStaffEparchyName());

            Dao.delete("TF_F_AGENTBANK_ACCOUNTINFO", data);
        }

        returnData.put("RESULT_CODE", 0);
        return returnData;
    }

    /**
     * 获取某个代理商银行账户信息
     * 
     * @param input
     *            [AGENT_ID]
     * @return
     * @throws Exception
     */
    public IDataset getAgentPayInfo(IData input) throws Exception
    {
        return AgentBankAcctInfoQry.queryAgentPayByPk(input.getString("AGENT_ID"));
    }

    /**
     * 导入代理商银行账户数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData importAgentPayInfos(IData input) throws Exception
    {
        IData resultData = new DataMap();
        resultData.put("RESULT_CODE", 0);
        IDataset sucSet = new DatasetList();
        IDataset errSet = new DatasetList();
        String filePath = input.getString("FILE_PATH");
        int sucSize = 0;
        if (StringUtils.isNotBlank(filePath))
        {
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

            IData array = ImpExpUtil.beginImport(null, filePath, ExcelConfig.getSheets("import/agent/AgentPayInfoImport.xml"));
            IDataset[] suc = (IDataset[]) array.get("right"); // 解析成功的数据
            IDataset[] err = (IDataset[]) array.get("error"); // 解析失败的数据
            sucSet.addAll(suc[0]);
            if (IDataUtil.isNotEmpty(err[0]))
            {
                for (Object data : err[0])
                {
                    ((IData) data).put("IMPORT_ERROR", "解析错误数据");
                }
                errSet.addAll(err[0]);
            }
        }
        if (sucSet.size() > 1000)
        {
            CSAppException.apperr(AgentsException.CRM_AGENTS_24);
        }
        sucSize = sucSet.size();
        // 保存导入数据
        IDataset saveErrs = saveImprotInfos(sucSet); // 数据校验错误数据
        if (IDataUtil.isNotEmpty(saveErrs))
        {
            errSet.addAll(saveErrs);
            sucSize -= saveErrs.size();
        }
        if (IDataUtil.isNotEmpty(errSet))
        {
            String url = createImportErrDataFile(errSet);

            resultData.put("RESULT_CODE", 1);
            resultData.put("DOWNLOAD_URL", url);

            resultData.put("SUC_SIZE", sucSize);
            resultData.put("ERR_SIZE", errSet.size());
        }
        return resultData;
    }

    /**
     * 查询供应商业务区
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryAgentCityCode(IData input) throws Exception
    {
        String staffId = getVisit().getStaffId();
        String subStaffId = staffId.substring(0, 4);
        String eparchy = getVisit().getCityCode();
        if (StringUtils.equals(subStaffId, "HNSJ") || StringUtils.equals(subStaffId, "HNYD") || StringUtils.equals(subStaffId, "SUPE"))
        {
            eparchy = CSBizBean.getTradeEparchyCode();
        }
        return UAreaInfoQry.qryAeraLikeAreaFrame(eparchy);
    }

    /**
     * 根据银行代码查询银行
     * 
     * @param param
     * @return
     * @throws Exception
     */
    private IDataset queryAgentBankByCode(String bankCode) throws Exception
    {
        return CommparaInfoQry.getCommparaByCodeCode1("CSM", "9767", "AGENT_PAYFOR_BANK_INFO", bankCode);
    }

    /**
     * 根据银行名称查询银行
     * 
     * @param param
     * @return
     * @throws Exception
     */
    private IDataset queryAgentBankByName(String bankName) throws Exception
    {
        return CommparaInfoQry.getCommparaByCodeName("CSM", "9767", "AGENT_PAYFOR_BANK_INFO", bankName);
    }

    /**
     * 保存前台新增或修改的代理商银行账户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData saveAgentPayInfo(IData input) throws Exception
    {
        IData resultData = new DataMap();
        boolean ret = false;
        String agentId = input.getString("AGENT_ID", "");
        String removeTag = input.getString("REMOVE_TAG", "");
        String oldRemoveTag = input.getString("OLD_REMOVE_TAG", "");
        String agentCode = input.getString("AGENT_CODE");
        // 效验输入的手机号是否有效
        if (!checkAgentSn(input.getString("SERIAL_NUMBER", "")))
        {
            CSAppException.apperr(AgentsException.CRM_AGENTS_19);
        }

        // 效验输入的代理商编码是否有效
        IDataset departs = DepartInfoQry.getAgentDepart(agentCode, "0");
        if (IDataUtil.isEmpty(departs))
        {
            CSAppException.apperr(AgentsException.CRM_AGENTS_20);
        }
        input.put("DEPART_ID", departs.getData(0).getString("DEPART_ID"));

        // 效验银行代码
        IDataset agentBanks = queryAgentBankByCode(input.getString("BANK_CODE", ""));
        if (IDataUtil.isEmpty(agentBanks))
        {
            CSAppException.apperr(AgentsException.CRM_AGENTS_21);
        }
        input.put("BANK_NAME", agentBanks.getData(0).getString("PARAM_NAME"));

        String sysDate = SysDateMgr.getSysTime();
        String staffId = getVisit().getStaffId();
        String staffName = getVisit().getStaffName();
        // 效验代理商银行账户是否已经存在
        if (StringUtils.isNotBlank(agentId))
        {
            if (StringUtils.isNotBlank(oldRemoveTag) && StringUtils.isNotBlank(removeTag) && !StringUtils.equals(oldRemoveTag, removeTag) && StringUtils.equals(removeTag, "0"))
            {
                IDataset tmpSet = AgentBankAcctInfoQry.queryNotSelfAgentPay(agentId, agentCode, removeTag);
                if (IDataUtil.isNotEmpty(tmpSet))
                {
                    CSAppException.apperr(AgentsException.CRM_AGENTS_23, agentCode);
                }
            }

            input.put("MODIFY_DATE", sysDate);
            input.put("MODIFY_STAFF_ID", staffId);
            input.put("MODIFY_STAFF_NAME", staffName);
            ret = Dao.save("TF_F_AGENTBANK_ACCOUNTINFO", input, new String[]
            { "AGENT_ID" });
        }
        else
        {
            IDataset agentPays = AgentBankAcctInfoQry.queryAgentPayByAgentCode(agentCode, "0");
            if (IDataUtil.isNotEmpty(agentPays))
            {
                CSAppException.apperr(AgentsException.CRM_AGENTS_22);
            }
            input.put("REMOVE_TAG", "0");
            input.put("AGENT_ID", SeqMgr.getTradeId());
            input.put("ADD_DATE", sysDate);
            input.put("OPER_STAFF_ID", staffId);
            input.put("OPER_STAFF_NAME", staffName);
            ret = Dao.insert("TF_F_AGENTBANK_ACCOUNTINFO", input);
        }
        resultData.put("RESULT_CODE", ret ? "0" : "1");
        return resultData;
    }

    /**
     * 保存导入数据
     * 
     * @param agentPayInfos
     * @return
     * @throws Exception
     */
    private IDataset saveImprotInfos(IDataset agentPayInfos) throws Exception
    {
        String sysDate = SysDateMgr.getSysTime();
        String staffId = getVisit().getStaffId();
        String staffName = getVisit().getStaffName();
        IDataset faildDatas = new DatasetList();
        IData agentPayData = new DataMap();
        for (int i = 0; i < agentPayInfos.size(); i++)
        {
            agentPayData = agentPayInfos.getData(i);

            IData flagData = new DataMap();
            // 效验
            checkImportAgentPayInfo(agentPayData, flagData);
            if (!flagData.getBoolean("FLAG"))
            {
                agentPayData.put("IMPORT_ERROR", flagData.getString("IMPORT_ERROR"));
                faildDatas.add(agentPayData);
                continue;
            }
            agentPayData.put("BANK_CODE", flagData.getString("BANK_CODE", ""));
            agentPayData.put("DEPART_ID", flagData.getString("DEPART_ID", ""));
            agentPayData.put("REMOVE_TAG", "0");
            agentPayData.put("ADD_DATE", sysDate);
            agentPayData.put("OPER_STAFF_ID", staffId);
            agentPayData.put("OPER_STAFF_NAME", staffName);

            agentPayData.put("AGENT_ID", SeqMgr.getTradeId());

            if (!Dao.insert("TF_F_AGENTBANK_ACCOUNTINFO", agentPayData))
            {
                agentPayData.put("IMPORT_ERROR", "保存代理商信息错误");
                faildDatas.add(agentPayData);
            }
        }
        return faildDatas;
    }
}
