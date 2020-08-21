package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.applyparam;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;
import com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.EcBbossCommonViewUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;

public abstract class BbossApplyParam extends BizTempComponent {
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "scripts/icsserv/component/enterprise/applyparam/BbossApplyParam.js";

        if (isAjax) {
            includeScript(writer, jsFile, false, false);
        } else {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }

        IData param = getPage().getData();
        String ibsysid = param.getString("IBSYSID");// 驳回的情况会有值
        String merchUserId = param.getString("EC_USER_ID");
        String ecOfferCode = param.getString("OFFER_CODE");
        String operType = param.getString("OPER_TYPE");
        String offerCode = getPage().getData().getString("OFFER_CODE");
        String merchpProductId = EcBbossCommonViewUtil.merchToProduct(this, offerCode, 1, true);
        IData info = new DataMap();
        IDataset bbossProductList = new DatasetList();
        if (StringUtils.isNotBlank(ibsysid)) {
            bbossProductList = queryBbossProductFromRep(ibsysid, ecOfferCode);
            info.put("IS_CHECK", "true");// 驳回时默认勾上复选框
            if (!EcEsopConstants.APPLYT_DEL.equals(operType)) // 注销的驳回不用"待设置"标记
            {
                info.put("IS_REP", "true");// 驳回显示"待设置"标记
            }

        } else {
            if (EcEsopConstants.APPLY_ADD.equals(operType)) {
                bbossProductList = queryBbossProductFromUpc();
                // 1.跨省专线业务（商品）,2.跨省互联网专线（商品）,3.跨省数据专线2.0（商品）支持新增多少
                if ("01011301".equals(merchpProductId) || "01011304".equals(merchpProductId) || "01011306".equals(merchpProductId)) {
                    info.put("IS_BBOSS_DATALINE", "true");// 开通上面几个产品可以添加多条
                }
            } else {
                // bbossProductList = queryBbossUserProductInfo(merchUserId,ecOfferCode);
                if (EcEsopConstants.APPLYT_DEL.equals(operType))// 注销时默认勾上复选框
                {
                    info.put("IS_CHECK", "true");
                }
            }
        }

        int productListSize = 0;
        if (IDataUtil.isNotEmpty(bbossProductList)) {
            productListSize = bbossProductList.size();
        }
        info.put("PRODUCT_LIST_SIZE", productListSize);
        info.put("BBOSS_OPER_TYPE", operType);
        setInfo(info);
    }

    private IDataset queryBbossProductFromUpc() throws Exception {
        String offerCode = getPage().getData().getString("OFFER_CODE");
        IDataset bbossProductList = new DatasetList();
        // UpcViewCall.queryOfferJoinRelOfferByOfferId(this, offerCode, UpcConst.ELEMENT_TYPE_CODE_PRODUCT, UpcConst.PM_OFFER_JOIN_REL_TYPE_POWER100, null);
        if (IDataUtil.isNotEmpty(bbossProductList)) {
            for (int i = 0; i < bbossProductList.size(); i++) {
                IData productList = bbossProductList.getData(i);
                productList.put("ROW_NUM", i);
            }

        }
        setSubOfferList(bbossProductList);
        return bbossProductList;
    }

    /**
     * 审核不通 查询EOP产品信息
     * 
     * @param ibsysid
     * @param ecOfferCode
     * @return
     * @throws Exception
     */
    private IDataset queryBbossProductFromRep(String ibsysid, String ecOfferCode) throws Exception {
        IDataset bbossProductList = new DatasetList();// WorkfromViewCall.qryEopProductByIbsysId(this, ibsysid);
        if (IDataUtil.isNotEmpty(bbossProductList)) {
            for (int i = 0, iSize = bbossProductList.size(); i < iSize; i++) {
                IData producData = bbossProductList.getData(i);
                String productId = producData.getString("OFFER_CODE");
                if (ecOfferCode.equals(productId)) {
                    bbossProductList.remove(i);
                    break;
                }

            }
            for (int z = 0; z < bbossProductList.size(); z++) {
                IData productList = bbossProductList.getData(z);
                ;
                productList.put("ROW_NUM", z);
            }
        }
        setSubOfferList(bbossProductList);
        return bbossProductList;
    }

    // 查询子产品
    public IDataset queryBbossUserProductInfo(String merchUserId, String ecOfferCode) throws Exception {
        // 现在已经存在UU关系的BBOSS子用户
        IDataset bbossUserProductList = EcBbossCommonViewUtil.queryBbossUserProductInfo(this, merchUserId, ecOfferCode);
        if (IDataUtil.isNotEmpty(bbossUserProductList)) {
            for (int i = 0; i < bbossUserProductList.size(); i++) {
                IData productList = bbossUserProductList.getData(i);
                productList.put("ROW_NUM", i);
            }

        }
        setSubOfferList(bbossUserProductList);
        return bbossUserProductList;
    }

    public abstract void setSubOfferList(IDataset subOfferList) throws Exception;

    public abstract void setInfo(IData info) throws Exception;
}
