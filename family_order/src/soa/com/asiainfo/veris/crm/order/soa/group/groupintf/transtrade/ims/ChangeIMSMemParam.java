
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.GrpImsInfoQuery;

public class ChangeIMSMemParam
{
    /************************* 成员参数处理开始 **************************/
    /*
     * @description 成员个性化参数
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperModifyMebAttr(IData data) throws Exception
    {
        IDataset attrLists = data.getDataset("PRODUCT_ATTR");
        if (IDataUtil.isEmpty(attrLists))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_65);// 获取参数无数据PRODUCT_ATTR
        }

        String userIdA = data.getString("USER_ID_A");
        String userIdB = data.getString("USER_ID_B");
        // 查询集团、成员用户信息
        UcaData grpUcaData = UcaDataFactory.getUcaByUserId(userIdA);
        UcaData memUcaData = UcaDataFactory.getUcaByUserId(userIdB);

        IDataset relat = RelaUUInfoQry.checkMemRelaByUserIdb(userIdA, userIdB, "", null);
        if (IDataUtil.isEmpty(relat))
        {
            CSAppException.apperr(GrpException.CRM_GRP_90);// 该成员和该集团用户无订购关系
        }
        IDataset memUserProd = UserProductInfoQry.getProductInfo(userIdB, userIdA, null);
        if (IDataUtil.isEmpty(memUserProd))
        {
            CSAppException.apperr(GrpException.CRM_GRP_129, userIdB, userIdA);// 根据成员用户id[%s]和集团用户id[%s]查询不到对应的成员产品订购信息！
        }
        String memUserProdId = memUserProd.getData(0).getString("PRODUCT_ID", "0");

        String instId = memUserProd.getData(0).getString("INST_ID", "0");
        // 处理参数，返回处理结果
        IData conmmitData = DealIMSCommon.setProductParam(attrLists, grpUcaData, memUcaData, memUserProdId, instId);// 参数处理结束

        conmmitData.put("USER_ID", userIdA);
        conmmitData.put("SERIAL_NUMBER", memUcaData.getSerialNumber());
        if ("".equals(conmmitData.getString("MEM_ROLE_B", "")))
        {
            conmmitData.put("MEM_ROLE_B", relat.getData(0).getString("ROLE_CODE_B", "1"));
        }
        conmmitData.put("PRODUCT_ID", grpUcaData.getProductId());
        conmmitData.put("IS_NEED_TRANS", false);

        IDataset dataset = new DatasetList();
        try
        {
            dataset = CSAppCall.call("CS.ChangeMemElementSvc.changeMemElement", conmmitData);
        }
        catch (Throwable e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, Utility.getBottomException(e).getMessage());
        }
        return dataset;
    }

    /************************* 成员参数处理结束 **************************/

    /************************* 修改成员用户的资费开始 **************************/
    /*
     * @description 修改成员用户的资费
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperModifyMebDiscnt(IData data) throws Exception
    {
        IDataset discntListInfos = data.getDataset("LIST_INFOS");
        if (IDataUtil.isEmpty(discntListInfos))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_66, "LIST_INFOS");// 接口参数检查，输入参数%s不存在
        }
        String userIdB = IDataUtil.chkParam(data, "USER_ID_B");
        String userIdA = IDataUtil.chkParam(data, "USER_ID_A");
        // 查询集团、成员用户信息
        UcaData grpUcaData = UcaDataFactory.getUcaByUserId(userIdA);
        UcaData memUcaData = UcaDataFactory.getUcaByUserId(userIdB);

        // 查询relat信息
        IDataset relat = RelaUUInfoQry.checkMemRelaByUserIdb(userIdA, userIdB, "", null);
        if (IDataUtil.isEmpty(relat))
        {
            CSAppException.apperr(GrpException.CRM_GRP_90);// 该成员和该集团用户无订购关系
        }

        // 查询成员订购产品信息
        IDataset memUserProd = UserProductInfoQry.getProductInfo(userIdB, userIdA, null);
        if (IDataUtil.isEmpty(memUserProd))
        {
            CSAppException.apperr(GrpException.CRM_GRP_129, userIdB, userIdA);// 根据成员用户id[%s]和集团用户id[%s]查询不到对应的成员产品订购信息！
        }
        String mebProductId = memUserProd.getData(0).getString("PRODUCT_ID", "0");
        String productId = grpUcaData.getProductId();

        // ***************处理资费信息**************
        IDataset elementInfo = DealIMSCommon.setElementInfo(discntListInfos, grpUcaData, memUcaData, mebProductId);

        IDataset productParamList = new DatasetList();
        if (GrpImsInfoQuery.VPN_GRP_PRODUCTID.equals(productId))
        {
            // 融合V网，处理短号
            String shortNum = relat.getData(0).getString("SHORT_CODE");
            IData shortData = new DataMap();
            shortData.put("ATTR_CODE", "SHORT_CODE");
            shortData.put("ATTR_VALUE", shortNum);
            productParamList.add(shortData);

            IData lodShortData = new DataMap();
            lodShortData.put("ATTR_CODE", "OLD_SHORT_CODE");
            lodShortData.put("ATTR_VALUE", shortNum);
            productParamList.add(lodShortData);
        }

        IData prodParamData = new DataMap();
        prodParamData.put("PRODUCT_ID", grpUcaData.getProductId());
        prodParamData.put("PRODUCT_PARAM", productParamList);
        IDataset prodParamInfo = new DatasetList();
        prodParamInfo.add(prodParamData);

        IData conmmitData = new DataMap();
        conmmitData.put("USER_ID", userIdA);
        conmmitData.put("SERIAL_NUMBER", memUcaData.getSerialNumber());
        conmmitData.put("MEM_ROLE_B", relat.getData(0).getString("ROLE_CODE_B", "1"));
        conmmitData.put("PRODUCT_ID", grpUcaData.getProductId());
        conmmitData.put("PRODUCT_PARAM_INFO", prodParamInfo);
        conmmitData.put("ELEMENT_INFO", elementInfo);
        conmmitData.put("IS_NEED_TRANS", false);

        IDataset dataset = new DatasetList();
        try
        {
            dataset = CSAppCall.call("CS.ChangeMemElementSvc.changeMemElement", conmmitData);
        }
        catch (Throwable e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, Utility.getBottomException(e).getMessage());
        }
        return dataset;
    }
    /************************* 修改成员用户的资费结束 **************************/
}
