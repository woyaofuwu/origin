package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class DestroyUserTpDealSVC extends ReqBuildService {

    private final String relationTypeCode = "T2";

    @Override
    public IData initReqData(IData input) throws Exception {
        String serialNumber = input.getString(TpConsts.comKey.serialNumber);
        if(StringUtils.isEmpty(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40004);
        }
        //获取手机号绑定的固话号
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        IDataset uuList = RelaUUInfoQry.check_byuserida_idbzm_A(ucaData.getUserId(),relationTypeCode,null);
        if(DataUtils.isEmpty(uuList)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400028);
        }
        String fixedTelNum = uuList.first().getString("SERIAL_NUMBER_B");

        //返回数据参数
        IData data = new DataMap();
        data.put(TpConsts.comKey.serialNumber,fixedTelNum);
        data.put(TpConsts.comKey.tradeTypeCode,getTradeTypeCode(null));
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        return data;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return "9723";
    }
}
