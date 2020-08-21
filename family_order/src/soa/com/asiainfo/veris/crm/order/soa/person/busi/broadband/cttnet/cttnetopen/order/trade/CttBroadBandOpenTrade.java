
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetopen.order.trade;

import java.util.Date;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.SvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.CttConstants;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetopen.order.requestdata.CttBroadBandOpenReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;

public class CttBroadBandOpenTrade extends BaseTrade implements ITrade
{
    private int sendBackDays = 0; // 报开宽带时长补退天数

    private int offsetDays = 0; // 偏移天数

    private String specStartDate = ""; // 补退优惠开始时间

    private String vaildEndDate = ""; // 有效优惠结束时间

    private String specDiscntInstId = ""; // 特殊优惠inst_id

    /**
     * 计算报开补退时长
     * 
     * @param discntData
     * @param lastStopDate
     * @return
     * @throws Exception
     */
    public int calculateSendBackDay(DiscntTradeData discntData, Date lastStopDate) throws Exception
    {
        int days = 0;
        String format = "yyyy-MM-dd";
        Date startDate = SysDateMgr.string2Date(discntData.getStartDate(), format);// sdf.parse(discntData.getStartDate());
        Date endDate = SysDateMgr.string2Date(discntData.getEndDate(), format); // sdf.parse(discntData.getEndDate());
        Date today = new Date();

        if (today.before(startDate))
        {// 当前报开时间在开始时间之前
            days = 0;

        }
        else if (today.before(endDate))
        {// 当前报开时间在优惠开始时间之后，在优惠结束时间之前
            if (lastStopDate.before(startDate))
            {// 最近一次报停时间，在优惠开始时间之前，计算优惠开始时间到报开时间为补退时长
                days = this.getDaysBetweenDate(startDate, today);
            }
            else
            {// 最近一次报停时间，在优惠开始时间之后，在当前报开时间之前，计算报停时间到当前报开时间为补退时长
                days = this.getDaysBetweenDate(lastStopDate, today);
            }
        }
        else
        {// 当前报开时间在结束时间只后
            if (lastStopDate.before(startDate))
            {// 报停时间在优惠开始时间之前，计算优惠开始时间到优惠结束时间为补退时长
                days = this.getDaysBetweenDate(startDate, endDate);
            }
            else if (lastStopDate.before(endDate))
            {// 报停时间在优惠开始时间之后，在优惠结束时间之前，计算报停时间到优惠结束时间段为补退时长
                days = this.getDaysBetweenDate(lastStopDate, endDate);
            }
            else
            {// 报停时间在优惠结束时间之后，没有报开补退时长
                days = 0;
            }
        }
        return days;
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        ChangeSvcStateComm changeSvcStateComm = new ChangeSvcStateComm();
        changeSvcStateComm.getSvcStateChangeTrade(btd); // 获取用户服务状态变更订单
        changeSvcStateComm.modifyMainSvcStateByUserId(btd);// 修改用户主体服务状态和最后停机时间

        geneTradeDiscnt(btd);// 登记优惠延续台账信息

        geneTradeOther(btd);

        geneTradeMain(btd);

        // geneTradeSvcState(btd);
    }

