/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

/**
 * @author chenyi 2014-7-29 如果该资费为集团付费海南需要插 tf_b_trade_user_payplan表，tf_b_trade_payrelation，tf_b_trade_user_specialepay
 *         湖南需要入表数据tf_b_trade_user_payplan表，tf_b_trade_payrelation，tf_b_trade_user_payitem 付费项目 PAYITEM td_b_discnt
 *         str2配置为G 如果新订购的资费为集团付费则需要入这些表 一条资费
 */
public class PayRelationDealBean
{
    /**
     * @param userid
     * @param useridA
     * @return
     * @throws Exception
     */
    public static IData infoPayPlan(String userid, String useridA) throws Exception
    {
        IData addPayPlanData = new DataMap();
        addPayPlanData.put("USER_ID", userid);
        addPayPlanData.put("USER_ID_A", useridA);
        addPayPlanData.put("PLAN_ID", SeqMgr.getPlanId());
        addPayPlanData.put("PLAN_TYPE_CODE", "G");
        addPayPlanData.put("PLAN_NAME", "集团付费");
        addPayPlanData.put("PLAN_DESC", "集团付费");
        addPayPlanData.put("START_DATE", SysDateMgr.getSysTime());
        addPayPlanData.put("END_DATE", SysDateMgr.getTheLastTime());
        addPayPlanData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        return addPayPlanData;
    }

