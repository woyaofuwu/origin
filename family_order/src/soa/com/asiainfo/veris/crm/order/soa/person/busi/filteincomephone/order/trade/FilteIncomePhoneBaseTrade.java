/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.requestdata.FilteIncomePhoneTradeReqData;

/**
 * @CREATED by gongp@2014-5-4 修改历史 Revision 2014-5-4 上午10:59:31
 */
public abstract class FilteIncomePhoneBaseTrade extends BaseTrade
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {

    }

    public void dealMainTradeTag(MainTradeData mainTD, FilteIncomePhoneTradeReqData reqData) throws Exception
    {
        String userId = reqData.getUca().getUserId();

        if ("1".equals(reqData.getIsOpenSms()))
        {
            if (existSmsService(userId))
            {
                mainTD.setRsrvStr9("1");// 1：接收来电通知 2：不接收来电通知
            }
        }
        else
        {
            if (!existSmsService(userId))
            {
                mainTD.setRsrvStr9("2");// 1：接收来电通知 2：不接收来电通知
            }
        }

        if (existVpmnRelation(userId))
        {
            mainTD.setRsrvStr10("0");// vpmn用户标识
        }
        else
        {
            mainTD.setRsrvStr10("1");// vpmn用户标识
        }

    }

    /**
     * 是否开通短信服务
     * 
     * @param ucaData
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-3
     */
    public boolean existSmsService(String user_id) throws Exception
    {
        return UserOtherInfoQry.getUserOther(user_id, "271").size() > 0;
    }

    /**
     * 是否存在VPMN
     * 
     * @param userId
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-3
     */
    public boolean existVpmnRelation(String userId) throws Exception
    {

        return RelaUUInfoQry.getRelationUUInfoByDeputySn(userId, "20", null).size() > 0;
    }

    /**
     * 生成优惠子表台账
     * 
     * @throws Exception
     */
    public void geneTradeDistcnt(BusiTradeData bd, String modifyTag) throws Exception
    {

        FilteIncomePhoneTradeReqData reqData = (FilteIncomePhoneTradeReqData) bd.getRD();

        DiscntTradeData td = new DiscntTradeData();

        if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
        {// tag：0 新增 1 删除
            td.setStartDate(reqData.getAcceptTime());// 写成实时的，防止一天内做开、关、再开时报主键冲突
            td.setEndDate(SysDateMgr.END_DATE_FOREVER);
            td.setUserId(reqData.getUca().getUserId());
            td.setInstId(SeqMgr.getInstId());
            td.setUserIdA("-1");
            td.setPackageId("-1");
            td.setProductId("-1");
            td.setElementId(this.getDiscntCode());
        }
        else
        {
            List<DiscntTradeData> discntDatas = reqData.getUca().getUserDiscntByDiscntId(this.getDiscntCode());

            if (discntDatas != null && discntDatas.size() > 0)
            {
                td = discntDatas.get(0);
            }
            else
            {
                return;
            }

            td.setEndDate(reqData.getAcceptTime());
        }
        td.setModifyTag(modifyTag);

        bd.add(reqData.getUca().getSerialNumber(), td);
    }

    public void geneTradeOther(BusiTradeData bd, String serialNumber, String remark, String modifyTag) throws Exception
    {

        FilteIncomePhoneTradeReqData reqData = (FilteIncomePhoneTradeReqData) bd.getRD();

        OtherTradeData td = new OtherTradeData();

        if ("0".equals(modifyTag))
        {
            td.setStartDate(SysDateMgr.getSysTime());
            td.setEndDate(SysDateMgr.END_DATE_FOREVER);
            td.setUserId(reqData.getUca().getUserId());
            td.setRsrvValueCode("1301");
            td.setRsrvValue(reqData.getUca().getUserId());
            td.setInstId(SeqMgr.getInstId());
            td.setRsrvStr1(serialNumber);
            td.setRsrvStr2(reqData.getUca().getSerialNumber());
            td.setStaffId(CSBizBean.getVisit().getStaffId());
            td.setDepartId(CSBizBean.getVisit().getDepartId());
        }
        else
        {
            td = getUserFilterPhoneOtherData(reqData.getUca().getUserId(), serialNumber);
            if (td == null)
            {
                return;
            }
            td.setEndDate(SysDateMgr.getSysTime());
            td.setStaffId(CSBizBean.getVisit().getStaffId());
            td.setDepartId(CSBizBean.getVisit().getDepartId());
        }

        td.setModifyTag(modifyTag);
        td.setRemark(remark);

        /*
         * if ("2".equals(modifyTag)) { td.setRsrvStr9("说明：MODIFY_TAG=2时为删除全部拒接号码"); td.setRsrvStr1("");// 全删时字段为空 }
         */

        bd.add(reqData.getUca().getSerialNumber(), td);
    }

    /**
     * 生成服务子表台账
     * 
     * @throws Exception
     */
    public void geneTradeSvc(BusiTradeData bd, String modifyTag) throws Exception
    {

        FilteIncomePhoneTradeReqData reqData = (FilteIncomePhoneTradeReqData) bd.getRD();

        SvcTradeData td = new SvcTradeData();

        if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
        {// tag：0 新增 1 删除
            td.setStartDate(SysDateMgr.getSysTime());
            td.setEndDate(SysDateMgr.END_DATE_FOREVER);
            td.setUserId(reqData.getUca().getUserId());
            td.setInstId(SeqMgr.getInstId());
            td.setUserIdA("-1");
            td.setPackageId("-1");
            td.setProductId("-1");
            td.setElementId("560");
            td.setMainTag("0");
        }
        else
        {
            List<SvcTradeData> svcDatas = reqData.getUca().getUserSvcBySvcId("560");

            if (svcDatas != null && svcDatas.size() > 0)
            {
                td = svcDatas.get(0);
            }
            else
            {
                return;
            }

            td.setEndDate(SysDateMgr.getSysTime());
        }

        td.setModifyTag(modifyTag);

        bd.add(reqData.getUca().getSerialNumber(), td);
    }

    /**
     * 开通或者关闭短信服务
     * 
     * @param bd
     * @param modifyTag
     * @throws Exception
     * @CREATE BY GONGP@2014-5-16
     */
    public void genSendSMSTradeOther(BusiTradeData bd, String modifyTag) throws Exception
    {

        FilteIncomePhoneTradeReqData reqData = (FilteIncomePhoneTradeReqData) bd.getRD();

        OtherTradeData td = new OtherTradeData();

        if ("0".equals(modifyTag))
        {
            if (getUserFilterPhoneSmsData(reqData.getUca().getUserId()) == null)
            {
                td.setStartDate(SysDateMgr.getSysTime());
                td.setEndDate(SysDateMgr.END_DATE_FOREVER);
                td.setUserId(reqData.getUca().getUserId());
                td.setRsrvValueCode("271");
                td.setRsrvValue("开通来电拒接短信提醒功能");
                td.setInstId(SeqMgr.getInstId());
                td.setModifyTag(modifyTag);
                td.setStaffId(CSBizBean.getVisit().getStaffId());
                td.setDepartId(CSBizBean.getVisit().getDepartId());

                bd.add(reqData.getUca().getSerialNumber(), td);
            }
        }
        else
        {
            td = getUserFilterPhoneSmsData(reqData.getUca().getUserId());
            if (td == null)
            {
                return;
            }
            td.setEndDate(SysDateMgr.getSysTime());
            td.setRsrvStr1(reqData.getTradeId());
            td.setRsrvValue("关闭来电拒接短信提醒功能");

            td.setModifyTag(modifyTag);

            bd.add(reqData.getUca().getSerialNumber(), td);
        }

    }

    public String getDiscntCode() throws Exception
    {

        IDataset outDs = CommparaInfoQry.getCommPkInfo("CSM", "6010", "VPMN", "0898");

        if (IDataUtil.isEmpty(outDs))
        {
            // common.error("558601:获取优惠编码出错！");
            CSAppException.apperr(ElementException.CRM_ELEMENT_91);
        }
        return outDs.getData(0).getString("PARA_CODE1", "");// 优惠编码
    }

    /**
     * 生效的拒接号码数量
     * 
     * @param user_id
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-3
     */
    public int getEffectRejectNoCount(BusiTradeData bd, String userId) throws Exception
    {
        int effectCount = UserOtherInfoQry.getUserOther(userId, "1301").size();

        List<OtherTradeData> otherTds = bd.getTradeDatas(TradeTableEnum.TRADE_OTHER);

        for (int i = 0, size = otherTds.size(); i < size; i++)
        {

            OtherTradeData otherTd = otherTds.get(i);
            if (userId.equals(otherTd.getUserId()))
            {
                if (BofConst.MODIFY_TAG_ADD.equals(otherTd.getModifyTag()))
                {
                    effectCount++;
                }
                else if (BofConst.MODIFY_TAG_DEL.equals(otherTd.getModifyTag()))
                {
                    effectCount--;
                }
            }
        }

        return effectCount;
    }

    public OtherTradeData getTradeUserFilterPhoneSmsData(String modifyTag) throws Exception
    {
        List<BusiTradeData> btds = DataBusManager.getDataBus().getBtds();

        int btdSize = btds.size();
        OtherTradeData td = null;

        for (int i = 0; i < btdSize; i++)
        {
            BusiTradeData btd = btds.get(i);

            List<OtherTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);

            for (int j = 0, size = list.size(); j < size; j++)
            {
                if ("271".equals(list.get(j).getRsrvValueCode()) && modifyTag.equals(list.get(j).getModifyTag()))
                {
                    td = list.get(j);
                    continue;
                }
            }
        }
        return td;
    }

    /**
     * 得到用户来电拒接对应号码的记录
     * 
     * @param user_id
     * @param serial_number
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-16
     */
    public OtherTradeData getUserFilterPhoneOtherData(String user_id, String serial_number) throws Exception
    {

        IDataset dataset = UserOtherInfoQry.getOtherInfoByCodeUserId(user_id, "1301");
        OtherTradeData td = null;
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData data = dataset.getData(i);

            if (serial_number.equals(data.getString("RSRV_STR1")))
            {
                td = new OtherTradeData(data);
                continue;
            }
        }
        return td;
    }

    /**
     * 查看本批次的数据有没有重复号码
     * 
     * @param user_id
     * @param serial_number
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-30
     */
    public OtherTradeData getUserFilterPhoneOtherTradeData(BusiTradeData btd, String serial_number) throws Exception
    {

        List<OtherTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);

        OtherTradeData td = null;
        for (int i = 0, size = list.size(); i < size; i++)
        {
            if (serial_number.equals(list.get(i).getRsrvStr1()))
            {
                td = list.get(i);
                continue;
            }
        }
        return td;
    }

    /**
     * 得到用户来电拒接短信提醒信息
     * 
     * @param user_id
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-16
     */
    private OtherTradeData getUserFilterPhoneSmsData(String user_id) throws Exception
    {

        IDataset dataset = UserOtherInfoQry.getOtherInfoByCodeUserId(user_id, "271");
        OtherTradeData td = null;

        if (IDataUtil.isNotEmpty(dataset))
        {
            return new OtherTradeData(dataset.getData(0));
        }

        return td;
    }
}
