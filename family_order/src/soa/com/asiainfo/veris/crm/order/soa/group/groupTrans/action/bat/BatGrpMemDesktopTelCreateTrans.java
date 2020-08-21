
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat.util.GroupBatTransUtil;

public class BatGrpMemDesktopTelCreateTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String userId = IDataUtil.getMandaData(condData, "USER_ID"); // 集团用户ID

        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 校验短号
        String shortCode = batData.getString("DATA1", ""); // 短号
        if (StringUtils.isBlank(shortCode))
        {
            CSAppException.apperr(GrpException.CRM_GRP_603, serial_number);
        }

        batData.put("SHORT_CODE", shortCode);
        batData.put("USER_ID", userId);
        if (!GroupImsUtil.checkImsShortCode(batData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_502, batData.getString("ERROR_MESSAGE"));
        }

        // 资源信息
        IData resData = new DataMap();
        resData.put("RES_TYPE_CODE", "S");
        resData.put("CHECKED", "true");
        resData.put("DISABLED", "true");
        resData.put("RES_CODE", shortCode);
        resData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        // 产品参数信息
        IData productParam = new DataMap();
        productParam.put("SHORT_CODE", shortCode);
        
        String impiId = condData.getString("HSS_IMPIATTR_IMPI_ID", "");
        if(StringUtils.isNotEmpty(impiId)){
            productParam.put("HSS_IMPIATTR_IMPI_ID", impiId);
        }
        String authType = condData.getString("HSS_AUTH_TYPE", "");
        if(StringUtils.isNotEmpty(authType)){
            productParam.put("HSS_AUTH_TYPE", authType);
        }
        String capsType = condData.getString("HSS_CAPS_TYPE", "");
        if(StringUtils.isNotEmpty(capsType)){
            productParam.put("HSS_CAPS_TYPE", capsType);
        }
        String capsId = condData.getString("HSS_CAPS_ID", "");
        if(StringUtils.isNotEmpty(capsId)){
            productParam.put("HSS_CAPS_ID", capsId);
        }

        IDataset productParamDataset = new DatasetList();
        GroupBatTransUtil.buildProductParam(batData.getString("PRODUCT_ID"), productParam, productParamDataset);

        svcData.put("RES_INFO", IDataUtil.idToIds(resData));
        svcData.put("PRODUCT_PARAM_INFO", productParamDataset);
    }

}
