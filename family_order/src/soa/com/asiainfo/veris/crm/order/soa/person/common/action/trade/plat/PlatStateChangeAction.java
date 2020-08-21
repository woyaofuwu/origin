
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class PlatStateChangeAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        UcaData uca = btd.getRD().getUca();
        List<PlatSvcTradeData> platSvcTradeDatas = uca.getUserPlatSvcs();
        if (platSvcTradeDatas != null && platSvcTradeDatas.size() > 0)
        {
            // 读取配置
            IDataset configs = CommparaInfoQry.getCommparaByAttrCode1("CSM", "888", btd.getRD().getTradeType().getTradeTypeCode(), btd.getRoute(), null);
            if (IDataUtil.isEmpty(configs))
            {
                // 没有找到配置，不处理
                return;
            }
            else
            {
                String operCode = configs.getData(0).getString("PARA_CODE5");
                String validFlag = configs.getData(0).getString("PARA_CODE3");
                String paramCode = configs.getData(0).getString("PARAM_CODE");
                if (StringUtils.isBlank(operCode) || "0".equals(validFlag))
                {
                    return;
                }

                int size = platSvcTradeDatas.size();
                for (int i = 0; i < size; i++)
                {
                    PlatSvcTradeData pstd = platSvcTradeDatas.get(i);

                    // 处理局数据不存在的情况
                    PlatOfficeData officeData = PlatOfficeData.getInstance(pstd.getElementId());

                    if (!BofConst.MODIFY_TAG_USER.equals(pstd.getModifyTag()))
                    {
                        continue;
                    }
                    if (pstd.getStartDate().compareTo(btd.getRD().getAcceptTime()) > 0 && !PlatConstants.OPER_CANCEL_ORDER.equals(operCode))
                    {
                        continue;
                    }
                    if ("98008057_98008003_98008004_98008005_98008013_98009044_98009056".indexOf(pstd.getElementId()) >= 0 && !PlatConstants.OPER_CANCEL_ORDER.equals(operCode))
                    {
                        continue;
                    }

                    // 单停时，只处理手机报
                    if ("DANXIANGTINGJI".equals(paramCode) && officeData != null)
                    {
                        if (!(("801234".equals(officeData.getSpCode()) || "820264".equals(officeData.getSpCode())) && "05".equals(officeData.getBizTypeCode())))
                        {
                            continue;
                        }
                    }

                    // 停机不处理飞信交友
                    if ("TINGJI".equals(paramCode) && officeData != null)
                    {
                        if ("FXSJHYDG".equals(officeData.getBizCode()))
                        {
                            continue;
                        }

                        // 停机时，暂停需要判断EDBIZ_PROCESS_TAG，第4位暂停，第9位主动暂停
                        if (PlatConstants.OPER_PAUSE.equals(operCode)  && officeData != null)
                        {
                            String edBizProcessTag = officeData.getEdBizProcessTag();
                            if (!("1".equals(edBizProcessTag.substring(3, 4))) || "1".equals(edBizProcessTag.substring(9, 10)))
                            {
                                continue;
                            }
                        }
                    }

                    // 销户不处理农信通
                    if (("YUXIAOHU".equals(paramCode) || "XIAOHU".equals(paramCode))  && officeData != null)
                    {
                        if (PlatConstants.PLAT_FARMING_CREDIT_ALL.equals(officeData.getBizTypeCode()))
                        {
                            if (officeData.getBizCode().split("-").length == 3)
                            {
                                continue;
                            }
                        }
                    }

                    // 不退订无线音乐会员，不退订默认开通业务
                    if ("GAIHAO".equals(paramCode) && officeData != null)
                    {
                        // 如果是无线音乐会员
                        if ("19".equals(officeData.getBizTypeCode()))
                        {
                            continue;
                        }

                        // 如果是默认开通业务
                        boolean isDefaultSvc = false;
                        IDataset bindDefaultOpenSvcConfigs = CommparaInfoQry.getCommparaByAttrCode1("CSM", "1113", "defaultopen", Route.CONN_CRM_CEN, null);
                        for (int j = 0; j < bindDefaultOpenSvcConfigs.size(); j++)
                        {
                            IData bindDefaultSvc = bindDefaultOpenSvcConfigs.getData(j);
                            if (pstd.getElementId().equals(bindDefaultSvc.getString("PARAM_CODE")))
                            {
                                isDefaultSvc = true;
                                break;
                            }
                        }

                        if (isDefaultSvc)
                        {
                            continue;
                        }
                    }

                    PlatSvcTradeData newPlatSvcTradeData = pstd.clone();
                    newPlatSvcTradeData.setOperCode(operCode);
                    if (PlatConstants.OPER_PAUSE.equals(operCode))
                    {
                        if (!PlatConstants.STATE_OK.equals(pstd.getBizStateCode()))
                        {
                            continue;
                        }

                        newPlatSvcTradeData.setBizStateCode(PlatConstants.STATE_PAUSE);
                        newPlatSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        newPlatSvcTradeData.setRsrvTag1("T");
                        newPlatSvcTradeData.setIsNeedPf("1");
                    }
                    else if (PlatConstants.OPER_RESTORE.equals(operCode))//恢复
                    {
                        if (!PlatConstants.STATE_PAUSE.equals(pstd.getBizStateCode()))
                        {
                            continue;
                        }
                        if ("40227762".equals(pstd.getElementId()) || "80025539".equals(pstd.getElementId()))//未来电视和IPTV平台服务
                        {
                        	 String realNameSwitch = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", 
                             		new java.lang.String[]{ "TYPE_ID", "DATA_ID" }, "PDATA_ID", new java.lang.String[]{ "EXPERIENCE_SWITCH", "ON" });
                        	if("EXPERIENCE_STOP".equals(pstd.getRsrvStr8())||"1".equals(realNameSwitch)){
                        		 continue;
                        	}
                        }

                        if ("T".equals(newPlatSvcTradeData.getRsrvTag1()))
                        {
                            newPlatSvcTradeData.setBizStateCode(PlatConstants.STATE_OK);
                            newPlatSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                            newPlatSvcTradeData.setRsrvTag1("");
                            newPlatSvcTradeData.setIsNeedPf("1");
                        }
                        else
                        {
                            continue;
                        }
                    }
                    else if (PlatConstants.OPER_CANCEL_ORDER.equals(operCode))
                    {
                        if (PlatConstants.STATE_CANCEL.equals(pstd.getBizStateCode()))
                        {
                            continue;
                        }
                        
                        //如果是过户业务，且平台服务是魔百和
                        if (("40227762".equals(pstd.getElementId())|| "80025539".equals(pstd.getElementId())) && "100".equals(btd.getTradeTypeCode()))
                        {
                        	String widenetDeal = btd.getRD().getPageRequestData().getString("WIDENET_DEAL", "");
                        	//System.out.println("=================PlatStateChangeAction================:widenetDeal"+widenetDeal);
                        	//用户已选择过户，则跳出循环，不退订了。
                        	if( "1".equals(widenetDeal))
                        	{
	                        	SendSMS(btd);
	                        	continue;
                        	}
                        	//否则终止魔百和营销活动
                        	else
                        	{
                        		IDataset userActives=SaleActiveInfoQry.getUserSaleActiveInfoInUse(btd.getRD().getUca().getUserId(), "69908030");
                            	if(userActives!=null && userActives.size()>0){
                            		IData activeEndDataParam = new DataMap();

                                    activeEndDataParam.put("SERIAL_NUMBER", btd.getRD().getUca().getSerialNumber());
                                    activeEndDataParam.put("PRODUCT_ID", userActives.getData(0).getString("PRODUCT_ID"));
                                    activeEndDataParam.put("PACKAGE_ID", userActives.getData(0).getString("PACKAGE_ID"));
                                    activeEndDataParam.put("CAMPN_TYPE", userActives.getData(0).getString("CAMPN_TYPE"));
                                    activeEndDataParam.put("RELATION_TRADE_ID", userActives.getData(0).getString("RELATION_TRADE_ID"));
                                    activeEndDataParam.put("IS_RETURN", "0");
                                    activeEndDataParam.put("CALL_TYPE", SaleActiveConst.CALL_TYPE_ACTIVE_TRANS);
                                    String ForceEndDate =SysDateMgr.getDateLastMonthSec( btd.getRD().getAcceptTime() );
                                    activeEndDataParam.put("FORCE_END_DATE", ForceEndDate);
                                    activeEndDataParam.put("END_DATE_VALUE", BofConst.MODIFY_TAG_FORCE_END);

                                    CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg", activeEndDataParam);
                            	}
                        	}
                        }
                        
                        newPlatSvcTradeData.setBizStateCode(PlatConstants.STATE_CANCEL);
                        newPlatSvcTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        if ("98008057_98008003_98008004_98008005_98008013_98009044".indexOf(pstd.getElementId()) >= 0)
                        {
                            newPlatSvcTradeData.setIsNeedPf("0");
                        }
                        else
                        {
                            newPlatSvcTradeData.setIsNeedPf("1");
                        }
                        newPlatSvcTradeData.setEndDate(btd.getRD().getAcceptTime());
                    }
                    newPlatSvcTradeData.setOprSource("08");
                    //如果是彩印业务，则将渠道编码设置为11（营业前台）
                    if (null != officeData && ("14".equals(officeData.getBizTypeCode()) || 
                			"36".equals(officeData.getBizTypeCode()) ||
                			"28".equals(officeData.getBizTypeCode()) ||
                			"19".equals(officeData.getBizTypeCode()) ||
                			"25".equals(officeData.getBizTypeCode()) ||
                			"77".equals(officeData.getBizTypeCode()) ||
                			"46".equals(officeData.getBizTypeCode()) ||
                			"78".equals(officeData.getBizTypeCode()) ||
                			"80".equals(officeData.getBizTypeCode())))
                    {
                    	newPlatSvcTradeData.setOprSource("11");
                    }
                    newPlatSvcTradeData.setOperTime(btd.getRD().getAcceptTime());
                    newPlatSvcTradeData.setAllTag("01");
                    newPlatSvcTradeData.setActiveTag("1");// 被动
                    btd.add(uca.getSerialNumber(), newPlatSvcTradeData);
                }

            }

        }
    }
    
    private void SendSMS(BusiTradeData btd) throws Exception
    {
        SmsTradeData std = new SmsTradeData();
        
        std.setSmsNoticeId(SeqMgr.getSmsSendId());
        std.setEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
        std.setBrandCode(btd.getRD().getUca().getBrandCode());
        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        std.setSmsNetTag("0");
        std.setChanId("11");
        std.setSendObjectCode("6");
        std.setSendTimeCode("1");
        std.setSendCountCode("1");
        std.setRecvObjectType("00");

        std.setRecvId(btd.getRD().getUca().getUserId());
        std.setSmsTypeCode("20");
        std.setSmsKindCode("02");
        std.setNoticeContentType("0");
        std.setReferedCount("0");
        std.setForceReferCount("1");
        std.setForceObject("");
        std.setForceStartTime("");
        std.setForceEndTime("");
        std.setSmsPriority("50");
        std.setReferTime(SysDateMgr.getSysTime());
        std.setReferDepartId(CSBizBean.getVisit().getDepartId());
        std.setReferStaffId(CSBizBean.getVisit().getStaffId());
        std.setDealTime(SysDateMgr.getSysTime());
        std.setDealStaffid(CSBizBean.getVisit().getStaffId());
        std.setDealDepartid(CSBizBean.getVisit().getDepartId());
        std.setDealState("0");// 处理状态，0：未处理
        std.setRemark("魔百和过户短信");
        std.setRevc1("");
        std.setRevc2("");
        std.setRevc3("");
        std.setRevc4("");
        std.setMonth(SysDateMgr.getSysTime().substring(5, 7));
        std.setDay(SysDateMgr.getSysTime().substring(8, 10));
        std.setCancelTag("0");
        String newCustName = btd.getRD().getPageRequestData().getString("CUST_NAME", "");
        String oldCustName = btd.getRD().getUca().getUserOriginalData().getCustPerson().getCustName();
        // 短信截取
        String strContent = "尊敬的客户，您的魔百和业务和营销活动已随手机号码从"+oldCustName +"客户（老客户姓名）过户至"+newCustName
    	+"客户（新客户姓名），业务使用和营销活动优惠、费用支付等原业务权责也将由"+newCustName+"客户（新客户姓名）承接，如遇费用问题由过户双方自行协商解决。"; 
        std.setNoticeContent(strContent);

        std.setRecvObject(btd.getRD().getUca().getSerialNumber());// 发送号码
        
        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), std);
    }

}
