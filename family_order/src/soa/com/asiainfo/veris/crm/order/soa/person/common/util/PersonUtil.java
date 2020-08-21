
package com.asiainfo.veris.crm.order.soa.person.common.util;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * 工具类方法
 * 
 * @author Administrator
 */
public class PersonUtil
{

    /**
     * 生成托收合同号 param acctId/bankCode（长度必须大于3）
     * 
     * @return
     * @throws Exception
     */
    public static String createContractNo(String acctId, String bankCode) throws Exception
    {
        StringBuilder contractNo = new StringBuilder(20);
        String tagInfo = "0";
        IDataset taginfos = TagInfoQry.getTagInfosByTagCode(CSBizBean.getUserEparchyCode(), "CS_INF_CONTRACTNOMUSTFILL", "CSM", "0");
        if (IDataUtil.isNotEmpty(taginfos))
        {
            tagInfo = taginfos.getData(0).getString("TAG_INFO");
        }

        if (StringUtils.equals("1", tagInfo))
        {
            contractNo.append("M");
            contractNo.append(bankCode.substring(bankCode.length() - 3));
            contractNo.append(acctId.substring(acctId.length() - 7));
        }
        return contractNo.toString();
    }

    /**
     * @Description: 是否4G卡用户
     * @param userId
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Sep 1, 2014 10:49:03 AM
     */
    public static boolean isLteCardUser(String userId) throws Exception
    {
        IDataset resDatas = UserResInfoQry.getUserResInfosByUserIdResTypeCode(userId, "1");

        if(IDataUtil.isNotEmpty(resDatas))
        {
            String lteTag = resDatas.getData(0).getString("RSRV_TAG3", "");

            if("1".equals(lteTag))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    
    /**
     * 组装产品变更业务的SELECTED_ELEMENTS
     * @param input
     * @param elemIds
     * @param elementTypeCode
     * @return
     * @throws Exception
     */
    public static IDataset buildChangeProdOrder(IData input, String elemIds, String elementTypeCode) throws Exception
    {

        UcaData ud = UcaDataFactory.getNormalUca(IDataUtil.chkParam(input, "SERIAL_NUMBER"));

        IDataset selectedElements = new DatasetList();

        String modifyTag = IDataUtil.chkParam(input, "MODIFY_TAG");

        String[] serviceids = elemIds.split(",");

        DataBusManager.getDataBus().setAcceptTime(SysDateMgr.getSysTime());

        ProductTradeData nextProduct = ud.getUserNextMainProduct();

        String productId = "";

        if (nextProduct != null)
        {
            productId = nextProduct.getProductId();

        }
        else
        {
            productId = ud.getProductId();
        }

        IDataset productElements = ProductInfoQry.getProductElements(productId, ud.getUserEparchyCode());

        for (int j = 0; j < serviceids.length; j++)
        {
            IData data = new DataMap();
            
            String elementId = serviceids[j];

            if ("0".equals(modifyTag))
            {
                data.put("END_DATE", StringUtils.isNotBlank(input.getString("END_DATE")) ? input.getString("END_DATE")
                                                                                        : SysDateMgr.END_DATE_FOREVER);
                data.put("START_DATE",
                         StringUtils.isNotBlank(input.getString("START_DATE")) ? input.getString("START_DATE")
                                                                              : SysDateMgr.getSysTime());

                IDataset pkgElement = DataHelper.filter(productElements, "ELEMENT_TYPE_CODE="+elementTypeCode+",ELEMENT_ID="
                                                                         + serviceids[j]);

                if (IDataUtil.isEmpty(pkgElement))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户主产品和元素[" + serviceids[j]
                                                                         + "]没有订购关系,不能操作此元素！");
                }

                if (StringUtils.isBlank(pkgElement.getData(0).getString("PACKAGE_ID")))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户可订购元素[" + serviceids[j]
                                                                         + "]没有归属产品编码不能为空，请检查配置！");
                }
                data.put("PRODUCT_ID", productId);
                data.put("PACKAGE_ID", pkgElement.getData(0).getString("PACKAGE_ID"));

                IDataset datasetsvc = getUserElementByIdAndType(elementTypeCode, ud, elementId);

                Boolean isCheckAdd = input.getBoolean("ADDCONTINUE", false);

                if (IDataUtil.isNotEmpty(datasetsvc) && isCheckAdd)
                {
                    continue;
                }

                data.put("ELEMENT_ID", serviceids[j]);
                data.put("MODIFY_TAG", modifyTag);
                data.put("ELEMENT_TYPE_CODE", elementTypeCode);
                selectedElements.add(data);
            }
            else if ("1".equals(modifyTag))
            {
                IDataset datasetsvc = getUserElementByIdAndType(elementTypeCode, ud, elementId);

                Boolean isCheckDel = input.getBoolean("DELCONTINUE", false);

                if (isCheckDel)
                {
                    if (IDataUtil.isEmpty(datasetsvc))
                    {
                        continue;
                    }
                }

                if (IDataUtil.isEmpty(datasetsvc))
                {
                    if ("D".equals(elementTypeCode))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户没有订购" + serviceids[j] + "优惠，不能执行该操作！"); 
                    }
                    else if ("S".equals(elementTypeCode))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户没有开通" + serviceids[j] + "服务，不能执行该操作！"); 
                    } 
                }

                for (Iterator iterator = datasetsvc.iterator(); iterator.hasNext();)
                {
                    IData item = (IData) iterator.next();

                    data.put("START_DATE", item.getString("START_DATE"));
                    data.put("END_DATE", SysDateMgr.getSysTime());
                    data.put("PRODUCT_ID", item.getString("PRODUCT_ID"));
                    data.put("PACKAGE_ID", item.getString("PACKAGE_ID"));
                    data.put("INST_ID", item.getString("INST_ID"));
                    data.put("ELEMENT_ID", serviceids[j]);
                    data.put("MODIFY_TAG", modifyTag);
                    data.put("ELEMENT_TYPE_CODE", elementTypeCode);
                    selectedElements.add(data);

                }
            }
        }

        return selectedElements;
    }

    private static IDataset getUserElementByIdAndType(String elementTypeCode, UcaData ud, String elementId)
            throws Exception
    {
        IDataset datasetsvc = null;
        if ("S".equals(elementTypeCode))
        {
            List<SvcTradeData> svcList = ud.getUserSvcBySvcId(elementId); 
            datasetsvc =  listToDataset(svcList);;
        }
        else if ("D".equals(elementTypeCode))
        {
            List<DiscntTradeData> svcList = ud.getUserDiscntByDiscntId(elementId);
            datasetsvc =  listToDataset(svcList);
        }
        else  
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "不支持的变更元素类型:" + elementTypeCode);
        }
        return datasetsvc;
    }
    
    public static IDataset listToDataset(List<? extends BaseTradeData> ora )
    {
        IDataset result = new DatasetList();
        if (CollectionUtils.isEmpty(ora))
        {
            return result;   
        }
        for (Iterator iterator = ora.iterator(); iterator.hasNext();)
        {
            BaseTradeData baseTradeData = (BaseTradeData) iterator.next();
            result.add(baseTradeData.toData());
            
        }
        return result;
    }
    
}
