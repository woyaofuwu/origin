
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.buildrequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

public class BuildPlat extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData orderParam, BaseReqData brd) throws Exception
    {
        PlatReqData prd = (PlatReqData) brd;
        IDataset selectedElements = null;
        if (!orderParam.getString("SELECTED_ELEMENTS", "").equals(""))
        {
            selectedElements = new DatasetList(orderParam.getString("SELECTED_ELEMENTS"));
        }

        List<PlatSvcData> platSvcs = new ArrayList<PlatSvcData>();
        UcaData uca = brd.getUca();
        if (selectedElements != null && selectedElements.size() > 0)
        {
            int size = selectedElements.size();
            for (int i = 0; i < size; i++)
            {
                IData element = selectedElements.getData(i);
                String operCode = element.getString("OPER_CODE");
                if (BofConst.MODIFY_TAG_UPD.equals(element.getString("MODIFY_TAG")))
                {
                  
                    if (PlatConstants.OPER_PAUSE.equals(operCode) || PlatConstants.OPER_RESTORE.equals(operCode) || PlatConstants.OPER_LOSE.equals(operCode) || PlatConstants.OPER_UNLOSE.equals(operCode)
                            || PlatConstants.OPER_CANCEL_ORDER.equals(operCode))
                    {
                        element.remove("ATTR_PARAM");
                    }

                }
                
                //和校园与和教育业务共用一套局数据，需依据入口渠道进行biz_code进行转换
                if ("80".equals(element.getString("BIZ_TYPE_CODE"))){
            		String opr_source = element.getString("OPR_SOURCE");
            		String biz_code = element.getString("BIZ_CODE");
            		element.put("RSRV_STR8", biz_code);
                	IDataset sourceData = ParamInfoQry.getCommparaByCode("CSM", "8002", "CHECKSOURCE", uca.getUser().getEparchyCode());
            		//判断当前渠道是否需要转换
            		boolean flag = false;
            		if (null != sourceData){
            			if (StringUtils.isNotEmpty(opr_source)){
        					IData source = sourceData.getData(0);
        					if (null != source && null != source.getString("PARA_CODE1")){
        						String str = source.getString("PARA_CODE1");
        						String[] ss = str.split(",");
        						for (String string : ss) {
        							if (opr_source.equals(string)){
        								flag = true;
        								break;
        							}
        						}
            				}
            			}
            		}
            		//如需转换，则获取当前biz_code对应的新的BIZ_CODE记录
            		String service_id = element.getString("SERVICE_ID",element.getString("ELEMENT_ID"));
            		if (flag){
            			IDataset bizData = ParamInfoQry.getCommparaByCode("CSM", "8002", biz_code, uca.getUser().getEparchyCode());
            			if (null != bizData && bizData.size() > 0){
            				biz_code = bizData.getData(0).getString("PARA_CODE1");
            				service_id = bizData.getData(0).getString("PARA_CODE2");
            			}
            		}
            		//重新设置BIZ_CODE
            		element.put("BIZ_CODE", biz_code);
            		element.put("SERVICE_ID", service_id);
            		element.put("ELEMENT_ID", service_id);
                }
                
                //校园wlan 套餐退订作 业务退订处理
                if("98009201".equals(element.getString("SERVICE_ID")) && PlatConstants.OPER_CANCEL_TC.equals(operCode))
                {
                	element.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
                }
				boolean isQqt = false; // 判断是否是亲情通业务
				if ((!"".equals(element.getString("SERVICE_ID")))
						&& "-99962184-99962185-99962186-".contains(element
								.getString("SERVICE_ID"))) {
					if (PlatConstants.OPER_ORDER.equals(operCode)) {
						
						operCode = "11";
					}
					if(PlatConstants.OPER_ORDER_PLAY.equals(operCode))
					{
						operCode = "04";
						element.put("OPER_CODE", operCode);
						
					}
					if(PlatConstants.OPER_BIND.equals(operCode))
					{
						operCode = "05";
						element.put("OPER_CODE", operCode);
						
					}
					
					isQqt = true;
				}
                
                
                
                IDataset attrParams = element.getDataset("ATTR_PARAM");
                // 过滤不需要的属性信息
                if (IDataUtil.isNotEmpty(attrParams))
                {
                    IDataset attrItems = AttrItemInfoQry.getElementItemA4Plat(element.getString("SERVICE_ID"), BizRoute.getRouteId());
                    if (IDataUtil.isNotEmpty(attrItems))
                    {
                        IDataset attrParamsTemp = new DatasetList();
                        int aSize = attrParams.size();
                        for (int j = 0; j < aSize; j++)
                        {
                            IData attrParam = attrParams.getData(j);
                            int iSize = attrItems.size();
                            for (int k = 0; k < iSize; k++)
                            {
                                IData attrItem = attrItems.getData(k);
                                if (attrItem.getString("ATTR_CODE").equals(attrParam.getString("ATTR_CODE")))
                                {
                                	if(isQqt)
                                	{
                                		element.put("GIFT_SERIAL_NUMBER",attrParam.getString("ATTR_VALUE"));
                                		
                                	}
                                	
                                    if (attrItem.getString("RSRV_STR2", "").indexOf(element.getString("OPER_CODE")) >= 0)
                                    {
                                        // 不为空的属性才提交
                                        if (attrParam.getString("ATTR_VALUE") != null && !"".equals(attrParam.getString("ATTR_VALUE")))
                                        {
                                            attrParamsTemp.add(attrParam);
                                        }

                                    }
                                    break;
                                }
                            }
                        }
                        element.put("ATTR_PARAM", attrParamsTemp);
                    }
                }
                PlatSvcData platSvcData = new PlatSvcData(element);
                platSvcs.add(platSvcData);
            }
        }

        // 全退订
        if (!orderParam.getString("ALL_CANCEL", "").equals(""))
        {
            IData allCancel = new DataMap(orderParam.getString("ALL_CANCEL"));
            if (IDataUtil.isNotEmpty(allCancel))
            {
                List<String> allCancels = new ArrayList<String>();
                Iterator lter = allCancel.keySet().iterator();
                while (lter.hasNext())
                {
                    String key = (String) lter.next();
                    if ("SP".equals(key))
                    {
                        allCancels.add(key + ":" + allCancel.getString(key));
                    }
                    else
                    {
                        allCancels.add(allCancel.getString(key));
                    }
                }
                prd.setAllPlatCancels(allCancels);
            }
        }
        // 开关
        if (StringUtils.isNotBlank(orderParam.getString("ALL_SWITCH")))
        {
            IDataset switches = new DatasetList(orderParam.getString("ALL_SWITCH"));
            // IDataset tempSwitches = new DatasetList();
            int size = switches.size();
            // 先看是否有总开关
            // boolean hasAllSwitch = false;
            // String operCode = "";
            // for (int i = 0; i < size; i++)
            // {
            // IData platSwitch = switches.getData(i);
            // if ("98009044".equals(platSwitch.getString("SERVICE_ID")))
            // {
            // hasAllSwitch = true;
            // operCode = platSwitch.getString("OPER_CODE");
            // break;
            // }
            // }
            // if (hasAllSwitch)
            // {
            // tempSwitches = StaticUtil.getStaticList("PLAT_SWITCH");
            // size = switches.size();
            for (int i = 0; i < size; i++)
            {
                IData platSwitch = switches.getData(i);
                String operCode = platSwitch.getString("OPER_CODE");
                IData temp = new DataMap();
                temp.put("SERVICE_ID", platSwitch.getString("SERVICE_ID"));
                temp.put("OPER_CODE", operCode);
                PlatSvcData platSvcSwitch = new PlatSvcData(temp);
                if (PlatConstants.OPER_SERVICE_CLOSE.equals(operCode))
                {
                    platSvcSwitch.setModifyTag(BofConst.MODIFY_TAG_ADD);
                }
                else
                {
                    platSvcSwitch.setModifyTag(BofConst.MODIFY_TAG_DEL);
                }
                platSvcs.add(platSvcSwitch);
            }
            // }
            // else
            // {
            // tempSwitches = switches;
            // for (int i = 0; i < size; i++)
            // {
            // IData temp = tempSwitches.getData(i);
            // PlatSvcData platSvcSwitch = new PlatSvcData(temp);
            // if (PlatConstants.OPER_SERVICE_CLOSE.equals(platSvcSwitch.getOperCode()))
            // {
            // platSvcSwitch.setModifyTag(BofConst.MODIFY_TAG_ADD);
            // }
            // else
            // {
            // platSvcSwitch.setModifyTag(BofConst.MODIFY_TAG_DEL);
            // }
            // platSvcs.add(platSvcSwitch);
            // }
            // }
        }
        prd.setPlatSvcDatas(platSvcs);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new PlatReqData();
    }
}
