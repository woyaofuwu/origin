/**
 * bboss业务商产品配置页面js
 * Created on 2016/12/27.
 */
function ShowMerchPop() {
    showPopup("popup","merchInfoPopupItem");
}

function ShowMerchpPop() {
    showPopup("popup2","merchpInfoPopupItem");
}

function showFloatDetailInfo(compId) {
    $("#" + compId + "_float").attr("class", "c_float c_float-show");
}

function hideFloatDetailInfo(compId) {
    $("#" + compId + "_float").attr("class", "c_float");
}
function addMerchBizMode() {
    $("#MerchBizMode[type='checkbox']");
    $("#MERCH_BIZMODE").val();
}
function merchOptDeal(obj) {
    var merchOpt = "";
    var arr = $("#merchOptPopupItem").find("input[type='checkbox']")
    for (var i=0;i<arr.length;i++){
        if (arr[i].checked){
            merchOpt = merchOpt + arr[i].value + ",";
        }
    }
    merchOpt = merchOpt.substr(0,merchOpt.length-1);
    $("#MERCH_OPT").val(merchOpt);
    backPopup(obj);
}

function merchInfoSubmit(el) {
    var bbossOffer = BbossOfferObj.getData();
    var merchInfo = bbossOffer.get("MERCH_INFO");
    if (!merchInfo)
        merchInfo = new Wade.DataMap();
    merchInfo.put("MERCH_NAME",$("#MERCH_NAME").val());
    merchInfo.put("MERCH_ID",$("#MERCH_ID").val());
    merchInfo.put("POPRODUCT_ID",$("#POPRODUCT_ID").val());
    merchInfo.put("MERCH_OPT",$("#MERCH_OPT").val());
    merchInfo.put("MERCH_BIZMODE",$("#MERCH_BIZMODE").val());
    BbossOfferObj.setData(bbossOffer);
    var html = "商品名称："+merchInfo.get("MERCH_NAME") +"<br>";
    html += "商品全网编码："+merchInfo.get("MERCH_ID")+"<br>";
    html += "商品本省编码："+merchInfo.get("POPRODUCT_ID")+"<br>";
    html += "商品操作类型："+merchInfo.get("MERCH_OPT")+"<br>";
    html += "商品业务开展模式："+merchInfo.get("MERCH_BIZMODE");
    $("#merchInfoShowPart").html(html);
    $("#MerchInfoPart").find("span[class='e_tag e_tag-red']").css("display","none");
    backPopup(el);
}