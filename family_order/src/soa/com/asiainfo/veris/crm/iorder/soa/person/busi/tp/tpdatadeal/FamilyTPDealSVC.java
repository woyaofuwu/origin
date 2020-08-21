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
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * 甩单家庭网处理
 *  SS.DestroyFamilyRegSVC.tradeReg
 *  SS.DelFamilyNetMemberRegSVC.tradeReg  284
 *  com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.buildrequest.BuildDelFamilyNetMemberReqData
 **/
public class FamilyTPDealSVC extends ReqBuildService {
    @Override
    public IData initReqData(IData input) throws Exception {
        String serialNumber = input.getString("SERIAL_NUMBER","");
        if(StringUtils.isEmpty(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40004);
        }
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        IDataset uuList = RelaUUInfoQry.getRelaUUInfoByRol(ucaData.getUserId(), "45");
        if (IDataUtil.isEmpty(uuList)){
            CSAppException.apperr(FamilyException.CRM_FAMILY_84);
        }
        IData uu = uuList.first();
        String roleCodeB = uu.getString("ROLE_CODE_B");
        if("2".equals(roleCodeB)){//副卡
            IDataset list = new DatasetList();
            IData data = new DataMap();
            data.put("SERIAL_NUMBER_B",serialNumber);
            list.add(data);
            input.put("MEB_LIST",list);
            input.put("SVC_KEY","ADDITIONAL_CARD");//副卡处理
        }else if("1".equals(roleCodeB)){//主卡
            input.put("SVC_KEY","MAIN_CARD");//主卡处理
        }else{
            CSAppException.apperr(FamilyException.CRM_FAMILY_66000239,serialNumber,roleCodeB);
        }
        input.put("ROLE_CODE_B",roleCodeB);
//        input.put("YY","1");
        return input;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return null;
    }

    public String getTradeRegSvc(IData input)throws Exception{
        String serialNumber = input.getString("SERIAL_NUMBER","");
        String roleCodeB = input.getString("ROLE_CODE_B","");
        if("2".equals(roleCodeB)){//副卡
            return "SS.DelFamilyNetMemberRegSVC.tradeReg";
        }else if("1".equals(roleCodeB)){
            return "SS.DestroyFamilyRegSVC.tradeReg";
        }else{
            CSAppException.apperr(FamilyException.CRM_FAMILY_66000239,serialNumber,roleCodeB);
        }
        return null;
    }
}
