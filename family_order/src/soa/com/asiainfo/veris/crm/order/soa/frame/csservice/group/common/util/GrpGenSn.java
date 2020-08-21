
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;

public final class GrpGenSn extends CSBizBean
{
    /**
     * 生成集团用户服务号码 PageData对象
     * 
     * @return String 集团编码
     * @author liaoyi
     * @throws Exception
     */
    public static IData genAUGSn1(IData idGen) throws Exception
    {

        String productId = idGen.getString("PRODUCT_ID", "");
        String groupId = idGen.getString("GROUP_ID", "");

        IData data = new DataMap();
        IData result = new DataMap();

        // 得到服务号码生成规则配置
        data.put("ID", productId);
        data.put("ID_TYPE", "P");
        data.put("ATTR_OBJ", "0");
        data.put("ATTR_CODE", "genGrpSn");
        IDataset dataList = AttrBizInfoQry.getBizAttr(productId, "P", "0", "genGrpSn", null);
        if (dataList.size() == 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_434, productId);
        }

        // 得到配置数据
        data = dataList.getData(0);
        String attrValue = data.getString("ATTR_VALUE", "");

        // 手工输入标志 1 = 手工输入，可改写 ；0 = 自动生成，不能改写
        String Input_Tag = "0";
        // 如果ATTR_VALUE="MUST_SN",为手工输入
        if (attrValue.equals("MUST_SN"))
        {
            result.put("IF_RES_CODE", "1");
            result.put("SERIAL_NUMBER", "");
            return result;
        }

        // 得到配置名称，根据[;]
        String ruleName[] = attrValue.split(";");

        // 根据配置规则生成服务号码，如：[PRODUCT_ID,1,2;EPARCHY_CODE,3,4]
        String strEparchyCode = CSBizBean.getTradeEparchyCode();
        String strCityCode = getVisit().getCityCode();
        String preSN = "";
        String seqVpmnId = "";

        String sn = "";
        String tmp = "";

        String ruelValue[];
        String value = "";
        int valueMin = -1;
        int valueMax = -1;
        int length = 0;

        for (String element : ruleName)
        {
            value = element;

            // 得到配置名称，根据[,]
            ruelValue = value.split(",");

            // 初始化
            value = "";
            valueMin = -1;
            valueMax = -1;

            // 得到配置项目
            for (int index = 0; index < ruelValue.length; index++)
            {
                tmp = ruelValue[index].trim();

                switch (index)
                {
                    case 0:
                    {
                        value = tmp; // 名称
                        break;
                    }
                    case 1:
                    {
                        if ((tmp == null) || tmp.equals(""))
                        {
                            valueMin = -1;
                        }
                        else
                        {
                            valueMin = Integer.valueOf(tmp); // sub小值
                        }
                        break;
                    }
                    case 2:
                    {
                        if ((tmp == null) || tmp.equals(""))
                        {
                            valueMax = -1;
                        }
                        else
                        {
                            valueMax = Integer.valueOf(tmp); // sub大值
                        }
                        break;
                    }
                }
            }

            if (value.equals("PRODUCT_ID"))
            { // 如果是产品
                sn = sn + productId;
            }
            else if (value.equals("EPARCHY_CODE"))
            { // 如果是地州
                sn = sn + strEparchyCode.substring(valueMin, valueMax);
            }
            else if (value.equals("CITY_CODE"))
            { // 如果是县市
                sn = sn + strCityCode.substring(valueMin, valueMax);
            }
            else if (value.equals("SEQ_VPMN_ID"))
            { // 如果是序列号
                seqVpmnId = SeqMgr.getVpmnIdIdForGrp();
                sn = sn + seqVpmnId.substring(valueMin, valueMax);
            }
            else if (value.equals("SEQ_VPN_ID"))
            { // 如果是序列号
                seqVpmnId = SeqMgr.getVpnIdIdForGrp();
                sn = sn + seqVpmnId.substring(valueMin, valueMax);
            }
            else if (value.equals("SEQ_VPMN_SERIAL"))
            { // 如果是序列号
                seqVpmnId = SeqMgr.getVpmnSerialForGrp();
                sn = sn + seqVpmnId.substring(valueMin, valueMax);

            }
            else if (value.equals("GROUP_ID"))
            {
                sn = sn + groupId;
            }
            else if (value.indexOf("SELECT_") != -1)
            {
                // 对于不同的业务，扩展getGrpSnBySelectParam方法即可
                // preSN = CommparaInfoQry.getGrpSnBySelectParam(productId, groupId);

                preSN = null;
                sn = sn + preSN;
            }
            else if (value.equals("SELF_DEF"))
            {
                length = value.length();
                continue;
            }
            else if (value.equals("INPUT_TAG"))
            {
                Input_Tag = "1";
            }
            else
            { // 其他的默认固定字符串
                sn = sn + value;
            }
        }

