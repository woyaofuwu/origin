
package com.asiainfo.veris.crm.order.soa.person.rule.run.np;

import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sharemeal.ShareInfoQry;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.CParamQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

import java.util.Iterator;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 *
 * @ClassName: CheckIsPurchaseUserB.java
 * @Description: 携出申请前的规则校验
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-23 上午10:25:05 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-23 lijm3 v1.0.0 修改原因
 */
public class CheckBeforeNpOut extends BreBase implements IBREScript
{
	 private static Logger logger = Logger.getLogger(CheckBeforeNpOut.class);
    /*
     * 根据老身份证获取新身份证 add by wangjx 2010-5-8
     */
    public String getNewPsptId(String pspId) throws Exception
    {

        int iS = 0;
        int iW[] =
        { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };// 加权因子常数
        String lastCode = "10X98765432";// 校验码常数
        String lastNo = "";// 新身份证最后一位校验码
        String newPsptId = "";// 新身份证号

        // 新身份证前17位
        newPsptId = pspId.substring(0, 6) + "19" + pspId.substring(6);
        String _psptId = newPsptId;
        // 进行加权求和
        for (int i = 0; i < newPsptId.length(); i++)
        {
            iS += Integer.parseInt(_psptId.substring(i, i + 1)) * iW[i];
        }

        // 取模运算，得到模值
        int iY = iS % 11;

        // 从lastCode中取得以模为索引号的值，加到身份证的最后一位，即为新身份证号
        for (int i = 0; i < lastCode.length(); i++)
        {
            if (i == iY)
            {
                lastNo = lastCode.substring(i, i + 1);
                break;
            }
        }
        newPsptId = newPsptId + lastNo;

        return newPsptId;
    }


    /**
     * REQ201601180003 携号转网699一般性业务错误返回码优化
     * chenxy3 2016-01-18
     * 将原699错误返回码多个不同类别改成具体的错误编码
     * @tanzheng 将返回的错误编码变成以竖线分割2017-10-10
     * */
    @Override
    public boolean run(IData paramIData, BreRuleParam paramBreRuleParam) throws Exception
    {
    	if (logger.isDebugEnabled())
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckBeforeNpOut() >>>>>>>>>>>>>>>>>>");
        //int lastOweFee = paramIData.getInt("LAST_OWE_FEE");// 往月欠费
    	if("1".equals(paramIData.getString("PORT_BACK_ID"))){//快速携回不需要进行校验
    		return false;
    	}
    	double acctBalance = paramIData.getDouble("LEAVE_REAL_FEE",0.0);// 实时结余
        String userId = paramIData.getString("USER_ID");
        IData reqData = paramIData.getData("REQDATA");
        String tradeId = "", tradeEparchyCode = "";

        String serialNumber = paramIData.getString("SERIAL_NUMBER");
        String strIdentityCheckTag = reqData.getString("IDENTITY_CHECK_TAG", "0");
        String psptTypeCode = paramIData.getString("PSPT_TYPE_CODE");
        String isRealName = paramIData.getString("IS_REAL_NAME");
        String custName = paramIData.getString("CUST_NAME").replace(" ", "").toUpperCase();

        String psptId = paramIData.getString("PSPT_ID").replace(" ", "").toUpperCase();
        String brandCode = paramIData.getString("BRAND_CODE").trim();

        UcaData uca = (UcaData) paramIData.get("UCADATA");
        String strMessageID = "";
        if (reqData.containsKey("MESSAGEID"))
        {
            strMessageID = reqData.getString("MESSAGEID").substring(3, 6);
        }

        String strCreditClass=uca.getUserCreditClass();

        String code = "", resultInfo = "";
        String tradeCode = "", tradeInfo = "";
        if (acctBalance < 0)
        {
        	double Balance = -acctBalance/100;
        	double CompareBalance = -acctBalance;

        	boolean isStarLvlBalance=false;
        	int iTarget = 0;
        	IDataset tempInfos = BreQryForCommparaOrTag.getCommpara("CSM", 5201, "1", "0898");
        	if ( tempInfos != null && tempInfos.size() > 0)
			{
				IData tempInfo = new DataMap();
				tempInfo = (IData) tempInfos.get(0);
				String StrTarget = tempInfo.getString("PARA_CODE1");
				iTarget = Integer.parseInt(StrTarget);


				String starLevel = tempInfo.getString("PARA_CODE2","");	//获取星级的配置

				if(!starLevel.equals("")){
					int starLevelInt=Integer.parseInt(starLevel);

					int iCreditClass = Integer.parseInt(strCreditClass);		//星级


					/*
					 * 如果用户的星级>=配置的星级，用户为亲亲网副号限制和欠费额度大于0限制
					 */
					if(iCreditClass>=starLevelInt){

						isStarLvlBalance=true;

						if(CompareBalance>0){	//验证欠费是否大于0
							if (StringUtils.isNotBlank(code))
			                {
			                    code += "|612";
			                    resultInfo += "|您的号码当前有" + Balance + "元费用尚未缴清影响携号转网办理";
			                }
			                else
			                {
			                    code = "612";
			                    resultInfo = "您的号码当前有" + Balance + "元费用尚未缴清影响携号转网办理";
			                }
						}

						//就判断是否是亲亲网的副号，，家庭网
						IDataset subFamilyNumber = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "45", "2");
						if(IDataUtil.isNotEmpty(subFamilyNumber)){
//							if (StringUtils.isNotBlank(code))
//			                 {
//			                     code += "|611";
//			                     resultInfo += "|用户是亲亲网子号码，无法办理携出";
//			                 }
//			                 else
//			                 {
//			                     code = "611";
//			                     resultInfo = "用户是亲亲网子号码，无法办理携出";
//			                 }
                            if (StringUtils.isNotBlank(tradeCode))
                            {
                                if(tradeInfo.indexOf("家庭网") > -1){
                                    //已包含家庭网
                                }else {
                                    tradeCode += "|611";
                                    tradeInfo += "|家庭网";
                                }
                            }
                            else
                            {
                                tradeCode = "611";
                                tradeInfo = "家庭网";
                            }
						}
					}else{
						isStarLvlBalance=false;
					}
				}

			}


    		if(!isStarLvlBalance&&CompareBalance > iTarget)
        	{
                if (StringUtils.isNotBlank(code))
                {
                    code += "|612";
                    resultInfo += "|您的号码当前有" + Balance + "元费用尚未缴清影响携号转网办理";
                }
                else
                {
                    code = "612";
                    resultInfo = "您的号码当前有" + Balance + "元费用尚未缴清影响携号转网办理";
                }
        	}

        }

