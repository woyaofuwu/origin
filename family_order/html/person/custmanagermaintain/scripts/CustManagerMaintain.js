$(document).ready(function(){
	 $("#exportDataButton").css("display", "none");
});

(function($){
	$.extend({custManager:{
	//初始化页面参数
		resetAreaPart:function(){
			var tableData = this.buildJsonData();
			$.table.get("seriCustInfosTable").cleanRows();
		    resetArea('commInfoPart', true);
		    $("#exportDataButton").css("display", "none");
	    },
	    
	    checkQueryPart:function(){
 		   $.beginPageLoading("查询中..");
		     $.ajax.submit('queryPart', 'getCustManagerInfos', null, 'custManagerInfoTablePart',function(data){
		    	$("#exportDataButton").css("display", "");
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
  			});
	    },
	    ExportQueryPart:function(){
 		   $.beginPageLoading("导出中..");
		     $.ajax.submit('queryPart', 'exportCustManagerInfos', null, '',function(data){
		     	var title= "批量数据导出结果!";
				var hint_message = "";
				var DATASET_SIZE = data.get("DATASET_SIZE");
				var FILE_ID = data.get("FILE_ID");
				var ERROR_URL = data.get("ERROR_URL");
				hint_message = "批量导出情况：共导出" + DATASET_SIZE + "条<br/>请击<a href="+ERROR_URL+">[批量导出列表.xls]</a>下载导出文件<br/>";
				$.showSucMessage(title,hint_message);
				
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
  			});
		  	
	    },
	    addrow:function(data){
		    var booktable = $.table.get("seriCustInfosTable").getTableData(null, true);
		    var serialNumber =  $("#SERIAL_NUMBER").val();
		   	 if(!$.isNumeric(serialNumber)){
		   		 MessageBox.alert("告警提示", "请输入普通客户手机号码！");
		   	 	return false;
		   	 }
		   	 var rsrvStr1 =  $("#RSRV_STR1").val();
		   	 if(!$.isNumeric(rsrvStr1)){
		   		 MessageBox.alert("告警提示", "请输入客户经理手机号码！")
		   	  	 return false;
		   	 }
		   	 
		   	 //判断是否已经添加 
		   	  var size = booktable.length;
			for(var i = 0; i < size; i++)
			{
				var tmp = booktable.get(i);
				var rsrvStrValue = tmp.get('col_RSRV_STR1');
				var serialNumberValue = tmp.get('col_SERIAL_NUMBER');
				if(serialNumberValue == serialNumber && rsrvStr1==rsrvStrValue){
		   	  		MessageBox.alert("告警提示", "请勿重复添加！");
		   	  		return false;
		   	  	}
			}
		   	 var custEdit = $.ajax.buildJsonData("commInfoPart");
		   	 custEdit["col_RSRV_STR1"] = rsrvStr1;
		   	 custEdit["col_SERIAL_NUMBER"] = serialNumber;
		   	 
		   	 if(data == 0){
		   	 	custEdit["col_DEAL_FLAG"] = "3";
		   	 }else{
		   		custEdit["col_DEAL_FLAG"] = "4";
		   	 }
		   	 
		   	 $.table.get("seriCustInfosTable").addRow(custEdit);
	    },
	    buildJsonData:function(){
	    	return $.table.get("seriCustInfosTable").getTableData(null, true);
	    },
	    submitSureClick:function(){
		    var chooseType ="";
		    if($("input[name='stopOpenRelation']:checked").val()=='0')
				chooseType="0";
			else
				chooseType="1";
				
			
	    	var buildSelectJsonData = this.buildJsonData();
	    	if(buildSelectJsonData.length<=0){
	    		MessageBox.alert("告警提示", "您没有做任何修改，请修改后提交!")
		   	 	return false;
	    	}
	    	
	    	var truthBeTold = confirm("确定要提交吗?");
			if (!truthBeTold) {
				return false;
			} 
	    	
	    	var param =  "&stopOpenRelation="+chooseType+"&buildSelectJsonData="+buildSelectJsonData;
	    	$.beginPageLoading("信息加载中.....");
			$.ajax.submit('', 'onTradeSubmit',param, 'seriCustInfosPart,commInfoPart', 
			function(data){
				var title= "批量数据导入结果!";   
				var DATASET_SIZE = data.get("DATASET_SIZE");
				var SUCC_SIZE = data.get("SUCC_SIZE");
				var FAILD_SIZE = data.get("FAILD_SIZE");
				var hint_message = "批量导入情况：共导入" + DATASET_SIZE + "条<br/>成功" + SUCC_SIZE + "条<br/>失败" + FAILD_SIZE + "条";
				var FAILED_TYPE = data.get("FAILED_TYPE");
				if(FAILED_TYPE == '1'){
					var FILE_ID = data.get("FILE_ID");
					var ERROR_URL = data.get("ERROR_URL");
					hint_message += "<br/>请击<a href="+ERROR_URL+">[批量导入失败列表.xls]</a>下载导入失败文件<br/>";
					$.showSucMessage(title,hint_message);
					$.endPageLoading();
				}else{
					$.showSucMessage(title,hint_message);
					$.endPageLoading();
				}
				
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
  			});
	    },
	    importOcsData:function(){
	    	var STICK_LIST = $("#cond_STICK_LIST").val();
			if(STICK_LIST == ''){
			 	alert('请先上传文件');
			 	return false;
			}
			
			var truthBeTold = confirm("确定要导入数据吗?");
			if (!truthBeTold) {
				return false;
			} 
			$.beginPageLoading("努力导入中...");
			$.ajax.submit('commInfoPart','importData','','seriCustInfosPart',function(data){
				var title= "批量数据导入结果!";
				var hint_message = "";
				var FAILED_TYPE = data.get("FAILED_TYPE");
				var DATASET_SIZE = data.get("DATASET_SIZE");
				var SUCC_SIZE = data.get("SUCC_SIZE");
				var FAILD_SIZE = data.get("FAILD_SIZE");
				hint_message += "批量导入情况：共导入" + DATASET_SIZE + "条<br/>成功" + SUCC_SIZE + "条<br/>失败" + FAILD_SIZE + "条<br/>";
				if(FAILED_TYPE == '1'){
					var FILE_ID = data.get("FILE_ID");
					var ERROR_URL = data.get("ERROR_URL");
					hint_message += "请击<a href="+ERROR_URL+">[批量导入失败列表.xls]</a>下载导入失败文件<br/>";
				}
				$.showSucMessage(title,hint_message);
				$.endPageLoading();
			},
			function(error_code,error_info){
				$.endPageLoading();
				alert(error_info);
			});
	    }
	    
	}});
})(Wade);
