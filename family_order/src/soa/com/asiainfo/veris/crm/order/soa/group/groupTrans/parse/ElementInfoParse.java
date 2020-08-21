
package com.asiainfo.veris.crm.order.soa.group.groupTrans.parse;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.SvcException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;

public class ElementInfoParse
{

    public static void parseElmentInfo(IData idata) throws Exception
    {

        IDataset intfGrpSvcDs = idata.getDataset("INTF_GRP_SVC_DATASET");
        IDataset intfGrpDiscntDs = idata.getDataset("INTF_GRP_DIS_DATASET");
        IDataset intfMebSvcDs = idata.getDataset("INTF_MEB_SVC_DATASET");
        IDataset intfMebDiscntDs = idata.getDataset("INTF_MEB_DIS_DATASET");

        String intfSvcStr = idata.getString("SERVICE_CODE", "").trim();// 传入的服务
        String intfDiscntStr = idata.getString("DISCNT_CODE", "").trim();// 传入的资费

        // 处理传入的服务信息
        if (!"".equals(intfSvcStr))
        {
            String[] inSvc = intfSvcStr.split(";");
            for (int i = 0, svcSize = inSvc.length; i < svcSize; i++)
            {
                String[] svcArray = inSvc[i].split(",");
                if (svcArray.length != 3)
                    CSAppException.apperr(GrpException.CRM_GRP_713, "解析传入的服务串参数失败!服务串参数格式错误,正确格式为:PRODUCT_ID,PACKAGE_ID,ELEMENT_ID(多个服务之间用分号;隔开)!");

                assortElement(svcArray[0], svcArray[1], svcArray[2], "0", "S", intfGrpSvcDs, intfGrpDiscntDs, intfMebSvcDs, intfMebDiscntDs);
            }
        }

        // 处理传入的资费信息
        if (!"".equals(intfDiscntStr))
        {
            String[] inDincnts = intfDiscntStr.split(";");
            for (int i = 0, iSize = inDincnts.length; i < iSize; i++)
            {
                String[] disArray = inDincnts[i].split(",");
                if (disArray.length != 3)
                    CSAppException.apperr(GrpException.CRM_GRP_713, "解析传入的资费串参数失败!资费串参数格式错误,正确格式为:PRODUCT_ID,PACKAGE_ID,ELEMENT_ID(多个资费之间用分号;隔开)!");

                assortElement(disArray[0], disArray[1], disArray[2], "0", "D", intfGrpSvcDs, intfGrpDiscntDs, intfMebSvcDs, intfMebDiscntDs);
            }
        }
    }

