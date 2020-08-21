package com.asiainfo.veris.crm.order.soa.group.groupintf.credit.esop;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustGroupTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.BookTradeQueryDAO;

public class EsopCreditBean extends CSBizBean
{
    public IDataset queryPauseBackLines(IData inparam) throws Exception
    {
        String userId = inparam.getString("USER_ID");
        String TradeTypeCode = inparam.getString("TRADE_TYPE_CODE");
        
        if (StringUtils.isEmpty(userId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "用户标识不能为空!");
        }
        
        if (StringUtils.isEmpty(TradeTypeCode))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "业务编码不能为空!");
        }
        
        IDataset dataLines = new DatasetList();

        if ("7110".equals(TradeTypeCode) || "7220".equals(TradeTypeCode) || "7305".equals(TradeTypeCode))
        {
            dataLines = TradeOtherInfoQry.queryUserDataLineByUserId(inparam);
        }
        else if ("7303".equals(TradeTypeCode) || "7304".equals(TradeTypeCode) || "7301".equals(TradeTypeCode)|| "7317".equals(TradeTypeCode))
        {
            // 7301 交费开机 7303 高额开机 7304 信用特殊开机
            dataLines = TradeOtherInfoQry.queryUserDataLineByUserIdRsrv3(inparam);
        }
        
        if (IDataUtil.isNotEmpty(dataLines))
        {
            for (int i = 0; i < dataLines.size(); i++)
            {
                setAddress(dataLines.getData(i), "A");
                setAddress(dataLines.getData(i), "Z");
            }
        }
        
        return dataLines;
    }
    
    public void setAddress(IData dataLine, String str)
    {
        String address = new StringBuilder(dataLine.getString("PROVINCE_"+ str,"" )).append(dataLine.getString("CITY_" + str,""))
                .append(dataLine.getString("AREA_" + str,"")).append(dataLine.getString("COUNTY_" + str,"")).append(dataLine.getString("VILLAGE_" + str,"")).toString();
        dataLine.put("ADDRESS_"+ str , address);
    }
    
    public IDataset queryTradeLines(IData inparam) throws Exception
    {
    	String mainSN = inparam.getString("SERIAL_NUMBER");
    	if (StringUtils.isEmpty(mainSN))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "产品编码不能为空!");
        }
    	UcaData uca = UcaDataFactory.getNormalUca(mainSN);
        String userId= uca.getUserId(); 
        CustGroupTradeData custGroup=uca.getCustGroup();
        IDataset dataLines = new DatasetList();//GrpUserDatalineEnRouteBean.selectTradeUserDatalineByUserId(userId);
        IDataset dataLinesNew=new DatasetList(); 
        if (IDataUtil.isNotEmpty(dataLines))
        {
        	 for (int i = 0; i < dataLines.size(); i++)
             {
        		 IData dataLine=(IData) dataLines.get(i);
        		 if(dataLine.getString("RSRV_STR3","").equals("0")||dataLine.getString("RSRV_STR3","").equals("1")){
        			 dataLine.put("groupId",custGroup.getGroupId());
        			 dataLine.put("custName",custGroup.getCustName());
        			 dataLinesNew.add(dataLine);
        		 }
             }
        }
        BookTradeQueryDAO bean = new BookTradeQueryDAO();
        IData user=new DataMap();
        user.put("USER_ID", userId);
        IDataset dataLinesUser=bean.getDatalineInfoByUserId(user);
        IData custGroupData=new DataMap();
        custGroupData.put("groupId",custGroup.getGroupId());
        custGroupData.put("custName",custGroup.getCustName());
        IData data=new DataMap();
        data.put("groupInfo", custGroupData);
        data.put("dataLines", dataLinesNew);
        data.put("dataLineUser", dataLinesUser);
        IDataset dataRrturn=new DatasetList(); 
        dataRrturn.add(data);
    	return dataRrturn;
    }
}
