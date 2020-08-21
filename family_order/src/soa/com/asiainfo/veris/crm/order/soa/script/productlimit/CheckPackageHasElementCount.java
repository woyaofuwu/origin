
package com.asiainfo.veris.crm.order.soa.script.productlimit;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.script.query.BreQryForProduct;

public class CheckPackageHasElementCount extends BreBase
{

    private static Logger logger = Logger.getLogger(CheckPackageHasElementCount.class);

    /**
     * 排列包上各个元素的时间点
     * 
     * @param dateRangeSet
     * @param date
     */
    public static void addDatePointSet(IDataset dateRangeSet, String date)
    {

        boolean iffound = false;
        for (int i = 0; i < dateRangeSet.size(); i++)
        {
            IData tempData = dateRangeSet.getData(i);
            String tempDate = (String) tempData.getString("DATE_POINT");

            if (tempDate.compareTo(date) == 0)
            {
                iffound = true;
                break;
            }
            else if (tempDate.compareTo(date) > 0)
            {

                IData newData = new DataMap();
                newData.put("DATE_POINT", date);
                dateRangeSet.add(i, newData);

                iffound = true;
                break;
            }

        }

        if (!iffound)
        {
            IData newData = new DataMap();
            newData.put("DATE_POINT", date);
            dateRangeSet.add(newData);

        }
    }

    /**
     * 整理时间点
     * 
     * @param dateRangeSet
     * @param date
     * @throws Exception
     */
    public static IDataset cleanDatePointSet(IDataset dateRangeSet, String nowDate) throws Exception
    {
        IDataset tempDateRangeSet = new DatasetList();

        String preDatePoint = "";
        String firstDatePoint = "";
        for (int i = 0; i < dateRangeSet.size(); i++)
        {
            IData tempData = dateRangeSet.getData(i);
            String tempDate = (String) tempData.getString("DATE_POINT");

            // 获取第一个时间点
            if (i == 0)
            {
                firstDatePoint = tempDate;
            }

            // 排除掉比当前时间小的无效时间点
            if (tempDate.compareTo(nowDate) < 0)
            {
                preDatePoint = tempDate;
                continue;
            }

            // 如果两个时间点间隔一秒，则认为两个时间点是同一个，不判读这两个时间点中间的元素个数是否在包限制范围内
            if (!preDatePoint.equals("") && !tempDate.equals(""))
            {
                String formtPreDateAddSec = SysDateMgr.getNextSecond(preDatePoint);
                String formtThisDate = SysDateMgr.decodeTimestamp(tempDate, SysDateMgr.PATTERN_STAND);
                if (formtThisDate.equals(formtPreDateAddSec))
                {
                    preDatePoint = tempDate;
                    continue;
                }
            }

            preDatePoint = tempDate;
            tempDateRangeSet.add(tempData);

        }

        if (IDataUtil.isNotEmpty(tempDateRangeSet))
        {
            // 第一个时间点如果比当前时间小，则需要将当前时间做为整理后的第一个时间点
            if (!StringUtils.isEmpty(firstDatePoint) && firstDatePoint.compareTo(nowDate) < 0)
            {
                IData tempDatePointData = tempDateRangeSet.getData(0);
                String tempDatePoint = tempDatePointData.getString("DATE_POINT", "");

                if (nowDate.compareTo(tempDatePoint) != 0)
                {
                    IData nowDatePointData = new DataMap();
                    nowDatePointData.put("DATE_POINT", nowDate);
                    tempDateRangeSet.add(0, nowDatePointData);

                }
            }

        }
        return tempDateRangeSet;

    }

    /**
     * 计算各个包上各个时间点上的元素个数（每个时间点上的个数表示的是上个时间点到当前时间点上有多少时间线）
     * 
     * @param dateRangeSet
     * @param StartDate
     * @param endDate
     */
    public static void dealDatePointCountSet(IDataset dateRangeSet, String StartDate, String endDate)
    {
        for (int i = 0; i < dateRangeSet.size(); i++)
        {
            IData tempData = dateRangeSet.getData(i);
            String tempDate = (String) tempData.getString("DATE_POINT");
            int pointCount = tempData.getInt("DATE_COUNT", 0);
            if (tempDate.compareTo(StartDate) > 0 && tempDate.compareTo(endDate) <= 0)
            {
                pointCount = pointCount + 1;
                tempData.put("DATE_COUNT", pointCount);
            }
        }
    }

