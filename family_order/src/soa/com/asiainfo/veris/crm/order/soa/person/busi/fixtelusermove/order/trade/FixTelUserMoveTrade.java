
package com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.TelephoneTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.telephone.TelInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.userconn.UserConnQry;
import com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove.order.requestdata.FixTelUserMoveRequestData;

public class FixTelUserMoveTrade extends BaseTrade implements ITrade
{
    final static String NOTICE_SERVICE_ID = "347";

    /**
     * 创建具体业务台账
     * 
     * @author dengyong3
     * @param btd
     * @throws Exception
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        createTelephoneTradeData(btd);

        FixTelUserMoveRequestData telUserMoveRD = (FixTelUserMoveRequestData) btd.getRD();
        btd.getMainTradeData().setRsrvStr1(telUserMoveRD.getNumChangeData().getSerialNumber());
        if (StringUtils.isNotBlank(telUserMoveRD.getNumChangeData().getSerialNumber()))
        {// 如果选择了新固话号码
            createUserTradeData(btd); // 登记用户台账
            createResTradeData(btd); // 登记资源台账
            // 铁通无条件呼叫转移、铁通改号报知服务删除和添加在action中处理
            dealSpeTrade(btd); // 登记特殊业务台账

            // 固话选号能否使用公共的ResEngrossaction,需要和资源侧确认,这个先不加action
            preUsePhoneNumber(btd); // 选占号码
        }
    }

    public void createResTradeData(BusiTradeData btd) throws Exception
    {
        FixTelUserMoveRequestData telUserMoveRD = (FixTelUserMoveRequestData) btd.getRD();
        IDataset resInfos = UserResInfoQry.queryUserSimInfo(telUserMoveRD.getUca().getUserId(), "N");// 获取固话号码信息
        if (IDataUtil.isEmpty(resInfos))
        {
            // CAppException.("没有获取到有效的用户资源信息！"); TODO:添加异常
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的用户资源信息！");
        }

        ResTradeData odlResTradeData = new ResTradeData(resInfos.getData(0));
        odlResTradeData.setEndDate(SysDateMgr.getSysTime());
        odlResTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        odlResTradeData.setRemark("因改号业务而终止原号码资源");
        btd.add(telUserMoveRD.getUca().getSerialNumber(), odlResTradeData);

        ResTradeData resTradeData = new ResTradeData();
        resTradeData.setUserId(telUserMoveRD.getUca().getUserId());
        resTradeData.setUserIdA("-1");
        resTradeData.setResTypeCode("N");
        resTradeData.setResCode(telUserMoveRD.getNumChangeData().getSerialNumber());
        resTradeData.setImsi("0");
        resTradeData.setKi("");
        resTradeData.setInstId(SeqMgr.getInstId());
        resTradeData.setCampnId("");
        resTradeData.setStartDate(SysDateMgr.getSysTime());
        resTradeData.setEndDate(SysDateMgr.getTheLastTime());
        resTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        resTradeData.setRemark(telUserMoveRD.getTelChangeData().getRemark());
        resTradeData.setRsrvTag1(telUserMoveRD.getNumChangeData().getResKindCode());// 资源小类
        resTradeData.setRsrvStr1(telUserMoveRD.getNumChangeData().getSwitchId());// 交换机编号
        resTradeData.setRsrvStr2(telUserMoveRD.getNumChangeData().getSwitchType());// 交换机类型

        btd.add(telUserMoveRD.getUca().getSerialNumber(), resTradeData);
    }

    public void createTelephoneTradeData(BusiTradeData btd) throws Exception
    {
        FixTelUserMoveRequestData telUserMoveRD = (FixTelUserMoveRequestData) btd.getRD();
        // 删除旧的telphone记录
        IDataset userTelephoneDatas = TelInfoQry.getTelInfo(telUserMoveRD.getUca().getUserId());// 获取用户固话装机信息
        IData userTelephoneData = null;
        if (userTelephoneDatas != null && !userTelephoneDatas.isEmpty())
        {
            userTelephoneData = userTelephoneDatas.getData(0);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1, "根据手机号码查询不到对应的装机信息");
        }
        TelephoneTradeData oldTelTradeData = new TelephoneTradeData(userTelephoneData);
        oldTelTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        oldTelTradeData.setEndDate(SysDateMgr.getSysTime());
        btd.add(telUserMoveRD.getUca().getSerialNumber(), oldTelTradeData);

        // 增加新的telphone记录
        TelephoneTradeData telTradeData = new TelephoneTradeData();
        telTradeData.setUserId(telUserMoveRD.getUca().getUserId());
        telTradeData.setCancelTag("0");
        telTradeData.setStandAddress(telUserMoveRD.getTelChangeData().getStandAddress());
        telTradeData.setdetailAddress(telUserMoveRD.getTelChangeData().getDetailAddress());
        telTradeData.setSignPath(telUserMoveRD.getTelChangeData().getSignPath());
        telTradeData.setStandAddressCode(telUserMoveRD.getTelChangeData().getStandAddressCode());
        telTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        telTradeData.setInstId(SeqMgr.getInstId());
        telTradeData.setStartDate(SysDateMgr.getSysTime());
        telTradeData.setSignPath(telUserMoveRD.getTelChangeData().getSignPath());
        telTradeData.setEndDate(SysDateMgr.getTheLastTime());
        telTradeData.setRemark(telUserMoveRD.getTelChangeData().getRemark());

        IDataset dataset = UserConnQry.getConnByUserIdAndType(telUserMoveRD.getUca().getUserId(), "BK");// 查询用户固话宽带共线信息
        // 没找到基础库判为空方法,先写成这样
        if (null != dataset && dataset.size() > 0)
        {// 如果存在宽带与固话共线信息,记录固话用户ID
            telTradeData.setRsrvStr5(dataset.getData(0).getString("USER_ID_A", ""));
        }

        btd.getMainTradeData().setRemark("用户固话移机操作");
        btd.getMainTradeData().setNetTypeCode("12");
        btd.getMainTradeData().setSubscribeType("300");
        btd.getMainTradeData().setPfType("300");
        btd.getMainTradeData().setOlcomTag("1");

        if (StringUtils.isNotBlank((telUserMoveRD.getNumChangeData().getSerialNumber())))
        {// 存在新固话号码，设置新服务号码
            btd.getMainTradeData().setRsrvStr1(telUserMoveRD.getNumChangeData().getSerialNumber()); // 新服务号码
        }

        btd.add(telUserMoveRD.getUca().getSerialNumber(), telTradeData);
    }

    public void createUserTradeData(BusiTradeData btd) throws Exception
    {
        FixTelUserMoveRequestData telUserMoveRD = (FixTelUserMoveRequestData) btd.getRD();
        UserTradeData userTradeData = telUserMoveRD.getUca().getUser().clone();
        userTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
        userTradeData.setSerialNumber(telUserMoveRD.getNumChangeData().getSerialNumber());
        btd.add(telUserMoveRD.getUca().getSerialNumber(), userTradeData);
    }

    /**
     * 其它特殊业务处理
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void dealSpeTrade(BusiTradeData btd) throws Exception
    {
        IData param = new DataMap();
        String userId = btd.getRD().getUca().getUserId();
        FixTelUserMoveRequestData fixMoveRD = (FixTelUserMoveRequestData) btd.getRD();

        // 终止无条件呼叫转移
        IDataset svcs346 = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userId, "346");
        if (svcs346 != null && svcs346.size() > 0)
        {
            geneTradeSvc(btd, svcs346);
        }

        // 终止原铁通改号报知服务
        param.clear();

        IDataset svc347 = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userId, "347");
        if (svc347 != null && svc347.size() > 0)
        {
            geneTradeSvc(btd, svc347);
        }

        if (StringUtils.isNotBlank(fixMoveRD.getChangteleNotice()))
            ;
        {
            geneTradeSvc(btd); // 服务台帐子表
        }

    }

    /**
     * 服务台帐子表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void geneTradeSvc(BusiTradeData btd) throws Exception
    {
        SvcTradeData svcData = new SvcTradeData();
        String tradeId;
        String update_time = btd.getRD().getAcceptTime();
        String update_staff_id = getVisit().getStaffId();
        String update_depart_id = getVisit().getDepartId();
        svcData.setUserId(btd.getRD().getUca().getUserId());
        svcData.setUserIdA("-1");
        svcData.setProductId("-1");
        svcData.setPackageId("-1");
        svcData.setElementId(NOTICE_SERVICE_ID);
        svcData.setMainTag("0");
        svcData.setInstId(SeqMgr.getInstId());
        svcData.setStartDate(update_time);
        svcData.setEndDate(SysDateMgr.END_DATE_FOREVER);
        svcData.setModifyTag("0");
        svcData.setRemark("因选择改号报知而新增的服务");

        btd.add(btd.getRD().getUca().getSerialNumber(), svcData);
    }

    /**
     * 服务台帐子表
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void geneTradeSvc(BusiTradeData btd, IDataset services) throws Exception
    {
        IDataset svcTradeDatas = new DatasetList();

        // String tradeId = td.getTradeId();
        String update_time = btd.getRD().getAcceptTime();
        String update_staff_id = getVisit().getStaffId();
        String update_depart_id = getVisit().getDepartId();

        for (int i = 0; i < services.size(); i++)
        {
            SvcTradeData svcData = new SvcTradeData(services.getData(i));
            // SvcTradeData svcData = (SvcTradeData)services.getData(i);
            // svcData.setInstId(svcData.getInstId());
            svcData.setUserId(btd.getRD().getUca().getUserId());
            svcData.setEndDate(update_time);
            svcData.setModifyTag("1");
            svcData.setRemark("因改号业务而终止该服务");
            // svcData.se("UPDATE_STAFF_ID", update_staff_id);
            // svcData.put("UPDATE_DEPART_ID", update_depart_id);
            btd.add(btd.getRD().getUca().getSerialNumber(), svcData);
        }

    }

    private void preUsePhoneNumber(BusiTradeData btd) throws Exception
    {

        String pstp_id = btd.getRD().getUca().getCustomer().getCustId();
        FixTelUserMoveRequestData telUserMoveRD = (FixTelUserMoveRequestData) btd.getRD();
        String resNo = telUserMoveRD.getNumChangeData().getSerialNumber();
        PBossCall.resPreOccupy(resNo, pstp_id);
    }
}
