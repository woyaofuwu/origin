package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.MD5Util;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;

import java.util.Map;
import java.util.Set;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2019/12/11 11:28
 */
public class BenefitCenterBean extends CSBizBean {
    /**
     * 根据条件 匹配权益类型和优惠
     * @param param
     * @return
     * @throws Exception
     */
    public   boolean checkBenefitConfigByCondition(IData param) throws Exception{
        IDataset benefitConfigs = queryBenefitConfigByCondition(param);
        if(IDataUtil.isNotEmpty(benefitConfigs)){
            return true;
        }
        return false;
    }



    /**
     * 根据条件 查询权益类型和优惠
     * @param param
     * @return
     * @throws Exception
     */
    public   IDataset queryBenefitConfigByCondition(IData param) throws Exception{
        String rightId=param.getString("RIGHT_ID");
        String discntCode=param.getString("DISCNT_CODE");
        String benefitConditionKey =param.getString("CONDITION_KEY");
        String benefitConditionValue =param.getString("CONDITION_VALUE");
        IDataset benefitConfigs = CommparaInfoQry.getCommparaByCode1to3("CSM", PersonConst.BENEFIT_CONDITION_CONFIG
                ,benefitConditionKey, benefitConditionValue,rightId,discntCode,"0898");
        return benefitConfigs;
    }

    /**
     * 根据USER_ID,DISCNT_CODE判断是否已办理权益
     * @param param
     * @return
     * @throws Exception
     */
    public  boolean checkHaveBenefitDiscnt(IData param) throws Exception{
        String discntCode=param.getString("DISCNT_CODE");
        String userId =param.getString("USER_ID");
        IDataset userDiscntInfos = UserDiscntInfoQry.getUserByDiscntCode(discntCode, userId);
        if(IDataUtil.isNotEmpty(userDiscntInfos)){
            return true;
        }
        return false;
    }
    /**
     * 根据RIGHT_ID,DISCNT_CODE,USER_ID判断是否已绑定权益标识
     * @param param
     * @return
     * @throws Exception
     */
    public  boolean checkHaveBenefitRelId(IData param) throws Exception{
        String discntCode=param.getString("DISCNT_CODE");
        String userId =param.getString("USER_ID");
        String rightId =param.getString("RIGHT_ID");
        IDataset retOtherInfos = UserOtherInfoQry.getUserOtherInfoByBenefit(userId
                , PersonConst.BENEFIT_TAG,null,rightId,discntCode);
        if(IDataUtil.isNotEmpty(retOtherInfos)){
            return true;
        }
        return false;
    }

    /**
     * 根据RIGHT_ID,DISCNT_CODE,USER_ID判断是否已绑定权益标识
     * @param param
     * @return
     * @throws Exception
     */
    public  IDataset queryBenefitRelId(IData param) throws Exception{
        String userId =param.getString("USER_ID");
        String rightId =param.getString("RIGHT_ID");
        String discntCode =param.getString("DISCNT_CODE");
        return UserOtherInfoQry.getUserOtherInfoByBenefit(userId
                , PersonConst.BENEFIT_TAG,null,rightId,discntCode);
    }

    /**
     * 根据RIGHT_ID,DISCNT_CODE,Rel_Id判断绑定的车牌号
     * add by liwei29
     * @param param
     * @return
     * @throws Exception
     */
    public  IDataset querySnByRelId(IData param) throws Exception{
        String relid =param.getString("RSRV_VALUE");
        String rightId =param.getString("RIGHT_ID");
        String discntCode =param.getString("DISCNT_CODE");
        return UserOtherInfoQry.querySnByRelId(relid
                , PersonConst.BENEFIT_TAG,rightId,discntCode);
    }