        if(!"1".equals(isRealName)){
            if (StringUtils.isNotBlank(code))
            {
                code += "|610";
                resultInfo += "|非实名制用户不能携出!";
            }
            else
            {
                code = "610";
                resultInfo = "非实名制用户不能携出!";
            }
        }
        //REQ201412150017 取消吉祥号码客户携出拦截限制
        boolean bSwitch = true;//携出拦截限制开关，默认拦截
        IDataset commpara2016 = CommparaInfoQry.getCommparaCode1("CSM", "2016", "SEL_OUTNP_SWITCH", CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(commpara2016)){
        	String strSwitch = commpara2016.getData(0).getString("PARA_CODE1", "1");
        	if( strSwitch.equals("0") ){
        		bSwitch = false;//设置取消携出拦截限制
        	}
        }
        //SEL_OUTNP_SWITCH=true 拦截，false 取消拦截
        if( bSwitch ){
	        /*IData inData = new DataMap();
	        inData.put("SERIAL_NUMBER", serialNumber);
	        inData.put("QRY_TAG", "1");
	        inData.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());// 用户归属地州
	        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 用户交易地州
	        inData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());// 受理业务区
	        inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());// 受理部门
	        inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());// 受理员工
	        inData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());*/
	        IDataset phones= ResCall.getMphonecodeInfo(serialNumber,"1"); //CSAppCall.call("RM.ResPhoneIntfSvc.getMphonecodeInfo", inData);
	        if(phones!=null&&phones.size()>0){
	        	String  beautifual_tag = ((IData)phones.get(0)).getString("BEAUTIFUAL_TAG");
	        	if("1".equals(beautifual_tag)){
	        		if (StringUtils.isNotBlank(code))
	                {
	                    code += "|611";
	                    resultInfo += "|该用户处在特殊优惠套餐期内!";
	                }
	                else
	                {
	                    code = "611";
	                    resultInfo = "该用户处在特殊优惠套餐期内!";
	                }
	        	}
	        }
        }

        //查询用户营销活动有效期
        IDataset cparamData =null;
        IData paramData = new DataMap();
        IData cparam = new DataMap();

        cparamData = CParamQry.getPurchaseUserBNew(userId, "99", "0",strCreditClass);  //getPurchaseUserB(userId, "99", "0");
        if (IDataUtil.isEmpty(cparamData))
        {
            cparamData = CParamQry.getPurchaseUserANew(userId, "99", "0",strCreditClass);	//getPurchaseUserA(userId, "99", "0");
        }
        //签约类营销活动
        if (IDataUtil.isNotEmpty(cparamData))
        {
            for (int i = 0, len = cparamData.size(); i < len; i++)
            {
                IData data = cparamData.getData(i);
                if (StringUtils.isNotBlank(code))
                {
                    code += "|611";
                    resultInfo += "|您的号码有" + data.getString("PRODUCT_NAME") + "（到期时间为"+ data.getString("END_DATE") +"）影响携号转网办理";
                }
                else
                {
                    code = "611";
                    resultInfo = "您的号码有" + data.getString("PRODUCT_NAME") + "（到期时间为"+ data.getString("END_DATE") + "）影响携号转网办理";
                }
            }

        }
        //非签约类营销活动
        IDataset newCparamData = CParamQry.getNewLimitActives(userId, "41");
        if (IDataUtil.isNotEmpty(newCparamData))
        {
            for (Iterator<Object> iterator = newCparamData.iterator(); iterator.hasNext(); ) {
                IData newCparam = (IData) iterator.next();
                System.out.println("====QueryNpMessageBean==newCparam="+newCparam);

                for(int i=0;i<cparamData.size();i++){
                    cparam = cparamData.getData(i);
                    String productId = cparam.getString("PRODUCT_ID");
                    if (productId.equals(newCparam.getString("PRODUCT_ID"))) {
                        System.out.println("====QueryNpMessageBean==remove=cparamData："+cparamData);
                        iterator.remove();//去重
                        break;
                    }
                }
            }

            if (IDataUtil.isNotEmpty(newCparamData)) {
                for (int i = 0; i < newCparamData.size(); i++) {
                    cparam = newCparamData.getData(i);
                    String productName = cparam.getString("PRODUCT_NAME");
                    String endDate = cparam.getString("END_DATE");
                    if (StringUtils.isNotBlank(code)) {
                        code += "|611";
                        resultInfo += "|您的号码有" + productName + "（到期时间为" + endDate + "）影响携号转网办理";
                    } else {
                        code = "611";
                        resultInfo = "您的号码有" + productName + "（到期时间为" + endDate + "）影响携号转网办理";
                    }
                }

            }
        }

//        ids = UserSaleActiveInfoQry.queryVpmnActiveRelationsByUserId(userId);
//        if (IDataUtil.isNotEmpty(ids))
//        {
//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|698";
//                resultInfo += "|该用户处在集团V网双网有礼营销活动有效期内，不允许携出!";
//            }
//            else
//            {
//                code = "698";
//                resultInfo = "该用户处在集团V网双网有礼营销活动有效期内，不允许携出!";
//            }
//        }

        IDataset ids = CParamQry.getAbnormalityUser(userId);
        if (IDataUtil.isNotEmpty(ids))
        {
            if (StringUtils.isNotBlank(code))
            {
                code += "|613";
                resultInfo += "|该用户处在非正常开通（高额停机、报停...）状态，不允许携出!";
            }
            else
            {
                code = "613";
                resultInfo = "该用户处在非正常开通（高额停机、报停...）状态，不允许携出!";
            }
        }

        ids = CParamQry.getAbnormalityUserA(userId);
        if (IDataUtil.isNotEmpty(ids))
        {
            if (StringUtils.isNotBlank(code))
            {
                code += "|613";
                resultInfo += "|该用户已经报失，不允许携出!";
            }
            else
            {
                code = "613";
                resultInfo = "该用户已经报失，不允许携出!";
            }
        }

