SELECT agent_fee/(sale_num*100) agent_fee,(sale_money-agent_fee)/(sale_num*100) sale_money,old_money/(sale_num*100) old_money
FROM tf_b_cardsale_log
WHERE log_id=:LOG_ID AND ROWNUM<2