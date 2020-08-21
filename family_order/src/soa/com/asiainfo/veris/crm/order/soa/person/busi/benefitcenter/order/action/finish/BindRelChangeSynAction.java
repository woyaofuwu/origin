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
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.BenefitCenterBean;

/**
 * 机场免费停车权益同步停简单平台,走能开
 * tf_f_user_other  BENEFIT_TAG标识记录
 * RSRV_STR1 权益类型 RSRV_STR2 权益编码 RSRV_STR3权益标识
 * RSRV_STR4 注册车牌接口返回carId      RSRV_STR5 该carId  是否生效 1生效 0失效(不需调用注册车牌)
 * RSRV_STR6 车牌预约接口返回orderId    RSRV_STR7 该orderId是否生效 1生效 0失效
 * RSRV_STR8 车牌是否已入场
 *
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/11 11:13
 */
public class BindRelChangeSynAction implements ITradeFinishAction {
    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String tradeId = mainTrade.getString("TRADE_ID");
        IDataset tradeOthers = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId, PersonConst.BENEFIT_TAG);
        if (IDataUtil.isNotEmpty(tradeOthers)) {
            //先删后加
            for (int i = 0; i < tradeOthers.size(); i++) {
                IData tradeOther = tradeOthers.getData(i);
                String modifyTag = tradeOther.getString("MODIFY_TAG");
                String instId = tradeOther.getString("INST_ID");
                String userId = tradeOther.getString("USER_ID");
                if (BofConst.MODIFY_TAG_DEL.equals(modifyTag)) {
                    //根据instId查tf_f_user_other记录
                    IDataset otherInfosByInst = UserOtherInfoQry.getByInstIdAndUserId(instId, userId);
                    IData otherInfoByInst = otherInfosByInst.first();
                    //RSRV_STR6记录注册车牌返回的停简单车牌ID,如果该字段有值标识已注册
                    String bookorderId = otherInfoByInst.getString("RSRV_STR6");
                    String bookorderIdTag = otherInfoByInst.getString("RSRV_STR7");
                    if (StringUtils.isNotBlank(bookorderId) && "1".equals(bookorderIdTag)) {
                        //调撤销车辆预约接口
                        IData abilityData = new DataMap();
                        abilityData.put("orderId", bookorderId);
                        abilityData.put("service", PersonConst.BENEFIT_TINGJD_BOOKREMOVE_SERVICE);
                        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
                        bean.callAbilityForTingJQ(PersonConst.BENEFIT_TINGJD_BOOKREMOVE_URL, abilityData);
                        otherInfoByInst.put("RSRV_STR7", "0");
                        Dao.update("TF_F_USER_OTHER", otherInfoByInst,new String[]{"INST_ID","PARTITION_ID"});
                    }

                }
            }

            for (int i = 0; i < tradeOthers.size(); i++) {
                IData tradeOther = tradeOthers.getData(i);
                String modifyTag = tradeOther.getString("MODIFY_TAG");
                String carNum = tradeOther.getString("RSRV_VALUE");
                String startDate = tradeOther.getString("START_DATE");
                String endDate = tradeOther.getString("END_DATE");
                String instId = tradeOther.getString("INST_ID");
                String userId = tradeOther.getString("USER_ID");
                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag)) {
                    //根据instId查tf_f_user_other记录
                    IDataset otherInfosByInst = UserOtherInfoQry.getByInstIdAndUserId(instId, userId);
                    IData otherInfoByInst = otherInfosByInst.first();
                    String otherInfoBookorderId = otherInfoByInst.getString("RSRV_STR6");
                    if (StringUtils.isBlank(otherInfoBookorderId)) {
                        IData abilityData = new DataMap();
                        abilityData.put("carNum", carNum);
                        abilityData.put("service", PersonConst.BENEFIT_TINGJD_BOOKADD_SERVICE);
                        abilityData.put("authStartDt", SysDateMgr.date2String(SysDateMgr.string2Date(startDate, SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));
                        abilityData.put("authEndDt",  SysDateMgr.date2String(SysDateMgr.string2Date(endDate, SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));
                        abilityData.put("bookStartDt",  SysDateMgr.date2String(SysDateMgr.string2Date(startDate, SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));
                        abilityData.put("bookEndDt",  SysDateMgr.date2String(SysDateMgr.string2Date(endDate, SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));

                        IDataset partners = CommparaInfoQry.getCommparaAllColByParser("CSM", PersonConst.BENEFIT_STATIC_CONFIG, PersonConst.BENEFIT_STATIC_ENTERPRISE,  "0898");
                        if(IDataUtil.isEmpty(partners)||StringUtils.isBlank(partners.first().getString("PARA_CODE1"))){
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


}
