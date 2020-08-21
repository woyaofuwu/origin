function queryData(){
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('queryPart', 'queryCustPsptLimitInfo', '', 'refresh_table,editPart', function(){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}	

function reset(){
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
			if(colId!='LIMIT_COUNT' && colId!='REMARK'){
				jqObj.attr('disabled',true);
			}
		}
	}
	
	/** 新增一条记录，对应表格新增按钮 */
	function addLimit() {
		//限制个人用户证件办理新增
		var papt_type_code = $("#PSPT_TYPE_CODE").val();

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
		//限制个人用户证件办理修改
		var papt_type_code = $("#PSPT_TYPE_CODE").val();
		//REQ201701160002关于集团客户界面优化需求 增加类型 3

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
	var psptTyeCode=$("#PSPT_TYPE_CODE").val();
	// 获取用户阈值调整权限
	var privateFlag = $("#privateFlag").val();
	if (privateFlag && privateFlag =="true"){
        if ($("#LIMIT_COUNT").val() > 20){
            alert("入网最大阀值最大可调至20户！");
            return flag;
		}else if($("#LIMIT_COUNT").val() < 5){
            alert("入网阀值不能小于5个！");
            return flag;
		}else {
            flag = true;
		}
	}else {// 没有调整权限
        alert("您目前的没有用户阈值调整权限！");
        return flag;
	}

	 var papt_type_code = $("#PSPT_TYPE_CODE").val();
	 if(0==papt_type_code || 1==papt_type_code || 3==papt_type_code){
		 
		 $("#PSPT_ID").attr("datatype","pspt");
		 
	 }else{
		 $("#PSPT_ID").attr("datatype","");
	 }
	 
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
		},
		function(error_code,error_info){
			$.endPageLoading();
			alert(error_info);
	    });
	}
	

   function  checkPsptId(){
	  
	    var psptId=$("#PSPT_ID").val();
	    var psptTypeCode=$("#PSPT_TYPE_CODE").val();
	    
	    if(psptTypeCode=="E"){			 
			if(psptId.length != 13 && psptId.length != 15 && psptId.length != 18 && psptId.length != 20 && psptId.length != 22 && psptId.length != 24){
				alert("营业执照类型校验：长度需满足13位、15位、18位、20位、22位或24位！当前："+psptId.length+"位。");
				$("#PSPT_ID").val("");
				$("#PSPT_ID").focus();				
				return false;
		    }
	    }else if(psptTypeCode=="M"){
				//组织机构代码校验
				if(psptId.length != 10 && psptId.length != 18){
					alert("组织机构代码证类型校验：长度需满足10位或18位。");
					$("#PSPT_ID").val("");
					$("#PSPT_ID").focus();					
					return false;
				}
				if(psptId.length == 10 &&psptId.charAt(8) != "-"){
					alert("组织机构代码证类型校验：规则为\"XXXXXXXX-X\"，倒数第2位是\"-\"。");
					$("#PSPT_ID").val("");
					$("#PSPT_ID").focus();					
					return false;
				}
		}else if(psptTypeCode=="G"){
			//事业单位法人登记证书：证件号码长度需满足12位
			if(psptId.length != 12 && psptId.length != 18){
				alert("事业单位法人登记证书类型校验：长度需满足12位或者18位！");
				$("#PSPT_ID").val("");
				$("#PSPT_ID").focus();				
				return false;
			}
		}			
  }
   
   
   
   