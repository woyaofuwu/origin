
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.trade;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.ChangeSvcStateRegSVC;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.EmergencyOpenReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ModifyStateByRelationUUAction.java
 * @Description: 停开机处理UU表关联用户的状态，对于海南老系统的TCS_ModifyStateByRelationUU结点
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-3-26 下午3:10:00
 */
public class ModifyStateByRelationUUAction implements ITradeAction
{
    private static final Logger logger = Logger.getLogger(ModifyStateByRelationUUAction.class);

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        // // TODO Auto-generated method stub
        // // 如果用户是一卡双号用户，获取到付费关系，不是合帐，则不关联停机
        // String userId = btd.getRD().getUca().getUserId();
        // IDataset payRelationDataset = PayRelaInfoQry.getAllValidPayRelationByUU(userId, "1", "30");
        // if (IDataUtil.isNotEmpty(payRelationDataset) && payRelationDataset.size() > 1)
        // {
        // String account1 = payRelationDataset.getData(0).getString("ACCT_ID");
        // for (int i = 1; i < payRelationDataset.size(); i++)
        // {
        // String accountTemp = payRelationDataset.getData(i).getString("ACCT_ID");
        // if (!StringUtils.equals(account1, accountTemp))
        // {
        // // return;
        // //当一卡双号的2个用户不满足合帐时，只需要剔除一卡双号这个关系的成员不处理，其他关系下的成员继续处理。
        // payRelationDataset.remove(i);
        // }
        // }
        // }
        //    	
        // // 停开机处理UU表关联的用户
        // ArrayList<String> relationSnList = (ArrayList<String>) getRelationUser(btd);

        String paySerialNumber = getPayRelaUser(btd);
        ArrayList<String> relationSnList = (ArrayList<String>) getRelationUUUser(btd);
        if (StringUtils.isNotBlank(paySerialNumber))
        {
            relationSnList.add(paySerialNumber);
        }

