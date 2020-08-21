package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateLineGroupMemberNew;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;

public class CreateImsGroupMember extends CreateLineGroupMemberNew {

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception {

        super.actTradeBefore();

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception {

        super.actTradeSub();
        actTradeDataline();
        actTradeDatalineAttr();
        //regOtherInfoTrade();

    }

    private void actTradeDatalineAttr() throws Exception {
        IData dataline = reqData.getDataline();
        IDataset attrDataList = reqData.getCommonData();
        IData internet = reqData.getInterData();
        IDataset dataset = new DatasetList();

        IData userData = new DataMap();
        userData.put("USER_ID", reqData.getUca().getUserId());
        userData.put("START_DATE", getAcceptTime());
        userData.put("SHEET_TYPE", "8");
//        userData.put("BANDWIDTH", internet.getString("pam_NOTIN_LINE_BROADBAND"));

        dataset = DatalineUtil.addTradeUserDataLineAttr(attrDataList, dataline, userData);

        super.addTradeDataLineAttr(dataset);
    }

    private void actTradeDataline() throws Exception {
        IDataset dataset = new DatasetList();
        IData dataline = reqData.getDataline();
        IData internet = reqData.getInterData();

        IData userData = new DataMap();
        userData.put("USER_ID", reqData.getUca().getUserId());
        userData.put("START_DATE", getAcceptTime());
        userData.put("SHEET_TYPE", "8");
        // add by REQ201802260030 关于集客业务支撑流程式快速开通功能需求
        if(dataline != null && dataline.getString("LINEOPENTAG", "").equals("1")) {
            dataline.put("RSRV_NUM1", dataline.getString("LINEOPENTAG", ""));
        }

        if(dataline!=null&&!dataline.getString("TRANSFERMODE","").equals("")){
        	dataline.put("RSRV_STR2", dataline.getString("TRANSFERMODE",""));
        }
        dataset = DatalineUtil.addTradeUserDataLine(dataline, internet, userData);

        super.addTradeDataLine(dataset);
    }


    protected void actTradeUser() throws Exception {
        UserTradeData userTradeData = reqData.getUca().getUser();
        // 用户
        if(userTradeData != null) {
            // 存产品产品信息到user表
            String product_id = reqData.getUca().getProductId();
            IData productParams = reqData.cd.getProductParamMap(product_id);
            if(IDataUtil.isNotEmpty(productParams)) {
            	
                userTradeData.setRsrvStr7(productParams.getString("NOTIN_DETMANAGER_INFO"));
                userTradeData.setRsrvStr8(productParams.getString("NOTIN_DETMANAGER_PHONE"));
                userTradeData.setRsrvStr9(productParams.getString("NOTIN_DETADDRESS"));
                userTradeData.setRsrvStr10(productParams.getString("NOTIN_PROJECT_NAME"));

            }
        }
        super.actTradeUser();
    }

    protected void regTrade() throws Exception {
        super.regTrade();
        IData data = bizData.getTrade();
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        if(IDataUtil.isNotEmpty(paramData)) {
            data.put("RSRV_STR7", paramData.getString("NOTIN_DETMANAGER_INFO", ""));
            data.put("RSRV_STR8", paramData.getString("NOTIN_DETMANAGER_PHONE", ""));
            data.put("RSRV_STR9", paramData.getString("NOTIN_DETADDRESS", ""));
            data.put("RSRV_STR10", paramData.getString("NOTIN_PROJECT_NAME", ""));
        }

        // data.put("RSRV_STR1", reqData.getGrpUca().getUserId());
        // data.put("RSRV_STR2", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        // data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());

    }

    protected void setTradeBase() throws Exception {

        super.setTradeBase();

        IData map = bizData.getTrade();

        map.put("OLCOM_TAG", "1");

        map.put("PF_WAIT", reqData.getPfWait());
    }

    protected void makReqData(IData map) throws Exception {
        super.makReqData(map);

        reqData.setPfWait(map.getInt("PF_WAIT"));
        reqData.setInterData(map.getData("ATTRINTERNET"));
        reqData.setDataline(map.getData("DATALINE"));
        reqData.setCommonData(map.getDataset("COMMON_DATA"));
    }
    
   
}
