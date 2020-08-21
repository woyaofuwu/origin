/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub.order.requestdata.ChlCommFeeSubsidyMgrRequestData;

/**
 * @CREATED by gongp@2014-4-15 修改历史 Revision 2014-4-15 下午03:03:52
 */
public class ChlCommFeeSubsidyMgrTrade extends BaseTrade implements ITrade
{

    public void beforeSubmitCheck(BusiTradeData bd, String userId) throws Exception
    {

        ChlCommFeeSubsidyMgrRequestData reqData = (ChlCommFeeSubsidyMgrRequestData) bd.getRD();

        String custType = reqData.getCustType();

        String staffId = reqData.getStaffId();

        if ("0".equals(custType))
        {
            checkResponsType(bd, userId);
        }

        if ("1".equals(custType))
        {
            IDataset staffSet = ParamInfoQry.getChnlStaffId(staffId);

            if (IDataUtil.isEmpty(staffSet))
            {
                // common.error("店员编号不存在，请确认后再办理!");
                CSAppException.apperr(CrmCommException.CRM_COMM_341);
            }
            this.checkShopType(userId, staffId);
        }
        if ("2".equals(custType))
        {
            checkNoCard(bd, userId);
        }

    }

    public void checkNoCard(BusiTradeData bd, String userId) throws Exception
    {

        ChlCommFeeSubsidyMgrRequestData reqData = (ChlCommFeeSubsidyMgrRequestData) bd.getRD();

        int chlLevel = Integer.parseInt(reqData.getChlLevel());

        if (chlLevel < 3)
        {
            // common.error("三星级以下渠道不可维护无卡店员!");
            CSAppException.apperr(CrmCommException.CRM_COMM_533);
        }

        IDataset userOthers = UserOtherInfoQry.getUserOtherByUserIdStr1Str3(userId, "CHNL", "2", reqData.getChlCode());

        int userOtherSize = userOthers.size();
        if (chlLevel == 3)
        {
            if (userOtherSize >= 2)
            {
                // common.error("三星级渠道只可维护2个无卡店员!");
                CSAppException.apperr(CrmCommException.CRM_COMM_532);
            }
        }
        else if (chlLevel <= 4)
        {
            if (userOtherSize >= 6)
            {
                // common.error("四星级渠道只可维护6个无卡店员!");
                CSAppException.apperr(CrmCommException.CRM_COMM_553);
            }
        }
        else if (chlLevel == 5)
        {
            if (userOtherSize >= 10)
            {
                // common.error("五星级渠道只可维护录入10个无卡店员!");
                CSAppException.apperr(CrmCommException.CRM_COMM_576);
            }
        }
        else if (chlLevel == 6)
        {
            if (userOtherSize >= 15)
            {
                // common.error("六星级渠道只可维护录入15个无卡店员!");
                CSAppException.apperr(CrmCommException.CRM_COMM_468);
            }
        }

    }

    /**
     * 客户类型为负责人时校验
     * 
     * @param bd
     * @param userId
     * @throws Exception
     * @CREATE BY GONGP@2014-5-10
     */
    public void checkResponsType(BusiTradeData bd, String userId) throws Exception
    {

        ChlCommFeeSubsidyMgrRequestData reqData = (ChlCommFeeSubsidyMgrRequestData) bd.getRD();

        int chlLevel = Integer.parseInt(reqData.getChlLevel());

        IDataset userOthers = UserOtherInfoQry.getUserOtherByUserIdStr1Str3(userId, "CHNL", "0", reqData.getChlCode());

        int userOtherSize = userOthers.size();
        if (chlLevel <= 4)
        {
            if (userOtherSize >= 1)
            {
                // common.error("四星级及以下渠道只可维护一个负责人!");
                CSAppException.apperr(CrmCommException.CRM_COMM_552);
            }
        }
        else if (chlLevel == 5)
        {
            if (userOtherSize >= 2)
            {
                // common.error("五星级渠道只可维护录入二名负责人!");
                CSAppException.apperr(CrmCommException.CRM_COMM_577);
            }
        }
        else if (chlLevel == 6)
        {
            if (userOtherSize >= 3)
            {
                // common.error("六星级渠道只可维护录入三名负责人!");
                CSAppException.apperr(CrmCommException.CRM_COMM_469);
            }
        }
    }

