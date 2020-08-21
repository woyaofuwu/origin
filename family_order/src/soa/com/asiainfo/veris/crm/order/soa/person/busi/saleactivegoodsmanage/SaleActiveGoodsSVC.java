
package com.asiainfo.veris.crm.order.soa.person.busi.saleactivegoodsmanage;
 
import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset; 
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.person.busi.nonbossfee.NonBossFeeItemMgrBean;
public class SaleActiveGoodsSVC extends CSBizService
{

    private static final long serialVersionUID = 8989405331390912792L;

    public IDataset getSaleGoodsCond(IData params) throws Exception
    { 
        String selectType=params.getString("SELECTTYPE","");
        IDataset rtnSet=null;
        if("".equals(selectType)){
        	rtnSet= SaleActiveGoodsBean.getProductsLists(params);
        }else if("PAKG".equals(selectType)){
        	rtnSet= SaleActiveGoodsBean.getPackagesLists(params);
        }else if("GOODS".equals(selectType)||"LISTS".equals(selectType)){
        	rtnSet= SaleActiveGoodsBean.getGoodsLists(params);
        }else if("EDITGOODS".equals(selectType)){
        	rtnSet= SaleActiveGoodsBean.getPackageGoods(params);
        }else if("CHECKTAB".equals(selectType)){
        	rtnSet= SaleActiveGoodsBean.checkIfExistTab(params);
        }else if("SUBMIT".equals(selectType)){
        	dealSubmitData(params);
        }
        return rtnSet;
    }
    /**
     * 处理提交数据     * 
     * */
    public void dealSubmitData(IData params) throws Exception{

        IDataset dataset = new DatasetList(params.getString("ITEM_DATASET")); 
        SaleActiveGoodsBean bean =new SaleActiveGoodsBean();
        if (dataset != null && dataset.size() > 0)
        {
            for (Iterator it = dataset.iterator(); it.hasNext();)
            {
                IData data = (IData) it.next();
                // 动态表格必须字段，区别提交数据的操作行为：(0：新增 1：删除 2:修改)
                // 信息管理界面只支持新增和删除两种操作
                String xTag = data.getString("tag");
                // 新增
                if ("0".equals(xTag))
                {
                	 
                    IData insertData = new DataMap();
                    long systime=System.currentTimeMillis();
                    insertData.put("EXT_ID",systime);
                    insertData.put("PRODUCT_ID",data.getString("SALE_PRODUCT"));
                    insertData.put("PACKAGE_ID",data.getString("SALE_PACKAGE"));
                    insertData.put("GOODS_ID",data.getString("SALE_GOODS"));
                    insertData.put("GOODS_NAME",data.getString("SALE_GOODS_NAME"));
                    insertData.put("RES_ID",data.getString("RES_ID"));
                    insertData.put("ACCOUNT_ID",data.getString("ACCOUNT_ID"));
                    insertData.put("ACCOUNT_NAME",data.getString("ACCOUNT_NAME"));
                    insertData.put("GOODS_PROPERTY",data.getString("GOODS_PROPERTY"));
                    insertData.put("PURCHASE_TYPE",data.getString("PURCHASE_TYPE_ID"));
                    insertData.put("CITY_CODE",data.getString("CITY_CODE"));
                    insertData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                    insertData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
                    bean.insertSaleGoodsExt(insertData);
                }
                // 删除
                else if ("1".equals(xTag))
                {
                    IData deleteData = new DataMap(); 
                    deleteData.put("EXT_ID",data.getString("EXT_ID"));
                    deleteData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                    deleteData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
                    bean.deleteSaleGoodsExt(deleteData);
                }
                // 修改
                else if ("2".equals(xTag))
                {
                    IData updata = new DataMap();
                    String extId = data.getString("EXT_ID", "");
                    if ("".equals(extId))
                    {
                        CSAppException.apperr(ParamException.CRM_PARAM_454);
                    }
                    updata.put("EXT_ID",extId);
                    updata.put("PRODUCT_ID",data.getString("SALE_PRODUCT"));
                    updata.put("PACKAGE_ID",data.getString("SALE_PACKAGE"));
                    updata.put("GOODS_ID",data.getString("SALE_GOODS"));
                    updata.put("GOODS_NAME",data.getString("SALE_GOODS_NAME"));
                    updata.put("RES_ID",data.getString("RES_ID"));
                    updata.put("ACCOUNT_ID",data.getString("ACCOUNT_ID"));
                    updata.put("ACCOUNT_NAME",data.getString("ACCOUNT_NAME"));
                    updata.put("GOODS_PROPERTY",data.getString("GOODS_PROPERTY"));
                    updata.put("PURCHASE_TYPE",data.getString("PURCHASE_TYPE_ID"));
                    updata.put("CITY_CODE",data.getString("CITY_CODE"));
                    updata.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                    updata.put("UPDATE_DEPART_ID", getVisit().getDepartId());
                    
                    bean.updateSaleGoodsExt(updata);
                }
                else
                {
                    CSAppException.apperr(ParamException.CRM_PARAM_455, xTag);
                }
            }

        }
        else
        {
            // common.error("没有可以操作的数据！");
            CSAppException.apperr(ParamException.CRM_PARAM_456);
        }
    } 
}
