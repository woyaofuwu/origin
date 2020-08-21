
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.acctday;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.AcctDayException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.AcctDayData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.IAcctDayChangeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserAcctDayTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changeacctday.ChangeAcctDaySVC;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: ChangeAcctDayAction.java
 * @Description: 用户主被动账期变更,拼用户因账期变更导致产品、资费、服务、活动有效期变更信息台账【仅针对用户下账期生效的账期变更方式】
 * @version: v1.0.0
 * @author: maoke
 * @date: 下午15:36:22 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-07-25 maoke v1.0.0 修改原因
 */

public class ChangeAcctDayAction implements ITradeAction
{

    /**
     * 因账期变更,计算开始、结束时间、设置MODIFY_TAG
     * 
     * @param acctDayChangeData
     * @param acctDay
     * @param acctDayStartDate
     * @param acctDayEndDate
     * @param acctDayFirstDate
     * @param bookAcctDay
     * @param bookAcctDayStartDate
     * @param bookAcctDayEndDate
     * @param bookAcctDayFirstDate
     * @return
     * @throws Exception
     */
    private void countVlidDateForAcctDayChg(BusiTradeData btd, IAcctDayChangeData acctDayChangeData, String acctDay, String acctDayStartDate, String acctDayEndDate, String acctDayFirstDate, String bookAcctDay, String bookAcctDayStartDate,
            String bookAcctDayEndDate, String bookAcctDayFirstDate) throws Exception
    {
        String tempStartDate = "";
        String tempEndDate = "";

        String startDate = this.formatDate(acctDayChangeData.getStartDate());// 资料开始时间
        String endDate = this.formatDate(acctDayChangeData.getEndDate());// 资料结束时间

        String rsrvDate2 = this.dealRsrvDate(acctDayChangeData.getRsrvDate2());// 资料最初始开始时间,资料如未进行过账期变更,此字段应是空值
        String rsrvDate3 = this.dealRsrvDate(acctDayChangeData.getRsrvDate3());// 资料最初始结束时间,资料如未进行过账期变更,此字段应是空值

        boolean isChgStartDate = false;// 记录资料的开始时间是否被修改
        String oldStartDate = acctDayChangeData.getStartDate();// 记录原始资料的开始时间

        // 开始时间计算
        if ("".equals(rsrvDate2))// RSRV_DATE2为空:资料原始开始是空值
        {
            if (startDate.compareTo(bookAcctDayStartDate) > 0)// 资料开始时间大于下账期开始时间
            {
                tempStartDate = SysDateMgr.getFirstDayNextAcct(startDate, bookAcctDay, bookAcctDayFirstDate);

                acctDayChangeData.setStartDate(tempStartDate + getDateHourMinSec(acctDayChangeData.getStartDate(), "0"));

                isChgStartDate = true;// 记录资料的开始时间被修改

            }
            acctDayChangeData.setModifyTag("2");

            acctDayChangeData.setRsrvDate2(startDate);
        }
        else
        // RSRV_DATE2非空:资料原始开始非空值
        {
            if (startDate.compareTo(bookAcctDayStartDate) > 0)// 处理开始时间,只对【资料开始时间大于下账期开始时间】做处理
            {
                String tempStart = SysDateMgr.getFirstDayNextAcct(startDate, bookAcctDay, bookAcctDayFirstDate);

                if (SysDateMgr.dayInterval(tempStart, rsrvDate2) > 30)// 开始时间日期归属账期的下账期的第一天 - 资料最初始时间 > 30
                // 天时,时间以最初始开始时间RSRV_DATE2为准
                {
                    tempStartDate = SysDateMgr.getFirstDayNextAcct(rsrvDate2, bookAcctDay, bookAcctDayFirstDate);

                    acctDayChangeData.setStartDate(tempStartDate + getDateHourMinSec(acctDayChangeData.getStartDate(), "0"));

                    acctDayChangeData.setModifyTag("2");
                }
                else
                {
                    tempStartDate = SysDateMgr.getFirstDayNextAcct(startDate, bookAcctDay, bookAcctDayFirstDate);

                    acctDayChangeData.setStartDate(tempStartDate + getDateHourMinSec(acctDayChangeData.getStartDate(), "0"));

                    acctDayChangeData.setModifyTag("2");
                }
            }
        }
        // 结束时间计算
        if ("".equals(rsrvDate3))// RSRV_DATE3为空:资料原始结束时间是空值
        {
            if (endDate.compareTo(acctDayEndDate) > 0)// 资料结束时间大于本账期结束时间
            {
                tempEndDate = SysDateMgr.getLastDayThisAcct(endDate, bookAcctDay, bookAcctDayFirstDate);

                acctDayChangeData.setEndDate(tempEndDate + getDateHourMinSec(endDate, "1"));
            }
            else
            // 资料结束时间小于本账期结束时间,取原有结束时间
            {
                acctDayChangeData.setEndDate(acctDayChangeData.getEndDate());
            }

            acctDayChangeData.setModifyTag("2");

            acctDayChangeData.setRsrvDate3(endDate);// 记录资料最原始结束时间到RSRV_DATE3
        }
        else
        // RSRV_DATE3非空:资料原始结束时间非空值
        {
            String tempEnd = SysDateMgr.getLastDayThisAcct(endDate, bookAcctDay, bookAcctDayFirstDate);

            if (SysDateMgr.dayInterval(tempEnd, rsrvDate3) > 30)// 结束时间日期归属账期的最后一天 - 资料最初始结束时间 > 30
            // 天时,时间以最初始结束时间RSRV_DATE3为准
            {
                tempEndDate = SysDateMgr.getLastDayThisAcct(rsrvDate3, bookAcctDay, bookAcctDayFirstDate);

                acctDayChangeData.setEndDate(tempEndDate + getDateHourMinSec(endDate, "1"));
            }
            else
            {
                if (endDate.compareTo(acctDayEndDate) > 0)// 资料结束时间大于本账期结束时间
                {
                    tempEndDate = SysDateMgr.getLastDayThisAcct(endDate, bookAcctDay, bookAcctDayFirstDate);

                    acctDayChangeData.setEndDate(tempEndDate + getDateHourMinSec(endDate, "1"));
                }
            }

            acctDayChangeData.setModifyTag("2");
        }

        if (isChgStartDate)// 本次变更了开始时间,则需要终止当前记录，新增一条记录
        {
            IAcctDayChangeData endDateChgData = acctDayChangeData.clone();
            endDateChgData.setStartDate(oldStartDate);// 不改变原来的开始时间，记录真实的轨迹
            endDateChgData.setEndDate(btd.getRD().getAcceptTime());
            endDateChgData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            btd.add(btd.getRD().getUca().getSerialNumber(), (BaseTradeData) endDateChgData);

            // 生成新的，主要是保留原始数据的开始轨迹
            acctDayChangeData.setInstId(SeqMgr.getInstId());
            acctDayChangeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(btd.getRD().getUca().getSerialNumber(), (BaseTradeData) acctDayChangeData);
        }
        else
        {
            btd.add(btd.getRD().getUca().getSerialNumber(), (BaseTradeData) acctDayChangeData);
        }
    }

