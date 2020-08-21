
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.product.goods;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.saleactive.order.requestdata.BaseSaleActiveReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

/**
 * 计算回传给资源的代办费
 * 
 * @author Mr.Z
 */
public class DeputyFeeProductAction implements IProductModuleAction
{
    @SuppressWarnings("unchecked")
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();

        String preType = saleActiveReqData.getPreType();
        String isConFirm = saleActiveReqData.getIsConfirm();

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
            return;

        if (SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(saleActiveReqData.getCallType()))
            return;

        SaleGoodsTradeData saleGoodsTradeData = (SaleGoodsTradeData) dealPmtd;

        if (SaleActiveConst.TERMINAL_BUY_TYPE_GIFT.equals(saleGoodsTradeData.getRsrvTag1()))
            return;

        String packageId = saleGoodsTradeData.getPackageId();

        String deductFee = getSaleGoodsCommFee(saleGoodsTradeData, btd);

        IDataset commparaDataset = CommparaInfoQry.getCommparaAllColByParser("CSM", "159", packageId, uca.getUserEparchyCode());

        if (IDataUtil.isNotEmpty(commparaDataset))
        {
            deductFee = get159SpecConfigDeductFee(commparaDataset, uca);

            saleGoodsTradeData.setRsrvStr6("");
        }

        saleGoodsTradeData.setRsrvStr7(deductFee);

        String compnTypde = saleActiveReqData.getCampnType();

        if ("YX04".equals(compnTypde))
            return;

        String tempDeductFee = getSaleGoodsDeDuctCommFee(saleGoodsTradeData, btd); // 新增配置代办费在现网标准情况下再扣减XX元

