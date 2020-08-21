//提交校验
function checkBeforeSubmit()
{  
//     if(!$.validate.verifyField($("#SERIAL_NUMBER")[0]))
//     {
//        return false;
//     }
     return true;
}

function serialNumberKeydown(event)
{
	event = event || window.event; 
	e = event.keyCode;
	
	if (e == 13 || e == 108)
	{
		getCommInfo();
	}
}

function getCommInfo()
{
	if(checkBeforeSubmit())
	{
		$.ajax.submit('QueryNumberPart', 'getCommInfo', null, 'QueryCommInfoPart,refreshArea,refreshArea2,refreshArea3,refreshArea4', function(){
		
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
    	});
	}
}

//BOSS接口查询
function initScoreBizInfos()
{
	var sn = $('#SERIAL_NUMBER').val();
	if(sn != '')
	{
		$.ajax.submit('QueryNumberPart,QueryPart1', 'queryScoreBizInfos', null, 'refreshArea', function(data){
		
			if(data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
    	});
	}

}

function queryScoreBizInfos()
{
	//alert('queryScoreBizInfos1');
	if(checkBeforeSubmit())
	{
		//alert('queryScoreBizInfos2');
		$.ajax.submit('QueryNumberPart,QueryPart1', 'queryScoreBizInfos', null, 'refreshArea', function(data){
		
			if(data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
    	});
	}

}

function queryScoreDetail()
{
	if(checkBeforeSubmit() && chkAcyc('START_CYCLE_ID','END_CYCLE_ID'))
	{
		$.ajax.submit('HintPart,QueryNumberPart,QueryPart2', 'queryScoreDetail', null, 'refreshArea2', function(data){
		
			if(data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
    	});
	}

}

function queryYearSumScore()
{
	if(checkBeforeSubmit() && chkAcyc('START_CYCLE_ID2','END_CYCLE_ID2'))
	{
		$.ajax.submit('QueryNumberPart,QueryPart3,HintPart', 'queryScoreYear', null, 'refreshArea3,integralFeeSum', function(data){
		
			if(data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
    	});
	}

}

function queryScoreExchangeYear()
{
	if(checkBeforeSubmit())
	{
		$.ajax.submit('QueryNumberPart,QueryPart4,HintPart', 'queryScoreExchangeYear', null, 'refreshArea4,scoreSum', function(data){
		
			if(data.get('ALERT_INFO') != '')
			{
				alert(data.get('ALERT_INFO'));
			}
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
    	});
	}

}

function myTabSwitchAction(ptitle,title){
	return true;
}
/** check year */
function chkYear() {
  var year_id = $("#YEAR_ID").val();
  if (year_id.length<=0 || year_id == "") {
  	  alert("年度不能为空!");
  	  return false;	
  }
  return true;
}


/** check acyc */
function chkAcyc(start_cycle_id,end_cycle_id) {
	var start_cycle_id = $('#'+start_cycle_id).val();
	var end_cycle_id = $('#'+end_cycle_id).val();
	if (start_cycle_id <=0 || start_cycle_id == "") {
		alert("请选择开始帐期!");
		return false;
	}
	if (end_cycle_id <=0 || end_cycle_id == "") {
		alert("请选择结束帐期!");
		return false;
	}
	if (start_cycle_id > end_cycle_id) {
		alert("结束帐期不能小于开始帐期!");
		return false;
	}
	return true;
}

/*
*@description 如果是从用户360视图查询界面过来的，则自动获取用户号码进行查询。
*/
function user360ViewAutoExecute(){
    var isFromUser360View = parent.$("#IS_FROM_USER360VIEW").val();
    if(isFromUser360View!=null && isFromUser360View!="undefined" && isFromUser360View!=""){
    	$("#title").css("display","none");
   		 $("#QueryNumberPart").css("display","none");
		$("#QueryCommInfoPart").css("display","none");
	    if(parent.$("#SERIAL_NUMBER").val()==""||parent.$("#SERIAL_NUMBER").val()==null){
			parent.mytab.switchTo("用户基本信息");
			return false;
		}
    	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val() 
		+ '&CUST_ID=' + parent.$("#CUST_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
		
		var serialNumber = parent.$("#SERIAL_NUMBER").val();
		$("#SERIAL_NUMBER").val(serialNumber);
		getCommInfo();
		queryScoreBizInfos();
		queryScoreExchangeYear();
	}
}

function initParams()
{

	var param = '&EPARCHY_CODE=' + parent.$("#EPARCHY_CODE").val() + '&CUST_ID=' + parent.$("#CUST_ID").val()
  			+ '&USER_ID=' + parent.$("#USER_ID").val() +'&SERIAL_NUMBER='+parent.$("#SERIAL_NUMBER").val();
	if(parent.$("#SERIAL_NUMBER").val() != '')
	{
		$.ajax.submit('', 'initParams', param, 'HintPart', function(data){
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
    	});
	}

}
