
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.changeUser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UStaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserEcrecepOfferfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class CrdtMngBBossUserDataBean extends GroupBean
{

    private static String merchProductId = "";

    /*
     * @description 组装停开机商品数据
     * @author xunyl
     * @date 2013-08-30
     */
    protected static IData getMerchInfoData(IData map) throws Exception
    {
        // 1- 定义商品数据
        IData merchInfo = new DataMap();

        // 2- 添加商品用户编号
        String merchUserId = map.getString("USER_ID");
        merchInfo.put("USER_ID", merchUserId);

        // 3- 添加商品编号
        IDataset productUserInfoList = UserProductInfoQry.getProductInfo(merchUserId, "-1", Route.CONN_CRM_CG);
        if (null == productUserInfoList || productUserInfoList.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_532, merchUserId);
        }
        merchProductId = productUserInfoList.getData(0).getString("PRODUCT_ID");
        merchInfo.put("PRODUCT_ID", merchProductId);

        // 4- 添加BBOSS侧商品信息
        IData goodInfo = new DataMap();
        String merchOperCode = map.getString("MERCH_OPER_CODE");
        goodInfo.put("MERCH_OPER_CODE", merchOperCode);
        goodInfo.put("IS_CREDIT", true);
        IDataset merchInfoList = UserGrpMerchInfoQry.qryMerchInfoByUserIdMerchSpecStatus(merchUserId, null, null, null);
        if (null == merchInfoList || merchInfoList.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_787, merchUserId);
        }
        String payMode = merchInfoList.getData(0).getString("RSRV_TAG1");
        String busNeedDegree = merchInfoList.getData(0).getString("BUS_NEED_DEGREE");
        goodInfo.put("PAY_MODE", payMode);
        goodInfo.put("BUS_NEED_DEGREE", busNeedDegree);

        // 5-添加联系人信息
        IDataset contactorinfos = getContactorinfos();
        goodInfo.put("CONTACTOR_INFOS", contactorinfos);
        merchInfo.put("GOOD_INFO", goodInfo);

        // 6- 返回商品信息
        return merchInfo;
    }
    
    /*
     * @description 组装停开机商品数据
     * @author xunyl
     * @date 2013-08-30
     */
    protected static IData getJKDTMerchInfoData(IData map) throws Exception
    {
        // 1- 定义商品数据
        IData merchInfo = new DataMap();

        // 2- 添加商品用户编号
        String merchUserId = map.getString("USER_ID");
        merchInfo.put("USER_ID", merchUserId);

        // 3- 添加商品编号
        IDataset productUserInfoList = UserProductInfoQry.getProductInfo(merchUserId, "-1", Route.CONN_CRM_CG);
        if (null == productUserInfoList || productUserInfoList.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_532, merchUserId);
        }
        merchProductId = productUserInfoList.getData(0).getString("PRODUCT_ID");
        merchInfo.put("PRODUCT_ID", merchProductId);

        // 4- 添加BBOSS侧商品信息
        IData goodInfo = new DataMap();
        String merchOperCode = map.getString("MERCH_OPER_CODE");
        goodInfo.put("MERCH_OPER_CODE", merchOperCode);
        goodInfo.put("IS_CREDIT", true);
        IDataset merchInfoList = UserEcrecepOfferfoQry.qryJKDTMerchInfoByUserIdMerchSpecStatus(merchUserId, null, null);
        if (null == merchInfoList || merchInfoList.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_787, merchUserId);
        }
        String payMode = merchInfoList.getData(0).getString("RSRV_TAG1");
        String busNeedDegree = merchInfoList.getData(0).getString("BUS_NEED_DEGREE");
        goodInfo.put("PAY_MODE", payMode);
        if(GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperCode)){//daidl
        	goodInfo.put("PAY_MODE", "2");//信控暂停写死传2
        }
        else{
        	goodInfo.put("PAY_MODE", "1");//信控恢复写死传1
        }
        goodInfo.put("BUS_NEED_DEGREE", busNeedDegree);

        // 5-添加联系人信息
        IDataset contactorinfos = getContactorinfos();
        goodInfo.put("CONTACTOR_INFOS", contactorinfos);
        merchInfo.put("GOOD_INFO", goodInfo);

        // 6- 返回商品信息
        return merchInfo;
    }

    /*
     * @description 组装停开机产品数据
     * @author xunyl
     * @date 2013-08-30
     */
    protected static IDataset getMerchpInfoData(IData map) throws Exception
    {
        // 1- 定义产品数据
        IDataset merchpInfoList = new DatasetList();

        // 2- 根据用户编号查询BB关系表，拼装子产品信息
        String merchUserId = map.getString("USER_ID");
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchProductId, "", true);
        IDataset relaBBInfo = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(merchUserId, merchRelationTypeCode, "0");
        if (null == relaBBInfo)
        {
            return merchpInfoList;
        }
        for (int i = 0; i < relaBBInfo.size(); i++)
        {
            IData productInfo = new DataMap();
            // 3-1 添加产品用户编号
            IData rela = relaBBInfo.getData(i);
            String userIdB = rela.getString("USER_ID_B");// 子产品用户编号
            productInfo.put("USER_ID", userIdB);

            // 3-2 添加子产品编号
            IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdB);
            String productId = userInfo.getString("PRODUCT_ID");
            productInfo.put("PRODUCT_ID", productId);

            // 3-3 添加产品操作类型
            String merchOperCode = map.getString("MERCH_OPER_CODE");
            String product_spec_code = GrpCommonBean.productToMerch(productId, 0);
            // 400语音 信控暂停商品与产品操作类型不一致
            if ("411501".equals(product_spec_code))
            {
                productInfo.put("PRODUCT_OPER_CODE", getMerchpOperType(merchOperCode));
            }
            else
            {
                productInfo.put("PRODUCT_OPER_CODE", merchOperCode);// 信控暂停商品与产品操作类型一致
                productInfo.put("PRODUCT_OPER_TYPE", merchOperCode);// 信控暂停商品与产品操作类型一致
            }

            // 3-4 添加产品处理标志
            String dealType = GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE.getValue();
            productInfo.put("DEAL_TYPE", dealType);

            // 3-5 将子产品信息添加到产品信息列表
            merchpInfoList.add(productInfo);
        }

        // 3- 返回产品数据
        return merchpInfoList;
    }
    
    /*
     * @description 组装停开机产品数据
     * @author xunyl
     * @date 2013-08-30
     */
    protected static IDataset getJKDTMerchpInfoData(IData map) throws Exception
    {
        // 1- 定义产品数据
        IDataset merchpInfoList = new DatasetList();

        // 2- 根据用户编号查询BB关系表，拼装子产品信息
        String merchUserId = map.getString("USER_ID");
        String merchRelationTypeCode = GrpCommonBean.getRelationTypeCodeByProdId(merchProductId, "", true);
        IDataset relaBBInfo = RelaBBInfoQry.qryRelaBBInfoByRoleCodeBForGrp(merchUserId, merchRelationTypeCode, "0");
        if (null == relaBBInfo)
        {
            return merchpInfoList;
        }
        for (int i = 0; i < relaBBInfo.size(); i++)
        {
            IData productInfo = new DataMap();
            // 3-1 添加产品用户编号
            IData rela = relaBBInfo.getData(i);
            String userIdB = rela.getString("USER_ID_B");// 子产品用户编号
            productInfo.put("USER_ID", userIdB);

            // 3-2 添加子产品编号
            IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdB);
            String productId = userInfo.getString("PRODUCT_ID");
            productInfo.put("PRODUCT_ID", productId);

            // 3-3 添加产品操作类型
            String merchOperCode = map.getString("MERCH_OPER_CODE");
            String product_spec_code = GrpCommonBean.productToMerch(productId, 0);
            // 400语音 信控暂停商品与产品操作类型不一致
            if ("411501".equals(product_spec_code))
            {
                productInfo.put("PRODUCT_OPER_CODE", getMerchpOperType(merchOperCode));
            }
            else
            {
                productInfo.put("PRODUCT_OPER_CODE", merchOperCode);// 信控暂停商品与产品操作类型一致
                productInfo.put("PRODUCT_OPER_TYPE", merchOperCode);// 信控暂停商品与产品操作类型一致
            }

            // 3-4 添加产品处理标志
            String dealType = GroupBaseConst.GROUP_CHANGE_DEAL_TYPE.MERCH_CHANGE.getValue();
            productInfo.put("DEAL_TYPE", dealType);
            //TODO 3-5 拼装产品参数(集团V网暂时写死参数)
        	IData productParam = new DataMap();
        	
            if(GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(merchOperCode)){
                IData param = new DataMap();
                param.put("ATTR_CODE", "9001");
                param.put("ATTR_NAME", "暂停原因");
                param.put("ATTR_VALUE", "暂停");
                param.put("END_DATE", SysDateMgr.getSysTime());
                param.put("STATE", GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue());
                IDataset productParamInfo = new DatasetList();
                productParamInfo.add(param);
                productParam.put("PRODUCT_PARAM", productParamInfo);
            }
            else if(GroupBaseConst.MERCH_STATUS.MERCH_CONTINUE.getValue().equals(merchOperCode)){
            	IData param = new DataMap();
                param.put("ATTR_CODE", "9003");
                param.put("ATTR_NAME", "恢复原因");
                param.put("ATTR_VALUE", "恢复");
                param.put("END_DATE", SysDateMgr.getTheLastTime());
                param.put("STATE", GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue());
                IDataset productParamInfo = new DatasetList();
                productParamInfo.add(param);
                productParam.put("PRODUCT_PARAM", productParamInfo);
            }
            else if(GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue().equals(merchOperCode)){
            	IData param = new DataMap();
                param.put("ATTR_CODE", "9007");
                param.put("ATTR_NAME", "注销原因");
                param.put("ATTR_VALUE", "注销");
                param.put("END_DATE", SysDateMgr.getSysTime());
                param.put("STATE", GroupBaseConst.PRODUCT_ATTR_STATUS_DESC.ATTR_ADD.getValue());
                IDataset productParamInfo = new DatasetList();
                productParamInfo.add(param);
                productParam.put("PRODUCT_PARAM", productParamInfo);
            }
            productInfo.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

            // 3-6 将子产品信息添加到产品信息列表
            merchpInfoList.add(productInfo);
        }

        // 3- 返回产品数据
        return merchpInfoList;
    }

    /**
     * @Function:
     * @Description:信控400语音获取产品操作类型
     * @author:chenyi
     * @date: 下午3:32:50 2013-10-23
     */
    private static String getMerchpOperType(String merchOperCode)
    {
        String merchpOperCode = "";
        if (GroupBaseConst.MERCH_STATUS.MERCH_CANCLEPREDESTORY.getValue().equals(merchOperCode))
        {
            merchpOperCode = GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLEPREDESTORY.getValue();
        }
        else if (GroupBaseConst.MERCH_STATUS.MERCH_PREDESTORY.getValue().equals(merchOperCode))
        {
            merchpOperCode = GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_PREDESTORY.getValue();
        }
        return merchpOperCode;
    }

    /*
     * @description 拼装信控发起的商品停开机
     * @author xunyl
     * @date 2013-08-30
     */
    public static IData makeData(IData map) throws Exception
    {
        // 1- 定义符合后台基类处理的商产品数据集
        IData returnVal = new DataMap();

        // 2- 拼装商品数据
        IData merchInfo = getMerchInfoData(map);
        returnVal.put("MERCH_INFO", merchInfo);

        // 3- 拼装产品数据
        IDataset productInfoList = getMerchpInfoData(map);
        returnVal.put("ORDER_INFO", productInfoList);

        // 4- 返回数据集
        return returnVal;
    }
    
    /*
     * @description 拼装信控发起的商品停开机
     * @author xunyl
     * @date 2013-08-30
     */
    public static IData makeJKDTData(IData map) throws Exception
    {
        // 1- 定义符合后台基类处理的商产品数据集
        IData returnVal = new DataMap();

        // 2- 拼装商品数据
        IData merchInfo = getJKDTMerchInfoData(map);
        // 3- 拼装产品数据
        IDataset productInfoList = getJKDTMerchpInfoData(map);
        
        //暂停，判断是立即生效还是下月生效
        if(GroupBaseConst.MERCH_STATUS.MERCH_PASTE.getValue().equals(map.getString("MERCH_OPER_CODE"))){
            String islastmonth = UStaticInfoQry.getStaticValue("PAYMENTDAY", productInfoList.getData(0).getString("PRODUCT_ID"));
            String payMode = "1";//立即生效
            
            if("1".equals(islastmonth)){
            	payMode = "2";//下月生效
            }
            IData goodInfo = merchInfo.getData("GOOD_INFO");
            goodInfo.put("PAY_MODE", payMode);
            merchInfo.put("GOOD_INFO", goodInfo); 
        }
        
        returnVal.put("MERCH_INFO", merchInfo);
        returnVal.put("ORDER_INFO", productInfoList);

        // 4- 返回数据集
        return returnVal;
    }

    /**
     * @Function:
     * @Description:信控拼写联系人信息
     * @author:chenyi
     * @date: 下午3:32:50 2014-9-19
     */
    public static IDataset getContactorinfos() throws Exception
    {
        IDataset Contactorinfos = new DatasetList();
        IData param =new DataMap();
        IDataset staffDataset = StaffInfoQry.qryStaffInfo(param);
        if (IDataUtil.isNotEmpty(staffDataset))
        {
            IData staff = staffDataset.getData(0);
            String staffName = staff.getString("STAFF_NAME");
            String staffPhone = staff.getString("SERIAL_NUMBER");

            // 拼写客户经理
            IData managerInfo = new DataMap();
            managerInfo.put("CONTACTOR_TYPE", "客户经理");
            managerInfo.put("CONTACTOR_NAME", staffName);
            managerInfo.put("CONTACTOR_PHONE", staffPhone);
            managerInfo.put("CONTACTOR_TYPE_CODE", "2");
            managerInfo.put("tag", "0");
            Contactorinfos.add(managerInfo);
            // 拼写订单提交人员
            IData traderInfo = new DataMap();
            traderInfo.put("CONTACTOR_TYPE", "订单提交人员");
            traderInfo.put("CONTACTOR_NAME", staffName);
            traderInfo.put("CONTACTOR_PHONE", staffPhone);
            traderInfo.put("CONTACTOR_TYPE_CODE", "5");
            traderInfo.put("tag", "0");

            Contactorinfos.add(traderInfo);
        }
        return Contactorinfos;
    }
}
