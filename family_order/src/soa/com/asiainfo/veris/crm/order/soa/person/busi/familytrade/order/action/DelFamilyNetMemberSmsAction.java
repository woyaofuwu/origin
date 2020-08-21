
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.DelFamilyNetMemberReqData;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class DelFamilyNetMemberSmsAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        DelFamilyNetMemberReqData reqData = (DelFamilyNetMemberReqData) btd.getRD();
        UcaData uca = reqData.getUca();
        String userId = uca.getUserId();
        String serialNumber = uca.getSerialNumber();
        
        String strSmsMain = "";
        String strSmsSecond = "";
        //IDataset userDiscntList = UserDiscntInfoQry.getDiscntsByPMode(userId, "05");
        IDataset familyOffers = UpcCall.queryMembOffersByProdMode("05", "D");
		String discntArrays = this.getDiscntArray(familyOffers);

		List<DiscntTradeData> userDiscntList = uca.getUserDiscntsByDiscntCodeArray(discntArrays);  
		
        if ( ArrayUtil.isNotEmpty(userDiscntList) ){
        	DiscntTradeData userDiscnt = userDiscntList.get(0);
            String discntCode = userDiscnt.getDiscntCode();
        	if( "3403".equals(discntCode) || "3404".equals(discntCode) ){
        		/*尊敬的XXX客户，主号码XXXXXXXXXXX已删除亲亲网成员：XXXXXXXXXXX（***）、XXXXXXXXXXX（***）、XXXXXXXXXXX（***）、XXXXXXXXXXX（***）、……，
        		被删除亲亲网成员的短号及优惠将于24小时内失效（失效后可组建或加入其他亲亲网继续享受优惠）。咨询电话10086*/
        		strSmsMain = "，被删除亲亲网成员的短号及优惠将于24小时内失效（失效后可组建或加入其他亲亲网继续享受优惠）。";
        		strSmsMain+= "咨询电话10086。";
        		
        		/*尊敬的XXX客户：您所在亲亲网的成员XXXXXXXXXXX（***）已退出，退出成员的短号及优惠将于24小时内失效（失效后可组建或加入其他亲亲网继续享受优惠）。
        		咨询电话10086。*/
        		strSmsSecond = "已退出，退出成员的短号及优惠将于24小时内失效（失效后可组建或加入其他亲亲网继续享受优惠）。";
        		strSmsSecond+=  "咨询电话10086。";
        	}else if( "3410".equals(discntCode) || "3411".equals(discntCode) ){
        		/*尊敬的XXX客户，主号码XXXXXXXXXXX已删除亲亲网成员：XXXXXXXXXXX（***）、XXXXXXXXXXX（***）、XXXXXXXXXXX（***）、XXXXXXXXXXX（***）、……，
        		被删除亲亲网成员的短号及优惠将于24小时内失效（失效后可组建或加入其他亲亲网继续享受优惠）。亲亲网本月可累计删除9个成员。咨询电话10086*/
        		strSmsMain = "，被删除亲亲网成员的短号及优惠将于24小时内失效（失效后可组建或加入其他亲亲网继续享受优惠）。";
        		strSmsMain+= "亲亲网本月可累计删除9个成员。咨询电话10086。";
        		
        		/*尊敬的XXX客户：您所在亲亲网的成员XXXXXXXXXXX（***）已退出，退出成员的短号及优惠将于24小时内失效（失效后可组建或加入其他亲亲网继续享受优惠）。
        		亲亲网本月内最多可退出9个亲亲网成员。咨询电话10086。*/
        		strSmsSecond = "已退出，退出成员的短号及优惠将于24小时内失效（失效后可组建或加入其他亲亲网继续享受优惠）。";
        		strSmsSecond+=  "亲亲网本月内最多可退出9个亲亲网成员。咨询电话10086。";
        	}
        }
        // 查询已删除的成员
        List<RelationTradeData> tradeRelations = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);

        //if (null != tradeRelations && tradeRelations.size() > 0)
        if( CollectionUtils.isNotEmpty(tradeRelations) )
        {
            RelationTradeData relaTrade = tradeRelations.get(0);
            String userIdA = relaTrade.getUserIdA();
            IDataset uuList = RelaUUInfoQry.qryRelaUUByUIdAAllDB(userIdA, "45");
            if (IDataUtil.isNotEmpty(uuList))
            {
                IData smsData = new DataMap();
                smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
                smsData.put("FORCE_OBJECT", "10086");// 发送对象
                smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空

                IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(userId, "45", null);

                String roleCodeB = "";
                if (IDataUtil.isNotEmpty(result))
                {
                    IData rela = result.getData(0);
                    roleCodeB = rela.getString("ROLE_CODE_B");
                }

                // 如果是主号删除成员
                if (StringUtils.equals(roleCodeB, "1"))
                {
                    StringBuilder smsContent = new StringBuilder();
                    StringBuilder strContentNowEffect = new StringBuilder();
                    StringBuilder strContentNextEffect = new StringBuilder();

                    for (int i = 0, size = tradeRelations.size(); i < size; i++)
                    {
                        RelationTradeData tradeRela = tradeRelations.get(i);
                        String serialNumberB = tradeRela.getSerialNumberB();
                        String shortCodeB = tradeRela.getShortCode();
                        String effectMode = tradeRela.getRsrvTag1();// 失效方式：0-下月失效 1-立即失效

                        // 下月失效
                        /*if (StringUtils.equals(effectMode, "0"))
                        {
                            if (StringUtils.isNotBlank(shortCodeB))
                            {
                                strContentNextEffect.append(serialNumberB);
                                strContentNextEffect.append("(");
                                strContentNextEffect.append(shortCodeB);
                                strContentNextEffect.append(")");
                            }
                            else
                            {
                                strContentNextEffect.append(serialNumberB);
                            }

                            if (i + 1 < size)
                            {
                                strContentNextEffect.append("、");
                            }
                        }*/

                        // 立即失效
                        //if (StringUtils.equals(effectMode, "1"))
                        //{
                            if (StringUtils.isNotBlank(shortCodeB))
                            {
                                strContentNowEffect.append(serialNumberB);
                                strContentNowEffect.append("(");
                                strContentNowEffect.append(shortCodeB);
                                strContentNowEffect.append(")");
                            }
                            else
                            {
                                strContentNowEffect.append(serialNumberB);
                            }

                            if (i + 1 < size)
                            {
                                strContentNowEffect.append("、");
                            }
                        //}
                    }

                    /*if (StringUtils.isNotBlank(strContentNextEffect) && StringUtils.isBlank(strContentNowEffect))
                    {
                        smsContent.append("主号码");
                        smsContent.append(serialNumber);
                        smsContent.append("已删除亲亲网成员：");
                        smsContent.append(strContentNextEffect);
                        smsContent.append("。下月生效。咨询电话10086。");
                    }*/
                    if (StringUtils.isNotBlank(strContentNowEffect) && StringUtils.isBlank(strContentNextEffect))
                    {
                        smsContent.append("主号码");
                        smsContent.append(serialNumber);
                        smsContent.append("已删除亲亲网成员：");
                        smsContent.append(strContentNowEffect);
                        smsContent.append(strSmsMain);
                    }
                    /*if (StringUtils.isNotBlank(strContentNextEffect) && StringUtils.isNotBlank(strContentNowEffect))
                    {
                        smsContent.append("主号码");
                        smsContent.append(serialNumber);
                        smsContent.append("已删除亲亲网成员：");
                        smsContent.append(strContentNextEffect);
                        smsContent.append("下月生效；");

                        smsContent.append("主号码");
                        smsContent.append(serialNumber);
                        smsContent.append("已删除亲亲网成员：");
                        smsContent.append(strContentNowEffect);
                        smsContent.append("立即生效。咨询电话10086。");
                    }*/

                    for (int i = 0, size = uuList.size(); i < size; i++)
                    {
                        IData uuInfo = uuList.getData(i);
                        String serialNumberB = uuInfo.getString("SERIAL_NUMBER_B");

                        IData userProd = UcaInfoQry.qryUserMainProdInfoBySn(serialNumberB);
                        if(IDataUtil.isEmpty(userProd)){ //如果没有主主产品的用户就跳过不下发短信。
                        	continue;
                        }
                        String brandCode = userProd.getString("BRAND_CODE");
                        String brandName = UBrandInfoQry.getBrandNameByBrandCode(brandCode);

                        StringBuilder smsInfo = new StringBuilder();
                        smsInfo.append("尊敬的");
                        smsInfo.append(brandName);
                        smsInfo.append("客户，");
                        smsInfo.append(smsContent);

                        smsData.put("NOTICE_CONTENT", smsInfo.toString());// 短信内容
                        smsData.put("RECV_OBJECT", serialNumberB);// 接收对象

                        PerSmsAction.insTradeSMS(btd, smsData);
                    }
                }

                // 如果是副号删除成员
                if (StringUtils.equals(roleCodeB, "2"))
                {
                    RelationTradeData tradeRela = tradeRelations.get(0);
                    String serialNumberB = tradeRela.getSerialNumberB();
                    String shortCodeB = tradeRela.getShortCode();
                    String effectMode = tradeRela.getRsrvTag1();// 失效方式：0-下月失效 1-立即失效

                    IData userProd = UcaInfoQry.qryUserMainProdInfoBySn(serialNumberB);
                    String brandCode = userProd.getString("BRAND_CODE");
                    String brandName = UBrandInfoQry.getBrandNameByBrandCode(brandCode);

                    StringBuilder smsInfo = new StringBuilder();
                    smsInfo.append("尊敬的");
                    smsInfo.append(brandName);
                    smsInfo.append("客户：您所在亲亲网的成员");

                    if (StringUtils.isNotBlank(shortCodeB))
                    {
                        smsInfo.append(serialNumberB);
                        smsInfo.append("(");
                        smsInfo.append(shortCodeB);
                        smsInfo.append(")");
                    }
                    else
                    {
                        smsInfo.append(serialNumberB);
                    }

                    // 下月失效
                    /*if (StringUtils.equals(effectMode, "0"))
                    {
                        smsInfo.append("已退出，下月生效。咨询电话10086。");
                    }*/
                    // 立即失效
                    /*if (StringUtils.equals(effectMode, "1"))
                    {*/
                        smsInfo.append(strSmsSecond);
                    //}

                    for (int i = 0, size = uuList.size(); i < size; i++)
                    {
                        IData uuInfo = uuList.getData(i);
                        String snB = uuInfo.getString("SERIAL_NUMBER_B");

                        smsData.put("NOTICE_CONTENT", smsInfo.toString());// 短信内容
                        smsData.put("RECV_OBJECT", snB);// 接收对象

                        PerSmsAction.insTradeSMS(btd, smsData);
                    }
                }
            }
        }
    }
    
    public String getDiscntArray(IDataset datas) throws Exception
    {
    	String discnts ="";
    	if(IDataUtil.isNotEmpty(datas))
    	{
    		for(int i=0;i<datas.size();i++)
    		{
    			IData data = datas.getData(i);
    			discnts += data.getString("OFFER_CODE") +",";
    		}
    		if(StringUtils.isNotBlank(discnts))
    		{
    			discnts = discnts.substring(0, discnts.length()-1);
    		}
    	}
    	return discnts;
    }

}
