package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;

public class CheckProductWorkPhoneDiscnt  extends BreBase implements IBREScript
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CheckProductWorkPhoneDiscnt.class);
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
    	System.out.println("20180730CheckProductWorkPhoneDiscnt ");
    	
    	
    	String xChoiceTag = databus.getString("X_CHOICE_TAG");

        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("1", xChoiceTag))// 提交时校验，依赖请求数据
        {
            IData reqData = databus.getData("REQDATA");// 请求的数据

            if (IDataUtil.isNotEmpty(reqData))
            {
                String userProductId = databus.getString("PRODUCT_ID");// 老产品
                String newProductId = reqData.getString("NEW_PRODUCT_ID");// 新产品
                logger.info("CheckProductWorkPhoneDiscnt userProductId:"+userProductId+" newProductId"+newProductId);
                if (StringUtils.isNotBlank(newProductId) && !userProductId.equals(newProductId))//主产品变更
                {
                	UcaData uca = (UcaData) databus.get("UCADATA");
		        	 List<DiscntTradeData> userDiscnts =uca.getUserDiscnts();
		        	 if(userDiscnts != null && userDiscnts.size() > 0)
		             {
		        		 logger.info("CheckProductWorkPhoneDiscnt userDiscnts:"+userDiscnts);
		        		 IDataset paramCommpara=ParamInfoQry.getCommparaByParamattr("CSM","6013","GPWP","0898");
		        		 logger.info("CheckProductWorkPhoneDiscnt paramCommpara:"+paramCommpara);
		        		 for(DiscntTradeData userDiscnt : userDiscnts)
		                 {
		        			 String discntCode=userDiscnt.getDiscntCode();
							 if(paramCommpara!=null){
								for(int i =0;i<paramCommpara.size();i++){
									IData result=paramCommpara.getData(i);
									if(result.getString("PARA_CODE1", "").equals(discntCode)){
										String endDateString=userDiscnt.getEndDate();
										Date endDate = SysDateMgr.string2Date(endDateString, SysDateMgr.PATTERN_STAND);
								        String lastDateString=SysDateMgr.getLastDateThisMonth();//本月最后一天最后时间
								        Date lastDate = SysDateMgr.string2Date(lastDateString, SysDateMgr.PATTERN_STAND);
								        System.out.println("20180730CheckProductWorkPhoneDiscnt endDateString"+endDateString+" lastDateString:"+lastDateString);
								        
								        
								        String newStartDateString=reqData.getString("BOOKING_DATE","");//预约套餐开始时间 
								        String newStartlastDateString=SysDateMgr.getAddMonthsLastDay(1,newStartDateString);//预约套餐开始时间月底最后一天，如果不预约则为本月最后一天
								        Date newStartlastDate = SysDateMgr.string2Date(newStartlastDateString, SysDateMgr.PATTERN_STAND);
								        //优惠当前时间>lastDate本月最后一秒      优惠当前时间>预约套餐当月最后一秒
								        if(endDate.after(lastDate)&&endDate.after(newStartlastDate)){
								        	List<AttrTradeData> userAttrs=uca.getUserAttrs();
								        	if(userAttrs != null && userAttrs.size() > 0)
								            {
								        		for(AttrTradeData userAttr : userAttrs)
								                {
									        		if(userDiscnt.getInstId().equals(userAttr.getRelaInstId())&&userAttr.getAttrCode().equals("114485")&&!userAttr.getAttrValue().equals("100")){
									        			IData iData = new DataMap();
											        	IDataset offer_type_code_list = new DatasetList();
											            iData.put("OFFER_CODE", discntCode);
											            iData.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
											            offer_type_code_list.add(iData);    	
											            IData input = new DataMap();
											    		input.put("OFFER_TYPE_CODE_LIST", offer_type_code_list);
											    		ServiceResponse response =BizServiceFactory.call("UPC.Out.OfferQueryFSV.qryOfferNamesByOfferTypesOfferCodes", input, null);
											            IData out = response.getBody();
											            System.out.println("20180717ff1 out:"+out);
											            IDataset outList=out.getDataset("OUTDATA");
											            String discntCodeName="相关打折优惠";
											            if(!IDataUtil.isEmpty(outList)){
											            	discntCodeName=outList.getData(0).getString("OFFER_NAME","");
											            }
											        	
											        	String errorMsg = "该用户存在["+discntCodeName+"],故主套餐【"+UProductInfoQry.getProductNameByProductId(userProductId)+"】不能更改成【"+UProductInfoQry.getProductNameByProductId(newProductId)+"】!";
										                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201807301500001", errorMsg);
										                return true;
									        		}
								                }
								            }
								        }
									}
								}
							 }
		                 }
		             }
                }
    	
                
            }
        }
		return false;
    }
}
