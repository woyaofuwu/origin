
package com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.ProductData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.res.ResParaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.requestdata.ResInfo;
import com.asiainfo.veris.crm.order.soa.person.busi.np.restoreuser.order.requestdata.RestoreUserNpReqData;

public class BuildRestoreUserNpReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        String invoiceNo = param.getString("INVOICE_NO");

        RestoreUserNpReqData reqData = (RestoreUserNpReqData) brd;

        reqData.setInvoiceNo(invoiceNo);

        ProductData product = new ProductData(param.getString("PRODUCT_ID"));
        reqData.setMainProduct(product);

        buildElems(param, reqData);

        buildResInfo(param, reqData);

    }

    public void buildElems(IData param, RestoreUserNpReqData brd) throws Exception
    {

        /* 拼装子元素 */

        String str = param.getString("SELECTED_ELEMENTS", "");
        if (StringUtils.isNotBlank(str))
        {
            IDataset elems = new DatasetList(str);
            int len = elems.size();
            for (int i = 0; i < len; i++)
            {
                IData elem = elems.getData(i);
                String elemTypeCode = elem.getString("ELEMENT_TYPE_CODE", "");

                if ("D".equals(elemTypeCode))
                {
                    brd.addPmd(new DiscntData(elem));
                }
                else if ("S".equals(elemTypeCode))
                {
                    // 如果用户有这个服务，则不拼到requestData中
                    if (brd.getUca().checkUserIsExistSvcId(elem.getString("ELEMENT_ID", "")))
                    {
                        continue;
                    }
                    brd.addPmd(new SvcData(elem));
                }
            }
        }

    }

    public void buildResInfo(IData param, RestoreUserNpReqData brd) throws Exception
    {
        String str = param.getString("resInfos");
        if (StringUtils.isNotBlank(str))
        {
            IDataset set = new DatasetList(str);
            for (int i = 0, len = set.size(); i < len; i++)
            {
                IData data = set.getData(i);

                String resTypeCode = data.getString("RES_TYPE_CODE");

                String tag = data.getString("tag");
                if ("0".equals(resTypeCode) && "O".equals(tag))
                {
                    ResInfo res = new ResInfo();
                    res.setResTypeCode(resTypeCode);
                    res.setResCode(data.getString("RES_CODE"));
                    res.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    // 手机号码用原来的
                    res.setInstId(SeqMgr.getInstId());
                    brd.addResInfo(res);

                }

                // if ("1".equals(resTypeCode) && "O".equals(tag))
                // {
                // res.setModifyTag("O");
                // // sim原来的要作废
                // res.setInstId(data.getString("INST_ID"));
                //
                // }
                if ("1".equals(resTypeCode) && "0".equals(tag))
                {
                    ResInfo res = new ResInfo();
                    res.setResTypeCode(resTypeCode);
                    res.setResCode(data.getString("RES_CODE"));
                    res.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    res.setImsi(data.getString("IMSI"));
                    res.setKi(data.getString("KI"));
                    res.setRsrvStr1(data.getString("NEW_RES_TYPE_CODE", "0").substring(1));
                    res.setRsrvStr3(data.getString("OPC", ""));
                    res.setRsrvTag3(checkUser4GUsimCard(data.getString("NEW_RES_TYPE_CODE", "0"), data.getString("OPC", "")));
                    // 新sim 卡
                    res.setInstId(SeqMgr.getInstId());
                    res.setOpc(data.getString("OPC", ""));
                    brd.addResInfo(res);

                }

            }
        }
    }

    // 重写构造UCA数据的方法
    public UcaData buildUcaData(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        return UcaDataFactory.getDestroyUcaByUserId(userId);
    }

    /**
     * @Function: checkUser4GUsimCard
     * @Description: 判断是否为4G卡
     * @param resTypeCode
     * @param opcValue
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年8月20日 下午3:57:14
     */
    public String checkUser4GUsimCard(String resTypeCode, String opcValue) throws Exception
    {
        String strSimTypeCode = resTypeCode.substring(1);
        IDataset uimInfo = ResParaInfoQry.checkUser4GUsimCard(strSimTypeCode);
        if (IDataUtil.isNotEmpty(uimInfo) && StringUtils.isNotBlank(opcValue))
        {
            return "1";// 4G卡
        }
        return "";
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new RestoreUserNpReqData();
    }

}