        ids = IDataUtil.idToIds(UcaInfoQry.qryDefaultPayRelaByUserId(userId));
        if (IDataUtil.isEmpty(ids))
        {
            if (StringUtils.isNotBlank(code))
            {
                code += "|699";
                resultInfo += "|用户没有付费关系记录!";
            }
            else
            {
                code = "699";
                resultInfo = "用户没有付费关系记录!";
            }
        }
        else
        {
            //托收
            ids = PayRelaInfoQry.queryPayreInfoByAcctId(paramIData.getString("ACCT_ID"));
            if (IDataUtil.isNotEmpty(ids) && ids.size() > 1)
            {

//                if (StringUtils.isNotBlank(code))
//                {
//                    code += "|616";//",699";
//                    resultInfo += "|请在申请授权码前做好托收业务变更手续。";//"|该用户账户为多个用户付费，不允许携出!";
//                }
//                else
//                {
//                    code = "616";//"699";
//                    resultInfo = "请在申请授权码前做好托收业务变更手续。";//"该用户账户为多个用户付费，不允许携出!";
//                }
                if (StringUtils.isNotBlank(tradeCode))
                {
                    if(tradeInfo.indexOf("托收") > -1){
                        //已包含托收
                    }else {
                        tradeCode += "|616";
                        tradeInfo += "|托收";
                    }
                }
                else
                {
                    tradeCode = "616";
                    tradeInfo = "托收";
                }
            }
        }
        //托收
        ids = PayRelaInfoQry.getAllUserPayRelationByAcctId(paramIData.getString("ACCT_ID"));
        if (IDataUtil.isNotEmpty(ids) && ids.size() > 1)
        {

//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|616";
//                resultInfo += "|请在申请授权码前做好托收业务变更手续。";
//            }
//            else
//            {
//                code = "616";
//                resultInfo = "请在申请授权码前做好托收业务变更手续。";
//            }
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("托收") > -1){
                    //已包含托收
                }else {
                    tradeCode += "|616";
                    tradeInfo += "|托收";
                }
            }
            else
            {
                tradeCode = "616";
                tradeInfo = "托收";
            }
        }

        if (IDataUtil.isNotEmpty(reqData))
        {
            tradeId = reqData.getString("TRADE_ID");
            tradeEparchyCode = reqData.getString("TRADE_EPARCHY_CODE");
        }

        //未完结的工单
        ids = CParamQry.getUndoneTrade1(userId, tradeId);
        if (IDataUtil.isNotEmpty(ids))
        {

//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|699";
//                resultInfo += "|有未完工工单，不允许携出!";
//            }
//            else
//            {
//                code = "699";
//                resultInfo = "有未完工工单，不允许携出!";
//            }
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("未完结的工单") > -1){
                    //已包含未完结的工单
                }else {
                    tradeCode += "|699";
                    tradeInfo += "|未完结的工单";
                }
            }
            else
            {
                tradeCode = "699";
                tradeInfo = "未完结的工单";
            }
        }
        ids = UserInfoQry.getUserInfoByAcctTag(userId, "2");
        if (IDataUtil.isNotEmpty(ids))
        {

            if (StringUtils.isNotBlank(code))
            {
                code += "|613";//",699";
                resultInfo += "|申请业务的号码是已停机的号码或已挂失的号码";//"|该用户处于待激活状态，不允许携出!";
            }
            else
            {
                code = "613";//"699";
                resultInfo = "申请业务的号码是已停机的号码或已挂失的号码";//"该用户处于待激活状态，不允许携出!";
            }
        }

        ids = UserDiscntInfoQry.getUserDisntsBylimitNpWithCreditClass(userId, tradeEparchyCode,strCreditClass);   //getUserDisntsBylimitNp(userId, tradeEparchyCode);
        if (IDataUtil.isNotEmpty(ids))
        {
//        	boolean flag=false;
//        	for(int i=0;i<ids.size();i++){
//        		IData data=ids.getData(i);
//        		String para_code3=data.getString("PARA_CODE3","");
//        		if(para_code3!=null&&!para_code3.equals("1")){
//        			flag=true;//存在非集团优惠
//        			break;
//        		}
//        	}
//        	if(flag){//存在非集团优惠
            String discntName = "";
            IData spDiscnt = new DataMap();
            for(int i=0;i<ids.size();i++){
                spDiscnt = ids.getData(i);
                if("".equals(discntName)){
                    discntName =spDiscnt.getString("DISCNT_NAME");
                }else{
                    discntName = discntName+"，"+spDiscnt.getString("DISCNT_NAME");
                }
            }

            if (StringUtils.isNotBlank(code))
            {
                code += "|611";
                resultInfo += "|您的号码有" + discntName + "影响携号转网办理";
            }
            else
            {
                code = "611";
                resultInfo = "您的号码有" + discntName + "影响携号转网办理";
            }
//        	}
//        	}else{
//        		 if (StringUtils.isNotBlank(code))
//                 {
//                     code += "|611";
//                     resultInfo += "|该客户正在使用集团网优惠套餐!";
//                 }
//                 else
//                 {
//                     code = "611";
//                     resultInfo = "该客户正在使用集团网优惠套餐!";
//                 }
//        	}

        }
        //目前生产无该配置
        ids = UserSvcInfoQry.getUserSvcsByLimitNpWithStarLevel(userId, tradeEparchyCode, strCreditClass);  //getUserSvcsByLimitNp(userId, tradeEparchyCode);
        if (IDataUtil.isNotEmpty(ids))
        {

            if (StringUtils.isNotBlank(code))
            {
                code += "|611";//",699";
                resultInfo += "|用户与携出方有未解除的协议!";// "|用户特殊服务限制，不允许携出!";
            }
            else
            {
                code = "611";//"699";
                resultInfo = "用户与携出方有未解除的协议!";//"用户特殊服务限制，不允许携出!";
            }
        }

        if (!"1".equals(strIdentityCheckTag))
        {
            ids = MsisdnInfoQry.getMsisDns(serialNumber);
            if (IDataUtil.isNotEmpty(ids))
            {
                String areaCode = ids.getData(0).getString("AREA_CODE", "");
                String strAreaCode = "";
                if (4 == areaCode.length())
                {
                    strAreaCode = areaCode.substring(1, 4);
                }
                else
                {
                    strAreaCode = areaCode;
                }
                if(!"AUTHCODE_REQ".equals(paramIData.getString("COMMANDCODE"))){//短厅发起授权码请求不校验MessageID
                if (!strAreaCode.equals(strMessageID))
                {
                    if (StringUtils.isNotBlank(code))
                    {
                        code += "|696";
                        resultInfo += "|MessageID与号码号段不匹配，请核查!";
                    }
                    else
                    {
                        code = "696";
                        resultInfo = "MessageID与号码号段不匹配，请核查!";
                    }
                }
            }
            }
            else
            {
                if (StringUtils.isNotBlank(code))
                {
                    code += "|696";
                    resultInfo += "|该号码不存在于号段表中，请核查!";
                }
                else
                {
                    code = "696";
                    resultInfo = "该号码不存在于号段表中，请核查!";
                }
            }
        }

        String strPsptId = "", strPsptTypeCode = "";
        if (reqData.containsKey("CREDNUMBER"))
        {
            strPsptId = reqData.getString("CREDNUMBER").replace(" ", "").toUpperCase();
        }
        else
        {
            throwsException("108006", "传入参数缺少必传字段：PSPT_ID!");
        }

        if (reqData.containsKey("CREDTYPE"))
        {
            strPsptTypeCode = reqData.getString("CREDTYPE");
        }
        else
        {
            throwsException("108007", "传入参数缺少必传字段：PSPT_TYPE_CODE!");
        }

        logger.debug(" >>>>>>>>>>>>>>>>> 进入 CheckBeforeNpOut() >>>>>>>>>>>>>>>>>>psptTypeCode="+psptTypeCode+";strPsptTypeCode"+strPsptTypeCode);
    	if("D".equals(psptTypeCode) || "E".equals(psptTypeCode) || "G".equals(psptTypeCode) || "L".equals(psptTypeCode) || "M".equals(psptTypeCode) || "C".equals(psptTypeCode))
    	{
    		if (StringUtils.isNotBlank(code))
            {
                code += "|610";
                resultInfo += "|您的号码为单位所有，暂无法办理携号转网!";
            }
            else
            {
                code = "610";
                resultInfo = "您的号码为单位所有，暂无法办理携号转网!";
            }
    	}

        if ("1".equals(isRealName))
        {
            // 实名制
            if ("0".equals(strPsptTypeCode))
            {
                if (!("0".equals(psptTypeCode) || "1".equals(psptTypeCode)))
                {
                    if (StringUtils.isNotBlank(code))
                    {
                        code += "|610";
                        resultInfo += "|用户传入身份证类型【" + strPsptTypeCode + "】与系统不符!";
                    }
                    else
                    {
                        code = "610";
                        resultInfo = "用户传入身份证类型【" + strPsptTypeCode + "】与系统不符!";
                    }
                }
                String strCustName = reqData.getString("USERNAME").replace(" ", "").toUpperCase();
                if (!custName.equals(strCustName))
                {
                    if (StringUtils.isNotBlank(code))
                    {
                        code += "|610";
                        resultInfo += "|用户传入客户姓名【" + strCustName + "】与系统不符!";
                    }
                    else
                    {
                        code = "610";
                        resultInfo = "用户传入客户姓名【" + strCustName + "】与系统不符!";
                    }
                }

                if (strPsptId.length() == 15)
                {
                    String newPsptId = getNewPsptId(strPsptId).replace(" ", "").toUpperCase();
                    if (!strPsptId.equals(psptId) && !newPsptId.equals(psptId))
                    {
                        if (StringUtils.isNotBlank(code))
                        {
                            code += "|610";
                            resultInfo += "|用户传入身份证件号码【" + strPsptId + "】与系统【" + psptId + "】不符!";
                        }
                        else
                        {
                            code = "610";
                            resultInfo = "用户传入身份证件号码【" + strPsptId + "】与系统【" + psptId + "】不符!";
                        }
                    }
                }
                else if (strPsptId.length() == 18)
                {
                    if (psptId.length() == 15)
                    {
                        psptId = getNewPsptId(psptId).replace(" ", "").toUpperCase();
                    }
                    if (!strPsptId.equals(psptId))
                    {
                        if (StringUtils.isNotBlank(code))
                        {
                            code += "|610";
                            resultInfo += "|用户传入身份证件号码【" + strPsptId + "】与系统【" + psptId + "】不符!";
                        }
                        else
                        {
                            code = "610";
                            resultInfo = "用户传入身份证件号码【" + strPsptId + "】与系统【" + psptId + "】不符!";
                        }
                    }

                }
                else
                {
                    if (StringUtils.isNotBlank(code))
                    {
                        code += "|610";
                        resultInfo += "|用户传入身份证位数不标准!";
                    }
                    else
                    {
                        code = "610";
                        resultInfo = "用户传入身份证位数不标准!";
                    }
                }
            }
            else
            {
                if (!strPsptTypeCode.equals(psptTypeCode))
                {
                    if (StringUtils.isNotBlank(code))
                    {
                        code += "|610";
                        resultInfo += "|用户传入证件类型【" + strPsptTypeCode + "】与系统不符!";
                    }
                    else
                    {
                        code = "610";
                        resultInfo = "用户传入证件类型【" + strPsptTypeCode + "】与系统不符!";
                    }
                }else {
                    String strCustName = reqData.getString("USERNAME").replace(" ", "").toUpperCase();
                    if (!custName.equals(strCustName)) {
                        if (StringUtils.isNotBlank(code)) {
                            code += "|610";
                            resultInfo += "|用户传入客户姓名【" + strCustName + "】与系统不符!";
                        } else {
                            code = "610";
                            resultInfo = "用户传入客户姓名【" + strCustName + "】与系统不符!";
                        }
                    }
                    psptId = psptId.replace(" ", "").toUpperCase();
                    if (!strPsptId.equals(psptId))
                    {
                        if (StringUtils.isNotBlank(code))
                        {
                            code += "|610";
                            resultInfo += "|用户传入证件号码【" + strPsptId + "】与系统不符!";
                        }
                        else
                        {
                            code = "610";
                            resultInfo = "用户传入证件号码【" + strPsptId + "】与系统不符!";
                        }
                    }
                }
            }
        }

        if ("0".equals(isRealName))
        {
            if ("0".equals(strPsptTypeCode))
            {

                if (!("0".equals(psptTypeCode) || "1".equals(psptTypeCode)))
                {

                    if (StringUtils.isNotBlank(code))
                    {
                        code += "|610";
                        resultInfo += "|用户传入身份证类型【" + strPsptTypeCode + "】与系统不符!";
                    }
                    else
                    {
                        code = "610";
                        resultInfo = "用户传入身份证类型【" + strPsptTypeCode + "】与系统不符!";
                    }
                }

                String strCustName = reqData.getString("USERNAME").replace(" ", "").toUpperCase();
                if (!custName.equals(strCustName))
                {
                    if (StringUtils.isNotBlank(code))
                    {
                        code += "|610";
                        resultInfo += "|用户传入客户姓名【" + strCustName + "】与系统不符!";
                    }
                    else
                    {
                        code = "610";
                        resultInfo = "用户传入客户姓名【" + strCustName + "】与系统不符!";
                    }
                }

                if (strPsptId.length() == 15)
                {
                    String newPsptId = getNewPsptId(strPsptId).replace(" ", "").toUpperCase();
                    if (!strPsptId.equals(psptId) && !newPsptId.equals(psptId))
                    {
                        if (StringUtils.isNotBlank(code))
                        {
                            code += "|610";
                            resultInfo += "|用户传入身份证件号码【" + strPsptId + "】与系统【" + psptId + "】不符!";
                        }
                        else
                        {
                            code = "610";
                            resultInfo = "用户传入身份证件号码【" + strPsptId + "】与系统【" + psptId + "】不符!";
                        }
                    }
                }
                else if (strPsptId.length() == 18)
                {
                    if (psptId.length() == 15)
                    {
                        psptId = getNewPsptId(psptId).replace(" ", "").toUpperCase();
                    }
                    if (!strPsptId.equals(psptId))
                    {
                        if (StringUtils.isNotBlank(code))
                        {
                            code += "|610";
                            resultInfo += "|用户传入身份证件号码【" + strPsptId + "】与系统【" + psptId + "】不符!";
                        }
                        else
                        {
                            code = "610";
                            resultInfo = "用户传入身份证件号码【" + strPsptId + "】与系统【" + psptId + "】不符!";
                        }
                    }

                }
                else
                {
                    if (StringUtils.isNotBlank(code))
                    {
                        code += "|610";
                        resultInfo += "|用户传入身份证位数不标准!";
                    }
                    else
                    {
                        code = "610";
                        resultInfo = "用户传入身份证位数不标准!";
                    }
                }
            }
            else
            {

                if (!strPsptTypeCode.equals(psptTypeCode))
                {
                    if (StringUtils.isNotBlank(code))
                    {
                        code += "|610";
                        resultInfo += "|用户传入证件类型【" + strPsptTypeCode + "】与系统不符!";
                    }
                    else
                    {
                        code = "610";
                        resultInfo = "用户传入证件类型【" + strPsptTypeCode + "】与系统不符!";
                    }
                }
            }

        }

        /*
         * REQ201905060005	中国移动携号转网支撑改造方案（试点省部分优化）
         * 取消157/188号段携出限制
         */
