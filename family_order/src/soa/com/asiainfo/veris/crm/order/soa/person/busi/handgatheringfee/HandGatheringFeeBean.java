
package com.asiainfo.veris.crm.order.soa.person.busi.handgatheringfee;

//import org.apache.log4j.Logger;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.TaxCalcUtils;
import com.asiainfo.veris.crm.order.pub.util.TaxUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;
import com.asiainfo.veris.crm.order.soa.person.common.query.financeboss.FinancialFeeUtil;

public class HandGatheringFeeBean extends CSBizBean
{
	//protected static Logger log = Logger.getLogger(HandGatheringFeeBean.class);
    /**
     * 登记手工收款补录
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData handGatheringFee(IData input) throws Exception
    {
        IData outData = new DataMap();
        boolean ygztag = false;
        if (TaxUtils.isYgzTag())
        {
            ygztag = true;
            String feetypecode = input.getString("FEE_NAME");
            IDataset list = CommparaInfoQry.getCommparaInfoByCode("CSM", "3011", "HAND_GATHER_FEE_NAME", feetypecode, "ZZZZ");

            if (null != list && list.size() > 0)
            {
                IData taxInfo = list.getData(0);
                input.put("TYPE", taxInfo.getString("PARA_CODE2")); // 计税方式
                input.put("RATE", taxInfo.getString("PARA_CODE3")); // 税率
                input.put("RSRV_NUM1", taxInfo.getString("PARA_CODE4"));// 混合税率
                input.put("FACT_PAY_FEE", input.getString("FEE_AMOUNT")); // 实收
                input.put("FEE", input.getString("FEE_AMOUNT"));// 应收

            }
            else
            {
                // error("税率配置错误，请联系管理员!");
                CSAppException.apperr(CrmCommException.CRM_COMM_1111);
            }

            IDataset feelist = new DatasetList();
            feelist.add(input);
            // 计税
            TaxCalcUtils.getTradeFeeTaxForCalculate(feelist);

            input = feelist.getData(0);
        }
        String eparchCode = CSBizBean.getTradeEparchyCode();
        String tradeId = SeqMgr.getTradeId();
        // 存储过程参数名
        String[] procParamName = new String[]
        { "v_tradeeparchycode", "v_tradecitycode", "v_tradedepartid", "v_tradestaffid", "v_para_code2", "v_para_code3", "v_para_code4", "v_para_code5", "v_para_code6", "v_para_code7", "v_para_code8", "v_para_code9", "v_para_code10", "v_para_code11",
                "v_para_code12", "v_para_code13", "v_para_code14", "v_para_code15", "v_para_code16", "v_para_code17", "v_para_code18", "v_para_code19", "v_para_code20", "v_para_code21", "v_para_code22", "v_para_code23", "v_para_code24",
                "v_para_code25", "v_para_code26", "v_para_code27", "v_para_code28", "v_para_code29", "v_para_code30", "v_resultcode", "v_resulterrinfo" };
        String feeName = StaticUtil.getStaticValue("HAND_GATHER_FEE_NAME", input.getString("FEE_NAME"));
        String feeAmount = input.getString("FEE_AMOUNT");
        String feeReason = input.getString("FEE_REASON");
        // 存储过程传入参数
        IData procData = new DataMap();
        procData.put("TRADE_EPARCHY_CODE", eparchCode);
        procData.put("TRADE_CITY_CODE", getVisit().getCityCode());
        procData.put("TRADE_DEPART_ID", getVisit().getDepartId());
        procData.put("TRADE_STAFF_ID", getVisit().getStaffId());
        procData.put("OPER_TYPE_CODE", input.getString("FEE_NAME"));
        procData.put("OPER_TYPE_NAME", feeName);
        procData.put("PAY_MODE", input.getString("FEE_PAY_MODE"));
        procData.put("OPER_FEE", feeAmount);
        procData.put("REMARK", feeReason);
        procData.put("LOG_ID", tradeId);
        
        StringBuilder sql = new StringBuilder();
        
        sql.append("INSERT INTO TF_F_HAND_GATHERING_LOG(LOG_ID,OPER_TYPE_CODE,OPER_TYPE_NAME,OPER_DATE, ");
        sql.append("             OPER_MONTH,TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_CITY_CODE,TRADE_EPARCHY_CODE, ");
        sql.append("             PAY_MODE,OPER_FEE,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,REMARK ");
        if(ygztag)
        {
        	sql.append("             ,NOTAX_FEE,TAX_FEE,TAX_RATE,RATE_TYPE,RSRV_NUM1,RSRV_NUM2) ");
        }else
        {
        	sql.append(") ");
        }
        sql.append("VALUES(:LOG_ID,:OPER_TYPE_CODE,:OPER_TYPE_NAME,SYSDATE, ");
        sql.append("            to_char(SYSDATE,'mm'),:TRADE_STAFF_ID,:TRADE_DEPART_ID,:TRADE_CITY_CODE,:TRADE_EPARCHY_CODE, ");
        sql.append("            :PAY_MODE,:OPER_FEE,NULL,NULL,NULL,NULL,NULL,:REMARK ");      

        if (ygztag)
        {
        	sql.append("            ,:NOTAX_FEE,:TAX_FEE,:TAX_RATE,:RATE_TYPE,:RSRV_NUM1,:RSRV_NUM2) ");
        	
            procData.put("NOTAX_FEE", input.getString("FEE1")); // 不含税价
            procData.put("TAX_FEE", input.getString("FEE2")); // 税额
            procData.put("TAX_RATE", input.getString("RATE")); // 税率
            procData.put("RATE_TYPE", input.getString("TYPE")); // 计税方式
            procData.put("RSRV_NUM1", input.getString("RSRV_NUM1", "")); // 混合税率
            procData.put("RSRV_NUM2", input.getString("RSRV_NUM2", ""));

            // 调用存储过程
            //Dao.callProc("P_CSM_HAND_GATHER_FEE_YGZ", procParamName, procData);
            Dao.executeUpdate(sql, procData);
        }
        // 营改增 end
        else
        {
        	sql.append(") ");
            IData commData = new DataMap();
           /* IDataset procSet = ParamInfoQry.getProcNameByHandGatherFee("CSM", "7801", "3", eparchCode);
            if (IDataUtil.isNotEmpty(procSet))
            {
                commData = procSet.getData(0);
            }
            String procName = commData.getString("PARA_CODE1", "P_CSM_HAND_GATHER_FEE");*/

