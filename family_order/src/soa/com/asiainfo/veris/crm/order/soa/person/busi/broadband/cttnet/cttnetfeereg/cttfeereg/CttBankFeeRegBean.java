
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetfeereg.cttfeereg;

import java.text.DecimalFormat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.fee.FeeRegQry;

public class CttBankFeeRegBean extends CSBizBean
{
    public IDataset queryBankFeeRegs(IData param) throws Exception
    {
        IDataset feeRegType = FeeRegQry.queryBankFeeRegs();// 查询费用登记类型
        int feeRegTypeSize = feeRegType.size();
        if (feeRegTypeSize == 0)
        {
            return new DatasetList();
        }
        IDataset feeReg = FeeRegQry.queryFeeRegsByLogId(param.getString("LOG_ID"));// 查询费用登记
        int feeRegSize = feeReg.size();
        IDataset returnData = new DatasetList();
        IData temp = null;
        IData feeTemp = null;
        IData feeTypeTemp = null;
        for (int i = 0; i < feeRegTypeSize; i++)
        {
            feeTypeTemp = feeRegType.getData(i);
            temp = new DataMap();
            temp.put("FEE_TYPE_CODE", feeTypeTemp.getString("FEE_TYPE_CODE"));
            temp.put("FEE_TYPE", feeTypeTemp.getString("FEE_TYPE"));
            temp.put("RATE", feeTypeTemp.getString("RATE"));
            temp.put("TYPE", feeTypeTemp.getString("TYPE"));
            if ("-1".equals(param.getString("LOG_ID")))
            {
                temp.put("FEE_MONEY", 0);
                temp.put("MODI_FLAG", 0);
                temp.put("LOG_ID", "-1");
            }
            else
            {
                for (int j = 0; j < feeRegSize; j++)
                {
                    feeTemp = feeReg.getData(j);
                    if (feeTemp.getString("FEE_TYPE_CODE").equals(feeTypeTemp.getString("FEE_TYPE_CODE")))
                    {
                        int feeMoney = feeTemp.getInt("FEE_MONEY", 0);
                        String modiFlag = feeTemp.getString("FEE_TYPE_CODE", "-1");
                        if ("-1".equals(modiFlag))
                        {
                            modiFlag = "0";
                        }
                        else
                        {
                            modiFlag = "1";
                        }
                        temp.put("FEE_MONEY", feeMoney);
                        temp.put("MODI_FLAG", modiFlag);
                        temp.put("LOG_ID", feeTemp.getString("LOG_ID"));
                    }
                }
            }
            returnData.add(temp);
        }
        return returnData;
    }

    public IDataset queryBankFeeRegTotle(IData param) throws Exception
    {

        return FeeRegQry.queryBankFeeRegTotle(param.getString("LOG_ID"));
    }

