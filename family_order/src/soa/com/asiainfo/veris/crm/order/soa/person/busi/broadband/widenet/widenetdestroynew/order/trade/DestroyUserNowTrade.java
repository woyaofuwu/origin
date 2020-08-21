package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ExtTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.requestdata.DestroyUserNowRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.DestroyUserComm;

public class DestroyUserNowTrade extends BaseTrade implements ITrade {
    /**
     * 修改主台帐字段
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd) throws Exception {

        btd.getMainTradeData().setSubscribeType("300");

        /**
         * REQ201609280002 宽带功能优化 chenxy3 2016-11-29 RSRV_STR2=销户原因编码 RSRV_STR8=销户原因中文显示内容
         * */
        DestroyUserNowRequestData rd = (DestroyUserNowRequestData) btd.getRD();
        btd.getMainTradeData().setRsrvStr2(rd.getDestoryReason());
        btd.getMainTradeData().setRsrvStr8(rd.getReasonElse());

    }

    /**
     * 实现父类抽象方法
     */
    public void createBusiTradeData(BusiTradeData btd) throws Exception {

        // 用户相关资料处理
        this.createEndUserInfoTrade(btd);
        // 修改用户主体服务
        this.ModifyMainSvcStateByUserid(btd);// 一定要放在服务状态变更订单生成之后做。

        appendTradeMainData(btd);

        // 中小企业快速办理集团成员新增入ext表
        createTradeExt(btd);
    }

    // 终止用户相关资料订单
    private void createEndUserInfoTrade(BusiTradeData<BaseTradeData> btd) throws Exception {
        DestroyUserComm destroyComm = new DestroyUserComm();
        destroyComm.createEndRelationUUTrade(btd);// uu关系
        destroyComm.createEndUserTrade(btd);// 用户
        destroyComm.createEndSvcInfoTrade(btd);// 服务
        destroyComm.createEndWidenetDiscntInfoTrade(btd);// 优惠
        destroyComm.createEndProductTrade(btd);// 产品
        destroyComm.createEndAttrInfoTrade(btd);// 属性
        destroyComm.createEndResInfoTrade(btd);// 资源
        destroyComm.createEndOtherTrade(btd);// 其他信息
        destroyComm.createEndPayRelationInfoTrade(btd);// 付费关系
        destroyComm.createEndUserWidenetTrade(btd);// 宽带账户资料

        // 终止营销活动放在了拆机完工后，给到期表中插入到期执行记录，调用终止营销活动服务

        // if (btd.getTradeTypeCode().equals("615"))//新增kangyt
        // {
        // DestroyUserNowRequestData rd = (DestroyUserNowRequestData)btd.getRD();
        // String serialnumbera = rd.getSerialNumberA();
        // IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0",serialnumbera);
        // String userId ="";
        // if (userInfos!=null && userInfos.size()>0)
        // {
        // userId = userInfos.getData(0).getString("USER_ID");
        // }
        // //终止营销活动
        // createEndSaleActiveTrade(btd,userId);
        // //终止营销活动优惠
        // createEndDiscntInfoTrade(btd,userId);
        // }

        // 几种宽带的业务类型合并了，把FTTH宽带的判断放在在下面的函数里面
        if (btd.getTradeTypeCode().equals("605") || btd.getTradeTypeCode().equals("615")) {
            EndOtherFTTHInfoBySerialNumber(btd);
        }

        if (btd.getTradeTypeCode().equals("635") || btd.getTradeTypeCode().equals("615") || btd.getTradeTypeCode().equals("7244")) {
            destroyComm.createEndUserWidenetOtherTrade(btd);
        }

    }

    /**
     * 构建服务状态变更订单表
     * 
     * @param btd
     * @throws Exception
     */
    private void ModifyMainSvcStateByUserid(BusiTradeData btd) throws Exception {
        ChangeSvcStateComm bean = new ChangeSvcStateComm();
        bean.modifyMainSvcStateByUserId(btd);
    }

    /**
     * 截止宽带光猫other表记录
     * 
     * @throws Exception
     */
    public void EndOtherFTTHInfoBySerialNumber(BusiTradeData btd) throws Exception {
        //
        DestroyUserNowRequestData rd = (DestroyUserNowRequestData) btd.getRD();
        String widetype = rd.getWideType();
        btd.getMainTradeData().setRsrvStr9(widetype);// 宽带类型
        btd.getMainTradeData().setRsrvStr6(rd.getModermReturn());// 是否退还
        if ("1".equals(widetype) || "2".equals(widetype) || "6".equals(widetype) || "4".equals(widetype)) {
            // kangyt
            return;// 只有FTTH宽带才有光猫
        }
        if (!"0".equals(rd.getModemMode()) && !"2".equals(rd.getModemMode())) {
            // 非租赁方式不需要处理押金，不需要处理光猫退订
            return;
        }
        // kangyt 将是否退光猫的操作保存在主台帐的rsrv_str1中
        btd.getMainTradeData().setRsrvStr1(rd.getModermReturn());
        // 宽带类型
        btd.getMainTradeData().setRsrvStr2(rd.getWideType());

        String serialNumber = rd.getSerialNumberA();

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);

        IDataset userinfo = CSAppCall.call("SS.DestroyUserNowSVC.getUserInfoBySerailNumber", param);
        if (!userinfo.isEmpty()) {
            IDataset userOtherinfo = null;
            if (userinfo.getData(0).getString("RSRV_STR10", "").equals("BNBD"))// 商务宽带，不论是否退光猫，都截止光猫记录
            {
                userOtherinfo = CSAppCall.call("SS.DestroyUserNowSVC.queryGroupUserOtherInfo", userinfo.first());
                if (!userOtherinfo.isEmpty()) {
                    OtherTradeData data = new OtherTradeData(userOtherinfo.first());
                    data.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());
                    // 记录退光猫记录
                    if (rd.getModermReturn().equals("1")) {
                        data.setRsrvTag2("3");// 退光猫时记录标志
                    } else {
                        data.setRsrvStr9("2");// 未退光猫标志
                    }

                    btd.add(serialNumber, data);
                }
            } else {
                // userOtherinfo = CSAppCall.call("SS.DestroyUserNowSVC.queryUserOtherInfo", userinfo.first());
                userOtherinfo = CSAppCall.call("SS.DestroyUserNowSVC.queryUserModemRent", userinfo.first());
                if (!userOtherinfo.isEmpty()) {
                    // rsrv_str1--光猫串号
                    // rsrv_str2--押金金额
                    // rsrv_str6--光猫型号
                    // rsrv_str7--押金状态 0,押金、1,已转移、2已退还、3,已沉淀
                    // rsrv_str8--BOSS押金转移流水
                    // rsrv_str9--移机、拆机未退光猫标志：1.移机未退光猫 2.拆机未退光猫
                    // rsrv_tag1--申领模式 0租赁，1购买，2赠送，3自备
                    // rsrv_tag2--光猫状态 1:申领，2:更改，3:退还，4:丢失
                    // rsrv_tag3--业务类型 1:开户，2:移机
                    // rsrv_date1--拆机时间，移机时间，，当拆机不退光猫的时候记录拆机时间，end_date就不修改了
                    if (rd.getModermReturn().equals("1")) // 个人宽带需要退光猫时截止光猫记录
                    {
                        OtherTradeData data = new OtherTradeData(userOtherinfo.first());
                        data.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());
                        data.setRsrvStr7("2");
                        data.setRsrvTag2("3");

                        btd.add(serialNumber, data);
                    } else { // 不退光猫时，
                             //
                        OtherTradeData data = new OtherTradeData(userOtherinfo.first());
                        data.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        data.setRemark("拆机不退光猫，90天内退，定时扫描");
                        // data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());

                        // rsrv_date1--拆机时间，当拆机不退光猫的时候记录拆机时间，end_date就不修改了，等退了光猫后再修改
                        data.setRsrvDate1(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());
                        data.setRsrvStr9("2");

                        btd.add(serialNumber, data);
                    }

                }

            }
        }

    }

    // 终止用户营销活动资料
    public void createEndSaleActiveTrade(BusiTradeData btd, String userId) throws Exception {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IData qryParam = new DataMap();
        qryParam.put("USER_ID", userId);
        qryParam.put("PARAM_CODE", btd.getRD().getTradeType().getTradeTypeCode());
        IDataset ds = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES3", qryParam);
        if (ds != null && ds.size() > 0) {
            for (int i = 0; i < ds.size(); i++) {
                String lastdate = SysDateMgr.getLastDateThisMonth();// 获取当月月底日期
                SaleActiveTradeData saleActiveTradeData = new SaleActiveTradeData(ds.getData(i));
                saleActiveTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                saleActiveTradeData.setProcessTag("1");
                saleActiveTradeData.setRemark("立即销户终止用户活动信息！");
                saleActiveTradeData.setEndDate(lastdate);// 活动终止时间到本月月底
                if (StringUtils.isNotEmpty(ds.getData(i).getString("RSRV_DATE2"))) {
                    saleActiveTradeData.setRsrvDate2(btd.getRD().getAcceptTime());
                }
                saleActiveTradeData.setRsrvStr8(CSBizBean.getVisit().getCityCode());
                saleActiveTradeData.setCancelDate(SysDateMgr.getSysTime());

                btd.add(serialNumber, saleActiveTradeData);
            }

        }

        // IDataset userSaleActiveInfos = UserSaleActiveInfoQry.queryUserSaleActiveByTag(userId);
        // if (IDataUtil.isNotEmpty(userSaleActiveInfos))
        // {
        // IDataset paramUserSaleProducts = CommparaInfoQry.getCommByParaAttr("CSM", "155", CSBizBean.getTradeEparchyCode());
        // if (IDataUtil.isNotEmpty(paramUserSaleProducts))
        // {
        // for (int i = 0, count = userSaleActiveInfos.size(); i < count; i++)
        // {
        // IData tempData = userSaleActiveInfos.getData(i);
        // String productId = tempData.getString("PRODUCT_ID");
        // boolean bFind = false;
        // for (int j = 0, jCount = paramUserSaleProducts.size(); j < jCount; j++)
        // {
        // String paraCode1 = paramUserSaleProducts.getData(j).getString("PARA_CODE1", "0");
        // if (StringUtils.equals(productId, paraCode1))
        // {
        // bFind = true;
        // break;
        // }
        // }
        // if (bFind)//修改
        // {
        // String lastdate = SysDateMgr.getLastDateThisMonth();//获取当月月底日期
        // SaleActiveTradeData saleActiveTradeData = new SaleActiveTradeData(tempData);
        // saleActiveTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
        // saleActiveTradeData.setProcessTag("1");
        // saleActiveTradeData.setRemark("立即销户终止用户活动信息！");
        // saleActiveTradeData.setEndDate(lastdate);
        // if (StringUtils.isNotEmpty(tempData.getString("RSRV_DATE2")))
        // {
        // saleActiveTradeData.setRsrvDate2(btd.getRD().getAcceptTime());
        // }
        // btd.add(serialNumber, saleActiveTradeData);
        //
        // }
        // }
        // }
        // else
        // {
        // CSAppException.apperr(CrmCommException.CRM_COMM_902);
        // }
        // }
    }

    // 终止用户优惠信息订单数据生成
    public void createEndDiscntInfoTrade(BusiTradeData<BaseTradeData> btd, String userId) throws Exception {
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        IData qryParam = new DataMap();
        qryParam.put("USER_ID", userId);
        qryParam.put("PARAM_CODE", btd.getRD().getTradeType().getTradeTypeCode());
        IDataset ds = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES2", qryParam);
        if (ds != null && ds.size() > 0) {
            String lastdate = SysDateMgr.getLastDateThisMonth();// 获取当月月底日期
            String firstDayNextAcct = AcctDayDateUtil.getFirstDayNextAcct(userId);
            for (int i = 0; i < ds.size(); i++) {
                DiscntTradeData tempTradeData = new DiscntTradeData(ds.getData(i));
                String tempEndDate = tempTradeData.getEndDate();
                // 只处理失效时间大于等于下个月月初的数据
                if (SysDateMgr.getTimeDiff(firstDayNextAcct, tempEndDate, SysDateMgr.PATTERN_STAND) <= 0) {
                    DiscntTradeData disctnData = tempTradeData.clone();
                    disctnData.setEndDate(lastdate);
                    disctnData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                    btd.add(serialNumber, disctnData);
                }
            }
        }

    }

    private void createTradeExt(BusiTradeData<BaseTradeData> btd) throws Exception {

        DestroyUserNowRequestData reqData = (DestroyUserNowRequestData) btd.getRD();

        if (StringUtils.isNotBlank(reqData.getEcSerialNumber()) && StringUtils.isNotBlank(reqData.getEcUserId())) {
            ExtTradeData newTeadeExt = new ExtTradeData();
            newTeadeExt.setAttrCode("ESOP");
            newTeadeExt.setAttrValue(reqData.getIbsysId());
            newTeadeExt.setRsrvStr1(reqData.getNodeId());
            newTeadeExt.setRsrvStr6(reqData.getRecordNum());
            newTeadeExt.setRsrvStr8(reqData.getBusiformId());
            newTeadeExt.setRsrvStr10("EOS");
            btd.add(reqData.getEcSerialNumber(), newTeadeExt);
        }
    }
}
