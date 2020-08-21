
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm.SaleActiveQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveBreConst;

/**
 * 仅仅用于前台选包时候的校验
 * 
 * @author Mr.Z
 */
public class CheckGiftGoods4SelectPkg extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1459732502478923529L;

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String activeCheckMode = databus.getString("ACTIVE_CHECK_MODE");

        if (!SaleActiveBreConst.ACTIVE_CHECK_MODE_SEL_PKG.equals(activeCheckMode))
            return true;

        String packageId = databus.getString("PACKAGE_ID");
        String eparchyCode = databus.getString("EPARCHY_CODE");
//        IDataset goodsInfos = SaleGoodsInfoQry.queryByPkgIdEparchy(packageId, eparchyCode);
        IDataset goodsInfos = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(packageId, BofConst.ELEMENT_TYPE_CODE_SALEGOODS);
        
        if (IDataUtil.isNotEmpty(goodsInfos))
        {
            IDataset checkResultSet = new DatasetList();
            for (int i = 0, size = goodsInfos.size(); i < size; i++)
            {
                IData goodsInfo = goodsInfos.getData(i);
                IData goodsInfoExt = UpcCall.qryOfferComChaTempChaByCond(goodsInfo.getString("GOODS_ID",""), BofConst.ELEMENT_TYPE_CODE_SALEGOODS);
                goodsInfo.putAll(goodsInfoExt);
                
                String resTypeCode = goodsInfo.getString("GOODS_TYPE_CODE", "");
                String resCheck = goodsInfo.getString("RES_CHECK", "");
                if ("D".equals(resTypeCode) && "1".equals(resCheck))
                {
                    String resId = goodsInfo.getString("RES_ID");
                    if (StringUtils.isEmpty(resId))
                    {
                    	/**
                         * REQ201504080009优惠活动礼品配置界面缺失修复
                         * 查询礼品配置信息
                         * chenxy3 2015-11-11 调接口使用礼品
                         * */
                    	//资源管理标记：0-资源无管理 ，不同步；1-资源管理，需同步
                    	String resTag=goodsInfo.getString("RES_TAG","");
                    	String prodId = databus.getString("PRODUCT_ID");
                    	String goodsId = goodsInfo.getString("GOODS_ID","");
                    	String cityCode = databus.getString("TRADE_CITY_CODE");
                    	if("1".equals(resTag)){
	                    	IData ext_inparam=new DataMap();	                    	
	                    	ext_inparam.put("PRODUCT_ID", prodId);
	                    	ext_inparam.put("PACKAGE_ID", packageId);
	                    	ext_inparam.put("GOODS_ID", goodsId);
	                    	ext_inparam.put("CITY_CODE", cityCode);  
	                    	IDataset extset=SaleActiveQry.querySaleActiveExt(ext_inparam);
	                    	if(extset !=null && extset.size()>0){
	                    		String resId_ext= extset.getData(0).getString("RES_ID","");
	                    		goodsInfo.put("RES_ID", resId_ext);
	                    	}else{ 
		                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14061302, "该营销包未关联EXT表礼品！[产品:" + prodId + "][包:" + packageId + "],[元素:" + goodsId + "],[业务区:" + cityCode + "]");
		                        return true;
	                    	}
                    	}else{
                    		BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14061302, "获取营销礼品属性有误！该营销包未关联礼品！[产品:" + prodId + "][包:" + packageId + "],[元素:" + goodsId + "],[业务区:" + cityCode + "]");
	                        return true;
                    	}
                    }
                    IData data = ResCall.queryGiftGoods4Sale(goodsInfo.getString("RES_ID")); 
                    if(IDataUtil.isEmpty(data)){
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14061302, "调用资源接口获取礼品库存有误！请检查资源侧礼品配置![RES_ID:"+goodsInfo.getString("RES_ID")+"]");
                        return true;
                    }
                    checkResultSet.add(data);
                }
            }

            if (IDataUtil.isNotEmpty(checkResultSet))
            {
                for (int j = 0, s = checkResultSet.size(); j < s; j++)
                {
                    IData checkResult = checkResultSet.getData(j);
                    if (Integer.parseInt(checkResult.getString("AVALID_AMOUNT", "0")) <= 0)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 14061302, "此营销包活动礼品库存为0!");
                        return true;
                    }
                }
            }
        }
        return true;
    }

}
