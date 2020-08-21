package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.offer;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class SelectedOfferSVC extends CSBizService
{
    private static final long serialVersionUID = 5678795070365429545L;

    public IDataset qrySelectedOffers(IData param) throws Exception
    {
        IDataset results = new DatasetList();
        IData resultData = new DataMap();
        String userId = param.getString("SUBSCRIBER_INS_ID");
        String accessNum = param.getString("ACCESS_NUM");
        if (StringUtils.isBlank(userId) && StringUtils.isBlank(accessNum))
        {
            return results;
        }
        
        UcaData uca = null;
        if (StringUtils.isNotBlank(accessNum))
        {
            uca = UcaDataFactory.getNormalUcaForGrp(accessNum, false, false);
        }
        else 
        {
            IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);

            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BofException.CRM_BOF_011, userId);
            }
            accessNum = userInfo.getString("SERIAL_NUMBER");
            uca = UcaDataFactory.getNormalUcaForGrp(accessNum, false, false);
        }
        
        List<ProductTradeData> products = uca.getUserProduct(uca.getProductId());
        List<SvcTradeData> svcs = uca.getUserSvcs();
        List<DiscntTradeData> discnts = uca.getUserDiscnts();
        
        IDataset svcList = new DatasetList();
        IDataset discntList = new DatasetList();
        IDataset mainOfferList = new DatasetList();
        
        if (products == null || products.size() == 0)
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_522, "获取用户产品信息为空！");
        }
        
        IData mainProductData = null;
        String sysDate = SysDateMgr.getSysDate();
        for (ProductTradeData product : products)
        {
            IData productData = product.toData();
            String productId = productData.getString("PRODUCT_ID");
            productData.put("OFFER_NAME", UProductInfoQry.getProductNameByProductId(productId));
            productData.put("OFFER_ID", productId);
            productData.put("OFFER_CODE", productId);
            productData.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
            productData.put("VALID_DATE", productData.getString("START_DATE"));
            productData.put("EXPIRE_DATE", productData.getString("END_DATE"));
            productData.put("ACTION", "exist");
            mainOfferList.add(productData);
            
            // 主产品
            if ("1".equals(product.getMainTag()) && product.getStartDate().compareTo(sysDate) <= 0)
            {
                mainProductData = productData;
            }
        }
        
        if (IDataUtil.isEmpty(mainProductData))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_522, "获取用户主产品信息为空！");
        }
        String mainProductId = mainProductData.getString("OFFER_CODE");
        
        if (svcs != null && svcs.size() > 0)
        {
            for (SvcTradeData svc : svcs)
            {
                IData svcData = svc.toData();
                String serviceId = svcData.getString("SERVICE_ID");
                svcData.put("OFFER_NAME", USvcInfoQry.getSvcNameBySvcId(serviceId));
                svcData.put("OFFER_ID", serviceId);
                svcData.put("OFFER_CODE", serviceId);
                svcData.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_SVC);
                svcData.put("VALID_DATE", svcData.getString("START_DATE"));
                svcData.put("EXPIRE_DATE", svcData.getString("END_DATE"));
                svcData.put("ACTION", "exist");
                svcData.put("REL_OFFER_ID", mainProductId);
                svcData.put("REL_OFFER_INS_ID", mainProductData.getString("INST_ID"));
                svcData.put("OFFER_INS_ID", svcData.getString("INST_ID"));
                svcList.add(svcData);
            }
        }
        
        if (discnts != null && discnts.size() > 0)
        {
            for (DiscntTradeData discnt : discnts)
            {
                IData discntData = discnt.toData();
                String discntCode = discntData.getString("DISCNT_CODE");
                discntData.put("OFFER_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode));
                discntData.put("OFFER_ID", discntCode);
                discntData.put("OFFER_CODE", discntCode);
                discntData.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                discntData.put("VALID_DATE", discntData.getString("START_DATE"));
                discntData.put("EXPIRE_DATE", discntData.getString("END_DATE"));
                discntData.put("ACTION", "exist");
                discntData.put("REL_OFFER_ID", mainProductId);
                discntData.put("REL_OFFER_INS_ID", mainProductData.getString("INST_ID"));
                discntData.put("OFFER_INS_ID", discntData.getString("INST_ID"));
                discntList.add(discntData);
            }
        }
        
        resultData.put("MAIN_OFFERLIST", mainOfferList);
        resultData.put("SVC_OFFERLIST", svcList);
        resultData.put("DISCNT_OFFERLIST", discntList);
        
        if (IDataUtil.isNotEmpty(mainProductData))
        {
            resultData.put("NOW_MAIN_OFFER", mainProductData);
        }
        
        results.add(resultData);
        
        return results;
    }
    
    public IDataset qryForcesOffers(IData param) throws Exception
    {
        IDataset results = new DatasetList();
        IData resultData = new DataMap();
        
        String offerId = param.getString("OFFER_ID");
        if (StringUtils.isBlank(offerId))
        {
            return results;
        }
        
        OfferCfg offerCfg = OfferCfg.getInstance(offerId, BofConst.ELEMENT_TYPE_CODE_PRODUCT);
        if (offerCfg == null)
        {
            return results;
        }
        IData nowMainOffer = offerCfg.toData();
        nowMainOffer.put("OFFER_NAME", offerCfg.getOfferName());
        nowMainOffer.put("OFFER_TYPE", offerCfg.getOfferType());
        nowMainOffer.put("VALID_DATE", SysDateMgr.getSysDate());
        nowMainOffer.put("EXPIRE_DATE", SysDateMgr.END_TIME_FOREVER);
        
        IDataset svcList = new DatasetList();
        IDataset discntList = new DatasetList();
        IDataset mainOfferList = new DatasetList();
        mainOfferList.add(nowMainOffer);
        
        resultData.put("MAIN_OFFERLIST", mainOfferList);
        resultData.put("SVC_OFFERLIST", svcList);
        resultData.put("DISCNT_OFFERLIST", discntList);
        
        if (IDataUtil.isNotEmpty(nowMainOffer))
        {
            resultData.put("NOW_MAIN_OFFER", nowMainOffer);
        }
        
        results.add(resultData);
        
        return results;
    }
    
    public IDataset qryMebSelectedOffers(IData param) throws Exception
    {
        IDataset results = new DatasetList();
        IData resultData = new DataMap();
        
        String userId = param.getString("SUBSCRIBER_INS_ID");
        String userIdA = param.getString("EC_SUBSCRIBER_INS_ID");

        // 查询用户服务
        IDataset userSvcList = UserSvcInfoQry.getUserProductSvc(userId, userIdA, null);
        if (IDataUtil.isNotEmpty(userSvcList))
        {
            for (int i = 0, size = userSvcList.size(); i < size; i++)
            {
                IData userSvc = userSvcList.getData(i); 
                String serviceId = userSvc.getString("SERVICE_ID");
                userSvc.put("OFFER_NAME", userSvc.getString("ELEMENT_NAME"));
                userSvc.put("OFFER_ID", serviceId);
                userSvc.put("OFFER_CODE", serviceId);
                userSvc.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_SVC);
                userSvc.put("VALID_DATE", userSvc.getString("START_DATE"));
                userSvc.put("EXPIRE_DATE", userSvc.getString("END_DATE"));
                userSvc.put("ACTION", "exist");
            }
        }
        
        // 查询用户资费信息
        IDataset userDiscntList = UserDiscntInfoQry.getUserProductDis(userId, userIdA);
        if (IDataUtil.isNotEmpty(userDiscntList))
        {
            for (int i = 0, size = userDiscntList.size(); i < size; i++)
            {
                IData userDiscnt = userDiscntList.getData(i);
                String discntCode = userDiscnt.getString("DISCNT_CODE");
                userDiscnt.put("OFFER_NAME", userDiscnt.getString("ELEMENT_NAME"));
                userDiscnt.put("OFFER_ID", discntCode);
                userDiscnt.put("OFFER_CODE", discntCode);
                userDiscnt.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                userDiscnt.put("VALID_DATE", userDiscnt.getString("START_DATE"));
                userDiscnt.put("EXPIRE_DATE", userDiscnt.getString("END_DATE"));
                userDiscnt.put("ACTION", "exist");
            }
        }

        IDataset mainOfferList = new DatasetList();
        IData nowMainOffer = new DataMap();
        
        IDataset userProductList = UserProductInfoQry.qryGrpMebProduct(userId, userIdA);
        if (ArrayUtil.isNotEmpty(userProductList))
        {
            for (int i = 0, size = userProductList.size(); i < size; i++)
            {
                IData mainOffer = userProductList.getData(i);
                String productId = mainOffer.getString("PRODUCT_ID");
                mainOffer.put("OFFER_NAME", UProductInfoQry.getProductNameByProductId(productId));
                mainOffer.put("OFFER_ID", productId);
                mainOffer.put("OFFER_CODE", productId);
                mainOffer.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
                mainOffer.put("VALID_DATE", mainOffer.getString("START_DATE"));
                mainOffer.put("EXPIRE_DATE", mainOffer.getString("END_DATE"));
                mainOffer.put("ACTION", "exist");
            }

            nowMainOffer = userProductList.getData(0);
            mainOfferList = userProductList;
        }
        
        resultData.put("MAIN_OFFERLIST", mainOfferList);
        resultData.put("SVC_OFFERLIST", userSvcList);
        resultData.put("DISCNT_OFFERLIST", userDiscntList);
        
        if (IDataUtil.isNotEmpty(nowMainOffer))
        {
            resultData.put("NOW_MAIN_OFFER", nowMainOffer);
        }
        
        results.add(resultData);
        
        return results;
    }
    
    public IDataset qryOperOffers(IData param) throws Exception
    {
        IDataset offers = param.getDataset("OFFERS");
        
        if (IDataUtil.isEmpty(offers))
        {
            return offers;
        }
        
        for (int i = 0, size = offers.size(); i < size; i++)
        {
            IData offer = offers.getData(i);
            String offerCode = offer.getString("OFFER_CODE");
            String action = offer.getString("ACTION");
            String offerType = offer.getString("OFFER_TYPE");
            OfferCfg offerCfg = OfferCfg.getInstance(offerCode, offerType);
            offer.put("OFFER_NAME", offerCfg.getOfferName());
            offer.put("ITEM_INDEX", String.valueOf(i));
            if (StringUtils.equals(TRADE_MODIFY_TAG.Add.getValue(), action))
            {
                offer.put("VALID_DATE", SysDateMgr.getSysDate());// 算时间
                offer.put("EXPIRE_DATE", SysDateMgr.getTheLastTime());// 算时间
            }
            else if (StringUtils.equals(TRADE_MODIFY_TAG.DEL.getValue(), action)) 
            {
                offer.put("EXPIRE_DATE", SysDateMgr.getLastDateThisMonth());// 算时间
            }
        }
        
        return offers;
    }
}
