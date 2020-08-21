/**
 * chenzg
 * @returns {Boolean}
 */
function queryGrpValueCardInfo()
{
	if (!$.validate.verifyAll()){
		return false;
	}
	if($('#cond_START_CARD_NUMBER').val().length==0 
			&&$('#cond_END_CARD_NUMBER').val().length==0
			&&$('#cond_GROUP_ID').val().length==0
			&&$('#cond_GROUP_NAME').val().length==0){
		alert("请至少输入一个查询条件！");
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('QueryCondPart,recordNav', 'queryGrpValueCardUseInfo', '', 'QueryDataPart', function(data){
		$.endPageLoading();
		if(data.get("DATA_COUNT")=="0"){
			alert("查询无数据！");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}


function fillEndCardNumber()
{
	if($('#cond_END_CARD_NUMBER').val().length==0)
	{
		$('#cond_END_CARD_NUMBER').val($('#cond_START_CARD_NUMBER').val());
	}
}

function displayFields()
{
	var stateCode = $('#cond_STATE_CODE').val();
	//入库状态
	if(stateCode == "1")
	{
		$('#li_GROUP_ID').css("display", "none");
		$('#li_GROUP_NAME').css("display", "none");
		$('#cond_GROUP_ID').val("");
		$('#cond_GROUP_NAME').val("");
	}
	//销售、充值状态
	else
	{
		$('#li_GROUP_ID').css("display", "");
		$('#li_GROUP_NAME').css("display", "");
	}
}

