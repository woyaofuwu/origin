//根据地市更新区县
function submitEparchy() {
//	ajaxSubmit('QueryCond', 'submitEparchy', '&PARENT_ADDR_ID=' + $(
//			"#eparchy").val(), null, null,
//			function(error_code,error_info) {
//		$.MessageBox.error(error_code, error_info);
//	});
	addressTree.empty(true);
	$("#GIS1").val(eparchy.selectedText); 
}

// 根据区县更新树
function refershAdressTreePart(obj) {	
	var obj = $(obj);
	$("#GIS2").val(city.selectedText); 
	addressTree.empty(true);
	addressTree.setParam('CITY_CODE', obj.val());
	addressTree.init();
}


function TreeSearch(){	
	var eparchy = $("#eparchy").val();
	if ("" == eparchy){
		MessageBox.alert("提示","请选择地州");
		return false;
    } 
	var treeSearch = $("#treeSearch").val();
	var houseSearch=$("#houseSearch").val();
	if ("" == treeSearch &&""==houseSearch){
		MessageBox.alert("提示","定位街道内容与定位小区 请填写至少一个");
		return false;
    } 	
	addressTree.empty(true);
	addressTree.setParam('CITY_CODE', eparchy);
	addressTree.setParam('TREE_SEARCH', treeSearch);
	addressTree.setParam('HOUSE_SEARCH', houseSearch);
	addressTree.init();
	
	addressTree.setParam('TREE_SEARCH', '');
	//addressTree.setParam('HOUSE_SEARCH', '');
}

//根据树上节点
function queryAddressByTree(node){  
	
	var b = node.dataid.split("●");
	var text = getParentNode(node,"");   
	
	ajaxSubmit('', 'getParentName', '&ADDR_NAME=' +node.text+'&PARENT_ADDR_ID=' + $(
	"#eparchy").val(), '',function(data) { 
		
		var addr_name =data.get(0).get("ADDR_NAME");
		
		$("#address").val(eparchy.selectedText + addr_name+ text + node.text); 
		$("#DETAILGIS").val(eparchy.selectedText + addr_name+ text + node.text); 	
		$("#nodeIds").val(node.dataid); 
		$('#addressCol').addClass('l_col-cur-2');
		
		},
		function(error_code,error_info)
			{  
				$.MessageBox.error(error_code,error_info);
		    });

	
}

function getParentNode(nodeInfo,text)
{
	var nodeData = addressTree._getParentNodeDataByDataId(nodeInfo.dataid);
	
	if(null == nodeData){
		return text;
	}
	text =  nodeData.text  + text; 
	
	return getParentNode(nodeData,text);
}

function queryAddressBtn()
{
	var eparchy =  $("#eparchy").val();
	if ("" == eparchy){
		MessageBox.alert("提示","请选择地州");
		return false;
	} 
	var selectAddr = $("#DETAILGIS").val();
	if ("" == eparchy){
		MessageBox.alert("提示","请填写地址信息或从左侧选取");
		return false;
	}
	
	var address = $("#address").val();
	
	if(selectAddr == address) {
		var nodeids = $("#nodeIds").val().split("●");	
		if (nodeids.length <= 1){
			MessageBox.alert("提示","请选取街道乡镇以下级别的地址");
			return false;
		}
	}
	//document.getElementById("DataTablePart").style.display = "block";
	// document.getElementById("DataTablePart2").style.display = "none";
	$.beginPageLoading("查询中..."); 
	//"&cityName="+$("#city option:selected").text()
	ajaxSubmit('QueryCond2', 'queryAddress', '', 'DataTablePart',
			function(data) { 
				$.endPageLoading();			
			},
			function(error_code,error_info)
			{  
				$.MessageBox.error(error_code,error_info);
		    });
}

function queryAddressLikeBtn(){
	var city =  $("#city").val();
	if ("" == city){
		MessageBox.alert("提示","请选择区/县");
		return false;
	} 
	var addressLike = $("#addressLike").val();
	if ("" == addressLike){
		MessageBox.alert("提示","请填写模糊查询地址信息");
		return false;
	} 	
	 document.getElementById("DataTablePart2").style.display = "block";
	 document.getElementById("DataTablePart").style.display = "none";
	//"&GIS1="+$("#eparchy option:selected").text()+"&GIS2="+$("#city option:selected").text()+"&DETAILGIS="+addressLike
	$.beginPageLoading("查询中..."); 
	ajaxSubmit('QueryCond2', 'queryAddressLike', "", 'DataTablePart2',
			function(data) { 
				$.endPageLoading();			
			},
			function(error_code,error_info)
			{  
				$.MessageBox.error(error_code,error_info);
		    });
}


function confirmReginalDate(obj){
	obj = $(obj)
	var isFamilyWide = $("#IS_FAMILY_WIDE").val();
	var portNum = obj.attr("CAPACITY_CANUSED");
	if(portNum==""||portNum==0||portNum<0){
		
		MessageBox.alert("提示","所选设备无可用端口！");
        return;

	}
	var device_id = obj.attr("DEVICE_ID");
	var open_type = obj.attr("OPEN_TYPE");
	
	if(open_type=="移动FTTH"){
		open_type="FTTH";
	}else if(open_type=="铁通FTTH"){
		open_type="TTFTTH";
	}else if(open_type=="移动FTTB"){
		open_type="GPON";
	}else if(open_type=="铁通FTTB"){
		open_type="TTFTTB";
	}else if(open_type=="铁通ADSL"){
		open_type="TTADSL";
	}
	
	var reginalName = obj.attr("REGION_NAME");
	var area_code = obj.attr("AREA_CODE");
	var gis_last = obj.attr("GIS8");
	if (gis_last == undefined){
		gis_last = "";
	}

	
	//家庭宽带过来，新模型
	if("Y" == isFamilyWide){
		
		parent.window.addressAdepter.setAddrPopupPageReturnValue(
				{'DEVICE_ID':device_id,
				 'OPEN_TYPE':open_type,
				 'STAND_ADDRESS':reginalName,
				 'AREA_CODE':area_code,
				 'INSTALL_ADDR':reginalName,
				 'FLOOR_AND_ROOM_NUM':gis_last,
				 'ADDRESS_BUILDING_NUM':gis_last,
				});
	 	hidePopup(this);//隐藏弹出框
	}else{
		setPopupReturnValue(this,{'DEVICE_ID':device_id,'OPEN_TYPE':open_type,'STAND_ADDRESS':reginalName,'AREA_CODE':area_code,
			'INSTALL_ADDR':reginalName,'FLOOR_AND_ROOM_NUM':gis_last,'ADDRESS_BUILDING_NUM':gis_last},true);
	}	
		
	$('#addressCol').removeClass('l_col-cur-2');
}

function openBlind(){
	var param = '&villageName='+$('#DETAILGIS').val()+
				'&AUTH_SERIAL_NUMBER='+$('#AUTH_SERIAL_NUMBER').val()+
				'&CUST_NAME='+$('#CUST_NAME').val()+
				'&CONTACT_SN='+$('#CONTACT_SN').val();
	openNav('盲点小区信息', 'pres.pbossres.blindSpot', 'init', param, 'pbossintf/pbossintf');
}

//页面初始化完成执行的方法
$(function(){
	submitEparchy() 
	$("#addressTree").checkBoxAction(function(e, nodeData){
		queryAddressByTree(nodeData);
		return true;
	});
})



