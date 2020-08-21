
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemodifyacct.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetActTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetchangemodifyacct.order.requestdata.CttBroadbandModifyAcctReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;

public class CttBroadbandModifyAcctTrade extends BaseTrade implements ITrade
{

    /**
     * 校验账号是否存在
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void checkAcctId(BusiTradeData btd) throws Exception
    {
        CttBroadbandModifyAcctReqData reqData = (CttBroadbandModifyAcctReqData) btd.getRD();
        String newActId = reqData.getNewAcctId();
        IDataset dataset = WidenetInfoQry.getUserWidenetActInfos(newActId);
        if (IDataUtil.isNotEmpty(dataset))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_19, newActId);
        }

        // 检查是否已有宽带账号变更业务占用了账号
        dataset = TradeInfoQry.qryTradeInfosBySnTrade(newActId, "0", "9732", "9711");
        if (IDataUtil.isNotEmpty(dataset))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_19, newActId);
        }
    }

    /**
     * 检查业务
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    public void checkBiz(BusiTradeData btd) throws Exception
    {
        IData param = new DataMap();
        UcaData ucaData = btd.getRD().getUca();
        String userId = ucaData.getUserId();
        IDataset addrInfoDataset = WidenetInfoQry.getUserWidenetInfo(userId);// BroadBandInfoQry.queryBroadBandAddressInfo(param);
        if (addrInfoDataset.isEmpty())
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_13, param.getString("SERIAL_NUMBER"));
        }

        // 获取用户服务状态
        IDataset datasetsvcstate = UserSvcStateInfoQry.queryUserSvcStateInfo(userId);
        if (datasetsvcstate == null || datasetsvcstate.size() == 0)
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_28, ucaData.getSerialNumber());
        }

        // 判断是否军区用户
        if (isBroadbandMilitary(ucaData.getSerialNumber()))
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_29);
            // common.error("军区用户不能修改宽带账号");
        }

    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {

        checkBiz(btd);
        checkAcctId(btd);
        // 登记主台账
        geneMainTrade(btd);

        // 登记宽带账号台账
        geneTradAccessAcct(btd);

        // 修改用户资料表serialNumber为宽带帐号
        geneTradeUserTrade(btd);

        // 修改营销活动信息
        geneTradeSaleActive(btd);

        // 修改用户关系表
        geneTradeRelation(btd);

        // 修改信息异动表 加action
        // geneTradeInfoChange(btd);

    }

    private void geneMainTrade(BusiTradeData btd) throws Exception
    {
        CttBroadbandModifyAcctReqData reqData = (CttBroadbandModifyAcctReqData) btd.getRD();

        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRsrvStr1(reqData.getNewAcctId());
        mainTradeData.setNetTypeCode(CttConstants.NET_TYPE_CODE);
        mainTradeData.setSubscribeType(CttConstants.SUBSCRIBE_TYPE);
        mainTradeData.setPfType(CttConstants.PF_TYPE);
        mainTradeData.setOlcomTag(CttConstants.OLCOM_TAG);

    }

    private void geneTradAccessAcct(BusiTradeData btd) throws Exception
    {
        CttBroadbandModifyAcctReqData reqData = (CttBroadbandModifyAcctReqData) btd.getRD();

        // 获取用户宽带账号信息--
        IDataset accessDataList = BroadBandInfoQry.getBroadBandWidenetActByUserId(reqData.getUca().getUserId());
        if (accessDataList.isEmpty())
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_11, reqData.getUca().getSerialNumber());
        }
        WideNetActTradeData accTradeData = new WideNetActTradeData(accessDataList.getData(0));
        accTradeData.setAcctId(reqData.getNewAcctId());
        accTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        accTradeData.setStartDate(reqData.getAcceptTime());
        btd.add(reqData.getUca().getSerialNumber(), accTradeData);
    }

    private void geneTradeRelation(BusiTradeData btd) throws Exception
    {
        CttBroadbandModifyAcctReqData reqData = (CttBroadbandModifyAcctReqData) btd.getRD();

        String userId = reqData.getUca().getUserId();

        IDataset relaUUInfos = RelaUUInfoQry.qryRelaUUByUserIdBAndEndDate(userId, reqData.getAcceptTime());
        if (IDataUtil.isNotEmpty(relaUUInfos))
        {
            for (int i = 0; i < relaUUInfos.size(); i++)
            {
                RelationTradeData relationTradeData = new RelationTradeData(relaUUInfos.getData(i));
                relationTradeData.setSerialNumberB(reqData.getNewAcctId());
                relationTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                btd.add(reqData.getUca().getSerialNumber(), relationTradeData);
            }
        }
    }

    private void geneTradeSaleActive(BusiTradeData btd) throws Exception
    {
        CttBroadbandModifyAcctReqData reqData = (CttBroadbandModifyAcctReqData) btd.getRD();

        String userId = reqData.getUca().getUserId();

        IDataset userSaleActive = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
        if (IDataUtil.isNotEmpty(userSaleActive))
        {
            for (int i = 0; i < userSaleActive.size(); i++)
            {
                SaleActiveTradeData saleActiveTradeData = new SaleActiveTradeData(userSaleActive.getData(i));
                saleActiveTradeData.setSerialNumber(reqData.getNewAcctId());
                saleActiveTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                btd.add(reqData.getUca().getSerialNumber(), saleActiveTradeData);
            }
        }

    }

    private void geneTradeUserTrade(BusiTradeData btd) throws Exception
    {
        CttBroadbandModifyAcctReqData reqData = (CttBroadbandModifyAcctReqData) btd.getRD();

        UserTradeData userTradeData = reqData.getUca().getUser().clone();

        userTradeData.setSerialNumber(reqData.getNewAcctId());
        userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);

        btd.add(reqData.getUca().getSerialNumber(), userTradeData);

    }

    /**
     * 查询军区宽带用户
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public boolean isBroadbandMilitary(String serialNumber) throws Exception
    {

        IDataset dataset = BroadBandInfoQry.qryisBroadbandMilitary(serialNumber, "1");

        if (dataset != null && !dataset.isEmpty())
        {
            return dataset.getData(0).getInt("NUM", 0) > 0;
        }

        return false;
    }

}
