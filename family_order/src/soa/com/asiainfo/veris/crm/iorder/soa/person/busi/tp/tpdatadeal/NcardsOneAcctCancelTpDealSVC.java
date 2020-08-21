package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
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
 * 甩单双卡统一付费取消处理
 * SS.NcardsOneAcctCancelRegSVC.tradeReg
 * com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.buildrequest.BuildNcardsOneAcctCancelReqData
 */
public class NcardsOneAcctCancelTpDealSVC extends ReqBuildService{

    private final String relationTypeCode = "34";

    @Override
    public IData initReqData(IData input) throws Exception {
        //1、根据号码判断是主卡还是副卡
        String serialNumber = input.getString("SERIAL_NUMBER","");
        if(StringUtils.isEmpty(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40004);
        }
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        IDataset uuList = RelaUUInfoQry.getRelaUUInfoByRol(ucaData.getUserId(), relationTypeCode);
        if (IDataUtil.isEmpty(uuList)){
            CSAppException.apperr(FamilyException.CRM_FAMILY_730);
        }
        //2、如果是主卡则取消下面全部副卡；3、如果是副卡则取消当前号码本身
        IData svcParam = new DataMap();
        IData uu = uuList.first();
        String roleCodeB = uu.getString("ROLE_CODE_B");
        if("1".equals(roleCodeB)) {//主卡
            svcParam = getIntfMsg(serialNumber);
        }else if("2".equals(roleCodeB)) {//副卡
            String userIdA = uu.getString("USER_ID_A");
            IDataset uuInfos = RelaUUInfoQry.getSEL_USER_ROLEA(userIdA,"1",relationTypeCode);
            if(DataUtils.isEmpty(uuInfos)){
                CSAppException.apperr(TpOrderException.TP_ORDER_40000);
            }
            String mainCardNumber = uuInfos.first().getString("SERIAL_NUMBER_B");
            svcParam = getIntfMsg(mainCardNumber);
        }else{
            CSAppException.apperr(FamilyException.CRM_FAMILY_66000239,serialNumber,roleCodeB);
        }
        //4、返回参数报文
        return svcParam;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return "324";
    }

    /**
     * 参数拼接
     * @param serialNumber
     * @return
     * @throws Exception
     */
    private IData getIntfMsg(String serialNumber) throws Exception{
        IData svcparam = new DataMap();
        svcparam.put("SERIAL_NUMBER",serialNumber);
        svcparam.put("TRADE_TYPE_CODE","324");
        return svcparam;
    }
}
