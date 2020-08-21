package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AccountTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;

import java.util.ArrayList;
import java.util.List;

public class BuildMergeWideUserCreateReqData extends BaseBuilder implements IBuilder {

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {

        MergeWideUserCreateRequestData reqData = (MergeWideUserCreateRequestData) brd;
        reqData.setUserPasswd(param.getString("USER_PASSWD", "kd123456"));
        reqData.setPsptId(param.getString("WIDE_PSPT_ID"));
        reqData.setPhone(param.getString("PHONE"));
        reqData.setContact(param.getString("CONTACT"));
        reqData.setContactPhone(param.getString("CONTACT_PHONE"));
        reqData.setStandAddress(param.getString("STAND_ADDRESS"));
        reqData.setStandAddressCode(param.getString("STAND_ADDRESS_CODE"));
        reqData.setIsHGS(param.getString("HGS_WIDE", "0"));// 新增判断是否海工商宽带开户
        // 宽带开户支付模式：P：立即支付 A：先装后付
        reqData.setWidenetPayMode(param.getString("WIDENET_PAY_MODE", "P"));
        reqData.setIsTTtransfer(param.getString("TT_TRANSFER", ""));
        /**
         * REQ201609280017_家客资源管理-九级地址BOSS侧改造需求
         * 
         * @author zhuoyingzhi 20161102 FLOOR_AND_ROOM_NUM_FLAG 1 表示查询出来就是有楼层和房号 FLOOR_AND_ROOM_NUM_FLAG 0 表示查询出来就是无楼层和房号
         */
        if ("1".equals(param.getString("FLOOR_AND_ROOM_NUM_FLAG"))) {
            reqData.setDetailAddress(param.getString("DETAIL_ADDRESS"));
        } else {
            reqData.setDetailAddress(param.getString("DETAIL_ADDRESS") + param.getString("FLOOR_AND_ROOM_NUM", ""));
        }

        reqData.setAreaCode(param.getString("AREA_CODE"));
        reqData.setMainProduct(param.getString("WIDE_PRODUCT_ID"));
        reqData.setOpenDate(brd.getAcceptTime());
        reqData.setNormalUserId(param.getString("NORMAL_USER_ID"));
        reqData.setNormalSerialNumber(param.getString("SERIAL_NUMBER"));
        reqData.setVirtualUserId(SeqMgr.getUserId());
        reqData.setSuggestDate(param.getString("SUGGEST_DATE", ""));

        if ("3".equals(param.getString("WIDE_PRODUCT_TYPE", "")) || "5".equals(param.getString("WIDE_PRODUCT_TYPE", ""))) {
            reqData.setModemStyle("0");
        } else {
            reqData.setModemStyle(param.getString("MODEM_STYLE", ""));
        }

        if (StringUtils.isNotBlank(param.getString("MODEM_DEPOSIT", ""))) {
            reqData.setModemDeposit(Integer.valueOf(param.getString("MODEM_DEPOSIT")) * 100);
        } else {
            reqData.setModemDeposit(0);
        }

        reqData.setModemNumeric(param.getString("MODEM_NUMERIC_CODE", ""));
        reqData.setWideType(param.getString("WIDE_PRODUCT_TYPE", ""));
        reqData.setSaleActiveId(param.getString("SALE_ACTIVE_ID", ""));
        reqData.setSaleActiveIdAttr(param.getString("SALE_ACTIVE_IDATTR", ""));
        reqData.setTopSetBoxSaleActiveId(param.getString("TOP_SET_BOX_SALE_ACTIVE_ID", ""));
        reqData.setDeviceId(param.getString("DEVICE_ID", ""));
        reqData.setSaleActiveId2(param.getString("SALE_ACTIVE_ID2", ""));
        reqData.setTopSetBoxSaleActiveId2(param.getString("TOP_SET_BOX_SALE_ACTIVE_ID2", ""));

        if (StringUtils.isNotBlank(param.getString("FIX_NUMBER", ""))) {
            if (param.getString("FIX_NUMBER", "").startsWith("0898")) {
                reqData.setFixNumber(param.getString("FIX_NUMBER", ""));
            } else {
                reqData.setFixNumber("0898" + param.getString("FIX_NUMBER", ""));
            }

            reqData.setImsProductId(param.getString("IMS_PRODUCT_ID", ""));
            reqData.setImsSaleActiveId(param.getString("IMS_SALE_ACTIVE_ID", ""));
            reqData.setImsSaleActiveProductId(param.getString("IMS_SALE_ACTIVE_PRODUCT_ID", ""));
            reqData.setImsSaleActiveFee(param.getString("IMS_SALE_ACTIVE_FEE", "0"));
        }

        reqData.setHeMuSaleActiveId(param.getString("HEMU_SALE_ACTIVE_ID", ""));
        reqData.setHeMuSaleActiveProductId(param.getString("HEMU_SALE_ACTIVE_PRODUCT_ID", ""));
        reqData.setHeMuResId(param.getString("HEMU_RES_ID", ""));
        reqData.setHeMuSaleActiveFee(param.getString("HEMU_SALE_ACTIVE_FEE", "0"));

        brd.getUca().setProductId(param.getString("WIDE_PRODUCT_ID"));

        // 魔百和产品ID
        reqData.setTopSetBoxProductId(param.getString("TOP_SET_BOX_PRODUCT_ID", ""));

        // 魔百和 必选套餐
        reqData.setTopSetBoxBasePkgs(param.getString("BASE_PACKAGES", ""));

        // 魔百和 可选套餐
        reqData.setTopSetBoxOptionPkgs(param.getString("OPTION_PACKAGES", ""));
        
        // 魔百和 必选优惠
        reqData.setTopSetBoxPlatSvcPkgs(param.getString("PLATSVC_PACKAGES", ""));
        // 宽带可选优惠
        reqData.setTopSetBoxPlatSvcPkgs2(param.getString("PLATSVC_PACKAGES2", ""));

        if (StringUtils.isNotBlank(param.getString("TOP_SET_BOX_DEPOSIT", ""))) {
            // 魔百和押金
            reqData.setTopSetBoxDeposit(Integer.valueOf(param.getString("TOP_SET_BOX_DEPOSIT")) * 100);
        } else {
            reqData.setTopSetBoxDeposit(0);
        }

        if (StringUtils.isNotBlank(param.getString("SALE_ACTIVE_FEE", ""))) {
            // 营销活动预存
            reqData.setSaleActiveFee(param.getString("SALE_ACTIVE_FEE"));
        } else {
            reqData.setSaleActiveFee("0");
        }

        if (StringUtils.isNotBlank(param.getString("TOP_SET_BOX_SALE_ACTIVE_FEE", ""))) {
            // 魔百和营销活动预存
            reqData.setTopSetBoxSaleActiveFee(param.getString("TOP_SET_BOX_SALE_ACTIVE_FEE"));
        } else {
            reqData.setTopSetBoxSaleActiveFee("0");
        }
        // BUS201907310012关于开发家庭终端调测费的需求
        if (StringUtils.isNotBlank(param.getString("SALE_ACTIVE_FEE2", ""))) {
            // 营销活动预存
            reqData.setSaleActiveFee2(param.getString("SALE_ACTIVE_FEE2"));
        } else {
            reqData.setSaleActiveFee2("0");
        }

        if (StringUtils.isNotBlank(param.getString("TOP_SET_BOX_SALE_ACTIVE_FEE2", ""))) {
            // 魔百和营销活动预存
            reqData.setTopSetBoxSaleActiveFee2(param.getString("TOP_SET_BOX_SALE_ACTIVE_FEE2"));
        } else {
            reqData.setTopSetBoxSaleActiveFee2("0");
        }
        // BUS201907310012关于开发家庭终端调测费的需求

        // 宽带开户魔百和不需要上门服务
        reqData.setArtificialServices("0");

        brd.getUca().setBrandCode(reqData.getMainProduct().getBrandCode());

        String serialNumberGrp = param.getString("SERIAL_NUMBER");

        if (StringUtils.isNotBlank(serialNumberGrp)) {
            IData productInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumberGrp);
            if (IDataUtil.isNotEmpty(productInfo)) {
                // 如果是集团商务宽带用户，做一个标识
                if ("7341".equals(productInfo.getString("PRODUCT_ID"))) {
                    reqData.setUserPasswd("123456");
                    reqData.setRsrvStr10("BNBD");

                    reqData.setBusinessWide(true);
                    // 中小企业快速商务宽带创建集团与成员受理的UU关系，入参
                    reqData.setEcSerialNumber(param.getString("SERIAL_NUMBER", ""));
                    reqData.setEcUserId(param.getString("EC_USER_ID", ""));
                    reqData.setIbsysId(param.getString("IBSYSID", ""));
                    reqData.setNodeId(param.getString("NODE_ID", ""));
                    reqData.setRecordNum(param.getString("RECORD_NUM", ""));
                    reqData.setBusiformId(param.getString("BUSIFORM_ID", ""));
                }
            }
        }

