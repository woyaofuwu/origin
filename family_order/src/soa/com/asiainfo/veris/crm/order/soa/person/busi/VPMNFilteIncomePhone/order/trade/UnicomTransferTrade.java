
package com.asiainfo.veris.crm.order.soa.person.busi.VPMNFilteIncomePhone.order.trade;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.TransPhoneTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.VPMNFilteIncomePhone.order.requestdata.UnicomTransferReqData;

public class UnicomTransferTrade extends BaseTrade implements ITrade
{

    /**
     * 检查用户是否具有办理资格。 携号转网用户 不允许办理
     * 
     * @return
     */
    private boolean checkUserLegalTrans(UnicomTransferReqData utrd) throws Exception
    {
        boolean flag = true;

        IDataset userDs = UserInfoQry.getLatestUserInfosBySerialNumber(utrd.getPhone_code_b());

        if (IDataUtil.isNotEmpty(userDs))
        {

            String userIdB = userDs.get(0, "USER_ID").toString();// 根据号码获取开户时间最大的user_id

            IDataset npDs = UserNpInfoQry.qryUserNpInfosByUserId(userIdB);
            if (npDs != null && npDs.size() > 0)
            {
                if (!npDs.get(0, "PORT_IN_NETID").toString().equals(npDs.get(0, "HOME_NETID").toString()))
                {
                    flag = false;
                }
            }
        }

        return flag;
    }

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        UnicomTransferReqData utrd = (UnicomTransferReqData) bd.getRD();
        // 对方为协号用户不允许办理
        if (!this.checkUserLegalTrans(utrd))
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_2000, utrd.getPhone_code_b());
        }
        this.createMainTradeData(bd);
        this.createDiscntTradeData(bd);
        this.createTransPhoneTradeData(bd);

    }

    /**
     * 添加优惠
     * 
     * @param bd
     * @throws Exception
     */
    private void createDiscntTradeData(BusiTradeData bd) throws Exception
    {
        UnicomTransferReqData utrd = (UnicomTransferReqData) bd.getRD();
        // 优惠台账数据
        DiscntTradeData dtd = new DiscntTradeData();
        UcaData uca = utrd.getUca();

        // 新增
        if (UnicomTransferReqData.MODIFY_ADD.equals(utrd.getModify_state()))
        {
            dtd.setUserId(uca.getUserId());
            dtd.setElementId("8");
            dtd.setPackageId("-1");
            dtd.setProductId("-1");
            // 开始时间
            dtd.setStartDate(utrd.getStart_date() + SysDateMgr.getSysTime().substring(10, 19));
            dtd.setEndDate(utrd.getEnd_date() + SysDateMgr.getEndTime235959());
            dtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
            dtd.setInstId(SeqMgr.getInstId());
            bd.add(uca.getSerialNumber(), dtd);
            // 删除
        }
        else if (UnicomTransferReqData.MODIFY_DELETE.equals(utrd.getModify_state()))
        {
            // 根据优惠编码查询已有优惠
            List<DiscntTradeData> userDisctList = uca.getUserDiscntByDiscntId("8");

            if (userDisctList != null && userDisctList.size() > 0)
            {
                // 原有优惠
                dtd = userDisctList.get(0).clone();
                dtd.setEndDate(SysDateMgr.getSysTime());
                dtd.setModifyTag(BofConst.MODIFY_TAG_DEL);
            }
            else
            {
                CSAppException.apperr(ElementException.CRM_ELEMENT_2001);
            }

            bd.add(uca.getSerialNumber(), dtd);
        }
    }

    /**
     * 处理主台账
     * 
     * @param bd
     * @throws Exception
     */
    private void createMainTradeData(BusiTradeData bd) throws Exception
    {
        UnicomTransferReqData utrd = (UnicomTransferReqData) bd.getRD();
        // 主台账
        MainTradeData mtd = bd.getMainTradeData();

        mtd.setRsrvStr1(utrd.getPhone_code_a());
        mtd.setRsrvStr2(utrd.getPhone_code_b());
        mtd.setRsrvStr5(SysDateMgr.getSysTime());
        // 删除
        if (UnicomTransferReqData.MODIFY_DELETE.equals(utrd.getModify_state()))
        {
            mtd.setRsrvStr6(BofConst.MODIFY_TAG_DEL);
        }
        else
        {
            // 新增、修改
            mtd.setRsrvStr3(utrd.getStart_date() + SysDateMgr.getSysTime().substring(10, 19));
            mtd.setRsrvStr4(utrd.getEnd_date() + SysDateMgr.getEndTime235959());
            if (UnicomTransferReqData.MODIFY_ADD.equals(utrd.getModify_state()))
            {
                mtd.setRsrvStr6(BofConst.MODIFY_TAG_ADD);
            }
            else if (UnicomTransferReqData.MODIFY_UPDATE.equals(utrd.getModify_state()))
            {
                mtd.setRsrvStr6(BofConst.MODIFY_TAG_UPD);
            }
        }

    }

    /**
     * @Function: createTransPhoneTradeData
     * @Description: 由于这个tf_F_trans_phoney设计有问题 完工不用这个台账，完工自己配action处理，登记这个台账只是为了同步
     * @param btd
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月31日 上午10:42:10
     */
    private void createTransPhoneTradeData(BusiTradeData btd) throws Exception
    {
        UnicomTransferReqData reqData = (UnicomTransferReqData) btd.getRD();
        TransPhoneTradeData tptd = new TransPhoneTradeData();
        tptd.setPhoneCodeA(reqData.getPhone_code_a());
        tptd.setPhoneCodeB(reqData.getPhone_code_b());
        // 删除
        if (UnicomTransferReqData.MODIFY_DELETE.equals(reqData.getModify_state()))
        {
        	tptd.setStartDate(reqData.getStart_date());
            tptd.setModifyTag(BofConst.MODIFY_TAG_DEL);
            tptd.setEndDate(reqData.getAcceptTime());
        }
        else
        {

            String startDate = reqData.getStart_date() + SysDateMgr.getSysTime().substring(10, 19);
            String endDate = reqData.getEnd_date() + SysDateMgr.getEndTime235959();
            tptd.setEndDate(endDate);
            tptd.setStartDate(startDate);
            // 新增
            if (BofConst.MODIFY_TAG_ADD.equals(btd.getMainTradeData().getRsrvStr6()))
            {
                tptd.setModifyTag(BofConst.MODIFY_TAG_ADD);

                // 修改
            }
            else if (BofConst.MODIFY_TAG_UPD.equals(btd.getMainTradeData().getRsrvStr6()))
            {
                tptd.setModifyTag(BofConst.MODIFY_TAG_UPD);
            }
        }
        btd.add(reqData.getUca().getSerialNumber(), tptd);
    }
}
