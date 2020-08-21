--IS_CACHE=Y
SELECT t.product_id,t.attr_code,t.attr_name,t.default_value,t.biz_type,t.priority,t.edit_type,t.format,t.visiable,t.mandatory,t.readonly,t.oninit_js,t.onchange_js,t.onsubmit_js,t.update_time,t.update_staff_id,t.eparchy_code,t.show_index,t.remark,t.groupattr,t.rsrv_str1,t.rsrv_str2,t.rsrv_str3,t.rsrv_str4,t.rsrv_str5,t.rsrv_str6,t.rsrv_str7,t.rsrv_str8,t.rsrv_str9,t.rsrv_str10
 FROM td_s_bboss_attr t
 WHERE 1=1
 AND (t.visiable=:OPERTYPE or t.visiable like :OPERTYPE||',%' or t.visiable like '%,'||:OPERTYPE or 
 t.visiable like '%,'||:OPERTYPE||',%')  
 AND product_id=:PRODUCT_ID
 AND biz_type=:BIZ_TYPE 
 ORDER BY show_index