
function importGrpChangeAction() {
	var import_mode = $('#GRP_BAT').val();
	var import_file = $('#IMPORT_FILE').val();
    if(import_mode == '0') {                                  //通过文件导入
		$('#cancel_text').css("display","none");
		$('#cancel_file').css("display","");
		$('#cancel_templet').css("display","");
		$("#import_file").css('nullable','no');
		$("#submitPart").css('display','none');
	} else if(import_mode == '1'){                             //通过后台自动导入
		$('#cancel_text').css("display","");
		$('#cancel_file').css("display","none");
		$('#cancel_templet').css("display","none");
		$("#import_file").css('nullable','yes');
		$("#submitPart").css('display','');
	}
}
function initGrpBatEsopTradeDeal(obj){
  	var operType= $('#BATCH_OPER_CODE').val();
		var coding_str = $('#CODING_STR');
		var pop_coding_str = $('#POP_CODING_STR');
		var operTypeName = $('#BATCH_OPER_NAME');

		coding_str.val("");
		pop_coding_str.val("");

		if (obj == "BATGRPOPENMEB"){
			$("#bsubmit").attr("disabled",true);
			$('#cancel_file').css("display","none");
	  	} else{
	  		$('#cancel_file').css('display','');
	  		$('#cancel_templet').css('display','');
	  	}
	  
	  if (obj == "BATADDSUPTELMEM"){
	  	  var info =$.DataMap();
	  	  info.put("BATCH_OPER_TYPE",obj);
	  	  $("#CODING_STR").val(info.toString());
	  	  
	  	}else if (obj == "BATDELUNITEUSER"){
	  	  $("#POP_CODING_STR").val(operTypeName.val());
	  	  $("#CODING_STR").val(operType.val());
	  	  
	  	}else if (obj == "BATADDUNITEUSER"){
	  	  $("#POP_CODING_STR").val(operTypeName.val());
	  	  $("#CODING_STR").val(operType.val());
	  	  
	  	
	  	}else{
	  	
	  	   $("#CODING_STR").val("");
	  	}	  		  	

}

function selectBatchOperType() {
	var operType= $('#BATCH_OPER_CODE').val();
	var operName = $("#BATCH_OPER_CODE :selected").text();
	$('#BATCH_OPER_NAME').val(operName);
	$.beginPageLoading('系统加载中......');
	ajaxSubmit(this,'queryComps','&BATCH_OPER_TYPE='+ operType,'BatCondPart,MqParamPart',function(){
		$.endPageLoading();
		operTypeAfter(operType)
	},function() {});
}

function operTypeAfter(obj){
  		var operType= $('#BATCH_OPER_CODE').val();
  		var operTypeName = $('#BATCH_OPER_NAME').val();
		
		$('#CODING_STR').val("");
		$('#POP_CODING_STR').val("");

		if (obj == "BATGRPOPENMEB"){
			$("#bsubmit").attr("disabled","true")
			$('#cancel_file').css("display","none");
	  	} else{
	  		$('#cancel_file').css("display","");
	  		$('#cancel_templet').css("display","");
	  	}
	  	 
	   var info =$.DataMap();
	   info.put("BATCH_OPER_TYPE",obj);
	  
	   if (obj == "BATADDSUPTELMEM"){
	  	  $("#CODING_STR").val(info.toString());
	  	  
	  	}else if (obj == "BATDELUNITEUSER"){
	  	  $("#POP_CODING_STR").val(operTypeName);
	  	  $("#CODING_STR").val(info.toString());
	  	  
	  	}else if (obj == "BATADDUNITEUSER"){
	  	  $("#POP_CODING_STR").val(operTypeName);
	  	  $("#CODING_STR").val(info.toString());
	  	}else if (obj == "BATDESTROYGRPUSER") {
	  	  $("#POP_CODING_STR").val(operTypeName);
	  	   $("#CODING_STR").val(info.toString());
	  	}else{
	  	   $("#CODING_STR").val("");
	  	}	  		  	
	  	if(obj == "GRPDESTROYONEKEY") {
	  		$("#BatImportPart").css('display','none');
	  		$("#submitPart").css('display','');
	  	} else {
	  		$("#submitPart").css('display','none');
	  	}
	  	
}

