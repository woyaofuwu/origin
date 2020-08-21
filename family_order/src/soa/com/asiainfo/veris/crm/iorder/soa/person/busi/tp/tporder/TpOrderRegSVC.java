package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporder;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.privm.CheckPriv;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.soa.frame.csservice.common.component.welcome.WelcomeBean;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpbase.TpOrderRuleRouteQry;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpbase.TpOrderTemplCfgQuery;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.rule.CheckTradeBean;

import java.net.URLDecoder;

public class TpOrderRegSVC extends CSBizService {

    /**
     * 提单校验服务（中测）
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkList(IData input) throws Exception{
        String serialNumber = input.getString("SERIAL_NUMBER","");

        //组装checkbefore参数
        IData param = new DataMap();
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        param.put(TpConsts.comKey.serialNumber,serialNumber);
        param.put(TpConsts.comKey.tradeTypeCode,"192");//立即销户业务类型
        param.put("USER_ID",ucaData.getUserId());
        param.put("CUST_ID",ucaData.getCustId());
        param.put("X_CHOICE_TAG","0");

        //checkbefore规则校验
        CheckTradeBean bean = BeanManager.createBean(CheckTradeBean.class);
        IDataset dataset = bean.checkBeforeTrade(param);
        if(DataUtils.isEmpty(dataset)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400033);
        }
        IDataset tipsError = dataset.first().getDataset("TIPS_TYPE_ERROR");
//        IData tipsError = new DataMap(URLDecoder.decode(URLDecoder.decode(tipsTypeError,"UTF-8"),"UTF-8"));
        if(DataUtils.isEmpty(tipsError)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400033);
        }

        //错误规则提示分类
        IData data = new DataMap();
        data.put("ERROR_SET",tipsError);
        data.put("SERIAL_NUMBER",serialNumber);
        IData ruleData = checkRuleExist(data);
        if(DataUtils.isEmpty(ruleData)){
            ruleData.put("RULE_TYPE","无规限制，可直接进行业务办理");
        }
        return ruleData;
    }

    /**
     * 规则列表校验是否存在规则模板表中
     * @param data
     * @return
     * @throws Exception
     */
    public IData checkRuleExist(IData data)throws Exception{
        IData result = new DataMap();
        IDataset errorSet = data.getDataset("ERROR_SET");
        if(DataUtils.isNotEmpty(errorSet)){
            IDataset tpRuleList = new DatasetList();
            IDataset unTpRuleList = new DatasetList();
            IDataset refuseRuleList = new DatasetList();
            for (int i = 0; i < errorSet.size();i++){
                IData errorRule = errorSet.getData(i);
                String errorCode = errorRule.getString("TIPS_CODE");

                //根据规则编码获取模板配置
                IData qryParam = new DataMap();
                qryParam.put("WITH_TYPE", TpConsts.withType.WITH_TYPE1);
                qryParam.put("WITH_OBJ",errorCode);
                IData tpOrderTemplCfg = TpOrderTemplCfgQuery.getOrderTempl(qryParam);
                if(DataUtils.isNotEmpty(tpOrderTemplCfg) && StringUtils.isNotEmpty(errorCode)){
                    String dealType = tpOrderTemplCfg.getString("DEAL_TYPE");
                    if(TpConsts.DealType.auto.equals(dealType)){
                        errorRule.put("RULE_TYPE","可甩单规则，可以进行甩单");
                        tpRuleList.add(errorRule);
                    }else if(TpConsts.DealType.page.equals(dealType)){
                        String menuId = tpOrderTemplCfg.getString("DEAL_ACTION");
                        boolean hasPriv = CheckPriv.checkMenuPermission(getVisit().getStaffId(),menuId);
                        if(hasPriv){
                            IData menuInfo = WelcomeBean.qrySystemGuiMenu(menuId);
                            errorRule.put("MENU_URL",menuInfo.getString("MENU_URL"));
                            errorRule.put("MENU_TITLE",menuInfo.getString("MENU_TITLE"));
                            errorRule.put("MENU_PATH_NAME",menuInfo.getString("MENU_PATH_NAME"));
                        }else {
                            errorRule.put("MENU_URL","无权访问");
                        }
                        errorRule.put("RULE_TYPE","跳转规则，需跳转办理完相应业务后，再返回进行甩单");
                        unTpRuleList.add(errorRule);
                    }else if(TpConsts.DealType.specialType.equals(dealType)){
                        IData param =  new DataMap();
                        param.put("TEMPL_ID",tpOrderTemplCfg.getString("TEMPL_ID"));
                        IData specalProcessCfg = TpOrderRuleRouteQry.getOrderRoute(param);
                        String routeAction = specalProcessCfg.getString("ROUTE_ACTION");
                        String routeStr = specalProcessCfg.getString("ROUTE_STR");

                        param.clear();
                        param.put(TpConsts.comKey.serialNumber,data.getString(TpConsts.comKey.serialNumber));
                        IData actionAfter = call(routeAction,param);
                        boolean flag = actionAfter.getBoolean(routeStr);
                        if(flag){
                            String menuId = specalProcessCfg.getString("MENU_ID");
                            boolean hasPriv = CheckPriv.checkMenuPermission(getVisit().getStaffId(),menuId);
                            if(hasPriv){
                                IData menuInfo = WelcomeBean.qrySystemGuiMenu(menuId);
                                errorRule.put("MENU_URL",menuInfo.getString("MENU_URL"));
                                errorRule.put("MENU_TITLE",menuInfo.getString("MENU_TITLE"));
                                errorRule.put("MENU_PATH_NAME",menuInfo.getString("MENU_PATH_NAME"));
                            }else {
                                errorRule.put("MENU_URL","无权访问");
                            }
                            errorRule.put("RULE_TYPE","跳转规则，需跳转办理完相应业务后，再返回进行甩单");
                            unTpRuleList.add(errorRule);
                        }else {
                            errorRule.put("RULE_TYPE","可甩单规则，可以进行甩单");
                            tpRuleList.add(errorRule);
                        }
                    }
                }else {
                    errorRule.put("RULE_TYPE","阻断规则，不能进行甩单");
                    refuseRuleList.add(errorRule);
                }
            }

            result.put("TP_RULE_LIST",tpRuleList);
            result.put("UNTP_RULE_LIST",unTpRuleList);
            result.put("REFUSE_RULE_LIST",refuseRuleList);
        }
        return result;
    }

