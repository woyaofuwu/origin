
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class ModifyMebDiscnt
{

    /**
     * 修改成员资费信息
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset modifyMebDiscnt(IData inParam) throws Exception
    {
        String userIdA = IDataUtil.chkParam(inParam, "USER_ID_A");
        String userId = IDataUtil.chkParam(inParam, "USER_ID_B");
        String serialNumber = IDataUtil.chkParam(inParam, "SERIAL_NUMBER");

        IData grpUserData = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userIdA);

        if (IDataUtil.isEmpty(grpUserData))
        {
            CSAppException.apperr(GrpException.CRM_GRP_122);
        }

        String productId = grpUserData.getString("PRODUCT_ID");

        String discntCodeStr = IDataUtil.chkParam(inParam, "DISCNT_CODE");
        String modifyTag = IDataUtil.chkParam(inParam, "MODIFY_TAG");

        IDataset elementList = new DatasetList();

        String[] discntCodeArray = StringUtils.split(discntCodeStr, ",");

        // 遍历资费
        for (int i = 0; i < discntCodeArray.length; i++)
        {
            String discntCode = discntCodeArray[i];

            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                // 集团定制
                if (ProductCompInfoQry.ifGroupCustomize(productId))
                {
                    IDataset customizeDiscntList = GrpUserPkgInfoQry.getGrpCustomizeDiscntByUserId(userIdA, null);

                    if (IDataUtil.isEmpty(customizeDiscntList))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_89);
                    }

                    for (int j = 0, jRow = customizeDiscntList.size(); j < jRow; j++)
                    {
                        IData customizeData = customizeDiscntList.getData(j);
                        String customizeDiscntCode = customizeData.getString("ELEMENT_ID");

                        if (discntCode.equals(customizeDiscntCode))
                        {
                            IData elementData = new DataMap();
                            elementData.put("PRODUCT_ID", customizeData.getString("PRODUCT_ID"));
                            elementData.put("PACKAGE_ID", customizeData.getString("PACKAGE_ID"));
                            elementData.put("ELEMENT_ID", discntCode);
                            elementData.put("ELEMENT_TYPE_CODE", "D");
                            elementData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                            // 获取元素生效方式
                            String enableTag = "0";
                            IData descData = PkgElemInfoQry.getElementByElementId(customizeData.getString("PACKAGE_ID"), "D", discntCode);

                            if (IDataUtil.isNotEmpty(descData))
                            {
                                enableTag = descData.getString("ENABLE_TAG");
                            }

                            String startDate = SysDateMgr.getSysTime();

                            if ("1".equals(enableTag))
                            {
                                startDate = SysDateMgr.getFirstDayOfNextMonth();
                            }
                            else if ("2".equals(enableTag))
                            {
                                startDate = SysDateMgr.getTomorrowDate();
                            }

                            elementData.put("START_DATE", startDate);
                            elementData.put("END_DATE", SysDateMgr.getTheLastTime());

                            // 添加元素
                            elementList.add(elementData);
                        }
                    }
                }
                else
                // 非集团定制
                {
                    IDataset mebProductList = ProductMebInfoQry.getMebProduct(productId);

                    if (IDataUtil.isEmpty(mebProductList))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_116, productId);
                    }

                    for (int j = 0; j < mebProductList.size(); j++)
                    {
                        String mebProductId = mebProductList.getData(j).getString("PRODUCT_ID_B");

                        IData pkgElementData = PkgElemInfoQry.getDiscntsByDiscntCode(discntCode, mebProductId, CSBizBean.getUserEparchyCode());

                        if (IDataUtil.isNotEmpty(pkgElementData))
                        {
                            IData elementData = new DataMap();
                            elementData.put("PRODUCT_ID", mebProductId);
                            elementData.put("PACKAGE_ID", pkgElementData.getString("PACKAGE_ID"));
                            elementData.put("ELEMENT_ID", discntCode);
                            elementData.put("ELEMENT_TYPE_CODE", "D");
                            elementData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                            // 获取元素生效方式
                            String enableTag = "0";
                            IData descData = PkgElemInfoQry.getElementByElementId(pkgElementData.getString("PACKAGE_ID"), "D", discntCode);

                            if (IDataUtil.isNotEmpty(descData))
                            {
                                enableTag = descData.getString("ENABLE_TAG");
                            }

                            String startDate = SysDateMgr.getSysTime();

                            if ("1".equals(enableTag))
                            {
                                startDate = SysDateMgr.getFirstDayOfNextMonth();
                            }
                            else if ("2".equals(enableTag))
                            {
                                startDate = SysDateMgr.getTomorrowDate();
                            }

                            elementData.put("START_DATE", startDate);
                            elementData.put("END_DATE", SysDateMgr.getTheLastTime());

                            // 添加元素
                            elementList.add(elementData);
                        }
                    }
                }
            }
            else if (TRADE_MODIFY_TAG.MODI.getValue().equals(modifyTag) || TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
            {
                IDataset userDiscntList = UserDiscntInfoQry.getUserDiscntByUserIdAB(userId, userIdA);

                for (int j = 0; j < userDiscntList.size(); j++)
                {
                    IData userDiscntData = userDiscntList.getData(j);

                    if (discntCode.equals(userDiscntData.getString("DISCNT_CODE")))
                    {
                        userDiscntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        userDiscntData.put("ELEMENT_ID", discntCode);

                        IData descData = PkgElemInfoQry.getElementByElementId(userDiscntData.getString("PACKAGE_ID"), "D", discntCode);

                        String cancelTag = "0";

                        String endDate = SysDateMgr.getSysTime();

                        if (IDataUtil.isNotEmpty(descData))
                        {
                            cancelTag = descData.getString("CANCEL_TAG");
                        }

                        if ("1".equals(cancelTag))
                        {
                            endDate = SysDateMgr.getYesterdayTime();
                        }
                        else if ("2".equals(cancelTag))
                        {
                            endDate = SysDateMgr.getSysDate() + SysDateMgr.END_DATE;
                        }
                        else if ("3".equals(cancelTag))
                        {
                            endDate = SysDateMgr.getLastDateThisMonth();
                        }

                        userDiscntData.put("END_DATE", endDate);

                        // 添加元素
                        elementList.add(userDiscntData);
                    }
                }
            }

        }

        IData svcData = new DataMap();
        svcData.put("USER_ID", userIdA); // 集团用户ID
        svcData.put("SERIAL_NUMBER", serialNumber); // 成员服务号码
        svcData.put("PRODUCT_ID", productId);// 集团产品编码
        svcData.put("IF_CENTRETYPE", null);
        svcData.put("ELEMENT_INFO", elementList);

        IDataset userAttrList = UserAttrInfoQry.getUserAttrByUserId(userId);

        IData productParam = new DataMap();
        productParam.put("PRODUCT_ID", productId);
        productParam.put("PRODUCT_PARAM", userAttrList);

        svcData.put("PRODUCT_PARAM_INFO", IDataUtil.idToIds(productParam));

        return CSAppCall.call("CS.ChangeMemElementSvc.changeMemElement", svcData);
    }
}
