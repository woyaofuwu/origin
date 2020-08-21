inioper();

function inioper(){
	$("#cond_IS_EXTEND_TIME").val("0");
}


//用户资料模糊查询
function queryRecyclePhones(){
	//查询条件校验
	if(!$.validate.verifyAll("QueryConditionPart")) {
		return false;
	}
	
	var isExtendTime=$("#cond_IS_EXTEND_TIME").val();
	if(isExtendTime=="1"){
		//验证是否存在跨月
		var startDate=$("#cond_START_DATE").val();
		var endDate=$("#cond_END_DATE").val(); 
		
		var isHasQueryDate=false;
		if(startDate!=""||endDate!=""){
			isHasQueryDate=true;
		}
		
		if(isHasQueryDate){
			if(startDate==""){
				MessageBox.alert("提示","延期操作开始日期和结束日期,不能只填写一个！");
				return false;
			}
			if(endDate==""){
				MessageBox.alert("提示","延期操作开始日期和结束日期,不能只填写一个！");
				return false;
			}
			
			var beginYear=startDate.substring(0,4);
			var beginMonth=startDate.substring(5,7);
			
			var endYear=endDate.substring(0,4);
			var endMonth=endDate.substring(5,7);
			
			if(!(beginYear==endYear&&beginMonth==endMonth)){
				MessageBox.alert("提示","不能跨月查询！");
				return false;
			}
		}
		
	}
	
	
	$.beginPageLoading("正在查询数据...");
	//用户资料模糊查询
	$.ajax.submit('QueryConditionPart', 'qryUserBackPhones', null, 'QueryListPart', function(data){
		$.endPageLoading();
		
		var queryResultCode=data.get("QUERY_RESULT_CODE");
		if(queryResultCode!="0"){
			var queryResultResult=data.get("QUERY_RESULT_RESULT");
			MessageBox.error("错误提示",queryResultResult, null, null, null, null);	
			return false;
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info);
    });
}

function extendRecycleTime()
{
	if(!$.validate.verifyAll("EXTEND_TIME_AREA")) {//先校验已配置的校验属性
		return false;
	}
	
	var extendTime=$("#EXTEND_TIME").val();
	var userIds="";
	var isChoosePhones=false;
	
	var isContinue=true;
	var tableObj = $.table.get("phoneInfos");
    var tbodyObj = $("tbody", tableObj.getTable()[0]);
    var size = tableObj.tabHeadSize;
    if(tbodyObj){
       $("tr", tbodyObj[0]).each(function(index, item){
           var checked = $("input[name=CHOOSE_PHONES]", item).attr("checked");
           if(checked){
        	   if(!isChoosePhones){
        		   isChoosePhones=true;
        	   }
        	   
        	   var json = tableObj.getRowData(null, index+size);
        	   var userId=json.get("USER_ID");
        	   var serialNumber=json.get("SERIAL_NUMBER");
        	   var openDate=json.get("OPEN_DATE");
        	   var isExntendTime=json.get("IS_EXTEND_TIME");
        	   if(isExntendTime=="1"){
        		   MessageBox.error("错误提示","号码"+serialNumber+"已经被延期无法再延期！", null, null, null, null);
        		   isContinue=false;
        		   return false;
        	   }
        	   if(userId&&userId!=""){
        		   userIds=userId+','+openDate+","+serialNumber+";"+userIds;
        	   }
           }
       });
    }
    
    if(!isContinue){
    	return false;
    }
    
	if(!isChoosePhones){
		alert("请选择要延期的手机号！");
		return false;
	}
	
	$.beginPageLoading("操作中...");
	
	$.ajax.submit('QueryConditionPart', 'extendPhones', '&EXTEND_TIME='+extendTime+"&USER_IDS="+userIds, 'QueryListPart', function(data)
	{
		$.endPageLoading();
		var extendResult=data.get("EXTEND_RESULT");
		if(extendResult=="0"){
			MessageBox.success("成功提示","操作成功!");
		}
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info);
	});
	
}

function chooseAll(obj){
	var jObj=$(obj);
	var checked=jObj.attr("checked");
	
	var tableObj = $.table.get("phoneInfos");
    var tbodyObj = $("tbody", tableObj.getTable()[0]);
    var size = tableObj.tabHeadSize;
    if(tbodyObj){
       $("tr", tbodyObj[0]).each(function(index, item){
           $("input[name=CHOOSE_PHONES]", item).attr("checked",checked);
       });
    }
	
}


function showTradeQueryArea(){
	var isExtend=$("#cond_IS_EXTEND_TIME").val();
	
	if(isExtend=="1"){
		$("#TRADE_QUERY_AREA").css("display","");
	}else if(isExtend=="0"){
		$("#TRADE_QUERY_AREA").css("display","none");
	}
}


function importData(){
	var extendTime=$("#EXTEND_TIME").val();
	if(extendTime==null||extendTime==""){
		MessageBox.alert("提示","请选择要延期的时间！");
		return false;
	}
	
	var STICK_LIST = $("#cond_STICK_LIST").val();
	if(STICK_LIST == ''){
		MessageBox.alert("提示","请先上传文件！");
	 	return false;
	}
	
	$.beginPageLoading("导入数据处理中...");
	$.ajax.submit('EXTEND_TIME_AREA,DataImportPart','importData','','',function(data){
		var title= "数据处理结果";
		
		var DATASET_SIZE = data.get("DATASET_SIZE");
		var SUCC_SIZE = data.get("SUCC_SIZE");
		var FAILD_SIZE = data.get("FAILD_SIZE");
		var FILE_ID = data.get("FILE_ID");
		var FILE_URL= data.get("FILE_URL");
		var hint_message= "处理情况总结：共导入" + DATASET_SIZE + "条<br/>成功" + SUCC_SIZE + "条<br/>失败" + FAILD_SIZE + "条<br/>请击<a href="+FILE_URL+">[处理结果列表.xls]</a>下载文件<br/>";
		
		
		$.showSucMessage(title,hint_message);
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
	
}
