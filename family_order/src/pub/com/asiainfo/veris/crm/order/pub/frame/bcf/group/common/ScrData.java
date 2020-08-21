package com.asiainfo.veris.crm.order.pub.frame.bcf.group.common;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcEsopConstants;

public class ScrData extends DataMap {
    private static final long serialVersionUID = 1L;

    public ScrData() {
        put(EcEsopConstants.TABLE_EOP_SUBSCRIBE, new DataMap());
        put(EcEsopConstants.TABLE_EOP_NODE, new DataMap());
        put(EcEsopConstants.TABLE_EOP_PRODUCT, new DatasetList());
        put(EcEsopConstants.TABLE_EOP_PAGE_SAVE, new DatasetList());
        put(EcEsopConstants.TABLE_EOP_OTHER, new DatasetList());
        put(EcEsopConstants.TABLE_EOP_DIS, new DatasetList());
        put(EcEsopConstants.TABLE_EOP_SVC, new DatasetList());
        put(EcEsopConstants.TABLE_EOP_ATTR, new DatasetList());
        put(EcEsopConstants.TABLE_EOP_EOMS, new DataMap());
        put(EcEsopConstants.TABLE_EOP_ATTACH, new DatasetList());
        
        //快速办理产品受理数据表(海南政企一单清/快速办理新增表)
        put(EcEsopConstants.TABLE_EOP_QUICKORDER_COND, new DatasetList());
        put(EcEsopConstants.TABLE_EOP_QUICKORDER_MEB, new DatasetList());
        put(EcEsopConstants.TABLE_EOP_QUICKORDER_DATA, new DatasetList());
        put(EcEsopConstants.TABLE_EOP_PRODUCT_EXT, new DatasetList());
    }

    public ScrData(String parameter) {
        super(parameter);
        if (get(EcEsopConstants.TABLE_EOP_SUBSCRIBE) == null)
            put(EcEsopConstants.TABLE_EOP_SUBSCRIBE, new DataMap());
        if (get(EcEsopConstants.TABLE_EOP_NODE) == null)
            put(EcEsopConstants.TABLE_EOP_NODE, new DataMap());
        if (get(EcEsopConstants.TABLE_EOP_PRODUCT) == null)
            put(EcEsopConstants.TABLE_EOP_PRODUCT, new DatasetList());
        if (get(EcEsopConstants.TABLE_EOP_PAGE_SAVE) == null)
            put(EcEsopConstants.TABLE_EOP_PAGE_SAVE, new DatasetList());
        if (get(EcEsopConstants.TABLE_EOP_OTHER) == null)
            put(EcEsopConstants.TABLE_EOP_OTHER, new DatasetList());
        if (get(EcEsopConstants.TABLE_EOP_DIS) == null)
            put(EcEsopConstants.TABLE_EOP_DIS, new DatasetList());
        if (get(EcEsopConstants.TABLE_EOP_SVC) == null)
            put(EcEsopConstants.TABLE_EOP_SVC, new DatasetList());
        if (get(EcEsopConstants.TABLE_EOP_ATTR) == null)
            put(EcEsopConstants.TABLE_EOP_ATTR, new DatasetList());
        if (get(EcEsopConstants.TABLE_EOP_EOMS) == null)
            put(EcEsopConstants.TABLE_EOP_EOMS, new DataMap());
        if (get(EcEsopConstants.TABLE_EOP_ATTACH) == null)
            put(EcEsopConstants.TABLE_EOP_ATTACH, new DatasetList());
        
        if (get(EcEsopConstants.TABLE_EOP_QUICKORDER_COND) == null)
            put(EcEsopConstants.TABLE_EOP_QUICKORDER_COND, new DatasetList());

        if (get(EcEsopConstants.TABLE_EOP_QUICKORDER_MEB) == null)
            put(EcEsopConstants.TABLE_EOP_QUICKORDER_MEB, new DatasetList());

        if (get(EcEsopConstants.TABLE_EOP_QUICKORDER_DATA) == null)
            put(EcEsopConstants.TABLE_EOP_QUICKORDER_DATA, new DatasetList());

        if (get(EcEsopConstants.TABLE_EOP_PRODUCT_EXT) == null)
            put(EcEsopConstants.TABLE_EOP_PRODUCT_EXT, new DatasetList());
    }

    public void addWorkformProduct(IDataset workformProduct) {
        IDataset productInfos = this.getDataset(EcEsopConstants.TABLE_EOP_PRODUCT);

        if (DataUtils.isNotEmpty(productInfos)) {
            productInfos.addAll(workformProduct);
            put(EcEsopConstants.TABLE_EOP_PRODUCT, productInfos);
        } else {
            put(EcEsopConstants.TABLE_EOP_PRODUCT, workformProduct);
        }
    }

