/**
 * 设置银行信息
 */
$(document).ready(function(){
    $("#qryBankBtn").bind("click", function(){
        if(!$.validate.verifyAll("QueryBankPart")){
            return;
        }
        $.beginPageLoading("加载银行。。。");
        $.ajax.submit("QueryBankPart", "queryBankList", null, "QueryListPart",
            function(data){
                $.endPageLoading();
            },function(code, info, detail){
                $.endPageLoading();
                alert("加载银行报错:"+info);
            });
    });
});
