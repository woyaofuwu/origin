
package com.asiainfo.veris.crm.order.soa.person.common.action.sms;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.DynamicDecisionTable;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreSuperLimit;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.mvelmisc.MvelMiscCheck;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSmsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class PerSmsAction implements ITradeAction
{
    /**
     * 改为BOF插短信
     * 
     * @param btd
     * @param smsData
     * @throws Exception
     *             wangjx 2013-9-7
     */
    @SuppressWarnings(
    { "rawtypes", "unchecked" })
    public static void insTradeSMS(BusiTradeData btd, IData smsData) throws Exception
    {
        String sysdate = btd.getRD().getAcceptTime();
        String tradeTypeCode = btd.getTradeTypeCode();

        SmsTradeData std = new SmsTradeData();
        std.setSmsNoticeId(smsData.getString("SMS_NOTICE_ID", SeqMgr.getSmsSendId()));
        std.setEparchyCode(smsData.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode()));
        std.setBrandCode(smsData.getString("BRAND_CODE"));
        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        std.setSmsNetTag(smsData.getString("SMS_NET_TAG", "0"));
        std.setChanId(smsData.getString("CHAN_ID", "11"));
        std.setSendObjectCode(smsData.getString("SEND_OBJECT_CODE", "6"));
        std.setSendTimeCode(smsData.getString("SEND_TIME_CODE", "1"));
        std.setSendCountCode(smsData.getString("SEND_COUNT_CODE", "1"));
        std.setRecvObjectType(smsData.getString("RECV_OBJECT_TYPE", "00"));
        std.setRecvObject(smsData.getString("RECV_OBJECT"));
        std.setRecvId(smsData.getString("RECV_ID", "-1"));
        // std.setSmsTypeCode(smsData.getString("SMS_TYPE_CODE", "20"));
        std.setSmsTypeCode(smsData.getString("SMS_TYPE_CODE", "I0"));
        std.setSmsKindCode(smsData.getString("SMS_KIND_CODE", "02"));
        std.setNoticeContentType(smsData.getString("NOTICE_CONTENT_TYPE", "0"));
        std.setReferedCount(smsData.getString("REFERED_COUNT", "0"));
        std.setForceReferCount(smsData.getString("FORCE_REFER_COUNT", "1"));
        std.setForceObject(smsData.getString("FORCE_OBJECT"));
        std.setForceStartTime(smsData.getString("FORCE_START_TIME", ""));
        std.setForceEndTime(smsData.getString("FORCE_END_TIME", ""));
        std.setSmsPriority(smsData.getString("SMS_PRIORITY", "50"));
        std.setReferTime(smsData.getString("REFER_TIME", sysdate));
        std.setReferDepartId(smsData.getString("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        std.setReferStaffId(smsData.getString("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        std.setDealTime(smsData.getString("DEAL_TIME", sysdate));
        std.setDealStaffid(smsData.getString("DEAL_STAFFID", CSBizBean.getVisit().getStaffId()));
        std.setDealDepartid(smsData.getString("DEAL_DEPARTID", CSBizBean.getVisit().getDepartId()));
        std.setDealState("0");// 处理状态，0：未处理
        std.setRemark(smsData.getString("REMARK"));
        std.setRevc1(smsData.getString("REVC1"));
        std.setRevc2(smsData.getString("REVC2"));
        std.setRevc3(smsData.getString("REVC3"));
        std.setRevc4(smsData.getString("REVC4"));
        std.setMonth(sysdate.substring(5, 7));
        std.setDay(sysdate.substring(8, 10));
        std.setCancelTag(smsData.getString("CANCEL_TAG"));

        // 短信截取
        String content = smsData.getString("NOTICE_CONTENT", "");
        int charLength = SmsSend.getCharLength(content, 4000);
        content = content.substring(0, charLength);
        std.setNoticeContent(content);

        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), std);
    }

    /**
     * 登记短信处理，直接写ti_o_sms表 业务短信一般不多，直接采用循环发送方式，不需要采用批量
     * 
     * @param regSmsInfos
     * @param btd
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    protected void dealRegSms(IDataset regSmsInfos, BusiTradeData btd) throws Exception
    {
        // 没有登记短信，则返回
        if (IDataUtil.isEmpty(regSmsInfos))
        {
            return;
        }

        for (Object obj : regSmsInfos)
        {
            IData regSmsInfo = (IData) obj;

            IData smsData = new DataMap();
            // add by chenzm 宽带短信处理
            // 630 校园宽带开户
            // 613 FTTH宽带开户
            // 611 GPON 子账号宽带开户
            // 612 ADSL宽带开户
            // 600 GPON宽带开户
            // 631 校园宽带产品变更
            if ("631".equals(btd.getTradeTypeCode()) || "600".equals(btd.getTradeTypeCode()) || "611".equals(btd.getTradeTypeCode()) || "612".equals(btd.getTradeTypeCode()) || "613".equals(btd.getTradeTypeCode()) || "630".equals(btd.getTradeTypeCode()))
            {
                smsData = getWidenetCommonSmsInfo(btd, regSmsInfo);
            }
            else
            {
                smsData = getCommonSmsInfo(btd, regSmsInfo);
            }
            smsData.put("NOTICE_CONTENT", regSmsInfo.getString("NOTICE_CONTENT"));

            SmsSend.insSms(smsData);
        }

    }

    /**
     * 处理完工短信，先登记tf_b_trade_sms，完工的时候再写ti_o_sms 业务短信一般不多，直接采用循环发送方式，不需要采用批量
     * 
     * @param btd
     * @param replaceContent
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    protected void dealSucSms(IDataset sucSmsInfos, BusiTradeData btd) throws Exception
    {
        // 没有完工短信，则返回
        if (IDataUtil.isEmpty(sucSmsInfos))
        {
            return;
        }

        // 原逻辑，特定工号不发短信
        IDataset commparas = CommparaInfoQry.getCommByParaAttr("CSM", "1112", CSBizBean.getUserEparchyCode());
        if (IDataUtil.isNotEmpty(commparas))
        {
            String staffId = CSBizBean.getVisit().getStaffId();
            boolean flag = false;

            for (Object obj : commparas)
            {
                IData commpara = (IData) obj;

                String paraCode23 = commpara.getString("PARA_CODE23", "");
                String[] staffIds = paraCode23.split("|");
                for (int i = 0; i < staffIds.length; i++)
                {
                    if (staffId.equals(staffIds[i]))
                    {
                        flag = true;
                        break;
                    }
                }

                if (flag)
                {
                    break;
                }
            }

            if (flag)
            {
                return;
            }
        }

        // 特殊业务不合并短信
        if (BofConst.TRADE_TYPE_CODE_FAMILYACCOUNTMANAGE.equals(btd.getTradeTypeCode()) || BofConst.TRADE_TYPE_CODE_FAMILYACCOUNTDESTROY.equals(btd.getTradeTypeCode()))
        {
            for (Object obj : sucSmsInfos)
            {
                IData sucSmsInfo = (IData) obj;

                IData smsData = getCommonSmsInfo(btd, sucSmsInfo);
                smsData.put("NOTICE_CONTENT", sucSmsInfo.getString("NOTICE_CONTENT"));

                insTradeSMS(btd, smsData);
            }
        }
        else
        {
            // 原逻辑，根据order_no合并短信
            String noticeContent = "";
            int orderNo = -1;
            String templateIds = "";
            IData sucSmsInfo = new DataMap();
            for (Object obj : sucSmsInfos)
            {
                sucSmsInfo = (IData) obj;
                String templateId = sucSmsInfo.getString("TEMPLATE_ID");
                String notice_content = sucSmsInfo.getString("NOTICE_CONTENT");
                int order_no = sucSmsInfo.getInt("ORDER_NO");

                // 第一条
                if (orderNo < 0)
                {
                    orderNo = order_no;
                }

                if (StringUtils.isNotBlank(notice_content))
                {
                    templateIds += templateId + ",";
                }

                sucSmsInfo.put("_TEMPLATE_ID", templateIds);

                // 发上一条
                if (orderNo < order_no && !"".equals(noticeContent.trim()))
                {
                    IData smsData = getCommonSmsInfo(btd, sucSmsInfo);
                    smsData.put("NOTICE_CONTENT", noticeContent);

                    insTradeSMS(btd, smsData);
                    noticeContent = "";
                    templateIds = "";
                    orderNo = order_no;
                }

                // 合并
                noticeContent = noticeContent + notice_content;

            }

            // 发最后一条
            if (!"".equals(noticeContent.trim()))
            {

                IData smsData = new DataMap();
                // add by chenzm 宽带密码变更完工短信下发
                // 631 校园宽带产品变更
                if ("631".equals(btd.getTradeTypeCode()) || "607".equals(btd.getTradeTypeCode()) || "626".equals(btd.getTradeTypeCode()) || "627".equals(btd.getTradeTypeCode()) || "634".equals(btd.getTradeTypeCode()))
                {
                    smsData = getWidenetCommonSmsInfo(btd, sucSmsInfo);
                }
                else
                {
                    smsData = getCommonSmsInfo(btd, sucSmsInfo);
                }
                smsData.put("NOTICE_CONTENT", noticeContent);
                insTradeSMS(btd, smsData);
            }
        }
    }

    /**
     * 获取模板拼串信息
     * 
     * @param filterSmsInfos
     * @param regSmsInfos
     * @param sucSmsInfos
     * @param rtd
     * @param relaInfo
     * @throws Exception
     *             wangjx 2013-9-7
     */
    @SuppressWarnings("rawtypes")
    protected void dealTemplate(IDataset filterSmsInfos, IDataset regSmsInfos, IDataset sucSmsInfos, RegTradeData rtd, IData tradeInfo) throws Exception
    {
        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());

        for (Object obj : filterSmsInfos)
        {
            IData data = new DataMap();
            data.putAll((IData) obj);
            String serialNumberB = "";// 存放特殊处理手机号码
            String userIdB = "";// 存放特殊处理用户ID
            String roleCode = "";// 模板记录存放的OBJ_CODE字段
            String modifyTag = "";// 模板记录存放的MODIFY_TAG字段

            if (IDataUtil.isNotEmpty(tradeInfo))
            {
                roleCode = data.getString("OBJ_CODE", "");
                modifyTag = data.getString("MODIFY_TAG", "");
                String roleCodeB = tradeInfo.getString("ROLE_CODE_B", tradeInfo.getString("RSRV_TAG2"));
                String modifyTagTrade = tradeInfo.getString("MODIFY_TAG");
                if (roleCode.equals(roleCodeB) && modifyTag.equals(modifyTagTrade))// 确定模板ID
                {
                    serialNumberB = tradeInfo.getString("SERIAL_NUMBER_B", tradeInfo.getString("RSRV_STR6"));
                    userIdB = tradeInfo.getString("USER_ID_B", tradeInfo.getString("USER_ID"));
                }
                else
                {
                    continue;
                }
            }

            exector.prepare(rtd, tradeInfo);// 模板变量解析

            String content = TemplateBean.getTemplate(data.getString("TEMPLATE_ID"));
            String replaceContent = exector.applyTemplate(content);
            int idx = replaceContent.indexOf("@{");
            if (idx > -1 && replaceContent.indexOf("}", idx) > -1)
            {
                replaceContent = exector.applyTemplate(replaceContent);
            }
            data.put("NOTICE_CONTENT", replaceContent);
            if (!"".equals(serialNumberB))
            {
                data.put("SPEC_SERIAL_NUMBER", serialNumberB);
            }
            if (!"".equals(userIdB))
            {
                data.put("SPEC_USER_ID", userIdB);
            }

            String eventType = data.getString("EVENT_TYPE");

            // 登记短信
            if (BofConst.SMS_REG.equals(eventType))
            {
                regSmsInfos.add(data);
            }
            // 完工短信
            else if (BofConst.SMS_SUC.equals(eventType))
            {
                sucSmsInfos.add(data);
            }
        }
    }

    /**
     * 短信处理 1、匹配模板 2、过滤模板 3、解析模板 4、发送短信
     */
    @SuppressWarnings("rawtypes")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        MainTradeData mainTradeData = btd.getMainTradeData();

        if (!btd.isNeedSms())
        {
            return;
        }

        // 批量不发短信
        if (StringUtils.isNotEmpty(mainTradeData.getBatchId()))
        {
        	//REQ201412260001_2015年新春入网预存有礼活动开发需求.
        	//2014-12-29 营销活动新春入网预存有礼批量必须发短信
        	String tradeTypeCode=mainTradeData.getTradeTypeCode();
        	String product_id=mainTradeData.getRsrvStr1();
        	if(product_id==null){
        		product_id="";
        	}
        	String package_id=mainTradeData.getRsrvStr2();
        	if(package_id==null){
        		package_id="";
        	}
        	if(!"240".equals(tradeTypeCode)&&!"69900869".equals(product_id)&&!"60012511".equals(package_id)){
        		return;
        	}
        }
        
        // REQ201506020023 证件办理业务触发完善客户信息
        // 自动触发升位身份证不能发短信chenxy3 2015-07-16
        String remark=mainTradeData.getRemark(); 
        String updstaff=CSBizBean.getVisit().getStaffId(); 
        if ("60".equals(mainTradeData.getTradeTypeCode())&&"ITFSM000".equals(updstaff)&&"系统自动触发完善证件号码或地址信息".equals(remark))
        {
        	return;
        }

		//宽带1+营销活动自动续约，特殊处理--Start
        String auto_book = (String)btd.getRD().getPageRequestData().getString("AUTO_BOOK","");
        if("1".equals(auto_book))
        {
        	String tradeTypeCode=mainTradeData.getTradeTypeCode();
        	String product_id=mainTradeData.getRsrvStr1();
        	//宽带1+营销活动自动续约特殊处理，不下发短信
        	if("240".equals(tradeTypeCode)&& ("69908001".equals(product_id) || "69908012".equals(product_id) || "69908015".equals(product_id)))
        	{
        		return;
        	}
        }
        //宽带1+营销活动自动续约，特殊处理--End        
        RegTradeData regData = new RegTradeData(btd);

        // 1、匹配模板
        // 根据TRADE_TYPE_CODE、BRAND_CODE等基本条件查询短信配置表TD_B_TRADE_SMS，匹配是否需要发短信
        String tradeTypeCode = mainTradeData.getTradeTypeCode();
        String processTagSet = mainTradeData.getProcessTagSet();
        int pLength = processTagSet.length();

        /**
         * 针对产品变更的特殊处理，由于产品变更，服务变更，优惠变更合并成一个业务，但是短信需要区分发送。 td_b_trade_sms 按110：主产品变更短信，120：服务变更发送短信，150：优惠变更发送短信
         * processTagSet 第9位，业务变更标志(1:仅服务变更;2:仅优惠变更;3:仅服务和优惠变更;4:主产品产品变更;5:附加产品变更）;第5位，是否变更主产品
         */
        if ("110".equals(tradeTypeCode))
        {
            if (pLength >= 9 && "4".equals(processTagSet.substring(8, 9)))
            {
                tradeTypeCode = "110";
            }
            else if (pLength >= 9 && ("1".equals(processTagSet.substring(8, 9)) || "3".equals(processTagSet.substring(8, 9))))
            {
                tradeTypeCode = "120";
            }
            else if (pLength >= 9 && "2".equals(processTagSet.substring(8, 9)))
            {
                tradeTypeCode = "150";
            }
            else
            {
                tradeTypeCode = "150";
            }
        }

        String brandCode = mainTradeData.getBrandCode();
        String productId = mainTradeData.getProductId();
        String cancelTag = mainTradeData.getCancelTag();
        String eparchyCode = mainTradeData.getEparchyCode();
        String inModeCode = mainTradeData.getInModeCode();
        
        // REQ201908060010_关于屏蔽呼叫转移状态变更短信告知的需求-wuwangfeng
        IDataset tradeSmsInfos = null;
        if("120".equals(tradeTypeCode)){        	
        	List<SvcTradeData> svcList = btd.get("TF_B_TRADE_SVC");        	
    		IDataset platSvcList = UserPlatSvcInfoQry.queryPlatSvcByUserIdNew(svcList.get(0).getUserId());
        	
    		// 获取平台服务编码 和 服务id
        	IDataset commparaSet = CommparaInfoQry.getCommparaAllColByParser("CSM","5203", "120", "0898");
        	IData commpara = commparaSet.getData(0);
            String[] paramName = commpara.getString("PARAM_NAME").split("\\|");
            String[] paraCode1 = commpara.getString("PARA_CODE1").split("\\|");
            
            String isAndMessageUser = "0";
            for (int i = 0; i < platSvcList.size(); i++) {
        		for (int k = 0; k < paramName.length; k++) {
                	if (platSvcList.getData(i).getString("SERVICE_ID").equals(paramName[k])) {
                		isAndMessageUser = "1";
                		break;
                	}	
    			}
        		if("1".equals(isAndMessageUser)) break;
            }
                        
            String isCallChangeSVC = "0";
            int svcNum = 0;
            for (int y = 0; y < svcList.size(); y++) {                
                for (int m = 0; m < paraCode1.length; m++) {
                	if (svcList.get(y).getElementId().equals(paraCode1[m])) {
                		isCallChangeSVC = "1";
                		svcNum = svcNum + 1;
                	}	
    			}
			}
        	                   	
            if(!(svcList.size()==svcNum && "1".equals(isAndMessageUser) && "1".equals(isCallChangeSVC))){
        		tradeSmsInfos = getTradeSmsInfos(tradeTypeCode, brandCode, productId, cancelTag, eparchyCode, inModeCode);
        	}            
        }else{
        	tradeSmsInfos = getTradeSmsInfos(tradeTypeCode, brandCode, productId, cancelTag, eparchyCode, inModeCode);       	
        }

        // 未获取到短信配置，则直接返回
        if (IDataUtil.isEmpty(tradeSmsInfos))
        {
            return;
        }

        // 2、过滤模板
        // 根据TD_B_TRADE_SMS.MVEL_EXPR进行其它条件判断，过滤出最终需要发送的短信配置
        IDataset filterTradeSmsInfos = filterTradeSmsInfos(tradeSmsInfos, regData);

        // 未获取到过滤后的短信配置，则直接返回
        if (IDataUtil.isEmpty(filterTradeSmsInfos))
        {
            return;
        }

        // 3、解析模板
        IDataset regSmsInfos = new DatasetList();
        IDataset sucSmsInfos = new DatasetList();
        this.processTemplate(filterTradeSmsInfos, regSmsInfos, sucSmsInfos, regData);

        // 4、发送短信
        // 发送登记短信
        dealRegSms(regSmsInfos, btd);

        // 发送完工短信
        dealSucSms(sucSmsInfos, btd);
    }

    /**
     * 过滤短信配置
     * 
     * @param dataset
     * @param btd
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    protected IDataset filterTradeSmsInfos(IDataset dataset, RegTradeData rtd) throws Exception
    {
        String objTypeCode = "";
        String objCode = "";
        String modifyTag = "";

        IData tempRuleParam = new DataMap(); // S类型的规则，需要调用规则，转换数据，后面处理只转一次
        IDataset filterTradeSmsInfos = new DatasetList(); // 过滤后的短信
        IDataset filterTradeSmsMInfos = new DatasetList(); // obj_type_code='M' 类型的模板

        for (Object obj : dataset)
        {
            IData data = (IData) obj;

            objTypeCode = data.getString("OBJ_TYPE_CODE", "");
            objCode = data.getString("OBJ_CODE", "");
            modifyTag = data.getString("MODIFY_TAG", "");

            // 如果短信条件存在，则过滤出满足条件的短信
            if (!"".equals(objTypeCode) && !"".equals(objCode))
            {
                // 产品
                if ("0".equals(objTypeCode))
                {
                    if (MvelMiscCheck.chkByPrdOper(rtd, objCode, modifyTag))
                    {
                        filterTradeSmsInfos.add(data);
                    }
                }
                // 服务
                else if ("1".equals(objTypeCode))
                {
                    if (MvelMiscCheck.chkBySvcOper(rtd, objCode, modifyTag))
                    {
                        filterTradeSmsInfos.add(data);
                    }
                }
                // 优惠
                else if ("2".equals(objTypeCode))
                {
                    if (MvelMiscCheck.chkByDisOper(rtd, objCode, modifyTag))
                    {
                        filterTradeSmsInfos.add(data);
                    }
                }
                // 品牌匹配(modifyTag:0-Like,1-Not Like，%只能放在最后)
                else if ("3".equals(objTypeCode))
                {
                    if (MvelMiscCheck.chkByBrand(rtd, objCode, modifyTag))
                    {
                        filterTradeSmsInfos.add(data);
                    }
                }
                // 校验台帐processTagSet的第N位是否等于某个值
                else if ("4".equals(objTypeCode))
                {
                    if (MvelMiscCheck.chkByProcTagSet(rtd, data.getInt("OBJ_CODE"), modifyTag, "=="))
                    {
                        filterTradeSmsInfos.add(data);
                    }
                }
                // 校验台帐processTagSet的第N位是否不等于某个值
                else if ("5".equals(objTypeCode))
                {
                    if (MvelMiscCheck.chkByProcTagSet(rtd, data.getInt("OBJ_CODE"), modifyTag, "!="))
                    {
                        filterTradeSmsInfos.add(data);
                    }
                }
                // 台帐表产品
                else if ("6".equals(objTypeCode))
                {
                    if (MvelMiscCheck.chkByProduct(rtd, objCode, "=="))
                    {
                        filterTradeSmsInfos.add(data);
                    }
                }
                // 营销活动类型判断
                else if ("7".equals(objTypeCode))
                {
                    if (MvelMiscCheck.chkByCampnType(rtd, objCode))
                    {
                        filterTradeSmsInfos.add(data);
                    }
                }
                // 支持特殊业务限制判断表定义
                else if ("S".equals(objTypeCode))
                {
                    if (IDataUtil.isEmpty(tempRuleParam))
                    {
                        tempRuleParam = rtd.getAllTableDataset();

                        IData userInfo = UcaInfoQry.qryMainProdInfoByUserId(rtd.getUca().getUserId());
                        if (IDataUtil.isNotEmpty(userInfo))
                        {
                            tempRuleParam.putAll(userInfo);
                        }
                    }

                    IData tempObjCode = new DataMap(objCode);

                    if (BreSuperLimit.jSuperLimit(tempRuleParam, tempObjCode))
                    {
                        filterTradeSmsInfos.add(data);
                    }

                }
                // 家庭业务
                else if ("U".equals(objTypeCode))
                {
                    if (BofConst.TRADE_TYPE_CODE_FAMILYACCOUNTMANAGE.equals(rtd.getTradeTypeCode()) || BofConst.TRADE_TYPE_CODE_FAMILYACCOUNTDESTROY.equals(rtd.getTradeTypeCode()))
                    {
                        if (MvelMiscCheck.chkByTM(rtd, rtd.getTradeTypeCode(), modifyTag))
                        {
                            filterTradeSmsInfos.add(data);
                        }
                    }
                }
                // 模板表达式td_s_crm_mvelmisc
                else if ("M".equals(objTypeCode))
                {
                    filterTradeSmsMInfos.add(data);
                }
            }
            else
            {
                filterTradeSmsInfos.add(data);
            }
        }

        // 如果存在M类型的模板，则再次过滤
        if (IDataUtil.isNotEmpty(filterTradeSmsMInfos))
        {
            DynamicDecisionTable dynamic = new DynamicDecisionTable("OBJ_CODE");
            IDataset result = dynamic.decide(filterTradeSmsMInfos, rtd);

            filterTradeSmsInfos.addAll(result);
        }

        return filterTradeSmsInfos;
    }

    /**
     * 公用信息
     * 
     * @param btd
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    protected IData getCommonSmsInfo(BusiTradeData btd, IData smsInfo) throws Exception
    {
        IData smsData = new DataMap();

        smsData.put("TRADE_ID", btd.getTradeId());
        String specSn = smsInfo.getString("SPEC_SERIAL_NUMBER", "");
        smsData.put("RECV_OBJECT", specSn == "" ? btd.getRD().getUca().getSerialNumber() : specSn);
        String specUserId = smsInfo.getString("SPEC_USER_ID", "");
        smsData.put("RECV_ID", specUserId == "" ? btd.getRD().getUca().getUserId() : specUserId);
        smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        smsData.put("SMS_PRIORITY", "50");
        smsData.put("CANCEL_TAG", btd.getMainTradeData().getCancelTag());
        smsData.put("REMARK", "业务短信通知");
        smsData.put("NOTICE_CONTENT_TYPE", smsInfo.getString("SMS_TYPE"));
        smsData.put("REVC4", smsInfo.getString("_TEMPLATE_ID", "").equals("") ? smsInfo.getString("TEMPLATE_ID") : smsInfo.getString("_TEMPLATE_ID", ""));// 对应模板ID

        String tempLateId = smsInfo.getString("TEMPLATE_ID");
        if (StringUtils.isNotEmpty(tempLateId))
        {
            IData templateIds = TemplateBean.getTemplateInfoByPk(tempLateId);
            if (IDataUtil.isNotEmpty(templateIds))
            {
                smsData.put("SMS_TYPE_CODE", templateIds.getString("TEMPLATE_TYPE"));
                smsData.put("SMS_KIND_CODE", templateIds.getString("TEMPLATE_KIND"));
                smsData.put("SMS_PRIORITY", templateIds.getString("SMS_PRIORITY"));
            }
        }
        return smsData;
    }

    /**
     * 查询短信配置
     * 
     * @param tradeTypeCode
     * @param brandCode
     * @param productId
     * @param cancelTag
     * @param eparchyCode
     * @param inModeCode
     * @return
     * @throws Exception
     */
    protected IDataset getTradeSmsInfos(String tradeTypeCode, String brandCode, String productId, String cancelTag, String eparchyCode, String inModeCode) throws Exception
    {
        // 最后一个参数 TD_B_TRADE_SMS.EVENT_TYPE：RegSms登记短信，SecSms二次确认短信，SucSms完工短息
        // 传null，表示不区分
        IDataset dataset = TradeSmsInfoQry.getTradeSmsInfos(tradeTypeCode, brandCode, productId, cancelTag, eparchyCode, inModeCode, null);

        return dataset;
    }

    /**
     * 公用信息 宽带短信
     * 
     * @author chenzm
     * @param btd
     * @return
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    protected IData getWidenetCommonSmsInfo(BusiTradeData btd, IData smsInfo) throws Exception
    {

        String serialNumber = btd.getRD().getUca().getSerialNumber();
        String userId = "";
        if (serialNumber.substring(0, 3).equals("KD_"))
        {
            serialNumber = serialNumber.substring(3);
        }
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            userId = userInfo.getString("USER_ID");
        }
        else
        {
            userId = "-1";
        }
        IData smsData = new DataMap();

        smsData.put("TRADE_ID", btd.getTradeId());
        smsData.put("RECV_OBJECT", serialNumber);
        smsData.put("RECV_ID", userId);
        smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        smsData.put("SMS_PRIORITY", "50");
        smsData.put("CANCEL_TAG", btd.getMainTradeData().getCancelTag());
        smsData.put("REMARK", "业务短信通知");
        smsData.put("NOTICE_CONTENT_TYPE", smsInfo.getString("SMS_TYPE"));

        smsData.put("REVC4", smsInfo.getString("_TEMPLATE_ID", "").equals("") ? smsInfo.getString("TEMPLATE_ID") : smsInfo.getString("_TEMPLATE_ID", ""));// 对应模板ID

        String tempLateId = smsInfo.getString("TEMPLATE_ID");
        if (StringUtils.isNotEmpty(tempLateId))
        {
            IData templateIds = TemplateBean.getTemplateInfoByPk(tempLateId);
            if (IDataUtil.isNotEmpty(templateIds))
            {
                smsData.put("SMS_TYPE_CODE", templateIds.getString("TEMPLATE_TYPE"));
                smsData.put("SMS_KIND_CODE", templateIds.getString("TEMPLATE_KIND"));
                smsData.put("SMS_PRIORITY", templateIds.getString("SMS_PRIORITY"));
            }
        }
        return smsData;
    }

    /**
     * 对TF_B_TRADE_OTHER表数据进行特殊处理
     * 
     * @param rtd
     * @param otherList
     * @throws Exception
     *             wangjx 2013-9-7
     */
    @SuppressWarnings("rawtypes")
    protected void otherDealProcess(RegTradeData rtd, List<OtherTradeData> otherList) throws Exception
    {
        String masterRoleCode = "1";// 主卡编码
        String modifyTagMod = "2";// 副卡修改操作

        List<OtherTradeData> tempResult = new ArrayList<OtherTradeData>();// 存放临时生成的主卡记录
        for (Object obj : otherList)
        {
            OtherTradeData otherTD = (OtherTradeData) obj;
            String relaModifyTag = otherTD.getModifyTag();
            if (!"2".equals(relaModifyTag))
            {
                continue;
            }

            otherTD.setRsrvStr6(otherTD.getRsrvStr5());// 存放要发送短信的副卡号码，统一放到STR6作为短信发送对象，STR5为MVEL取值对象
            if (relaModifyTag.equals(modifyTagMod))// 存在副卡修改操作
            {
                OtherTradeData otherInfo = otherTD.clone();
                /* 由于有成员修改，此时没有拼主卡的other表数据，为了给主卡发短信，人为的根据副卡操作情况给主卡拼一条数据，仅在发短信时使用 */
                otherInfo.setRsrvStr6(rtd.getMainTradeData().getSerialNumber());// 存放要发送短信的主卡号码
                otherInfo.setRsrvTag2(masterRoleCode);// 存放主卡编码
                otherInfo.setUserId(rtd.getMainTradeData().getUserId());
                otherInfo.setModifyTag(modifyTagMod);
                otherInfo.setRsrvTag1("1");// 用于发短信后删除
                tempResult.add(otherInfo);
            }
        }

        // 将主卡发短信用的数据临时拼到OtherTradeData
        otherList.addAll(tempResult);
    }

    /**
     * 解析短信模板
     * 
     * @param filterSmsInfos
     * @param regSmsInfos
     * @param sucSmsInfos
     * @param btd
     * @throws Exception
     */
    @SuppressWarnings(
    { "unchecked", "rawtypes" })
    protected void processTemplate(IDataset filterSmsInfos, IDataset regSmsInfos, IDataset sucSmsInfos, RegTradeData rtd) throws Exception
    {
        // 特殊业务分析TF_B_TRADE_RELATION表
        if ("283".equals(rtd.getTradeTypeCode()))
        {
            // 新增、删除时发短信
            List<RelationTradeData> relaList = rtd.get("TF_B_TRADE_RELATION");
            int sizeRela = relaList.size();
            if (null != relaList || sizeRela > 0)
            {
                relationDealProcess(rtd, relaList);// 给主卡拼串好发短信

                for (RelationTradeData relaTD : relaList)
                {
                    IData relaInfo = relaTD.toData();

                    dealTemplate(filterSmsInfos, regSmsInfos, sucSmsInfos, rtd, relaInfo);
                }

                // 发完短信后删除自己拼的数据串
                List<RelationTradeData> newRelaList = new ArrayList<RelationTradeData>();// 存放原有的拼串数据
                for (RelationTradeData relationTradeData : relaList)
                {
                    if (!"1".equals(relationTradeData.getRsrvTag1()))
                    {
                        newRelaList.add(relationTradeData);
                    }
                }
                relaList.clear();
                relaList.addAll(newRelaList);
            }

            // 修改时发短信
            List<OtherTradeData> otherList = rtd.get("TF_B_TRADE_OTHER");
            int sizeOther = otherList.size();
            if (null != otherList || sizeOther > 0)
            {
                otherDealProcess(rtd, otherList);

                for (OtherTradeData otherTD : otherList)
                {
                    IData otherInfo = otherTD.toData();
                    if (!"2".equals(otherInfo.getString("MODIFY_TAG")))// 非修改不发短信，走TRADE_RELATION发
                    {
                        continue;
                    }

                    dealTemplate(filterSmsInfos, regSmsInfos, sucSmsInfos, rtd, otherInfo);
                }

                // 发完短信后删除自己拼的数据串
                List<OtherTradeData> newOtherList = new ArrayList<OtherTradeData>();// 存放原有拼串数据
                for (OtherTradeData otherTradeData : otherList)
                {
                    if (!"1".equals(otherTradeData.getRsrvTag1()))
                    {
                        newOtherList.add(otherTradeData);
                    }
                }
                otherList.clear();
                otherList.addAll(newOtherList);
            }
        }
        else
        // 其他普通业务
        {
            dealTemplate(filterSmsInfos, regSmsInfos, sucSmsInfos, rtd, null);
        }
    }

    /**
     * 对TF_B_TRADE_RELATION表数据进行特殊处理
     * 
     * @param rtd
     * @param relaList
     * @throws Exception
     *             wangjx 2013-9-7
     */
    @SuppressWarnings("rawtypes")
    protected void relationDealProcess(RegTradeData rtd, List<RelationTradeData> relaList) throws Exception
    {
        boolean masterRelaAdd = false;
        boolean masterRelaDel = false;
        boolean memberRelaAdd = false;
        boolean memberRelaDel = false;

        String masterRoleCode = "1";// 主卡编码
        String modifyTagAddMaster = "0";// 主卡新增操作
        String modifyTagDelMaster = "1";// 主卡删除操作
        String modifyTagAdd = "0";// 副卡新增操作
        String modifyTagDel = "1";// 副卡删除操作

        RelationTradeData relaInfo = new RelationTradeData();
        for (Object obj : relaList)
        {
            RelationTradeData relaTD = (RelationTradeData) obj;
            if (null == relaInfo || "".equals(relaInfo))
            {
                relaInfo = relaTD.clone();
            }

            String relaRoleCodeB = relaTD.getRoleCodeB();
            String relaModifyTag = relaTD.getModifyTag();

            if (relaRoleCodeB.equals(masterRoleCode))
            {
                if (modifyTagAddMaster.equals(relaModifyTag))// 存在主卡新增记录
                {
                    masterRelaAdd = true;
                }
                if (modifyTagDelMaster.equals(relaModifyTag))// 存在主卡删除记录
                {
                    masterRelaDel = true;
                }
            }
            if (relaModifyTag.equals(modifyTagAdd))// 存在副卡新增操作
            {
                memberRelaAdd = true;
            }
            if (relaModifyTag.equals(modifyTagDel))// 存在副卡删除操作
            {
                memberRelaDel = true;
            }
        }

        /* 由于有成员新增、删除，此时没有拼主卡的relation数据，为了给主卡发短信，人为的根据副卡操作情况给主卡拼一条数据，仅在发短信时使用 */
        // 成员有新增，主卡没有新增的情况（即非创建时的成员新增）
        if (!masterRelaAdd && memberRelaAdd)
        {
            relaInfo.setSerialNumberB(rtd.getMainTradeData().getSerialNumber());
            relaInfo.setUserIdB(rtd.getMainTradeData().getUserId());
            relaInfo.setRoleCodeB(masterRoleCode);
            relaInfo.setModifyTag(modifyTagAdd);
            relaInfo.setRsrvTag1("1");// 用于发短信后删除
            relaList.add(relaInfo);
        }

        // 成员有删除，主卡没有删除记录的情况（即非创建时的成员删除）
        if (!masterRelaDel && memberRelaDel)
        {
            RelationTradeData delRelaData = relaInfo.clone();
            delRelaData.setSerialNumberB(rtd.getMainTradeData().getSerialNumber());
            delRelaData.setUserIdB(rtd.getMainTradeData().getUserId());
            delRelaData.setRoleCodeB(masterRoleCode);
            delRelaData.setModifyTag(modifyTagDel);
            delRelaData.setRsrvTag1("1");// 用于发短信后删除
            relaList.add(delRelaData);
        }
    }

}