function importSubmit(){
	var CODING_STR = $('#CODING_STR').val();
    if (CODING_STR == ""){
      $.showWarnMessage('请先选择批量条件','',null);
      return false;
    }
    var params = '';
    if($.validate.verifyAll("popup")) {
    	$.beginPageLoading();
    	ajaxSubmit('PopupPart','createBatDealInfo',params,'PopupPart',function (data) {
    		  $.endPageLoading();
    		var hint_message = data.get('hint_message');
      		if(!$.isEmptyObject(hint_message)) {
      			 $.showSucMessage("批量业务创建成功",hint_message);
      		}
    	},function () {})
    } 
}

function chooseCompSTR() {
	var taskName = $('#BATCH_TASK_NAME').val();
	var CODING_STR = $('#CODING_STR').val();
	if(taskName == "") {
	  $.showWarnMessage('请先填写批量任务名称','',null);
	  $('#BATCH_TASK_NAME').focus();
      return false;
	}
    if (CODING_STR == ""){
       $.showWarnMessage('请先选择批量条件','',null);
      return false;
    }
    return true;
}

function finishImportData () {
	var batchTaskId = $('#BATCH_TASK_ID').val();
	$.showSucMessage("批量业务创建成功",'批量业务流水号：' + batchTaskId);
}

function importGrpOpenAction(obj) {
    var conStr = $('#CODING_STR');
    if (conStr == ""){
      showMessageBox("请先输入批量条件","");
      return false;
    }
    ajaxSubmit(this, 'initOpenGrpInput', null, 'OpenArea',null,false,function(){afterFunc(obj);});
}

function afterFunc(obj){
  	var flag = this.ajaxDataset.get(0,"FLAG");
	 if( flag == "false"){
	 	$('#open_text').css('display','none');
	 	$('#cancel_file').css('display','none');
	 	return false;
	 }
	 $("#bsubmit").attr("disabled",false);
 	//var import_mode = getElementValue('OPEN_BAT');
 	var import_mode = obj.value;
	var import_file = $('#IMPORT_FILE');
    if(import_mode == '0') {                                  //通过文件导入
		$('#open_text').style.display="none";
		$('#cancel_file').css('display','');
		$('#cond_SIM_NUM').nullable="yes";
		$('#cond_START_PHONE').nullable="yes";
		$('#cond_SIM_CODE').nullable="yes";
		import_file.nullable="no";
	} else if(import_mode == '1'){                            //号段导入
		$('#open_text').css('display','');
		$('#cancel_file').style.display="none";
		$('#cond_SIM_NUM').nullable="no";
		import_file.nullable="yes";
	}

}

function batComp(){
  var popustr = $('#CODING_BUTTON').attr('onclickstr');
  if(popustr != ''){
  	 var popupEsopPage = popustr.replace('popupPage','popupESOPPage');
  	 eval(popupEsopPage);
  }

}
function popupESOPPage(page, listener, params, title, width, height){
  var param = '&ESOP_TAG=ESOP'+'&cond_GROUP_ID='+$('#GROUP_ID').val()+'&USER_ID='+$('#USER_ID').val()+'&PRODUCT_ID='+$('#PRODUCT_ID').val()+'&CUST_ID='+$('#CUST_ID').val();
  if(params ==null){
     params='';
  }
  params= params +param;
  popupPage(page, listener, params, title, width, height);

}

/**错误提示*/
function showMessageBox(mess, mb){
	MessageBox.show({
	           title:'系统提示：',
	           msg: mess,
	           buttons: MessageBox.OK,
	           fn: showResult,
	           animEl: mb
	       });
}
function showResult(btn){
      // Wade.dataimport.msg('按钮点击', '你点击了 {0} 按钮', btn);
    };