
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemove.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.userconn.UserConnQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemove.order.requestdata.CttBroadbandMoveReqData;

public class CttBroadbandMoveTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        // 修改联系人信息
        createCustPersonTradeData(btd);

        // 地址信息
        createTradeAddr(btd);

        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRemark("用户宽带移机操作");// 备注
        mainTradeData.setNetTypeCode("11");// 设置网别编码11：宽带用户
        mainTradeData.setSubscribeType("300");// 走PBOSS流程
        mainTradeData.setPfType("300");// 走PBOSS流程
        mainTradeData.setOlcomTag("1");// 发指令流程
        CttBroadbandMoveReqData reqData = (CttBroadbandMoveReqData) btd.getRD();
        UcaData ucaData = reqData.getUca();
        String productId = ucaData.getProductId();
        String productName = UProductInfoQry.getProductNameByProductId(productId);
        mainTradeData.setRsrvStr5(productName);
        // IDataset userSvcInfos = UserSvcInfoQry.queryUserAllSvc(ucaData.getUserId());

    }

    private void createCustPersonTradeData(BusiTradeData btd) throws Exception
    {
        CttBroadbandMoveReqData reqData = (CttBroadbandMoveReqData) btd.getRD();
        CustPersonTradeData cusPerTradeData = reqData.getUca().getCustPerson().clone();
        boolean flag = false;
        if (cusPerTradeData.getPhone() != null && !cusPerTradeData.getPhone().equals(reqData.getPhone()))
        {
            cusPerTradeData.setPhone(reqData.getPhone());
            flag = true;
        }
        if (cusPerTradeData.getContact() != null && !cusPerTradeData.getPhone().equals(reqData.getContact()))
        {
            cusPerTradeData.setContact(reqData.getContact());
            flag = true;
        }
        if (cusPerTradeData.getContactPhone() != null && !cusPerTradeData.getPhone().equals(reqData.getContactPhone()))
        {
            cusPerTradeData.setContactPhone(reqData.getContactPhone());
            flag = true;
        }

        if (flag)
        {
            cusPerTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(reqData.getUca().getSerialNumber(), cusPerTradeData);
        }

    }

    private void createTradeAddr(BusiTradeData btd) throws Exception
    {
        CttBroadbandMoveReqData reqData = (CttBroadbandMoveReqData) btd.getRD();
        IDataset widenetInfo = WidenetInfoQry.getUserWidenetInfo(reqData.getUca().getUserId());
        IData oldWidenetInfo = widenetInfo.getData(0);
        // 删除老的资料
        WideNetTradeData oldWidenetData = new WideNetTradeData(oldWidenetInfo);
        oldWidenetData.setEndDate(reqData.getAcceptTime());
        oldWidenetData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        btd.add(reqData.getUca().getSerialNumber(), oldWidenetData);
        // 新增的宽带资料
        WideNetTradeData wideNetTradeData = new WideNetTradeData(oldWidenetInfo);
        wideNetTradeData.setStartDate(reqData.getAcceptTime());
        wideNetTradeData.setStandAddress(reqData.getStandAddress());
        wideNetTradeData.setDetailAddress(reqData.getAddrDesc());
        wideNetTradeData.setSignPath(reqData.getMofficeId());
        wideNetTradeData.setPhone(reqData.getPhone());
        wideNetTradeData.setContact(reqData.getContact());
        wideNetTradeData.setContact(reqData.getContactPhone());
        wideNetTradeData.setInstId(SeqMgr.getInstId());
        wideNetTradeData.setStandAddressCode(reqData.getStandAddressCode());
        wideNetTradeData.setOldDetailAddress(oldWidenetInfo.getString("STAND_ADDRESS"));
        wideNetTradeData.setOldStandAddressCode(oldWidenetInfo.getString("STAND_ADDRESS_CODE"));
        wideNetTradeData.setOldDetailAddress(oldWidenetInfo.getString("DETAIL_ADDRESS"));
        wideNetTradeData.setRsrvStr1(reqData.getConnectType());
        wideNetTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        IDataset dataset = UserConnQry.getConnInfosByIdA(reqData.getUca().getUserId(), "BK"); // 查询用户宽带固话共线信息
        if (IDataUtil.isNotEmpty(dataset))
        {
            wideNetTradeData.setRsrvStr5(dataset.getData(0).getString("USER_ID_B", ""));
        }
        else
        {
            wideNetTradeData.setRsrvStr5(oldWidenetInfo.getString("RSRV_STR5", ""));
        }

        btd.add(reqData.getUca().getSerialNumber(), wideNetTradeData);
    }

}
