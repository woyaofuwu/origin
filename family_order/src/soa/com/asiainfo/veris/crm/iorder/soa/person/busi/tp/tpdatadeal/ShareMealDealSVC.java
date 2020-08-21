package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.biz.exception.BizException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

/**
 * 甩单多终端共享（新）处理
 *  SS.ShareMealRegSVC.tradeReg
 *  com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.querynpmessage.QueryNpMessageBean#queryNpMessage
 **/
public class ShareMealDealSVC extends ReqBuildService {

    @Override
    public IData initReqData(IData input) throws Exception {
        String serialNumber = input.getString("SERIAL_NUMBER","");
        if(StringUtils.isEmpty(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40004);
        }
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);//获得三户信息
        String userId = ucaData.getUserId();
        IData inParam = new DataMap();
        inParam.put("SERIAL_NUMBER",serialNumber);
        inParam.put("USER_ID",userId);
        inParam.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset results = CSAppCall.call("SS.ShareMealSVC.queryFamilyMebList", inParam);
        if(DataUtils.isEmpty(results)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"该号码没有共享的副卡！");
        }
        IDataset memberList = new DatasetList();
        for(int i = 0;i<results.size();i++){
            IData result = results.getData(i);
            String serialNumberB = result.getString("SERIAL_NUMBER");
            String instId = result.getString("INST_ID");
            String startDate = result.getString("START_DATE");
            String endDate = result.getString("END_DATE");
            String rsrvTag1 = result.getString("RSRV_TAG1");//是否统付
            String dealTag = result.getString("DEAL_TAG");
            IData list = new DataMap();
            if(!"1".equals(dealTag)){
                list.put("SERIAL_NUMBER",serialNumberB);
                list.put("INST_ID",instId);
                list.put("START_DATE",startDate);
                list.put("END_DATE",endDate);
                list.put("RSRV_TAG1",rsrvTag1);
                list.put("DEAL_TAG","0");
                list.put("tag","1");
                memberList.add(list);
            }
        }
        IData data = new DataMap();
        data.put("SERIAL_NUMBER",serialNumber);
        data.put("MEMBER_CANCEL","0");
        data.put("MEB_LIST",memberList);
        data.put("TRADE_TYPE_CODE","275");
        data.put("CHECK_MODE","0");
        data.put("SUBMIT_SOURCE","CRM_PAGE");
        data.put("AUTH_USER_SCORE","0");
        data.put("AUTH_CHECK_PSPT_ID","");
        data.put("AUTH_SERIAL_NUMBER",serialNumber);
        data.put("AUTH_CHECK_PSPT_TYPE_CODE","");
        data.put("REMOTECARD_TYPE","");
        data.put("POP_AUTH_PARAMS","");
        data.put("AGENT_CALL_PHONE","");
        data.put("FRONTBASE64","");
        data.put("REMARK","");
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
//        IDataset returnResults = CSAppCall.call("SS.ShareMealRegSVC.tradeReg", data);
        return data;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return null;
    }
}
