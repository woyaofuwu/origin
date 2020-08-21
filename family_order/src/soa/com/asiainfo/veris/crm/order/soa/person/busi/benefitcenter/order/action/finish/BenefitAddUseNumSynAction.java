package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.BenefitCenterBean;

/**
 * 增加使用次数,若当前权益标识未解绑,且已同步取消预约车牌,需在增加次数同时调用预约车辆接口
 * @author 梁端刚
 * @version V1.0
 * @date 2020/1/15 20:40
 */
public class BenefitAddUseNumSynAction implements ITradeFinishAction {

    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String tradeId = mainTrade.getString("TRADE_ID");
        String modifyTag = mainTrade.getString("RSRV_STR4");
        if(!"2".equals(modifyTag)){
            return;
        }
        IDataset tradeOthers = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId, PersonConst.BENEFIT_TAG);
        if(IDataUtil.isNotEmpty(tradeOthers)){
            IData tradeOther = tradeOthers.first();
            String bookOrderIdTag = tradeOther.getString("RSRV_STR7");
            if("0".equals(bookOrderIdTag)){
                //0表示已取消,已取消预约
                String carNum = tradeOther.getString("RSRV_VALUE");
                String startDate = tradeOther.getString("START_DATE");
                String endDate = tradeOther.getString("END_DATE");
                String instId = tradeOther.getString("INST_ID");
                String userId = tradeOther.getString("USER_ID");
                //根据instId查tf_f_user_other记录
                IDataset otherInfosByInst = UserOtherInfoQry.getByInstIdAndUserId(instId, userId);
                IData otherInfoByInst = otherInfosByInst.first();
                String otherInfoBookorderIdTag = otherInfoByInst.getString("RSRV_STR7");
                String endDateValid = otherInfoByInst.getString("RSRV_DATE1");
                if(SysDateMgr.compareTo(SysDateMgr.getSysTime(),endDateValid)<0&&"0".equals(otherInfoBookorderIdTag)){
                    //增加次数时,若当前车牌未解绑,且已取消预约,需调停简单车辆预约接口
                    IData abilityData = new DataMap();
                    abilityData.put("carNum", carNum);
                    abilityData.put("service", PersonConst.BENEFIT_TINGJD_BOOKADD_SERVICE);
                    abilityData.put("authStartDt", SysDateMgr.date2String(SysDateMgr.string2Date(startDate, SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));
                    abilityData.put("authEndDt",  SysDateMgr.date2String(SysDateMgr.string2Date(endDate, SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));
                    abilityData.put("bookStartDt",  SysDateMgr.date2String(SysDateMgr.string2Date(startDate, SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));
                    abilityData.put("bookEndDt",  SysDateMgr.date2String(SysDateMgr.string2Date(endDate, SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));

                    IDataset partners = CommparaInfoQry.getCommparaAllColByParser("CSM", PersonConst.BENEFIT_STATIC_CONFIG, PersonConst.BENEFIT_STATIC_ENTERPRISE,  "0898");
                    if(IDataUtil.isEmpty(partners)|| StringUtils.isBlank(partners.first().getString("PARA_CODE1"))){
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "停简单COMMPARAM7174未配置ENTERPRISE");
                    }
                    abilityData.put("enterpriseId",partners.first().getString("PARA_CODE1"));
                    BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
                    IData bookAddResult =  bean.callAbilityForTingJQ(PersonConst.BENEFIT_TINGJD_BOOKADD_URL, abilityData);
                    String bookorderId = bookAddResult.getString("orderId");
                    otherInfoByInst.put("RSRV_STR6", bookorderId);
                    otherInfoByInst.put("RSRV_STR7", "1");
                    Dao.update("TF_F_USER_OTHER", otherInfoByInst,new String[]{"INST_ID","PARTITION_ID"});
                }
            }
        }
    }
}
