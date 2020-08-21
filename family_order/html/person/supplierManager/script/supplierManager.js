
function reset(){
	$('#submit_part :input').val('');
}

function querySupplier()
{
	$.beginPageLoading("数据刷新中...");
	
	$.ajax.submit('QueryCondPart', 'querySupplier', null, 'result_Table', function(data)
    {
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

function addSupplierPage(){
	  openNav('手机卖场及供应商新增','supplierManager.Add', '');
}

function  modifySupplierPage(){
	var table = $.table.get("supplierTable");
	if(table.getSelected('codeId')==null){
		MessageBox.alert('请选择数据','请选择数据后再做修改操作');
		return;
	}
	var codeId = table.getSelected('codeId').html();	
//	debugger;
	openNav('手机卖场及供应商修改','supplierManager.Edit','initEditPage','SUPPLIER_ID='+codeId);
}

/**
 * 删除供应商
 */
function  deleteSupplier(){
	var table = $.table.get("supplierTable");
	if(table.getSelected('codeId')==null){
		MessageBox.alert('请选择数据','请选择数据后再做删除操作');
		return;
	}
	
	var codeId = table.getSelected('codeId').html();
//	debugger;
	$.beginPageLoading("正在删除...");	
	$.ajax.submit('', "deleteSupplier", 'SUPPLIER_ID='+codeId, 'result_Table', function(data){
			$.endPageLoading();			
			querySupplier();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
}

function addSupplier()
{
	//查询条件校验
	if(!$.validate.verifyAll("addPart"))
	{
		return false;
	}
	
	$.beginPageLoading("数据新增中...");
	
	$.ajax.submit('addPart', 'addSupplier', null, 'addPart', function(data)
    {
		$.endPageLoading();
		var resultcode = data.get(0).get("X_RESULTCODE");
		if(resultcode == '0'){
			alert("新增成功！");			
		}else{
			alert("新增失败");
		}
		
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}


function editSupplier()
{
	//查询条件校验
	if(!$.validate.verifyAll("editPart"))
	{
		return false;
	}
	 
	$.beginPageLoading("数据修改中...");
	
	$.ajax.submit('editPart', 'editSupplier', null, 'editPart', function(data)
    {
		$.endPageLoading();
		var resultcode = data.get(0).get("X_RESULTCODE");
		if(resultcode == '0'){
			alert("修改成功！");		
			closeNav();
		}else{
			alert("修改失败");
		}		
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		alert(error_info);
    });
}