    /**
     * 根据RIGHT_ID,DISCNT_CODE,USER_ID查询用户绑定权益标识REL_ID次数
     * @param param
     * @return
     * @throws Exception
     */
    public  int queryBenefitBindNum(IData param) throws Exception{
        String rightId=param.getString("RIGHT_ID");
        String discntCode=param.getString("DISCNT_CODE");
        String userId =param.getString("USER_ID");
//        int bindNum=0;
//        IDataset retOtherHisInfos = UserOtherInfoQry.getUserOtherInfoHisByBenefit(userId
//                , PersonConst.BENEFIT_TAG, rightId, discntCode);
//        IDataset retOtherInfos = UserOtherInfoQry.getUserOtherInfoByBenefit(userId
//                , PersonConst.BENEFIT_TAG,null,rightId,discntCode);
//        if(IDataUtil.isNotEmpty(retOtherHisInfos)){
//            bindNum+=retOtherHisInfos.size();
//        }
//        if(IDataUtil.isNotEmpty(retOtherInfos)){
//            bindNum+=retOtherInfos.size();
//        }
        //查询用户当年绑定表示次数
        IDataset benefitBindInfos = UserOtherInfoQry.getLastCuryear(userId, PersonConst.BENEFIT_TAG, null, rightId, discntCode);
        if(IDataUtil.isNotEmpty(benefitBindInfos)){
            return benefitBindInfos.size();
        }
        return 0;
    }

    /**
     * 根据RIGHT_ID,DISCNT_LEVEL,DISCNT_CODE查询权益配置,
     * 当DISCNT_CODE为机场权益,PARA_CODE3为机场权益免费停车次数
     *      若入参DISCNT_LEVEL为空表示暂定同一个权益不同等级变更次数配置相同
     *
     * @param param
     * @return
     * @throws Exception
     */
    public  IData queryBenefitConfig(IData param) throws Exception{
        String rightId=param.getString("RIGHT_ID");
        String discntLevel=param.getString("DISCNT_LEVEL");
        String discntCode=param.getString("DISCNT_CODE");
        //只根据discntCode查询利用索引且结果集size不大效率更高
        IDataset configs = CommparaInfoQry.getCommparaAllColByParser("CSM", PersonConst.BENEFIT_CODE_CONFIG, discntCode,  "0898");
        if(IDataUtil.isNotEmpty(configs)){
            for (int i = 0; i < configs.size(); i++) {
                IData config = configs.getData(i);
                if(!rightId.equals(config.getString("PARA_CODE2"))){
                    continue;
                }
                if((StringUtils.isBlank(discntLevel)||discntLevel.equals(config.getString("PARA_CODE1")))){
                    return config;
                }
            }
        }
        return new DataMap();
    }

    /**
     * 校验权益变更次数
     * @param param
     * @return
     * @throws Exception
     */
    public  boolean checkBenefitChangeNum(IData param) throws Exception{
        return  queryBenefitChangeNum(param)>0;
    }

