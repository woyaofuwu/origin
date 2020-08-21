
package com.asiainfo.veris.crm.order.soa.script.rule.discnt;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AirlinesInterRoamUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import org.apache.log4j.Logger;

/**
 * 航空公司专属国漫资费系统支撑方案V1.2
 * 订购航空公司专属优惠是判断用户是否为白名单号码
 * 产品变更（TRADE_TYPE_CODE = 110）规则
 * add by xieyf5
 */
public class InterRoamAirGsm8Discnt extends BreBase implements IBREScript {
    private static Logger logger = Logger.getLogger(InterRoamAirGsm8Discnt.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug(">>>>>>>>>>>>>>>>>>进入InterRoamAirGsm8Discnt规则判断>>>>>>>>>>>>>>>>>>");
        }

        // 有可能的情况：
        // 1: 无套餐，无八折优惠，要订购套餐和八折优惠
        // 2：有套餐，无八折优惠，要订购八折优惠：//
        // 4：有套餐，有八折优惠，要退订套餐和八折优惠 //
        // 5：有套餐，有八折优惠，要变更套餐，不退订八折优惠 //
        // 3: 有套餐，有八折优惠，要退订八折优惠 //
        // 上面的情况都可以走产商品的依赖互斥，不需要判断

        // 1：先判断用户是否订购了“全球通无限尊享计划套餐”
        // 2：假如用户没有订购全球通无限尊享计划套餐，则提示用户先订购尊享套餐，才能订购折扣产品。
        // 上面两步可以走产商品的依赖互斥，不需要在规则里判断。

        boolean BreResult = false;
        String serial_number = databus.getString("SERIAL_NUMBER"); // 用户手机号码

        // 先获取用户做产品变更时的优惠
        IDataset tradeDiscntSet = databus.getDataset("TF_B_TRADE_DISCNT"); // 产品变更台账优惠

        // 遍历优惠台账，看是否有增加八折优惠的记录
        for (int i = 0; i < tradeDiscntSet.size(); i++) {
            IData tradeDiscntItem = tradeDiscntSet.getData(i);
            String discntCode = tradeDiscntItem.getString("DISCNT_CODE"); // 台账优惠编码
            String modifyTag = tradeDiscntItem.getString("MODIFY_TAG"); // 台账修改标记
            // 假如用户是订购航空公司专属优惠
            if ((AirlinesInterRoamUtil.getUnLimitProduct8Discnt()).equals(discntCode) ||
                    (AirlinesInterRoamUtil.getInterRoamStandard8Discnt()).equals(discntCode) ||
                    (AirlinesInterRoamUtil.getInterRoamCasDayDiscnt()).equals(discntCode) ||
                    (AirlinesInterRoamUtil.getInterRoamCasMonthDiscnt()).equals(discntCode)) {
                if ((BofConst.MODIFY_TAG_ADD).equals(modifyTag)) {
                    // 查询用户是否为白名单用户
                    IDataset interRoamAirWhiteSet = AirlinesInterRoamUtil.qryInterRoamAirWhite(serial_number);
                    if (IDataUtil.isEmpty(interRoamAirWhiteSet)) {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2999, "您不是航空公司白名单用户，不允许订购航空公司专属优惠" + discntCode);
                    }
                    break;
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug(">>>>>>>>>>>>>>>>>>退出InterRoamAirGsm8Discnt规则判断：" + BreResult + ">>>>>>>>>>>>>>>>>>");
        }

        // 假如在白名单中则用户可以订购八折优惠
        return BreResult;
    }

}
