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
			if(colId!='LIMIT_COUNT' && colId!='REMARK'){
				jqObj.attr('disabled',true);
			}
			//证件类型
			if(colId == 'PSPT_TYPE_CODE'){
				changeSelectByPsptTypeCode(rowData.map[colId]);
			}
		}
	}
	
	/** 新增一条记录，对应表格新增按钮 */
	function addLimit() {
		//限制个人用户证件办理新增
		var papt_type_code = $("#PSPT_TYPE_CODE").val();
		//REQ201701160002关于集团客户界面优化需求 增加类型 3
		if("0" == papt_type_code || "1" == papt_type_code || "2" == papt_type_code || "3" == papt_type_code || "J" == papt_type_code || "I" == papt_type_code || "A" == papt_type_code || "C" == papt_type_code || "H" == papt_type_code)
		{
			alert("个人证件类型不能办理新增实名制阀值记录业务！");
			return false;
		}
		/* 校验所有的输入框 */
		if (!checkEditPart()) return false;		
		/* 验证集团信息**/
		if(!checkGroupInfo()) return false;
		
		//获取编辑区的数据
		var editData = $.ajax.buildJsonData("editPart");
		var psptTypeName = $("#PSPT_TYPE_CODE")[0].options[$("#PSPT_TYPE_CODE")[0].selectedIndex].text;
		editData['PSPT_TYPE_NAME']=psptTypeName;
		
		var adjustType = $("#ADJUST_TYPE_CODE")[0].options[$("#ADJUST_TYPE_CODE")[0].selectedIndex].text;
		editData['ADJUST_TYPE_NAME']=adjustType;
		
		/* 新增表格行 */
		$.table.get("paramTable").addRow(editData);
	}
	
	/** 修改一条记录，对应表格修改按钮 */
	function editLimit() {
		//限制个人用户证件办理修改
		var papt_type_code = $("#PSPT_TYPE_CODE").val();
		//REQ201701160002关于集团客户界面优化需求 增加类型 3
		if("0" == papt_type_code || "1" == papt_type_code || "2" == papt_type_code || "3" == papt_type_code || "J" == papt_type_code || "I" == papt_type_code || "A" == papt_type_code || "C" == papt_type_code || "H" == papt_type_code)
		{
			alert("个人证件类型不能办理修改实名制阀值记录业务！");
			return false;
		}
	 	/* 校验所有的输入框 */
		if (!checkEditPart()) 
			return false;
		/* 验证集团信息**/
		if(!checkGroupInfo()) return false;
		var editData = $.ajax.buildJsonData("editPart");
		var psptTypeName = $("#PSPT_TYPE_CODE")[0].options[$("#PSPT_TYPE_CODE")[0].selectedIndex].text;
		editData['PSPT_TYPE_NAME']=psptTypeName;
		
		var psptTypeName = $("#ADJUST_TYPE_CODE")[0].options[$("#ADJUST_TYPE_CODE")[0].selectedIndex].text;
		editData['ADJUST_TYPE_NAME']=psptTypeName;
		
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
	 if(("D" == psptTyeCode||"E" == psptTyeCode
	    	||"G" == psptTyeCode||"L" == psptTyeCode||"M" == psptTyeCode) && ($("#privateFlag").val() != "true" || $("#privateFlag_50").val() != "true" || $("#privateFlag_100").val() != "true")){
		 if($("#privateFlag").val() != "true"){
			 if($("#privateFlag_100").val() != "true"){
				 if($("#privateFlag_50").val() != "true"){
					 if("0" == $("#ADJUST_TYPE_CODE").val() && $("#LIMIT_COUNT").val() > 20){
						 alert("您目前的操作权限不能将入网阀值调整至超过【20】！");
			             return flag;
					 }else if("1" == $("#ADJUST_TYPE_CODE").val() && $("#LIMIT_COUNT").val() > 200){
						 alert("您目前的操作权限不能将入网阀值调整至超过【200】！");
			             return flag; 
					 }else{
						 flag = true;
					 }
				 }else{
					 if("0" == $("#ADJUST_TYPE_CODE").val() && $("#LIMIT_COUNT").val() > 50){
						 alert("您目前的操作权限不能将入网阀值调整至超过【50】！");
			             return flag;
					 }else if("1" == $("#ADJUST_TYPE_CODE").val() && $("#LIMIT_COUNT").val() > 200){
						 alert("您目前的操作权限不能将入网阀值调整至超过【200】！");
			             return flag; 
					 }else{
						 flag = true;
					 }
				 }
			 }else{
				 if("0" == $("#ADJUST_TYPE_CODE").val() && $("#LIMIT_COUNT").val() > 100){
					 alert("您目前的操作权限不能将入网阀值调整至超过【100】！");
		             return flag;
				 }else if("1" == $("#ADJUST_TYPE_CODE").val() && $("#LIMIT_COUNT").val() > 200){
					 alert("您目前的操作权限不能将入网阀值调整至超过【200】！");
		             return flag; 
				 }else{
					 flag = true;
				 }
			 }
		 }else{
			 flag = true;
		 }
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
	
   /**
    * REQ201610120016 优化实名制开户阙值调整界面优化需求
    * @author zhuoyingzhi
    * 20161021
    * @returns {Boolean}
    */
	function queryCustInfos() {

		var grpid = $('#cond_RSRV_STR4').val();
		if(grpid==""){
			alert('集团客户编码不能为空！');
			return false;
		}
		$.beginPageLoading();
		$.ajax.submit('QueryGrpCondPart', 'queryGroupCusts', '', 'UCAViewPart', function(data){
			$.endPageLoading();
			if(data.get("resultNum") == 0){
				MessageBox.alert("提示","查询无集团客户资料.");
			}
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	 });
	}

  /**
   * 根据证件类型判断是否显示集团成员信息和是否需要清空集团成员信息
   * @param flag
   */
   function  changeSelectByPsptTypeCode(flag){
	   var  psptTyeCode='';
	    if(flag != ''){
	    	//从列表中传过来
	    	psptTyeCode=flag;
	    }else{
		     //获取证件类型
		    psptTyeCode=$("#PSPT_TYPE_CODE").val();
		    
	    	//清空集团成员信息
	    	$('#cond_RSRV_STR4').val('');
	    	$('#RSRV_STR4').val('');
	    	$('#RSRV_STR5').val('');
	    	$('#RSRV_STR6').val('');
	    	$('#RSRV_STR7').val('');
	    }
	   
	    /**
	     *  1、营业执照、
			2、组织机构代码证
			3、事业单位法人证书
			4、社会团体法人登记证书
			5、单位证明
	     */
	    if(psptTyeCode == "D"||psptTyeCode == "E"
	    	||psptTyeCode == "G"||psptTyeCode == "L"||psptTyeCode == "M"){
	    	//显示 
	    	$('#group_info_id').css('display','block');
	    }else{
	    	//隐藏
	    	$('#group_info_id').css('display','none');
	    }
   }
   /**
    * 验证集团信息
    */
   function  checkGroupInfo(){
	    var RSRV_STR4=$("#RSRV_STR4").val();
	    var REMARK=$("#REMARK").val();
	    if(RSRV_STR4 == ''){
	    	alert("集团客户编码不能为空");
	    	return  false;
	    }else if(REMARK == ''){
	    	alert("备注不能为空");
	    	return  false;
	    }else{
	    	return true;
	    }
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

   
   /**
    * 判断是否满足导出条件 
    * @returns {Boolean}
    */
   function  checkPsptTypeToExport(){
	   //判断是否有权限导出
	   var exportFlag=$("#exportFlag").val();
	   if(exportFlag == "false"){
		   alert("您没有权限导出");
		   return false;
	   }
	   
	   var cond_PSPT_TAG=$("#cond_PSPT_TAG").val();
	   if(cond_PSPT_TAG == "1"){
		   return true;
	   }else{
		  alert("只有选择单位证件类型时，才能导出");
		  return false;
	   }
   }
  
   
   
   
   