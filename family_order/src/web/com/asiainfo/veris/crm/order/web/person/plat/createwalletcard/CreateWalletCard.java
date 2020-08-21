
package com.asiainfo.veris.crm.order.web.person.plat.createwalletcard;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class CreateWalletCard extends PersonBasePage
{

    public void checkRealName(IRequestCycle cycle) throws Exception
    {

        IData temp = getData(); // SERIAL_NUMBER,custinfo_PSPT_ID,custinfo_CUST_NAME,custinfo_BOSS_ID
        temp.put("SERIAL_NUMBER", temp.getString("AUTH_SERIAL_NUMBER"));
        IDataset dataset = CSViewCall.call(this, "SS.CreateWalletCardSVC.checkRealName", temp);
        IData data = null;
        if (IDataUtil.isNotEmpty(dataset))
        {
            data = dataset.getData(0);
        }

        if (IDataUtil.isNotEmpty(data) && !"0000".equals(data.getString("X_RSPCODE", "")))
        // 并不是只有2998才是平台返回错误，这里修改成不为0000
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "平台返回错误：" + data.getString("X_RESULTINFO", ""));
        }
        String fild_id = data.getString("FILD_ID");
        String path_fildid = data.getString("PATH_FILD_ID");

        if (StringUtils.isBlank(fild_id))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "平台返回数据错误:FILD_ID为空");
        }

        if (StringUtils.isBlank(path_fildid))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "平台返回数据错误：PATH_FILD_ID为空");
        }

        // 这个可以不需要配置了，使用上传后的FTP地址。
        IData urldata = new DataMap();
        urldata.put("PARAM_ATTR", "6666");
        urldata.put("PARA_CODE3", "url");
        urldata.put("SUBSYS_CODE", "CSM");
        IDataset urlset = CSViewCall.call(this, "SS.CreateWalletCardSVC.querySvcstatecomm", urldata);
        // bean.querySvcstatecomm(pd, // urldata);
        String url = "";
        if (urlset != null && urlset.size() > 0)
        {
            url = urlset.getData(0).getString("PARAM_CODE", "") + fild_id;
        }
        else
        {
            CSViewException.apperr(PlatException.CRM_PLAT_88);
        }

        String tests = getTest();

        if (StringUtils.isNotBlank(data.getString("CUSTOMER_PICTURE", "")) && data.getString("CUSTOMER_PICTURE", "").length() > 64)
        {
            if (data.getString("CUSTOMER_PICTURE").startsWith("data:image"))
            {
                tests = data.getString("CUSTOMER_PICTURE");
            }
            else
            {
                tests = "data:image/png;base64," + data.getString("CUSTOMER_PICTURE", "");
            }
        }

        // 生成图片临时文件
        IData input = new DataMap();
        input.put("BASE", tests);
        input.put("PATH_FILD", path_fildid);

        IDataset toBase = CSViewCall.call(this, "SS.CreateWalletCardSVC.dealIoToBase64", input);

        // 将本地文件上传到FTP服务器，并获得下载url
        IData ftpParam = new DataMap();
        ftpParam.put("FTP_SITE", "personserv");
        ftpParam.put("FTP_PATH", "upload/attach");
        ftpParam.put("FULL_PATH_NAME", path_fildid);
        ftpParam.put("FILE_ID", fild_id);
        ftpParam.put("DOWNLOAD_FILE_NAME", fild_id);
        IDataset urlDataset = CSViewCall.call(this, "SS.CreateWalletCardSVC.dealFile", ftpParam);

        data.put("custInfo_RSP_CODE", data.getString("RSP_CODE", ""));

        data.put("custInfo_IDCARD_DEPARTMENT", data.getString("ID_CARD_DEPARTMENT", ""));
        data.put("URL", url);

        setUrl(url);
        setAjax(data);
        setInfo(data);
    }

    public void checkRealNameFail(IRequestCycle cycle) throws Exception
    {

        IData temp = getData();
        temp.put("SERIAL_NUMBER", temp.getString("AUTH_SERIAL_NUMBER", ""));
        IDataset dataset = CSViewCall.call(this, "SS.CreateWalletCardSVC.checkRealNameFail", temp);
        IData data = null;
        if (IDataUtil.isNotEmpty(dataset))
        {
            data = dataset.getData(0);
        }
        if (IDataUtil.isNotEmpty(data) && "2998".equals(data.getString("X_RSPCODE", "")))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "平台返回错误：" + data.getString("X_RESULTINFO", ""));
        }
        // IData data = new DataMap();
        // common.error("1111");
        // pd.setAjaxData(inparam);
        // redirectToMsg("实名验证失败！","getPageName()","onInitTrade");
        // common.error("1111");

    }

    public void getCustInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData custInfo = new DataMap(data.getString("CUST_INFO", ""));

        IData allData = new DataMap();
        allData.putAll(custInfo);

        IDataset tradeDataset = CSViewCall.call(this, "SS.CreateWalletCardSVC.getTradeId", allData);
        allData.put("BOSS_ID", tradeDataset.getData(0).getString("TRADE_ID"));
        allData.put("UPDATE_STAFF_ID", getVisit().getStaffId());

        setCustInfo(allData);

    }

    public String getTest() throws Exception
    {
        String tests = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAkGBwgHBgkIBwgKCgkLDRYPDQwMDRsUFRAWIB0iIiAd" + "Hx8kKDQsJCYxJx8fLT0tMTU3Ojo6Iys/RD84QzQ5Ojf/2wBDAQoKCg0MDRoPDxo3JR8lNzc3Nzc3"
                + "Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzc3Nzf/wAARCACKAGMDASIA" + "AhEBAxEB/8QAGwAAAgMBAQEAAAAAAAAAAAAAAAQBAwUGAgf/xABCEAACAQMBBAYHAwoFBQAAAAAB"
                + "AgMABBEFBhIhMRNBUWFxgRQiMkKRocEjUtEHFTNicoKSsbLCFqLS4fAlNDVTdP/EABQBAQAAAAAA" + "AAAAAAAAAAAAAAD/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwD7hRUVNBBIAyTw"
                + "ql7uBAS0gAHXzrzqF3HaQb8nEkhVXGSxPIAVRbxyyMJZx9pzVM5Cf70FhupZf+2gbH35QUHw51Tc" + "R6nIv2V3bRn/AOdj/eKfVMcySa9bo7B8KDm5I9qIDvQXNjcge4yMuf5/zrS0fULq6jddSsWs50OC"
                + "N/eR+9Tw+BFadK3kEcxi6WFJVV84YZAPLl50DVFLSxPCnSWo4rzj6mHYOw17trhLiISIeB6uyguo" + "oooIooooJqCd0ZPKprH2jkZ7ZLKNiHu3ER3ee7zf/LkeLCgrsCdVvG1DOYFylrw4BeRk8W447FAP"
                + "XW0ihBha8W0K28CRIAqqAAByFW0BRRRQRUMoIwe3NeqW1C49FtWl4ZBAAPXxoGKQnRrK4NzGPsXP" + "2ydn647+3tHHq4tWdwl1bpMnJh8Oo1awDKQaAUhgCDkGppa0HRb0B4BOKD9X/b8KZoCiiiggnAJr"
                + "Ets3m0Ukh4x2kW6v7THif8vyFbUmdw45ngKy9nlUx3lwvKW6dVP6qfZj+knzoNaioqaAooqMgY76" + "BLULKe6dGh1G4tAoIYRBfWyDx4g8ckfCud13S5t5YRrWpMI4Zrl8unqgKQPd6y3Dwrpbu5SNTlwF"
                + "HM9n/OJ8qyp4JbzTdRnUYlvkFvDn3Yz6o+bMaBHZ+yugZrGTVrxDupcxFdzJSRRnmpzhlbs9qupt" + "ozFBHG0rzFVA6STG83ecACsrW19Aez1OIYS0PRTAf+hsAn90hW8A1aVnMJBImcmNyPI8R8iKDzfH"
                + "olS5HOI+t3qef40yDkZFVXib9rKo4kqcUvo0/T2EeeaeofLl8sUDtFFFBTezrbWstw/sxI0jeABP" + "0pDZRGTZzTw/tmEM57WPE/MmvO1bFdnr1BzlQRD98hfrTWiYOj2RHIwIfkKBwsAyjtqia5C3CwLx"
                + "fd3iO7kPjx+Bqq9n6GRXIJ3QcAcSTwAHzpLZ/pJI7jUrtvWuHLIx4KsY4LjuwM+B7c0GszCOMtIw" + "HaTWYb83N0VgzuL6iEe83vHwAwPEnsrOe8k1y8dYnMenQrvvIOBZeYOe1uY7FGfeFWCY2enNcxwg"
                + "zzKBBCOGAfYXu7T2CgVvJW1TXo9HtWzFbgS3jjkAfZXxP9PiK19SXUAFEF1DZwp7O5bmeQ8OzkPg" + "anZvRxpNiRI3SXc7ma5lI4vI3PyHAAdQFcL+V7S9o9Z02aXTGlFnZToDZxpvekLu5aRl94BiF3cH"
                + "2WPHhQdtpt1bXu/atqbXjlSHRhFxHWCFHAeNI2k0mj6vBp87MyOBFFIx9tMncz3j1lPb6p68Vw/5" + "ONlPzisuqa7HEJ5IwkUsFmtt0DhhuGPdVfWHrZIHWAc8RXW7YrOdU0yGBs3LgNE5HvxneHkTQdme"
                + "IwaxdEboL+9s26nLr39vyK1qWdwl3aQ3MXsSoHXPYRmkujWPXt48Olh3ge9eHzBPwoNOijFFBj7T" + "ENaxxdpaQjuVT9StM6D/AOHtB92ML8OH0pLaNt2Kdzyjt8A97MM/00/o43bIJ91iPr9aDI2oumjt"
                + "dQihfE4iG7jmu9jB+K1DRnXehgtiE0iJF3sH9Pw9VB+p2nrHLgc142q09ZjJeNI6rEUWUZ4GM4z4" + "cevsz3YX/JhqAudDnsXcNPp1y8Dke8Oat4EH5UG3f20Vtpno68I5ZB0zdbAnLnxIBHyFNQ2280c1"
                + "wg6UZbH3WPD5DhRJH6VeoG/RWx3iPvSEcPgDnxI7KcoIqi5s7e6ZWnjDOnsuCVZfAjiKYooK1gjB" + "U7u8V5FiWI8zXG7azrDtTs4GOFJmLnOMDC8SfjXbV8t2+H5z/KBYWIHSRWtkJJVABALu2Ac/sg4+"
                + "lB2uyV2l1ZXHRDEC3DmDviY7ynzzw7sUzqR6O8tJh7kgU+DHd/k1J6IBbX7W4JO/AjEk+8C2f6hT" + "mtIWiYKPW3GI8Rx+lBp0UKQyhhyPEUUGJtYP+lPu+0x+OBWhp5CpKOrpB81U/WkdpDvQFOeEyfN1"
                + "X6mrFl6Gwu5vuRiT+FB/poGL+Pehu1PsyQH4jP4iuEtLj8w7b2V3wSx1fToUuMDAWRSEV/IlV/fz" + "1V9CuAHjA573qjzBrgdeiD6Zs7dMqMolmtHDjgVdW592YxQfQ0UKDjrJJr1WNp18ltBDHNKzwMej"
                + "jlkOWVgPZc+R9b48eJ2M9lAVOaivEsqQoXkYKo+fd30FOp39rpen3F/fSrFbW6F5HY8gK+Y7IdNq" + "mr32u3ilbm+m3kRj+iQDCL3YUDJ/kSSiW3Wr3Ov65Lp1xMbTT7N23YSQCzKAekkPHlngMHHA8ScU"
                + "7sxMvo7rArdBECDvDJc893B7zkjrJCk+2zB2lkQdajZMlVg3QfF0OfMHPgR1k1r3RDXkCHrz81b8" + "Kz7GHcie5b2jIMnOc8QefX1caZuJM6laHtyD5HH1oNGFSkKKfdUD5UVIGBiigx9aG9bXr9jwR/B1"
                + "P91REOn069h7YWQ/w4/Grtfj3NHvXXmMSn90qf7aX0hsy6omf0cpTH8X0IoGNEuTd7PaVdMcma3g" + "kPmqn61kR6et/onorFlEGovulDxUb7YI8mB8687C3Jk2E08k5MDmA925MUHyArY0ldy7vVHsySmT"
                + "l+7j/L8+6gwtTSSPZu5eNVLwqk6gcAAjBmPhugjryFHOvelXF/DZme2k+zBA9HkRnXPUBjiCeeBk" + "Dsqdaa5/Nl3bRjennPo2FIOWcbvDtALDPnnHCuhhtDapGsIDRqeCjhjJOST1nt86DOg1DaC7bdj0"
                + "a3tUzxmubrPDtCKuT4ErWpbWjI/TXUxnn6mK7qp3KvV8z301U0Hx7anTIv8AGN+8zMCZ42QFeDFl" + "UgD73EnhyyBkjhjatjbQ6TMsSIqLCd0L75ZSRnxBLfsnPOQhew1XRLe+ufSJULl1ETqCRlc4zwPY"
                + "TzzWZf7NRxJJP0zMN1x0eAFyx4YHmM8eOO80DtpcGaLVowSRDPwJ5n1VP414dw05mHEQXAJ8Gcf6" + "s+VJ7NSia51ZieEzdJ/E0v0ApzQ06aTUonwQrpGQf2Bmg36KAMADOaKCu7gW6tZrd/ZlRkPgRisL"
                + "Z/JvdeQnLi4APj0a/XNdF1VjaTbG01W9V/anUTHzkk/kCooOc2GJi2U1OPPCHUZiO4Flf+6ulsgj" + "3U0YLZZpd4g4ON/l3f8AO2sDZmzntdA15JUKlrmR0z1jcUfSuo02LAaXHtu5znqLkj+dBZLbBJIJ"
                + "IY1xCGUIABwOOXV1VatxGThiVPY6kVbRQKw39tM7qkqEKSN7eGDjnVqy9IfsgSv3zy8u2vXRoWDF" + "FLduK90EAY680lrR3dLuH+4oc+AIP0p6qriETwvExIDDGRQcZoKPZy3AcFWxAHXsJE2Rnr4mtfY9"
                + "+mj1Gf792f6E/Gqtpk9DQyxL+kCoMdbgPu/M0/szaGzsJEIwWmZuXVwA+QFBr0UUUBSd0pS9tZxy" + "9aJu4Ngj5rjzpyjGedBSYFMMkQHqvnPnXjTVKafbK3tCJQfHHGmaKAqOupooCiiigKKKKBO/sheS" + "2pkxuQyiQjtI5U0Fwcjwr1RQFFFFB//Z";

        return tests;
    }

    public abstract String getUrl();

    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        setCondition(data);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData authData = getData("AUTH", true);
        IData acceptData = getData("custInfo", true);
        IData param = new DataMap();
        param.putAll(authData);
        param.putAll(acceptData);
        IDataset dataset = CSViewCall.call(this, "SS.CreateWalletCardRegSVC.tradeReg", param);
        setAjax(dataset);
    }

    public abstract void setAjaxDataset(IDataset ajaxDataset);

    public abstract void setCondition(IData condition);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setInfo(IData info);

    public abstract void setUrl(String url);

}
