
/* auth查询后自定义查询 */
function refreshPartAtferAuth(data)
{
	var param = "&ROUTE_EPARCHY_CODE="+data.get("USER_INFO").get("EPARCHY_CODE")+"&USER_ID="+data.get("USER_INFO").get("USER_ID")
	+"&ACCT_ID="+data.get("ACCT_INFO").get("ACCT_ID");

	$.ajax.submit(this, 'loadChildInfo', param, 'userDiscntList,chooseDiscntPart', function(data)
	{
	    $("#SPEC_DEL_LIMIT").val(data.get(0).get("SPEC_DEL_LIMIT"));
		$("#SPEC_HAVE_DEPOSIT").val(data.get(0).get("SPEC_HAVE_DEPOSIT"));
	    $("#CURRENT_TIME").val(data.get(0).get("CURRENT_TIME"));
	    $("#FIRST_TIME_NEXT_MONTH").val(data.get(0).get("FIRST_TIME_NEXT_MONTH"));
	    $("#LAST_TIME_CURRENT_MONTH").val(data.get(0).get("LAST_TIME_CURRENT_MONTH"));
	    $("#REMAIN_DAYS").val(data.get(0).get("REMAIN_DAYS"));
	    $("#SPEC_DEFER_MONTHS").val(data.get(0).get("MONTHS"));
	    dealUserYetDiscnt();
	    dealCheckBoxDisabled();
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code,error_info);
    });
}

function getSpecDiscnt(){
  
   $.beginPageLoading("资费查询中。。。");
   var param="&DISCNT_CODE="+$("#CHOOSE_DISCNT").val();
   $.ajax.submit(this, 'getSpecDiscnt', param, null, function(data)
	{
         $("#ENABLE_TAG").val(data.get(0).get("ENABLE_TAG"));
         $("#DEPOSIT_CODE").val(data.get(0).get("RSRV_STR3"));
         $("#DISCNT_FEE").val(data.get(0).get("RSRV_STR2"));
         $("#DAY_PRICE").val(data.get(0).get("RSRV_STR4"));
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code,error_info);
    });
}
function addDiscntCheck()
{

    var backTag= false;
    var existsDiscntCode ="";
    var chooseDiscntCode = $("#CHOOSE_DISCNT").val();
    if(chooseDiscntCode == '' || chooseDiscntCode == null){
        alert("请选择需要新增的宽带校园网套餐！");
        return false;
    }
    var discntTable = $.table.get("userDiscntTable").getTableData(null, true);
    discntTable.each(function(item,index,totalCount){
        existsDiscntCode=existsDiscntCode+item.get("DISCNT_CODE")+"";
		if(item.get("DISCNT_CODE") == chooseDiscntCode){
            backTag=true ;
            return false;
		}
	});
	if(backTag){
	  alert("用户已存在尚未失效的宽带校园网套餐，不允许重复办理！");
	  $("#CHOOSE_DISCNT").val("");
	  return false;
	}
  

	var param = "&SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val()+"&NEW_DISCNT="+chooseDiscntCode+"&EXISTS_DISCNT="+existsDiscntCode;
	 $.ajax.submit(this, 'checkDiscnt', param, null, function(data)
	{
	     if(data.get(0).get("CODE") == '0')
	    {
	        addDiscnt();
	    }else{
	        alert('所选择的宽带校园网套餐与客户现有套餐互斥！');
	        $("#CHOOSE_DISCNT").val("");
	        return false;
	    }
	    
		$.endPageLoading();
	},
	function(error_code,error_info)
	{
		$.endPageLoading();
		$.MessageBox.error(error_code,error_info);
    });
}

