
package com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationxxtinfo;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.relationxxtinfo.RelationXXTInfoIntf;

public class RelationXXTInfoIntfViewUtil
{
    /**
     * 查询校讯通学生参数信息
     *
     * @param bc
     * @param serialNumber
     * @param userIdA
     * @param serialNumberB
     * @param type
     * @return
     * @throws Exception
     */
    public static IDataset qryStuDisParamInfoBySnAUserIdASnBDsiType(IBizCommon bc, String serialNumber, String userIdA, String serialNumberB, String type) throws Exception
    {
        IDataset infosDataset = RelationXXTInfoIntf.qryStuDisParamInfoBySnAUserIdASnBDsiType(bc, serialNumber, userIdA, serialNumberB, type);
        if (IDataUtil.isEmpty(infosDataset))
        {
            infosDataset = new DatasetList();
        }
        return infosDataset;
    }

    /**
     * 查询同一集团 同一个付费号下所有代付号码
     *
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset queryMemInfoBySNandUserIdA(IBizCommon bc, String serialNumber, String userIdA) throws Exception
    {
        IDataset infosDataset = RelationXXTInfoIntf.queryMemInfoBySNandUserIdA(bc, serialNumber, userIdA);
        if (IDataUtil.isEmpty(infosDataset))
        {
            infosDataset = new DatasetList();
        }
        return infosDataset;
    }

    /**
     *
     *
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset queryXxtInfoBySnaGroup(IBizCommon bc, String serialNumbera, String serialNumbernb, String ecUserId) throws Exception
    {
        IDataset infosDataset = RelationXXTInfoIntf.queryXxtInfoBySnaGroup(bc, serialNumbera, serialNumbernb, ecUserId);
        if (IDataUtil.isEmpty(infosDataset))
        {
            infosDataset = new DatasetList();
        }
        return infosDataset;
    }
    

    /**
     *
     *
     * @param bc
     * @param userIdB
     * @param userIdA
     * @param routeId
     * @return
     * @throws Exception
     */
    public static IDataset qryMemInfoBySNForUIPDestroy(IBizCommon bc, String serialNumbera, String ecUserId) throws Exception
    {
        IDataset infosDataset = RelationXXTInfoIntf.qryMemInfoBySNForUIPDestroy(bc, serialNumbera, ecUserId);
        if (IDataUtil.isEmpty(infosDataset))
        {
            infosDataset = new DatasetList();
        }
        return infosDataset;
    }

    /**
     * 学护卡号码的家长号码是否订购过校讯通套餐4302、4303、4304套餐之一
     * return true 订购过校讯通10套餐，false 没有订购过
     * @param bc
     * @param userIdB
     * @return
     * @throws Exception
     */
    public static boolean qryXFKMemInfoBySNandUserIdA(IBizCommon bc, String serialNumber) throws Exception
    {
    	boolean flag = false ;
        IDataset infosDataset = RelationXXTInfoIntf.qryMemInfoBySNandUserIdA(bc, serialNumber);
    	if (IDataUtil.isNotEmpty(infosDataset))
    	{
    	    IDataset limitDiscnt = StaticUtil.getStaticList("EDC_DISCNT_15_LIMIT");
            for (int j = 0, jsize = infosDataset.size(); j < jsize; j++)
            {
                IData map = infosDataset.getData(j);
                String disId = map.getString("ELEMENT_ID", "");
                IDataset filterResult = DataHelper.filter(limitDiscnt, "DATA_ID=" + disId);
                if (IDataUtil.isNotEmpty(filterResult))
                {
                	flag = true;
                	break;
                }

            }
    	}
    	return flag;
    }
}