    /**
     * 客户类型为店员时校验
     * 
     * @throws Exception
     */
    public void checkShopType(String userId, String staffId) throws Exception
    {
        IDataset checkSet = ParamInfoQry.checkStaffId(staffId);
        for (int i = 0; i < checkSet.size(); i++)
        {
            if (!checkSet.getData(i).getString("PARA_CODE1").equals(userId))
            {
                // common.error("每个店员编号仅能对应一个补贴号码!");
                CSAppException.apperr(CrmCommException.CRM_COMM_495);
            }
        }
        IDataset subnum = UserOtherInfoQry.getUserOtherByUserIdStr1Str5(userId, "CHNL", "1", staffId);
        if (subnum.size() >= 2)
        {
            // common.error("该店员编号已办理过两次补贴!");
            CSAppException.apperr(CrmCommException.CRM_COMM_392);
        }
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        ChlCommFeeSubsidyMgrRequestData reqData = (ChlCommFeeSubsidyMgrRequestData) bd.getRD();

        IDataset paramDatas = CommparaInfoQry.getCommparaAllCol("CSM", "655", "CHL_COMM", "0898");

        if (IDataUtil.isEmpty(paramDatas))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未配置渠道通讯费补贴管理参数!");
        }

        IData paramData = paramDatas.getData(0);

        String phoneSub = reqData.getPhoneSub();// 话音补贴
        String newTrade = reqData.getNewTrade();// 新业务补贴
        String custType = reqData.getCustType();// 客户类型

        this.beforeSubmitCheck(bd, reqData.getUca().getUserId());

        if ("1".equals(phoneSub))
        {
            this.genUserOtherTrade(bd, reqData, paramData, 0);
        }

        if ("1".equals(newTrade))
        {
            this.genUserOtherTrade(bd, reqData, paramData, 1);
        }

        if ("0".equals(custType) || "3".equals(custType))
        { // modify by qinbin add店员（高级及以上级别）
            if ("1".equals(phoneSub))
            {
                this.genUserDiscntTrade(bd, reqData, paramData.getString("PARA_CODE3"));// 1402
            }
        }
        else if ("1".equals(custType) || "2".equals(custType))
        {
            if ("1".equals(phoneSub))
            {
                this.genUserDiscntTrade(bd, reqData, paramData.getString("PARA_CODE2"));// 1401
            }
        }

        if ("1".equals(newTrade))
        {
            this.genUserDiscntTrade(bd, reqData, paramData.getString("PARA_CODE4"));// 1403
            this.genUserDiscntTrade(bd, reqData, paramData.getString("PARA_CODE5"));// 4807
        }
    }

    private void genUserDiscntTrade(BusiTradeData bd, ChlCommFeeSubsidyMgrRequestData reqData, String discntCode) throws Exception
    {
        DiscntTradeData discntTD = new DiscntTradeData();

        discntTD.setModifyTag("0");
        discntTD.setInstId(SeqMgr.getInstId());
        discntTD.setUserId(reqData.getUca().getUserId());
        discntTD.setUserIdA("-1");
        discntTD.setProductId("-1");
        discntTD.setPackageId("-1");
        discntTD.setSpecTag("0");
        discntTD.setElementId(discntCode);
        discntTD.setStartDate(SysDateMgr.getSysTime());
        discntTD.setEndDate(SysDateMgr.END_TIME_FOREVER);

        bd.add(reqData.getUca().getSerialNumber(), discntTD);
    }

    private void genUserOtherTrade(BusiTradeData bd, ChlCommFeeSubsidyMgrRequestData reqData, IData paramData, int isNew) throws Exception
    {
        OtherTradeData otherTD = new OtherTradeData();

        otherTD.setRsrvValueCode("CHNL");
        otherTD.setRsrvValue("记录渠道通讯费补贴");
        otherTD.setUserId(reqData.getUca().getUserId());
        otherTD.setInstId(SeqMgr.getInstId());
        otherTD.setStartDate(SysDateMgr.getSysTime());
        otherTD.setEndDate(SysDateMgr.END_TIME_FOREVER);
        otherTD.setModifyTag("0");

        if (isNew == 0)
        {// 话音补贴
            if ("0".equals(reqData.getCustType()) || "3".equals(reqData.getCustType()))
            {
                otherTD.setRsrvStr6(paramData.getString("PARA_CODE3"));// 1402
            }
            else
            {
                otherTD.setRsrvStr6(paramData.getString("PARA_CODE2"));// 1401
            }
        }
        else if (isNew == 1)
        {// 新业务补贴
            otherTD.setRsrvStr7(paramData.getString("PARA_CODE4"));// 1403
        }

        otherTD.setRsrvStr2(reqData.getChlType());
        otherTD.setRsrvStr3(reqData.getChlCode());
        otherTD.setRsrvStr4(reqData.getChlLevel());

        otherTD.setRsrvStr1(reqData.getCustType());
        otherTD.setRsrvStr5(reqData.getStaffId());
        otherTD.setRsrvStr8(CSBizBean.getVisit().getCityCode());
        otherTD.setRsrvStr9(reqData.getChlName());
        otherTD.setRemark(reqData.getRemark());

        bd.add(reqData.getUca().getSerialNumber(), otherTD);
    }
}