function addDiscnt(){
    //读取新增套餐对应存折的已有余额
    var depositInfo =$("#SPEC_HAVE_DEPOSIT").val().split(",");
    var money = "0";
    for(var j=0; j<depositInfo.length; j++){
        if($("#DEPOSIT_CODE").val() == depositInfo[j].split("_")[0]){
            money = depositInfo[j].split("_")[1];
            break;
        }
    }
    var enable_tag= $("#ENABLE_TAG").val();
    if (enable_tag == '3' || enable_tag == '4'){//申请时下帐期生效
        var startDate = $("#FIRST_TIME_NEXT_MONTH").val();
    }
    else if (enable_tag == '1' || enable_tag == '2'){//申请时立即生效
        var startDate = $("#CURRENT_TIME").val();
    }
    else{
        alert("您选择的宽带校园网套餐目前无效！");
        return false;
    }
    
  	var discntEdit = new Array();
  	var str=new Array();
  	discntEdit["CHECK"]="<input type='checkbox' id='chkbox' name='chkbox' value='"+$("#CHOOSE_DISCNT").val()+"'/>";
  	discntEdit["DISCNT_CODE"]= $("#CHOOSE_DISCNT").val();
	discntEdit["DISCNT_NAME"]=$("#CHOOSE_DISCNT option:selected").text();
    discntEdit["START_DATE"] = startDate;
	discntEdit["END_DATE"] = startDate;
	discntEdit["DEPOSIT_ABLANCE"] =toYuan(money);
   
	var deferMonths=$("#SPEC_DEFER_MONTHS").val().split(",");

	str.push('<select id="DEFER_MONTHS" name="DEFER_MONTHS" onchange=updateDiscntInfo(this)>');
	//str.push('<option id="">--请选择--</option>');
	for(var i=0; i<deferMonths.length; i++){
	  ss="<option id=op"+i+">"+deferMonths[i]+"</option>"
	  str.push(ss);
	}
	str.push('</select>');
	discntEdit["DEFER_MONTHS"] =str.join("");
	discntEdit["ENABLE_TAG"]= enable_tag;
	discntEdit["INST_ID"]= "0";
	discntEdit["DEFER_TAG"]="A";
	discntEdit["DISCNT_FEE"]=$("#DISCNT_FEE").val();
	discntEdit["DEPOSIT_CODE"]=$("#DEPOSIT_CODE").val();
	discntEdit["DAY_PRICE"]=$("#DAY_PRICE").val();
	discntEdit["DEFINE_MONTHS"]=$("#DEFINE_MONTHS").val();
    $.table.get("userDiscntTable").addRow(discntEdit);
    var i=$("#userDiscntTable").attr("selected");

    if(i != -1){

           $("#userDiscntTable tr:eq("+(i+1)+")").children("td:nth-child(5)").attr("preEndDate", startDate);
           $("#userDiscntTable tr:eq("+(i+1)+")").children("td:nth-child(6)").attr("preMoney", money);
           $("#userDiscntTable tr:eq("+(i+1)+")").children("td:nth-child(6)").attr("addMoney", "0");
         
        }else{
		    $("#userDiscntTable_Body tr:last").children("td:nth-child(5)").attr("preEndDate", startDate);
		    $("#userDiscntTable_Body tr:last").children("td:nth-child(6)").attr("preMoney", money);
		    $("#userDiscntTable_Body tr:last").children("td:nth-child(6)").attr("addMoney", "0");
    }

}

