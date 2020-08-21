/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.requestdata.NcardsOneAcctCancelReqData;

/**
 * @CREATED by gongp@2014-5-20 修改历史 Revision 2014-5-20 上午09:42:34
 */
public class NcardsOneAcctCancelTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub

        NcardsOneAcctCancelReqData reqData = (NcardsOneAcctCancelReqData) bd.getRD();

        this.getSecondNumberInfo(bd);
        this.geneTradeRelation(bd);
        this.geneTradePayRelation(bd);
        this.geneTradeDiscnt(bd);
        this.delVirtualUserInfo(bd);// 删除虚拟用户资料

        MainTradeData mainTD = bd.getMainTradeData();

        mainTD.setRsrvStr3(reqData.getUca().getFirstDate()); // 当前账期
        mainTD.setRsrvStr4(reqData.getSerialNumberSecond());// 副号码
        mainTD.setRsrvStr5(this.getUserSimCardNum(reqData.getUserIdSecond())); // 副SIM卡
        mainTD.setRsrvStr7(this.getUserSimCardNum(reqData.getUca().getUserId()));// 主SIM卡
    }

    public void delVirtualUserInfo(BusiTradeData bd) throws Exception
    {

        NcardsOneAcctCancelReqData reqData = (NcardsOneAcctCancelReqData) bd.getRD();

        UcaData ucaData = UcaDataFactory.getVirtualUca(reqData.getSerialNumberA(), false, false);

        UserTradeData userTd = ucaData.getUser();

        userTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
        userTd.setRemoveTag("2");

        bd.add(reqData.getSerialNumberA(), userTd);

        ProductTradeData userProductTd = ucaData.getUserMainProduct();

        userProductTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
        userProductTd.setEndDate(reqData.getAcceptTime());

        bd.add(reqData.getSerialNumberA(), userProductTd);

        IDataset dataset = UserAcctDayInfoQry.getValidUserAcctDays(reqData.getUserIdA(), reqData.getAcceptTime());

        if (IDataUtil.isNotEmpty(dataset))
        {

            UserAcctDayTradeData userAcctDayTd = new UserAcctDayTradeData(dataset.getData(0));

            userAcctDayTd.setUserId(reqData.getUserIdA());
            userAcctDayTd.setModifyTag(BofConst.MODIFY_TAG_DEL);
            userAcctDayTd.setEndDate(reqData.getAcceptTime());

            bd.add(reqData.getSerialNumberA(), userAcctDayTd);
        }

    }

    public void geneTradeDiscnt(BusiTradeData bd) throws Exception
    {
        NcardsOneAcctCancelReqData reqData = (NcardsOneAcctCancelReqData) bd.getRD();

        IDataset dataset = DiscntInfoQry.getDiscntNowValid(reqData.getUserIdA(), "5905", null);

        if (dataset.size() > 0)
        {

            DiscntTradeData td = new DiscntTradeData(dataset.getData(0));

            td.setModifyTag(BofConst.MODIFY_TAG_DEL);
            td.setUserId(reqData.getUserIdA());
            td.setEndDate(SysDateMgr.getSysDate());

            bd.add(reqData.getUca().getSerialNumber(), td);
        }
    }

    /**
     * 修改副号码付费关系
     * 
     * @param bd
     * @CREATE BY GONGP@2014-5-20
     */
    public void geneTradePayRelation(BusiTradeData bd) throws Exception
    {

        NcardsOneAcctCancelReqData reqData = (NcardsOneAcctCancelReqData) bd.getRD();

        IDataset acctInfos = AcctInfoQry.getOldAcctInfo(reqData.getCustIdSecond());

        if (acctInfos.size() > 0)
        {

            PayRelationTradeData td = new PayRelationTradeData();

            td.setUserId(reqData.getUserIdSecond());
            td.setAcctId(acctInfos.getData(0).getString("ACCT_ID"));
            td.setModifyTag(BofConst.MODIFY_TAG_ADD);
            td.setPayitemCode("-1");
            td.setAcctPriority("0");
            td.setUserPriority("0");
            td.setBindType("1");
            td.setActTag("1");
            td.setDefaultTag("1");
            td.setModifyTag("0");
            td.setLimit("0");
            td.setLimitType("0");
            td.setComplementTag("0");
            td.setAddupMethod("0");
            td.setAddupMonths("0");
            td.setInstId(SeqMgr.getInstId());
            td.setStartCycleId(reqData.getCurCycleId());
            td.setEndCycleId(SysDateMgr.getEndCycle20501231());

            bd.add(reqData.getSerialNumberSecond(), td);

            IDataset userValidPayRelas = PayRelaInfoQry.qryValidPayRelationByUserId(reqData.getUserIdSecond(), "1", "1");

            if (IDataUtil.isNotEmpty(userValidPayRelas))
            {

                IData data = userValidPayRelas.getData(0);

                PayRelationTradeData oldPayTd = new PayRelationTradeData(data);

                oldPayTd.setModifyTag(BofConst.MODIFY_TAG_DEL);

                oldPayTd.setEndCycleId(StringUtils.replace(SysDateMgr.addDays(SysDateMgr.decodeTimestamp(reqData.getCurCycleId(), "yyyy-MM-dd"), -1), "-", ""));

                bd.add(reqData.getSerialNumberSecond(), oldPayTd);
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "副号码无法获取正常账户信息，业务无法继续！");
        }

    }

    /**
     * 删除UU关系
     * 
     * @param bd
     * @throws Exception
     * @CREATE BY GONGP@2014-5-20
     */
    public void geneTradeRelation(BusiTradeData bd) throws Exception
    {

        NcardsOneAcctCancelReqData reqData = (NcardsOneAcctCancelReqData) bd.getRD();

        IDataset userRelationInfos = RelaUUInfoQry.getUserRelationByUserIdBRe(reqData.getUca().getUserId(), "34");
        // 主号码UU关系删除
        if (userRelationInfos.size() > 0)
        {
            RelationTradeData td = new RelationTradeData(userRelationInfos.getData(0));

            td.setModifyTag("2");
            td.setUserIdB(reqData.getUca().getUserId());
            td.setEndDate(SysDateMgr.getSysDate());
            td.setModifyTag(BofConst.MODIFY_TAG_DEL);

            bd.add(reqData.getUca().getSerialNumber(), td);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有查询到主号码双卡统一付费UU关系，业务无法继续！");
        }

        IDataset secondUserRelaInfos = RelaUUInfoQry.getUserRelationByUserIdBRe(reqData.getUserIdSecond(), "34");
        // 副号码UU关系删除
        if (secondUserRelaInfos.size() > 0)
        {
            RelationTradeData td = new RelationTradeData(secondUserRelaInfos.getData(0));

            td.setModifyTag("2");
            td.setUserIdB(reqData.getUserIdSecond());
            td.setEndDate(SysDateMgr.getSysDate());
            td.setModifyTag(BofConst.MODIFY_TAG_DEL);

            bd.add(reqData.getUca().getSerialNumber(), td);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有查询到副号码双卡统一付费UU关系，业务无法继续！");
        }
    }

    public void getSecondNumberInfo(BusiTradeData bd) throws Exception
    {

        NcardsOneAcctCancelReqData reqData = (NcardsOneAcctCancelReqData) bd.getRD();

        IDataset userRelationInfos = RelaUUInfoQry.getUserRelationByUR(reqData.getUca().getUserId(), "34");

        if (userRelationInfos == null || userRelationInfos.size() == 0 || "".equals(userRelationInfos.getData(0).getString("USER_ID_A")))
        {
            // common.error("该用户不是双卡统一付费用户，业务无法继续！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户不是双卡统一付费用户，业务无法继续！");
        }
        IData relationInfo = userRelationInfos.getData(0);

        reqData.setUserIdA(relationInfo.getString("USER_ID_A"));
        reqData.setSerialNumberA(relationInfo.getString("SERIAL_NUMBER_A"));

        IDataset userIdARelaInfos = RelaUUInfoQry.getUserRelationAll(reqData.getUserIdA(), "34");

        if (userIdARelaInfos.size() < 1)
        {
            // common.error("没有获取到有效的双卡统一付费虚拟集团资料，业务无法继续！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的双卡统一付费虚拟集团资料，业务无法继续！");
        }

        for (int i = 0, size = userIdARelaInfos.size(); i < size; i++)
        {
            IData userReLa = userIdARelaInfos.getData(i);
            String role_code_b = userReLa.getString("ROLE_CODE_B");
            if (role_code_b.equals("2"))
            {
                reqData.setSerialNumberSecond(userReLa.getString("SERIAL_NUMBER_B"));
            }
        }

        if (StringUtils.isBlank(reqData.getSerialNumberSecond()))
        {
            // common.error("没有获取到有效的双卡统一付费副号码信息");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的双卡统一付费副号码信息！");
        }
        else
        {
            if (reqData.getSerialNumberSecond().equals(reqData.getUca().getSerialNumber()))
            {
                // common.error("您输入的服务号码不是双卡统一付费主号码，业务无法继续！");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您输入的服务号码不是双卡统一付费主号码，业务无法继续！");
            }
        }

        UcaData data = UcaDataFactory.getNormalUca(reqData.getSerialNumberSecond());

        reqData.setUserIdSecond(data.getUserId());
        reqData.setCustIdSecond(data.getCustId());

        String curCycleId = DiversifyAcctUtil.getFirstDayThisAcct(reqData.getUca().getUserId());
        // 查询副卡
        String secCycleId = DiversifyAcctUtil.getFirstDayThisAcct(reqData.getUserIdSecond());

        if (curCycleId.compareTo(secCycleId) < 0)
            curCycleId = secCycleId;

        reqData.setCurCycleId(curCycleId.replaceAll("-", ""));
    }

    public String getUserSimCardNum(String userId) throws Exception
    {

        IDataset resInfos = UserResInfoQry.queryUserSimInfo(userId, "1");

        if (resInfos.size() == 0)
        {
            // common.error("获取用户资源资料无数据");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户资源资料无数据");
        }
        return resInfos.getData(0).getString("RES_CODE");

    }

}
