var number = 0;
var table = new Wade.DatasetList();
var submitTable = new Wade.DatasetList();//记录变更表格数据(含tag_A和index)
function initPageParam_110020130110() {
	if(submitTable.length==0){//如果没有变更，下标重新计算
		number=0;
	}
	
	var offerTpye = $("#cond_OPER_TYPE").val();
	window["OtherTable"] = new Wade.Table("OtherTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#OtherTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(OtherTable.getSelectedRowData());
	});
	$.each(table,function(index,data) {
		OtherTable.addRow($.parseJSON(data.toString()));
	});
	
	if(table.length==0&&submitTable.length==0)
	{
		$.each(new Wade.DatasetList($("#WLAN_INFO").val()),function(index,data) { 
			data.put("index",number+'');
			OtherTable.addRow($.parseJSON(data.toString()));
			number++;
		});
	}
	
	$("#pam_GRP_WLAN_CODE").val("");
	$("#pam_NET_LINE").val("");
	$("#pam_PRICE").val("");
	$("#pam_DIS_DATA").val("");
	$("#pam_COMPANY_NAME_CODE").val("");
	$("#pam_REMARK").val("");
	
	$("#OtherTable tbody tr").attr("class","");//去掉背景色
}


function initCrtUs() {
	
}

function validateParamPage(methodName) {
	//debugger;
    if(methodName == "CrtUs")
	{
	   var pamidc = $("#pam_GRP_WLAN").val();		
	    if(pamidc != "" && pamidc != null)
	    {
	   		//alert ("请增加集团WLAN！");
	   		$.validate.alerter.one($("#OtherTable")[0], "请增加集团WLAN！");
		    return false;
	    }
	    
	    /*var data = OtherTable.getData();
	    $("#pam_WLANINFOS").val(data);*/
	}
	
	if(methodName == "ChgUs")
	{
		/*var data = OtherTable.getData();
	    
   		$("#pam_WLANINFOS").val(data);*/
	}
	
	var tempTable=new Wade.DatasetList();
	//debugger;
	var submitData=submitTable;
    $.each(submitData,function(index,data) {
    	if(data.get("tag_A")=="02"){
    		data.put("tag","0");
    	}else{
    		data.put("tag",data.get("tag_A"));
    	}
		
		/*data.removeKey("index");
		data.removeKey("tag_A");*/
    	var tempData=new Wade.DataMap();
    	tempData.put("INSTID",data.get("INSTID"));
    	tempData.put("X_TAG",data.get("X_TAG"));
    	tempData.put("pam_COMPANY_NAME",data.get("pam_COMPANY_NAME"));
    	tempData.put("pam_COMPANY_NAME_CODE",data.get("pam_COMPANY_NAME_CODE"));
    	tempData.put("pam_DIS_DATA",data.get("pam_DIS_DATA"));
    	tempData.put("pam_GRP_WLAN",data.get("pam_GRP_WLAN"));
    	tempData.put("pam_GRP_WLAN_CODE",data.get("pam_GRP_WLAN_CODE"));
    	tempData.put("pam_NET_LINE",data.get("pam_NET_LINE"));
    	tempData.put("pam_PRICE",data.get("pam_PRICE"));
    	tempData.put("pam_REMARK",data.get("pam_REMARK"));
    	tempData.put("tag",data.get("tag"));
		tempTable.add(tempData);
	});
    //alert(tempTable);
    $("#pam_WLANINFOS").val(tempTable);

    
    //清空提交数据
    $("#pam_GRP_WLAN_CODE").val("");
	$("#pam_NET_LINE").val("");
	$("#pam_PRICE").val("");
	$("#pam_DIS_DATA").val("");
	$("#pam_COMPANY_NAME_CODE").val("");
	$("#pam_REMARK").val("");
	$("#INSTID").val("");
	if(submitTable.length>0){
		$("#pam_GRP_WLAN").val("");
		$("#pam_COMPANY_NAME").val("");
	}
	//去除必选标记
	//$("#pam_GRP_WLAN_CODE").closest("li").removeClass("link required");
	$("#pam_GRP_WLAN_CODE").attr("nullable", "yes");
	//$("#pam_NET_LINE").closest("li").removeClass("link required");
	$("#pam_NET_LINE").attr("nullable", "yes");
	//$("#pam_PRICE").closest("li").removeClass("link required");
	$("#pam_PRICE").attr("nullable", "yes");
	//$("#pam_COMPANY_NAME_CODE").closest("li").removeClass("link required");
	$("#pam_COMPANY_NAME_CODE").attr("nullable", "yes");
	//$("#pam_REMARK").closest("li").removeClass("link required");
	$("#pam_REMARK").attr("nullable", "yes");
	
    return true;
}
	
