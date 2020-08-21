
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.action.sms.PerSmsAction;

public class FamilyShortCodeBusiSmsAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<SvcTradeData> svcList = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);

        if (null != svcList && svcList.size() > 0)
        {
            String userIdA = "";
            StringBuilder strContent = new StringBuilder();
            strContent.append("尊敬的客户，您所在的亲亲网短号已变更。变更后的短号为：");

            StringBuilder strMainContent = new StringBuilder();
            StringBuilder strMebContent = new StringBuilder();
            for (int i = 0, size = svcList.size(); i < size; i++)
            {
                SvcTradeData svc = svcList.get(i);
                String serialNumberB = svc.getRsrvStr1();// 手机号
                String shortCodeB = svc.getRsrvStr2();// 变更后的短号

                IData user = UcaInfoQry.qryUserInfoBySn(serialNumberB);
                String userId = user.getString("USER_ID");

                IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(userId, "45", null);

                if (IDataUtil.isNotEmpty(result))
                {
                    IData uuInfo = result.getData(0);
                    userIdA = uuInfo.getString("USER_ID_A");
                    String roleCodeB = uuInfo.getString("ROLE_CODE_B");

                    // 如果是主号
                    if (StringUtils.equals(roleCodeB, "1"))
                    {
                        if (StringUtils.isNotBlank(shortCodeB))
                        {
                            strMainContent.append("主号码");
                            strMainContent.append(serialNumberB);
                            strMainContent.append("短号");
                            strMainContent.append(shortCodeB);
                        }
                        else
                        {
                            strMainContent.append("主号码");
                            strMainContent.append(serialNumberB);
                        }
                    }

                    // 如果是副号
                    if (StringUtils.equals(roleCodeB, "2"))
                    {
                        if (StringUtils.isNotBlank(shortCodeB))
                        {
                            strMebContent.append(";副号码");
                            strMebContent.append(serialNumberB);
                            strMebContent.append("短号");
                            strMebContent.append(shortCodeB);
                        }
                        else
                        {
                            strMebContent.append(";副号码");
                            strMebContent.append(serialNumberB);
                        }
                    }
                }
            }

            strContent.append(strMainContent);
            if (StringUtils.isBlank(strMainContent.toString()))
            {// 如果不存在主号短号变更，则去掉第一个副号的分号
                strContent.append(strMebContent.substring(1, strMebContent.length()));
            }
            else
            {
                strContent.append(strMebContent);
            }
            strContent.append("。亲亲网成员间在省内可拨打短号通话、通过短号发送短信及彩信，享受通话优惠。");

            IData smsData = new DataMap();
            smsData.put("SMS_PRIORITY", "5000");// 优先级：老系统默认5000
            smsData.put("FORCE_OBJECT", "10086");// 发送对象
            smsData.put("CANCEL_TAG", "0");// 返销标志：0-未返销，1-被返销，2-返销 不能为空
            smsData.put("NOTICE_CONTENT", strContent.toString());// 短信内容

            IDataset uuList = RelaUUInfoQry.qryRelaUUByUIdAAllDB(userIdA, "45");
            if (IDataUtil.isNotEmpty(uuList))
            {
                for (int i = 0, size = uuList.size(); i < size; i++)
                {
                    IData uuInfo = uuList.getData(i);
                    String snB = uuInfo.getString("SERIAL_NUMBER_B");
                    smsData.put("RECV_OBJECT", snB);// 接收对象
                    PerSmsAction.insTradeSMS(btd, smsData);
                }
            }
        }

    }

}
