
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import java.util.ArrayList;
import java.util.List;



import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.util.SaleActiveBreUtil;
import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class SaleActiveDateBean extends CSBizBean
{

    public IData callActiveStartEndDate(IData input) throws Exception
    {
        String campnType = input.getString("CAMPN_TYPE");

        String enableTag = input.getString("ENABLE_TAG");
        String startAbsoluteDate = input.getString("START_ABSOLUTE_DATE");
        String startOffset = input.getString("START_OFFSET");
        String startUnit = input.getString("START_UNIT");
        String endEnableTag = input.getString("END_ENABLE_TAG");
        String endAbsoluteDate = input.getString("END_ABSOLUTE_DATE");
        String endOffset = input.getString("END_OFFSET");
        String endUnit = input.getString("END_UNIT");
        String productId = input.getString("PRODUCT_ID");
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        String userId=  uca.getUser().getUserId();
        String packageId = input.getString("PACKAGE_ID","");
        
        if("Y".equals(input.getString("SP_TAG"))){
            uca.setSubmitType(BofConst.SUBMIT_TYPE_SHOPPING_CART);
        }
        IData saleActiveDateData = new DataMap();

        String startDate = SysDateMgr.startDate(enableTag, startAbsoluteDate, startOffset, startUnit);
        
        if (isWidenetSpecOpen(serialNumber, productId, "D", "0898")||(isNetTVSpecOpen(userId,productId, "D", "0898") && "70012710".equals(packageId) ))
        {
            startDate = SysDateMgr.startDateBook(startDate, "1", startAbsoluteDate, startOffset, startUnit);
        }
        
        String endDate = SysDateMgr.endDate(startDate, endEnableTag, endAbsoluteDate, endOffset, endUnit);
        saleActiveDateData.put("START_DATE", startDate);
        saleActiveDateData.put("END_DATE", endDate);

        if (SaleActiveUtil.isQyyx(campnType))
        {
            input.put("START_DATE", startDate);
            input.put("END_DATE", endDate);
            IData bookDateData = getActiveDates(input, uca);
            saleActiveDateData.putAll(bookDateData);
        }

        return saleActiveDateData;
    }

    private boolean checkThisActiveIsBackActive(String productId, String packageId, String eparchyCode) throws Exception
    {
        IDataset noBackConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "155", eparchyCode);
        boolean noBack = SaleActiveUtil.isInCommparaConfigs(productId, packageId, noBackConfigs);

        return noBack ? false : true;
    }

    private boolean checkThisActiveIsNoBook(String productId, String packageId, String eparchyCode) throws Exception
    {
        IDataset bookConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "158", eparchyCode);
        return SaleActiveUtil.isInCommparaConfigs(productId, packageId, bookConfigs);
    }

    /**
     * 这个算法，不再考虑967参数的情况！本着签约类的返还类活动必须顺延的原则计算！因为签约类活动中的非返还类活动，是不需要顺延的！
     * 但是967参数还是有用的，用在限制重复办理的规则上，判断该活动是否可重复办理。规则上取签约类活动的最大结束时间来判断！ 不管用户当前办理的是返还类还是非返还类，也不管用户已经办理的签约活动是返还类或者是非返还类。
     * 限制重复办理的规则，在该时间计算方式之前执行！
     * 
     * 基本上都是关于顺延影响活动开始时间、结束时间、约定在网开始结束时间的
     * @param input
     * @param uca
     * @return
     * @throws Exception
     */
    private IData getActiveDates(IData input, UcaData uca) throws Exception
    {
        String relPackageId="";
        String netPackageId="";
        String productId = input.getString("PRODUCT_ID");
        String packageId = input.getString("PACKAGE_ID");
        String startDate = input.getString("START_DATE");
        String endEnableTag = input.getString("END_ENABLE_TAG");
        String endAbsoluteDate = input.getString("END_ABSOLUTE_DATE");
        String endOffset = input.getString("END_OFFSET");
        String endUnit = input.getString("END_UNIT");
        String endOnnetOffset = input.getString("RSRV_STR23", "0");
        String eparchyCode = uca.getUserEparchyCode();

        String endDate = input.getString("END_DATE");
        String onNetStartDate = "", onNetEndDate = "", bookDate = "", _startDate = startDate, _endDate = endDate;

        //判断comm158，不知道为什么配置，除了一个上网本，其他都没有配，所以基本上都是false
        boolean thisActiveIsNoBook = checkThisActiveIsNoBook(productId, packageId, eparchyCode);

        if (thisActiveIsNoBook) // 当前办理的是不能顺延的活动
        {
            onNetStartDate = startDate;
        }
        else
        // 当前办理的是可顺延的活动
        {
        	//判断comm155的配置，非返还类配置
            boolean thisActiveIsBackActive = checkThisActiveIsBackActive(productId, packageId, eparchyCode);

            if (thisActiveIsBackActive) // 当前办理的是返还类活动
            {
                List<SaleActiveTradeData> userBookAndBackSaleActives = getUserBookAndBackActives(uca, eparchyCode);
                if (CollectionUtils.isNotEmpty(userBookAndBackSaleActives))
                {
                    String maxEndDate = SaleActiveUtil.getMaxEndDateFromUserSaleActive(userBookAndBackSaleActives);
                    if (StringUtils.isNotBlank(maxEndDate) && startDate.compareTo(maxEndDate) < 0)
                    {
                        bookDate = SysDateMgr.getNextSecond(maxEndDate);//有返还可顺延活动的最晚结束时间后一秒
                    }
                    
                    if("Y".equals(input.getString("SP_TAG"))){//商品订购新增字段
                        IData maxPackage = SaleActiveUtil.getMaxPackageIdFromUserSaleActiveMerch(userBookAndBackSaleActives);
                        String maxMerchEndDate=maxPackage.getString("MAX_END_DATE");
                        if (StringUtils.isNotBlank(maxMerchEndDate) && startDate.compareTo(maxMerchEndDate) < 0)
                        {
                            //bookDate = SysDateMgr.getNextSecond(maxEndDate);//有返还可顺延活动的最晚结束时间后一秒
                            relPackageId=maxPackage.getString("REL_PACKAGE_ID");
                        }
                    }
                }
            }
            else
            // 当前办理的是非返还类活动，看是否自身与自身可以顺延办理。如果是，则需要顺延。
            {
                IDataset shunyanBySelfCommparaData = CommparaInfoQry.getCommparaCode1("CSM", "166", productId, eparchyCode);
                if (IDataUtil.isNotEmpty(shunyanBySelfCommparaData))
                {
                    List<SaleActiveTradeData> activeList = uca.getUserSaleActiveByProductId(productId);

                    if (CollectionUtils.isNotEmpty(activeList))
                    {
                    	String _maxEndDate = SaleActiveUtil.getMaxEndDateFromUserSaleActive(activeList);
                        bookDate = SysDateMgr.getNextSecond(_maxEndDate);
                    }
                }
                //add by liangdg3 for REQ201908280008关于优化和路通营销活动延续生效规则的需求 at 20101009
                //td_s_commpara表param_attr=155 增加顺延生效配置9155,
                //非返类在155中配置,155活动在9155中配置指定活动,且用户已办理活动包含指定活动,则顺延生效办理该活动,此处增加此类规则生效时间修改
                List<SaleActiveTradeData> userExists9155Actives= SaleActiveBreUtil.getUserExists9155Actives(uca.getUserSaleActives(),productId,packageId,eparchyCode);
                if(CollectionUtils.isNotEmpty(userExists9155Actives)){
                    //用户已办理活动 有当前办理获得指定的依赖活动 取指定活动中最晚的结束时间
                    String maxEndDate = SaleActiveUtil.getMaxEndDateFromUserSaleActive(userExists9155Actives);
                    bookDate = SysDateMgr.getNextSecond(maxEndDate);
                }
                //add by liangdg3 for REQ201908280008关于优化和路通营销活动延续生效规则的需求 at 20101009 end
            }

            //约定在网时间的处理
            List<SaleActiveTradeData> userBookSaleActives = getUserBookActives(uca, eparchyCode);
            String maxOnNetEndDate = getMaxOnNetDateFromUserSaleActive(userBookSaleActives);

            if (StringUtils.isNotBlank(maxOnNetEndDate) && startDate.compareTo(maxOnNetEndDate) < 0)
            {
                onNetStartDate = SysDateMgr.getNextSecond(maxOnNetEndDate);
                if("Y".equals(input.getString("SP_TAG"))){//商品订购新增字段
                    IData maxNetPackage = getMerchMaxOnNetDateFromUserSaleActive(userBookSaleActives);
                    netPackageId=maxNetPackage.getString("NET_PACKAGE_ID");                    
                }
            }
            else
            {
                if (StringUtils.isBlank(onNetStartDate))
                {
                    onNetStartDate = startDate;
                }

            }
            //约定在网时间处理结束
        }

        //如果存在顺延后的时间，则将该时间赋给活动开始时间和结束时间
        if (StringUtils.isNotBlank(bookDate))
        {
            startDate = bookDate;
            endDate = SysDateMgr.endDate(startDate, endEnableTag, endAbsoluteDate, endOffset, endUnit);
        }

        // 1593的活动，不做顺延
        SaleActiveCheckBean saleActiveCheckBean = BeanManager.createBean(SaleActiveCheckBean.class);
        boolean isNoCheckActives = saleActiveCheckBean.checkisNoCheckActives(productId, uca, false);
        if (isNoCheckActives)
        {
            startDate = _startDate;
            endDate = _endDate;
            onNetStartDate = _startDate;
            bookDate = "";
        }
        
        if(onNetStartDate.compareTo(startDate)<0)
        {
        	onNetStartDate = startDate;
        }

        onNetEndDate = SysDateMgr.endDate(onNetStartDate, endEnableTag, endAbsoluteDate, endOnnetOffset.equals("0") ? endOffset : endOnnetOffset, endUnit);

        IData saleActiveDateData = new DataMap();

        saleActiveDateData.put("START_DATE", startDate);
        saleActiveDateData.put("END_DATE", endDate);
        saleActiveDateData.put("BOOK_DATE", bookDate);
        saleActiveDateData.put("ONNET_START_DATE", onNetStartDate);
        saleActiveDateData.put("ONNET_END_DATE", onNetEndDate);
        
        saleActiveDateData.put("REL_PACKAGE_ID", relPackageId);
        saleActiveDateData.put("NET_PACKAGE_ID", netPackageId);

        return saleActiveDateData;
    }

    private String getMaxOnNetDateFromUserSaleActive(List<SaleActiveTradeData> userSaleActives)
    {
        String maxDate = "";
        int size = userSaleActives.size();
        for (int i = 0; i < size; i++)
        {
            SaleActiveTradeData userSaleActive = userSaleActives.get(i);
            String onNetEndDate = userSaleActive.getRsrvDate2();
            if (StringUtils.isNotBlank(onNetEndDate) && onNetEndDate.compareTo(maxDate) > 0)
            {
                maxDate = onNetEndDate;
            }
        }
        return maxDate;
    }

    /**
     * 获取用户已经办理的所有可顺延的活动
     * 只保留除了158内的之外，签约类的PROCESS_TAG为0或4的活动
     * @param uca
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    private List<SaleActiveTradeData> getUserBookActives(UcaData uca, String eparchyCode) throws Exception
    {
        List<String> exceptProductIds = new ArrayList<String>();
        List<String> exceptPackageIds = new ArrayList<String>();

        IDataset bookConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "158", eparchyCode);

        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, bookConfigs);

        List<SaleActiveTradeData> userBookSaleActives = uca.getUserSaleActiveExceptProductAndPackage(exceptProductIds, exceptPackageIds);

        userBookSaleActives = SaleActiveUtil.filterUserSaleActivesByProcessTag(userBookSaleActives);

        return SaleActiveUtil.filterUserSaleActivesByQyyx(userBookSaleActives);
    }

    /**
     * 获取用户办理的可顺延，且有返还的活动
     * 
     * @param uca
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    private List<SaleActiveTradeData> getUserBookAndBackActives(UcaData uca, String eparchyCode) throws Exception
    {
        List<String> exceptProductIds = new ArrayList<String>();
        List<String> exceptPackageIds = new ArrayList<String>();

        IDataset bookConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "158", eparchyCode);

        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, bookConfigs);

        IDataset noBackConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "155", eparchyCode);

        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, noBackConfigs);

        List<SaleActiveTradeData> userBookAndBackSaleActives = uca.getUserSaleActiveExceptProductAndPackage(exceptProductIds, exceptPackageIds);

        userBookAndBackSaleActives = SaleActiveUtil.filterUserSaleActivesByProcessTag(userBookAndBackSaleActives);

        return SaleActiveUtil.filterUserSaleActivesByQyyx(userBookAndBackSaleActives);
    }
    
    private static boolean isWidenetSpecOpen(String serialNumber, String productId, String elementTypeCode, String eparchyCode) throws Exception
    {
        //add by zhangxing3 for 宽带包年转宽带1+活动接续特殊处理--start
    	IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
    	if(!IDataUtil.isEmpty(userinfo)){
	    	String userId = userinfo.getData(0).getString("USER_ID", "");
	    	String yearProductId="67220428";
	    	IDataset oldSaleActives = UserSaleActiveInfoQry.querySaleActiveByUserIdPrdId(userId,  yearProductId);
	    	if(IDataUtil.isNotEmpty(oldSaleActives) && "69908001".equals(productId))
	    	{
	    		return false;//如果用户存在已生效的宽带包年活动，且要办理新的活动为宽带1+活动，则返回false不走25日规则。
	    	}
    	}
    	
    	//add by zhangxing3 for 宽带包年转宽带1+活动接续特殊处理 --end
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
    
    private IData getMerchMaxOnNetDateFromUserSaleActive(List<SaleActiveTradeData> userSaleActives)
    {
        IData data=new DataMap();
        String maxDate = "";
        String netPackageId="";
        int size = userSaleActives.size();
        for (int i = 0; i < size; i++)
        {
            SaleActiveTradeData userSaleActive = userSaleActives.get(i);
            String onNetEndDate = userSaleActive.getRsrvDate2();
            if (StringUtils.isNotBlank(onNetEndDate) && onNetEndDate.compareTo(maxDate) > 0)
            {
                maxDate = onNetEndDate;
                netPackageId=userSaleActive.getPackageId();
            }
        }
        data.put("NET_PACKAGE_ID", netPackageId);

        return data;
    }

}
