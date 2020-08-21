var setSaleBookInfo = function () {
    var obj = $("input[name=saleActiveRadio]:checked");

    if (obj.length !== 1) {
        $.validate.alerter.one($("#saleActiveBookInfoTable")[0], "请选择一条记录！");
        return;
    }
    var netOrderId = obj.val();
    parent.saleActiveFilter.setParam(netOrderId);
    //parent.saleActiveFilter.refreshMarketPartAtferAuth();
    hidePopup(this);
};

$(document).ready(function () {
    $("#submitBtn").tap(setSaleBookInfo);
});