(function($){
	$.extend({
		CrudMgr:{ 
			idStrData:$.DataMap(),//临时参数容器
			idDataRecord:$.DataMap(),
			// 获取用户选择的记录
			getSelectRecord:function(){
				$.CrudMgr.idStrData.clear();
				$("input[name='chkbox']").each(function(){
					debugger;					
					if($(this).attr("checked")==true && $(this).val() && $(this).val().length>0){
						$.CrudMgr.idStrData.put($(this).val(),$(this).val());
					}
				});
				return $.CrudMgr.idStrData;
			},
			// 将唯一记录赋值
			getRecord:function(){
				$.CrudMgr.idDataRecord.clear();
				var IDStr="IDStr";
				$("input[name='chkbox']").each(function(){
					debugger;					
					if($(this).attr("checked")==true && $(this).val() && $(this).val().length>0){
						$.CrudMgr.idDataRecord.put(IDStr,$(this).val());
					}
				});				
				return $.CrudMgr.idDataRecord.get("IDStr");
			},
			//判断用户是否选择了记录
			checkSelectedRecord:function(){
				var ids=this.getSelectRecord();
				if(!ids || ids.length<1){
					alert("请选择记录!");
					return false;
				}else if(ids.length>1){
					alert("只能勾选一个");
					return false;
				}
				return true;
			},	
			//根据手机号码查询一个月中购卡记录
			queryECardAtferAuth:function(data)
			{
				$.beginPageLoading("数据查询中..");
				$.ajax.submit('QueryCondPart', 'queryECardForCancel',"&IDVALUE="+$("#AUTH_SERIAL_NUMBER").val(), 'BasicInfosPart', function(data){
					if(!data || data.length<1 || null==data){
						MessageBox.alert("状态","没有可补发的电子有价卡记录。");
					}
					$.endPageLoading();
					if(!(!data || data.length<1 || null==data)){
						alert("请选择一条数据，进行电子有价卡补发!");
					}
				},
				function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
				});
			},	
			//根据卡号进行补发
			submitOneInfo:function(){
				var transCardNum=this.getRecord();
				var cardNum = transCardNum.split(";");
				var cardtype=cardNum[3];
				if(cardtype=='01'){
					alert("按卡号进行补发时，电子有价卡不可以为流量充值卡！！请重新选择话费充值卡进行补发");
					return false;
				}
				var cardno=cardNum[0];
				
				if(!window.confirm("确定将卡号为" +cardno+
				"的电子有价卡补发吗？")){
					return false;
				}
				var param ='&ids='+this.getRecord();
				param +="&IDVALUE=" + $("#AUTH_SERIAL_NUMBER").val();
				param +="&REISSUE_TYPE=" + $("#QRY_TYPE").val();

				$.beginPageLoading("数据提交中..");
				$.ajax.submit('BasicInfosPart', 'onSubmitInfo', param,null, function(data){
					$.endPageLoading();
					if(!window.confirm("电子化有价卡补发成功。请问是否继续使用该手机号码进行电子有价卡补发？")){
						var href = window.location.href;
						if(href){
							if(href.lastIndexOf("#nogo") == href.length-5){
								href = href.substring(0, href.length-5);
							}
							var url = href.substring(0, href.indexOf("?"));
							var reqParam = href.substr(href.indexOf("?")+1);
							
							var paramObj = $.params.load(reqParam);
							paramObj.remove("SERIAL_NUMBER");
							paramObj.remove("DISABLED_AUTH");
							paramObj.remove("AUTO_AUTH");
							var param = paramObj.toString();
							window.location.href = url+"?"+param;
						}		
					}else{
						$.CrudMgr.queryECardAtferAuth();
					}

				},
				function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
				});
			},
			//获取勾选记录的交易流水号
			returnTransId:function(){
				var lineTrans=this.getRecord();
				var newTransId = lineTrans.split(";");
				return newTransId[1];
			},
			//按交易流水号进行补发
			submitTransInfo:function(){
				var origialTransId = this.returnTransId();
				
				if(!window.confirm("确定将流水号为" +origialTransId+
				"的所有电子有价卡补发吗？")){
					return false;
				}
				var param ='&ids='+this.getRecord();
				param +="&IDVALUE=" + $("#AUTH_SERIAL_NUMBER").val();
				param +="&REISSUE_TYPE=" + $("#QRY_TYPE").val();
				
				$.beginPageLoading("数据提交中..");
				$.ajax.submit('BasicInfosPart', 'onSubmitInfo', param,null, function(data){

					$.endPageLoading();
					if(!window.confirm("电子化有价卡补发成功。请问是否继续使用该手机号码进行电子有价卡补发？")){
						var href = window.location.href;
						if(href){
							if(href.lastIndexOf("#nogo") == href.length-5){
								href = href.substring(0, href.length-5);
							}
							var url = href.substring(0, href.indexOf("?"));
							var reqParam = href.substr(href.indexOf("?")+1);
							
							var paramObj = $.params.load(reqParam);
							paramObj.remove("SERIAL_NUMBER");
							paramObj.remove("DISABLED_AUTH");
							paramObj.remove("AUTO_AUTH");
							var param = paramObj.toString();
							window.location.href = url+"?"+param;
						}		
					}else{
						var qryFlag = document.getElementById("qryFlag");
						qryFlag.checked=false;
						$.CrudMgr.queryECardAtferAuth();
					}

								


				},
				function(error_code,error_info){
					$.endPageLoading();
					alert(error_info);
				});
			},
			//提交校验
			onTradeSubmit:function(data)
			{

				var IDVALUE=$("#AUTH_SERIAL_NUMBER").val();
				if (IDVALUE == '') {
					alert("请输入服务号码！");
					return false;
				}
				var objTable = document.getElementById("BadnessTradeTable");
				if(objTable.rows.length <= 1){
					MessageBox.alert("提示","没有可以补发的有价电子卡记录不能提交！");
					return false;
				}
				if(!this.checkSelectedRecord()){
					return false;
				}
				var qryFlag = document.getElementById("qryFlag");
				if(qryFlag.checked){
					$("#QRY_TYPE").val("1");

				}else
					$("#QRY_TYPE").val("2");
				var qryType = $("#QRY_TYPE").val();

				if(qryType == 2){
					$.CrudMgr.submitOneInfo();
				}else 
					$.CrudMgr.submitTransInfo();
			}
		}
	});
})(Wade);

function refreshPartAtferAuth(data){
	$.CrudMgr.queryECardAtferAuth(data);
}
