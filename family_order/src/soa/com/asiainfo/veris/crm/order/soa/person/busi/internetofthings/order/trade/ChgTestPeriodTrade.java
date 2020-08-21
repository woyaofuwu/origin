
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.trade;

import java.util.List;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.internetofthings.order.requestdata.ChgTestPeriodReqData;

public class ChgTestPeriodTrade extends BaseTrade implements ITrade
{

    /**
     * 修改普通产品的生效时间。 物联网的普通优惠和服务的生效时间是在测试期产品失效后才能生效
     * 
     * @param btd
     * @throws Exception
     */
    private void changeNormalProductEffectTime(BusiTradeData btd, UcaData uca, String endData) throws Exception
    {
        IDataset discntConfigList = CommparaInfoQry.getCommByParaAttr("CSM", "0731", "9013");
        IDataset svcConfigList = CommparaInfoQry.getCommByParaAttr("CSM", "0731", "9014");
        if (discntConfigList != null)
        {
            for (int i = 0; i < discntConfigList.size(); i++)
            {
                IData discntConfig = discntConfigList.getData(i);
                DiscntTradeData discntData = null;
                // 如果PARA_CODE4配置为1，则该物联网服务和优惠需要在测试期到期后生效
                if ("1".equals(discntConfig.getString("PARA_CODE4")))
                {
                    List<DiscntTradeData> discntDataList = uca.getUserDiscntByDiscntId(discntConfig.getString("PARAM_CODE"));
                    if (discntDataList != null && !discntDataList.isEmpty())
                    {
                        discntData = discntDataList.get(0);
                    }
                    if (discntData != null)
                    {
                        DiscntTradeData newDiscntData = discntData.clone();
                        newDiscntData.setStartDate(SysDateMgr.addSecond(endData, 2));
                        String end = getEndTime(newDiscntData.getProductId(),newDiscntData.getStartDate(),newDiscntData.getElementId(),newDiscntData.getPackageId());
                        newDiscntData.setEndDate(end);
                        newDiscntData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        btd.add(uca.getSerialNumber(), newDiscntData);
                    }
                }

            }
        }

        if (svcConfigList != null)
        {
            for (int j = 0; j < svcConfigList.size(); j++)
            {
                IData svcConfig = svcConfigList.getData(j);
                SvcTradeData svcData = null;
                // 如果PARA_CODE4配置为1，则该物联网服务和优惠需要在测试期到期后生效
                if ("1".equals(svcConfig.getString("PARA_CODE4")))
                {
                    List<SvcTradeData> svcDataList = uca.getUserSvcBySvcId(svcConfig.getString("PARAM_CODE"));
                    if (svcDataList != null && !svcDataList.isEmpty())
                    {
                        svcData = svcDataList.get(0);
                    }
                    if (svcData != null)
                    {
                        SvcTradeData newSvcData = svcData.clone();
                        newSvcData.setStartDate(SysDateMgr.getNextSecond(endData));
                        newSvcData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                        btd.add(uca.getSerialNumber(), newSvcData);
                    }
                }

            }
        }

    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
    	ChgTestPeriodReqData prd = (ChgTestPeriodReqData) btd.getRD();
        UcaData uca = btd.getRD().getUca();
        ProductModuleData discnt = prd.getTestingDiscnt();

        if(discnt != null)
        {
        	if(!"".equals(prd.getTestFlag()) && null != prd.getTestFlag())
        	{
        		
        		List<DiscntTradeData> discntList = uca.getUserDiscntByDiscntId(discnt.getElementId());
                DiscntTradeData discntTradeData = null;
                if (discntList != null && !discntList.isEmpty())
                {
                    discntTradeData = discntList.get(0).clone();
                }
                //获取实际生效时间
                String startDate = discnt.getStartDate();
                String realStartDate = "";
                if(SysDateMgr.compareTo(startDate,SysDateMgr.getSysDate())==0){
                	realStartDate = SysDateMgr.getSysTime();
                }else{
                	realStartDate = startDate+" 00:00:01";
                }
                
                // 生效时间修改
                discntTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                discntTradeData.setStartDate(realStartDate);
                //结束时间修改
                String endDate = getEndTime(discntTradeData.getProductId(),realStartDate,discntTradeData.getElementId(),discntTradeData.getPackageId());
                discntTradeData.setEndDate(endDate);
                btd.add(uca.getSerialNumber(), discntTradeData);
                changeUserAttr(prd, btd,discntTradeData.getInstId(),realStartDate,endDate);
                
                if("0".equals(prd.getTestFlag()))
                {
                	//测试期产品1，3，6月的                    
                    IDataset normalConfigList = CommparaInfoQry.getCommByParaAttr("CSM", "9013", CSBizBean.getUserEparchyCode());
                    
                    if(!normalConfigList.isEmpty()){
                    	for (int i = 0; i < normalConfigList.size(); i++)
                        {
                    		IData config = normalConfigList.getData(i);
                    		if("NB_GPRS".equals(config.getString("PARA_CODE20")) || "NB_SMS".equals(config.getString("PARA_CODE20")))
                    		{
                    			DiscntTradeData discntData = new DiscntTradeData();
                                List<DiscntTradeData> discntLists = uca.getUserDiscntByDiscntId(config.getString("PARAM_CODE"));
                                if (discntLists != null && !discntLists.isEmpty()){
                                	discntData = discntLists.get(0).clone();
                                    // 正常期的开始结束时间修改
                                    discntData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                                    //正常期开始时间为测试期结束时间加2秒
                                    discntData.setStartDate(SysDateMgr.addSecond(endDate, 2));
                                    //计算enddata
                                    String date = getEndTime(discntTradeData.getProductId(),SysDateMgr.addSecond(endDate, 2),discntTradeData.getElementId(),discntTradeData.getPackageId());
                                    discntData.setEndDate(date);

                                    btd.add(uca.getSerialNumber(), discntData);
                                    changeUserAttr(prd, btd,discntData.getInstId(),discntData.getStartDate(),discntData.getEndDate());
                                } 
                    		}
                            
                        }
                    }
                }
                
                if("1".equals(prd.getTestFlag()))
                {
                    
                    IDataset testConfigList = CommparaInfoQry.getCommByParaAttr("CSM", "9013", CSBizBean.getUserEparchyCode());
                    
                    if(!testConfigList.isEmpty()){
                    	for (int i = 0; i < testConfigList.size(); i++)
                        {
                      	    IData config = testConfigList.getData(i);
                      	    if( "NB_SMS".equals(config.getString("PARA_CODE20")))
                      	    {
                      	    	DiscntTradeData discntData = new DiscntTradeData();

                                List<DiscntTradeData> discntLists = uca.getUserDiscntByDiscntId(config.getString("PARAM_CODE"));

                                if (discntLists != null && !discntLists.isEmpty())
                                   {
                                       discntData = discntLists.get(0).clone();
                                       discntData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                                       discntData.setStartDate(discntTradeData.getStartDate());
                                       discntData.setEndDate(discntTradeData.getEndDate());
                                       
                                       btd.add(uca.getSerialNumber(), discntData);
                                       changeUserAttr(prd, btd,discntData.getInstId(),discntData.getStartDate(),discntData.getEndDate());
                                    }
                      	    }
                            
                          }
                       
                    }
                }     
	
//                IDataset svcConfigList = CommparaInfoQry.getCommByParaAttr("CSM", "9115", CSBizBean.getUserEparchyCode());
//                
//                if(!svcConfigList.isEmpty()){
//                	for (int i = 0; i < svcConfigList.size(); i++)
//                    {
//                		IData config = svcConfigList.getData(i);
//            	        SvcTradeData svcData  = new SvcTradeData();
//                    	List<SvcTradeData> servicelists = uca.getUserSvcBySvcId(config.getString("PARAM_CODE"));
//                	    if (servicelists != null && !servicelists.isEmpty()){
//                	    	svcData = servicelists.get(0).clone();
//                            discntTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
//                            if(SysDateMgr.compareTo(svcData.getStartDate(), discnt.getStartDate()) > 0)
//                             {
//                            	svcData.setStartDate(discnt.getStartDate());
//                                btd.add(uca.getSerialNumber(), svcData);
//                                changeUserAttr(prd, btd,svcData.getInstId(),svcData.getStartDate(),svcData.getEndDate());
//                             }
//                	    }
//                    }
//                    
//                  }
                }else{
                	List<DiscntTradeData> discntList = uca.getUserDiscntByDiscntId(discnt.getElementId());
                    DiscntTradeData discntTradeData = null;
                    if (discntList != null && !discntList.isEmpty())
                    {
                        discntTradeData = discntList.get(0).clone();
                    }
                    // 测试期变更，将测试期的结束时间修改
                    discntTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                    discntTradeData.setEndDate(discnt.getEndDate());
                    btd.add(uca.getSerialNumber(), discntTradeData);
                    changeNormalProductEffectTime(btd, uca, discnt.getEndDate());// 修改普通产品的生效时间                	
                }       	
        }
    }
    private String getEndTime(String product_id, String start_date, String element_id,String package_id) throws Exception
    {
    	
    	IData element = new DataMap();
    	element.put("PRODUCT_ID", product_id);   	
    	element.put("START_DATE", start_date);
    	element.put("ELEMENT_ID", element_id);
    	element.put("PACKAGE_ID", package_id);
        DiscntData discntData = new DiscntData(element);
        String endDate = ProductModuleCalDate.calEndDate(discntData, start_date);
        return endDate;
    	
    }
	private void changeUserAttr(ChgTestPeriodReqData rd, BusiTradeData btd,String relaAttrInstId, String startDate, String endDate) throws Exception
    {
    	
		  UcaData uca = btd.getRD().getUca();
		  List<AttrTradeData> attrTDs = uca.getUserAttrsByRelaInstId(relaAttrInstId);
		  if (attrTDs != null && !attrTDs.isEmpty())
	        {
	            for (int i = 0; i < attrTDs.size(); i++)
	            {	            	
	               AttrTradeData attrTd = new AttrTradeData();
                   attrTd = attrTDs.get(i).clone();
                   attrTd.setStartDate(startDate);
                   attrTd.setEndDate(endDate);
                   attrTd.setModifyTag(BofConst.MODIFY_TAG_UPD);
                   btd.add(rd.getUca().getSerialNumber(), attrTd);
	            }
          }
    	
    }
}
