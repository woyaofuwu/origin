package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.trade;

import java.util.List;

import com.ailk.biz.util.Encryptor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DESUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ExtTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PayRelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;

public class MergeWideUserCreateTrade extends BaseTrade implements ITrade {
    public void createBusiTradeData(BusiTradeData btd) throws Exception {
        // REQ201810190032 和家固话开户界面增加实名制校验—BOSS侧 by mqx end
        // 新增权限，有该权限的工号可以在家庭产品开户界面、家庭IMS固话开户界面进行单位证件开户。
        // 没有权限的工号进行单位证件开户时提示：该工号不具有和家固话单位证件开户权限（仅限自办厅工号申请）
        this.verifyOrganizationPriv(btd);

        MergeWideUserCreateRequestData reqData = (MergeWideUserCreateRequestData) btd.getRD();
        List<ProductModuleData> selectedElements = reqData.getProductElements();

        createTradeWidenet(btd, reqData);

        // 生成宽带用户
        createTradeUser(btd, reqData);

        // 生成虚拟用户
        createTradeVirtualUser(btd, reqData);

        // 生成虚拟产品台账
        createTradeProductVirtualUser(btd, reqData, reqData.getVirtualUserId());

        // 生成用户关系
        createTradeRelationUU(btd, reqData);

        // 宽带用户产品台账
        createTradeProduct(btd, reqData);

        // 虚拟用户绑定资费
        createTradeVirtualDiscnt(btd, reqData);

        // 生成光猫信息台账，//申领模式 0租赁，1购买，2赠送,3自备 ，自备不生成other台账
        if (StringUtils.isNotBlank(reqData.getModemStyle()) && !"3".equals(reqData.getModemStyle())) {
            createModemTradeData(btd, reqData);
        }
        // 如果是海工商宽带开户则新增一条other台账
        if (null != reqData.getIsHGS() && "1".equals(reqData.getIsHGS())) {
            createHGSOtherTradeData(btd, reqData);
        }

        // 生成魔百和临时信息
        if (StringUtils.isNotBlank(reqData.getTopSetBoxProductId())) {
            createTopSetBoxTradeData(btd, reqData);
            
            //魔百和必选优惠包
            if (StringUtils.isNotBlank(reqData.getTopSetBoxPlatSvcPkgs())) {
            	createPlatSvcTradeData(btd, reqData,"TOPSET_PLATSVC");
            }
        }
        
        //是否商务宽带
        if (!reqData.isBusinessWide()) {
        	//宽带 可选优惠包
            if (StringUtils.isNotBlank(reqData.getTopSetBoxPlatSvcPkgs2())) {
            	 createPlatSvcTradeData(btd, reqData,"BIND_PLATSVC");
            }
        }
        

        // IMS固话临时信息
        if (StringUtils.isNotBlank(reqData.getFixNumber()) && StringUtils.isNotBlank(reqData.getImsProductId())) {
            createIMSTradeData(btd, reqData);
            createCustInfoTradeData(btd, reqData);
        }

        // 生成付费关系
        createTradePayRelation(btd, reqData);

        // 构建产品
        ProductModuleCreator.createProductModuleTradeData(selectedElements, btd);

        // 追加主台账信息
        appendTradeMainData(btd, reqData);

        btd.addOpenUserAcctDayData(reqData.getUca().getUserId(), "1");
        btd.addOpenUserAcctDayData(reqData.getVirtualUserId(), "1");

        // 记录原铁通帐号Other表
        IData idPageRequest = btd.getRD().getPageRequestData();
        if (IDataUtil.isNotEmpty(idPageRequest)) {
            String strTTWidenet = idPageRequest.getString("TT_WIDENET", "");
            if (StringUtils.isNotBlank(strTTWidenet)) {
                String strTTUserID = idPageRequest.getString("TT_USER_ID", "");
                createTTWideNetTradeData(btd, strTTWidenet, strTTUserID);
            }
        }
        // 中小企业快速办理集团成员新增入ext表
        createTradeExt(btd, reqData);

    }

