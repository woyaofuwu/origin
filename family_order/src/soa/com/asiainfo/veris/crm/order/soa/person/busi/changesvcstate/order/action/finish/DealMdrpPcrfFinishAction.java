package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class DealMdrpPcrfFinishAction implements ITradeFinishAction {

    /**
     * 1，根据手机号码查询三户资料获取对应的品牌编码
     * 2、查询当前的业务类型是否为停开机类型的
     * 3、根据停开机类型发送PCC签约/解签约操作
     *
     * @param mainTrade
     * @throws Exception
     * @author tanjl
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        IDataset pdList = CommparaInfoQry.getCommparaAllCol("CSM", "2001", "MDRP_PCCPCRF_STOPANDSUSPEND", CSBizBean.getVisit().getStaffEparchyCode());
        if (IDataUtil.isNotEmpty(pdList)) {
            IDataset tradeTypeCodeFilter = DataHelper.filter(pdList, "PARA_CODE1=" + tradeTypeCode);
            if (IDataUtil.isNotEmpty(tradeTypeCodeFilter)) {
                //查询三户资料获取品牌
                UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
                if ("MDRP".equals(uca.getBrandCode())) {     //一号双终端副设备
                    String policyId = "11000010000000000201811260914171";    //默认的策略ID
                    String operatorMind = "131".equals(tradeTypeCode) ? "6" : "7";     //1,2,4,5降速  3恢复  6一号双终端副设备停机 7一号双终端副设备复机
                            policyId = tradeTypeCodeFilter.getData(0).getString("PARA_CODE6", policyId);
                    operatorMind = tradeTypeCodeFilter.getData(0).getString("PARA_CODE5", operatorMind);   //停机的情况下设置为签约，复机的情况下设置成解除签约
                    IData param = new DataMap();
                    param.put("ROUTE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
                    param.put("USER_ID", uca.getUserId());
                    param.put("SERIAL_NUMBER", serialNumber);
                    param.put("OPERATOR_MIND", operatorMind);
                    param.put("MDRP_STOP_STRATEGY_ID", policyId);
                    CSAppCall.call("SS.PccActionSVC.pccIntf", param);
                }
            }
        }
    }
}