function dealUserYetDiscnt(){
   var depositInfo =$("#SPEC_HAVE_DEPOSIT").val().split(",");
   
  var discntTable = $("#userDiscntTable_Body").find("tr");
      discntTable.each(function(){    
        var money = "0";
	    for(var j=0; j<depositInfo.length; j++){
	      if($(this).children("td:nth-child(12)").text()==depositInfo[j].split("_")[0]){
	            money = depositInfo[j].split("_")[1];
	            $(this).children("td:nth-child(6)").text(toYuan(money));
	            break;
	        }
	    }
	    if( $(this).children("td:nth-child(6)").text()==''){
	       $(this).children("td:nth-child(6)").text(toYuan(money));
	    }
        $(this).children("td:nth-child(5)").attr("preEndDate", $(this).children("td:nth-child(5)").text());
        $(this).children("td:nth-child(6)").attr("preMoney", money);
        $(this).children("td:nth-child(6)").attr("addMoney", "0");
        $(this).children("td:nth-child(10)").text("B");
       
      });
}
function dealCheckBoxDisabled(){
    var imutex=new Array();
    var boxObj = $("#userDiscntTable_Body").find(":checkbox");
       //根据存折编码判断，如果有一个以上同校的不同套餐
    for(var j=0; j<boxObj.length; j++){
        for(var k=j+1; k<boxObj.length; k++){
               if($(boxObj[k].parentNode.parentNode).children("td:nth-child(12)").text()== $(boxObj[j].parentNode.parentNode).children("td:nth-child(12)").text()){
                imutex.push($(boxObj[k].parentNode.parentNode).children("td:nth-child(12)").text());
               }
            }
      }
   
    var discntTable = $("#userDiscntTable_Body").find("tr");
      discntTable.each(function(){

        if($(this).children("td:nth-child(10)").text() == "A"){
            //本次新增的套餐可以勾选
            return true;
        }

        if(isNotInDelLimit($(this).find("input[type=checkbox]").val())){
            //营业员没有删除权限
          $(this).find("input[type=checkbox]").attr("checked", false);
          $(this).find("input[type=checkbox]").attr("disabled", true);
          $(this).find("input[type=checkbox]").css("display", "none");
          $("#operationTip").css("display", "");
          $("#operationTip").text("营业员没有退订【"+ $(this).children("td:nth-child(3)").text()+"】的权限！");
        }
        if($(this).children("td:nth-child(5)").text()==$("#LAST_TIME_CURRENT_MONTH").val()){
        
          //优惠结束时间等于月末时间
          $(this).find("input[type=checkbox]").attr("checked", false);
          $(this).find("input[type=checkbox]").attr("disabled", true);
          $(this).find("input[type=checkbox]").css("display", "none");
          //设置业务解释
          $("#operationTip").css("display", "");
          $("#operationTip").text("用户存在本月到期的宽带套餐 ( 结束时间在本月底 ) ，暂不允许办理退订，套餐本月末会自动截止！")
        }

        if($(this).children("td:nth-child(4)").text()>$("#CURRENT_TIME").val()){      
         //优惠的开始时间大于当前时间
          $(this).find("input[type=checkbox]").attr("checked", false);
          $(this).find("input[type=checkbox]").attr("disabled", true);
          $(this).find("input[type=checkbox]").css("display", "none");
          //设置业务解释
          $("#operationTip").css("display", "");
          $("#operationTip").text("用户存在尚未生效的宽带套餐 ( 开始时间大于当前时间 ) ，暂不允许办理退订，套餐生效后才能办理退订！")
        }

        
        for(var i=0; i<imutex.length; i++){
          if($(this).children("td:nth-child(12)").html()==imutex[i]){
             if($(this).children("td:nth-child(5)").text()==$("#LAST_TIME_CURRENT_MONTH").val()){
               $(this).children("td:nth-child(7)").css("display", "none");
             }
          }
        }
        
      });
       //选中第一个未禁用的checkBox  
    discntTable.each(function(){  
    	if(!$(this).find("input[type=checkbox]").attr("disabled")){
    		$(this).find("input[type=checkbox]").attr("checked", true);
    		return false;
    	}

	});
}