    /**
     * 生成海工商宽带开户other台账
     *
     * @param btd
     * @param reqData
     * @throws Exception
     */
    private void createHGSOtherTradeData(BusiTradeData btd, MergeWideUserCreateRequestData reqData) throws Exception {
        OtherTradeData HGSOtherTradeData = new OtherTradeData();

        HGSOtherTradeData.setRsrvValueCode("HGS_WIDE_ADD");
        HGSOtherTradeData.setRsrvValue("海工商宽带开户");

        HGSOtherTradeData.setUserId(reqData.getUca().getUserId());

        HGSOtherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        HGSOtherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        HGSOtherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        HGSOtherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        HGSOtherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值

        HGSOtherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(reqData.getUca().getSerialNumber(), HGSOtherTradeData);

    }

    /**
     * 记录原铁通帐号Other表
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void createTTWideNetTradeData(BusiTradeData<BaseTradeData> btd, String strTTWideNet, String strTTUserID) throws Exception {
        String strSn = btd.getMainTradeData().getSerialNumber();
        String strTradeId = btd.getTradeId();
        String strTradeTypeCode = btd.getTradeTypeCode();

        OtherTradeData otherTradeData = new OtherTradeData();

        otherTradeData.setRsrvValueCode("TT_WIDENET");
        otherTradeData.setRsrvValue(strTTWideNet);
        otherTradeData.setRsrvStr1(strTTUserID);
        otherTradeData.setRsrvStr2(strSn);
        otherTradeData.setRsrvStr5(strTradeId);

        otherTradeData.setUserId(btd.getMainTradeData().getUserId());

        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值

        // 业务类型 600:开户
        otherTradeData.setRsrvStr11(strTradeTypeCode);

        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(strSn, otherTradeData);
    }

    /**
     * 修改主台帐字段
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void appendTradeMainData(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {
        btd.getMainTradeData().setSubscribeType("300");

        // 宽带开户支付模式：P：立即支付 A：先装后付
        btd.getMainTradeData().setRsrvStr1(reqData.getWidenetPayMode());

        if ("A".equals(reqData.getWidenetPayMode())) {
            // 标识用户是否已经付费
            btd.getMainTradeData().setRsrvStr8("N");
        } else {
            btd.getMainTradeData().setRsrvStr8("Y");
        }

        // 标记宽带开户的光猫模式
        if (StringUtils.isNotBlank(reqData.getModemStyle())) {
            btd.getMainTradeData().setRsrvStr2(reqData.getModemStyle());
        }

        // 标记用户订购了魔百和业务
        if (StringUtils.isNotBlank(reqData.getTopSetBoxProductId())) {
            // 告知服开订购的魔百和业务
            btd.getMainTradeData().setRsrvStr3("1");
        }

        // 生成魔百和临时信息
        if (StringUtils.isNotBlank(reqData.getTopSetBoxProductId())) {
            if (reqData.getTopSetBoxDeposit() > 0) {
                // 新生成一个TradeId,魔百和转押金时调用此传入此tradeId，可以多单独返销
                btd.getMainTradeData().setRsrvStr4(SeqMgr.getTradeId());
            }
        }

        if (StringUtils.isNotBlank(reqData.getFixNumber())) {
            if (StringUtils.isNotBlank(reqData.getImsSaleActiveId())) {
                // 1表示办理IMS固话的礼包类营销活动，服开那边需要进行出库操作
                btd.getMainTradeData().setRsrvStr7("1");
            } else {
                // 0表示只单独办理了IMS固话，不需要进行出库操作
                btd.getMainTradeData().setRsrvStr7("0");
            }
        }

        // REQ201904280013 关于高价值小区攻坚专项营销活动的开发需求
        btd.getMainTradeData().setRsrvStr10(btd.getRD().getPageRequestData().getString("IS_HIGHTACTIVE"));

    }

    /**
     * 生成光猫台账信息
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void createModemTradeData(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {
        OtherTradeData otherTradeData = new OtherTradeData();

        if (reqData.isBusinessWide()) {
            otherTradeData.setRsrvValueCode("FTTH_GROUP");
            otherTradeData.setRsrvValue("商务宽带光猫申领");
            otherTradeData.setRsrvStr3(reqData.getUca().getSerialNumber());
            otherTradeData.setRsrvStr4(reqData.getNormalSerialNumber());
            otherTradeData.setRsrvStr5(reqData.getTradeId());

            otherTradeData.setUserId(reqData.getUca().getUserId());
        } else {
            otherTradeData.setRsrvValueCode("FTTH");
            otherTradeData.setRsrvValue("FTTH光猫申领");
            otherTradeData.setUserId(reqData.getNormalUserId());
        }

        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值

        // 光猫押金
        // BUS201907310012关于开发家庭终端调测费的需求
        /* if("79082818".equals(reqData.getSaleActiveId2()) || "2818".equals(reqData.getSaleActiveId2())) { otherTradeData.setRsrvStr2(String.valueOf(reqData.getSaleActiveFee2())); } else { otherTradeData.setRsrvStr2("0"); } */
        otherTradeData.setRsrvStr2("0");
        // BUS201907310012关于开发家庭终端调测费的需求

