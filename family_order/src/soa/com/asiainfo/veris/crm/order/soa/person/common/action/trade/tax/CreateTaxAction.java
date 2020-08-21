 
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.tax;

import java.util.Iterator;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TaxException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.TaxCalcUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTaxTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ElementTaxInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.FeeItemTaxInfoQry;

public class CreateTaxAction implements ITradeAction
{

    private IDataset createTaxForGift(BusiTradeData btd) throws Exception
    {
        return null;
    }

    /**
     * 非视同销售
     * 
     * @param btd
     * @return
     * @throws Exception
     */
    private IDataset createTaxForNotAsSale(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        UcaData uca = btd.getRD().getUca();
        String productId = uca.getProductId();
        IDataset taxDataList = new DatasetList();
        String goodsName = "";
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        if ("255".equals(tradeTypeCode) || "257".equals(tradeTypeCode) || "258".equals(tradeTypeCode) || "252".equals(tradeTypeCode) || "3814".equals(tradeTypeCode) || "3815".equals(tradeTypeCode))
        {
            // 255非签约类购机活动受理
            // 258非签约类购机活动活动付款预受理
            // 257营销活动货到付款预受理
            // 252非签约类营销活动受理
            // 3814无线固话营销活动受理
        	// 3815铁通营销活动受理
            // 这5类活动的费用配置都是配的240，所以这里要临时改成240
            tradeTypeCode = "240";
        }

        if ("240".equals(tradeTypeCode) || "256".equals(tradeTypeCode))
        {
            List<SaleGoodsTradeData> saleGodosTDList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEGOODS);
            for (int i = 0, size = saleGodosTDList.size(); i < size; i++)
            {
                SaleGoodsTradeData saleGoodsTD = saleGodosTDList.get(i);
                String resTypeCode = saleGoodsTD.getResTypeCode();
                if (!"4".equals(resTypeCode))
                {
                    continue;
                }
                String saleProductId = saleGoodsTD.getProductId();
                String salePackageId = saleGoodsTD.getPackageId();
                String elementId = "-1";// 目前先写死-1，暂时没有场景用到具体的elementId
                String deviceCost = saleGoodsTD.getDeviceCost();
                if (StringUtils.isBlank(deviceCost))
                {
                    deviceCost = "0";
                }
                goodsName = saleGoodsTD.getGoodsName();

                IDataset taxList = ElementTaxInfoQry.qryTaxByPackageId(tradeTypeCode, saleProductId, salePackageId, elementId, tradeEparchyCode);
                if (IDataUtil.isEmpty(taxList) || taxList.size() > 1)
                {
                    CSAppException.apperr(TaxException.CRM_TAX_1);
                }
                IData taxInfo = taxList.getData(0);
                String taxType = taxInfo.getString("TYPE");
                String taxRate = taxInfo.getString("RATE");

                if (!"4".equals(taxType))
                {
                    // 仅处理非视同销售的
                    continue;
                }

                // 这一段先这么写吧，由于现在设计上存在一定的缺陷，没找到好办法填写feeTypeCode等值
                String feeTypeCode = "60";
                String factFee = "0";
                List<FeeTradeData> feeTDList = btd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
                for (int j = 0, feeSize = feeTDList.size(); j < feeSize; j++)
                {
                    FeeTradeData feeTD = feeTDList.get(j);
                    if ("0".equals(feeTD.getFeeMode()))
                    {
                        feeTypeCode = feeTD.getFeeTypeCode();
                        factFee = feeTD.getFee();
                        break;
                    }
                }

                IData taxData = new DataMap();
                taxData.put("USER_ID", uca.getUserId());
                taxData.put("FEE_MODE", "0");
                taxData.put("FEE_TYPE_CODE", feeTypeCode);
                taxData.put("FACT_PAY_FEE", factFee);
                taxData.put("FEE", deviceCost);
                taxData.put("RATE", taxRate);
                taxData.put("TYPE", taxType);
                taxData.put("COUNT", "1");
                taxData.put("GOODS_NAME", goodsName);
                taxDataList.add(taxData);
            }
        }

