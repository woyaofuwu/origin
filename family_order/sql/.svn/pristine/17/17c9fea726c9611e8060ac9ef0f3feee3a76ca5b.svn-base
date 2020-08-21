--IS_CACHE=Y
SELECT t.SUBSYS_CODE, t.PARAM_ATTR, t.PARAM_CODE, t.PARAM_NAME, t.PARA_CODE1, t.PARA_CODE2, t.PARA_CODE3, t.PARA_CODE4, t.PARA_CODE5, t.PARA_CODE6, t.PARA_CODE7, t.PARA_CODE8, t.PARA_CODE9, t.PARA_CODE10, t.PARA_CODE11, t.PARA_CODE12, t.PARA_CODE13, t.PARA_CODE14, t.PARA_CODE15, t.PARA_CODE16, t.PARA_CODE17, t.PARA_CODE18, t.PARA_CODE19, t.PARA_CODE20, t.PARA_CODE21, t.PARA_CODE22, t.PARA_CODE23, t.PARA_CODE24, t.PARA_CODE25, to_char(t.PARA_CODE26,'yyyy-mm-dd hh24:mi:ss') PARA_CODE26, to_char(t.PARA_CODE27,'yyyy-mm-dd hh24:mi:ss') PARA_CODE27, to_char(t.PARA_CODE28,'yyyy-mm-dd hh24:mi:ss') PARA_CODE28, to_char(t.PARA_CODE29,'yyyy-mm-dd hh24:mi:ss') PARA_CODE29, to_char(t.PARA_CODE30,'yyyy-mm-dd hh24:mi:ss') PARA_CODE30, to_char(t.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(t.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, t.EPARCHY_CODE, t.UPDATE_STAFF_ID, t.UPDATE_DEPART_ID, to_char(t.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, t.REMARK
,b.note_item
FROM TD_S_COMMPARA t,td_b_noteitem b
WHERE b.note_item_code = t.para_code1
AND t.param_attr = :PARAM_ATTR
AND t.subsys_code = :SUBSYS_CODE
AND t.PARAM_CODE = :PARAM_CODE
AND b.templet_code='50000001' and b.print_level=0
ORDER BY t.PARA_CODE1