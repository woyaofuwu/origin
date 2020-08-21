package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.OneCardMultiNoBean;

/**
 *  甩单和多号取消处理
 *  SS.OneCardMultiNoRegSVC.tradeReg
 *  com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino.buildrequest.BuildOneCardMultiNoRequestData
 **/
public class OneCardMultiNoCancelTpDealSVC extends ReqBuildService {

    private final String relationTypeCode = "M2";

    @Override
    public IData initReqData(IData input) throws Exception {
        //1、获取和多号数据
        String serialNumber = input.getString("SERIAL_NUMBER");
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        String userId = ucaData.getUserId();
        IDataset uuInfos = RelaUUInfoQry.getRelaUUInfoByUserIda(userId,relationTypeCode,null);

        IData param = new DataMap();
        if(DataUtils.isNotEmpty(uuInfos)){//主卡
            param.put("NUMBERTYPE","1");
        }else {
            param.put("NUMBERTYPE","0");//副卡
        }
        param.put("SERIAL_NUMBER",input.getString("SERIAL_NUMBER"));
        OneCardMultiNoBean bean = BeanManager.createBean(OneCardMultiNoBean.class);
        IDataset dataset = bean.qryRelationListForCRM(input);

        IData svcParam = new DataMap();
        if(DataUtils.isNotEmpty(uuInfos)) {//主卡

        }else {
            svcParam = getIntfMsg(serialNumber,dataset);
        }
        return svcParam;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return "3798";
    }

    /**
     * 拼装接口数据
     * @param serialNumber
     * @param dataset
     * @return
     * @throws Exception
     */
    private IData getIntfMsg(String serialNumber, IDataset dataset)throws Exception{
        IData param = new DataMap();
        if(DataUtils.isNotEmpty(dataset)){
            for (int i = 0; i < dataset.size();i++){
                IData additionalCardInfo = dataset.getData(i);
                String serialNumberB = additionalCardInfo.getString("SERIAL_NUMBER_B");
                if(serialNumber.equals(serialNumberB)){
                    param.put("ORDERNO",additionalCardInfo.getString("ORDERNO"));
                    param.put("SERIAL_NUMBER_A",additionalCardInfo.getString("SERIAL_NUMBER_A"));
                    param.put("SERIAL_NUMBER_B",additionalCardInfo.getString("SERIAL_NUMBER_B"));
                    param.put("CATEGORY",additionalCardInfo.getString("CATEGORY"));
                    break;
                }
            }
        }
        param.put("SERIAL_NUMBER",serialNumber);
        param.put("NUMBERTYPE","0");
        param.put("MOSP_CATEGORY","虚拟副号");
        param.put("CHECK_MODE","1");
        param.put("TRADE_TYPE_CODE","3798");
        return param;
    }
}
