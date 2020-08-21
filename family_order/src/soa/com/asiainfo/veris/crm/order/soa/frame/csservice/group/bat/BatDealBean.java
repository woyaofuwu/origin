
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat;

import net.sf.json.JSONArray;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.statement.Parameter;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.BatDealStateUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.BatBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpAllInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupDiversifyUtilBean;

public class BatDealBean extends BatBaseBean
{
	private String merchpSpecCode = "99903"; // 一点支付产品编码，IBOSS扫表时，标识业务
	
    public static String getBatDealDate() throws Exception
    {
    	 
    	 
        String nowDatetime = SysDateMgr.getSysTime();

        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.bat.nowrun", false); // 批量业务是否立即启动开关

        if (nowRunFlag)
        {
            return nowDatetime;
        }

        String todayDateTime07 = SysDateMgr.getAddHoursDate(SysDateMgr.getSysDate(), 7); // 早上7点
        String todayDateTime20 = SysDateMgr.getAddHoursDate(SysDateMgr.getSysDate(), 20); // 晚上20

        if (nowDatetime.compareTo(todayDateTime07) < 0)
        {
            return nowDatetime;
        }
        else if (nowDatetime.compareTo(todayDateTime07) > 0 && nowDatetime.compareTo(todayDateTime20) < 0)
        {
            return todayDateTime20;
        }
        else
        {
            return nowDatetime;
        }
    }

    /*
     * 集团客户一点支付生成附件名称
     */
    public static String getMemAttachFileName() throws Exception
    {
        return "A" + ProvinceUtil.getProvinceCodeGrpCorp() + SysDateMgr.getSysDate("yyyyMMddHHmmss") + "." + "000";
    }

    /**
     * 更新批量详情表
     * 
     * @param data
     * @throws Exception
     */
    public static void updateBatDealStateByBatchId(IData data) throws Exception
    {

        StringBuilder sql = new StringBuilder();

        sql.append(" UPDATE tf_b_trade_batdeal a");
        sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_time = SYSDATE");
        sql.append(" where 1=1");
        sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
        sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        sql.append(" and a.deal_state = '0'");

        Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * BBOSS成员批量（新增、变更、暂停、恢复、注销）
     * 
     * @param batch_id
     *            batchOperType
     * @throws Exception
     */
    public void startBatDealBBOSSOther(String batch_id, String batchOperType) throws Exception
    {
        IData data = new DataMap();

        String deal_state = null;
        // 批量默认启动状态，默认为1
        if (deal_state == null || deal_state.equals(""))
        {
            deal_state = BatDealStateUtils.DEAL_STATE_1;
        }

        data.put("BATCH_ID", batch_id);
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batch_id));
        IData batchdata = BatTradeInfoQry.qryTradeBatByPK(data);
        if (batchdata == null || batchdata.size() == 0)
        {
            CSAppException.apperr(BatException.CRM_BAT_35);
        }

        String batch_task_id = batchdata.getString("BATCH_TASK_ID");

        String condString = BatTradeInfoQry.getTaskCondString(batch_task_id);
        if (condString == null || condString.length() == 0)
        {
            CSAppException.apperr(BatException.CRM_BAT_11);
        }

        IData ddd = new DataMap(condString);

        // 转换需要认真测试
        JSONArray array = new JSONArray();
        array.element(condString);
        IDataset dataparas = new DatasetList(array.toString());
        if (IDataUtil.isEmpty(dataparas))
        {
            CSAppException.apperr(BatException.CRM_BAT_36);
        }

