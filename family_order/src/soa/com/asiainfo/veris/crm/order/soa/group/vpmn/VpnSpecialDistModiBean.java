
package com.asiainfo.veris.crm.order.soa.group.vpmn;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class VpnSpecialDistModiBean extends ChangeMemElement
{
    private VpnSpecialDistModiReqData reqData = null;

    private String smsTemplateId = "VpnDisChg"; // 短信模版ID

    public VpnSpecialDistModiBean()
    {

    }

    /**
     * 生成登记信息
     * 
     * @author xiajj
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        infoRegDataDiscnt();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author lixiuyu@20101115
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new VpnSpecialDistModiReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (VpnSpecialDistModiReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setNEXT_ACCT_DISCODE(map.getString("NEXT_ACCT_DISCODE", ""));
        reqData.setTHIS_ACCT_DISCODE(map.getString("THIS_ACCT_DISCODE", ""));

        if (StringUtils.isNotBlank(reqData.getNEXT_ACCT_DISCODE()) && StringUtils.isBlank(reqData.getTHIS_ACCT_DISCODE()))
        {
            reqData.setDISCNT_NAME(UDiscntInfoQry.getDiscntNameByDiscntCode(reqData.getNEXT_ACCT_DISCODE()));
            reqData.setNOWACCT_FLAG("0");
        }
        if (StringUtils.isNotBlank(reqData.getTHIS_ACCT_DISCODE()) && StringUtils.isBlank(reqData.getNEXT_ACCT_DISCODE()))
        {
            reqData.setDISCNT_NAME(UDiscntInfoQry.getDiscntNameByDiscntCode(reqData.getTHIS_ACCT_DISCODE()));
            reqData.setNOWACCT_FLAG("1");
        }

    }

    /**
     * VPMN一些个性化参数存放到主台帐表的预留字段里
     */
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        data.put("RSRV_STR6", smsTemplateId);// 短信模版ID

        // 短信
        String mebUserId = reqData.getUca().getUserId();
        StringBuilder sb = new StringBuilder();
        sb.append("套餐为");
        sb.append(reqData.getDISCNT_NAME());
        sb.append(",");
        if ("0".equals(reqData.getNOWACCT_FLAG()))
        {
            sb.append("下账期");
            sb.append(DiversifyAcctUtil.getFirstDayNextAcct(mebUserId));
            sb.append("生效。");
        }
        if ("1".equals(reqData.getNOWACCT_FLAG()))
        {
            sb.append("24小时内生效,");
            sb.append(DiversifyAcctUtil.getUserAcctDescMessage(mebUserId, "0"));
            sb.append("收取两次集团套餐功能费。");
        }

        data.put("RSRV_STR7", sb.toString());
    }

    /**
     * 358优惠变更逻辑处理
     */
    public void infoRegDataDiscnt() throws Exception
    {
        IDataset operDiscntInfos = reqData.cd.getDiscnt();
        String memUserId = reqData.getUca().getUserId();
        String grpUserId = reqData.getGrpUca().getUserId();
        String memEparchyCode = reqData.getUca().getUserEparchyCode();

        // 获取昨天的日期 j2ee 原来是这样滴：YesterdayDate + " 23/:59/:59"
        String YesterdayDate = SysDateMgr.getYesterdayDate() + SysDateMgr.getEndTime235959();
        // 获取下账期第一天 nextMonthFirstTime
        String firstDayNextAcct = DiversifyAcctUtil.getFirstDayNextAcct(memUserId);
        // 获取本账期最后时间 noeMonthLastTime
        String lastTimeThisAcct = DiversifyAcctUtil.getLastTimeThisAcctday(memUserId, null);
        // 获取下账期第一时间
        String firstTimeNextAcct = DiversifyAcctUtil.getFirstTimeNextAcct(memUserId);
        // 是否有下账期358优惠变更
        boolean hasChgNextAcctDis = false;
        // 是否有本账期358优惠变更
        boolean hasChgNowAcctDis = false;
        // 查成员本集团产品订购的优惠
        IDataset discntinfos = UserDiscntInfoQry.getUserProductDis(memUserId, grpUserId);
        // 判断是否需要新增下账期生效优惠， true表示需要，false表示选择的优惠原end_date就是本账期最后一天 addDiscntCode
        boolean addNextAcctDisCode = true;
        // 判断本月优惠, j2ee true在本方法中没有变化 addDiscntCodet
        boolean addThisAcctDisCode = true;
        // 选择的下账期优惠
        String nextAcctDisCode = reqData.getNEXT_ACCT_DISCODE();
        // 选择的本账期优惠
        String thisAcctDisCode = reqData.getTHIS_ACCT_DISCODE();
        String productId = "";
        String packageId = "";
        // 1.如果成员本集团产品有订购优惠 start
        if (IDataUtil.isNotEmpty(discntinfos))
        {
            // 循环成员订购优惠 start
            for (int i = 0; i < discntinfos.size(); i++)
            {
                IData discnt = discntinfos.getData(i);
                String start_date = discnt.getString("START_DATE").substring(0, 10);
                String discnt_code = discnt.getString("DISCNT_CODE");
                String end_data = discnt.getString("END_DATE");

                // 1.1 如果选了“下账期“生效的优惠 start
                if (StringUtils.isNotBlank(nextAcctDisCode) && "".equals(thisAcctDisCode))
                {
                    // 如果原优惠是358优惠
                    if ("1285".equals(discnt_code) || "1286".equals(discnt_code) || "1391".equals(discnt_code))
                    {
                        // 如果选择的下账期优惠已经存在，且"开始"时间也是下账期第一天
                        if (discnt_code.equals(nextAcctDisCode) && start_date.equals(firstDayNextAcct))
                        {
                            String discnt_name = get358Name(discnt_code);
                            CSAppException.apperr(VpmnUserException.VPMN_USER_51, firstDayNextAcct, discnt_name);
                        }
                        // 如果选择的优惠已经存在，且"结束"时间是本账期最后一天,则变更该优惠结束时间到2050年，且不需要再新增下账期358优惠了
                        if (discnt_code.equals(nextAcctDisCode) && end_data.equals(lastTimeThisAcct))
                        {
                            discnt.put("ELEMENT_TYPE_CODE", "D");
                            discnt.put("DISCNT_CODE", discnt_code);
                            discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                            discnt.put("END_DATE", SysDateMgr.getTheLastTime());
                            discnt.put("DIVERSIFY_ACCT_TAG", "1");
                            operDiscntInfos.add(discnt);
                            // 不需要新增下账期358优惠
                            addNextAcctDisCode = false;
                            hasChgNextAcctDis = true;
                        }
                        // 如果原优惠开始时间是下账期第一时间，则立即结束该优惠，且需要新增下账期358优惠
                        if (start_date.equals(firstDayNextAcct))
                        {
                            discnt.put("ELEMENT_TYPE_CODE", "D");
                            discnt.put("DISCNT_CODE", discnt_code);
                            discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            discnt.put("END_DATE", YesterdayDate);
                            discnt.put("DIVERSIFY_ACCT_TAG", "1");
                            operDiscntInfos.add(discnt);
                            hasChgNextAcctDis = true;
                            // 需要新增下账期358优惠
                            productId = discnt.getString("PRODUCT_ID");
                            packageId = discnt.getString("PACKAGE_ID");
                        }
                    }

                    // 若选择下月生效,还是按原来的逻辑走 add by zhaoyi
                }
                // 1.1 如果选了“下账期“生效的优惠 end
                // 1.2 如果选择了“本账期”生效的优惠 start
                else if (StringUtils.isNotBlank(thisAcctDisCode) && StringUtils.isBlank(nextAcctDisCode))
                {
                    // 如果原优惠是358优惠
                    if ("1285".equals(discnt_code) || "1286".equals(discnt_code) || "1391".equals(discnt_code))
                    {
                        // 如果选择的本账期优惠已经存在，且（结束时间是本账期最后一天 ，或者（开始时间不是下账期第一天， 且结束时间为2050/-12/-31 23/:59/:59），则报错
                        if (discnt_code.equals(thisAcctDisCode) && (end_data.equals(lastTimeThisAcct) || (!start_date.equals(firstDayNextAcct) && end_data.equals(SysDateMgr.getTheLastTime()))))
                        {
                            String discnt_name = get358Name(discnt_code);
                            CSAppException.apperr(VpmnUserException.VPMN_USER_46, DiversifyAcctUtil.getUserAcctDescMessage(memUserId, "0"), discnt_name);
                        }
                        // 如果开始时间是下账期第一天，且选择的本账期优惠为358优惠，则删除该优惠
                        if (start_date.equals(firstDayNextAcct) && ("1285".equals(thisAcctDisCode) || "1286".equals(thisAcctDisCode) || "1391".equals(thisAcctDisCode)))
                        {
                            discnt.put("ELEMENT_TYPE_CODE", "D");
                            discnt.put("DISCNT_CODE", discnt_code);
                            discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            discnt.put("END_DATE", YesterdayDate);
                            discnt.put("DIVERSIFY_ACCT_TAG", "1");
                            operDiscntInfos.add(discnt);
                            hasChgNowAcctDis = true;
                            // 需要新增本账期358优惠
                            productId = discnt.getString("PRODUCT_ID");
                            packageId = discnt.getString("PACKAGE_ID");

                        }
                        // （如果结束时间是本账期最后一天,或者（开始时间不是下账期第一天， 且结束时间为2050/-12/-31 23/:59/:59）），且选择的本账期优惠为358优惠
                        if ((end_data.equals(lastTimeThisAcct) || (!start_date.equals(firstDayNextAcct) && (end_data.equals(SysDateMgr.getTheLastTime()))))
                                && ("1285".equals(thisAcctDisCode) || "1286".equals(thisAcctDisCode) || "1391".equals(thisAcctDisCode)))
                        {
                            discnt.put("ELEMENT_TYPE_CODE", "D");
                            discnt.put("DISCNT_CODE", discnt_code);
                            discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                            discnt.put("END_DATE", SysDateMgr.getSysTime());
                            discnt.put("DIVERSIFY_ACCT_TAG", "1");
                            operDiscntInfos.add(discnt);
                            hasChgNowAcctDis = true;
                            // 需要新增本账期358优惠
                            productId = discnt.getString("PRODUCT_ID");
                            packageId = discnt.getString("PACKAGE_ID");
                        }
                    }

                }
                // 1.2 如果选择了“本账期”生效的优惠 end
            }
            // 循环成员订购优惠 end
        }
        // 1.如果成员本集团产品有订购优惠 end

        // 2. 如果前台选择了下账期优惠,则必须原来有下账期生效的358套餐才能继续 start
        if (StringUtils.isNotBlank(nextAcctDisCode) && StringUtils.isBlank(thisAcctDisCode))
        {
            if (!hasChgNextAcctDis) // 必须原来有下账期生效的358套餐才能继续
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_49, firstDayNextAcct);
            }
            // 需要新增下账期生效优惠
            if (addNextAcctDisCode)
            {
                IData param = new DataMap();
                param.put("PRODUCT_ID", productId);
                param.put("PACKAGE_ID", packageId);
                param.put("DISCNT_CODE", nextAcctDisCode);
                param.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                param.put("START_DATE", firstDayNextAcct);
                param.put("END_DATE", SysDateMgr.getTheLastTime());
                param.put("ELEMENT_TYPE_CODE", "D");
                param.put("INST_ID", SeqMgr.getInstId(memEparchyCode));
                param.put("DIVERSIFY_ACCT_TAG", "1");
                operDiscntInfos.add(param);
            }
        }// 2. 如果前台选择了下账期优惠,则必须原来有下账期生效的358套餐才能继续 end
        // 3. 如果前台选择了本账期优惠 start
        else if (StringUtils.isNotBlank(thisAcctDisCode) && StringUtils.isBlank(nextAcctDisCode))
        {
            if (!hasChgNowAcctDis) // 必须原来有本账期生效的358套餐才能继续
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_50, DiversifyAcctUtil.getUserAcctDescMessage(memUserId, "0"));
            }
            // 当月只允许变更一次，去掉了 productId, packageId,
            IDataset datas = UserDiscntInfoQry.getVpnSpecialChangeDiver(memUserId, grpUserId, firstTimeNextAcct, lastTimeThisAcct);
            if (IDataUtil.isNotEmpty(datas))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_47, DiversifyAcctUtil.getUserAcctDescMessage(memUserId, "0"));
            }
            // 需要新增本账期生效的358优惠
            if (addThisAcctDisCode)
            {
                IData param = new DataMap();
                param.put("PRODUCT_ID", productId);
                param.put("PACKAGE_ID", packageId);
                param.put("DISCNT_CODE", thisAcctDisCode);
                param.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                param.put("START_DATE", SysDateMgr.getSysTime());
                param.put("END_DATE", SysDateMgr.getTheLastTime());
                param.put("ELEMENT_TYPE_CODE", "D");
                param.put("INST_ID", SeqMgr.getInstId(memEparchyCode));
                param.put("DIVERSIFY_ACCT_TAG", "1");
                operDiscntInfos.add(param);

            }
        }
        // 3. 如果前台选择了本账期优惠 end

    }

    private String get358Name(String discnt_code)
    {
        String discnt_name = "";
        if ("1285".equals(discnt_code))
        {
            discnt_name = "VPMN套餐JDD";
        }
        else if ("1286".equals(discnt_code))
        {
            discnt_name = "VPMN套餐JDE";
        }
        else if ("1391".equals(discnt_code))
        {
            discnt_name = "VPMN套餐JDF";
        }
        return discnt_name;
    }
}