function createData(){
	//debugger;
    //var grpwlan = $("#pam_GRP_WLAN_CODE option:selected").text();    
	var grpwlan = $("#pam_GRP_WLAN_CODE").text();    
    var grpwlancode = $('#pam_GRP_WLAN_CODE').val();
    var netline = $('#pam_NET_LINE').val();
    var priceline = $("#pam_PRICE").val();
    var discount = $("#pam_DIS_DATA").val();
    //var company = $("#pam_COMPANY_NAME_CODE option:selected").text();
    var company = $("#pam_COMPANY_NAME_CODE").text();
    var companycode = $("#pam_COMPANY_NAME_CODE").val();
    var remark = $("#pam_REMARK").val();
    
    if(!verifySupTelListTable()) return false;
    
    var numberCode = Number(grpwlancode)+1;
 
    //var pamAttrList = $.table.get("OtherTable").getTableData(null,true);
    var pamAttrList = OtherTable.getData(true);
	
	if("" != pamAttrList){
		for(var i=0; i<pamAttrList.length ;i++){
			var attrs = pamAttrList.get(i);
			var attrsNumCode = attrs.get("pam_GRP_WLAN_CODE");
			var attrsNum = attrs.get("pam_GRP_WLAN");
			var tag = attrs.get("tag");
			if(numberCode == attrsNumCode && tag != 1){
				//alert ("关键字段'集团WLAN'已经存在同样的值"+attrsNum);
				$.validate.alerter.one($("#pam_GRP_WLAN_CODE")[0], "关键字段'集团WLAN'已经存在同样的值"+attrsNum);
				return false;
			}
		}
	}
	
	var datalineData = $.ajax.buildJsonData("DiscntsPart");
	var data = $.DataMap(datalineData);
	data.put("pam_GRP_WLAN_CODE",numberCode);
	data.put("pam_GRP_WLAN",$("#pam_GRP_WLAN_CODE").text());
	data.put("pam_DISCOUNT",$("#pam_DIS_DATA").text());
	data.put("pam_COMPANY_NAME",$("#pam_COMPANY_NAME_CODE").text());
	data.put("tag_A","0");
	data.put("index",number+"");
	OtherTable.addRow($.parseJSON(data.toString()));
	//展示数据保存
	//table.add(data);
	//变更数据保存
	submitTable.add(OtherTable.getRowData(OtherTable.getData().length-1));
	
	$("#pam_GRP_WLAN_CODE").val("");
	$("#pam_GRP_WLAN").val("");
	$("#pam_NET_LINE").val("");
	$("#pam_PRICE").val("");
	$("#pam_DISCOUNT").val("");
	$("#pam_DIS_DATA").val("");
	$("#pam_COMPANY_NAME").val("");
	$("#pam_COMPANY_NAME_CODE").val("");
	$("#pam_REMARK").val("");
	number++;
	
	$("#OtherTable tbody tr").attr("class","");//去掉背景色
}

