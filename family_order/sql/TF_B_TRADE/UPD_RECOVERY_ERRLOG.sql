UPDATE tl_bpm_error_log 
 SET deal_tag='1',deal_desc='系统后台自动异常处理完成',deal_time=SYSDATE,
 deal_staff_id='OMENGINE',deal_depart_id='OMENG'  
 where ERR_TIME>=(SYSDATE) -:INDAYS 
 AND DEAL_TAG='0' 
 and RSRV_STR1<=10