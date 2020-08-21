document.write("<script language=\"JavaScript\" src=\"local\/scripts\/spamdeal\/json2.js\"><\/script>"); 
document.write("<script language=\"JavaScript\" src=\"local\/scripts\/spamdeal\/Utils.js\"><\/script>");
function queryList(){
	//查询条件校验
	if(!$.validate.verifyAll("psptPart")) {
		return false;
	}
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('psptPart', 'queryCampOnList', null, 'QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}


function delWasteWordInfo(obj){
	var tabVal = $.table.get("listFormTable");
	if ($.validate.verifyBox(obj, "SEQ", "请选中一条数据")) {
		MessageBox.confirm("提示信息", "你确定要解除预占吗？", function(btn) {
			if(btn=='ok'){
				var resStr ="";
				$("input:checked",tabVal.getTable()[0]).each(function(idx, item){
					var row = $(item).parent().parent();
					rowNo = row.attr("rowIndex");
					var rowData = tabVal.getRowData(null,rowNo);
//					var jsonrowData = String2JSON(rowData);
					if(rowData.get("checkInfo") != 'on'){
						resStr += rowData+",";
					}
				});
				resStr = "["+resStr.substring(0,resStr.length-1)+"]";
				idCardType = document.getElementById("PSPT_TYPE_CODE").value;
				idCardNum  = document.getElementById("PSPT_ID").value;
				$.beginPageLoading();
				$.ajax.submit('','releaseCampOn','&LIST='+resStr+'&ID_CARD_TYPE='+idCardType+'&ID_CARD_NUM='+idCardNum,'',function(data){
					$("input:checked",tabVal.getTable()[0]).each(function(idx, item){
						var row = $(item).parent().parent();
						rowNo = row.attr("rowIndex");
						var rowData = tabVal.getRowData(null,rowNo);
						tabVal.deleteRow(row);
						
					});
					alert("解除成功");
					$.endPageLoading();
				},function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
				});
			}else{
				closeMessage();
			}

		});



	}
}
function reInfoAction(){
	var relInfo = $("#reInfo").val();
//	alert(relInfo);
	var editType = $("#editType").val();
	var tabVal = $.table.get("listFormTable");
	var jsonData = String2JSON(relInfo);
	jsonData["checkInfo"]="<input type='checkbox' id='SEQ' name='SEQ'  value='"+jsonData["SEQ"]+"'/>";
	if(editType=="1"){//add
		tabVal.addRow(jsonData);
	}else{
		$("input:checked",tabVal.getTable()[0]).each(function(idx, item){
			var row = $(item).parent().parent();
			var rowNo = row.attr("rowIndex");
			tabVal.updateRow(jsonData,rowNo);
		});
	}
}
//function submitInfo(){
//	if(!verifyAll('QueryCondPart')){
//		return false;
//	}
//	//submit
//	$.beginPageLoading();
//	$.ajax.submit('QueryCondPart', 'submitInfo', null, null, function(data){
//		$.endPageLoading();
//		var editType = $("#editType").val();
//		if(data.get("RESULT_CODE")=="0"){
//			alert("记录："+data.get("RESULT_INFO").get("WASTE_WORD")+" 已存在！请重新录入信息！");
//		}else{
//			if(editType == "1"){ //add
//				alert("垃圾关键词新增成功！");
//			}else{//upd
//				alert("垃圾关键词修改成功！");
//			}
//			var resData = data.get("RESULT_INFO");
//			$('#reInfo', parent.document).val(resData);
//			$.setReturnValue(resData,"",true);
//		}
//	},
//	function(error_code,error_info){
//		$.endPageLoading();
//		alert(error_info);
//	});
//
//}