
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupunit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class GroupProductUnitSVC extends CSBizService
{

    /*
     * @discription 鑾峰彇鐢ㄦ埛璁㈣喘鏃舵垚鍛樺繀閫夊厓绱� @author xunyl @date 2013-03-13
     */
    public static IDataset getMemberBaseElement(IData idata) throws Exception
    {
        IDataset memProductList = idata.getDataset("MEMPRODUCTLIST");
        boolean flag = idata.getBoolean("FLAG");
        IData result = idata.getData("RESULT");
        return GroupProductUnit.getMemberBaseElement(memProductList, flag, result);
    }

    /**
     * 鎿嶄綔鍗曞厓鏁版嵁
     * 
     * @author weixb3
     * @param idata
     * @return
     * @throws Exception
     */
    public static IDataset operelementsdata(IData idata) throws Exception
    {
        IDataset userElement = (IDataset) idata.getData("userElement");
        IDataset elemResult = new DatasetList();
        IData data = idata.getData("data");
        boolean flag = idata.getBoolean("flag");
        IDataset result = GroupProductUnit.operelementsdata(userElement, elemResult, data, flag);
        return result;
    }

    public IDataset getCreateUserBaseElement(IData idata) throws Exception
    {
        String baseProductId = idata.getString("PRODUCT_ID");
        return GroupProductUnit.getCreateUserBaseElement(baseProductId);
    }

    public IDataset getMemberBaseElementForGrp(IData idata) throws Exception
    {
        IDataset memProductList = idata.getString("MEM_PRODUCT_LIST", "").equals("") ? new DatasetList() : idata.getDataset("MEM_PRODUCT_LIST");
        boolean flag = idata.getBoolean("FLAG", true);
        IData memMustChooseresult = idata.getString("MEM_CHOOSE_RESULT", "").equals("") ? new DataMap() : idata.getData("MEM_CHOOSE_RESULT");
        GroupProductUnit.getMemberBaseElementForGrp(memProductList, flag, memMustChooseresult);
        IDataset result = new DatasetList();
        result.add(memMustChooseresult);
        return result;
    }
}
