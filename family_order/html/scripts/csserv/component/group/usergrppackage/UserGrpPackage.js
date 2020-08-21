function renderUserGrpPackage(grpProductId, grpUserEparchyCode,grpUserId){

	var data = '&TAG=1&GRP_PRODUCT_ID='+grpProductId+'&GRP_USER_EPARCHYCODE='+grpUserEparchyCode+'&GRP_USER_ID='+grpUserId;
	$.ajax.submit(null,null,data,$("#USERGRPPACKAGE_COMPONENT_ID").val(),'',function(data){showDetailErrorInfo(error_code,error_info,derror);},{async:false});
}

function packageTreeTextAction(data){
	var nodeid = data.id;
	if(nodeid.indexOf('node_')==0){
		var product_package=nodeid.replace("node_","").split('^');
	
		if(product_package.length != 2) return;
		var productid = product_package[0];
		var packageid = product_package[1];
		var grpUserEparchyCode = $('#USERGRPPACKAGE_EPARCHY_CODE').val();
		$('#USERGRPPACKAGE_PRODUCT_ID').val(productid);
		$('#USERGRPPACKAGE_PACKAGE_ID').val(packageid);
		var params = "&PACKAGE_ID="+packageid+"&PRODUCT_ID="+productid+'&TAG=0'+'&GRP_USER_EPARCHYCODE='+grpUserEparchyCode;
		hhSubmit(null,"com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.usergrppackage.UserGrpPackageHttpHandler","renderUserGrpPackageList", params, function(data){initElementList(data)},'');
		
	}
	
}

function initElementList(data){
	var elementList = data;
	$("#qryGrpPackageList").html('');
	var productId = $('#USERGRPPACKAGE_PRODUCT_ID').val();
	var packageId = $('#USERGRPPACKAGE_PACKAGE_ID').val();
	if(elementList==null||elementList==""||elementList=="undefined"){
		
	}
	else{
		elementList.each(function(item,index,totalCount){
			var elementId = item.get("ELEMENT_ID");
			var elementName = item.get("ELEMENT_NAME");
			var elementType = item.get("ELEMENT_TYPE_CODE");
			var elementIdDetail = productId+'^'+packageId+'^'+elementId+'^'+elementType+'^'+elementName;
			$("#qryGrpPackageList").prepend(makeLiQryGrpPackageHTML(elementId,elementName,elementType,elementIdDetail));
		});
	}
}
		
//废弃
function addUserGrpElements(){
	var grpPackageList = $("#grpPackageCheckPart input:checked");
	grpPackageList.each(function(){
	
		var elementid = $(this).val();
		var elementname = $(this).attr('elementname');
		var elementtype = $(this).attr('elementtype');
		if(!ExistGrpElement(elementid,elementtype)){
			$("#selectedGrpPackageList").prepend(makeSelectedGrpPackaeHTML(elementid,elementname,elementtype));
		}else{
			alert(elementname+'已经存在，无需重复选择！');
		}	
	});
}

function addUserGrpElement(elementId,elementTypeCode,elementName,elementIdDetail){
	if(ExistGrpElement(elementId,elementTypeCode)){
		alert(elementName+'已经存在，无需重复选择！');
		return;
	}	
	
	$("#selectedGrpPackageList").prepend(makeLiSelectedGrpPackaeHTML(elementIdDetail,elementName,elementTypeCode));
	var liEle = 'PELI_'+elementId+'_'+elementTypeCode;
	$('#'+liEle).attr('class','e_dis');
	var labelEle = 'PELABEL_'+elementId+'_'+elementTypeCode;
	
}

function ExistGrpElement(elementid,elementTypeCode){
	var grpPackageList = $("#selectedGrpPackageList input");
	var existelement = false;
	grpPackageList.each(function(){
		var seleStr = $(this).val();
		var seleArray = seleStr.split('^');
		var seleElementId = seleArray[2];
		var seleElementTypeCode = seleArray[3];
		if(seleElementId==elementid && seleElementTypeCode==elementTypeCode){
			existelement=true;
			return false;
		}
	});
	return existelement;
}

function delUserGrpElements(){
	var grpPackageList = $("#selectedGrpPackageList input:checked");
	grpPackageList.each(function(){
		$(this).parent().parent().remove();
	});
}

function delUserGrpElement(delObj){
	var chidren = $("input:first",delObj);
	var chidrenValue = $(chidren).val();
	var elementId = chidrenValue.split('^')[2];
	var elementTypeCode = chidrenValue.split('^')[3];
	var elementname = chidrenValue.split('^')[4];
	
	$(delObj).parent().remove();
	
	var liEle = 'PELI_'+elementId+'_'+elementTypeCode;
	$('#'+liEle).attr('class','');
	
}

//废弃
function makeSelectedGrpPackaeHTML(elementid, elementname, elementtype){
		var html="";
		html += '<tr>';
		html += '<td><input id="ELEMENT_" type="checkbox" value="'+elementid+'" /></td>';
		html += '<td class="wrap">' + elementname+ '</td>';
		html += '<td class="wrap" style="display:none">' + elementtype+ '</td>';
		html += '</tr>';
		
		return html;
}

function makeLiSelectedGrpPackaeHTML(elementDeatail, elementname, elementtype){
		var elementIdStr = elementDeatail.split('^')[2];
		var html="";
		html += '<li title=\''+elementname+'\'>';
		html += '<label  class="text" ondblclick="delUserGrpElement(this);">';
		html += '<input type="text" style="display:none" id="usercheck" name="usercheck"  value=\''+elementDeatail+'\' />';
		html += '<span>['+elementIdStr+']'+elementname+'</span>';
		html += '</label>';
		html += '</li>';
		return html;
}


