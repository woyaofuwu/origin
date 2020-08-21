package com.asiainfo.veris.crm.iorder.web.person.saleactive;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

import java.util.StringTokenizer;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2020/1/8 9:44
 */
public abstract class SaleActiveRuleCheckMulChannel extends PersonBasePage {
    /**
     * 取活动
     * */
    public void initCampnTypes(IRequestCycle cycle) throws Exception
    {
        IData params = this.getData();

        IDataset activeList = CSViewCall.call(this, "SS.SaleActiveRuleCheckMulChannelSVC.queryCampnTypes", params);
        setBatcampnTypes(activeList);;
    }

    /**
     * 取产品
     * */
    public void queryProdByLabel(IRequestCycle cycle) throws Exception
    {
        IData params =  getData();
        params.put("EPARCHY_CODE", this.getTradeEparchyCode());
        IDataset prodList = CSViewCall.call(this, "SS.SaleActiveRuleCheckMulChannelSVC.queryProductsByLabel", params);

        if (IDataUtil.isNotEmpty(prodList)) {
            for (int i = 0; i < prodList.size(); i++) {
                IData prodMap = prodList.getData(i);
                prodMap.put("SALE_PRODUCT_NAME", prodMap.getString("PRODUCT_ID") + "|" + prodMap.getString("PRODUCT_NAME"));
            }
        }
        setSaleProducts(prodList);
        setAjax(prodList);
    }

    /**
     * 取包列表
     * */
    public void queryPackageList(IRequestCycle cycle) throws Exception
    {
        IData params =  getData();
        params.put("EPARCHY_CODE", this.getTradeEparchyCode());
        IDataset packList = CSViewCall.call(this, "SS.SaleActiveRuleCheckMulChannelSVC.queryPackageByProdID", params);
        //增加全部选项,默认
        DatasetList packAllList = new DatasetList();
        DataMap aLLMap = new DataMap();
        aLLMap.put("PACKAGE_ID","");
        aLLMap.put("PACKAGE_NAME","全部");
        packAllList.add(aLLMap);
        packAllList.addAll(packList);
        setSalePackages(packAllList);
        setAjax(packList);
    }


    /**
     * 校验规则
     * */
    public void checkSaleActiveRule(IRequestCycle cycle) throws Exception{
        IData cond = this.getData();
        Pagination page = getPagination("listnav");
        IDataOutput callOut = CSViewCall.callPage(this, "SS.SaleActiveSVC.checkPrdAndPkgForSMS", cond,page);
        IDataset callSet= callOut.getData();
        IDataset rtnSet=new DatasetList();

        String packageId = cond.getString("PACKAGE_ID","");
        //是否查询某活动所有营销包标识
        //当查询所有营销包,若规则返回中有20191204则不显示该条营销包校验结果
        //当查询单个营销包,若规则返回中有20191204则只显示该条营销包校验结果
        boolean isAllCheck=false;
        if("".equals(packageId)){
            //前端传入packageId为"",表示查询所有
            isAllCheck=true;
        }
        for(int k=0;k<callSet.size();k++){
            IData otherRightData=callSet.getData(k);
            /*
             * 0$$查询成功  14024019$$该用户不是个人宽带装机完工用户！
             * 14024019$$该用户不是个人宽带装机完工用户！##14024019$$该用户不是个人宽带装机完工用户！
             * */
            String result=otherRightData.getString("RESULTS","");
            /*
             * 接口可能涉及工号权限，返回有所不同，要分别判断
             * */
            if("".equals(result)){
                String xResultCode=otherRightData.getString("X_RESULTCODE","");
                String xResultInfo=otherRightData.getString("X_RESULTINFO","");
                IData errData=new DataMap();
                errData.put("ERR_NUM", xResultCode);
                errData.put("ERR_INFO", xResultInfo);
                errData.put("DEAL_DESC", "产品及权限错误");
                errData.put("DEAL_LINK", "产品及权限错误");
                rtnSet.add(errData);
            }else{
                IData errData=new DataMap();
                errData.put("OFFER_NAME", otherRightData.getString("OFFER_NAME",""));

                StringTokenizer st=new StringTokenizer(result,"##");
                String rtnInfos="";
                boolean isShow=true;
                String productId = otherRightData.getString("PRODUCT_ID");
                while(st.hasMoreElements()) {
                    IData rtnData = new DataMap();
                    String rtnCode = "";
                    String rtnInfo = "";
                    String code_info = st.nextToken();
                    rtnCode = code_info.substring(0, code_info.indexOf("$$"));
                    rtnInfo = code_info.substring(code_info.indexOf("$$") + 2);
                    if ("0".equals(rtnCode)) {
                        rtnInfos += "该用户可以办理此活动。";
                        break;
                    }
                    if("69900631".equals(productId)&&"20191204".equals(rtnCode)){
                        //20191204该条件若查询全部则不显示,若查询单个则只显示该条规则返回
                        if(isAllCheck){
                            isShow=false;
                        }else{
                            rtnInfos=rtnCode+":"+rtnInfo;
                        }
                        break;
                    }
                    rtnInfos+=rtnCode+":"+rtnInfo;
                }
                errData.put("ERR_INFO",  rtnInfos);
                if(isShow){
                    rtnSet.add(errData);
                }
            }
        }
        //long dataCount=result.getDataCount();
        //setRecordCount(dataCount);
        setInfos(rtnSet);
    }

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset products);

    public abstract void setBatcampnTypes(IDataset products);

    public abstract void setSaleProducts(IDataset saleProducts);

    public abstract void setSalePackages(IDataset salePackages);

    public abstract void setRecordCount(long recordCount);
}
