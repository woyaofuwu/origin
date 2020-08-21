
package com.asiainfo.veris.crm.order.soa.person.busi.interroam.airline;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AirlinesInterRoamUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import org.apache.log4j.Logger;

public class AirlinesWhiteUserBean extends CSBizBean {
    private static transient Logger log = Logger.getLogger(Dao.class);

    // AEE定时任务调用接口，将REMOVE_TAG为2的白名单用户进行退订同步国漫，然后将REMOVE_TAG改成1
    public void batChangeUserState() throws Exception {
        // 先查询出数据库里REMOVE_TAG状态为2的白名单用户
        IDataset whiteUserCancelSet = AirlinesInterRoamUtil.queryWhiteUserCancel();
        for (int i = 0; i < whiteUserCancelSet.size(); i++) {
            String serial_number = whiteUserCancelSet.getData(i).getString("SERIAL_NUMBER");
            // 调产品变更接口退订，产品变更的action里会自动将commpara里2742的配置同步给国漫？
            // 查出手机号码对应的有效的USER_ID
            IDataset userInfoSet = UserInfoQry.getEffUserInfoBySn(serial_number, "0", CSBizBean.getVisit().getStaffEparchyCode());
            // 假如没有，说明用户已经销户了，直接跳过
            if (IDataUtil.isEmpty(userInfoSet)) {
                // CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号未找到有效的用户信息");
                break;
            }

            // 根据USER_ID查找用户的优惠
            String user_id = userInfoSet.getData(0).getString("USER_ID");
            IDataset userDiscntSet = UserDiscntInfoQry.queryUserAllValidDiscnt(user_id);
            if (IDataUtil.isEmpty(userDiscntSet)) {
                break;
            }
            for (int j = 0; j < userDiscntSet.size(); j++) {
                String userDiscntCode = userDiscntSet.getData(j).getString("DISCNT_CODE");
                // 全球通无限尊享计划套餐八折：走产品变更，action会自动调319同步（配置在commpara = 1806）
                if ((AirlinesInterRoamUtil.getUnLimitProduct8Discnt()).equals(userDiscntCode)) {
                    IData cancelParam = new DataMap();
                    cancelParam.put("SERIAL_NUMBER", serial_number);
                    cancelParam.put("ELEMENT_ID", userDiscntCode);
                    cancelParam.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                    cancelParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                    cancelParam.put("BOOKING_TAG", "0");
                    CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", cancelParam);
                }

                /**
                 *  国漫标准资费八折、国漫专属叠加日包、国漫专属叠加月包、国漫流量日优惠包：调TRADE_TYPE_CODE = 300接口，会自动同步国漫集中中心
                 *  这些是先调305同步给国漫，国漫再调307落地到我们。（配置在commpara = 2742）
                 **/
                // 国漫标准资费八折
                if ((AirlinesInterRoamUtil.getInterRoamStandard8Discnt()).equals(userDiscntCode)) {
                    IData cancelParam = new DataMap();
                    cancelParam.put("AUTH_SERIAL_NUMBER", serial_number);
                    cancelParam.put("SERIAL_NUMBER", serial_number);
                    cancelParam.put("TRADE_TYPE_CODE", "300");
                    cancelParam.put("DISCNT_CODE", userDiscntCode);
                    cancelParam.put("OP_TAG", "1");
                    cancelParam.put("ELEMENT_ID", userDiscntCode);
                    cancelParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                    cancelParam.put("USER_TYPE", "00");
                    CSAppCall.call("SS.InterRoamDayRegSVC.tradeReg", cancelParam);
                }
                // 国漫专属叠加日包
                if ((AirlinesInterRoamUtil.getInterRoamCasDayDiscnt()).equals(userDiscntCode)) {
                    IData cancelParam = new DataMap();
                    cancelParam.put("SERIAL_NUMBER", serial_number);
                    cancelParam.put("AUTH_SERIAL_NUMBER", serial_number);
                    cancelParam.put("TRADE_TYPE_CODE", "300");
                    cancelParam.put("DISCNT_CODE", userDiscntCode);
                    cancelParam.put("OP_TAG", "1");
                    cancelParam.put("ELEMENT_ID", userDiscntCode);
                    cancelParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                    cancelParam.put("USER_TYPE", "00");
                    CSAppCall.call("SS.InterRoamDayRegSVC.tradeReg", cancelParam);
                }
                // 国漫专属叠加月包
                if ((AirlinesInterRoamUtil.getInterRoamCasMonthDiscnt()).equals(userDiscntCode)) {
                    IData cancelParam = new DataMap();
                    cancelParam.put("SERIAL_NUMBER", serial_number);
                    cancelParam.put("AUTH_SERIAL_NUMBER", serial_number);
                    cancelParam.put("TRADE_TYPE_CODE", "300");
                    cancelParam.put("DISCNT_CODE", userDiscntCode);
                    cancelParam.put("OP_TAG", "1");
                    cancelParam.put("ELEMENT_ID", userDiscntCode);
                    cancelParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                    cancelParam.put("USER_TYPE", "00");
                    CSAppCall.call("SS.InterRoamDayRegSVC.tradeReg", cancelParam);
                }

                // 6个月保底优惠退订：直接走产品变更，而且不需要同步（只要存在，不需要判断是否满足6个月）
                if ((AirlinesInterRoamUtil.getGuaranteeMoney6MonthDiscnt()).equals(userDiscntCode)) {
                    IData changeParam = new DataMap();
                    changeParam.put("SERIAL_NUMBER", serial_number);
                    changeParam.put("ELEMENT_ID", userDiscntCode);
                    changeParam.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                    changeParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                    changeParam.put("BOOKING_TAG", "0");
                    changeParam.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                    CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", changeParam);
                }
            }

            // 将此号码的REMOVE_TAG改为1
            String Serial_number = whiteUserCancelSet.getData(i).getString("SERIAL_NUMBER");
            IData input = new DataMap();
            input.put("SERIAL_NUMBER", Serial_number);
            AirlinesInterRoamUtil.updateRemoveWhiteList(input);
        }
    }

