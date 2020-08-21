package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action.finishaction;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import org.apache.log4j.Logger;

/**
 * 关于一级能力开放平台新增SID查询接口及花卡相关接口改造的通知
 * 新增“CIP00113 号卡未激活释放信息推送接口”，省侧通过该接口回传未激活释放的号卡信息
 * 本接口用于省侧主动推送号卡未激活释放信息。当已订的号卡未激活释放时，即时触发接口回传未激活释放信息给一级能开。
 */
public class FlowerCardReleaseRspGroupAction implements ITradeFinishAction {

    public static Logger logger = Logger.getLogger(FlowerCardReleaseRspGroupAction.class);

    private static int EXPSIZE = 3999;

    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String orderId = mainTrade.getString("ORDER_ID");
        String tradeId = mainTrade.getString("TRADE_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        String productId = mainTrade.getString("PRODUCT_ID");
        String sysTime = SysDateMgr.getSysTime();
        String oprTime = SysDateMgr.addDays(1);

        IDataset comparaInfo = CommparaInfoQry.getCommparaByAttrCode1("CSM", "2578", productId, "ZZZZ", null);
        if (IDataUtil.isNotEmpty(comparaInfo)) {
            String state = "1";
            String resultCode = "0000";
            String resultInfo = "success";
            String orderNum = "";

            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serialNumber);
            IDataset postinfo = Dao.qryByCode("TD_B_POSTCARD_INFO", "SEL_FLOWER_RELEASE_BY_SN", param, Route.getCrmDefaultDb());
            if (IDataUtil.isNotEmpty(postinfo)) { // 未激活（1-预开）
            	orderNum = postinfo.getData(0).getString("ORDER_NO"); // 一级能开子订单编码，示例：SC01204T18101000000010-01
            } else { // TD_B_POSTCARD_INFO无未激活（1-预开）记录，表示此用户销户不是在线售卡因未激活而自动销户
                return;
            }

            //如果主套餐为配置的套餐，则调用能开接口
            String Abilityurl = "";
            IData param1 = new DataMap();
            param1.put("PARAM_NAME", "crm.ABILITY.CIP113");
            StringBuilder getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ");
            IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
            if (Abilityurls != null && Abilityurls.size() > 0) {
                Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
            } else {
                CSAppException.appError("-1", "crm.ABILITY.CIP113接口地址未在TD_S_BIZENV表中配置");
            }
            String apiAddress = Abilityurl;

            String ctrmProductId = comparaInfo.getData(0).getString("PARA_CODE4"); // 能开产品编码
            String ctrmProductName = "";
            IData userMainProductInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
            if (IDataUtil.isNotEmpty(userMainProductInfo)) {
                ctrmProductName = userMainProductInfo.getString("PARAM_NAME");
            }

            IData abilityData = new DataMap();
            abilityData.put("number", serialNumber); // 用户激活手机号码
            abilityData.put("oprType", "1"); // 操作类型：0-用户激活；1-系统释放
            abilityData.put("oprTime", Utility.decodeTimestamp(SysDateMgr.PATTERN_STAND_SHORT, oprTime)); // 号卡激活时间
            abilityData.put("planId", ctrmProductId); // 当前主套餐编码(一级能开产品ID)
            abilityData.put("planName", ctrmProductName); // 号码主套餐名称
            abilityData.put("orderNum", orderNum); // 能开订单号
            // 调用能力开放平台接口
            IData retData = new DataMap();
            try {
                retData = AbilityEncrypting.callAbilityPlatCommon(apiAddress, abilityData);
                logger.debug("FlowerCardOpenRspGroupAction retData = " + retData);
            } catch (Exception e) {
                    logger.error("FlowerCardReleaseRspGroupAction sendUserUnActiveDate2OpenAbli exception:" + e.getMessage());
            }

            if (IDataUtil.isNotEmpty(retData)) {
                String resCode = retData.getString("resCode");
                IData out = retData.getData("result");
                String X_RSPCODE = out.getString("bizCode");
                String X_RSPDESC = out.getString("bizDesc");
                if ("00000".equals(resCode)) {
                    if ("0000".equals(X_RSPCODE)) {
                        // 调用成功
                        state = "-1"; // -1-发送成功，0-未发送，1-发送失败一次，2-发送失败二次，3-发送失败三次
                    } else {
                        logger.error("调用能开参数：" + abilityData.toString());
                        logger.error("调用能开返回结果：" + retData.toString());
                        resultCode = X_RSPCODE;
                        resultInfo = X_RSPDESC;
                    }
                } else {
                    logger.error("调用能开参数：" + abilityData.toString());
                    logger.error("调用能开返回结果：" + retData.toString());
                    resultCode = X_RSPCODE;
                    resultInfo = X_RSPDESC;
                }
            } else {
                resultCode = "2999";
                resultInfo = "调用省能开接口异常返回报文为空[" + retData + "]";
            }

            IData insertParm = new DataMap();
            insertParm.put("TRADE_ID", tradeId);
            insertParm.put("ORDER_ID", orderId);
            insertParm.put("SERIAL_NUMBER", serialNumber); // 用户激活手机号码
            insertParm.put("PRODUCT_ID", productId); // 号码主套餐编码(一级能开产品ID)
            insertParm.put("PRODUCT_NAME", ctrmProductName); // 号码主套餐名称insertParm.put("UPDATE_TIME", sysTime);
            insertParm.put("UPDATE_TIME", sysTime);
            insertParm.put("STATE", state); // 系统释放发送状态：-1-发送成功，0-未发送，1-发送失败一次，2-发送失败二次，3-发送失败三次
            insertParm.put("RESULT_CODE", resultCode);
            insertParm.put("RESULT_INFO", resultInfo.length() > EXPSIZE ? resultInfo.substring(0, EXPSIZE) : resultInfo);
            insertParm.put("RSRV_STR1", orderNum); // 能开订单号
            insertParm.put("RSRV_TAG1", "1"); // 操作类型：0-用户激活；1-系统释放
            insertParm.put("RSRV_DATE1", oprTime); // 号卡释放时间
            insertParm.put("RSRV_NUM1", "898"); // 省编码

            try {
                saveActiveTime(insertParm);
            } catch (Exception e) {
                    logger.error("FlowerCardReleaseRspGroupAction saveActiveTime exception:" + e.getMessage());
            }
        }
    }

    /**
     * 号卡激活状态登记表
     *
     * @param insertData
     * @throws Exception
     * @author zhaohj3
     */
    public void saveActiveTime(IData insertData) throws Exception {
        IData data = Dao.qryByPK("TF_B_SYNC_ACTIVATE_TIME", insertData, Route.CONN_CRM_CEN);
        if (IDataUtil.isEmpty(data)) {
            Dao.insert("TF_B_SYNC_ACTIVATE_TIME", insertData, Route.CONN_CRM_CEN);
        } else {
            Dao.save("TF_B_SYNC_ACTIVATE_TIME", insertData, Route.CONN_CRM_CEN);
        }
    }
}