function updateData() {
	//debugger;
    //效验表单
    if(!verifySupTelListTable()) return false;

    //var grpwlan = $("#pam_GRP_WLAN_CODE option:selected").text();  
    var grpwlan = $("#pam_GRP_WLAN_CODE").text();
    var grpwlancode = $('#pam_GRP_WLAN_CODE').val();
    var netline = $('#pam_NET_LINE').val();
    var priceline = $("#pam_PRICE").val();
    var discount = $("#pam_DISCOUNT").val();
    //var company = $("#pam_COMPANY_NAME_CODE option:selected").text();
    var company = $("#pam_COMPANY_NAME_CODE").text();
    var companycode = $("#pam_COMPANY_NAME_CODE").val();
    var remark = $("#pam_REMARK").val();
    var instid = $("#INSTID").val();
    
    var maxnumberline = $("#MAX_NUMBER_LINE").value;
    
    var numberCode = Number(grpwlancode)+1;
    
	//var pamAttrList = $.table.get("OtherTable").getTableData(null,true);
    var pamAttrList = OtherTable.getData();
	
	//var rowData = $.table.get("OtherTable").getRowData();
    var rowData = OtherTable.getRowData(OtherTable.selected)
    var rowNumber = rowData.get("pam_GRP_WLAN_CODE");
    
	if("" != pamAttrList){
		for(var i=0; i<pamAttrList.length ;i++){
			var attrs = pamAttrList.get(i);
			var attrsNumCode = attrs.get("pam_GRP_WLAN_CODE");
			var tag = attrs.get("tag");
			if(rowNumber == numberCode){
				break ;
			}
			if(numberCode == attrsNumCode && tag != 1){
				//alert ("该集团WLAN已存在不能重复");
				$.validate.alerter.one($("#pam_GRP_WLAN_CODE")[0], "该集团WLAN已存在不能重复");
				return false;
			}
		}
	}
	
	//$("#pam_GRP_WLAN_CODE").val(numberCode);
	/*$("#pam_GRP_WLAN").val(grpwlan);
	$("#pam_NET_LINE").val(netline);
	$("#pam_PRICE").val(priceline);
	$("#pam_DISCOUNT").val(discount);
	$("#pam_COMPANY_NAME").val(company);
	$("#pam_COMPANY_NAME_CODE").val(companycode);
	$("#pam_REMARK").val(remark);
	$("#INSTID").val(instid);*/
	
	var discntData = $.ajax.buildJsonData("DiscntsPart");
	var data = $.DataMap(discntData);
	data.put("pam_GRP_WLAN_CODE",numberCode);
	data.put("pam_GRP_WLAN",$("#pam_GRP_WLAN_CODE").text());
	data.put("pam_DISCOUNT",$("#pam_DIS_DATA").text());
	data.put("pam_COMPANY_NAME",$("#pam_COMPANY_NAME_CODE").text());
	var checkedData =rowData;
	if(checkedData.get("pam_GRP_WLAN_CODE")==data.get("pam_GRP_WLAN_CODE")&&
		checkedData.get("pam_NET_LINE")==data.get("pam_NET_LINE")&&
		checkedData.get("pam_PRICE")==data.get("pam_PRICE")&&
		checkedData.get("pam_DIS_DATA")==data.get("pam_DIS_DATA")&&
		checkedData.get("pam_COMPANY_NAME_CODE")==data.get("pam_COMPANY_NAME_CODE")&&
		checkedData.get("pam_REMARK")==data.get("pam_REMARK")
	){
		$.validate.alerter.one($("#pam_GRP_WLAN_CODE")[0], "请修改！");
		return false;
	}
	
	
	var selectData=OtherTable.getRowData(OtherTable.selected);
	if(selectData.get("tag_A")!="0"&&selectData.get("tag_A")!="02"){//之前不是新增的
		data.put("tag_A","2");//修改了旧数据
	}else{
		data.put("tag_A","02");//本质上是新增
	}
	data.put("index",selectData.get("index"));
	OtherTable.updateRow($.parseJSON(data.toString()),OtherTable.selected);
	
	//展示数据保存
	//table.add(data);
	//变更数据保存
	//submitTable.add(data);
	//table.add(data);
	//debugger;
	var updateFlag=true;
	var selectData=OtherTable.getRowData(OtherTable.selected);
	$.each(submitTable,function(index,data) {
    	if(data.get("index")==selectData.get("index")){
    		//data=OtherTable.getRowData(OtherTable.selected);
    		//submitTable.remove(this);
    		data.put("INSTID",selectData.get("INSTID"));
    		data.put("X_TAG",selectData.get("X_TAG"));
    		data.put("pam_COMPANY_NAME",selectData.get("pam_COMPANY_NAME"));
    		data.put("pam_COMPANY_NAME_CODE",selectData.get("pam_COMPANY_NAME_CODE"));
    		data.put("pam_DIS_DATA",selectData.get("pam_DIS_DATA"));
    		data.put("pam_GRP_WLAN",selectData.get("pam_GRP_WLAN"));
    		data.put("pam_GRP_WLAN_CODE",selectData.get("pam_GRP_WLAN_CODE"));
    		data.put("pam_NET_LINE",selectData.get("pam_NET_LINE"));
    		data.put("pam_PRICE",selectData.get("pam_PRICE"));
    		data.put("pam_REMARK",selectData.get("pam_REMARK"));
    		data.put("tag",selectData.get("tag"));
    		data.put("index",selectData.get("index"));
    		data.put("tag_A",selectData.get("tag_A"));
    		updateFlag=false;
    	}
	});
	if(updateFlag){
		submitTable.add(OtherTable.getRowData(OtherTable.selected));
	}
	
	
	$("#pam_GRP_WLAN_CODE").val("");
	$("#pam_GRP_WLAN").val("");
	$("#pam_NET_LINE").val("");
	$("#pam_PRICE").val("");
	$("#pam_DISCOUNT").val("");
	$("#pam_DIS_DATA").val("");
	$("#pam_COMPANY_NAME").val("");
	$("#pam_COMPANY_NAME_CODE").val("");
	$("#pam_REMARK").val("");
}

