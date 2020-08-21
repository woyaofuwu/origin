$(function(){
	debugger;
	$("#importFile").beforeAction(function(e){
			var aa = false;
			ajaxSubmit('SubmitCondPart','importData','','',function(data){
				if(parseInt(data.get(0).get("URL_TAG"))==1){
	                MessageBox.alert("","有部分数据导入失败，请查看失败列表！");
					$('#downPartLi').css("display","block");
					$('#downPart :first-child').attr("href",data.get(0).get("url"));
				}else
				{
					  aa = true;
				}
			},
			function(error_code,error_info){
				$.MessageBox.error("错误提示", error_info);
				$.endPageLoading();
		    },{
			async: false
			});
			return aa;
	});

	$("#myExport").beforeAction(function(e){
		if ($("#tableBody").children("tr").length < 1) {
            MessageBox.alert("提示信息", "没有可导出的数据！");
            return false;
        }
		return confirm('是否导出?');
	});
	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
	if(groupInfo!=undefined && groupInfo!=null){
		 $("#POP_cond_GROUP_ID").val(groupInfo.get("GROUP_ID"));
		/*$("#cond_GROUP_NAME").val(groupInfo.get("GROUP_NAME"));*/
	}	
	
	
});

/**
 * 初始化页面参数
 * */
function resetPage(){
	$.beginPageLoading("努力刷新中...");
	$.ajax.submit('','','','SubmitCondPart',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("",error_info);
	});
}



function importData(){
	if($("#FILE_FIELD1").val()==""){
		MessageBox.alert("",'上传文件不能为空！');
		return false;
	}
	$.beginPageLoading("努力导入中...");
	$.ajax.submit('SubmitCondPart','importData','','',function(data){
	    if(parseInt(data.get(0).get("URL_TAG"))==1){
	                MessageBox.alert("","有部分数据导入失败，请查看失败列表！");
					$('#downPartLi').css("display","block");
					$('#downPart :first-child').attr("href",data.get(0).get("url"));
		}
		else
		{
		   MessageBox.alert("",'导入成功！');
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("",error_info);
	});
}




function queryData(){	
	
	var groupId =  $("#POP_cond_GROUP_ID").val();
	var serialNumber =  $("#cond_CUST_NAME").val();
	
	$.beginPageLoading();
	$.ajax.submit('', 'queryData', '&CUST_ID=' + groupId + '&CUST_NAME=' + serialNumber, 'queryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("",error_info);
    });
}

function exportData(){	
	var params = "EXPORT_PAGESIZE=5000";
	var exportCurrent = $("#EXPORT_CURRENT").val();
	var listFormList = $.table.get("listFormTable").getTableData(null,true);
	if(typeof(listFormList) != "undefined" && listFormList.length == 0) {
		MessageBox.alert("","没有数据可以导出！");
		return false;
	}

	$.beginPageLoading();
	$.ajax.submit('queryCondPart', 'exportData', params, '', function(data){
		 if(parseInt(data.get(0).get("URL_TAG"))==1){
			 window.open(data.get(0).get("url"),"_self");
			 $("#EXPORT_CURRENT").val(parseInt(exportCurrent) + 1);
	      }else{
	    	  MessageBox.alert("","没有数据可以导出！");
	      }	  
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("",error_info);
    });
}

//集团资料查询成功后调用的方法
function selectGroupAfterAction(data) {
	//var groupidname = $.trim($('#GROUP_ID_NAME').val());
	//alert(groupidname);
	var dateid=$("#POP_cond_GROUP_ID").val();
	$('#cond_CUST_ID').val(dateid);
	//alert($('#cond_CUST_ID').val());
}