        // 参数处理
        // AppCtx.setTransfer("SELF_DEF_LENGTH", String.valueOf(length));// 服务号码长度-目前没什么用
        // AppCtx.setTransfer("PRE_SN", preSN);// 服务号码前面部分，用于规则校验（代码很别扭，先用着吧）
        result.put("IF_RES_CODE", Input_Tag);// 服务号码输入框是否有效标志，如果IF_RES_CODE=0，服务号码框Disabled
        result.put("SERIAL_NUMBER", sn);// 生成的服务号码
        return result;
    }

    /**
     * 生成集团用户服务号码 PageData对象
     * 
     * @return String 集团编码
     * @author xiajj
     * @throws Exception
     */
    public static IData genGrpSn(IData idGen) throws Exception
    {

        String productId = idGen.getString("PRODUCT_ID", "");

        IData data = new DataMap();

        // 得到服务号码生成规则配置
        data.put("ID", productId);
        data.put("ID_TYPE", "P");
        data.put("ATTR_OBJ", "0");
        data.put("ATTR_CODE", "genGrpSn");
        IDataset dataList = AttrBizInfoQry.getBizAttr(productId, "P", "0", "genGrpSn", null);

        if (IDataUtil.isEmpty(dataList))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_514, productId);
        }

        // 得到配置数据
        data = dataList.getData(0);
        String attrValue = data.getString("ATTR_VALUE", "");
        IData result = new DataMap();

        // 如果ATTR_VALUE="MUST_SN",为手工输入
        if (attrValue.equals("MUST_SN"))
        {
            result.put("IF_RES_CODE", "1"); // 手工输入标志 1 = 手工输入，可改写 ；0 = 自动生成，不能改写
            result.put("SERIAL_NUMBER", "");
            return result;
        }

        // 得到配置名称，根据[;]
        String ruleName[] = attrValue.split(";");
        // 获取市县编码
        String cityId = "";
        String sn = "";
        String value = "";

        String strCityCode = getVisit().getCityCode();
        
        //查询1219 TD_S_COMMPARA配置编码，是否启用查询TF_B_TRADE表来较验serial_num是否已经存在
        String ifQryTrade = "false";
		IDataset CommparaParas = CommparaInfoQry.getCommByParaAttr("CSM", "1219", "ZZZZ");
		if (IDataUtil.isNotEmpty(CommparaParas)) {
			ifQryTrade = CommparaParas.getData(0).getString("PARA_CODE1", "").trim();
		}

        for (int i = 0; i < 9999999; i++)
        {
            for (int row = 0; row < ruleName.length; row++)
            {
                value = ruleName[row];

                if (value.equals("CITY_ID"))
                { // 如果是市县编码

                    IDataset cityIds = CommparaInfoQry.getCommparaByAttrCode1("CGM", "4", strCityCode, "ZZZZ", null);
                    if (IDataUtil.isNotEmpty(cityIds))
                    {
                        cityId = cityIds.getData(0).getString("PARAM_CODE", "").trim();
                    }
                    else
                    {
                        IDataset noramlCityset = CommparaInfoQry.getCommparaByAttrCode1("CGM", "4", "ZZZZ", "ZZZZ", null);
                        if (IDataUtil.isNotEmpty(noramlCityset))
                        {
                            cityId = noramlCityset.getData(0).getString("PARAM_CODE", "").trim();
                        }
                    }

                    sn = sn + cityId;
                }
                else if (value.equals("SEQ_VPMN_ID"))
                { // 如果是序列号
                    // 获取流水号
                    String seqVpmnId = SeqMgr.getVpmnIdIdForGrp();
                    seqVpmnId = seqVpmnId.substring(seqVpmnId.length() - 6);

                    sn = sn + seqVpmnId;
                }
                else
                { // 默认固定字符串
                    sn = sn + value;
                }
            }
            // add by lixiuyu@20100305 验证集团产品编码是否存在
            IData dataserial = new DataMap();
            dataserial.put("SERIAL_NUMBER", sn.trim());
            dataserial.put("REMOVE_TAG", "0");
            dataserial.put("NET_TYPE_CODE", "00");

            IData snList = UcaInfoQry.qryUserInfoBySnForGrp(sn.trim());

            boolean isBreak = false;
            if (IDataUtil.isNotEmpty(snList))
            {
                sn = "";
            }
            else
            {
                //break;
            	isBreak = true;
            }
            
            // add by songxw@20190219 BUG20190131150518优化BBOSS类产品存在生成同样的集团产品编码问题 begin
            if(ifQryTrade.equalsIgnoreCase("TRUE") && !sn.trim().equals(""))
            {
	            IData param = new DataMap();
	            param.put("SERIAL_NUMBER", sn.trim());
	            IDataset tradeList = UTradeInfoQry.qryTradebySerialNum(sn);
	    		if(IDataUtil.isNotEmpty(tradeList))
	    		{
	    			sn = "";
	    			isBreak = false;
	    		}
	            else
	            {
	            	isBreak = true;
	            }
            }
            //add by songxw@20190219 BUG20190131150518优化BBOSS类产品存在生成同样的集团产品编码问题 end
    		
            if(isBreak){break;};
        }
        result.put("IF_RES_CODE", "0");// 服务号码输入框是否有效标志，如果IF_RES_CODE=0，服务号码框Disabled
        result.put("SERIAL_NUMBER", sn.trim());// 生成的服务号码

        return result;
    }

}
