
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.trade;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.RestorePersonUserComm;
import com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.requestdata.RestoreUserReqData;

public class RestorePersonUserTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        RestoreUserReqData restoreUserReqData = (RestoreUserReqData) btd.getRD();
        RestorePersonUserComm restoreComm = new RestorePersonUserComm();
        
        MainTradeData mainTradeData = btd.getMainTradeData();
		mainTradeData.setRsrvStr8(btd.getRD().getPageRequestData().getString("FAMILY_FALG"));//恢复亲亲网关系

        restoreComm.createUserTradeData(btd);// 用户资料台账
        // 如果是缴费复机，则资源信息从备份中获取
        if (StringUtils.equals("7302", btd.getTradeTypeCode()))
        {
            // 获取最后销户的订单信息
            String destroyTradeId = restoreComm.getLastDestroyTradeId(btd);
            if (StringUtils.isEmpty(destroyTradeId))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_171);
            }
            restoreComm.createResTradeDataFromBak(btd, destroyTradeId);
            restoreComm.createProductTradeDataFromBak(btd, destroyTradeId);// 产品台账
            restoreComm.createDiscntAndRelaTradeDataFromBak(btd, destroyTradeId);// 优惠/关系台账信息
            IData restoreSvcData = restoreComm.createSvcTradeDataFromBak(btd, destroyTradeId);// 服务台账、返回被恢复的原始服务信息
            restoreComm.createAttrTradeDataFromBak(btd, destroyTradeId, restoreSvcData);// 生成属性相关台账信息 --放在优惠、服务台账生成之后
            ChangeSvcStateComm changeSvcStateComm = new ChangeSvcStateComm();
            changeSvcStateComm.getSvcStateChangeTrade(btd);
        }
        else
        {
            restoreComm.createResTradeData(btd);// 组织资源台账
            restoreComm.createProductTradeData(btd);// 产品台账
            ProductModuleCreator.createProductModuleTradeData(restoreUserReqData.getPmds(), btd);
            restoreComm.createPayRelaTradeData(btd);

            // 发票号
            String invoiceNo = restoreUserReqData.getInvoiceNo();
            if (StringUtils.isNotEmpty(invoiceNo))
            {
                btd.getMainTradeData().setInvoiceNo(invoiceNo);
            }
        }

        restoreComm.createNewScoreTradeData(btd);  //创建积分计划台账
        
        this.modifyUserMainSvcStateGroups(btd);// 一定要放在服务状态台账生成之后处理
        // 可以考虑都加上容错处理 产品、服务状态
        /******************** 如下通过action处理 *****************************/
        // 资源预占( IModifyStateInfoAction.java）
        // TCS_PlatInfoRegForYY
        // TCS_DestroyByperson
        // 财务BOSS登记和完工,TCS_FinancialBossReg，TCS_FinishFinancialBoss
        // 隔月复机，老系统调用TAM_DISDEPOSIT_CANCEL
        // 处理局方停机

        /******************** 如下的结点，跟开户的相同，调用开户的action处理 *****************************/
        // 920服务加入特定优惠
        // 优惠台帐绑定处理
        // 定制服务处理
        // 服务绑定服务
        
        
        //恢复营销活动资料、恢复营销活动相关的优惠元素 by songlm  20150813  参见8月11日邮件“关于复机时营销活动恢复问题”
        //获取本月最后销户的订单信息
        String tradeIdTemp = restoreComm.getCurrentMonthLastDestroyTradeId(btd);
        if(StringUtils.isNotEmpty(tradeIdTemp))
        {
        	restoreComm.restoreSaleActiveDataFromBak(btd, tradeIdTemp);// 恢复营销活动资料和营销活动相关的优惠元素资料
        }
    }

    // 修改用户表中的主体服务状态
    // 一定要放在服务状态台账生成之后处理
    private void modifyUserMainSvcStateGroups(BusiTradeData btd) throws Exception
    {
        ChangeSvcStateComm bean = new ChangeSvcStateComm();
        bean.modifyMainSvcStateByUserId(btd);
        // 放在最后面，不要影响到主体服务状态
        if (StringUtils.equals("310", btd.getTradeTypeCode()))// 如果是立即复机业务的话，还需要清理一下扩展字段的标记
        {
            bean.clearRsrvSpecTagInfo(btd);
        }
    }
}