function updateDiscntInfo(obj){

 	var remainDays = parseInt($("#REMAIN_DAYS").val()); //本月剩余天数(含当天)
	var deferMonths = parseInt($(obj).val()); //延期月数
	var discntEndDate = $(obj.parentNode.parentNode).children("td:nth-child(5)").text();
    var discntMoney  = $(obj.parentNode.parentNode).children("td:nth-child(6)").text();
	var discntEnableTag = $(obj.parentNode.parentNode).children("td:nth-child(8)").text();
	var discntDeferTag = $(obj.parentNode.parentNode).children("td:nth-child(10)").text();
	var discntFee = parseInt($(obj.parentNode.parentNode).children("td:nth-child(11)").text());
	var discntPayID = $(obj.parentNode.parentNode).children("td:nth-child(12)").text();
	var discntDayFee = parseInt($(obj.parentNode.parentNode).children("td:nth-child(13)").text()); //首月按日费用单价
	var discntDefineMonths = $(obj.parentNode.parentNode).children("td:nth-child(14)").text(); //包月标志
	var endDate = $("#LAST_TIME_CURRENT_MONTH").val();
	var preEndDate = $(obj.parentNode.parentNode).children("td:nth-child(5)").attr("preEndDate");
	var preMoney = parseInt($(obj.parentNode.parentNode).children("td:nth-child(6)").attr("preMoney")); //存折余额
	var addMoney = parseInt($(obj.parentNode.parentNode).children("td:nth-child(6)").attr("addMoney")); //新增费用
	var money = 0; //本次办理所需费用
	//新增的优惠
    if(discntDeferTag == "A"){
          endDate = dateAdd(preEndDate, deferMonths-1);
          if(discntDefineMonths == "0"){
          //非包月优惠当月按日收取，之后按月收取
			var money0 = discntDayFee * remainDays;   //当月按日收取费用(分)
			var money1 = discntFee * (deferMonths-1); //之后按月收取费用(分)
			money0 = money0 > discntFee ? discntFee : money0 ;
            money = money0 + money1;
          }	else{
			//包月优惠按月收取(含当月)(分)
			money = discntFee * deferMonths;
		    }
		 
			if(deferMonths !="0"){
			
			$.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2",discntPayID);
	        var feeData = $.DataMap();
          	feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", discntPayID);
			feeData.put("FEE",  money);
			feeData.put("PAY",  money);		
			feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());

			$.feeMgr.insertFee(feeData);	
			
			$(obj.parentNode.parentNode).children("td:nth-child(6)").empty();	
			$.insertHtml('beforeend',$(obj.parentNode.parentNode).children("td:nth-child(6)") ,toYuan(preMoney)+"<span class='e_red'> + "+toYuan(money)+"</span>");
			$(obj.parentNode.parentNode).children("td:nth-child(5)").text(endDate);
		//	$(obj.parentNode.parentNode).children("td:nth-child(6)").text(toYuan(money.toString())+toYuan(preMoney.toString()));
			$(obj.parentNode.parentNode).children("td:nth-child(6)").attr("addMoney",money); 
			
			}else{
			
				var feeData = $.DataMap();
	          	feeData.clear();
				feeData.put("MODE", "2");
				feeData.put("CODE", discntPayID);
				feeData.put("FEE",  addMoney);
				feeData.put("PAY",  addMoney);		
				feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
				
				$.feeMgr.deleteFee(feeData);	
			
				$(obj.parentNode.parentNode).children("td:nth-child(5)").text(preEndDate);
			    $(obj.parentNode.parentNode).children("td:nth-child(6)").text(toYuan(preMoney));
			    $(obj.parentNode.parentNode).children("td:nth-child(6)").attr("addMoney","0");
			}
    }
    //可延期的优惠
    else if(discntDeferTag == "B"){
        money = discntFee * deferMonths;
		endDate = dateAdd(preEndDate, deferMonths);
		if(deferMonths !="0"){
		    $.feeMgr.removeFee($("#TRADE_TYPE_CODE").val(),"2",discntPayID);
	        var feeData = $.DataMap();
          	feeData.clear();
			feeData.put("MODE", "2");
			feeData.put("CODE", discntPayID);
			feeData.put("FEE",  money);
			feeData.put("PAY",  money);		
			feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
			
			$.feeMgr.insertFee(feeData);
			
			$(obj.parentNode.parentNode).children("td:nth-child(6)").empty();	
			$.insertHtml('beforeend',$(obj.parentNode.parentNode).children("td:nth-child(6)") ,toYuan(preMoney)+"<span class='e_red'> + "+toYuan(money)+"</span>");
			$(obj.parentNode.parentNode).children("td:nth-child(5)").text(endDate);
		//	$(obj.parentNode.parentNode).children("td:nth-child(6)").text(toYuan(money)+toYuan(preMoney));
			$(obj.parentNode.parentNode).children("td:nth-child(6)").attr("addMoney",money); 
	   }else{
	            var feeData = $.DataMap();
	          	feeData.clear();
				feeData.put("MODE", "2");
				feeData.put("CODE", discntPayID);
				feeData.put("FEE",  addMoney);
				feeData.put("PAY",  addMoney);		
				feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
				
				$.feeMgr.deleteFee(feeData);	
				$(obj.parentNode.parentNode).children("td:nth-child(5)").text(preEndDate);
			    $(obj.parentNode.parentNode).children("td:nth-child(6)").text(toYuan(preMoney));
			    $(obj.parentNode.parentNode).children("td:nth-child(6)").attr("addMoney","0");
	   
	   }
    }
    //不可延期的优惠
    else if(discntDeferTag == "C"){
    
    }
    
}

