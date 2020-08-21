var checkedProvinceList = new Wade.DatasetList();
var agentList = new Wade.DatasetList();
//js页面初始化方法
function initPageParam()
{
	$(".top").attr("hidden","true");
	window["AgentTable"] = new Wade.Table("AgentTable", {
		fixedMode:true,
		editMode:true,
	});
	
	$("#AgentTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(AgentTable.getSelectedRowData());
	});
	//保存代理商表格数据
	$.each(agentList, function(index, data) {
		AgentTable.addRow($.parseJSON(data.toString()));
	});
	//判断产品变更的时候集团规范属性是否可写
	var if_vpn_scare_code = $("#IF_VPN_SCARE_CODE").val();
	if(if_vpn_scare_code == "no"){
		$("#pam_VPN_SCARE_CODE").attr("disabled", true);
	}
	//省份表格内容初始化
	var provinceInfo = $("#provinceInfo").text();
	//将provinceInfo转成list类型
	var datalist = $.DatasetList(provinceInfo);
	//将数据放入省份表格中
	datalist.each(function(item){
		var checkBox = "<input type=\"checkbox\" id=\"itemcodes\" name=\"itemcodes\" value=\"ognl:condition.NOTE_ITEM_CODE\" jwcid=\"@Checkbox\" chk=\"ognl:condition.TAG\"  />"
		item.put("itemcodes",checkBox);
		ProvinceInfoTable.addRow($.parseJSON(item.toString()));
	});
}


function changeVpnScareTag(){
	var tag1 = $("#IF_PROV_VPN") ;
	var tag5 = $("#IF_PROV_ITEM");
	var provGroupID = $("#pam_PROV_GROUPID");
	var vpn_scare_code = $("#pam_VPN_SCARE_CODE").val();
	if(vpn_scare_code== '2'){
		tag1.css("display","");
		tag5.css("display","");
		provGroupID.attr("nullable", "no");

	}else{
		tag1.css("display","none");
		tag5.css("display","none");
		provGroupID.attr("nullable", "yes");
	}
	showTag();
}

function showTag()
{
	var checkboxs = $("input[name=itemcodes]");
	for(var i = 0; i < checkboxs.length; i++) 
	{
		if(checkboxs[i].getAttribute('chk') == "1") 
		{
			checkboxs[i].checked = true;
		}			
	}
}

function checkProvInfo() {

	var provInfo = $("#pam_VPN_SCARE_CODE").val();
	if(provInfo=="2"){
	
		var s = document.getElementsByName("itemcodes");
		var s2 = "";
		for( var i = 0; i < s.length; i++ )	{
			if ( s[i].checked ){
				s2 += s[i].value+'|';
			}
		}

		if(s2.length<1){
			MessageBox.alert("","请选择省代码！");
			return "false";
		}
	}
}

function showAgent(){
	$("#AGENT").css("display","");
}

function queryAgent()
{
	var departId = $("#pam_DEPART_ID").val();
	var param = "&DEPART_ID=" + departId ;
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.param.ProxyParam", "queryAgent", param,afterValidateShortNum);
	
}

function afterValidateShortNum (data) {
	var list =  AgentTable.getData(true,'DEPART_CODE');
	var flag = 0;
	list.each(function(item,index,totalcount){
		if($("#pam_DEPART_ID").val()==list.get(index,'DEPART_CODE')){
			flag = 1;
		}
	});
	if(flag == 0){
		AgentTable.addRow($.parseJSON(data.get(0).toString()));
	}else{
		$.validate.alerter.one($("#pam_DEPART_ID")[0], "关键字段\"编码\"已经存在相同的记录\"");

	}
}

function tableRowClick(data) {
	 $("#pam_AGENT_DEPART_ID").val(data.get("PARA_CODE2"));
}

function checkSub(obj){
	$("#pam_PROV_GROUPID").attr("nullable","yes");
	checkedProvinceList = null;
	checkedProvinceList = ProvinceInfoTable.getRowData(ProvinceInfoTable.checked);
	
	agentList = null;
	agentList = AgentTable.getData();
	
	$("#pam_Hidden").val(agentList.toString());
	
	submitOfferCha();
	var result = submitOfferCha();
	if(result==true){
		backPopup(obj);
	}
	try {
		//backPopup(obj);
	} catch (msg) {
			$.error(msg.message);
	}

}

