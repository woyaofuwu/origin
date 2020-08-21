function queryData(){
	//取消删除全部标志
    $("#del_all_flag").val("");
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('queryPart', 'queryBadWebInfo', '', 'refresh_table,editPart', function(){
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
	$('#WEB_ADDR').val('');
	$('#WEB_NAME').val('');
	$('#WEB_SOURCE').val('');
	$('#REMARKS').val('');
	
	//取消删除全部标志
    $("#del_all_flag").val("");
    
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
		}
	   //给网站来源 赋值(编码)
		$('#WEB_SOURCE').val(rowData.map['WEB_SOURCE_CODE']);
		//取消删除全部标志
	    $("#del_all_flag").val("");
	}
	
	/** 新增一条记录，对应表格新增按钮 */
	function addLimit() {
		
		var date = new Date();
		/* 校验所有的输入框 */
		if (!checkEditPart()) return false;		
		//获取编辑区的数据
		var editData = $.ajax.buildJsonData("editPart");
		editData['STAFF_ID']=$("#NGBOSS_STAFF_ID").val();
		editData['STAFF_NAME']=$("#NGBOSS_STAFF_NAME").val();
		editData['UPDATE_TIME']=date.toLocaleDateString();
		editData['MODIFY_TYPE']="新增";
		editData['WEB_SOURCE']=$("#WEB_SOURCE")[0].options[$("#WEB_SOURCE")[0].selectedIndex].text;
		editData['WEB_SOURCE_CODE']=$("#WEB_SOURCE").val();
		//取消删除全部标志
	    $("#del_all_flag").val("");
		/* 新增表格行 */
		$.table.get("paramTable").addRow(editData);
	}
	
	/** 修改一条记录，对应表格修改按钮 */
	function editLimit() {
		
	 	/* 校验所有的输入框 */
		if (!checkEditPart()) 
			return false;
		var editData = $.ajax.buildJsonData("editPart");
		
		editData['WEB_SOURCE']=$("#WEB_SOURCE")[0].options[$("#WEB_SOURCE")[0].selectedIndex].text;
		editData['WEB_SOURCE_CODE']=$("#WEB_SOURCE").val();
		
		//取消删除全部标志
	    $("#del_all_flag").val("");
		/* 修改表格行 */
		$.table.get("paramTable").updateRow(editData);
	}
	
	/** 删除一条记录，对应表格删除按钮 */
	function delLimit() {
		var  modify_type=$("#MODIFY_TAG").val();
		if(modify_type == 1){
			alert("已经删除,不能再删除");
			return false;
		}
		//取消删除全部标志
	    $("#del_all_flag").val("");
		$.table.get("paramTable").deleteRow();
	}
	
	/**
	 * 
	 * @returns {Boolean}
	 */
	function delAllLimit() {
		
		var del_data = $.table.get("paramTable").getRowData('ID',1);
		if(del_data == 'null' || del_data == null){
			alert('没有数据删除！');
			return false;
		}
		
		var  cond_MODIFY_TAG=$("#cond_MODIFY_TAG").val();
		//alert("cond_MODIFY_TAG:"+cond_MODIFY_TAG);
		if(cond_MODIFY_TAG != 0||cond_MODIFY_TAG == ''){
			alert("查询操作类型必须:新增");
			return false;
		}
		
		//添加删除全部标志
	    $("#del_all_flag").val("del_all_flag");
	    
	    //删除所有
		$.table.get("paramTable").cleanRows();
	}	

	//检查输入返回false表示不通过。
function checkEditPart(){
	var flag = true;
	 return flag;
}
	/** submit depts */
	function submitDepts(obj) {
		//删除所有
		var  del_all_flag=$("#del_all_flag").val();
		var areaids="";
		/***
		 * 当时删除所有时不需要给edit_table赋值
		 */
		if(del_all_flag == ''){
			var submit_data = $.table.get("paramTable").getTableData();
			if(submit_data.length<1){
				alert('没有修改任何数据！');
				return false;
			}else{
				$("#edit_table").val(submit_data.toString());
			}
			areaids="submit_part,queryPart,QueryCondPart";
		}else{
			//提交查询条件即可
			areaids="queryPart";
		}

		/* 将指定列拼成串，后台将串解析生成数据  确定提交吗？ */	
//		alert($.table.get("paramTable").getTableData().toString());
		/** 提交操作，用AJAX局部刷新提交 */
		$.beginPageLoading("正在提交数据...");
		$.ajax.submit(areaids, 'BadWebInfoSubmit', '', 'refresh_table', function(data){
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

	/**
	 * 导入
	 * @returns {Boolean}
	 */
	function importOcsData(){

		if($("#cond_STICK_LIST").val()==""){
			alert('上传文件不能为空！');
			return false;
		}
		
		$.beginPageLoading("努力导入中...");
		$.ajax.submit('SubmitImportBadwebinfo','importBadwebinfo','','',function(data){
			alert('导入成功！');
			resetPage();
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
		$.beginPageLoading("努力刷新中...");
		$.ajax.submit('','','','SubmitImportBadwebinfo',function(data){
			$.endPageLoading();
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
		});
	}