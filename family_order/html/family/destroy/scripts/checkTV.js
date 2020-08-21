// 引入json2
document.write("<script src='scripts/person/json2.js'></script>");

function checkTermial(data){
	//this data struct
	/*
	 * \\未回收
	 * var list=new Array();
	 * var sn1 =new Map();
	 * sn1.set("SN",SN);
	 * sn1.set("FLAG",1);
	 * 
	 * \\已回收
	 * var sn2 =new Map();
	 * sn2.set("SN",SN);
	 * sn2.set("FLAG",0);
	 * list[0]=sn1;
	 * list[1]=sn2;
	 * 
	 */
	if(data.length<1){
		return true;
	}
	var rhwgRes=[];
	var param = "&SN_LIST=" + JSON.stringify(data);
	$.httphandler.get('com.asiainfo.veris.crm.order.web.person.broadband.widenet.widenetmove.CheckTV',
					'checkRHWG', param, function(d) {
						rhwgRes = d;
					}, function(e, i) {
						alert(e + ":" + i);
					},{
						async: false
					});
	
	var msg = "";
	var sn_flag ="";
	for (i = 0; i < data.length; i++) {
		var sn_data = data[i];
		sn_flag = sn_data['FLAG'];
		var sn = sn_data['SN'];
		for (j = 0; j < rhwgRes.length; j++) {
			var hitv_info = rhwgRes.get(j);
			var hitv_sn = hitv_info.get('SN');
			if (hitv_sn == sn) {
				// 如果是融合网关，就不做提示，改成X状态
				sn_flag = 'X';
			}
		}
		if (sn_flag == 1) {
			msg += "物品[" + sn + "]未回收，将收取魔百盒设备费用100/50元"+"\r\n";
		} else if (sn_flag == 0) {
			msg += "物品[" + sn + "]已回收"+"\r\n";
		}
	}
	if(sn_flag=='X'){
		return true;
	}
	if(msg==null||msg==""){
		return false;
	}
	
	if(confirm(msg)){
		return true;
	}else{
		return false;
	}
}