        param.put("SERIAL_NUMBER", "KD_" + param.getString("WIDE_SERIAL_NUMBER"));

        // 针对信控统计，区分宽带类型
        // 虚拟优惠：GPON-5906；ADSL-5907；FTTH-5908; 校园宽带-5909
        if (StringUtils.equals("2", reqData.getWideType())) {
            // adsl
            reqData.setLowDiscntCode("5907");
        } else if (StringUtils.equals("3", reqData.getWideType())) {
            // 移动FTTH
            reqData.setLowDiscntCode("5908");
        } else if (StringUtils.equals("5", reqData.getWideType())) {
            // 铁通FTTH
            reqData.setLowDiscntCode("59072");
        } else if (StringUtils.equals("6", reqData.getWideType())) {
            // 铁通FTTB
            reqData.setLowDiscntCode("59071");
        } else {
            // 移动FTTB
            reqData.setLowDiscntCode("5906");
        }

        IDataset selectedElements = new DatasetList(param.getString("SELECTED_ELEMENTS"));
        if (IDataUtil.isNotEmpty(selectedElements)) {
            List<ProductModuleData> elements = new ArrayList<ProductModuleData>();
            int size = selectedElements.size();
            for (int i = 0; i < size; i++) {
                IData element = selectedElements.getData(i);

                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE"))) {
                    DiscntData discntData = new DiscntData(element);
                    discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    elements.add(discntData);

                } else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE"))) {
                    SvcData svcData = new SvcData(element);
                    svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    elements.add(svcData);

                }

            }

            reqData.setProductElements(elements);
        } else {
            CSAppException.apperr(CrmUserException.CRM_USER_1036);
        }
        // REQ201810190032++和家固话开户界面增加实名制校验—BOSS侧 by mqx
        if (StringUtils.isNotBlank(param.getString("FIX_NUMBER", ""))) {
            reqData.setCustName(param.getString("CUST_NAME"));
            reqData.setPsptTypeCode(param.getString("PSPT_TYPE_CODE"));
            reqData.setCustPsptId(param.getString("PSPT_ID"));
            reqData.setPsptEndDate(param.getString("PSPT_END_DATE", ""));
            reqData.setPsptAddr(param.getString("PSPT_ADDR", ""));
            reqData.setSex(param.getString("SEX", ""));
            reqData.setBirthday(param.getString("BIRTHDAY", ""));
            reqData.setPostAddress(param.getString("POST_ADDRESS"));
            reqData.setPostCode(param.getString("POST_CODE"));
            reqData.setCustPhone(param.getString("CUST_PHONE"));
            reqData.setFaxNbr(param.getString("FAX_NBR"));
            reqData.setEmail(param.getString("EMAIL"));
            reqData.setHomeAddress(param.getString("HOME_ADDRESS"));
            reqData.setWorkName(param.getString("WORK_NAME"));
            reqData.setWorkDepart(param.getString("WORK_DEPART"));
            reqData.setCustContact(param.getString("CUST_CONTACT"));
            reqData.setCustContactPhone(param.getString("CUST_CONTACT_PHONE"));
            reqData.setAgentCustName(param.getString("AGENT_CUST_NAME", ""));// 经办人名称
            reqData.setAgentPsptTypeCode(param.getString("AGENT_PSPT_TYPE_CODE", ""));// 经办人证件类型
            reqData.setAgentPsptId(param.getString("AGENT_PSPT_ID", ""));// 经办人证件号码
            reqData.setAgentPsptAddr(param.getString("AGENT_PSPT_ADDR", ""));// 经办人证件地址
            reqData.setLegalperson(param.getString("legalperson", ""));// 法人
            reqData.setStartdate(param.getString("startdate", ""));// 成立日期
            reqData.setTermstartdate(param.getString("termstartdate", ""));// 营业开始时间
            reqData.setTermenddate(param.getString("termenddate", ""));// 营业结束时间
            reqData.setCallingTypeCode(param.getString("CALLING_TYPE_CODE"));
            reqData.setRsrvStr5(param.getString("USE"));// 使用人姓名
            reqData.setRsrvStr6(param.getString("USE_PSPT_TYPE_CODE"));// 使用人证件类型
            reqData.setRsrvStr7(param.getString("USE_PSPT_ID"));// 使用人证件号码
            reqData.setRsrvStr8(param.getString("USE_PSPT_ADDR"));// 使用人证件地址
        }
    }

    public UcaData buildUcaData(IData param) throws Exception {
        UcaData uca = new UcaData();
        super.buildUcaData(param);
        UserTradeData userTradeData = DataBusManager.getDataBus().getUca(param.getString("SERIAL_NUMBER")).getUser();
        AccountTradeData acctTradeData = DataBusManager.getDataBus().getUca(param.getString("SERIAL_NUMBER")).getAccount();
        CustomerTradeData customerTradeData = DataBusManager.getDataBus().getUca(param.getString("SERIAL_NUMBER")).getCustomer();
        param.put("NORMAL_USER_ID", userTradeData.getUserId());
        IData userInfo = new DataMap();
        userInfo.put("USER_ID", SeqMgr.getUserId());
        userInfo.put("CUST_ID", customerTradeData.getCustId());
        userInfo.put("USECUST_ID", customerTradeData.getCustId());
        userInfo.put("SERIAL_NUMBER", "KD_" + param.getString("WIDE_SERIAL_NUMBER"));
        userInfo.put("NET_TYPE_CODE", userTradeData.getNetTypeCode());
        userInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        if ("1".equals(CSBizBean.getVisit().getInModeCode())) {
            userInfo.put("CITY_CODE", userTradeData.getCityCode());
        } else {
            userInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        }
        userInfo.put("MODIFY_TAG", "0");
        userInfo.put("USER_STATE_CODESET", "0");
        userInfo.put("USER_TYPE_CODE", StringUtils.isBlank(userTradeData.getUserTypeCode()) ? "0" : userTradeData.getUserTypeCode());
        userInfo.put("DEVELOP_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userInfo.put("DEVELOP_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("OPEN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userInfo.put("OPEN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("IN_STAFF_ID", CSBizBean.getVisit().getStaffId());
        userInfo.put("IN_DEPART_ID", CSBizBean.getVisit().getDepartId());
        userInfo.put("OPEN_MODE", "0");
        userInfo.put("ACCT_TAG", "0");
        userInfo.put("PREPAY_TAG", "0");
        userInfo.put("MPUTE_MONTH_FEE", "0");
        userInfo.put("CONTRACT_ID", "0");
        userInfo.put("REMOVE_TAG", "0");

        String userId = userTradeData.getUserId();

        if (StringUtils.isNotBlank(userId)) {
            IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(productInfo)) {
                // 如果是集团商务宽带用户，做一个标识
                if ("7341".equals(productInfo.getString("PRODUCT_ID"))) {
                    userInfo.put("RSRV_STR10", "BNBD");
                }
            }
        }

        uca.setUser(new UserTradeData(userInfo));
        uca.setAccount(acctTradeData);
        uca.setCustomer(customerTradeData);
        return uca;
    }

    protected void checkBefore(IData input, BaseReqData reqData) throws Exception {

        UcaData uca = super.buildUcaData(input);
        reqData.setUca(uca);
        super.checkBefore(input, reqData);
        reqData.setUca(DataBusManager.getDataBus().getUca("KD_" + input.getString("WIDE_SERIAL_NUMBER")));
    }

    public BaseReqData getBlankRequestDataInstance() {
        return new MergeWideUserCreateRequestData();
    }

}