    /**
     * 根据输入条件模糊查询表
     */
    public IDataset queryAirlineswhite(IData userInfo, Pagination pagination) throws Exception {
        userInfo.put("REMOVE_TAG", "0");
        IDataset whiteList = Dao.qryByCodeParser("TF_F_AIRLINES_WHITE", "SEL_BY_ALL", userInfo, pagination, Route.getCrmDefaultDb());
        return whiteList;
    }

    /**
     * 新增员工数据(界面处理)
     */
    public IData createAirlinesWhite(IData userInfo) throws Exception {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "1");
        result.put("X_RESULTINFO", "新增失败");
        String serialNumber = userInfo.getString("SERIAL_NUMBER");
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("REMOVE_TAG", "0");
        IDataset whiteList = Dao.qryByCodeParser("TF_F_AIRLINES_WHITE", "SEL_BY_ALL", param, Route.getCrmDefaultDb());
        if (DataUtils.isEmpty(whiteList)) {//没有就新增
            userInfo.put("REMOVE_TAG", "0");
            userInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
            boolean isAdd = Dao.insert("TF_F_AIRLINES_WHITE", userInfo, Route.getCrmDefaultDb());
            if (isAdd) {
                result.put("X_RESULTCODE", "0");
            }
        } else {//有就更新
            IData input = whiteList.getData(0);
            input.putAll(userInfo);//覆盖原来的值
            AirlinesInterRoamUtil.updateNewWhiteList(input);
        }
        return result;
    }

    /**
     * 将员工移除白名单，取消对应国漫八折资费，表REMOVE_TAG更新为非0状态,删除数据为列表中的数据，所以查询结果肯定有值
     */
    public IData deleteAirlinesWhite(IData userInfo) throws Exception {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");

        userInfo.put("REMOVE_TAG", "0");
        IDataset whiteList = Dao.qryByCodeParser("TF_F_AIRLINES_WHITE", "SEL_BY_ALL", userInfo, Route.getCrmDefaultDb());
        IData input = whiteList.getData(0);
        int i = AirlinesInterRoamUtil.deleteWhiteList(input);
        if (i == 0) {
            result.put("X_RESULTCODE", "1");
        }
        return result;
    }

    //批量导入
    public IData batImportAirList(IData param) throws Exception {
        IData result = checkImportFile(param);
        String operTag = param.getString("operTag");
        IDataset successes = result.getDataset("SUCCESS");
        if (successes.size() > 0) {
            if ("0".equals(operTag)) {
                importDealAdd(successes); // 批量新增
            } else if ("1".equals(operTag)) {
                importDealDelete(successes); // 批量删除
            }
        } else {
            CSAppException.apperr(BatException.CRM_BAT_6);
        }
        return result;
    }

    //解析校验
    private IData checkImportFile(IData params) throws Exception {
        IDataset succds = new DatasetList();
        IDataset faileds = new DatasetList();
        IData fileData = params.getData("FILEDATA");
        IDataset[] datasets = (IDataset[]) fileData.get("right");
        for (int i = 0; i < datasets.length; i++) {
            IDataset dataset = datasets[i];
            if (IDataUtil.isEmpty(dataset)) {
                CSAppException.apperr(BatException.CRM_BAT_20);
            }
            if (dataset.size() > 1000) {
                CSAppException.apperr(BatException.CRM_BAT_21, "1000");
            }
            for (int j = 0; j < dataset.size(); j++) {
                IData data = dataset.getData(j);
                String serialNumber = data.getString("SERIAL_NUMBER", "");
                String airLinesName = data.getString("AIRLINES_NAME", "");
                String mainairPrvName = data.getString("MAINAIR_PRV_NAME", "");
                String provinceName = data.getString("PROVINCE_NAME", "");
                String cityName = data.getString("CITY_NAME", "");
                String staffName = data.getString("STAFF_NAME", "");
                if (StringUtils.isEmpty(serialNumber)) {
                    data.put("REMARK", data.getString("REMARK") + "||错误描述：员工手机号码不能为空！");
                    faileds.add(data);
                    continue;
                }
                if (!StringUtils.isNumeric(serialNumber)) {
                    data.put("REMARK", data.getString("REMARK") + "||错误描述：员工手机号码格式不对!");
                    faileds.add(data);
                    continue;
                }
                if (StringUtils.isEmpty(airLinesName)) {
                    data.put("REMARK", data.getString("REMARK") + "||错误描述：航空公司名称不能为空！");
                    faileds.add(data);
                    continue;
                }
                if (StringUtils.isEmpty(mainairPrvName)) {
                    data.put("REMARK", data.getString("REMARK") + "||错误描述：航空公司总部所在省份不能为空！");
                    faileds.add(data);
                    continue;
                }
                if (StringUtils.isEmpty(provinceName)) {
                    data.put("REMARK", data.getString("REMARK") + "||错误描述：航空公司分支机构所在省份不能为空！");
                    faileds.add(data);
                    continue;
                }
                if (StringUtils.isEmpty(cityName)) {
                    data.put("REMARK", data.getString("REMARK") + "||错误描述：航空公司分支机构所在地市不能为空！");
                    faileds.add(data);
                    continue;
                }
                if (StringUtils.isEmpty(staffName)) {
                    data.put("REMARK", data.getString("REMARK") + "||错误描述：员工姓名不能为空！");
                    faileds.add(data);
                    continue;
                }
                if (dataset.size() > 1) {
                    for (int k = j + 1; k < dataset.size(); k++) {
                        if (StringUtils.equals(serialNumber, dataset.getData(k).getString("SERIAL_NUMBER"))) {
                            dataset.getData(k).put("REMARK", data.getString("REMARK") + "||错误描述：文件中存在重复的号码!");
                            faileds.add(dataset.getData(k));
                            dataset.remove(k);
                            k--;
                            continue;
                        }
                    }
                }
                data.put("SERIAL_NUMBER", serialNumber);
                data.put("AIRLINES_NAME", airLinesName);
                data.put("MAINAIR_PRV_NAME", mainairPrvName);
                data.put("PROVINCE_NAME", provinceName);
                data.put("CITY_NAME", cityName);
                data.put("STAFF_NAME", staffName);
                data.put("IMPORT_TIME", data.getString("IMPORT_TIME", SysDateMgr.getSysTime()));
                succds.add(data);
            }
        }

        IData result = new DataMap();
        result.put("SUCCESS", succds);
        result.put("FAILED", faileds);
        return result;
    }

    // 批量删除导入数据处理
    private void importDealDelete(IDataset dataset) throws Exception {
        IDataset deleteParams = new DatasetList();
        for (int i = 0; i < dataset.size(); i++) {
            IData data = dataset.getData(i);
            String serial_number = data.getString("SERIAL_NUMBER");
            // 构建插入数据库的数据
            IData param = new DataMap();
            param.put("AIRLINES_NAME", data.getString("AIRLINES_NAME"));
            param.put("MAINAIR_PRV_NAME", data.getString("MAINAIR_PRV_NAME"));
            param.put("PROVINCE_NAME", data.getString("PROVINCE_NAME"));
            param.put("CITY_NAME", data.getString("CITY_NAME"));
            param.put("STAFF_NAME", data.getString("STAFF_NAME"));
            param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            param.put("SERIAL_NUMBER", serial_number);
            // 先判断用户是不是白名单号码
            IDataset AirlinesWhiteUser = AirlinesInterRoamUtil.qryInterRoamAirWhite(serial_number);
            // 假如用户已经存在则添加到删除列表
            if (IDataUtil.isNotEmpty(AirlinesWhiteUser)) {
                deleteParams.add(param);
            }
        }
        if (IDataUtil.isNotEmpty(deleteParams)) {
            AirlinesInterRoamUtil.updateBatchDeleteWhiteList(deleteParams);
        }
    }

    // 批量新增导入数据处理
    private void importDealAdd(IDataset dataset) throws Exception {
        IDataset addParams = new DatasetList();
        IDataset updateParams = new DatasetList();
        for (int i = 0; i < dataset.size(); i++) {
            IData data = dataset.getData(i);
            String serial_number = data.getString("SERIAL_NUMBER");
            // 构建插入数据库的数据
            IData param = new DataMap();
            param.put("AIRLINES_NAME", data.getString("AIRLINES_NAME"));
            param.put("MAINAIR_PRV_NAME", data.getString("MAINAIR_PRV_NAME"));
            param.put("PROVINCE_NAME", data.getString("PROVINCE_NAME"));
            param.put("CITY_NAME", data.getString("CITY_NAME"));
            param.put("STAFF_NAME", data.getString("STAFF_NAME"));
            param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            param.put("SERIAL_NUMBER", serial_number);
            // 先判断用户是不是白名单号码
            IDataset AirlinesWhiteUser = AirlinesInterRoamUtil.qryInterRoamAirWhite(serial_number);
            // 假如用户已经存在则添加到更新列表
            if (IDataUtil.isNotEmpty(AirlinesWhiteUser)) {
                updateParams.add(param);
            } else { // 假如用户不存在则添加到新增列表
                param.put("IMPORT_TIME", data.getString("IMPORT_TIME"));
                param.put("UPDATE_TIME", SysDateMgr.getSysTime());
                param.put("REMOVE_TAG", "0");
                param.put("END_DATE", "2050-12-31 23:59:59");
                param.put("REMARK", "批量导入数据");
                addParams.add(param);
            }
        }
        if (IDataUtil.isNotEmpty(addParams)) {
            Dao.insert("TF_F_AIRLINES_WHITE", addParams, Route.getCrmDefaultDb());
        }
        if (IDataUtil.isNotEmpty(updateParams)) {
            AirlinesInterRoamUtil.updateBatchAddWhiteList(updateParams);
        }
    }

    // 用于“全球通无限尊享计划套餐”套餐费打八折订购/取消（供短厅使用，包含短信下发）
    public IData gsmUnlimitedPackage8ForSms(IData input) throws Exception {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "2998");
        result.put("X_RESULTINFO", "业务受理失败");
        String serial_number = input.getString("SERIAL_NUMBER");
        String modifyTag = input.getString("MODIFY_TAG");
        String elementID = IDataUtil.chkParam(input, "ELEMENT_ID");
        IDataset whiteList = AirlinesInterRoamUtil.qryInterRoamAirWhite(serial_number);
        if (IDataUtil.isEmpty(whiteList)) {
            String errMsg = "对不起，本业务目前只支持指定用户办理，感谢支持。中国移动";
            sendErroSMS(serial_number, errMsg);
            result.put("X_RESULTINFO", errMsg);
            return result;

        }
        IDataset userInfo = UserInfoQry.getUserInfoBySn(serial_number, "0");
        if (DataUtils.isEmpty(userInfo)) {
            String errMsg = "对不起，您的手机号码状态存在异常，本次业务办理失败。请联系10086客服或前往当地营业厅咨询处理，感谢支持。中国移动";
            sendErroSMS(serial_number, errMsg);
            result.put("X_RESULTINFO", errMsg);
            return result;
        }

        input.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        if ("1".equals(modifyTag)) {//取消操作
            IDataset discntInfo = getDiscntForEnddateGeqSysdateByUserIdDiscnt(userInfo.getData(0).getString("USER_ID"), elementID);
            if (DataUtils.isEmpty(discntInfo)) {
                String errMsg = "尊敬的客户，您当前尚未订购【全球通无限尊享计划套餐八折】，无需取消。感谢支持。中国移动";
                sendErroSMS(serial_number, errMsg);
                result.put("X_RESULTINFO", errMsg);
                return result;
            }
            input.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
        }
        
        if(DataUtils.isEmpty(isGlobalPackage(userInfo.getData(0).getString("USER_ID")))){
        	 String errMsg = "对不起，您目前尚未订购全球通无限尊享计划套餐，本次产品订购失败，请先订购全球通无限尊享计划套餐后再试。中国移动";
             sendErroSMS(serial_number, errMsg);
             result.put("X_RESULTINFO", errMsg);
             return result;
        }

        
        try {
            input.put("REMARK", "短厅受理");
            log.debug("--------gsmUnlimitedPackage8ForSms--------input=" + input);
            IDataset tradeReg = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", input);
            log.debug("--------gsmUnlimitedPackage8ForSms--------tradeReg=" + tradeReg);
            if (DataUtils.isEmpty(tradeReg)) {
                return result;
            }
			result.put("X_RESULTINFO", "业务受理成功");
            result.put("X_RESULTCODE", "0000");
            result.putAll(tradeReg.getData(0));
            return result;
        } catch (Exception e) {
            result.put("X_RESULTINFO", e.getMessage());
            return result;
        }
    }

    public void sendErroSMS(String serialNumber, String smsInfo) throws Exception {
        IData sendInfo = new DataMap();
        sendInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        sendInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        sendInfo.put("RECV_OBJECT", serialNumber);
        sendInfo.put("RECV_ID", serialNumber);
        sendInfo.put("SMS_PRIORITY", "50");
        sendInfo.put("NOTICE_CONTENT", smsInfo);
        sendInfo.put("REMARK", "白名单业务受理");
        sendInfo.put("FORCE_OBJECT", "10086");
        SmsSend.insSms(sendInfo);
    }

    public static IDataset getDiscntForEnddateGeqSysdateByUserIdDiscnt(String userId, String discnt_code) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("DISCNT_CODE", discnt_code);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_F_USER_DISCNT T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL(" AND discnt_code = :DISCNT_CODE ");
        parser.addSQL(" AND end_date>sysdate ");
        return Dao.qryByParse(parser);
    }
    
    /**
     * 是全球通无限尊享八折
     * @param userId
     * @return
     * @throws Exception
     */
    public static IDataset isGlobalPackage(String userId) throws Exception {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* ");
        parser.addSQL(" FROM TF_F_USER_DISCNT T ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND T.PARTITION_ID = MOD(:USER_ID, 10000) ");
        parser.addSQL(" AND discnt_code in ('8638','8639','8640','8647','8648','8649') ");
        parser.addSQL(" AND end_date>sysdate ");
        return Dao.qryByParse(parser);
    }

    // 国漫专属叠加日包、国漫专属叠加月包订购/取消接口（供短厅使用，包含短信下发）
    public IData interRoamDayForSms(IData input) throws Exception {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "2998");
        result.put("X_RESULTINFO", "业务受理失败");
        String sn = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataset whiteList = AirlinesInterRoamUtil.qryInterRoamAirWhite(sn);
        if (DataUtils.isEmpty(whiteList)) {
            String errMsg = "对不起，本业务目前只支持指定用户办理，感谢支持。中国移动";
            sendErroSMS(sn, errMsg);
            result.put("X_RESULTINFO", errMsg);
            return result;
        }
        try {
            IDataset tradeReg = CSAppCall.call("SS.InterRoamDayRegSVC.tradeReg", input);
            if (DataUtils.isEmpty(tradeReg)) {
                return result;
            }
            result.put("X_RESULTCODE", "0000");
			result.put("X_RESULTINFO", "业务受理成功");
            result.putAll(tradeReg.getData(0));
            return result;
        } catch (Exception e) {
            result.put("X_RESULTINFO", e.getMessage());
            return result;
        }
    }

}