function delDiscntCheck(){

       var discntsDel = $("#SPEC_DEL_DISCNT").val();
	    var delDiscntFee = $("#SPEC_DEL_DISCNT_FEE").val();
	    var delDiscntPayId = $("#SPEC_DEL_DISCNT_PAYMENTID").val();
	    
	    if(!$("input[name=chkbox]:checked") || $("input[name=chkbox]:checked").length == 0){
	        alert("请选择需要删除的宽带校园网套餐！");
	        return false;
	    }
	    if($("input[name=chkbox]:checked").length > 1){
	        alert("一次只能删除一个宽带校园网套餐！");
	        return false;
	    }
	    	    
	    var thisMonthEnd = $("#LAST_TIME_CURRENT_MONTH").val();
	    var nextMonthStart = $("#FIRST_TIME_NEXT_MONTH").val();
	    var currentTime = $("#CURRENT_TIME").val();
		var remainDays = parseInt($("#REMAIN_DAYS").val()); //本月剩余天数(含当天)
	    var boxObj = $("#userDiscntTable_Body").find(":checkbox");
	    for(var i=0; i<boxObj.length; i++){
	       if($(boxObj[i]).attr("checked")){

	        var discntCode = $(boxObj[i].parentNode.parentNode).children("td:nth-child(2)").text();
            var discntStartDate = $(boxObj[i].parentNode.parentNode).children("td:nth-child(4)").text();
            var enable_tag =  $(boxObj[i].parentNode.parentNode).children("td:nth-child(8)").text();
            var discntInstId =  $(boxObj[i].parentNode.parentNode).children("td:nth-child(9)").text();
            var discntDeferTag =  $(boxObj[i].parentNode.parentNode).children("td:nth-child(10)").text();
            var depositCode =  $(boxObj[i].parentNode.parentNode).children("td:nth-child(12)").text();
            var addMoney =  $(boxObj[i].parentNode.parentNode).children("td:nth-child(6)").attr("addMoney");
            var preMoney =  $(boxObj[i].parentNode.parentNode).children("td:nth-child(6)").attr("preMoney");
        	var discntFee =  $(boxObj[i].parentNode.parentNode).children("td:nth-child(11)").text(); //月费
        	var discntDayFee =  $(boxObj[i].parentNode.parentNode).children("td:nth-child(13)").text(); //首月按日费用单价
        	var discntDefineMonths =  $(boxObj[i].parentNode.parentNode).children("td:nth-child(14)").text(); //包月标志
        	var thisMonthFee = 0;//本月费用
   
        	 //start--------新计算方式------------------------------
	            var remainMonths = calcBetweenMonths(nextMonthStart, $(boxObj[i].parentNode.parentNode).children("td:nth-child(5)").text());
	            var backMoney = discntFee * remainMonths ;
	            thisMonthFee = preMoney - backMoney ;
	    		//end--------- 新计算方式------------------------------
	    		$(boxObj[i]).attr("checked", true);
	    		$(boxObj[i]).attr("disabled", true);
	    		
	    		var feeData = $.DataMap();
	          	feeData.clear();
				feeData.put("MODE", "2");
				feeData.put("CODE", depositCode);
				feeData.put("FEE",  addMoney);
				feeData.put("PAY",  addMoney);		
				feeData.put("TRADE_TYPE_CODE",$("#TRADE_TYPE_CODE").val());
			    $.feeMgr.deleteFee(feeData);	
	    		 //对于本次新增后又删除的优惠, 直接删除行节点
	            if(discntDeferTag == "A"){
	            	$.table.get("userDiscntTable").deleteRow($(boxObj[i].parentNode.parentNode).rowIndex);
	                 return false;
	            }
	             //如果延期后又点击退订，则恢复原有余额信息
	             $(boxObj[i].parentNode.parentNode).children("td:nth-child(7)").attr("disabled",true);
	             $(boxObj[i].parentNode.parentNode).children("td:nth-child(6)").text(toYuan(preMoney));
	             if(backMoney >=0){
	                  //设置业务解释
				          $("#operationTip").css("display", "");
				          var hint ="请确认： 退订后，宽带预存余额中的【"+toYuan(backMoney)+"元】将转到手机话费里，【"+toYuan(thisMonthFee)+"元】将作为【老套餐】宽带费用！"
				          $("#operationTip").text(hint);
	             }
	            $(boxObj[i].parentNode.parentNode).children("td:nth-child(10)").text("D"); 
	            
	             $(boxObj[i].parentNode.parentNode).children("td:nth-child(6)").empty();
	    		 $.insertHtml('beforeend',$(boxObj[i].parentNode.parentNode).children("td:nth-child(6)"), toYuan(preMoney) + "<span class='e_red'> - "+toYuan(backMoney)+"<br>(将用于新套餐)</span>");
	    		 
	              if (enable_tag == '2' || enable_tag == '3'){//取消时下帐期生效
	                $(boxObj[i].parentNode.parentNode).children("td:nth-child(5)").empty();
	                $.insertHtml('beforeend',$(boxObj[i].parentNode.parentNode).children("td:nth-child(5)"), "<span class='e_red'>"+thisMonthEnd+"</span>");
	    		 
	              }   else if (enable_tag == '1' || enable_tag == '4'){//取消时立即生效
	                $(boxObj[i].parentNode.parentNode).children("td:nth-child(5)").empty();
	                $.insertHtml('beforeend',$(boxObj[i].parentNode.parentNode).children("td:nth-child(5)"), "<span class='e_red'>"+currentTime+"</span>");
	              }
	            discntsDel+=discntCode+",";
	            delDiscntFee+=backMoney.toString() +",";
	            delDiscntPayId+=depositCode+",";
	            $("#SPEC_DEL_DISCNT").val(discntsDel);
	            $("#SPEC_DEL_DISCNT_FEE").val(delDiscntFee);
	            $("#SPEC_DEL_DISCNT_PAYMENTID").val(delDiscntPayId);
	       }
	    }
	    
	    
	       
}

