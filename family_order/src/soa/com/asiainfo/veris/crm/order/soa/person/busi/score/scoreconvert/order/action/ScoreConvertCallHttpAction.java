
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.requestdata.ScoreConvertRequestData;

/**
 * @des 统一积分兑换调IBOSS接口
 * @author huangsl
 */
public class ScoreConvertCallHttpAction implements ITradeAction
{

    public static final String saleServId = "0003";// 营业厅接入

    public static final String kfId = "0005";// 10086接入

    public static final String saleServName = "CRM渠道-营业厅";

    public static final String kfName = "CRM渠道-10086";

    public static final String GTag = "G";// 全球通

    public static final String MTag = "M";// 动感地带

    public static final String ETag = "E";// 神州行

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        ScoreConvertRequestData reqData = (ScoreConvertRequestData) btd.getRD();
        MainTradeData mainTrade = btd.getMainTradeData();
        String seq = mainTrade.getRsrvStr4();
        String orgId = saleServId;// IBOSS接入渠道标识
        String orgName = saleServName;// IBOSS接入渠道名称
        String inModeCode = CSBizBean.getVisit().getInModeCode();
        if ("0".equals(inModeCode) || "3".equals(inModeCode))
        {
            orgId = saleServId;
            orgName = saleServName;
        }
        else if ("1".equals(inModeCode))
        {
            orgId = kfId;
            orgName = kfName;
        }
        else
        {
            // 非客服和营业渠道不能办理积分兑换！
            CSAppException.apperr(IBossException.CRM_IBOSS_45);
        }

        String userBrand = "";
        String brandCode = reqData.getUca().getBrandCode();
        if ("G001".equals(brandCode))// 全球通
        {
            userBrand = GTag;
        }
        else if ("G010".equals(brandCode))// 动感地带
        {
            userBrand = MTag;
        }
        else
        // 其他品牌
        {
            userBrand = ETag;
        }
        String userLocalProv = "898";
        // 再查一次积分
        IDataset scoreInfo = AcctCall.queryUserScore(reqData.getUca().getUserId());
        if (IDataUtil.isEmpty(scoreInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_6);
        }
        String userScore = scoreInfo.getData(0).getString("SUM_SCORE"); // 用户可兑换积分

        String ordOprTime = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        String itemId = reqData.getItemId();
        String itemCount = reqData.getItemNum();
        String serialNumber = reqData.getUca().getSerialNumber();
        /* 由于积分平台下发数据全球通普通客户、动感地带客户没有按照集团规范传值，所以必须做转换 */
        IData vipLevelData = this.queryVipClassForOrder(btd);
        String userLevel = vipLevelData.getString("PARA_CODE2", "");// 客户级别
        String custName = reqData.getUca().getCustomer().getCustName();
        String delivProv = reqData.getDelivProvince();
        String city = reqData.getCity();
        String destrict = reqData.getDistrict();
        String custAddr = reqData.getCusAdd();
        String custTel = reqData.getCusTel();
        String custAddrCode = reqData.getCusAddcode();
        String delivTimeReq = reqData.getDelivTimeReq();
        //zhouyl5 20150325 用户星级
        IData creditInfo = CreditCall.queryUserCreditInfos(reqData.getUca().getUserId());
        //STAR_LEVEL
        
        int creditClass = creditInfo.getInt("CREDIT_CLASS",-1);
        IData ibossResult = IBossCall.callScoreConvert(orgId, orgName, seq, userBrand, userLocalProv, ordOprTime, userScore, itemId, itemCount, serialNumber,tranStarLevel(creditClass), custName, delivProv, city, destrict, custAddr, custTel, custAddrCode,
                delivTimeReq);

        if (IDataUtil.isNotEmpty(ibossResult))
        {
            String xRspCode = ibossResult.getString("X_RSPCODE", "");

            IData inParam = new DataMap();
            if ("0000".equals(xRspCode))// IBOSS返回成功
            {
                String tradeResCode = ibossResult.getString("TRADE_RES_CODE");
                String tradeResDesc = ibossResult.getString("TRADE_RES_DESC");

                if ("01".equals(tradeResCode))// 返回成功
                {
                    inParam.put("ORDER_SEQ", seq);
                    inParam.put("PROC_STATE", "01");
                    inParam.put("ORDER_ID", ibossResult.getString("ORDER_ID"));
                    inParam.put("REMARK", tradeResDesc);

                    boolean successFlag = Dao.save("TF_F_USER_UPMS_ORDER", inParam);
                    if (!successFlag)
                    {
                        CSAppException.apperr(IBossException.CRM_IBOSS_11);
                    }
                }
                else
                // 返回交易失败
                {
                    CSAppException.apperr(IBossException.CRM_IBOSS_5, tradeResDesc);
                }
            }
            else if ("0101".equals(xRspCode) || "0102".equals(xRspCode) || "".equals(xRspCode))// IBOSS返回超时，crm侧仍然登记，等待手工发起超时查询补全ORDER_ID或撤销
            {
                inParam.put("ORDER_SEQ", seq);
                inParam.put("PROC_STATE", "02");
                inParam.put("REMARK", "IBOSS返回超时");

                boolean successFlag = Dao.save("TF_F_USER_UPMS_ORDER", inParam);
                if (!successFlag)
                {
                    CSAppException.apperr(IBossException.CRM_IBOSS_12);
                }
            }
            else
            {
                CSAppException.apperr(IBossException.CRM_IBOSS_13);
            }
        }
        else
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_18);
        }
    }

    /**
     * 下兑换订单时获取客户级别
     */
    public IData queryVipClassForOrder(BusiTradeData btd) throws Exception
    {
        IData data = new DataMap();
        VipTradeData vipInfo = btd.getRD().getUca().getVip();
        String brandCode = btd.getRD().getUca().getBrandCode();
        // -------------查询大客户信息----------------
        if (vipInfo != null)
        {
            IDataset commparaInfo = CommparaInfoQry.getCommparaInfoBy5("CSM", "8765", vipInfo.getVipClassId(), vipInfo.getVipTypeCode(), "0898", null);
            if (IDataUtil.isNotEmpty(commparaInfo))
            {
                data = commparaInfo.getData(0);
            }
        }
        // --------------未查询到大客户信息，则根据品牌显示普通用户-----------------
        if (data.isEmpty())
        {
            if ("G001".equals(brandCode))
            {
                data.put("PARA_CODE2", "303");// 原有300客户级别映射到303级别
                data.put("PARA_CODE3", "全球通普通客户");
            }
            else if ("G010".equals(brandCode))
            {
                data.put("PARA_CODE2", "100");
                data.put("PARA_CODE3", "动感地带普通客户");
            }
            else
            {
                data.put("PARA_CODE2", "100");
                data.put("PARA_CODE3", "神州行普通客户");
            }
        }
        return data;
    }
    
    /**
     * 转换客户星级
     */
    public String tranStarLevel(int creditClass){
    	String starLevel = "13";
    	if(creditClass == -1){
    		starLevel = "13";
    	}
    	if(creditClass == 0){
    		starLevel = "12";
    	}
    	if(creditClass == 1){
    		starLevel = "11";
    	}
    	if(creditClass == 2){
    		starLevel = "10";
    	}
    	if(creditClass == 3){
    		starLevel = "09";
    	}
    	if(creditClass == 4){
    		starLevel = "08";
    	}
    	if(creditClass == 5){
    		starLevel = "07";
    	}
    	if(creditClass == 6){
    		starLevel = "06";
    	}
    	if(creditClass == 7){
    		starLevel = "05";
    	}
    	
    	return starLevel;
    }
}