    /**
     * 查询权益可变更次数
     * @param param
     * @return
     * @throws Exception
     */
    public  int queryBenefitChangeNum(IData param) throws Exception{
        int benefitBindNum = queryBenefitBindNum(param);
        IData benefitConfig = queryBenefitConfig(param);
        if(IDataUtil.isEmpty(benefitConfig)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"根据权益编码未找到对应配置");
        }
        int benefitChangeNumconfig = benefitConfig.getInt("PARA_CODE3");
        if(benefitBindNum==0){
            return  benefitChangeNumconfig;
        }else{
            return  benefitChangeNumconfig+1-benefitBindNum;
        }

    }
    /**
     * 权益使用记录
     * @param param
     * @return
     * @throws Exception
     */
    public  IDataset queryBenefitUseRecord(IData param) throws Exception{
        String discntCode=param.getString("DISCNT_CODE");
        String userId =param.getString("USER_ID");
        String rightId =param.getString("RIGHT_ID");
        String relId =param.getString("REL_ID");
        String startDate =param.getString("START_DATE");
        return UserOtherInfoQry.getRightUseRecordByUserId(userId, rightId, discntCode, relId,startDate);
    }
    /**
     * 根据时间查询权益使用记录
     * @param param
     * @return
     * @throws Exception
     */
    public  IDataset queryBenefitUseRecordByDate(IData param) throws Exception{
        String discntCode=param.getString("DISCNT_CODE");
        String userId =param.getString("USER_ID");
        String rightId =param.getString("RIGHT_ID");
        String relId =param.getString("REL_ID");
        String startDate =param.getString("START_DATE");
        String endDate =param.getString("END_DATE");
        return UserOtherInfoQry.getRightUseRecordByUserIdAndDate(userId, rightId, discntCode, relId,startDate,endDate);
    }
    /**
     * 校验用户是否有绑定指定权益标识
     * @param param
     * @return
     * @throws Exception
     */
    public   boolean checkRelIdBindedByUserId(IData param) throws Exception{
        String discntCode=param.getString("DISCNT_CODE");
        String rightId =param.getString("RIGHT_ID");
        String relId =param.getString("REL_ID");
        String userId =param.getString("USER_ID");
        IDataset  userOtherInfo = UserOtherInfoQry.getUserOtherInfoByBenefit(userId,PersonConst.BENEFIT_TAG
                , relId, rightId, discntCode);
        return IDataUtil.isNotEmpty(userOtherInfo);

    }

    /**
     * 校验用户是否有绑定指定权益标识
     * @param param
     * @return
     * @throws Exception
     */
    public   IDataset queryRelIdBindedByUserId(IData param) throws Exception{
        String discntCode=param.getString("DISCNT_CODE");
        String rightId =param.getString("RIGHT_ID");
        String relId =param.getString("REL_ID");
        String userId =param.getString("USER_ID");
        IDataset  userOtherInfo = UserOtherInfoQry.getUserOtherInfoByBenefit(userId,PersonConst.BENEFIT_TAG
                , relId, rightId, discntCode);
        return userOtherInfo;

    }


    /**
     * 校验指定权益标识是否已被绑定
     * @param param
     * @return
     * @throws Exception
     */
    public   boolean checkRelIdBinded(IData param) throws Exception{
        String discntCode=param.getString("DISCNT_CODE");
        String rightId =param.getString("RIGHT_ID");
        String relId =param.getString("REL_ID");
        IDataset  userOtherInfo = UserOtherInfoQry.getUserIdByRelID(PersonConst.BENEFIT_TAG
                , relId, rightId, discntCode);
        if(IDataUtil.isNotEmpty(userOtherInfo)){
            String userId =param.getString("USER_ID");
            if(userId.equals(userOtherInfo.first().getString("USER_ID"))){
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您已绑定该权益标识");
            }
        }
        return IDataUtil.isNotEmpty(userOtherInfo);

    }

    /**
     * 校验指定权益标识是否正在被绑定
     * @param param
     * @return
     * @throws Exception
     */
    public   boolean checkRelIdIsTrading(IData param) throws Exception{
        String discntCode=param.getString("DISCNT_CODE");
        String rightId =param.getString("RIGHT_ID");
        String relId =param.getString("REL_ID");
        IDataset  tradeBenefitChange = TradeInfoQry.queryTradeBenefitChange(relId,discntCode,rightId);
       if(IDataUtil.isNotEmpty(tradeBenefitChange)){
           String userId =param.getString("USER_ID");
           if(userId.equals(tradeBenefitChange.first().getString("USER_ID"))){
               CSAppException.apperr(CrmCommException.CRM_COMM_103, "您绑定的权益标识正在受理中");
           }
       }
        return IDataUtil.isNotEmpty(tradeBenefitChange);

    }

    /**
     * 查询用户剩余使用次数
     * @param param
     * @return
     * @throws Exception
     */
    public   int queryRemainUseNum(IData param) throws Exception{
           /** RSRV_STR3记录绑定权益时的权益次数,以后更换权益标识按该字段来记录次数
            * 先查询当年最后一次绑定权益标识的记录,获取RSRV_STR3的次数
            * 再查询该绑定标识startDate后的使用权益记录
            * 扣除使用的次数
            */
        String discntCode=param.getString("DISCNT_CODE");
        String rightId =param.getString("RIGHT_ID");
        String userId =param.getString("USER_ID");
        String serialNumber =param.getString("SERIAL_NUMBER");
        IDataset lastOtherInfoCuryears = UserOtherInfoQry.getLastCuryear(userId, PersonConst.BENEFIT_TAG
                , null, rightId,discntCode);
        if(IDataUtil.isNotEmpty(lastOtherInfoCuryears)) {
            IData lastOtherInfoCuryear = lastOtherInfoCuryears.first();
            int remainRecordNumConf = lastOtherInfoCuryear.getInt("RSRV_STR3");
            if (remainRecordNumConf <= 0) {
                return 0;
            }
            String startDate = lastOtherInfoCuryear.getString("START_DATE");
            //查询该条车牌记录之后的已使用次数
            IDataset rightUseRecords = UserOtherInfoQry.getRightUseRecordByUserId(userId, rightId
                    , discntCode, null, startDate);
            //使用记录去重
            IData checkDistinct = new DataMap();
            if (IDataUtil.isNotEmpty(rightUseRecords)) {
                for (int i = 0; i < rightUseRecords.size(); i++) {
                    IData rightUseRecord = rightUseRecords.getData(i);
                    int recordUseNum = rightUseRecord.getInt("RSRV_VALUE");
                    String tradeIdTJD = rightUseRecord.getString("RSRV_STR4");
                    if(StringUtils.isBlank(tradeIdTJD)){
                        continue;
                    }
                    if(StringUtils.isNotBlank(checkDistinct.getString(tradeIdTJD,""))){
                        //不为空说明已经计算过
                       continue;
                    }
                    remainRecordNumConf = remainRecordNumConf - recordUseNum;
                    checkDistinct.put(tradeIdTJD,"1");
                }
            }
            return remainRecordNumConf >= 0 ? remainRecordNumConf : 0;
        }else{
            //当年第一次绑定权益,从配置表里获取可使用次数
            IData qryParam=new DataMap();
            qryParam.put("SERIAL_NUMBER",serialNumber);
            qryParam.put("RIGHT_ID",rightId);
            qryParam.put("DISCNT_CODE",discntCode);
            qryParam.put("USER_ID",userId);
            return queryAllUseNumConfig(qryParam);
        }
    }


    /**
     * 查询用户总使用次数(随办理增加次数业务变化)
     * @param param
     * @return
     * @throws Exception
     */
    public   int queryAllUseNum(IData param) throws Exception{
        /** RSRV_STR3记录绑定权益时的权益次数,以后更换权益标识按该字段来记录次数
         * 先查询当年第一次绑定权益标识的记录,获取RSRV_STR3的次数
         */
        String discntCode=param.getString("DISCNT_CODE");
        String rightId =param.getString("RIGHT_ID");
        String userId =param.getString("USER_ID");
        String serialNumber =param.getString("SERIAL_NUMBER");
        IDataset lastOtherInfoCuryears = UserOtherInfoQry.getLastCuryear(userId, PersonConst.BENEFIT_TAG
                , null, rightId,discntCode);
        if(IDataUtil.isNotEmpty(lastOtherInfoCuryears)) {
            IData lastOtherInfoCuryear = lastOtherInfoCuryears.first();
            int remainRecordNumConf = lastOtherInfoCuryear.getInt("RSRV_STR3");
            if (remainRecordNumConf <= 0) {
                remainRecordNumConf =0;
            }
            //查询该条车牌记录之前的已使用次数
            IDataset rightUseAllRecords = UserOtherInfoQry.getRightUseRecordByUserId(userId, rightId
                    , discntCode, null, null);
            int rightUseAllNum=0;
            IData checkDistinct = new DataMap();
            if(IDataUtil.isNotEmpty(rightUseAllRecords)){
                for (int i = 0; i < rightUseAllRecords.size(); i++) {
                    IData rightUseRecord = rightUseAllRecords.getData(i);
                    int recordUseNum = rightUseRecord.getInt("RSRV_VALUE");
                    String tradeIdTJD = rightUseRecord.getString("RSRV_STR4");
                    if(StringUtils.isBlank(tradeIdTJD)){
                        continue;
                    }
                    if(StringUtils.isNotBlank(checkDistinct.getString(tradeIdTJD,""))){
                        //不为空说明已经计算过
                        continue;
                    }
                    rightUseAllNum = rightUseAllNum + recordUseNum;
                    checkDistinct.put(tradeIdTJD,"1");
                }
            }
            String startDate = lastOtherInfoCuryear.getString("START_DATE");
            IDataset rightUseAfterRecords = UserOtherInfoQry.getRightUseRecordByUserId(userId, rightId
                    , discntCode, null, startDate);
            int rightUseAfterNum=0;
            IData checkDistinctAfter = new DataMap();
            if(IDataUtil.isNotEmpty(rightUseAfterRecords)){
                for (int i = 0; i < rightUseAfterRecords.size(); i++) {
                    IData rightUseRecord = rightUseAfterRecords.getData(i);
                    int recordUseNum = rightUseRecord.getInt("RSRV_VALUE");
                    String tradeIdTJD = rightUseRecord.getString("RSRV_STR4");
                    if(StringUtils.isBlank(tradeIdTJD)){
                        continue;
                    }
                    if(StringUtils.isNotBlank(checkDistinctAfter.getString(tradeIdTJD,""))){
                        //不为空说明已经计算过
                        continue;
                    }
                    rightUseAfterNum = rightUseAfterNum + recordUseNum;
                    checkDistinctAfter.put(tradeIdTJD,"1");
                }
            }
            int allUseNum=remainRecordNumConf+rightUseAllNum-rightUseAfterNum;
            return allUseNum >= 0 ? allUseNum : 0;
        }else{
            //当年第一次绑定权益,从配置表里获取可使用次数
            IData qryParam=new DataMap();
            qryParam.put("SERIAL_NUMBER",serialNumber);
            qryParam.put("RIGHT_ID",rightId);
            qryParam.put("DISCNT_CODE",discntCode);
            qryParam.put("USER_ID",userId);
            return queryAllUseNumConfig(qryParam);
        }
    }

    /**
     * 查询用户总使用次数配置
     * @param param
     * @return
     * @throws Exception
     */
    public   int queryAllUseNumConfig(IData param) throws Exception{

        //1查询userother RIGHT_NUM_CONFIG 记录,累加次数
        IData qryParam=new DataMap();
        qryParam.put("USER_ID",param.getString("USER_ID"));
        qryParam.put("RIGHT_ID",param.getString("RIGHT_ID"));
        qryParam.put("DISCNT_CODE",param.getString("DISCNT_CODE"));
        IDataset addRightUseNumConfigs = queryAddRightUseNumConfig(qryParam);
        int allUseNumConfig=0;
        if(IDataUtil.isNotEmpty(addRightUseNumConfigs)){
            for (int i = 0; i < addRightUseNumConfigs.size(); i++) {
                allUseNumConfig+=addRightUseNumConfigs.getData(i).getInt("RSRV_VALUE");
            }
        }
        //2查询配置
        qryParam.clear();
        String userId = param.getString("USER_ID", "");
        if(StringUtils.isBlank(userId)){
            IData userinfo = UserInfoQry.getUserInfoBySN(param.getString("SERIAL_NUMBER"));
            if (IDataUtil.isEmpty(userinfo)) {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到该用户信息");
            }
            userId=userinfo.getString("USER_ID");
        }
        qryParam.put("USER_ID",userId);
        IDataset userClassInfo = UserClassInfoQry.queryUserClass_1(qryParam);
        String userClass="0";
        String offerCode="";
        if(IDataUtil.isNotEmpty(userClassInfo)){
            userClass=userClassInfo.first().getString("USER_CLASS","0");
            offerCode=userClassInfo.first().getString("RSRV_STR3");
        }
        //由指定营销活动赠送的白金钻石卡等级不计算入配置的次数
        IDataset commpara7175=new DatasetList();
        if(StringUtils.isNotBlank(offerCode)){
            commpara7175 = CommparaInfoQry.getCommparaByCode1("CSM", "7175", offerCode, "0898");
        }
        if(IDataUtil.isNotEmpty(commpara7175)){
            String paraCode11 = commpara7175.first().getString("PARA_CODE11");
            if("1".equals(paraCode11)){
                return allUseNumConfig;
            }
        }
        IDataset benefitConfigs = CommparaInfoQry.getCommparaByCode1to3("CSM", PersonConst.BENEFIT_CONDITION_CONFIG
                ,"USERCLASS", userClass,param.getString("RIGHT_ID"),param.getString("DISCNT_CODE"),"0898");
        if(IDataUtil.isNotEmpty(benefitConfigs)){
            allUseNumConfig+=benefitConfigs.first().getInt("PARA_CODE4");
        }
        return allUseNumConfig;
    }

    /**
     * 调能力开放平台停简单接口
     * @param apiAddressConf
     * @param input
     * @return
     * @throws Exception
     */
    public  IData callAbilityForTingJQ(String apiAddressConf,IData input) throws Exception {
        IDataset partners = CommparaInfoQry.getCommparaAllColByParser("CSM", PersonConst.BENEFIT_STATIC_CONFIG, PersonConst.BENEFIT_STATIC_PARTNER,  "0898");
        if(IDataUtil.isEmpty(partners)||StringUtils.isBlank(partners.first().getString("PARA_CODE1"))){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "停简单COMMPARAM7174未配置PARTNER");
        }
        IDataset signs = CommparaInfoQry.getCommparaAllColByParser("CSM", PersonConst.BENEFIT_STATIC_CONFIG, PersonConst.BENEFIT_STATIC_SIGN,  "0898");
        if(IDataUtil.isEmpty(signs)||StringUtils.isBlank(signs.first().getString("PARA_CODE1"))){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "停简单COMMPARAM7174未配置SIGN");
        }
        IData abilityData = new DataMap();
        abilityData.put("version", "1.0");
        abilityData.put("partner", partners.first().getString("PARA_CODE1"));
        abilityData.put("signType", "md5");
        abilityData.put("charset", "utf-8");
        abilityData.put("timestamp", SysDateMgr.getSysTime());
        abilityData.putAll(input);
        Map<String, String> abilitySortData = IDataUtil.sortIData(abilityData);
        Set<String> abilityDataKeys = abilitySortData.keySet();
        StringBuilder stringBuilder = new StringBuilder(3000);
        for (String abilityDataKey : abilityDataKeys) {
            if (StringUtils.isNotBlank(abilitySortData.get(abilityDataKey))&&!"signType".equals(abilityDataKey)) {
                stringBuilder.append(abilityDataKey).append("=").append(abilitySortData.get(abilityDataKey)).append("&");
            }
        }
        String signStr = stringBuilder.substring(0, stringBuilder.length() - 1) + signs.first().getString("PARA_CODE1");
        abilityData.put("sign", MD5Util.getMD5Str(signStr));
        IData abilityRes = AbilityEncrypting.callAbilityPlatCommon(AbilityEncrypting.getApiAddress(apiAddressConf), abilityData);
        String abilityResCode = abilityRes.getString("resCode");
        String abilityResMsg = abilityRes.getString("resMsg");
        if (!"00000".equals(abilityResCode)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用能开接口[" + apiAddressConf + "]报错:" + abilityResMsg);
        }
        IData result = abilityRes.getData("result");
        String bookIsSuccess = result.getString("isSuccess");
        String bookErrorMSG = result.getString("errorMSG");
        String returnMsg = result.getString("returnMsg");
        if (!"0".equals(bookIsSuccess)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用停简单接口[" + apiAddressConf + "]报错:" + bookErrorMSG+";"+returnMsg);
        }
        return result;
    }

    /**
     * 车牌号正则校验
     * @param carnumber
     * @return
     */
    public boolean checkCarNum(String carnumber) throws Exception{
        String carnumRegex = "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}(([0-9]{5}[DF])|([DF]([A-HJ-NP-Z0-9])[0-9]{4})))|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z]{1}[A-Z]{1}[A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳]{1})";

        if (StringUtils.isBlank(carnumber)) {
            return false;
        }else {
            return carnumber.matches(carnumRegex);
        }

    }

    /**
     * 记录错误日志
     * @param param
     */
    public void insertErrorLog(IData param) throws Exception{
        IData paramInfo=new DataMap();
        //调用服务名
        paramInfo.put("SERVICE",param.getString("SERVICE"));
        //办理业务类型
        paramInfo.put("TRADE_TYPE_CODE",param.getString("TRADE_TYPE_CODE"));
        //错误信息
        String errorMsg=param.getString("ERROR_MSG","").length()>500?
                param.getString("ERROR_MSG","").substring(0,490):param.getString("ERROR_MSG","");
        paramInfo.put("ERROR_MSG",errorMsg);
        //入表时间
        paramInfo.put("IN_DATE",SysDateMgr.getSysTime());
        //权益类型
        paramInfo.put("RSRV_STR1",param.getString("RIGHT_ID"));
        //权益编码
        paramInfo.put("RSRV_STR2",param.getString("DISCNT_CODE"));
        //权益标识
        paramInfo.put("RSRV_STR3",param.getString("REL_ID"));
        //预留字段4
        paramInfo.put("RSRV_STR4",param.getString("RSRV_STR4"));
        //预留字段5
        paramInfo.put("RSRV_STR5",param.getString("RSRV_STR5"));
        //预留字段6
        paramInfo.put("RSRV_STR6",param.getString("RSRV_STR6"));
        Dao.insert("TL_B_BENEFIT_ERROR_LOG", paramInfo);
    }


    /**
     * 根据条件 查询增加使用次数配置
     * @param param
     * @return
     * @throws Exception
     */
    public   IDataset queryAddRightUseNumConfig(IData param) throws Exception{
        String userId=param.getString("USER_ID");
        String rightId=param.getString("RIGHT_ID");
        String discntCode=param.getString("DISCNT_CODE");
        IDataset benefitConfigs = UserOtherInfoQry.getUserOtherInfoVaild(userId,PersonConst.BENEFIT_RIGHT_NUM_CONFIG,null,rightId,discntCode);
        return benefitConfigs;
    }
    
    /**
     * 获取本月权益
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public  IDataset queryRightsInterests(String serialNumber) throws Exception{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		return Dao.qryByCode("TF_F_USER_RIGHTS_INTERESTS", "SEL_ALL_BY_SERIAL_NUMBER", param);
	}
    
    /**
     * 插入权益表
     * @param orderId  订单号
     * @param subOrderId  子订单号
     * @param userId  手机号码
     * @param serialNumber  手机号码
     * @param discntCode   优惠编码
     * @param goodsId   能开编码
     * @param discntName  优惠名称
     */
    public void  insertRightsInterests(String orderId,String subOrderId,String userId,String serialNumber,String discntCode,String goodsId,String discntName)  throws Exception{
    	IData param = new DataMap();
    	param.put("ORDER_ID", orderId);
    	param.put("SUB_ORDER_ID", subOrderId);
    	param.put("USER_ID", userId);
    	param.put("SERIAL_NUMBER", serialNumber);
    	param.put("DISCNT_CODE", discntCode);
    	param.put("GOODS_ID", goodsId);
    	param.put("DISCNT_NAME", discntName);
    	param.put("DEAL_STATE", "0");
    	Dao.executeUpdateByCodeCode("TF_F_USER_RIGHTS_INTERESTS", "INS_ALL", param);
    }

    
    /**
     * 计算使用次数
     * @param param
     * @return
     * @throws Exception
     */
    public String countCurUseNum(IData param) throws Exception{
        //根据时间配置设置时间
        IDataset freeTimes = CommparaInfoQry.getCommparaAllColByParser("CSM", PersonConst.BENEFIT_STATIC_CONFIG, PersonConst.BENEFIT_STATIC_FREETIME,  "0898");
        long freeTime=0L;
        if(IDataUtil.isNotEmpty(freeTimes)&& StringUtils.isNotBlank(freeTimes.first().getString("PARA_CODE1"))){
            freeTime=freeTimes.first().getLong("PARA_CODE1",0L);
        }
        String startDate = param.getString("START_DATE");
        String endDate = param.getString("END_DATE");
        long countCurUseNum = SysDateMgr.daysBetweenForBenefit(startDate, endDate, freeTime);
        DataMap qryParam = new DataMap();
        qryParam.put("DISCNT_CODE",param.getString("DISCNT_CODE"));
        qryParam.put("RIGHT_ID",param.getString("RIGHT_ID"));
        qryParam.put("USER_ID",param.getString("USER_ID"));
        qryParam.put("SERIAL_NUMBER",param.getString("SERIAL_NUMBER"));
        int remainUseNum = this.queryRemainUseNum(qryParam);
        if(countCurUseNum>remainUseNum){
            return String.valueOf(remainUseNum);
        }else{
            return String.valueOf(countCurUseNum);
        }
    }
}