    /**
     * 付费关系
     * 
     * @param userid
     *            成员用户
     * @param acct_id
     *            集团账户
     * @param payItem
     *            付费项目
     * @param acct_id
     * @param end_cycle_id
     * @return
     * @throws Exception
     */
    public static IData infoPayrelation(String userid, String payItem, String acct_id) throws Exception
    {
        IData payRela = new DataMap();
        payRela.put("USER_ID", userid);
        payRela.put("ACCT_ID", acct_id);// 集团账户
        payRela.put("PAYITEM_CODE", payItem);
        payRela.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
        payRela.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行
        payRela.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
        payRela.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
        payRela.put("DEFAULT_TAG", "0"); // 默认标志
        payRela.put("LIMIT_TYPE", 0); // 限定方式：0-不限定，1-金额，2-比例
        payRela.put("LIMIT", 0); // 限定值
        payRela.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足
        payRela.put("INST_ID", SeqMgr.getInstId());
        payRela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 状态属性：0-增加，1-删除，2-变更
        payRela.put("START_CYCLE_ID", SysDateMgr.getNowCycle());
        payRela.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());
        return payRela;

    }

    /**
     * @param userid成员用户
     * @param payItem
     *            付费项目
     * @param acct_id
     *            集团账户
     * @param useridA
     *            集团用户
     * @param acct_id_b
     *            成员账户
     * @return
     * @throws Exception
     */
    public static IData infoSpecialpay(String userid, String payItem, String acct_id, String useridA, String acct_id_b) throws Exception
    {
        IData addSpecialPayData = new DataMap();
        addSpecialPayData.put("USER_ID", userid);
        addSpecialPayData.put("ACCT_ID", acct_id);// 集团账户
        addSpecialPayData.put("PAYITEM_CODE", payItem);
        addSpecialPayData.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
        addSpecialPayData.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行
        addSpecialPayData.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
        addSpecialPayData.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
        addSpecialPayData.put("DEFAULT_TAG", "0"); // 默认标志
        addSpecialPayData.put("LIMIT_TYPE", 0); // 限定方式：0-不限定，1-金额，2-比例
        addSpecialPayData.put("LIMIT", 0); // 限定值
        addSpecialPayData.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足
        addSpecialPayData.put("INST_ID", SeqMgr.getInstId());
        addSpecialPayData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 状态属性：0-增加，1-删除，2-变更
        addSpecialPayData.put("START_CYCLE_ID", SysDateMgr.getNowCycle());
        addSpecialPayData.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());
        addSpecialPayData.put("USER_ID_A", useridA);
        addSpecialPayData.put("ACCT_ID_B", acct_id_b);
        return addSpecialPayData;
    }

    /**
     * @Function:
     * @Description:根据生效规则和有效期 设定 TF_B_TRADE_RELATION， TF_B_TRADE_MEB_CENPAY，TF_B_TRADE_DISCNT，TF_B_TRADE_PAYRELATION
     *                         生效时间
     * @author:chenyi
     * @throws Exception
     * @date: 下午3:32:50 2014-9-30
     */
    public static void dealPayBizRelaEffectCYCLE(IData iData, IDataset paramDataList) throws Exception
    {

        if (IDataUtil.isEmpty(paramDataList))
        {
            return;
        }

        String nowcycle = "";
        // 反向受理 attr_code与规范相同 正向受理的attr_code是product_id与规范attr_code组合
        // 1根据生效规则 生成START_CYCLE_ID
        for (int i = 0, sizeI = paramDataList.size(); i < sizeI; i++)
        {
            IData paramData = paramDataList.getData(i);
            if (paramData.getString("ATTR_CODE").endsWith("1103"))
            {
                String effRule = paramData.getString("ATTR_VALUE");// 成员账期生效规则 1-立即生效 2-下账期生效
                if ("1".equals(effRule))
                {
                    nowcycle = SysDateMgr.getNowCycle();
                    iData.put("START_CYCLE_ID", nowcycle);
                }
                else if ("2".equals(effRule))
                {
                	nowcycle = SysDateMgr.decodeTimestamp(SysDateMgr.getAddMonthsNowday(1, SysDateMgr.getSysTime()), "yyyyMMdd");//
                	//add by xusf 取下一个账期
                    //nowcycle = SysDateMgr.getNextCycle();
                    iData.put("START_CYCLE_ID", nowcycle);
                }

            }
        }

        // 2根据生效规则和START_CYCLE_ID 生成END_CYCLE_ID
        for (int i = 0, sizeI = paramDataList.size(); i < sizeI; i++)
        {
            IData paramData = paramDataList.getData(i);

            if (paramData.getString("ATTR_CODE").endsWith("1104"))
            {
                String effMonth = paramData.getString("ATTR_VALUE");// 成员有效账期，月为单位，00代表无限期，变更仅能从00变成有限期，已有限期不能变更
                if ("00".equals(effMonth))
                {
                    iData.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());
                }
                else
                {
                    int month = Integer.parseInt(effMonth);
                    nowcycle = SysDateMgr.getDateForSTANDYYYYMMDD(nowcycle);
                    iData.put("END_CYCLE_ID", SysDateMgr.getDateForYYYYMMDD(SysDateMgr.getAddMonthsLastDay(month, nowcycle)));
                }

            }
        }
    }

    /**
     * @Function:
     * @Description:流量统付业务-如果生效规则或赠送期发生变化，则新增的付费关系的生效时间需要根据这两个值生成， 如果这两值没发生变化 则新增付费关系需要从user-attr表取出这两值 生成生效时间
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @throws Exception
     * @date: 下午3:32:50 2013-10-23
     */
    public static void getChgEffectTime(IData tempData, IData chgData, String userId) throws Exception
    {
        // 1-如果生效规则或赠送期发生变化不为空
        if (IDataUtil.isNotEmpty(tempData))
        {
            BbossPayBizInfoDealbean.chgEffect(chgData, tempData);// 直接变更start_date或end-date
        }
        else
        {
            // 2-1获取user-attr赠送有效期 和生效规则
            IData effecRuleData = UserAttrInfoQry.getUserAttrByUserId(userId, "1103").getData(0);
            String effecRule = effecRuleData.getString("ATTR_VALUE");

            IData effecMonthData = UserAttrInfoQry.getUserAttrByUserId(userId, "1104").getData(0);
            String effecMonth = effecMonthData.getString("ATTR_VALUE");
            // 2-2生成start-date
            String startDate = "";
            if ("1".equals(effecRule))
            {
                startDate = SysDateMgr.getSysDate();
                chgData.put("START_DATE", startDate);
            }
            else if ("2".equals(effecRule))
            {
                startDate = SysDateMgr.getNextCycle();
                chgData.put("START_DATE", startDate);
            }

            // 2-3生成end-date
            if ("00".equals(effecMonth))
            {
                chgData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            }
            else
            {
                int month = Integer.parseInt(effecMonth);
                chgData.put("END_DATE", SysDateMgr.getAddMonthsLastDay(month, startDate));
            }
        }

    }

    /**
     * @Function:
     * @Description:流量统付业务-如果生效规则或赠送期发生变化，则新增的付费关系的生效账期需要根据这两个值生成， 如果这两值没发生变化 则新增付费关系需要从user-attr表取出这两值 生成生效账期
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @date: 下午3:32:50 2013-10-23
     */
    public static void getChgEffectCYCLE(IData tempData, IData chgData, String userId) throws Exception
    {
        // 1-如果生效规则或赠送期发生变化不为空
        if (IDataUtil.isNotEmpty(tempData))
        {
            // 获取变更处理后的 start_date 和endDate
            String start_date = tempData.getString("START_DATE");
            String end_date = tempData.getString("END_DATE");
            // 将对应时间改成账期
            chgData.put("START_CYCLE_ID", SysDateMgr.getDateForYYYYMMDD(start_date));
            chgData.put("END_CYCLE_ID",SysDateMgr.getDateForYYYYMMDD(end_date));
        }
        else
        {
            // 2-1获取user-attr赠送有效期 和生效规则
            IData effecRuleData = UserAttrInfoQry.getUserAttrByUserId(userId, "1103").getData(0);
            String effecRule = effecRuleData.getString("ATTR_VALUE");

            IData effecMonthData = UserAttrInfoQry.getUserAttrByUserId(userId, "1104").getData(0);
            String effecMonth = effecMonthData.getString("ATTR_VALUE");
            // 2-2生成start-date
            String startDate = "";
            if ("1".equals(effecRule))
            {
                startDate = SysDateMgr.getNowCycle();
                chgData.put("START_CYCLE_ID", startDate);
            }
            else if ("2".equals(effecRule))
            {
                startDate = SysDateMgr.getFirstDayOfNextMonth();
                startDate = SysDateMgr.getDateForYYYYMMDD(startDate);
                chgData.put("START_CYCLE_ID", startDate);
            }

            // 2-3生成end-date
            if ("00".equals(effecMonth))
            {
                chgData.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());
            }
            else
            {
                int month = Integer.parseInt(effecMonth);
                startDate = SysDateMgr.getDateForSTANDYYYYMMDD(startDate);
                String end_cycle_id=SysDateMgr.getAddMonthsLastDay(month, startDate);
                chgData.put("END_CYCLE_ID", SysDateMgr.getDateForYYYYMMDD(end_cycle_id));
            }
        }

    }
    
    /**
     * @description 登记集团客户一点支付的高级付费关系
     * @author xunyl
     * @date 2014-10-24
     */
    public static IData addYDZFPayRelation(String userId,String grpUserId,String acctId,IDataset paramInfoList)throws Exception{
        //1- 获取支付方式和支付额度
        String payType = "";
        int payValue = 0;
        if(IDataUtil.isEmpty(paramInfoList)){
            return null;
        }
        for(int i=0;i<paramInfoList.size();i++){
            IData paramInfo = paramInfoList.getData(i);
            String attrCode = paramInfo.getString("ATTR_CODE");
            String attrValue = paramInfo.getString("ATTR_VALUE","0");
            if("1007".equals(attrCode) && StringUtils.equals("0", attrValue)){//全额
                payType = "0";
                payValue =0;
            }else if("1007".equals(attrCode)){//限额
                payType = "1";
                payValue = Integer.parseInt(attrValue)*100;
            }else if("1008".equals(attrCode)){//比例
                payType = "2";
                payValue = Integer.parseInt(attrValue);
            }
        }
        
        //2- 拼装高级付费关系数据
        IData payRela = new DataMap();
        payRela.put("USER_ID", userId);
        payRela.put("ACCT_ID", acctId);// 集团账户
        payRela.put("PAYITEM_CODE", "-1");
        payRela.put("ACCT_PRIORITY", "0"); // 帐户优先级：当一个用户的某个帐目由多个帐户为其付费时的顺序
        payRela.put("USER_PRIORITY", "0"); // 用户优先级：基于帐户做优惠时，作用在用户上按优先级进行
        payRela.put("BIND_TYPE", "0"); // 绑定帐户方式：0-按优先级，1-按金额几何平分
        payRela.put("ACT_TAG", "1"); // 作用标志：0-不作用，1-作用
        payRela.put("DEFAULT_TAG", "0"); // 默认标志     
        payRela.put("LIMIT_TYPE", payType); // 限定方式：0-不限定，1-金额，2-比例
        payRela.put("LIMIT", payValue); // 限定值
        payRela.put("COMPLEMENT_TAG", "0"); // 是否补足：0-不补足，1-补足
        payRela.put("INST_ID", SeqMgr.getInstId());
        payRela.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue()); // 状态属性：0-增加，1-删除，2-变更
        payRela.put("START_CYCLE_ID", Utility.decodeTimestamp("yyyyMMdd", SysDateMgr.getFirstDayOfThisMonth()));
        payRela.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231());
        payRela.put("RSRV_STR1", grpUserId);// 成员关系注销时用到该字段结束代付关系
        return payRela;
    }
    /**
     * 集团一点支付高级付费关系变更
     * 需同步一条新增 下账期生效
     * 一条删除  本账期末失效
     * chenyi
     * 2014-1-4
     * @param userId
     * @param acctId
     * @param paramInfoList
     * @return
     * @throws Exception
     */
    public static IDataset chgYDZFPayRelation(String userId,String acctId,IDataset paramInfoList)throws Exception{
        
        IDataset payRelaDataset=new DatasetList();
        
        //1- 获取支付方式和支付额度
        if(IDataUtil.isEmpty(paramInfoList)){
            return payRelaDataset;
        }
        
        // 2过滤参数，获取add状态参数
        paramInfoList = GrpCommonBean.getOthererParam(paramInfoList);
        
        // 3获取变化参数   反向受理 attr_code与规范相同 正向受理的attr_code是product_id与规范attr_code组合
        IData tempData = new DataMap();// 暂存有变化的参数
        for (int i = 0, sizeI = paramInfoList.size(); i < sizeI; i++)
        {
            IData paramData = paramInfoList.getData(i);
            String attrCode = paramData.getString("ATTR_CODE");
            String attrValue = paramData.getString("ATTR_VALUE");
            if (attrCode.endsWith("1007") && StringUtils.equals("0", attrValue))
            {
                tempData.put("newPayType", "0");
                tempData.put("newPayValue", 0);                   
            }else if(attrCode.endsWith("1007") && StringUtils.isNotEmpty(attrValue)){
                int newPayValue = Integer.parseInt(attrValue)*100;
                tempData.put("newPayType", "1");
                tempData.put("newPayValue", newPayValue);               
            }else if (attrCode.endsWith("1008") && StringUtils.isNotEmpty(attrValue))
            {             
                int newPayValue=Integer.parseInt(attrValue);
                tempData.put("newPayType", "2");
                tempData.put("newPayValue", newPayValue);             
            }
        }
        
        if(IDataUtil.isEmpty(tempData)){
            return payRelaDataset;
        }
        
        //4- 查询资料表对应的付费关系信息
        IDataset userPayRelaInfoDataset= PayRelaInfoQry.getPayRelatByUidAid(acctId,"0","-1",userId,null);
        
        if(IDataUtil.isNotEmpty(userPayRelaInfoDataset)){
            IData  oldPayRelationInfo=userPayRelaInfoDataset.getData(0);
            IData  newPayRelationInfo=(IData)Clone.deepClone(oldPayRelationInfo);
            //5-将老数据信息的enddate改成本账期末
            oldPayRelationInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            oldPayRelationInfo.put("END_CYCLE_ID", SysDateMgr.getNowCycle());
            //6-蒋变更后的新数据的生效时间设成下账期
            String startDate = SysDateMgr.getFirstDayOfNextMonth();
            newPayRelationInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            newPayRelationInfo.put("START_CYCLE_ID", SysDateMgr.getDateForYYYYMMDD(startDate));
            //7-将更新的参数同步payRela信息
            if(StringUtils.isNotEmpty(tempData.getString("newPayType"))){
                newPayRelationInfo.put("LIMIT_TYPE", tempData.getString("newPayType")); // 限定方式：0-不限定，1-金额，2-比例
            } 
           if(StringUtils.isNotEmpty(tempData.getString("newPayValue"))){
             newPayRelationInfo.put("LIMIT", tempData.getString("newPayValue")); // 限定值
            }
           newPayRelationInfo.put("INST_ID", SeqMgr.getInstId());
           
           payRelaDataset.add(oldPayRelationInfo);
           payRelaDataset.add(newPayRelationInfo);
        }
        return payRelaDataset;
    }
}