function deleteData(){
	//debugger;
	//$.table.get("OtherTable").deleteRow();
	var selectData=OtherTable.getRowData(OtherTable.selected);
	var tableTag=selectData.get("tag_A");
	var tableIndex=selectData.get("index");
	if(tableTag=="0"||tableTag=="02"){
		var tempTable=new Wade.DatasetList();
	    $.each(submitTable,function(index,data) {
			if(data.get("index")!=tableIndex){//把之前新增的数据过滤掉，不提交
				tempTable.add(data);
			}
		});
	    submitTable= tempTable;
	}else{//tag_A为空或2的情况
		var tempTable=new Wade.DatasetList();
		var tempTag=false;
	    $.each(submitTable,function(index,data) {
			if(data.get("index")==tableIndex){//标记1,提交删除
				data.put("tag_A","1");
				tempTag=true;
			}
			tempTable.add(data);
		});
	    if(tempTag){
	    	submitTable= tempTable;
	    }else{
	    	selectData.put("tag_A","1");
	    	submitTable.add(selectData);
	    }
	    
	}
	OtherTable.deleteRow(OtherTable.selected);
	
}

function tableRowClick(data) {
	//var rowData = $.table.get("OtherTable").getRowData();
	$("#pam_GRP_WLAN_CODE").val((Number(data.get("pam_GRP_WLAN_CODE"))-1)+'');
	//$("#pam_GRP_WLAN").val(data.get("pam_GRP_WLAN"));
	$("#pam_NET_LINE").val(data.get("pam_NET_LINE"));
	$("#pam_PRICE").val(data.get("pam_PRICE"));
	//$("#pam_DISCOUNT").val(data.get("pam_DISCOUNT"));
	$("#pam_DIS_DATA").val(data.get("pam_DIS_DATA"));
	//$("#pam_COMPANY_NAME").val(data.get("pam_COMPANY_NAME"));
	$("#pam_COMPANY_NAME_CODE").val(data.get("pam_COMPANY_NAME_CODE"));
	$("#pam_REMARK").val(data.get("pam_REMARK"));
	$("#INSTID").val(data.get("INSTID"));
}

