
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm.SaleActiveQry;

public class CheckMebElementLimit extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckMebElementLimit.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入规则 CheckMebElementLimit() >>>>>>>>>>>>>>>>>>");

        String resultStr = "";

        String serialNumber = databus.getString("SERIAL_NUMBER", "");
        String ctrlType = databus.getString("CTRL_TYPE", "");

        // 1.集团成员注销时 判断：存在有效的学护卡终端赠送，不能取消学护卡业务。
        resultStr = runXHKDesMebLimit(serialNumber, ctrlType);
        if (StringUtils.isNotBlank(resultStr))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201409050001", resultStr);
            return true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 结束规则 CheckMebElementLimit() 返回结果>>>>>>>>>>>>>>>>>>");

        return false;
    }

    /**
     * 该手机号没有订购校讯通10元套餐，不允许订购学护卡15元包
     *
     * @param grpUserId
     * @param outSn
     * @param mebElementList
     * @return
     * @throws Exception
     */
    public static String runXHKMebDisLimit(String grpUserId, String outSn, IDataset mebElementList) throws Exception
    {
        String retStr = ""; // 返回错误信息
        boolean isExistXXT = true;
        boolean isDisCodeXXT = true;

        IDataset mebDisElement = DataHelper.filter(mebElementList, "MODIFY_TAG=0,ELEMENT_TYPE_CODE=D");// 只处理变更时删除的定制服务
        if (IDataUtil.isNotEmpty(mebDisElement))
        {
            for (int i = 0, row = mebDisElement.size(); i < row; i++)
            {
                IData mebDisData = mebDisElement.getData(i);
                String distcntId = mebDisData.getString("ELEMENT_ID", "");
                if ("32000001".equals(distcntId))// ，如果该成员用户要订购学护卡15元包,则必须是订购过校讯通10元套餐的用户
                {

                    IDataset xxtOutMemInfo = RelaXxtInfoQry.queryMemInfoByOutSnandUserIdA(outSn, grpUserId);
                    xxtOutMemInfo = DataHelper.filter(xxtOutMemInfo, "ELEMENT_TYPE_CODE=D");
                    if (IDataUtil.isEmpty(xxtOutMemInfo))
                    {
                        isExistXXT = false;
                    }
                    IDataset limitDiscnt = StaticUtil.getStaticList("EDC_DISCNT_15_LIMIT");
                    if (IDataUtil.isNotEmpty(xxtOutMemInfo))
                    {
                        for (int j = 0, jsize = xxtOutMemInfo.size(); j < jsize; j++)
                        {
                            IData data = xxtOutMemInfo.getData(j);
                            String disId = data.getString("ELEMENT_ID", "");
                            IDataset filterResult = DataHelper.filter(limitDiscnt, "DATA_ID=" + disId);
                            if (IDataUtil.isNotEmpty(filterResult))
                            {
                                isDisCodeXXT = false;
                                break;
                            }

                        }
                        if (isDisCodeXXT == true)
                        {
                            isExistXXT = false;
                        }

                    }
                }

            }

            if (isExistXXT == false)
            {
                retStr = "成员手机号：【 " + outSn + " 】要订购学护卡15元包,必须是先订购校讯通10元套餐或者8元套餐！";
            }
        }

        return retStr;
    }

    /**
     * 存在有效的学护卡终端赠送，不能取消学护卡业务
     *
     * @param serialNumber
     * @param ctrlType
     * @return
     * @throws Exception
     */
    public static String runXHKDesMebLimit(String serialNumber, String ctrlType) throws Exception
    {
        String retStr = ""; // 返回错误信息
        if (!BizCtrlType.DestoryMember.equals(ctrlType))
        {
            return retStr;
        }
        IDataset SaleActiveInfo = SaleActiveQry.qrySaleActiveBySn(serialNumber);
        if (IDataUtil.isNotEmpty(SaleActiveInfo))
        {
            retStr = "成员手机号：【 " + serialNumber + " 】存在有效的学护卡终端赠送，不能取消学护卡业务！";
        }

        return retStr;
    }

}
