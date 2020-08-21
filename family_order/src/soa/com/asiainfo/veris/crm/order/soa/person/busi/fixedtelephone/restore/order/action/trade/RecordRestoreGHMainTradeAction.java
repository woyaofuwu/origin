
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.order.action.trade;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.requestdata.RestoreUserReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RecordRestoreUserMainTradeAction.java
 * @Description: 记录资源信息到主订单预留字段
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-6-5 下午4:54:23
 */
public class RecordRestoreGHMainTradeAction implements ITradeAction
{
    public void executeAction(BusiTradeData btd) throws Exception
    {
        RestoreUserReqData restoreUserRD = (RestoreUserReqData) btd.getRD();
        String codingStr = restoreUserRD.getX_coding_str();// 页面返回的资源信息,及本次复机之后有用的
        IDataset pageResList = new DatasetList(codingStr);
        if (IDataUtil.isNotEmpty(pageResList) && pageResList.size() == 2)
        {
            for (int i = 0; i < pageResList.size(); i++)
            {
                String resTypeCode = pageResList.getData(i).getString("col_RES_TYPE_CODE", "");
                String resCode = pageResList.getData(i).getString("col_RES_CODE", "");
                String oldResCode = pageResList.getData(i).getString("col_OLD_RES_CODE", "");
                if (!StringUtils.endsWithIgnoreCase(resCode, oldResCode))
                {
                    if ("0".equals(resTypeCode))
                    {
                        btd.getMainTradeData().setRsrvStr2(resCode);// 新手机号码
                    }
                    else if ("1".equals(resTypeCode))
                    {
                        btd.getMainTradeData().setRsrvStr7(resCode);// 新sim卡
                    }
                }
            }

            /*
             * //宽带复机改密码 IData usergene=new DataMap(); usergene.put("USER_ID", td.getUserId()); IDataset
             * usergenes=this.queryBySql(pd, usergene, "TF_F_USER_ENCRYPT_GENE", "SEL_BY_USERID");
             * if(usergenes!=null&&usergenes.size()>=1){
             * if("1".equals(td.getString("HAS_CHANGED_SIM","0"))&&!"".equals(td
             * .getString("NEW_PASSWD",""))&&!"".equals(td.getString("PASS_CODE",""))){
             * //如果是新的初始化服务密码，置台帐PROCESS_TAG_SET第一位为N，完工时根据此标识发提醒短信 String processTagSet =
             * td.getTradeData(td.getTradeId()).getString("PROCESS_TAG_SET", "0"); processTagSet =
             * "N"+processTagSet.substring(1); td.setChildTradeInfo(X_TRADE_DATA.X_TRADE_MAIN, "PROCESS_TAG_SET",
             * processTagSet); td.setChildTradeInfo(X_TRADE_DATA.X_TRADE_MAIN, "RSRV_STR6","1");//需变更密码
             * stringTableTradeUser(pd,td); } }
             */

        }
    }

}
