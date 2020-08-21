package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.paramtrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.IParamTrans;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

/**
 * @ClassName FamilyImsCreateRegTrans
 * @Description 新增固话家庭成员参数转换
 * @Author yuyz
 * @Date 2020/8/8 15:43
 * @Version 1.0
 */
public class FamilyImsCreateRegTrans implements IParamTrans {

    @Override
    public IData getTransParamters(IData role) throws Exception {
        IData imsData = new DataMap();
        imsData.putAll(role);
        imsData.put("PRODUCT_TYPE_CODE", "IMSP");//固话产品类型
        imsData.put("PRODUCT_TYPE_NAME", "IMS固话");//固话产品品牌
        imsData.put("HAS_REAL_NAME_INFO", "HAS_REAL_NAME_INFO");
        // 特殊转换
        String mainSerialNumber = imsData.getString("MEMBER_MAIN_SN");//451工单的值
        imsData.put("SERIAL_NUMBER", mainSerialNumber);

        String imsNum = imsData.getString("FIX_NUMBER");
        imsData.put("OLD_FIX_NUMBER",imsData.getString("FIX_NUMBER"));
        if (!imsNum.startsWith("0898") && imsNum.length() != 12) {
            imsData.put("WIDE_SERIAL_NUMBER", "0898" + imsNum);
        }
        dealImsOffers(imsData);
        imsData.put("CALL_REGSVC", "SS.IMSLandLineRegSVC.tradeReg");
        imsData.put("TRADE_TYPE_CODE", FamilyConstants.ROLE_TRADE_TYPE.IMS_OPEN);
        return imsData;
    }
    /**
     * @return void
     * @author yuyz
     * @Description 根据前台传进来的productId, 去获取产品下的D ,S 元素
     * @Date 16:46 2020/8/3
     * @Param [role]
     **/
    private void dealImsOffers(IData imsData) throws Exception {
        String eparchyCode = imsData.getString("EPARCHY_CODE");
        String tradeTypeCode = imsData.getString("TRADE_TYPE_CODE");
        if(!imsData.containsKey("OFFERS")){
            CSAppException.apperr(WidenetException.CRM_WIDENET_9);
        }
        IDataset imsOffers = new DatasetList(imsData.getString("OFFERS", "[]"));
        IDataset selectedEles = new DatasetList();
        if (IDataUtil.isNotEmpty(imsOffers)) {
            for (int i = 0; i < imsOffers.size(); i++) {
                IData imsOffer = imsOffers.getData(i);
                IData param = new DataMap();
                param.put("EPARCHY_CODE", eparchyCode);
                param.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
                param.put("NEW_PRODUCT_ID", imsOffer.getString("ELEMENT_ID"));
                param.put("TRADE_TYPE_CODE", tradeTypeCode);
                imsData.put("PRODUCT_ID", imsOffer.getString("ELEMENT_ID"));
                imsData.put("PRODUCT_NAME", imsOffer.getString("ELEMENT_NAME"));
                IDataset allImsOfffers = CSAppCall.call("CS.SelectedElementSVC.getUserOpenElements", param);
                selectedEles.addAll(allImsOfffers.first().getDataset("SELECTED_ELEMENTS"));
            }
            imsData.put("SELECTED_ELEMENTS", selectedEles);
        }
    }

}
