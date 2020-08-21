
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 * 物联网测试期套餐转沉默期
 * @author yanwu
 *
 */
public class WlwTestDiscntAction implements ITradeAction
{

    @SuppressWarnings("unchecked")
	@Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	if (!"PWLW".equals(btd.getMainTradeData().getBrandCode()))
        {
            return;
        }
    	
    	IDataset idsCommpara1551 = CommparaInfoQry.getOnlyByAttr("CSM", "1551", btd.getRD().getUca().getUserEparchyCode());
        if (IDataUtil.isEmpty(idsCommpara1551))
        {
            return;
        }
        
        UcaData uca = btd.getRD().getUca();
        String strSn = uca.getSerialNumber();
        
        boolean bIsExist = false;
        List<DiscntTradeData> lsDiscntTradeData = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if(CollectionUtils.isEmpty(lsDiscntTradeData))
        {
        	return;
        }
        
        for (int i = 0; i < lsDiscntTradeData.size(); i++) 
        {
            if (!bIsExist)
            {
            	DiscntTradeData dtDiscntTradeData = lsDiscntTradeData.get(i);
                if (BofConst.MODIFY_TAG_DEL.equals(dtDiscntTradeData.getModifyTag()))
                {
                    for (int j = 0; j < idsCommpara1551.size(); j++)
                    {
                    	IData idCommpara1551 = idsCommpara1551.getData(j);
                        String strParaCode = idCommpara1551.getString("PARAM_CODE");
                        if (dtDiscntTradeData.getElementId().equals(strParaCode))
                        {
                        	dtDiscntTradeData.setEndDate(SysDateMgr.getSysTime());
                        	bIsExist = true;
                            break;
                        }
                    }
                }
            }

            if (bIsExist)
            {
                break;
            }
        }
        if (bIsExist)
        {
        	UserTradeData utUser = uca.getUser();
            if(utUser != null)
            {
            	utUser.setModifyTag("2");
            	utUser.setAcctTag("2");
            	utUser.setUserTypeCode("F");
            	btd.add(strSn, utUser);
            }
            SilenceTransNormal(btd, uca);
            updateIotBookDealTag(btd, strSn);
        }
    }
    
    @SuppressWarnings("unchecked")
	private void SilenceTransNormal(BusiTradeData btd, UcaData uca) throws Exception
    {
    	// 新增一条6个帐期后由沉默期转正常的记录
        IData newTaskParam = new DataMap();
        newTaskParam.put("SERIAL_NUMBER", uca.getSerialNumber());
        newTaskParam.put("USER_ID", uca.getUser().getUserId());
        newTaskParam.put("INST_ID", SeqMgr.getInstId(Route.CONN_CRM_CG));
        newTaskParam.put("OLD_STATE_CODE", "1");
        newTaskParam.put("NEW_STATE_CODE", "2");
        newTaskParam.put("TRAN_DATE", SysDateMgr.endDateOffset(SysDateMgr.getSysTime(), "6", "3"));// 根据当地规则结合分散帐期修改
        newTaskParam.put("DEAL_TAG", "0");
        newTaskParam.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        newTaskParam.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        newTaskParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
        newTaskParam.put("TRADE_ID", btd.getTradeId());
        newTaskParam.put("RSRV_STR1", btd.getMainTradeData().getOrderId());
        Dao.insert("TF_F_INTERNETOFTHINGS_BOOK", newTaskParam, Route.CONN_CRM_CEN);
    }
    
    @SuppressWarnings("unchecked")
	private void updateIotBookDealTag(BusiTradeData btd, String sn) throws Exception
    {
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", sn); 
    	param.put("OLD_STATE_CODE", "0");
        param.put("NEW_STATE_CODE", "1");
    	   
    	StringBuilder sql = new StringBuilder(1000);
    	sql.append(" UPDATE TF_F_INTERNETOFTHINGS_BOOK T ");
    	sql.append(" SET T.DEAL_TAG = '1', T.UPDATE_TIME = SYSDATE ");
    	sql.append(" WHERE T.SERIAL_NUMBER = :SERIAL_NUMBER ");
    	sql.append("   AND T.OLD_STATE_CODE = :OLD_STATE_CODE ");
    	sql.append("   AND T.NEW_STATE_CODE = :NEW_STATE_CODE ");
        Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
    }
}