        // 申领模式 0租赁，1购买，2赠送,3自备
        otherTradeData.setRsrvTag1(reqData.getModemStyle());

        // --押金状态 0,押金、1,已沉淀、2已退还
        otherTradeData.setRsrvStr7("0");

        // 1:申领，2:更改，3:退还，4:丢失
        otherTradeData.setRsrvTag2("1");

        // 获取基本押金金额commpara表param_attr=6131
        IDataset paras = CommparaInfoQry.getCommparaAllCol("CSM", "6131", "2", "0898");

        String deposit = paras.getData(0).getString("PARA_CODE1");

        int modemDepositFee = Integer.parseInt(deposit);

        // 如果正式收取的光猫押金小于配置的原始光猫押金金额，则标记改用户是优惠租赁的光猫
        if (reqData.getModemDeposit() < modemDepositFee) {
            otherTradeData.setRsrvTag3("1");
        }

        if (reqData.getModemDeposit() > 0) {
            String tradeId = SeqMgr.getTradeId();

            // 账务扣减押金的流水
            otherTradeData.setRsrvStr8(tradeId);
        }

        // 业务类型 600:开户
        otherTradeData.setRsrvStr11("600");

        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(reqData.getUca().getSerialNumber(), otherTradeData);
    }
    
    /**
     * 生成平台服务other记录台账信息
     * @author lizj
     * @param btd
     * @throws Exception
     */
    private void createPlatSvcTradeData(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData,String type) throws Exception
    {
    	OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode(type);
        otherTradeData.setRsrvValue("平台服务信息");
        otherTradeData.setUserId(reqData.getNormalUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());
         //用户手机号码
        otherTradeData.setRsrvStr1(reqData.getNormalSerialNumber());
        //手机用户USER_ID
        otherTradeData.setRsrvStr2(reqData.getNormalUserId());
        //（平台服务serviceId）
        if("TOPSET_PLATSVC".equals(type)){
        	 otherTradeData.setRsrvStr3(reqData.getTopSetBoxPlatSvcPkgs());
        }else{
        	 otherTradeData.setRsrvStr3(reqData.getTopSetBoxPlatSvcPkgs2());
        }
        otherTradeData.setRsrvStr4("Y");
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        btd.add(reqData.getUca().getSerialNumber(), otherTradeData);
    }

    /**
     * 生成魔百和临时台账信息
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void createTopSetBoxTradeData(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("TOPSETBOX");
        otherTradeData.setRsrvValue("魔百和开户临时信息");
        otherTradeData.setUserId(reqData.getUca().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值

        // 用户手机号码
        otherTradeData.setRsrvStr1(reqData.getNormalSerialNumber());

        // 手机用户USER_ID
        otherTradeData.setRsrvStr2(reqData.getNormalUserId());

        // 魔百和产品ID
        otherTradeData.setRsrvStr3(reqData.getTopSetBoxProductId());

        // 魔百和 基础平台服务
        otherTradeData.setRsrvStr4(reqData.getTopSetBoxBasePkgs());

        // 魔百和可选平台服务
        otherTradeData.setRsrvStr5(reqData.getTopSetBoxOptionPkgs());

        if (reqData.getTopSetBoxDeposit() > 0) {
            // 魔百和押金
            otherTradeData.setRsrvStr6(String.valueOf(reqData.getTopSetBoxDeposit()));
        } else {
            // 魔百和押金
            otherTradeData.setRsrvStr6("0");
        }

        // BUS201907310012关于开发家庭终端调测费的需求
        otherTradeData.setRsrvStr6("0");
        /* if("79082918".equals(reqData.getTopSetBoxSaleActiveId2()) || "2918".equals(reqData.getTopSetBoxSaleActiveId2())) { otherTradeData.setRsrvStr6(String.valueOf(reqData.getTopSetBoxSaleActiveFee2())); } */
        // BUS201907310012关于开发家庭终端调测费的需求

        // 是否需要上门安装
        otherTradeData.setRsrvStr7(reqData.getArtificialServices());

        // 宽带详细地址
        otherTradeData.setRsrvStr21(reqData.getDetailAddress());

        // 标记是否生成 魔百和业务订单 0：未生成 Y：已生成 N:用户确认不开通,C 撤单返销
        otherTradeData.setRsrvTag1("0");

        // 是否有魔百和营销活动。1：有，0：没有
        if (StringUtils.isNotEmpty(reqData.getTopSetBoxSaleActiveId())) {
            otherTradeData.setRsrvTag2("1");
        } else {
            otherTradeData.setRsrvTag2("0");
        }

        // start-wangsc10-20190214 REQ201809040036+关于开通IPTV业务服务的需求
        IDataset topSetBoxDepositDataIPTV = CommparaInfoQry.getCommParas("CSM", "182", "600", reqData.getTopSetBoxProductId(), "0898");
        if (IDataUtil.isNotEmpty(topSetBoxDepositDataIPTV)) {
            String PARA_CODE2 = topSetBoxDepositDataIPTV.getData(0).getString("PARA_CODE2");
            if (PARA_CODE2 != null && !PARA_CODE2.equals("")) {
                if (PARA_CODE2.equals("IPTV")) {
                    otherTradeData.setRsrvStr24("IPTV_OPEN");// 600宽带开户加IPTV，告诉服开，这是IPTV开户
                }
            }
        }
        // end

        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(reqData.getUca().getSerialNumber(), otherTradeData);
    }

    /**
     * 生成IMS固话临时台账信息
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void createIMSTradeData(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("IMSTRADE");
        otherTradeData.setRsrvValue("IMS固话开户临时信息");
        otherTradeData.setUserId(reqData.getUca().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值

        // 用户手机号码
        otherTradeData.setRsrvStr1(reqData.getNormalSerialNumber());
        // 手机用户USER_ID
        otherTradeData.setRsrvStr2(reqData.getNormalUserId());
        // 魔百和产品ID
        otherTradeData.setRsrvStr3(reqData.getFixNumber());
        // IMS固话产品ID
        otherTradeData.setRsrvStr4(reqData.getImsProductId());
        // IMS固话产品名称
        otherTradeData.setRsrvStr5(UProductInfoQry.getProductNameByProductId(reqData.getImsProductId()));
        // 宽带产品名称
        otherTradeData.setRsrvStr6(reqData.getMainProduct().getProductName());
        // IMS固话营销活动产品ID
        otherTradeData.setRsrvStr7(reqData.getImsSaleActiveProductId());
        // IMS固话营销活动包ID
        otherTradeData.setRsrvStr8(reqData.getImsSaleActiveId());
        // IMS固话营销活动预存费用
        otherTradeData.setRsrvStr9(reqData.getImsSaleActiveFee());

        String imsPwd = StrUtil.getRandomNumAndChar(15);
        String encryptImsPwd = DESUtil.encrypt(imsPwd);// 加密。 服开再解密

        // IMS固话密码
        otherTradeData.setRsrvStr19(imsPwd);

        // IMS固话密码
        otherTradeData.setRsrvStr20(encryptImsPwd);

        // 标记是否生成 IMS固话工单 0：未生成 Y：已生成 N:用户确认不开通
        otherTradeData.setRsrvTag1("0");

        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);

        otherTradeData.setRsrvTag10(reqData.getIsTTtransfer());

        btd.add(reqData.getUca().getSerialNumber(), otherTradeData);

    }

    /**
     * 生成台帐付费表
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void createTradePayRelation(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {
        PayRelationTradeData payrelationTD = new PayRelationTradeData();
        payrelationTD.setUserId(reqData.getUca().getUserId());
        payrelationTD.setAcctId(reqData.getUca().getAcctId());
        payrelationTD.setPayitemCode("-1");
        payrelationTD.setAcctPriority("0");
        payrelationTD.setUserPriority("0");
        payrelationTD.setBindType("1");
        payrelationTD.setStartCycleId(SysDateMgr.getNowCycle());
        payrelationTD.setEndCycleId(SysDateMgr.getEndCycle20501231());
        payrelationTD.setActTag("1");
        payrelationTD.setDefaultTag("1");
        payrelationTD.setLimitType("0");
        payrelationTD.setLimit("0");
        payrelationTD.setComplementTag("0");
        payrelationTD.setInstId(SeqMgr.getInstId());
        payrelationTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(btd.getRD().getUca().getSerialNumber(), payrelationTD);
    }

    private void createTradeProduct(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(reqData.getUca().getUserId());
        productTD.setUserIdA("-1");
        productTD.setProductId(reqData.getMainProduct().getProductId());
        productTD.setProductMode(reqData.getMainProduct().getProductMode());
        productTD.setBrandCode(reqData.getMainProduct().getBrandCode());
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getOpenDate());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");

        btd.add(btd.getRD().getUca().getSerialNumber(), productTD);
    }

    /**
     * 虚拟用户产品台账拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createTradeProductVirtualUser(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData, String userId) throws Exception {
        ProductTradeData productTD = new ProductTradeData();
        productTD.setUserId(userId);
        productTD.setUserIdA("-1");
        productTD.setProductId(reqData.getMainProduct().getProductId());
        productTD.setProductMode(reqData.getMainProduct().getProductMode());
        productTD.setBrandCode(reqData.getMainProduct().getBrandCode());
        productTD.setInstId(SeqMgr.getInstId());
        productTD.setStartDate(reqData.getOpenDate());
        productTD.setEndDate(SysDateMgr.getTheLastTime());
        productTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        productTD.setMainTag("1");

        btd.add("KV_" + reqData.getNormalSerialNumber(), productTD);
    }

    /**
     * 用户关系台账表拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createTradeRelationUU(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {
        genTradeRelaInfoWide(reqData.getVirtualUserId(), reqData.getNormalUserId(), reqData.getNormalSerialNumber(), "1", "47", btd, reqData);
        genTradeRelaInfoWide(reqData.getVirtualUserId(), reqData.getUca().getUserId(), reqData.getUca().getSerialNumber(), "2", "47", btd, reqData);

        // 中小企业快速商务宽带创建集团与成员受理的UU关系
        if (StringUtils.isNotBlank(reqData.getEcSerialNumber()) && StringUtils.isNotBlank(reqData.getEcUserId())) {
            genTradeRelaInfoWide(reqData.getEcUserId(), reqData.getUca().getUserId(), reqData.getUca().getSerialNumber(), "2", "MR", btd, reqData);

        }

    }

    /**
     * 用户子台账表拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createTradeUser(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {
        UserTradeData userTD = reqData.getUca().getUser();
        String ori = "000000000" + reqData.getUca().getUserId();
        String ori1 = ori.substring(ori.length() - 9, ori.length());
        String pwd = Encryptor.fnEncrypt(reqData.getUserPasswd(), ori1);
        userTD.setUserPasswd(pwd);
        userTD.setOpenDate(reqData.getOpenDate());
        userTD.setInDate(reqData.getOpenDate());
        btd.add(btd.getRD().getUca().getSerialNumber(), userTD);
    }

    private void createTradeVirtualDiscnt(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {

        DiscntTradeData newDiscnt = new DiscntTradeData();
        newDiscnt.setUserId(reqData.getVirtualUserId());
        newDiscnt.setUserIdA("-1");
        newDiscnt.setProductId("-1");
        newDiscnt.setPackageId("-1");
        newDiscnt.setElementId(reqData.getLowDiscntCode());
        newDiscnt.setRelationTypeCode("47");
        newDiscnt.setInstId(SeqMgr.getInstId());
        newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
        newDiscnt.setSpecTag("2");
        newDiscnt.setStartDate(reqData.getOpenDate());
        newDiscnt.setEndDate(SysDateMgr.getTheLastTime());
        newDiscnt.setRemark("宽带保底优惠");
        btd.add("KV_" + reqData.getNormalSerialNumber(), newDiscnt);
    }

    /**
     * 虚拟用户子台账表拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createTradeVirtualUser(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {
        UserTradeData userTD = new UserTradeData();
        String ori = "000000000" + reqData.getVirtualUserId();
        String ori1 = ori.substring(ori.length() - 9, ori.length());
        String pwd = Encryptor.fnEncrypt(reqData.getUserPasswd(), ori1);

        userTD.setUserId(reqData.getVirtualUserId());
        userTD.setCustId(reqData.getUca().getCustomer().getCustId());
        userTD.setUsecustId(reqData.getUca().getCustomer().getCustId());
        userTD.setSerialNumber("KV_" + reqData.getNormalSerialNumber());
        userTD.setNetTypeCode(reqData.getUca().getUser().getNetTypeCode());
        userTD.setUserTypeCode(reqData.getUca().getUser().getUserTypeCode());
        userTD.setUserStateCodeset("0");
        userTD.setUserPasswd(pwd);
        userTD.setPrepayTag("0");
        userTD.setMputeMonthFee("0");
        userTD.setAcctTag("0");
        userTD.setDevelopCityCode(reqData.getUca().getUser().getCityCode());
        userTD.setDevelopDate(reqData.getOpenDate());
        userTD.setDevelopDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setDevelopEparchyCode(CSBizBean.getUserEparchyCode());
        userTD.setDevelopStaffId(CSBizBean.getVisit().getStaffId());
        userTD.setInDate(reqData.getOpenDate());
        userTD.setInStaffId(CSBizBean.getVisit().getStaffId());
        userTD.setInDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setOpenDepartId(CSBizBean.getVisit().getDepartId());
        userTD.setOpenStaffId(CSBizBean.getVisit().getStaffId());
        userTD.setOpenDate(reqData.getOpenDate());
        userTD.setOpenMode("0");
        userTD.setEparchyCode(CSBizBean.getUserEparchyCode());
        userTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        userTD.setCityCode(reqData.getUca().getUser().getCityCode());
        userTD.setRemoveTag("0");
        btd.add("KV_" + reqData.getNormalSerialNumber(), userTD);
    }

    /**
     * 用户宽带台帐拼串
     * 
     * @param btd
     * @param reqData
     * @throws Exception
     * @author yuyj3
     */
    private void createTradeWidenet(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {
        WideNetTradeData wtd = new WideNetTradeData();
        String ori = "000000000" + reqData.getUca().getUserId();
        String ori1 = ori.substring(ori.length() - 9, ori.length());
        String pwd = Encryptor.fnEncrypt(reqData.getUserPasswd(), ori1);
        wtd.setUserId(reqData.getUca().getUserId());
        wtd.setStandAddress(reqData.getStandAddress());
        wtd.setDetailAddress(reqData.getDetailAddress());
        wtd.setStandAddressCode(reqData.getStandAddressCode());
        wtd.setAcctPasswd(pwd);
        wtd.setContact(reqData.getContact());
        wtd.setContactPhone(reqData.getContactPhone());
        wtd.setPhone(reqData.getPhone());
        wtd.setInstId(SeqMgr.getInstId());
        wtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        wtd.setStartDate(reqData.getOpenDate());
        wtd.setEndDate(SysDateMgr.getTheLastTime());
        wtd.setSuggestDate(reqData.getSuggestDate());
        wtd.setRsrvStr1(DESUtil.encrypt(reqData.getUserPasswd()));
        wtd.setRsrvStr4(reqData.getAreaCode());
        wtd.setRsrvStr5(reqData.getPsptId());
        wtd.setRsrvStr2(reqData.getWideType());

        // 设备号
        wtd.setRsrvNum1(reqData.getDeviceId());

        if (StringUtils.isNotBlank(reqData.getModemStyle())) {
            wtd.setRsrvTag1("7");// 7代表铁通;
            wtd.setRsrvTag2(reqData.getModemStyle());// 铁通领用领用光猫
        }

        btd.add(btd.getRD().getUca().getSerialNumber(), wtd);
    }

    private void genTradeRelaInfoWide(String userIdVirtual, String userId, String serialNumber, String roleCodeB, String relationTypeCode, BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {

        RelationTradeData rtd = new RelationTradeData();

        rtd.setUserIdA(userIdVirtual);
        rtd.setUserIdB(userId);
        // 中小企业快速商务宽带 存集团的服务号码
        if ("MR".equals(relationTypeCode)) {
            rtd.setSerialNumberA(reqData.getNormalSerialNumber());
        } else {
            rtd.setSerialNumberA("-1");
        }
        rtd.setInstId(SeqMgr.getInstId());
        rtd.setModifyTag(BofConst.MODIFY_TAG_ADD);
        rtd.setRelationTypeCode(relationTypeCode);
        rtd.setSerialNumberB(serialNumber);
        rtd.setRoleCodeA("0");
        rtd.setRoleCodeB(roleCodeB);// 2表示副卡
        rtd.setOrderno("0");
        rtd.setStartDate(reqData.getOpenDate());
        rtd.setEndDate(SysDateMgr.getTheLastTime());
        btd.add(reqData.getUca().getUser().getSerialNumber(), rtd);
    }

    /**
     * 生成IMS固话临时客戶身份信息
     * 
     * @author yuyj3
     * @param btd
     * @throws Exception
     */
    private void createCustInfoTradeData(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("IMSCUSTINFO");
        otherTradeData.setRsrvValue("IMS固话开户临时客戶身份信息");
        otherTradeData.setUserId(reqData.getUca().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值

        otherTradeData.setRsrvStr1(reqData.getCustName());
        otherTradeData.setRsrvStr2(reqData.getPsptTypeCode());
        otherTradeData.setRsrvStr3(reqData.getCustPsptId());
        otherTradeData.setRsrvStr4(reqData.getPsptEndDate());
        otherTradeData.setRsrvStr5(reqData.getPsptAddr());
        otherTradeData.setRsrvStr6(reqData.getSex());
        otherTradeData.setRsrvStr7(reqData.getBirthday());
        otherTradeData.setRsrvStr8(reqData.getPostAddress());
        otherTradeData.setRsrvStr9(reqData.getPostCode());
        otherTradeData.setRsrvStr10(reqData.getCustPhone());
        otherTradeData.setRsrvStr11(reqData.getFaxNbr());
        otherTradeData.setRsrvStr12(reqData.getEmail());
        otherTradeData.setRsrvStr13(reqData.getHomeAddress());
        otherTradeData.setRsrvStr14(reqData.getWorkName());
        otherTradeData.setRsrvStr15(reqData.getWorkDepart());
        otherTradeData.setRsrvStr16(reqData.getCustContact());
        otherTradeData.setRsrvStr17(reqData.getCustContactPhone());
        otherTradeData.setRsrvStr18(reqData.getAgentCustName());
        otherTradeData.setRsrvStr19(reqData.getAgentPsptTypeCode());
        otherTradeData.setRsrvStr20(reqData.getAgentPsptId());
        otherTradeData.setRsrvStr21(reqData.getAgentPsptAddr());
        otherTradeData.setRsrvStr22(reqData.getLegalperson());
        otherTradeData.setRsrvStr23(reqData.getStartdate());
        otherTradeData.setRsrvStr24(reqData.getTermstartdate());
        otherTradeData.setRsrvStr25(reqData.getTermenddate());
        otherTradeData.setRsrvStr26(reqData.getCallingTypeCode());
        otherTradeData.setRsrvStr27(reqData.getRsrvStr5());
        otherTradeData.setRsrvStr28(reqData.getRsrvStr6());
        otherTradeData.setRsrvStr29(reqData.getRsrvStr7());
        otherTradeData.setRsrvStr30(reqData.getRsrvStr8());

        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        btd.add(reqData.getUca().getSerialNumber(), otherTradeData);

    }

    /**
     * REQ201810190032 和家固话开户界面增加实名制校验—BOSS侧 by mqx 20190108 单位证件开户权限判断
     */
    private void verifyOrganizationPriv(BusiTradeData btd) throws Exception {
        MergeWideUserCreateRequestData reqData = (MergeWideUserCreateRequestData) btd.getRD();
        String psptTypeCode = reqData.getPsptTypeCode();
        String staffId = getVisit().getStaffId();
        System.out.println("===mqx====psptTypeCode:" + psptTypeCode);

        IDataset organizationPsptTypes = CommparaInfoQry.getCommNetInfo("CSM", "2019", "VERIFY_ORGANIZATION_PSPT_TYPE_CODE");
        if (!StaffPrivUtil.isFuncDataPriv(staffId, "OP_ORGANIZATION_PRIV") && psptTypeCode != null) {
            if (IDataUtil.isNotEmpty(organizationPsptTypes)) {
                for (int i = 0; i < organizationPsptTypes.size(); i++) {
                    IData organizationPsptType = organizationPsptTypes.getData(i);
                    if (psptTypeCode.equals(organizationPsptType.getString("PARA_CODE1"))) {
                        CSAppException.appError("201901080", "该工号不具有和家固话单位证件开户权限（仅限自办厅工号申请）");
                    }
                }
            }
        }
    }

    private void createTradeExt(BusiTradeData<BaseTradeData> btd, MergeWideUserCreateRequestData reqData) throws Exception {

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
