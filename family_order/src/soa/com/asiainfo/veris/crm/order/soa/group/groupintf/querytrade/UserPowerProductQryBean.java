
package com.asiainfo.veris.crm.order.soa.group.groupintf.querytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class UserPowerProductQryBean
{

    public static IDataset getProductInfo(IDataset productList, IDataset userList, String userId) throws Exception
    {
        IDataset addProductList = new DatasetList();
        IData addProductData = null;

        for (int i = 0; i < productList.size(); i++)
        {
            IData productData = productList.getData(i);

            productData.put("IS_EXIST", "false");

            for (int j = 0; j < userList.size(); j++)
            {
                IData userData = userList.getData(j);

                if (userData.getString("PRODUCT_ID", "").equals(productData.getString("PRODUCT_ID_B")))
                {
                    if ("true".equals(productData.getString("IS_EXIST_UU")))
                    {
                        addProductData = new DataMap();
                        addProductData.putAll(productData);
                        addProductData.put("USER_ID", userData.getString("USER_ID"));
                        addProductData.put("USER_ID_A", userId);
                        addProductData.put("OPEN_DATE", userData.getString("OPEN_DATE"));
                        addProductData.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
                        addProductData.remove("DISCNT_CODE");
                        addProductData.remove("DISCNT_NAME");
                        IDataset discntList = UserDiscntInfoQry.getUserSingleProductDisParser(userData.getString("USER_ID"), userId, productData.getString("PRODUCT_ID_B"), null, null, null, null);
                        if (IDataUtil.isNotEmpty(discntList))
                        {
                            StringBuilder discntCode = new StringBuilder();
                            StringBuilder discntName = new StringBuilder();
                            for (int z = 0; z < discntList.size(); z++)
                            {
                                IData discntData = discntList.getData(z);
                                discntCode.append(" [" + discntData.getString("DISCNT_CODE"));
                                discntCode.append("]");
                                discntCode.append(" [" + UDiscntInfoQry.getDiscntNameByDiscntCode(discntData.getString("DISCNT_CODE")));
                                discntCode.append("  " + discntData.getString("START_DATE") + "--");
                                discntCode.append(" " + discntData.getString("END_DATE"));
                                discntCode.append("]");
                            }
                            addProductData.put("DISCNT_CODE", discntCode.toString());
                            addProductData.put("DISCNT_NAME", discntName.toString());
                            addProductList.add(addProductData);
                        }
                    }
                    else
                    {
                        productData.put("IS_EXIST_UU", "true");
                        productData.put("IS_EXIST", "true");
                        productData.put("USER_ID", userData.getString("USER_ID"));
                        productData.put("OPEN_DATE", userData.getString("OPEN_DATE"));
                        productData.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
                        productData.put("USER_ID_A", userId);

                        IDataset userDiscntList = UserDiscntInfoQry.getUserSingleProductDisParser(userData.getString("USER_ID"), userId, productData.getString("PRODUCT_ID_B"), null, null, null, null);
                        if (IDataUtil.isNotEmpty(userDiscntList))
                        {
                            StringBuilder discntCode = new StringBuilder();
                            StringBuilder discntName = new StringBuilder();
                            for (int z = 0; z < userDiscntList.size(); z++)
                            {
                                IData discntData = userDiscntList.getData(z);
                                discntCode.append(" [" + discntData.getString("DISCNT_CODE"));
                                discntCode.append("]");
                                discntName.append(" [" + UDiscntInfoQry.getDiscntNameByDiscntCode(discntData.getString("DISCNT_CODE")));
                                discntName.append("  " + discntData.getString("START_DATE") + "--");
                                discntName.append(" " + discntData.getString("END_DATE"));
                                discntName.append("]");
                            }
                            productData.put("DISCNT_CODE", discntCode.toString());
                            productData.put("DISCNT_NAME", discntName.toString());
                        }
                    }
                }
            }
        }
        productList.addAll(addProductList);
        return productList;
    }

    public static IDataset qryUserPowerProductInfo(IData data) throws Exception
    {
        String groupId = IDataUtil.getMandaData(data, "GROUP_ID");
        String productId = IDataUtil.getMandaData(data, "PRODUCT_ID");
        String operCode = IDataUtil.getMandaData(data, "OPER_CODE");

        IDataset retDataset = new DatasetList();

        // 查询客户信息
        IData grpCustData = UcaInfoQry.qryGrpInfoByGrpId(groupId);

        if (IDataUtil.isEmpty(grpCustData))
        {
            CSAppException.apperr(CustException.CRM_CUST_996, groupId);
        }

        String custId = grpCustData.getString("CUST_ID", "");

        // 查询必选产品
        IDataset forceProductList = ProductInfoQry.queryProductByComp(productId, "1", "1");

        // 查询可选产品
        IDataset productList = ProductInfoQry.queryProductByComp(productId, "1", "0");

        productList.addAll(forceProductList);

        if (IDataUtil.isEmpty(productList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_163);
        }

        // 1--新增 2--删除 3--修改
        if ("1".equals(operCode))
        {
            IData param = new DataMap();
            param.put("CUST_ID", custId);
            param.put("PRODUCT_ID", productId);

            IDataset userInfoList = queryUserInfo(param);

            retDataset = spellProductInfoByOpen(productList, userInfoList);

        }
        else if ("2".equals(operCode) || "3".equals(operCode))
        {
            String userId = IDataUtil.getMandaData(data, "USER_ID");

            // 动力100的产品关系类型
            String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);

            IDataset relaInfoList = RelaUUInfoQry.getRelaUUInfoByUserIda(userId, relationTypeCode, null);

            if (IDataUtil.isEmpty(relaInfoList))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_948);
            }

            // UU关系中存在的用户列表
            IDataset existUserInfoList = new DatasetList();

            // 遍历UU关系
            for (int i = 0; i < relaInfoList.size(); i++)
            {
                IData relaData = relaInfoList.getData(i);

                String userIdB = relaData.getString("USER_ID_B", "");

                IDataset userInfoList = UserInfoQry.qryUserAndProductByUserIdForGrp(userIdB);

                if (IDataUtil.isEmpty(userInfoList))
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_682, userIdB);
                }

                existUserInfoList.add(userInfoList.getData(0));
            }

            IDataset productInfoList = getProductInfo(productList, existUserInfoList, userId);

            IData param = new DataMap();
            param.put("CUST_ID", custId);
            param.put("PRODUCT_ID", productId);

            IDataset userInfoList = queryUserInfo(param);

            retDataset = spellProductInfoByChange(productInfoList, userInfoList);

        }
        else
        {
            CSAppException.apperr(ParamException.CRM_PARAM_433, operCode);
        }

        return retDataset;
    }

    /**
     * 查询用户信息
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset queryUserInfo(IData inparam) throws Exception
    {
        String custId = inparam.getString("CUST_ID");
        String productId = inparam.getString("PRODUCT_ID");

        // 查询集团用户信息
        IDataset userInfoList = UserInfoQry.qryUserAndProductByCustIdForGrp(custId, null);

        // 动力100的产品关系类型
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(productId);

        // 动力100下产品对应的用户列表, 组成其他动力100的产品对应的用户
        IDataset returnDataset = new DatasetList();

        for (int i = 0; i < userInfoList.size(); i++)
        {
            IData userInfoData = userInfoList.getData(i);
            IDataset relaInfoList = RelaUUInfoQry.getRelaUUInfoByRolForGrp(userInfoData.getString("USER_ID", ""), relationTypeCode, null);
            if (IDataUtil.isEmpty(relaInfoList))
            {
                returnDataset.add(userInfoData);
            }
        }

        return returnDataset;
    }

    public static IDataset spellProductInfoByChange(IDataset productList, IDataset userList) throws Exception
    {
        IData userData = null;
        IData productData = null;

        IDataset addProductList = new DatasetList();
        IData addProductData = null;

        int max = userList.size();

        for (int x = 0; x < productList.size(); x++)
        {
            productData = productList.getData(x);

            for (int y = 0; y < max; y++)
            {
                userData = userList.getData(y);
                if (userData.getString("PRODUCT_ID").equals(productData.getString("PRODUCT_ID_B")))
                {
                    if ("".equals(productData.getString("USER_ID", "")))
                    {
                        productData.put("IS_EXIST_UU", "false");
                        productData.put("IS_EXIST", "true");
                        productData.put("USER_ID", userData.getString("USER_ID"));
                        productData.put("OPEN_DATE", userData.getString("OPEN_DATE"));
                        productData.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
                    }
                    else
                    {
                        addProductData = new DataMap();
                        addProductData.putAll(productData);
                        addProductData.put("USER_ID", userData.getString("USER_ID"));
                        addProductData.put("OPEN_DATE", userData.getString("OPEN_DATE"));
                        addProductData.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
                        addProductData.put("IS_EXIST_UU", "false");
                        addProductData.remove("DISCNT_CODE");
                        addProductData.remove("DISCNT_NAME");
                        addProductList.add(addProductData);
                    }
                }
            }
        }
        productList.addAll(addProductList);
        productList.removeAll(DataHelper.filter(productList, "IS_EXIST=false"));

        return productList;
    }

    /**
     * 解析集团产品受理产品信息
     * 
     * @param productList
     * @param userInfoList
     * @return
     * @throws Exception
     */
    public static IDataset spellProductInfoByOpen(IDataset productList, IDataset userInfoList) throws Exception
    {

        IDataset addProductList = new DatasetList();
        IData addProductData = null;

        for (int i = 0; i < productList.size(); i++)
        {
            IData productData = productList.getData(i);
            productData.put("USER_ID", "");
            productData.put("OPEN_DATE", "");
            productData.put("SERIAL_NUMBER", "");

            for (int j = 0; j < userInfoList.size(); j++)
            {
                IData userInfoData = userInfoList.getData(j);
                if (userInfoData.getString("PRODUCT_ID", "").equals(productData.getString("PRODUCT_ID_B")))
                {
                    if ("true".equals(productData.getString("IS_EXIST")))
                    {
                        addProductData = new DataMap();
                        productData.put("IS_MANY", "true");// 同一产品多个用户
                        addProductData.putAll(productData);

                        addProductData.put("USER_ID", userInfoData.getString("USER_ID", ""));
                        addProductData.put("OPEN_DATE", userInfoData.getString("OPEN_DATE"));
                        addProductData.put("SERIAL_NUMBER", userInfoData.getString("SERIAL_NUMBER"));
                        addProductList.add(addProductData);
                    }
                    else
                    {
                        productData.put("IS_EXIST", "true");
                        productData.put("USER_ID", userInfoData.getString("USER_ID", ""));
                        productData.put("OPEN_DATE", userInfoData.getString("OPEN_DATE"));
                        productData.put("SERIAL_NUMBER", userInfoData.getString("SERIAL_NUMBER"));
                    }
                }
            }
        }
        productList.addAll(addProductList);
        return productList;
    }
}
