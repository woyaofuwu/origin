
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.build;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.trans.AttrTrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.trans.OperCodeTrans;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.PlatReload;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

public class BuildPlatBatchIntf extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        PlatReqData req = (PlatReqData) brd;
        List<PlatSvcData> platSvcDatas = new ArrayList<PlatSvcData>();
        String bizTypeCode = param.getString("BIZ_TYPE_CODE");
        String serviceId = param.getString("SERVICE_ID");
        String strOperCode = param.getString("OPER_CODE");

        // 特殊处理，数组形式的转换成DataMap丢失”,所以特殊形式保存；对实体卡订购时，要先退订不是用实体卡订购的，再订购的处理
        IDataset serviceDataset = new DatasetList();
        IDataset operCodeDataset = new DatasetList();
        if (serviceId != null && serviceId.startsWith("@_@"))
        {
            String[] serviceIdArray = serviceId.substring(3).split("@_@");
            String[] operCodeArray = strOperCode.substring(3).split("@_@");
            for (int i = 0; i < serviceIdArray.length; i++)
            {
                serviceDataset.add(serviceIdArray[i]);
                operCodeDataset.add(operCodeArray[i]);
            }

            serviceId = serviceDataset.toString();
            strOperCode = operCodeDataset.toString();
        }

        if (serviceId != null && serviceId.startsWith("["))
        {
            JSONArray services = JSONArray.fromObject(serviceId);
            JSONArray operCodes = JSONArray.fromObject(strOperCode); // 操作编码
            JSONArray startDates = JSONArray.fromObject(param.getString("START_DATE")); // 开始日期
            JSONArray infoCodes = null;
            JSONArray infoValues = null;
            if (!StringUtils.isBlank(param.getString("INFO_CODE")) && !StringUtils.isBlank(param.getString("INFO_VALUE")))
            {
                infoCodes = JSONArray.fromObject(param.getString("INFO_CODE")); // 属性
                infoValues = JSONArray.fromObject(param.getString("INFO_CODE")); // 属性值
            }

            int length = services.size();
            for (int i = 0; i < length; i++)
            {
                IData singleParam = new DataMap();
                Object operCode = operCodes.get(i);
                Object startDate = null;

                if (StringUtils.isNotBlank(param.getString("START_DATE")))
                {
                    startDate = startDates.getString(i);
                }

                if (infoCodes != null && infoValues != null)
                {
                    JSONArray infoCode = JSONArray.fromObject(infoCodes.get(i));
                    JSONArray infoValue = JSONArray.fromObject(infoValues.get(i));
                    if (infoValue.size() > 0)
                    {
                        singleParam.put("INFO_VALUE", infoValue.toString());
                    }
                    if (infoCode.size() > 0)
                    {
                        singleParam.put("INFO_CODE", infoCode.toString());
                    }
                }

                singleParam.put("SERVICE_ID", services.get(i));
                singleParam.put("OPER_CODE", operCode.toString());
                singleParam.put("OPR_SOURCE", param.getString("OPR_SOURCE", "08"));
                if (operCode.equals(PlatConstants.OPER_ORDER))
                {
                    singleParam.put("ENTITY_CARD_NO", param.getString("ENTITY_CARD_NO"));
                }

                if (startDate != null && startDate.toString().length() > 0)
                {
                    singleParam.put("START_DATE", startDate.toString());
                }

                platSvcDatas.add(constructRequestData(singleParam, brd));
            }
        }
        else if (bizTypeCode != null && bizTypeCode.startsWith("["))
        {

            // 如果是多条，拆分成单条处理
            JSONArray bizTypes = JSONArray.fromObject(param.getString("BIZ_TYPE_CODE"));
            JSONArray operCodes = JSONArray.fromObject(strOperCode); // 操作编码
            JSONArray spCodes = JSONArray.fromObject(param.getString("SP_CODE")); // sp服务商编码
            JSONArray bizCodes = JSONArray.fromObject(param.getString("BIZ_CODE"));// 业务代码
            JSONArray startDates = JSONArray.fromObject(param.getString("START_DATE")); // 开始日期
            JSONArray infoCodes = null;
            JSONArray infoValues = null;
            if (!StringUtils.isBlank(param.getString("INFO_CODE")) && !StringUtils.isBlank(param.getString("INFO_VALUE")))
            {
                infoCodes = JSONArray.fromObject(param.getString("INFO_CODE")); // 属性
                infoValues = JSONArray.fromObject(param.getString("INFO_CODE")); // 属性值
            }

            int length = bizTypes.size();
            for (int i = 0; i < length; i++)
            {
                IData singleParam = new DataMap();
                Object bizType = bizTypes.get(i);
                Object operCode = operCodes.get(i);
                Object spCode = spCodes.get(i);
                Object bizCode = bizCodes.get(i);
                Object startDate = startDates.get(i);

                if (infoCodes != null && infoValues != null)
                {
                    JSONArray infoCode = JSONArray.fromObject(infoCodes.get(i));
                    JSONArray infoValue = JSONArray.fromObject(infoValues.get(i));
                    if (infoValue.size() > 0)
                    {
                        singleParam.put("INFO_VALUE", infoValue.toString());
                    }
                    if (infoCode.size() > 0)
                    {
                        singleParam.put("INFO_CODE", infoCode.toString());
                    }
                }

                singleParam.put("BIZ_TYPE_CODE", bizType.toString());
                singleParam.put("OPER_CODE", operCode.toString());
                if (spCode != null && spCode.toString().length() > 0)
                {
                    singleParam.put("SP_CODE", spCode.toString());
                }
                if (bizCode != null && bizCode.toString().length() > 0)
                {
                    singleParam.put("BIZ_CODE", bizCode.toString());
                }
                if (startDate != null && startDate.toString().length() > 0)
                {
                    singleParam.put("START_DATE", startDate.toString());
                }

                // 这个处理需要？
                if ((bizType.toString()).equals("16") || bizType.equals("23"))
                {
                    singleParam.put("OPR_SOURCE", "11");
                }
                else
                {
                    singleParam.put("OPR_SOURCE", param.getString("OPR_SOURCE", "08"));
                }

                platSvcDatas.add(constructRequestData(singleParam, brd));
            }

        }
        else
        {
            // 如果是单条
            platSvcDatas.add(constructRequestData(param, brd));

        }
        req.setPlatSvcDatas(platSvcDatas);

    }

    private PlatSvcData constructRequestData(IData param, BaseReqData brd) throws Exception
    {
        UcaData uca = brd.getUca();
        OperCodeTrans.operCodeTrans(param);
        IDataset attrParams = param.getDataset("ATTR_PARAM");
        if (IDataUtil.isEmpty(attrParams))
        {
            AttrTrans.trans(param);
        }
		PlatSvcData psd = new PlatSvcData(param);
		if ("02".equals(psd.getOfficeData().getBizTypeCode())
				|| "93".equals(psd.getOfficeData().getBizTypeCode())) {
			if (PlatConstants.OPER_ORDER_TC.equals(psd.getOperCode())) {
				if (uca.getUserPlatSvcByServiceId(psd.getElementId()).size() <= 0) {
					psd.setOperCode(PlatConstants.OPER_ORDER);
					psd.setModifyTag(BofConst.MODIFY_TAG_ADD);
				}
			}
		} else if ("16".equals(psd.getOfficeData().getBizTypeCode())) {
			if (uca.getUserPlatSvcByServiceId(PlatReload.mail139Free).size() >= 0) { 
				if("07".equals( param.getString("OPER_CODE")))
				{
					   psd.setModifyTag(BofConst.MODIFY_TAG_DEL);
				       psd.setOperCode(PlatConstants.OPER_CANCEL_ORDER); 
				}
				else   
				 {
					   psd.setModifyTag(BofConst.MODIFY_TAG_ADD); 
				       psd.setOperCode(PlatConstants.OPER_ORDER); 
				 }
			}
		}
        return psd;
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new PlatReqData();
    }

}
