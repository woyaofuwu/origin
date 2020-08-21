package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.DiscntPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ProductPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.SvcPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SaleTerminalLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.FTTHModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo.QueryInfoSVC;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;

public class MergeWideUserCreateSVC extends CSBizService {
    private static final long serialVersionUID = 1L;

    public IData loadChildInfo(IData input) throws Exception {
        IData resultData = new DataMap();

        String serialNumber = input.getString("serialNumber");

        IDataset topSetBoxProducts = ProductInfoQry.queryTopSetBoxProducts("182", "600");

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isEmpty(userInfo)) {
            // 没有获取到有效的主号码信息！
            CSAppException.apperr(CrmUserException.CRM_USER_615);
        }

        IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userInfo.getString("USER_ID"));

        if (IDataUtil.isEmpty(productInfo)) {
            // 没有获取到有效的主产品信息！
            CSAppException.apperr(CrmUserException.CRM_USER_631);
        }

        // 判断是否是商务宽带开户逻辑
        if ("7341".equals(productInfo.getString("PRODUCT_ID"))) {
            resultData.put("IS_BUSINESS_WIDE", "Y");
        } else {
            resultData.put("IS_BUSINESS_WIDE", "N");
        }

        IDataset wideModemStyleDataset = StaticUtil.getStaticList("WIDE_MODEM_STYLE");
        // log.info("("*******cxy******wideModemStyleDataset="+wideModemStyleDataset);
        // 如果营业员没有 光猫自备模式权限，则过滤掉此模式

        // log.info("("*******cxy******WIDE_MODEM_STYLE_3#####="+StaffPrivUtil.isPriv(getVisit().getStaffId(), "WIDE_MODEM_STYLE_3","1"));
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "WIDE_MODEM_STYLE_3")) {
            // log.info("("*******cxy******WIDE_MODEM_STYLE_3="+StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "WIDE_MODEM_STYLE_3"));
            if (IDataUtil.isNotEmpty(wideModemStyleDataset)) {
                for (int i = 0; i < wideModemStyleDataset.size(); i++) {
                    if ("3".equals(wideModemStyleDataset.getData(i).getString("DATA_ID"))) {
                        wideModemStyleDataset.remove(i);
                        break;
                    }
                }
            }
        }
        // log.info("("*******cxy******wideModemStyleDataset2="+wideModemStyleDataset);
        // log.info("("*******cxy******FTTH_FREE_RIGHT###="+StaffPrivUtil.isPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT","1"));
        // 如果营业员没有“光猫赠送模式权限”，则过滤掉此模式
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT")) {
            // log.info("("*******cxy******FTTH_FREE_RIGHT="+StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT"));
            if (IDataUtil.isNotEmpty(wideModemStyleDataset)) {
                for (int j = 0; j < wideModemStyleDataset.size(); j++) {
                    if ("2".equals(wideModemStyleDataset.getData(j).getString("DATA_ID"))) {
                        wideModemStyleDataset.remove(j);
                        break;
                    }
                }
            }
        }

        // 付费模式权限控制
        IDataset widenetPayMode = StaticUtil.getStaticList("WIDENET_PAY_MODE");
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "WIDENET_PAY_MODE")) {
            // log.info("("*******cxy******FTTH_FREE_RIGHT="+StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT"));
            if (IDataUtil.isNotEmpty(widenetPayMode)) {
                for (int k = 0; k < widenetPayMode.size(); k++) {
                    if ("A".equals(widenetPayMode.getData(k).getString("DATA_ID"))) {
                        widenetPayMode.remove(k);
                        break;
                    }
                }
            }
        }

        // 宽带开户方式权限控制
        IDataset mergeWideUserStyleList = StaticUtil.getStaticList("HGS_WIDE");
        if (!StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "HGS_WIDE")) {
            // log.info("("*******cxy******FTTH_FREE_RIGHT="+StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT"));
            if (IDataUtil.isNotEmpty(mergeWideUserStyleList)) {
                for (int k = 0; k < mergeWideUserStyleList.size(); k++) {
                    if ("1".equals(mergeWideUserStyleList.getData(k).getString("DATA_ID"))) {
                        mergeWideUserStyleList.remove(k);
                        break;
                    }
                }
            }
        }

        // 魔百和个数申领个数列表
        IDataset topSetBoxNumList = new DatasetList();
        IData topSetBoxNum = null;
        String num = "5";
        IDataset topSetBoxNumDataset = ProductInfoQry.queryTopSetBoxProducts("182", "TOP_SET_BOX_NUM");
        if (IDataUtil.isNotEmpty(topSetBoxNumDataset)) {

            num = topSetBoxNumDataset.getData(0).getString("PARA_CODE1", "5");

        }
        for (int i = 1; i <= Integer.valueOf(num); i++)

        {
            topSetBoxNum = new DataMap();

            topSetBoxNum.put("DATA_NAME", i);

            topSetBoxNum.put("DATA_ID", i);

            topSetBoxNumList.add(topSetBoxNum);

        }
        resultData.put("TOP_SET_BOX_NUM", topSetBoxNumList);
        resultData.put("WIDE_MODEM_STYLE", wideModemStyleDataset);
        resultData.put("TOP_SET_BOX_PRODUCTS", topSetBoxProducts);
        resultData.put("WIDENET_PAY_MODE_LIST", widenetPayMode);
        resultData.put("MERGE_WIDE_LIST", mergeWideUserStyleList);
        return resultData;
    }

    /**
     * 根据宽带类型查询宽带产品
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset getWidenetProductInfoByWideType(IData input) throws Exception {
        String wideProductType = input.getString("wideProductType");

        String serialNumber = input.getString("SERIAL_NUMBER");

        if (StringUtils.isBlank(wideProductType)) {
            return null;
        }

        String productMode = "";

        if ("2".equals(wideProductType)) {
            // adsl
            productMode = "09";
        } else if ("3".equals(wideProductType)) {
            // FTTH
            productMode = "11";
        } else if ("5".equals(wideProductType)) {
            // 铁通FTTH，用移动FTTH的产品
            productMode = "11";
        } else if ("6".equals(wideProductType)) {
            // 铁通FTTB，用移动FTTB的产品
            productMode = "07";
        } else {
            // GPON
            productMode = "07";
        }

        IDataset productDataset = ProductInfoQry.getWidenetProductInfo(productMode, CSBizBean.getTradeEparchyCode());

        // add by zhangxing3 for REQ201704110012开发1000M宽带产品资费
        // isGigabit 表示端口是否支持1000M宽带，否-不支持；是-支持
        /* String isGigabit = input.getString("isGigabit",""); if("否".equals(isGigabit) || "".equals(isGigabit))//如果isGigabit = 否 或 空，表示端口不支持1000M宽带，需要从宽带产品中过滤掉 { productDataset = WideNetUtil.filterGigabitProduct(productDataset); } */
        // add by zhangxing3 for REQ201704110012开发1000M宽带产品资费 end

        // 中小企业快速商务宽带受理增加标记，不通过服务号码查询
        String flag = input.getString("FLAG", "");
        IDataset wideNetList = productDataset;
        if (!"MINOREC".equals(flag)) {
            wideNetList = WideNetUtil.filterBusinessProduct(serialNumber, productDataset);
        }

        return wideNetList;
    }

    /**
     * 根据宽带类型查询宽带营销活动
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset getSaleActiveByProductId(IData input) throws Exception {
        String productId = input.getString("productId");

        // add by zhangxing3 for REQ201808030011优化200M及以上的宽带产品业务流程 start
        /* String bandwidth = input.getString("bandwidth","0"); String new_rate = WideNetUtil.getWidenetProductRate(productId); System.out.println("--------------getSaleActiveByProductId---------------bandwidth:"+bandwidth+",new_rate:"+new_rate); if(
         * Integer.valueOf(new_rate)/1024 >= 200 && Integer.valueOf(new_rate) > (Integer.valueOf(bandwidth) * 1024)) { CSAppException.appError("-1", "您所选择的设备暂不支持"+Integer.valueOf(new_rate)/1024+"M产品"); } */
        // add by zhangxing3 for REQ201808030011优化200M及以上的宽带产品业务流程 end

        IDataset saleActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "600", productId);

        saleActiveList = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), saleActiveList);

        return saleActiveList;
    }

    /**
     * 查询终端调测费活动 add by zhangxing3 for BUS201907310012关于开发家庭终端调测费的需求
     */
    public IDataset getSaleActiveByParaCode1(IData input) throws Exception {
        String paraCode1 = input.getString("PARA_CODE1", "");
        String paramCode = input.getString("PARAM_CODE", "600");

        IDataset saleActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", paramCode, paraCode1);

        saleActiveList = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), saleActiveList);

        return saleActiveList;
    }

    /**
     * 根据宽带营销活动查询宽带附加活动
     * 
     * @param input
     * @return
     * @throws Exception
     * @author xuyt
     */
    public IDataset getSaleActiveByPackageId(IData input) throws Exception {
        String packageId = input.getString("PACKAGE_ID");
        String para_code1 = input.getString("PARA_CODE1");

        IDataset saleActiveList = CommparaInfoQry.getCommparaInfoByCode5("CSM", "178", "600", para_code1, packageId, null, "0898");

        return saleActiveList;
    }

    /**
     * 获得光猫费用，并校验现金余额是否足够
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkModemDeposit(IData input) throws Exception {
        IData rtnData = new DataMap();

        String saleActiveId = input.getString("saleActiveId");

        String serialNumber = input.getString("serialNumber");

        IData inParam = new DataMap();

        inParam.put("SERIAL_NUMBER", serialNumber);

        IDataset custModemInfo = new FTTHModemManageBean().getCustModermInfo(inParam);

        if (IDataUtil.isNotEmpty(custModemInfo)) {
            CSAppException.appError("-1", "该用户已经租赁或者赠送过光猫不能再次租赁或赠送！请选择自备模式办理；或者光猫管理将原有光猫进行处理后再次办理。");
        }

        // 获取用户是否有优惠租赁光猫的历史记录,不判断用户是否有优惠申领记录
        // IDataset discountModemInfo = FTTHModemManageBean.getUserOtherInfoByRsrvTag3(inParam);

        // 魔百和押金
        String topSetBoxDeposit = input.getString("TOP_SET_BOX_DEPOSIT", "");

        if (StringUtils.isBlank(topSetBoxDeposit)) {
            topSetBoxDeposit = "0";
        }

        int modemDepositFee = 0;

        // 默认需要交纳200押金
        String payType = "2";

        // 1.如果开户选择了营销活动，则只需要交纳100押金,有优惠租赁记录则不减免
        if (StringUtils.isNotEmpty(saleActiveId)) {
            payType = "1";
        }

        // 2.获取押金金额commpara表param_attr=6131
        IDataset paras = CommparaInfoQry.getCommparaAllCol("CSM", "6131", payType, "0898");

        String deposit = paras.getData(0).getString("PARA_CODE1");

        modemDepositFee = Integer.parseInt(deposit);

        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        String strCreditClass = uca.getUserCreditClass();

        if (StringUtils.isNotEmpty(strCreditClass)) {
            String tagCode = "CS_CLASSUSERMODEMFEEMITIGATE" + strCreditClass;

            // 星级客户光猫租金减免
            int modemFeeMitigate = 0;
            IDataset tagInfo = TagInfoQry.getTagInfosByTagCode(getTradeEparchyCode(), tagCode, "CSM", "0");

            if (IDataUtil.isNotEmpty(tagInfo)) {
                modemFeeMitigate = Integer.parseInt(tagInfo.getData(0).getString("TAG_NUMBER"));

                modemDepositFee = modemDepositFee - modemFeeMitigate;

                if (modemDepositFee < 0) {
                    modemDepositFee = 0;
                }
            }
        }

        // 用户余额校验，校验用户余额是否够交纳各类押金
        // rtnData = userBalanceCheck(serialNumber,Integer.valueOf(topSetBoxDeposit)*100, modemDepositFee);

        rtnData.put("MODEM_DEPOSIT", modemDepositFee / 100);
        rtnData.put("resultCode", "0");

        return rtnData;
    }

    /**
     * 根据宽带类型查询宽带产品
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkSaleActive(IData input) throws Exception {
        String isTopSetBoxSaleActive = input.getString("IS_TOP_SET_BOX_SALE_ACTIVE", "");

        // 如果是魔百和营销活动还需要做依赖宽带1+营销活动校验
        if ("Y".equals(isTopSetBoxSaleActive)) {
            String saleActiveId = input.getString("SALE_ACTIVE_ID", "");
            String topSetBoxSaleActiveId = input.getString("TOP_SET_BOX_SALE_ACTIVE_ID", "");

            // add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费（即度假宽带月、季、半年套餐）
            // 度假宽带活动与魔百和营销活动互斥
            // System.out.println("=============checkSaleActive================saleActiveId:"+saleActiveId);
            if (("113245".equals(saleActiveId) || "113257".equals(saleActiveId) || "113254".equals(saleActiveId) || "71450".equals(saleActiveId) || "71451".equals(saleActiveId)) && !"66000308".equals(input.getString("PRODUCT_ID", ""))) {
                CSAppException.appError("-1", "您已经选择了度假宽带活动，不能再办理该魔百和营销活动！");
            }
            // add by zhangxing3 for REQ201807310009有手机号码候鸟宽带资费（即度假宽带月、季、半年套餐）

            if (StringUtils.isNotBlank(topSetBoxSaleActiveId)) {
                IDataset topSetBoxCommparaInfos = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", topSetBoxSaleActiveId, "0898");

                if (IDataUtil.isNotEmpty(topSetBoxCommparaInfos)) {
                    IData topSetBoxCommparaInfo = topSetBoxCommparaInfos.first();

                    // 所依赖的宽带1+营销活动虚拟ID
                    String paraCode25 = topSetBoxCommparaInfo.getString("PARA_CODE25");

                    boolean flag = false;

                    // 不为空，则需要校验
                    if (StringUtils.isNotBlank(paraCode25)) {
                        if (StringUtils.isNotBlank(saleActiveId)) {
                            String paraCode25Array[] = paraCode25.split("\\|");

                            // 判断是否选中了依赖的1+营销活动
                            for (int i = 0; i < paraCode25Array.length; i++) {
                                if (saleActiveId.equals(paraCode25Array[i])) {
                                    flag = true;
                                    break;
                                }
                            }

                            if (!flag) {
                                CSAppException.appError("-1", "请先选择办理宽带1+营销活动才能办理该魔百和营销活动！");
                            }
                        } else {
                            CSAppException.appError("-1", "请先选择办理宽带1+营销活动才能办理该魔百和营销活动！");
                        }
                    }
                }
            }

        }

        String packageId = input.getString("PACKAGE_ID", "");
        // add by zhangxing3 for "REQ201807230001线上包年宽带打折套餐的需求"
        // System.out.println("=============choicePackageNode=========PACKAGE_ID:"+packageId);
        if ("84015243".equals(packageId) || "84015244".equals(packageId)) {
            checkSaleActiveStock(packageId);
        }
        // add by zhangxing3 for "REQ201807230001线上包年宽带打折套餐的需求"

        // 标记是宽带开户营销活动
        input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");

        // 预受理校验，不写台账
        input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
        input.put("TRADE_TYPE_CODE", "240");
        CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);

        return checkSelectedDiscnts(input);
    }

    /**
     * 校验营销包库存 zhangxing3
     */
    public void checkSaleActiveStock(String packageId) throws Exception {
        IDataset result = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
        // System.out.println("=============checkSaleActiveStock=========result:"+result);

        if (IDataUtil.isEmpty(result)) {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_23, packageId);
        }
        IData pkgExtInfo = result.getData(0);
        String condFactor3 = pkgExtInfo.getString("COND_FACTOR3");
        // System.out.println("=============checkSaleActiveStock=========condFactor3:"+condFactor3);

        if (StringUtils.isBlank(condFactor3)) {
            return;
        }

        if ("ZZZZ".equals(condFactor3)) {

        } else {
            result = ActiveStockInfoQry.queryByResKind(condFactor3, CSBizBean.getVisit().getStaffId(), CSBizBean.getVisit().getCityCode(), CSBizBean.getTradeEparchyCode());
            // System.out.println("=============checkSaleActiveStock=========result:"+result);

            if (IDataUtil.isEmpty(result)) {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_43);
            }
            IData activeStockInfo = result.getData(0);
            // System.out.println("=============checkSaleActiveStock=========activeStockInfo:"+activeStockInfo);

            int warnningValueU = activeStockInfo.getInt("WARNNING_VALUE_U");
            int warnningValueD = activeStockInfo.getInt("WARNNING_VALUE_D");
            if (warnningValueU >= warnningValueD) {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_44);
            }
        }
    }

    /**
     * 根据宽带类型查询宽带产品
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkSaleActiveAttr(IData input) throws Exception {

        IDataset saleActiveList = CommparaInfoQry.getCommparaInfoByCode5("CSM", "178", "600", null, null, input.getString("PARA_CODE11", ""), "0898");
        if (IDataUtil.isNotEmpty(saleActiveList)) {
            input.put("PRODUCT_ID", saleActiveList.getData(0).getString("PARA_CODE14"));
            input.put("PACKAGE_ID", saleActiveList.getData(0).getString("PARA_CODE15"));
            // 标记是宽带开户营销活动
            input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");

            // 预受理校验，不写台账
            input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
            input.put("TRADE_TYPE_CODE", "240");

            CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);

        }
        return input;
    }

    /**
     * 营销活动依赖校验
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkSaleActiveDependence(IData input) throws Exception {
        IData resultData = new DataMap();

        String resultCode = "0";

        String topSetBoxSaleActiveId = input.getString("TOP_SET_BOX_SALE_ACTIVE_ID", "");

        IDataset topSetBoxCommparaInfos = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", topSetBoxSaleActiveId, "0898");

        if (IDataUtil.isNotEmpty(topSetBoxCommparaInfos)) {
            IData topSetBoxCommparaInfo = topSetBoxCommparaInfos.first();

            // 所依赖的宽带1+营销活动虚拟ID
            String paraCode25 = topSetBoxCommparaInfo.getString("PARA_CODE25");

            // 不为空，则依赖于宽带营销活动
            if (StringUtils.isNotBlank(paraCode25)) {
                resultCode = "-1";
                // CSAppException.appError("-1", "魔百和营销活动依赖于当前取消的宽带1+营销活动，魔百和营销活动将同时被取消！");
            }
        }

        resultData.put("RESULT_CODE", resultCode);

        return resultData;
    }

    /**
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkTTWideNet(IData input) throws Exception {
        IData resultData = new DataMap();

        String strSn = input.getString("SERIAL_NUMBER");
        IDataset idsTTWideNet = UserInfoQry.getTTWideNetBySn(strSn);
        if (IDataUtil.isNotEmpty(idsTTWideNet)) {
            String strUserID = "";
            resultData.put("X_RESULTCODE", "0");
            resultData.put("X_RESULTINFO", "ok");
            for (int i = 0; i < idsTTWideNet.size(); i++) {
                IData idTTWideNet = idsTTWideNet.getData(i);
                String strRemoveTag = idTTWideNet.getString("REMOVE_TAG");
                if ("0".equals(strRemoveTag)) {
                    strUserID = idTTWideNet.getString("USER_ID");
                    resultData.put("TT_USER_ID", strUserID);
                    break;
                }
            }
            if (StringUtils.isBlank(strUserID)) {
                IData idTTWideNet = idsTTWideNet.first();
                strUserID = idTTWideNet.getString("USER_ID");
                resultData.put("TT_USER_ID", strUserID);
            }
        } else {
            resultData.put("X_RESULTCODE", "-1");
            resultData.put("X_RESULTINFO", "铁通号码填写错误，请重新填写");
        }

        return resultData;
    }

    /**
     * 校验用户选中的是否是包年优惠
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkSelectedDiscnts(IData input) throws Exception {
        IData resultData = new DataMap();

        // 用户已选择优惠ID
        String discntIds = input.getString("DISCNT_IDS");

        // 默认是校验通过
        String resultCode = "0";

        String discntCode = "";

        String discntName = "";

        if (StringUtils.isNotBlank(discntIds)) {
            // 查询出参数中配置的宽带包年优惠
            IDataset discntDataset = CommparaInfoQry.getCommNetInfo("CSM", "532", "600");

            if (IDataUtil.isNotEmpty(discntDataset)) {
                IData discntInfo = null;

                for (int i = 0; i < discntDataset.size(); i++) {
                    discntInfo = discntDataset.getData(i);

                    // 如果用户订购了宽带包年资费
                    if (discntIds.contains(discntInfo.getString("PARA_CODE1"))) {
                        resultCode = "-1";
                        discntCode = discntInfo.getString("PARA_CODE1");

                        break;
                    }
                }
            }

            // 查询出参数中配置的VIP体验套餐
            IDataset discntDataset2 = CommparaInfoQry.getCommNetInfo("CSM", "532", "699");

            if (IDataUtil.isNotEmpty(discntDataset2)) {
                IData discntInfo = null;

                for (int i = 0; i < discntDataset2.size(); i++) {
                    discntInfo = discntDataset2.getData(i);

                    // 如果用户订购了VIP体验套餐
                    if (discntIds.contains(discntInfo.getString("PARA_CODE1"))) {
                        resultCode = "-2";
                        discntCode = discntInfo.getString("PARA_CODE1");
                        if (StringUtils.isNotBlank(discntInfo.getString("PARA_CODE17"))) {
                            discntName = discntInfo.getString("PARA_CODE17");
                        }

                        break;
                    }
                }
            }

            // 查询出参数中配置的宽带包年优惠
            IDataset monthDiscntComms = CommparaInfoQry.getCommNetInfo("CSM", "532", "MONTH_DISCNT");

            if (IDataUtil.isNotEmpty(monthDiscntComms)) {
                IData monthDiscntComm = null;

                for (int i = 0; i < monthDiscntComms.size(); i++) {
                    monthDiscntComm = monthDiscntComms.getData(i);

                    // 如果用户订购了宽带包月资费
                    if (discntIds.contains(monthDiscntComm.getString("PARA_CODE1"))) {
                        resultCode = "-3";
                        discntCode = monthDiscntComm.getString("PARA_CODE1");

                        break;
                    }
                }
            }
        }

        resultData.put("resultCode", resultCode);
        resultData.put("DISCNT_CODE", discntCode);
        if (StringUtils.isNotBlank(discntName)) {
            resultData.put("DISCNT_NAME", discntName);
        }

        IDataset saleActiveAttrCommparaInfos = CommparaInfoQry.getCommparaInfoByCode5("CSM", "178", "600", null, input.getString("PACKAGE_ID", ""), null, "0898");
        if (IDataUtil.isNotEmpty(saleActiveAttrCommparaInfos)) {
            resultData.put("FLAG", "1");
        } else {
            resultData.put("FLAG", "0");
        }

        return resultData;
    }

    /**
     * 查询互联网电视机顶盒基础优惠包（0）和可选优惠包（2）
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData queryTopSetBoxDiscntPackagesByPID(IData input) throws Exception {
        IData retData = new DataMap();
        String topSetBoxProductId = input.getString("TOP_SET_BOX_PRODUCT_ID");

        String topSetBoxSaleActiveId = input.getString("TOP_SET_BOX_SALE_ACTIVE_ID");

        String serialNumber = input.getString("serialNumber");

        // 光猫押金
        String modemDeposit = input.getString("MODEM_DEPOSIT", "");

        if (StringUtils.isBlank(modemDeposit)) {
            modemDeposit = "0";
        }

        if (StringUtils.isNotBlank(topSetBoxProductId)) {
            // 获取费用信息
            String topSetBoxDeposit = "20000";

            IDataset topSetBoxDepositDatas = CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");

            if (IDataUtil.isNotEmpty(topSetBoxDepositDatas)) {
                topSetBoxDeposit = topSetBoxDepositDatas.getData(0).getString("PARA_CODE1", "20000");
            }
            
            String topSetBoxType="NO";
            // start-wangsc10-20181119 REQ201809040036+关于开通IPTV业务服务的需求
            String IPTV = "NO";
            IDataset topSetBoxDepositDataIPTV = CommparaInfoQry.getCommParas("CSM", "182", "600", topSetBoxProductId, "0898");
            if (IDataUtil.isNotEmpty(topSetBoxDepositDataIPTV)) {
                String PARA_CODE2 = topSetBoxDepositDataIPTV.getData(0).getString("PARA_CODE2");
                topSetBoxType=PARA_CODE2;
                if (PARA_CODE2 != null && !PARA_CODE2.equals("")) {
                    if (PARA_CODE2.equals("IPTV")) {
                        topSetBoxDeposit = "10000";
                        IPTV = "YES";
                    }
                }
            }
            // end

            // 如果选择了魔百和营销活动，则不需要缴纳魔百和押金
            if (StringUtils.isNotBlank(topSetBoxSaleActiveId)) {
                topSetBoxDeposit = "0";
            }

            // add by zhangxing3 for
            String saleActiveId = input.getString("SALE_ACTIVE_ID", "");
            // System.out.println("================queryTopSetBoxDiscntPackagesByPID================saleActiveId:"+saleActiveId);
            if (StringUtils.isNotBlank(saleActiveId) && ("113245".equals(saleActiveId) || "113254".equals(saleActiveId) || "113257".equals(saleActiveId) || "71450".equals(saleActiveId) || "71451".equals(saleActiveId))) {
                topSetBoxDeposit = "10000";
            }

            // 用户余额校验，校验用户余额是否够交纳各类押金 中间不需要校验，提交的时候一次性校验
            // retData = userBalanceCheck(serialNumber, Integer.valueOf(topSetBoxDeposit), Integer.valueOf(modemDeposit)*100);

            retData.put("resultCode", "0");

            // start-REQ201903010007增加IPTV业务办理条件限制-wangsc10-20190326
            retData.put("resultIPTVCode", "0");
            retData.put("resultIPTVInfo", "");
            String queryType = input.getString("QUERY_TYPE");
            IDataset isIPTV = CommparaInfoQry.getCommParas("CSM", "182", "IS_IPTV_TIP", topSetBoxProductId, "0898");
            if (IDataUtil.isNotEmpty(isIPTV) && (null == queryType || "".equals(queryType))) {
                String wideProductType = input.getString("WIDE_PRODUCT_TYPE", "");// 宽带类型
                String wideProductId = input.getString("WIDE_PRODUCT_ID", "");// 宽带产品档次

                if (null != wideProductType && !"".equals(wideProductType)) {
                    if (StringUtils.equals("1", wideProductType) || StringUtils.equals("6", wideProductType)) {
                        retData.put("resultIPTVCode", "-1");
                        retData.put("resultIPTVInfo", "您的宽带制式所限，目前无法办理魔百和直播电视业务，建议办理魔百和互联网电视业务！");
                    }
                } else {
                    retData.put("resultIPTVCode", "-1");
                    retData.put("resultIPTVInfo", "选择魔百和IPTV业务，请先选择宽带产品类型！");
                }
                if (null != wideProductId && !"".equals(wideProductId)) {

                    IDataset ftthkddc = CommparaInfoQry.getCommParas("CSM", "182", "KD_DC_50M", wideProductId, "0898");// 查询宽带FTTH档次是不是50M以下
                    if (IDataUtil.isNotEmpty(ftthkddc)) {
                        retData.put("resultIPTVCode", "-1");
                        retData.put("resultIPTVInfo", "您所办理的宽带业务网速太低，无法办理魔百和直播电视业务，请将宽带升档至50M及以上再办理！");
                    }
                } else {
                    retData.put("resultIPTVCode", "-1");
                    retData.put("resultIPTVInfo", "选择魔百和IPTV业务，请先选择宽带产品！");
                }
            }
            // end-wangsc10-20190326

            // 魔百和押金
            // BUS201907310012关于开发家庭终端调测费的需求
            // retData.put("TOP_SET_BOX_DEPOSIT", topSetBoxDeposit);
            retData.put("TOP_SET_BOX_DEPOSIT", "0");
            // BUS201907310012关于开发家庭终端调测费的需求

            IData topSetBoxPlatSvcPackages = PlatSvcInfoQry.queryDiscntPackagesByPID(topSetBoxProductId);

            // 基础服务包
            retData.put("B_P", topSetBoxPlatSvcPackages.getDataset("B_P"));

            // 可选服务包
            retData.put("O_P", topSetBoxPlatSvcPackages.getDataset("O_P"));
            
            //必选营销包TOPSET_TYPE
            IData data =new DataMap();
            data.put("TOPSET_TYPE", "1");
            data.put("PRODUCT_ID", topSetBoxProductId);
            IDataset platSvcPackages = CSAppCall.call("SS.InternetTvOpenIntfSVC.queryPlatSvc", data);
            if(IDataUtil.isNotEmpty(platSvcPackages)){
            	retData.put("P_P", platSvcPackages.first().getDataset("PLATSVC_INFO_LIST")); 
            }

            IDataset topSetBoxSaleActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "3800", "0");
            //REQ202003050012关于开发融合套餐增加魔百和业务优惠体验权益的需求 --魔百和和IPTV才显示本次需求新增活动
            input.put("SERIAL_NUMBER", input.getString("serialNumber"));
    		IData result = CSAppCall.call("SS.InternetTvOpenSVC.checkTopSetBox", input).first(); 
    		if("1".equals(result.getString("SALEACTIVE_TAG"))){
				retData.put("resultIPTVCode", "-1");
				retData.put("resultIPTVInfo", "用户已经订购了魔百和体验基础包，则不能办理第2条或第2条以上魔百和业务！");
			}
            IDataset topSetBoxSaleActiveList3 = new DatasetList();
            for(int i=0;i<topSetBoxSaleActiveList.size();i++){
            	String paraCode8 = topSetBoxSaleActiveList.getData(i).getString("PARA_CODE8");
            	if(StringUtils.isNotBlank(paraCode8)){
            		if(!topSetBoxType.equals(paraCode8)){
            			//2019年10月1日前从未办理过原价基础包且办理融合套餐费大于（含）58元的客户
                		if("1".equals(result.getString("COMMPARA_TAG"))&&"1".equals(result.getString("DATE_TAG"))&&!"1".equals(result.getString("PLATSVC_TAG"))){
                			topSetBoxSaleActiveList3.add(topSetBoxSaleActiveList.getData(i));
                		}
            		}
            		
            	}else{
            		topSetBoxSaleActiveList3.add(topSetBoxSaleActiveList.getData(i));
            	}
            }
            topSetBoxSaleActiveList=topSetBoxSaleActiveList3;
            
            // IDataset topSetBoxSaleActiveList = CommparaInfoQry.getCommparaInfoByCode2("CSM", "178", "3800", topSetBoxSaleActiveId, "0898");

            topSetBoxSaleActiveList = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), topSetBoxSaleActiveList);

            // 魔百和营销活动
            // retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", topSetBoxSaleActiveList);
            // start-wangsc10-20181119 REQ201809040036+关于开通IPTV业务服务的需求
            if (IPTV.equals("YES")) {

                // REQ201905080023关于将IPTV加入魔百和“移动电视尝鲜”活动的需求
                IDataset saleActiveListForIPTV = new DatasetList();
                if (IDataUtil.isNotEmpty(topSetBoxSaleActiveList)) {
                    for (int i = 0; i < topSetBoxSaleActiveList.size(); i++) {
                        String paraCode15 = topSetBoxSaleActiveList.getData(i).getString("PARA_CODE15", "");
                        if ("SUPPORT_IPTV".equals(paraCode15))// PARA_CODE15 = IPTV,标识该活动支持IPTV业务。
                        {
                            saleActiveListForIPTV.add(topSetBoxSaleActiveList.getData(i));
                        }
                    }
                }
                retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", saleActiveListForIPTV);
                // REQ201905080023关于将IPTV加入魔百和“移动电视尝鲜”活动的需求

                // start-REQ201903010007增加IPTV业务办理条件限制-wangsc10-20190326
                if (retData.getString("resultIPTVCode").equals("-1")) {
                    retData.put("B_P", new DataMap());
                    retData.put("O_P", new DataMap());
                    retData.put("TOP_SET_BOX_DEPOSIT", "0");
                }
                // end-wangsc10-20190326

            } else {
                retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", topSetBoxSaleActiveList);
            }

            // BUS201907310012关于开发家庭终端调测费的需求
            IDataset topSetBoxSaleActiveList2 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "3800", "TOPSETBOX");
            topSetBoxSaleActiveList2 = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), topSetBoxSaleActiveList2);
            retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST2", topSetBoxSaleActiveList2);
            // BUS201907310012关于开发家庭终端调测费的需求
            // end
        } else {
            retData.put("B_P", new DataMap());
            retData.put("O_P", new DataMap());
            retData.put("TOP_SET_BOX_DEPOSIT", "0");
            retData.put("TOP_SET_BOX_SALE_ACTIVE_LIST", new DataMap());
        }

        return retData;
    }

    /**
     * 用户余额校验，校验用户余额是否够交纳各类押金
     * 
     * @param serialNumber
     * @author yuyj3
     * @throws Exception
     */
    // private IData userBalanceCheck(String serialNumber, int topSetBoxDeposit, int modemDeposit) throws Exception
    // {
    // IData resultData = new DataMap();
    //
    // IData param=new DataMap();
    // param.put("SERIAL_NUMBER", serialNumber);
    //
    // //3、获取默认账户 （acct_id)
    // IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
    //
    // if (IDataUtil.isEmpty(accts))
    // {
    // resultData.put("resultCode", "-1");
    // resultData.put("resultInfo", "该服务号码默认账户信息不存在!");
    //
    // return resultData;
    // }
    //
    // String acctId=accts.getData(0).getString("ACCT_ID");
    // param.put("X_PAY_ACCT_ID", acctId);
    //
    // //4、调接口判断用户的现金是否足够，不够则提示缴费；调用接口
    // String cash = WideNetUtil.qryBalanceDepositBySn(serialNumber);
    //
    // int depositFee = topSetBoxDeposit + modemDeposit;
    //
    // if(Integer.parseInt(cash)<depositFee)
    // {
    // resultData.put("resultCode", "-1");
    // resultData.put("resultInfo", "账户存折可用余额不足，请先办理缴费。账户余额："+Double.parseDouble(cash)/100+"元，魔百和押金金额："+topSetBoxDeposit/100+"元,光猫押金金额："+modemDeposit/100+"元");
    // }
    // else
    // {
    // resultData.put("resultCode", "0");
    // }
    //
    // return resultData;
    // }

    /**
     * 营销活动费用校验 宽带开户营销活动及魔百和营销活动暂时只支持转账类的预存营销活动，如果配置的不是转账类的预存营销，则报错 营销活动转入转出存折走费用配置表TD_B_PRODUCT_TRADEFEE IN_DEPOSIT_CODE,OUT_DEPOSIT_CODE
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData queryCheckSaleActiveFee(IData param) throws Exception {
        IData result = new DataMap();
        int totalFee = 0;

        String activeFlag = param.getString("ACTIVE_FLAG"); // 活动标记，1：宽带营销活动，2：魔百和营销活动
        String productId = param.getString("PRODUCT_ID", "");
        String packageId = param.getString("PACKAGE_ID", "");

        // 查询营销包下面所有默认必选元素费用
        IDataset businessFee = WideNetUtil.getWideNetSaleAtiveTradeFee(productId, packageId);

        if (IDataUtil.isEmpty(businessFee)) {
            // 如果该营销活动未配置费用，则直接返回成功
            result.put("SALE_ACTIVE_FEE", "0");
            result.put("X_RESULTCODE", "0");
            return result;
        }

        for (int j = 0; j < businessFee.size(); j++) {
            IData feeData = businessFee.getData(j);

            String payMode = feeData.getString("PAY_MODE");
            String feeMode = feeData.getString("FEE_MODE");
            String fee = feeData.getString("FEE");

            if (fee != null && !"".equals(fee) && Integer.parseInt(fee) > 0) {
                if (!"1".equals(payMode)) {
                    // 付费模式非转账报错
                    String errorMsg = "";
                    if ("1".equals(activeFlag)) {
                        errorMsg = "营销包配置[" + packageId + "]错误，融合宽带营销活动付款模式暂时只支持转账类的营销活动!不支持[" + getPayModeName(payMode) + "]营销活动";
                    } else {
                        errorMsg = "营销包配置[" + packageId + "]错误，融合宽开户魔百和营销活动付款模式暂时只支持转账类的营销活动!不支持[" + getPayModeName(payMode) + "]营销活动";
                    }
                    CSAppException.appError("61312", errorMsg);
                }

                if (!"2".equals(feeMode)) {
                    // 费用类型非预存费报错
                    String errorMsg = "";
                    if ("1".equals(activeFlag)) {
                        errorMsg = "营销包配置[" + packageId + "]错误，融合宽开户营销活动费用类型暂时只支持预存费用的营销活动!不支持[" + ("2".equals(feeMode) ? "预存" : "1".equals(feeMode) ? "押金" : "营业费") + "]类型";
                    } else {
                        errorMsg = "营销包配置[" + packageId + "]错误，融合宽开户魔百和营销活动暂时只支持预存费用的营销活动!不支持[" + ("2".equals(feeMode) ? "预存" : "1".equals(feeMode) ? "押金" : "营业费") + "]类型";
                    }
                    CSAppException.appError("61313", errorMsg);
                }
            }

            totalFee += Integer.parseInt(fee);
        }

        result.put("SALE_ACTIVE_FEE", totalFee);
        result.put("X_RESULTCODE", "0");

        return result;
    }

    public String getPayModeName(String payMode) throws Exception {
        String payModeName = "";
        if (payMode == null || "".equals(payMode)) {
            payModeName = "未知类型";
            return payModeName;
        }
        if ("0".equals(payMode)) {
            payModeName = "现金";
        } else if ("1".equals(payMode)) {
            payModeName = "转账";
        } else if ("2".equals(payMode)) {
            payModeName = "可选现金、转账";
        } else if ("3".equals(payMode)) {
            payModeName = "清退";
        } else if ("4".equals(payMode)) {
            payModeName = "分期付款";
        }
        return payModeName;
    }

    /**
     * 提交前费用校验
     * 
     * @param cycle
     * @throws Exception
     * @author zhangyc5
     */
    public IData checkFeeBeforeSubmit(IData param) throws Exception {
        IData result = new DataMap();
        String modemDeposit = param.getString("MODEM_DEPOSIT", "0");
        String topSetBoxDeposit = param.getString("TOPSETBOX_DEPOSIT", "0");
        String saleActiveFee = param.getString("SALE_ACTIVE_FEE", "0");
        String topSetBoxSaleActiveFee = param.getString("TOPSETBOX_SALE_ACTIVE_FEE", "0");
        String heMuSaleActiveFee = param.getString("HEMU_SALE_ACTIVE_FEE", "0");
        String imsSaleActiveFee = param.getString("IMS_SALE_ACTIVE_FEE", "0");

        String serialNumber = param.getString("SERIAL_NUMBER", "");

        String leftFee = WideNetUtil.qryBalanceDepositBySn(serialNumber);
        // BUS201907310012关于开发家庭终端调测费的需求
        String saleActiveFee2 = param.getString("SALE_ACTIVE_FEE2", "0");
        String topSetBoxSaleActiveFee2 = param.getString("TOPSETBOX_SALE_ACTIVE_FEE2", "0");
        int allTotalTransFee = Integer.parseInt(modemDeposit) * 100 + Integer.parseInt(topSetBoxDeposit) * 100 + Integer.parseInt(saleActiveFee) + Integer.parseInt(imsSaleActiveFee) + Integer.parseInt(heMuSaleActiveFee)
                + Integer.parseInt(saleActiveFee2) + Integer.parseInt(topSetBoxSaleActiveFee2);
        if (Integer.parseInt(leftFee) < allTotalTransFee) {
            String errorMsg = "您的账户存折可用余额不足，请先办理缴费。本次需转出费用：[光猫调测费金额：" + Double.parseDouble(saleActiveFee2) / 100 + "元，魔百和调测费金额：" + Double.parseDouble(topSetBoxSaleActiveFee2) / 100 + "元,宽带营销活动费用金额：" + Double.parseDouble(saleActiveFee) / 100 + "元,"
                    + "魔百和营销活动费用金额:" + Double.parseDouble(topSetBoxSaleActiveFee) / 100 + "元";

            /* int allTotalTransFee = Integer.parseInt(modemDeposit)*100 + Integer.parseInt(topSetBoxDeposit)*100 + Integer.parseInt(saleActiveFee) + Integer.parseInt(imsSaleActiveFee) + Integer.parseInt(heMuSaleActiveFee);
             * 
             * if(Integer.parseInt(leftFee) < allTotalTransFee) { String errorMsg = "您的账户存折可用余额不足，请先办理缴费。本次需转出费用：[光猫押金金额：" + Double.parseDouble(modemDeposit)+"元，魔百和押金金额：" + Double.parseDouble(topSetBoxDeposit) + "元,宽带营销活动费用金额：" +
             * Double.parseDouble(saleActiveFee)/100+"元," + "魔百和营销活动费用金额:" + Double.parseDouble(topSetBoxSaleActiveFee)/100 + "元"; */
            // BUS201907310012关于开发家庭终端调测费的需求

            // 如果存在IMS固话费用跟和目费用
            if (Integer.parseInt(heMuSaleActiveFee) > 0 || Integer.parseInt(imsSaleActiveFee) > 0) {
                errorMsg = errorMsg + ",和目营销活动费用金额：" + Double.parseDouble(heMuSaleActiveFee) / 100 + "元,IMS固话营销动费用金额：" + Double.parseDouble(imsSaleActiveFee) / 100 + "元]";
            } else {
                errorMsg = errorMsg + "]";
            }

            CSAppException.appError("61314", errorMsg);
        }

        result.put("X_RESULTCODE", "0");

        return result;
    }

    /**
     * 根据宽带类型查询宽带产品
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset getIMSProductByType(IData input) throws Exception {
        String imsProductType = input.getString("IMS_PRODUCT_TYPE_CODE");

        if (StringUtils.isBlank(imsProductType)) {
            return null;
        }

        // IDataset imsProduct = UpcCall.qryOfferCatalogInfoByCatalogId(imsProductType,"","");
        IDataset imsProduct = UpcCall.qryOffersByCatalogId(imsProductType, null);
        // IDataset imsProducts = imsProduct.getDataset("OFFERS");
        IDataset imsProductNew = new DatasetList();
        // 将OFFER_CODE转换为PRODUCT_ID
        if (IDataUtil.isNotEmpty(imsProduct)) {
            for (int i = 0; i < imsProduct.size(); i++) {
                imsProduct.getData(i).put("PRODUCT_ID", imsProduct.getData(i).getString("OFFER_CODE"));
                if (!("84018059".equals(imsProduct.getData(i).getString("OFFER_CODE"))))// 智能音箱只能在家庭IMS固话开户(新)界面办理，屏蔽其他界面
                {
                    imsProductNew.add(imsProduct.getData(i));
                }
            }
        }
        // 产品权限控制
        ProductPrivUtil.filterProductListByPriv(getVisit().getStaffId(), imsProductNew);

        return imsProductNew;
    }

    /**
     * 获取和目营销活动列表
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset getHeMuSaleActive(IData input) throws Exception {
        String productId = input.getString("productId");

        IDataset saleActiveList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "178", "HEMU", productId);

        saleActiveList = WideNetUtil.filterWideSaleActiveListByPriv(getVisit().getStaffId(), saleActiveList);

        return saleActiveList;
    }

    /**
     * 和目营销活动校验
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkHeMuSaleActive(IData input) throws Exception {

        IDataset saleActiveList = CommparaInfoQry.getCommparaByParaCode("CSM", "178", "HEMU", null, null, input.getString("HEMU_SALE_ACTIVE_ID", ""), null);

        if (IDataUtil.isNotEmpty(saleActiveList)) {
            input.put("PRODUCT_ID", saleActiveList.getData(0).getString("PARA_CODE4"));
            input.put("PACKAGE_ID", saleActiveList.getData(0).getString("PARA_CODE5"));
            // 标记是宽带开户营销活动
            input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");

            // 预受理校验，不写台账
            input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
            input.put("TRADE_TYPE_CODE", "240");

            // CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);

        }

        return input;
    }

    /**
     * 和目营销活动校验
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData checkHeMuTerminal(IData input) throws Exception {
        String heMuResNo = input.getString("HEMU_RES_ID");
        String staffId = getVisit().getStaffId();
        String serialNumber = input.getString("SERIAL_NUMBER");
        String heMuSalePackageId = input.getString("HEMU_SALE_ACTIVE_ID");
        String heMuSaleProuctId = input.getString("HEMU_SALE_ACTIVE_PRODUCT_ID");

        IDataset hdhkActives = SaleActiveInfoQry.queryHdfkActivesByResNo(input.getString("RES_NO"));

        if (IDataUtil.isNotEmpty(hdhkActives)) {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_29);
        }

        IDataset terminalDataset = HwTerminalCall.getTerminalInfoByTerminalId(heMuResNo, staffId, serialNumber, "");

        IData terminalData = terminalDataset.getData(0);

        if (!"0".equals(terminalData.getString("X_RESULTCODE"))) {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_25, terminalData.getString("X_RESULTINFO"));
        }

        if (!"1".equals(terminalData.getString("TERMINAL_STATE"))) {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_39);
        }

        IData terminalLimit = SaleTerminalLimitInfoQry.queryByPK(heMuSaleProuctId, heMuSalePackageId, "0", terminalData.getString("DEVICE_MODEL_CODE"), "0898");

        if (IDataUtil.isEmpty(terminalLimit)) {
            CSAppException.appError("2018031501", "该终端型号与所选营销活动不匹配！");
        }

        return input;
    }

    /**
     * 获取魔百和押金
     */
    public IData getTopSetBoxDeposit(IData input) throws Exception {
        String topSetBoxSaleActiveId = input.getString("TOP_SET_BOX_SALE_ACTIVE_ID");

        // 默认是一个
        int topSetBoxNum = 1;
        String topSetBoxNumString = input.getString("TOP_SET_BOX_NUM", "");

        if (StringUtils.isNotBlank(topSetBoxNumString)) {
            topSetBoxNum = Integer.valueOf(topSetBoxNumString);
        }

        IData retData = new DataMap();

        // 获取费用信息
        String singleTopSetBoxDeposit = "20000";
        int topSetBoxDeposit = 0;

        IDataset topSetBoxDepositDatas = CommparaInfoQry.getCommNetInfo("CSM", "1626", "TOP_SET_BOX_MONEY");

        if (IDataUtil.isNotEmpty(topSetBoxDepositDatas)) {
            singleTopSetBoxDeposit = topSetBoxDepositDatas.getData(0).getString("PARA_CODE1", "20000");
        }

        // 如果选择了魔百和营销活动，可以减免掉一个魔百和的押金
        if (StringUtils.isNotBlank(topSetBoxSaleActiveId)) {
            topSetBoxDeposit = Integer.valueOf(singleTopSetBoxDeposit) * (topSetBoxNum - 1);
        } else {
            topSetBoxDeposit = Integer.valueOf(singleTopSetBoxDeposit) * topSetBoxNum;
        }

        // 魔百和押金
        retData.put("TOP_SET_BOX_DEPOSIT", topSetBoxDeposit);

        return retData;
    }

    public IData getTopSetBoxProducts(IData input) throws Exception {
        IData retData = new DataMap();
        IDataset topSetBoxProducts = ProductInfoQry.queryTopSetBoxProducts("182", "600");
        if (IDataUtil.isNotEmpty(topSetBoxProducts)) {
            retData.put("TOP_SET_BOX_PRODUCTS", topSetBoxProducts);
        }
        return retData;
    }

    /**
     * 宽带产品元素查询接口
     * 
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData qryProductElements(IData input) throws Exception {
        IData resultProductElements = new DataMap();

        IDataset productElements = new QueryInfoSVC().qryProductElementsInft(input);

        if (IDataUtil.isNotEmpty(productElements)) {
            DatasetList discntList = new DatasetList();
            DatasetList svcList = new DatasetList();

            IData productElement = null;

            for (int i = 0; i < productElements.size(); i++) {
                productElement = productElements.getData(i);

                if ("D".equals(productElement.getString("ELEMENT_TYPE_CODE"))) {
                    discntList.add(productElements.getData(i));
                } else {
                    svcList.add(productElements.getData(i));
                }
            }

            // 过滤掉没有权限的优惠
            DiscntPrivUtil.filterDiscntListByPriv(getVisit().getStaffId(), discntList);
            SvcPrivUtil.filterSvcListByPriv(getVisit().getStaffId(), svcList);

            resultProductElements.put("DISCNT_LIST", discntList);
            resultProductElements.put("SVC_LIST", svcList);
        }

        return resultProductElements;
    }

    /**
     * 校验赠送光猫FTTH
     * 
     * @author wangck3
     * @param param
     * @return
     * @throws Exception
     */
    public IData checkModemFTTH(IData param) throws Exception {
        IDataset result = ActiveStockInfoQry.queryByResKind("FTTH", CSBizBean.getVisit().getStaffId(), CSBizBean.getVisit().getCityCode(), CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isEmpty(result)) {
            return null;
        }
        return result.getData(0);
    }


    /**
     * 校验宽带开户或家庭产品开户的联系人电话
     * @author yanghb6
     * @param param
     * @return
     * @throws Exception
     */
    public IData checkContactPhone(IData param) throws Exception {
        IData retData = new DataMap();
        retData.put("IS_CHECK", "1");

        IDataset result = CommparaInfoQry.getCommNetInfo("CSM", "1927", "CONTACT_PHONE_CHECK");
        if (IDataUtil.isNotEmpty(result)) {
            String isCheck = result.getData(0).getString("PARA_CODE1", "0");
            if(StringUtils.equals("1", isCheck)) {
                String contactPhone = param.getString("CONTACT_PHONE");
                IData userInfo = UcaInfoQry.qryUserInfoBySn(contactPhone);
                if (IDataUtil.isEmpty(userInfo)) {
                    retData.put("IS_CHECK", "0");
                }else {
                    IData msisdnInfo = MsisdnInfoQry.getCrmMsisonBySerialnumberNew(contactPhone);
                    if (IDataUtil.isEmpty(msisdnInfo)) {
                        retData.put("IS_CHECK", "0");
                    }
                }
            }
        }
        return retData;
    }
}
