package com.asiainfo.veris.crm.order.soa.person.busi.cmonline.selfterminal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;

public class SelfTerminalUtil {
    public final static String CODE_RIGHT = "0000";

    public final static String CODE_ERROR = "0001";

    private static final Logger log = Logger.getLogger(SelfTerminalUtil.class);
    
    /**
     * 相应成功结果
     * @param result
     * @return
     */
	public static IData responseSuccess (IData result){
		IData output = new DataMap();
		output.put("bizCode", CODE_RIGHT);
		output.put("bizDesc", "success");
		output.putAll(result);
		return output;
	}
	 /**
     * 相应失败结果
     * @param result
     * @return
     */
	public static IData responseFail (String desc,IData result){
		IData output = new DataMap();
		output.put("bizCode", CODE_ERROR);
		output.put("bizDesc", desc);
		if(IDataUtil.isNotEmpty(result)){
			output.putAll(result);
		}
		return output;
	}
	
	/**
	 * 获取用户状态编码
	 */
	public static String getUserStatus(String param)
	{
		IData userStateData = new DataMap();
		userStateData.put("N", "00");// 信用有效时长开通
		userStateData.put("T", "01");// 骚扰电话半停机
		userStateData.put("0", "00");// 开通
		userStateData.put("1", "02");// 申请停机
		userStateData.put("2", "02");// 挂失停机
		userStateData.put("3", "02");// 并机停机
		userStateData.put("4", "02");// 局方停机
		userStateData.put("5", "02");// 欠费停机
		userStateData.put("6", "03");// 申请销号
		userStateData.put("7", "02");// 高额停机
		userStateData.put("8", "03");// 欠费预销号
		userStateData.put("9", "04");// 欠费销号
		userStateData.put("A", "01");// 欠费半停机
		userStateData.put("B", "01");// 高额半停机
		userStateData.put("E", "04");// 转网销号停机
		userStateData.put("F", "03");// 申请预销停机
		userStateData.put("G", "01");// 申请半停机
		userStateData.put("I", "02");// 申请停机（收月租）

		if(StringUtils.isEmpty(userStateData.getString(param))){
			return "99";
		}else{
			return userStateData.getString(param);
		}
	}
	/**
	 * 获取用户状态名称
	 */
	public static String getUserStatusName(String param)
	{
		IData userStateData = new DataMap();
		userStateData.put("00","正常");
		userStateData.put("01","单向停机");
		userStateData.put("02","停机");
		userStateData.put("03","预销户");
		userStateData.put("04","销户");
		userStateData.put("05","过户");
		userStateData.put("06","改号");
		userStateData.put("99","此号码不存在");

		return userStateData.getString(getUserStatus(param));
	}
	/**
	 * 获取年龄
	 * @param birthDay
	 * @return
	 * @throws Exception
	 */
	public static int getAge(String inBirthDay) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date birthDay = formatter.parse(inBirthDay);
		
		// 获取当前系统时间
		Calendar cal = Calendar.getInstance();
		// 如果出生日期大于当前时间，则抛出异常
		if (cal.before(birthDay)) {
			throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
		}
		// 取出系统当前时间的年、月、日部分
		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH);
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
 
		// 将日期设置为出生日期
		cal.setTime(birthDay);
		// 取出出生日期的年、月、日部分
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH);
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
		// 当前年份与出生年份相减，初步计算年龄
		int age = yearNow - yearBirth;
		// 当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
		if (monthNow <= monthBirth) {
			// 如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
			if (monthNow == monthBirth) {
				if (dayOfMonthNow < dayOfMonthBirth)
					age--;
			} else {
				age--;
			}
		}
		//System.out.println("age:" + age);
		return age;
	}
	
	/**
	 * 根据productId获取必选服务和优惠
	 * @throws Exception 
	 */
	public static IDataset getElements(String productId) throws Exception{
		IDataset selecments=new DatasetList();
		
		// 默认必选服务处理
        IDataset svcElems = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, BofConst.ELEMENT_TYPE_CODE_SVC);
        if (svcElems.isEmpty())
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_192, productId);
        }
        boolean gprsFlag = false;
        String packageIdForGprs = "";
        for (int i = 0; i < svcElems.size(); i++)
        {
            IData elem = svcElems.getData(i);
            if ("22".equals(elem.getString("SERVICE_ID")))
            {
                gprsFlag = true;
                packageIdForGprs = elem.getString("PACKAGE_ID");
            }
           
            
            SvcData svcData = new SvcData(elem);
            ProductTimeEnv env = new ProductTimeEnv();
            env.setBasicAbsoluteStartDate(SysDateMgr.getSysTime());
            String startDate = ProductModuleCalDate.calStartDate(svcData, env);
            svcData.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(svcData, startDate);
            svcData.setEndDate(endDate);
            svcData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            
            List<AttrData> attrDatas = new ArrayList<AttrData>();
//            IDataset svcAttrItems = AttrItemInfoQry.getSvcElementItemaByPk(elem.getString("SERVICE_ID"), eparchyCode);
            IDataset svcAttrItems = AttrItemInfoQry.getElementItemA(BofConst.ELEMENT_TYPE_CODE_SVC, elem.getString("SERVICE_ID"), "0898");
            for (int j = 0; j < svcAttrItems.size(); j++)
            {
                IData svcAttrItem = svcAttrItems.getData(j);
                if("0".equals(svcAttrItem.getString("ATTR_CAN_NULL",""))){
                	if ("".equals(svcAttrItem.getString("ATTR_INIT_VALUE")))
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_193, elem.getString("SERVICE_ID"), svcAttrItem.getString("ATTR_CODE"));
                    }
                    else
                    {
                        AttrData attrData = new AttrData();
                        attrData.setAttrCode(svcAttrItem.getString("ATTR_CODE"));
                        attrData.setAttrValue(svcAttrItem.getString("ATTR_INIT_VALUE"));
                        attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        attrDatas.add(attrData);
                    }
                }
            }
            svcData.setAttrs(attrDatas);
