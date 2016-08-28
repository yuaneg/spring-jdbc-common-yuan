package com.jdbc.yuan.modle;

import com.jdbc.yuan.common.annotation.Condition;
import com.jdbc.yuan.common.annotation.Operator;
import com.jdbc.yuan.common.annotation.QueryBean;

public class SysUserQueryBean extends QueryBean {

	@Condition(field = "id", operator = Operator.EQ)
	private Long id;

	@Condition(field = "real_name", operator = Operator.EQ)
	private String name;

	@Condition(field = "real_name", operator = Operator.LRLIKE)
	private String rname;
	
	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRname(String rname) {
		this.rname = rname;
	}

	
}
