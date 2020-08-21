package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.trade;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.requestdata.BenefitAddUseNumReqDate;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2020/1/15 20:15
 */
public class BenefitAddUseNumTrade extends BaseTrade implements ITrade {
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception{
        BenefitAddUseNumReqDate rd=(BenefitAddUseNumReqDate)btd.getRD();
        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRsrvStr1(rd.getRightId());
        mainTradeData.setRsrvStr2(rd.getDiscntCode());
        mainTradeData.setRsrvStr3(rd.getAddUseNum());
        mainTradeData.setRsrvStr4(rd.getModifyTag());
        //空表示批量任务1AEE定时任务补赠2营销活动赠送
        String addUseNumType = rd.getAddUseNumType();
        mainTradeData.setRsrvStr5(addUseNumType);
        //构建userOtherTrade
        if("2".equals(addUseNumType)){
            dealOtherTradeActive(btd);
        }else {
            dealOtherTrade(btd);
        }

    }

    /**
     * 构建OtherTrade
     * @param btd
     * @throws Exception
     */
    public void dealOtherTrade(BusiTradeData btd) throws Exception{
        BenefitAddUseNumReqDate rd=(BenefitAddUseNumReqDate)btd.getRD();
        String modifyTag = rd.getModifyTag();
        if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setUserId(rd.getUca().getUserId());
            otherTradeData.setRsrvValueCode(PersonConst.BENEFIT_RIGHT_NUM_CONFIG);
            otherTradeData.setRsrvValue(rd.getAddUseNum());
            otherTradeData.setRsrvStr1(rd.getRightId());
            otherTradeData.setRsrvStr2(rd.getDiscntCode());
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTradeData.setStartDate(SysDateMgr.getSysTime());
            otherTradeData.setEndDate(SysDateMgr.getLastDayOfThisYear());
            btd.add(rd.getUca().getSerialNumber(), otherTradeData);
        }else if(BofConst.MODIFY_TAG_UPD.equals(modifyTag)){
            IDataset lastOtherInfoCuryears = UserOtherInfoQry.getLastCuryear(rd.getUca().getUserId(), PersonConst.BENEFIT_TAG
                    , null, rd.getRightId(),rd.getDiscntCode());
            if(IDataUtil.isEmpty(lastOtherInfoCuryears)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到绑定权益,请重新增加使用次数");
            }
            IData lastBenefitTag = lastOtherInfoCuryears.first();
            int remainUseNum = lastBenefitTag.getInt("RSRV_STR3");
            lastBenefitTag.put("RSRV_STR3",String.valueOf(Integer.parseInt(rd.getAddUseNum())+remainUseNum));
            //统计下赠送的总次数,在升档补差次需计算首次绑定的次数
            String rsrvNum1 = lastBenefitTag.getString("RSRV_NUM1", "0");
            if(StringUtils.isNotBlank(rsrvNum1)){
                lastBenefitTag.put("RSRV_NUM1",Integer.parseInt(rd.getAddUseNum())+Integer.parseInt(rsrvNum1));
            }else{
                lastBenefitTag.put("RSRV_NUM1",Integer.parseInt(rd.getAddUseNum()));
            }
            lastBenefitTag.put("RSRV_NUM2",Integer.parseInt(rd.getAddUseNum()));
            OtherTradeData otherTradeData = new OtherTradeData(lastBenefitTag);
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(rd.getUca().getSerialNumber(), otherTradeData);
        }
    }

    /**
     * 营销活动构建OtherTrade
     * @param btd
     * @throws Exception
     */
    public void dealOtherTradeActive(BusiTradeData btd) throws Exception{
        BenefitAddUseNumReqDate rd=(BenefitAddUseNumReqDate)btd.getRD();
        IData pageRequestData = rd.getPageRequestData();
        IDataset lastOtherInfoCuryears = UserOtherInfoQry.getLastCuryear(rd.getUca().getUserId(), PersonConst.BENEFIT_TAG
                , null, rd.getRightId(),rd.getDiscntCode());
        if(IDataUtil.isEmpty(lastOtherInfoCuryears)){
            //未绑定车牌
            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setUserId(rd.getUca().getUserId());
            otherTradeData.setRsrvValueCode(PersonConst.BENEFIT_RIGHT_NUM_CONFIG);
            otherTradeData.setRsrvValue(rd.getAddUseNum());
            otherTradeData.setRsrvStr1(rd.getRightId());
            otherTradeData.setRsrvStr2(rd.getDiscntCode());
            otherTradeData.setRsrvStr3(pageRequestData.getString("PRODUCT_ID"));
            otherTradeData.setRsrvStr4(pageRequestData.getString("PACKAGE_ID"));
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            if(StringUtils.isNotBlank(pageRequestData.getString("START_DATE"))){
                otherTradeData.setStartDate(pageRequestData.getString("START_DATE"));
            }else{
                otherTradeData.setStartDate(SysDateMgr.getSysTime());
            }
            if(StringUtils.isNotBlank(pageRequestData.getString("END_DATE"))){
                otherTradeData.setEndDate(pageRequestData.getString("END_DATE"));
            }else{
                otherTradeData.setEndDate(SysDateMgr.getLastDayOfThisYear());
            }
            btd.add(rd.getUca().getSerialNumber(), otherTradeData);

            MainTradeData mainTradeData = btd.getMainTradeData();
            mainTradeData.setRsrvStr4("0");
        }else{
            IData lastBenefitTag = lastOtherInfoCuryears.first();
            int remainUseNum = lastBenefitTag.getInt("RSRV_STR3");
            lastBenefitTag.put("RSRV_STR3",String.valueOf(Integer.parseInt(rd.getAddUseNum())+remainUseNum));
            //统计下赠送的总次数,在升档补差次需计算首次绑定的次数
            String rsrvNum1 = lastBenefitTag.getString("RSRV_NUM1", "0");
            if(StringUtils.isNotBlank(rsrvNum1)){
                lastBenefitTag.put("RSRV_NUM1",Integer.parseInt(rd.getAddUseNum())+Integer.parseInt(rsrvNum1));
            }else{
                lastBenefitTag.put("RSRV_NUM1",Integer.parseInt(rd.getAddUseNum()));
            }
            lastBenefitTag.put("RSRV_NUM2",Integer.parseInt(rd.getAddUseNum()));
            OtherTradeData otherTradeUpdateData = new OtherTradeData(lastBenefitTag);
            otherTradeUpdateData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            btd.add(rd.getUca().getSerialNumber(), otherTradeUpdateData);


            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setUserId(rd.getUca().getUserId());
            otherTradeData.setRsrvValueCode(PersonConst.BENEFIT_RIGHT_NUM_CONFIG);
            otherTradeData.setRsrvValue(rd.getAddUseNum());
            otherTradeData.setRsrvStr1(rd.getRightId());
            otherTradeData.setRsrvStr2(rd.getDiscntCode());
            otherTradeData.setRsrvStr3(pageRequestData.getString("PRODUCT_ID"));
            otherTradeData.setRsrvStr4(pageRequestData.getString("PACKAGE_ID"));
            //仅记录该笔赠送记录,1表示查赠送记录时直接累加进用户总次数
            otherTradeData.setRsrvNum1("1");
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            if(StringUtils.isNotBlank(pageRequestData.getString("START_DATE"))){
                otherTradeData.setStartDate(pageRequestData.getString("START_DATE"));
            }else{
                otherTradeData.setStartDate(SysDateMgr.getSysTime());
            }
            if(StringUtils.isNotBlank(pageRequestData.getString("END_DATE"))){
                otherTradeData.setEndDate(pageRequestData.getString("END_DATE"));
            }else{
                otherTradeData.setEndDate(SysDateMgr.getLastDayOfThisYear());
            }
            btd.add(rd.getUca().getSerialNumber(), otherTradeData);

            MainTradeData mainTradeData = btd.getMainTradeData();
            //BenefitAddUseNumSynAction需要改值校验是否同步机场
            mainTradeData.setRsrvStr4("2");
        }
    }
}
