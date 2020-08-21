	
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
			if(colId!='PARA_CODE23'){
				jqObj.attr('disabled',true);
			}
		}
	}
	
	function reset(){
//		$.table.get("paramTable").cleanRow();
		var selected = $.table.get("paramTable").getSelected();
		if(selected!=null){
			$.table.get("paramTable").getTable().attr("selected",'-1');
			selected.removeAttr("class");
		}
		$('#editPart :input[disabled]').val('');
		$('#PARA_CODE23').val('');
		$('#editPart :input[disabled]').removeAttr('disabled');
	}
	/** 新增一条记录，对应表格新增按钮 */
	function createDept() {
		
		var tableDatas  = getAllTableDataStatus("paramTable",null);
		var _type_code = $("#TYPE_CODE").val();
		var _param_code = $("#PARAM_CODE").val();
		if(tableDatas){
			for(var i=0,len =tableDatas.length;i<len;i++){
				var typeCode = tableDatas.get(i).get("TYPE_CODE");
				var paramCode = tableDatas.get(i).get("PARAM_CODE");
				
				var tag = tableDatas.get(i).get("tag");
				if(_type_code == typeCode && tag!=1){
					alert("类型编码重复！");
					return false;
				}
				
				if(paramCode == _param_code && tag!=1){
					alert("业务编码重复！");
					return false;
				}
			}
			
		}
		
		/* 校验所有的输入框 */
		if (!verifyAll()) return false;		
		//获取编辑区的数据
		var editData = $.ajax.buildJsonData("editPart");
		/* 新增表格行 */
		$.table.get("paramTable").addRow(editData);
	}
	
	/** 修改一条记录，对应表格修改按钮 */
	function updateDept() {
	 	/* 校验所有的输入框 */
		if (!verifyAll()) return false;
		var editData = $.ajax.buildJsonData("editPart");
		/* 修改表格行 */
		$.table.get("paramTable").updateRow(editData);
	}
	
	/** 删除一条记录，对应表格删除按钮 */
	function deleteDept() {
		$.table.get("paramTable").deleteRow();
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
		if (!window.confirm("确认提交吗？")) return false;
		/* 将指定列拼成串，后台将串解析生成数据  确定提交吗？ */	
//		alert($.table.get("paramTable").getTableData().toString());
		/** 提交操作，用AJAX局部刷新提交 */
		$.ajax.submit('submit_part', 'submitDepts', '', 'refresh_table', function(){
//			$(':input').val('');
			reset();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}
