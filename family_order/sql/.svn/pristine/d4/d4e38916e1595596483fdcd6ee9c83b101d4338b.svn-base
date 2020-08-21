--IS_CACHE=Y
select to_char(t.PRODUCT_ID_A) PRODUCT_ID_A,to_char(t.PRODUCT_ID_B) PRODUCT_ID_B,t.PRODUCT_NAME,t.PRODUCT_EXPLAIN,t.RELATION_TYPE_CODE,t.FORCE_TAG,t.DEFAULT_TAG,to_char(t.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE ,
to_char(t.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE ,t.EPARCHY_CODE,t.UPDATE_TIME,t.UPDATE_STAFF_ID,t.UPDATE_DEPART_ID,t.REMARK
 from TD_B_PRODUCT_COMP_RELA t
where t.product_id_a=:PRODUCT_ID and t.relation_type_code=:RELATION_TYPE_CODE
and sysdate between t.START_DATE and t.END_DATE