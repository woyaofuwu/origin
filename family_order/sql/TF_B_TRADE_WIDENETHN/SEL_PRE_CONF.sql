--IS_CACHE=Y
select DATA_ID, DATA_NAME from td_s_static where SUBSYS_CODE='CSM'  and TYPE_ID = :TYPE_ID