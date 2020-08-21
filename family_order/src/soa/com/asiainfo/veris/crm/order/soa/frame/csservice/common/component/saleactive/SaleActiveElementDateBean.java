
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class SaleActiveElementDateBean extends CSBizBean
{
    /**
     * 0或不填:和active表开始时间一致（默认） 1:立即生效 2：下账期生效 3:次日生效 4：绝对时间 
     * 5:以个人铁通宽带用户优惠的开始时间为准
     * 6:以移动个人用户的用户优惠的开始时间为准
     * 
     * @param elementData
     * @return
     * @throws Exception
     */
    private static String getElementBookDateByPkgeleTag3(UcaData uca, IData elementData) throws Exception
    {
        String elementTypeCode = elementData.getString("ELEMENT_TYPE_CODE", "X");

        if ("|D|S|C|Z|".indexOf(elementTypeCode) < 0)
        {
        	return null;
        }

        String enableTag3 = elementData.getString("RSRV_TAG3");
        if (StringUtils.isBlank(enableTag3) || "0".endsWith(enableTag3))
            return null;

        if ("1".equals(enableTag3))
        {
            return SysDateMgr.getSysTime();
        }
        else if ("2".equals(enableTag3))
        {
            return firstDayOfMonth(uca, elementData.getString("PRODUCT_ID"), elementData.getString("ELEMENT_TYPE_CODE"), elementData.getString("DISCNT_CODE"));
        }
        else if ("3".equals(enableTag3))
        {
            return SysDateMgr.getTomorrowDate();
        }
        else if ("4".equals(enableTag3))
        {
            return elementData.getString("START_ABSOLUTE_DATE", "");
        }
        return null;
    }
    /**
     * 对于RSRV_TAG3=2的情况，对宽带1+自身顺延的活动又做特殊处理！你妹妹的，艹！
     * @param uca
     * @param productId
     * @param elementTypeCode
     * @return
     * @throws Exception
     */
    public static String firstDayOfMonth(UcaData uca, String productId, String elementTypeCode, String elementid)throws Exception
    {
    	if(!"D".equals(elementTypeCode))
    	{
    		return SysDateMgr.firstDayOfMonth(1);
    	}
    	
    	IDataset _comm166Dataset = CommparaInfoQry.getCommPkInfo("CSM", "166", productId, "0898");
    	if(IDataUtil.isEmpty(_comm166Dataset))
    	{
    		return SysDateMgr.firstDayOfMonth(1);
    	}
    	
		List<String> _productIds = new ArrayList<String>();
	    SaleActiveUtil.getCommparaProductIds(_productIds, _comm166Dataset);
	    List<SaleActiveTradeData> _userSaleActiveList = uca.getUserSaleActiveByProductIds(_productIds);
	    
	    String userId = uca.getUserId();
	    if(CollectionUtils.isNotEmpty(_userSaleActiveList))
	    {
	    	String _endDate = "";
	    	//宽带1+自身顺延的活动bug修复
	    	if(productId.equals("69908001")){
                //List<DiscntTradeData> UserDiscnt = uca.getUserDiscntByDiscntId(elementid);
	    		IDataset UserDiscnt = UserDiscntInfoQry.getVirUserDiscnts(userId, "69908001");
                if ( IDataUtil.isNotEmpty(UserDiscnt) ){
                    _endDate = SaleActiveUtil.getMaxEndDateFromUserDiscnt(UserDiscnt);
                }else{
                    _endDate = _userSaleActiveList.get(0).getEndDate();
                }
	    	}else if(productId.equals("69908015")){
	    		IDataset UserDiscnt = UserDiscntInfoQry.getVirUserDiscnts(userId, "69908015");
                if ( IDataUtil.isNotEmpty(UserDiscnt) ){
                    _endDate = SaleActiveUtil.getMaxEndDateFromUserDiscnt(UserDiscnt);
                }else{
                    _endDate = _userSaleActiveList.get(0).getEndDate();
                }
	    	}else{
        		_endDate = _userSaleActiveList.get(0).getEndDate();
        	}
	    	
	    	return SysDateMgr.getNextSecond(_endDate);
	    }
    	
	    return SysDateMgr.firstDayOfMonth(1);
    }

    private static boolean isWidenetSpecOpen(String serialNumber, String productId, String elementTypeCode, String eparchyCode) throws Exception
    {
        IDataset commparaSet = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "1024", elementTypeCode, productId, eparchyCode);
        if (IDataUtil.isNotEmpty(commparaSet))
        {
            String widenetSn = "KD_" + serialNumber;
            IDataset widenetInfo = UserInfoQry.getWideUserOpenAfter25(widenetSn, "0");
            if (IDataUtil.isNotEmpty(widenetInfo))
            {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isNetTVSpecOpen(String userId, String productId, String elementTypeCode, String eparchyCode) throws Exception
    {
        IDataset commparaSet = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "1024", elementTypeCode, productId, eparchyCode);
        if (IDataUtil.isNotEmpty(commparaSet))
        { 
            IDataset netTvInfo = UserInfoQry.getNetTvUserOpenAfter25(userId);
            if (IDataUtil.isNotEmpty(netTvInfo))
            {
                return true;
            }
        }
        return false;
    }

    public IData callElementStartEndDate(IData input) throws Exception
    {
        String campnType = input.getString("CAMPN_TYPE");
        String productId = input.getString("PRODUCT_ID");
        String packageId = input.getString("PACKAGE_ID");
        String elementTypeCode = input.getString("ELEMENT_TYPE_CODE");
        String eparchyCode = input.getString(Route.ROUTE_EPARCHY_CODE);

//        String enableTag = input.getString("ENABLE_TAG");
//        String startAbsoluteDate = input.getString("START_ABSOLUTE_DATE");
//        String startOffset = input.getString("START_OFFSET");
//        String startUnit = input.getString("START_UNIT");
//        String endEnableTag = input.getString("END_ENABLE_TAG");
//        String endAbsoluteDate = input.getString("END_ABSOLUTE_DATE");
//        String endOffset = input.getString("END_OFFSET");
//        String endUnit = input.getString("END_UNIT");
        
        String enableTag = input.getString("ENABLE_MODE");
        String startAbsoluteDate = input.getString("ABSOLUTE_ENABLE_DATE");
        String startOffset = input.getString("ENABLE_OFFSET");
        String startUnit = input.getString("ENABLE_UNIT");
        String endEnableTag = input.getString("DISABLE_MODE");
        String endAbsoluteDate = input.getString("ABSOLUTE_DISABLE_DATE");
        String endOffset = input.getString("DISABLE_OFFSET");
        String endUnit = input.getString("DISABLE_UNIT");

        String serialNumber = input.getString("SERIAL_NUMBER");
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        String userId=uca.getUser().getUserId();

        String basicCalStartDate = input.getString("BASIC_CAL_START_DATE");
        String customAbsoluteStartDate = input.getString("CUSTOM_ABSOLUTE_START_DATE");

        String startDate = SysDateMgr.startDate(enableTag, startAbsoluteDate, startOffset, startUnit);

        //判断受理的该活动是否是预受理转正式类的转正式的营销活动产品 为了解决非签约类营销活动预受理转正式的问题 by songlm 20150128
        //QR-20150112-09 用户办理宽带包年未按要求顺延用户活动优惠生效时间 
        boolean thisActiveIs942Active = checkThisActiveIs942Active(productId, packageId, eparchyCode);
        
        //原代码中只处理签约类的，后面增加了非签约类中含有预受理转正式的营销活动
        if (SaleActiveUtil.isQyyx(campnType) || thisActiveIs942Active)
        {
            String elementSpecBookDate = getElementBookDateByPkgeleTag3(uca, input);
            
            if (StringUtils.isNotBlank(elementSpecBookDate))
            {
                startDate = SysDateMgr.startDateBook(elementSpecBookDate, enableTag, startAbsoluteDate, startOffset, startUnit);
            }
            else if (StringUtils.isNotBlank(customAbsoluteStartDate))
            {
                startDate = customAbsoluteStartDate;
            }
            else if (StringUtils.isNotBlank(basicCalStartDate))
            {
                startDate = SysDateMgr.startDateBook(basicCalStartDate, enableTag, startAbsoluteDate, startOffset, startUnit);
            }
            else
            {
                startDate = SysDateMgr.startDate(enableTag, startAbsoluteDate, startOffset, startUnit);
            }
            
            //针对宽带完工后营销活动预受理转正式的优惠生效时间特殊处理
            if (isWidenetSpecOpen(serialNumber, productId, elementTypeCode, eparchyCode)||(isNetTVSpecOpen(userId,productId, elementTypeCode, eparchyCode) && "70012710".equals(packageId) ))
            {
            	if(StringUtils.isBlank(basicCalStartDate))
            	{
            		basicCalStartDate = startDate;
            	}
                startDate = SysDateMgr.startDateBook(basicCalStartDate, "1", startAbsoluteDate, startOffset, startUnit);
            }
        }

        String endDate = SysDateMgr.endDate(startDate, endEnableTag, endAbsoluteDate, endOffset, endUnit);

        IData elementDateMap = new DataMap();
        elementDateMap.put("START_DATE", startDate);
        elementDateMap.put("END_DATE", endDate);

        return elementDateMap;
    }
    
    private boolean checkThisActiveIs942Active(String productId, String packageId, String eparchyCode) throws Exception
    {
        IDataset Configs = CommparaInfoQry.getCommparaByParaAttr("CSM", "942", eparchyCode);
        return SaleActiveUtil.isInCommparaParaCode2Configs(productId, packageId, Configs);
    }
}