//        if ("157".equals(serialNumber.substring(0, 3)) || "188".equals(serialNumber.substring(0, 3)))
//        {
//        	/*
//        	 * 201908携号转网网间联调测试，取消157/188号段携出限制
//        	 * 因需要生产测试，先通过配置实现配置号码取消该限制。
//        	 * mengqx
//        	 */
//			IDataset Commpara423 = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "2016", "NP_UNLIMIT", serialNumber, "0898");
//			System.out.println("QueryNpMessageBeanxxxxxxxxxxxxxxx936 " + Commpara423 + " XXXXSERIAL_NUMBER" + serialNumber);
//
//			if (IDataUtil.isNotEmpty(Commpara423) && Commpara423.size() != 0) {
//				
//			}else {
//        	
//	            if (StringUtils.isNotBlank(code))
//	            {
//	                code += "|699";
//	                resultInfo += "|157、188用户不允许携出!";
//	            }
//	            else
//	            {
//	                code = "699";
//	                resultInfo = "157、188用户不允许携出!";
//	            }
//			}
//
//        }
      //1.号段校验   PARA_CODE2:号段类型,0-卫星移动,1-移动通信转售,2-物联网应用  目前仅存在这三类号码限制
		//由于号段存在4位，所以原逻辑做调整 add by dengyi5
		IDataset limitSnList = CommparaInfoQry.getCommparaInfos("CSM", "173", "LIMITSN");
		if(IDataUtil.isNotEmpty(limitSnList))
		{//存在号段限制配置
			for(int i=0; i<limitSnList.size();i++)
			{
				IData limitSn = limitSnList.getData(i);
				String snNum = limitSn.getString("PARA_CODE1");
				if(serialNumber.startsWith(snNum))
				{
	                if (StringUtils.isNotBlank(code))
	    	        {
	                	code += "|699";
		                resultInfo += "|您的号码暂不支持携号转网!";
    	            }
    	            else
	            	{
    	            	code += "699";
		                resultInfo += "您的号码暂不支持携号转网!";
    	            }
				}
			}
		}

