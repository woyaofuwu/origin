DECLARE
v_seqid number(16);
BEGIN
SELECT f_sys_getseqid(:EPARCHY_CODE,'seq_smssend_id') into v_seqid FROM dual;
INSERT INTO ti_o_sms(sms_notice_id,partition_id,send_count_code,refered_count,eparchy_code,in_mode_code,chan_id,recv_object_type,recv_object,recv_id,sms_type_code,sms_kind_code,notice_content_type,notice_content,force_refer_count,sms_priority,refer_time,refer_staff_id,refer_depart_id,deal_time,deal_state,remark,force_object,send_time_code,send_object_code)
SELECT v_seqid,mod(v_seqid,1000),'1','0',:EPARCHY_CODE,:IN_MODE_CODE,
       '11',                           
       '00',                           
       :SERIAL_NUMBER,               
       NVL(TO_NUMBER(:USER_ID),0),     
       '20',                           
       '02',                           
       '0',                            
       substrb(:NOTICE_CONTENT,0,500),               
       1,                              
       :PRIORITY+1000,                     
       SYSDATE,                         
       :STAFF_ID,                     
       :DEPART_ID,                    
       SYSDATE,                        
       '15',                            
       :REMARK,
       :FORCE_OBJECT,                        
       1,6
  FROM dual;
END;