    /**
     * 因账期变更,计算开始、结束时间(海南优惠时间特殊处理)
     * 
     * @param acctDayChangeData
     * @param acctDay
     * @param acctDayStartDate
     * @param acctDayEndDate
     * @param acctDayFirstDate
     * @param bookAcctDay
     * @param bookAcctDayStartDate
     * @param bookAcctDayEndDate
     * @param bookAcctDayFirstDate
     * @return
     * @throws Exception
     */
    private void countVlidDateForAcctDayChgDiscntSpec(BusiTradeData btd, IAcctDayChangeData acctDayChangeData, String acctDay, String acctDayStartDate, String acctDayEndDate, String acctDayFirstDate, String bookAcctDay, String bookAcctDayStartDate,
            String bookAcctDayEndDate, String bookAcctDayFirstDate) throws Exception
    {
        String tempStartDate = "";
        String tempEndDate = "";

        String startDate = this.formatDate(acctDayChangeData.getStartDate());// 资料开始时间
        String endDate = this.formatDate(acctDayChangeData.getEndDate());// 资料结束时间

        String rsrvDate2 = this.dealRsrvDate(acctDayChangeData.getRsrvDate2());// 资料最初始开始时间,资料如未进行过账期变更,此字段应是空值
        String rsrvDate3 = this.dealRsrvDate(acctDayChangeData.getRsrvDate3());// 资料最初始结束时间,资料如未进行过账期变更,此字段应是空值

        boolean isChgStartDate = false;// 记录资料的开始时间是否被修改
        String oldStartDate = acctDayChangeData.getStartDate();// 记录原始资料的开始时间

        String discntCode = ((DiscntTradeData) acctDayChangeData).getDiscntCode();
        boolean isBackwardTag = elementBackwardTag("8970", discntCode);// 结束时间偏移标识：向前偏移：false,向后偏移：true

        // 开始时间计算
        if ("".equals(rsrvDate2))// RSRV_DATE2为空:资料原始开始是空值
        {
            if (startDate.compareTo(bookAcctDayStartDate) > 0)// 资料开始时间大于下账期开始时间
            {
                tempStartDate = SysDateMgr.getFirstDayNextAcct(startDate, bookAcctDay, bookAcctDayFirstDate);

                acctDayChangeData.setStartDate(tempStartDate + getDateHourMinSec(acctDayChangeData.getStartDate(), "0"));

                isChgStartDate = true;// 记录资料的开始时间被修改
            }

            acctDayChangeData.setModifyTag("2");

            acctDayChangeData.setRsrvDate2(startDate);

        }
        else
        // RSRV_DATE2非空:资料原始开始非空值
        {
            if (startDate.compareTo(bookAcctDayStartDate) > 0)// 处理开始时间,只对【资料开始时间大于下账期开始时间】做处理
            {
                String tempStart = SysDateMgr.getFirstDayNextAcct(startDate, bookAcctDay, bookAcctDayFirstDate);

                if (SysDateMgr.dayInterval(tempStart, rsrvDate2) > 30)// 开始时间日期归属账期的下账期的第一天 - 资料最初始时间 > 30
                // 天时,时间以最初始开始时间RSRV_DATE2为准
                {
                    tempStartDate = SysDateMgr.getFirstDayNextAcct(rsrvDate2, bookAcctDay, bookAcctDayFirstDate);

                    acctDayChangeData.setStartDate(tempStartDate + getDateHourMinSec(acctDayChangeData.getStartDate(), "0"));

                    acctDayChangeData.setModifyTag("2");
                }
                else
                {
                    tempStartDate = SysDateMgr.getFirstDayNextAcct(startDate, bookAcctDay, bookAcctDayFirstDate);

                    acctDayChangeData.setStartDate(tempStartDate + getDateHourMinSec(acctDayChangeData.getStartDate(), "0"));

                    acctDayChangeData.setModifyTag("2");
                }
            }
        }
        // 结束时间计算
        if ("".equals(rsrvDate3))// RSRV_DATE3为空:资料原始结束时间是空值
        {
            if (endDate.compareTo(acctDayEndDate) > 0)// 资料结束时间大于本账期结束时间
            {
                tempEndDate = SysDateMgr.getLastDayThisAcct(endDate, bookAcctDay, bookAcctDayFirstDate);

                if (!isBackwardTag)// 需向前偏移一个月
                {
                    tempEndDate = SysDateMgr.getAddMonthsNowday(-1, tempEndDate);
                }

                acctDayChangeData.setEndDate(tempEndDate + getDateHourMinSec(endDate, "1"));
            }
            else
            // 资料结束时间小于本账期结束时间,取原有结束时间
            {
                acctDayChangeData.setEndDate(acctDayChangeData.getEndDate());
            }

            acctDayChangeData.setModifyTag("2");

            acctDayChangeData.setRsrvDate3(endDate);// 记录资料最原始结束时间到RSRV_DATE3
        }
        else
        // RSRV_DATE3非空:资料原始结束时间非空值
        {
            String tempEnd = SysDateMgr.getLastDayThisAcct(endDate, bookAcctDay, bookAcctDayFirstDate);

            if (SysDateMgr.dayInterval(tempEnd, rsrvDate3) > 30)// 结束时间日期归属账期的最后一天 - 资料最初始结束时间 > 30
            // 天时,时间以最初始结束时间RSRV_DATE3为准
            {
                tempEndDate = SysDateMgr.getLastDayThisAcct(rsrvDate3, bookAcctDay, bookAcctDayFirstDate);

                acctDayChangeData.setEndDate(tempEndDate + getDateHourMinSec(endDate, "1"));
            }
            else
            {
                if (endDate.compareTo(acctDayEndDate) > 0)// 资料结束时间大于本账期结束时间
                {
                    tempEndDate = SysDateMgr.getLastDayThisAcct(endDate, bookAcctDay, bookAcctDayFirstDate);

                    if (!isBackwardTag)// 需向前偏移一个月
                    {
                        tempEndDate = SysDateMgr.getAddMonthsNowday(-1, tempEndDate);
                    }

                    acctDayChangeData.setEndDate(tempEndDate + getDateHourMinSec(endDate, "1"));
                }
            }

            acctDayChangeData.setModifyTag("2");
        }

        if (isChgStartDate)// 本次变更了开始时间,则需要终止当前记录，新增一条记录
        {
            IAcctDayChangeData endDateChgData = acctDayChangeData.clone();
            endDateChgData.setStartDate(oldStartDate);// 不改变原来的开始时间，记录真实的轨迹
            endDateChgData.setEndDate(btd.getRD().getAcceptTime());
            endDateChgData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            btd.add(btd.getRD().getUca().getSerialNumber(), (BaseTradeData) endDateChgData);

            // 生成新的，主要是保留原始数据的开始轨迹
            acctDayChangeData.setInstId(SeqMgr.getInstId());
            acctDayChangeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(btd.getRD().getUca().getSerialNumber(), (BaseTradeData) acctDayChangeData);
        }
        else
        {
            btd.add(btd.getRD().getUca().getSerialNumber(), (BaseTradeData) acctDayChangeData);
        }
    }