    /**
     * 保存费用登记数据
     * 
     * @param data
     * @param data
     * @throws Exception
     */
    public String saveBankFeeReg(IData data) throws Exception
    {
        DecimalFormat df = new DecimalFormat("0");
        String logId = data.getString("LOG_ID", "-1");
        String a = data.getString("feeDataset");
        IDataset feeDataset = new DatasetList(a);
        if (IDataUtil.isEmpty(feeDataset))
        {
            return logId;
        }
        if ("-1".equals(logId))
        {
            logId = SeqMgr.getFeeRegLogId();
        }

        String regDate = data.getString("REG_DATE");
        String regStaffId = data.getString("REG_STAFF_ID");
        String regDepartId = data.getString("REG_DEPART_ID");
        String updateTime = SysDateMgr.getSysDate();
        String updateStaffId = getVisit().getStaffId();
        String updateDepartId = getVisit().getDepartId();
        String regFlag = data.getString("REG_FLAG");

        IDataset dataset = new DatasetList();
        IDataset dataset2 = new DatasetList();

        // 费用子项
        for (int i = 0; i < feeDataset.size(); i++)
        {
            String feeTypeCode = feeDataset.getData(i).getString("FEE_TYPE_CODE");
            String feeMoney = feeDataset.getData(i).getString("FEE_MONEY");
            if (StringUtils.isBlank(feeMoney))
            {
                continue;
            }
            String modiFlag = feeDataset.getData(i).getString("MODI_FLAG");// 0-新增 1-修改

            // 如果是新增费用，并且有费用录入，则执行新增操作
            if ((StringUtils.isBlank(modiFlag) || "0".equals(modiFlag)) && !"0".equals(df.format(Double.valueOf(feeMoney) * 100)))
            {
                IData temp = new DataMap();
                temp.put("LOG_ID", logId);
                temp.put("FEE_TYPE_CODE", feeTypeCode);
                temp.put("FEE_MONEY", df.format(Double.valueOf(feeMoney) * 100));
                temp.put("REG_DATE", regDate);
                temp.put("REG_STAFF_ID", regStaffId);
                temp.put("REG_DEPART_ID", regDepartId);
                temp.put("STATE", "0"); // 0-保存
                temp.put("UPDATE_TIME", updateTime);
                temp.put("UPDATE_STAFF_ID", updateStaffId);
                temp.put("UPDATE_DEPART_ID", updateDepartId);
                temp.put("RSRV_STR1", data.getString("RSRV_STR1")); // 上级银行编码
                temp.put("RSRV_STR2", data.getString("RSRV_STR2")); // 银行编码
                temp.put("RSRV_TAG1", data.getString("RSRV_TAG1")); // 缴款方式
                temp.put("REMARK", data.getString("REMARK"));
                dataset.add(temp);
            }
            // 如果是修改数据，则执行修改操作
            else if ("1".equals(modiFlag))
            {
                IData temp2 = new DataMap();
                temp2.put("LOG_ID", logId);
                temp2.put("FEE_TYPE_CODE", feeTypeCode);
                temp2.put("FEE_MONEY", df.format(Double.valueOf(feeMoney) * 100));
                temp2.put("REG_DATE", regDate);
                temp2.put("REG_STAFF_ID", regStaffId);
                temp2.put("REG_DEPART_ID", regDepartId);
                temp2.put("STATE", "0"); // 0-保存
                temp2.put("UPDATE_TIME", updateTime);
                temp2.put("UPDATE_STAFF_ID", updateStaffId);
                temp2.put("UPDATE_DEPART_ID", updateDepartId);
                temp2.put("RSRV_STR1", data.getString("RSRV_STR1")); // 上级银行编码
                temp2.put("RSRV_STR2", data.getString("RSRV_STR2")); // 银行编码
                temp2.put("RSRV_TAG1", data.getString("RSRV_TAG1")); // 缴款方式
                temp2.put("REMARK", data.getString("REMARK"));
                dataset2.add(temp2);
            }
        }

        // 合计金额
        IData temp3 = new DataMap();
        temp3.put("LOG_ID", logId);
        temp3.put("FEE_TYPE_CODE", "T5"); // 上缴款合计费用
        temp3.put("FEE_MONEY", df.format(Double.valueOf(data.getString("feeTotle")) * 100));
        temp3.put("REG_DATE", regDate);
        temp3.put("REG_STAFF_ID", regStaffId);
        temp3.put("REG_DEPART_ID", regDepartId);
        temp3.put("STATE", "0"); // 0-保存
        temp3.put("UPDATE_TIME", updateTime);
        temp3.put("UPDATE_STAFF_ID", updateStaffId);
        temp3.put("UPDATE_DEPART_ID", updateDepartId);
        temp3.put("RSRV_STR1", data.getString("RSRV_STR1")); // 上级银行编码
        temp3.put("RSRV_STR2", data.getString("RSRV_STR2")); // 银行编码
        temp3.put("RSRV_TAG1", data.getString("RSRV_TAG1")); // 缴款方式
        temp3.put("REMARK", data.getString("REMARK"));
        if ("-1".equals(data.getString("LOG_ID", "-1")))
        {
            dataset.add(temp3);
        }
        else
        {
            dataset2.add(temp3);
        }

        // 新增
        if (dataset.size() > 0)
        {
            Dao.executeBatchByCodeCode("TF_F_FEEREG", "INS_BANKFEEREG", dataset);
        }

        // 修改
        if (dataset2.size() > 0)
        {
            Dao.executeBatchByCodeCode("TF_F_FEEREG", "UPD_BANKFEEREG", dataset2);
        }

        // 如果是”提交“操作
        if ("1".equals(regFlag))
        {
            IData temp4 = new DataMap();
            temp4.put("LOG_ID", logId);
            temp4.put("STATE", "1"); // 1-提交
            temp4.put("RSRV_STR1", data.getString("RSRV_STR1")); // 上级银行编码
            temp4.put("RSRV_STR2", data.getString("RSRV_STR2")); // 银行编码
            temp4.put("RSRV_TAG1", data.getString("RSRV_TAG1")); // 缴款方式

            Dao.executeUpdateByCodeCode("TF_F_FEEREG", "UPD_BANKFEEREG2", temp4);
        }

        return logId;
    }

