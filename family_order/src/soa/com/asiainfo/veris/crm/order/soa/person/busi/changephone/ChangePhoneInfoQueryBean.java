
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.PreTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAltsnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ChangePhoneInfoQueryBean extends CSBizBean
{

    public IDataset queryChangeCardInfo(IData input) throws Exception
    {
        IDataset result = new DatasetList();
        IData info = new DataMap();
        String serial_number = input.getString("SERIAL_NUMBER", "");
        if ("".equals(serial_number))
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_201); // 号码为空
        }
        String trade_type_code_pre = "799";
        IDataset dataset = PreTradeInfoQry.queryPreStatus1(serial_number, trade_type_code_pre);

        if (IDataUtil.isNotEmpty(dataset))
        {
            info.put("SERIAL_NUMBER", serial_number);
            info.put("NEW_SERIAL_NUMBER", dataset.getData(0).getString("RSRV_STR1"));
            info.put("OLD_SERIAL_NUMBER", dataset.getData(0).getString("RSRV_STR2"));

            if ("1".equals(dataset.getData(0).getString("STATUS"))){
                info.put("ROW_STATUS", 1);
                info.put("PRE_STATUS", "预受理登记成功");
            }
            else if ("2".equals(dataset.getData(0).getString("STATUS"))){
                info.put("ROW_STATUS", 2);
                info.put("PRE_STATUS", "预受理取消");
            }
            else if ("2".equals(dataset.getData(0).getString("STATUS"))){
                info.put("ROW_STATUS", 3);
                info.put("PRE_STATUS", "预受理对应业务成功");
            }
            else{
                info.put("ROW_STATUS", "");
                info.put("PRE_STATUS", "");
            }
            // info.put("PRE_STATUS", "预受理成功" ) ; //"1");//预约状态 1 预约受理中 2 改号完成
            info.put("LABEL1", "受理时间");
            info.put("LABEL2", "失效时间");
            info.put("VALUE1", dataset.getData(0).getString("PRE_ACCEPT_TIME"));
            info.put("VALUE2", dataset.getData(0).getString("PRE_INVALID_TIME"));
            info.put("FLAG", "1");
            result.add(info);
        }
        else
        {

            IDataset dataset1 = UserAltsnInfoQry.queryAllAltStateInfoBySn(serial_number);
            if (IDataUtil.isNotEmpty(dataset1))
            {
                if ("0".equals(dataset1.getData(0).getString("STATUS"))){
                    info.put("ROW_STATUS", 4);
                    info.put("PRE_STATUS", "已登记");
                }
                else if ("1".equals(dataset1.getData(0).getString("STATUS"))){
                    info.put("ROW_STATUS", 5);
                    info.put("PRE_STATUS", "已激活");
                }
                else if ("2".equals(dataset1.getData(0).getString("STATUS"))){
                    info.put("ROW_STATUS", 6);
                    info.put("PRE_STATUS", "已登记取消");
                    }
                else if ("3".equals(dataset1.getData(0).getString("STATUS"))){
                    info.put("ROW_STATUS", 7);
                    info.put("PRE_STATUS", "已取消");
                }
                // 获取改号信息
                info.put("LABEL1", "号码归属地州");
                info.put("LABEL2", "老号码归属地州");
                if ("1".equals(dataset1.getData(0).getString("RELA_TYPE")))
                {
                    info.put("SERIAL_NUMBER", dataset1.getData(0).getString("SERIAL_NUMBER"));
                    info.put("NEW_SERIAL_NUMBER", dataset1.getData(0).getString("SERIAL_NUMBER"));// 新号码
                    info.put("OLD_SERIAL_NUMBER", dataset1.getData(0).getString("RELA_SERIAL_NUMBER"));// 老号码
                    info.put("VALUE1", dataset1.getData(0).getString("EPARCHY_CODE"));
                    info.put("VALUE2", dataset1.getData(0).getString("RELA_EPARCHY_CODE"));
                }
                else if ("2".equals(dataset1.getData(0).getString("RELA_TYPE")))
                {
                    info.put("SERIAL_NUMBER", dataset1.getData(0).getString("RELA_SERIAL_NUMBER"));
                    info.put("NEW_SERIAL_NUMBER", dataset1.getData(0).getString("RELA_SERIAL_NUMBER"));// 新号码
                    info.put("OLD_SERIAL_NUMBER", dataset1.getData(0).getString("SERIAL_NUMBER"));// 老号码
                    info.put("VALUE2", dataset1.getData(0).getString("EPARCHY_CODE"));
                    info.put("VALUE1", dataset1.getData(0).getString("RELA_EPARCHY_CODE"));
                }
                info.put("FLAG", "2");
                result.add(info);
            }
            else
            {
                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_203); // 你输入的号码，不是改号号码！
            }

        }

        return result;
    }

    /**
     * 返回老号码相应的信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryNewImsiByTrade(IData input) throws Exception
    {
        // TODO Auto-generated method stub
        String trade_id = input.getString("TRADE_ID");
        String res_type_code = "1";
        String modify_tag = "0";
        String opr_time = "";

        IDataset result = TradeResInfoQry.qryTradeResByTradeTypeMtag(trade_id, res_type_code, modify_tag);
        
        if (IDataUtil.isNotEmpty(result))
        {
            result.getData(0).put("VOLTE_TYPE", "0");
            String userId = result.getData(0).getString("USER_ID");
            IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);

            if (IDataUtil.isNotEmpty(userInfo))
            {
                opr_time = userInfo.getString("OPR_TIME");
            }
            IDataset userVolte = UserSvcInfoQry.getSvcUserId(userId, "190");
            if(IDataUtil.isNotEmpty(userVolte)){
                IDataset impuInfo = TradeImpuInfoQry.qryTradeImpuInfo(trade_id);
                result.getData(0).put("VOLTE_TYPE", "1");
                result.getData(0).put("OLD_IMPI", impuInfo.getData(0).get("IMPI"));
                
            }
        }
        return result;

    }

}