function makeLiQryGrpPackageHTML(elementid, elementname, elementtype, elementIdDetail){
		
		var html="";
		var className ="";
		var className1 ="";
		var dblclcickAction ="";
		if(ExistGrpElement(elementid,elementtype)){
			className =" class='e_dis' ";
			className1 ="e_dis";
		}
		dblclcickAction =' ondblclick="addUserGrpElement(\''+elementid+'\',\''+elementtype+'\',\''+elementname+'\',\''+elementIdDetail+'\');"';
		var addButton = 'onmouseover="$(this).prev().css(\'display\',\'\');" onmouseout="$(this).prev().css(\'display\',\'none\');"';
		
		html += '<li id="PELI_'+elementid+'_'+elementtype+'"  title=\''+elementname+'\' '+className+' >';
		
		if(className1!="e_dis")
		{
			html +='<button type="button" style="display:none" onmouseover="this.style.display = \'\';" onmouseout="this.style.display = \'none\';" onclick="addUserGrpElement(\''+elementid+'\',\''+elementtype+'\',\''+elementname+'\',\''+elementIdDetail+'\');"><i class="e_ico-next"></i><span></span></button>';
		}
		html += '<label id="PELABEL_'+elementid+'_'+elementtype+'" class="text" '+dblclcickAction +" " + ((className1!="e_dis")?addButton:'') +' ><span>['+elementid+']'+elementname+'</span></label>';
		html += '</li>';
		return html;
}



function commparaGrpPackageElements(){
    var selectedGrpPackageElements =  $.DatasetList();
	var oldselectliststr = $("#OLD_GRPPACKAGE_LIST").val();
	
	if(oldselectliststr==''){
		oldselectliststr='[]';
	}
	var oldselectlist= $.DatasetList(oldselectliststr);
	var grpPackageList = $("#selectedGrpPackageList input");
	//判断新增的元素
	grpPackageList.each(function(){
		var checkvalue = $(this).val();
		var checkvaluelist = checkvalue.split('^');
		var productId = checkvaluelist[0];
		var packageId = checkvaluelist[1];
		var elementid = checkvaluelist[2];
		var elementtype = checkvaluelist[3];
		
		var existSelectEle = false;
		oldselectlist.each(function(item, index, totalcount){
			if(productId==item.get("PRODUCT_ID") && packageId==item.get("PACKAGE_ID") && elementid==item.get("ELEMENT_ID") && elementtype==item.get("ELEMENT_TYPE_CODE")){
				existSelectEle=true;
				return false;
			}
		});
		if(!existSelectEle){
			var elem = $.DataMap();
			elem.put("ELEMENT_ID",elementid);
			elem.put("PRODUCT_ID",productId);
			elem.put("PACKAGE_ID",packageId);
			elem.put("ELEMENT_TYPE_CODE",elementtype);
			elem.put("MODIFY_TAG","0");
			selectedGrpPackageElements.add(elem);
		}
		
	});
	
	
	//判断新增的元素
	oldselectlist.each(function(item, index, totalcount){
	
		var existSelectEle = false;
		grpPackageList.each(function(){
			var checkvalue = $(this).val();
			var checkvaluelist = checkvalue.split('^');
			var productId = checkvaluelist[0];
			var packageId = checkvaluelist[1];
			var elementid = checkvaluelist[2];
			var elementtype = checkvaluelist[3];
			if(productId==item.get("PRODUCT_ID") && packageId==item.get("PACKAGE_ID") && elementid==item.get("ELEMENT_ID") && elementtype==item.get("ELEMENT_TYPE_CODE")){
				existSelectEle=true;
				return false;
			}
		});
		if(!existSelectEle){
			var elem = $.DataMap();
			elem.put("ELEMENT_ID",item.get("ELEMENT_ID"));
			elem.put("PRODUCT_ID",item.get("PRODUCT_ID"));
			elem.put("PACKAGE_ID",item.get("PACKAGE_ID"));
			elem.put("ELEMENT_TYPE_CODE",item.get("ELEMENT_TYPE_CODE"));
			elem.put("MODIFY_TAG","1");
			selectedGrpPackageElements.add(elem);
		}
		
	});
	
	$("#SELECTED_GRPPACKAGE_LIST").val(selectedGrpPackageElements);
	
}

//判断是否必选包已经选上
function checkUserGrpPkgForce(){
	var forcePackageStr = $("#FORCE_PACKAGE_LIST").val();
	var forcePkgList= $.DatasetList(forcePackageStr);
	
	var grpPackageList = $("#selectedGrpPackageList input");
	var result =true;
	//判断新增的元素
	forcePkgList.each(function(item, index, totalcount){
	
		var existForcePkg = false;
		
		grpPackageList.each(function(){
			var checkvalue = $(this).val();
			var checkvaluelist = checkvalue.split('^');
			var packageId = checkvaluelist[1];
			if( packageId==item.get("PACKAGE_ID")){
				existForcePkg=true;
				return false;
			}
		});
		if(!existForcePkg){
			alert("集团定制信息中包"+item.get("PACKAGE_NAME")+"是必选包，必须选择包下的至少一个元素");
			result = false;
			return false;
		}
		
	});
	
	return result;
	
}