    /**
     * 因账期变更,计算开始、结束时间(海南专用,针对海南营销活动特殊处理)
     * 
     * @param acctDayChangeData
     * @param acctDay
     * @param acctDayStartDate
     * @param acctDayEndDate
     * @param acctDayFirstDate
     * @param bookAcctDay
     * @param bookAcctDayStartDate
     * @param bookAcctDayEndDate
     * @param bookAcctDayFirstDate
     * @return
     * @throws Exception
     */
    private void countVlidDateForAcctDayChgSaleActiveSpec(BusiTradeData btd, SaleActiveTradeData saleActiveTradeData, String acctDay, String acctDayStartDate, String acctDayEndDate, String acctDayFirstDate, String bookAcctDay,
            String bookAcctDayStartDate, String bookAcctDayEndDate, String bookAcctDayFirstDate) throws Exception
    {
        String tempStartDate = "";
        String tempEndDate = "";

        String startDate = this.formatDate(saleActiveTradeData.getStartDate());// 资料开始时间
        String endDate = this.formatDate(saleActiveTradeData.getEndDate());// 资料结束时间

        String initStartDate = this.dealRsrvDate(saleActiveTradeData.getRsrvStr12());// 资料最初始开始时间,资料如未进行过账期变更,此字段应是空值
        String initEndDate = this.dealRsrvDate(saleActiveTradeData.getRsrvStr13());// 资料最初始结束时间,资料如未进行过账期变更,此字段应是空值

        boolean isChgStartDate = false;// 记录资料的开始时间是否被修改
        String oldStartDate = saleActiveTradeData.getStartDate();// 记录原始资料的开始时间

        String packageId = saleActiveTradeData.getPackageId();
        boolean isBackwardTag = elementBackwardTag("8971", packageId);// 结束时间偏移标识：向前偏移：false,向后偏移：true

        // 开始时间计算
        if ("".equals(initStartDate))// initStartDate为空:资料原始开始是空值
        {
            if (startDate.compareTo(bookAcctDayStartDate) > 0)// 资料开始时间大于下账期开始时间
            {
                tempStartDate = SysDateMgr.getFirstDayNextAcct(startDate, bookAcctDay, bookAcctDayFirstDate);

                saleActiveTradeData.setStartDate(tempStartDate + getDateHourMinSec(saleActiveTradeData.getStartDate(), "0"));

                if (!isBackwardTag)// 需向前偏移一个月
                {
                    saleActiveTradeData.setStartDate(SysDateMgr.getAddMonthsNowday(-1, saleActiveTradeData.getStartDate()));
                }

                isChgStartDate = true;// 记录资料的开始时间被修改
            }

            saleActiveTradeData.setModifyTag("2");

            saleActiveTradeData.setRsrvStr12(startDate);

        }
        else
        // initStartDate非空:资料原始开始非空值
        {
            if (startDate.compareTo(bookAcctDayStartDate) > 0)// 处理开始时间,只对【资料开始时间大于下账期开始时间】做处理
            {
                String tempStart = SysDateMgr.getFirstDayNextAcct(startDate, bookAcctDay, bookAcctDayFirstDate);

                if (SysDateMgr.dayInterval(tempStart, initStartDate) > 30)// 开始时间日期归属账期的下账期的第一天 - 资料最初始时间 > 30
                // 天时,时间以最初始开始时间RSRV_DATE2为准
                {
                    tempStartDate = SysDateMgr.getFirstDayNextAcct(initStartDate, bookAcctDay, bookAcctDayFirstDate);

                    saleActiveTradeData.setStartDate(tempStartDate + getDateHourMinSec(saleActiveTradeData.getStartDate(), "0"));

                    saleActiveTradeData.setModifyTag("2");
                }
                else
                {
                    tempStartDate = SysDateMgr.getFirstDayNextAcct(startDate, bookAcctDay, bookAcctDayFirstDate);

                    saleActiveTradeData.setStartDate(tempStartDate + getDateHourMinSec(saleActiveTradeData.getStartDate(), "0"));

                    if (!isBackwardTag)// 需向前偏移一个月
                    {
                        saleActiveTradeData.setStartDate(SysDateMgr.getAddMonthsNowday(-1, saleActiveTradeData.getStartDate()));
                    }

                    saleActiveTradeData.setModifyTag("2");
                }
            }
        }
        // 结束时间计算
        if ("".equals(initEndDate))// initEndDate为空:资料原始结束时间是空值
        {
            if (endDate.compareTo(acctDayEndDate) > 0)// 资料结束时间大于本账期结束时间
            {
                tempEndDate = SysDateMgr.getLastDayThisAcct(endDate, bookAcctDay, bookAcctDayFirstDate);

                if (!isBackwardTag)// 需向前偏移一个月
                {
                    tempEndDate = SysDateMgr.getAddMonthsNowday(-1, tempEndDate);
                }

                saleActiveTradeData.setEndDate(tempEndDate + getDateHourMinSec(endDate, "1"));
            }
            else
            // 资料结束时间小于本账期结束时间,取原有结束时间
            {
                saleActiveTradeData.setEndDate(saleActiveTradeData.getEndDate());
            }

            saleActiveTradeData.setModifyTag("2");

            saleActiveTradeData.setRsrvStr13(endDate);// 记录资料最原始结束时间到RSRV_DATE3
        }
        else
        // initEndDate非空:资料原始结束时间非空值
        {
            String tempEnd = SysDateMgr.getLastDayThisAcct(endDate, bookAcctDay, bookAcctDayFirstDate);

            if (SysDateMgr.dayInterval(tempEnd, initEndDate) > 30)// 结束时间日期归属账期的最后一天 - 资料最初始结束时间 > 30
            // 天时,时间以最初始结束时间RSRV_DATE3为准
            {
                tempEndDate = SysDateMgr.getLastDayThisAcct(initEndDate, bookAcctDay, bookAcctDayFirstDate);

                saleActiveTradeData.setEndDate(tempEndDate + getDateHourMinSec(endDate, "1"));
            }
            else
            {
                if (endDate.compareTo(acctDayEndDate) > 0)// 资料结束时间大于本账期结束时间
                {
                    tempEndDate = SysDateMgr.getLastDayThisAcct(endDate, bookAcctDay, bookAcctDayFirstDate);

                    if (!isBackwardTag)// 需向前偏移一个月
                    {
                        tempEndDate = SysDateMgr.getAddMonthsNowday(-1, tempEndDate);
                    }

                    saleActiveTradeData.setEndDate(tempEndDate + getDateHourMinSec(endDate, "1"));
                }
            }

            saleActiveTradeData.setModifyTag("2");
        }

        if (isChgStartDate)// 本次变更了开始时间,则需要终止当前记录，新增一条记录
        {
            SaleActiveTradeData endDateChgData = saleActiveTradeData.clone();
            endDateChgData.setStartDate(oldStartDate);// 不改变原来的开始时间，记录真实的轨迹
            endDateChgData.setEndDate(btd.getRD().getAcceptTime());
            endDateChgData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            btd.add(btd.getRD().getUca().getSerialNumber(), endDateChgData);

            // 生成新的，主要是保留原始数据的开始轨迹
            saleActiveTradeData.setInstId(SeqMgr.getInstId());
            saleActiveTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            btd.add(btd.getRD().getUca().getSerialNumber(), saleActiveTradeData);
        }
        else
        {
            btd.add(btd.getRD().getUca().getSerialNumber(), saleActiveTradeData);
        }
    }

