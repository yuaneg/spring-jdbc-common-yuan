package com.jdbc.yuan.common.annotation;

import java.lang.reflect.Field;

/**
 * create by 袁恩光 2016-8-28
 */
public abstract class QueryBean {

	private String orderBy;

	private Long startIndex;

	private Long endIndex;

	private String mySql;

	public String getSql() {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(getMySql());
			sb.append(getConditionSql());
			sb.append(getOrderBy());
			sb.append(getLimit());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private String getConditionSql() throws IllegalArgumentException, IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		sb.append(" where 1=1 ");
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			Condition condition = field.getAnnotation(Condition.class);
			field.setAccessible(true);
			Object obj = field.get(this);
			if (!condition.field().equals("") && null != obj) {
				String oper = getOperator(condition.operator());
				if (oper.equals("like")) {
					obj = getValue(obj, condition.operator());
				}
				sb.append(" and " + condition.field() + " " + oper + " '" + obj + "'");
			}
		}
		return sb.toString();
	}

	public void limit(Long startIndex, Long endIndex) {
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void setMySql(String mySql) {
		this.mySql = mySql;
	}

	private String getLimit() {
		String sql = "";
		if (startIndex != null && endIndex != null) {
			sql += " limit " + startIndex + "," + endIndex;
		}
		return sql;
	}

	private String getMySql() {
		return mySql == null ? "" : mySql;
	}

	private String getOrderBy() {
		return orderBy == null ? "" : orderBy;
	}

	/**
	 * 获得运算符
	 * 
	 * @param operator
	 * @return
	 */
	private String getOperator(Operator operator) {
		String oper = "";
		switch (operator) {
		case LT:
			oper = "<";
			break;
		case GT:
			oper = ">";
			break;
		case EQ:
			oper = "=";
			break;
		case NEQ:
			oper = "<>";
			break;
		case LTE:
			oper = "<=";
			break;
		case GTE:
			oper = ">=";
			break;
		default:
			oper = "like";
			break;
		}
		return oper;
	}

	private Object getValue(Object obj, Operator operator) {
		switch (operator) {
		case LIKE:
			break;
		case LLIKE:
			obj = "%" + obj;
			break;
		case RLIKE:
			obj = obj + "%";
			break;
		case LRLIKE:
			obj = "%" + obj + "%";
			break;
		default:
			break;
		}
		return obj;
	}

}
