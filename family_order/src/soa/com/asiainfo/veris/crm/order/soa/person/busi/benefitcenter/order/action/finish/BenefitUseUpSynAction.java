package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.BenefitCenterBean;

/**
 * 免费停车权益次数用尽,需同步停简单,调用注销车牌,撤销预约车辆
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/24 22:49
 */
public class BenefitUseUpSynAction implements ITradeFinishAction {

    @Override
    public void executeAction(IData mainTrade) throws Exception {
        //查询免费停车权益剩余次数
        String rightId=mainTrade.getString("RSRV_STR1");
        String discntCode=mainTrade.getString("RSRV_STR2");
        String relId=mainTrade.getString("RSRV_STR3");
        String userId=mainTrade.getString("USER_ID");
        String serialNumber=mainTrade.getString("SERIAL_NUMBER");

        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        DataMap param = new DataMap();
        param.put("SERIAL_NUMBER",serialNumber);
        param.put("USER_ID",userId);
        param.put("RIGHT_ID",rightId);
        param.put("DISCNT_CODE",discntCode);
        int remainUseNum = bean.queryRemainUseNum(param);
        if(remainUseNum<=0){
            //无剩余次数需同步停简单,调用注销车牌,撤销预约车辆
            //查询当前生效的车牌
            param.clear();
            param.put("USER_ID",userId);
            param.put("RIGHT_ID",rightId);
            param.put("DISCNT_CODE",discntCode);
            param.put("REL_ID",relId);
            IDataset benefitRelIdInfos = bean.queryRelIdBindedByUserId(param);
            if(IDataUtil.isNotEmpty(benefitRelIdInfos)){
                IData benefitRelIdInfo = benefitRelIdInfos.first();
                String orderId = benefitRelIdInfo.getString("RSRV_STR6");
                String orderIdTag = benefitRelIdInfo.getString("RSRV_STR7");
                if(StringUtils.isNotBlank(orderId)&&"1".equals(orderIdTag)){
                    //调撤销车辆预约接口
                    IData abilityData = new DataMap();
                    abilityData.put("orderId", orderId);
                    abilityData.put("service", PersonConst.BENEFIT_TINGJD_BOOKREMOVE_SERVICE);
                    bean.callAbilityForTingJQ(PersonConst.BENEFIT_TINGJD_BOOKREMOVE_URL, abilityData);
                    benefitRelIdInfo.put("RSRV_STR7", "0");
                    Dao.update("TF_F_USER_OTHER", benefitRelIdInfo,new String[]{"INST_ID","PARTITION_ID"});
                }
            }

        }

    }
}
