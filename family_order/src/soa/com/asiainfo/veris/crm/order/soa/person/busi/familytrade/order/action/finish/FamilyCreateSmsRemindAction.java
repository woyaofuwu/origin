
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 亲亲网组建/新增成员 短信提醒处理
 * 
 * @author zhouwu
 * @date 2014-07-22 21:05:28
 */
public class FamilyCreateSmsRemindAction implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String staffId = mainTrade.getString("TRADE_STAFF_ID", "");
        String departId = mainTrade.getString("TRADE_DEPART_ID", "");
        String serialNumberB = mainTrade.getString("RSRV_STR7", "");// 成员号码

        // 如果是短信验证
        if (StringUtils.isNotBlank(serialNumberB))
        {
            String shortCodeB = mainTrade.getString("RSRV_STR6", "");// 成员短号
            String appDiscntCode = mainTrade.getString("RSRV_STR8", "");// 成员叠加优惠
            String mainShortCode = mainTrade.getString("RSRV_STR3", "");// 主号短号

            String[] serialNumberBs = StringUtils.split(serialNumberB, ",");
            String[] shortCodeBs = StringUtils.split(shortCodeB, ",");
            String[] appDiscntCodes = StringUtils.split(appDiscntCode, ",");

            if (null != serialNumberBs && serialNumberBs.length > 0)
            {
                for (int i = 0, length = serialNumberBs.length; i < length; i++)
                {
                    String snB = serialNumberBs[i];
                    String scB = shortCodeBs[i];
                    String appDc = appDiscntCodes[i];
                    if (StringUtils.isBlank(appDc))
                    {
                        appDc = "";
                    }

                    if (StringUtils.isBlank(snB))
                        continue;

                    IData param = new DataMap();
                    param.put("TRADE_ID", tradeId);
                    param.put("SERIAL_NUMBER_A", serialNumber);// 主号
                    param.put("SERIAL_NUMBER_B", snB);// 副号
                    param.put("ROLE_CODE_B", "2");// 角色
                    param.put("SHORT_CODE", scB);// 短号
                    param.put("DISCNT_CODE", appDc);// 叠加包优惠编码
                    param.put("RELATION_TRADE_ID", "");
                    param.put("UPDATE_STAFF_ID", staffId);
                    param.put("UPDATE_DEPART_ID", departId);
                    param.put("REMARK", "");
                    param.put("RSRV_STR1", mainShortCode);// 主号的短号

                    Dao.executeUpdateByCodeCode("TF_F_FAMILY_REMIND", "INSERT_FAMILY_REMIND", param);
                }
            }
        }
        else
        { // 如果不是，则把提醒记录置为无效
            IData param = new DataMap();
            param.put("TRADE_ID", tradeId);
            param.put("SERIAL_NUMBER_A", serialNumber);// 主号
            param.put("UPDATE_STAFF_ID", staffId);
            param.put("UPDATE_DEPART_ID", departId);
            
            StringBuilder sql = new StringBuilder(300);
            sql.append("SELECT B.SERIAL_NUMBER_B  FROM TF_B_TRADE_RELATION B ");
            sql.append("WHERE B.TRADE_ID = :TRADE_ID ");
            sql.append("           AND B.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2)) ");
            sql.append("           AND B.RELATION_TYPE_CODE = '45' ");
            sql.append("           AND B.MODIFY_TAG = '0' ");
            sql.append("           AND B.END_DATE > SYSDATE ");
            
            IDataset tradeRelaInfos = Dao.qryBySql(sql, param, Route.getJourDb());
            if(IDataUtil.isNotEmpty(tradeRelaInfos))
            {
            	for(int i=0;i<tradeRelaInfos.size();i++)
            	{
            		IData temp = tradeRelaInfos.getData(i);
            		param.put("SERIAL_NUMBER_B", temp.getString("SERIAL_NUMBER_B"));
            		Dao.executeUpdateByCodeCode("TF_F_FAMILY_REMIND", "UPD_FAMILY_REMIND_BACK", param);
            	}
            }
        }
    }
}