function isNotInDelLimit(str){	//营业员删除权限
    var delLimit = $("#SPEC_DEL_LIMIT").val();
    var keys = delLimit.split(",");
    for(var j=0; j<keys.length; j++){
        if(str == keys[j]){
            return false; 
        }
    }
    return true;
  // return false; //测试用
}


/**
 * 计算间隔月数
 * 例如 startId = 2012-09-01 00:00:00 
 *      endId   = 2013-02-28 23:59:59
 * 则 返回 6
 */
function calcBetweenMonths(startId, endId){
	var startDate = startId;
	var endDate = endId;	
	var startArray = startDate.split("-");
	var endArray = endDate.split("-");
    var months = (parseInt(endArray[0],10) - parseInt(startArray[0],10))*12 + (parseInt(endArray[1],10) - parseInt(startArray[1],10)) + 1;
	return months;
}
function addTableRow() {
	//获取编辑区的数据
	var custEdit = $.ajax.buildJsonData("EditPart");
	//往表格里添加一行并将编辑区数据绑定上
	$.table.get("userDiscntTable").addRow(custEdit);
}




/**
 * 获得N个月后的日期
 * @param startdate
 * @param addmonth
 * @returns {String}
 */
function dateAdd(startdate,addmonth){
    var year=startdate.split("-")[0];
    var month=startdate.split("-")[1];
    var day=startdate.split("-")[2];
    var add=parseInt(addmonth);

    if(month.substr(0,1)==0)
        month=month.substr(1,1);
    if(day.substr(0,1)==0)
        day=day.substr(1,1);

    //字符转换成数字
    year=parseInt(year);
    month=parseInt(month);
    day=parseInt(day);

    //计算新的年和月
    var newmonth=month+add; 
    year+=parseInt(newmonth/12); 
    if(newmonth>=12){       
        if(newmonth%12==0){
            year=year-1;
            month=12;
        }else
            month=parseInt(newmonth%12);
    }else
        month+=add;

    //计算day
    if( month==2) { 
        if(year%4==0 && year%100!=0)  //闰年
            day=29;
        else
            day=28;     //平年
    }
    else{    
         switch(month){
         case 1:
         case 3:
         case 5:
         case 7:
         case 8:
         case 10:
         case 12:
            day=31;break;
         case 4:
         case 6:
         case 9:
         case 11:
            day=30;break;
         }
    }
    var enddate=year+"-"+parseInt(month/10)+month%10+"-"+parseInt(day/10)+day%10+" "+"23:59:59";
    return enddate;
}
/**
 * 显示时转成  单位:元
 * 输入string，输出string
 */
function toYuan(obj){
	return (parseInt(obj,10)/100).toString();
}

function onSubmit(){
   var ds=$.DatasetList();  
    var discntTable = $.table.get("userDiscntTable").getTableData(null, true);
    for(var i=0; i<discntTable.getCount(); i++){
         var deferTag=discntTable.get(i).get("DEFER_TAG");
         var deferMonths=discntTable.get(i).get("DEFER_MONTHS");
         if(deferTag =="A" && (deferMonths == "" ||deferMonths=="--请选择--" || deferMonths == "0")){        
           continue;
         }else if(deferTag =="B" && (deferMonths == "" || deferMonths=="--请选择--" || deferMonths == "0")){
           continue;
         }
         ds.add(discntTable.get(i));
    }

    if(ds.getCount()==0){
       alert("您未对任何资费进行修改！")
        return false;
    }
	var param = "&SPC_USER_DISCNTS="+ds+"&SPC_DEL_DISCNT_FEE="+ $("#SPEC_DEL_DISCNT_FEE").val()+"&SPC_DEL_DISCNT_PAYMENTID="+
	$("#SPEC_DEL_DISCNT_PAYMENTID").val()+"&CAMPUS_TAG="+$("#CAMPUS_TAG").val();
	$.cssubmit.addParam(param);	
	return true;
}