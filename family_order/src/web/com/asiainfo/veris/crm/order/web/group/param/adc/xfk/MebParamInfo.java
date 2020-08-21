
package com.asiainfo.veris.crm.order.web.group.param.adc.xfk;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationxxtinfo.RelationXXTInfoIntfViewUtil;

public class MebParamInfo extends IProductParamDynamic
{

    public IData getFamSnXxtDisByajax(IBizCommon bp, IData data) throws Throwable
    {
    	IData result = new DataMap();
    	IData results = new DataMap();
    	String serialNumber = data.getString("FAM_SN", "");
    	//true 订购过校讯通10套餐，false 没有订购过
    	boolean isXxtdisFlag = RelationXXTInfoIntfViewUtil.qryXFKMemInfoBySNandUserIdA(bp, serialNumber);
       result.put("isXxtDisFlag", isXxtdisFlag);
       results.put("AJAX_DATA", result);
       return results;


    }

    /**
     * 调用学护卡平台校验接口，校验号码是否已经录入
     *
     * @param bp
     * @param data
     * @return
     * @throws Throwable
     */
    public IData QryXfkNumIsExists(IBizCommon bp, IData data) throws Throwable
    {
        IData result = new DataMap();
        IData results = new DataMap();
        String serialNumber = data.getString("FAM_SN", "");
        IDataset selectedElement = new DatasetList(data.getString("SELECTED_ELEMENT"));
        String groupId = data.getString("GROUP_ID", "");
        boolean flag = true;
        String bizServCode = "";

        IData elementMap = null;
        IDataset attrParamList = null;
        IData attrParamMap = null;
        for(int i=0; i < selectedElement.size(); i++) {
            elementMap = selectedElement.getData(i);
            if ("574401".equals(elementMap.getString("ELEMENT_ID", ""))) {
                attrParamList = elementMap.getDataset("ATTR_PARAM");
                for (int j = 0; j < attrParamList.size(); j++) {
                    attrParamMap = attrParamList.getData(j);
                    if (!IDataUtil.isEmpty(attrParamMap.getData("PLATSVC"))) {
                        bizServCode = attrParamMap.getData("PLATSVC").getString("pam_BIZ_IN_CODE");
                    }
                }
            }
        }

        IData param = new DataMap();
        param.put("EC_ID", groupId);
        param.put("MOB_NUM", serialNumber);
        param.put("BIZ_SERV_CODE", bizServCode);
        param.put("OPR_CODE", "01");

        IDataset resultDs = CSViewCall.call(bp, "SS.XfkFamNumSynSVC.qryXfkNumIsExists", param);
        if(IDataUtil.isNotEmpty(resultDs) && ("0000".equals(resultDs.getData(0).getString("X_RESULTCODE")) || "00".equals(resultDs.getData(0).getString("X_RESULTCODE")))){
            flag = true;
        }else{
            flag = false;
        }
        result.put("isExistsFlag", flag);
        results.put("AJAX_DATA", result);
        return results;
    }

}
