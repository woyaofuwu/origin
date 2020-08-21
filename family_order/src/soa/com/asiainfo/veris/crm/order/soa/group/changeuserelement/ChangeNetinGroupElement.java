package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeNetinGroupElement extends ChangeUserElement {
    protected ChangeInternetUserElementReqData reqData = null;

    public ChangeNetinGroupElement() {
    }

    protected BaseReqData getReqData() throws Exception {
        return new ChangeInternetUserElementReqData();
    }

    protected void actTradeBefore() throws Exception {
        super.actTradeBefore();

    }

    protected void initReqData() throws Exception {
        super.initReqData();

        reqData = (ChangeInternetUserElementReqData) getBaseReqData();

    }

    protected void makReqData(IData map) throws Exception {
        super.makReqData(map);

        reqData.setInterData(map.getData("ATTRINTERNET"));

        reqData.setDataline(map.getData("DATALINE"));

        reqData.setCommonData(map.getDataset("COMMON_DATA"));

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception {
        super.actTradeSub();
        actTradeUserAAttr();

    }

    protected void setTradeBase() throws Exception {
        super.setTradeBase();

        IData map = bizData.getTrade();

        // CRM发起资料变更走开环
        if (null == reqData.getDataline() || null == reqData.getCommonData() || reqData.getDataline().size() < 1) {
            map.put("OLCOM_TAG", "0");
        } else {
            // ESOP发起变更根据PFWAIT处理，真正的变更闭环处理，开通转变更的根据ESOP传的PFWAIT处理
            int pfWait = 0;
            IDataset commonData = reqData.getCommonData();
            if (null != commonData && commonData.size() > 0) {
                for (int i = 0; i < commonData.size(); i++) {
                    IData data = commonData.getData(i);

                    if ("PF_WAIT".equals(data.getString("ATTR_CODE")) && StringUtils.isNotBlank(data.getString("ATTR_CODE"))) {

                        pfWait = data.getInt("ATTR_VALUE");
                    }
                }
            }

            map.put("OLCOM_TAG", "1");
            map.put("PF_WAIT", pfWait);

        }
    }

    @Override
    protected void regTrade() throws Exception {
        super.regTrade();
    }

    private void actTradeUserAAttr() throws Exception {
        String lineConf = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "PBOSS_COMM_INFO_CONFIG", "COMM_INFO" });
        String userIda = reqData.getUca().getUser().getUserId();
        IDataset attrDataList = reqData.getCommonData();
        IDataset dataSet = new DatasetList();
        for (Object attrInf : attrDataList) {
            IData attrInfo = (IData) attrInf;
            String attrCode = attrInfo.getString("ATTR_CODE");
            String attrValue = attrInfo.getString("ATTR_VALUE");
            if (lineConf.contains(attrCode)) {
                IDataset userAttrs = UserAttrInfoQry.getUserAttrByUserInstType(userIda, attrCode);
                if (IDataUtil.isNotEmpty(userAttrs)) {
                    IData data = new DataMap();
                    IData userAttr = new DataMap();
                    data = userAttrs.getData(0);
                    if (!data.getString("ATTR_VALUE").equals(attrValue)) {
                        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        data.put("END_DATE", SysDateMgr.getSysDate());
                        dataSet.add(data);
                        userAttr.put("USER_ID", userIda);
                        userAttr.put("USER_ID_A", "-1");
                        userAttr.put("INST_TYPE", "P");
                        userAttr.put("RELA_INST_ID", SeqMgr.getInstId());
                        userAttr.put("INST_ID", SeqMgr.getInstId());
                        userAttr.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        userAttr.put("ATTR_CODE", attrCode);
                        userAttr.put("ATTR_VALUE", attrValue);
                        userAttr.put("START_DATE", SysDateMgr.getSysDate());
                        userAttr.put("END_DATE", SysDateMgr.getTheLastTime());
                        dataSet.add(userAttr);

                    }
                }
            }
        }
        addTradeAttr(dataSet);
    }

}
