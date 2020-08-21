window.onload = function () {
	$("#cond_GROUP_ID_NAME").focus();
	$('#delButton').css('display','none');
	$("#ShowPopupPart").css('display','none');

}

// 新增批量
function addRedDeal() {
 	
	displayLayer();   
}

// 显示Layer
function displayLayer() {
	
	//$.ajax.submit(this, "initBatCreatePoup", null, "RedCreatePart", null, null);
	
	$("#ShowPopupPart").css("display", "");
}

// 隐藏Layer
function hiddenLayer() {



	MessageBox.confirm("提示信息","确定要取消编辑红名单信息吗?",function(btn){
		if(btn=='ok'){
			$("#ShowPopupPart").css("display", "none");
			$("#GROUP_ID").val('');
			$("#GROUP_NAME").val('');
			$("#OFFER_NAME").val('');
			$("#REMARK").val('');
			$("#OFFER_CODE").val('');
		}
	});




}

//导入方式选择处理
function importGrpChangeAction() {
	var import_mode = $('#GRP_BAT').val();
    if(import_mode == '0') {                                  //通过文件导入
		$('#cancel_text').css("display","none");
		$('#cancel_file').css("display","");
		$('#cancel_templet').css("display","");
		$("#submitPart").css('display','none');
	} else if(import_mode == '1'){                            //通过后台自动导入
		$('#cancel_text').css("display","");
		$('#cancel_file').css("display","none");
		$('#cancel_templet').css("display","none");
		$("#submitPart").css('display','');
	}
}
//批量弹出条件框初始化
function selectBatchOperType() {
	
	//重置金库开关
	has4AOK = false;
	need4A = false;
	
	var operType= $('#BATCH_OPER_CODE').val();
	var operName = $("#BATCH_OPER_CODE :selected").text();
	
	$('#BATCH_OPER_NAME').val(operName);
	
	if(operType == 'VPMNCHANGEDSHORTCODE' || operType == 'SPECVPMNCHGSHORTCODE' || operType == 'BATADDVPMNMEM')
	{
		$("#SMSGO").css('display','');
		$("#SMS_FLAG").attr("disabled",false);
	}
	else
	{
		$("#SMSGO").css('display','none');
		$("#SMS_FLAG").attr("disabled",true);
		$("#SMS_FLAG").val("0");
	}
	 
	$.beginPageLoading('系统加载中......');
	
	$.ajax.submit(this, 'queryComps', '&BATCH_OPER_TYPE=' + operType, 'BatCondPart,MqParamPart', 
		function(){
			$.endPageLoading();
			operTypeAfter(operType);
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}


function importSubmit2(){
	var GROUP_ID = $('#GROUP_ID').val();
    if (GROUP_ID == ""){
		MessageBox.alert("提示信息","集团编码不能为空");
      return false;
    }
	var GROUP_NAME = $('#GROUP_NAME').val();
	if (GROUP_NAME == ""){
		MessageBox.alert("提示信息","集团名称不能为空");

		return false;
	}
	var OFFER_CODE = $('#OFFER_CODE').val();
	if (OFFER_CODE == ""||OFFER_CODE.length > 16){
		MessageBox.alert("提示信息","商品不能为空且长度不能大于16");
		return false;
	}
	var OFFER_NAME = $('#OFFER_NAME').val();
	if (OFFER_NAME == ""||OFFER_NAME.length > 200){
		MessageBox.alert("提示信息","商品名称不能为空且长度不能大于200");
		return false;
	}


    var params = '';
    if($.validate.verifyAll("popup")) {
    	$.beginPageLoading("正在查询...");
    	$.ajax.submit('ShowPopupPart','createAddRedInfo','','',function (data) {//ShowPopupPart
    		$.endPageLoading();
    		var error_message = data.get('error_message');
    		if(!$.isEmptyObject(error_message))
    		{
				MessageBox.alert("提示信息",error_message);

    		}else{
				MessageBox.success("提示信息","信息添加成功！");
				$("#ShowPopupPart").css('display','none');
				$("#GROUP_ID").val('');
				$("#GROUP_NAME").val('');
				$("#OFFER_NAME").val('');
				$("#REMARK").val('');
				$("#OFFER_CODE").val('');
			}


      		
    	},
    	function(error_code,error_info,derror){
    		$.endPageLoading();
    		showDetailErrorInfo(error_code,error_info,derror);
        })
    } 
}

//查询两级业务欠费红名单信息
function getListValue(){
	  var value=$('#cond_GROUP_ID_NAME').val();
	  if(value==""){
	  	alert("请输入集团编码或集团名称");
	  	return false;
	  }
	  if('0'==value) {//0:未启动
	  	$('#startButton').css('display','');
	  	$('#delButton').css('display','');
	  }else {//1:启动 删除
	  	$('#addButton').css('display','');
	  	$('#delButton').css('display','');
	  }
	  

	  //$('#operType').val(value);,hintBar
	  $.beginPageLoading();
	  $.ajax.submit('QueryFormInfo','queryTwoBusinessRedInfo','','RedListPart',
		function (data){ 
			$.endPageLoading();
		},function(error_code,error_info,derror){
			  $.endPageLoading();
			  showDetailErrorInfo(error_code,error_info,derror);
		  });
	  return true;
}




//查询两级业务欠费红名单信息
function getOfferName(){
	var value=$('#OFFER_CODE').val();
	if(value==""){
		alert("请选择商品");
		return false;
	}

	$.beginPageLoading();
	$.ajax.submit('ShowPopupPart','queryOfferName','','insertOfferName',
		function (data){
			$.endPageLoading();

			var error_message = data.get('error_message');
			if(!$.isEmptyObject(error_message))
			{
				MessageBox.alert("提示信息",error_message);

			}
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
	return true;
}

//查询两级业务欠费红名单信息
function getGroupName(){
	var value=$('#GROUP_ID').val();
	if(value==""){
		alert("请输入集团编码");
		return false;
	}

	$.beginPageLoading();
	$.ajax.submit('ShowPopupPart','queryGroupName','','insertGroupName',
		function (data){
			$.endPageLoading();
			var error_message = data.get('error_message');
			if(!$.isEmptyObject(error_message))
			{
				MessageBox.alert("提示信息",error_message);

			}
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
	return true;
}


//删除批量

function deleteRedDeal(){
	var redid = getCheckedValues("trades");
   if(null == redid || "" == redid) {
   		alert("请选择需要删除的红名单！");
   }else {
   		MessageBox.confirm("提示信息","确定删除红名单信息吗?",function(btn){
		if(btn=='ok'){
		   $.ajax.submit('QueryForm','delRedTrades','&RED_IDS='+redid,'RedListPart',function(data){
				$.endPageLoading();
			   MessageBox.alert("提示信息","红名单数据删除成功!");
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}
	});
   }
}


 


 
