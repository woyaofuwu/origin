package com.asiainfo.veris.crm.order.soa.person.common.action.trade.np;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.config.DBRouteCfg;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class CheckNpCodeByPlatSvcAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        String serialNumber = btd.getMainTradeData().getSerialNumber();
        String userId = btd.getMainTradeData().getSerialNumber();
        String eparchyCode = btd.getMainTradeData().getEparchyCode();
        IDataset ids = TradeNpQry.getValidTradeNpBySn(serialNumber);
        if (IDataUtil.isNotEmpty(ids))
        {
            String asp = ids.getData(0).getString("ASP", "").trim();
             if("2".equals(asp) || "3".equals(asp)){
                List<PlatSvcTradeData> platSvcs = btd.get("TF_B_TRADE_PLATSVC");
                if (platSvcs != null)
                {
                    IDataset idsDataset = CommparaInfoQry.getOnlyByAttr("CSM","7575",eparchyCode);
                    Map m = new HashedMap();
                    if(IDataUtil.isNotEmpty(idsDataset)){
                     
                        for(int i =0,len = idsDataset.size();i<len;i++){
                            IData data =  idsDataset.getData(i);
                            String paramCode = data.getString("PARAM_CODE","").trim();
                            String paramCode1 = data.getString("PARA_CODE1","").trim();
                            String key = paramCode+"_"+paramCode1;
                            m.put(key, data);
                        }
                    }
                    
                    
                    String bizTypeCode = "",orgDomain = "";
                    for(PlatSvcTradeData platSvc:platSvcs){
                       String modifyTag = platSvc.getModifyTag();
                       if(BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
                           String elementId = platSvc.getElementId();
                           IDataset tdbPlatSvcs = BofQuery.getPlatInfoByServiceId(elementId);
                           if (IDataUtil.isNotEmpty(tdbPlatSvcs))
                           {
                               bizTypeCode = tdbPlatSvcs.getData(0).getString("BIZ_TYPE_CODE","").trim();
                               orgDomain = tdbPlatSvcs.getData(0).getString("ORG_DOMAIN","").trim();
                               String newKeyString = bizTypeCode+"_"+orgDomain;
                               if(m.containsKey(newKeyString)){
                                   sendSms(serialNumber, userId, eparchyCode);
                                   String err_code = elementId + ":" + newKeyString;
                                   CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, err_code + "该业务暂不向携转号码开放使用，请您谅解!");
                               }
                           }
                       }
                    }
                    
                }
            }

        }
    }

    private void sendSms(String serialNumber, String userId, String eparchyCode) throws Exception
    {
        DBConnection conn = null;
        try
        {

            BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
            String route = DBRouteCfg.getRoute(DBRouteCfg.getGroup(CSBizBean.getVisit().getSubSysCode()), eparchyCode);
            
            conn = SessionManager.getInstance().getAsyncConnection(route);

            IData smsData = new DataMap();

            smsData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            smsData.put("RECV_OBJECT", serialNumber);
            smsData.put("RECV_ID", userId);
            smsData.put("NOTICE_CONTENT", "尊敬的用户，感谢您对中国移动业务的支持，该业务暂不向携转号码开放使用，请您谅解。");
            smsData.put("REMARK", "联通电信用户办理平台业务拦截");
            
            dao.insert(conn, "TI_O_SMS", SmsSend.prepareSmsData(smsData));

            conn.commit();
        }
        catch (Exception ex)
        {

            ex.printStackTrace();
            try
            {
                if(conn!=null){
                    conn.rollback();
                }
                
            }
            catch (Exception ex1)
            {
                ex1.printStackTrace();
            }
        }
        finally
        {
            try
            {
                if(conn!=null){
                    conn.close();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

        

}