    public void addWorkformPageSave(IDataset workformPageSave) {
        IDataset pageSaveInfos = this.getDataset(EcEsopConstants.TABLE_EOP_PAGE_SAVE);

        if (DataUtils.isNotEmpty(pageSaveInfos)) {
            pageSaveInfos.addAll(workformPageSave);
            put(EcEsopConstants.TABLE_EOP_PAGE_SAVE, pageSaveInfos);
        } else {
            put(EcEsopConstants.TABLE_EOP_PAGE_SAVE, workformPageSave);
        }
    }

    public void addWorkformOther(IDataset workformOther) {
        IDataset otherInfos = this.getDataset(EcEsopConstants.TABLE_EOP_OTHER);

        if (DataUtils.isNotEmpty(otherInfos)) {
            otherInfos.addAll(workformOther);
            put(EcEsopConstants.TABLE_EOP_OTHER, otherInfos);
        } else {
            put(EcEsopConstants.TABLE_EOP_OTHER, workformOther);
        }
    }

    public void addWorkformDis(IDataset workformDis) {
        IDataset disInfos = this.getDataset(EcEsopConstants.TABLE_EOP_DIS);

        if (DataUtils.isNotEmpty(disInfos)) {
            disInfos.addAll(workformDis);
            put(EcEsopConstants.TABLE_EOP_DIS, disInfos);
        } else {
            put(EcEsopConstants.TABLE_EOP_DIS, workformDis);
        }
    }

    public void addWorkformSvc(IDataset workformSvc) {
        IDataset svcInfos = this.getDataset(EcEsopConstants.TABLE_EOP_SVC);

        if (DataUtils.isNotEmpty(svcInfos)) {
            svcInfos.addAll(workformSvc);
            put(EcEsopConstants.TABLE_EOP_SVC, svcInfos);
        } else {
            put(EcEsopConstants.TABLE_EOP_SVC, workformSvc);
        }
    }

    public void addWorkformAttr(IDataset workformAttr) {
        IDataset attrInfos = this.getDataset(EcEsopConstants.TABLE_EOP_ATTR);

        if (DataUtils.isNotEmpty(attrInfos)) {
            attrInfos.addAll(workformAttr);
            put(EcEsopConstants.TABLE_EOP_ATTR, attrInfos);
        } else {
            put(EcEsopConstants.TABLE_EOP_ATTR, workformAttr);
        }
    }

    public void addWorkformAttach(IDataset workformAttach) {
        IDataset attachInfos = this.getDataset(EcEsopConstants.TABLE_EOP_ATTACH);

        if (DataUtils.isNotEmpty(attachInfos)) {
            attachInfos.addAll(workformAttach);
            put(EcEsopConstants.TABLE_EOP_ATTACH, attachInfos);
        } else {
            put(EcEsopConstants.TABLE_EOP_ATTACH, workformAttach);
        }
    }

    public IData getWorkformSubscribe() {
        return this.getData(EcEsopConstants.TABLE_EOP_SUBSCRIBE);
    }

    public void setWorkformSubscribe(IData workformSubscribe) {
        put(EcEsopConstants.TABLE_EOP_SUBSCRIBE, workformSubscribe);
    }

    public IData getWorkformNode() {
        return this.getData(EcEsopConstants.TABLE_EOP_NODE);
    }

    public void setWorkformNode(IData workformNode) {
        put(EcEsopConstants.TABLE_EOP_NODE, workformNode);
    }

    public IData getWorkformEoms() {
        return this.getData(EcEsopConstants.TABLE_EOP_EOMS);
    }

    public void setWorkformEoms(IData workformEoms) {
        put(EcEsopConstants.TABLE_EOP_EOMS, workformEoms);
    }

    public IDataset getWorkformProduct() {
        return this.getDataset(EcEsopConstants.TABLE_EOP_PRODUCT);
    }

    public void setWorkformProduct(IDataset workformProduct) {
        put(EcEsopConstants.TABLE_EOP_PRODUCT, workformProduct);
    }

    public IDataset getWorkformProductSub() {
        return this.getDataset(EcEsopConstants.TABLE_EOP_PRODUCT_SUB);
    }

    public void setWorkformProductSub(IDataset workformProductSub) {
        put(EcEsopConstants.TABLE_EOP_PRODUCT_SUB, workformProductSub);
    }

    public IDataset getWorkformPageSave() {
        return this.getDataset(EcEsopConstants.TABLE_EOP_PAGE_SAVE);
    }

    public void setWorkformPageSave(IDataset workformPageSave) {
        put(EcEsopConstants.TABLE_EOP_PAGE_SAVE, workformPageSave);
    }

    public IDataset getWorkformOther() {
        return this.getDataset(EcEsopConstants.TABLE_EOP_OTHER);
    }

    public void setWorkformOther(IDataset workformOther) {
        put(EcEsopConstants.TABLE_EOP_OTHER, workformOther);
    }

    public IDataset getWorkformDis() {
        return this.getDataset(EcEsopConstants.TABLE_EOP_DIS);
    }

    public void setWorkformDis(IDataset workformDis) {
        put(EcEsopConstants.TABLE_EOP_DIS, workformDis);
    }