    /**
     * 根据用户元素信息按包分类
     * 
     * @param databus
     * @param listUserAllElement
     * @return
     * @throws Exception
     */
    public static IDataset dealPackageElemens(IData databus, IDataset listUserAllElement) throws Exception
    {
        if (IDataUtil.isEmpty(listUserAllElement))
            return null;

        String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        IDataset listTradePackage = new DatasetList();
        for (int i = 0; i < listUserAllElement.size(); i++)
        {
            IData userElement = listUserAllElement.getData(i);
            String packageId = userElement.getString("PACKAGE_ID", "");
            String elementId = userElement.getString("ELEMENT_ID", "");
            String elementTypeCode = userElement.getString("ELEMENT_TYPE_CODE", "");
            if (packageId.equals("-1"))
            {
                continue;
            }
            if (StringUtils.equals("240", tradeTypeCode)) // 营销活动组合包下面的元素不参与最大最小元素判断
            {
//                IData pkgElem = PkgElemInfoQry.getElementByElementId(packageId, elementId);
                String packageName = UPackageInfoQry.getPackageNameByPackageId(packageId);
                if(StringUtils.isNotBlank(packageName))//判断是否是营销活动包
                {
                    IData pkgElem = UPackageElementInfoQry.getElementInfo(packageId, "K", elementId, elementTypeCode);
                    if (IDataUtil.isNotEmpty(pkgElem))
                    {
                        String rsrvTag1 = pkgElem.getString("RSRV_TAG1", "");
                        if (StringUtils.equals("B", rsrvTag1))
                        {
                            continue;
                        }
                    }
                }
            }
            String userId = userElement.getString("USER_ID", "");
            String userIdA = userElement.getString("USER_ID_A", "-1");
            String modifyTag = userElement.getString("MODIFY_TAG", "exist");
            String tempElementTypeCode = userElement.getString("ELEMENT_TYPE_CODE");

            boolean ifexistPackage = false;
            for (int k = 0; k < listTradePackage.size(); k++)
            {
                IData tradePackage = listTradePackage.getData(k);
                String tradeUserId = tradePackage.getString("USER_ID", "");
                String tradeUserIdA = tradePackage.getString("USER_ID_A", "-1");
                String tradePackageId = tradePackage.getString("PACKAGE_ID");
                String tradeEmentTypeCode = tradePackage.getString("LIMIT_TYPE", "");
                String tradeModifyTag = tradePackage.getString("MODIFY_TAG", "");
                if (packageId.equals(tradePackageId) && tradeUserId.equals(userId) && tradeUserIdA.equals(userIdA))
                {
                    ifexistPackage = true;

                    if (modifyTag.equals("USER") || (modifyTag.equals("exist") || modifyTag.equals("EXIST")))
                    {
                        if (!(tradeModifyTag.equals("USER") || (tradeModifyTag.equals("exist") || tradeModifyTag.equals("EXIST"))))
                        {
                            tradeModifyTag = "2";
                        }
                    }
                    else if (modifyTag.equals("1") || modifyTag.equals("5"))
                    {
                        if (!(tradeModifyTag.equals("1") || tradeModifyTag.equals("5")))
                        {
                            tradeModifyTag = "2";
                        }
                    }
                    else if (modifyTag.equals("0"))
                    {
                        if (!tradeModifyTag.equals("0"))
                        {
                            tradeModifyTag = "2";
                        }
                    }

                    tradePackage.put("MODIFY_TAG", tradeModifyTag);
                    if (("".equals(tradeEmentTypeCode) || tradeEmentTypeCode.equals(tempElementTypeCode)))
                    {
                        IDataset packageUserElement = tradePackage.getDataset("PACKAGE_ELEMENTS");
                        if (packageUserElement == null)
                            packageUserElement = new DatasetList();
                        packageUserElement.add(userElement);
                        tradePackage.put("PACKAGE_ELEMENTS", packageUserElement);
                    }else{
                    	IDataset packageUserElement = tradePackage.getDataset("PACKAGE_ELEMENTS");
                        if(IDataUtil.isEmpty(packageUserElement)){
                        	packageUserElement = new DatasetList();
                            tradePackage.put("PACKAGE_ELEMENTS", packageUserElement);
                        }
                        
                    }
                    break;

                }
            }

            if (!ifexistPackage)
            {
                IDataset listPackage = null;
                try
                {
                    listPackage = BreQryForProduct.getPackageById(packageId,"P",userElement.getString("PRODUCT_ID", ""));
                }
                catch (Exception e)
                {
                    listPackage = null;// 根据offer_code和offer_type到产商品那边取offer_id为空报错，可能订购了老产品 捕获下异常
                }

                if (IDataUtil.isEmpty(listPackage))
                {   //每天有倒换成组的包补全-1
                    continue;
                    // BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201539, "#业务登记后条件判断:获取包资料[" +
                    // packageId + "]无数据");
                }

                String tradeEmentTypeCode = listPackage.getData(0).getString("LIMIT_TYPE", "");
                int iMax = listPackage.getData(0).getInt("MAX_NUM");
                int iMin = listPackage.getData(0).getInt("MIN_NUM");

                IDataset packageUserElements = new DatasetList();
                if (("".equals(tradeEmentTypeCode) || tradeEmentTypeCode.equals(tempElementTypeCode)))
                {
                    packageUserElements.add(userElement);
                }
                IData tradePackage = new DataMap();
                tradePackage.put("PACKAGE_ID", packageId);
                tradePackage.put("USER_ID", userId);
                tradePackage.put("USER_ID_A", userIdA);
                tradePackage.put("PACKAGE_ELEMENTS", packageUserElements);
                tradePackage.put("MAX_NUMBER", iMax);
                tradePackage.put("MIN_NUMBER", iMin);
                tradePackage.put("LIMIT_TYPE", tradeEmentTypeCode);
                tradePackage.put("MODIFY_TAG", modifyTag);
                listTradePackage.add(tradePackage);

            }
        }

        return listTradePackage;
    }