        return taxDataList;
    }

    /**
     * 这种是针对收了用户钱的 针对营销活动货到付款预受理的情况可能还有些不支持，因为其收了钱，但按新系统模式可能没生成sale_active台账
     * 
     * @param btd
     * @return
     * @throws Exception
     */
    private IDataset createTaxForPay(BusiTradeData btd) throws Exception
    {
        String tradeTypeCode = btd.getTradeTypeCode();
        UcaData uca = btd.getRD().getUca();
        String productId = uca.getProductId();
        IDataset taxDataList = new DatasetList();
        String goodsName = "";
        String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
        if ("255".equals(tradeTypeCode) || "257".equals(tradeTypeCode) || "258".equals(tradeTypeCode) || "252".equals(tradeTypeCode) || "3814".equals(tradeTypeCode) || "3815".equals(tradeTypeCode))
        {
            // 255非签约类购机活动受理
            // 258非签约类购机活动活动付款预受理
            // 257营销活动货到付款预受理
            // 252非签约类营销活动受理
            // 3814无线固话营销活动受理
        	// 3815铁通营销活动受理
            // 这5类活动的费用配置都是配的240，所以这里要临时改成240
            tradeTypeCode = "240";
        }
        
        if("9115".equals(tradeTypeCode)){	//如果是铁通终端销售不计算税率
        	return new DatasetList();
        }
        

        if ("240".equals(tradeTypeCode))
        {
            List<SaleActiveTradeData> saleActiveTDList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
            if (saleActiveTDList == null || saleActiveTDList.size() == 0)
            {
                return new DatasetList();
            }
        }

        List<FeeTradeData> feeTDList = btd.getTradeDatas(TradeTableEnum.TRADE_FEESUB);
        for (int i = 0, size = feeTDList.size(); i < size; i++)
        {
            FeeTradeData feeTD = feeTDList.get(i);
            String feeMode = feeTD.getFeeMode();
            String feeTypeCode = feeTD.getFeeTypeCode();
            String fee = feeTD.getFee();
            if (!"0".equals(feeMode) || Integer.parseInt(fee) <= 0)
            {
                // 如果不是营业费或费用小于等于0，则不计税。
                // 这里小于0的判断是暂时的，后续还要看
                continue;
            }

            // 查询税率
            IDataset taxList = new DatasetList();
            if ("240".equals(tradeTypeCode) || "256".equals(tradeTypeCode))
            {
                // 营销活动的比较特殊
                List<SaleActiveTradeData> saleActiveTDList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
                SaleActiveTradeData saleActiveTD = saleActiveTDList.get(0);
                String saleProductId = saleActiveTD.getProductId();
                String salePackageId = saleActiveTD.getPackageId();
                String elementId = "-1";

                taxList = ElementTaxInfoQry.qryTaxByPackageId(tradeTypeCode, saleProductId, salePackageId, elementId, tradeEparchyCode);
                if (IDataUtil.isEmpty(taxList) || taxList.size() > 1)
                {
                    CSAppException.apperr(TaxException.CRM_TAX_1);
                }
                IData taxInfo = taxList.getData(0);
            }
            else
            {
                // 先根据用户的主产品查，查不到则查productId=-1的
                taxList = FeeItemTaxInfoQry.qryTaxByTradeProductFee(tradeTypeCode, productId, feeMode, feeTypeCode, tradeEparchyCode);
                if (IDataUtil.isEmpty(taxList))
                {
                    taxList = FeeItemTaxInfoQry.qryTaxByTradeProductFee(tradeTypeCode, "-1", feeMode, feeTypeCode, tradeEparchyCode);
                }
            }
            //不知道为什么142没有配手续费的税，却没有报错，有可能是142没有收手续费，而跨区补卡是要收手续费，149的税配置暂跟142保持一致。避免报错，单独处理下
            if ("149".equals(tradeTypeCode) && IDataUtil.isEmpty(taxList)) {
            	continue;
            } 
            if (IDataUtil.isEmpty(taxList) || taxList.size() > 1)
            {
                CSAppException.apperr(TaxException.CRM_TAX_1);
            }
            IData taxInfo = taxList.getData(0);// 税率配置表
            IData taxData = new DataMap();// 当前费用的信息

            String taxRate = taxInfo.getString("RATE");
            String taxType = taxInfo.getString("TYPE");

            if ("4".equals(taxType))
            {
                // 非视同销售的要单独算，逻辑与其他的有区别
                continue;
            }
            taxData.put("USER_ID", uca.getUserId());
            taxData.put("FEE_MODE", feeMode);
            taxData.put("FEE_TYPE_CODE", feeTypeCode);
            taxData.put("FACT_PAY_FEE", fee);
            taxData.put("FEE", fee);
            taxData.put("RATE", taxRate);
            taxData.put("TYPE", taxType);
            taxData.put("COUNT", "1");
            taxData.put("GOODS_NAME", goodsName);
            taxDataList.add(taxData);
        }

        return taxDataList;
    }

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        IDataset taxForPay = createTaxForPay(btd);

        IDataset taxForGift = createTaxForGift(btd);

        IDataset taxForNotAsSale = createTaxForNotAsSale(btd);

        IDataset taxInfo = new DatasetList();
        if (IDataUtil.isNotEmpty(taxForPay))
        {
            taxInfo.addAll(taxForPay);
        }
        if (IDataUtil.isNotEmpty(taxForGift))
        {
            taxInfo.addAll(taxForGift);
        }
        if (IDataUtil.isNotEmpty(taxForNotAsSale))
        {
            taxInfo.addAll(taxForNotAsSale);
        }

        TaxCalcUtils.getTradeFeeTaxForCalculate(taxInfo);

        setRegTradeFeeTax(taxInfo, btd);

    }

    public void setRegTradeFeeTax(IDataset taxInfos, BusiTradeData btd) throws Exception
    {
        for (Iterator iterator = taxInfos.iterator(); iterator.hasNext();)
        {
            IData taxInfo = (IData) iterator.next();
            FeeTaxTradeData tradeData = new FeeTaxTradeData(taxInfo);

            btd.add(btd.getRD().getUca().getSerialNumber(), tradeData);
        }
    }
}
