/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

/**
 * @CREATED by gongp@2014-4-14 修改历史 Revision 2014-4-14 下午04:24:29
 */
public class ChlCommFeeSubSVC extends CSBizService
{

    private static final long serialVersionUID = 1807906356685914418L;

    /**
     * 格式化资费编码
     * 
     * @param dataset
     * @param columnName
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-19
     */
    public String fromDatasetToString(IDataset dataset, String columnName) throws Exception
    {
        StringBuilder builder = new StringBuilder();
        for (Iterator iter = dataset.iterator(); iter.hasNext();)
        {
            IData element = (IData) iter.next();
            builder.append(element.getString(columnName));
            builder.append(",");
        }
        return builder.length() > 0 ? builder.substring(0, builder.length() - 1).toString() : "";
    }

    /**
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-4-16
     */
    public IDataset getChlCode(IData input) throws Exception
    {

        return CommparaInfoQry.getChlCode(input);
    }

    /**
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-4-16
     */
    public IDataset getChlName(IData input) throws Exception
    {

        return CommparaInfoQry.getChlName(input.getString("PARA_CODE1"));
    }

    public IDataset getCustTypeOther(IData input) throws Exception
    {
        return null;
    }

    public IDataset getUserInfoChange(IData input) throws Exception
    {
        return UserInfoQry.getUserInfoChgByUserIdNxtvalid(input.getString("USER_ID"));
    }

    public IDataset loadChildInfo(IData input) throws Exception
    {

        IDataset paramDatas = CommparaInfoQry.getCommparaAllCol("CSM", "655", "CHL_COMM", "0898");

        if (IDataUtil.isEmpty(paramDatas))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未配置渠道通讯费补贴管理参数!");
        }

        IData paramData = paramDatas.getData(0);

        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");

        String userDiscntStr = "";

        IData info = new DataMap();

        if ("6200".equals(tradeTypeCode))
        {
            IDataset result = UserDiscntInfoQry.getAllDiscntByUser(input.getString("USER_ID"), paramData.getString("PARA_CODE1"));// ParamInfoQry.getVpmnJpaInfo(input.getString("USER_ID"));//discnt_code
            // =655
            if (IDataUtil.isEmpty(result))
            {
                // common.error("请先办理代理商套餐(VPMN JPA)才能办理该业务!");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "请先办理代理商套餐(VPMN JPA)才能办理该业务!");
            }

            IDataset discnt = ParamInfoQry.getChlDiscntInfo(input.getString("USER_ID"));

            if (discnt != null && discnt.size() >= 2)
            {
                // common.error( "该号码已办理过补贴!");//本月
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码已办理过补贴!");
            }

            IDataset userDiscnts = UserDiscntInfoQry.getUserDiscntByUserIdUserIdA(input.getString("USER_ID"), "-1");

            userDiscntStr = fromDatasetToString(userDiscnts, "DISCNT_CODE");

            info.put("PHONE_SUB", userDiscntStr.indexOf(paramData.getString("PARA_CODE2")) != -1 || userDiscntStr.indexOf(paramData.getString("PARA_CODE3")) != -1 ? "0" : "1");// 如果已经办理了该套餐，则置为否
            info.put("NEW_TRADE", userDiscntStr.indexOf(paramData.getString("PARA_CODE4")) != -1 || userDiscntStr.indexOf(paramData.getString("PARA_CODE5")) != -1 ? "0" : "1");// 如果已经办理了该套餐，则置为否

        }
        else if ("6201".equals(tradeTypeCode))
        {

            IDataset userDiscnt = this.queryUserNextMonthValidDiscnt(input);

            userDiscntStr = fromDatasetToString(userDiscnt, "DISCNT_CODE");

            if (userDiscntStr == null
                    || (userDiscntStr.indexOf(paramData.getString("PARA_CODE2")) == -1 && userDiscntStr.indexOf(paramData.getString("PARA_CODE3")) == -1 && userDiscntStr.indexOf(paramData.getString("PARA_CODE4")) == -1 && userDiscntStr
                            .indexOf(paramData.getString("PARA_CODE5")) == -1))
            {
                // common.error("","您还没有办理相应补贴或已经被删除不能办理该业务");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您还没有办理相应补贴或已经被删除不能办理该业务!");
            }

            info.put("PHONE_SUB", userDiscntStr.indexOf(paramData.getString("PARA_CODE2")) != -1 || userDiscntStr.indexOf(paramData.getString("PARA_CODE3")) != -1 ? "1" : "0");// 如果已经办理了该套餐，则置为是
            info.put("NEW_TRADE", userDiscntStr.indexOf(paramData.getString("PARA_CODE4")) != -1 || userDiscntStr.indexOf(paramData.getString("PARA_CODE5")) != -1 ? "1" : "0");// 如果已经办理了该套餐，则置为是
        }

        IDataset otherSet = ParamInfoQry.getChnlOtherInfo(input.getString("USER_ID"));

        if (IDataUtil.isNotEmpty(otherSet))
        {
            otherSet.getData(0).putAll(info);
        }
        else
        {
            IDataset numberInfos = ParamInfoQry.getNumberDepartInfo(input.getString("SERIAL_NUMBER"));
            if (IDataUtil.isNotEmpty(numberInfos))
            {
                IData temp = numberInfos.getData(0);
                info.put("NUMBER_DEPART", temp.getString("CUMU_DEPART_ID"));
            }
            otherSet.add(info);
        }

        return otherSet;
    }

    /**
     * 查询用户下月有效优惠
     * 
     * @param userId
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-4-15
     */
    public IDataset queryUserNextMonthValidDiscnt(IData input) throws Exception
    {

        return UserDiscntInfoQry.queryUserNextMonthValidDiscnt(input.getString("USER_ID"));

    }
    
    
    /**
     * 查询品牌名称和产品名称,调产商品接口
     * @param input
     * @return
     * @throws Exception
     * add by duhj 
     * 2017/03/06
     * 
     * 原逻辑当前品牌当前产品与下个月品牌产品一样，应该是是有问题，暂时按原有逻辑
     */
    public IData getUserName(IData input) throws Exception
    {
    	IData  resuData=new DataMap();
        String productName = UProductInfoQry.getProductNameByProductId(input.getString("PRODUCT_ID"));

        String brandName=UBrandInfoQry.getBrandNameByBrandCode(input.getString("BRAND_CODE"));
        resuData.put("PRODUCT_NAME", productName);
        resuData.put("BRAND_NAME", brandName);



        return resuData;
    }

}
