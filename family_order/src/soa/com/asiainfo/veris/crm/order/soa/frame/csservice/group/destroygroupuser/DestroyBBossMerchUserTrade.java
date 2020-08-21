
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class DestroyBBossMerchUserTrade extends DestroyGroupUser
{
    protected DestroyBBossUserReqData reqData = null;

    /**
     * 修改BBOSS商品及商品资费台帐数据（生成台帐后）
     * 
     * @author sht
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        // 1- 继承基类处理
        super.actTradeSub();

        // 2- 登记业务台帐BBOSS产品用户订购表子表、优惠子表
        infoRegDataEntireMerch();
        this.addTradeGrpMerch(reqData.getMERCH());

        // 3- 登记other表
        infoRegDataOther();
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyBBossUserReqData();
    }

    /**
     * 业务台帐BBOSS商品用户订购表子表、优惠子表
     * 
     * @throws Exception
     */
    public void infoRegDataEntireMerch() throws Exception
    {

        // 商品表
        String userId = reqData.getUca().getUser().getUserId();

        IDataset results = UserGrpMerchInfoQry.qryMerchInfoByUserIdMerchSpecStatus(userId, null, null, null);
        if (results == null || results.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_769);
        }

        IData result = results.getData(0);
        result.put("STATUS", "D");
        //result.put("END_DATE", getAcceptTime());
        result.put("END_DATE", SysDateMgr.getLastDateThisMonth()); //由于集团产品注销的时候，商品截止时间必须到月底，不能影响到集团成员当月使用，bug单：QR-20170204-01 
        result.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        result.put("RSRV_STR1", GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue());// 商品操作类型
        reqData.setMERCH(result);

    }

    /*
     * @descripiton 插Other表
     * @author chenyi
     * @date 2013-08-24
     */
    public void infoRegDataOther() throws Exception
    {
        IDataset dataset = new DatasetList();

        // 1- 附件信息(合同附件和普通附件)入表
        IDataset attInfosDS = reqData.getGOOD_INFO().getDataset("ATT_INFOS");

        if (attInfosDS != null && attInfosDS.size() > 0)
        {
            for (int i = 0; i < attInfosDS.size(); i++)
            {
                IData attInfos = new DataMap();
                DataMap dm = (DataMap) attInfosDS.get(i);
                attInfos.put("USER_ID", reqData.getUca().getUser().getUserId());
                attInfos.put("RSRV_VALUE_CODE", "ATT_INFOS");
                attInfos.put("RSRV_STR1", reqData.getUca().getProductId());
                attInfos.put("RSRV_STR2", dm.getString("ATT_TYPE_CODE"));
                IData inparam = new DataMap();
                inparam.put("CUST_ID", reqData.getUca().getCustId());
                inparam.put("PRODUCT_ID", reqData.getUca().getProductId());
                inparam.put("ATT_TYPE_CODE", dm.getString("ATT_TYPE_CODE"));
                IData cliAttInfo = CSAppCall.callCCHT("ITF_CRM_ContractList", inparam, false).getData(0);
                attInfos.put("RSRV_STR3", cliAttInfo.getString("CONTRACT_BBOSS_CODE"));
                attInfos.put("RSRV_STR4", cliAttInfo.getString("CONTRACT_NAME"));
                String attName = GrpCommonBean.checkFileState(dm.getString("ATT_NAME"));
                attInfos.put("RSRV_STR5", attName);
                attInfos.put("RSRV_STR6", cliAttInfo.getString("CONTRACT_START_DATE"));
                attInfos.put("RSRV_STR7", cliAttInfo.getString("CONTRACT_END_DATE"));
                attInfos.put("RSRV_STR8", cliAttInfo.getString("CONTRACT_IS_AUTO_RENEW"));
                attInfos.put("RSRV_STR9", cliAttInfo.getString("RENEW_END_DATE"));
                attInfos.put("RSRV_STR10", cliAttInfo.getString("CONT_FEE"));
                attInfos.put("RSRV_STR11", cliAttInfo.getString("PERFER_PALN"));
                attInfos.put("RSRV_STR12", cliAttInfo.getString("CONTRACT_AUTO_RENEW_CYCLE","按月"));
                attInfos.put("RSRV_STR13", cliAttInfo.getString("CONTRACT_IS_RENEW"));
                attInfos.put("START_DATE", getAcceptTime());
                attInfos.put("END_DATE", getAcceptTime());
                attInfos.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                attInfos.put("INST_ID", SeqMgr.getInstId());
                dataset.add(attInfos);
            }
        }

        // 2- 审批信息入表
        IDataset auditorInfosDS = reqData.getGOOD_INFO().getDataset("AUDITOR_INFOS");
        if (auditorInfosDS != null && auditorInfosDS.size() > 0)
        {
            for (int i = 0; i < auditorInfosDS.size(); i++)
            {
                IData auditorInfos = new DataMap();
                DataMap dm2 = (DataMap) auditorInfosDS.get(i);
                auditorInfos.put("USER_ID", reqData.getUca().getUser().getUserId());
                auditorInfos.put("RSRV_VALUE_CODE", "AUDITOR_INFOS");
                auditorInfos.put("RSRV_STR1", reqData.getUca().getProductId());
                auditorInfos.put("RSRV_STR2", dm2.getString("AUDITOR"));
                auditorInfos.put("RSRV_STR3", dm2.getString("AUDITOR_TIME"));
                auditorInfos.put("RSRV_STR4", dm2.getString("AUDITOR_DESC"));
                auditorInfos.put("START_DATE", getAcceptTime());
                auditorInfos.put("END_DATE", getAcceptTime());
                auditorInfos.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                auditorInfos.put("INST_ID", SeqMgr.getInstId());
                dataset.add(auditorInfos);
            }
        }

        // 3- 联系人信息入表
        IDataset contactorInfosDS = reqData.getGOOD_INFO().getDataset("CONTACTOR_INFOS");
        if (contactorInfosDS != null && contactorInfosDS.size() > 0)
        {
            for (int i = 0; i < contactorInfosDS.size(); i++)
            {
                IData contactorInfos = new DataMap();
                DataMap dm3 = (DataMap) contactorInfosDS.get(i);
                contactorInfos.put("USER_ID", reqData.getUca().getUser().getUserId());
                contactorInfos.put("RSRV_VALUE_CODE", "CONTACTOR_INFOS");
                contactorInfos.put("RSRV_STR1", reqData.getUca().getProductId());
                contactorInfos.put("RSRV_STR2", dm3.getString("CONTACTOR_TYPE_CODE"));
                contactorInfos.put("RSRV_STR3", dm3.getString("CONTACTOR_NAME"));
                contactorInfos.put("RSRV_STR4", dm3.getString("CONTACTOR_PHONE"));
                contactorInfos.put("RSRV_STR5", dm3.getString("STAFF_NUMBER"));
                contactorInfos.put("START_DATE", getAcceptTime());
                contactorInfos.put("END_DATE", getAcceptTime());
                contactorInfos.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                contactorInfos.put("INST_ID", SeqMgr.getInstId());
                dataset.add(contactorInfos);
            }
        } 
        else 
        {
            IDataset userotherData = UserOtherInfoQry.getUserOtherByUseridRsrvcode(reqData.getUca().getUserId(), "CONTACTOR_INFOS", null);
            IDataset otherData = DataHelper.distinct(userotherData, "RSRV_STR2", "");
            if (otherData != null && otherData.size() > 0)
            {
                for (int i = 0; i < otherData.size(); i++)
                {
                    IData contactorInfos = new DataMap();
                    DataMap dm3 = (DataMap) otherData.get(i);
                    contactorInfos.put("USER_ID", reqData.getUca().getUser().getUserId());
                    contactorInfos.put("RSRV_VALUE_CODE", "CONTACTOR_INFOS");
                    contactorInfos.put("RSRV_STR1", reqData.getUca().getProductId());
                    contactorInfos.put("RSRV_STR2", dm3.getString("RSRV_STR2"));
                    contactorInfos.put("RSRV_STR3", dm3.getString("RSRV_STR3"));
                    contactorInfos.put("RSRV_STR4", dm3.getString("RSRV_STR4"));
                    contactorInfos.put("RSRV_STR5", dm3.getString("RSRV_STR5"));
                    contactorInfos.put("START_DATE", getAcceptTime());
                    contactorInfos.put("END_DATE", getAcceptTime());
                    contactorInfos.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    contactorInfos.put("INST_ID", SeqMgr.getInstId());
                    dataset.add(contactorInfos);
                }
            }
        }

        // 4- BBOSS侧服开信息入表(服务开通用)
        IData serviceInfo = new DataMap();
        serviceInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
        serviceInfo.put("RSRV_VALUE_CODE", "BBSS");
        serviceInfo.put("RSRV_VALUE", "集团BBOSS标志");
        serviceInfo.put("RSRV_STR9", "7810");// 服务开通侧集团service_id对应为7810
        serviceInfo.put("OPER_CODE", "07");
        serviceInfo.put("START_DATE", getAcceptTime());
        serviceInfo.put("END_DATE", getAcceptTime());
        serviceInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        serviceInfo.put("INST_ID", SeqMgr.getInstId());
        dataset.add(serviceInfo);

        this.addTradeOther(dataset);
    }

    /*
     * @description 初始化RD
     * @author xunyl
     * @date 2013-04-02
     */
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (DestroyBBossUserReqData) getBaseReqData();
    }

    /*
     * @description 将前台传递过来的BBOSS数据放入RD中，暂时没有使用，留作以后拓展
     * @author xunyl
     * @date 2013-04-03
     */
    public void makBBossReqData(IData map) throws Exception
    {
        // 反向集团注销，走批量不传合同信息
        reqData.setGOOD_INFO(new DataMap(map.getString("GOOD_INFO","{}")));
    }

    /*
     * @description 給RD賦值
     * @author xunyl
     * @date 2013-04-02
     */
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        makBBossReqData(map);
    }

    /*
     * @description 获取商品台账的相关信息，用于子产品台账信息中
     * @author xunyl
     * @2014-04-24
     */
    protected void retTradeData(IDataset dataset) throws Exception
    {
        super.retTradeData(dataset);
        // 将商品用户编号返回
        IData merchToMerchPInfo = new DataMap();
        merchToMerchPInfo.put("BBOSS_TAG", "BBOSS_TAG");
        merchToMerchPInfo.put("USER_ID", reqData.getUca().getUser().getUserId());
        dataset.add(merchToMerchPInfo);
    }

    /*
     * @descripiton 重写基类的登记主台账方法,BBOSS侧默认为全部需要发送服务开通
     * @author xunyl
     * @date 2013-08-21
     */
    protected void setTradeBase() throws Exception
    {
        // 1- 调用基类方法注入值
        super.setTradeBase();

        // 2- 子类修改OLCOM_TAG值，BBOSS侧默认设置为１
        IData data = bizData.getTrade();
        if ("6".equals(CSBizBean.getVisit().getInModeCode()))
        {// 渠道类型为IBOSS
            data.put("OLCOM_TAG", "0");
        }
        else
        {
            data.put("OLCOM_TAG", "1");
        }
    }
}
