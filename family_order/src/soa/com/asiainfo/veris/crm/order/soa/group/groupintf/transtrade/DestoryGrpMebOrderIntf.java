
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

/**
 * 集团成员产品退订接口(支持一到多)
 * 
 * @author sungq3
 */
public class DestoryGrpMebOrderIntf
{
    /**
     * 集团成员产品退订
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public static IDataset destoryGrpMebOrder(IData inparam) throws Exception
    {
        // 集团用户id
        IDataset resDataset = inparam.getDataset("USER_ID_A");

        if (IDataUtil.isEmpty(resDataset))
        {
            String userIdA = inparam.getString("USER_ID_A", "-1");
            resDataset = new DatasetList();
            resDataset.add(userIdA);
        }

        // 成员手机号码
        String serial_number = IDataUtil.getMandaData(inparam, "SERIAL_NUMBER");

        // 操作员工
        String staff_id = IDataUtil.getMandaData(inparam, "CREATE_STAFF_ID");

        // 操作部门
        String depart_id = IDataUtil.getMandaData(inparam, "CREATE_DEPART_ID");

        // 所在地市
        String city_code = IDataUtil.getMandaData(inparam, "CREATE_CITY_CODE");

        // 接入方式
        String in_mode_code = CSBizBean.getVisit().getInModeCode();
        // String in_mode_code = IDataUtil.getMandaData(inparam, "IN_MODE_CODE");

        // 查询退订成员的用户信息，获取用户id
        IData userData = UcaInfoQry.qryUserInfoBySn(serial_number);
        if (IDataUtil.isEmpty(userData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serial_number);
        }

        String eparchyCode = userData.getString("EPARCHY_CODE");

        IDataset resultDataset = new DatasetList();

        // 返回参数定义
        IData result = new DataMap();

        StringBuilder tradeIds = new StringBuilder();

        // 成员退订处理
        for (int i = 0, size = resDataset.size(); i < size; i++)
        {
            // 集团用户编码
            String userId = (String) resDataset.get(i);
            
            IData userMainProdInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
            
            if (IDataUtil.isEmpty(userMainProdInfo))
            {
                continue;
            }
            String productId = userMainProdInfo.getString("PRODUCT_ID");
            // 设置服务入参
            IData inData = new DataMap();
            inData.put("SERIAL_NUMBER", serial_number); // 成员手机号码
            inData.put("USER_ID", userId);
            inData.put("USER_ID_A", userId); // 集团用户id
            inData.put("TRADE_STAFF_ID", staff_id); // 操作员
            inData.put("TRADE_DEPART_ID", depart_id); // 所属部门
            inData.put("TRADE_CITY_CODE", city_code); // 区域编码
            inData.put("TRADE_EPARCHY_CODE", eparchyCode);// 地州编码
            inData.put("IN_MODE_CODE", in_mode_code); // 接入方式
            inData.put("PRODUCT_ID", userMainProdInfo.getString("PRODUCT_ID"));//产品ID取库中的  不取入参中的
            inData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

            String merchProductId=GrpCommonBean.productToMerch(productId, 0);
        	if("5001301".equals(merchProductId)){//集团V网（全国版）
        		resultDataset = CSAppCall.call("CS.SynMebInfoToBBossForJKSVC.synMeb", inData);
        	}else{
        		 // 调用集团成员退订服务
                resultDataset = CSAppCall.call("CS.DestroyGroupMemberSvc.destroyGroupMember", inData);
        	}
            // 设置订单号
            tradeIds.append("#").append(resultDataset.getData(0).getString("TRADE_ID"));
        }

        // 设置返回参数({ "0", "受理成功" })
        result.put("TRADE_ID", tradeIds.length() > 0 ? tradeIds.substring(1).toString() : "");
        result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        return IDataUtil.idToIds(result);
    }

    /**
     * 过滤校讯通产品
     * 
     * @param resDataset
     *            需要退订的成员产品及集团用户id
     * @return
     * @throws Exception
     */
    protected static IDataset filterXXTProduct(IDataset resDataset) throws Exception
    {
        // 存放校讯通产品
        IDataset xxtList = new DatasetList();
        for (int i = 0, size = resDataset.size(); i < size; i++)
        {
            IData resData = resDataset.getData(i);

            // 校验集团编码
            IDataUtil.chkParam(resData, "GROUP_ID");
            // 校验集团用户编码
            IDataUtil.chkParam(resData, "USER_ID_A");
            // 校验集团用户产品编码
            String productId = IDataUtil.chkParam(resData, "PRODUCT_ID");

            if (productId.indexOf("-") != -1)
            {
                String[] prodDis = productId.split("-");
                if (prodDis.length < 2 && "10005742".equals(prodDis[0]))
                {
                    // 接口参数检查:请验证该用户[校讯通产品]优惠数据完整性！
                    CSAppException.apperr(GrpException.CRM_GRP_729);
                }
                productId = prodDis[0];
                resData.put("PRODUCT_ID", productId);
                resData.put("DISCNT_CODE", prodDis[1]);

                if ("10005742".equals(productId))
                { // 如果是校讯通产品
                    xxtList.add(resData);
                    resDataset.remove(resData);
                }
            }
            else
            {
                if ("10005742".equals(productId))
                {
                    // 接口参数检查:请验证该用户[校讯通产品]优惠数据完整性！
                    CSAppException.apperr(GrpException.CRM_GRP_729);
                }
            }
        }
        return xxtList;
    }

    /**
     * 解析输入参数，将IData转换成IDataset
     * 
     * @param data
     *            输入参数
     * @param resDataset
     *            返回解析结果
     * @param keyStr
     *            需要提取的参数
     * @throws Exception
     */
    public static void parseMap2List(IData data, IDataset resDataset, String keyStr) throws Exception
    {
        if (!data.containsKey(keyStr))
        {
            CSAppException.apperr(GrpException.CRM_GRP_728, keyStr);
        }
        String dataStr = data.get(keyStr).toString();
        if (dataStr.indexOf("[") != -1)
        {
            IDataset dataSet = data.getDataset(keyStr);
            if (dataSet != null)
            {
                for (int i = 0, size = dataSet.size(); i < size; i++)
                {
                    String keyValue = dataSet.get(i).toString();
                    IData keyMap;
                    if (resDataset.size() == i)
                    {
                        keyMap = new DataMap();
                    }
                    else
                    {
                        keyMap = resDataset.getData(i);
                    }
                    keyMap.put(keyStr, keyValue);
                    if (resDataset.size() == i)
                    {
                        resDataset.add(i, keyMap);
                    }
                }
            }
        }
        else
        {
            IData keyMap;
            if (resDataset.size() < 1)
            {
                keyMap = new DataMap();
            }
            else
            {
                keyMap = resDataset.getData(0);
            }
            keyMap.put(keyStr, data.getString(keyStr));
            if (resDataset.size() == 0)
            {
                resDataset.add(0, keyMap);
            }
        }
    }
}
