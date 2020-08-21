function setAreaCode(obj){  
//	var areaCode = obj.value;
//	$.Search.get("addressSearch").setParams("&AREA_CODE="+areaCode);
}

function ajaxSubmitQry(areaids,listener,params,partids){
	//清空列表
	ajaxSubmit(null,null,null,'editPart',null,null,null);
	$.beginPageLoading('数据正在提交中....');
	$.ajax.submit(areaids,listener,params,partids,function(data){
		$.endPageLoading();
		
		/*BUG20200426103836  优化H5地址问题  
		 *根据手机高度调整界面高度
		 */
		
		if($.os.phone && $(document.body).height() < 560)
		{
			$("#mytable").height($(document.body).height()
					-$("#QueryCond").outerHeight()
					-$(".c_header").outerHeight()-75);
		}
		
	});
}

function ajaxSubmitQryForCorr(areaids,listener,params,partids){
	$.beginPageLoading('数据正在提交中....');
	$.ajax.submit(areaids,listener,params,partids,function(data){
		$.endPageLoading();
	});
}

function  ctrlSearchDis(obj){
	
	if(obj.value=="0"){
		
		getElement("SP_TR").style.display="";
		getElement("NAME_TR").style.display="none";
		getElement("CITY_TR").style.display="none";

		getElement("cond_REGION_SP").nullable="no";
		getElement("cond_REGION_NAME").nullable="yes";
		document.getElementById("cond_REGION_NAME").value = "" ;

		document.getElementById("cond_AREA_CODE1").value = "" ;
	}else{
		
		getElement("SP_TR").style.display="none";
		getElement("NAME_TR").style.display="";
		getElement("CITY_TR").style.display="";

		getElement("cond_REGION_SP").nullable="yes";
		document.getElementById("cond_REGION_SP").value = "" ; 
		getElement("cond_REGION_NAME").nullable="no";
		
		document.getElementById("cond_AREA_CODE1").value = "" ;
	}
	
}

function fn_MouseOver(disObj,fontObj){
	if(disObj){
		disObj.style.cursor="hand";
	}
	if(fontObj){
		fontObj.color="red";
	}
}

function fn_MouseOut(disObj,fontObj){
	if(disObj){
		disObj.style.cursor="";
	}
	
	if(fontObj){
		fontObj.color="";
	}
}

function setRowDateEdit(o){
	var obj = $(o);
	obj.siblings().removeClass();
	var region_name = encodeURIComponent(obj.attr("REGION_NAME"));
	var device_id = obj.attr("DEVICE_ID");
	//设置此节点class为class="on"
	obj.addClass("on");
	ajaxSubmit(this,'queryDeviceByReginal','&REGION_NAME='+region_name+'&DEVICE_ID='+device_id,'editPart',null,null,null);
	$('#addressCol').addClass('l_col-cur-2');
}

function setEditPartDate(o){
	var obj = $(o);
	obj.siblings().removeClass();
	var region_name = encodeURIComponent(obj.attr("REGION_NAME"));
	var device_id = obj.attr("DEVICE_ID");
	//设置此节点class为class="on"
	obj.addClass("on");
	ajaxSubmit(this,'queryDeviceByReginal','&REGION_NAME='+region_name+'&DEVICE_ID='+device_id,'editPart',null,null,null);
	showPopup('topSetBoxInfopopup','topSetBoxInfopopup_item',true);
}

function areaTreeTextAction(nodedata){
	var regid = nodedata.id;
	var epaCode = nodedata.value;
	if(nodedata['haschild']=="true"){
		MessageBox.alert("提示","请选择最小子节点后双击选中！");
	}else{
		setTreeDateEdit(regid,epaCode);
	}
}

function setTreeDateEdit(regionId,eparchyCode){	
	ajaxSubmit(this,'queryDeviceByReginal','&REGION_ID='+regionId+'&TRADE_EPARCHY_CODE='+eparchyCode,'editPart',null,null,null);
}

