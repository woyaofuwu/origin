
package com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge.order.action.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.TaxCalcUtils;
import com.asiainfo.veris.crm.order.pub.util.TaxUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.PayMoneyData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.FeeItemTaxInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge.order.requestdata.SupplierChargeReqData;
import com.asiainfo.veris.crm.order.soa.person.common.query.financeboss.FinancialFeeUtil;

public class SupplierChargeTradeAction implements ITradeAction
{

    @SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SupplierChargeReqData supplierChargeRd = (SupplierChargeReqData) btd.getRD();

        financialHandle(btd, supplierChargeRd);
    }

    /**
     * 财务化BOSS处理方法
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    public void financialHandle(BusiTradeData btd, SupplierChargeReqData rd) throws Exception
    {
        /** 插入财务接口日志表 TF_B_TRADE_BFAS_IN **/

        insertTradeBfasIn(btd, rd);
        /** 插入财务接口日志表 TF_A_BFAS_IN **/
        IData bfasinData = new DataMap();
        bfasinData.put("SUB_LOG_ID", rd.getTradeId());
        bfasinData.put("CANCEL_TAG", "0"); // 返销标记 0 正业务 1返销业务
        FinancialFeeUtil.insertBfasIn(bfasinData);
    }

    /**
     * 获取手机卖场收费管理拼接数据 财务费用接口日志表 插表 TF_B_TRADE_BFAS_IN
     * 
     * @author YYZ
     * @param pd
     * @param td
     * @throws Exception
     */
    public void insertTradeBfasIn(BusiTradeData btd, SupplierChargeReqData rd) throws Exception
    {
        String sysDate = SysDateMgr.getSysTime();
        String saleTypeCode = "1"; // 销售类型，默认为正常销售 1 打折销售 2 赠送3
        String paymoneyCode = "0"; // 收款方式
        // TF_B_TRADEFEE_PAYMONEY表获取pay_money_code默认现金0
        String present_fee = ""; // 促销
        String formFee = ""; // 代收手续费/佣金
        String resTypeCode = ""; // 资源大类编码1 sim卡 6 白卡
        String simTypeCode = ""; // 资源小类编码
        String capacityTypeCode = ""; // 面值/容量
        IDataset listdata = new DatasetList();

        IData param = new DataMap();
        param.put("TRADE_ID", rd.getTradeId());

        List<PayMoneyData> paymodeList = rd.getPayMoneyList();// ("TF_B_TRADEFEE_PAYMONEY");
        // Dao.queryListByCodeCode("TF_B_TRADEFEE_PAYMONEY",
        // "SEL_BY_TRADE",
        // accdataparam);
        if (paymodeList != null && !paymodeList.isEmpty())
        {
            paymoneyCode = paymodeList.get(0).getPayMoneyCode();
        }

        String paymodeCode = rd.getUca().getAccount().getPayModeCode(); // 账户类别
        // 根据tf_b_trade_accout表获取PAY_MODE_CODE
        if (!"0".equals(paymodeCode) || !"1".equals(paymodeCode))
        {
            paymodeCode = "";
        }
        /******* 会计日期 *******/
        MainTradeData mtd = btd.getMainTradeData();
        String acceptData = rd.getAcceptTime();

        FeeData feeData = new FeeData();
        List<FeeData> feeList = rd.getFeeList();
        if (feeList != null && !feeList.isEmpty())
        {
            for (int i = 0; i < feeList.size(); i++)
            {
                feeData = feeList.get(i);

                String feeTypeCode = ""; // 费用类型
                String feeMode = feeData.getFeeMode();// 费用类型
                String feeItemTypeCode = feeData.getFeeTypeCode(); // 费用明细类型
                // (TF_B_TRADEFEE_SUB)
                String oldFee = feeData.getOldFee();// 应收金额
                String fee = feeData.getFee();// 实收金额
                saleTypeCode = "1";
                present_fee = "";
                formFee = "";
                if ("0".equals(feeMode))// 营业费，卡费
                {
                    feeTypeCode = "10";// 营业费用
                }
                if ("".equals(feeTypeCode))
                {
                    continue;
                }
                if (Integer.parseInt(oldFee) > 0)
                {
                    if (Integer.parseInt(oldFee) > Integer.parseInt(fee) && Integer.parseInt(fee) != 0)
                    { // 打折销售
                        saleTypeCode = "2";
                    }
                    else if (Integer.parseInt(fee) == 0)
                    { // 赠送
                        saleTypeCode = "3";
                    }
                }
                if (saleTypeCode != "1")
                {
                    int itempFee = Integer.parseInt(oldFee) - Integer.parseInt(fee);
                    present_fee = String.valueOf(itempFee);
                    formFee = "0";
                }
                param.put("SALE_TYPE_CODE", saleTypeCode); // 销售类型
                param.put("PAY_MONEY_CODE", paymoneyCode); // 收款方式
                param.put("FEE_TYPE_CODE", feeTypeCode); // 费用类型
                param.put("FEE_ITEM_TYPE_CODE", feeItemTypeCode); // 费用明细类型
                param.put("PAY_MODE_CODE", paymodeCode); // 账户类别
                param.put("BRAND_CODE", rd.getUca().getBrandCode());// 用户品牌
                param.put("PRODUCT_ID", rd.getUca().getProductId());// 用户产品
                param.put("USER_TYPE_CODE", rd.getUca().getUser().getUserTypeCode()); // 用户类型
                param.put("RES_TYPE_CODE", resTypeCode);// 资源类型
                param.put("RES_KIND_CODE", simTypeCode);// 资源种类
                param.put("CAPACITY_TYPE_CODE", capacityTypeCode);// 面值/容量
                param.put("RECE_FEE", oldFee);// 应收金额
                param.put("FEE", fee);// 实收金额
                param.put("PRESENT_FEE", present_fee);// 促销赠送金额
                param.put("FORM_FEE", formFee); // 代收手续费/佣金
                param.put("ACC_DATE", acceptData);// 会计日期
                param.put("OPER_DATE", acceptData);// 交易日期
                param.put("CANCEL_TAG", "0");// 返销标记 0 正业务 1返销业务
                param.put("PROC_TAG", "0"); // 0 未处理 1正在拆分 3拆分成功 4错单
                param.put("RSRV_NUM1", rd.getUca().getUser().getUserId()); // 填写用户的USER_ID
                // 公共参数bfas
                param.put("TRADE_ID", rd.getTradeId());
                param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 地市编码
                param.put("CITY_CODE", CSBizBean.getVisit().getCityCode());// 业务区编码
                param.put("DEPART_ID", CSBizBean.getVisit().getDepartId());// 部门编码
                param.put("OPER_STAFF_ID", CSBizBean.getVisit().getStaffId());// 员工编码
                param.put("OPER_TYPE_CODE", mtd.getTradeTypeCode());// 系统业务类型
                param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入方式td.getInModeCode()
                param.put("NET_TYPE_CODE", mtd.getNetTypeCode());// 网别
                if (TaxUtils.isYgzTag())
                {
                    IDataset taxInfo = FeeItemTaxInfoQry.qryTaxByTypeCode(btd.getTradeTypeCode(), feeTypeCode, feeMode, rd.getUca().getProductId(), "", "", sysDate, CSBizBean.getTradeEparchyCode());
                    if (IDataUtil.isEmpty(taxInfo) || taxInfo.size() > 1)
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_1111);

                    }
                    else
                    {
                        String rate = taxInfo.getData(0).getString("RATE");
                        String type = taxInfo.getData(0).getString("TYPE");
                        IDataset infos = new DatasetList();
                        IData info = new DataMap();
                        info.put("RATE", rate);
                        info.put("TYPE", type);
                        info.put("FEE", oldFee);
                        info.put("FACT_PAY_FEE", fee);
                        infos.add(info);
                        TaxCalcUtils.getTradeFeeTaxForCalculate(infos);
                        info = infos.getData(0);
                        if (IDataUtil.isNotEmpty(info))
                        {
                            param.put("NO_TAX_FEE", info.getString("FEE1"));// 不含税价
                            param.put("TAX_FEE", info.getString("FEE2"));// 不含税价
                            param.put("RATE", info.getString("RATE"));// 不含税价
                            param.put("RSRV_NUM5", info.getString("FEE3"));// 不含税价
                            param.put("RSRV_STR3", type);// 不含税价
                        }

                    }
                }
                listdata.add(param);
            }
        }
        FinancialFeeUtil.insertTradeBfasIn(listdata);
    }
}