//        if ("G011".equals(brandCode))
//        {
//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|611";//",699";
//                resultInfo += "|用户与携出方有未解除的协议!";//"|移动公话用户需变更为普通客户才能办理携号转网!";
//            }
//            else
//            {
//                code = "611";//"699";
//                resultInfo = "用户与携出方有未解除的协议!";//"移动公话用户需变更为普通客户才能办理携号转网!";
//            }
//        }
//
//        if ("G005".equals(brandCode))
//        {
//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|611";//",699";
//                resultInfo += "|用户与携出方有未解除的协议!";//"|随E行用户需变更为普通客户才能办理携号转网!";
//            }
//            else
//            {
//                code = "611";//"699";
//                resultInfo = "用户与携出方有未解除的协议!";//"随E行用户需变更为普通客户才能办理携号转网!";
//            }
//        }

        //一卡多号副号不允许携出
        IDataset dataset2 = RelaUUInfoQry.qryRelaBySerialNumberBAndRelationTypeCode("M2", serialNumber, null);
        if (IDataUtil.isNotEmpty(dataset2)) {
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("一卡多号") > -1) {
                    //已包含一卡多号副号
                } else {
                    tradeCode += "|611";
                    tradeInfo += "|一卡多号";
                }
            } else {
                tradeCode += "|611";
                tradeInfo = "一卡多号";
            }
        }

        //一代TD固话绑定的157号码不允许携出
        IDataset dataset3 = RelaUUInfoQry.check_byuserida_idbzm_A(userId, "T2", null, null);
        if (IDataUtil.isNotEmpty(dataset3)) {
            if (StringUtils.isNotBlank(tradeInfo)) {
                if (tradeInfo.indexOf("固移融合") > -1) {
                    //已包含一代TD固话绑定的157号码
                } else {
                    tradeCode += "|611";
                    tradeInfo += "|固移融合";
                }
            } else {
                tradeCode += "|611";
                tradeInfo = "固移融合";
            }
        }

        //一号一终端号码不允许携出
        String simCardNo = "";
        IDataset userRes = UserResInfoQry.getUserResInfoByUserId(userId);
        logger.debug("业务限制userInfos：" + userRes);
        if (IDataUtil.isNotEmpty(userRes)) {
            for (int i = 0; i < userRes.size(); i++) {
                if ("1".equals(userRes.getData(i).getString("RES_TYPE_CODE"))) {
                    simCardNo = userRes.getData(i).getString("RES_CODE");
                }
            }

        }
        logger.debug("业务限制simCardNo：" + simCardNo);
        if (StringUtils.isNotEmpty(simCardNo)) {
            IDataset simCardInfo = ResCall.getSimCardInfo("0", simCardNo, "", "1", "");
            logger.debug("业务限制simCardInfo：" + simCardInfo);
            if (IDataUtil.isNotEmpty(simCardInfo)) {
                String res_kind_code = simCardInfo.getData(0).getString("RES_KIND_CODE");
                logger.debug("业务限制res_kind_code：" + res_kind_code);
                if (StringUtils.equals("10F1", res_kind_code)) {
                    if (StringUtils.isNotBlank(tradeInfo)) {
                        if (tradeInfo.indexOf("一号一终端") > -1) {
                            //已包含一号一终端号码
                        } else {
                            tradeCode += "|611";
                            tradeInfo += "|一号一终端";
                        }
                    } else {
                        tradeCode += "|611";
                        tradeInfo = "一号一终端";
                    }
                }
            }
        }

        //主副卡
        ids = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "97", "2");
        if (IDataUtil.isNotEmpty(ids))
        {
//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|616";
//                resultInfo += "|用户在携出方使用了一卡付多号副卡业务，不允许携出！";
//            }
//            else
//            {
//                code = "616";
//                resultInfo = "用户在携出方使用了一卡付多号副卡业务，不允许携出！";
//            }
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("主副卡") > -1){
                    //已包含主副卡
                }else {
                    tradeCode += "|616";
                    tradeInfo += "|主副卡";
                }
            }
            else
            {
                tradeCode = "616";
                tradeInfo = "主副卡";
            }
        }
        //一卡双号
        ids = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "30", "2");
        if (IDataUtil.isNotEmpty(ids))
        {
//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|616";
//                resultInfo += "|用户在携出方使用了一卡双号副卡业务，不允许携出！";
//            }
//            else
//            {
//                code = "616";
//                resultInfo = "用户在携出方使用了一卡双号副卡业务，不允许携出！";
//            }
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("一卡双号") > -1){
                    //已包含一卡双号
                }else {
                    tradeCode += "|616";
                    tradeInfo += "|一卡双号";
                }
            }
            else
            {
                tradeCode = "616";
                tradeInfo = "一卡双号";
            }
        }
        //主副卡
        ids = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "34", "2");
        if (IDataUtil.isNotEmpty(ids))
        {
//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|616";
//                resultInfo += "|用户在携出方使用了双卡统一付费副卡业务，不允许携出！";
//            }
//            else
//            {
//                code = "616";
//                resultInfo = "用户在携出方使用了双卡统一付费副卡业务，不允许携出！";
//            }
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("主副卡") > -1){
                    //已包含主副卡
                }else {
                    tradeCode += "|616";
                    tradeInfo += "|主副卡";
                }
            }
            else
            {
                tradeCode = "616";
                tradeInfo = "主副卡";
            }
        }

        ids = TradeNpQry.getTradeNpsByRsrvstr5(serialNumber, "41");
        if (IDataUtil.isNotEmpty(ids))
        {
            String str = ids.getData(0).getString("RSRV_STR5", "");
            if (str.length() > 5)
            {
                if (StringUtils.isNotBlank(code))
                {
                    code += "|699";
                    resultInfo += "|" + str.substring(5);
                }
                else
                {
                    code = "699";
                    resultInfo = str.substring(5);
                }
            }

        }
        //add by panyu5携出时间未达到120天
