package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.trade;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.BenefitCenterBean;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.requestdata.BenefitUseReqData;

import java.sql.Timestamp;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/10 9:50
 */
public class BenefitUseTrade extends BaseTrade implements ITrade {
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception{
        BenefitUseReqData rd = (BenefitUseReqData)btd.getRD();
        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRsrvStr1(rd.getRightId());
        mainTradeData.setRsrvStr2(rd.getDiscntCode());
        mainTradeData.setRsrvStr3(rd.getRelId());
        mainTradeData.setRsrvStr4(rd.getModifyTag());
        mainTradeData.setRsrvStr5(rd.getStartDate());
        mainTradeData.setRsrvStr6(rd.getEndDate());
       //构建userOtherTrade
        addOtherTrade(btd);
    }

    /**
     * 构建OtherTrade记录权益使用记录
     * @param btd
     * @throws Exception
     */
    public void addOtherTrade(BusiTradeData btd) throws Exception{
        BenefitUseReqData rd = (BenefitUseReqData)btd.getRD();

        if(BofConst.MODIFY_TAG_ADD.equals(rd.getModifyTag())){
            //入场通知,需修改车牌标识RSRV_STR8标识已入场
            //根据userId,relId找对应车牌信息
            IDataset benefitTagInfos = UserOtherInfoQry.getUserOtherInfoByBenefit(rd.getUca().getUserId(), PersonConst.BENEFIT_TAG,
                    rd.getRelId(), rd.getRightId(), rd.getDiscntCode());
            if(IDataUtil.isNotEmpty(benefitTagInfos)){
                IData benefitTagInfo = benefitTagInfos.first();
                OtherTradeData otherTradeData = new OtherTradeData(benefitTagInfo);
                otherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                otherTradeData.setRsrvStr8("1");
                btd.add(rd.getUca().getSerialNumber(), otherTradeData);
            }

        }else if(BofConst.MODIFY_TAG_UPD.equals(rd.getModifyTag())){

            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setUserId(rd.getUca().getUserId());
            otherTradeData.setRsrvValueCode(PersonConst.BENEFIT_RIGHT_USE_RECORD);
            //根据时间配置设置时间
            IDataset freeTimes = CommparaInfoQry.getCommparaAllColByParser("CSM", PersonConst.BENEFIT_STATIC_CONFIG, PersonConst.BENEFIT_STATIC_FREETIME,  "0898");
            long freeTime=0L;
            if(IDataUtil.isNotEmpty(freeTimes)&& StringUtils.isNotBlank(freeTimes.first().getString("PARA_CODE1"))){
                freeTime=freeTimes.first().getLong("PARA_CODE1",0L);
            }
            otherTradeData.setRsrvValue(queryCountRecordNum(rd));
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTradeData.setRsrvStr1(rd.getRightId());
            otherTradeData.setRsrvStr2(rd.getDiscntCode());
            otherTradeData.setRsrvStr3(rd.getRelId());
            IData rightUseInfo = rd.getRightUseInfo();
            otherTradeData.setRsrvStr4(rightUseInfo.getString("tradeId"));
            otherTradeData.setRsrvStr5(rightUseInfo.getString("parkId"));
            otherTradeData.setRsrvStr6(rightUseInfo.getString("parkName"));;
            otherTradeData.setStartDate(rd.getStartDate());
            //使用结束时间RSRV_DATE1,,END_DATE暂时按年底,防止END_DATE失效数据被搬表
            otherTradeData.setEndDate(SysDateMgr.getLastDayOfThisYear());
            otherTradeData.setRsrvDate1(rd.getEndDate());
            btd.add(rd.getUca().getSerialNumber(), otherTradeData);


            //出场通知,需修改车牌标识RSRV_STR8,标识已出场
            //根据userId,relId找对应车牌信息
            IDataset benefitTagInfos = UserOtherInfoQry.getUserOtherInfoByBenefit(rd.getUca().getUserId(), PersonConst.BENEFIT_TAG,
                    rd.getRelId(), rd.getRightId(), rd.getDiscntCode());
            if(IDataUtil.isNotEmpty(benefitTagInfos)){
                IData benefitTagInfo = benefitTagInfos.first();
                OtherTradeData benefitTagOtherTradeData = new OtherTradeData(benefitTagInfo);
                benefitTagOtherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                benefitTagOtherTradeData.setRsrvStr8("0");
                btd.add(rd.getUca().getSerialNumber(), benefitTagOtherTradeData);
            }
        }

    }
    /**
     * 获取免费停车权益当次使用次数
     * REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param rd
     * @return
     * @throws Exception
     */
    public String queryCountRecordNum(BenefitUseReqData rd) throws Exception {
        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        DataMap param = new DataMap();
        param.put("USER_ID",rd.getUca().getUserId());
        param.put("SERIAL_NUMBER", rd.getUca().getSerialNumber());
        param.put("RIGHT_ID", rd.getRightId());
        param.put("DISCNT_CODE", rd.getDiscntCode());
        param.put("START_DATE", rd.getStartDate());
        param.put("END_DATE", rd.getEndDate());

        return  bean.countCurUseNum(param);
    }

}
