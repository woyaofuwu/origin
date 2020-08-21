
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.filter;

import com.ailk.common.util.DataUtils;
import org.apache.log4j.Logger;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.TimeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductMultiFilter.java
 * @Description: 产品变更接口入参转换,适用【适用于多个元素变更】
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 17, 2014 5:12:13 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 17, 2014 maoke v1.0.0 修改原因
 */
public class ChangeProductMultiFilter implements IFilterIn
{
	protected static Logger log = Logger.getLogger(ChangeProductMultiFilter.class);

    /**
     * 必输参数检查
     * 
     * @param input
     * @throws Exception
     */
    public void checkInputData(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");// 办理号码
        IDataUtil.chkParam(input, "ELEMENTS");// 元素操作串集合

        if (input.containsKey("BOOKING_TAG"))
        {
            if (!"0".equals(input.getString("BOOKING_TAG")) && !"1".equals(input.getString("BOOKING_TAG")))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_507);
            }

            if ("1".equals(input.getString("BOOKING_TAG")))
            {
                if (StringUtils.isBlank(input.getString("START_DATE", "")))
                {
                    input.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
                }

                if (SysDateMgr.decodeTimestamp(input.getString("START_DATE"), SysDateMgr.PATTERN_STAND_YYYYMMDD).compareTo(SysDateMgr.getSysDate()) <= 0)
                {
                    CSAppException.apperr(TimeException.CRM_TIME_67);
                }

                if (SysDateMgr.monthInterval(SysDateMgr.getSysDate(), input.getString("START_DATE")) - 1 > 5)
                {
                    CSAppException.apperr(TimeException.CRM_TIME_68);
                }

                input.put("BOOKING_DATE", input.getString("START_DATE"));
            }
        }

        IDataset elements = input.getDataset("ELEMENTS");

        for (int i = 0, size = elements.size(); i < size; i++)
        {
            IData element = elements.getData(i);

            IDataUtil.chkParam(element, "ELEMENT_TYPE_CODE");
            IDataUtil.chkParam(element, "ELEMENT_ID");
            IDataUtil.chkParam(element, "MODIFY_TAG");

            String elementTypeCode = element.getString("ELEMENT_TYPE_CODE");
            String modifyTag = element.getString("MODIFY_TAG");

            if (!(BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) || BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode) || BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) || BofConst.ELEMENT_TYPE_CODE_PLATSVC
                    .equals(elementTypeCode)))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_508);
            }

            if (!(BofConst.MODIFY_TAG_ADD.equals(modifyTag) || BofConst.MODIFY_TAG_DEL.equals(modifyTag) || BofConst.MODIFY_TAG_UPD.equals(modifyTag)))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_509);
            }
        }
    }

    /**
     * 处理ATTR参数
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset dealAttrData(IData input) throws Exception
    {
        String attrStr1 = input.getString("ATTR_STR1", "");
        String attrStr2 = input.getString("ATTR_STR2", "");
        String attrStr3 = input.getString("ATTR_STR3", "");
        String attrStr4 = input.getString("ATTR_STR4", "");
        String attrStr5 = input.getString("ATTR_STR5", "");
        String attrStr6 = input.getString("ATTR_STR6", "");
        String attrStr7 = input.getString("ATTR_STR7", "");
        String attrStr8 = input.getString("ATTR_STR8", "");
        String attrStr9 = input.getString("ATTR_STR9", "");
        String attrStr10 = input.getString("ATTR_STR10", "");
        String attrStr11 = input.getString("ATTR_STR11", "");
        String attrStr12 = input.getString("ATTR_STR12", "");
        String attrStr13 = input.getString("ATTR_STR13", "");
        String attrStr14 = input.getString("ATTR_STR14", "");
        String attrStr15 = input.getString("ATTR_STR15", "");
        String attrStr16 = input.getString("ATTR_STR16", "");

        IDataset attrDatas = new DatasetList();

        String modifyTag = input.getString("MODIFY_TAG");

        if (StringUtils.isNotBlank(attrStr1))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR2");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr1);
                attr.put("ATTR_VALUE", attrStr2);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr3))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR4");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr3);
                attr.put("ATTR_VALUE", attrStr4);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr5))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR6");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr5);
                attr.put("ATTR_VALUE", attrStr6);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr7))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR8");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr7);
                attr.put("ATTR_VALUE", attrStr8);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr9))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR10");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr9);
                attr.put("ATTR_VALUE", attrStr10);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr11))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR12");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr11);
                attr.put("ATTR_VALUE", attrStr12);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr13))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR14");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr13);
                attr.put("ATTR_VALUE", attrStr14);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }
        if (StringUtils.isNotBlank(attrStr15))
        {
            if (!BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                IDataUtil.chkParam(input, "ATTR_STR16");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attrStr15);
                attr.put("ATTR_VALUE", attrStr16);
                attr.put("MODIFY_TAG", modifyTag);
                attrDatas.add(attr);
            }
        }

        return attrDatas;
    }

    /**
     * 处理可选优惠
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset dealOptionDiscntData(IData input) throws Exception
    {
        String elementTypeCode = input.getString("ELEMENT_TYPE_CODE");
        String modifyTag = input.getString("MODIFY_TAG");

        String discntStr1 = input.getString("DISCNT_STR1", "");
        String discntStr2 = input.getString("DISCNT_STR2", "");
        String discntStr3 = input.getString("DISCNT_STR3", "");
        String discntStr4 = input.getString("DISCNT_STR4", "");
        String discntStr5 = input.getString("DISCNT_STR5", "");

        IDataset discntDatas = new DatasetList();

        if (StringUtils.isNotBlank(discntStr1))
        {
            IData discnt = new DataMap();
            discnt.put("ELEMENT_ID", discntStr1);
            discnt.put("MODIFY_TAG", BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) ? BofConst.MODIFY_TAG_ADD : modifyTag);
            discnt.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntDatas.add(discnt);
        }

        if (StringUtils.isNotBlank(discntStr2))
        {
            IData discnt = new DataMap();
            discnt.put("ELEMENT_ID", discntStr2);
            discnt.put("MODIFY_TAG", BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) ? BofConst.MODIFY_TAG_ADD : modifyTag);
            discnt.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntDatas.add(discnt);
        }

        if (StringUtils.isNotBlank(discntStr3))
        {
            IData discnt = new DataMap();
            discnt.put("ELEMENT_ID", discntStr3);
            discnt.put("MODIFY_TAG", BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) ? BofConst.MODIFY_TAG_ADD : modifyTag);
            discnt.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntDatas.add(discnt);
        }

        if (StringUtils.isNotBlank(discntStr4))
        {
            IData discnt = new DataMap();
            discnt.put("ELEMENT_ID", discntStr4);
            discnt.put("MODIFY_TAG", BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) ? BofConst.MODIFY_TAG_ADD : modifyTag);
            discnt.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntDatas.add(discnt);
        }

        if (StringUtils.isNotBlank(discntStr5))
        {
            IData discnt = new DataMap();
            discnt.put("ELEMENT_ID", discntStr5);
            discnt.put("MODIFY_TAG", BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) ? BofConst.MODIFY_TAG_ADD : modifyTag);
            discnt.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            discntDatas.add(discnt);
        }

        return discntDatas;
    }

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        this.checkInputData(input);

        IDataset selectedElements = new DatasetList();

        IDataset elements = input.getDataset("ELEMENTS");

        for (int i = 0, size = elements.size(); i < size; i++)
        {
            IData selectElement = new DataMap();
            selectElement.clear();

            IData element = elements.getData(i);

            selectElement.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
            selectElement.put("MODIFY_TAG", element.getString("MODIFY_TAG"));
            selectElement.put("ELEMENT_TYPE_CODE", element.getString("ELEMENT_TYPE_CODE"));
            selectElement.put("INST_ID", element.getString("INST_ID"));

            IDataset optionDiscntDatas = dealOptionDiscntData(element);

            if (IDataUtil.isNotEmpty(optionDiscntDatas))
            {
                for (int j = 0, opSize = optionDiscntDatas.size(); j < opSize; j++)
                {
                    selectedElements.add(optionDiscntDatas.getData(j));
                }
            }

            // 处理ATTR参数
            if (IDataUtil.isNotEmpty(dealAttrData(element)))
            {
                selectElement.put("ATTR_PARAM", dealAttrData(element));
            }

            selectedElements.add(selectElement);
        }

        input.put("SELECTED_ELEMENTS", selectedElements);
        //电渠测特殊处理，由于电渠测没有把旧的优惠传过来，所以要将用户的旧的优惠编码填充进去。@tanzheng@20190301
        this.specialDealForDianqu(input);

        // 短厅"0000"删除国际漫游和国际长途,给用户绑上国内漫游和国际长途
        this.addRoam(input);
        // 接口产品变更时候,加入NEW_PRODUCT_ID方便规则取值以便调用规则和前台一致
        if (IDataUtil.isNotEmpty(selectedElements))
        {
            for (int k = 0, size = selectedElements.size(); k < size; k++)
            {
                String elementTypeCode = selectedElements.getData(k).getString("ELEMENT_TYPE_CODE");
                String elementId = selectedElements.getData(k).getString("ELEMENT_ID");

                if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode))
                {
                    input.put("NEW_PRODUCT_ID", elementId);
                    break;
                }
            }
        }
    }

	/**
	 * @description 电渠侧入参特殊处理
	 * @param @param input
	 * @return void
	 * @author tanzheng
	 * @date 2019年2月26日
	 * @param input
	 * @throws Exception 
	 */
	private void specialDealForDianqu(IData input) throws Exception {
		// TODO Auto-generated method stub
		log.debug("电渠侧入参特殊处理,入参："+input.toString());
		String inModeCode = CSBizBean.getVisit().getInModeCode();
		log.debug("电渠侧入参特殊处理,渠道："+inModeCode);
		String serialNumber =  input.getString("SERIAL_NUMBER");// 办理号码
		log.debug("电渠侧入参特殊处理,手机号："+serialNumber);
		log.debug("电渠侧入参特殊处理,操作员工："+CSBizBean.getVisit().getStaffId());
		IDataset selectedElements = new DatasetList();
		//如果是这个渠道进来的，就将变更的优惠的用户已有的优惠拼装进去
		if("L".equals(inModeCode)||"2".equals(inModeCode)){
			UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
			String productId = ucaData.getProductId();
			log.debug("电渠侧入参特殊处理,产品："+productId);
			//如果是4g自选套餐
			if("10004445".equals(productId)){
				
				
				 	IDataset elements = input.getDataset("ELEMENTS");
				 	//获取10004450  4G套餐语音包 下所有商品
				 	IData data = new DataMap();
				 	data.put("GROUP_ID", "10004450");
				 	IData result = call(null, "UPC.Out.OfferQueryFSV.queryGroupComRelOfferByGroupId", data);
				 	IDataset voiceChildren = new DatasetList();
				 	voiceChildren = result.getDataset("OUTDATA");
				 	//将进来的工号写死
				 	
				 	ElementPrivUtil.filterElementListByPriv(CSBizBean.getVisit().getStaffId(), voiceChildren);
				 	//DataHelper.sort(children, "OFFER_TYPE",IDataset.TYPE_STRING,IDataset.ORDER_DESCEND,"OFFER_CODE", IDataset.TYPE_INTEGER,IDataset.ORDER_ASCEND);
				 	
				 	log.debug("4G套餐语音包 :"+voiceChildren.toString());
				 	//获取10004453  4G套餐流量包 下所有商品
				 	IData data2 = new DataMap();
				 	data2.put("GROUP_ID", "10004453");
				 	IData result2 = call(null, "UPC.Out.OfferQueryFSV.queryGroupComRelOfferByGroupId", data2);
				 	IDataset flowChildren = new DatasetList();
				 	flowChildren = result2.getDataset("OUTDATA");
				 	//将进来的工号写死
				 	ElementPrivUtil.filterElementListByPriv(CSBizBean.getVisit().getStaffId(), flowChildren);
				 	log.debug("4G流量包 :"+flowChildren.toString());
				 	
				 	
				 	//循环入参
			        for (int i = 0, size = elements.size(); i < size; i++)
			        {
			            IData selectElement = new DataMap();
			            selectElement.clear();
			            IData element = elements.getData(i);
			            String oldDiscntCode = "";
			            String oldInstId = "";
			            String packageId = "";
			            String startDate = "";
			            String endDate = "";
			            boolean flag = true;
			            if("D".equals(element.getString("ELEMENT_TYPE_CODE"))){
			            	String newDiscntCode = element.getString("ELEMENT_ID");
			            	log.debug("新办理的优惠id:"+newDiscntCode);
			            	
			            	//判断新办理的优惠是不是语音包中的优惠
			            	for(Object data3 :voiceChildren){
			            		if(newDiscntCode.equals(((IData)data3).getString("OFFER_CODE")) && flag){
			            			for(Object data4 :voiceChildren){
			            				IData inparams = new DataMap();
			            				inparams.put("USER_ID", ucaData.getUserId());
			            				inparams.put("DISCNT_CODE", ((IData)data4).getString("OFFER_CODE"));
			            				IDataset discntList = UserDiscntInfoQry.getUserDiscntByUserID(inparams);
			            				//如果不为空，说明用户需要将这个优惠截至掉
			            				if(IDataUtil.isNotEmpty(discntList)){
			            					for(Object data5 :discntList){
			            						if(((IData)data5).getString("END_DATE").startsWith("2050")){
			            							oldDiscntCode = ((IData)data5).getString("DISCNT_CODE");
			            							oldInstId = ((IData)data5).getString("INST_ID"); 
			            							packageId = "10004450";
			            							startDate = ((IData)data5).getString("START_DATE");
			            							//将语音包的截至时间设置为新优惠开始的前一秒
			            							endDate = SysDateMgr.addSecond(element.getString("START_DATE"),-1);
			            							flag = false;
			            							log.debug("新办理的优惠"+newDiscntCode+"是语音包中的优惠，对应的老优惠："+oldDiscntCode);
			            							break;
			            						}
			            					}
			            				}
			            				if(!flag){
			            					break;
			            				}
			            			}
			            		}
			            		if(!flag){
	            					break;
	            				}
			            	}
			            	flag = true;
			            	//判断新办理的优惠是不是流量包中的优惠
			            	for(Object data3 :flowChildren){
			            		if(newDiscntCode.equals(((IData)data3).getString("OFFER_CODE"))){
			            			for(Object data4 :flowChildren){
			            				IData inparams = new DataMap();
			            				inparams.put("USER_ID", ucaData.getUserId());
			            				inparams.put("DISCNT_CODE", ((IData)data4).getString("OFFER_CODE"));
			            				IDataset discntList = UserDiscntInfoQry.getUserDiscntByUserID(inparams);
			            				//如果不为空，说明用户需要将这个优惠截至掉
			            				//如果不为空，说明用户需要将这个优惠截至掉
			            				if(IDataUtil.isNotEmpty(discntList)){
			            					for(Object data5 :discntList){
			            						if(((IData)data5).getString("END_DATE").startsWith("2050")){
			            							oldDiscntCode = ((IData)data5).getString("DISCNT_CODE");
			            							oldInstId = ((IData)data5).getString("INST_ID"); 
			            							startDate = ((IData)data5).getString("START_DATE");
			            							//流量包的旧优惠的截至时间设置为月底
			            							endDate = SysDateMgr.getDateLastMonthSec(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD));
			            							packageId = "10004453";
			            							flag = false;
			            							log.debug("新办理的优惠"+newDiscntCode+"是流量包中的优惠，对应的老优惠："+oldDiscntCode);
			            							break;
			            						}
			            					}
			            				}
			            				if(!flag){
			            					break;
			            				}
			            				
			            			}
			            		}
			            		
			            		if(!flag){
	            					break;
	            				}
			            	}
			            	
			            	
			            }
			            selectElement.put("ELEMENT_ID", oldDiscntCode);
			            selectElement.put("ELEMENT_TYPE_CODE", "D");
			            selectElement.put("MODIFY_TAG", "1");
			            selectElement.put("INST_ID", oldInstId);  
			            selectElement.put("PRODUCT_ID", "10004445");
			            selectElement.put("PACKAGE_ID", packageId);
			            selectElement.put("START_DATE", startDate);
			            selectElement.put("END_DATE", endDate);
			            	
			            selectedElements.add(element);	
			            selectedElements.add(selectElement);
			        }
			        input.put("SELECTED_ELEMENTS", selectedElements);
				
			}
			log.debug("电渠侧入参特殊处理之后的参数："+input.toString());
		}

		
	}

    private void addRoam(IData input) throws Exception {
	    // 15--国际长途 19--国际漫游 14--国内长途 18--国内漫游
        String inModeCode = CSBizBean.getVisit().getInModeCode();
        boolean del15 = false;
        boolean del19 = false;
        // 短厅进来的
        if ("5".equals(inModeCode)) {
            IDataset ids = input.getDataset("SELECTED_ELEMENTS");
            if (DataUtils.isNotEmpty(ids) && ids.size() > 0) {
                for (int i = 0 ; i < ids.size() ;i ++) {
                    IData data = ids.getData(i);
                    String elementId = data.getString("ELEMENT_ID");
                    String modifyTag = data.getString("MODIFY_TAG");
                    String elementType = data.getString("ELEMENT_TYPE_CODE");
                    // 如果删除国际漫游
                    if ("S".equals(elementType) && "19".equals(elementId) && BofConst.MODIFY_TAG_DEL.equals(modifyTag)) {
                        del19 = true;
                    }
                    // 如果删除国际长途
                    if ("S".equals(elementType) && "15".equals(elementId) && BofConst.MODIFY_TAG_DEL.equals(modifyTag)) {
                        del15 = true;
                    }
                }
            }
            // 只要有删除国际漫游或长途,就自动给用户绑定国内漫游和长途
            if (del15 || del19) {
                // 国际漫游和长途如果只删除一个,则另一个同步删除
                if (del15 && !del19) {
                    IData element19 = new DataMap();
                    element19.put("ELEMENT_ID","19");
                    element19.put("MODIFY_TAG","1");
                    element19.put("ELEMENT_TYPE_CODE","S");
                    element19.put("INST_ID",null);
                    ids.add(element19);
                }
                if (del19 && !del15) {
                    IData element15 = new DataMap();
                    element15.put("ELEMENT_ID","15");
                    element15.put("MODIFY_TAG","1");
                    element15.put("ELEMENT_TYPE_CODE","S");
                    element15.put("INST_ID",null);
                    ids.add(element15);
                }
                // 添加国内漫游和长途的数据
                IData element14 = new DataMap();
                element14.put("ELEMENT_ID","14");
                element14.put("MODIFY_TAG","0");
                element14.put("ELEMENT_TYPE_CODE","S");
                element14.put("INST_ID",null);
                ids.add(element14);

                IData element18 = new DataMap();
                element18.put("ELEMENT_ID","18");
                element18.put("MODIFY_TAG","0");
                element18.put("ELEMENT_TYPE_CODE","S");
                element18.put("INST_ID",null);
                ids.add(element18);

                input.put("SELECTED_ELEMENTS",ids);
            }
        }
	}

    private final static IData call(IBizCommon bc,String svcName, IData input) throws Exception{
        return call(bc, svcName, input, null, true);
    }
	private final static IData call(IBizCommon bc,String svcName, IData input, Pagination pagin, boolean iscatch) throws Exception{

        ServiceResponse response = BizServiceFactory.call(svcName, input, pagin);
        IData out = response.getBody();
        if(pagin != null)
            out.put("X_COUNTNUM", response.getDataCount());
        
        return out;
        
    }
}
