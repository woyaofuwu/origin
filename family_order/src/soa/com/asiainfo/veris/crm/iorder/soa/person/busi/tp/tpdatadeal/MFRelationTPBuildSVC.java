package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.person.busi.family.mfc.MfcCommonUtil;

/**
 全国家庭网业务办理 甩单
 **/
public class MFRelationTPBuildSVC extends ReqBuildService {
    @Override
    public IData initReqData(IData input) throws Exception {
        String serialNumber = input.getString("SERIAL_NUMBER","");
        String userIdA = input.getString("USER_ID_A","");
        if(StringUtils.isEmpty(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40004);
        }
//        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
//        IDataset uuList = RelaUUInfoQry.getRelaUUInfoByRol(ucaData.getUserId(), "45");
//        if (IDataUtil.isEmpty(uuList)){
//            CSAppException.apperr(FamilyException.CRM_FAMILY_84);
//        }
        String relation_type_code = "MF";
        IData uu = null;
//        IDataset uuinfos = MfcCommonUtil.getRelationUusByUserSnRole(serialNumber, relation_type_code,null,null);
        IDataset uuinfos = MfcCommonUtil.getSEL_USER_ROLEA(userIdA,null,relation_type_code,null);
        boolean checkMember = false;
        String mainSN = null,productOfferingId = null,remarks = null;
        if(IDataUtil.isEmpty(uuinfos)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400017,userIdA,relation_type_code);
        }else{
            int size = uuinfos.size();
            for (int i = 0;i < size;i++) {
                IData temp = uuinfos.getData(i);
                if(StringUtils.equals(serialNumber,temp.getString("SERIAL_NUMBER_B"))){
                    checkMember = true;
                    uu = temp;
                }
                if(StringUtils.equals("1",temp.getString("ROLE_CODE_B"))){
                    mainSN = temp.getString("SERIAL_NUMBER_B");
                    productOfferingId = temp.getString("RSRV_STR2");
                    remarks = temp.getString("REMARK");
                }
            }
        }
        if(uu == null){
            CSAppException.apperr(TpOrderException.TP_ORDER_400018,userIdA,serialNumber,relation_type_code);
        }
        if(StringUtils.isBlank(mainSN)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400019,userIdA,relation_type_code);
        }
        String roleCodeB = uu.getString("ROLE_CODE_B");
        /******很无奈  productCode 在界面上是这么取的*************/
        String productCode = "MFC000001";//全国亲情网
        if(StringUtils.isNotBlank(remarks)){
            String[] remarkArr = remarks.split("\\|");
            if(remarkArr != null && remarkArr.length == 2){
                productCode = remarkArr[1];
            }
        }
        IData result = new DataMap();
        IDataset rtDataset = new DatasetList();
        if("2".equals(roleCodeB)){//副卡
            IDataset productOrderMember = new DatasetList();
            IData mebInfo = new DataMap();
            IData reqInfo = new DataMap();
            mebInfo.put("MEM_TYPE", "1");
            mebInfo.put("MEM_AREA_CODE", "");
            mebInfo.put("MEM_NUMBER",serialNumber );// MemNumbers.getString("SERIAL_NUMBER_B")
            mebInfo.put("MEM_ORDER_NUMBER", "");
            mebInfo.put("FINISH_TIME", "");
            mebInfo.put("NOTES", "");
            mebInfo.put("EFF_TIME", "");
            mebInfo.put("EXP_TIME", "");
            productOrderMember.add(mebInfo);
            reqInfo.put("PRODUCT_CODE",productCode);
            reqInfo.put("CUSTOMER_PHONE", mainSN);
            reqInfo.put("PRODUCT_ORDER_MEMBER", productOrderMember);
            reqInfo.put("ACTION", "51");
            reqInfo.put("ORDER_SOURCE", "01");
            reqInfo.put("PRODUCT_OFFERING_ID",productOfferingId);
            rtDataset = CSAppCall.call("SS.FamilyAllNetBusiManageSVC.controlInfo", reqInfo);
        }else if("1".equals(roleCodeB)){//主卡
            IData reqInfo = new DataMap();
            reqInfo.put("PRODUCT_CODE", productCode);
            reqInfo.put("ORDER_SOURCE", "01");
            reqInfo.put("CUSTOMER_TYPE", "1");
            reqInfo.put("CUSTOMER_PHONE", mainSN);
            reqInfo.put("ACTION", "01");
            reqInfo.put("PRODUCT_OFFERING_ID",productOfferingId);
            rtDataset = CSAppCall.call("SS.FamilyAllNetBusiManageSVC.putMeb", reqInfo);
        }else{
            CSAppException.apperr(FamilyException.CRM_FAMILY_66000239,serialNumber,roleCodeB);
        }
        return input;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return null;
    }

}
