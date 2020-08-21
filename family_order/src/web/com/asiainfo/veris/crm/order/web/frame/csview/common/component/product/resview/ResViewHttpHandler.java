
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.resview;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public class ResViewHttpHandler extends CSBizHttpHandler
{

    public void getResTypeName() throws Exception
    {
        String resTypeCode = getData().getString("RES_TYPE_CODE", "");
        String resTypeName = StaticUtil.getStaticValueDataSource(getVisit(), Route.CONN_RES, "RES_TYPE", "RES_TYPE_ID", "RES_TYPE_NAME", resTypeCode);
        IData resParamData = new DataMap();
        resParamData.put("RES_TYPE", resTypeName);
        this.setAjax(resParamData);
    }

    public void qryResInfosByMebUserIdGrpUserIdProductId() throws Exception
    {
        IData param = getData();
        IDataset resList = GroupProductUtilView.initResList(this, param.getString("MEB_USER_ID", ""), param.getString("GRP_USER_ID", ""), param.getString("MEB_EPARCHY_CODE", ""));

        this.setAjax(resList);
    }

}
