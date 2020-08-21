
package com.asiainfo.veris.crm.order.soa.person.common.query.financeboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class FinancialFeeUtil
{
    /**
     * 财务化BOSS校验
     * 
     * @param dataset
     * @throws Exception
     */
    public static void checkFinancialBoss(IDataset dataset) throws Exception
    {
        IData inparam = new DataMap();
        // 查询校验标记
        inparam.put("EPARCHY_CODE", "0898");
        inparam.put("TAG_CODE", "CSM_FINANCIAL_CHECK_TAG");
        inparam.put("SUBSYS_CODE", "CSM");
        inparam.put("USE_TAG", "0");
        IDataset checkTagSet = Dao.qryByCode("TD_S_TAG", "SEL_BY_TAGCODE", inparam, Route.CONN_CRM_CEN);
        if (checkTagSet != null && checkTagSet.size() > 0)
        {
            String financialCheckTag = checkTagSet.getData(0).getString("TAG_CHAR");
            if (!financialCheckTag.equals("0"))
            {
                return;
            }
        }

        String cancelTag = dataset.getData(0).getString("CANCEL_TAG", "0");
        String tradeId = dataset.getData(0).getString("TRADE_ID");
        inparam.clear();
        inparam.put("LOG_ID", "20" + tradeId);
        inparam.put("CANCEL_TAG", cancelTag);

        IDataset financialSet = Dao.qryByCode("TF_B_TRADE_BFAS_IN", "SEL_BY_LOGID", inparam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        for (int i = 0; i < financialSet.size(); i++)
        {
            int allCount = 0;
            IData financialData = financialSet.getData(i);
            String sysCode = financialData.getString("SYS_CODE");
            String operTypeCode = financialData.getString("OPER_TYPE_CODE");
            inparam.clear();
            inparam.put("SYS_CODE", sysCode);
            inparam.put("OPER_TYPE_CODE", operTypeCode);
            IDataset rulesSet = Dao.qryByCode("TD_S_ACCT_RULES", "SEL_BY_SYS_OPER", inparam);

            if (rulesSet.size() == 0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_1106);
            }

            for (int j = 0; j < rulesSet.size(); j++)
            {
                IData ruleData = rulesSet.getData(j);
                String ruleCheckTag = ruleData.getString("CHECK_TAG");
                if (!"1".equals(ruleCheckTag))
                {
                    return;
                }
                String checkTag = "0";
                String paraType = ruleData.getString("PARA_TYPE");
                String feeTypeCode = ruleData.getString("FEE_TYPE_CODE");

                if ("0".equals(paraType))
                {// 校验费用
                    inparam.clear();
                    inparam.put("SYS_CODE", sysCode);
                    inparam.put("SUB_LOG_ID", tradeId);
                    inparam.put("OPER_TYPE_CODE", operTypeCode);
                    inparam.put("FEE_TYPE_CODE", feeTypeCode);
                    inparam.put("CANCEL_TAG", cancelTag);
                    IDataset financialCheckSet = Dao.qryByCode("TF_B_TRADE_BFAS_IN", "SEL_BY_SUB_FEE", inparam);
                    for (int a = 0; a < financialCheckSet.size(); a++)
                    {
                        allCount++;
                        IData financialCheckData = financialCheckSet.getData(a);
                        inparam.clear();
                        inparam.put("SYS_CODE", sysCode);
                        inparam.put("FEE_TYPE_CODE", feeTypeCode);
                        inparam.put("OPER_TYPE_CODE", operTypeCode);
                        IDataset ruleCheckSet = Dao.qryByCode("TD_S_ACCT_RULES_CHECK", "SEL_BY_FEETYPE", inparam);

                        if (ruleCheckSet.size() <= 0)
                        {
                            CSAppException.apperr(CrmCommException.CRM_COMM_1107, feeTypeCode);
                        }

                        for (int b = 0; b < ruleCheckSet.size(); b++)
                        {
                            // 对表中数据进行规则校验
                            IData ruleCheckData = ruleCheckSet.getData(b);
                            checkTag = "0";
                            String columnName = ruleCheckData.getString("COLUMN_NAME");
                            String columnValue = ruleCheckData.getString("COLUMN_VALUE");

                            if (!"".equals(columnName))
                            {
                                String checkInfo = financialCheckData.getString(columnName);// 根据列名获取值
                                String values[] = StringUtils.split(columnValue, "|");
                                for (int c = 0; c < values.length; c++)
                                {

                                    if (values[c].equals(checkInfo))
                                    {// 匹配值是否正确
                                        checkTag = "1";
                                        break;
                                    }
                                }
                                if (checkTag.equals("0"))
                                {
                                    CSAppException.apperr(CrmCommException.CRM_COMM_1108, columnName);
                                }
                            }
                        }
                    }
                }
            }
            if (allCount < 1)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_1109);
            }
        }
    }

    /**
     * Java财务完工处理 插表TF_A_BFAS_IN
     * 
     * @param bfasinDataset
     * @throws Exception
     */
    public static void insertBfasIn(IData bfasinData) throws Exception
    {
        int iResult = Dao.executeUpdateByCodeCode("TF_A_BFAS_IN", "INS_BY_TRADEBFASIN", bfasinData);
        if (iResult <= 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1104);
        }
    }

    /**
     * Java资源完工处理 插表TF_R_RESOURCE_INTER
     * 
     * @param resinterData
     * @throws Exception
     */
    public static void insertResInter(IData resinterData) throws Exception
    {
        int iResult = Dao.executeUpdateByCodeCode("TF_R_RESOURCE_INTER", "INS_BY_TRADERESOURCE", resinterData);
        if (iResult <= 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1105);
        }
    }

    /**
     * 财务费用接口日志表 插表TF_B_TRADE_BFAS_IN
     * 
     * @param bfasinDataset
     * @throws Exception
     */
    public static void insertTradeBfasIn(IDataset bfasinDataset) throws Exception
    {
        String sysDate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT);
        IDataset bfaslist = new DatasetList();
        if (bfasinDataset != null && bfasinDataset.size() > 0)
        {
            for (int i = 0; i < bfasinDataset.size(); i++)
            {
                IData param = new DataMap();
                IData bfasinData = bfasinDataset.getData(i);
                param.put("BFAS_ID", SeqMgr.getCrmBfasId());// BFAS_ID 流水号
                param.put("SYS_CODE", "BUSNS");// SYS_CODE功能模块编码
                param.put("LOG_ID", "20" + bfasinData.getString("TRADE_ID"));// 记账凭证号
                param.put("SUB_LOG_ID", bfasinData.getString("TRADE_ID"));// 系统业务流水
                param.put("PARTITION_ID", StrUtil.getAcceptMonthById(bfasinData.getString("TRADE_ID")));// 分区标示
                param.put("EPARCHY_CODE", bfasinData.getString("EPARCHY_CODE"));// 地市编码 pd.getData()->td
                param.put("CITY_CODE", bfasinData.getString("CITY_CODE"));// 业务区编码
                param.put("PROFIT_CEN_ID", bfasinData.getString("PROFIT_CEN_ID", ""));// 利润中心编码
                param.put("DEPART_ID", bfasinData.getString("DEPART_ID"));// 部门编码
                param.put("AGENT_CODE", bfasinData.getString("AGENT_CODE"));// 代理商编码
                param.put("OPER_STAFF_ID", bfasinData.getString("OPER_STAFF_ID"));// 员工编码
                param.put("OPER_TYPE_CODE", bfasinData.getString("OPER_TYPE_CODE"));// 系统业务类型
                param.put("SALE_TYPE_CODE", bfasinData.getString("SALE_TYPE_CODE", ""));// 销售类型
                param.put("PAY_MONEY_CODE", bfasinData.getString("PAY_MONEY_CODE", ""));// 收款方式
                param.put("CAMPN_ID", bfasinData.getString("CAMPN_ID", ""));// 营销案编码
                param.put("FEE_TYPE_CODE", bfasinData.getString("FEE_TYPE_CODE", ""));// 费用类型
                param.put("FEE_ITEM_TYPE_CODE", bfasinData.getString("FEE_ITEM_TYPE_CODE", ""));// 费用明细类型
                param.put("PAY_MODE_CODE", bfasinData.getString("PAY_MODE_CODE", ""));// 账户类别
                param.put("ACCT_ID", bfasinData.getString("ACCT_ID", ""));// 账户账户/代扣号
                param.put("COLL_AGEN_CODE", bfasinData.getString("COLL_AGEN_CODE", ""));// 代收机构编码/银行编码
                param.put("LOGOUT_TAG", bfasinData.getString("LOGOUT_TAG", ""));// 核销标记
                param.put("IN_MODE_CODE", bfasinData.getString("IN_MODE_CODE"));// 接入方式td.getInModeCode()
                param.put("NET_TYPE_CODE", bfasinData.getString("NET_TYPE_CODE"));// 网别
                param.put("BRAND_CODE", bfasinData.getString("BRAND_CODE", ""));// 用户品牌
                param.put("PRODUCT_ID", bfasinData.getString("PRODUCT_ID", ""));// 用户产品
                param.put("USER_TYPE_CODE", bfasinData.getString("USER_TYPE_CODE", ""));// 用户类型
                param.put("CHECK_NUMBER", bfasinData.getString("CHECK_NUMBER", ""));// 支票号
                param.put("RES_TYPE_CODE", bfasinData.getString("RES_TYPE_CODE", ""));// 资源类型
                param.put("RES_KIND_CODE", bfasinData.getString("RES_KIND_CODE", ""));// 资源种类
                param.put("CAPACITY_TYPE_CODE", bfasinData.getString("CAPACITY_TYPE_CODE", ""));// 面值/容量
                param.put("DEVICE_TYPE_CODE", bfasinData.getString("DEVICE_TYPE_CODE", ""));// 终端类型
                param.put("DEVICE_MODEL_CODE", bfasinData.getString("DEVICE_MODEL_CODE", ""));// 终端型号
                param.put("DEVICE_COLOR_CODE", bfasinData.getString("DEVICE_COLOR_CODE", ""));// 终端颜色
                param.put("ORDER_ID", bfasinData.getString("ORDER_ID", ""));// 采购订单
                param.put("DEVICE_PRODUCT", bfasinData.getString("DEVICE_PRODUCT", ""));// 终端品牌
                param.put("SUPPLY_TYPE", bfasinData.getString("SUPPLY_TYPE", ""));// 供货类型
                param.put("PROCUREMENT_TYPE", bfasinData.getString("PROCUREMENT_TYPE", ""));// 采购类型/方式
                param.put("AGENCY_ID", bfasinData.getString("AGENCY_ID", ""));// 供应商编码
                param.put("INCOME_CODE", bfasinData.getString("INCOME_CODE", ""));// 收入项目
                param.put("RECE_FEE", bfasinData.getString("RECE_FEE", ""));// 应收金额
                param.put("FEE", bfasinData.getString("FEE", ""));// 实收金额
                param.put("PRESENT_FEE", bfasinData.getString("PRESENT_FEE", ""));// 促销赠送金额
                param.put("FORM_FEE", bfasinData.getString("FORM_FEE", ""));// 代收手续费/佣金
                param.put("SCORE", bfasinData.getString("SCORE", ""));// 兑换积分
                param.put("ACC_DATE", bfasinData.getString("ACC_DATE", ""));// 会计日期
                param.put("OPER_DATE", bfasinData.getString("OPER_DATE", ""));// 交易日期
                param.put("CANCEL_SUB_LOG_ID", bfasinData.getString("CANCEL_SUB_LOG_ID", ""));// 业务返销原流水
                param.put("CANCEL_DATE", bfasinData.getString("CANCEL_DATE", ""));// 返销日期
                param.put("CANCEL_TAG", bfasinData.getString("CANCEL_TAG", "0"));// 返销标记 0 正业务 1返销业务
                param.put("DEPOSIT_BEGIN_DATE", bfasinData.getString("DEPOSIT_BEGIN_DATE", ""));// 押金开始有效期
                param.put("DEPOSIT_END_DATE", bfasinData.getString("DEPOSIT_END_DATE", "")); // 押金终止有效期
                param.put("PROC_TAG", bfasinData.getString("PROC_TAG", "")); // 0 未处理 1正在拆分 3拆分成功 4错单
                param.put("RSRV_TAG1", bfasinData.getString("RSRV_TAG1", ""));
                param.put("RSRV_TAG2", bfasinData.getString("RSRV_TAG2", ""));
                param.put("RSRV_TAG3", bfasinData.getString("RSRV_TAG3", ""));
                param.put("RSRV_STR1", bfasinData.getString("RSRV_STR1", ""));
                param.put("RSRV_STR2", bfasinData.getString("RSRV_STR2", ""));
                param.put("RSRV_STR3", bfasinData.getString("RSRV_STR3", ""));
                param.put("RSRV_STR4", bfasinData.getString("RSRV_STR4", ""));
                param.put("RSRV_STR5", bfasinData.getString("RSRV_STR5", ""));
                param.put("RSRV_STR6", bfasinData.getString("RSRV_STR6", ""));
                param.put("RSRV_STR7", bfasinData.getString("RSRV_STR7", ""));
                param.put("RSRV_NUM1", bfasinData.getString("RSRV_NUM1", "")); // 填写用户的USER_ID
                param.put("RSRV_NUM2", bfasinData.getString("RSRV_NUM2", ""));
                param.put("RSRV_NUM3", bfasinData.getString("RSRV_NUM3", ""));
                param.put("RSRV_NUM4", bfasinData.getString("RSRV_NUM4", ""));
                param.put("RSRV_NUM5", bfasinData.getString("RSRV_NUM5", ""));
                param.put("RSRV_DATE1", bfasinData.getString("RSRV_DATE1", ""));
                param.put("RSRV_DATE2", bfasinData.getString("RSRV_DATE2", ""));
                param.put("RSRV_DATE3", bfasinData.getString("RSRV_DATE3", ""));
                
                bfaslist.add(param);
            }
        }
        Dao.insert("TF_B_TRADE_BFAS_IN", bfaslist, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
        // 财务化BOSS校验
        checkFinancialBoss(bfasinDataset);
    }

    /**
     * 财务BOSS资源出入库统一接口子台账表 插表TF_B_TRADE_RESOURCE_INTER
     * 
     * @author YYZ
     * @param pd
     * @param td
     * @param resinterDataset
     * @throws Exception
     */
    public static void insertTradeResInter(IDataset resinterDataset) throws Exception
    {
        IDataset bfaslist = new DatasetList();
        if (resinterDataset != null && resinterDataset.size() > 0)
        {
            for (int i = 0; i < resinterDataset.size(); i++)
            {
                IData param = new DataMap();
                IData resinterData = resinterDataset.getData(i);
                param.put("LOG_ID", "20" + resinterData.getString("TRADE_ID"));// 会计凭证号,同一笔业务多种费用和借贷均为相同会计凭证号,CRM个人业务为’20’＋原业务流水，资源管理为’30’+原业务流水
                param.put("SUB_LOG_ID", resinterData.getString("TRADE_ID"));// 业务子流水,crm为trade_id,资源为log_id
                param.put("PARTITION_ID", StrUtil.getAcceptMonthById(resinterData.getString("TRADE_ID")));// 系统分区
                param.put("INVENT_ORG_ID", "HIC");// 库存组织编码
                param.put("EPARCHY_CODE", resinterData.getString("EPARCHY_CODE"));// 操作地州编码
                param.put("CITY_CODE", resinterData.getString("CITY_CODE"));// 业务区编码
                param.put("OPER_DEPART_ID", resinterData.getString("OPER_DEPART_ID"));// 部门编码
                param.put("OPER_STAFF_ID", resinterData.getString("OPER_STAFF_ID"));// 员工编码
                param.put("STOCK_ID_O", resinterData.getString("STOCK_ID_O", ""));// 原库存编码
                param.put("STOCK_ID_N", resinterData.getString("STOCK_ID_N", ""));// 目标库存编码
                /* 操作标识: 1：入库（入到中心库） 2：调拨 3：分配 4：下发 5：回收 6：报废出库 7：回退 8：销售和销售返销 9: 销售回收 */
                param.put("OPER_TAG", resinterData.getString("OPER_TAG", ""));
                param.put("COST_CEN_ID", resinterData.getString("COST_CEN_ID", ""));// 成本中心代码
                param.put("OPER_TIME", resinterData.getString("OPER_TIME", ""));// 操作时间
                param.put("ASSIGN_TIME", resinterData.getString("ASSIGN_TIME", ""));// 记帐日期
                param.put("RES_TYPE_CODE", resinterData.getString("RES_TYPE_CODE", ""));// 资源类型
                param.put("RES_KIND_CODE", resinterData.getString("RES_KIND_CODE", ""));// 资源种类
                param.put("CAPACITY_TYPE_CODE", resinterData.getString("CAPACITY_TYPE_CODE", ""));// 资源容量（面值）
                param.put("DEVICE_TYPE_CODE", resinterData.getString("DEVICE_TYPE_CODE", ""));// 终端类型
                param.put("DEVICE_FACTORY_CODE", resinterData.getString("DEVICE_FACTORY_CODE", ""));// 终端制造商
                param.put("DEVICE_MODEL_CODE", resinterData.getString("DEVICE_MODEL_CODE", ""));// 终端型号
                param.put("DEVICE_COLOR_CODE", resinterData.getString("DEVICE_COLOR_CODE", ""));// 终端颜色
                param.put("ORDER_ID", resinterData.getString("ORDER_ID", ""));// 采购订单
                param.put("SUPPLY_ID", resinterData.getString("SUPPLY_ID", ""));// 供货单号
                param.put("SUPPLY_TYPE", resinterData.getString("SUPPLY_TYPE", ""));// 终端来源
                param.put("AGENCY_ID", resinterData.getString("AGENCY_ID", ""));// 供应商编码
                param.put("PROCUREMENT_TYPE", resinterData.getString("PROCUREMENT_TYPE", ""));// 采购类型/方式
                param.put("RES_STATE", resinterData.getString("RES_STATE", ""));// 资源状态
                param.put("OPER_TYPE_CODE", resinterData.getString("OPER_TYPE_CODE"));// 系统业务类型
                param.put("CAMPN_ID", resinterData.getString("CAMPN_ID", ""));// 营销方案
                param.put("PRODUCT_ID", resinterData.getString("PRODUCT_ID", ""));// 产品编码
                param.put("SALE_TYPE_CODE", resinterData.getString("SALE_TYPE_CODE", ""));// 销售类型
                param.put("OPER_NUM", resinterData.getString("OPER_NUM", ""));// 数量
                param.put("DC_TAG", resinterData.getString("DC_TAG", "D"));// 借贷标记
                param.put("CANCEL_SUB_LOG_ID", resinterData.getString("CANCEL_SUB_LOG_ID", ""));// 业务返销原流水
                param.put("CANCEL_DATE", resinterData.getString("CANCEL_DATE", ""));// 返销日期
                param.put("REMARK", resinterData.getString("REMARK", ""));// 备注
                param.put("RSRV_TAG1", resinterData.getString("RSRV_TAG1", ""));
                param.put("RSRV_TAG2", resinterData.getString("RSRV_TAG2", ""));
                param.put("RSRV_TAG3", resinterData.getString("RSRV_TAG3", ""));
                param.put("RSRV_DATE1", resinterData.getString("RSRV_DATE1", ""));
                param.put("RSRV_DATE2", resinterData.getString("RSRV_DATE2", ""));
                param.put("RSRV_DATE3", resinterData.getString("RSRV_DATE3", ""));
                param.put("RSRV_STR1", resinterData.getString("RSRV_STR1", ""));
                param.put("RSRV_STR2", resinterData.getString("RSRV_STR2", ""));
                param.put("RSRV_STR3", resinterData.getString("RSRV_STR3", ""));
                param.put("RSRV_STR4", resinterData.getString("RSRV_STR4", ""));
                param.put("RSRV_STR5", resinterData.getString("RSRV_STR5", ""));
                param.put("RSRV_STR6", resinterData.getString("RSRV_STR6", ""));
                param.put("RSRV_STR7", resinterData.getString("RSRV_STR7", ""));
                param.put("RSRV_NUM1", resinterData.getString("RSRV_NUM1", ""));
                param.put("RSRV_NUM2", resinterData.getString("RSRV_NUM2", ""));
                param.put("RSRV_NUM3", resinterData.getString("RSRV_NUM3", ""));
                bfaslist.add(param);
            }
        }
        Dao.insert("TF_B_TRADE_RESOURCE_INTER", bfaslist);
    }

}
