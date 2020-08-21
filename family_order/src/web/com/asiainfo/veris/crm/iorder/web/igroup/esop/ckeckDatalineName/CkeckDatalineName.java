package com.asiainfo.veris.crm.iorder.web.igroup.esop.ckeckDatalineName;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class CkeckDatalineName extends BizHttpHandler {

    public void ckeckDatalineName() throws Exception {
        IData data = getData();
        String tradeName = data.getString("TRADENAME", "");
        data.put("DATA_LINE_NAME", tradeName);
        IData datalineNameInfo = CSViewCall.callone(this, "SS.QcsGrpIntfSVC.qryGrpUserDatalineByName", data);
        IData results = new DataMap();
        results.put("AJAX_DATA", datalineNameInfo);

        String ajaxdatastr = results.getString("AJAX_DATA", "");

        if (StringUtils.isNotBlank(ajaxdatastr)) {
            this.setAjax(new DataMap(ajaxdatastr));
        }
    }

}