function countWlanFee(){
    var wlannet = $('#pam_NET_LINE').val();
    if(!(parseInt(wlannet) > 1 && parseInt(wlannet) < 2561))
    {
        //alert("输入带宽在2M~2560M之间！");
        $.validate.alerter.one($("#pam_NET_LINE")[0], "输入带宽在2M~2560M之间！");
        return false;
    }
       
	  var priceline = $("#pam_PRICE").val();
	  var strbandwidthdatas=$("#BANDWIDTHDATAS").val();
	  var pricedataset =new Wade.DatasetList(strbandwidthdatas);
	  var pricecount=pricedataset.length;
	  var wlanDataMin = 1,wlanFeeMin,wlanDataMax = 2561,wlanFeeMax;
	  var d,f ;
	  
	  for (i=0;i<pricecount;i++)
	  {
	     var wlanData = pricedataset.get(i,'PARA_CODE1');
	     var wlanFee =  pricedataset.get(i,'PARA_CODE2');
	     var bandwidthdatas = pricedataset.get(i);
	     
	     if(wlannet == wlanData)
	     {
	        $("#pam_PRICE").val(wlanFee);
	    	break; 
	      }
	      else{
	   			if( parseInt(wlanData) < parseInt(wlannet) && parseInt(wlanDataMin) < parseInt(wlanData))
	   			{
	   				wlanDataMin = wlanData;
	   				wlanFeeMin = wlanFee ;
	   			}
	   			if( parseInt(wlanData) > parseInt(wlannet) &&  parseInt(wlanData) < parseInt(wlanDataMax) )
	   			{
	   				wlanDataMax = wlanData;
	   				wlanFeeMax = wlanFee ;
	   			}
	   		    d=(parseInt(wlanDataMax)-parseInt(wlannet))/(parseInt(wlannet)-parseInt(wlanDataMin));
			    f=(parseInt(wlanFeeMax)+parseInt(wlanFeeMin)*d)/(d+1);
			    
			    if(!isNaN(f)){
			    	var tempf = f + "";
			    	if(tempf.indexOf(".") !=-1){
						var tempLength = tempf.indexOf(".");
						f = tempf.substring(0,tempLength);
					}
			    }
			    $("#pam_PRICE").val(f);
	          }          
	    }

		var strdiscountdata = $('#DISCOUNTDATA').val();
		var discountinfoset = new Wade.DatasetList(strdiscountdata);
		
		 var wlandisdata=  discountinfoset.get(0,'PARA_CODE2');
		   
		 var discountData =  "";
		 if (wlannet <= parseInt(wlandisdata)) 
		 {
			  discountData = discountinfoset.get(0,'PARA_CODE3'); 
	           
     	 }else
     	 {
			  discountData = discountinfoset.get(0,'PARA_CODE4'); 
         }
         
         if(discountData){
         	discountData.length = '0' ;
         }
         
		 for(var i = discountData;i <= 10; i++)
		 {
	       //jsAddItemToSelect($("#pam_DIS_DATA"), i, i);
	       //$("#pam_DIS_DATA").bindData({i:i});
	     } 
 }
	
function verifySupTelListTable()
{
	if(!checkFiled("pam_GRP_WLAN_CODE")) return false;
	if(!checkFiled("pam_NET_LINE")) return false;
	if(!checkFiled("pam_PRICE")) return false;
	if(!checkFiled("pam_COMPANY_NAME_CODE")) return false;

	return true;
}

function checkFiled(obj)
{
	var keyValue = $("#" + obj).val();	
	
	if(null == keyValue || "" == keyValue)
	{
	    //alert($("#" + obj).attr("desc") + '不能为空');
	    $.validate.alerter.one($("#" + obj)[0], $("#" + obj).attr("desc") + '不能为空');
	    $("#" + obj).focus()
	 	return false;
	}
	
	return $.validate.verifyField($("#" + obj));
	
	return true;
}

function jsAddItemToSelect(objSelect, objItemText, objItemValue) { 
       objSelect.append("<option selected value=\"" +objItemValue+"\">" +objItemText+ "</option>");
}

//提交
function checkSub(obj)
{
	var offerTpye = $("#cond_OPER_TYPE").val();
	if(!validateParamPage(offerTpye))
		return false;
	if(!submitOfferCha())
		return false; 
	table = null;
	table = OtherTable.getData();
	backPopup(obj);
}
