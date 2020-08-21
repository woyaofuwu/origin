package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custmanager.CustManagerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;

/**
 * 权益中心接口类
 *
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/10 9:23
 */
public class BenefitCenterIntfSVC extends CSBizService {
	public static Logger logger = Logger.getLogger(BenefitCenterIntfSVC.class);

    /**
     * 全球通用户类型接口
     *
     * @param input
     * @return
     * @throws Exception
     */
    public static IData qryUserClassInfo(IData input) throws Exception {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "成功");
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        //查询全球通等级
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        IData userClass = UserClassInfoQry.queryUserClassBySN(input);
        if (IDataUtil.isEmpty(userClass)) {
            result.put("USER_CLASS", "0");
        } else {
            result.put("USER_CLASS", userClass.getString("USER_CLASS", "0"));
            result.put("USER_CLASS_NAME", userClass.getString("USER_CLASS_NAME"));
            result.put("USER_ID", userClass.getString("USER_ID"));
            result.put("START_DATE", userClass.getString("START_DATE"));
            result.put("END_DATE", userClass.getString("END_DATE"));
        }
        //查询是否集团关键人
        IData groupKeyMem = CustManagerInfoQry
                .getGroupKeyMemByGrpdMemSn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(groupKeyMem)) {
            result.put("KEYM_TAG", "0");
        } else {
            result.put("KEYM_TAG", "1");
        }
        return result;
    }


    /**
     * 权益办理接口（权益注册或绑定接口）
     * 作用：(全球通客户)在权益方发起权益的办理（注册或绑定）时，权益方系统调用。
     *
     * @param input
     * @return
     * @throws Exception
     */
    public static IData benefitBindReg(IData input) throws Exception {
        //入参:RIGHT_ID权益类型ID   DISCNT_CODE权益实例
        //      REL_ID权益关联的标识   SERIAL_NUMBER手机号
        //1校验入参
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "成功");
        IDataUtil.chkParam(input, "RIGHT_ID");
        IDataUtil.chkParam(input, "DISCNT_CODE");
        IDataUtil.chkParam(input, "REL_ID");
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        //正则校验车牌号
        boolean isCarNum = bean.checkCarNum(input.getString("REL_ID"));
        if (!isCarNum) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "请输入正确车牌号");
        }


        //  2校验是否权益办理资格
        //  2.0获取用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userinfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该用户信息");
        }
        //  2.1SERIAL_NUMBER->tf_f_user_info_class查全球通等级
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        IData userClass = UserClassInfoQry.queryUserClassBySN(param);
        String userClassTag = "0";
        if (IDataUtil.isNotEmpty(userClass)) {
            userClassTag = userClass.getString("USER_CLASS", "0");
        }
        //  2.2查权益配置7173校验DISCNT_CODE是否配置用户全球通等级
        param.clear();
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        param.put("CONDITION_KEY", "USERCLASS");
        param.put("CONDITION_VALUE", userClassTag);
        boolean isAvailableBenefit = bean.checkBenefitConfigByCondition(param);
        if (!isAvailableBenefit) {
            result.put("X_RESULTCODE", "1112");
            result.put("X_RESULTINFO", "您无法办理该类型权益");
            return result;
        }
        //  2.3校验REL_ID是否已被绑定
        param.clear();
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        param.put("REL_ID", input.getString("REL_ID"));
        param.put("USER_ID", userinfo.getString("USER_ID"));
        if (bean.checkRelIdBinded(param) || bean.checkRelIdIsTrading(param)) {
            result.put("X_RESULTCODE", "1115");
            result.put("X_RESULTINFO", "此车牌号已被其它客户绑定使用，无法绑定车牌");
            return result;
        }

        param.clear();
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        param.put("USER_ID", userinfo.getString("USER_ID"));
        boolean isHavaRelId = bean.checkHaveBenefitRelId(param);
        if (isHavaRelId) {
            result.put("X_RESULTCODE", "1113");
            result.put("X_RESULTINFO", "您已绑定权益");
            return result;
        }
        param.clear();
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        param.put("USER_ID", userinfo.getString("USER_ID"));
        boolean isHaveChangeNum = bean.checkBenefitChangeNum(param);
        if (!isHaveChangeNum) {
            result.put("X_RESULTCODE", "1114");
            result.put("X_RESULTINFO", "您变更绑定车牌的次数为0，无法变更绑定车牌");
            return result;
        }

        //  2.5判断用户是否有使用次数
        param.clear();
        param.put("USER_ID", userinfo.getString("USER_ID"));
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        param.put("REL_ID", input.getString("REL_ID"));
        int remainUseNum = bean.queryRemainUseNum(param);
        if (remainUseNum <= 0) {
            result.put("X_RESULTCODE", "1116");
            result.put("X_RESULTINFO", "您无免费停车次数，无法绑定车牌");
            return result;
        }


        //变更权益标识,该接口进来的都为新增权益标识
        param.clear();
        param.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        param.put("REL_ID", input.getString("REL_ID"));
        CSAppCall.call("SS.BenefitBindRelChangeSVC.tradeReg", param);
        return result;
    }

    /**
     * 权益变更接口
     *
     * @param input
     * @return
     * @throws Exception
     */
    public static IData benefitBindRelChangeReg(IData input) throws Exception {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "成功");
        IDataUtil.chkParam(input, "RIGHT_ID");
        IDataUtil.chkParam(input, "DISCNT_CODE");
        IDataUtil.chkParam(input, "REL_ID");
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        boolean isCarNum = bean.checkCarNum(input.getString("REL_ID"));
        if (!isCarNum) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "请输入正确车牌号");
        }
        //获取用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userinfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该用户信息");
        }
        //查询REL_ID是否存在权益绑定关系
        IData param = new DataMap();
        param.put("REL_ID", input.getString("REL_ID"));
        param.put("USER_ID", userinfo.getString("USER_ID"));
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        IDataset bindRelIds = bean.queryRelIdBindedByUserId(param);
        if (IDataUtil.isEmpty(bindRelIds)) {
            result.put("X_RESULTCODE", "1117");
            result.put("X_RESULTINFO", "您未绑定该车牌号[" + input.getString("REL_ID") + "]");
            return result;
        }
        //已入场车辆不能解绑
        String isIn = bindRelIds.first().getString("RSRV_STR8");
        if ("1".equals(isIn)) {
            result.put("X_RESULTCODE", "1118");
            result.put("X_RESULTINFO", "此车牌号正在使用全球通尊享停车位，无法操作");
            return result;
        }
        String modifyTag = BofConst.MODIFY_TAG_DEL;
        if (StringUtils.isNotBlank(input.getString("NEW_REL_ID"))) {

            boolean isNewCarNum = bean.checkCarNum(input.getString("NEW_REL_ID"));
            if (!isNewCarNum) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "请输入正确车牌号");
            }
            if (input.getString("NEW_REL_ID").equals(input.getString("REL_ID"))) {
                result.put("X_RESULTCODE", "1119");
                result.put("X_RESULTINFO", "您未修改原绑定的车牌号，无法变更车牌");
                return result;
            }

            param.clear();
            param.put("REL_ID", input.getString("NEW_REL_ID"));
            param.put("RIGHT_ID", input.getString("RIGHT_ID"));
            param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
            param.put("USER_ID", userinfo.getString("USER_ID"));
            if (bean.checkRelIdBinded(param) || bean.checkRelIdIsTrading(param)) {
                result.put("X_RESULTCODE", "1120");
                result.put("X_RESULTINFO", "此车牌号已被其它客户绑定使用，无法绑定车牌");
                return result;
            }
            modifyTag = BofConst.MODIFY_TAG_UPD;

            //  判断用户是否有变更次数
            param.clear();
            param.put("RIGHT_ID", input.getString("RIGHT_ID"));
            param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
            param.put("USER_ID", userinfo.getString("USER_ID"));
            boolean isHaveChangeNum = bean.checkBenefitChangeNum(param);
            if (!isHaveChangeNum) {
                result.put("X_RESULTCODE", "1114");
                result.put("X_RESULTINFO", "您变更绑定车牌的次数为0，无法变更绑定车牌");
                return result;
            }

            //  判断用户是否有使用次数
            param.clear();
            param.put("USER_ID", userinfo.getString("USER_ID"));
            param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            param.put("RIGHT_ID", input.getString("RIGHT_ID"));
            param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
            param.put("REL_ID", input.getString("REL_ID"));
            int remainUseNum = bean.queryRemainUseNum(param);
            if (remainUseNum <= 0) {
                result.put("X_RESULTCODE", "1116");
                result.put("X_RESULTINFO", "您无免费停车次数，无法绑定车牌");
                return result;
            }
        }

        //变更权益标识,该接口进来的都为新增权益标识
        param.clear();
        param.put("MODIFY_TAG", modifyTag);
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        param.put("REL_ID", input.getString("REL_ID"));
        param.put("NEW_REL_ID", input.getString("NEW_REL_ID"));
        CSAppCall.call("SS.BenefitBindRelChangeSVC.tradeReg", param);
        return result;
    }

    /**
     * 客户与权益关系查询接口校验权益关系接口
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryBenefitRelDetail(IData input) throws Exception {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "RIGHT_ID");
        IDataUtil.chkParam(input, "DISCNT_CODE");

        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "成功");
        //0.获取用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userinfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该用户信息");
        }
        //1. SERIAL_NUMBER->tf_f_user_info_class查全球通等级
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        IData userClass = UserClassInfoQry.queryUserClassBySN(param);
        String userClassTag = "0";
        if (IDataUtil.isNotEmpty(userClass)) {
            userClassTag = userClass.getString("USER_CLASS", "0");
        }
        result.put("USER_CLASS", userClassTag);
        //2.查询关联权益标识
        param.clear();
        param.put("USER_ID", userinfo.getString("USER_ID"));
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        IDataset benefitRelIds = bean.queryBenefitRelId(param);
        if (IDataUtil.isNotEmpty(benefitRelIds)) {
            result.put("REL_ID", benefitRelIds.first().getString("RSRV_VALUE"));
        }
        //3查询可变更次数
        param.clear();
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        param.put("USER_ID", userinfo.getString("USER_ID"));
        int benefitChangeNum = bean.queryBenefitChangeNum(param);
        result.put("REMAIN_CHANGE_NUM", benefitChangeNum);
        //4查询权益使用次数
        param.clear();
        param.put("USER_ID", userinfo.getString("USER_ID"));
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        int remainUseNum = bean.queryRemainUseNum(param);
        result.put("REMAIN_USE_NUM", String.valueOf(remainUseNum));
        //5查询权益总使用次数
        param.clear();
        param.put("USER_ID", userinfo.getString("USER_ID"));
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        param.put("DISCNT_CODE", input.getString("DISCNT_CODE"));
        int allUseNum = bean.queryAllUseNum(param);
        result.put("RIGHT_OBJ", String.valueOf(allUseNum));
        return result;
    }

    /**
     * 客户与权益关系查询接口(校验权益关系接口)(暂时不使用)
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryBenefitRel(IData input) throws Exception {
        //入参RIGHT_ID  REL_ID  SERIAL_NUMBER(可为空)
        /**
         * 机场权益RIGHT_ID==1
         * 若只传REL_ID(车牌号)
         *  查询tf_f_user_other 根据RELID查询USERID
         *      未找到 该车牌无权益
         *      找到
         *          根据USERID查询tf_f_user_other RIGHT_USE_RECORD(权益使用次数)
         *          根据USERID查询tf_f_user_info_class全球通等级,查询用户优惠tf_f_user_discnt
         *          查询td_s_commpara权益配置,匹配对应上限次数
         *  出参CLASS_INFO  RIGHT_NANE  RIGHT_OBJ  RIGHT_USE_TIME
         */

        IDataUtil.chkParam(input, "RIGHT_ID");

        if (StringUtils.isBlank(input.getString("SERIAL_NUMBER"))
                && StringUtils.isBlank(input.getString("REL_ID"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "接口参数检查: 输入参数[SERIAL_NUMBER][REL_ID]不可都为空");
        }

        if (StringUtils.isBlank(input.getString("SERIAL_NUMBER"))) {
            //根据REL_ID找到对应SERIAL_NUMBER
            IDataset UserOtherInfos = UserOtherInfoQry.getUserIdByRelID(PersonConst.BENEFIT_TAG, input.getString("REL_ID")
                    , input.getString("RIGHT_ID"), input.getString("DISCNT_CODE"));
            if (IDataUtil.isEmpty(UserOtherInfos)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据权益关联标识未找到用户");
            }
            IDataset userInfos = UserInfoQry.selUserInfo(UserOtherInfos.first().getString("USER_ID"));
            input.put("SERIAL_NUMBER", userInfos.first().getString("SERIAL_NUMBER"));
        }

        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "成功");
        IDataset rightRelList = new DatasetList();
        result.put("RIGHT_REL_LIST", rightRelList);
        //0.获取用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userinfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该用户信息");
        }

        //1. SERIAL_NUMBER->tf_f_user_info_class查全球通等级
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        IData userClass = UserClassInfoQry.queryUserClassBySN(param);
        String userClassTag = "0";
        if (IDataUtil.isNotEmpty(userClass)) {
            userClassTag = userClass.getString("USER_CLASS", "0");
        }

        //2.根据USER_ID,RIGHT_ID查询所有的权益标识记录
        param.clear();
        param.put("USER_ID", userinfo.getString("USER_ID"));
        if (!"0".equals(input.getString("RIGHT_ID"))) {
            param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        }
        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        IDataset benefitRelIds = bean.queryBenefitRelId(param);
        if (IDataUtil.isEmpty(benefitRelIds)) {
            return result;
        }

        for (int i = 0; i < benefitRelIds.size(); i++) {
            IData benefitRelId = benefitRelIds.getData(i);
            IData rightRel = new DataMap();
            rightRel.put("USER_CLASS", userClassTag);
            rightRel.put("REL_ID", benefitRelId.getString("RSRV_VALUE"));
            rightRel.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            rightRel.put("RIGHT_ID", benefitRelId.getString("RSRV_STR1"));
            rightRel.put("DISCNT_CODE", benefitRelId.getString("RSRV_STR2"));
            rightRel.put("RIGHT_OBJ", benefitRelId.getString("RSRV_STR3"));
            //查询权益名称
            param.clear();
            param.put("RIGHT_ID", benefitRelId.getString("RSRV_STR1"));
            param.put("DISCNT_CODE", benefitRelId.getString("RSRV_STR2"));
            IData benefitConfig = bean.queryBenefitConfig(param);
            if (IDataUtil.isEmpty(benefitConfig)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据权益编码未找到对应配置");
            }
            rightRel.put("RIGHT_NAME", benefitConfig.getString("PARA_CODE5"));
            //查询可变更次数
            param.clear();
            param.put("RIGHT_ID", benefitRelId.getString("RSRV_STR1"));
            param.put("DISCNT_CODE", benefitRelId.getString("RSRV_STR2"));
            param.put("USER_ID", userinfo.getString("USER_ID"));
            int benefitChangeNum = bean.queryBenefitChangeNum(param);
            rightRel.put("REMAIN_CHANGE_NUM", benefitChangeNum);

            //查询权益使用次数
            param.clear();
            param.put("RIGHT_ID", benefitRelId.getString("RSRV_STR1"));
            param.put("DISCNT_CODE", benefitRelId.getString("RSRV_STR2"));
            param.put("USER_ID", userinfo.getString("USER_ID"));
            param.put("START_DATE", benefitRelId.getString("START_DATE"));
            IDataset benefitUseRecords = bean.queryBenefitUseRecord(param);
            if (IDataUtil.isEmpty(benefitUseRecords)) {
                rightRel.put("RIGHT_USE", "0");
            } else {
                int rightUseCount = 0;
                for (int j = 0; j < benefitUseRecords.size(); j++) {
                    IData benefitUseRecord = benefitUseRecords.getData(j);
                    if (StringUtils.isBlank(benefitUseRecord.getString("RSRV_VALUE"))) {
                        continue;
                    }
                    rightUseCount += benefitUseRecord.getInt("RSRV_VALUE");
                }
                rightRel.put("RIGHT_USE", String.valueOf(rightUseCount));
            }

            rightRelList.add(rightRel);
        }
        result.put("RIGHT_REL_LIST", rightRelList);
        return result;
    }

    /**
     * 根据车牌号查询绑定手机号吗
     *add by liwei29
     * @param input
     * @return
     * @throws Exception
     */
    public IData querySnByRelId(IData input) throws Exception {
        IDataUtil.chkParam(input, "REL_ID");
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "成功");
        IData param = new DataMap();
        param.put("RSRV_VALUE", input.getString("REL_ID"));
        param.put("RIGHT_ID", "1");
        param.put("DISCNT_CODE", "717171");
        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        IDataset benefitRelIds = bean.querySnByRelId(param);
        if (IDataUtil.isEmpty(benefitRelIds)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据车牌未找到绑定的号码！");
        }
        String userId = benefitRelIds.first().getString("USER_ID","");
        IData data = UcaInfoQry.qryUserInfoByUserId(userId);
        String SERIAL_NUMBER = data.getString("SERIAL_NUMBER","");
        result.put("SERIAL_NUMBER", SERIAL_NUMBER);
        result.put("USER_ID", userId);
        return result;
    }

    /**
     * 客户使用权益明细查询
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryBenefitUseDetail(IData input) throws Exception {
        IDataUtil.chkParam(input, "RIGHT_ID");
        IDataUtil.chkParam(input, "SERIAL_NUMBER");

        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "成功");

        //获取用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userinfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该用户信息");
        }

        IData param = new DataMap();
        if (!"0".equals(input.getString("RIGHT_ID"))) {
            param.put("RIGHT_ID", input.getString("RIGHT_ID"));
        }
        param.put("USER_ID", userinfo.getString("USER_ID"));
        //电渠H5页面查询最近半年使用记录
        param.put("START_DATE", SysDateMgr.addMonths(SysDateMgr.getSysTime(), -6));
        param.put("END_DATE", SysDateMgr.getSysTime());
        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        IDataset benefitUseRecords = bean.queryBenefitUseRecordByDate(param);
        if (IDataUtil.isEmpty(benefitUseRecords)) {
            return result;
        }
        DatasetList useDetailList = new DatasetList();
        for (int i = 0; i < benefitUseRecords.size(); i++) {
            IData benefitUseRecord = benefitUseRecords.getData(i);
            DataMap useDetail = new DataMap();
            useDetail.put("START_DATE", benefitUseRecord.getString("START_DATE"));
            useDetail.put("END_DATE", benefitUseRecord.getString("RSRV_DATE1",benefitUseRecord.getString("END_DATE")));
            useDetail.put("RIGHT_ID", benefitUseRecord.getString("RSRV_STR1"));
            useDetail.put("DISCNT_CODE", benefitUseRecord.getString("RSRV_STR2"));
            useDetail.put("REL_ID", benefitUseRecord.getString("RSRV_STR3"));
            useDetail.put("RIGHT_USE_NUM", benefitUseRecord.getString("RSRV_VALUE"));
            useDetailList.add(useDetail);
        }
        result.put("RIGHT_USE_LIST", useDetailList);
        return result;
    }

    /**
     * 权益使用接口--停简停入场通知接口
     * 车辆入场时停简单调用该接口记录入场信息
     * @param input
     * @return
     * @throws Exception
     */
    public static IData benefitUseInNoticeForTingJD(IData input) throws Exception {
        //入参REL_ID权益关联的标识   SERIAL_NUMBER 可为空
        //      DISCNT_CODE     START_DATE      END_DATE
        //1校验入参
        IData result = new DataMap();
        result.put("isSuccess", "0");
        result.put("errorMSG", "成功");
        try{
            IDataUtil.chkParam(input, "carNum");
            IDataUtil.chkParam(input, "inDt");
            //2调权益使用接口
            IData param = new DataMap();
            param.put("REL_ID", input.getString("carNum"));
            param.put("RIGHT_ID", input.getString("rightId", PersonConst.BENEFIT_AIRPORT));
            param.put("DISCNT_CODE", input.getString("discntCode", PersonConst.BENEFIT_AIRPORT_FREE_PARKING));
            if (input.getString("inDt").length() != 14) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入日期格式不正确");
            }
            param.put("START_DATE", SysDateMgr.getDateForSTANDYYYYMMDDHHMMSS(input.getString("inDt")));
            param.put("MODIFY_TAG", "0");
            param.put("RIGHT_USE_INFO", input);
            CSAppCall.call("SS.BenefitUseRegSVC.tradeReg", param);
        }catch (Exception e){
            result.put("isSuccess", "1");
            result.put("errorMSG", e.getMessage());

            BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
            IData paramInfo = new DataMap();
            paramInfo.put("SERVICE","benefitUseInNoticeForTingJD");
            paramInfo.put("TRADE_TYPE_CODE","713");
            paramInfo.put("ERROR_MSG",e.getMessage());
            paramInfo.put("RIGHT_ID",input.getString("rightId", PersonConst.BENEFIT_AIRPORT));
            paramInfo.put("DISCNT_CODE",input.getString("discntCode", PersonConst.BENEFIT_AIRPORT_FREE_PARKING));
            paramInfo.put("REL_ID",input.getString("carNum"));
            paramInfo.put("RSRV_STR4",input.getString("inDt"));
            paramInfo.put("RSRV_STR5",input.getString("outDt"));
            paramInfo.put("RSRV_STR6",input.getString("tradeId"));
            bean.insertErrorLog(paramInfo);
        }
        return result;
    }

    /**
     * 权益使用接口--停简停出场通知接口
     * 车辆入场时停简单调用该接口记录出场信息
     * 该接口记录停车使用时间,正常情况成功记录
     * 若个别情况登记台账出现异常,无法控制车辆停车行为,不能记录该车辆当前停车使用次数
     * @param input
     * @return
     * @throws Exception
     */
    public static IData benefitUseOutNoticeForTingJD(IData input) throws Exception {
        //入参REL_ID权益关联的标识   SERIAL_NUMBER 可为空
        //      DISCNT_CODE     START_DATE      END_DATE
        //1校验入参
        IData result = new DataMap();
        result.put("isSuccess", "0");
        result.put("errorMSG", "成功");
        try{
            IDataUtil.chkParam(input, "carNum");
            IDataUtil.chkParam(input, "tradeId");
            IDataUtil.chkParam(input, "inDt");
            IDataUtil.chkParam(input, "outDt");

            if (input.getString("inDt").length() != 14 || input.getString("outDt").length() != 14) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入日期格式不正确");
            }
            IDataset isCacheTags = CommparaInfoQry.getCommparaAllColByParser("CSM", PersonConst.BENEFIT_STATIC_CONFIG, "ISCACHETAG",  "0898");
            if(IDataUtil.isNotEmpty(isCacheTags)&&"1".equals(isCacheTags.first().getString("PARA_CODE1"))){
                String cacheTag="benefitUseOutNoticeForTingJD";
                String carNumCache = (String)SharedCache.get(cacheTag + input.getString("carNum"));
                if(StringUtils.isNotBlank(carNumCache)&&input.getString("tradeId").equals(carNumCache)){
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, input.getString("tradeId")+"出场通知重复调用");
                }
                SharedCache.set(cacheTag+ input.getString("carNum"), input.getString("tradeId"), isCacheTags.first().getInt("PARA_CODE2",60*2));
            }else{
                //停简停重试情况下,判断boss系统是否已入该笔台账,跟据停简单传的tradeId判断是否已入台账,已有记录直接返回
                IDataset userOthersByTingJDTradeID = UserOtherInfoQry.getByTingJDTradeID(input.getString("tradeId"));
                if(IDataUtil.isNotEmpty(userOthersByTingJDTradeID)){
                    result.put("isSuccess", "1");
                    result.put("errorMSG", "出场通知已记录");
                    return result;
                }
            }

            //BUG20200420112956 机场停车权益重复使用记录BUG start//没控制住,加缓存处理
            //查询tradeOther子台账