    public static void tacCheckPackageHasElementCount(IData databus, String strEparchyCode, IDataset listUserAllElements) throws Exception
    {
        IDataset listTag = BreQryForCommparaOrTag.getTagByTagCode(strEparchyCode, "CHECK_ELEMENT_COUNT_TAG", "CSM", "0");

        if (listTag.size() > 0 && listTag.getData(0).getString("TAG_CHAR").equals("1"))
        {
            return;
        }
        IDataset listTradePackage = dealPackageElemens(databus, listUserAllElements);

        if (IDataUtil.isEmpty(listTradePackage))
            return;

        int iCountTradePackage = listTradePackage.size();
        for (int iapIdx = 0; iapIdx < iCountTradePackage; iapIdx++)
        {

            IData tradePackage = listTradePackage.getData(iapIdx);
            String modifyTag = tradePackage.getString("MODIFY_TAG", "");
            if (modifyTag.equals("1") || modifyTag.equals("5"))
                continue;

            if (modifyTag.equals("USER") || modifyTag.equals("exist") || modifyTag.equals("EXIST"))
                continue;

            if (logger.isDebugEnabled())
                logger.debug("------tacCheckPackageHasElementCount！－－－－－－－－－－包信息" + iapIdx + "－－－ " + tradePackage);
            String strapPackageId = tradePackage.getString("PACKAGE_ID");

            int iMax = tradePackage.getInt("MAX_NUMBER");
            int iMin = tradePackage.getInt("MIN_NUMBER");
            String limitType = tradePackage.getString("LIMIT_TYPE", "");
            String limitTypeName = "";
            if (limitType.equals("D"))
                limitTypeName = "资费";
            else if (limitType.equals("S"))
                limitTypeName = "服务";

            if (iMax == -1 && iMin == -1)
                continue;

            IDataset listUserPackageElements = tradePackage.getDataset("PACKAGE_ELEMENTS");
            if (logger.isDebugEnabled())
                logger.debug("------tacCheckPackageHasElementCount！－－－－－－－－－－包下元素信息" + iapIdx + "－－－ " + listUserPackageElements);

            int iCountUserAllElement = listUserPackageElements.size();
            
            if(StringUtils.equals("240", databus.getString("TRADE_TYPE_CODE")))
            {
            	String errString = judgeMaxMinElementCount(iMin, iMax, iCountUserAllElement ,strapPackageId ,limitTypeName);
                if(StringUtils.isNotBlank(errString)){
                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201540, errString);
                	return;
                }
            	continue;
            }
            // 计算包下元素分布的时间点
            IDataset datePointset = new DatasetList();
            for (int iueIdx = 0; iueIdx < iCountUserAllElement; iueIdx++)
            {

                IData userAllElement = listUserPackageElements.getData(iueIdx);
                String tempStartDate = userAllElement.getString("START_DATE");
                String tempEndDate = userAllElement.getString("END_DATE");

                addDatePointSet(datePointset, tempStartDate);
                addDatePointSet(datePointset, tempEndDate);
            }
            if (logger.isDebugEnabled())
                logger.debug("------tacCheckPackageHasElementCount！－－－－－－－－－－初始时间落点" + iapIdx + "－－－ " + datePointset);
            // 整理时间点，去掉无效的时间点
            String nowDate = SysDateMgr.getSysTime();
            if (logger.isDebugEnabled())
                logger.debug("------tacCheckPackageHasElementCount！－－－－－－－－－－当前时间" + iapIdx + "－－－ " + nowDate);

            datePointset = cleanDatePointSet(datePointset, nowDate);
            if (logger.isDebugEnabled())
                logger.debug("------tacCheckPackageHasElementCount！－－－－－－－－－－整理后的时间落点" + iapIdx + "－－－ " + datePointset);
            // 计算每个时间点在每个元素的起止时间分布上存在多少个落点
            for (int iueIdx = 0; iueIdx < iCountUserAllElement; iueIdx++)
            {

                IData userAllElement = listUserPackageElements.getData(iueIdx);
                String tempStartDate = userAllElement.getString("START_DATE");
                String tempEndDate = userAllElement.getString("END_DATE");

                dealDatePointCountSet(datePointset, tempStartDate, tempEndDate);
                if (logger.isDebugEnabled())
                    logger.debug("------tacCheckPackageHasElementCount！－－－－－－－－－－计算落点" + iapIdx + "－－－" + iueIdx + "－－ start_date=" + tempStartDate + "－－ end_date=" + tempEndDate + "datePointset=>" + datePointset);
            }

            if (logger.isDebugEnabled())
                logger.debug("------tacCheckPackageHasElementCount！－－－－－－－－－－时间落点及个数" + iapIdx + "－－－ " + datePointset);

            // 取出各个时间点上落点最多和最少的两个点用来比较最大最小
            if(IDataUtil.isEmpty(datePointset)){
            	
            	String errString = judgeMaxMinElementCount(iMin, iMax, 0 ,strapPackageId ,limitTypeName);
                if(StringUtils.isNotBlank(errString)){
                	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201540, errString);
                	return;
                }
                continue;
            }
            
