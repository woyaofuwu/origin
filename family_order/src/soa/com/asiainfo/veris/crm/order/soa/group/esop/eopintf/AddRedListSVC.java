package com.asiainfo.veris.crm.order.soa.group.esop.eopintf;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweNodeQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformSubscribeBean;

public class AddRedListSVC extends CSBizService {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static transient Logger logger = Logger.getLogger(AddRedListSVC.class);

    public void addRedList(IData param) throws Exception {
        String ibsysId = param.getString("IBSYSID");
        String nodeId = param.getString("NODE_ID");
        String subBusiformId = param.getString("BUSIFORM_ID", "");

        logger.debug("-------------流程步骤入参查看：" + param.toString());
        
        //获取订单信息
        IDataset subscriberDatas = WorkformSubscribeBean.qryWorkformSubscribeByIbsysid(ibsysId);
        if(DataUtils.isEmpty(subscriberDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据流水号：" + ibsysId + "未查询到订单信息！");
        }
        IData subscriberData = subscriberDatas.first();
        String bpmTempletId = subscriberData.getString("BPM_TEMPLET_ID");
        IData input = new DataMap();
        input.put("BPM_TEMPLET_ID", bpmTempletId);
        input.put("NODE_TYPE", "3");
        IDataset nodeTempleteList = CSAppCall.call("SS.NodeTempletInfoSVC.qryInfoByBpmTempletType", input);
        if(IDataUtil.isEmpty(nodeTempleteList)){
            CSAppException.apperr(GrpException.CRM_GRP_713, "未查询到流程【"+bpmTempletId+"】开始节点！");
        }
        String startNodeId = nodeTempleteList.first().getString("NODE_ID");
        IData comParam = new DataMap();
        comParam.put("IBSYSID", ibsysId);
        comParam.put("NODE_ID", startNodeId);
        comParam.put("RECORD_NUM", "0");
        IDataset commonList = WorkformAttrBean.getNewInfoByIbsysidAndNodeId(comParam);
        if(IDataUtil.isEmpty(commonList)){
            CSAppException.apperr(GrpException.CRM_GRP_713, "未查询到流程公共信息！");
        }
        
        //红名单开始与结束时间
        String startDate = "";
        String endDate = "";
        for(int i=0;i<commonList.size();i++){
            IData commonData = commonList.getData(i);
            String attrCode = commonData.getString("ATTR_CODE");
            if("START_DATE".equals(attrCode)){
                startDate = commonData.getString("ATTR_VALUE");
            }
            if("END_DATE".equals(attrCode)){
                endDate = commonData.getString("ATTR_VALUE");
            }
        }
        
        if(StringUtils.isBlank(startDate)||StringUtils.isBlank(endDate)){
            CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到红名单时长信息！");
        }else{
            startDate = SysDateMgr.suffixDate(startDate, 0);
            endDate = SysDateMgr.suffixDate(endDate, 1);
        }

        //查流程订单信息
        IDataset eweRaleDatas = EweNodeQry.qryBySubBusiformId(subBusiformId);
        if(DataUtils.isEmpty(eweRaleDatas)) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据子流程ID：" + subBusiformId + "未查询到流程关系表数据！");
        }
        IData eweRaleData = eweRaleDatas.first();
        String recordNum = eweRaleData.getString("RELE_VALUE", "");

        IData snParam = new DataMap();
        snParam.put("IBSYSID", ibsysId);
        snParam.put("RECORD_NUM", recordNum);
        IDataset userSns = WorkformAttrBean.getInfoByIbsysidRecordNum(snParam);
        boolean flag = true;
        if(DataUtils.isNotEmpty(userSns)) {
            for (int i = 0; i < userSns.size(); i++) {
                IData userSn = userSns.getData(i);
                String attrCode = userSn.getString("ATTR_CODE");
                if("SERIAL_NUMBER".equals(attrCode)) {
                    String sn = userSn.getString("ATTR_VALUE");
                    IData inparam = new DataMap();
                    inparam.put("SERIAL_NUMBER", sn);
                    IData isRedData = CreditCall.queryIsRedList(inparam);
                    String addTag = "";
                    if(IDataUtil.isEmpty(isRedData)) {
                        addTag = "0";
                    } else if("0".equals(isRedData.getString("ACT_TAG"))) {
                        addTag = "2";
                    }
                    callCreditAddRedList(sn, addTag,startDate,endDate);
                    flag = false;
                }
            }
        }
        if(flag) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "未从TF_B_EOP_ATTR表获取到需要添加红名单的SERIAL_NUMBER，请检查数据是否正确！");
        }
    }

    private void callCreditAddRedList(String sn, String addTag,String startDate,String endDate) throws Exception {
        IData param = new DataMap();
        IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0", sn);
        if(DataUtils.isNotEmpty(userInfos)) {
            IData userInfo = userInfos.first();
            String userId = userInfo.getString("USER_ID");
            param.put("USER_ID", userId);
            param.put("SERIAL_NUMBER", sn);
            param.put("tag", addTag);
            param.put("X_CTAG_FEE", "是");
            param.put("X_CTAG_HSTOP", "是");
            param.put("X_CTAG_STOP", "是");
            param.put("X_CTAG_DESTROY", "是");
            param.put("TAG_NAME", "是");
            param.put("START_DATE", startDate);
            param.put("END_DATE", endDate);
            param.put("REMARK", "ESOP信控流程添加");
            param.put("UPDATE_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            CreditCall.addRedList(param);
        }
        
    }
}