//modify by duhj
function confirmReginalDate(o){
	var isFamilyWide = $("#IS_FAMILY_WIDE").val();
	var obj = $(o);
	var portNum = obj.attr("CAPACITY_CANUSED");
	if(portNum==""||portNum==0||portNum<0){
		
		MessageBox.alert("提示","所选设备无可用端口！");
        return;
		
		
//		popupPage('broadband.wideband.WidePreReg',null,null,'五级地址名称','680','600','fiveLevelAddress',null,'/personserv/personserv');

        
		//检查port_num为0，是否会进入此方法
		//此时调转到CRM的页面，传递5级地址及全称
		//broadband.wideband.WidePreReg
//		var gis1 = obj.GIS;
//		var gis2 = obj.GIS1;
//		var gis3 = obj.GIS2;
//		var gis4 = obj.GIS3;
//		var gis5 = obj.GIS4;
//		var detail_gis = obj.REGION_NAME;
//		var param = '&ADDR_NAME1='+gis1+'&ADDR_NAME2='+gis2+'&ADDR_NAME3='+gis3+'&ADDR_NAME4='+gis4+'&ADDR_NAME5='+gis5+'&ADDR_NAME='+detail_gis;
//		popupPage('broadband.wideband.WidePreReg','initPage',param,'五级地址名称','680','600','fiveLevelAddress',null,'/personserv/personserv');
//		return;
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
//	setReturnValue({'DEVICE_ID':device_id,'OPEN_TYPE':open_type,'STAND_ADDRESS':reginalName,'AREA_CODE':area_code,'INSTALL_ADDR':reginalName,'FLOOR_AND_ROOM_NUM':gis_last,'ADDRESS_BUILDING_NUM':gis_last},true);
		setPopupReturnValue(this, {'DEVICE_ID':device_id,'OPEN_TYPE':open_type,'STAND_ADDRESS':reginalName,'AREA_CODE':area_code,'INSTALL_ADDR':reginalName,'FLOOR_AND_ROOM_NUM':gis_last,'ADDRESS_BUILDING_NUM':gis_last})
	}
	
	$('#addressCol').removeClass('l_col-cur-2');
}

function queryAddressManager(){
	var area_code = $("#cond_AREA_CODE1").val();
	var region_sp = $("#cond_REGION_SP").val();
	rm.ajaxSubmitQry('CondPartSeq','queryReginalsAll','&REGION_SP='+region_sp+'&AREA_CODE='+area_code,'DataTablePart');
}

//地址纠错
function editAddress(obj){
	if(!$.validate.verifyAll(obj)) {
		return false;
	}
	if($('#AddrNameOld').val() == $('#AddrNameNew').val()) {
		MessageBox.alert("提示","旧标准地址名称与新标准地址名称相同！");
		return false;
	}
	rm.ajaxSubmitMsg(obj, 'addressErrCor', null, null);
}

function confirmCorrectData(obj){
	var device_id = obj.DEVICE_ID;
	var reginalName = obj.REGION_NAME;
	setReturnValue({'DEVICE_ID':device_id,'origAddress':reginalName,'newAddress':reginalName},true);

}

function openBlind(){
	var param = '&villageName='+encodeURIComponent($('#addressSearch').val())+
				'&AUTH_SERIAL_NUMBER='+encodeURIComponent($('#AUTH_SERIAL_NUMBER').val())+
				'&CUST_NAME='+encodeURIComponent($('#CUST_NAME').val())+
				'&CONTACT_SN='+encodeURIComponent($('#CONTACT_SN').val());
	openNav('盲点小区信息', 'pres.pbossres.blindSpot', 'init', param, "/pbossintf/pbossintf");
}
function openAddressPredeal(){
	var param = '&AUTH_SERIAL_NUMBER='+encodeURIComponent($('#AUTH_SERIAL_NUMBER').val());
	openNav('预受理地址', 'pres.pbossres.addrPreDeal', 'init', param, "/pbossintf/pbossintf");
}

//树状地址查询
function addressTreeSelect(){	
	openNav('树状地址查询','res.popup.AddressQryTreeSearchNew','init','',"/order/iorder");
}

function addAddrPredeal(o){
	var obj = $(o);
	if($('#AUTH_SERIAL_NUMBER').val()==''){
		MessageBox.alert("提示",'请先输入手机号查询信息');
		return;
	}
	if(obj.attr('CAPACITY_CANUSED')>0){
		MessageBox.alert("提示",'不能受理，还有可用端口');
		return;
	}
	
	var param="&REGION_NAME=" + encodeURIComponent(obj.attr('REGION_NAME'))
 	+"&DEVICE_ID=" + obj.attr('DEVICE_ID')
 	+"&SERIAL_NUMBER="+ $('#AUTH_SERIAL_NUMBER').val()
 	+"&AREA_CODE="+ obj.attr('AREA_CODE')
 	+"&DEVICE_NAME="+ encodeURIComponent(obj.attr('DEVICE_NAME'));
	
	$.beginPageLoading("数据提交中..");
	$.ajax.submit('', 'addAddrPredeal', param, '', function(data){
		$.endPageLoading();
		MessageBox.alert("提示",data.get("RESULT"));
		if(data.get("RESULT")=="添加成功！"){
			hidePopup(o);
		}
	},	
	function(error_code,error_info){
		$.endPageLoading();
		MessageBox.alert("提示",error_info);
    });
	

}


