package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.group.esop.query.QuickOrderCondBean;

public class WideUserCreateBean extends CSBizBean {

    public IData checkSerialNumber(IData input) throws Exception {
        IData data = new DataMap();
        String mainSerialNumber = input.getString("SERIAL_NUMBER_A");
        String type = input.getString("PREWIDE_TYPE");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(mainSerialNumber);
        if (IDataUtil.isEmpty(userInfo)) {
            // 没有获取到有效的主号码信息！
            CSAppException.apperr(CrmUserException.CRM_USER_615);
        }
        // 查询主号码是否为宽带用户
        IDataset mainSet = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + mainSerialNumber);
        if (IDataUtil.isEmpty(mainSet)) {
            // 该主账号还没有办理过宽带业务！
            CSAppException.apperr(CrmUserException.CRM_USER_1067);
        } else {
            if (!mainSet.getData(0).getString("RSRV_STR2").equals("1")) {
                // 非gpon宽带不能办理
                CSAppException.apperr(WidenetException.CRM_WIDENET_11);
            }
        }

        IDataset dataUU = null;// 查询主账号下的子账号
        String roleCodeB = null;
        String userIdB = mainSet.getData(0).getString("USER_ID");
        // 判断主账号是否有家庭,平行的子账号
        if ("2".equals(type))// 平行
        {
            dataUU = RelaUUInfoQry.isMasterAccount(userIdB, "77");
            if (IDataUtil.isNotEmpty(dataUU)) {
                roleCodeB = dataUU.getData(0).getString("ROLE_CODE_B");
                if ("2".equals(roleCodeB)) {
                    String number = "";// 主账号号码
                    // 查询主账号下所有子账号
                    IDataset allAcct = RelaUUInfoQry.getAllSubAcct(userIdB, "77");
                    if (IDataUtil.isNotEmpty(allAcct)) {
                        number = allAcct.getData(0).getString("SERIAL_NUMBER_B").substring(3);

                    }

                    // 该账号为子账号,不能继续开通平行子账号.其主账号号码为:["+number+"]
                    CSAppException.apperr(CrmUserException.CRM_USER_1068, number);
                }
            }

            dataUU = RelaUUInfoQry.isMasterAccount(userIdB, "78");// 选择平行账号主账号下不能有家庭子账号
            if (IDataUtil.isNotEmpty(dataUU)) {
                // 此主账号存在家庭子账号，不能继续开通平行子账号!
                CSAppException.apperr(CrmUserException.CRM_USER_1069);
            }
        } else if ("1".equals(type))// 家庭
        {
            dataUU = RelaUUInfoQry.isMasterAccount(userIdB, "78");
            if (IDataUtil.isNotEmpty(dataUU)) {
                // 该账号不是普通账号,不能继续开通家庭子账号!
                CSAppException.apperr(CrmUserException.CRM_USER_1070);

            }
            dataUU = RelaUUInfoQry.isMasterAccount(userIdB, "77");
            if (IDataUtil.isNotEmpty(dataUU)) {
                // 该账号不是普通账号,不能继续开通家庭子账号!
                CSAppException.apperr(CrmUserException.CRM_USER_1070);

            }

        }
        // 判断主号码下面未完工单
        judgeMasterAccount(userIdB);

        IDataset resultList = CommparaInfoQry.getCommpara("CSM", "1900", null, CSBizBean.getTradeEparchyCode());// 配置参数表取限定个数
        if (IDataUtil.isEmpty(resultList)) {
            // 没有配置宽带子账号个数，请配置！
            CSAppException.apperr(CrmUserException.CRM_USER_1075);
        }
        int num = resultList.getData(0).getInt("PARA_CODE1");
        int numfamily = resultList.getData(0).getInt("PARA_CODE2");

        if ("2".equals(type))// 判断平行关系个数
        {
            IDataset ExituserB = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("77", userIdB, "1");
            if (ExituserB.size() > 0 && ExituserB != null) {// 根据虚拟的id捞取下列所有的副卡用户
                IDataset ExituserAll = RelaUUInfoQry.getSEL_USER_ROLEA("77", ExituserB.getData(0).getString("USER_ID_A"), "2");
                if (ExituserAll.size() > 0 && ExituserAll != null) {
                    // 平行账号只能有6个

                    if (num <= ExituserAll.size()) {
                        // 主账号已存在"+num+"个平行关系子账号，请换一个主账号
                        CSAppException.apperr(CrmUserException.CRM_USER_1076, num);
                    }

                }

            }
        }
        if ("1".equals(type)) {// 判断家庭关系个数
            IDataset ExitFauser = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("78", userIdB, "1");
            if (ExitFauser.size() > 0 && ExitFauser != null) {
                IDataset ExituserFaAll = RelaUUInfoQry.getSEL_USER_ROLEA("78", ExitFauser.getData(0).getString("USER_ID_A"), "2");
                if (ExituserFaAll.size() > 0 && ExituserFaAll != null) {
                    // 家庭只能有一个

                    if (numfamily <= ExituserFaAll.size()) {
                        // 主账号已存在"+numfamily+"个家庭关系子账号，请换一个主账号
                        CSAppException.apperr(CrmUserException.CRM_USER_1077, numfamily);
                    }

                }
            }
        }
        data.put("GPON_USER_ID", mainSet.getData(0).getString("USER_ID"));
        data.put("GPON_SERIAL_NUMBER", "KD_" + mainSerialNumber);

