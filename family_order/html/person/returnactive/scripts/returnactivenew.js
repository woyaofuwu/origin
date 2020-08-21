function refreshPartAtferAuth(data)
{	
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	var param = '&SERIAL_NUMBER=' + serialNumber;
	param += '&USER_ID=' + data.get('USER_INFO').get('USER_ID');
	$.ajax.submit(null, 'loadInfo', param, 'doModePart,FlowActivePart,GGCardPart',loadInfoSucc,loadInfoError);
	
}
function loadInfoSucc(ajaxData)
{
//	$('#HAVE_NUM').val(ajaxData.get('HAVE_NUM'));
	$('#ACCEPT_NUM_YC').val('0');
	$('#HIDDEN_HAVE_NUM').val('0');
	$("#ACCEPT_NUM_YC").attr("disabled",null);
}

function loadInfoError(code,info){
	$.cssubmit.disabledSubmitBtn(true);
	$.showErrorMessage("错误",info);
}

/**
 * 选择办理方式
 * @param obj
 * @return
 */
function onChangeMode(obj){
	var doMode = obj.value;

	if(doMode == "1"){//预存
		$('#ZZ_MODE').css("display","none");
		$('#YC_MODE').css("display","");
		$('#YC_BTN').css("display","");
		$('#PAY_MODE').css("display","none");
		$('#SCORE_MODE').css("display","none");
		$('#JOIN_MODE').css("display","none");
//		$('#ROW_COUNT').val('0');	
		$('#ACCEPT_NUM_YC').val('0');
		$('#HIDDEN_HAVE_NUM').val('0');
		$('#FlowActivePart').css("display","none");	
	}else if(doMode == "2"){//缴费
		$('#YC_MODE').css("display","none");
		$('#YC_BTN').css("display","none");
		$('#PAY_MODE').css("display","");
		$('#SCORE_MODE').css("display","none");
		$('#JOIN_MODE').css("display","none");
		$('#HIDDEN_HAVE_NUM').val($('#HAVE_NUM_PAY').val());	
		$('#FlowActivePart').css("display","none");
	}else if(doMode == "3"){//积分
		$('#YC_MODE').css("display","none");
		$('#YC_BTN').css("display","none");
		$('#PAY_MODE').css("display","none");
		$('#SCORE_MODE').css("display","");
		$('#JOIN_MODE').css("display","none");
		$('#HIDDEN_HAVE_NUM').val($('#HAVE_NUM_SCORE').val());	
		$('#FlowActivePart').css("display","none");
	}else if(doMode == "4"){//入网
		$('#YC_MODE').css("display","none");
		$('#YC_BTN').css("display","none");
		$('#PAY_MODE').css("display","none");
		$('#SCORE_MODE').css("display","none");
		$('#JOIN_MODE').css("display","");
		$('#HIDDEN_HAVE_NUM').val($('#HAVE_NUM_JOIN').val());
		$('#FlowActivePart').css("display","none");
	}else if(doMode == "5"){//流量王
		$('#YC_MODE').css("display","none");
		$('#YC_BTN').css("display","none");
		$('#PAY_MODE').css("display","none");
		$('#SCORE_MODE').css("display","none");
		$('#JOIN_MODE').css("display","none");
		$('#HIDDEN_HAVE_NUM').val($('#HAVE_NUM_FLOW').val());
		$('#FlowActivePart').css("display","");
	}
	
	$.feeMgr.removeFee("428","2","1870");
	deleteAllRows();
	
}
/**
 * 清空已领卡列表
 * @return
 */
function deleteAllRows(){

	
	var table = $.table.get("GGCardTable");
//	var length = table.length;
//	table.deleteRow();
	
	table.cleanRows();
}
/**
 * 预存费用处理
 * @param obj
 * @return
 */
function doAdvancePayFee(obj){

	if(!/^(?:[1-9]\d*|0)$/.test(obj.val())){
		alert("预存次数必须是整数!");
		return false;
	}
	var limitMoney = 5000;	//50元一次
	var counts = parseFloat(obj.val(), 10) ;
	
	var feeObj = new $.DataMap();
	feeObj.put("TRADE_TYPE_CODE", "428");
	feeObj.put("MODE","2");				//预存类
	feeObj.put("CODE", "1870");	//存折
	feeObj.put("FEE", 5000*counts);			//总费用
//	removeFee(feeObj);
//	insertFee(feeObj);
	$.feeMgr.removeFee("428","2","1870");
	$.feeMgr.insertFee(feeObj);
	
	//记录预存办理次数,因为输入的次数随时都可能改
	$('#HIDDEN_HAVE_NUM').val(counts);	
	
	//换预存次数时，重新清空领卡列表
	deleteAllRows();
}

function tableRowOnClick(e){
	var ggCardNo_s = $('#START_CARD').val();	//起始卡号
	$('#END_CARD').val() = ggCardNo_s;	//截止卡号
}

function addGGCard(){

	var startCard = $('#START_CARD').val();
	var endCard = $("#END_CARD").val();
	
	if(startCard == '')
	{
		alert('请输入起始卡号');
		return;
	}
	if(endCard == '')
	{
		alert('请输入截止号码');
		return;
	}
	
	var rowCount = $('#HIDDEN_HAVE_NUM').val();	//未领卡数量
	rowCount = parseFloat(rowCount, 10);
	var haveNum = parseFloat($('#HIDDEN_HAVE_NUM').val(), 10);	
	if(rowCount>0){
		$.beginPageLoading("刮刮卡号码校验...");
		$.ajax.submit(null, 'checkCardCodeRes', '&START_CARD='+startCard+'&END_CARD='+endCard,'', checkAddGGCardSucc, 
		function(errorcode, errorinfo){
			$.endPageLoading();
			alert(errorinfo);
		});
	}else{
		alert("您的未领卡数量为0，不可以领卡！");
		return false;
	}
}