//            IDataset userInfos = UserOtherInfoQry.getUserIdByRelID(PersonConst.BENEFIT_TAG, input.getString("carNum"),
//                    input.getString("rightId", PersonConst.BENEFIT_AIRPORT)
//                    , input.getString("discntCode", PersonConst.BENEFIT_AIRPORT_FREE_PARKING));
//            if(IDataUtil.isEmpty(userInfos)){
//                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据权益关联标识未找到用户");
//            }
//            IDataset tradeOtherByRsrvStr4 = TradeOtherInfoQry.getTradeOtherByRsrvStr4(userInfos.first().getString("USER_ID"), input.getString("tradeId"));
//            if(IDataUtil.isNotEmpty(tradeOtherByRsrvStr4)){
//                CSAppException.apperr(CrmCommException.CRM_COMM_103, "出场通知已记录");
//            }
            //BUG20200420112956 机场停车权益重复使用记录BUG end
            //停简停重试情况下,判断boss系统是否已入该笔台账,跟据停简单传的tradeId判断是否已入台账,已有记录直接返回
//            IDataset userOthersByTingJDTradeID = UserOtherInfoQry.getByTingJDTradeID(input.getString("tradeId"));
//            if(IDataUtil.isNotEmpty(userOthersByTingJDTradeID)){
//                result.put("isSuccess", "1");
//                result.put("errorMSG", "出场通知已记录");
//                return result;
//            }

            //2调权益使用接口
            IData param = new DataMap();
            param.put("REL_ID", input.getString("carNum"));
            param.put("RIGHT_ID", input.getString("rightId", PersonConst.BENEFIT_AIRPORT));
            param.put("DISCNT_CODE", input.getString("discntCode", PersonConst.BENEFIT_AIRPORT_FREE_PARKING));
            param.put("START_DATE", SysDateMgr.getDateForSTANDYYYYMMDDHHMMSS(input.getString("inDt")));
            param.put("END_DATE", SysDateMgr.getDateForSTANDYYYYMMDDHHMMSS(input.getString("outDt")));
            param.put("MODIFY_TAG", "2");
            param.put("RIGHT_USE_INFO", input);
            CSAppCall.call("SS.BenefitUseRegSVC.tradeReg", param);
        }catch (Exception e){
            result.put("isSuccess", "1");
            result.put("errorMSG", e.getMessage());

            BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
            IData paramInfo = new DataMap();
            paramInfo.put("SERVICE","benefitUseOutNoticeForTingJQ");
            paramInfo.put("TRADE_TYPE_CODE","713");
            paramInfo.put("ERROR_MSG",e.getMessage());
            paramInfo.put("RIGHT_ID",input.getString("rightId", PersonConst.BENEFIT_AIRPORT));
            paramInfo.put("DISCNT_CODE",input.getString("discntCode", PersonConst.BENEFIT_AIRPORT_FREE_PARKING));
            paramInfo.put("REL_ID",input.getString("carNum"));
            paramInfo.put("RSRV_STR4",input.getString("inDt"));
            paramInfo.put("RSRV_STR5",input.getString("outDt"));
            paramInfo.put("RSRV_STR6",input.getString("tradeId"));
            bean.insertErrorLog(paramInfo);
        }
        return result;
    }

    /**
     * 容错接口,若出现已完工,且预约车辆成功已入表,用户无法入场,手动调用车辆预约
     * @param input
     * @return
     * @throws Exception
     */
    public static IData bindRelIdErrorForTingJD(IData input) throws Exception {

        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "REL_ID");

        IData userinfo = UserInfoQry.getUserInfoBySN(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userinfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该用户信息");
        }
        IData param = new DataMap();
        param.put("USER_ID",userinfo.getString("USER_ID"));
        param.put("RIGHT_ID",PersonConst.BENEFIT_AIRPORT);
        param.put("DISCNT_CODE",PersonConst.BENEFIT_AIRPORT_FREE_PARKING);
        param.put("REL_ID",input.getString("REL_ID"));

        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        IDataset benefitRelIdInfos = bean.queryRelIdBindedByUserId(param);
        if(IDataUtil.isEmpty(benefitRelIdInfos)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "手机号未找到对应车牌号");
        }
        IData  benefitRelIdInfo = benefitRelIdInfos.first();
        String orderId = benefitRelIdInfo.getString("RSRV_STR6");
        String orderIdTag = benefitRelIdInfo.getString("RSRV_STR7");
        if(StringUtils.isNotBlank(orderId)&&"1".equals(orderIdTag)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该车牌已进行预约");
        }

        IData abilityData = new DataMap();
        abilityData.put("carNum", input.getString("REL_ID"));
        abilityData.put("service", PersonConst.BENEFIT_TINGJD_BOOKADD_SERVICE);
        abilityData.put("authStartDt",  SysDateMgr.date2String(SysDateMgr.string2Date(benefitRelIdInfo.getString("START_DATE"), SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));
        abilityData.put("authEndDt", SysDateMgr.date2String(SysDateMgr.string2Date(benefitRelIdInfo.getString("END_DATE"), SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));
        abilityData.put("bookStartDt",SysDateMgr.date2String(SysDateMgr.string2Date(benefitRelIdInfo.getString("START_DATE"), SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));
        abilityData.put("bookEndDt", SysDateMgr.date2String(SysDateMgr.string2Date(benefitRelIdInfo.getString("END_DATE"), SysDateMgr.PATTERN_STAND),SysDateMgr.PATTERN_STAND_SHORT));

        IDataset partners = CommparaInfoQry.getCommparaAllColByParser("CSM", PersonConst.BENEFIT_STATIC_CONFIG, PersonConst.BENEFIT_STATIC_ENTERPRISE, "0898");
        if (IDataUtil.isEmpty(partners) || StringUtils.isBlank(partners.first().getString("PARA_CODE1"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "停简单COMMPARAM7174未配置ENTERPRISE");
        }
        abilityData.put("enterpriseId", partners.first().getString("PARA_CODE1"));
        IData bookAddResult = bean.callAbilityForTingJQ(PersonConst.BENEFIT_TINGJD_BOOKADD_URL, abilityData);
        IData result = new DataMap();
        result.put("isSuccess", "0");
        result.put("errorMSG", "成功");
        result.put("orderId", bookAddResult.getString("orderId"));

        benefitRelIdInfo.put("RSRV_STR6",bookAddResult.getString("orderId"));
        benefitRelIdInfo.put("RSRV_STR7","1");
        Dao.update("TF_F_USER_OTHER", benefitRelIdInfo,new String[]{"INST_ID","PARTITION_ID"});

        return result;
    }


    /**
     * 容错接口,若出现需要手动解除预约
     * @param input
     * @return
     * @throws Exception
     */
    public static IData changeBindRelIdErrorForTingJD(IData input) throws Exception {

        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "REL_ID");

        IData userinfo = UserInfoQry.getUserInfoBySN(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userinfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该用户信息");
        }
        IData param = new DataMap();
        param.put("USER_ID",userinfo.getString("USER_ID"));
        param.put("RIGHT_ID", PersonConst.BENEFIT_AIRPORT);
        param.put("DISCNT_CODE",PersonConst.BENEFIT_AIRPORT_FREE_PARKING);
        param.put("REL_ID",input.getString("REL_ID"));

        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        IDataset benefitRelIdInfos = bean.queryRelIdBindedByUserId(param);
        if(IDataUtil.isEmpty(benefitRelIdInfos)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "手机号未找到对应车牌号");
        }
        IData  benefitRelIdInfo = benefitRelIdInfos.first();
        String orderId = benefitRelIdInfo.getString("RSRV_STR6");
        String orderIdTag = benefitRelIdInfo.getString("RSRV_STR7");
        if(StringUtils.isNotBlank(orderId)&&"0".equals(orderIdTag)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该车牌已取消预约");
        }

        IData abilityData = new DataMap();
        abilityData.put("orderId", orderId);
        abilityData.put("service", PersonConst.BENEFIT_TINGJD_BOOKREMOVE_SERVICE);
        bean.callAbilityForTingJQ(PersonConst.BENEFIT_TINGJD_BOOKREMOVE_URL, abilityData);
        IData result = new DataMap();
        result.put("isSuccess", "0");
        result.put("errorMSG", "成功");

        benefitRelIdInfo.put("RSRV_STR7","0");
        Dao.update("TF_F_USER_OTHER", benefitRelIdInfo,new String[]{"INST_ID","PARTITION_ID"});

        return result;
    }
    
    /**
     * 权益申请接口
     * REQ202004290001关于与全网权益平台对接的需求
     * by chenyw7
     * @param input
     * @return
     * @throws Exception
     */
    public static IData applyRightsInterests(IData input) throws Exception {
    	logger.debug("================BenefitCenterIntfSVC.BenefitCenterIntfSVC  input:"+input);
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "成功");
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String discntCodes =IDataUtil.chkParam(input, "DISCNT_CODES");
        String[] discntCodeArr = discntCodes.split("#");
        if(discntCodeArr.length > 0){
        	if(discntCodeArr.length > 2){
        		result.put("X_RESULTCODE", "0002");
                result.put("X_RESULTINFO", "格式不正确");
                return result;
        	}else{
        		IDataset userInfos = UserInfoQry.getAllUserInfoBySn(serialNumber);
        		logger.debug("================BenefitCenterIntfSVC.BenefitCenterIntfSVC  userInfos:"+userInfos);
    	    	if(IDataUtil.isNotEmpty(userInfos)){
    	    		String userId = userInfos.getData(0).getString("USER_ID");
    	    		String custId = userInfos.getData(0).getString("CUST_ID");
    	    		IDataset discntList = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(userId, "84019050", null);//查询用户是否办理随心选会员优惠
    	    		logger.debug("================BenefitCenterIntfSVC.BenefitCenterIntfSVC  discntList:"+discntList);
    	    		if(IDataUtil.isNotEmpty(discntList)){
    	    			BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
    	    			//获取本月权益
    	    			IDataset dataList = bean.queryRightsInterests(serialNumber);
    	    			logger.debug("================BenefitCenterIntfSVC.BenefitCenterIntfSVC  dataList:"+dataList);
    	    			if(IDataUtil.isNotEmpty(dataList)){
    	    				if(dataList.size() >= 2){
    	    					result.put("X_RESULTCODE", "0005");
    	                        result.put("X_RESULTINFO", "本月已领取2项会员");
    	                        return result;
    	    				}else if(dataList.size() == 1 && discntCodeArr.length >=2){
    	    					result.put("X_RESULTCODE", "0006");
    	                        result.put("X_RESULTINFO", "本月已领取了1项会员，只允许再领取1项会员");
    	                        return result;
    	    				}
    	    				
    	    				for (int i = 0; i < dataList.size(); i++) {
    	    					for (int j = 0; j < discntCodeArr.length; j++) {
    	    						String discntCode = dataList.getData(i).getString("DISCNT_CODE","");
									if(discntCode.equals(discntCodeArr[j])){//本月已领取过该权益
										result.put("X_RESULTCODE", "0008");
		    	                        result.put("X_RESULTINFO", "本月已领取过该权益");
		    	                        return result;
									}
								}
							}
    	    			}
    	    			
    	    			
    	    			//拼接能开参数
    	    			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    	    			Date date = new Date();
    	    			IData abilityData = new DataMap();
    	    			String dateStr = sdf.format(date);
    	    			String tradeId = SeqMgr.getTradeIdFromDb();
    	    			String orderId = "QY"+dateStr+tradeId;//订单号
    	    			
    	    			
    	    			if(orderId.length() > 32){
    	    				orderId = orderId.substring(0,32);//若订单号大于32位，则截取前32位
    	    			}else if(orderId.length() < 32){//若订单号小于32位，则补足32位
    	    				for(int i = orderId.length(); i < 32; i++){
    	    					if(i == 31){
    	    						orderId = orderId+"1";
    	    					}else{
    	    						orderId = orderId+"0";
    	    					}
    	    				}
    	    			}
    	    			
    	    			//获取客户信息
    	    			IData custData = UcaInfoQry.qryCustomerInfoByCustId(custId);
    	    			String custName  =  custData.getString("CUST_NAME");
    	    			
    	    			abilityData.put("orderId", orderId);//订单编码
    	    			abilityData.put("createTime", dateStr);//订单创建时间
    	    			abilityData.put("buyerNickname", custName);//买家名称
    	    			
    	    			IData paymentInfo =  new DataMap();//支付信息
    	    			paymentInfo.put("chargeType", "1");//扣费类型  0：统一支付扣费；1：话费扣费; 2：第三方扣费；3：统一支付话费充值；4：线下支付；5：积分支付6：能力平台支付
    	    			paymentInfo.put("payment", 0);//实际支付金额 单位：分
    	    			paymentInfo.put("paymentTime",dateStr);//支付时间
    	    			abilityData.put("paymentInfo", paymentInfo);
    	    			
    	    			abilityData.put("needDistribution", "2");//是否需配送 1-需要；2-不需要
    	    			abilityData.put("name",custName);//收货人姓名
    	    			abilityData.put("province","");//收货人所在省份
    	    			abilityData.put("city","");//收货人所在市
    	    			abilityData.put("district","");//收货人所在地区
    	    			abilityData.put("address","");//收货人的详细地址
    	    			abilityData.put("mobilephone",serialNumber);//收货人手机号码
    	    			abilityData.put("needInvoice","2");//是否需要开发票  1-需要；2-不需要
    	    			
    	    			IDataset subOrderList = new DatasetList();//子订单集合
    	    			
    	    			for (int i = 0; i < discntCodeArr.length; i++) {
    	    				String discntCode = discntCodeArr[i];
    	    				IData subOrderData = new DataMap();
    	    				
    	    				String subTradeId = SeqMgr.getTradeIdFromDb();
        	    			String subOrderId = "QY"+dateStr+subTradeId;//子订单号
        	    			
        	    			
        	    			if(subOrderId.length() > 32){
        	    				subOrderId = subOrderId.substring(0,32);//若子订单号大于32位，则截取前32位
        	    			}else if(subOrderId.length() < 32){//若子订单号小于32位，则补足32位
        	    				for(int j = subOrderId.length(); j < 32; j++){
        	    					if(j == 31){
        	    						subOrderId = subOrderId+"1";
        	    					}else{
        	    						subOrderId = subOrderId+"0";
        	    					}
        	    				}
        	    			}
        	    			
        	    			String goodsId = "";
        	    			String goodsTitle = "";
        	    			String corGoodsId = "";
        	    			IDataset commparaInfo = CommparaInfoQry.getCommNetInfo("CSM", "5891", discntCode);//根据优惠编码查询td_s_commpara的能开编码
        	    			if(IDataUtil.isNotEmpty(commparaInfo)){
        	    				goodsId = commparaInfo.getData(0).getString("PARA_CODE1");
        	    				goodsTitle = commparaInfo.getData(0).getString("PARA_CODE2");
        	    				corGoodsId = commparaInfo.getData(0).getString("PARA_CODE4");
        	    			}else{
        	    				continue;
        	    			}
    	    				
    	    				subOrderData.put("subOrderId", subOrderId);//子订单编码
    	    				subOrderData.put("discntCode", discntCode);//优惠编码
    	    				
    	    				IData subscriberInfo = new DataMap();//用户号码信息
    	    				subscriberInfo.put("numberOprType", "12");//业务号码操作类型  12：赠送类权益订购（即权益领取）
    	    				subscriberInfo.put("number", serialNumber);//业务号码
    	    				subscriberInfo.put("numberType", "1");//业务号码类型  1- 手机号；2- 宽带号码
    	    				subOrderData.put("subscriberInfo", subscriberInfo);
    	    				
    	    				
    	    				IData goodsInfo = new DataMap();//订购商品信息
    	    				goodsInfo.put("goodsId", goodsId);//商品编码
    	    				goodsInfo.put("goodsTitle", goodsTitle);//商品标题
    	    				goodsInfo.put("amount", 1);//购买数量
    	    				goodsInfo.put("corGoodsId", corGoodsId);//关联商品编码
    	    				goodsInfo.put("price", 0);//商品价格  单位：分
    	    				goodsInfo.put("goodsProvince", "海南");//商品所在省份
    	    				goodsInfo.put("goodsCity", "海南");//商品所在城市
    	    				subOrderData.put("goodsInfo", goodsInfo);
    	    				
    	    				subOrderData.put("subtotalFee", 0);//子订单总金额  单位：分
    	    				subOrderData.put("adjustFee", 0);//手工调整金额  单位：分
    	    				subOrderData.put("orderStatus", "00");//订单状态  00 初始状态
    	    				
    	    				subOrderList.add(subOrderData);
						}
    	    			
    	    			abilityData.put("subOrderList",subOrderList);
    	    			logger.debug("BenefitCenterIntfSVC.applyRightsInterests abilityData = " + abilityData);
    	    			
    	    			if(IDataUtil.isNotEmpty(subOrderList)){
    	    				
    	    				//调用能开接口
        	    			IData retData = callAbilityCIP00080(abilityData);
        	    			
        	    			
        	    			if (IDataUtil.isNotEmpty(retData)) {
        	    	            String resCode = retData.getString("resCode");
        	    	            String resMsg = retData.getString("resMsg");
        	    	            IData out = retData.getData("result");
        	    	            String bizCode = out.getString("bizCode");
        	    	            String bizDesc = out.getString("bizDesc");
        	    	            if ("00000".equals(resCode)) {
        	    	                if (!"0000".equals(bizCode)) {
        	    	                	logger.error("调用能开失败 ，原因："+bizDesc);
        	    	                	logger.error("调用能开参数：" + abilityData.toString());
        	    	                    logger.error("调用能开返回结果：" + retData.toString());
        	    	                    
        	    	                    result.put("X_RESULTCODE", "0007");
        	                            result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
        	                            return result;
        	    	                } else {
        	    	                	 // 调用成功 
        	    	                	logger.debug("调用能开成功");
        	    	                	logger.debug("调用能开参数：" + abilityData.toString());
        	    	                    logger.debug("调用能开返回结果：" + retData.toString());
        	    	                    
        	    	                    //入库
        	    	                    for (int i = 0; i < subOrderList.size(); i++) {
        	    	                    	IData subOrderData = subOrderList.getData(i);
        	    	                    	String subOrderId = subOrderData.getString("subOrderId");
        	    	                    	String discntCode = subOrderData.getString("discntCode");
        	    	                    	String goodsId = subOrderData.getData("goodsInfo").getString("goodsId");
        	    	                    	String goodsTitle = subOrderData.getData("goodsInfo").getString("goodsTitle");
        	    	                    	bean.insertRightsInterests(orderId,subOrderId,userId,serialNumber,discntCode,goodsId,goodsTitle);
											
										}
        	    	                   
        	    	                }
        	    	            } else {
        	    	            	logger.error("调用能开失败 ，原因："+resMsg);
        	    	                logger.error("调用能开参数：" + abilityData.toString());
        	    	                logger.error("调用能开返回结果：" + retData.toString());
        	    	                
        	    	                result.put("X_RESULTCODE", "0007");
    	                            result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
    	                            return result;
        	    	            }
        	    	        } 
    	    				
    	    			}
    	    			
    	    			
    	    			
    	    			
    	    		}else{
    	    			result.put("X_RESULTCODE", "0004");
                        result.put("X_RESULTINFO", "未办理“随心选会员升级版”优惠");
                        return result;
    	    		}
    	    	}else{
    	    		result.put("X_RESULTCODE", "0003");
                    result.put("X_RESULTINFO", "未找到用户信息");
                    return result;
    	    	}
        	}
        }else{
        	 result.put("X_RESULTCODE", "0001");
             result.put("X_RESULTINFO", "优惠编码为空");
             return result;
        }
        return result;
    }
    
    /**
     * 用能力开放平台CIP00080接口
     * by chenyw7
     * @param: input
     */
    private static IData callAbilityCIP00080(IData abilityData)  throws Exception {
        IData retData = new DataMap();
        String Abilityurl = "";
        IData param1 = new DataMap();
        param1.put("PARAM_NAME", "crm.ABILITY.CIP00080");
        StringBuilder getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ");
        IDataset Abilityurls;
		Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
		if (Abilityurls != null && Abilityurls.size() > 0) {
            Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
        } else {
            CSAppException.appError("-1", "crm.ABILITY.CIP00080接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = Abilityurl;
        retData = AbilityEncrypting.callAbilityPlatCommon(apiAddress, abilityData);
    	logger.debug("BenefitCenterIntfSVC.callAbilityCIP00080 retData = " + retData);
		return retData;
        
    }
    
    /**
     * 查询本月权益
     * REQ202004290001关于与全网权益平台对接的需求
     * by chenyw7
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset queryRightsInterests(IData input) throws Exception {
    	logger.debug("================BenefitCenterIntfSVC.queryRightsInterests  input:"+input);
    	IDataset resultList = new DatasetList();
    	
    	BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
    	String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
		//获取本月权益
		IDataset dataList = bean.queryRightsInterests(serialNumber);
		logger.debug("================BenefitCenterIntfSVC.queryRightsInterests  dataList:"+dataList);
		if(IDataUtil.isNotEmpty(dataList)){
			for (int i = 0; i < dataList.size(); i++) {
				IData data = new DataMap();
				String discntCode = dataList.getData(i).getString("DISCNT_CODE");
				String discntName = dataList.getData(i).getString("DISCNT_NAME");
				String discntTime = dataList.getData(i).getString("INSERT_TIME");
				data.put("SERIAL_NUMBER", serialNumber);
				data.put("DISCNT_CODE", discntCode);
				data.put("DISCNT_NAME", discntName);
				data.put("DISCNT_TIME", discntTime);
				resultList.add(data);
			}
		}
		logger.debug("================BenefitCenterIntfSVC.queryRightsInterests  resultList:"+resultList);
    	return resultList;
    	
    }
    
    

    /**
     * 批量业务调用接口增加权益使用次数
     * @param input
     * @return
     * @throws Exception
     */
    public static IData batAddRigntUseNum(IData input) throws Exception {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "ADD_USE_NUM");
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "成功");
        //1.查询手机号是否已办理权益,绑定车牌
        IData userinfo = UserInfoQry.getUserInfoBySN(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userinfo)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该用户信息");
        }
        String userId=userinfo.getString("USER_ID");
        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        DataMap param = new DataMap();
        IDataset lastOtherInfoCuryears = UserOtherInfoQry.getLastCuryear(userId, PersonConst.BENEFIT_TAG
                , null, PersonConst.BENEFIT_AIRPORT,PersonConst.BENEFIT_AIRPORT_FREE_PARKING);
        if(IDataUtil.isNotEmpty(lastOtherInfoCuryears)){
            //1.1绑定车牌,走变更使用次数业务
            param.clear();
            param.put("BATCH_ID",input.getString("BATCH_ID",""));
            param.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD);
            param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            param.put("RIGHT_ID", PersonConst.BENEFIT_AIRPORT);
            param.put("DISCNT_CODE", PersonConst.BENEFIT_AIRPORT_FREE_PARKING);
            param.put("ADD_USE_NUM", input.getString("ADD_USE_NUM"));
            IDataset callResult =CSAppCall.call("SS.BenefitAddUseNumRegSvc.tradeReg", param);
            result.put("ORDER_ID",callResult.first().getString("TRADE_ID"));
            return result;
        }