    /**
     * 处理预留时间字段
     * 
     * @param rsrvDate
     * @return
     * @throws Exception
     */
    private String dealRsrvDate(String rsrvDate) throws Exception
    {
        if (StringUtils.isBlank(rsrvDate))
        {
            return "";
        }
        else
        {
            return formatDate(rsrvDate);
        }
    }

    /**
     * 处理单个用户的账期日变更资料修改
     */
    private IDataset dealUserAcctDayChg(String needChgUserId, String acctDay, BusiTradeData btd) throws Exception
    {
        IData input = new DataMap();
        input.put("USER_ID", needChgUserId);
        input.put("ACCT_DAY", acctDay);
        if (StringUtils.equals("2012", btd.getTradeTypeCode()))
        {
            input.put("CHG_TYPE", "0");// 0：主动变更,1：被动变更
        }
        else
        {
            input.put("CHG_TYPE", "1");// 0：主动变更,1：被动变更
        }

        IDataset newUserAcctDayDataset = new ChangeAcctDaySVC().getNewAcctDayByModify(input);

        this.stringTableTradeUserAcctDay(btd, newUserAcctDayDataset);// 处理用户账期台账数据

        this.geneUserAllInfosTrade(btd, newUserAcctDayDataset);

        return newUserAcctDayDataset;
    }