    public IDataset getWorkformSvc() {
        return this.getDataset(EcEsopConstants.TABLE_EOP_SVC);
    }

    public void setWorkformSvc(IDataset workformSvc) {
        put(EcEsopConstants.TABLE_EOP_SVC, workformSvc);
    }

    public IDataset getWorkformAttr() {
        return this.getDataset(EcEsopConstants.TABLE_EOP_ATTR);
    }

    public void setWorkformAttr(IDataset workformAttr) {
        put(EcEsopConstants.TABLE_EOP_ATTR, workformAttr);
    }

    public IDataset getWorkformAttach() {
        return this.getDataset(EcEsopConstants.TABLE_EOP_ATTACH);
    }

    public void setWorkformAttach(IDataset workformAttach) {
        put(EcEsopConstants.TABLE_EOP_ATTACH, workformAttach);
    }
    
    /**
     * 快速办理产品受理数据表
     * 海南政企一单清/快速办理新增表
     * @param quickOrderCondList
     */
    public void addWorkformQuickOrderCond(IDataset quickOrderCondList)
    {
        IDataset attachInfos = this.getDataset(EcEsopConstants.TABLE_EOP_QUICKORDER_COND);
        if (DataUtils.isNotEmpty(attachInfos))
        {
            attachInfos.addAll(quickOrderCondList);
            put(EcEsopConstants.TABLE_EOP_QUICKORDER_COND, attachInfos);
        }
        else
        {
            put(EcEsopConstants.TABLE_EOP_QUICKORDER_COND, quickOrderCondList);
        }
    }
    
    /**
     * 快速办理产品受理数据表
     * 海南政企一单清/快速办理新增表
     */
    public IDataset getWorkformQuickOrderCond() 
    {
        return this.getDataset(EcEsopConstants.TABLE_EOP_QUICKORDER_COND);
    }


    public void setWorkformQuickOrderCond(IDataset workformQuickOrderCond) {
        put(EcEsopConstants.TABLE_EOP_QUICKORDER_COND, workformQuickOrderCond);
    }

    /**
     * 快速办理产品受理数据表
     * 海南政企一单清/快速办理新增表
     */
    public IDataset getWorkformQuickOrderMeb()
    {
        return this.getDataset(EcEsopConstants.TABLE_EOP_QUICKORDER_MEB);
    }

    public void setWorkformQuickOrderMeb(IDataset workformQuickOrderMeb) {
        put(EcEsopConstants.TABLE_EOP_QUICKORDER_MEB, workformQuickOrderMeb);
    }

    /**
     * 快速办理产品受理数据表
     * 海南政企一单清/快速办理新增表
     */
    public IDataset getWorkformQuickOrderData()
    {
        return this.getDataset(EcEsopConstants.TABLE_EOP_QUICKORDER_DATA);
    }


    public void setWorkformQuickOrderData(IDataset workformQuickOrderData) {
        put(EcEsopConstants.TABLE_EOP_QUICKORDER_DATA, workformQuickOrderData);
    }

    /**
     * 快速办理产品受理数据表
     * 海南政企一单清/快速办理新增表
     */
    public IDataset getWorkformProductExt()
    {
        return this.getDataset(EcEsopConstants.TABLE_EOP_PRODUCT_EXT);
    }

    public void setWorkformProductExt(IDataset workformProductExt) {
        put(EcEsopConstants.TABLE_EOP_PRODUCT_EXT, workformProductExt);
    }

    public boolean isBizEmpty() {
        boolean flag = true;
        if (DataUtils.isNotEmpty(this.getWorkformSubscribe()))// TABLE_EOP_SUBSCRIBE
        {
            return false;
        }
        if (DataUtils.isNotEmpty(this.getWorkformNode()))// TABLE_EOP_NODE
        {
            return false;
        }
        if (DataUtils.isNotEmpty(this.getWorkformProduct()))// TABLE_EOP_PRODUCT
        {
            return false;
        }
        if (DataUtils.isNotEmpty(this.getWorkformPageSave()))// TABLE_EOP_PAGE_SAVE
        {
            return false;
        }
        if (DataUtils.isNotEmpty(this.getWorkformOther()))// TABLE_EOP_OTHER
        {
            return false;
        }
        if (DataUtils.isNotEmpty(this.getWorkformDis()))// TABLE_EOP_DIS
        {
            return false;
        }
        if (DataUtils.isNotEmpty(this.getWorkformSvc()))// TABLE_EOP_SVC
        {
            return false;
        }
        if (DataUtils.isNotEmpty(this.getWorkformAttr()))// TABLE_EOP_ATTR
        {
            return false;
        }
        if (DataUtils.isNotEmpty(this.getWorkformEoms()))// TABLE_EOP_EOMS
        {
            return false;
        }
        if (DataUtils.isNotEmpty(this.getWorkformAttach()))// TABLE_EOP_ATTACH
        {
            return false;
        }

        return flag;
    }
}