    // 元素分类,根据接口传进来的productId,packageid,elementId 归类
    // 当productid为接口传时用此方法
    public static void assortElement(String productId, String packageId, String elementId, String modifyTag, String elementType, IDataset intfGrpSvcDs, IDataset intfGrpDiscntDs, IDataset intfMebSvcDs, IDataset intfMebDiscntDs) throws Exception
    {
        // productId,pacakgeId,elementId 都不能为null
        boolean isBankFlag = StringUtils.isBlank(modifyTag) || StringUtils.isBlank(productId) || StringUtils.isBlank(packageId) || StringUtils.isBlank(elementId);
        if (isBankFlag)
            CSAppException.apperr(GrpException.CRM_GRP_713, "传入的元素编码[" + elementId + "]包标识[" + packageId + "]产品标识[" + productId + "]有空的数值!");

        if ("S".equals(elementType))
        {
            IData service = PkgElemInfoQry.getServicesByServiceCode(elementId, productId);
            if (IDataUtil.isEmpty(service))
                CSAppException.apperr(GrpException.CRM_GRP_713, "传入的产品服务编码[" + elementId + "]在系统内不存在，请联系系统管理员或10086");

            service.put("PRODUCT_ID", productId);
            service.put("FROM_INTF", "1");
            service.remove("START_DATE");
            service.remove("END_DATE");
            service.put("MODIFY_TAG", modifyTag);
            String productMode = UProductInfoQry.getProductModeByProductId(productId);
            if (productMode.equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue()) || productMode.equals(GroupBaseConst.PRODUCT_MODE.USER_PLUS_PRODUCT.getValue()))
            {
                intfGrpSvcDs.add(service);
            }
            else
            {
                intfMebSvcDs.add(service);
            }
        }
        else if ("D".equals(elementType))
        {
            IData discnt = PkgElemInfoQry.getDiscntsByDiscntCode(elementId, productId, CSBizBean.getUserEparchyCode());

            if (IDataUtil.isEmpty(discnt))
                CSAppException.apperr(GrpException.CRM_GRP_713, "传入的产品资费编码[" + elementId + "]在系统内不存在，请联系系统管理员或10086");

            discnt.put("PRODUCT_ID", productId);
            discnt.put("FROM_INTF", "1");
            discnt.remove("START_DATE");
            discnt.remove("END_DATE");
            discnt.put("MODIFY_TAG", modifyTag);// 默认为新增,变更时候需要覆盖此值
            String productMode = UProductInfoQry.getProductModeByProductId(productId);
            if (productMode.equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue()) || productMode.equals(GroupBaseConst.PRODUCT_MODE.USER_PLUS_PRODUCT.getValue()))
            {
                intfGrpDiscntDs.add(discnt);
            }
            else
            {
                intfMebDiscntDs.add(discnt);
            }
        }
    }

    // 只传了集团产品标识的方法
    public static void assortElementByGrpProductId(IDataset list, int i, String productId, String packageId, String elementId, String modifyTag, String elementType, IDataset intfGrpSvcDs, IDataset intfGrpDiscntDs, IDataset intfMebSvcDs,
            IDataset intfMebDiscntDs) throws Exception
    {
        boolean isBankFlag = StringUtils.isBlank(modifyTag) || StringUtils.isBlank(elementId);
        // boolean isBankFlag = StringUtils.isBlank(modifyTag) || StringUtils.isBlank(productId) ||
        // StringUtils.isBlank(packageId) || StringUtils.isBlank(elementId);
        if (isBankFlag)
            CSAppException.apperr(GrpException.CRM_GRP_713, "传入的元素编码[" + elementId + "]包标识[" + packageId + "]产品标识[" + productId + "]有空的数值!");

        if ("S".equals(elementType))
        {
            // 集团
            IDataset grpPlusSvcDs = UProductElementInfoQry.getElementInfosByProductIdAndElementTypeCode(productId, elementType);

            StringBuilder filterCondi = new StringBuilder(100);
            filterCondi.append("ELEMENT_ID=" + elementId);
            String packageCondi = StringUtils.isBlank(packageId) ? "" : ",PACKAGE_ID=" + packageId;
            String productIdCondi = StringUtils.isBlank(productId) ? "" : ",PRODUCT_ID=" + productId;

            filterCondi.append(packageCondi);

            StringBuilder filterCondiMeb = filterCondi;

            IDataset isGrpElment = DataHelper.filter(grpPlusSvcDs, filterCondi.append(productIdCondi).toString());
            if (IDataUtil.isNotEmpty(isGrpElment))
            {
                isGrpElment.getData(0).put("MODIFY_TAG", modifyTag);
                // 处理结束时间和开始时间
                // to-do
                //
                list.remove(i);
                intfGrpSvcDs.add(isGrpElment.getData(0));
            }

            String mebBaseProductId = ProductMebInfoQry.getMemberMainProductByProductId(productId);
            
            IDataset svcPlusDataset = UProductElementInfoQry.getElementInfosByProductIdAndElementTypeCode(mebBaseProductId, elementType);

            String mebProductIdCondi = StringUtils.isBlank(mebBaseProductId) ? "" : ",PRODUCT_ID=" + mebBaseProductId;

            IDataset isMebElment = DataHelper.filter(svcPlusDataset, filterCondiMeb.append(mebProductIdCondi).toString());
            // 成员
            if (IDataUtil.isNotEmpty(isMebElment))
            {
                isMebElment.getData(0).put("MODIFY_TAG", modifyTag);
                // 处理结束时间和开始时间
                // to-do
                //
                list.remove(i);
                intfMebSvcDs.add(isMebElment.getData(0));
            }

        }
        else if ("D".equals(elementType))
        {
            // 集团
            IDataset grpPlusDiscntDs = UProductElementInfoQry.getElementInfosByProductIdAndElementTypeCode(productId, elementType);

            StringBuilder filterCondi = new StringBuilder(100);
            filterCondi.append("ELEMENT_ID=" + elementId);
            String packageCondi = StringUtils.isBlank(packageId) ? "" : ",PACKAGE_ID=" + packageId;
            String productIdCondi = StringUtils.isBlank(productId) ? "" : ",PRODUCT_ID=" + productId;
            filterCondi.append(packageCondi);

            StringBuilder filterCondiMeb = filterCondi;

            IDataset isGrpElment = DataHelper.filter(grpPlusDiscntDs, filterCondi.append(productIdCondi).toString());
            if (IDataUtil.isNotEmpty(isGrpElment))
            {
                isGrpElment.getData(0).put("MODIFY_TAG", modifyTag);
                list.remove(i);
                intfGrpDiscntDs.add(isGrpElment.getData(0));
            }
            // 成员

            String mebBaseProductId = ProductMebInfoQry.getMemberMainProductByProductId(productId);
            
            IDataset disPlusDataset = UProductElementInfoQry.getElementInfosByProductIdAndElementTypeCode(mebBaseProductId, elementType);

            String mebProductIdCondi = StringUtils.isBlank(mebBaseProductId) ? "" : ",PRODUCT_ID=" + mebBaseProductId;

            IDataset isMebElment = DataHelper.filter(disPlusDataset, filterCondiMeb.append(mebProductIdCondi).toString());
            if (IDataUtil.isNotEmpty(isMebElment))
            {
                isMebElment.getData(0).put("MODIFY_TAG", modifyTag);
                list.remove(i);
                intfMebDiscntDs.add(isMebElment.getData(0));
            }
        }
    }

    public static void parseElmentInfoChsUs(IData idata) throws Exception
    {
        IDataset intfGrpSvcDs = idata.getDataset("INTF_GRP_SVC_DATASET");
        IDataset intfGrpDiscntDs = idata.getDataset("INTF_GRP_DIS_DATASET");
        IDataset intfMebSvcDs = idata.getDataset("INTF_MEB_SVC_DATASET");
        IDataset intfMebDiscntDs = idata.getDataset("INTF_MEB_DIS_DATASET");

        String productId = IDataUtil.chkParam(idata, "PRODUCT_ID");
        IDataset lists = idata.getDataset("LIST_INFOS");

        // IDataset tempList = (IDataset)Clone.deepClone(lists);

        int resultsiz = lists.size();

        for (int i = resultsiz - 1; i >= 0; i--)
        {
            IData list = lists.getData(i);
            // 如果modify_tag为0的需要校验,1时需要校验是不是订购 modify_tag为2的暂不支持
            String discntCode = list.getString("DISCNT_CODE");
            String packageId = list.getString("PACKAGE_ID");
            String modifyTag = list.getString("MODIFY_TAG");
            assortElementByGrpProductId(lists, i, productId, packageId, discntCode, modifyTag, "D", intfGrpSvcDs, intfGrpDiscntDs, intfMebSvcDs, intfMebDiscntDs);
        }
        if (IDataUtil.isNotEmpty(lists))
            CSAppException.apperr(GrpException.CRM_GRP_713, "LIST_INFOS参数中有DISCNT_CODE和PACKAGE_ID值错误!");

    }

    public static void parseElmentInfoChsMeb(IData idata) throws Exception
    {
        IDataset intfGrpSvcDs = idata.getDataset("INTF_GRP_SVC_DATASET");
        IDataset intfGrpDiscntDs = idata.getDataset("INTF_GRP_DIS_DATASET");
        IDataset intfMebSvcDs = idata.getDataset("INTF_MEB_SVC_DATASET");
        IDataset intfMebDiscntDs = idata.getDataset("INTF_MEB_DIS_DATASET");

        String productId = IDataUtil.chkParam(idata, "PRODUCT_ID");

        IDataset lists = idata.getDataset("LIST_INFOS");// 资费,服务to-do

        if (IDataUtil.isEmpty(lists))
            return;

        // 358资费
        /*
         * boolean IS358DiscntsOld = false; boolean IS358DiscntsNew = false; String [] disnctsTemp = new
         * String[]{"1285","1286","1391"};//先写死 for(int k=0,kSize = lists.size();k<kSize;k++){ IData kList =
         * lists.getData(k); String modifyTag = kList.getString("MODIFY_TAG"); String discntCode =
         * kList.getString("DISCNT_CODE"); if("1".equals(modifyTag) && StringUtils.strAtArray(discntCode,
         * disnctsTemp)>-1){ IS358DiscntsOld = true; idata.put("OLD_DISCNT_358",discntCode); } if("0".equals(modifyTag)
         * && StringUtils.strAtArray(discntCode, disnctsTemp)>-1){ IS358DiscntsNew = true;
         * idata.put("NEW_DISCNT_358",discntCode); } } if(IS358DiscntsOld && IS358DiscntsNew){ idata.put("IS358Discnts",
         * idata.getBoolean("IS358Discnts",true)); if(!idata.getBoolean("ISNOT358Discnts",false)){
         * change358Disnct(idata); return; } }
         */
        // IDataset tempList = (IDataset)Clone.deepClone(lists);
        int resultsiz = lists.size();
        for (int i = resultsiz - 1; i >= 0; i--)
        {
            IData list = lists.getData(i);
            // 如果modify_tag为0的需要校验,1时需要校验是不是订购 modify_tag为2的暂不支持
            String discntCode = list.getString("DISCNT_CODE");
            String packageId = list.getString("PACKAGE_ID");
            // 成员变更为何不传packageId,集团变更又传了
            String modifyTag = list.getString("MODIFY_TAG");
            assortElementByGrpProductId(lists, i, productId, packageId, discntCode, modifyTag, "D", intfGrpSvcDs, intfGrpDiscntDs, intfMebSvcDs, intfMebDiscntDs);
        }
        if (IDataUtil.isNotEmpty(lists))
            CSAppException.apperr(GrpException.CRM_GRP_713, "LIST_INFOS参数中有DISCNT_CODE和PACKAGE_ID值错误!");

        // 服务to-do

    }

    public static void parseElmentInfoCrtMeb(IData idata) throws Exception
    {
        IDataset intfGrpSvcDs = idata.getDataset("INTF_GRP_SVC_DATASET");
        IDataset intfGrpDiscntDs = idata.getDataset("INTF_GRP_DIS_DATASET");
        IDataset intfMebSvcDs = idata.getDataset("INTF_MEB_SVC_DATASET");
        IDataset intfMebDiscntDs = idata.getDataset("INTF_MEB_DIS_DATASET");

        String intfSvcStr = idata.getString("SERVICE_CODE", "").trim();// 传入的服务
        String intfDiscntStr = idata.getString("DISCNT_CODE", "").trim();// 传入的资费

        // 处理传入的服务信息
        if (!"".equals(intfSvcStr))
        {
            String[] inSvc = intfSvcStr.split(";");
            for (int i = 0, svcSize = inSvc.length; i < svcSize; i++)
            {
                String[] svcArray = inSvc[i].split(",");
                if (svcArray.length != 3)
                    CSAppException.apperr(GrpException.CRM_GRP_713, "解析传入的服务串参数失败!服务串参数格式错误,正确格式为:PRODUCT_ID,PACKAGE_ID,ELEMENT_ID(多个服务之间用分号;隔开)!");

                assortElement(svcArray[0], svcArray[1], svcArray[2], "0", "S", intfGrpSvcDs, intfGrpDiscntDs, intfMebSvcDs, intfMebDiscntDs);
            }
        }

        // 处理传入的资费信息
        if (!"".equals(intfDiscntStr))
        {
            String[] inDincnts = intfDiscntStr.split(";");
            for (int i = 0, iSize = inDincnts.length; i < iSize; i++)
            {
                String[] disArray = inDincnts[i].split(",");
                if (disArray.length != 3)
                    CSAppException.apperr(GrpException.CRM_GRP_713, "解析传入的资费串参数失败!资费串参数格式错误,正确格式为:PRODUCT_ID,PACKAGE_ID,ELEMENT_ID(多个资费之间用分号;隔开)!");

                assortElement(disArray[0], disArray[1], disArray[2], "0", "D", intfGrpSvcDs, intfGrpDiscntDs, intfMebSvcDs, intfMebDiscntDs);
            }
        }
    }

    public static void change358Disnct(IData idata) throws Exception
    {

        String grpUserId = idata.getString("USER_ID");
        String sn = idata.getString("SERIAL_NUMBER");
        idata.put("USER_ID", grpUserId);
        idata.put("SERIAL_NUMBER", sn);

        IData params = new DataMap();
        params.put("SERIAL_NUMBER", sn);
        IDataset mebInfos = CSAppCall.call("CS.UcaInfoQrySVC.qryUserInfoBySn", params);

        if (IDataUtil.isEmpty(mebInfos))
            CSAppException.apperr(BofException.CRM_BOF_002);

        String memUserId = mebInfos.getData(0).getString("USER_ID");// 无用数据,前台服务这么写的,没办法
        String memEparchyCode = mebInfos.getData(0).getString("EPARCHY_CODE");// 无用数据,前台服务这么写的,没办法

        idata.put("PRODUCT_ID", "8000");
        IData param = new DataMap();
        param.put("USER_ID", memUserId);
        param.put("USER_ID_A", grpUserId);
        param.put(Route.ROUTE_EPARCHY_CODE, memEparchyCode);
        IDataset serviceinfos = CSAppCall.call("CS.UserSvcInfoQrySVC.getUserProductSvc", param);
        if (IDataUtil.isEmpty(serviceinfos))
        {
            CSAppException.apperr(SvcException.CRM_SVC_3, "860");
        }
        boolean notHave860 = true;
        IDataset productElements = new DatasetList(); // 元素
        for (int i = 0; i < serviceinfos.size(); i++)
        {
            IData service = serviceinfos.getData(i);
            String serviceId = service.getString("SERVICE_ID");
            if ("860".equals(serviceId))
            {
                service.put("ELEMENT_ID", "860");
                service.put("MODIFY_TAG", "2");
                service.put("ELEMENT_TYPE_CODE", "S");
                productElements.add(service);
                notHave860 = false;
                break;
            }
        }
        if (notHave860)
        {
            CSAppException.apperr(SvcException.CRM_SVC_3, "860");
        }
        IDataset productParam = new DatasetList();// 产品参数

        idata.put("ELEMENT_INFO", productElements);
        idata.put("NEXT_ACCT_DISCODE", idata.getString("NEW_DISCNT_358"));// 下账期优惠
        idata.put("THIS_ACCT_DISCODE", "");// 本账期优惠
        if (IDataUtil.isNotEmpty(productParam))
            idata.put("PRODUCT_PARAM_INFO", productParam);
        idata.put(Route.ROUTE_EPARCHY_CODE, memEparchyCode);
    }
}
