 
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plugin.financialboss;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
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
import com.asiainfo.veris.crm.order.soa.frame.bof.plugin.IPlugin;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class FinancialBoss implements IPlugin
{

    private void addResLog(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        if ("421".equals(tradeTypeCode) || "9".equals(tradeTypeCode) || "149".equals(tradeTypeCode))
        {
            return;
        }

        MainTradeData mainTrade = btd.getMainTradeData();
        String tradeId = btd.getTradeId();
        String logId = "20" + tradeId;
        String userId = mainTrade.getUserId();
        if (StringUtils.isBlank(userId))
        {
            userId = "1";
        }
        String inventOrgId = "HIC";
        String operTag = "8";
        String dcTag = "D";
        String operTime = btd.getRD().getAcceptTime();
        String assignTime = mainTrade.getExecTime();
        String cancelDate = "";
        String cancelSubLogId = "";
        if ("429".equals(tradeTypeCode) || "419".equals(tradeTypeCode))
        {
            operTag = "9";
        }

        List<ResTradeData> resTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_RES);
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
                    if (IDataUtil.isNotEmpty(simcardInfos))
                    {
                        IData simcardInfo = simcardInfos.getData(0);
                        stockId = simcardInfo.getString("RECEIVER_ID");
                        resState = simcardInfo.getString("RES_STATE");
                        resKindCode = simcardInfo.getString("RES_KIND_CODE");
                        emptyCardNo = simcardInfo.getString("EMPTY_CARD_ID");
                    }
                }
                if (StringUtils.isNotBlank(emptyCardNo))
                {
                    // 调用资源接口取白卡信息
                    IDataset simcardInfos = ResCall.getEmptycardInfo(emptyCardNo, "", "");
                    if (IDataUtil.isNotEmpty(simcardInfos))
                    {
                        IData simcardInfo = simcardInfos.getData(0);
                        stockId = simcardInfo.getString("RECEIVER_ID");
                        resState = simcardInfo.getString("RES_STATE");
                        resKindCode = simcardInfo.getString("RES_KIND_CODE");
                        emptyCardNo = simcardInfo.getString("EMPTY_CARD_ID");
                    }
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
                btd.add(null, resourceTradeData);
            }
        }

        if ("461".equals(tradeTypeCode) || "416".equals(tradeTypeCode) || "419".equals(tradeTypeCode) || "420".equals(tradeTypeCode) || "424".equals(tradeTypeCode) || "430".equals(tradeTypeCode) || "330".equals(tradeTypeCode))
        {
            List<DeviceTradeData> deviceTrades = btd.getTradeDatas(TradeTableEnum.TRADE_FEEDEVICE);
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
                        btd.add(null, resourceTradeData);
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
                    btd.add(null, resourceTradeData);
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
            IDataset simcardInfos = ResCall.getSimCardInfo("0", simCardNo, "", "");
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
            btd.add(null, resourceTradeData);
        }
    }

    @Override
    public void deal(List<BusiTradeData> btds, IData input) throws Exception
    {
        for (BusiTradeData btd : btds)
        {
            if (!"0".equals(btd.getMainTradeData().getCancelTag()))
            {
                continue;// 只处理正向
            }
            if ("502".equals(btd.getMainTradeData().getTradeTypeCode()))
            {
                continue;
            }
            this.dealWithFeeSub(btd);
            this.dealWithDeviceFee(btd);
            this.dealWithSimCard(btd, btd.getTradeDatas(TradeTableEnum.TRADE_RES));
            this.addResLog(btd);
        }
    }

    private void dealFeeTax(BusiTradeData btd) throws Exception
    {
        List<FeeTaxTradeData> feeTaxTrades = btd.getTradeDatas(TradeTableEnum.TRADE_FEETAX);
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
                this.insertFinancialBossTrade("20", feeTaxTrade.getFeeTypeCode(), Integer.parseInt(fee), Integer.parseInt(oldFee), 0, "", "", "", 0, "", taxFee, rate, noTaxFee, taxTag1, presentFee, btd);
            }
        }
    }

    private void dealWithDeviceFee(BusiTradeData btd) throws Exception
    {
        List<DeviceTradeData> deviceTrades = btd.getTradeDatas(TradeTableEnum.TRADE_FEEDEVICE);
        if (deviceTrades == null || deviceTrades.size() == 0)
        {
            return;
        }
        int fee = 0;
        int oldFee = 0;
        String feeTypeCode = "20";
        String bfasFeeType = "10";
        List<FeeTradeData> feeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
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
        String tradeTypeCode = btd.getTradeTypeCode();
        String cardFlag = btd.getMainTradeData().getRsrvStr5();
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
            }

            resTypeCode = deviceTrade.getRsrvStr1();
            if (StringUtils.isBlank(resTypeCode))
            {
                if ("421".equals(tradeTypeCode))
                {
                    resTypeCode = "I";
                }
                else
                {
                    resTypeCode = "3";
                }
            }
            this.insertFinancialBossTrade(bfasFeeType, feeTypeCode, fee, oldFee, 0, resTypeCode, resKindCode, capacityTypeCode, cardNum, "", "", "", "", "", "", btd);
        }
    }

    private void dealWithFeeSub(BusiTradeData btd) throws Exception
    {
        List<FeeTradeData> feeTrades = btd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
        String tradeTypeCode = btd.getTradeTypeCode();
        if (feeTrades == null || feeTrades.size() == 0)
        {
            if ("234".equals(tradeTypeCode))
            {
                int fee = Integer.parseInt(btd.getMainTradeData().getRsrvStr3() == null ? "0" : btd.getMainTradeData().getRsrvStr3());
                this.insertFinancialBossTrade("10", "10", fee, fee, 0, btd.getMainTradeData().getRsrvStr5(), btd.getMainTradeData().getRsrvStr8(), "", 0, "", "", "", "", "", "", btd);
            }
            return;
        }
        if ("461".equals(tradeTypeCode) || "416".equals(tradeTypeCode) || "419".equals(tradeTypeCode) || "421".equals(tradeTypeCode))
        {
            return;
        }
        int fee = 0;
        int oldFee = 0;

        for (FeeTradeData feeData : feeTrades)
        {
            String bfasFeeType = "";
            fee = Integer.parseInt(feeData.getFee());
            oldFee = Integer.parseInt(feeData.getOldfee());
            String feeMode = feeData.getFeeMode();
            String feeTypeCode = feeData.getFeeTypeCode();

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
            if ("10".equals(feeTypeCode) && "0".equals(feeMode) && !"146".equals(tradeTypeCode))
            {
                // 有专门的方法处理卡费
                continue;
            }

            if (fee == 0 && oldFee == 0)
            {
                if ("234".equals(tradeTypeCode))
                {
                    this.insertFinancialBossTrade("10", "10", fee, oldFee, 0, "", "", "", 0, "", "", "", "", "", "", btd);
                    continue;
                }
            }
            else
            {
                this.insertFinancialBossTrade(bfasFeeType, feeTypeCode, fee, oldFee, 0, "", "", "", 0, "", "", "", "", "", "", btd);
            }
        }
    }

    private void dealWithSimCard(BusiTradeData btd, List<ResTradeData> resTrades) throws Exception
    {
        if (resTrades == null || resTrades.size() == 0)
        {
            return;
        }

        if ("9721".equals(btd.getMainTradeData().getTradeTypeCode()))
        {
            return;
        }

        int fee = 0, oldFee = 0;
        String feeType = "10", feeItem = "10";

        List<FeeTradeData> feeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
        if (feeDatas != null && feeDatas.size() > 0)
        {
            for (FeeTradeData feeData : feeDatas)
            {
                if ("10".equals(feeData.getFeeTypeCode()) && "0".equals(feeData.getFeeMode()))
                {
                    fee = Integer.parseInt(feeData.getFee());
                    oldFee = Integer.parseInt(feeData.getOldfee());
                }
            }
        }
        String buyCardFlag = "";
        int formFee = 0;
        for (ResTradeData resTrade : resTrades)
        {
            if ("1".equals(resTrade.getResTypeCode()) && BofConst.MODIFY_TAG_ADD.equals(resTrade.getModifyTag()))
            {
                this.insertFinancialBossTrade(feeType, feeItem, fee, oldFee, formFee, resTrade.getRsrvStr2(), resTrade.getRsrvStr1(), "", 0, "1", "", "", "", "", "", btd);
                // 查询sim卡的资源相关信息
                if ("10".equals(btd.getTradeTypeCode()) || "142".equals(btd.getTradeTypeCode()))
                {
                    if ("A".equals(resTrade.getRsrvTag2()))
                    {
                        // sim卡买断
                        String buyCardFee = resTrade.getRsrvNum5();
                        if (StringUtils.isBlank(buyCardFee))
                        {
                            buyCardFee = "0";
                        }
                        formFee = Integer.parseInt(buyCardFee);
                        this.insertFinancialBossTrade(feeType, feeItem, (-1) * formFee, (-1) * formFee, formFee, resTrade.getRsrvStr2(), resTrade.getRsrvStr1(), "", 0, "2", "", "", "", "", "", btd);
                        break;
                    }
                }
            }
        }
    }

    private void insertFinancialBossTrade(String feeTypeCode, String feeItemTypeCode, int fee, int oldFee, int formFee, String resTypeCode, String resKindCode, String capacityTypeCode, int cardNum, String buyCardFlag, String taxFee, String rate,
            String noTaxFee, String taxTag1, String taxFee3, BusiTradeData btd) throws Exception
    {
        MainTradeData mainTradeData = btd.getMainTradeData();
        String tradeId = btd.getTradeId();
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
        String acceptTime = btd.getRD().getAcceptTime();
        String deviceModelCode = null;
        String deviceTypeCode = null;
        String terminalOrderId = null;
        String deviceProduct = null;
        String procurementType = null;
        String payMoneyCode = "Z";
        String agencyId = null;
        int presentFee = 0;
        int saleNum = 1;

        if (cardNum > 0)
        {
            saleNum = cardNum;
        }

        String saleTypeCode = "1";// 正常销售
        if (oldFee > 0)
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

        List<AccountTradeData> accounts = btd.getTradeDatas(TradeTableEnum.TRADE_ACCOUNT);
        String acctPayModeCode = "";
        if (accounts != null && accounts.size() > 0)
        {
            acctPayModeCode = accounts.get(0).getPayModeCode();
        }
        else
        {
            acctPayModeCode = btd.getRD().getUca().getAccount().getPayModeCode();
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
            List<PayMoneyTradeData> payMoneys = btd.getTradeDatas(TradeTableEnum.TRADE_PAYMONEY);
            if (payMoneys != null && payMoneys.size() > 0)
            {
                payMoneyCode = payMoneys.get(0).getPayMoneyCode();
            }
        }

        if ("240".equals(tradeTypeCode) || "252".equals(tradeTypeCode) || "255".equals(tradeTypeCode) || "3814".equals(tradeTypeCode) || "3815".equals(tradeTypeCode))
        {
            if (fee == 0 && oldFee == 0)
            {
                // 应收实收都为0，返回
                return;
            }

            List<SaleActiveTradeData> saleActives = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
            if (saleActives != null && saleActives.size() > 0)
            {
                SaleActiveTradeData saleActive = saleActives.get(0);
                campnId = saleActive.getProductId() + "|" + saleActive.getPackageId();
            }

            List<SaleGoodsTradeData> saleGoods = btd.getTradeDatas(TradeTableEnum.TRADE_SALEGOODS);
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
        financialData.setUserTypeCode(btd.getRD().getUca().getUser().getUserTypeCode());

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

        financialData.setRsrvTag1(buyCardFlag);
        if ("2".equals(buyCardFlag))
        {
            financialData.setRsrvTag2("A");
        }

        financialData.setRsrvNum1(mainTradeData.getUserId());
        financialData.setRsrvNum2(saleNum + "");

        btd.add(null, financialData);
    }
}