//        param.clear();
//        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
//        IData userClass = UserClassInfoQry.queryUserClassBySN(param);
//        String userClassTag = "0";
//        if (IDataUtil.isNotEmpty(userClass)) {
//            userClassTag = userClass.getString("USER_CLASS", "0");
//        }
//        param.clear();
//        param.put("RIGHT_ID", PersonConst.BENEFIT_AIRPORT);
//        param.put("DISCNT_CODE", PersonConst.BENEFIT_AIRPORT_FREE_PARKING);
//        param.put("CONDITION_KEY", "USERCLASS");
//        param.put("CONDITION_VALUE", userClassTag);
//        //1.2未绑定车牌,查询是否有配置次数
//        IDataset benefitConfigs = bean.queryBenefitConfigByCondition(param);
//        if(IDataUtil.isNotEmpty(benefitConfigs)){
//            int benefitNumConfig = benefitConfigs.first().getInt("PARA_CODE4");
//            if(benefitNumConfig>0){
//                //1.21有配置次数,需先绑定,无法增加次数
//                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已配置使用次数,还未绑定权益,请先绑定权益");
//            }
//        }
//        //1.22无配置次数,查询是否已配置增加使用次数权限
//        param.clear();
//        param.put("RIGHT_ID", PersonConst.BENEFIT_AIRPORT);
//        param.put("DISCNT_CODE", PersonConst.BENEFIT_AIRPORT_FREE_PARKING);
//        param.put("USER_ID", userinfo.getString("USER_ID"));
//        IDataset addRightUseNumConfig = bean.queryAddRightUseNumConfig(param);
//        if(IDataUtil.isNotEmpty(addRightUseNumConfig)){
//            //已配置增加使用次数权限,需先绑定,无法增加次数
//            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已配置增加使用次数权限,还未绑定权益,请先绑定权益");
//        }
        //1.23无配置次数,走增加配置使用次数业务类型
        //不增加拦截,为办理绑定权益,tf_f_user_other增加次数配置,可增加多条,办理绑定权益累加
        param.clear();
        param.put("BATCH_ID",input.getString("BATCH_ID",""));
        param.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("RIGHT_ID", PersonConst.BENEFIT_AIRPORT);
        param.put("DISCNT_CODE", PersonConst.BENEFIT_AIRPORT_FREE_PARKING);
        param.put("ADD_USE_NUM", input.getString("ADD_USE_NUM"));
        IDataset callResult = CSAppCall.call("SS.BenefitAddUseNumRegSvc.tradeReg", param);
        result.put("ORDER_ID",callResult.first().getString("TRADE_ID"));
        return result;

    }
}

