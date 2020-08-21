/*批量平台业务前台JS*/
function initPlatTask() {
	var bizTypeCode = $("#BIZ_TYPE_CODE").val();
	//alert('bizTypeCode:'+bizTypeCode);
	if(bizTypeCode=='19'){
		$("#USERLEVEL").css("display", "");
		$("#BIZ_TYPE_CODE2").attr('disabled',false);
	}else{
		$("#USERLEVEL").css("display", "none");
		$("#BIZ_TYPE_CODE2").attr('disabled',true);
	}
}

function clearValue(){
	var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
	var param = "&BATCH_OPER_TYPE="+batch_oper_type;
	$.beginPageLoading("数据查询中...");
	$.ajax.submit(null,'initBatPopuPages',param,'SelectListB,SelectListC,SelectListD',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function clearValue1(){
	var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
	var param = "&BATCH_OPER_TYPE="+batch_oper_type;
	$.beginPageLoading("数据查询中...");
	$.ajax.submit(null,'initBatPopuPages',param,'SelectListC,SelectListD',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function clearValue2(){
	var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
	var param = "&BATCH_OPER_TYPE="+batch_oper_type;
	$.beginPageLoading("数据查询中...");
	$.ajax.submit(null,'initBatPopuPages',param,'SelectListD',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function queryOperInfo(spCode,bizCode,bizTypeCode){
	var bizCodeFinal = "";
	var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
	var paramOperInfo = "QUERY_OPER_INFO";
	if(bizCode != ""){
		bizCodeFinal = bizCode.replace('+','#');
	}
	var params = "&BATCH_OPER_TYPE="+batch_oper_type;
	params = params + '&CLICK_TAG=' + paramOperInfo;
	params = params + '&BIZ_TYPE_CODE=' + bizTypeCode;
	params = params + '&SP_CODE=' + spCode;
	params = params + '&BIZ_CODE=' + bizCodeFinal;
	//alert(params);
	$.beginPageLoading("数据查询中..");
	$.ajax.submit(null, 'initBatPopuPages', params, 'SelectListD', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function querySpInfo(){
	var bizTypeCode = $("#PARAM_BIZ_TYPE_CODE").val();
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryPlatInfo', '', 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

function tableRowDBClick(){
	var table = $.table.get("QueryListTable");
	var json = table.getRowData();
	var spCode = json.get('SP_CODE','');
	var spName = json.get('SP_NAME','');
	setPopupReturnValue(spCode,spName);
}

function returnAllValue(){
	if(!$.validate.verifyAll("MustInfoPart")){
		return false;
	}
	var BIZ_TYPE_CODE = $("#BIZ_TYPE_CODE").val();
	var SP_CODE = $("#SP_CODE").val();
	var BIZ_CODE = $("#BIZ_CODE").val();
	var OPER_CODE = $("#OPER_CODE").val();
	var START_DATE = $("#START_DATE").val();
	var REMARK = $("#REMARK").val();
	var INFO_VALUE = $("#INFO_VALUE").val();
	var d4=new Wade.DataMap();
	d4.put('BIZ_TYPE_CODE',BIZ_TYPE_CODE);
	d4.put('SP_CODE',SP_CODE);
	d4.put('BIZ_CODE',BIZ_CODE);
	d4.put('OPER_CODE',OPER_CODE);
	d4.put('START_DATE',START_DATE);
	d4.put('REMARK',REMARK);
	
	//无线音乐会员的属性处理
	if(BIZ_TYPE_CODE == '19')
	{
		d4.put('INFO_VALUE',INFO_VALUE);
		d4.put('INFO_CODE',"302");
	}
	var codingStr = d4.toString();
	var codingStr1 = $("#CODING1",window.parent.document).val();	
    var codingStr2 = $("#CODING2",window.parent.document).val();	
    var codingStr3 = $("#CODING3",window.parent.document).val();	
    var codingStr4 = $("#CODING4",window.parent.document).val();	
	
	var COMP_ID = $("#COMP_ID").val(); //设置返回值的到COMP_ID组件
    
	if(COMP_ID == '' || COMP_ID == undefined) //未设置，则默认
	{
		$.setReturnValue({'POP_CODING_STR':'已选择批量条件'},false); 
		$.setReturnValue({'CODING_STR':d4},true);
	}else if(COMP_ID == 'CODING1')
	{
		if(codingStr== codingStr2 || codingStr ==codingStr3 ||  codingStr ==codingStr4)
		{
			MessageBox.alert("提示","批量条件与其他三个批量条件相同，请重新设置批量条件");
			return ;
		}
		$.setReturnValue({'POP_CODING1':'已选择批量条件'},false); 
		$.setReturnValue({'CODING1':d4},true)
	}else if(COMP_ID == 'CODING2')
	{
		if(codingStr== codingStr1 || codingStr ==codingStr3 ||  codingStr ==codingStr4)
		{
			MessageBox.alert("提示","批量条件与其他三个批量条件相同，请重新设置批量条件");
			return ;
		}
		$.setReturnValue({'POP_CODING2':'已选择批量条件'},false); 
		$.setReturnValue({'CODING2':d4},true)
	}
	else if(COMP_ID == 'CODING3')
	{
		if(codingStr== codingStr1 || codingStr ==codingStr2 ||  codingStr ==codingStr4)
		{
			MessageBox.alert("提示","批量条件与其他三个批量条件相同，请重新设置批量条件");
			return ;
		}
		$.setReturnValue({'POP_CODING3':'已选择批量条件'},false); 
		$.setReturnValue({'CODING3':d4},true)
	}
	else if(COMP_ID == 'CODING4')
	{
		if(codingStr== codingStr1 || codingStr ==codingStr2 ||  codingStr ==codingStr3)
		{
			MessageBox.alert("提示","批量条件与其他三个批量条件相同，请重新设置批量条件");
			return ;
		}
		$.setReturnValue({'POP_CODING4':'已选择批量条件'},false); 
		$.setReturnValue({'CODING4':d4},true)
	}else
	{
		$.setReturnValue({'POP_CODING_STR':'已选择批量条件'},false); 
		$.setReturnValue({'CODING_STR':d4},true);
	}
	
}