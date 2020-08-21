
package com.asiainfo.veris.crm.order.web.frame.csview.group.bbossorderopen;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.ECFetionConstants;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class BbossOrderOpen extends CSBasePage
{
	/**
	 * 页面初始化方法
	 * @param cycle
	 * @throws Exception
	 * @Author:chenzg
	 * @Date:2017-3-27
	 */
	public void initial(IRequestCycle cycle) throws Exception
    {
		this.setPoInfos(UpcViewCall.queryPoByValid(this));
    }
    /**
     * ajax传参方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void ajaxSetPospecNumber(IRequestCycle cycle) throws Exception
    {
    	IData pData = this.getData();
    	String pospecnumber = pData.getString("cond_POSPECNUMBER", "");
    	this.setPoProducts(UpcViewCall.queryPoproductByPospecNumber(this, pospecnumber));
    }

    /**
     * 发送竣工
     * 
     * @author guyx
     * @throws Exception
     */
    public void BbossComplete(IRequestCycle cycle) throws Exception
    {
        // OrderOpenDao bean = new OrderOpenDao(pd);

        // IData iData = getData();
        IDataset orders = new DatasetList(getData().getString("SELECTEDORDERS"));
        String result_tag = getParameter("result_tag", "0");
        String rspCode = StringUtils.equals("0", result_tag) ? "00" : "99";
        String rspDesc = StringUtils.equals("00", rspCode) ? "受理成功" : "其他错误";

        for (int i = 0, size = orders.size(); i < size; i++)
        {
            IData currentOrder = orders.getData(i);
            String prodOrder = currentOrder.getString("PRODUCTORDERNUMBER");
            String productSpecNumber = currentOrder.getString("PRODUCTSPECNUMBER");
            String accept_month = currentOrder.getString("ACCEPT_MONTH");

            IData map = new DataMap();
            map.put("ORDER_NO", prodOrder);
            map.put("RSPCODE", rspCode);
            map.put("RSPDESC", rspDesc);
            // map.put("OPR_TIME", DualMgr.getSysDate4Id(pd));

            // 反馈结果为成功,需要拼入反馈属性

            if (StringUtils.equals("00", rspCode))
            {
                // 取反馈属性值

                IData data = new DataMap();
                data.put("PRODUCTSPECNUMBER", productSpecNumber);
                data.put("PRODUCTORDERNUMBER", prodOrder);
                data.put("ACCEPT_MONTH", accept_month);
                data.put("RESULT_TAG", rspCode);
                data.put("OPERATIONSUBTYPEID", currentOrder.getString("OPERATIONSUBTYPEID"));

                // 检查是否有反馈属性

                IDataset extendCharacters = getExtendCharacters(data);

                // 如果有反馈属性，查询并且拼入报文
                if (null != extendCharacters && extendCharacters.size() > 0)
                {
                    IDataset orderExtends = CSViewCall.call(this, "CS.CommparaInfoQrySVC.queryBbossOrderExtendsRecord", data);
                    // 查询不到反馈值或者查询到的反馈值比需要反馈的数量不符，报错提示

                    if (orderExtends == null || orderExtends.size() == 0 || orderExtends.size() != extendCharacters.size())
                    {
                    }
                    IDataset characterValue = new DatasetList();
                    IDataset characterName = new DatasetList();
                    IDataset characterId = new DatasetList();
                    IDataset characterGroup = new DatasetList();
                    for (int n = 0, sizes = orderExtends.size(); n < sizes; n++)
                    {
                        String curCGroup = orderExtends.getData(n).getString("CHARACTERGROUP", "");
                        // 反馈属性成组,需要拆分

                        if (curCGroup.indexOf(";") > 0)
                        {
                            String cgroups[] = StringUtils.split(curCGroup, ";");
                            String cvalues[] = StringUtils.split(orderExtends.getData(n).getString("CHARACTERVALUE"), ";");
                            for (int j = 0, length = cgroups.length; j < length; j++)
                            {
                                characterId.add(orderExtends.getData(n).getString("CHARACTERID"));
                                characterName.add(orderExtends.getData(n).getString("CHARACTERNAME"));
                                characterValue.add(cvalues[j]);
                                characterGroup.add(cgroups[j]);
                            }
                        }
                        else
                        {
                            characterId.add(orderExtends.getData(n).getString("CHARACTERID"));
                            characterName.add(orderExtends.getData(n).getString("CHARACTERNAME"));
                            characterValue.add(orderExtends.getData(n).getString("CHARACTERVALUE"));
                            characterGroup.add(orderExtends.getData(n).getString("CHARACTERGROUP", ""));
                        }
                    }
                    map.put("CHARACTER_ID", characterId);
                    map.put("CHARACTER_NAME", characterName);
                    map.put("CHARACTER_VALUE", characterValue);
                    map.put("CHARACTER_GROUP", characterGroup);
                }
            }
            // 更新受理状态

            CSViewCall.call(this, "CS.OrderOpenDaoSVC.modifyOrderInfoState", map);
        }
    }

    /**
     * AJAX调用，检查该工单对应的产品是否有反馈属性
     * 
     * @author liuyt3
     */
    public void checkBbossOrderOpenExtendsExists(IRequestCycle cycle) throws Exception
    {
        String order_info = getData().getString("ORDERINFO");
        if (StringUtils.equals("", order_info))
        {
            IData orderInfo = new DataMap();
            // 检查是否有反馈属性

            IDataset extendCharacters = getExtendCharacters(orderInfo);
            boolean existsExtends = false;
            if (null != extendCharacters && extendCharacters.size() > 0)
            {
                existsExtends = true;
            }
            IData data = new DataMap();
            data.put("EXISTSEXTENDS", existsExtends);
            data.put("SQLPARAMS", extendCharacters);
            data.put("ORDERINFO", orderInfo);
            setAjax(data);
        }
    }

    /**
     * 检查工单是否为企业飞信工单，以及集团是否支持语音
     * 
     * @param cycle
     * @return 支持语音或者不是企业飞信工单返回false,是企业飞信工单并且不支持语音返回true
     * @throws Exception
     */
    private boolean checkECFetionVideoUnsupport(IData order) throws Exception
    {
        // OrderOpenDao bean = new OrderOpenDao();
        boolean isSupport = false;
        IData para = new DataMap();
        String poProductSpecNumber = order.getString("POPRODUCTSPECNUMBER", "");
        para.put("PRODUCTORDERNUMBER", order.getString("PRODUCTORDERNUMBER", ""));
        IDataOutput dop = CSViewCall.callPage(this, "CS.CommparaInfoQrySVC.orderDetailQry", para, getPagination("infonav"));// CommparaInfoQry.orderDetailQry(para,
        // null);// 查询工单属性

        IDataset result = dop.getData();
        String filterValue = StringUtils.equals(ECFetionConstants.FC_POP_ID_LOCAL, poProductSpecNumber) ? ECFetionConstants.FC_PARAM_SUPPORT_AUDIO_LOCAL : ECFetionConstants.FC_PARAM_SUPPORT_AUDIO_WHOLE;
        IDataset temp = DataHelper.filter(result, "PRODUCTSPECCHARACTERNUMBER=" + filterValue + ",ACTION=增加");// 过滤出语音属性

        // 如果不支持语音，返回true
        if (temp != null && temp.size() > 0 && StringUtils.equals("02", temp.getData(0).getString("CHARACTERVALUE", "")))
        {
            isSupport = true;
        }
        return isSupport;
    }

    /**
     * 检查该工单对应的产品是否有反馈属性,
     * 
     * @return 产品在该操作类型下所有需要反馈的属性的sql配置
     * @author liuyt3
     * @return 是否有反馈属性
     */
    public IDataset getExtendCharacters(IData orderInfo) throws Exception
    {

        IDataset extendCharacters = new DatasetList();
        String result_tag = orderInfo.getString("RESULT_TAG", "");
        String productSpecNumber = orderInfo.getString("PRODUCTSPECNUMBER");
        // 如果选择"开通失败"，不用反馈

        if (StringUtils.equals("1", result_tag))
        {
            return null;
        }
        // 查询反馈阶段配置
        IData para = new DataMap();
        para.put("EPARCHY_CODE", getTradeEparchyCode());
        para.put("PRODUCTSPECNUMBER", productSpecNumber);
        IDataset extendParam = CSViewCall.call(this, "CS.CommparaInfoQrySVC.queryExtendsParams", para);
        if (null == extendParam || extendParam.size() == 0)
        {
            return null;
        }
        // 企业飞信特殊处理，如果语音属性为02，不用反馈

        if (StringUtils.equals(ECFetionConstants.FC_POP_ID_LOCAL, productSpecNumber) || StringUtils.equals(ECFetionConstants.FC_POP_ID_WHOLE, productSpecNumber))
        {
            // 查询语音属性是否为02，02返回true
            boolean videoChk = checkECFetionVideoUnsupport(orderInfo);
            if (videoChk)
            {
                return null;
            }
        } // 企业飞信特殊处理--end
        // 查询产品级操作类型反馈配置

        para.put("SUBSYS_CODE", "CSM");
        para.put("PARAM_ATTR", "3400");
        para.put("PARAM_CODE", "productSpecNumber");
        IDataOutput dop = CSViewCall.callPage(this, "CS.CommparaInfoQrySVC.getCommparaInfo", para, getPagination("infonav"));
        IDataset commparaInfos = dop.getData();
        if (null == commparaInfos || commparaInfos.size() == 0)
        {
            CSViewException.apperr(ProductException.CRM_PRODUCT_34);
        }
        String operationg_subtype_id = orderInfo.getString("OPERATIONSUBTYPEID");
        for (int i = 0, size = commparaInfos.size(); i < size; i++)
        {
            IData sqlParam = new DataMap();
            IData commparaInfo = commparaInfos.getData(i);
            String character_id = commparaInfo.getString("PARA_CODE1");
            String flagStr = commparaInfo.getString("PARA_CODE2");
            String flags[] = StringUtils.split(flagStr, ";");
            int index = -1;
            for (int j = 0, length = flags.length; j < length; j++)
            {
                if (StringUtils.equals(operationg_subtype_id, flags[j].trim()))
                {
                    index = j;
                    break;
                }
            }
            if (index != -1)
            {
                sqlParam.put("CHARATER_ID", character_id);
                sqlParam.put("DEFAULTQRY", commparaInfo.getString("PARA_CODE21", ""));// 默认值查询

                String routeStr = commparaInfo.getString("PARA_CODE23", "");
                String routes[] = StringUtils.split(routeStr, ";");
                if (index < routes.length)
                {
                    String route = routes[index];
                    String field = StringUtils.split(commparaInfo.getString("PARA_CODE22", ""), ";")[index];
                    // String tab_name = commparaInfo.getString("PARA_CODE24", "").split(";")[index];
                    // String sql_ref = commparaInfo.getString("PARA_CODE25", "").split(";")[index];

                    sqlParam.put("ROUTE", route);
                    // sqlParam.put("TAB_NAME", tab_name);
                    // sqlParam.put("SQL_REF", sql_ref);
                    sqlParam.put("FIELD", field);
                }
                extendCharacters.add(sqlParam);
            }
        }
        return extendCharacters;
    }

    /**
     * 查询成员手机号
     * 
     * @author guyx
     * @throws Exception
     */
    public void queryMemberInfos(IRequestCycle cycle) throws Exception
    {
        IData para = new DataMap();
        para.put("PRODUCTORDERNUMBER", getParameter("ORDER_NUM"));
        IDataOutput ido = CSViewCall.callPage(this, "CS.CommparaInfoQrySVC.orderMemQry", para, getPagination("uif"));
        IDataset result = ido.getData();
        setCondition(getData("cond", true));
        setMemberDetails(result);
    }

    /**
     * 查询代办工单
     * 
     * @author weixb3
     * @throws Exception
     */
    public void queryOrderInfos(IRequestCycle cycle) throws Exception
    {

        IData param = getData("cond", true);
        IDataset dataset = new DatasetList();

        if ("6".equals(param.get("OPERATIONSUBTYPEID")))
        {
            dataset = CSViewCall.call(this, "CS.TradePoInfoQrySVC.orderMemQry", param, getPagination("infonav"));
        }
        else
        {
            dataset = CSViewCall.call(this, "CS.TradePoInfoQrySVC.orderInfoQry", param, getPagination("infonav"));
        }
        IData ctrlInfo = new DataMap();
        if (dataset.size() == 0)
        {
            ctrlInfo.put("strHint", "没有符合条件的查询结果！");
        }
        else
        {
            ctrlInfo.put("strHint", "查询成功！");
        }
        setCtrlInfo(ctrlInfo);
        setInfoCount(dataset.size());
        setCondition(getData("cond", true));
        setInfos(dataset);
    }

    public abstract void setCondition(IData condition);

    public abstract void setCtrlInfo(IData ctrlInfo);

    public abstract void setInfoCount(long infoCount);

    public abstract void setInfoCount2(long infoCount2);

    public abstract void setInfos(IDataset infos);

    public abstract void setMemberDetails(IDataset menberDetails);

    public abstract void setOrderDetails(IDataset orderDetails);
    public abstract void setPoInfos(IDataset poInfos);
    public abstract void setPoProducts(IDataset poProducts);

    /**
     * 查询相应的订购信息
     * 
     * @author guyx
     * @throws Exception
     */
    public void showDetail(IRequestCycle cycle) throws Exception
    {
        IData param = getData("cond", true);

        IDataset result = CSViewCall.call(this, "CS.CommparaInfoQrySVC.orderDetailQry", param, getPagination("inf3onav"));
        setOrderDetails(result);
    }

}
