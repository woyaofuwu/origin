function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO")+"&CUST_NAME="+data.get("CUST_INFO").get("CUST_NAME"), 'refreshParts,refreshParts2', function(error_code,error_info){
		var needscore = parseInt($("#SCORE_NEED").val());
		var scoretotle = parseInt($("#SCORE_TOTLE").val());
		var subscore = parseInt($("#SCORE_SUR").val());
		var subscore =  scoretotle - needscore ;		
		$("#SCORE_SUR").val(subscore);
    });
}


function chkTatle() {
	debugger;
	var scoretotletmp = 0;
	var scoresurtmp = 0;
	var needscoretmp = 0;
	var evaluetmp = 0;
	var perscoretmp = 0;
	
	var scoretotletmp = parseInt($("#SCORE_TOTLE").val());
	var scoresurtmp = parseInt($("#SCORE_SUR").val());
	var needscoretmp = parseInt($("#SCORE_NEED").val());
	var evaluetmp  = parseInt($("#EVALUE").val());
	var perscoretmp = parseInt($("#PER_SCORE").val());
	
	needscoretmp = evaluetmp * perscoretmp ;
	
	if(needscoretmp > scoretotletmp)
	{
		$("#EVALUE").val("") ;
		$("#SCORE_NEED").val("") ;
		
		alert("可兑积分不足本次兑换，请重新输入");

		return ;

	}
	
	if(evaluetmp < 1)
	{
		alert("电子券金额不能小于1");
	}
	
	else
	{
		scoresurtmp = scoretotletmp - needscoretmp ;	
		$("#SCORE_NEED").val(needscoretmp) ;
		$("#SCORE_SUR").val(scoresurtmp) ;
	}
	
}

/*
//业务提交
function onTradeSubmit()
{
	$.cssubmit.submitTrade();
}*/
