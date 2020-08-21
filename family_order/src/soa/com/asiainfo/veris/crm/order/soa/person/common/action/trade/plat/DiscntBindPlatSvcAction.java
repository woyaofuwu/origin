
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;

/**
 * 修改了老代码的逻辑
 * 原来部分品牌需要默认绑定139邮箱；现在不在绑定，统一由默认开通互联网服务时处理
 * @author xiekl
 *
 */
public class DiscntBindPlatSvcAction implements ITradeAction
{

     
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();

        List<DiscntTradeData> tradeDiscnts = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        if (tradeDiscnts == null || tradeDiscnts.size() <= 0)
        {
            return;
        }


        int size = tradeDiscnts.size();
        for (int i = 0; i < size; i++)
        {
            List<ProductModuleData> bindPlatSvcs = new ArrayList<ProductModuleData>();
            DiscntTradeData tradeDiscnt = tradeDiscnts.get(i);
            String modifyTag = tradeDiscnt.getModifyTag();
            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                // 查询优惠绑平台服务的配置
                IDataset bindPlatSvcConfigs = CommparaInfoQry.getDiscntBindPlatSvc(btd.getTradeTypeCode(), tradeDiscnt.getElementId(), modifyTag, uca.getUserEparchyCode());

                if (IDataUtil.isEmpty(bindPlatSvcConfigs))
                {
                    continue;
                }

                int bindSize = bindPlatSvcConfigs.size();
                for (int j = 0; j < bindSize; j++)
                {
                    IData bindConfig = bindPlatSvcConfigs.getData(j);
                    String bindServiceId = bindConfig.getString("PARA_CODE1");
                    String attrValue = bindConfig.getString("PARA_CODE3");
                    String dateFlag = bindConfig.getString("PARA_CODE6","");//特殊时间标志：1:立即生效
                    // PARA_CODE4为1表示需要判断客户梦网总开关状态和gprs服务，如果没有开通则不自动绑定开通相应的平台服务
                    if ("1".equals(bindConfig.getString("PARA_CODE4")))
                    {
                        // 梦网开关关，不开通
                        List<PlatSvcTradeData> dreamNetSwitchList = uca.getUserPlatSvcByServiceId("98009044");
                        if (dreamNetSwitchList.size() > 0)
                        {
                            PlatSvcTradeData dreamNetSwitch = dreamNetSwitchList.get(0);
                            if (!BofConst.MODIFY_TAG_DEL.equals(dreamNetSwitch.getModifyTag()))
                            {
                                continue;
                            }
                        }

                        // 彩铃分开关关，不开通
                        List<PlatSvcTradeData> colorRingSwitchList = uca.getUserPlatSvcByServiceId("98008005");
                        if (colorRingSwitchList.size() > 0)
                        {
                            PlatSvcTradeData colorRingSwitch = colorRingSwitchList.get(0);
                            if (!BofConst.MODIFY_TAG_DEL.equals(colorRingSwitch.getModifyTag()))
                            {
                                continue;
                            }
                        }

                        // GPRS未开通，不开通平台服务
                        List<SvcTradeData> gprsTradeList = uca.getUserSvcBySvcId("22");
                        SvcTradeData gprsTrade = null;
                        if (gprsTradeList.size() > 0)
                        {
                            gprsTrade = gprsTradeList.get(0);
                        }
                        if (gprsTrade == null)
                        {
                            continue;
                        }
                        else
                        {
                            if (gprsTrade.getModifyTag().equals(BofConst.MODIFY_TAG_DEL))
                            {
                                continue;
                            }
                        }
                    }

                    if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                    {
                        
                    	//add by zhangxing3 for “咪咕流量福包”活动开发需求 start
                    	//1.咪咕福包首免版
                        if ("84009642".equals(tradeDiscnt.getElementId()) || "84009643".equals(tradeDiscnt.getElementId())
                        		|| "84009644".equals(tradeDiscnt.getElementId()) || "84009645".equals(tradeDiscnt.getElementId()))
                        {
                        	
                        	//1.三个月未办理过相应咪咕业务的新用户,赠送1个月免费体验
                        	String freeStartDate = tradeDiscnt.getStartDate();
                        	String freeEndDate = SysDateMgr.getAddMonthsLastDay(1, freeStartDate);
                        	String startDate = tradeDiscnt.getStartDate();
                        	String endDate = "";
                        	String endDateDXLL = "";
                        	String curDay=SysDateMgr.getCurDay();
                        	int curDate = Integer.parseInt(curDay);
            				if(curDate>=20){
            					freeEndDate = SysDateMgr.getAddMonthsLastDay(2, freeStartDate);
            				}
            				IDataset ids = UserPlatSvcInfoQry.qryUserPlatByUserServiceIdMonthNew(btd.getRD().getUca().getUserId(),bindServiceId,"3");
                        	if(ids == null || ids.size() <= 0)
                        	{
                        		//1.1新用户,赠送1个月免费体验
                        		DiscntTradeData newDiscnt1 = new DiscntTradeData();
                        		
                        		newDiscnt1.setUserId(btd.getRD().getUca().getUserId());
                        		newDiscnt1.setUserIdA("-1");
                        		newDiscnt1.setProductId("-1");
                        		newDiscnt1.setPackageId("-1");
                        		if("84009642".equals(tradeDiscnt.getElementId()))
                        		{
                            		newDiscnt1.setElementId("84009838");                    

                        		}
                        		else if("84009643".equals(tradeDiscnt.getElementId()))
                        		{
                            		newDiscnt1.setElementId("84009839");                    

                        		}
                        		else if("84009644".equals(tradeDiscnt.getElementId()))
                        		{
                            		newDiscnt1.setElementId("84009840");                    

                        		}
                        		else if("84009645".equals(tradeDiscnt.getElementId()))
                        		{
                            		newDiscnt1.setElementId("84009841");                    
                        		}
                        		else
                        		{
                        			CSAppException.apperr(CrmUserException.CRM_USER_783,"无法匹配到该首免版咪咕福包对应的首免套餐！");
                        		}
                        		newDiscnt1.setInstId(SeqMgr.getInstId());
                        		newDiscnt1.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        		newDiscnt1.setSpecTag("0");
                        		newDiscnt1.setStartDate(freeStartDate);
                        		newDiscnt1.setEndDate(freeEndDate);
                        		newDiscnt1.setRsrvStr5(tradeDiscnt.getElementId());

                        		newDiscnt1.setRemark("“咪咕流量福包”活动，绑定首免套餐！");				
                				
                				btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt1);
                				
                				//startDate = SysDateMgr.getDateNextMonthFirstDay(freeEndDate);
                            	endDate = SysDateMgr.getAddMonthsLastDay(4, freeEndDate);
                            	endDateDXLL  = SysDateMgr.getAddMonthsLastDay(13, freeEndDate);
                            	//咪咕流量福包优惠，设置RSRV_TAG=1标识新用户，后边打印免填单、发送短信时使用。
                            	tradeDiscnt.setRsrvTag1("1");
                            	//咪咕流量福包优惠，设置RSRV_DATE1为受理时间，设置RSRV_DATE2为首免结束时间，后边打印免填单、发送短信时使用。
                            	tradeDiscnt.setRsrvStr3(SysDateMgr.getChinaDate(btd.getRD().getAcceptTime(), SysDateMgr.PATTERN_CHINA_DATE));
                            	tradeDiscnt.setRsrvStr4(SysDateMgr.getChinaDate(freeEndDate, SysDateMgr.PATTERN_CHINA_DATE));
                        	}
                        	//1.2如果是老用户，不绑定首免套餐.
                        	else
                        	{
    			        		CSAppException.apperr(CrmUserException.CRM_USER_783,"老用户不能办理首免版咪咕福包！");
                        	}
            				
            				
                        	//2.修改福包优惠对应结束时间。
                        	tradeDiscnt.setEndDate(endDate);
                        	tradeDiscnt.setRsrvStr5(tradeDiscnt.getElementId());

                    		//4.给用户绑定咪咕定向流量
            				DiscntTradeData newDiscnt = new DiscntTradeData();
            				
                            newDiscnt.setUserId(btd.getRD().getUca().getUserId());
                            newDiscnt.setUserIdA("-1");
                            newDiscnt.setProductId("-1");
                            newDiscnt.setPackageId("-1");
                            if( "84009642".equals(tradeDiscnt.getElementId()) || "84009643".equals(tradeDiscnt.getElementId()))
                            {
                            	newDiscnt.setElementId("84008844");
                            }
                            else{
                            	newDiscnt.setElementId("84008845");
                            }
                            newDiscnt.setInstId(SeqMgr.getInstId());
                            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                            newDiscnt.setSpecTag("0");
                            newDiscnt.setStartDate(freeStartDate);
                            newDiscnt.setEndDate( endDateDXLL );
                            newDiscnt.setRsrvStr5(tradeDiscnt.getElementId());
                            newDiscnt.setRemark("“咪咕流量福包”活动，绑定定向流量套餐！");				
            				
            				btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);
                        }
                        //2.咪咕福包普通版
                        if ("84008840".equals(tradeDiscnt.getElementId()) || "84008841".equals(tradeDiscnt.getElementId())
                        		|| "84008842".equals(tradeDiscnt.getElementId()) || "84008843".equals(tradeDiscnt.getElementId()))
                        {                    	
                        	String startDate = tradeDiscnt.getStartDate();
                        	String endDate = "";
                        	String endDateDXLL = "";
                        	
                    		//startDate = tradeDiscnt.getStartDate();
                        	endDate = SysDateMgr.getAddMonthsLastDay(3, startDate);
                        	endDateDXLL  = SysDateMgr.getAddMonthsLastDay(12, startDate);

                        	//咪咕流量福包优惠，设置RSRV_TAG=0标识老用户，后边打印免填单、发送短信时使用。
                        	tradeDiscnt.setRsrvTag1("0");
                        	//咪咕流量福包优惠，设置RSRV_DATE1为受理时间，后边打印免填单、发送短信时使用。
                        	tradeDiscnt.setRsrvStr3(SysDateMgr.getChinaDate(btd.getRD().getAcceptTime(), SysDateMgr.PATTERN_CHINA_DATE));

            				
            				
                        	//2.修改福包优惠对应结束时间。
                        	tradeDiscnt.setEndDate(endDate);
                        	tradeDiscnt.setRsrvStr5(tradeDiscnt.getElementId());

                    		//4.给用户绑定咪咕定向流量
            				DiscntTradeData newDiscnt = new DiscntTradeData();
            				
                            newDiscnt.setUserId(btd.getRD().getUca().getUserId());
                            newDiscnt.setUserIdA("-1");
                            newDiscnt.setProductId("-1");
                            newDiscnt.setPackageId("-1");
                            if("84008840".equals(tradeDiscnt.getElementId()) || "84008841".equals(tradeDiscnt.getElementId()))
                            {
                            	newDiscnt.setElementId("84008844");
                            }
                            else{
                            	newDiscnt.setElementId("84008845");
                            }
                            newDiscnt.setInstId(SeqMgr.getInstId());
                            newDiscnt.setModifyTag(BofConst.MODIFY_TAG_ADD);
                            newDiscnt.setSpecTag("0");
                            newDiscnt.setStartDate(startDate);
                            newDiscnt.setEndDate( endDateDXLL );
                            newDiscnt.setRsrvStr5(tradeDiscnt.getElementId());
                            newDiscnt.setRemark("“咪咕流量福包”活动，绑定定向流量套餐！");				
            				
            				btd.add(btd.getRD().getUca().getSerialNumber(), newDiscnt);
                        }
                        //add by zhangxing3 for “咪咕流量福包”活动开发需求 end
                    	
                    	
                    	// 是否用户已经订购了该服务
                        List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(bindServiceId);
                     
                        //无线音乐会员很特殊，可以变更
                        if (userPlatSvcs.size() > 0 && !bindServiceId.equals("98001901"))
                        {
                            continue;
                        }
                        IData platParam = new DataMap();
                        platParam.put("SERVICE_ID", bindServiceId);
                        platParam.put("OPER_CODE", PlatConstants.OPER_ORDER);
                        platParam.put("OPR_SOURCE", "08");
                        platParam.put("START_DATE", tradeDiscnt.getStartDate());//xiekl修复bug，解决预约产品变更时，平台服务立即生效的问题。
                        if("1".equals(dateFlag))
                        {
                        	platParam.put("START_DATE", SysDateMgr.getSysTime());
                        }

                        PlatSvcData psd = new PlatSvcData(platParam);
                        psd.setRemark("绑定优惠增加平台服务属性");
                        //增加优惠连带平台服务的商品关系订单项
                        /*OfferRelTradeData offerRel = new OfferRelTradeData();
            			offerRel.setInstId(SeqMgr.getInstId());
            			offerRel.setModifyTag(BofConst.MODIFY_TAG_ADD);
            			offerRel.setOfferInsId(tradeDiscnt.getInstId());
            			offerRel.setOfferType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
            			offerRel.setOfferCode(tradeDiscnt.getElementId());
            			offerRel.setUserId(tradeDiscnt.getUserId());
            			offerRel.setRelOfferInsId(tradeDiscnt.getInstId());
            			offerRel.setRelOfferCode(tradeDiscnt.getElementId());
            			offerRel.setRelOfferType(BofConst.ELEMENT_TYPE_CODE_DISCNT);
            			offerRel.setRelType(BofConst.OFFER_REL_TYPE_COM);//连带关系
            			offerRel.setStartDate(tradeDiscnt.getStartDate());
            			offerRel.setEndDate(tradeDiscnt.getEndDate());
            			offerRel.setRelUserId(tradeDiscnt.getUserId());
            			offerRel.setGroupId("-1");//连带关系groupId 设置为-1
            			btd.add(uca.getSerialNumber(), offerRel);*/
            			
            			
                        PlatOfficeData officeData = psd.getOfficeData();
                        String bizTypeCode = officeData.getBizTypeCode();

                        // 需要发特殊短信
                        if (bizTypeCode.equals("19") || bizTypeCode.equals("81") || bizTypeCode.equals("28")
                                || bizTypeCode.equals("60") || bizTypeCode.equals("65"))
                        {
                            psd.setRsrvStr10("SMS:DISCNT_BIND_PLATSVC");
                        }

                        // 如果是无线音乐会员
                        if (PlatConstants.PLAT_WIRELESS_MUSIC.equals(bizTypeCode))
                        {
                            List<PlatSvcTradeData> wireLessMusicList = uca.getUserPlatSvcByServiceId("98001901");
                            if (wireLessMusicList.size() == 0)
                            {
                                // 如果配置了属性值，绑定订购无线音乐高级会员
                                if (StringUtils.isEmpty(attrValue))
                                {
                                    continue;
                                }
                                else
                                {
                                    List<AttrData> attrs = new ArrayList<AttrData>();
                                    AttrData wirelessMusicLevel = new AttrData();
                                    wirelessMusicLevel.setAttrCode("302");
                                    wirelessMusicLevel.setAttrValue(attrValue);
                                    wirelessMusicLevel.setModifyTag(BofConst.MODIFY_TAG_ADD);
                                    attrs.add(wirelessMusicLevel);
                                    psd.setAttrs(attrs);
                                }

                            }
                            else
                            {
                                PlatSvcTradeData wireLessMusic = wireLessMusicList.get(0);
                                if (BofConst.MODIFY_TAG_DEL.equals(wireLessMusic.getModifyTag()))
                                {
                                    continue;
                                }

                                List<AttrData> attrs = new ArrayList<AttrData>();
                                List<AttrTradeData> musicLevelList = uca.getUserAttrsByRelaInstId(wireLessMusic.getInstId());
                                AttrTradeData musicLevel = musicLevelList.get(0);
                                if ("302".equals(musicLevel.getAttrCode()) && "3".equals(musicLevel.getAttrValue()))
                                {
                                    continue;
                                }

                                // 当前级别不是特级音乐会员，变成特级音乐会员
                                AttrData wirelessMusicLevel = new AttrData();
                                wirelessMusicLevel.setAttrCode("302");
                                wirelessMusicLevel.setAttrValue("3");
                                wirelessMusicLevel.setModifyTag(BofConst.MODIFY_TAG_UPD);
                                attrs.add(wirelessMusicLevel);
                                psd.setAttrs(attrs);
                                psd.setOperCode(PlatConstants.OPER_USER_DATA_MODIFY);
                            }
                        }
                        if(bindServiceId.equals("80012675"))//modified by chencheng9 定位到新特会服务
                        {
                            List<PlatSvcTradeData> wireLessMusicList = uca.getUserPlatSvcByServiceId("98001901");
                            if (wireLessMusicList != null && wireLessMusicList.size() > 0)
                            {
                                PlatSvcTradeData wireLessMusic = wireLessMusicList.get(0);
                                if (BofConst.MODIFY_TAG_DEL.equals(wireLessMusic.getModifyTag()))
                                {
                                    continue;
                                }
                                String instId = wireLessMusic.getInstId();
                                boolean isLv3Member = isLv3Member(uca, instId);
                                //若用户已经是老的特级会员，则不需要再开通新特级会员
                                if(isLv3Member)
                                {
                                    continue;
                                }

                                //老无线音乐会员服务的当前级别不是特级音乐会员，要先退订再开通新的特级会员
                                bindPlatSvcs.add(psd);
                                platParam = new DataMap();
                                platParam.put("SERVICE_ID", "98001901");
                                platParam.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
                                platParam.put("OPR_SOURCE", "08");
                                platParam.put("IS_NEED_PF", "1");
                                platParam.put("END_DATE", SysDateMgr.getSysTime());
                                psd = new PlatSvcData(platParam);
                            }

                        }

                        bindPlatSvcs.add(psd);
                    }else{
                    	// 查询删除优惠时绑定平台服务的配置
                        bindPlatSvcConfigs = CommparaInfoQry.getCommpara("CSM","9897",tradeDiscnt.getElementId(),"0022");
                        //System.out.println("========DiscntBindPlatSvcAction=========bindPlatSvcConfigs:"+bindPlatSvcConfigs);
                        if (IDataUtil.isEmpty(bindPlatSvcConfigs))
                        {
                            continue;
                        }
                        String discntCode = tradeDiscnt.getElementId();
                        //System.out.println("========DiscntBindPlatSvcAction=========discntCode:"+discntCode);

                        if((BofConst.MODIFY_TAG_DEL.equals(modifyTag)) && ("84008840".equals(discntCode)||"84008841".equals(discntCode) 
                        		|| "84008842".equals(discntCode) || "84008843".equals(discntCode)||"84009642".equals(discntCode)
                        		||"84009643".equals(discntCode) || "84009644".equals(discntCode) || "84009645".equals(discntCode)))
                        {
                        	tradeDiscnt.setEndDate(SysDateMgr.getLastDateThisMonth());
                        	//是否用户已经订购了该优惠
                        	String delDiscnts="84008844,84008845,84008846,84009838,84009839,84009840,84009841";
                            List<DiscntTradeData> userDiscnts = uca.getUserDiscntsByDiscntCodeArray(delDiscnts);
                            //System.out.println("========DiscntBindPlatSvcAction=========userDiscnts:"+userDiscnts);

                            if (userDiscnts.size() == 0)
                            {
                                continue;
                            }
                            for (int k = 0; k < userDiscnts.size(); k++)
                            {
	            	        	DiscntTradeData delDiscntTD = userDiscnts.get(k).clone();
	            	        	if(!delDiscntTD.getRsrvStr5().equals(discntCode))
	            	        	{
	            	        		continue;
	            	        	}
	            	        	delDiscntTD.setEndDate(SysDateMgr.getLastDateThisMonth());
	            	        	delDiscntTD.setModifyTag(BofConst.MODIFY_TAG_DEL);
	            	        	delDiscntTD.setRemark("取消“咪咕流量福包”活动，终止绑定套餐！");				          				
	            				btd.add(btd.getRD().getUca().getSerialNumber(), delDiscntTD);
	                    		
                            }
                            
                        }
                        
                        bindSize = bindPlatSvcConfigs.size();
                        //System.out.println("========DiscntBindPlatSvcAction=========bindSize:"+bindSize);

                        for (int m = 0; m < bindSize; m++)
                        {

                            IData bindConfig1 = bindPlatSvcConfigs.getData(m);
                            String bindServiceId1 = bindConfig1.getString("PARA_CODE1");
                            String bindType = bindConfig1.getString("PARA_CODE2");
                            String bindTag = bindConfig1.getString("PARA_CODE4");//1：如果新增和截至优惠都在909配置，不截至平台服务
                            // 是否用户已经订购了该服务
                            List<PlatSvcTradeData> userPlatSvcs = uca.getUserPlatSvcByServiceId(bindServiceId1);
                            //System.out.println("========DiscntBindPlatSvcAction=========userPlatSvcs:"+userPlatSvcs);
                            if (userPlatSvcs.size() == 0)
                            {
                                continue;
                            }
                            boolean sameTag = this.isSameConfig(btd, bindServiceId1);//判断新增优惠909配置里是否包含该需要截止的平台服务
                            if("1".equals(bindTag)&&sameTag){
                            	continue;
                            }
                            if (StringUtils.equals("1", bindType)) {//1:连带取消 2：新增优惠时，与用户已有的平台服务互斥
                                IData platParam = new DataMap();
                                platParam.put("SERVICE_ID", bindServiceId1);
                                platParam.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
                                platParam.put("OPR_SOURCE", "08");
                                platParam.put("IS_NEED_PF", "1");
                                platParam.put("END_DATE", SysDateMgr.getSysTime());
                                PlatSvcData psd = new PlatSvcData(platParam);
                                //System.out.println("========DiscntBindPlatSvcAction=========psd:"+psd);
                                bindPlatSvcs.add(psd);
                            }

                        }
                        
                    }
                   
                }
                
                
                if (bindPlatSvcs.size() > 0)
                {
                    ProductModuleCreator.createProductModuleTradeData(bindPlatSvcs, uca, btd);
                    //增加优惠连带平台服务的商品关系订单项
                    List<OfferRelTradeData> offerRelTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OFFER_REL);
                    List<PlatSvcTradeData> platSvcTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
                    if(offerRelTradeDatas != null && offerRelTradeDatas.size() > 0){
                    	for(OfferRelTradeData offerRelTradeData : offerRelTradeDatas){
                    		//获取当前优惠连带平台服务的商品关系订单项
                    		if(BofConst.OFFER_REL_TYPE_LINK.equals(offerRelTradeData.getRelType()) 
                    			&& tradeDiscnt.getInstId().equals(offerRelTradeData.getOfferInsId())
                    			&& BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(offerRelTradeData.getRelOfferType()))
                    		{
                    			for(PlatSvcTradeData platSvcTradeData : platSvcTradeDatas){
                    				if(offerRelTradeData.getRelOfferCode().equals(platSvcTradeData.getElementId())){
                    					offerRelTradeData.setRelOfferInsId(platSvcTradeData.getInstId());
                    					offerRelTradeData.setRelUserId(platSvcTradeData.getUserId());
                    					if(SysDateMgr.compareTo(platSvcTradeData.getStartDate(), offerRelTradeData.getStartDate()) > 0){
                    						offerRelTradeData.setStartDate(platSvcTradeData.getStartDate());
                    					}
                    					if(SysDateMgr.compareTo(offerRelTradeData.getEndDate(), platSvcTradeData.getEndDate()) > 0){
                    						offerRelTradeData.setEndDate(platSvcTradeData.getEndDate());
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

    private boolean isLv3Member(UcaData uca, String instId) throws Exception
    {
        List<AttrTradeData> musicLevelList = uca.getUserAttrsByRelaInstId(instId);
        AttrTradeData musicLevel = musicLevelList.get(0);
        if ("302".equals(musicLevel.getAttrCode()) && "3".equals(musicLevel.getAttrValue()))
        {
            return true;
        } else
        {
            return false;
        }
    }

    private String psptTypeConvert(String psptType) throws Exception
    {
        if ("0".equals(psptType) || "1".equals(psptType))
        {
            return "00";
        }
        else if ("A".equals(psptType))
        {
            return "02";
        }
        else if ("C".equals(psptType))
        {
            return "04";
        }
        else
        {
            return "99";
        }
    }
    
    
    private boolean isSameConfig(BusiTradeData btd, String serviceId) throws Exception
    {
    	 if(StringUtils.isBlank(serviceId)){
    		return false;
    	 }
    	 
    	 UcaData uca = btd.getRD().getUca();
    	 List<DiscntTradeData> tradeDiscnts = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
    	 for (int i = 0; i < tradeDiscnts.size(); i++)
         {
         	String modifyTag = tradeDiscnts.get(i).getModifyTag();
         	if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
         		String discntCodeAdd = tradeDiscnts.get(i).getDiscntCode();
     		    // 查询优惠绑平台服务的配置
                IDataset bindPlatSvcConfigs = CommparaInfoQry.getDiscntBindPlatSvc(btd.getTradeTypeCode(), discntCodeAdd, modifyTag, uca.getUserEparchyCode());
                if (IDataUtil.isNotEmpty(bindPlatSvcConfigs))
                {
                	 for (int j = 0; j < bindPlatSvcConfigs.size(); j++)
                     {
                		 if(serviceId.equals(bindPlatSvcConfigs.getData(j).getString("PARA_CODE1"))){
                			 return true;
                		 }
                     }
                }
         		
            }
         }
         return false;
    }

}
