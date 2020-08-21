
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.FamilyTradeHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyRejectRemindSvcBusiReqData;

public class FamilyRejectRemindSvcBusiTrade extends BaseTrade implements ITrade
{

    private void addMebRejectSvc(BusiTradeData bd, UcaData mebUca) throws Exception
    {
        String userId = mebUca.getUserId();
        IDataset result = new DatasetList();
        FamilyRejectRemindSvcBusiReqData reqData = (FamilyRejectRemindSvcBusiReqData) bd.getRD();
        String sysdate = reqData.getAcceptTime();
        String tradeTypeCode = bd.getMainTradeData().getTradeTypeCode();
        result = RelaUUInfoQry.getUserRelationByUserIdBRe(userId, "45");
        if (IDataUtil.isEmpty(result))
        {
            // 您还未开通亲亲网业务,不能办理该业务
            CSAppException.apperr(FamilyException.CRM_FAMILY_89);
        }

        // 查询成员短号服务
        IDataset userShortCodeSvc = UserSvcInfoQry.getSvcUserId(userId, "831");
        if (IDataUtil.isEmpty(userShortCodeSvc))
        {
            // 成员不存在亲亲网短号服务,不能办理该业务
            CSAppException.apperr(FamilyException.CRM_FAMILY_94);
        }

        // 查询成员拒收挂机提醒服务
        IDataset userRRSvc = UserSvcInfoQry.getSvcUserId(userId, "833");
        if (IDataUtil.isNotEmpty(userRRSvc))
        {
            // 成员已存在拒收挂机提醒服务,不能重复办理该业务
            CSAppException.apperr(FamilyException.CRM_FAMILY_95);
        }

        // 校验成员未完工工单限制 ----start----
        IData data = new DataMap();
        data.put("TRADE_TYPE_CODE", tradeTypeCode);
        data.put("USER_ID", mebUca.getUserId());
        data.put("SERIAL_NUMBER", mebUca.getSerialNumber());
        data.put("EPARCHY_CODE", mebUca.getUser().getEparchyCode());
        data.put("BRAND_CODE", "");
        FamilyTradeHelper.checkMemberUnfinishTrade(data);
        // 校验成员未完工工单限制 ----end----

        IData rela = result.getData(0);
        String userIdA = rela.getString("USER_ID_A");
        UcaData virtualUca = UcaDataFactory.getUcaByUserId(userIdA);
        String serialNumberA = virtualUca.getSerialNumber();

        SvcTradeData addSvcTD = new SvcTradeData();
        addSvcTD.setUserId(userId);
        addSvcTD.setUserIdA(userIdA);
        addSvcTD.setProductId("-1");
        addSvcTD.setPackageId("-1");
        addSvcTD.setElementId("833");
        addSvcTD.setStartDate(sysdate);
        addSvcTD.setEndDate(SysDateMgr.getTheLastTime());
        addSvcTD.setMainTag("0");
        addSvcTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        addSvcTD.setInstId(SeqMgr.getInstId());
        addSvcTD.setRsrvStr1(serialNumberA);
        addSvcTD.setRsrvStr2(mebUca.getSerialNumber());
        addSvcTD.setRsrvStr3(rela.getString("ROLE_CODE_B"));
        addSvcTD.setRsrvStr4(rela.getString("SHORT_CODE"));

        bd.add(serialNumberA, addSvcTD);

    }

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        FamilyRejectRemindSvcBusiReqData reqData = (FamilyRejectRemindSvcBusiReqData) bd.getRD();
        String rejectMode = reqData.getRejectMode();
        UcaData uca = reqData.getUca();
        String sysdate = reqData.getAcceptTime();

        IDataset result = RelaUUInfoQry.getUserRelationByUserIdBRe(uca.getUserId(), "45");
        if (IDataUtil.isEmpty(result))
        {
            // 您还未开通亲亲网业务,不能办理该业务
            CSAppException.apperr(FamilyException.CRM_FAMILY_89);
        }
        IData rela = result.getData(0);
        String userIdA = rela.getString("USER_ID_A");
        String roleCodeB = rela.getString("ROLE_CODE_B");

        if (rejectMode.equals("1"))
        {
            // 全网拒收
            if (StringUtils.equals(roleCodeB, "2"))
            {// 如果是副号，则不能办理全网拒收
                CSAppException.apperr(FamilyException.CRM_FAMILY_90);
            }

            UcaData virtualUca = UcaDataFactory.getUcaByUserId(userIdA);
            String serialNumberA = virtualUca.getSerialNumber();

            SvcTradeData addSvcTD = new SvcTradeData();
            addSvcTD.setUserId(userIdA);
            addSvcTD.setUserIdA(userIdA);
            addSvcTD.setProductId("-1");
            addSvcTD.setPackageId("-1");
            addSvcTD.setElementId("832");
            addSvcTD.setStartDate(sysdate);
            addSvcTD.setEndDate(SysDateMgr.getTheLastTime());
            addSvcTD.setMainTag("0");
            addSvcTD.setRsrvStr1(serialNumberA);
            addSvcTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            addSvcTD.setInstId(SeqMgr.getInstId());

            bd.add(serialNumberA, addSvcTD);
        }
        else if (rejectMode.equals("2"))
        {
            // 成员拒收
            List<UcaData> mebUcaList = reqData.getMebUcaList();

            // 提交数据为空
            if (null == mebUcaList || mebUcaList.size() == 0)
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_91);
            }

            // 每次只能给一个成员办理拒收挂机提醒服务
            if (mebUcaList.size() > 1)
            {
                CSAppException.apperr(FamilyException.CRM_FAMILY_92);
            }

            // 副号码只能给自己办理
            if (StringUtils.equals(roleCodeB, "2"))
            {
                String sn = uca.getSerialNumber();
                String mebSn = mebUcaList.get(0).getSerialNumber();
                if (!StringUtils.equals(sn, mebSn))
                {
                    CSAppException.apperr(FamilyException.CRM_FAMILY_93);
                }
            }

            for (int i = 0, size = mebUcaList.size(); i < size; i++)
            {
                addMebRejectSvc(bd, mebUcaList.get(i));
            }
        }
    }
}
