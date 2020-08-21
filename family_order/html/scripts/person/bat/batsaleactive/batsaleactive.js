/**
 * 初始化页面参数
 * */
function initBatchId(){
	$.beginPageLoading("正在进行中...");
	$.ajax.submit("BatSaleActive","getBatchId",'','NewBatchId',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

/**
 * 初始化页面参数
 * */
function resetPage(){
	$("#importKey").attr("disabled",false);
	$.beginPageLoading("刷新中...");
	$.ajax.submit('BatSaleActive','init','','SubmitCondPart',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function selectPackage(){
	//$("#cond_SALE_GOODS").empty();
	//$("#cond_SALE_GOODS").append("<option value=''>--请选择--</option>");
	var product_id=$("#cond_SALE_PRODUCT").val();
	if(product_id==null||product_id==""){
		return;
	}
	var params="&SELECTTYPE=PAKG&PRODUCT_ID="+product_id;
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('','getPackages',params,'PackagePart',
			function(data){  
			  	$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});
}


function importBatData(){
	
	if($("#cond_SALE_PRODUCT").val()==""){
	    alert("请先选择营销活动");
	    return false;
	}
	if($("#cond_SALE_PACKAGE").val()==""){
		alert('请先选择营销包');
		return false;
	}
	if($("#BATCH_ID").val()==""){
	    alert("请先获取批次号");
	    return false;
	}
	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	$.beginPageLoading("导入中...");
	$.ajax.submit('SubmitCondPart','importBatData','','',function(data){
		alert('导入成功！');
		//resetPage();
		$("#importKey").attr("disabled",true);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function checkAndSubmit(){
	
	if($("#cond_SALE_PRODUCT").val()==""){
	    alert("请先选择营销活动");
	    return false;
	}
	if($("#cond_SALE_PACKAGE").val()==""){
		alert('请先选择营销包');
		return false;
	}
	if($("#BATCH_ID").val()==""){
	    alert("请先获取批次号");
	    return false;
	}
	if($("#cond_STICK_LIST").val()==""){
		alert('上传文件不能为空！');
		return false;
	}
	$.beginPageLoading("数据已提交！活动规则校验中...请耐心等待");
	$.ajax.submit('SubmitCondPart','dealSubmit','','',function(data){
		alert('提交成功！');
		var batch_id = $("#BATCH_ID").val();
		$("#cond_BATCH_ID").val(batch_id);
		queryImportData()
		resetPage();

		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function queryImportData()
{
	 if(!$.validate.verifyAll("QueryPart")){
		 return false;
	 }
	 
	 $.beginPageLoading("数据查询中...");
	 $.ajax.submit('QueryPart', 'queryImportData', null, 'ImportDataPart',function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}