
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.template;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.DynamicDecisionTable;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.BizData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.GrpRegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreSuperLimit;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSmsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class GroupSmsTemplateBean
{
    /**
     * 获取模板拼串信息
     * 
     * @param templateIds
     * @param regSmsInfos
     * @param sucSmsInfos
     * @param tradeAllData
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void dealTemplate(IDataset templateIds, IDataset regSmsInfos, IDataset sucSmsInfos, IDataset secSmsInfos, IData tradeAllData, BizData bizData, BaseReqData reqData, IData map) throws Exception
    {
        if (IDataUtil.isEmpty(tradeAllData))
            tradeAllData = new DataMap();

        if (bizData == null)
            bizData = new BizData();

        if (IDataUtil.isEmpty(map))
            map = new DataMap();

        if (reqData == null && (!map.getBoolean("IS_GROUP_BIZ", false)))
            reqData = new MemberReqData();

        MVELExecutor exector = new MVELExecutor();
        exector.setMiscCache(CRMMVELMiscCache.getMacroCache());

        for (int i = 0, size = templateIds.size(); i < size; i++)
        {
            IData idTemplate = templateIds.getData(i);

            String roleCode = idTemplate.getString("ROLE_CODE");// 3表示集团客户经理

            boolean isCustManger = false;

            if ("3".equals(roleCode))
            {

                isCustManger = true;
            }

            idTemplate.put("IS_CUST_MANAGER", isCustManger);

            // idTemplate.putAll(templateIds.getData(i));

            GrpRegTradeData grpRegTradeData = new GrpRegTradeData(bizData.getTrade());

            grpRegTradeData.setGrpRegTradeData(tradeAllData);

            boolean is_group_biz = map.getBoolean("IS_GROUP_BIZ", false);
            grpRegTradeData.setIs_group_biz(is_group_biz);

            // 处理UCA和GRPUCA
            grpRegTradeData.setUca(reqData.getUca());// 如果是集团操作此处UCA即是GRPUCA 否则为成员UCA

            if (!is_group_biz)// 是否集团业务 默认操作集团成员业务
            {
                MemberReqData memberReqData = (MemberReqData) reqData;

                UcaData grpUcaData = memberReqData.getGrpUca();

                grpRegTradeData.setUca(grpUcaData);
            }

            exector.prepare(grpRegTradeData);// 模板变量解析

            String content = TemplateBean.getTemplate(idTemplate.getString("TEMPLATE_ID"));
            String replaceContent = exector.applyTemplate(content);
            idTemplate.put("TEMPLATE_REPLACED", replaceContent);

            String eventType = idTemplate.getString("EVENT_TYPE");

            // 登记短信
            if (BofConst.SMS_REG.equals(eventType))
            {
                regSmsInfos.add(idTemplate);
            }
            // 完工短信
            else if (BofConst.SMS_SUC.equals(eventType))
            {
            	//短信内容不为空才发短信
            	if(replaceContent!=null && replaceContent.trim().length()>0){
            		sucSmsInfos.add(idTemplate);
            	}
            }
            // 二次确认短信
            else if (BofConst.SMS_SEC.equals(eventType))
            {
                secSmsInfos.add(idTemplate);
            }

        }
    }

    /**
     * 过滤模板
     * 
     * @return
     * @throws Exception
     */
    public static IDataset filterSmsTemplates(IDataset smsTemplates, IData tradeAllData, BizData bizData, BaseReqData reqData, IData map) throws Exception
    {

        if (IDataUtil.isEmpty(smsTemplates))
        {
            return new DatasetList();
        }

        if (IDataUtil.isEmpty(tradeAllData))
            tradeAllData = new DataMap();

        if (bizData == null)
            bizData = new BizData();

        if (IDataUtil.isEmpty(map))
            map = new DataMap();

        if (reqData == null && (!map.getBoolean("IS_GROUP_BIZ", false)))
            reqData = new MemberReqData();

        String objTypeCode = "";
        String objCode = "";
        String modifyTag = "";
        String remark = "";

        IData tempRuleParam = new DataMap(); // S类型的规则，需要调用规则，转换数据，后面处理只转一次
        IDataset filterSmsTemplates = new DatasetList(); // 过滤后的短信
        IDataset filterSmsMTemplates = new DatasetList(); // obj_type_code='M' 类型的模板

        for (int i = 0, size = smsTemplates.size(); i < size; i++)
        {
            IData smsTemplate = smsTemplates.getData(i);

            String eventType = smsTemplate.getString("EVENT_TYPE");
            // 特殊处理确认不发完工短信,批量业务不发短信
            if (!reqData.isNeedSms() && BofConst.SMS_SUC.equals(eventType))
            {
                continue;
            }

            objTypeCode = smsTemplate.getString("OBJ_TYPE_CODE", "");
            objCode = smsTemplate.getString("OBJ_CODE", "");
            modifyTag = smsTemplate.getString("MODIFY_TAG", "");
            remark = smsTemplate.getString("REMARK", "");

            // 如果短信条件存在，则过滤出满足条件的短信
            if (StringUtils.isNotBlank(objTypeCode) && StringUtils.isNotBlank(objCode))
            {
                // 产品
                if ("0".equals(objTypeCode))
                {
                    if (MvelSmsCheck.chkByPrdOper(tradeAllData, objCode, modifyTag))
                    {
                        filterSmsTemplates.add(smsTemplate);
                    }
                }
                // 服务
                else if ("1".equals(objTypeCode))
                {
                    if (MvelSmsCheck.chkBySvcOper(tradeAllData, objCode, modifyTag))
                    {
                        filterSmsTemplates.add(smsTemplate);
                    }
                }
                // 优惠
                else if ("2".equals(objTypeCode))
                {
                    if (MvelSmsCheck.chkByDisOper(tradeAllData, objCode, modifyTag))
                    {
                        filterSmsTemplates.add(smsTemplate);
                    }
                }
                // 品牌匹配
                else if ("3".equals(objTypeCode))
                {
                    if (MvelSmsCheck.chkByBrand(tradeAllData, objCode, modifyTag))
                    {
                        filterSmsTemplates.add(smsTemplate);
                    }
                }
                // 属性值
                else if ("X".equals(objTypeCode))
                {
                    if (MvelSmsCheck.chkByAttr(tradeAllData, objCode, remark))
                    {
                        filterSmsTemplates.add(smsTemplate);
                    }
                }
                // 主台账表objCode字段有值才发短信
                else if ("P".equals(objTypeCode))
                {
                    if (MvelSmsCheck.chkByBatchOrder(tradeAllData, objCode))
                    {
                        filterSmsTemplates.add(smsTemplate);
                    }
                }
                // 支持特殊业务限制判断表定义
                else if ("S".equals(objTypeCode))
                {
                    if (IDataUtil.isEmpty(tempRuleParam))
                    {
                        IData tableDataClone = (IData) Clone.deepClone(tradeAllData);

                        tempRuleParam.putAll(tableDataClone);

                        IData userInfo = UcaInfoQry.qryMainProdInfoByUserId(reqData.getUca().getUserId());
                        if (IDataUtil.isNotEmpty(userInfo))
                        {
                            tempRuleParam.putAll(userInfo);
                        }
                    }

                    IData tempObjCode = new DataMap(objCode);

                    if (BreSuperLimit.jSuperLimit(tempRuleParam, tempObjCode))
                    {
                        filterSmsTemplates.add(smsTemplate);
                    }

                }
                // 模板表达式td_s_crm_mvelmisc
                else if ("M".equals(objTypeCode))
                {
                    filterSmsMTemplates.add(smsTemplate);
                }
                // 根据模版ID过滤
                else if ("T".equals(objTypeCode))
                {
                    String templateId = smsTemplate.getString("TEMPLATE_ID", "");

                    if (MvelSmsCheck.chkByTemplateId(tradeAllData, templateId, objCode))
                    {
                        filterSmsTemplates.add(smsTemplate);
                    }

                }

            }
            else
            {
                filterSmsTemplates.add(smsTemplate);
            }

        }

        // 如果存在M类型的模板，则再次过滤
        if (IDataUtil.isNotEmpty(filterSmsMTemplates))
        {
            DynamicDecisionTable dynamic = new DynamicDecisionTable("OBJ_CODE");

            GrpRegTradeData grpRegTradeData = new GrpRegTradeData(bizData.getTrade());

            grpRegTradeData.setGrpRegTradeData(tradeAllData);

            // 处理UCA和GRPUCA
            grpRegTradeData.setUca(reqData.getUca());// 如果是集团操作此处UCA即是GRPUCA 否则为成员UCA

            if (!map.getBoolean("IS_GROUP_BIZ", false))// 是否集团业务 默认操作集团成员业务
            {
                MemberReqData memberReqData = (MemberReqData) reqData;

                UcaData grpUcaData = memberReqData.getGrpUca();

                grpRegTradeData.setUca(grpUcaData);
            }

            IDataset result = dynamic.decide(filterSmsMTemplates, grpRegTradeData);

            filterSmsTemplates.addAll(result);
        }

        return filterSmsTemplates;
    }

    public static IDataset getTemplate(IData idata) throws Exception
    {
        String tradeTypeCode = idata.getString("TRADE_TYPE_CODE");
        String brandCode = idata.getString("BRAND_CODE");
        String productId = idata.getString("PRODUCT_ID");
        String cancelTag = idata.getString("CANCEL_TAG");
        String epachyCode = idata.getString("EPARCHY_CODE");
        String inModeCode = idata.getString("IN_MODE_CODE");

        // 查询模板
        IDataset ids = TradeSmsInfoQry.getTradeSmsInfos(tradeTypeCode, brandCode, productId, cancelTag, epachyCode, inModeCode, null);

        return ids;
    }

    public static void getTemplateValues(IData inData, IData vars) throws Exception
    {

        String custId = inData.getString("CUST_ID");// 必传

        IData idataset = UcaInfoQry.qryGrpInfoByCustId(custId);

        IData custInfo = idataset;// 查询客户信息

        if (vars.containsKey("GROUP_NAME") || vars.containsKey("CUST_NAME"))
        {
            String groupName = custInfo.getString("CUST_NAME", "");

            inData.put("GROUP_NAME", inData.getString("GROUP_NAME", groupName));
            inData.put("CUST_NAME", inData.getString("CUST_NAME", groupName));
        }

        // 产品名称
        String productId = "";
        if (vars.containsKey("PRODUCT_ID") || vars.containsKey("PRODUCT_NAME"))
        {
            productId = inData.getString("PRODUCT_ID", "");
            String product_name = UProductInfoQry.getProductNameByProductId(productId);
            inData.put("PRODUCT_NAME", inData.getString("PRODUCT_NAME", product_name));
        }
        // 品牌名称
        if (vars.containsKey("BRAND_NAME"))
        {
            String brandName = UBrandInfoQry.getBrandNameByBrandCode(productId);
            inData.put("BRAND_NAME", inData.getString("BRAND_NAME", brandName));
        }

        // 客户经理名称
        if (vars.containsKey("CUST_MANAGER_NAME"))
        {
            String custManagerId = custInfo.getString("CUST_MANAGER_ID");
            String custManagerName = UStaffInfoQry.getCustManageNameByCustManagerId(custManagerId);
            inData.put("CUST_MANAGER_NAME", inData.getString("CUST_MANAGER_NAME", custManagerName));
        }

        // 短号码
        if (vars.containsKey("SHORT_CODE"))
        {
            String shortCode = inData.getString("SHORT_CODE", "");
            inData.put("SHORT_CODE", inData.getString("SHORT_CODE", shortCode));
        }

        if (vars.containsKey("EXEC_TIME"))
        {
            inData.put("EXEC_TIME", inData.getString("EXEC_TIME", SysDateMgr.getSysDate()));
        }
    }

}