    /**
     * 保存费用登记数据
     * 
     * @param data
     * @param data
     * @throws Exception
     */
    public String saveBankFeeRegM(IData data) throws Exception
    {
        DecimalFormat df = new DecimalFormat("0");
        String logId = data.getString("LOG_ID", "-1");
        String a = data.getString("feeDataset");
        IDataset feeDataset = new DatasetList(a);
        if (IDataUtil.isEmpty(feeDataset))
        {
            return logId;
        }
        if ("-1".equals(logId))
        {
            logId = SeqMgr.getFeeRegLogId();
        }

        String regDate = data.getString("REG_DATE");
        String regStaffId = data.getString("REG_STAFF_ID");
        String regDepartId = data.getString("REG_DEPART_ID");
        String updateTime = SysDateMgr.getSysDate();
        String updateStaffId = getVisit().getStaffId();
        String updateDepartId = getVisit().getDepartId();
        String regFlag = data.getString("REG_FLAG");

        IDataset dataset = new DatasetList();
        IDataset dataset2 = new DatasetList();

        // 费用子项
        for (int i = 0; i < feeDataset.size(); i++)
        {
            String feeTypeCode = feeDataset.getData(i).getString("FEE_TYPE_CODE");
            String feeMoney = feeDataset.getData(i).getString("FEE_MONEY");
            if (StringUtils.isBlank(feeMoney))
            {
                continue;
            }
            String modiFlag = feeDataset.getData(i).getString("MODI_FLAG");// 0-新增 1-修改

            // 如果是新增费用，并且有费用录入，则执行新增操作
            if ((StringUtils.isBlank(modiFlag) || "0".equals(modiFlag)) && !"0".equals(df.format(Double.valueOf(feeMoney) * 100)))
            {
                IData temp = new DataMap();
                temp.put("LOG_ID", logId);
                temp.put("FEE_TYPE_CODE", feeTypeCode);
                temp.put("FEE_MONEY", df.format(Double.valueOf(feeMoney) * 100));
                temp.put("REG_DATE", regDate);
                temp.put("REG_STAFF_ID", regStaffId);
                temp.put("REG_DEPART_ID", regDepartId);
                temp.put("STATE", "1");
                temp.put("UPDATE_TIME", updateTime);
                temp.put("UPDATE_STAFF_ID", updateStaffId);
                temp.put("UPDATE_DEPART_ID", updateDepartId);
                temp.put("RSRV_STR1", data.getString("RSRV_STR1")); // 上级银行编码
                temp.put("RSRV_STR2", data.getString("RSRV_STR2")); // 银行编码
                temp.put("RSRV_TAG1", data.getString("RSRV_TAG1")); // 缴款方式
                temp.put("REMARK", data.getString("REMARK"));
                dataset.add(temp);
            }
            // 如果是修改数据，则执行修改操作
            else if ("1".equals(modiFlag))
            {
                IData temp2 = new DataMap();
                temp2.put("LOG_ID", logId);
                temp2.put("FEE_TYPE_CODE", feeTypeCode);
                temp2.put("FEE_MONEY", df.format(Double.valueOf(feeMoney) * 100));
                temp2.put("REG_DATE", regDate);
                temp2.put("REG_STAFF_ID", regStaffId);
                temp2.put("REG_DEPART_ID", regDepartId);
                temp2.put("STATE", "1");
                temp2.put("UPDATE_TIME", updateTime);
                temp2.put("UPDATE_STAFF_ID", updateStaffId);
                temp2.put("UPDATE_DEPART_ID", updateDepartId);
                temp2.put("RSRV_STR1", data.getString("RSRV_STR1")); // 上级银行编码
                temp2.put("RSRV_STR2", data.getString("RSRV_STR2")); // 银行编码
                temp2.put("RSRV_TAG1", data.getString("RSRV_TAG1")); // 缴款方式
                temp2.put("REMARK", data.getString("REMARK"));
                dataset2.add(temp2);
            }
        }

        // 合计金额
        IData temp3 = new DataMap();
        temp3.put("LOG_ID", logId);
        temp3.put("FEE_TYPE_CODE", "T5"); // 上缴款合计费用
        temp3.put("FEE_MONEY", df.format(Double.valueOf(data.getString("feeTotle")) * 100));
        temp3.put("REG_DATE", regDate);
        temp3.put("REG_STAFF_ID", regStaffId);
        temp3.put("REG_DEPART_ID", regDepartId);
        temp3.put("STATE", "1");
        temp3.put("UPDATE_TIME", updateTime);
        temp3.put("UPDATE_STAFF_ID", updateStaffId);
        temp3.put("UPDATE_DEPART_ID", updateDepartId);
        temp3.put("RSRV_STR1", data.getString("RSRV_STR1")); // 上级银行编码
        temp3.put("RSRV_STR2", data.getString("RSRV_STR2")); // 银行编码
        temp3.put("RSRV_TAG1", data.getString("RSRV_TAG1")); // 缴款方式
        temp3.put("REMARK", data.getString("REMARK"));
        if ("-1".equals(data.getString("LOG_ID", "-1")))
        {
            dataset.add(temp3);
        }
        else
        {
            dataset2.add(temp3);
        }

        // 新增
        if (dataset.size() > 0)
        {
            Dao.executeBatchByCodeCode("TF_F_FEEREG", "INS_BANKFEEREG", dataset);
        }

        // 修改
        if (dataset2.size() > 0)
        {
            Dao.executeBatchByCodeCode("TF_F_FEEREG", "UPD_BANKFEEREG", dataset2);
        }

        return logId;
    }
}