function checkAddGGCardSucc(ajaxDataset){
	$.endPageLoading();
	$("#ACCEPT_NUM_YC").attr("disabled",null);
	
	
	var remark = $('#REMARK').val();
	for(var i=0;i<ajaxDataset.length;i++){
		rowCount = parseFloat($('#HIDDEN_HAVE_NUM').val(),10);	//未领卡数量
		if(rowCount<=0){
			alert("未领卡数量不足！");
			break;
		}
		$('#START_CARD').val(ajaxDataset.get(i, 'VALUE_CARD_NO'));
		$('#END_CARD').val($('#START_CARD').val());
	  
	 	var data = $.table.get("GGCardTable").getTableData();//校验是否重复添加刮刮卡
		var isRepeat = 0;	
				  
	 	for(var j=0, size = data.length; j<size;j++){
	// 		var tt = data.getRowData('col_GGCARD_NO', j);
			var rowdata = data.get(j);
			var card_sn = rowdata.get('col_GGCARD_NO')
	 		if(card_sn==$('#START_CARD').val()){
	// 			alert('不能重复添加刮刮卡！');
	 			isRepeat = 1;
	 			continue;
	 		}
	 		
	 	}
	 	if(isRepeat ==0 ){	
	 		var GGCardEdit = new Array();
	//		GGCardEdit["INST_ID"]='<input type="checkbox" id="cardCheckBox" name="cardCheckBox" value="">';
		    GGCardEdit["col_GGCARD_NO"] = ajaxDataset.get(i, 'VALUE_CARD_NO');
		    GGCardEdit["col_REMARK"] = remark;
		    $.table.get("GGCardTable").addRow(GGCardEdit);
			$('#HIDDEN_HAVE_NUM').val(rowCount-1);
		}else{
			continue;
		}	
	}
}


function delGGCard()
{
	var table = $.table.get("GGCardTable");

	if(table.getSelected()==null){
	 	alert('请选中一条记录');
	 	return;
	}
	table.deleteRow();
	$('#HIDDEN_HAVE_NUM').val(parseFloat($('#HIDDEN_HAVE_NUM').val())+1);
	/*
	$("#GGCardTable_Body input[name=cardCheckBox]").each(function(){
	   if(this.checked){
	     	this.click();
	     	//删除时修改结束时间
	        var rowIndex = this.parentNode.parentNode.rowIndex;
	   	    var table = $.table.get("GGCardTable");
            var json = table.getRowData(null, rowIndex);
	     
	        table.deleteRow();
	   }
	});*/
}	



//输入起始卡号的同时，赋值给截止卡号 
function startCardKeyUp() {
	var start = $("#START_CARD").val();
	$("#END_CARD").val(start);
}



//流量王活动
function onclickCheckBox(obj){
	

	if(obj.checked){
		$.ajax.submit(null, 'checkGGCardActiveNum', '&CHECKED_ACTIVE_DATA='+obj.val(), '',function(data){
			
			if(data.length = 0){
				alert("获取活动对应的可领卡次数失败!");
				return false;
			}
			alert("校验成功");
			var haveNum = data.get('PARA_CODE2', '0');
			var hiddenHaveNum = $('#HIDDEN_HAVE_NUM').val();
			if(trim(hiddenHaveNum).length == 0){
				hiddenHaveNum = 0;
			}
			$('#HIDDEN_HAVE_NUM').val( parseFloat(hiddenHaveNum,10) + parseFloat(haveNum,10) );
			
		
		},function(code,info){
			$.cssubmit.disabledSubmitBtn(true);
			$.showErrorMessage("错误",info);
		});
	}else{
		obj.checked = true;
	}
	
}

/*
*提交
*/
function onTradeSubmit()
{
	var data = $.table.get("GGCardTable").getTableData();
	
	var getCounts = data.length;
	var doMode = $("#DO_MODE").val();
	var YcNum = parseFloat($('#ACCEPT_NUM_YC').val(), 10);			//预存可领卡数量
	var joinNum = parseFloat($('#HAVE_NUM_JOIN').val(), 10);			//预存可领卡数量
	
	var param = '&SERIAL_NUMBER='+$("#AUTH_SERIAL_NUMBER").val();
	param += '&CARD_LIST='+data;
	param += '&DO_MODE='+doMode;
	
	
	if(data.length == 0)
	{
		alert("您没有进行任何操作，不能提交！");
		return false;
	}
	
	
	if(doMode == "1"){//预存
		if(YcNum != getCounts){
			alert('预存办理时,领卡数量['+getCounts+']与总领卡数量['+YcNum+']必须一致！');
			return false;
		}
	}
	
	if(doMode == "4"){//入网
		if(joinNum != getCounts){
			alert('入网有礼活动办理时,领卡数量['+getCounts+']与总领卡数量['+joinNum+']必须一致！');
			return false;
		}
	}
	
	
	if(confirm('您确定领取刮刮卡['+getCounts+']张吗？')){
		$.cssubmit.addParam(param);
		return true;
	}else{
		return false;
	}
}

