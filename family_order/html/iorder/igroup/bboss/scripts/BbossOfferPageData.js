/**
 * bboss业务商产品配置模型对象
 * Created by chenkh on 2016/12/29.
 */
var BbossOfferPageData = function (el, hidePartId) {
    var that = this;
    if (hidePartId) {
        that.partId=hidePartId;
    }else{
        that.partId = "BBOSS_INFO_OBJ";
    }
    if ($("#"+hidePartId)){
        $("body").append("<span id='"+that.partId+"' style='display:none;'></span>");
    }
};
(function ($) {
    "use strict";
    var b = BbossOfferPageData.prototype;
    b.setData=function (pageObj,data) {
        // 为了兼容集团组件的pageData工具类进行修改，针对两个参数和单个参数进行重载处理
        if(pageObj.length>0 && !pageObj instanceof Wade.DataMap) {
            pageObj.text(data.toString());
        }
        // 如果是bboss页面存储产品结构
        else if (pageObj instanceof Wade.DataMap){
            $("#"+this.partId).text(pageObj.toString());
        }
        // bboss页面存储集团组件存储产品结构
        else {
            $("#"+this.partId).text(data.toString());
        }
    }
    b.getData=function (pageObj) {
        // 兼容集团数据对象取值方式，提供页面元素取值法
        if (pageObj && pageObj.length>0) {
            var data = pageObj.text();
            return $.DataMap(data);
        }
        var data = $("#"+this.partId).text();
        return $.DataMap(data);
    }
})(window.Wade);