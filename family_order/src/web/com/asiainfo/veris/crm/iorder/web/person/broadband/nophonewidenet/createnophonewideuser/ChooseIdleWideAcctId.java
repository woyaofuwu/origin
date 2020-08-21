package com.asiainfo.veris.crm.iorder.web.person.broadband.nophonewidenet.createnophonewideuser;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

import org.apache.tapestry.IRequestCycle;

/**
 * @ClassName ChooseIdleWideAcctId
 * @Description 宽带账号选号
 * @Author ApeJungle
 * @Date 2019/7/8 17:34
 * @Version 1.0
 */
public abstract class ChooseIdleWideAcctId extends PersonBasePage {

    public abstract void setPrefixList(IDataset prefixList);

    public abstract void setPhoneList(IDataset phoneList);

    public abstract void setPageCount(long pageCount);

    public void onInitTrade(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        IDataset prefixList = CSViewCall.call(this, "SS.ChooseIdleWideAcctIdSVC.getSerialNumberPrefixList", data);
        this.setPrefixList(prefixList);
    }

    public void qrySerialNumberList(IRequestCycle cycle) throws Exception {
        IData data = this.getData();
        data.put("SEL_TAG", "1");
        data.put("CODE_AREA_CODE", StringUtils.substring(data.getString("CODE_AREA_CODE"), 0, 3));  
        data.put("MGMT_COUNTY", this.getVisit().getCityCode());

        Pagination page = this.getPagination("pageNav");
        data.put("PAGE_NUM", page.getCurrent());
        data.put("PAGE_SIZE", "18");
        data.put("IS_NEW_SELFTERM", "1");
        data.put(Route.ROUTE_EPARCHY_CODE, "0898");

        IDataset result = CSViewCall.call(this, "SS.ChooseIdleWideAcctIdSVC.qrySerialNumberList", data);
        IDataset outdata = result.first().getDataset("OUTDATA");
        if (IDataUtil.isNotEmpty(outdata)) {
            this.setPhoneList(outdata);
            this.setPageCount(outdata.first().getLong("PAGE_COUNT", 0L));
        }
        this.setAjax(outdata);
    }
}
