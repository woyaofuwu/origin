
package com.asiainfo.veris.crm.order.soa.person.busi.twobusiarrearagered;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class TwoBusiArrearageRedSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     *
     * 插入两级业务欠费红名单表
     * @param input
     * @return
     * @throws Exception
     */
    public int inserRednfo(IData input) throws Exception
    {

        if (!IDataUtil.isEmpty(input)){
            IData param = new DataMap();
            param.put("GROUP_ID", input.getString("GROUP_ID"));
            param.put("GROUP_NAME", input.getString("GROUP_NAME"));
            param.put("OFFER_CODE", input.getString("OFFER_CODE"));
            param.put("OFFER_NAME", input.getString("OFFER_NAME"));
            param.put("REMOVE_TAG", input.getString("REMOVE_TAG"));
            param.put("INSERT_DATE", input.getString("INSERT_DATE"));
            param.put("RED_ID", input.getString("GROUP_ID")+input.getString("OFFER_CODE"));

            param.put("INSERT_STAFF_ID", input.getString("INSERT_STAFF_ID"));
            if (null!=input.getString("UPDATE_DATE")&&!input.getString("UPDATE_DATE").equals(""))
                param.put("UPDATE_DATE", input.getString("UPDATE_DATE"));
            if (null!=input.getString("UPDATE_STAFF_ID")&&!input.getString("UPDATE_STAFF_ID").equals(""))
                param.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
            param.put("REMARK", input.getString("REMARK"));
            return TwoBusiArrearageRedQry.insertInfo(param);
        }
        return 0;

    }


    /**
     *
     * 插入两级业务欠费红名单表查询
     */
    public IDataset selectRednfoPagination(IData input) throws Exception
    {
//            IData param = new DataMap();
//            param.put("GROUP_ID", input.getString("GROUP_ID"));
//            param.put("GROUP_NAME", input.getString("GROUP_NAME"));
//            param.put("OFFER_CODE", input.getString("OFFER_CODE"));
            return TwoBusiArrearageRedQry.queryRedInfoByGroupIdAndTradeTypeCode(input, getPagination());

    }

    /**
     * 删除操作
     * @param input
     * @return
     * @throws Exception
     */
    public int delelteRednfo(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
        param.put("UPDATE_DATE", input.getString("UPDATE_DATE"));
        String[] redIds = input.getString("RED_ID").split(",");
        String newStr="";

//        for (int i=0;i<redIds.length;i++) {
//            if (i==0){
//                newStr=redIds[0];
//            }else{
//                newStr+=" OR T.red_id ="+redIds[i];
//            }
//        }
        for (int i=0;i<redIds.length;i++) {
            if (i==0){
                newStr="'"+redIds[0]+"'";
            }else{
                newStr+=",'"+redIds[i]+"'";
            }
        }
        param.put("RED_IDS", newStr);
        return TwoBusiArrearageRedQry.updateRedInfoTradeAttr(param);

    }

    public  IDataset queryComparalistValue(IData input) throws Exception {
        //指定的云MAS产品不生成Limit关联  modify  by xuzh5
        IDataset prodCommparaList = CommparaInfoQry.getCommpara("CSM", "5532",
                "199701", "0898");
        //  if (prodCommparaList.size() > 0)
        return prodCommparaList;


    }

    /**
     *
     * 插入两级业务欠费红名单表查询
     */
    public IDataset selectRednfo(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("GROUP_ID", input.getString("GROUP_ID"));
        param.put("GROUP_NAME", input.getString("GROUP_NAME"));
        param.put("OFFER_CODE", input.getString("OFFER_CODE"));
        param.put("REMOVE_TAG", input.getString("REMOVE_TAG"));
        return TwoBusiArrearageRedQry.queryRedInfoByGroupIdAndTradeTypeCode(param);

    }



}
