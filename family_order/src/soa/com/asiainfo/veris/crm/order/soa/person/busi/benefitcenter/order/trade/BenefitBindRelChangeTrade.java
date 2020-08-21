package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.trade;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.BenefitCenterBean;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.requestdata.BenefitBindRelReqData;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/11 10:22
 */
public class BenefitBindRelChangeTrade extends BaseTrade implements ITrade {
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception{
        BenefitBindRelReqData rd = (BenefitBindRelReqData)btd.getRD();
        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRsrvStr1(rd.getRightId());
        mainTradeData.setRsrvStr2(rd.getDiscntCode());
        mainTradeData.setRsrvStr4(rd.getModifyTag());
        if(BofConst.MODIFY_TAG_ADD.equals(rd.getModifyTag())){
            mainTradeData.setRsrvStr3(rd.getRelId());
        }else if(BofConst.MODIFY_TAG_DEL.equals(rd.getModifyTag())){
            mainTradeData.setRsrvStr5(rd.getRelId());
        }else{
            mainTradeData.setRsrvStr3(rd.getNewRelId());
            mainTradeData.setRsrvStr5(rd.getRelId());
        }
        //构建userOtherTrade
        dealOtherTrade(btd);
    }
    /**
     * 构建OtherTrade
     * @param btd
     * @throws Exception
     */
    public void dealOtherTrade(BusiTradeData btd) throws Exception{
        BenefitBindRelReqData rd = (BenefitBindRelReqData)btd.getRD();
        String modifyTag = rd.getModifyTag();
        if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setUserId(rd.getUca().getUserId());
            otherTradeData.setRsrvValueCode(PersonConst.BENEFIT_TAG);
            otherTradeData.setRsrvValue(rd.getRelId());
            otherTradeData.setRsrvStr1(rd.getRightId());
            otherTradeData.setRsrvStr2(rd.getDiscntCode());
            otherTradeData.setRsrvStr3(this.queryRemainRecordNum(rd));
            otherTradeData.setRsrvStr8("0");//是否进出场标识1进场 0未进场
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTradeData.setStartDate(rd.getStartDate());
            otherTradeData.setEndDate(rd.getEndDate());
            otherTradeData.setRsrvDate1(rd.getEndDate());
            btd.add(rd.getUca().getSerialNumber(), otherTradeData);
        }else if(BofConst.MODIFY_TAG_UPD.equals(modifyTag)){
            IDataset retOtherInfo = UserOtherInfoQry.getUserOtherInfoByBenefit(btd.getRD().getUca().getUserId()
                    , PersonConst.BENEFIT_TAG,rd.getRelId(),rd.getRightId(),rd.getDiscntCode());
            if (IDataUtil.isEmpty(retOtherInfo)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据权益关联标识未找到记录");
            }
            OtherTradeData otherTradeData = new OtherTradeData(retOtherInfo.first());
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            otherTradeData.setRsrvDate1(SysDateMgr.getSysTime());
            btd.add(rd.getUca().getSerialNumber(), otherTradeData);

            OtherTradeData newOtherTradeData = new OtherTradeData();
            newOtherTradeData.setInstId(SeqMgr.getInstId());
            newOtherTradeData.setUserId(rd.getUca().getUserId());
            newOtherTradeData.setRsrvValueCode(PersonConst.BENEFIT_TAG);
            newOtherTradeData.setRsrvValue(rd.getNewRelId());
            newOtherTradeData.setRsrvStr1(rd.getRightId());
            newOtherTradeData.setRsrvStr2(rd.getDiscntCode());
            newOtherTradeData.setRsrvStr3(this.queryRemainRecordNum(rd));
            newOtherTradeData.setRsrvStr8("0");//是否进出场标识1进场 0未进场
            newOtherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            newOtherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
            newOtherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            newOtherTradeData.setStartDate(rd.getStartDate());
            //截止时截止RSRV_DATE1,失效时间按此字段,防止截止END_DATE数据被搬表
            newOtherTradeData.setEndDate(rd.getEndDate());
            newOtherTradeData.setRsrvDate1(rd.getEndDate());
            btd.add(rd.getUca().getSerialNumber(), newOtherTradeData);
        }else if(BofConst.MODIFY_TAG_DEL.equals(modifyTag)){
            IDataset retOtherInfo = UserOtherInfoQry.getUserOtherInfoByBenefit(btd.getRD().getUca().getUserId()
                    , PersonConst.BENEFIT_TAG,rd.getRelId(),rd.getRightId(),rd.getDiscntCode());
            if (IDataUtil.isEmpty(retOtherInfo)){
                CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据权益关联标识未找到记录");
            }
            OtherTradeData otherTradeData = new OtherTradeData(retOtherInfo.first());
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
            //截止RSRV_DATE1,失效时间按此字段
            otherTradeData.setRsrvDate1(SysDateMgr.getSysTime());
            btd.add(rd.getUca().getSerialNumber(), otherTradeData);
        }

    }

    /**
     * 获取免费停车权益剩余免费停车次数
     * RSRV_STR3记录绑定权益时的权益次数,以后更换权益标识按该字段来记录次数
     * REQ201912140002 关于全球通美兰机场免费停车的需求
     * @param rd
     * @return
     * @throws Exception
     */
    public String queryRemainRecordNum(BenefitBindRelReqData rd) throws Exception {
        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        DataMap param = new DataMap();
        param.put("USER_ID",rd.getUca().getUserId());
        param.put("SERIAL_NUMBER", rd.getUca().getSerialNumber());
        param.put("RIGHT_ID", rd.getRightId());
        param.put("DISCNT_CODE", rd.getDiscntCode());
        int remainUseNum = bean.queryRemainUseNum(param);
        return String.valueOf(remainUseNum);
    }
}
