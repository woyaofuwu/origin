SELECT textfile_name,text_kind,deal_status,file_records,dealed_records,to_char(created_date,'yyyy-mm-dd hh24:mi:ss') created_date,to_char(back_deal_date,'yyyy-mm-dd hh24:mi:ss') back_deal_date,to_char(before_deal_date,'yyyy-mm-dd hh24:mi:ss') before_deal_date,depart_id,staff_id,eparchy_code,remark 
  FROM tf_a_baddebt_textdeallog
 WHERE textfile_name=:TEXTFILE_NAME