    /**
     * 获取元素结束时间偏移标识：向前偏移：false,向后偏移：true
     * 
     * @param discntCode
     * @return
     * @throws Exception
     */
    private boolean elementBackwardTag(String paramAttr, String paramCode) throws Exception
    {
        IDataset discntInfos = CommparaInfoQry.getCommparaAllColByParser("CSM", paramAttr, paramCode, "0898");
        if (IDataUtil.isNotEmpty(discntInfos))
        {
            return true;
        }
        return false;
    }

    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<AcctDayData> userAcctDayChangeList = btd.getUserAcctDayChangeList();// 得到需要变更的数据,可能存在多条

        // 原则：遍历userAcctDayChangeList中所有需要变更USER_ID数据，
        // 1、needSyncSameAcctUser=true 根据此数据捞取默认ACCT_ID，再根据此ACCT_ID捞取其下所有USER_ID，统一变更
        // 2、needSyncSameAcctUser=false 只修改当前用户的资料
        if (!userAcctDayChangeList.isEmpty())
        {
            for (int i = 0, isize = userAcctDayChangeList.size(); i < isize; i++)
            {
                String userId = userAcctDayChangeList.get(i).getId();
                String acctDay = userAcctDayChangeList.get(i).getAcctDay();
                boolean needSyncSameAcctUser = userAcctDayChangeList.get(i).isNeedSyncSameAccountUser();// 是否需要同时修改同账户下的用户

                if (needSyncSameAcctUser)// 需要同步修改同账户下的用户
                {
                    // 查询USER_ID默认对应的ACCT_ID
                    IData defaultPayRela = UcaInfoQry.qryDefaultPayRelaByUserId(userId);

                    if (IDataUtil.isEmpty(defaultPayRela))
                    {
                        CSAppException.apperr(CrmUserException.CRM_USER_850, userId);
                    }

                    String acctId = defaultPayRela.getString("ACCT_ID");

                    // 查询此ACCT_ID下所有默认付费关系用户
                    IDataset allPayRelation = PayRelaInfoQry.getAllValidUserPayByAcctId(acctId);

                    IDataset newUserAcctDayDataset = new DatasetList();

                    for (int j = 0, jsize = allPayRelation.size(); j < jsize; j++)
                    {
                        newUserAcctDayDataset.clear();
                        // 处理单个用户账户结账日变更导致的资料变更
                        newUserAcctDayDataset = dealUserAcctDayChg(allPayRelation.getData(j).getString("USER_ID"), acctDay, btd);
                    }

                    List<AccountAcctDayTradeData> accountAcctDayTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_ACCOUNT_ACCTDAY);
                    if (accountAcctDayTradeData != null && accountAcctDayTradeData.size() > 0)
                    {
                        boolean isExists = false;
                        for (int k = 0, ksize = accountAcctDayTradeData.size(); k < ksize; k++)
                        {
                            // 检查账户账期台账是否已经存在此ACCT_ID的数据,如存在,此ACCT_ID不再做变更操作以免重复变更
                            if (accountAcctDayTradeData.get(k).getAcctId().equals(acctId))
                            {
                                isExists = true;
                                break;
                            }
                            /*
                             * else { this.stringTableTradeAccountAcctDay(btd, newUserAcctDayDataset, acctId);//
                             * 处理账户账期台账数据 }
                             */
                        }
                        if (!isExists)
                        {
                            this.stringTableTradeAccountAcctDay(btd, newUserAcctDayDataset, acctId);// 处理账户账期台账数据
                        }

                    }
                    else
                    {
                        this.stringTableTradeAccountAcctDay(btd, newUserAcctDayDataset, acctId);// 处理账户账期台账数据
                    }
                }
                else
                {
                    // 单个用户资料变更
                    dealUserAcctDayChg(userId, acctDay, btd);
                }
            }
        }
    }

    /**
     * 返回标准时间格式（YYYY-MM-DD）
     * 
     * @param strDate
     *            用户账期信息
     * @throws Exception
     */
    private String formatDate(String strDate) throws Exception
    {
        return java.sql.Date.valueOf(strDate.split(" ")[0]).toString();
    }

    /**
     * 拼用户因账期变更导致产品、资费、服务、活动有效期变更信息台账
     * 
     * @param btd
     * @param userAcctDayData
     *            用户新的账期信息
     * @throws Exception
     */
    public void geneUserAllInfosTrade(BusiTradeData btd, IDataset userAcctDayData) throws Exception
    {
        String acctDay = "";// 用户当前账期日
        String acctDayStartDate = "";// 用户当前账期开始时间
        String acctDayEndDate = "";// 用户当前账期结束时间
        String acctDayFirstDate = "";// 用户当前账期首次结账日时间
        String bookAcctDay = "";// 用户预约账期日
        String bookAcctDayStartDate = "";// 用户预约账期开始时间
        String bookAcctDayEndDate = "";// 用户预约账期结束时间
        String bookAcctDayFirstDate = "";// 用户预约账期首次结账日时间

        if (userAcctDayData.size() > 1)
        {
            DataHelper.sort(userAcctDayData, "START_DATE", IDataset.ORDER_ASCEND);

            acctDay = userAcctDayData.getData(0).getString("ACCT_DAY");

            acctDayStartDate = this.formatDate(userAcctDayData.getData(0).getString("START_DATE"));

            acctDayEndDate = this.formatDate(userAcctDayData.getData(0).getString("END_DATE"));

            acctDayFirstDate = this.formatDate(userAcctDayData.getData(0).getString("FIRST_DATE"));

            bookAcctDay = userAcctDayData.getData(1).getString("ACCT_DAY");

            bookAcctDayStartDate = this.formatDate(userAcctDayData.getData(1).getString("START_DATE"));

            bookAcctDayEndDate = this.formatDate(userAcctDayData.getData(1).getString("END_DATE"));

            bookAcctDayFirstDate = this.formatDate(userAcctDayData.getData(1).getString("FIRST_DATE"));
        }

        String broderDate = this.getBroderDate();// 获取判定资料是否无限期有效临界日期

        String userAcctDayEndDate = acctDayEndDate + SysDateMgr.END_DATE;// 用户当前账期最后结束时间

        List<IAcctDayChangeData> userNeedChangeDatas = btd.getRD().getUca().getUserAcctDayNeedChangeData();// 得到需要变更的账期数据,已过滤掉本身业务中修改的数据

        if (userNeedChangeDatas != null && userNeedChangeDatas.size() > 0)
        {
            for (int i = 0, isize = userNeedChangeDatas.size(); i < isize; i++)
            {
                IAcctDayChangeData temAcctDayChgData = userNeedChangeDatas.get(i);
                String tempEndDate = temAcctDayChgData.getEndDate();
                String endDate = this.formatDate(tempEndDate) + getDateHourMinSec(tempEndDate, "1");// 获取END_DATE

                if (endDate.compareTo(broderDate) <= 0 && endDate.compareTo(userAcctDayEndDate) > 0)// END_DATE在临界值时间和小于等于当前账期结束时间才做修改
                {
                    // 优惠特殊处理
                    if (StringUtils.equals("TF_B_TRADE_DISCNT", temAcctDayChgData.getTableName()))
                    {
                        countVlidDateForAcctDayChgDiscntSpec(btd, temAcctDayChgData, acctDay, acctDayStartDate, acctDayEndDate, acctDayFirstDate, bookAcctDay, bookAcctDayStartDate, bookAcctDayEndDate, bookAcctDayFirstDate);
                    }
                    else if (StringUtils.equals("TF_B_TRADE_SALE_ACTIVE", temAcctDayChgData.getTableName()))// 营销活动特殊处理
                    {
                        SaleActiveTradeData saleActiveTradeData = (SaleActiveTradeData) temAcctDayChgData;// 转换
                        countVlidDateForAcctDayChgSaleActiveSpec(btd, saleActiveTradeData, acctDay, acctDayStartDate, acctDayEndDate, acctDayFirstDate, bookAcctDay, bookAcctDayStartDate, bookAcctDayEndDate, bookAcctDayFirstDate);
                    }
                    else
                    {
                        countVlidDateForAcctDayChg(btd, temAcctDayChgData, acctDay, acctDayStartDate, acctDayEndDate, acctDayFirstDate, bookAcctDay, bookAcctDayStartDate, bookAcctDayEndDate, bookAcctDayFirstDate);
                    }
                }
            }
        }
        else
        {
            return;
        }
    }

    /**
     * 判定资料是否无限期有效临界日期点 大于【td_s_static中type_id='BORDER_DATE'】时间视为无限期有效
     * 
     * @return
     * @throws Exception
     */
    private String getBroderDate() throws Exception
    {
        String borderDate = "";

        borderDate = StaticUtil.getStaticValue(null, "TD_S_STATIC", "TYPE_ID", "DATA_ID", "BORDER_DATE");

        if (StringUtils.isBlank(borderDate))
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_21);
        }

        return borderDate;
    }

    /**
     * 根据传入时间获取相应时间的时分秒
     * 
     * @param strDate
     * @param dateFlag
     *            0:开始时间,1:结束时间
     * @return String
     * @throws Exception
     */
    private String getDateHourMinSec(String strDate, String dateFlag) throws Exception
    {
        int i = strDate.lastIndexOf(' ');

        if (i != -1)
        {
            return strDate.substring(i);
        }
        else
        {
            if (StringUtils.equals("0", dateFlag))
            {
                return SysDateMgr.START_DATE_FOREVER;
            }
            else
            {
                return SysDateMgr.END_DATE;
            }
        }
    }

    /**
     * 设置账户结账日台帐表的数据
     * 
     * @param btd
     * @param newUserAcctDayDataset
     * @param acctId
     * @throws Exception
     */
    private void stringTableTradeAccountAcctDay(BusiTradeData btd, IDataset newUserAcctDayDataset, String acctId) throws Exception
    {
        // 查询帐户帐期表信息
        IData accountAcctDay = UserAcctDayInfoQry.getAccountValidAcctDay(acctId);

        if (accountAcctDay == null)
        {
            CSAppException.apperr(AcctDayException.CRM_ACCTDAY_22);
        }

        String instId = accountAcctDay.getString("INST_ID");

        for (int i = 0, isize = newUserAcctDayDataset.size(); i < isize; i++)
        {
            AccountAcctDayTradeData accountAcctDayTradeData = new AccountAcctDayTradeData(newUserAcctDayDataset.getData(i));
            accountAcctDayTradeData.setAcctId(acctId);

            /*
             * accountAcctDayTradeData.setAcctDay(newUserAcctDayDataset.getData(i).getString("ACCT_DAY"));
             * accountAcctDayTradeData.setAcctId(acctId);
             * accountAcctDayTradeData.setChgDate(newUserAcctDayDataset.getData(i).getString("CHG_DATE"));
             * accountAcctDayTradeData.setChgMode(newUserAcctDayDataset.getData(i).getString("CHG_MODE"));
             * accountAcctDayTradeData.setChgType(newUserAcctDayDataset.getData(i).getString("CHG_TYPE"));
             * accountAcctDayTradeData.setEndDate(newUserAcctDayDataset.getData(i).getString("END_DATE"));
             * accountAcctDayTradeData.setFirstDate(newUserAcctDayDataset.getData(i).getString("FIRST_DATE"));
             * accountAcctDayTradeData.setModifyTag(newUserAcctDayDataset.getData(i).getString("MODIFY_TAG"));
             * accountAcctDayTradeData.setRemark(newUserAcctDayDataset.getData(i).getString("REMARK"));
             * accountAcctDayTradeData.setRsrvDate1(newUserAcctDayDataset.getData(i).getString("RSRV_DATE1"));
             * accountAcctDayTradeData.setRsrvDate2(newUserAcctDayDataset.getData(i).getString("RSRV_DATE2"));
             * accountAcctDayTradeData.setRsrvDate3(newUserAcctDayDataset.getData(i).getString("RSRV_DATE3"));
             * accountAcctDayTradeData.setRsrvNum1(newUserAcctDayDataset.getData(i).getString("RSRV_NUM1"));
             * accountAcctDayTradeData.setRsrvNum2(newUserAcctDayDataset.getData(i).getString("RSRV_NUM2"));
             * accountAcctDayTradeData.setRsrvNum3(newUserAcctDayDataset.getData(i).getString("RSRV_NUM3"));
             * accountAcctDayTradeData.setRsrvNum4(newUserAcctDayDataset.getData(i).getString("RSRV_NUM4"));
             * accountAcctDayTradeData.setRsrvNum5(newUserAcctDayDataset.getData(i).getString("RSRV_NUM5"));
             * accountAcctDayTradeData.setRsrvStr1(newUserAcctDayDataset.getData(i).getString("RSRV_STR1"));
             * accountAcctDayTradeData.setRsrvStr2(newUserAcctDayDataset.getData(i).getString("RSRV_STR2"));
             * accountAcctDayTradeData.setRsrvStr3(newUserAcctDayDataset.getData(i).getString("RSRV_STR3"));
             * accountAcctDayTradeData.setRsrvStr4(newUserAcctDayDataset.getData(i).getString("RSRV_STR4"));
             * accountAcctDayTradeData.setRsrvStr5(newUserAcctDayDataset.getData(i).getString("RSRV_STR5"));
             * accountAcctDayTradeData.setRsrvTag1(newUserAcctDayDataset.getData(i).getString("RSRV_TAG1"));
             * accountAcctDayTradeData.setRsrvTag2(newUserAcctDayDataset.getData(i).getString("RSRV_TAG2"));
             * accountAcctDayTradeData.setRsrvTag3(newUserAcctDayDataset.getData(i).getString("RSRV_TAG3"));
             * accountAcctDayTradeData.setStartDate(newUserAcctDayDataset.getData(i).getString("START_DATE"));
             */

            if (StringUtils.equals(BofConst.MODIFY_TAG_ADD, accountAcctDayTradeData.getModifyTag()))
            {
                accountAcctDayTradeData.setInstId(SeqMgr.getInstId());
            }
            else
            {
                accountAcctDayTradeData.setInstId(instId);
            }
            btd.add(btd.getRD().getUca().getSerialNumber(), accountAcctDayTradeData);
        }
    }

    /**
     * 设置用户结账日台帐表的数据
     * 
     * @param btd
     * @param newUserAcctDayDataset
     * @throws Exception
     */
    private void stringTableTradeUserAcctDay(BusiTradeData btd, IDataset newUserAcctDayDataset) throws Exception
    {
        for (int i = 0, isize = newUserAcctDayDataset.size(); i < isize; i++)
        {
            UserAcctDayTradeData userAcctDayTradeData = new UserAcctDayTradeData(newUserAcctDayDataset.getData(i));

            /*
             * userAcctDayTradeData.setAcctDay(newUserAcctDayDataset.getData(i).getString("ACCT_DAY"));
             * userAcctDayTradeData.setChgDate(newUserAcctDayDataset.getData(i).getString("CHG_DATE"));
             * userAcctDayTradeData.setChgMode(newUserAcctDayDataset.getData(i).getString("CHG_MODE"));
             * userAcctDayTradeData.setChgType(newUserAcctDayDataset.getData(i).getString("CHG_TYPE"));
             * userAcctDayTradeData.setEndDate(newUserAcctDayDataset.getData(i).getString("END_DATE"));
             * userAcctDayTradeData.setFirstDate(newUserAcctDayDataset.getData(i).getString("FIRST_DATE"));
             * userAcctDayTradeData.setInstId(newUserAcctDayDataset.getData(i).getString("INST_ID"));
             * userAcctDayTradeData.setModifyTag(newUserAcctDayDataset.getData(i).getString("MODIFY_TAG"));
             * userAcctDayTradeData.setRemark(newUserAcctDayDataset.getData(i).getString("REMARK"));
             * userAcctDayTradeData.setRsrvDate1(newUserAcctDayDataset.getData(i).getString("RSRV_DATE1"));
             * userAcctDayTradeData.setRsrvDate2(newUserAcctDayDataset.getData(i).getString("RSRV_DATE2"));
             * userAcctDayTradeData.setRsrvDate3(newUserAcctDayDataset.getData(i).getString("RSRV_DATE3"));
             * userAcctDayTradeData.setRsrvNum1(newUserAcctDayDataset.getData(i).getString("RSRV_NUM1"));
             * userAcctDayTradeData.setRsrvNum2(newUserAcctDayDataset.getData(i).getString("RSRV_NUM2"));
             * userAcctDayTradeData.setRsrvNum3(newUserAcctDayDataset.getData(i).getString("RSRV_NUM3"));
             * userAcctDayTradeData.setRsrvNum4(newUserAcctDayDataset.getData(i).getString("RSRV_NUM4"));
             * userAcctDayTradeData.setRsrvNum5(newUserAcctDayDataset.getData(i).getString("RSRV_NUM5"));
             * userAcctDayTradeData.setRsrvStr1(newUserAcctDayDataset.getData(i).getString("RSRV_STR1"));
             * userAcctDayTradeData.setRsrvStr2(newUserAcctDayDataset.getData(i).getString("RSRV_STR2"));
             * userAcctDayTradeData.setRsrvStr3(newUserAcctDayDataset.getData(i).getString("RSRV_STR3"));
             * userAcctDayTradeData.setRsrvStr4(newUserAcctDayDataset.getData(i).getString("RSRV_STR4"));
             * userAcctDayTradeData.setRsrvStr5(newUserAcctDayDataset.getData(i).getString("RSRV_STR5"));
             * userAcctDayTradeData.setRsrvTag1(newUserAcctDayDataset.getData(i).getString("RSRV_TAG1"));
             * userAcctDayTradeData.setRsrvTag2(newUserAcctDayDataset.getData(i).getString("RSRV_TAG2"));
             * userAcctDayTradeData.setRsrvTag3(newUserAcctDayDataset.getData(i).getString("RSRV_TAG3"));
             * userAcctDayTradeData.setStartDate(newUserAcctDayDataset.getData(i).getString("START_DATE"));
             * userAcctDayTradeData.setUserId(newUserAcctDayDataset.getData(i).getString("USER_ID"));
             */

            btd.add(btd.getRD().getUca().getSerialNumber(), userAcctDayTradeData);
        }
    }
}
