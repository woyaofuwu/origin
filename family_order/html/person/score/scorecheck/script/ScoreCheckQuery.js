function queryScoreTrade(){
	
	if(!$.validate.verifyAll("QueryRecordPart"))
	{
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('ScoreQryCondPart','queryScoreCheck','','ScoreQryResultPart',
			function(){  
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}

function resetQry(){
	$("#cond_CHECK_ID").val('');
	$("#cond_SCORE_TRADE_TYPE").val('');
	$("#cond_START_DATE").val('');
	$("#cond_END_DATE").val('');
}


function tableRowDBClick(){
	var rowData = $.table.get("scoreTable").getRowData();
	var checkId = rowData.get("CHECK_ID");
	var params = "&CHECK_ID="+checkId; 
	$.popupPage("score.ScoreCheckQueryDet", "queryScoreCheckDet", params, "查询积分对账明细！", "800", "450",
            null, null, null, false, true);
}
 