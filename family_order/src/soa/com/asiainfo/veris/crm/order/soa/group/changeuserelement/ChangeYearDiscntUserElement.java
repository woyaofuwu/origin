
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeYearDiscntUserElementReqData;

public class ChangeYearDiscntUserElement extends ChangeUserElement
{
    protected ChangeYearDiscntUserElementReqData reqData = null;

    /**
     * 生成登记信息
     * 
     * @author hud
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        // 处理包年套餐优惠信息
        orderYearDiscnt();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();

    }

    /**
     * 处理包年套餐优惠信息
     * 
     * @throws Exception
     */
    public void orderYearDiscnt() throws Exception
    {
        // 产品标识
        String productId = reqData.getProductId();

        IDataset yearDiscntDataset = new DatasetList();

        // 是否有当月到期的包年套餐
        boolean isCurMonthYearDis = false;

        // 查询产品的包年套餐
        IDataset discntDataset = UDiscntInfoQry.getDiscntByProduct(productId);//DiscntInfoQry.qryDiscntInfoByProdIdRsrv5(productId, "9");
        if (IDataUtil.isEmpty(discntDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_720);
        }
        for (int k = 0, size2 = discntDataset.size(); k < size2; k++)
        {
            IData discntData = discntDataset.getData(k);
            
            IDataset discntRsrv5Dataset = UpcCall.queryTempChaByOfferTableField("D", discntData.getString("DISCNT_CODE"), "TD_B_DISCNT", "RSRV_STR5");
            if(IDataUtil.isEmpty(discntRsrv5Dataset))
            {
            	continue;
            }
            String rsrvStr5 = discntRsrv5Dataset.getData(0).getString("FIELD_VALUE");
            if(!rsrvStr5.equals("9"))
            {
            	continue;
            }

            // 获取当月最后一天的日期
            String lastDate = SysDateMgr.getLastDateThisMonth().substring(0, 10);

            // 查询用户下本月到期的包年套餐优惠
            IDataset userDiscntDataset = UserDiscntInfoQry.getUserProdDisByUserIdProdIdPkgIdDisIdEndDate(reqData.getGrpUserId(), null, null, discntData.getString("DISCNT_CODE"), lastDate);
            if (IDataUtil.isEmpty(userDiscntDataset))
            {
                continue;
            }
            isCurMonthYearDis = true;

            // 获取下月第一天
            String nxtMonthFirstDate = SysDateMgr.firstDayOfMonth(1);

            // 查询用户是否有下月生效的包年套餐优惠(即已办理过包年套餐续订)
            IDataset userDisNextDataset = UserDiscntInfoQry.getUserProdDisByUserIdProdIdPkgIdDisIdStartDate(reqData.getGrpUserId(), null, null, discntData.getString("DISCNT_CODE"), nxtMonthFirstDate);
            if (IDataUtil.isNotEmpty(userDisNextDataset))
            {
                CSAppException.apperr(GrpException.CRM_GRP_722);
            }

            for (int m = 0, size3 = userDiscntDataset.size(); m < size3; m++)
            {
                IData userDiscntData = userDiscntDataset.getData(m);

                userDiscntData.put("INST_ID", SeqMgr.getInstId());

                // 设置生效时间：下个月第一天(yyyy-mm-dd hh24:mi:ss)
                String startDate = nxtMonthFirstDate + SysDateMgr.START_DATE_FOREVER;
                userDiscntData.put("START_DATE", startDate);

                // 设置失效时间
                String endDate = SysDateMgr.getAddMonthsLastDay(12, startDate);
                userDiscntData.put("END_DATE", endDate);

                userDiscntData.put("UPDATE_TIME", SysDateMgr.getSysTime());
                userDiscntData.put("RSRV_TAG1", "0");
                userDiscntData.put("REMARK", "包年套餐到期自动续期");

                userDiscntData.put("STATE", "ADD");
                userDiscntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                yearDiscntDataset.add(userDiscntData);
            }
        }
        if (!isCurMonthYearDis)
        {
            CSAppException.apperr(GrpException.CRM_GRP_721);
        }

        this.addTradeDiscnt(yearDiscntDataset);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ChangeYearDiscntUserElementReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setSmsSeq(map.getString("SMS_SEQ"));

        reqData.setSerialNumber(map.getString("SERIAL_NUMBER"));

        reqData.setUserTag(map.getString("USER_TAG"));

        reqData.setProductId(map.getString("PRODUCT_ID"));

        reqData.setProductName(map.getString("PRODUCT_NAME"));

        reqData.setEparchyCode(map.getString("EPARCHY_CODE"));

        reqData.setGrpMgrUserId(map.getString("USER_ID"));

        reqData.setCustMgrId(map.getString("CUST_MANAGER_ID"));

        reqData.setCustName(map.getString("CUST_NAME"));

        reqData.setGrpUserId(map.getString("GRP_USER_ID"));
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeYearDiscntUserElementReqData();
    }

    /**
     * 构建uca
     */
    protected void makUca(IData map) throws Exception
    {
        String grpUserId = map.getString("GRP_USER_ID");
        IData d = new DataMap();
        d.put("USER_ID", grpUserId);
        UcaData grpUCA = UcaDataFactory.getNormalUcaByUserIdForGrp(d);
        reqData.setUca(grpUCA);
    }

    @Override
    protected void prepareSucSmsData(IData smsData) throws Exception
    {
        super.prepareSucSmsData(smsData);

        // 地州编码
        smsData.put("EPARCHY_CODE", reqData.getEparchyCode());

        // 接入方式
        smsData.put("IN_MODE_CODE", "6");

        // 员工ID
        smsData.put("REFER_STAFF_ID", "IBOSS000");

        // 部门ID
        smsData.put("REFER_DEPART_ID", StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "DEPART_ID", "IBOSS000"));

        // 处理状态，0：已处理，15未处理
        smsData.put("DEAL_STATE", "15");

        // 备注
        smsData.put("REMARK", "包年套餐到期提醒");

        smsData.put("SEND_OBJECT_CODE", "6");// 通知短信,见TD_B_SENDOBJECT

        if ("CRM_SMS_GRP_COMM_000000".equals(smsData.getString("TEMPLATE_ID")))
        { // 给集团联系人发送成功通知短信
            smsData.put("RECV_OBJECT", reqData.getSerialNumber());
            smsData.put("RECV_ID", reqData.getGrpMgrUserId());
        }
        else if ("CRM_SMS_GRP_COMM_000001".equals(smsData.getString("TEMPLATE_ID")))
        { // 给集团管理员发送续订合同短信

            // 获取集团管理员手机号码
            String custMgrSN = UStaffInfoQry.getStaffSnByStaffId(reqData.getCustMgrId());

            // 获取集团管理员用户id
            String mgrUserId = "0";
            IDataset userDataset = UserInfoQry.getUserInfoBySN(custMgrSN, "0", "00");
            if (IDataUtil.isNotEmpty(userDataset))
            {
                mgrUserId = userDataset.getData(0).getString("USER_ID");
            }

            // 手机号(集团客户经理)
            smsData.put("RECV_OBJECT", custMgrSN);

            // 集团客户经理用户id
            smsData.put("RECV_ID", mgrUserId);
        }
    }

    /*
     * @Override protected void setSmsVarData(BizData bizData, IData varName, IData varData) throws Exception {
     * super.setSmsVarData(bizData, varName, varData); varData.put("CUST_NAME", reqData.getCustName());
     * varData.put("PRODUCT_NAME", reqData.getProductName()); }
     */
}
