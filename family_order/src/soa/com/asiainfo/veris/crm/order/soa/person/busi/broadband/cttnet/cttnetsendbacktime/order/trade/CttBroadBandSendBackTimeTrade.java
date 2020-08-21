
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetsendbacktime.order.trade;

import java.util.Date;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetsendbacktime.order.requestdata.CttBroadBandSendBackTimeReqData;

public class CttBroadBandSendBackTimeTrade extends BaseTrade implements ITrade
{

    private String specDiscntInstId;

    /**
     * 增加特殊补退优惠
     * 
     * @param pd
     * @param td
     * @param userDiscnt
     * @param strStartDate
     * @param strEndDate
     * @return
     * @throws Exception
     */
    public DiscntTradeData addSpecDiscntTrade(BusiTradeData btd, String strStartDate, String strEndDate) throws Exception
    {

        CttBroadBandSendBackTimeReqData reqData = (CttBroadBandSendBackTimeReqData) btd.getRD();
        UcaData ucaData = reqData.getUca();
        String instId = SeqMgr.getInstId();// DualMgr.getSeqId(pd,"SEQ_INST_ID");
        this.setSpecDiscntInstId(instId);// 设置other表中字段

        DiscntTradeData discntTradeData = new DiscntTradeData();
        discntTradeData.setUserId(ucaData.getUserId());
        discntTradeData.setUserIdA("-1");
        discntTradeData.setProductId("-1");
        discntTradeData.setPackageId("-1");
        discntTradeData.setSpecTag("0");
        discntTradeData.setInstId(instId);
        discntTradeData.setCampnId("0");
        discntTradeData.setElementId("31910001"); // 宽带补退特殊优惠编码
        discntTradeData.setStartDate(strStartDate);
        discntTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); // 0-新增,1-删除,2-修改
        discntTradeData.setEndDate(strEndDate);
        discntTradeData.setRemark("宽带退补时长");
        return discntTradeData;
    }

    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        CttBroadBandSendBackTimeReqData reqData = (CttBroadBandSendBackTimeReqData) btd.getRD();
        stringTableTradeMain(btd);
        geneTradeDiscnt(btd);
        geneTradeOther(btd);

    }

    /**
     * @param pd
     * @param td
     * @param strTagDate
     * @param discntDataset
     * @throws Exception
     */
    public void geneOffsetDiscntTrade(BusiTradeData btd, String strTagDate, IDataset discntDataset) throws Exception
    {
        CttBroadBandSendBackTimeReqData reqData = (CttBroadBandSendBackTimeReqData) btd.getRD();

        if (discntDataset == null || discntDataset.isEmpty())
        {
            return;
        }

        String modemDiscntCode = BroadBandInfoQry.getModemDiscntCode(); // 取modem租用优惠编码

        Date tagDate = SysDateMgr.string2Date(strTagDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);

        int sendBackDays = Integer.valueOf(reqData.getSend_back_days()); // 补退时间

        for (int i = 0; i < discntDataset.size(); i++)
        {
            DiscntTradeData discntData = new DiscntTradeData(discntDataset.getData(i));
            String strDiscntCode = discntData.getDiscntCode();
            if (modemDiscntCode.equals(strDiscntCode)) // 不偏移modem租用优惠
            {
                continue;
            }

            String strStartDate = discntData.getStartDate();
            String strEndDate = discntData.getEndDate();

            if (SysDateMgr.string2Date(strStartDate, SysDateMgr.PATTERN_STAND_YYYYMMDD).compareTo(tagDate) >= 0)// 如果优惠的开始时间大于等于标志时间,则需要偏移该优惠
            {
                strStartDate = SysDateMgr.addDays(strStartDate, sendBackDays) + strStartDate.substring(10);
                strEndDate = SysDateMgr.addDays(strEndDate, sendBackDays) + strEndDate.substring(10);

                discntData.setStartDate(strStartDate);
                discntData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                discntData.setEndDate(strEndDate);

                btd.add(reqData.getUca().getSerialNumber(), discntData);
            }
        }
    }

    /**
     * 报开时登记优惠延续信息
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    protected void geneTradeDiscnt(BusiTradeData btd) throws Exception
    {
        CttBroadBandSendBackTimeReqData reqData = (CttBroadBandSendBackTimeReqData) btd.getRD();
        UcaData ucaData = reqData.getUca();
        int sendBackDays = Integer.valueOf(reqData.getSend_back_days()); // 补退时间

        String strStartDate = "";
        String strEndDate = "";
        String strTagEndDate = "";// 结束时间标识
        String formatDate = "yyyy-MM-dd HH:mm:ss";

        String modemDiscntCode = BroadBandInfoQry.getModemDiscntCode();// 取modem租用优惠编码
        IData specDiscntData = getSpecDiscnt(ucaData.getUserId());
        DiscntTradeData specDiscnt = null;// 如果存在有效的补退优惠，设置补退优惠存在标志
        if (IDataUtil.isNotEmpty(specDiscntData))
        {
            specDiscnt = new DiscntTradeData(specDiscntData);
        }

        DiscntTradeData normalDiscnt =null; // 正常优惠

        IDataset userDiscnts = UserDiscntInfoQry.queryUserAllDiscntByUserId(ucaData.getUserId());

        if (userDiscnts != null && userDiscnts.size() > 0)
        {

            for (int i = 0; i < userDiscnts.size(); i++)
            {
                DiscntTradeData userdiscnt = new DiscntTradeData(userDiscnts.getData(i));
                String discntCode = userdiscnt.getDiscntCode();
                strEndDate = userdiscnt.getEndDate();

                if (modemDiscntCode.equals(discntCode))// 如果存在modem租用优惠，不处理
                {
                    continue;
                }
                else if ("31910001".equals(discntCode))// 如果存在有效的补退优惠，设置补退优惠存在标志
                {
                    // specDiscnt = userdiscnt.clone();
                    continue;
                }
                else if (SysDateMgr.string2Date(strEndDate, formatDate).before(SysDateMgr.string2Date(reqData.getAcceptTime(), formatDate)))// 如果优惠已经结束不处理
                {
                    continue;
                }
                else
                {
                    normalDiscnt = userdiscnt.clone();
                    break;
                }
            }

            if (sendBackDays >= 0) // 补退天数为自然数
            {
                if (specDiscnt != null)// 如果存在特殊优惠，则修改特殊优惠
                {
                    strStartDate = specDiscnt.getStartDate(); // 取宽带套餐优惠开始时间
                    strEndDate = specDiscnt.getEndDate(); // 取宽带套餐优惠结束时间
                    strTagEndDate = strEndDate;

                    strEndDate = SysDateMgr.addDays(strEndDate, sendBackDays) + strEndDate.substring(10); // 计算补退后的结束时间

                    this.modifySpecDiscntTrade(btd, specDiscnt, strStartDate, strEndDate); // 修改特殊补退优惠

                    geneOffsetDiscntTrade(btd, strTagEndDate, userDiscnts); // 偏移优惠
                }
                else
                // 如果不存在特殊优惠，增加特殊优惠
                {

                    if (normalDiscnt == null)// 如果当前用户没有有效的宽带优惠，则补退优惠的开始时间取当前时间
                    {
                        strStartDate = reqData.getAcceptTime();
                        strEndDate = strStartDate;
                    }
                    else
                    {
                        strStartDate = normalDiscnt.getEndDate(); // 取宽带套餐优惠开始时间
                        strEndDate = normalDiscnt.getEndDate(); // 取宽带套餐优惠结束时间
                    }

                    strTagEndDate = strEndDate;

                    strEndDate = SysDateMgr.addDays(strEndDate, sendBackDays) + strEndDate.substring(10); // 计算补退后的结束时间

                    geneOffsetDiscntTrade(btd, strTagEndDate, userDiscnts); // 偏移优惠

                    btd.add(ucaData.getSerialNumber(), this.addSpecDiscntTrade(btd, strStartDate, strEndDate));// 修改特殊补退优惠

                }

            }
            else
            // 补退天数为负数，在原有优惠上修改
            {
                if(normalDiscnt == null){
                    CSAppException.apperr(CrmCommException.CRM_COMM_103,"未获取到用户有效资费！");
                }
                strStartDate = normalDiscnt.getStartDate();// 取优惠开始时间
                strEndDate = normalDiscnt.getEndDate();// 取优惠结束时间
                strTagEndDate = strEndDate;

                strEndDate = SysDateMgr.addDays(strEndDate, sendBackDays) + strEndDate.substring(10);
                normalDiscnt.setStartDate(strStartDate);
                normalDiscnt.setModifyTag(BofConst.MODIFY_TAG_UPD); // 0-新增,1-删除,2-修改
                normalDiscnt.setEndDate(strEndDate);
                normalDiscnt.setRemark("宽带退补时长");
                // discntTradeDatas.add(discntTradeData);

                this.setSpecDiscntInstId(normalDiscnt.getInstId()); // 为other表设置inst_id

                geneOffsetDiscntTrade(btd, strTagEndDate, userDiscnts); // 偏移优惠

                btd.add(ucaData.getSerialNumber(), normalDiscnt);
            }

        }
    }

    /**
     * 业务检查
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    // public IData checkBiz(BusiTradeData btd) throws Exception {
    //		
    // IData userBroadbandData = queryBroadband(pd, td); //获取用户宽带装机信息
    // if(userBroadbandData.isEmpty()){
    // common.error("获取用户宽带资料表无数据");
    // }
    //		
    // // 获取用户服务状态
    // IData usersvcstate = new DataMap();
    // usersvcstate.put("X_CONN_DB_CODE", pd.getRouteEparchy());
    // usersvcstate.put("USER_ID", td.getUserId());
    // IDataset datasetsvcstate = getUserSvcState(pd, usersvcstate);
    // if (datasetsvcstate == null || datasetsvcstate.size() == 0) {
    // common.error("515002: " + StateChangeFactory.USER_SVC_STATE_INFO);
    // }
    // td.setBaseCommInfo(userBroadbandData);
    //		
    // String userStateCodeset = td.getUserInfo().getString("USER_STATE_CODESET", "");
    // if(!"0".equals(userStateCodeset)){
    // common.error("宽带用户服务状态不是【开通】状态，不能办理此业务");
    // }
    // return null;
    // }
    /**
     * 设置补退信息
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    protected void geneTradeOther(BusiTradeData btd) throws Exception
    {
        CttBroadBandSendBackTimeReqData reqData = (CttBroadBandSendBackTimeReqData) btd.getRD();
        UcaData ucaData = reqData.getUca();
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setUserId(ucaData.getUserId());
        otherTradeData.setRsrvValueCode("REMAIN_TIME"); // 宽带退补时长标示
        otherTradeData.setRsrvValue(reqData.getSend_back_days());// 补退时长（天）
        otherTradeData.setStartDate(reqData.getAcceptTime());// 开始时间
        otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);// 结束时间
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);// 新增
        otherTradeData.setInstId(SeqMgr.getInstId());
        otherTradeData.setRemark("宽带补退时长操作");// 备注
        otherTradeData.setRsrvStr1(this.getSpecDiscntInstId());// 存储特殊优惠的inst_id,建立对应关系
        btd.add(reqData.getUca().getSerialNumber(), otherTradeData);

    }

    public IData getSpecDiscnt(String userId) throws Exception
    {

        IDataset dataset = UserDiscntInfoQry.getSpecDiscnt(userId);
        if (dataset != null && !dataset.isEmpty())
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                IData data = dataset.getData(i);
                String discntCode = data.getString("DISCNT_CODE", "");
                if ("31910001".equals(discntCode))
                {
                    return data;
                }
            }
        }
        return null;
    }

    public String getSpecDiscntInstId()
    {
        return specDiscntInstId;
    }

    /**
     * 修改特殊补退优惠
     * 
     * @param pd
     * @param td
     * @param specDiscnt
     * @param strStartDate
     * @param strEndDate
     * @return
     * @throws Exception
     */
    public void modifySpecDiscntTrade(BusiTradeData btd, DiscntTradeData specDiscnt, String strStartDate, String strEndDate) throws Exception
    {
        this.setSpecDiscntInstId(specDiscnt.getInstId());

        specDiscnt.setStartDate(strStartDate);
        specDiscnt.setModifyTag(BofConst.MODIFY_TAG_UPD);
        specDiscnt.setEndDate(strEndDate);
        specDiscnt.setRemark("宽带退补时长");

        btd.add(btd.getRD().getUca().getSerialNumber(), specDiscnt);
    }

    public void setSpecDiscntInstId(String specDiscntInstId)
    {
        this.specDiscntInstId = specDiscntInstId;
    }

    /**
     * 设置主台账信息
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    protected void stringTableTradeMain(BusiTradeData btd) throws Exception
    {
        CttBroadBandSendBackTimeReqData reqData = (CttBroadBandSendBackTimeReqData) btd.getRD();
        MainTradeData mainTradeData = btd.getMainTradeData();
        String sendbackdays = reqData.getSend_back_days();
        // td.setChildTradeInfo(X_TRADE_DATA.X_TRADE_MAIN, "REMARK", pd.getParameter("REMARK", ""));
        mainTradeData.setNetTypeCode(CttConstants.NET_TYPE_CODE);
        mainTradeData.setSubscribeType(CttConstants.SUBSCRIBE_TYPE);// 走PBOSS流程
        mainTradeData.setPfType(CttConstants.PF_TYPE);
        mainTradeData.setOlcomTag(CttConstants.OLCOM_TAG);// 发指令流程
        // td.setChildOrderInfo(X_TRADE_ORDER.X_CUST_ORDER, "APP_TYPE", "300");
    }
}
