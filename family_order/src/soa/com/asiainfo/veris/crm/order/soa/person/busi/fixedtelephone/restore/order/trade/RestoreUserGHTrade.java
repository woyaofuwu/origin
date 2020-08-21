
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.order.trade;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.TelephoneTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.ChangeSvcStateComm;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.RestoreUserGHComm;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.order.requestdata.RestoreUserGHReqData;
import com.sun.org.apache.regexp.internal.recompile;

public class RestoreUserGHTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        RestoreUserGHReqData restoreUserReqData = (RestoreUserGHReqData) btd.getRD();
        RestoreUserGHComm restoreComm = new RestoreUserGHComm();

        // 获取最后销户的订单信息
        String destroyTradeId = restoreComm.getLastDestroyTradeId(btd);

        // 如果是缴费复机，则资源信息从备份中获取
        if (StringUtils.equals("7302", btd.getTradeTypeCode()))
        {
            restoreComm.createResTradeDataFromBak(btd, destroyTradeId);
            restoreComm.createProductTradeDataFromBak(btd, destroyTradeId);// 产品台账
        }
        else
        {
            restoreComm.createResTradeData(btd);// 组织资源台账
            restoreComm.createProductTradeData(btd);// 产品台账
            ProductModuleCreator.createProductModuleTradeData(restoreUserReqData.getPmds(), btd);
            restoreComm.createPayRelaTradeData(btd);
        }

        restoreComm.createDiscntAndRelaTradeDataFromBak(btd, destroyTradeId);// 优惠/关系台账信息
        // IData restoreSvcData = restoreComm.createSvcTradeDataFromBak(btd, destroyTradeId);// 服务台账、返回被恢复的原始服务信息
        // restoreComm.createAttrTradeDataFromBak(btd, destroyTradeId, restoreSvcData);// 生成属性相关台账信息 --放在优惠、服务台账生成之后

        // restoreComm.setTradeResKiInfo(btd);// 设置sim卡关键字段值
        restoreComm.createUserTradeData(btd);// 用户资料台账

        this.modifyUserMainSvcStateGroups(btd);// 一定要放在服务状态台账生成之后处理
        this.dealTelephoneTrade(btd);
        this.dealUser(btd);
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
    }

    private void dealTelephoneTrade(BusiTradeData btd) throws Exception
    {
        String user_id = btd.getRD().getUca().getUserId();
        IDataset telephoneInfo = UserInfoQry.getTelephoneInfoByUserId(user_id);
        if (IDataUtil.isEmpty(telephoneInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1177);
        }
        TelephoneTradeData telTrade = new TelephoneTradeData(telephoneInfo.getData(0)).clone();
        telTrade.setStartDate(SysDateMgr.getSysDate());
        telTrade.setModifyTag(BofConst.MODIFY_TAG_UPD);
        telTrade.setEndDate(SysDateMgr.END_TIME_FOREVER);
        telTrade.setRemark("固话复机");
        btd.add(btd.getRD().getUca().getSerialNumber(), telTrade);
    }
    
    

    private void dealUser(BusiTradeData btd) throws Exception
    {
    	RestoreUserGHReqData rd = (RestoreUserGHReqData)btd.getRD();
    	UserTradeData userTD = btd.getRD().getUca().getUser();
    	String productId = rd.getMainProduct().getProductId();
    	List<UserTradeData> svcList = btd.get(TradeTableEnum.TRADE_USER.getValue());
        for (UserTradeData uTD : svcList)
        {
//fengxx            String prepayTag = StaticUtil.getStaticValue(getVisit(), "TD_B_PRODUCT", "PRODUCT_ID", "PREPAY_TAG", rd.getMainProduct().getProductId());
//            userTD.setPrepayTag(prepayTag);
        	String prepayTag = "";
        	IData prepayTagDataset = UpcCall.queryOfferByOfferId("P", rd.getMainProduct().getProductId(), "Y");
			if(IDataUtil.isNotEmpty(prepayTagDataset)){
				prepayTag=prepayTagDataset.getString("PREPAY_TAG");
			}
			uTD.setPrepayTag(prepayTag);
        }
    }
   

    // 修改用户表中的主体服务状态
    // 一定要放在服务状态台账生成之后处理
    private void modifyUserMainSvcStateGroups(BusiTradeData btd) throws Exception
    {
        ChangeSvcStateComm bean = new ChangeSvcStateComm();
        bean.modifyMainSvcStateByUserId(btd);
        // 放在最后面，不要影响到主体服务状态
        if (StringUtils.equals("9706", btd.getTradeTypeCode()))// 如果是立即复机业务的话，还需要清理一下扩展字段的标记
        {
            bean.clearRsrvSpecTagInfo(btd);
        }
    }
}