//        ids = CParamQry.getXieChuIngA(userId);
//        if (IDataUtil.isNotEmpty(ids))
//        {
//            if (StringUtils.isNotBlank(code))
//            {
//           	 code+="|611";
//				 resultInfo += "|您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于X年X月X日后再查询!";
//            }
//            else
//            {
//                code = "611";
//                resultInfo = "您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于X年X月X日后再查询!";
//            }
//        }
        /*
         * 修改上面判断过程，应支持“两次携转时间间隔”灵活配置。
         */
        int para_code1 = 120;
        int day = 0;
        ids = CParamQry.getXieChuIngB(userId);//把120天时间限制去掉
        if (IDataUtil.isNotEmpty(ids)){
            String portInDate = ids.getData(0).getString("PORT_IN_DATE");//携入时间

            IDataset commpara = CommparaInfoQry.getCommPkInfo("CSM", "170", "1", "0898");//配置两次携转间隔时间，PARA_CODE1，单位为天
            if (IDataUtil.isNotEmpty(commpara))
            {
                para_code1 = commpara.getData(0).getInt("PARA_CODE1", 120);// 默认为120天

            }
            day = SysDateMgr.dayInterval(portInDate, SysDateMgr.getSysDate());
            if (para_code1 > day)
            {
                String enddate = SysDateMgr.addDays(portInDate, para_code1);
                enddate = enddate.substring(0,4)+"年"+enddate.substring(5, 7)+"月"+enddate.substring(8, 10)+"日";
                if (StringUtils.isNotBlank(code))
                {
                    code += "|611";
                    resultInfo += "|您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于"+enddate+"后再查询。";
                }
                else
                {
                    code = "611";
                    resultInfo = "您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于"+enddate+"后再查询。";
                }

            }
        }


        //入网时间
        ids = CommparaInfoQry.getCommPkInfo("CSM", "170", "0", "0898");//现在已不做该限制，配置时间为0天
        para_code1 = 120;
        if (IDataUtil.isNotEmpty(ids))
        {
            para_code1 = ids.getData(0).getInt("PARA_CODE1", 120);// 默认为120天

        }
        String userTagSet = uca.getUser().getUserTagSet();
        if(!"1".equals(userTagSet) && !"6".equals(userTagSet)) {//非携入号码
            day = SysDateMgr.dayInterval(uca.getUser().getOpenDate(), SysDateMgr.getSysDate());
            if (para_code1 > day) {
                if (StringUtils.isNotBlank(code)) {
                    code += "|600";//",699";
                    resultInfo += "|您的号码入网时间未达规定值，暂无法办理携号转网";//现在已不做该限制，配置时间为0天
                } else {
                    code = "600";//"699";
                    resultInfo = "您的号码入网时间未达规定值，暂无法办理携号转网";//现在已不做该限制，配置时间为0天
                }
            }
        }
        //固移融合
        ids = UserInfoQry.getWideUsersBySerialNumber(serialNumber);
        if (IDataUtil.isNotEmpty(ids))
        {
//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|616";//",699";
//                resultInfo += "|用户在携出方使用了影响其他号码付费或资费套餐使用的业务";//"|用户在本月已办理宽带开户，开户当月不能办理手机号码携转出网!";
//            }
//            else
//            {
//                code = "616";//"699";
//                resultInfo = "用户在携出方使用了影响其他号码付费或资费套餐使用的业务";//"用户在本月已办理宽带开户，开户当月不能办理手机号码携转出网!";
//            }
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("固移融合") > -1){
                    //已包含固移融合
                }else {
                    tradeCode += "|611";
                    tradeInfo += "|固移融合";
                }
            }
            else
            {
                tradeCode = "611";
                tradeInfo = "固移融合";
            }
        }

		//亲亲网早期没有短号，不会办理831服务，不判831服务，直接判UU表关系
        //List<SvcTradeData> svcTradeDatas = uca.getUserSvcs();
        //for (SvcTradeData svcTradeData : svcTradeDatas)
        //if ("831".equals(svcTradeData.getElementId()) && !"1".equals(svcTradeData.getModifyTag()))
        //家庭网
        ids = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "45", "2");
        if (IDataUtil.isEmpty(ids))
        {
            ids = RelaUUInfoQry.getRelaUUInfoByRol(userId, "45");

            if(!IDataUtil.isEmpty(ids))
            {
//                IData data = GetMaxEndDate(ids);
//                 if (StringUtils.isNotBlank(code))
//                 {
//                     code += "|611";
//                     resultInfo += "|用户处在亲亲网关系有效期内，不允许携出!";
//                 }
//                 else
//                 {
//                     code = "611";
//                     resultInfo = "用户处在亲亲网关系有效期内，不允许携出!";
//                 }
                if (StringUtils.isNotBlank(tradeCode))
                {
                    if(tradeInfo.indexOf("家庭网") > -1){
                        //已包含家庭网
                    }else {
                        tradeCode += "|611";
                        tradeInfo += "|家庭网";
                    }
                }
                else
                {
                    tradeCode = "611";
                    tradeInfo = "家庭网";
                }
            }
        }

        //统付
        ids = RelaUUInfoQry.getRelationUusByUserIdBTypeCode(userId, "56");
        if (IDataUtil.isNotEmpty(ids))
        {
//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|616";//",699";
//                resultInfo += "|用户在携出方使用了影响其他号码付费或资费套餐使用的业务!";//"|用户是统一账户付费用户不能办理手机号码携转出网!";
//            }
//            else
//            {
//                code = "616";//"699";
//                resultInfo = "用户在携出方使用了影响其他号码付费或资费套餐使用的业务!";//"用户是统一账户付费用户不能办理手机号码携转出网!";
//            }
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("主副卡") > -1){
                    //已包含主副卡
                }else {
                    tradeCode += "|616";
                    tradeInfo += "|主副卡";
                }
            }
            else
            {
                tradeCode = "616";
                tradeInfo = "主副卡";
            }
        }
        //固移融合
        ids = TradeInfoQry.getWindTradeInfoBySn("KD_" + serialNumber);
        if (IDataUtil.isNotEmpty(ids))
        {
//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|611";
//                resultInfo += "|该用户处在营销活动有效期内：该手机用户有宽带用户不能办理手机号码携转出网!";
//            }
//            else
//            {
//                code = "611";
//                resultInfo = "该用户处在营销活动有效期内：该手机用户有宽带用户不能办理手机号码携转出网!";
//            }
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("固移融合") > -1){
                    //已包含固移融合
                }else {
                    tradeCode += "|611";
                    tradeInfo += "|固移融合";
                }
            }
            else
            {
                tradeCode = "611";
                tradeInfo = "固移融合";
            }
        }
        //固移融合
        ids = IDataUtil.idToIds(UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber));
        if (IDataUtil.isNotEmpty(ids))
        {
        	IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber);
        	IData data = GetMaxEndDate(widenetInfos);
        	String product_name = "";
        	String wideType = data.getString("RSRV_STR2");
        	if("1".equals(wideType))
        	{
        		product_name = "GPON宽带开户业务";
        	}
        	else if("2".equals(wideType))
        	{
        		product_name = "ADSL宽带开户业务";
        	}
        	else if("3".equals(wideType))
        	{
        		product_name = "FTTH宽带开户业务";
        	}
        	else
        	{
        		product_name = "未知宽带业务";
        	}
