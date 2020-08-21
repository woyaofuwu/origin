function queryData(){
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('queryPart', 'queryCustPsptLimitInfo', null, 'refresh_table', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}	

function reset(){
//	$.table.get("paramTable").cleanRow();
	var selected = $.table.get("paramTable").getSelected();
	if(selected!=null){
		$.table.get("paramTable").getTable().attr("selected",'-1');
		selected.removeAttr("class");
	}
	$('#editPart :input[disabled]').val('');
	$('#LIMIT_COUNT').val('');
	$('#editPart :input[disabled]').removeAttr('disabled');
}
//点击表格行，初始化编辑区
	function tableRowClick(){
		var rowData = $.table.get("paramTable").getRowData();
		for(var i = 0;i< rowData.keys.length;i++){
			var colId = rowData.keys[i];
			var jqObj = $('#'+colId);
			if(jqObj!=null){
				jqObj.val(rowData.map[colId]);
			}
			//除入网阙值外全部disable
			if(colId!='LIMIT_COUNT'){
				jqObj.attr('disabled',true);
			}
		}
	}
	
	/** 新增一条记录，对应表格新增按钮 */
	function addLimit() {
		/* 校验所有的输入框 */
		if (!checkEditPart()) return false;		
		//获取编辑区的数据
		var editData = $.ajax.buildJsonData("editPart");
		var psptTypeName = $("#PSPT_TYPE_CODE")[0].options[$("#PSPT_TYPE_CODE")[0].selectedIndex].text;
		editData['PSPT_TYPE_NAME']=psptTypeName;
		/* 新增表格行 */
		$.table.get("paramTable").addRow(editData);
	}
	
	/** 修改一条记录，对应表格修改按钮 */
	function editLimit() {
	 	/* 校验所有的输入框 */
		if (!checkEditPart()) 
			return false;
		var editData = $.ajax.buildJsonData("editPart");
		var psptTypeName = $("#PSPT_TYPE_CODE")[0].options[$("#PSPT_TYPE_CODE")[0].selectedIndex].text;
		editData['PSPT_TYPE_NAME']=psptTypeName;
		
		/* 修改表格行 */
		$.table.get("paramTable").updateRow(editData);
	}
	
	/** 删除一条记录，对应表格删除按钮 */
	function delLimit() {
		$.table.get("paramTable").deleteRow();
	}	

	//检查输入返回false表示不通过。
function checkEditPart(){
	var flag = false;
	 if($("#LIMIT_COUNT").val() > 30&&$("#privateFlag").val() != "true"){
                alert("您目前的操作权限不能将入网阀值调整至超过【30】！");
                return flag;
        }else{
        	flag = true;
        }
	 var papt_type_code = $("#PSPT_TYPE_CODE").val();
	 if(0==papt_type_code || 1==papt_type_code){
		 
		 $("#PSPT_ID").attr("datatype","pspt");
		 
	 }else{
		 $("#PSPT_ID").attr("datatype","");
	 }
	 //身份证校验
	 /**
	 if($("#PSPT_TYPE_CODE").val()=='0'||$("#PSPT_TYPE_CODE").val()=='1'){
			 var msg = $.verifylib.checkPspt('PSPT_ID');
			 if(msg!=''){
				 alert(msg);
				return false;
			 }
		}**/
	 
	 flag = flag&&verifyAll();
	 return flag;
}
	/** submit depts */
	function submitDepts(obj) {
		var submit_data = $.table.get("paramTable").getTableData();
		if(submit_data.length<1){
			alert('没有修改任何数据！');
			return false;
		}else{
			$("#edit_table").val(submit_data.toString());
		}
		/* 将指定列拼成串，后台将串解析生成数据  确定提交吗？ */	
//		alert($.table.get("paramTable").getTableData().toString());
		/** 提交操作，用AJAX局部刷新提交 */
		$.beginPageLoading("正在提交数据...");
		$.ajax.submit('submit_part,queryPart', 'custPsptLimitInfoSubmit', '', 'refresh_table', function(data){
			$.endPageLoading();
			var title = "业务受理成功";
			if(data){
				var delCounts = data.get(0).get("DELETE_COOUNTS");
				if(delCounts<=0){
					title = "业务受理失败";
					MessageBox.error(title,title,null,null,null);
				}else{
					MessageBox.success(title,title,queryData,null,null);
				}
			}
			
			//reset();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}