        if (Integer.parseInt(tempDeductFee) > 0)
        {
            int feeInt = new Integer(deductFee) - new Integer(tempDeductFee);
            deductFee = feeInt < 0 ? "0" : String.valueOf(feeInt);
            saleGoodsTradeData.setRsrvStr7(deductFee);
        }
    }

    private String get159SpecConfigDeductFee(IDataset commparaDataset, UcaData uca) throws Exception
    {
        IData commparaData = commparaDataset.getData(0);

        String deductFee = commparaData.getString("PARA_CODE1", "0");
        String paraCode3 = commparaData.getString("PARA_CODE3");

        if (StringUtils.isNotBlank(paraCode3))
        {
            int paraCode3Int = Integer.parseInt(paraCode3);
            String userOpenDate = uca.getUser().getOpenDate();
            int dayInterval = SysDateMgr.dayInterval(SysDateMgr.getSysTime(), userOpenDate);

            if (dayInterval < paraCode3Int)
            {
                deductFee = commparaData.getString("PARA_CODE2", "0");
            }
        }

        return deductFee;
    }

    private String getRewardType(String productId, String packageId, String eparchyCode) throws Exception
    {
        IDataset params161Dataset = CommparaInfoQry.getCommparaAllColByParser("CSM", "161", productId, eparchyCode);

        String rewardType = "";

        if (IDataUtil.isEmpty(params161Dataset))
            return null;

        for (int i = 0, size = params161Dataset.size(); i < size; i++)
        {
            IData params161Data = params161Dataset.getData(i);
            String tempPackageId = params161Data.getString("PARA_CODE1");

            if ("0".equals(tempPackageId) || packageId.equals(tempPackageId))
            {
                rewardType = params161Data.getString("PARA_CODE2", "");
            }
        }

        return rewardType;
    }

    /**
     * 获取终端代办费
     * 
     * @param saleGoodsTradeData
     * @param btd
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private String getSaleGoodsCommFee(SaleGoodsTradeData saleGoodsTradeData, BusiTradeData btd) throws Exception
    {
        String productId = saleGoodsTradeData.getProductId();
        String packageId = saleGoodsTradeData.getPackageId();
        String eparchyCode = btd.getRD().getUca().getUserEparchyCode();

        String rewardType = getRewardType(productId, packageId, eparchyCode);

        if (StringUtils.isBlank(rewardType))
            return rewardType;

        // 根据销售额获取佣金 para_code3=佣金 para_code1=销售额下限 para_code2=销售额上限

        String operFee = btd.getMainTradeData().getOperFee();
        long operFeeLong = StringUtils.isBlank(operFee) ? 0 : Long.parseLong(operFee);
        String advanceFee = btd.getMainTradeData().getAdvancePay();
        long advanceFeeLong = StringUtils.isBlank(advanceFee) ? 0 : Long.parseLong(advanceFee);
        String foreGiftFee = btd.getMainTradeData().getForegift();
        long foreGiftFeeLong = StringUtils.isBlank(foreGiftFee) ? 0 : Long.parseLong(foreGiftFee);

        long allFee = operFeeLong + advanceFeeLong + foreGiftFeeLong;

        if ("2".equals(rewardType)) // 以终端的活动合约价为准计算薪酬
        {
            if (SaleActiveConst.TERMINAL_BUY_TYPE_PURCHASE.equals(saleGoodsTradeData.getRsrvTag1()))
            {
                int deviceCost = Integer.parseInt(StringUtils.defaultIfEmpty(saleGoodsTradeData.getDeviceCost(), "0")); // 终端结算价,结算价(进货价)

                IDataset params1741Dataset = CommparaInfoQry.getCommparaInfoByCode45("CSM", "1741", productId, deviceCost); // 根据终端结算价获取合约价

                if (IDataUtil.isEmpty(params1741Dataset))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据终端结算价获取活动合约价错误！");
                }

                allFee = params1741Dataset.getData(0).getInt("PARA_CODE1", 0); // para_code1:活动合约价（分）
                saleGoodsTradeData.setRsrvStr6(String.valueOf(allFee)); // 记录下终端活动合约价tf_b_Trade_sale_goods.rsrv_Str6，统计要用
            }
        }

        if (allFee <= 0)
            return "";

        BaseSaleActiveReqData sard = (BaseSaleActiveReqData) btd.getRD();
        String campnType = sard.getCampnType();

        String deductFee = "0";

        IDataset param160Dataset = CommparaInfoQry.getBetwenCommparaInfoByCode1("CSM", "160", String.valueOf(allFee), eparchyCode);

        if (IDataUtil.isNotEmpty(param160Dataset))
        {
            deductFee = param160Dataset.getData(0).getString("PARA_CODE3", "0");
        }

        if ("YX08".equals(campnType) || "YX09".equals(campnType)) // YX08 购机活动（买断供货终端）,YX09 购机活动（虚拟供货终端）
        {
            IDataset param165Dataset = CommparaInfoQry.getBetwenCommparaInfoByCode1("CSM", "165", String.valueOf(allFee), eparchyCode);

            if (IDataUtil.isNotEmpty(param165Dataset))
            {
                deductFee = param165Dataset.getData(0).getString("PARA_CODE3", "0");
            }
        }

        return deductFee;
    }

    /**
     * 获取特殊扣减的费用 param_code=product_id,para_code1=0：该活动产品下所有包的代办费都扣减para_code2分
     * param_code=product_id,para_code1=package_id：该活动产品下para_code1包的代办费都扣减para_code2分
     * 
     * @param saleGoodsTradeData
     * @param btd
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private String getSaleGoodsDeDuctCommFee(SaleGoodsTradeData saleGoodsTradeData, BusiTradeData btd) throws Exception
    {
        String productId = saleGoodsTradeData.getProductId();
        String packageId = saleGoodsTradeData.getPackageId();
        IDataset paramDs = CommparaInfoQry.getCommparaAllColByParser("CSM", "162", productId, "0898");
        String retFee = "0";

        if (IDataUtil.isNotEmpty(paramDs))
        {
            for (int i = 0, size = paramDs.size(); i < size; i++)
            {
                IData dt = paramDs.getData(i);
                String packageIdTmp = dt.getString("PARA_CODE1", "");

                if ("0".equals(packageIdTmp)) // 配置该产品id下所有包都统一扣减一样的代办费
                {
                    retFee = dt.getString("PARA_CODE2", "0");
                    break;
                }

                else if (packageIdTmp.equals(packageId)) // 配置该产品id下某个包扣减para_code2费用
                {
                    retFee = dt.getString("PARA_CODE2", "0");
                    break;
                }
            }
        }
        return retFee;
    }
}