    public IData call(String svcName,IData data)throws Exception{
        ServiceResponse response = BizServiceFactory.call(svcName, data);
        return response.getBody();
    }

    /**
     * 订单创建（中测）
     * @param input
     * @return
     * @throws Exception
     */
    public IData orderCreateReg(IData input) throws Exception{
        String serialNumber = input.getString("SERIAL_NUMBER","");

        //组装checkbefore参数
        IData param = new DataMap();
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        param.put(TpConsts.comKey.serialNumber,serialNumber);
        param.put(TpConsts.comKey.tradeTypeCode,"192");//立即销户业务类型
        param.put("USER_ID",ucaData.getUserId());
        param.put("CUST_ID",ucaData.getCustId());
        param.put("X_CHOICE_TAG","0");

        //checkbefore规则校验
        CheckTradeBean bean = BeanManager.createBean(CheckTradeBean.class);
        IDataset dataset = bean.checkBeforeTrade(param);
        if(DataUtils.isEmpty(dataset)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400033);
        }
        IDataset tipsError = dataset.first().getDataset("TIPS_TYPE_ERROR");
//        IData tipsError = new DataMap(URLDecoder.decode(URLDecoder.decode(tipsTypeError,"UTF-8"),"UTF-8"));
        if(DataUtils.isEmpty(tipsError)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400033);
        }

        //错误规则提示分类
        IData data = new DataMap();
        data.put("ERROR_SET",tipsError);
        data.put("SERIAL_NUMBER",serialNumber);
        IData ruleData = checkRuleExist(data);
        if(DataUtils.isEmpty(ruleData)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400029);
        }
        IDataset refuseRuleList = ruleData.getDataset("REFUSE_RULE_LIST");
        if(DataUtils.isNotEmpty(refuseRuleList)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400030);
        }
        IDataset untpRuleList = ruleData.getDataset("UNTP_RULE_LIST");
        if(DataUtils.isNotEmpty(untpRuleList)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400031);
        }
        //获得可甩单的规则
        String tpRuleList = ruleData.getString("TP_RULE_LIST");
        IDataset ruleList = dealTpRuleList(tpRuleList,input);
        data.put("OBJ_LIST",ruleList);
        //甩单提交
        IDataset output = CSAppCall.call("SS.TpOrderSVC.toRuleListSubmit", data);
        if(DataUtils.isNotEmpty(output)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400032);
        }

        StringBuilder orderIds = new StringBuilder();
        for (int i = 0; i < output.size();i++){
            orderIds.append(output.get(i));
        }
        IData orderIdData = new DataMap();
        orderIdData.put("ORDER_IDS",orderIds.toString());

        return orderIdData;
    }

    /**
     * 规则组装
     * @param tpRuleList
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset dealTpRuleList(String tpRuleList,IData data)throws Exception{
        String serialNumber = data.getString("SERIAL_NUMBER");
        String custName = "123";
        String tradeTypeCode = "192";//立即销户
        IDataset ruleList = null;
        if(StringUtils.isNotEmpty(tpRuleList)){
            ruleList = new DatasetList(URLDecoder.decode(URLDecoder.decode(tpRuleList,"UTF-8"),"UTF-8"));
            for (int i = 0; i < ruleList.size();i++){
                IData rule = ruleList.getData(i);
                String tipsCode = rule.getString("TIPS_CODE");
                rule.put("WITH_OBJ",tipsCode);
                rule.put("ACCESS_NUMBER",serialNumber);
                rule.put("SERIAL_NUMBER",serialNumber);
                rule.put("CUST_NAME",custName);
                rule.put("TRADE_TYPE_CODE",tradeTypeCode);
            }
        }
        return ruleList;
    }

}
