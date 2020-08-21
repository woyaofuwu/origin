
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
public class FeeProtectLimitAction implements ITradeAction
{
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
		UcaData uca = btd.getRD().getUca();
		List<PlatSvcTradeData> platSvcTradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        if (platSvcTradeDataList == null || platSvcTradeDataList.size() <= 0)
        {
            return;
        }

        int size = platSvcTradeDataList.size();
        for (int i = 0; i < size; i++)
        {
        	PlatSvcTradeData tradePlatsvc = platSvcTradeDataList.get(i);
            
        	String modifyTag = tradePlatsvc.getModifyTag();
            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag)||BofConst.MODIFY_TAG_UPD.equals(modifyTag))
            {
	            //查找增值业务总控开关资费编码-PARA_CODE1:企业代码起始段PARA_CODE2:企业代码终止段PARA_CODE3:业务代码PARA_CODE4:对应的开关资费
	            IDataset discntCodes = CommparaInfoQry.getCommPkInfo("CSM","1815", "all", "0898");
	            String discntCode = "";
				if(IDataUtil.isNotEmpty(discntCodes))
				{
					discntCode = discntCodes.getData(0).getString("PARA_CODE4");
				}
				
				IDataset userDiscnts = UserDiscntInfoQry.getAllDiscntByUserId(uca.getUserId(), discntCode);
				if(IDataUtil.isNotEmpty(userDiscnts))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户办理了增值业务总控开关资费【"+discntCode+"】，不能办理平台业务!");
				}
				else
				{
		            PlatOfficeData officeData = null;
		            if (tradePlatsvc.getPmd() == null)
		            {
		                officeData = PlatOfficeData.getInstance(tradePlatsvc.getElementId());
		            }
		            else
		            {
		                officeData = ((PlatSvcData) tradePlatsvc.getPmd()).getOfficeData();
		            }
		            String spCode = officeData.getSpCode();
		            String bizCode = officeData.getBizCode();
		            String bizTypeCode = officeData.getBizTypeCode();
		            
					IData indata = new DataMap();
					indata.put("BIZ_TYPE_CODE", bizTypeCode);
					indata.put("SP_CODE", spCode);
					indata.put("BIZ_CODE", bizCode);
					
					SQLParser parser = new SQLParser(indata);
			        parser.addSQL("SELECT PARAM_NAME,PARA_CODE4 FROM TD_S_COMMPARA WHERE 1=1 AND PARAM_ATTR='1815'");
			        parser.addSQL(" AND PARAM_CODE = :BIZ_TYPE_CODE");
			        parser.addSQL(" AND PARA_CODE1 <= :SP_CODE");
			        parser.addSQL(" AND PARA_CODE2 >= :SP_CODE");
			        parser.addSQL(" AND (PARA_CODE3 = :BIZ_CODE OR PARA_CODE3 IS NULL)" );
			        parser.addSQL(" AND sysdate BETWEEN start_date AND end_date");
			        discntCodes = Dao.qryByParse(parser,Route.CONN_CRM_CEN);
			        String discntName = "";
			        if(IDataUtil.isNotEmpty(discntCodes))
					{
						discntCode = discntCodes.getData(0).getString("PARA_CODE4");
						discntName = discntCodes.getData(0).getString("PARAM_NAME");
					}
					
					userDiscnts = UserDiscntInfoQry.getAllDiscntByUserId(uca.getUserId(), discntCode);
					if(IDataUtil.isNotEmpty(userDiscnts))
					{
						//CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户办理了增值业务开关资费【"+discntName+"】【"+discntCode+"】，不能办理平台业务!");
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户有【"+discntName+"】计费保护开关");
					}
				}
	        }
        }
	}

}