        if (!relationSnList.isEmpty())
        {
            BaseReqData baseReqData = btd.getRD();
            IData paramData = new DataMap();
            paramData.put("TRADE_TYPE_CODE", btd.getTradeTypeCode());
            // 大客户担保开机、担保开机、紧急停机需要获取特有的请求数据
            if ("492".equals(btd.getTradeTypeCode()))
            { // 大客户担保开机
                EmergencyOpenReqData emergencyOpenReqData = (EmergencyOpenReqData) baseReqData;
                paramData.put("OPEN_HOURS", emergencyOpenReqData.getOpenHours());
                paramData.put("GUATANTEE_USER_ID", emergencyOpenReqData.getGuaranteeUserId());
                paramData.put("REMARK", "大客户担保开机关联开机");
            }
            else if ("496".equals(btd.getTradeTypeCode()))
            { // 担保开机
                EmergencyOpenReqData emergencyOpenReqData = (EmergencyOpenReqData) baseReqData;
                paramData.put("OPEN_HOURS", emergencyOpenReqData.getOpenHours());
                paramData.put("GUATANTEE_USER_ID", emergencyOpenReqData.getGuaranteeUserId());
                paramData.put("REMARK", "客户担保开机关联开机");
            }
            else if ("497".equals(btd.getTradeTypeCode()))
            { // 紧急开机
                EmergencyOpenReqData emergencyOpenReqData = (EmergencyOpenReqData) baseReqData;
                paramData.put("CREDIT_CLASS", emergencyOpenReqData.getCreditClass());
                paramData.put("OPEN_HOURS", emergencyOpenReqData.getOpenHours());
                paramData.put("REMARK", "紧急开机关联开机");
            }

            // 关联用户停开机处理
            ChangeSvcStateRegSVC regSvc = new ChangeSvcStateRegSVC();
            for (int i = 0; i < relationSnList.size(); i++)
            {
                String serialNumber = relationSnList.get(i);
                paramData.put("SERIAL_NUMBER", serialNumber);
                try
                {
                    regSvc.tradeReg(paramData);
                }
                catch (Exception e)
                {
                    if (logger.isDebugEnabled())
                    {
                        String sn = btd.getRD().getUca().getSerialNumber();
                        String msg = Utility.getBottomException(e).getMessage();
                    }
                }
            }
        }
    }

    /**
     * 如果用户是一卡双号用户，获取到付费关系，不是合帐，则不关联停机 当一卡双号的2个用户不满足合帐时，只需要剔除一卡双号这个关系的成员不处理，其他关系下的成员继续处理。
     * 
     * @Function: getOneCardTwoUser
     * @Description: 提取一卡双号用户
     * @date Jul 14, 2014 11:28:12 AM
     * @param btd
     * @return
     * @author longtian3
     */
    private String getPayRelaUser(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String serialNumber = "";
        IDataset payRelationDataset = PayRelaInfoQry.getAllValidPayRelationByUU(userId, "1", "30");
        if (IDataUtil.isNotEmpty(payRelationDataset) && payRelationDataset.size() > 1)
        {
            String account1 = payRelationDataset.getData(0).getString("ACCT_ID");
            for (int i = 1; i < payRelationDataset.size(); i++)
            {
                String accountTemp = payRelationDataset.getData(i).getString("ACCT_ID");
                if (!StringUtils.equals(account1, accountTemp))
                {
                    // 当一卡双号的2个用户不满足合帐时，只需要剔除一卡双号这个关系的成员不处理，其他关系下的成员继续处理。
                }
                else
                {
                    String userIdB = payRelationDataset.getData(i).getString("USER_ID");
                    if (!StringUtils.equals(userId, userIdB))
                    {
                        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userIdB);
                        if (IDataUtil.isNotEmpty(userInfo))
                        {
                            serialNumber = userInfo.getString("SERIAL_NUMBER");
                        }
                    }
                }
            }
        }
        return serialNumber;
    }

    /**
     * @methodName: getRelationUser
     * @Description: 提取停开机时需要处理的关联用户
     * @version: v1.0.0
     * @author: xiaozb
     * @date: 2014-3-26 下午3:09:57
     */
    private List<String> getRelationUser(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String tradeTypeCode = btd.getTradeTypeCode();
        ArrayList<String> relationSnList = new ArrayList<String>();
        String[] relationCodeArray = new String[]
        { "50", "56", "30", "34" };
        for (int i = 0; i < relationCodeArray.length; i++)
        {
            String relationTypeCode = relationCodeArray[i];
            IDataset relationUserB = RelaUUInfoQry.getRelaUUInfoByRol(userId, relationTypeCode, CSBizBean.getTradeEparchyCode(), null);
            if (relationUserB != null && !relationUserB.isEmpty())
            {
                String roleCodeB = relationUserB.getData(0).getString("ROLE_CODE_B");
                // 报停和挂失时，家庭统付成员不关联停机;家庭统付成员副号码办业务不关联其他号码停开机
                if ("56".equals(relationTypeCode) && ("131".equals(tradeTypeCode) || "132".equals(tradeTypeCode) || "2".equals(roleCodeB)))
                {
                    continue;
                }
                // 查询用户此关系的所有成员
                String userIdA = relationUserB.getData(0).getString("USER_ID_A");
                IDataset relationUserMembers = RelaUUInfoQry.queryRelationByUserIdA(userIdA);
                if (null != relationUserMembers && !relationUserMembers.isEmpty())
                {
                    for (int j = 0; j < relationUserMembers.size(); j++)
                    {
                        IData relationMember = relationUserMembers.getData(j);
                        String serialNumberB = relationMember.getString("SERIAL_NUMBER_B");
                        String userIdB = relationMember.getString("USER_ID_B");
                        if (StringUtils.equals(userId, userIdB))
                        {
                            continue;
                        }
                        // 统一账户付费主卡开机时，如果副卡是报停或者挂失状态，则副卡不能关联开机
                        if ("56".equals(relationTypeCode))
                        {
                            IDataset userSvcStateDataset = UserSvcStateInfoQry.getUserLastStateByUserSvc(userIdB, "0", CSBizBean.getTradeEparchyCode());
                            if (userSvcStateDataset != null && !userSvcStateDataset.isEmpty())
                            {
                                String stateCode = userSvcStateDataset.getData(0).getString("STATE_CODE");
                                if ("1".equals(stateCode) || "2".equals(stateCode))
                                {
                                    continue;
                                }
                            }
                        }
                        relationSnList.add(serialNumberB);
                    }
                }
            }
        }
        return relationSnList;
    }

    /**
     * 只有当前用户是关系中的主成员角色【role_code_b=1】时，才需要连带处理下面的成员。
     * 
     * @Function: getRelationUUUser
     * @Description: 提取停开机时需要处理的关联用户
     * @date Jul 14, 2014 2:53:12 PM
     * @param btd
     * @return
     * @throws Exception
     * @author longtian3
     */
    private ArrayList<String> getRelationUUUser(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String tradeTypeCode = btd.getTradeTypeCode();
        ArrayList<String> relationSnList = new ArrayList<String>();
        String[] relationCodeArray = new String[]
        { "50", "56", "34", "80" ,"CP"};
        for (int i = 0; i < relationCodeArray.length; i++)
        {
            String relationTypeCode = relationCodeArray[i];

            IDataset relationUserB = RelaUUInfoQry.getRelaUUInfoByRol(userId, relationTypeCode, CSBizBean.getTradeEparchyCode(), null);
            if (IDataUtil.isNotEmpty(relationUserB))
            {
                String roleCodeB = relationUserB.getData(0).getString("ROLE_CODE_B");// 成员角色
                if ("1".equals(roleCodeB))
                {
                    // 报停和挂失时，家庭统付成员不关联停机;家庭统付成员副号码办业务不关联其他号码停开机
                    if ("56".equals(relationTypeCode) && ("131".equals(tradeTypeCode) || "132".equals(tradeTypeCode)))
                    {
                        continue;
                    }
                    // 查询用户此关系的所有成员
                    String userIdA = relationUserB.getData(0).getString("USER_ID_A");
                    IDataset relationUserMembers = RelaUUInfoQry.queryRelationByUserIdA(userIdA);
                    if (IDataUtil.isNotEmpty(relationUserMembers))
                    {
                        for (int j = 0; j < relationUserMembers.size(); j++)
                        {
                            IData relationMember = relationUserMembers.getData(j);
                            String serialNumberB = relationMember.getString("SERIAL_NUMBER_B");
                            String userIdB = relationMember.getString("USER_ID_B");
                            if (StringUtils.equals(userId, userIdB))
                            {
                                continue;
                            }
                            // 统一账户付费主卡开机时，如果副卡是报停或者挂失状态，则副卡不能关联开机
                            if ("56".equals(relationTypeCode))
                            {
                                IDataset userSvcStateDataset = UserSvcStateInfoQry.getUserLastStateByUserSvc(userIdB, "0", CSBizBean.getTradeEparchyCode());
                                if (IDataUtil.isNotEmpty(userSvcStateDataset))
                                {
                                    String stateCode = userSvcStateDataset.getData(0).getString("STATE_CODE");
                                    if ("1".equals(stateCode) || "2".equals(stateCode))
                                    {
                                        continue;
                                    }
                                }
                            }
                            relationSnList.add(serialNumberB);
                        }
                    }
                }
            }
        }
        return relationSnList;
    }
}
