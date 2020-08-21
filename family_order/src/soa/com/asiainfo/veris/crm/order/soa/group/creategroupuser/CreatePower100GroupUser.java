
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreatePower100GroupUserReqData;

public class CreatePower100GroupUser extends CreateGroupUser
{

    private IDataset userInfos = new DatasetList();

    protected CreatePower100GroupUserReqData reqData;

    public CreatePower100GroupUser()
    {
    }

    /**
     * 生成登记信息
     * 
     * @author 孙翰韬
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

        // 获取产品对应的用户信息
        getUserInfos();

        // 将优惠挂在子用户下
        infoRegDataPower100Element();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();

        // 建立子产品和动力100的uu关系
        infoRegDataRelation();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreatePower100GroupUserReqData();
    }

    /**
     * 获取产品对应的用户信息
     * 
     * @throws Exception
     */
    public void getUserInfos() throws Exception
    {
        IDataset power100setList = reqData.getPower100Infos();

        if (IDataUtil.isEmpty(power100setList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_100);
        }

        for (int i = 0; i < power100setList.size(); i++)
        {
            IData result = UserInfoQry.getGrpUserInfoByUserIdForGrp(power100setList.getData(i).getString("USER_ID"), "0");

            if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_100);
            }
            result.put("immeffect", (power100setList.getData(i).getString("USER_ID") == null) ? true : false);
            userInfos.add(result);
        }

    }

    /**
     * 将动力100的 优惠挂在子用户下
     * 
     * @throws Exception
     */
    public void infoRegDataPower100Element() throws Exception
    {
        IDataset paramDataset = reqData.cd.getElementParam();
        IDataset grpPackage = reqData.cd.getGrpPackage();
        IDataset svcDataset = reqData.cd.getSvc();
        IDataset dctDataset = reqData.cd.getDiscnt();
        IData dctdata = null;

        if (IDataUtil.isEmpty(grpPackage))
        {
            return;
        }
        for (int i = 0; i < grpPackage.size(); i++)
        {
            IData packageData = grpPackage.getData(i);
            String elementType = packageData.getString("ELEMENT_TYPE_CODE", "");
            String productId = packageData.getString("PRODUCT_ID");

            // 获取该元素属于哪个子产品，预留字段1表示产品ID
            IData results = UProductElementInfoQry.queryElementInfoByProductIdAndPackageIdAndElementId(productId, packageData.getString("PACKAGE_ID"), packageData.getString("ELEMENT_ID"), elementType, null);
            if (results == null || results.size() == 0)
            {
                continue;
            }
            // 自己维护，不到产商品取了
            String productIdB = "ALL";
            IDataset powerParams = AttrBizInfoQry.getBizAttr(packageData.getString("PACKAGE_ID"), elementType, packageData.getString("ELEMENT_ID"), "Power100", null);
            if (IDataUtil.isNotEmpty(powerParams))
            {
                productIdB = powerParams.getData(0).getString("ATTR_VALUE");
            }
            if (StringUtils.isBlank(productIdB))
            {
                continue;
            }

            // packageData.put("START_DATE", SysDateMgr.getEnableDate(packageData.getString("START_DATE")));
            packageData.put("END_DATE", SysDateMgr.getEndDate(packageData.getString("END_DATE")));

            if (!productIdB.equals("ALL"))
            {
                for (int j = 0; j < userInfos.size(); j++)
                {
                    if (productIdB.equals(userInfos.getData(j).getString("PRODUCT_ID")))
                    {
                        String userId = userInfos.getData(j).getString("USER_ID");
                        if ("S".equals(elementType))
                        { // 服务
                            saveSvcData(packageData, svcDataset, paramDataset, userId);
                        } // 优惠
                        else if ("D".equals(elementType))
                        {
                            saveDiscntData(packageData, dctDataset, paramDataset, userId);
                        }
                    }
                }
            }
            else
            {
                for (int j = 0; j < userInfos.size(); j++)
                {
                    String tmpUserId = userInfos.getData(j).getString("USER_ID");
                    if ("S".equals(elementType))
                    { // 服务
                        saveSvcData(packageData, svcDataset, paramDataset, tmpUserId);
                    }
                    else if ("D".equals(elementType))
                    { // 优惠
                        saveDiscntData(packageData, dctDataset, paramDataset, tmpUserId);
                    }
                }
            }
        }
        // 去掉主用户的资费
        for (int d = 0; d < dctDataset.size(); d++)
        {
            IData dct = dctDataset.getData(d);
            if (dct.getString("USER_ID") == reqData.getUca().getUserId())
            {
                dctDataset.remove(dct);
            }
        }
    }

    /**
     * 建立子产品和动力100的uu关系
     * 
     * @throws Exception
     */
    public void infoRegDataRelation() throws Exception
    {
        IDataset dataset = new DatasetList();
        for (int i = 0; i < userInfos.size(); i++)
        {
            IData userInfo = userInfos.getData(i);

            IData map = new DataMap();
            map.put("USER_ID_A", reqData.getUca().getUserId());
            map.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber());
            map.put("USER_ID_B", userInfo.getString("USER_ID"));
            map.put("SERIAL_NUMBER_B", userInfo.getString("SERIAL_NUMBER"));
            map.put("RELATION_TYPE_CODE", map.getString("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getUca().getProductId()))); // 关系类型
            map.put("ROLE_CODE_A", "0");
            map.put("ROLE_CODE_B", "0");
            map.put("INST_ID", SeqMgr.getInstId());
            map.put("START_DATE", getAcceptTime()); // 开始时间
            map.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            dataset.add(map);
        }
        addTradeRelation(dataset);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreatePower100GroupUserReqData) getBaseReqData();
    }

    /**
     * 构造函数
     * 
     * @param pd
     */

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setPower100Infos(map.getDataset("POWER100_PRODUCT_INFO"));
    }

    /**
     * 保存优惠信息
     * 
     * @throws Exception
     */
    public void saveDiscntData(IData packageData, IDataset dctDataset, IDataset paramDataset, String userId) throws Exception
    {
        String instId = SeqMgr.getInstId();
        IData map = new DataMap();
        map.putAll(packageData);
        map.put("INST_ID", instId);
        map.put("USER_ID", userId);
        map.put("USER_ID_A", reqData.getUca().getUserId());
        map.put("DISCNT_CODE", packageData.get("ELEMENT_ID"));
        // 查询该用户是否本月终止过动力100业务
        IDataset tmpSet = RelaUUInfoQry.getUserLastRelation(userId, reqData.getUca().getProductId());
        if (tmpSet != null && tmpSet.size() > 0)
        {
            map.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
        }
        dctDataset.add(map); // 优惠信息
        IDataset dstParam = IDataUtil.getDataset(packageData, "DST_PARAM");
        if (dstParam != null && dstParam.size() == 0)
        {
            for (int k = 0; k < dstParam.size(); k++)
            {
                IData param = dstParam.getData(k);
                param.put("INST_TYPE", "D");
                param.put("INST_ID", instId);
                param.put("STATE", packageData.getString("STATE"));
                param.put("START_DATE", packageData.getString("START_DATE"));
                param.put("END_DATE", packageData.getString("END_DATE"));
                param.put("RELATION_TYPE_CODE", packageData.getString("RELATION_TYPE_CODE", ""));
                param.put("SPEC_TAG", "2");
            }
            paramDataset.addAll(dstParam);
        }
    }

    /**
     * 保存服务信息
     * 
     * @throws Exception
     */
    public void saveSvcData(IData packageData, IDataset svcDataset, IDataset paramDataset, String userId) throws Exception
    {
        String instId = SeqMgr.getInstId();
        IData map = new DataMap();
        map.putAll(packageData);
        map.put("INST_ID", instId);
        map.put("USER_ID", userId);
        map.put("USER_ID_A", reqData.getUca().getUserId());
        // 查询该用户是否本月终止过动力100业务
        IDataset tmpSet = RelaUUInfoQry.getUserLastRelation(userId, reqData.getUca().getProductId());
        if (tmpSet != null && tmpSet.size() > 0)
        {
            map.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
        }
        svcDataset.add(map); // 服务信息
        IDataset servParam = IDataUtil.getDataset(packageData, "SERV_PARAM");
        if (servParam != null && servParam.size() == 0)
        {
            for (int k = 0; k < servParam.size(); k++)
            {
                IData param = servParam.getData(k);
                param.put("INST_TYPE", "S");
                param.put("INST_ID", instId);
                param.put("STATE", packageData.getString("STATE"));
                param.put("START_DATE", packageData.getString("START_DATE"));
                param.put("END_DATE", packageData.getString("END_DATE"));
            }
            paramDataset.addAll(servParam); // 服务个性化参数
        }
    }
}
