
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyRolesEnum;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyMemberSingleReqData;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilyRole;
import com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata.FamilySaleActiveData;
import com.asiainfo.veris.crm.iorder.soa.family.common.trade.BaseFamilyBusiTrade;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.FamilyBusiRegUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FamilyMemberChaTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FamilyUserMemberTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;

/**
 * @Description 成员新增受理类
 * @Auther: zhenggang
 * @Date: 2020/7/31 17:18
 * @version: V1.0
 */
public class AddSingleFamilyMemberTrade extends BaseFamilyBusiTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        FamilyMemberSingleReqData reqData = (FamilyMemberSingleReqData) btd.getRD();
        FamilyRole familyRole = reqData.getFamilyRole();
        // 1.创建关系台账
        this.createFamilyUserMemberTrade(reqData, familyRole, btd);
        // 2.营销活动处理
        this.createSaleActiveTrade(reqData, familyRole, btd);
        // 3.主台账设置
        btd.getMainTradeData().setRsrvStr1("MANAGER_SN|" + reqData.getManagerSn());
        btd.getMainTradeData().setRsrvStr2("ROLE_CODE|" + familyRole.getRoleCode());
        btd.getMainTradeData().setRsrvStr3("ROLE_TYPE|" + familyRole.getRoleType());
        btd.getMainTradeData().setRsrvStr4("FAMILY_ACCT_ID|" + reqData.getFmyAcctId());
        btd.getMainTradeData().setRsrvStr5("TOP_TRADE_ID|" + reqData.getTopTradeId());
        // 4.设置短信字段
        btd.getMainTradeData().setRsrvStr8(reqData.getPrintContent());
        // 5.商品处理
        super.createBusiTradeData(btd);
        // 6.处理工单依赖台账
        FamilyBusiRegUtil.createLimitTrade(reqData);
    }

    /**
     * @Description: 成员关系登记
     * @Param: [reqData, fr, btd]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/31 18:13
     */
    private void createFamilyUserMemberTrade(FamilyMemberSingleReqData reqData, FamilyRole fr, BusiTradeData btd) throws Exception
    {
        FamilyUserMemberTradeData futd = new FamilyUserMemberTradeData();
        futd.setInstId(reqData.getFamilyMemberInstId());
        futd.setFamilyUserId(reqData.getFamilyUserId());
        futd.setFamilySerialNum(reqData.getFamilySn());
        futd.setMainSerialNum(fr.getMemberMainSn());
        futd.setMemberSerialNum(reqData.getUca().getSerialNumber());
        futd.setMemberUserId(reqData.getUca().getUserId());
        futd.setMemberRoleCode(fr.getRoleCode());
        futd.setMemberRoleType(fr.getRoleType());
        futd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        if (FamilyConstants.FamilyTradeType.ACCEPT.getValue().equals(btd.getRD().getOrderTypeCode()))
        {
            futd.setStartDate(btd.getRD().getAcceptTime());
        }
        else
        {
            futd.setStartDate(SysDateMgr.getFirstDayOfNextMonth());
        }
        futd.setEndDate(SysDateMgr.END_DATE_FOREVER);
        futd.setRemark("FMY|" + btd.getRD().getOrderTypeCode());

        // 记录用户加入之前的产品，恢复产品时使用
        if (FamilyConstants.TYPE_OLD.equals(fr.getRoleType()))
        {
            futd.setRsrvStr1("RECOVER_PRODUCT_ID|" + reqData.getUca().getUserMainProduct().getProductId());
            if (null != reqData.getUca().getUserNextMainProduct())
            {
                futd.setRsrvStr1("RECOVER_PRODUCT_ID|" + reqData.getUca().getUserNextMainProduct().getProductId());
            }
        }

        btd.add(reqData.getUca().getSerialNumber(), futd);

        if (reqData.getUca().getSerialNumber().equals(reqData.getManagerSn()) && fr.getRoleCode().equals(FamilyRolesEnum.PHONE.getRoleCode()))
        {
            // 管理员新增登记成员属性表
            FamilyMemberChaTradeData fmtd = new FamilyMemberChaTradeData();
            fmtd.setInstId(SeqMgr.getInstId());
            fmtd.setFamilyUserId(reqData.getFamilyUserId());
            fmtd.setMemberUserId(reqData.getUca().getUserId());
            fmtd.setChaType(FamilyConstants.CHA_TYPE.MANAGER);
            fmtd.setRelInstId(futd.getInstId());
            fmtd.setChaCode(FamilyConstants.FamilyMemCha.FAMILY_MANAGER.getValue());
            fmtd.setChaValue(FamilyConstants.SWITCH.YES);
            fmtd.setStartDate(futd.getStartDate());
            fmtd.setEndDate(futd.getEndDate());
            fmtd.setModifyTag(futd.getModifyTag());
            btd.add(reqData.getUca().getSerialNumber(), fmtd);
        }
        // 成员关系生成后回填角色对象的实例ID，后面调用454,458使用
        reqData.getFamilyRole().setRoleInstId(futd.getInstId());
    }

    /**
     * @Description: 成员营销活动订购
     * @Param: [reqData, familyRole, btd]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/31 18:13
     */
    private void createSaleActiveTrade(FamilyMemberSingleReqData reqData, FamilyRole familyRole, BusiTradeData btd) throws Exception
    {
        List<ProductModuleData> fsads = familyRole.getRoleSaleActiveDatas();

        if (null != fsads && fsads.size() > 0)
        {
            for (ProductModuleData pmd : fsads)
            {
                IData param = new DataMap();
                FamilySaleActiveData fsad = (FamilySaleActiveData) pmd;
                param.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
                param.put("ROUTE_EPARCHY_CODE", reqData.getTopEparchyCode());
                param.put("PRODUCT_ID", fsad.getProductId());
                param.put("PACKAGE_ID", fsad.getSalePackageId());
                param.put("START_DATE", fsad.getStartDate());
                IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param);
                if (IDataUtil.isNotEmpty(result))
                {
                    String saleactiveTradeId = result.first().getString("TRADE_ID");
                    FamilyBusiRegUtil.insertTradeLimit(saleactiveTradeId, reqData.getTradeId(), reqData.getUca().getUserEparchyCode());
                }
            }
        }
    }
}