//            brd.addPmd(svcData);
            
            //=================================================
            DataMap svcMap=new DataMap();
            svcMap.put("ELEMENT_TYPE_CODE", svcData.getElementType());
            svcMap.put("ELEMENT_ID", svcData.getElementId());
            svcMap.put("MODIFY_TAG", svcData.getModifyTag());
            svcMap.put("START_DATE", svcData.getStartDate());
            svcMap.put("END_DATE", svcData.getEndDate());
            svcMap.put("PRODUCT_ID", svcData.getProductId());
            svcMap.put("PACKAGE_ID", svcData.getPackageId());
            svcMap.put("CAMPN_ID", svcData.getCampnId());
            svcMap.put("INST_ID", svcData.getInstId());
            svcMap.put("REMARK", svcData.getRemark());
            
            if(svcData.getAttrs()!=null&&svcData.getAttrs().size()>0){
            	IDataset arrDataset=new DatasetList();
            	for(AttrData attrData :svcData.getAttrs()){
            		IData aIData=new DataMap();
            		aIData.put("ATTR_CODE", attrData.getAttrCode());
            		aIData.put("ATTR_VALUE", attrData.getAttrValue());
            		aIData.put("MODIFY_TAG", attrData.getModifyTag());
            		arrDataset.add(aIData);
            	}
            	 svcMap.put("ATTR_PARAM", arrDataset);
            }
            selecments.add(svcMap);
        }

        // 22服务绑定902优惠
        if (gprsFlag)
        {
            IData elemD = new DataMap();
            ProductTimeEnv env = new ProductTimeEnv();
            elemD.put("PRODUCT_ID", productId);
            elemD.put("PACKAGE_ID", packageIdForGprs);
            elemD.put("DISCNT_CODE", "902");
            DiscntData discntData = new DiscntData(elemD);
            env.setBasicAbsoluteStartDate(SysDateMgr.getSysTime());
            String startDateD = ProductModuleCalDate.calStartDate(discntData, env);
            discntData.setStartDate(startDateD);
            String endDateD = ProductModuleCalDate.calEndDate(discntData, startDateD);
            discntData.setEndDate(endDateD);
            discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            
            DataMap discntMap=new DataMap();
            discntMap.put("ELEMENT_TYPE_CODE", "D");
            discntMap.put("ELEMENT_ID", discntData.getElementId());
            discntMap.put("MODIFY_TAG", discntData.getModifyTag());
            discntMap.put("START_DATE", discntData.getStartDate());
            discntMap.put("END_DATE", discntData.getEndDate());
            discntMap.put("PRODUCT_ID", discntData.getProductId());
            discntMap.put("PACKAGE_ID", discntData.getPackageId());
            discntMap.put("CAMPN_ID", discntData.getCampnId());
            discntMap.put("INST_ID", discntData.getInstId());
            discntMap.put("REMARK", discntData.getRemark());
            selecments.add(discntMap);
//            brd.addPmd(discntData);
        }
        
        // 默认必选优惠处理
        IDataset discntEles = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, BofConst.ELEMENT_TYPE_CODE_DISCNT);
        for (int i = 0; i < discntEles.size(); i++)
        {
            IData elem = discntEles.getData(i);
            DiscntData discntData = new DiscntData(elem);
            ProductTimeEnv env = new ProductTimeEnv();
            env.setBasicAbsoluteStartDate(SysDateMgr.getSysTime());
            String startDate = ProductModuleCalDate.calStartDate(discntData, env);
            discntData.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(discntData, startDate);
            discntData.setEndDate(endDate);
            discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            List<AttrData> attrDatas = new ArrayList<AttrData>();
//            IDataset discntAttrItems = AttrItemInfoQry.getDiscntElementItemaByPk(elem.getString("DISCNT_CODE"), eparchyCode);
            IDataset discntAttrItems = AttrItemInfoQry.getElementItemA(BofConst.ELEMENT_TYPE_CODE_DISCNT, elem.getString("DISCNT_CODE"), "0898");
            for (int j = 0; j < discntAttrItems.size(); j++)
            {
                IData discntAttrItem = discntAttrItems.getData(j);
                if("0".equals(discntAttrItem.getString("ATTR_CAN_NULL",""))){
                	if ("".equals(discntAttrItem.getString("ATTR_INIT_VALUE")))
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_194, elem.getString("DISCNT_CODE"), discntAttrItem.getString("ATTR_CODE"));
                    }
                    else
                    {
                        AttrData attrData = new AttrData();
                        attrData.setAttrCode(discntAttrItem.getString("ATTR_CODE"));
                        attrData.setAttrValue(discntAttrItem.getString("ATTR_INIT_VALUE"));
                        attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        attrDatas.add(attrData);
                    }
                }
            }
            discntData.setAttrs(attrDatas);
            
            DataMap discntMap=new DataMap();
            discntMap.put("ELEMENT_TYPE_CODE", "D");
            discntMap.put("ELEMENT_ID", discntData.getElementId());
            discntMap.put("MODIFY_TAG", discntData.getModifyTag());
            discntMap.put("START_DATE", discntData.getStartDate());
            discntMap.put("END_DATE", discntData.getEndDate());
            discntMap.put("PRODUCT_ID", discntData.getProductId());
            discntMap.put("PACKAGE_ID", discntData.getPackageId());
            discntMap.put("CAMPN_ID", discntData.getCampnId());
            discntMap.put("INST_ID", discntData.getInstId());
            discntMap.put("REMARK", discntData.getRemark());
            selecments.add(discntMap);