            for (int iueIdx = 0; iueIdx < datePointset.size(); iueIdx++)
            {

                IData datePoint = datePointset.getData(iueIdx);

                // 当前时间点到左侧的无穷小时间点的时段不需要统计
                if (iueIdx == 0)
                {
                    continue;
                }

                int pointCount = datePoint.getInt("DATE_COUNT");
                
                //短厅两城一家和非常假期特殊提示
                if(CSBizBean.getVisit().getInModeCode()!=null&&CSBizBean.getVisit().getInModeCode().equals("5")
                		&&(strapPackageId.equals("10000880")||strapPackageId.equals("10000881"))){
                	String errString=judgeMaxMinElementCountForCityVocation(iMin, iMax, pointCount ,strapPackageId ,limitTypeName, listUserPackageElements);
                	
                	if(StringUtils.isNotBlank(errString)){
                    	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201545, errString);
                    	return;
                    }
                }else{
                	String errString=judgeMaxMinElementCount(iMin, iMax, pointCount ,strapPackageId ,limitTypeName);
                	
                	if(StringUtils.isNotBlank(errString)){
                    	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 201540, errString);
                    	return;
                    }
                }
                
                
                
            }

        }

    }

    private static String judgeMaxMinElementCount(int iMin ,int iMax , int pointCount ,String strapPackageId ,String limitTypeName) throws Exception{
    	
        if (iMin == -1)
        {
            if (pointCount > iMax && iMax != -1)
            {
                String strPackageName = BreQueryHelp.getNameByCode("PackageName", strapPackageId);
                
                String strError="#业务登记后条件判断:包[" + strPackageName + "]选择" + limitTypeName + "元素最多[" + iMax + "]个, 当前已选择[" + pointCount + "]个, 业务不能继续!";
                return strError;
            }

        }
        else if (iMax == -1)
        {
            if (pointCount < iMin && iMin != -1)
            {
                String strPackageName = BreQueryHelp.getNameByCode("PackageName", strapPackageId);

                String strError = "#业务登记后条件判断:包[" + strPackageName + "]选择" + limitTypeName + "元素最少[" + iMin + "]个, 当前已选择[" + pointCount + "]个, 业务不能继续!";
                return strError;
            }
        }
        else if (pointCount < iMin || pointCount > iMax)
        {
            String strPackageName = BreQueryHelp.getNameByCode("PackageName", strapPackageId);

            String strError = "#业务登记后条件判断:包[" + strPackageName + "]选择" + limitTypeName + "元素最少[" + iMin + "]个最多[" + iMax + "]个, 当前已选择[" + pointCount + "]个, 业务不能继续!";
            return strError;
        }
        return "";

    }
    
    
    private static String judgeMaxMinElementCountForCityVocation(int iMin ,int iMax , int pointCount ,String strapPackageId ,String limitTypeName, IDataset packageElements) throws Exception{
    	
    	
        if (iMin == -1)
        {
            if (pointCount > iMax && iMax != -1)
            {
                String strPackageName = BreQueryHelp.getNameByCode("PackageName", strapPackageId);
                
                String strError=null;
                
                if(strapPackageId.equals("10000880")||strapPackageId.equals("10000881")){
                	strError=composeContent(strapPackageId, packageElements);
                }else{
                	strError="#业务登记后条件判断:包[" + strPackageName + "]选择" + limitTypeName + "元素最多[" + iMax + "]个, 当前已选择[" + pointCount + "]个, 业务不能继续!";
                }

                return strError;
            }

        }
        else if (iMax == -1)
        {
            if (pointCount < iMin && iMin != -1)
            {
                String strPackageName = BreQueryHelp.getNameByCode("PackageName", strapPackageId);

                String strError = "#业务登记后条件判断:包[" + strPackageName + "]选择" + limitTypeName + "元素最少[" + iMin + "]个, 当前已选择[" + pointCount + "]个, 业务不能继续!";
                return strError;
            }
        }
        else if (pointCount < iMin || pointCount > iMax)
        {
            String strPackageName = BreQueryHelp.getNameByCode("PackageName", strapPackageId);

            String strError = "#业务登记后条件判断:包[" + strPackageName + "]选择" + limitTypeName + "元素最少[" + iMin + "]个最多[" + iMax + "]个, 当前已选择[" + pointCount + "]个, 业务不能继续!";
            return strError;
        }
        return "";

    }
    
    
    public static String composeContent(String packageId, IDataset packageElements)throws Exception{
    	StringBuilder content=new StringBuilder();
    	
    	if(IDataUtil.isEmpty(packageElements)){
    		return content.toString();
    	}
    	
    	String userId=packageElements.getData(0).getString("USER_ID");
    	
    	IDataset set=new DatasetList();
    	
    	IDataset userDiscnts=UserDiscntInfoQry.queryUserValidDiscntByPackageId(userId, packageId);
    	List<String> discnts=new ArrayList<String>();
    	if(IDataUtil.isNotEmpty(userDiscnts)){
    		for(int i=0,size=userDiscnts.size();i<size;i++){
    			IData userDiscnt=userDiscnts.getData(i);
    			String discntCode=userDiscnt.getString("DISCNT_CODE");
    			
    			discnts.add(discntCode);
    		}
    	}
    	
    	for(int i=0,size=packageElements.size();i<size;i++){
    		IData packageElement=packageElements.getData(i);
    		String elementTypeCode=packageElement.getString("ELEMENT_TYPE_CODE","");
    		String elementId=packageElement.getString("ELEMENT_ID","");
    		
    		if(elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT)&&
    				discnts.contains(elementId)){
    			set.add(packageElement);
    		}
    			
    	}
    	
    	String elementContent="";
    	StringBuilder elementName=new StringBuilder();
    	if(IDataUtil.isNotEmpty(set)){
    		for(int i=0,size=set.size();i<size;i++){
    			String elementId=set.getData(i).getString("ELEMENT_ID","");
    			String elementTypeCode=set.getData(i).getString("ELEMENT_TYPE_CODE","");
    			
    			if(!elementId.equals("")){
    				if(elementTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT)){	//优惠
    					IData discntData=DiscntInfoQry.getDiscntInfoByCode2(elementId);
    					if(IDataUtil.isNotEmpty(discntData)){
    						elementName.append(discntData.getString("DISCNT_NAME",""));
    						elementName.append("、");
    					}
    				}
    			}
    		}
    		
    		if(elementName.toString().lastIndexOf("、")==elementName.length()-1){
    			elementContent=elementName.substring(0, elementName.length()-1).toString();
    		}
    	}
    	
    	if(packageId.equals("10000880")){	//非常假期包
    		//content.append("尊敬的动感地带客户：");
    		content.append("您已开通");
    		content.append(elementContent);
    		content.append("的非常假期套餐，不能再开通其他省份的非常假期套餐。如需变更漫游省，");
    		content.append("发送短信“BGFCJQ原漫游申请地长途区号#变更后漫游申请地长途区号”到10086，变更后下月1日生效。中国移动海南公司");
    		
    	}else if(packageId.equals("10000881")){		//两城一家包
    		//content.append("尊敬的神州行客户：您已开通");
    		content.append("您已开通");
    		content.append(elementContent);
    		content.append("的两城一家套餐，不能再开通其他省份的两城一家套餐。如需变更漫游省，");
    		content.append("发送短信“BGLCYJ原漫游申请地长途区号#变更后漫游申请地长途区号”到10086，变更后下月1日生效。中国移动海南公司");
    	}
    	
    	return content.toString();
    }

}