        // 产品ID
        String product_id = ddd.getString("PRODUCT_ID");
        String merchPCode = AttrBizInfoQry.getAttrValueBy1BAttrCodeObj(product_id, "PRO");
        // 商品ID
        String merchCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_F_POPRODUCT", new java.lang.String[]
        { "PRODUCTSPECNUMBER" }, "POSPECNUMBER", new java.lang.String[]
        { merchPCode });
        String merch_id = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new java.lang.String[]
        { "ID", "ID_TYPE", "ATTR_OBJ", "ATTR_VALUE" }, "ATTR_CODE", new java.lang.String[]
        { "1", "B", "PRO", merchCode });

        IData inmparm = new DataMap();
        // BBOSS的商品ID
        inmparm.put("MERCH_ID", merch_id);
        inmparm.put("PRODUCT_ID", product_id);

        // 查询BBOSS独立的产品属性表 2为成员属性
        IDataset param = BBossAttrQry.qryBBossAttrByPospecBiztype(merchPCode, "2");

        // 查找用户产品订单和订购关系
        IData datapara = (IData) dataparas.get(0);

        String puserId = datapara.getString("USER_ID");
        String groupId = datapara.getString("GROUP_ID");

        IDataset prodoff = UserGrpMerchpInfoQry.qryMerchpInfoByGroupIdMerchScProductScUserId(puserId, groupId, merchCode, merchPCode);

        if (prodoff == null || prodoff.size() == 0 || prodoff.size() > 1)
        {
            // 更改TF_B_TRADE_BATDEAL的状态标志为预处理失败 tf_b_trade_batdeal
            IData result = new DataMap();
            result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_B);
            result.put("DEAL_RESULT", "取集团用户[" + datapara.getString("GROUP_ID") + "]下产品[" + merchPCode + "]不存在有效订购关系.请与管理员联系");
            result.put("BATCH_ID", batch_id);

            this.updateBatDealByBatchIdSn(result);

            CSAppException.apperr(GrpException.CRM_GRP_401, datapara.getString("GROUP_ID"), merchPCode);

        }
        else if ("".equals(prodoff.getData(0).getString("PRODUCT_OFFER_ID", "")))
        {

            // 更改TF_B_TRADE_BATDEAL的状态标志为预处理失败 tf_b_trade_batdeal
            IData result = new DataMap();
            result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_B);
            result.put("DEAL_RESULT", "集团用户[" + datapara.getString("GROUP_ID") + "]下产品[" + merchPCode + "]的订购关系为空.请与管理员联系");
            result.put("BATCH_ID", batch_id);

            this.updateBatDealByBatchIdSn(result);

            CSAppException.apperr(GrpException.CRM_GRP_21, datapara.getString("GROUP_ID"), merchPCode);

        }

        String PRODUCT_OFFER_ID = prodoff.getData(0).getString("PRODUCT_OFFER_ID");

        IDataset memberDataset = BatTradeInfoQry.queryBBossMemberInfos(data, Route.getJourDb(Route.CONN_CRM_CG));
        if (IDataUtil.isEmpty(memberDataset))
        {
            CSAppException.apperr(GrpException.CRM_GRP_335);
        }

        // 是否允许外省成员
        boolean allowExtMeb = false;
        IDataset configs = AttrBizInfoQry.getBizAttr("1", "B", "CanExtMeb", product_id, null);
        // 允许添加外省号码
        if (null != configs && configs.size() > 0)
        {
            allowExtMeb = true;
        }

        // 是否允许加携入号码
        boolean allowNp = false;
        IDataset configsNp = AttrBizInfoQry.getBizAttr("1", "B", "CanNp", product_id, null);
        // 允许添加外省号码
        if (null != configsNp && configsNp.size() > 0)
        {
            allowNp = true;
        }

        IData baseInfo = new DataMap();
        for (int k = 0; k < memberDataset.size(); k++)
        {

            String memberType = ((IData) memberDataset.get(k)).getString("DATA19");
            // 转换成员类型参数 取成员参数比较
            String sn = ((IData) memberDataset.get(k)).getString("SERIAL_NUMBER");

            try
            {
                boolean isChinaMobileNumber = false;
                String SERIAL_NUMBER = memberDataset.getData(k).getString("SERIAL_NUMBER", "");
                IData snInfo = MsisdnInfoQry.getMsisonBySerialnumber(SERIAL_NUMBER, null);
                if (snInfo != null && "1".equals(snInfo.getString("ASP")))
                {
                    isChinaMobileNumber = true;
                }
                else if (allowNp)
                {
                    // 全网MAS允许加携转号码 isChinaMobileNumber 标识为移动号码或携入移动的其他运营商号码
                    IData userNp = new DataMap();
                    IDataset userNpAll = UserNpAllInfoQry.queryUserNpAll(sn);
                    if (IDataUtil.isNotEmpty(userNpAll))
                    {
                        userNp.clear();
                        userNp = userNpAll.getData(0);
                        // 002标示运营商为移动
                        if ("002".equals(userNp.getString("PORT_IN_NETID").substring(0, 3)))
                        {
                            isChinaMobileNumber = true;
                        }
                        else
                        {
                            isChinaMobileNumber = false;
                        }
                    }
                }

                if (!isChinaMobileNumber)
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_617, sn);
                }

                // 从本省号码表查号码
                // 海南特有，查询TD_M_MSISDN 信息
                IDataset mofficeInfo = MsisdnInfoQry.getMsdnBySn(sn);

                // 获取号码默认地州
                String defaultEparchyCode = RouteInfoQry.getEparchyCodeBySnForCrm(sn);
                // 是否外省成员
                boolean isExtMebUser = false;
                // 在本省号码表没找到数据，如果允许添加外省号码，则去遍历地州号码表 以防出现遗漏
                if (IDataUtil.isEmpty(mofficeInfo))
                {
                    if (allowExtMeb)
                    {
                        String[] connNames = Route.getAllCrmDb();

                        if (connNames == null)
                        {
                            break;
                        }
                        for (int i = 0, len = connNames.length; i < len; i++)
                        {
                            String connName = connNames[i];
                            if (connName.indexOf("crm") >= 0)
                            {
                                // 从crm库查询号码信息
                                mofficeInfo = UserInfoQry.getUserInfoBySn(sn, "0", "06", connName);
                                if (IDataUtil.isNotEmpty(mofficeInfo))
                                {
                                    // 已经存在这个号码
                                    isExtMebUser = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                // 如果用户资料不存在，而且不是新增操作，并且不允许外省成员，直接记录错误
                if (IDataUtil.isEmpty(mofficeInfo) && !(batchOperType.equals("BATADDBBOSSMEMBER") && (allowExtMeb || allowNp)))
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_112, sn);
                }

                // 不是新增时需判断成员的订购关系是否存在
                // 新增时 判断订购关系是否 重复
                if (!batchOperType.equals("BATADDBBOSSMEMBER") || (batchOperType.equals("BATADDBBOSSMEMBER") && IDataUtil.isNotEmpty(mofficeInfo)))
                {
                    CSBizBean.getVisit().setStaffEparchyCode(defaultEparchyCode);

                    // 查询用户信息
                    String net_type_code = "";

                    if (isExtMebUser)
                    {
                        net_type_code = "06";
                    }
                    else
                    {
                        net_type_code = "00";
                    }

                    IDataset userInfos = UserInfoQry.getUserInfoBySn(sn, "0", net_type_code, CSBizBean.getTradeEparchyCode());
                    if (IDataUtil.isEmpty(userInfos))
                    {
                        IData result = new DataMap();
                        result.put("BATCH_ID", batch_id);
                        result.put("SERIAL_NUMBER", sn);
                        result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_B);
                        result.put("DEAL_RESULT", "根据成员手机号码" + sn + "查询用户信息无资料!");
                        updateBatDealByBatchIdSn(result);
                        continue;
                    }

                    String userId = userInfos.getData(0).getString("USER_ID");
                    String grpUserId = datapara.getString("USER_ID");

                    IDataset datas = UserGrpMerchMebInfoQry.getSEL_BY_USERID_USERIDA(userId, grpUserId, defaultEparchyCode);

                    if (IDataUtil.isEmpty(datas) && !batchOperType.equals("BATADDBBOSSMEMBER"))
                    {
                        IData result = new DataMap();
                        result.put("BATCH_ID", batch_id);
                        result.put("SERIAL_NUMBER", sn);
                        result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_B);
                        result.put("DEAL_RESULT", "未找到BBOSS成员订购产品信息,不能进行新增之外的操作！");
                        updateBatDealByBatchIdSn(result);

                        CSAppException.apperr(ProductException.CRM_PRODUCT_156);
                    }
                    else if ((datas != null && datas.size() > 0) && batchOperType.equals("BATADDBBOSSMEMBER"))
                    {
                        IData result = new DataMap();
                        result.put("BATCH_ID", batch_id);
                        result.put("SERIAL_NUMBER", sn);
                        result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_B);
                        result.put("DEAL_RESULT", "已经存在BBOSS成员订购产品信息,不能进行新增操作！");
                        updateBatDealByBatchIdSn(result);

                        CSAppException.apperr(ProductException.CRM_PRODUCT_150);

                    }
                }

                // 分散逻辑 start
                IDataset mebUserInfos = MsisdnInfoQry.getMsdnBySn(sn);
                if (IDataUtil.isNotEmpty(mebUserInfos))
                {
                    String mebUserId = mebUserInfos.getData(0).getString("USER_ID");
                    // 判断商品是否必须要求为自然月账期
                    boolean ifMerchNatureDay = GroupDiversifyUtilBean.getNatureProductTag(merch_id);
                    // 判断产品是否必须要求为自然月账期
                    boolean ifProductNatrueDay = !GroupDiversifyUtilBean.getSpecialImmeProductTag(product_id);

                    // 成员用户账期判断
                    if (ifMerchNatureDay || ifProductNatrueDay)
                    {
                        IData mebUserAcctDay = AcctDayDateUtil.getUserAcctDayAndFirstDateInfo(mebUserId, CSBizBean.getTradeEparchyCode());
                        if (mebUserAcctDay != null && mebUserAcctDay.size() > 0)
                        {
                            // 非自然月账期的情况
                            if (!DiversifyAcctUtil.checkUserAcctDay(mebUserAcctDay, "1", false))
                            {
                                String diverDistribute = DiversifyAcctUtil.userAcctDayDistribution(mebUserAcctDay, "1");// 成员账期分布

                                if (diverDistribute.equals(GroupBaseConst.UserDaysDistribute.FALSE_TRUE.getValue()))
                                {
                                    IData result = new DataMap();
                                    result.put("BATCH_ID", batch_id);
                                    result.put("SERIAL_NUMBER", sn);
                                    result.put("DEAL_STATE", "7");
                                    result.put("DEAL_RESULT", "当前用户的出账日不是1号,不允许办理业务,请在集团产品成员新增页面受理!");
                                    updateBatDealByBatchIdSn(result);
                                    continue;
                                }
                                else
                                {
                                    IData result = new DataMap();
                                    result.put("BATCH_ID", batch_id);
                                    result.put("SERIAL_NUMBER", sn);
                                    result.put("DEAL_STATE", "7");
                                    result.put("DEAL_RESULT", "当前用户的出账日不是1号,不允许办理业务,须将账期改为自然月才可办理改业务!");
                                    updateBatDealByBatchIdSn(result);
                                    continue;
                                }
                            }
                        }
                    }
                }
                // 分散逻辑 end

                baseInfo.put("SRC", "1");
                baseInfo.put("MODIFY_TAG", "0");
                baseInfo.put("OPER_FLAG", "1");
                baseInfo.put("RSRV_STR10", "2");
                baseInfo.put("IBSYSID", SeqMgr.getUipSysId());
                baseInfo.put("PKGSEQ", SeqMgr.getTradeId());

                baseInfo.put("BIPCODE", "BIP4B257");
                baseInfo.put("ACTIVITYCODE", "T4101034");
                baseInfo.put("ORDERNUMBER", "");// 订单编码
                baseInfo.put("PRODUCTID", PRODUCT_OFFER_ID);// 订购关系id
                baseInfo.put("ORDERSOURCE", "0"); // 0- BBOSS受理 1-省BOSS上传 2- EC上传

                baseInfo.put("MEMBERNUMBER", sn);// 成员号码
                if (batchOperType.equals("BATADDBBOSSMEMBER"))
                {
                    baseInfo.put("OPER_ACTION", "1");// 操作类型新增
                }
                else if (batchOperType.equals("BATDELBBOSSMEMBER"))
                {
                    baseInfo.put("OPER_ACTION", "0");// 操作类型删除
                }
                else if (batchOperType.equals("BATCONBBOSSMEMBER"))
                {
                    baseInfo.put("OPER_ACTION", "4");// 操作类型恢复
                }
                else if (batchOperType.equals("BATPASBBOSSMEMBER"))
                {
                    baseInfo.put("OPER_ACTION", "3");// 操作类型暂停
                }
                else if (batchOperType.equals("BATMODBBOSSMEMBER"))
                {
                    baseInfo.put("OPER_ACTION", "6");// 操作类型变更
                }

                baseInfo.put("MEMBERTYPEID", memberType);// 成员类型
                baseInfo.put("EFFDATE", SysDateMgr.getSysTime());// 期望生效时间
                baseInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());// 更新时间
                baseInfo.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                baseInfo.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
                baseInfo.put("TRADE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
                baseInfo.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                baseInfo.put("RSRV_STR9", batch_id);// 插入bath_id，给一级BBOSS归档调用，更改状态标志
                baseInfo.put("DAY", SysDateMgr.getCurDay());// 插入DAY字段

                // 插入到主表
                Dao.insert("TI_B_BBOSSMEMBERDATA_UDR", baseInfo, Route.CONN_CRM_CEN);

                // 查数据到明细表 读取批量表的参数数据
                IDataset subData = new DatasetList();
                for (int j = 0, pSize = param.size(); j < pSize; j++)
                {

                    String attr_code = param.getData(j).getString("ATTR_CODE");
                    attr_code = attr_code.substring(attr_code.length() - 5, attr_code.length() - 1);
                    String attr_name = param.getData(j).getString("ATTR_NAME");
                    String dataCol = param.getData(j).getString("RSRV_STR1");

                    IData sub = (IData) memberDataset.get(k);
                    IData putData = new DataMap();
                    String value = sub.getString(dataCol);
                    if (sub.getString(dataCol) == null || "".equals(sub.getString(dataCol)))
                    {
                        continue;
                    }

                    putData.put("IBSYSID", SeqMgr.getSysIbSysId());
                    putData.put("ID", baseInfo.getString("IBSYSID"));
                    putData.put("PARAMNAMEEN", attr_code);
                    putData.put("PARAMNAMECN", attr_name);
                    putData.put("PARAMVALUE", value);
                    putData.put("PARAMTYPE", "1"); // 解决批量业务不传成员参数bug
                    subData.add(putData);
                }

                // 插明细表信息
                Dao.insert("TI_B_BBOSSMEMBERDATA_UDR_SUB", subData, Route.CONN_CRM_CEN);

                // 更改TF_B_TRADE_BAT 的表的状态标志为激活
                data.put("DEAL_STATE", deal_state);
                data.put("BATCH_ID", batch_id);
                data.put("ACTIVE_FLAG", "1");
                data.put("ACTIVE_TIME", getBatDealDate());
                Dao.save("TF_B_TRADE_BAT", data, new String[]{ "BATCH_ID" },Route.getJourDb(Route.CONN_CRM_CG));

                // 更改TF_B_TRADE_BATDEAL的状态标志为成功 tf_b_trade_batdeal
                IData result = new DataMap();
                result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_9);
                result.put("DEAL_RESULT", "成员批量数据提交一级BBOSS成功");
                result.put("BATCH_ID", batch_id);
                result.put("SERIAL_NUMBER", sn);
                this.updateBatDealByBatchIdSn(result);

            }
            catch (Exception e)
            {
                String err = e.getMessage();
                if (null == err || StringUtils.isEmpty(err))
                {
                    err = "处理失败！";
                }
                // 更改TF_B_TRADE_BATDEAL的状态标志为失败 tf_b_trade_batdeal
                IData result = new DataMap();
                result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_B);
                result.put("DEAL_RESULT", err);
                result.put("BATCH_ID", batch_id);
                result.put("SERIAL_NUMBER", sn);
                this.updateBatDealByBatchIdSn(result);
            }
        }
    }

    /**
     * 启动行业应用卡批量任务（多个）
     * 
     * @param startBatDeals
     * @throws Exception
     */
    public IDataset startBBossHYYYKBatDeals(IData inData) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        String batch_oper_type = inData.getString("BATCH_OPER_TYPE", "");
        String element = inData.getString("BATCH_ID");

        if (batch_oper_type.equals("BATADDHYYYKMEM"))
        {
            // 行业应用卡批量处理
            fileNameList = startHYYYKBatDeal(element);
        }
        else if (batch_oper_type.equals("BATOPENHYYYKMEM"))
        {
            // 行业应用卡配合省反馈成员开通结果
            fileNameList = startHYYYKOpenBatDeal(element);
        }

        return fileNameList;
    }

    /**
     * 启动一点支付批量任务（多个）
     * 
     * @param startBatDeals
     * @throws Exception
     */
    public IDataset startBBossYDZFBatDeals(IData inData) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        String batch_oper_type = inData.getString("BATCH_OPER_TYPE", "");
        String element = inData.getString("BATCH_ID");

        if (batch_oper_type.equals("BATADDYDZFMEM"))
        {
            // 一点支付批量处理
            fileNameList = startYDZFBatDeal(element);
        }
        else if (batch_oper_type.equals("BATCONFIRMYDZFMEM"))
        {
            // 一点支付配合省反馈成员确认结果
            fileNameList = startYDZFConfirmBatDeal(element);
        }
        else if (batch_oper_type.equals("BATOPENYDZFMEM"))
        {
            // 一点支付配合省反馈成员开通结果
            fileNameList = startYDZFOpenBatDeal(element);
        }

        return fileNameList;
    }

    /**
     * 行业应用卡 主办省上传成员明细文件 批量处理
     */
    @SuppressWarnings("static-access")
    public IDataset startHYYYKBatDeal(String batch_id) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        BatTradeInfoQry dao = new BatTradeInfoQry();

        IData inparams = new DataMap();
        inparams.put("BATCH_ID", batch_id);

        IDataset bats = dao.queryBBossMemberInfosByBidSn(inparams, Route.getJourDb(Route.CONN_CRM_CG));

        StringBuilder sql = new StringBuilder(
                "insert into TI_B_BBOSS_USER (ORG_DOMAIN, IBSYSID_SUB, IBSYSID, PROVCODE, INFO_TYPE, MSISDN, FEEPLAN, OPERCODE, ACCOUNTNAMEREQ, PAYTYPE, PAYAMOUNT, EFFRULE, NAMEMATCH, CURRFEEPLAN, USERSTATUS, CENTROLPAYSTATUS, ACCOUNTNAME, FAILDESC, ISNEWUSER, NEWUSERFAILDESC, NEWUSERCOUNT, UPDATE_TIME, STATUS, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, PRODINFO)"
                        + "select ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,?,? from dual");

        Parameter[] param = new Parameter[bats.size()];
        String ibsysid = SeqMgr.getCenIbSysId();
        for (int i = 0, size = bats.size(); i < size; i++)
        {
            /** 构造绑定对象，按顺序绑定参数值 */
            IData bat = bats.getData(i);
            param[i] = new Parameter();
            param[i].add("BOSS");
            param[i].add(SeqMgr.getCenIbSysSubId());
            param[i].add(ibsysid);
            // 省代码
            param[i].add(bat.getString("DATA1"));
            // 信息类型
            param[i].add(bat.getString("DATA2"));
            param[i].add(bat.getString("SERIAL_NUMBER"));
            // 套餐要求
            param[i].add(bat.getString("DATA11"));
            // 成员操作类型
            param[i].add(bat.getString("DATA4"));
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            // 新开卡数量
            param[i].add(bat.getString("DATA3"));
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            // 成员功能需求
            param[i].add(bat.getString("DATA12"));
        }

        Dao.executeBatch(sql, param, Route.CONN_CRM_CEN);

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("BATCH_ID", batch_id);
        data.put("STATUS", "1");
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("REMARK", "");
        String fileName = getMemAttachFileName();
        fileNameList.add(fileName);
        data.put("FILE_NAME", fileName);
        data.put("ORG_DOMAIN", "BOSS");
        data.put("FILE_TYPE", "1");

        Dao.insert("TI_B_BBOSS", data);
        // 更新状态
        updateYDZFBatState(batch_id);

        return fileNameList;
    }

    /**
     * 行业应用卡 配合省反馈成员开通结果批量处理
     */
    @SuppressWarnings("static-access")
    public IDataset startHYYYKOpenBatDeal(String batch_id) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        BatTradeInfoQry dao = new BatTradeInfoQry();

        IData inparams = new DataMap();
        inparams.put("BATCH_ID", batch_id);

        IDataset bats = dao.queryBBossMemberInfosByBidSn(inparams, Route.getJourDb(Route.CONN_CRM_CG));

        StringBuilder sql = new StringBuilder(
                "insert into TI_B_BBOSS_USER (ORG_DOMAIN, IBSYSID_SUB, IBSYSID, PROVCODE, INFO_TYPE, MSISDN, FEEPLAN, OPERCODE, ACCOUNTNAMEREQ, PAYTYPE, PAYAMOUNT, EFFRULE, NAMEMATCH, CURRFEEPLAN, USERSTATUS, CENTROLPAYSTATUS, ACCOUNTNAME, FAILDESC, ISNEWUSER, NEWUSERFAILDESC, NEWUSERCOUNT, UPDATE_TIME, STATUS, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5)"
                        + "select ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,? from dual");

        Parameter[] param = new Parameter[bats.size()];
        String ibsysid = SeqMgr.getCenIbSysId();
        for (int i = 0, size = bats.size(); i < size; i++)
        {
            /** 构造绑定对象，按顺序绑定参数值 */
            IData bat = bats.getData(i);
            param[i] = new Parameter();
            param[i].add("BOSS");
            param[i].add(SeqMgr.getCenIbSysSubId());
            param[i].add(ibsysid);
            // 省代码
            param[i].add(bat.getString("DATA1"));
            param[i].add("");
            param[i].add(bat.getString("SERIAL_NUMBER"));
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            // 代付关系处理是否成功
            param[i].add(bat.getString("DATA2"));
            param[i].add("");
            // 处理失败原因
            param[i].add(bat.getString("DATA11"));
            // 是否新开卡成员
            param[i].add(bat.getString("DATA3"));
            // 未能足量完成新开卡和建立代付关系的原因
            param[i].add(bat.getString("DATA12"));
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
        }

        Dao.executeBatch(sql, param, Route.CONN_CRM_CEN);

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("BATCH_ID", batch_id);
        data.put("STATUS", "1");
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("REMARK", "");
        String fileName = getMemAttachFileName();
        fileNameList.add(fileName);
        data.put("FILE_NAME", fileName);
        data.put("ORG_DOMAIN", "BOSS");
        data.put("FILE_TYPE", "3");

        Dao.insert("TI_B_BBOSS", data);
        // 更新状态
        updateYDZFBatState(batch_id);

        return fileNameList;
    }

    /**
     * 一点支付启动批量任务
     * 
     * @param batch_id
     * @param batchdata
     * @throws Exception
     */
    public IDataset startYDZFBatDeal(String batch_id) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        IData inparams = new DataMap();
        inparams.put("BATCH_ID", batch_id);

        IDataset bats = BatTradeInfoQry.queryBBossMemberInfosByBidSn(inparams, Route.getJourDb(Route.CONN_CRM_CG));

        StringBuilder sql = new StringBuilder(
                "insert into TI_B_BBOSS_USER (ORG_DOMAIN, IBSYSID_SUB, IBSYSID, PROVCODE, INFO_TYPE, MSISDN, FEEPLAN, OPERCODE, ACCOUNTNAMEREQ, PAYTYPE, PAYAMOUNT, EFFRULE, NAMEMATCH, CURRFEEPLAN, USERSTATUS, CENTROLPAYSTATUS, ACCOUNTNAME, FAILDESC, ISNEWUSER, NEWUSERFAILDESC, NEWUSERCOUNT, UPDATE_TIME, STATUS, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5)"
                        + "select ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,? from dual");

        Parameter[] param = new Parameter[bats.size()];
        String ibsysid = SeqMgr.getCenIbSysId();
        for (int i = 0, size = bats.size(); i < size; i++)
        {
            /** 构造绑定对象，按顺序绑定参数值 */
            IData bat = bats.getData(i);
            param[i] = new Parameter();
            param[i].add("BOSS");
            param[i].add(SeqMgr.getCenIbSysSubId());
            param[i].add(ibsysid);
            // 省代码
            param[i].add(bat.getString("DATA1"));
            // 信息类型
            param[i].add(bat.getString("DATA2"));
            param[i].add(bat.getString("SERIAL_NUMBER"));
            // 套餐要求
            param[i].add(bat.getString("DATA4"));
            // 成员操作类型
            param[i].add(bat.getString("DATA7"));
            // 户名调查要求
            param[i].add(bat.getString("DATA8"));
            // 支付类型
            param[i].add(bat.getString("DATA5"));
            // 支付额度
            param[i].add(bat.getString("DATA6"));
            // 账期生效规则
            param[i].add(bat.getString("DATA9"));
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            // 新开卡数量
            param[i].add(bat.getString("DATA3"));
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
        }

        Dao.executeBatch(sql, param, Route.CONN_CRM_CEN);

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("BATCH_ID", batch_id);
        data.put("STATUS", "1");
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("REMARK", "");
        String fileName = getMemAttachFileName();
        fileNameList.add(fileName);
        data.put("FILE_NAME", fileName);
        data.put("ORG_DOMAIN", "BOSS");
        data.put("FILE_TYPE", "1");
        data.put("RSRV_STR3", merchpSpecCode); // 和IBOSS约定，用来标识一点支付业务
        
        Dao.insert("TI_B_BBOSS", data);
        // 更新状态
        updateYDZFBatState(batch_id);

        return fileNameList;
    }

    /**
     * 一点支付配合省反馈确认结果批量启动
     * 
     * @param batch_id
     * @param batchdata
     * @throws Exception
     */
    public IDataset startYDZFConfirmBatDeal(String batch_id) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        IData inparams = new DataMap();
        inparams.put("BATCH_ID", batch_id);

        IDataset bats = BatTradeInfoQry.queryBBossMemberInfosByBidSn(inparams, Route.getJourDb(Route.CONN_CRM_CG));

        StringBuilder sql = new StringBuilder(
                "insert into TI_B_BBOSS_USER (ORG_DOMAIN, IBSYSID_SUB, IBSYSID, PROVCODE, INFO_TYPE, MSISDN, FEEPLAN, OPERCODE, ACCOUNTNAMEREQ, PAYTYPE, PAYAMOUNT, EFFRULE, NAMEMATCH, CURRFEEPLAN, USERSTATUS, CENTROLPAYSTATUS, ACCOUNTNAME, FAILDESC, ISNEWUSER, NEWUSERFAILDESC, NEWUSERCOUNT, UPDATE_TIME, STATUS, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5)"
                        + "select ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,? from dual");

        Parameter[] param = new Parameter[bats.size()];
        String ibsysid = SeqMgr.getCenIbSysId();
        for (int i = 0, size = bats.size(); i < size; i++)
        {
            /** 构造绑定对象，按顺序绑定参数值 */
            IData bat = bats.getData(i);
            param[i] = new Parameter();
            param[i].add("BOSS");
            param[i].add(SeqMgr.getCenIbSysSubId());
            param[i].add(ibsysid);
            // 省代码
            param[i].add(bat.getString("DATA1"));
            // 信息类型
            param[i].add("");
            param[i].add(bat.getString("SERIAL_NUMBER"));
            // 套餐要求
            param[i].add("");
            // 成员操作类型
            param[i].add("");
            // 户名调查要求
            param[i].add("");
            // 支付类型
            param[i].add("");
            // 支付额度
            param[i].add("");
            // 账期生效规则
            param[i].add("");
            // 户名是否匹配
            param[i].add(bat.getString("DATA2"));
            // 当前套餐
            param[i].add(bat.getString("DATA3"));
            // 用户状态
            param[i].add(bat.getString("DATA4"));
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            // 新开卡数量
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
        }

        Dao.executeBatch(sql, param, Route.CONN_CRM_CEN);

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("BATCH_ID", batch_id);
        data.put("STATUS", "1");
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("REMARK", "");
        String fileName = getMemAttachFileName();
        fileNameList.add(fileName);
        data.put("FILE_NAME", fileName);
        data.put("ORG_DOMAIN", "BOSS");
        data.put("FILE_TYPE", "2");
        data.put("RSRV_STR3", merchpSpecCode); // 和IBOSS约定，用来标识一点支付业务
        
        Dao.insert("TI_B_BBOSS", data);
        // 更新状态
        updateYDZFBatState(batch_id);

        return fileNameList;
    }

    /**
     * 一点支付反馈成员开通结果批量启动
     * 
     * @param batch_id
     * @param batchdata
     * @throws Exception
     */
    public IDataset startYDZFOpenBatDeal(String batch_id) throws Exception
    {
        IDataset fileNameList = new DatasetList();

        IData inparams = new DataMap();
        inparams.put("BATCH_ID", batch_id);

        IDataset bats = BatTradeInfoQry.queryBBossMemberInfosByBidSn(inparams, Route.getJourDb(Route.CONN_CRM_CG));

        StringBuilder sql = new StringBuilder(
                "insert into TI_B_BBOSS_USER (ORG_DOMAIN, IBSYSID_SUB, IBSYSID, PROVCODE, INFO_TYPE, MSISDN, FEEPLAN, OPERCODE, ACCOUNTNAMEREQ, PAYTYPE, PAYAMOUNT, EFFRULE, NAMEMATCH, CURRFEEPLAN, USERSTATUS, CENTROLPAYSTATUS, ACCOUNTNAME, FAILDESC, ISNEWUSER, NEWUSERFAILDESC, NEWUSERCOUNT, UPDATE_TIME, STATUS, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5)"
                        + "select ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,? from dual");

        Parameter[] param = new Parameter[bats.size()];
        String ibsysid = SeqMgr.getCenIbSysId();
        for (int i = 0, size = bats.size(); i < size; i++)
        {
            /** 构造绑定对象，按顺序绑定参数值 */
            IData bat = bats.getData(i);
            param[i] = new Parameter();
            param[i].add("BOSS");
            param[i].add(SeqMgr.getCenIbSysSubId());
            param[i].add(ibsysid);
            // 省代码
            param[i].add(bat.getString("DATA1"));
            // 信息类型
            param[i].add("");
            param[i].add(bat.getString("SERIAL_NUMBER"));
            // 套餐要求
            param[i].add(bat.getString("DATA9"));
            // 成员操作类型
            param[i].add("");
            // 户名调查要求
            param[i].add("");
            // 支付类型
            param[i].add(bat.getString("DATA6"));
            // 支付额度
            param[i].add(bat.getString("DATA7"));
            // 账期生效规则
            param[i].add(bat.getString("DATA3"));
            // 户名是否匹配
            param[i].add("");
            // 当前套餐
            param[i].add("");
            // 用户状态
            param[i].add("");
            // 代付关系处理是否成功
            param[i].add(bat.getString("DATA2"));
            // 当前户名
            param[i].add(bat.getString("DATA4"));
            // 处理失败原因
            param[i].add(bat.getString("DATA5"));
            // 是否新开卡成员
            param[i].add(bat.getString("DATA8"));
            // 未能足量完成新开卡和建立代付关系的原因
            param[i].add(bat.getString("DATA10"));
            // 新开卡数量
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
            param[i].add("");
        }

        Dao.executeBatch(sql, param, Route.CONN_CRM_CEN);

        IData data = new DataMap();
        data.put("IBSYSID", ibsysid);
        data.put("BATCH_ID", batch_id);
        data.put("STATUS", "1");
        data.put("UPDATE_TIME", SysDateMgr.getSysDate());
        data.put("REMARK", "");
        String fileName = getMemAttachFileName();
        fileNameList.add(fileName);
        data.put("FILE_NAME", fileName);
        data.put("ORG_DOMAIN", "BOSS");
        data.put("FILE_TYPE", "3");
        data.put("RSRV_STR3", merchpSpecCode); // 和IBOSS约定，用来标识一点支付业务
        
        Dao.insert("TI_B_BBOSS", data);
        // 更新状态
        updateYDZFBatState(batch_id);

        return fileNameList;
    }

    /**
     * 通过BatchID、SerialNumber更新未启动批量详情表,更新deal_state/deal_result
     * 
     * @param data
     * @throws Exception
     */
    public void updateBatDealByBatchIdSn(IData data) throws Exception
    {

        StringBuilder sql = new StringBuilder();

        sql.append(" UPDATE tf_b_trade_batdeal a");
        sql.append(" SET a.deal_state = :DEAL_STATE,a.deal_time = SYSDATE");
        sql.append(" ,a.DEAL_RESULT = :DEAL_RESULT");
        sql.append(" ,a.DEAL_DESC = :DEAL_DESC");
        sql.append(" where 1=1");
        sql.append(" and a.batch_id = TO_NUMBER(:BATCH_ID)");
        sql.append(" and a.accept_month = TO_NUMBER(SUBSTR(:BATCH_ID, 5, 2)) ");
        sql.append(" and a.deal_state = '0'");
        sql.append(" and a.serial_number = :SERIAL_NUMBER");

        Dao.executeUpdate(sql, data, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 更新一点支付批量状态
     * 
     * @param batch_id
     * @throws Exception
     */
    public void updateYDZFBatState(String batch_id) throws Exception
    {

        IData data = new DataMap();
        // 默认为1
        String deal_state = "1";
        // 更改TF_B_TRADE_BAT 的表的状态标志为激活
        data.put("DEAL_STATE", deal_state);
        data.put("BATCH_ID", batch_id);
        data.put("ACTIVE_FLAG", "1");
        data.put("ACTIVE_TIME", SysDateMgr.getSysTime());
        Dao.save("TF_B_TRADE_BAT", data, new String[]
        { "BATCH_ID" }, Route.getJourDb(Route.CONN_CRM_CG));

        // 更改TF_B_TRADE_BATDEAL的状态标志为成功 tf_b_trade_batdeal
        IData result = new DataMap();
        result.put("DEAL_STATE", BatDealStateUtils.DEAL_STATE_9);
        result.put("BATCH_ID", batch_id);
        updateBatDealStateByBatchId(result);
    }

}
