
package com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * @author Administrator
 */
public class AttrTrade
{

    public static void createAttrTradeData(ProductModuleTradeData pmtd, List<AttrData> attrDatas, UcaData uca) throws Exception
    {
        OrderDataBus databus = DataBusManager.getDataBus();
        if (attrDatas == null || attrDatas.size() == 0)
        {
            return;
        }

        List<AttrTradeData> attrTradeDatas = new ArrayList<AttrTradeData>();
        int size = attrDatas.size();
        for (int i = 0; i < size; i++)
        {
            AttrData attrData = attrDatas.get(i);
            if (attrData.getModifyTag().equals(BofConst.MODIFY_TAG_ADD) && StringUtils.isNotBlank(attrData.getAttrValue()))
            {
                AttrTradeData attrTradeData = new AttrTradeData();
                attrTradeData.setAttrCode(attrData.getAttrCode());
                attrTradeData.setAttrValue(attrData.getAttrValue());
                attrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                attrTradeData.setUserId(pmtd.getUserId());
                attrTradeData.setEndDate(pmtd.getEndDate());
                attrTradeData.setStartDate(pmtd.getStartDate());
                attrTradeData.setRelaInstId(pmtd.getInstId());
                attrTradeData.setInstType(pmtd.getElementType());
                attrTradeData.setInstId(SeqMgr.getInstId());
                attrTradeData.setElementId(pmtd.getElementId());
                attrTradeData.setRsrvNum1(pmtd.getElementId());// 海南需求填ELEMENT_ID
                /**
                 * 个人定向语音服务属性特殊处理
                 */
                dealSpecialAttr(attrData,attrTradeData,pmtd,uca);
                
                attrTradeDatas.add(attrTradeData);
            }
            else if (attrData.getModifyTag().equals(BofConst.MODIFY_TAG_UPD))
            {
            	if("userArea".equals(attrData.getAttrCode())||"userWhiteNum".equals(attrData.getAttrCode())){
					
					AttrTradeData userattr = uca.getUserAttrsByRelaInstIdAttrCodeAttrValue(pmtd.getInstId(), attrData.getAttrCode(), attrData.getAttrValue());;
					
					AttrTradeData delAttrTradeData = userattr.clone();
					delAttrTradeData.setEndDate(SysDateMgr.getLastSecond(databus.getAcceptTime()));
					
					delAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
					delAttrTradeData.setRemark("WLW_SVC_ATTR");
					if("userArea".equals(attrData.getAttrCode())){
						delAttrTradeData.setRsrvStr2("userArea_DEL");
					}else{
						delAttrTradeData.setRsrvStr2("userWhiteNum_DEL");
					}
					attrTradeDatas.add(delAttrTradeData);
						
				}else{
					AttrTradeData userAttrTradeData = uca.getUserAttrsByRelaInstIdAttrCode(pmtd.getInstId(), attrData.getAttrCode());
	                if (userAttrTradeData != null && userAttrTradeData.getAttrValue() != null && userAttrTradeData.getAttrValue().equals(attrData.getAttrValue()))
	                {
	                    // 当请求值与原数据一致时不处理该属性
	                    continue;
	                }

	                // attr要拆成一条新增一条删除
	                if (StringUtils.isNotBlank(attrData.getAttrValue()))
	                {
	                    AttrTradeData addAttrTradeData = new AttrTradeData();
	                    addAttrTradeData.setAttrCode(attrData.getAttrCode());
	                    addAttrTradeData.setAttrValue(attrData.getAttrValue());
	                    addAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
	                    addAttrTradeData.setUserId(pmtd.getUserId());
	                    addAttrTradeData.setEndDate(pmtd.getEndDate());
	                    addAttrTradeData.setStartDate(databus.getAcceptTime());
	                    addAttrTradeData.setRelaInstId(pmtd.getInstId());
	                    addAttrTradeData.setInstType(pmtd.getElementType());
	                    addAttrTradeData.setInstId(SeqMgr.getInstId());
	                    addAttrTradeData.setElementId(pmtd.getElementId());
	                    addAttrTradeData.setRsrvNum1(pmtd.getElementId());
	                    
	                    dealSpecialAttr(attrData,addAttrTradeData,pmtd,uca);
	                    
	                    attrTradeDatas.add(addAttrTradeData);
	                }

	                if (userAttrTradeData != null)
	                {
	                    AttrTradeData delAttrTradeData = userAttrTradeData.clone();
	                    delAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
	                    delAttrTradeData.setEndDate(SysDateMgr.getLastSecond(databus.getAcceptTime()));
	                    
	                    dealSpecialAttr(attrData,delAttrTradeData,pmtd,uca);
	                    
	                    attrTradeDatas.add(delAttrTradeData);
	                }
				}
                
            }
            else if (attrData.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
            {
            	
            	if("userArea".equals(attrData.getAttrCode())||"userWhiteNum".equals(attrData.getAttrCode())){
            		AttrTradeData userattr = uca.getUserAttrsByRelaInstIdAttrCodeAttrValue(pmtd.getInstId(), attrData.getAttrCode(), attrData.getAttrValue());
            		
            		AttrTradeData delAttrTradeData = userattr.clone();
					delAttrTradeData.setEndDate(SysDateMgr.getLastSecond(databus.getAcceptTime()));
					
					delAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
					delAttrTradeData.setRemark("WLW_SVC_ATTR");
					if("userArea".equals(attrData.getAttrCode())){
						delAttrTradeData.setRsrvStr2("userArea_DEL");
					}else{
						delAttrTradeData.setRsrvStr2("userWhiteNum_DEL");
					}
					attrTradeDatas.add(delAttrTradeData);
            		
            	}else{
            		AttrTradeData delAttrTradeData = uca.getUserAttrsByRelaInstIdAttrCode(pmtd.getInstId(), attrData.getAttrCode());
                    if (delAttrTradeData != null)
                    {
                        delAttrTradeData = delAttrTradeData.clone();
                        delAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        delAttrTradeData.setEndDate(SysDateMgr.getLastSecond(databus.getAcceptTime()));
                        /**
                         * 定向语音服务属性特殊处理
                         */
                        dealSpecialAttr(attrData,delAttrTradeData,pmtd,uca);
                        
                        attrTradeDatas.add(delAttrTradeData);
                    }
            	}
            }
        }
        pmtd.setAttrTradeDatas(attrTradeDatas);
    }
    
    private static void dealSpecialAttr(AttrData attrData,
			AttrTradeData attrTradeData,ProductModuleTradeData pmtd,UcaData uca) throws Exception{
		// TODO Auto-generated method stub
		
    	IDataset service = CommparaInfoQry.getCommNetInfo("CSM", "9014", "99011019");
		
		String service_id = service.getData(0).getString("PARAM_CODE");
    	
		if(!service_id.equals(pmtd.getElementId())){
			return;
		}
		
		if(BofConst.MODIFY_TAG_ADD.equals(attrTradeData.getModifyTag())){
			if("userWhiteNum".equals(attrData.getAttrCode())){
				attrTradeData.setRsrvStr2("userWhiteNum_ADD");
			}else if("userArea".equals(attrData.getAttrCode())){
				attrTradeData.setRsrvStr2("userArea_ADD");
			}else if("userWhiteNumOperType".equals(attrData.getAttrCode())){
				attrTradeData.setRsrvStr2("userWhiteNum_ADD");
			}else if("userAreaOperType".equals(attrData.getAttrCode())){
				attrTradeData.setRsrvStr2("userArea_ADD");
			}
		}else if(BofConst.MODIFY_TAG_DEL.equals(attrTradeData.getModifyTag())){
			
			if("userWhiteNum".equals(attrData.getAttrCode())){
				attrTradeData.setRsrvStr2("userWhiteNum_DEL");
			}else if("userArea".equals(attrData.getAttrCode())){
				attrTradeData.setRsrvStr2("userArea_DEL");
			}else if("userWhiteNumOperType".equals(attrData.getAttrCode())){
				attrTradeData.setRsrvStr2("userWhiteNum_DEL");
			}else if("userAreaOperType".equals(attrData.getAttrCode())){
				attrTradeData.setRsrvStr2("userArea_DEL");
			}
			
		}
		
		attrTradeData.setRemark("WLW_SVC_ATTR");
		
	}
}
