
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.ccsp;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

/**
 * 融合通信一级boss入参转换
 * 
 * @author zhangbo18
 */
public class CcspIbossInFilter implements IFilterIn
{

	@Override
	public void transferDataInput(IData input) throws Exception
	{
		String operCode = input.getString("OPER_CODE");
		String range = input.getString("RANGE");
		
		AttrTradeData userAttr = null;
		UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER", input.getString("ID_VALUE")));
		IData ccspService = ParamInfoQry.getCommparaByCode("CSM", "4602", "CCSPSERVICEID", uca.getUser().getEparchyCode()).getData(0);
		List<PlatSvcTradeData> userPlatSvcList = uca.getUserPlatSvcByServiceId(ccspService.getString("PARA_CODE1"));
        if (userPlatSvcList != null && userPlatSvcList.size() > 0)
        {
            userAttr = uca.getUserAttrsByRelaInstIdAttrCode(userPlatSvcList.get(0).getInstId(), "7901");
        }
        if("01".equals(operCode))  //订购融合通信业务
		{
        	String oper_code = "06";
        	String modify_tag = "0";
            if (userAttr != null)
            {
            	String infoValue = userAttr.getAttrValue();
                if (("02".equals(infoValue) && "03".equals(range)) 
						|| ("03".equals(infoValue) && "02".equals(range))){
                	range = "01";
					oper_code = "08";
					modify_tag = "2";
				}
            }
        	
			input.put("OPER_CODE", oper_code);   //服务订购
			IDataset attrDatas = new DatasetList();
			IData attr = new DataMap();
			attr.put("ATTR_CODE", "7901");
			attr.put("ATTR_VALUE", range);
			attr.put("MODIFY_TAG", modify_tag);
			attrDatas.add(attr);
			input.put("ATTR_PARAM", attrDatas);
			
		}else if ("02".equals(operCode)) {
  			
			if (userAttr != null)
	        {
				String infoValue = userAttr.getAttrValue();
				if(("01").equals(infoValue)&&"02".equals(range)){
					range = "03";
				}else if(("01").equals(infoValue)&&"03".equals(range)){
					range = "02";
				}else if((("02").equals(infoValue)||("03").equals(infoValue))&&!(infoValue.equals(range))){
					 CSAppException.apperr(PlatException.CRM_PLAT_0999_1, "不能退订未订购的业务");
				}
				
				if (infoValue.equals(range)){
					//标识用户主动退订业务
					input.put("OPER_CODE", "07");   //服务退订
					input.put("RSRV_STR4", "1");
				}else {
					input.put("OPER_CODE", "08");
					IDataset attrDatas = new DatasetList();
					IData attr = new DataMap();
					attr.put("ATTR_CODE", "7901");
					attr.put("ATTR_VALUE", range);
					attr.put("MODIFY_TAG", "2");
					attrDatas.add(attr);
					input.put("ATTR_PARAM", attrDatas);
				}
	        }
  		}
	    //设置默认的SP_CODE与BIZ_CODE
		if ("".equals(input.getString("SP_CODE"))){
			input.put("SP_CODE", "790001");
		}
		if ("".equals(input.getString("BIZ_CODE"))){
			input.put("BIZ_CODE", "RHTX");
		}
        //同步操作流水，存储到工单表中
        if (input.getString("OPR_NUMB") != null
				&& !"".equals(input.getString("OPR_NUMB"))) {
        	input.put("INTF_TRADE_ID", input.getString("OPR_NUMB"));
		}
	}

}
