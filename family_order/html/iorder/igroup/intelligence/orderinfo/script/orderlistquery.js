function queryListInfo() {
    if (!$.validate.verifyAll("conditionPart")) {
        return false;
    }
    $.beginPageLoading('正在查询信息...');
    $.ajax.submit('conditionPart','queryListInfo',null,'QueryResultPart,CustInfoPart',function(data){
        if( data != null && typeof(data.get("HAS_GROUP_INFO")) !="undefined" ){
            $("#FormInfoArea").css("display", "");
            $("#CustInfoPart").css("display", "");
        }
        $.endPageLoading();
    },function(error_code,error_info,derror){
        $.endPageLoading();
        showDetailErrorInfo(error_code,error_info,derror);
    });
}
function orderInfoQuery( obj ) {
    var orderId = $(obj).attr("ORDER_ID") ;
    var tradeId = $(obj).attr("TRADE_ID") ;
    $.beginPageLoading('正在查询信息...');
    $.ajax.submit(null,'orderInfoQuery','&orderId='+orderId+'&TRADE_ID='+tradeId,'OrderConditionPart',function(data){
        showPopup('queryPopup','queryPopup_Order');
        $.endPageLoading();
    },function(error_code,error_info,derror){
        $.endPageLoading();
        showDetailErrorInfo(error_code,error_info,derror);
    });
}
function getContractInfo( obj ) {
    var contractNumber =  $("#TRADE_CONTRACT_ID").text().trim();
    var custName = $("#TRADE_CUST_NAME").text().trim();
    $.beginPageLoading('正在查询信息...');
    $.ajax.submit(null,'getContractInfoByContractId','&contractNumber='+contractNumber+'&CUST_NAME='+custName,
        'ContractInfoPart,ContractDetailsPart',function(data){
        showPopup('queryPopup','queryPopup_Order');
        $.endPageLoading();
    },function(error_code,error_info,derror){
        $.endPageLoading();
        showDetailErrorInfo(error_code,error_info,derror);
    });
}
function showgoodlist(  ) {
    var offerId =  $("#PRO_ID").text().trim();
    var length =  ProTable.getData(true).length;
    if( offerId != '' && length == 0 ){
        $.beginPageLoading('正在查询信息...');
        $.ajax.submit(null,'getGoodList','&offerID='+offerId,
            'QueryProsResultPart',function(data){
                showPopup('queryPopup','queryPopup_Order');
                $("#QueryProsResultPart").removeClass("e_hide");
                $.endPageLoading();
            },function(error_code,error_info,derror){
                $.endPageLoading();
                showDetailErrorInfo(error_code,error_info,derror);
        });
    }
    if( length > 0 ){
        if( $("#QueryProsResultPart").hasClass("e_hide")){
            $("#QueryProsResultPart").removeClass("e_hide");
        }else{
            $("#QueryProsResultPart").addClass("e_hide");
        }
    }
}

function getOrderItemInfo(  ) {
    var orderId =  $("#ORDER_ID").text().trim();
    var tradeId = $("#TRADE_ID").text().trim();
    $.beginPageLoading('正在查询信息...');
    $.ajax.submit(null,'getOrderItemByOrderId','&orderId='+orderId+'&TRADE_ID='+tradeId,
        'OrderItemPart',function(data){
        showPopup('queryPopup','queryPopup_Order');
        $.endPageLoading();
    },function(error_code,error_info,derror){
        $.endPageLoading();
        showDetailErrorInfo(error_code,error_info,derror);
    });
}
function changeQueryValAttr(  ) {
    var obj = $("#queryValue");
    var queryType = $("#queryType").val();
    if( queryType != 1 ){
        obj.attr("datatype","numeric");
    }else {
        obj.attr("datatype","");
    }
}

//ajax 解析java中返回的错误信息
function showDetailErrorInfo(error_code,error_info,errorDetail){
    var err_desc = '服务调用异常';
    if(error_code != null && error_code != ''){
        error_info ='错误编码:'+error_code+ '<br />' +'错误信息:'+error_info;

    }
    MessageBox.error(err_desc, err_desc, null, null, error_info, errorDetail);
    return false;

}

function changeDefaultOp(obj,tp){
    debugger;
    if("1"==tp){
        // getContractInfo();
        $("#OrderPart1").css("display","");
        $("#openDefaultOp").css("display","none");
        $("#hideDefaultOp").css("display","");
    }
    if("2"==tp){
        $("#OrderPart1").css("display","none");
        $("#openDefaultOp").css("display","");
        $("#hideDefaultOp").css("display","none");
    }

}
function showDetailsInfo1(){
    var orderId =  $("#ORDER_ID").text().trim();
    var userId = $("#SPAN_USER_ID").text().trim();
    if( orderId != "" && userId == "" ){
        getOrderItemInfo();
    }
    if( $("#OrderItemPart").hasClass("e_hide")){
        $("#OrderItemPart").removeClass("e_hide");
    }else{
        $("#OrderItemPart").addClass("e_hide");
    }
}
function showDetailsInfo(){
    if( $("#ContractDetailsPart").hasClass("e_hide")){
        $("#ContractDetailsPart").removeClass("e_hide");
    }else{
        $("#ContractDetailsPart").addClass("e_hide");
    }
}