//            brd.addPmd(discntData);
        }

        IDataset commparamInfos = CommparaInfoQry.getCommparaInfoByForceDiscnt("CSM", "829", productId, CSBizBean.getVisit().getCityCode(), "0898");
        if (IDataUtil.isNotEmpty(commparamInfos))
        {
            String package_id = commparamInfos.getData(0).getString("PARA_CODE2");
            String discnt_code = commparamInfos.getData(0).getString("PARA_CODE3");
            // 必选优惠组绑定的优惠单独处理
            IData elem = new DataMap();
            elem.put("PRODUCT_ID", productId);
            elem.put("PACKAGE_ID", package_id);
            elem.put("DISCNT_CODE", discnt_code);
            DiscntData discntData = new DiscntData(elem);
            ProductTimeEnv env = new ProductTimeEnv();
            env.setBasicAbsoluteStartDate(SysDateMgr.getSysTime());
            String startDate = ProductModuleCalDate.calStartDate(discntData, env);
            discntData.setStartDate(startDate);
            String endDate = ProductModuleCalDate.calEndDate(discntData, startDate);
            discntData.setEndDate(endDate);
            discntData.setModifyTag(BofConst.MODIFY_TAG_ADD);

            List<AttrData> attrDatas = new ArrayList<AttrData>();
//            IDataset discntAttrItems = AttrItemInfoQry.getDiscntElementItemaByPk(discnt_code, eparchyCode);
            IDataset discntAttrItems = AttrItemInfoQry.getElementItemA(BofConst.ELEMENT_TYPE_CODE_DISCNT, discnt_code, "0898");
            for (int j = 0; j < discntAttrItems.size(); j++)
            {
                IData discntAttrItem = discntAttrItems.getData(j);
                if("0".equals(discntAttrItem.getString("ATTR_CAN_NULL",""))){
                	if ("".equals(discntAttrItem.getString("ATTR_INIT_VALUE")))
                    {
                        CSAppException.apperr(ProductException.CRM_PRODUCT_194, discnt_code, discntAttrItem.getString("ATTR_CODE"));
                    }
                    else
                    {
                        AttrData attrData = new AttrData();
                        attrData.setAttrCode(discntAttrItem.getString("ATTR_CODE"));
                        attrData.setAttrValue(discntAttrItem.getString("ATTR_INIT_VALUE"));
                        attrData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        attrDatas.add(attrData);
                    }
                }
            }
            discntData.setAttrs(attrDatas);

            DataMap discntMap=new DataMap();
            discntMap.put("ELEMENT_TYPE_CODE", "D");
            discntMap.put("ELEMENT_ID", discntData.getElementId());
            discntMap.put("MODIFY_TAG", discntData.getModifyTag());
            discntMap.put("START_DATE", discntData.getStartDate());
            discntMap.put("END_DATE", discntData.getEndDate());
            discntMap.put("PRODUCT_ID", discntData.getProductId());
            discntMap.put("PACKAGE_ID", discntData.getPackageId());
            discntMap.put("CAMPN_ID", discntData.getCampnId());
            discntMap.put("INST_ID", discntData.getInstId());
            discntMap.put("REMARK", discntData.getRemark());
            selecments.add(discntMap);
//            brd.addPmd(discntData);
        }
		
		return selecments;
	}

}