//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|611";
//                resultInfo += "|该用户处在"+ product_name + "有效期内, " + data.getString("END_DATE") + "到期，不允许携出";
//            }
//            else
//            {
//                code = "611";
//                resultInfo = "该用户处在"+ product_name + "有效期内, " + data.getString("END_DATE") + "到期，不允许携出";
//            }
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("固移融合") > -1){
                    //已包含固移融合
                }else {
                    tradeCode += "|611";
                    tradeInfo += "|固移融合";
                }
            }
            else
            {
                tradeCode = "611";
                tradeInfo = "固移融合";
            }
        }

        //单方过户不允许携出 chenbl6

		String custId = paramIData.getString("CUST_ID");

		IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
		String specialCustDate = custInfo.getString("RSRV_DATE1", "");

		if(!specialCustDate.trim().equals("")){
			//终止时间
			String validEndDate=SysDateMgr.addYears(specialCustDate, 2);

			//当前时间
			String curTime=SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);

			if(curTime.compareTo(validEndDate)<=0){
			    if (StringUtils.isNotBlank(code))
                {
                    code += "|611";
                    resultInfo += "|您的号码有单方过户（到期时间为" + validEndDate + "）影响携号转网办理" ;
                }
                else
                {
                    code = "611";
                    resultInfo =  "您的号码有单方过户（到期时间为" + validEndDate + "）影响携号转网办理" ;
		        }
			}
		}

		//集团成员不允许携出  chenbl6