        IDataset allAcct = new DatasetList();
        IDataset relationInfos = RelaUUInfoQry.getAllSubAcct(userIdB, "77");

        if (relationInfos.size() > 0) {
            for (int i = 0, size = relationInfos.size(); i < size; i++) {
                IData relationInfo = relationInfos.getData(i);
                String userId = relationInfo.getString("USER_ID_B");
                String serialNumberB = relationInfo.getString("SERIAL_NUMBER_B");
                roleCodeB = relationInfo.getString("ROLE_CODE_B");
                IDataset svcStateInfos = UserSvcStateInfoQry.getUserValidSvcStateByUserId(userId);
                String stateNameStr = USvcStateInfoQry.qryStateNameBySvcIdStateCode("2010", svcStateInfos.getData(0).getString("STATE_CODE")).getData(0).getString("STATE_NAME");
                IData info = new DataMap();
                info.put("SERIAL_NUMBER", serialNumberB.substring(3));// 号码
                info.put("TYPE", roleCodeB.equals("1") ? "主账号" : "子账号");// 类型
                info.put("USER_STATE_CODESET", stateNameStr);// 主体服务状态
                allAcct.add(info);
            }

        } else {
            IDataset svcStateInfos = UserSvcStateInfoQry.getUserValidSvcStateByUserId(userIdB);
            String stateNameStr = USvcStateInfoQry.qryStateNameBySvcIdStateCode("2010", svcStateInfos.getData(0).getString("STATE_CODE")).getData(0).getString("STATE_NAME");
            IData info = new DataMap();
            info.put("SERIAL_NUMBER", mainSerialNumber);// 号码
            info.put("TYPE", "普通账号");// 类型
            info.put("USER_STATE_CODESET", stateNameStr);// 主体服务状态
            allAcct.add(info);
        }
        data.put("ALL_ACCT", allAcct);
        return data;
    }

    public IDataset getWidenetProductInfo(IData input) throws Exception {
        String productMode = "";
        if ("612".equals(input.getString("TRADE_TYPE_CODE"))) {
            productMode = "09";// adsl
        } else if ("613".equals(input.getString("TRADE_TYPE_CODE"))) {
            productMode = "11"; // FTTH
        } else if ("630".equals(input.getString("TRADE_TYPE_CODE")) || "650".equals(input.getString("TRADE_TYPE_CODE"))) {
            productMode = "13"; // 校园宽带
        } else {
            productMode = "07"; // GPON
        }
        return ProductInfoQry.getWidenetProductInfo(productMode, CSBizBean.getTradeEparchyCode());

    }

    /**
     * 判断主账号下面是否有未完工单
     * 
     * @param userId
     * @throws Exception
     */
    private void judgeMasterAccount(String userId) throws Exception {

        // 需要判断有没有未完工的子账号开户
        IDataset ExituserFamib = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("77", userId, "1");
        if (IDataUtil.isNotEmpty(ExituserFamib)) {
            IDataset ExituserFamiA = TradeInfoQry.getExistTrade("77", ExituserFamib.getData(0).getString("USER_ID_A"), "2");
            if (IDataUtil.isNotEmpty(ExituserFamiA)) {
                // 此主账号存在未完工的平行子账号开户，不能继续开通子账号！
                CSAppException.apperr(CrmUserException.CRM_USER_1072);
            }
        }

        // 没有子账号
        IDataset Exituser = TradeInfoQry.getExistUser("77", userId, "1");

        if (IDataUtil.isNotEmpty(Exituser)) {
            // 此主账号存在未完工的平行子账号开户，不能继续开通子账号！
            CSAppException.apperr(CrmUserException.CRM_USER_1072);
        }
        IDataset ExituserU = TradeInfoQry.getExistUser("78", userId, "1");
        if (IDataUtil.isNotEmpty(ExituserU)) {
            // 此主账号存在未完工的家庭子账号开户，不能继续开通子账号！
            CSAppException.apperr(CrmUserException.CRM_USER_1073);
        }
    }

    /**
     * 获取宽带开户的号码
     * 
     * @param serial_number
     * @throws Exception
     */
    public IData getWideSerialNumber(IData input) throws Exception {

        IData data = new DataMap();
        String wideSerialNumber = input.getString("SERIAL_NUMBER");
        String wideProductID = input.getString("WIDE_PRODUCT_ID");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(wideSerialNumber);
        if (IDataUtil.isEmpty(userInfo)) {
            // 没有获取到有效的主号码信息！
            CSAppException.apperr(CrmUserException.CRM_USER_615);
        }

        IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userInfo.getString("USER_ID"));
        if (IDataUtil.isEmpty(productInfo)) {
            // 没有获取到有效的主产品信息！
            CSAppException.apperr(CrmUserException.CRM_USER_631);
        }

        // 如果不是商务宽带用户，号码不变
        if (!"7341".equals(productInfo.getString("PRODUCT_ID"))) {
            data.put("WIDE_SERIAL_NUMBER", wideSerialNumber);
        }
        // 如果是商务宽带用户，号码后加4位数字，并且从0000开始递增
        else {
            IDataset maxNumberSet = null;// 获取集团商户宽带用户
            IDataset tradeMaxNumberSet = null;// 获取集团商户宽带用户

            String cust_id = userInfo.getString("CUST_ID");

            maxNumberSet = WidenetInfoQry.getMaxNetSN(cust_id, wideProductID, wideSerialNumber, null);

            tradeMaxNumberSet = WidenetInfoQry.getTradeMaxNetSN(cust_id, wideProductID, wideSerialNumber, null);

            if (IDataUtil.isNotEmpty(tradeMaxNumberSet)) {
                // 如果用户资料中最大号码记录为空
                if (IDataUtil.isEmpty(maxNumberSet)) {
                    maxNumberSet = tradeMaxNumberSet;
                } else {
                    String maxNumber = maxNumberSet.getData(0).getString("MAX_SERIAL_NUMBER", "");
                    String tradeMaxNumber = tradeMaxNumberSet.getData(0).getString("MAX_SERIAL_NUMBER", "");

                    // 如果台账中的最大号码大于资料表中的最大号码
                    if (tradeMaxNumber.compareTo(maxNumber) > 0) {
                        maxNumberSet = tradeMaxNumberSet;
                    }
                }

            }

            String strNewNumber = "";
            if (IDataUtil.isEmpty(maxNumberSet)) {
                // 该用户还没有办理过商务宽带业务！
                strNewNumber = wideSerialNumber + "0001";
            } else {
                // 办理过商务宽带，根据用户号码递增
                String maxNumber = maxNumberSet.getData(0).getString("MAX_SERIAL_NUMBER", "");

                // 再次判断用户没有办理过商务宽带业务！
                if (StringUtils.isBlank(maxNumber)) {
                    strNewNumber = wideSerialNumber + "0001";
                } else {
                    //
                    if (maxNumber.startsWith("KD_")) {
                        maxNumber = maxNumber.substring(3, maxNumber.length());
                    }

                    int num = Integer.parseInt(maxNumber.substring(maxNumber.length() - 4, maxNumber.length()));

                    num = num + 1;

                    if (num < 10000) {
                        strNewNumber = maxNumber.substring(0, maxNumber.length() - 4) + num;
                    }
                    if (num < 1000) {
                        strNewNumber = maxNumber.substring(0, maxNumber.length() - 4) + "0" + num;
                    }
                    if (num < 100) {
                        strNewNumber = maxNumber.substring(0, maxNumber.length() - 4) + "00" + num;
                    }
                    if (num < 10) {
                        strNewNumber = maxNumber.substring(0, maxNumber.length() - 4) + "000" + num;
                    }
                }
            }

            data.put("WIDE_SERIAL_NUMBER", strNewNumber);
        }
        return data;
    }

    /**
     * 获取批量宽带开户的号码
     * 
     * @param serial_number
     * @throws Exception
     */
    public IData getBatWideSerialNumber(IData input) throws Exception {

        IData data = new DataMap();
        String strWideSerialNumber = input.getString("SERIAL_NUMBER");
        String strWideProductID = input.getString("WIDE_PRODUCT_ID");
        int nWideNum = input.getInt("WIDE_NUM");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(strWideSerialNumber);
        if (IDataUtil.isEmpty(userInfo)) {
            // 没有获取到有效的主号码信息！
            CSAppException.apperr(CrmUserException.CRM_USER_615);
        }

        IData productInfo = UcaInfoQry.qryMainProdInfoByUserId(userInfo.getString("USER_ID"));
        if (IDataUtil.isEmpty(productInfo)) {
            // 没有获取到有效的主产品信息！
            CSAppException.apperr(CrmUserException.CRM_USER_631);
        }

        // 如果不是商务宽带用户，号码不变
        if (!"7341".equals(productInfo.getString("PRODUCT_ID"))) {
            data.put("WIDE_SERIAL_NUMBER", strWideSerialNumber);
        }
        // 如果是商务宽带用户，号码后加4位数字，并且从0000开始递增
        else {
            // IDataset maxNumberSet = null;// 获取集团商户宽带用户
            String cust_id = userInfo.getString("CUST_ID");

            IDataset maxNumberSet = WidenetInfoQry.getMaxNetSN(cust_id, strWideProductID, strWideSerialNumber, null);

            IDataset tradeMaxNumberSet = WidenetInfoQry.getTradeMaxNetSN(cust_id, strWideProductID, strWideSerialNumber, null);

            if (IDataUtil.isNotEmpty(tradeMaxNumberSet)) {
                // 如果用户资料中最大号码记录为空
                if (IDataUtil.isEmpty(maxNumberSet)) {
                    maxNumberSet = tradeMaxNumberSet;
                } else {
                    String maxNumber = maxNumberSet.getData(0).getString("MAX_SERIAL_NUMBER", "");
                    String tradeMaxNumber = tradeMaxNumberSet.getData(0).getString("MAX_SERIAL_NUMBER", "");

                    // 如果台账中的最大号码大于资料表中的最大号码
                    if (tradeMaxNumber.compareTo(maxNumber) > 0) {
                        maxNumberSet = tradeMaxNumberSet;
                    }
                }

            }

            String strNewNumber = "";
            if (IDataUtil.isEmpty(maxNumberSet) && nWideNum == 0) {
                // 该用户还没有办理过商务宽带业务！
                strNewNumber = strWideSerialNumber + "0001";
            } else {
                // 办理过商务宽带，根据用户号码递增
                String maxNumber = maxNumberSet.getData(0).getString("MAX_SERIAL_NUMBER", "");

                // 再次判断用户没有办理过商务宽带业务！
                if (StringUtils.isBlank(maxNumber)) {
                    strNewNumber = strWideSerialNumber + "0001";
                } else {
                    //
                    if (maxNumber.startsWith("KD_")) {
                        maxNumber = maxNumber.substring(3, maxNumber.length());
                    }

                    int num = Integer.parseInt(maxNumber.substring(maxNumber.length() - 4, maxNumber.length()));

                    num = num + 1 + nWideNum;

                    IDataset batMaxNumberSet = WidenetInfoQry.getBatMaxNetSN(strWideSerialNumber);
                    if (IDataUtil.isNotEmpty(batMaxNumberSet)) {
                        int nMaxNumber = batMaxNumberSet.first().getInt("CNT");
                        num = num + nMaxNumber;
                    }

                    if (num < 10000) {
                        strNewNumber = maxNumber.substring(0, maxNumber.length() - 4) + num;
                    }
                    if (num < 1000) {
                        strNewNumber = maxNumber.substring(0, maxNumber.length() - 4) + "0" + num;
                    }
                    if (num < 100) {
                        strNewNumber = maxNumber.substring(0, maxNumber.length() - 4) + "00" + num;
                    }
                    if (num < 10) {
                        strNewNumber = maxNumber.substring(0, maxNumber.length() - 4) + "000" + num;
                    }
                }
            }

            data.put("WIDE_SERIAL_NUMBER", strNewNumber);
        }
        return data;
    }

    /**
     * 获取宽带开户的号码
     * 
     * @param serial_number
     * @throws Exception
     */
    public IDataset getWideSerialNumberMinorec(IData input) throws Exception {

        IDataset setWideSerBerList = new DatasetList();

        String serialNumber = input.getString("SERIAL_NUMBER");
        String wideProductID = input.getString("WIDE_PRODUCT_ID");
        String wideSerialNumber = input.getString("WIDE_SERIAL_NUMBER", "");// 获取已有的最大号码
        String operType = input.getString("OPER_TYPE");// 获取操作类型
        String cust_id = input.getString("CUST_ID");//
        int serNumberSize = Integer.parseInt(input.getString("SERNUMBER_SIZE"));
        int wideNum = 0;
        if ("crtUs".equals(operType)) {
            setWideSerBerList = createAdderssSerNnmber(serNumberSize, wideNum, wideSerialNumber, serialNumber);// 生成宽带号码
        } else {// 变更逻辑
            // 如果是商务宽带用户，号码后加4位数字，并且从0000开始递增
            IDataset maxNumberSet = null;// 获取集团商户宽带用户资料
            IDataset tradeMaxNumberSet = null;// 获取集团商户宽带用户台账
            maxNumberSet = WidenetInfoQry.getMaxNetSN(cust_id, wideProductID, serialNumber, null);
            tradeMaxNumberSet = WidenetInfoQry.getTradeMaxNetSN(cust_id, wideProductID, serialNumber, null);
            IDataset quickorderCondList = QuickOrderCondBean.getMaxSerNumberSN(input);// 获取在途的最大号码

            if (StringUtils.isNotBlank(wideSerialNumber)) {// 去掉KD_开头，传过来的最大宽带号码
                if (wideSerialNumber.startsWith("KD_")) {
                    wideSerialNumber = wideSerialNumber.substring(3, wideSerialNumber.length());
                }
            }
            if (IDataUtil.isNotEmpty(quickorderCondList)) {// 比较在途表的宽带号码，赋值最大的
                String quickMaxNumber = quickorderCondList.first().getString("SERIAL_NUMBER", "");
                if (quickMaxNumber.compareTo(wideSerialNumber) > 0) {
                    wideSerialNumber = quickMaxNumber;
                }
            }
            if (IDataUtil.isNotEmpty(tradeMaxNumberSet)) {// 比较资料表的宽带号码，赋值最大的
                String tradeMaxNumber = tradeMaxNumberSet.first().getString("MAX_SERIAL_NUMBER", "");
                if (tradeMaxNumber.startsWith("KD_")) {
                    tradeMaxNumber = tradeMaxNumber.substring(3, tradeMaxNumber.length());
                }
                if (tradeMaxNumber.compareTo(wideSerialNumber) > 0) {
                    wideSerialNumber = tradeMaxNumber;
                }
            }
            if (IDataUtil.isNotEmpty(maxNumberSet)) {// 比较台账表的宽带号码，赋值最大的
                String maxNumber = maxNumberSet.first().getString("MAX_SERIAL_NUMBER", "");
                if (maxNumber.startsWith("KD_")) {
                    maxNumber = maxNumber.substring(3, maxNumber.length());
                }
                if (maxNumber.compareTo(wideSerialNumber) > 0) {
                    wideSerialNumber = maxNumber;
                }
            }
            setWideSerBerList = createAdderssSerNnmber(serNumberSize, wideNum, wideSerialNumber, serialNumber);// 生成宽带号码
        }
        return setWideSerBerList;
    }

    // 中小企业生成宽带号码
    public IDataset createAdderssSerNnmber(int serNumberSize, int wideNum, String wideSerialNumber, String serialNumber) throws Exception {
        IDataset setWideSerBerList = new DatasetList();
        if (StringUtils.isNotBlank(wideSerialNumber)) {
            wideNum = Integer.parseInt(wideSerialNumber.substring(wideSerialNumber.length() - 4, wideSerialNumber.length()));
        }
        for (int i = 1; i < serNumberSize + 1; i++) {
            IData datNumber = new DataMap();
            String serNumber = "";
            if (StringUtils.isBlank(wideSerialNumber)) {// 如果没有最大的号码，从0001开始新增
                if (10000 > i && 1000 <= i) {
                    serNumber = serialNumber + i;
                }
                if (1000 > i && 100 <= i) {
                    serNumber = serialNumber + "0" + i;
                }
                if (100 > i && 10 <= i) {
                    serNumber = serialNumber + "00" + i;
                }
                if (10 > i && 0 < i) {
                    serNumber = serialNumber + "000" + i;
                }
                datNumber.put("SERIAL_NUMBER", serNumber);
                setWideSerBerList.add(datNumber);
            } else {// 如果有最大的号码，取位数，每次加1
                wideNum = wideNum + 1;
                if (10000 > wideNum && 1000 <= wideNum) {
                    serNumber = serialNumber + wideNum;
                }
                if (1000 > wideNum && 100 <= wideNum) {
                    serNumber = serialNumber + "0" + wideNum;
                }
                if (100 > wideNum && 10 <= wideNum) {
                    serNumber = serialNumber + "00" + wideNum;
                }
                if (10 > wideNum && 0 < wideNum) {
                    serNumber = serialNumber + "000" + wideNum;
                }
                datNumber.put("SERIAL_NUMBER", serNumber);
                setWideSerBerList.add(datNumber);
            }
        }
        return setWideSerBerList;
    }
}
