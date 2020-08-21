
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser.order.requestdata.CttBroadbandCreateReqData;

/**
 * 开户时modem方式选择的是租用，则给绑定modem优惠
 */
public class AddModemDiscnt implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        CttBroadbandCreateReqData cttBroadbandcreateRD = (CttBroadbandCreateReqData) btd.getRD();
        UserTradeData userData = cttBroadbandcreateRD.getUca().getUser();
        String userId = userData.getUserId();
        List discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if (discntTradeDatas == null || discntTradeDatas.size() == 0)
        {
            return;
        }
        DiscntTradeData oldDiscntTradeData = (DiscntTradeData) discntTradeDatas.get(0);
        if (StringUtils.equals("2", cttBroadbandcreateRD.getModemStyle()))
        {
            // 拼资费台帐
            String ModemNumberic = cttBroadbandcreateRD.getModemNumberic();
            IDataset commparas = ParamInfoQry.getCommparaByCode("CSM", "1129", ModemNumberic, CSBizBean.getTradeEparchyCode());
            // 如果没有则取默认的
            if (IDataUtil.isEmpty(commparas))
            {
                commparas = ParamInfoQry.getCommparaByCode("CSM", "1128", "D", CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isEmpty(commparas))
                {
                    CSAppException.apperr(WidenetException.CRM_WIDENET_5);
                }
            }
            // REQ201305100009租用新增光MODEM型号、月租金20元月的相关需求

            String specDiscntCode = commparas.getData(0).getString("PARA_CODE1", "-1");

            DiscntTradeData discntTradeData = new DiscntTradeData();
            discntTradeData.setUserId(userId);
            discntTradeData.setUserIdA("-1");
            discntTradeData.setProductId("-1");
            discntTradeData.setPackageId("-1");
            discntTradeData.setElementId(specDiscntCode);
            discntTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            discntTradeData.setInstId(SeqMgr.getInstId());
            discntTradeData.setStartDate(oldDiscntTradeData.getStartDate());
            discntTradeData.setEndDate(oldDiscntTradeData.getEndDate());
            btd.add(userData.getSerialNumber(), discntTradeData);

            IDataset commparasSvc = ParamInfoQry.getCommparaByCode("CSM", "1128", "S", CSBizBean.getTradeEparchyCode());
            if (IDataUtil.isEmpty(commparasSvc))
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_6);
            }
            String specServiceCode = commparas.getData(0).getString("PARA_CODE1", "-1");
            SvcTradeData svcTradeData = new SvcTradeData();
            svcTradeData.setUserId(userId);
            svcTradeData.setUserIdA("-1");
            svcTradeData.setProductId("-1");
            svcTradeData.setPackageId("-1");
            svcTradeData.setElementId(specServiceCode);
            svcTradeData.setMainTag("0");
            svcTradeData.setInstId(SeqMgr.getInstId());
            svcTradeData.setStartDate(cttBroadbandcreateRD.getAcceptTime());
            svcTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            svcTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            svcTradeData.setRsrvNum1("0");// 该服务对应的速率
            btd.add(userData.getSerialNumber(), svcTradeData);
        }

    }

}