//        ids=BreQry.getGropMemberInfoByUserId(userId);
//        if (IDataUtil.isNotEmpty(ids)){
//            if (StringUtils.isNotBlank(code))
//            {
//                code += "|611";
//                resultInfo += "|您的号码有集团成员，影响携号转网办理";
//            }
//            else
//            {
//                code = "611";
//                resultInfo += "您的号码有集团成员，影响携号转网办理";
//            }
//        }

        //海洋通 船东用户 不允许携出 add by xuzh5 2018-7-2 18:51:38
        IDataset idataset=UserOtherInfoQry.getOtherInfoByCodeUserId(userId, "HYT");
        if(IDataUtil.isNotEmpty(idataset)){
            if("1".equals(idataset.getData(0).getString("RSRV_STR2"))){
                if (StringUtils.isNotBlank(code))
                {
                    code += "|611";
                    resultInfo += "|您的号码有海洋通船东用户，影响携号转网办理!";
                }
                else
                {
                    code = "611";
                    resultInfo = "您的号码有海洋通船东用户，影响携号转网办理!";
                }
            }
        }

        //REQ201911280002 1125关于做好携号转网服务用户交互规范工作的通知（更新版）
        //一号多终端OM
        ids = RelaUUInfoQry.getRelationUusByUserIdBTypeCode(userId, "OM");
        if (IDataUtil.isNotEmpty(ids)){
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("一号多终端") > -1){
                    //已包含一号多终端
                }else {
                    tradeCode += "|611";
                    tradeInfo += "|一号多终端";
                }
            }
            else
            {
                tradeCode = "611";
                tradeInfo = "一号多终端";
            }
        }else {
            ids = RelaUUInfoQry.getUserRelationByUserIDA(userId,"OM");
            if (IDataUtil.isNotEmpty(ids)){
                if (StringUtils.isNotBlank(tradeInfo)) {
                    if (tradeInfo.indexOf("一号多终端") > -1) {
                        //已包含一号多终端
                    } else {
                        tradeInfo += "|一号多终端";
                    }
                } else {
                    tradeInfo = "一号多终端";
                }
            }
        }

        //全国亲情网（群主）MF
        ids = RelaUUInfoQry.getRelationUusByUserSnRole(serialNumber, "MF", "1");
        if (IDataUtil.isNotEmpty(ids)){
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("家庭网") > -1){
                    //已包含家庭网
                }else {
                    tradeCode += "|611";
                    tradeInfo += "|家庭网";
                }
            }
            else
            {
                tradeCode = "611";
                tradeInfo = "家庭网";
            }
        }

        //共享业务（群主）
        ids = ShareInfoQry.queryMemberRela(userId,"01");
        if (IDataUtil.isNotEmpty(ids)){
            if (StringUtils.isNotBlank(tradeCode))
            {
                if(tradeInfo.indexOf("共享业务") > -1){
                    //已包含共享业务
                }else {
                    tradeCode += "|611";
                    tradeInfo += "|共享业务";
                }
            }
            else
            {
                tradeCode = "611";
                tradeInfo = "共享业务";
            }
        }
        //END REQ201911280002 1125关于做好携号转网服务用户交互规范工作的通知（更新版）

        if(!StringUtils.isBlank(tradeCode)){
            tradeInfo = "请在申请授权码前做好" + tradeInfo.replace("|","、") + "业务变更手续。";
            if (StringUtils.isNotBlank(code))
            {
                code += "|" + tradeCode;
                resultInfo += "|" + tradeInfo;
            }
            else
            {
                code = tradeCode;
                resultInfo = tradeInfo;
            }
        }

        if (StringUtils.isBlank(code))
        {
            return false;
        }
        else
        {
            String msg = resultInfo;
            BreTipsHelp.addNorTipsInfo(paramIData, BreFactory.TIPS_TYPE_ERROR, -99, msg);
        }


        return false;
    }

    private void throwsException(String errorCode, String errorInfo) throws Exception
    {
        IDataset errorInfos = new DatasetList();

        IData errorData = new DataMap();
        errorData.put("TIPS_CODE", errorCode);
        errorData.put("TIPS_INFO", errorInfo);
        errorInfos.add(errorData);

        IData checkData = new DataMap();
        checkData.put("TIPS_TYPE_ERROR", errorInfos);

        CSAppException.breerr(checkData);
    }
    private IData GetMaxEndDate(IDataset Array) throws Exception
    {
        String MaxDate = "";
        int iMax = 0;
        for(int i = 0; i < Array.size(); i++)
        {
        	if("".equals(MaxDate.toString()))
        	{
        		MaxDate = Array.getData(i).getString("END_DATE");
        		iMax = i;
        		continue;
        	}
        	String date = SysDateMgr.decodeTimestamp(MaxDate, SysDateMgr.PATTERN_STAND);
            String compareDate = Array.getData(i).getString("END_DATE");
            compareDate = SysDateMgr.decodeTimestamp(compareDate, SysDateMgr.PATTERN_STAND);
            int result = 0;
            if(date.substring(0, 10).equals(SysDateMgr.END_TIME_FOREVER)
                    && compareDate.substring(0, 10).equals(SysDateMgr.END_TIME_FOREVER))
            {
            	result = 0;
            	continue;
            }
            result = date.compareTo(compareDate);
        	if(result < 0)
        	{
        		MaxDate = Array.getData(i).getString("END_DATE");
        		iMax = i;
        	}
        }
        return Array.getData(iMax);
    }
}
