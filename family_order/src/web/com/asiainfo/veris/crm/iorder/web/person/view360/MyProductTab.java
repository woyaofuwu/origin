
package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.View360Const;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class MyProductTab extends PersonBasePage {

    /**
     * 产品信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();

        // 如果查询所有信息复选框勾选，将 SelectTag = 1 传入后台
        String selectTag = "true".equals(data.getString("QUERY_ALL", "false")) ? "1" : "0";
        data.put("SelectTag", selectTag);

        if (StringUtils.isNotBlank(data.getString("USER_ID", ""))) {
            data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
            IDataset productInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryProductInfo", data);
            IDataset svcInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserSvcInfo", data);
            IDataset discntInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserDiscntInfo", data);
            IDataset platInfo;
            if ("1".equals(selectTag)) {
                platInfo = CSViewCall.call(this, "CS.PlatComponentSVC.getUserPlatSvcs12", data);
            } else {
                platInfo = CSViewCall.call(this, "CS.PlatComponentSVC.getUserPlatSvcs11", data);
            }

            if (IDataUtil.isNotEmpty(platInfo)) {
                for (Object obj : platInfo) {
                    IData plat = (IData) obj;
                    String bizStateColor = View360Const.PLATSVC_COMMON + View360Const.PLATSVC_COLOR.get(plat.getString("BIZ_STATE_CODE"));
                    plat.put("BIZ_STATE_COLOR", bizStateColor);
                }
            }
            //我的权益查询接口，参考优惠的写
            IDataset welfareInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserWelfareInfo", data);
            //REQ201910090025 关于新一代客服系统、网厅、短厅、中国移动APP不显示集团短彩信行业短信成员的需求
            IDataset returnProductList = new DatasetList();
           // IDataset configList = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryTdBQryConfig", data);
            IDataset configList= pageutil.getStaticList("OFFER_CODE_NO_DISPLAY_MODE");
            for (int i = 0; i < productInfo.size(); i++) {
                IData productData = productInfo.getData(i);
                String productId = productData.getString("PRODUCT_ID");
                if (IDataUtil.isNotEmpty(DataHelper.filter(configList, "DATA_ID=" + productId))) {//是否存在 在则不显示
                    continue;

                }
                returnProductList.add(productData);

            }
            //REQ201910090025 关于新一代客服系统、网厅、短厅、中国移动APP不显示集团短彩信行业短信成员的需求
            setProductInfos(returnProductList);
           // setProductInfos(productInfo);
            setSvcInfos(svcInfo);
            setDiscntInfos(discntInfo);
            setPlatInfos(platInfo);
            setWelfareInfos(welfareInfo);
        }
    }

    public abstract void setProductInfos(IDataset productInfos);

    public abstract void setSvcInfos(IDataset svcInfos);

    public abstract void setDiscntInfos(IDataset discntInfos);

    public abstract void setPlatInfos(IDataset platInfos);
    
    public abstract void setWelfareInfos(IDataset welfareInfos);
}
