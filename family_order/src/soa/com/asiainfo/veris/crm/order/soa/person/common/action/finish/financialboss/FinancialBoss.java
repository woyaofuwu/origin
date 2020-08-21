
package com.asiainfo.veris.crm.order.soa.person.common.action.finish.financialboss;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BfasInTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DeviceTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTaxTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayMoneyTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResourceInterTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class FinancialBoss implements ITradeFinishAction
{

    private void addResLog(RegTradeData rtd) throws Exception
    {
        String tradeTypeCode = rtd.getTradeTypeCode();
        if ("421".equals(tradeTypeCode) || "9".equals(tradeTypeCode))
        {
            return;
        }

        MainTradeData mainTrade = rtd.getMainTradeData();
        String tradeId = rtd.getTradeId();
        String logId = "20" + tradeId;
        String cancelTag = mainTrade.getCancelTag();
        String userId = mainTrade.getUserId();
        if (StringUtils.isBlank(userId))
        {
            userId = "1";
        }
        String inventOrgId = "HIC";
        String operTag = "8";
        String dcTag = "D";
        String operTime = DataBusManager.getDataBus().getAcceptTime();
        String assignTime = mainTrade.getExecTime();
        String cancelDate = "";
        String cancelSubLogId = "";
        if ("2".equals(cancelTag))
        {
            operTime = mainTrade.getCancelDate();
            dcTag = "C";// 返销
            cancelSubLogId = tradeId;
            cancelDate = DataBusManager.getDataBus().getAcceptTime();
        }
        if ("429".equals(tradeTypeCode) || "419".equals(tradeTypeCode))
        {
            operTag = "9";
        }

        List<ResTradeData> resTradeDatas = rtd.getTradeDatas(TradeTableEnum.TRADE_RES.getValue());
        if (resTradeDatas != null && resTradeDatas.size() > 0)
        {
            for (ResTradeData resTradeData : resTradeDatas)
            {
                String resTypeCode = resTradeData.getResTypeCode();
                String modifyTag = resTradeData.getModifyTag();
                if (BofConst.MODIFY_TAG_DEL.equals(modifyTag) || "3".equals(modifyTag) || BofConst.MODIFY_TAG_INHERIT.equals(modifyTag))
                {
                    continue;
                }
                if (!"1".equals(resTypeCode) && !"3".equals(resTypeCode) && !"6".equals(resTypeCode))
                {
                    continue;
                }
                String emptyCardNo = "";
                String stockId = "";
                String resState = "";
                String resKindCode = "";
                if ("1".equals(resTypeCode) || "6".equals(resTypeCode))
                {
                    String imsi = resTradeData.getImsi();
                    // 调用资源接口 ResCallXXX查询sim卡相关信息
                    IDataset simcardInfos = ResCall.getSimCardInfo("1", "", imsi, "");
                    if (IDataUtil.isEmpty(simcardInfos))
                    {
                        CSAppException.apperr(CrmCardException.CRM_CARD_10);
                    }
                    IData simcardInfo = simcardInfos.getData(0);
                    stockId = simcardInfo.getString("RECEIVER_ID");
                    resState = simcardInfo.getString("RES_STATE");
                    resKindCode = simcardInfo.getString("RES_KIND_CODE");
                    emptyCardNo = simcardInfo.getString("EMPTY_CARD_ID");
                }
                if (StringUtils.isNotBlank(emptyCardNo))
                {
                    // 调用资源接口取白卡信息
                    IDataset simcardInfos = ResCall.getEmptycardInfo(emptyCardNo, "", "");
                    if (IDataUtil.isEmpty(simcardInfos))
                    {
                        CSAppException.apperr(CrmCardException.CRM_CARD_10);
                    }
                    IData simcardInfo = simcardInfos.getData(0);
                    stockId = simcardInfo.getString("RECEIVER_ID");
                    resState = simcardInfo.getString("RES_STATE");
                    resKindCode = simcardInfo.getString("RES_KIND_CODE");
                    emptyCardNo = simcardInfo.getString("EMPTY_CARD_ID");
                }
                ResourceInterTradeData resourceTradeData = new ResourceInterTradeData();
                resourceTradeData.setLogId(logId);
                resourceTradeData.setSubLogId(tradeId);
                resourceTradeData.setPartitionId(StrUtil.getAcceptMonthById(tradeId));
                resourceTradeData.setInventOrgId(inventOrgId);
                resourceTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
                resourceTradeData.setCityCode(CSBizBean.getVisit().getCityCode());
                resourceTradeData.setOperDepartId(CSBizBean.getVisit().getDepartId());
                resourceTradeData.setOperStaffId(CSBizBean.getVisit().getStaffId());
                resourceTradeData.setStockIdO(stockId);
                resourceTradeData.setOperTag(operTag);
                resourceTradeData.setOperTime(operTime);
                resourceTradeData.setAssignTime(assignTime);
                resourceTradeData.setResTypeCode(resTypeCode);
                resourceTradeData.setResKindCode(resKindCode);
                resourceTradeData.setResState(resState);
                resourceTradeData.setOperTypeCode(tradeTypeCode);
                resourceTradeData.setDcTag(dcTag);
                resourceTradeData.setOperNum("1");
                resourceTradeData.setCancelSubLogId(cancelSubLogId);
                resourceTradeData.setCancelDate(cancelDate);
                Dao.insert("TF_B_TRADE_RESOURCE_INTER", resourceTradeData.toData());
            }
        }

        if ("416".equals(tradeTypeCode) || "419".equals(tradeTypeCode) || "420".equals(tradeTypeCode) || "424".equals(tradeTypeCode) || "430".equals(tradeTypeCode) || "330".equals(tradeTypeCode))
        {
            List<DeviceTradeData> deviceTrades = rtd.getTradeDatas(TradeTableEnum.TRADE_FEEDEVICE.getValue());
            if (deviceTrades != null && deviceTrades.size() > 0)
            {
                for (DeviceTradeData deviceTrade : deviceTrades)
                {
                    String resKindCode = deviceTrade.getDeviceTypeCode();
                    String deviceNo = deviceTrade.getDeviceNoS();
                    String operNum = deviceTrade.getDeviceNum();
                    // 调用资源接口查询有价卡相关信息

                    IDataset valueCardInfos = ResCall.queryValueCardInfoIntf(deviceNo, deviceNo, "", "");
                    if (IDataUtil.isEmpty(valueCardInfos))
                    {
                        CSAppException.apperr(CrmCardException.CRM_CARD_208);
                    }
                    IData valueCardInfo = valueCardInfos.getData(0);
                    String resState = valueCardInfo.getString("RES_STATE", "");
                    String stockIdO = valueCardInfo.getString("RECEIVER_ID", "");
                    String stockIdN = "";
                    if ("424".equals(tradeTypeCode))
                    {
                        stockIdN = valueCardInfo.getString("RECEIVER_ID", "");
                    }
                    else
                    {
                        stockIdO = "";
                    }
                    if ("420".equals(tradeTypeCode))
                    {
                        ResourceInterTradeData resourceTradeData = new ResourceInterTradeData();
                        resourceTradeData.setLogId(logId);
                        resourceTradeData.setSubLogId(tradeId);
                        resourceTradeData.setPartitionId(StrUtil.getAcceptMonthById(tradeId));
                        resourceTradeData.setInventOrgId(inventOrgId);
                        resourceTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
                        resourceTradeData.setCityCode(CSBizBean.getVisit().getCityCode());
                        resourceTradeData.setOperDepartId(CSBizBean.getVisit().getDepartId());
                        resourceTradeData.setOperStaffId(CSBizBean.getVisit().getStaffId());
                        resourceTradeData.setStockIdO(stockIdO);
                        resourceTradeData.setOperTag(operTag);
                        resourceTradeData.setOperTime(operTime);
                        resourceTradeData.setAssignTime(assignTime);
                        resourceTradeData.setResTypeCode("3");
                        resourceTradeData.setResKindCode(resKindCode);
                        resourceTradeData.setResState(resState);
                        resourceTradeData.setOperTypeCode(tradeTypeCode);
                        resourceTradeData.setDcTag(dcTag);
                        resourceTradeData.setOperNum("1");
                        resourceTradeData.setCancelSubLogId(cancelSubLogId);
                        resourceTradeData.setCancelDate(cancelDate);
                        Dao.insert("TF_B_TRADE_RESOURCE_INTER", resourceTradeData.toData());
                    }
                    ResourceInterTradeData resourceTradeData = new ResourceInterTradeData();
                    resourceTradeData.setLogId(logId);
                    resourceTradeData.setSubLogId(tradeId);
                    resourceTradeData.setPartitionId(StrUtil.getAcceptMonthById(tradeId));
                    resourceTradeData.setInventOrgId(inventOrgId);
                    resourceTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
                    resourceTradeData.setCityCode(CSBizBean.getVisit().getCityCode());
                    resourceTradeData.setOperDepartId(CSBizBean.getVisit().getDepartId());
                    resourceTradeData.setOperStaffId(CSBizBean.getVisit().getStaffId());
                    resourceTradeData.setStockIdO(stockIdO);
                    resourceTradeData.setOperTag(operTag);
                    resourceTradeData.setOperTime(operTime);
                    resourceTradeData.setAssignTime(assignTime);
                    resourceTradeData.setResTypeCode("3");
                    resourceTradeData.setResKindCode(resKindCode);
                    resourceTradeData.setResState(resState);
                    resourceTradeData.setOperTypeCode(tradeTypeCode);
                    resourceTradeData.setDcTag(dcTag);
                    resourceTradeData.setOperNum("1");
                    resourceTradeData.setCancelSubLogId(cancelSubLogId);
                    resourceTradeData.setCancelDate(cancelDate);
                    Dao.insert("TF_B_TRADE_RESOURCE_INTER", resourceTradeData.toData());
                }
            }
        }
        if ("234".equals(tradeTypeCode))
        {
            String simCardNo = mainTrade.getRsrvStr7();
            String resTypeCode = "";
            String emptyCardNo = "";
            String stockId = "";
            String resState = "";
            String resKindCode = "";
            IDataset simcardInfos = ResCall.getSimCardInfo("0", "", simCardNo, "");
            if (IDataUtil.isEmpty(simcardInfos))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_10);
            }
            IData simcardInfo = simcardInfos.getData(0);
            stockId = simcardInfo.getString("RECEIVER_ID");
            resState = simcardInfo.getString("RES_STATE");
            resKindCode = simcardInfo.getString("RES_KIND_CODE");
            emptyCardNo = simcardInfo.getString("EMPTY_CARD_ID");
            resTypeCode = simcardInfo.getString("RES_TYPE_CODE");
            ResourceInterTradeData resourceTradeData = new ResourceInterTradeData();
            resourceTradeData.setLogId(logId);
            resourceTradeData.setSubLogId(tradeId);
            resourceTradeData.setPartitionId(StrUtil.getAcceptMonthById(tradeId));
            resourceTradeData.setInventOrgId(inventOrgId);
            resourceTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
            resourceTradeData.setCityCode(CSBizBean.getVisit().getCityCode());
            resourceTradeData.setOperDepartId(CSBizBean.getVisit().getDepartId());
            resourceTradeData.setOperStaffId(CSBizBean.getVisit().getStaffId());
            resourceTradeData.setStockIdO("");
            resourceTradeData.setOperTag(operTag);
            resourceTradeData.setOperTime(operTime);
            resourceTradeData.setAssignTime(assignTime);
            resourceTradeData.setResTypeCode("3");
            resourceTradeData.setResKindCode(resKindCode);
            resourceTradeData.setResState(resState);
            resourceTradeData.setOperTypeCode(tradeTypeCode);
            resourceTradeData.setDcTag(dcTag);
            resourceTradeData.setOperNum("1");
            resourceTradeData.setCancelSubLogId(cancelSubLogId);
            resourceTradeData.setCancelDate(cancelDate);
            Dao.insert("TF_B_TRADE_RESOURCE_INTER", resourceTradeData.toData());
        }
    }

    public void deal(RegTradeData rtd) throws Exception
    {
        this.dealWithFeeSub(rtd);
        this.dealWithDeviceFee(rtd);
        this.dealFeeTax(rtd);
        this.addResLog(rtd);
    }

    private void dealFeeTax(RegTradeData rtd) throws Exception
    {
        List<FeeTaxTradeData> feeTaxTrades = rtd.getTradeDatas(TradeTableEnum.TRADE_FEETAX.getValue());
        if (feeTaxTrades != null && feeTaxTrades.size() > 0)
        {
            for (FeeTaxTradeData feeTaxTrade : feeTaxTrades)
            {
                String rate = feeTaxTrade.getRate();
                String oldFee = feeTaxTrade.getSalePrice();
                String fee = feeTaxTrade.getBusiFee();
                String noTaxFee = feeTaxTrade.getFee1();
                String taxFee = feeTaxTrade.getFee2();
                String presentFee = feeTaxTrade.getFee3();
                String score = feeTaxTrade.getScoreValue();
                String taxTag1 = feeTaxTrade.getRsrvTag1();
                this.insertFinancialBossTrade("10", feeTaxTrade.getFeeTypeCode(), Integer.parseInt(fee), Integer.parseInt(oldFee), 0, "", "", "", 0, "", taxFee, rate, noTaxFee, taxTag1, presentFee, rtd);
            }
        }
    }

    private void dealWithDeviceFee(RegTradeData rtd) throws Exception
    {
        List<DeviceTradeData> deviceTrades = rtd.getTradeDatas(TradeTableEnum.TRADE_FEEDEVICE.getValue());
        if (deviceTrades == null || deviceTrades.size() == 0)
        {
            return;
        }
        int fee = 0;
        int oldFee = 0;
        String feeTypeCode = "20";
        String bfasFeeType = "10";
        List<FeeTradeData> feeDatas = rtd.getTradeDatas(TradeTableEnum.TRADE_FEESUB.getValue());
        if (feeDatas != null && feeDatas.size() > 0)
        {
            feeTypeCode = feeDatas.get(0).getFeeTypeCode();
            String feeMode = feeDatas.get(0).getFeeMode();

            if ("0".equals(feeMode))
            {
                bfasFeeType = "10";
            }
            else if ("1".equals(feeMode))
            {
                bfasFeeType = "11";
            }
        }
        String tradeTypeCode = rtd.getTradeTypeCode();
        String cardFlag = rtd.getMainTradeData().getRsrvStr5();
        String resTypeCode = "";
        for (DeviceTradeData deviceTrade : deviceTrades)
        {
            String resKindCode = deviceTrade.getDeviceTypeCode();
            String capacityTypeCode = deviceTrade.getRsrvStr1();
            int cardNum = Integer.parseInt(deviceTrade.getDeviceNum());
            fee = Integer.parseInt(deviceTrade.getSalePrice());
            oldFee = Integer.parseInt(deviceTrade.getDevicePrice()) * cardNum;
            if ("419".equals(tradeTypeCode))
            {
                fee = -1 * fee;
                oldFee = fee;
                resTypeCode = "3";
            }
            else if ("416".equals(tradeTypeCode))
            {
                resTypeCode = "3";
            }
            else if ("421".equals(tradeTypeCode))
            {
                resTypeCode = "I";
            }
            else if ("418".equals(tradeTypeCode) || (("330".equals(tradeTypeCode) || "333".equals(tradeTypeCode)) && "cards".equals(cardFlag)))
            {
                resTypeCode = "3";
            }
            this.insertFinancialBossTrade(bfasFeeType, feeTypeCode, fee, oldFee, 0, resTypeCode, resKindCode, capacityTypeCode, cardNum, "", "", "", "", "", "", rtd);
        }
    }

    private void dealWithFeeSub(RegTradeData rtd) throws Exception
    {
        List<FeeTradeData> feeTrades = rtd.getTradeDatas(TradeTableEnum.TRADE_FEESUB.getValue());
        String tradeTypeCode = rtd.getTradeTypeCode();
        if (feeTrades == null || feeTrades.size() == 0)
        {
            if ("10".equals(tradeTypeCode) || "142".equals(tradeTypeCode))
            {
                this.dealWithSimCard(rtd, rtd.getTradeDatas(TradeTableEnum.TRADE_RES.getValue()), 0, 0, "10", "10");
            }
            else if ("234".equals(tradeTypeCode))
            {
                int fee = Integer.parseInt(rtd.getMainTradeData().getRsrvStr3() == null ? "0" : rtd.getMainTradeData().getRsrvStr3());
                this.insertFinancialBossTrade("10", "10", fee, fee, 0, "", "", "", 0, "", "", "", "", "", "", rtd);
            }
            return;
        }
        int fee = 0;
        int oldFee = 0;
        String cancelTag = rtd.getMainTradeData().getCancelTag();
        String bfasFeeType = "";
        List<ResTradeData> resTrades = rtd.getTradeDatas(TradeTableEnum.TRADE_RES.getValue());
        for (FeeTradeData feeData : feeTrades)
        {
            fee = Integer.parseInt(feeData.getFee());
            oldFee = Integer.parseInt(feeData.getOldfee());
            String feeMode = feeData.getFeeMode();
            String feeTypeCode = feeData.getFeeTypeCode();
            if ("2".equals(cancelTag) && fee != 0)
            {
                fee = -1 * fee;
                oldFee = -1 * oldFee;
            }

            if ("0".equals(feeMode))
            {
                bfasFeeType = "10";
            }
            else if ("1".equals(feeMode))
            {
                bfasFeeType = "11";
            }
            if (StringUtils.isBlank(bfasFeeType))
            {
                continue;
            }
            if ("10".equals(feeTypeCode) && "0".equals(feeMode))
            {
                this.dealWithSimCard(rtd, rtd.getTradeDatas(TradeTableEnum.TRADE_RES.getValue()), fee, oldFee, bfasFeeType, feeTypeCode);
                continue;
            }

            if (fee == 0 && oldFee == 0)
            {
                if ("234".equals(tradeTypeCode))
                {
                    this.insertFinancialBossTrade("10", "10", fee, oldFee, 0, "", "", "", 0, "", "", "", "", "", "", rtd);
                    continue;
                }
                else if (resTrades != null && resTrades.size() > 0)
                {
                    this.insertFinancialBossTrade("10", "10", fee, oldFee, 0, "", "", "", 0, "", "", "", "", "", "", rtd);
                }
            }
            else
            {
                this.insertFinancialBossTrade(bfasFeeType, feeTypeCode, fee, oldFee, 0, "", "", "", 0, "", "", "", "", "", "", rtd);
            }
        }
    }

    private void dealWithSimCard(RegTradeData rtd, List<ResTradeData> resTrades, int fee, int oldFee, String feeType, String feeItem) throws Exception
    {
        if (resTrades == null || resTrades.size() == 0)
        {
            return;
        }

        String buyCardFlag = "";
        int formFee = 0;
        for (ResTradeData resTrade : resTrades)
        {
            if ("1".equals(resTrade.getResTypeCode()) && BofConst.MODIFY_TAG_ADD.equals(resTrade.getModifyTag()))
            {
                this.insertFinancialBossTrade(feeType, feeItem, fee, oldFee, formFee, resTrade.getResTypeCode(), resTrade.getRsrvStr1(), "", 0, "1", "", "", "", "", "", rtd);
                // 查询sim卡的资源相关信息
                if ("10".equals(rtd.getTradeTypeCode()) || "142".equals(rtd.getTradeTypeCode()))
                {
                    if ("A".equals(resTrade.getRsrvTag2()))
                    {
                        // sim卡买断
                        formFee = Integer.parseInt(resTrade.getRsrvNum5());
                        this.insertFinancialBossTrade(feeType, feeItem, (-1) * formFee, (-1) * formFee, formFee, resTrade.getResTypeCode(), resTrade.getRsrvStr1(), "", 0, "2", "", "", "", "", "", rtd);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        DataBusManager.getDataBus().setAcceptTime(mainTrade.getString("ACCEPT_DATE"));
        this.deal(new RegTradeData(mainTrade));
    }

    private void insertFinancialBossTrade(String feeTypeCode, String feeItemTypeCode, int fee, int oldFee, int formFee, String resTypeCode, String resKindCode, String capacityTypeCode, int cardNum, String buyCardFlag, String taxFee, String rate,
            String noTaxFee, String taxTag1, String taxFee3, RegTradeData rtd) throws Exception
    {
        MainTradeData mainTradeData = rtd.getMainTradeData();
        String tradeId = rtd.getTradeId();
        String eparchyCode = mainTradeData.getEparchyCode();
        String cityCode = mainTradeData.getCityCode();
        if (StringUtils.isBlank(eparchyCode))
        {
            eparchyCode = CSBizBean.getVisit().getStaffEparchyCode();
        }
        if (StringUtils.isBlank(cityCode))
        {
            cityCode = CSBizBean.getVisit().getCityCode();
        }
        String bfasId = SeqMgr.getCrmBfasId();
        String logId = "20" + tradeId;
        String campnId = "";
        String tradeTypeCode = mainTradeData.getTradeTypeCode();
        if ("2".equals(buyCardFlag))
        {
            tradeTypeCode = "B01";
        }
        String acceptTime = DataBusManager.getDataBus().getAcceptTime();
        String deviceModelCode = null;
        String deviceTypeCode = null;
        String terminalOrderId = null;
        String deviceProduct = null;
        String procurementType = null;
        String payMoneyCode = "Z";
        String agencyId = null;
        int presentFee = 0;

        String cancelTag = mainTradeData.getCancelTag();
        String saleTypeCode = "1";// 正常销售
        if ("0".equals(cancelTag) && oldFee > 0)
        {
            if (oldFee > fee && fee != 0)
            {
                saleTypeCode = "2";// 打折销售
            }
            else if (fee == 0)
            {
                saleTypeCode = "3";// 赠送
            }
        }
        else if (!"0".equals(cancelTag))
        {
            // 查询正向业务的那条财务化boss记录来得到sale_type_code
        }

        if (!"1".equals(saleTypeCode))
        {
            presentFee = oldFee - fee;
            formFee = 0;
        }

        if ("330".equals(tradeTypeCode) || "333".equals(tradeTypeCode) || "418".equals(tradeTypeCode))
        {
            saleTypeCode = "3";
            presentFee = oldFee;
            fee = 0;
        }

        List<AccountTradeData> accounts = rtd.getTradeDatas(TradeTableEnum.TRADE_ACCOUNT.getValue());
        String acctPayModeCode = "";
        if (accounts != null && accounts.size() > 0)
        {
            acctPayModeCode = accounts.get(0).getPayModeCode();
        }
        else
        {
            acctPayModeCode = rtd.getUca().getAccount().getPayModeCode();
        }

        if ("Z".equals(acctPayModeCode) || "3".equals(acctPayModeCode))
        {
            // 支票需要从tradefee_check中获取支票号数据，目前还没有此类数据
        }

        if ("234".equals(tradeTypeCode))
        {
            payMoneyCode = mainTradeData.getRsrvStr4();
        }
        else
        {
            List<PayMoneyTradeData> payMoneys = rtd.getTradeDatas(TradeTableEnum.TRADE_PAYMONEY.getValue());
            if (payMoneys != null && payMoneys.size() > 0)
            {
                payMoneyCode = payMoneys.get(0).getPayMoneyCode();
            }
        }

        if ("240".equals(tradeTypeCode) || "252".equals(tradeTypeCode) || "255".equals(tradeTypeCode) || "3814".equals(tradeTypeCode))
        {
            if (fee == 0 && oldFee == 0)
            {
                // 应收实收都为0，返回
                return;
            }

            List<SaleActiveTradeData> saleActives = rtd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE.getValue());
            if (saleActives != null && saleActives.size() > 0)
            {
                SaleActiveTradeData saleActive = saleActives.get(0);
                campnId = saleActive.getProductId() + "|" + saleActive.getPackageId();
            }

            List<SaleGoodsTradeData> saleGoods = rtd.getTradeDatas(TradeTableEnum.TRADE_SALEGOODS.getValue());
            if (saleGoods != null && saleGoods.size() > 0)
            {
                for (SaleGoodsTradeData saleGood : saleGoods)
                {
                    if ("4".equals(saleGood.getResTypeCode()))
                    {
                        resTypeCode = "4";
                        deviceModelCode = saleGood.getRsrvStr9();// 终端型号
                        deviceProduct = saleGood.getDeviceBrandCode();// 终端品牌
                        agencyId = saleGood.getRsrvStr1();// 提供商编码
                        resKindCode = saleGood.getRsrvStr9();// 终端型号
                        break;
                    }
                }
            }
        }
        else if ("242".equals(tradeTypeCode) || "243".equals(tradeTypeCode) || "249".equals(tradeTypeCode))
        {
            resTypeCode = "4";
            deviceTypeCode = "4";
            deviceModelCode = "01";
        }

        BfasInTradeData financialData = new BfasInTradeData();
        financialData.setBfasId(bfasId);
        financialData.setLogId(logId);
        financialData.setSysCode("BUSNS");
        financialData.setSubLogId(tradeId);
        financialData.setPartitionId(StrUtil.getAcceptMonthById(tradeId));
        financialData.setEparchyCode(eparchyCode);
        financialData.setCityCode(cityCode);
        financialData.setDepartId(CSBizBean.getVisit().getDepartId());
        financialData.setOperStaffId(CSBizBean.getVisit().getStaffId());
        financialData.setOperTypeCode(tradeTypeCode);

        financialData.setSaleTypeCode(saleTypeCode);
        financialData.setPayMoneyCode(payMoneyCode);

        financialData.setAgentCode("");
        financialData.setCollAgenCode("");// 代收话费业务时填写,暂时不确定从哪取值

        financialData.setCampnId(campnId);
        financialData.setFeeTypeCode(feeTypeCode);
        financialData.setFeeItemTypeCode(feeItemTypeCode);
        if ("B01".equals(tradeTypeCode))
        {
            financialData.setFeeItemTypeCode("1");
        }
        financialData.setPayModeCode(acctPayModeCode);
        financialData.setInModeCode(mainTradeData.getInModeCode());
        financialData.setNetTypeCode(StringUtils.isBlank(mainTradeData.getNetTypeCode()) ? "00" : mainTradeData.getNetTypeCode());
        financialData.setBrandCode(mainTradeData.getBrandCode());
        financialData.setProductId(mainTradeData.getProductId());
        financialData.setUserTypeCode(rtd.getUca().getUser().getUserTypeCode());

        financialData.setCheckNumber("");// 支票号
        financialData.setResTypeCode(resTypeCode);// 资源大类
        financialData.setResKindCode(resKindCode);// 资源的具体子类
        financialData.setCapacityTypeCode(capacityTypeCode);// 资源的容量类型
        financialData.setDeviceTypeCode(deviceTypeCode);
        financialData.setDeviceModelCode(deviceModelCode);
        financialData.setOrderId(terminalOrderId);// 采购订单
        financialData.setProcurementType(procurementType);
        financialData.setAgencyId(agencyId);// 供应商编码
        financialData.setDeviceProduct(deviceProduct);
        financialData.setSupplyType("");

        financialData.setReceFee(oldFee + "");
        financialData.setFee(fee + "");
        financialData.setPresentFee(presentFee + "");// 促销赠送金额
        financialData.setFormFee(formFee + ""); // 代收手续费，佣金
        financialData.setProcTag("0");
        financialData.setAccDate(acceptTime);
        financialData.setOperDate(acceptTime);
        financialData.setCancelTag(mainTradeData.getCancelTag());

        if (!"0".equals(mainTradeData.getCancelTag()))
        {
            financialData.setCancelDate(mainTradeData.getCancelDate());
            financialData.setRsrvStr2(mainTradeData.getCancelStaffId());
            financialData.setCancelSubLogId(tradeId);
        }

        financialData.setRsrvTag1(buyCardFlag);
        if ("2".equals(buyCardFlag))
        {
            financialData.setRsrvTag2("A");
        }

        // 营改增
        financialData.setRsrvStr3(taxTag1);
        financialData.setRsrvNum5(taxFee3);
        financialData.setTaxFee(taxFee);
        financialData.setRate(rate);
        financialData.setNoTaxFee(noTaxFee);

        financialData.setRsrvNum1(mainTradeData.getUserId());
        financialData.setRsrvNum2(cardNum + "");

        Dao.insert("TF_B_TRADE_BFAS_IN", financialData.toData());
    }
}
