package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporder;

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
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tporderoper.TpOrderOperBean;
import com.asiainfo.veris.crm.order.pub.consts.TpConsts;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class TpOrderSVC extends TpService {

    // 新增
    public boolean insertOrder(IData param) throws Exception {
        TpOrderBean tpOrderBean = BeanManager.createBean(TpOrderBean.class);
        boolean result = tpOrderBean.insertOrder(param);
        return result;
    }

    //查询甩单主表
    public IDataset queryTpOrderInfos(IData data) throws Exception
    {
        TpOrderBean tpOrderBean = BeanManager.createBean(TpOrderBean.class);
        return tpOrderBean.queryTpOrderInfos(data, this.getPagination());
    }

    /**
     * 甩单开始
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset toRuleListSubmit(IData param)throws Exception{
        return this.doThrowOrder(param);
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
                            unTpRuleList.add(errorRule);
                        }else {
                            tpRuleList.add(errorRule);
                        }
                    }
                }else {
                    refuseRuleList.add(errorRule);
                }
            }

            result.put("TP_RULE_LIST",tpRuleList);
            result.put("UNTP_RULE_LIST",unTpRuleList);
            result.put("REFUSE_RULE_LIST",refuseRuleList);
        }
        return result;
    }

    //查询甩单主表
    public IDataset queryUncheckOrder(IData data) throws Exception
    {
        TpOrderBean tpOrderBean = BeanManager.createBean(TpOrderBean.class);
        return tpOrderBean.queryUncheckOrder(data, this.getPagination());
    }

    //审核甩单工单
    public IData auditTpOrder(IData data) throws Exception
    {
        TpOrderBean tpOrderBean = BeanManager.createBean(TpOrderBean.class);
        TpOrderOperBean tpOrderOperBean = BeanManager.createBean(TpOrderOperBean.class);
        //获得工单号字符串
        String orderId = data.getString("ORDER_ID");
        if(StringUtils.isEmpty(orderId)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400011);
        }
        String[] orderIds = orderId.split(",");
        //校验甩单工单数据状态是否未审核
        for(int i =0;i<orderIds.length;i++){
            String order = orderIds[i];
            IData inParam = new DataMap();
            inParam.put("ORDER_ID",order);
            IDataset trueData=tpOrderBean.queryUncheckOrder(inParam, null);
            if(DataUtils.isEmpty(trueData)){
                CSAppException.apperr(TpOrderException.TP_ORDER_400012);
            }
            inParam.put("IS_AUDIT",data.getString("IS_AUDIT"));
            //根据审核状态修改主表状态
            tpOrderBean.auditTpOrder(inParam);
            //组装参数，记录甩单操作表
            IData operParam = new DataMap();
            operParam.put("ORDER_ID",order);
            operParam.put("OPER_ID", SeqMgr.getTpOrderOperId());
            operParam.put("OPER_TYPE","0");//操作类型 待确定
            operParam.put("CREATE_DATE", SysDateMgr.getSysDate());
            operParam.put("DONE_DATE", SysDateMgr.getSysDate());
            operParam.put("OP_ID", getVisit().getStaffId());
            operParam.put("ORG_ID", getVisit().getDepartId());
            tpOrderOperBean.insertOrderOper(operParam);
        }
        IData dataTag = new DataMap();
        dataTag.put("SUCCESS", "工单审核成功");
        return dataTag;
    }

    //工单操作包括工单直接归档，撤档等
    public IData submitAction(IData data) throws Exception
    {
        TpOrderBean tpOrderBean = BeanManager.createBean(TpOrderBean.class);
        TpOrderOperBean tpOrderOperBean = BeanManager.createBean(TpOrderOperBean.class);
        String actionType = data.getString("ACTION_TYPE");
        String remarks = data.getString("REMARKS");
        IDataset tabInfos = data.getDataset("TABLE_INFOS");
        if(DataUtils.isEmpty(tabInfos)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"工单号不能为空！");
        }
        //校验选中的工单号状态是否为待处理
        if("1".equals(actionType)){
            for(int i = 0;i<tabInfos.size();i++){
                IData order = tabInfos.getData(i);
                String orderId = order.getString("ORDER_ID");
                String state = order.getString("STATE");
                if(!"0".equals(state)){
                    CSAppException.apperr(CrmCommException.CRM_COMM_103,"工单状态不为待处理，不能直接归档");
                }
                //将主表状态更改为直接归档
                IData inParam = new DataMap();
                inParam.put("ORDER_ID",orderId);
                inParam.put("REMARKS",remarks);
                tpOrderBean.archiveOrder(inParam);
            }
        }else if("2".equals(actionType)){
            for(int i = 0;i<tabInfos.size();i++){
                IData order2 = tabInfos.getData(i);
                String orderId2 = order2.getString("ORDER_ID");
                String isAudit = order2.getString("IS_AUDIT");
                String state = order2.getString("STATE");
                if(!"0".equals(isAudit) && !"2".equals(isAudit)||!"0".equals(state)){
                    CSAppException.apperr(CrmCommException.CRM_COMM_103,"该工单不能进行撤单");
                }
                //将主表状态更改为直接归档
                IData inParam = new DataMap();
                inParam.put("ORDER_ID",orderId2);
                inParam.put("REMARKS",remarks);
                tpOrderBean.cancelOrder(inParam);
            }
        }

        IData dataTag = new DataMap();
        dataTag.put("SUCCESS", "工单操作成功");
        return dataTag;
    }
}
