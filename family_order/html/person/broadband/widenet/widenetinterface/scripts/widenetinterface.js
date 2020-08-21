//查询
function queryInterface()
{
	//查询条件校验
	if(!$.validate.verifyAll("QueryWidenetPart")) {
		return false;
	}
    $.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryWidenetPart', 'getTradeInterface', null, 'QueryListPart', function(data){
		$.endPageLoading();
		if(data.get("ALERT_CODE") != null) {
			$("#TipInfoPart").css("display","block");
		}else {
			$("#TipInfoPart").css("display","none");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
//重调
function restartInterface()
{
   
    var boxObj=$(":radio:checked");
	var size = boxObj.length;
	if(size<1){
		alert("请选择需要调城市热点接口的工单！");
		return false;
	}
	var tradeId = $(boxObj[0]).val();
    $.beginPageLoading("接口调用中..");
	$.ajax.submit(null, 'restartInterface', "&TRADE_ID="+tradeId, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function execInterface()
{
    $.beginPageLoading("批量调用中..");

	$.ajax.submit(null, 'execInterface', null, 'QueryListPart', function(data){
		$.endPageLoading();
		if(data.get("ALERT_CODE") != null) {
			$("#TipInfoPart").css("display","block");
		}else {
			$("#TipInfoPart").css("display","none");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}