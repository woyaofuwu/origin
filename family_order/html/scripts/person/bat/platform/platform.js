if(typeof(BatPlatform)=="undefined"){
	window["BatPlatform"]=function(){
		
	};
	var batplatform = new BatPlatform();
}

(function(){
	$.extend(BatPlatform.prototype,{
	
	initPlatTask: function () {
		var bizTypeCode = $("#BIZ_TYPE_CODE").val();
		//alert('bizTypeCode:'+bizTypeCode);
		if(bizTypeCode=='19'){
			$("#USERLEVEL").css("display", "");
			$("#BIZ_TYPE_CODE2").attr('disabled',false);
		}else{
			$("#USERLEVEL").css("display", "none");
			$("#BIZ_TYPE_CODE2").attr('disabled',true);
		}
	},
	
	clearValue: function (){
		var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
		var param = "&BATCH_OPER_TYPE="+batch_oper_type;
		$.beginPageLoading("数据查询中...");
		$.ajax.submit(null,'',param,'SelectListB,SelectListC,SelectListD',function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
	},
	
	clearValue1: function (){
		var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
		var param = "&BATCH_OPER_TYPE="+batch_oper_type;
		$.beginPageLoading("数据查询中...");
		$.ajax.submit(null,'',param,'SelectListC,SelectListD',function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
	},
	
	clearValue2: function (){
		var batch_oper_type =  $('#cond_BATCH_OPER_TYPE').val();
		var param = "&BATCH_OPER_TYPE="+batch_oper_type;
		$.beginPageLoading("数据查询中...");
		$.ajax.submit(null,'',param,'SelectListD',function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
	},
	
	queryOperInfo: function (spCode,bizCode,bizTypeCode){
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
		var start = new Date().getTime();
		while(true)
			{if(new Date().getTime() - start > 1000)
				{break;}}
		$.beginPageLoading("数据查询中..");
		$.ajax.submit(null, '', params, 'SelectListD', function(data){
			//alert("data======="+data);
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	},
	
	querySpInfo: function (){
		var bizTypeCode = $("#PARAM_BIZ_TYPE_CODE").val();
		$.beginPageLoading("数据查询中..");
		$.ajax.submit('QueryCondPart', 'queryPlatInfo', '', 'QueryListPart', function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	},
	
	tableRowDBClick: function (){
		var table = $.table.get("QueryListTable");
		var json = table.getRowData();
		var spCode = json.get('SP_CODE','');
		var spName = json.get('SP_NAME','');
		setPopupReturnValue(spCode,spName);
	}

	});
	}
)();