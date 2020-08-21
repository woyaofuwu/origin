
function queryStaffs(obj){
	//查询条件校验
	if(!$.validate.verifyAll("QueryCondPart")) {//先校验已配置的校验属性
		return false;
	}
 	
	$.ajax.submit('QueryCondPart', 'queryStaffs', null, 'StaffPart', function(data){
		if(data.get('ALERT_INFO') != '')
		{
			alert(data.get('ALERT_INFO'));
		}
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

Wade.org = ( function() {
	var f = Wade.dom;
	var d = Wade.ajax;
	var c = Wade.dialog;
	var a = Wade.util;
	var b = {
		GRANT_TYPE_PERSON : [ "0", "个人" ],
		GRANT_TYPE_DEPART : [ "1", "部门" ],
		GRANT_TYPE_AREA : [ "2", "业务区" ],
		GRANT_OPTION_STAFF : [ "1", "普通员工" ],
		GRANT_OPTION_MANAGER : [ "2", "客户经理" ]
	};
	function e(l, m) {
		var h = f.getElement("GRANT_FROMS");
		var k = f.getElement("GRANT_TOS");
		if (m == "GRANT_FROMS") {
			for ( var j = k.length; j > 0;) {
				var g = k.options[--j].value;
				if (g && g.indexOf("&GRANT_TO=" + l) != -1) {
					return true;
				}
			}
		} else {
			for ( var j = h.length; j > 0;) {
				var g = h.options[--j].value;
				if (g == "&GRANT_TYPE=" + b.GRANT_TYPE_PERSON[0]
						+ "&GRANT_FROM=" + l) {
					return true;
				}
			}
		}
		return false;
	}
	return {
		addTabsetByAreaTree : function() {
			var g = new TabSet("tabset");
			g.addTab("归属区域", f.getElement("areaTree"));
			g.addTab("授权区域", f.getElement("grantTree"));
			g.draw();
			g.onTabSelect(this.tabEventHandlerByAreaTree);
		},
		tabEventHandlerByAreaTree : function(h) {
			var g = h.getActiveTab();
			if (g.caption == "授权区域") {
				if (f.getElement("GrantLoadTree") == null) {
					d.ajaxDirect("component.org.AreaTree",
							"loadAreaTreeByGrant", "&clickflag=grant",
							"GrantPart");
				}
			}
		},
		addTabsetByDeptTree : function() {
			var g = new TabSet("tabset");
			g.addTab("归属部门", f.getElement("deptTree"));
			g.addTab("授权部门", f.getElement("grantTree"));
			g.draw();
			g.onTabSelect(this.tabEventHandlerByDeptTree);
		},
		tabEventHandlerByDeptTree : function(h) {
			var g = h.getActiveTab();
			if (g.caption == "授权部门") {
				if (f.getElement("GrantLoadTree") == null) {
					d.ajaxDirect("component.org.DeptTree",
							"loadDeptTreeByGrant", "&clickflag=grant",
							"GrantPart");
				}
			}
		},
		initStaffSelect : function() {
			var h = $("#relaflag").val();
			var g = document.getElementById("bcleanup");
			if (h == "true") {
				g.value = " 全部 ";
			}
		},
		initManagerSelect : function() {
			var g = f.getElement("bcleanup");
			if (!hasPriv("ORG_LIST_AREA") && !hasPriv("ORG_LIST_DEPT")) {
				hidden(g, true);
			}
		},
		selectStaffs : function() {
			var data = $.table.get("ResultTable").getCheckedRowDatas();// 获取选择中的数据		
			if (data == null || data.length == 0) {
				alert("请先选择员工！");
				return false;
			}else if (data.length !=1) {
				alert("请先选择一名员工！");
				return false;
			}
			var staffId="";
			var staffName="";
			var staffIds="";
			var staffNames="";
			for ( var j = 0; j < data.length; j++) {
				 var temp = data.get(j);
				 staffId = temp.get("STAFF_ID");
				 staffName = temp.get("STAFF_NAME");
				 staffIds += (staffIds == "" ? "" : ",") + staffId;
				 staffNames += (staffNames == "" ? "" : ",") + staffName;
			}
			 $.setReturnValue(['condition_STAFF_NAME',staffNames,'condition_STAFF_ID',staffIds],true);
		},
		
		cleanupSelect : function() {
			debugger;
			var g = $("relaflag").val();
			
			var h = $("isblank").val();
			if (h == "true") {
				$.setReturnValue("", "");
			} else {
				$.setReturnValue("", "全部员工");
			}
		},
		addGrantObj : function(i) {
			var h = f.getElementValue("GRANT_TYPE");
			var g = i.getAttribute("grant_target");
			if (h == "") {
				alert("请选择对象类型");
				return false;
			}
			if (h == b.GRANT_TYPE_PERSON[0]) {
				f.getElement("SELECT_STAFF_ID").setAttribute("grant_type",
						"GRANT_TYPE");
				f.getElement("SELECT_STAFF_ID").setAttribute("grant_target", g);
				f.getElement("POP_IMG_SELECT_STAFF_ID").click();
			}
			if (h == b.GRANT_TYPE_DEPART[0]) {
				f.getElement("SELECT_DEPART_ID").setAttribute("grant_type",
						"GRANT_TYPE");
				f.getElement("SELECT_DEPART_ID")
						.setAttribute("grant_target", g);
				f.getElement("POP_IMG_SELECT_DEPART_ID").click();
			}
			if (h == b.GRANT_TYPE_AREA[0]) {
				f.getElement("SELECT_AREA_CODE").setAttribute("grant_type",
						"GRANT_TYPE");
				f.getElement("SELECT_AREA_CODE")
						.setAttribute("grant_target", g);
				f.getElement("POP_IMG_SELECT_AREA_CODE").click();
			}
		},
		delGrantObj : function(j) {
			var g = f.getElement("GRANT_FROMS");
			for ( var h = g.length; h > 0;) {
				var k = g.options[--h];
				if (k.selected) {
					g.remove(h);
				}
			}
		},
		addGrantStaff : function(i) {
			var g = f.getElementValue("GRANT_OBJ_TYPE");
			var h = i.getAttribute("grant_target");
			if (g == "") {
				alert("请选择员工类型");
				return false;
			}
			if (g == b.GRANT_OPTION_STAFF[0]) {
				f.getElement("SELECT_STAFF_ID").setAttribute("grant_type",
						"GRANT_OBJ_TYPE");
				f.getElement("SELECT_STAFF_ID").setAttribute("grant_target", h);
				f.getElement("POP_IMG_SELECT_STAFF_ID").click();
			}
			if (g == b.GRANT_OPTION_MANAGER[0]) {
				f.getElement("SELECT_MANAGER_ID").setAttribute("grant_type",
						"GRANT_OBJ_TYPE");
				f.getElement("SELECT_MANAGER_ID").setAttribute("grant_target",
						h);
				f.getElement("POP_IMG_SELECT_MANAGER_ID").click();
			}
		},
		delGrantStaff : function(j) {
			var h = f.getElement("GRANT_TOS");
			for ( var g = h.length; g > 0;) {
				var k = h.options[--g];
				if (k.selected) {
					h.remove(g);
				}
			}
		},
		confirmGrant : function(i) {
			var g = f.getElement("GRANT_FROMS");
			var h = f.getElement("GRANT_TOS");
			if (g.options.length == 0) {
				alert("授权对象不能为空！");
				f.focus(g);
				return false;
			}
			if (h.options.length == 0) {
				alert("授权员工不能为空！");
				f.focus(h);
				return false;
			}
			f.selectedAll("GRANT_FROMS");
			f.selectedAll("GRANT_TOS");
			return confirmForm(i);
		},
		afterSelectStaff : function(m) {
			var k = f.getElement("SELECT_STAFF_ID");
			var j = f.getElement("POP_SELECT_STAFF_ID");
			var i = f.getElementValue(k.getAttribute("grant_type"));
			var h = k.getAttribute("grant_target");
			if (!k) {
				if (k.value == "" && j.value != "") {
					alert("添加失败，不能选择全部员工");
				}
				return false;
			}
			if (e(k.value, h)) {
				alert("添加失败，不能将员工授权给自己");
				return false;
			}
			var l = "&" + k.getAttribute("grant_type") + "=" + i + "&"
					+ h.substring(0, h.length - 1) + "=" + k.value;
			var n = "对象类型:" + b.GRANT_TYPE_PERSON[1] + "       对象名称:" + j.value;
			var g = f.getElement(h);
			if (!containKey(g.options, l)) {
				g.add(new Option(n, l));
			}
			k.value = "";
			j.value = "";
			k.setAttribute("grant_type", "");
			k.setAttribute("grant_target", "");
		},
		afterSelectDepart : function(m) {
			var j = f.getElement("SELECT_DEPART_ID");
			var k = f.getElement("POP_SELECT_DEPART_ID");
			var i = f.getElementValue(j.getAttribute("grant_type"));
			var h = j.getAttribute("grant_target");
			if (!j) {
				if (j.value == "" && k.value != "") {
					alert("添加失败，不能选择全部部门");
				}
				return false;
			}
			var l = "&" + j.getAttribute("grant_type") + "=" + i + "&"
					+ h.substring(0, h.length - 1) + "=" + j.value;
			var n = "对象类型:" + b.GRANT_TYPE_DEPART[1] + "       对象名称:" + k.value;
			var g = f.getElement(h);
			if (!containKey(g.options, l)) {
				g.add(new Option(n, l));
			}
			j.value = "";
			k.value = "";
			j.setAttribute("grant_type", "");
			j.setAttribute("grant_target", "");
		},
		afterSelectManager : function(m) {
			var j = f.getElement("SELECT_MANAGER_ID");
			var l = f.getElement("POP_SELECT_MANAGER_ID");
			var i = f.getElementValue(j.getAttribute("grant_type"));
			var h = j.getAttribute("grant_target");
			if (!j) {
				if (j.value == "" && l.value != "") {
					alert("添加失败，不能选择全部员工");
				}
				return false;
			}
			if (e(j.value, h)) {
				alert("添加失败，不能将员工授权给自己");
				return false;
			}
			var k = "&" + j.getAttribute("grant_type") + "=" + i + "&"
					+ h.substring(0, h.length - 1) + "=" + j.value;
			var n = "员工类型:" + b.GRANT_OPTION_MANAGER[1] + "    员工名称:" + l.value;
			var g = f.getElement(h);
			if (!containKey(g.options, k)) {
				g.add(new Option(n, k));
			}
			j.value = "";
			l.value = "";
			j.setAttribute("grant_type", "");
			j.setAttribute("grant_target", "");
		},
		afterSelectArea : function(m) {
			var l = f.getElement("SELECT_AREA_CODE");
			var j = f.getElement("POP_SELECT_AREA_CODE");
			var i = f.getElementValue(l.getAttribute("grant_type"));
			var h = l.getAttribute("grant_target");
			if (!l) {
				if (l.value == "" && j.value != "") {
					alert("添加失败，不能选择全部区域");
				}
				return false;
			}
			var k = "&" + l.getAttribute("grant_type") + "=" + i + "&"
					+ h.substring(0, h.length - 1) + "=" + l.value;
			var n = "对象类型:" + b.GRANT_TYPE_AREA[1] + "    对象名称:" + j.value;
			var g = f.getElement(h);
			if (!containKey(g.options, k)) {
				g.add(new Option(n, k));
			}
			l.value = "";
			j.value = "";
			l.setAttribute("grant_type", "");
			l.setAttribute("grant_target", "");
		}
	};
})();