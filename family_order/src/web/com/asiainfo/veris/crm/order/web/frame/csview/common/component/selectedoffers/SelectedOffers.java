package com.asiainfo.veris.crm.order.web.frame.csview.common.component.selectedoffers;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class SelectedOffers extends CSBizTempComponent
{
    public abstract String getInitService();

    public abstract String getRenderService();

    public abstract String getOperService();

    public abstract void setSvcList(IDataset svcList);

    public abstract void setDiscntList(IDataset discntList);

    public abstract void setMainOfferList(IDataset mainOfferList);

    public abstract void setDistrict(String district);

    public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax)
        {
            includeScript(writer, "scripts/csserv/component/offer/selectedoffers.js");
        }
        else
        {
            this.getPage().addResBeforeBodyEnd("scripts/csserv/component/offer/selectedoffers.js");
        }

        IData data = this.getPage().getData();
        String submitType = data.getString("SUBMIT_TYPE");

        String serviceName = "";
        if ("render".equals(submitType))
        {
            serviceName = this.getRenderService();
        }

        if (StringUtils.isBlank(serviceName))
        {
            return;
        }

        String district = data.getString("ROUTE_CODE", "0898");
        this.setDistrict(district);
        data.put(Route.ROUTE_EPARCHY_CODE, district);

        IData offerStorage = CSViewCall.callone(this, serviceName, data);

        if (IDataUtil.isEmpty(offerStorage))
        {
            return;
        }
        IDataset svcList = offerStorage.getDataset("SVC_OFFERLIST");
        IDataset discntList = offerStorage.getDataset("DISCNT_OFFERLIST");
        IDataset mainOfferList = offerStorage.getDataset("MAIN_OFFERLIST");
        IData nowMainOffer = offerStorage.getData("NOW_MAIN_OFFER");
        IData nextMainOffer = offerStorage.getData("NEXT_MAIN_OFFER");

        this.setMainOfferList(mainOfferList);
        this.setSvcList(svcList);
        this.setDiscntList(discntList);

        IDataset offers = new DatasetList();
        if (IDataUtil.isNotEmpty(svcList))
        {
            offers.addAll(svcList);
        }
        if (IDataUtil.isNotEmpty(discntList))
        {
            offers.addAll(discntList);
        }

        for (int i = 0, size = offers.size(); i < size; i++)
        {
            IData offer = offers.getData(i);
            offer.put("INDEX", i);
        }

        IData ajaxRst = new DataMap();
        ajaxRst.put("OFFERS", offers);
        if (IDataUtil.isNotEmpty(nowMainOffer))
            ajaxRst.put("NOW_MAIN_OFFER", nowMainOffer);

        if (IDataUtil.isNotEmpty(nextMainOffer))
            ajaxRst.put("NEXT_MAIN_OFFER", nextMainOffer);

        this.getPage().setAjax(ajaxRst);
    }
}
