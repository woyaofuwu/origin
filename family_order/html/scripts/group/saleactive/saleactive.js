
//集团资料查询成功后调用的方法
function getGroupInfo(data){
	if(data == null)
	  return;
	 
  	var grpUserInfo = data.get("GRP_USER_INFO");
  	var grpCustInfo = data.get("GRP_CUST_INFO");
  	var grpAcctInfo = data.get("GRP_ACCT_INFO");
  	$('#CUST_NAME').html(grpCustInfo.get('CUST_NAME'));  	
  	$('#BRAND_NAME').html(grpUserInfo.get('BRAND_NAME'));
  	$('#PRODUCT_NAME').html(grpUserInfo.get('PRODUCT_NAME'));
  	$('#CLASS_NAME').html(grpCustInfo.get('CLASS_NAME'));
  	$('#OPEN_DATE').html(grpUserInfo.get('OPEN_DATE'));
  	$('#CUST_ID').val(grpCustInfo.get('CUST_ID'));
  	$('#USER_ID').val(grpUserInfo.get('USER_ID'));
  	$('#GRP_SERIAL_NUMBER').val(grpUserInfo.get('SERIAL_NUMBER'));
}

function changeCampnType(){
    var campnType = $("#CAMPN_TYPE").val();  //成员号码   
    if(campnType == ''){
       alert("活动类型不能为空");
       return false;
    }    
    //查询营销活动方案
	$.beginPageLoading();
	$.ajax.submit(this, 'queryCampnNames', '&CAMPN_TYPE='+campnType, 'ProductPart', function(data){	
		$.endPageLoading();			
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function changeCampn(){
	
	// 校验营销活动产品是否可选
	var campnType = $("#CAMPN_TYPE").val();  //成员号码   
	$.beginPageLoading();
	$.ajax.submit(this, 'checkByProduct', '&CAMPN_TYPE='+campnType, '', function(data){	
		$.endPageLoading();
		var resultcode = data.get('FLAG',true);
		var message = data.get('ERROR_MESSAGE','0');
   		if(resultcode=='false'){   	
   			$.showWarnMessage("系统提示",message);
   		}		
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
	queryPackages();
}

function queryPackages(){
	
	groupPackageTree.empty(true);
	groupPackageTree.setParam('MAIN_PRODUCT_ID',$('#SALE_PRODUCT_ID').val());
	groupPackageTree.setParam('METHOD_NAME','loadGroupProductPackageTree');
	groupPackageTree.init();
}

function querySalePackage(data){
	
	var nodeid = data.id;
	if(nodeid.indexOf('node_')==0){
	var product_package=nodeid.replace("node_","").split('^');

	if(product_package.length != 2) return;
	var productid = product_package[0];
	var packageid = product_package[1];
	$('#PACKAGE_ID').val(packageid);
	var campnType = $("#CAMPN_TYPE").val();
	var grpUserId = $("#USER_ID").val();
	var campnType = $("#CAMPN_TYPE").val();
	var params = "&PACKAGE_ID="+packageid+"&PRODUCT_ID="+productid+"&GRP_USER_ID="+grpUserId+"&CAMPN_TYPE="+campnType;
    //查询营销包信息
	$.beginPageLoading();
	$.ajax.submit(this, 'querySalePackage', params , 'ContractStartDatePart', function(data){	
		$.endPageLoading();			
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	}
}

