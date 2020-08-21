//用来初始化页面的显示
function initPageParam(){
	//获得隐藏域中第二张表格的数据
	var svcotherlists = $("#svcotherlists").text();
	//将svcotherlists转成list类型
	var datalist = $.DatasetList(svcotherlists);
	//初始化的时候将数据放入
	datalist.each(function(item){
		tjOtherTable.addRow($.parseJSON(item.toString()));
	});
	
	var serviceId = $("#SERVICE_ID").val();
	dealOperStateoptions(serviceId);
	
}


function checkSub(obj){
	try {
		//调用公共方法处理
		submitOfferCha();
		backPopup(obj);
	} catch (msg) {
		$.error(msg.message);
	}
}

//从ajax取值,赋给putpagedata
function putpagedataByajax(serviceId)
{
	var serverParamdataset=this.ajaxDataset;
	putpagedata(serverParamdataset,serviceId);
}

function dealOperStateoptions(serviceId)
{  
 	var modifytag=$("#pam_MODIFY_TAG").val();
 	var bizattr=$("#pam_BIZ_ATTR").val();
 	//$("#pam_OPER_STATE options").length = 0; 
 	if("0"==modifytag)//新增时
 	{ 
 		var operStateList = new Wade.DatasetList();
 		var operState = new Wade.DataMap();
 		operState.put("TEXT", "新增");
 		operState.put("VALUE", "0");
 		operStateList.add(operState);
 		$("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);
 		$("#pam_OPER_STATE").val("01"); //新增
 		
 	}else if("2"==modifytag)//对原有记录进行修改时
 	{
 	  var platsyncState=$("#pam_PLAT_SYNC_STATE").val();
 	  if(platsyncState == '')
 	  {
 	    platsyncState="1";
 	  }
 	  
 	  if (platsyncState == "1"){
 			$('#pam_OPER_STATE').html("");
 	 	    $('#pam_OPER_STATE options').length = 0;  //清空操作状态选项
 	 	    var operStateList = new Wade.DatasetList();
 			var operState = new Wade.DataMap();
 			operState.put("TEXT", "暂停");
 			operState.put("VALUE", "04");
 			var operState2 = new Wade.DataMap();
 			operState2.put("TEXT", "变更");
 			operState2.put("VALUE", "08");
 			operStateList.add(operState);
 			operStateList.add(operState2);
 			$("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);
 			$("#pam_OPER_STATE").val("08"); //默认选为变更
 	  }else{
 		var operStateList = new Wade.DatasetList();
 	 		var operState = new Wade.DataMap();
 	 		operState.put("TEXT", "恢复");
 	 		operState.put("VALUE", "05");
 	 		operStateList.add(operState);
 	 		$("#pam_OPER_STATE_span").changeSegmentData(pam_OPER_STATE, "TEXT", "VALUE", operStateList);
 	 		$("#pam_OPER_STATE").val("05");//默认选为恢复
 	  }
 	} 
 	setStatetype();		
}

/**
 * 作用：根据不同的操作类型，页面输入框的可见性.
 *  	04- 暂停 05-恢复
 */
function setStatetype(){
  var operState=$("#pam_OPER_STATE").val();
  if (operState == "04"){
//	  tableDisabled("platsvctab",true);
//	  tableDisabled("tjOtherTable",true);
	  $("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	  $("#pam_ACCESS_MODE").attr('disabled',true);
	  $("#pam_BIZ_ATTR").attr('disabled',true);
	  $("#pam_OPER_STATE").attr('disabled',false);
  } 
  if (operState == "05"){
//	  tableDisabled("platsvctab",true);
	  $("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	  $("#pam_ACCESS_MODE").attr('disabled',true);
	  $("#pam_BIZ_ATTR").attr('disabled',true);
//	  tableDisabled("tjOtherTable",true);
	  $("#pam_OPER_STATE").attr('disabled',false);
	  //parent.MessageBox.alert("","您确定要暂停、恢复成员此业务吗？否则请按底页的[取消]按钮!");
  }
  if (operState == "01"){  
//	tableDisabled("platsvctab",true);
	
	$("#pam_PLAT_SYNC_STATE").attr('disabled',true);
	$("#pam_ACCESS_MODE").attr('disabled',true);
	$("#pam_BIZ_ATTR").attr('disabled',true);
	$("#pam_OPER_STATE").attr('disabled',false);
  }
}



/**
 * 作用：下拉列表给值
 */
function jsAddItemToSelect(objSelect, objItemText, objItemValue) { 
    //判断是否存在        
    if (jsSelectIsExitItem(objSelect, objItemValue)) {        
        MessageBox.alert("","该Item的Value值已经存在");        
    } else {   
       objSelect.append("<option selected value=\"" +objItemValue+"\">" +objItemText+ "</option>");
       //objSelect.options.add(varItem);   
    }        
}   		

 function jsSelectIsExitItem(objSelect, objItemValue) {  
    var isExit=false; 
    objSelect.children("option").each(function(){
         if(this.value==objItemValue)
            return !isExit;
    });
   return isExit;
} 

/**
 * 作用：动态表格列元素值的修改，
 */
function inputBlur(ipt){
  var field = ipt.getAttribute("field");
  var filltype = ipt.getAttribute("filltype");
  var value = $(ipt).val();
  if(field != "" && field != null && field != value){
  	 if(filltype == "0"){
     	ipt.parentNode.parentNode.cells[0].innerText="2";
     }else if(filltype == "1"){
        ipt.parentNode.parentNode.parentNode.cells[0].innerText="2";
     }
  }else{
     if(filltype == "0"){
     	ipt.parentNode.parentNode.cells[0].innerText="1";
     }else if(filltype == "1"){
     	ipt.parentNode.parentNode.parentNode.cells[0].innerText="1";
     }
  }
  
  if(filltype == "0"){
     	ipt.parentNode.parentNode.cells[4].innerText=$(ipt).val();;
     }else if(filltype == "1"){
     	ipt.parentNode.parentNode.parentNode.cells[4].innerText=$(ipt).val();;
     }  
}
/**
 * 作用：控制TABLE里的值是否可填
 * @param tableName 
 * @param flag
 */
function tableDisabled(tableName, flag){

$("#" + tableName + " input").each(function(){
    this.disabled=flag;
});
	  
$("#" + tableName + " SELECT").each(function(){
     this.disabled=flag;
});
}

function getTableData() {
	//var data = $.table.get("MoListTable").getTableData(null,true);
	//$("#MoListTable tbody tr")[1].setAttribute("status", "2");
		var data1 = $.table.get("tjOtherTable").getTableData(null, true);
	MessageBox.alert("",data1);
}

function dealDataByStr(data,str)
{
	data.eachKey(function(item,index,totalcount){
		if((item+"").indexOf(str)<0||(item+"")==undefined){
			data.removeKey(item);
		}
	});
	return data;
}
