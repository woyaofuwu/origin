function refreshPartAtferAuth(data)
{
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'PlatFamilyManagePart,CustInfoPart', function(data){
		if(data.get('FLAG') == 'true')
		{			
			document.getElementById("QRY_FLAG").value = "QRY";
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}
/** 
 * 对输入的亲情信息进行客户端校验
 */
function checkfmymember()
{  
	if (document.getElementById("QRY_FLAG").value == null || 
		document.getElementById("QRY_FLAG").value == ''){
		alert("请先执行查询操作。");
		return;
	}   
	  
	var serial_number=document.getElementById("AUTH_SERIAL_NUMBER").value;
	
	if (null == serial_number || "" == serial_number){return;}
	var t_role = "";
	tbody = document.getElementById("fmymembers"); 
	var flag = false;            
	for (var i = 0; i < tbody.rows.length; i++) {                 
		var mainmsisdn = tbody.rows[i].cells[2].childNodes[0].value;  
		if (mainmsisdn == serial_number){
			flag = true;
		}
	}
	if (!flag){
		t_role = "户主";
	}
	
	var fmymembers = document.getElementById("fmymembers");   //已有亲情成员
	fmyrows=fmymembers.getElementsByTagName("tr");

	var rowstr=document.createElement("tr");
	var colstr1=document.createElement("td");
	var colstr2=document.createElement("td");
	var colstr3=document.createElement("td");
	var colstr4=document.createElement("td");
	var colstr5=document.createElement("td");
	var colstr6=document.createElement("td");
	
	colstr1.setAttribute("class","e_center");
	colstr2.setAttribute("class","e_center");
	colstr3.setAttribute("class","e_rSet");
	colstr4.setAttribute("class","e_rSet");
	colstr5.setAttribute("class","e_lSet");
	colstr6.setAttribute("class","e_lSet");
	
	colstr1.innerHTML="<input type='checkbox' id='ognl:'checkbox' + rowIndex' name='chkbox' jwcid='@Any' value='newtag' />";
	colstr2.innerHTML ="<span jwcid='@Insert' value='" + (parseInt(fmyrows.length)+1) + "' raw='false' > " + (parseInt(fmyrows.length)+1) + " </span>";     //序号
	colstr3.innerHTML = "<input type='text' jwcid='cond_MAIN_MSISDN@TextField' value='"+ serial_number +"' desc='' class='e_input' datatype='mbphone' disabled = 'true'/>";
	colstr4.innerHTML = "<input type='text' jwcid='cond_TARGET_MSISDN@TextField' value='" +((fmyrows.length == 0 || t_role != '') ? serial_number : '')+ "' desc='' class='e_input' datatype='mbphone' "+((fmyrows.length == 0 || t_role != '' )? "disabled = 'true'" : '')+"/>";
	colstr5.innerHTML = "<input type='text' jwcid='cond_TARGET_NAME@TextField' value='' desc='' class='e_input' datatype='string' />";
	colstr6.innerHTML = "<input type='text' jwcid='cond_TARGET_ROLE@TextField' value='" +((fmyrows.length == 0 || t_role != '') ? '户主' : '非户主')+ "' desc=''	class='e_input' datatype='string' disabled = 'true'/>"; 
	rowstr.appendChild(colstr1);
	rowstr.appendChild(colstr2);
	rowstr.appendChild(colstr3);
	rowstr.appendChild(colstr4);
	rowstr.appendChild(colstr5);
	rowstr.appendChild(colstr6);

	fmymembers.appendChild(rowstr);
	
	refresh();

}

/** 
 * 前台客户端删除亲情信息，并进行客户端JS拼串
 */
function delfmymember()
{  
	if (document.getElementById("QRY_FLAG").value == null || 
		document.getElementById("QRY_FLAG").value == ''){
		alert("请先执行查询操作。");
		return;
	}   
	var fmymembers=document.getElementById("fmymembers");
	var boxObj=document.getElementsByName("chkbox");
	
	for(i=0;i<boxObj.length;i++)
	{ 
		if(boxObj[i].checked)
		{
			var tdObj=boxObj[i].parentNode.parentNode;//单元格，行
			tdObj.parentNode.removeChild(tdObj);//删除整行,删除增加拼串
			i--;		//多行删除时，行数会动态变更，故每删除一行则I减1;
		}
	}
	refresh();
}
/** 
 * 提交前校验
 */
function setConfirmTag()
{
	var fmymembers = document.getElementById("fmymembers");   //已有亲情成员
	fmyrows=fmymembers.getElementsByTagName("tr");
	for(var i=0; i < fmyrows.length ; i++){
		var target_msisdn = document.getElementById("cond_TARGET_MSISDN"+(i==0?"":("$"+(i-1).toString())));
		var msisdn = (null != target_msisdn ? target_msisdn.value : "");
		//手机号码格式校验
		if(""!=msisdn && !verifyField (msisdn))
		{
			return;
		}
	}
}

function refresh(){
	tbody = document.getElementById("fmymembers");             
	for (var i = 0; i < tbody.rows.length; i++) {  
		//alert("INDEX_VALUE--->"+tbody.rows[i].cells[1].value + "INDEX--->"+tbody.rows[i].cells[1].innerText);
		tbody.rows[i].cells[1].innerText = (parseInt(i)+1);
	}
	
	var boxObj=document.getElementsByName("chkbox");
	
	for(var i=0;i<boxObj.length;i++)
	{ 
		if(boxObj[i].checked)
		{
			boxObj[i].checked = false;
		}
	}
}

//保存         
function save() {  
	if (document.getElementById("QRY_FLAG").value == null || 
		document.getElementById("QRY_FLAG").value == ''){
		alert("请先执行查询操作。");
		return;
	}   
	var detailstr = "" ,                 
	tbody = document.getElementById("fmymembers"); 
	var msisdn = "";
	if (null != document.getElementById("AUTH_SERIAL_NUMBER")){    
		msisdn = document.getElementById("AUTH_SERIAL_NUMBER").value;
	}
	var j = 0;            
	for (var i = 0; i < tbody.rows.length; i++) {                 
		var mainmsisdn = tbody.rows[i].cells[2].childNodes[0].value;
		if (mainmsisdn == msisdn){
			j++;
		}                  
		var targetmsisdn = tbody.rows[i].cells[3].childNodes[0].value; 
		if ("" == targetmsisdn || null == targetmsisdn){
			alert("成员号码未录入，请确认。");
			return false;
		}  
		var targetname = tbody.rows[i].cells[4].childNodes[0].value;                 
		var targetrole = tbody.rows[i].cells[5].childNodes[0].value;                 
		var detailstr = detailstr + mainmsisdn + "^" + targetmsisdn + "^" + targetname + "^" + targetrole + ((i != tbody.rows.length -1) ? "|":"");                                                
	}     
	if (j > 6){
		alert("亲情圈群组人数超过限制,一个群组最多为6人(1个户主、5个成员),请修改.");
		return false;
	}                        
	
	var eparchy_code = document.getElementById("USER_EPARCHY_CODE").value;
	var tradeData = document.getElementById("tradeData");
	var tradeDataMap = "";
	if (tradeData)
	{
		tradeDataMap = $.DataMap(tradeData.value);
	}
		
	$.ajax.submit(this, 'onTradeSubmit', "&FAMILY_CIRCLE_LIST="+detailstr+"&USER_EPARCHY_CODE="+eparchy_code+"&SERIAL_NUMBER="+document.getElementById("AUTH_SERIAL_NUMBER").value, 'PlatFamilyManagePart', function(data){
		if (null != data.get('ERR_MSG') && '' != data.get('ERR_MSG'))
		{	
			alert(data.get('ERR_MSG'));
		}else{
			alert("提交成功,"+data.get('SUCCESS_MSG'));
			refresh();
			saveRefresh();		
		}	
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });              
} 

function saveRefresh(){
		var tbody = document.getElementById("fmymembers");
		
		for(var i=0;i<tbody.rows.length;i++)
		{
			//alert(tbody.rows[i].cells[4].childNodes[0].disabled) 
			if(!tbody.rows[i].cells[4].childNodes[0].disabled)
			{
				tbody.rows[i].cells[4].childNodes[0].disabled = "disabled";
			}
			if(!tbody.rows[i].cells[3].childNodes[0].disabled)
			{
				tbody.rows[i].cells[3].childNodes[0].disabled = "disabled";
			}
		}
}
//修改	
function UpdateFamily(){  
	if (document.getElementById("QRY_FLAG").value == null || 
		document.getElementById("QRY_FLAG").value == ''){
		alert("请先执行查询操作。");
		return;
	}   
	var fmymembers=document.getElementById("fmymembers");
	var tbody = document.getElementById("fmymembers");
	var boxObj=document.getElementsByName("chkbox");
	
	for(var i=0;i<boxObj.length;i++)
	{ 
		if(boxObj[i].checked)
		{
			var targetname = tbody.rows[i].cells[4].childNodes[0].value;  
			tbody.rows[i].cells[4].childNodes[0].disabled = "";
		}
	}
	
	refresh();
}
