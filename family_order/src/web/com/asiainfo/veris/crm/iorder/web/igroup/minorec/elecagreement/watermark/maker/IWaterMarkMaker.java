package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.watermark.maker;

import com.ailk.common.data.IData;

import javax.servlet.http.HttpServletResponse;

/**
 * 水印制作接口(单例，请注意statc公共变量的操作)
 *
 * @author ckh
 * @date 2018/12/14.
 */
public interface IWaterMarkMaker
{
    /**
     * 处理水印
     *
     * @param paramMap 传入参数集合
     * @param response 返回对象
     * @throws Exception 处理异常
     */
    void dealWaterMarkMaker(IData paramMap, HttpServletResponse response) throws Exception;
}