            // 调用存储过程
            //Dao.callProc(procName, procParamName, procData);
            Dao.executeUpdate(sql, procData);
        }

        // 得到输出参数
        //outData.put("RESULT_CODE", procData.getString("v_resultcode"));
        //outData.put("RESULT_INFO", procData.getString("v_resulterrinfo"));
        outData.put("RESULT_CODE", "0");
        outData.put("RESULT_INFO", "trade ok");
        outData.put("TRADE_ID", tradeId);
        outData.put("FEE_NAME", feeName);
        outData.put("FEE_AMOUNT", feeAmount);
        outData.put("FEE_REASON", feeReason);

        if ("0".equals(procData.getString("v_resultcode")))
        {
            input.put("TRADE_ID", tradeId);
            insertTradeBfasIn(input);
        }
        return outData;
    }

    /**
     * 登记财务BOSS日志表
     * 
     * @param data
     * @throws Exception
     */
    public void insertTradeBfasIn(IData data) throws Exception
    {
        IDataset listdata = new DatasetList();
        IData param = new DataMap();
        param.put("SALE_TYPE_CODE", "1"); // 销售类型
        param.put("PAY_MONEY_CODE", data.getString("FEE_PAY_MODE")); // 收款方式
        param.put("FEE_ITEM_TYPE_CODE", "1"); // 费用明细类型
        param.put("FEE_TYPE_CODE", "10"); // 费用类型
        param.put("RECE_FEE", data.getString("FEE_AMOUNT"));// 应收金额
        param.put("FEE", data.getString("FEE_AMOUNT"));// 实收金额
        param.put("ACC_DATE", SysDateMgr.getSysTime());// 会计日期
        param.put("OPER_DATE", SysDateMgr.getSysTime());// 交易日期
        param.put("CANCEL_TAG", "0"); // 返销标记 0 正业务 1返销业务
        param.put("PROC_TAG", "0"); // 0 未处理 1正在拆分 3拆分成功 4错单
        param.put("IN_MODE_CODE", data.getString("IN_MODE_CODE", getVisit().getInModeCode()));
        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("CITY_CODE", getVisit().getCityCode());
        param.put("DEPART_ID", getVisit().getDepartId());
        param.put("OPER_STAFF_ID", getVisit().getStaffId());
        // 业务类型
        String operTypeCode = data.getString("FEE_NAME", "");
        // 维修费：trade_type_code=B02,fee_type_code=10,fee_item_type_code=1
        if ("0".equals(operTypeCode))
        {
            param.put("OPER_TYPE_CODE", "B02");
        }
        else if ("1".equals(operTypeCode))
        {// 礼品遗失赔偿：trade_type_code=B03,fee_type_code=10,fee_item_type_code=1
            param.put("OPER_TYPE_CODE", "B03");
        }
        else if ("2".equals(operTypeCode))
        {// 代办扣罚缴费：trade_type_code=B04,fee_type_code=10,fee_item_type_code=1
            param.put("OPER_TYPE_CODE", "B04");
        }
        else
        {
            // 操作类型不正确！
        }
        param.put("NET_TYPE_CODE", data.getString("NET_TYPE_CODE", "00"));
        param.put("TRADE_ID", data.getString("TRADE_ID"));

        listdata.add(param);
        FinancialFeeUtil.insertTradeBfasIn(listdata);

        IData inparam = new DataMap();
        inparam.put("SUB_LOG_ID", data.getString("TRADE_ID"));
        inparam.put("CANCEL_TAG", "0");
        FinancialFeeUtil.insertBfasIn(inparam);
    }

    /**
     * 打印手工收款补录
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset printHandGathering(IData input) throws Exception
    {
        String tradeId = input.getString("TRADE_ID");

        long feeAmount = input.getLong("FEE_AMOUNT", 0);
        String totalFee = String.format("%1$3.2f", feeAmount / 100.0);

        String feeName = input.getString("FEE_NAME");
        //String feeReason = input.getString("FEE_REASON");

        String tradeTypeCode = "1101";
        String tradeType = "手工收款补录";
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        IData receiptData = new DataMap();
        IDataset printRes = new DatasetList();
        StringBuilder reamrkSb = new StringBuilder();
        
        reamrkSb.append("原因：" + input.getString("FEE_REASON",""));
        receiptData.put("TRADE_NAME", ReceiptNotePrintMgr.TRADE_NAME);
        receiptData.put("TRADE_TYPE", tradeType);
        receiptData.put("ALL_MONEY_LOWER", totalFee);
        receiptData.put("ALL_MONEY_UPPER", FeeUtils.floatToRMB(feeAmount / 100.0));
        receiptData.put("FEE_TYPE", feeName);
        receiptData.put("FEE", totalFee);
        receiptData.put("TRADE_ID", tradeId);
        receiptData.put("STAFF_NAME", getVisit().getStaffName());
        receiptData.put("STAFF_ID", getVisit().getStaffId());
        receiptData.put("DEPART_NAME", getVisit().getDepartName());
        receiptData.put("OPERATION_DATE", SysDateMgr.getSysDate());
        receiptData.put("REMARK", reamrkSb);

        IDataset templetItem = ReceiptNotePrintMgr.getReceiptTempletItems(tradeTypeCode, ReceiptNotePrintMgr.RECEIPT_P0002, "0", tradeEparchyCode);
        IData printData = ReceiptNotePrintMgr.parsePrintData(receiptData, templetItem);

        IData printInfo = new DataMap();
        printInfo.put("NAME", "手工收款补录收据");
        printInfo.put("PRINT_DATA", printData);
        printInfo.put("TYPE", ReceiptNotePrintMgr.RECEIPT_P0002);
        printInfo.put("FEE_MODE", "2");
        printInfo.put("TOTAL_FEE", feeAmount);
        printInfo.put("HAS_TICKET", "1");
        printInfo.put("TRADE_ID", tradeId);
        printInfo.put("SERIAL_NUMBER", "");
        printInfo.put("EPARCHY_CODE", tradeEparchyCode);
        printInfo.put("TRADE_TYPE_CODE", tradeTypeCode);
        printRes.add(printInfo);

        return printRes;
    }

}
