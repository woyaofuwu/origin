
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.IProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.BaseProductModuleTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;

import java.util.Iterator;
import java.util.List;

/**
 * @author Administrator
 */
public class SvcTrade extends BaseProductModuleTrade implements IProductModuleTrade
{
    @Override
    public ProductModuleTradeData createSubProductModuleTrade(ProductModuleData dealPmd, List<ProductModuleData> productModuleDatas, UcaData uca, BaseReqData brd, ProductTimeEnv env) throws Exception
    {
        SvcData svcData = (SvcData) dealPmd;
        String acceptTime = brd.getAcceptTime();
        SvcTradeData svcTd = new SvcTradeData();
        if (svcData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD))
        {
            if (uca.getUserSvcBySvcId(svcData.getElementId()).size() > 0)
            {
                // 台账中有删除的不算重复订购
                boolean findDel = false;
                for (Iterator iterator = productModuleDatas.iterator(); iterator.hasNext();)
                {
                    ProductModuleData productModuleData = (ProductModuleData) iterator.next();

                    if (productModuleData.getElementId().equals(svcData.getElementId()) && productModuleData.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
                    {
                        findDel = true;
                    }

                }
                String orderMode = svcData.getOfferCfg().getOrderMode();
                if (!"R".equals(orderMode) && !findDel)
                {
                    String serviceName = USvcInfoQry.getSvcNameBySvcId(svcData.getElementId());
                    CSAppException.apperr(ElementException.CRM_ELEMENT_34, serviceName);
                }
            }
            svcTd.setUserId(uca.getUserId());
            svcTd.setElementId(svcData.getElementId());
            svcTd.setElementType(svcData.getElementType());
            svcTd.setCampnId(svcData.getCampnId());
            svcTd.setInstId(SeqMgr.getInstId());
            svcTd.setProductId(svcData.getProductId());
            svcTd.setPackageId(svcData.getPackageId());
            svcTd.setUserIdA("-1");
            String startDate = ProductModuleCalDate.calStartDate(svcData, env);
            if (startDate.compareTo(brd.getAcceptTime()) < 0)
            {
                startDate = acceptTime;
            }
            svcTd.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(svcData, startDate);
            svcTd.setEndDate(endDate.substring(0, 10) + SysDateMgr.getEndTime235959());
            svcTd.setRemark(svcData.getRemark());
            svcTd.setMainTag("0");
            String productId = svcData.getProductId();
            // 修改兼容非正常产品报错，使得没有主产品的服务也能走公共逻辑
            if (!WelfareConstants.offerCode.FAKE_CODE.getValue().equals(productId))
            {
                OfferCfg mainOfferCfg = OfferCfg.getInstance(productId, "P");
                OfferCfg childOfferCfg = mainOfferCfg.findChild("S", svcData.getElementId());
                if (childOfferCfg != null)
                {
                    svcTd.setMainTag(childOfferCfg.isMain() ? "1" : "0");
                }
            }
        }
        else
        {

            if (StringUtils.isBlank(svcData.getInstId()))
            {
                // 如果build层未传入instId,则取用户资料中同一元素编码的第一条记录，如果该元素有多条的话，在build层就要负责将instId设置好
                SvcTradeData userSvc = uca.getUserSvcBySvcId(svcData.getElementId()).get(0);
                svcTd = userSvc.clone();
            }
            else
            {
                SvcTradeData userSvc = uca.getUserSvcByInstId(svcData.getInstId());
                if (userSvc == null)
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, svcData.getElementId() + "|" + USvcInfoQry.getSvcNameBySvcId(svcData.getElementId()) + "|" + "用户未订购该服务或已退订！");
                }
                svcTd = userSvc.clone();
            }
            if (BofConst.MODIFY_TAG_DEL.equals(svcData.getModifyTag()))
            {
                String cancelDate = "";
                if (env == null || StringUtils.isBlank(env.getBasicAbsoluteCancelDate()))
                {
                    if ("0".equals(svcData.getCancelTag()) || StringUtils.isBlank(svcData.getCancelTag()))
                    {
                        cancelDate = SysDateMgr.getLastSecond(acceptTime);
                    }
                }

                if (StringUtils.isBlank(cancelDate))
                {
                    cancelDate = ProductModuleCalDate.calCancelDate(svcData, env);
                }
                svcTd.setEndDate(cancelDate);
            }
            else if (BofConst.MODIFY_TAG_INHERIT.equals(svcData.getModifyTag()))
            {
                String oldProductId = svcTd.getProductId();
                String oldPackageId = svcTd.getPackageId();
                svcTd.setProductId(svcData.getProductId());
                svcTd.setPackageId(svcData.getPackageId());
                svcTd.setRsrvStr3(oldProductId);
                svcTd.setRsrvStr4(oldPackageId);
            }
            else if (BofConst.MODIFY_TAG_UPD.equals(svcData.getModifyTag()))
            {
                if (!svcData.getProductId().equals(svcTd.getProductId()) || !svcData.getPackageId().equals(svcTd.getPackageId()))
                {
                    String oldProductId = svcTd.getProductId();
                    String oldPackageId = svcTd.getPackageId();
                    svcTd.setProductId(svcData.getProductId());
                    svcTd.setPackageId(svcData.getPackageId());
                    svcTd.setRsrvStr3(oldProductId);
                    svcTd.setRsrvStr4(oldPackageId);
                }
            }
            else if (BofConst.MODIFY_TAG_FORCE_END.equals(svcData.getModifyTag()))
            {
                svcTd.setEndDate(svcData.getEndDate());
                svcData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            }
        }
        svcTd.setModifyTag(svcData.getModifyTag());

        if (svcData.getAttrs() != null && svcData.getAttrs().size() > 0 && !BofConst.MODIFY_TAG_DEL.equals(svcTd.getModifyTag()))
        {
            AttrTrade.createAttrTradeData(svcTd, svcData.getAttrs(), uca);
        }

        return svcTd;
    }

}