    private void geneTradeDiscnt(BusiTradeData btd) throws Exception
    {
        CttBroadBandOpenReqData openReqData = (CttBroadBandOpenReqData) btd.getRD();
        UcaData ucaData = openReqData.getUca();
        // TODO Auto-generated method stub
        DiscntTradeData specDiscntData = null; // 补退优惠
        DiscntTradeData normalDiscntData = null; // 宽带套餐优惠

        // DateFormatUtils.

        Date today = new Date();
        String format = "yyyy-MM-dd HH:mm:ss";
        Date lastStopDate = SysDateMgr.string2Date(ucaData.getUser().getLastStopTime(), format);
        // sdf.parse(ucaData.getUser().getLastStopTime());//最后停机时间

        String modemDiscntCode = this.getModemDiscntCode();// 获取moden租用特殊优惠参数

        IDataset userDiscntDataset =UserDiscntInfoQry.queryUserAllDiscntByUserId(ucaData.getUserId());

        if (userDiscntDataset != null && userDiscntDataset.size() > 0)
        {
            for (int i = 0; i < userDiscntDataset.size(); i++)
            {

                DiscntTradeData userDiscntTradeData = new DiscntTradeData(userDiscntDataset.getData(i));
                int days = 0;
                String discntCode = userDiscntTradeData.getDiscntCode();

                // 如果在报停前，优惠已经结束，不处理
                Date discntEndDate = SysDateMgr.string2Date(userDiscntTradeData.getEndDate(), format);
                // sdf.parse(userDiscntTradeData.getEndDate());
                if (discntEndDate.before(lastStopDate))
                {
                    continue;
                }

                // 如果在报开时，优惠还没有开始,并且不是补退优惠，不处理
                Date discntStartDate = SysDateMgr.string2Date(userDiscntTradeData.getStartDate(), format);
                // sdf.parse(userDiscntTradeData.getStartDate());
                if (today.before(discntStartDate) && !"31910001".equals(discntCode))
                {
                    continue;
                }

                if (modemDiscntCode.equals(discntCode))// 如果用户有modem租用特殊优惠,不处理该优惠
                {
                    continue;
                }
                else if ("31910001".equals(discntCode))// 补退特殊优惠编码
                {
                    specDiscntData = new DiscntTradeData(userDiscntDataset.getData(i));
                    days = this.calculateSendBackDay(userDiscntTradeData, lastStopDate); // 计算补退优惠产生的补退时长
                    this.setSendBackDays(this.getSendBackDays() + days); // 设置补退天数
                }
                else
                {
                    normalDiscntData = new DiscntTradeData(userDiscntDataset.getData(i));
                    days = this.calculateSendBackDay(userDiscntTradeData, lastStopDate); // 计算宽带套餐产生的补退时长
                    this.setSendBackDays(this.getSendBackDays() + days); // 设置补退天数
                }
            }

            String strStartDate = "";
            String strEndDate = "";

            // 如果没有补退时长，不登记台账
            if (this.getSendBackDays() <= 0)
            {
                return;
            }

            if (specDiscntData != null)
            {// 如果存在有效的补退优惠,修改补退优惠

                strStartDate = specDiscntData.getStartDate();
                strEndDate = specDiscntData.getEndDate();

                this.setOffsetDays(this.getDaysBetweenDate(lastStopDate, today)); // 设置优惠偏移时间

                strEndDate = SysDateMgr.addDays(strEndDate, this.getOffsetDays()) + strEndDate.substring(10);
                // DateUtils.addDays(sdf.parse(strEndDate),this.getOffsetDays()) + strEndDate.substring(10); //计算优惠结束时间

                this.setVaildEndDate(strEndDate); // 设置有效优惠结束时间

                // 设置inst_id，提供给TF_F_USER_OTHER表记录
                String instId = specDiscntData.getInstId();
                this.setSpecDiscntInstId(instId);

                specDiscntData.setStartDate(strStartDate);
                specDiscntData.setEndDate(strEndDate);
                specDiscntData.setModifyTag(BofConst.MODIFY_TAG_UPD);// 0-新增,1-删除,2-修改
                specDiscntData.setRemark("宽带报开补退时长");

                // discntTradeDatas.add(discntTradeData);
                btd.add(ucaData.getSerialNumber(), specDiscntData);

                // 偏移特殊优惠之后的正常优惠时间
                for (int i = 0; i < userDiscntDataset.size(); i++)
                {
                    DiscntTradeData discntTradeData = new DiscntTradeData(userDiscntDataset.getData(i));
                    String discntCode = discntTradeData.getDiscntCode();
                    // sdf.parse(userDiscntDataset.get(i).getStartDate());//优惠开始时间
                    Date discntStartDate = SysDateMgr.string2Date(discntTradeData.getStartDate(), format);

                    // sdf.parse(specDiscntData.getEndDate()); //特殊优惠的结束时间
                    Date specDiscntEndDate = SysDateMgr.string2Date(specDiscntData.getEndDate(), format);

                    // 如果优惠开始时间大于等于补退优惠结束时间，并且优惠不是补退优惠、不是modeom租用优惠,需要偏移该优惠
                    if (discntStartDate.compareTo(specDiscntEndDate) >= 0 && !modemDiscntCode.equals(discntCode) && !"31910001".equals(discntCode))
                    {
                        geneTradeNormalDiscnt(btd, discntTradeData, this.getOffsetDays()); // 偏移优惠
                    }
                }

            }
            else if (normalDiscntData != null) // 不存在补退优惠新增补退优惠
            {
                strStartDate = normalDiscntData.getStartDate(); // 以原有优惠的结束时间为补退优惠的开始时间
                strEndDate = normalDiscntData.getEndDate();

                this.setOffsetDays(this.getDaysBetweenDate(lastStopDate, today)); // 设置优惠偏移时间

                // DateUtils.addDays(sdf.parse(strEndDate),this.getOffsetDays()) + strEndDate.substring(10); //计算优惠结束时间
                strEndDate = SysDateMgr.addDays(strEndDate, this.getOffsetDays()) + strEndDate.substring(10);

                this.setVaildEndDate(strEndDate); // 设置优惠优惠结束时间

                // 设置inst_id，提供给TF_F_USER_OTHER表记录
                String instId = SeqMgr.getInstId();
                this.setSpecDiscntInstId(instId);

                DiscntTradeData discntTradeData = new DiscntTradeData();
                discntTradeData.setUserId(ucaData.getUserId());
                discntTradeData.setUserIdA("-1");
                discntTradeData.setProductId("-1");
                discntTradeData.setPackageId("-1");
                discntTradeData.setElementId("31910001"); // 补退特殊优惠
                discntTradeData.setSpecTag(normalDiscntData.getSpecTag());
                discntTradeData.setRelationTypeCode(normalDiscntData.getRelationTypeCode());
                discntTradeData.setCampnId(normalDiscntData.getCampnId());
                discntTradeData.setInstId(SeqMgr.getInstId());
                discntTradeData.setStartDate(strStartDate);// 以原有优惠的结束时间为补退优惠的开始时间
                discntTradeData.setEndDate(strEndDate);
                discntTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); // 0-新增,1-删除,2-修改
                discntTradeData.setRemark("宽带报开补退时长");
                btd.add(ucaData.getSerialNumber(), discntTradeData);

                // 偏移未开始的优惠
                for (int i = 0; i < userDiscntDataset.size(); i++)
                {
                    DiscntTradeData NDiscntTradeData = new DiscntTradeData(userDiscntDataset.getData(i));
                    
                    String discntCode = NDiscntTradeData.getDiscntCode();
                    Date discntStartDate = SysDateMgr.string2Date(NDiscntTradeData.getStartDate(), format);
                    // sdf.parse(userDiscntDataset.get(i).getStartDate());

                    // 如果优惠开始时间大于等于报开时间，并且优惠不是补退优惠、不是modeom租用优惠,需要偏移该优惠
                    if (discntStartDate.compareTo(today) >= 0 && !modemDiscntCode.equals(discntCode) && !"31910001".equals(discntCode))
                    {
                        geneTradeNormalDiscnt(btd,NDiscntTradeData, this.getOffsetDays()); // 偏移优惠
                    }
                }
            }
            // 设置优惠开始时间，提供给TF_F_USER_OTHER表记录
            this.setSpecStartDate(strStartDate);

        }

    }

    private void geneTradeMain(BusiTradeData btd) throws Exception
    {
        MainTradeData mainTradeData = btd.getMainTradeData();

        mainTradeData.setNetTypeCode(CttConstants.NET_TYPE_CODE);
        mainTradeData.setSubscribeType(CttConstants.SUBSCRIBE_TYPE);// 走PBOSS流程
        mainTradeData.setPfType(CttConstants.PF_TYPE); // 走PBOSS流程
        mainTradeData.setOlcomTag(CttConstants.OLCOM_TAG); // 发指令流程
    }

    /**
     * 修改宽带套餐优惠
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    protected void geneTradeNormalDiscnt(BusiTradeData btd, DiscntTradeData discntData, int offsetDay) throws Exception
    {

        // 偏移开始时间
        String strStartDate = discntData.getStartDate();

        strStartDate = SysDateMgr.addDays(strStartDate, offsetDay) + strStartDate.substring(10);
        // DateUtils.addDays(sdf.parse(strStartDate), offsetDay) + strStartDate.substring(10);

        // 偏移结束时间
        String strEndDate = discntData.getEndDate();
        strEndDate = SysDateMgr.addDays(strEndDate, offsetDay) + strEndDate.substring(10);
        // DateUtils.addDays(sdf.parse(strEndDate), offsetDay) + strEndDate.substring(10);
        this.setVaildEndDate(strEndDate); // 设置优惠优惠结束时间

        DiscntTradeData discntTradeData = discntData.clone();

        discntTradeData.setStartDate(strStartDate);
        discntTradeData.setEndDate(strEndDate);
        discntTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);

        btd.add(btd.getRD().getUca().getSerialNumber(), discntTradeData);
    }

    /**
     * 设置补退信息
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    protected void geneTradeOther(BusiTradeData btd) throws Exception
    {

        OtherTradeData inparams = new OtherTradeData();
        CttBroadBandOpenReqData openReqData = (CttBroadBandOpenReqData) btd.getRD();

        UcaData ucaData = openReqData.getUca();

        inparams.setUserId(ucaData.getUserId());
        inparams.setRsrvValueCode("REMAIN_TIME"); // 宽带退补时长标示
        inparams.setRsrvValue(String.valueOf(this.getSendBackDays())); // 补退时长（天）
        inparams.setStartDate(openReqData.getAcceptTime()); // 开始时间
        inparams.setEndDate(SysDateMgr.END_DATE_FOREVER);// 结束时间
        inparams.setModifyTag(BofConst.MODIFY_TAG_ADD); // 新增
        inparams.setInstId(SeqMgr.getInstId());
        inparams.setRemark("宽带补退时长操作");// 备注
        inparams.setRsrvTag1("1"); // 设置报开补退标示
        inparams.setRsrvDate1(this.getSpecStartDate()); // 设置补退开始时间
        inparams.setRsrvDate2(openReqData.getAcceptTime()); // 设置报开时间
        inparams.setRsrvStr1(this.getSpecDiscntInstId());
        btd.add(ucaData.getSerialNumber(), inparams);
    }

    private void geneTradeSvcState(BusiTradeData btd) throws Exception
    {
        CttBroadBandOpenReqData openReqData = (CttBroadBandOpenReqData) btd.getRD();
        UcaData ucaData = openReqData.getUca();

        IDataset stateparams = new DatasetList();
        stateparams = SvcStateInfoQry.queryTradeTypeSvcStates(btd.getTradeTypeCode(), ucaData.getBrandCode(), ucaData.getProductId(), ucaData.getUser().getEparchyCode());
        IData tempdata = new DataMap();
        if (!stateparams.isEmpty() && stateparams.size() > 0)
        {
            for (int i = 0; i < stateparams.size(); i++)
            {
                tempdata = stateparams.getData(i);
                IDataset userSvcStates = UserSvcStateInfoQry.getUserSvcStateBySvcId(ucaData.getUserId(), tempdata.getString("SERVICE_ID"), tempdata.getString("OLD_STATE_CODE"));
                if (!IDataUtil.isEmpty(userSvcStates))
                {
                    SvcStateTradeData delData = new SvcStateTradeData(userSvcStates.getData(0));
                    delData.setServiceId((String) stateparams.get(i, "SERVICE_ID"));
                    delData.setEndDate(openReqData.getAcceptTime());
                    delData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(ucaData.getSerialNumber(), delData);

                    SvcStateTradeData addData = new SvcStateTradeData();
                    addData.setUserId(ucaData.getUserId());
                    addData.setServiceId((String) stateparams.get(i, "SERVICE_ID"));
                    addData.setMainTag((String) userSvcStates.get(0, "MAIN_TAG"));
                    addData.setStateCode((String) stateparams.get(0, "NEW_STATE_CODE"));
                    addData.setStartDate(openReqData.getAcceptTime());
                    addData.setEndDate(SysDateMgr.END_DATE_FOREVER);
                    addData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    addData.setInstId(SeqMgr.getInstId());
                    btd.add(ucaData.getSerialNumber(), addData);

                }
            }
        }
    }

    /**
     * 查询2个日期相差天数
     * 
     * @param firstDate
     * @param secrondDate
     * @return
     */
    public int getDaysBetweenDate(Date firstDate, Date secondDate) throws Exception
    {

        long firstTime = firstDate.getTime();
        long secondTime = secondDate.getTime();
        return Integer.parseInt(String.valueOf((secondTime - firstTime) / (1000 * 60 * 60 * 24)));
    }

    /**
     * 获取moden租用特殊优惠参数
     * 
     * @return
     * @throws Exception
     */
    public String getModemDiscntCode() throws Exception
    {
        String discntCode = "";
        // TODO PARAM_ATTR 暂定 1128
        IDataset dataset = CommparaInfoQry.getCommparaCode1("CSM", "1128", "D", "ZZZZ");
        if (dataset != null && !dataset.isEmpty())
        {
            discntCode = dataset.getData(0).getString("PARA_CODE1", "");
        }
        return discntCode;
    }

    public int getOffsetDays()
    {
        return offsetDays;
    }

    public int getSendBackDays()
    {
        return sendBackDays;
    }

    public String getSpecDiscntInstId()
    {
        return specDiscntInstId;
    }

    public String getSpecStartDate()
    {
        return specStartDate;
    }

    public String getVaildEndDate()
    {
        return vaildEndDate;
    }

    public void setOffsetDays(int offsetDays)
    {
        this.offsetDays = offsetDays;
    }

    public void setSendBackDays(int sendBackDays)
    {
        this.sendBackDays = sendBackDays;
    }

    public void setSpecDiscntInstId(String specDiscntInstId)
    {
        this.specDiscntInstId = specDiscntInstId;
    }

    public void setSpecStartDate(String specStartDate)
    {
        this.specStartDate = specStartDate;
    }

    public void setVaildEndDate(String vaildEndDate)
    {
        this.vaildEndDate = vaildEndDate;
    }

}
