
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.product.deposit;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.BaseSaleDepositData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.saleactive.order.requestdata.BaseSaleActiveReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

/**
 * 职责：处理预存赠送他人的逻辑 giftUserId, giftStartDate, giftEndDate
 * 海南，如果预存是赠送他人，那么，tf_b_trade_sale_deposit表的USER_ID插赠送人的USER_ID，USER_ID_A插业务办理人的User_Id； 并且需要生成赠送人的营销活动台帐
 * 
 * @author Mr.Z
 */
public class GiftUserProductAction implements IProductModuleAction
{

    @SuppressWarnings("unchecked")
    private void createGiftUserActiveInfo(BusiTradeData btd, BaseSaleDepositData depositReqData) throws Exception
    {
        SaleActiveTradeData satd = new SaleActiveTradeData();
        BaseSaleActiveReqData sard = (BaseSaleActiveReqData) btd.getRD();

        satd.setUserId(depositReqData.getGiftUserId());
        satd.setSerialNumber(depositReqData.getSerialNumberB());
        satd.setCampnCode(sard.getCampnCode());
        satd.setCampnId(sard.getCampnId());
        satd.setCampnType(sard.getCampnType());
        satd.setStartDate(depositReqData.getGiftStartDate());
        satd.setEndDate(depositReqData.getGiftEndDate());
        satd.setPackageId(sard.getPackageId());
        satd.setProductId(sard.getProductId());
        satd.setProcessTag("0");
        satd.setRelationTradeId(sard.getTradeId());
        satd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        IData productInfo = UProductInfoQry.qrySaleActiveProductByPK(depositReqData.getProductId());
        satd.setProductName(productInfo.getString("PRODUCT_NAME"));
        satd.setProductMode(productInfo.getString("PRODUCT_MODE"));
        String packageId = depositReqData.getPackageId();
        IData packageInfo = PkgInfoQry.getPackageByPK(packageId);
        satd.setPackageName(packageInfo.getString("PACKAGE_NAME"));
        satd.setRemark("预存赠送他人时生成的业务资料");
        satd.setOperFee(btd.getOperFee());
        satd.setForegift(btd.getForeGift());
        satd.setAdvancePay(btd.getAdvanceFee());
        satd.setMonths(String.valueOf(SysDateMgr.monthInterval(depositReqData.getStartDate(), depositReqData.getEndDate())));
        satd.setInstId(SeqMgr.getInstId());
        satd.setRsrvTag1("1");
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(depositReqData.getGiftUserId());
        satd.setRsrvStr21(userInfo.getString("CITY_CODE"));

        btd.add(btd.getRD().getUca().getSerialNumber(), satd);
    }

    @SuppressWarnings("unchecked")
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        SaleDepositTradeData depositTradeData = (SaleDepositTradeData) dealPmtd;

        BaseSaleDepositData depositReqData = (BaseSaleDepositData) depositTradeData.getPmd();

        if (StringUtils.isBlank(depositReqData.getSerialNumberB()))
            return;

        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();

        String preType = saleActiveReqData.getPreType();
        String isConFirm = saleActiveReqData.getIsConfirm();

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
            return;

        if (SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(saleActiveReqData.getCallType()))
            return;

        depositTradeData.setUserId(depositReqData.getGiftUserId());
        depositTradeData.setStartDate(depositReqData.getGiftStartDate());
        depositTradeData.setEndDate(depositReqData.getGiftEndDate());

        createGiftUserActiveInfo(btd, depositReqData);
    }

}
