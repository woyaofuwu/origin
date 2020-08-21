
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.trade;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyMemberSingleReqData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyRole;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyUserMemberQuery;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyBusiRegUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FamilyUserMemberTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;

/**
 * @Description 成员删除受理类
 * @Auther: zhenggang
 * @Date: 2020/7/31 17:18
 * @version: V1.0
 */
public class DelSingleFamilyMemberTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        FamilyMemberSingleReqData reqData = (FamilyMemberSingleReqData) btd.getRD();

        // 主台账预留字段放入家庭产品ID 规则使用
        addFamilyProductToMainTrade(reqData, btd);
        // 删除副卡关系
        delFamilyMemberTrade(reqData, btd);
        // 处理工单依赖台账
        FamilyBusiRegUtil.createLimitTrade(reqData);
    }

    private void addFamilyProductToMainTrade(FamilyMemberSingleReqData reqData, BusiTradeData btd) throws Exception
    {
        FamilyRole fr = reqData.getFamilyRole();
        MainTradeData mtd = btd.getMainTradeData();
        // 家庭产品ID
        mtd.setRsrvStr1(reqData.getFmyProductId());
        // 管理员号码
        mtd.setRsrvStr2(reqData.getManagerSn());
        // 当前删除角色
        mtd.setRsrvStr3(fr.getRoleCode());
        // 设置短信字段
        mtd.setRsrvStr8(reqData.getPrintContent());
    }

    private void delFamilyMemberTrade(FamilyMemberSingleReqData reqData, BusiTradeData btd) throws Exception
    {
        FamilyRole fr = reqData.getFamilyRole();

        if (fr != null)
        {
            List<ProductModuleData> delPmds = new ArrayList<ProductModuleData>();
            String roleCode = fr.getRoleCode();
            // 副卡UCA
            UcaData uca = reqData.getUca();
            // 1.删除融合成员上挂的融合商品元素
            delFamilyMemberOffers(uca, reqData, delPmds, btd, roleCode);
            // 2.终止关系
            delFamilyMemberRels(uca, reqData, btd, roleCode);
            // 3.终止代付关系
            delMemberPayRels(uca, reqData, btd, roleCode);
        }
    }

    private void delFamilyMemberOffers(UcaData uca, FamilyMemberSingleReqData reqData, List<ProductModuleData> delPmds, BusiTradeData btd, String roleCode) throws Exception
    {
        String familyUserId = reqData.getFamilyUserId();

        // 1.终止优惠
        List<DiscntTradeData> dtds = uca.getUserDiscnts();

        for (DiscntTradeData dtd : dtds)
        {
            if (familyUserId.equals(dtd.getUserIdA()) && roleCode.equals(dtd.getRsrvStr2()))
            {
                DiscntData dd = new DiscntData(dtd.toData());
                dd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                dd.setEndDate(SysDateMgr.getLastDateThisMonth());
                dd.setRemark(dd.getRemark() + ":MEMBER END");
                delPmds.add(dd);
            }
        }
        // 2.终止服务
        List<SvcTradeData> stds = uca.getUserSvcs();

        for (SvcTradeData std : stds)
        {
            if (familyUserId.equals(std.getUserIdA()) && roleCode.equals(std.getRsrvStr2()))
            {
                SvcData sd = new SvcData(std.toData());
                sd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                sd.setEndDate(SysDateMgr.getLastDateThisMonth());
                sd.setRemark(sd.getRemark() + ":MEMBER END");
                delPmds.add(sd);
            }
        }
        // 3.终止平台服务
        List<PlatSvcTradeData> pstds = uca.getUserPlatSvcs();

        for (PlatSvcTradeData pstd : pstds)
        {
            if (familyUserId.equals(pstd.getRsrvStr1()) && roleCode.equals(pstd.getRsrvStr2()))
            {
                PlatSvcData psd = new PlatSvcData(pstd.toData());
                psd.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
                psd.setEndDate(SysDateMgr.getLastDateThisMonth());
                psd.setRemark(psd.getRemark() + ":MEMBER END");
                delPmds.add(psd);
            }
        }
        ProductModuleCreator.createProductModuleTradeData(delPmds, btd);
    }

    private void delFamilyMemberRels(UcaData uca, FamilyMemberSingleReqData reqData, BusiTradeData btd, String roleCode) throws Exception
    {
        IDataset memberRelas = FamilyUserMemberQuery.queryMembersByMemberUserId(uca.getUserId());
        if (IDataUtil.isNotEmpty(memberRelas))
        {
            for (int i = 0; i < memberRelas.size(); i++)
            {
                IData memberRela = memberRelas.getData(i);
                FamilyUserMemberTradeData rtd = new FamilyUserMemberTradeData(memberRela);

                rtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                rtd.setEndDate(SysDateMgr.getLastDateThisMonth());
                rtd.setRemark(rtd.getRemark() + ":MEMBER END");
                btd.add(uca.getSerialNumber(), rtd);

                if (FamilyRolesEnum.PHONE.getRoleCode().equals(roleCode))
                {
                    // TODO 针对手机副卡属于融合特殊产品需要更换产品
                }
            }
        }
    }

    private void delMemberPayRels(UcaData uca, FamilyMemberSingleReqData reqData, BusiTradeData btd, String roleCode) throws Exception
    {

        // 获取成员副卡的默认付费关系
        IDataset payRelas = PayRelaInfoQry.qryPayRelaByUserAcctIdDefTag(uca.getUserId(), reqData.getFmyAcctId(), uca.getUserEparchyCode());
        if (IDataUtil.isNotEmpty(payRelas))
        {
            // 本账期最后一天
            String endCycleId = AcctDayDateUtil.getCycleIdLastDayThisAcct(uca.getUserId());

            for (Object obj : payRelas)
            {
                IData payRela = (IData) obj;
                PayRelationTradeData dprtd = new PayRelationTradeData(payRela);

                dprtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
                dprtd.setEndCycleId(endCycleId);
                dprtd.setRemark("DEL|FMY|452");
                btd.add(uca.getSerialNumber(), dprtd);
            }
        }
    }
}
