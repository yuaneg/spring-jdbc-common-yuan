package com.jdbc.yuan.common.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

/**
 * create by 袁恩光 2016-8-27
 */
public class SqlUtil {

	/**
	 * 构建通用update sql
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */

	protected <T> String updateSql(T entity) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("update ");
		sb.append(getTableName(entity.getClass()) + " set ");
		// 获得所有的字段
		Field[] fields = entity.getClass().getDeclaredFields();
		Id primaryKey = null;
		Long idValue = -1L;
		String primaryKeyName = "";
		int i = 0;
		for (Field field : fields) {
			String methodName = "get" + getMethodName(field.getName());
			Id findId = field.getAnnotation(Id.class);
			Column column = field.getAnnotation(Column.class);
			// 获取get 方法
			Method method = entity.getClass().getMethod(methodName);
			// 得到返回值
			Object obj = method.invoke(entity);
			if (null != findId) {
				primaryKey = findId;
				if (null == obj) {
					throw new Exception("primary key is null");
				}
				idValue = Long.valueOf(obj.toString());
				primaryKeyName = underscoreName(field.getName());
			}
			if (null != column && null != obj) {
				i++;
				if ("".equals(column.name())) {
					sb.append(underscoreName(field.getName()) + "='" + obj + "',");
				} else {
					sb.append(column.name() + "='" + obj + "',");
				}
			}
		}

		if (i == 0) {
			throw new Exception("ensures that the filed is set value");
		}
		sb.delete(sb.length() - 1, sb.length());
		// 获得主键
		if (primaryKey == null) {
			throw new Exception("get primary exception,ensures that the Primary key is annotated by @Id");
		}
		if (idValue.compareTo(-1L) == 0) {
			throw new Exception("ensures that the id is be setted value");
		}
		sb.append("  where " + primaryKeyName + " =" + idValue);
		return sb.toString();
	}

	/**
	 * 通过id 删除
	 * 
	 * @param id
	 * @param entityClass
	 * @return
	 * @throws Exception
	 */
	protected String deleteSql(Long id, Class<?> entityClass) throws Exception {
		StringBuilder sb = new StringBuilder();
		String tableName = getTableName(entityClass);
		String primaryKeyName = primaryKeyName(entityClass);
		sb.append("delete from " + tableName + " where " + primaryKeyName + "=" + id);
		return sb.toString();
	}

	/**
	 * 获取信息总条数
	 * 
	 * @param entityClass
	 * @return
	 * @throws Exception
	 */
	protected String countSql(Class<?> entityClass) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from " + getTableName(entityClass));
		return sb.toString();
	}

	/**
	 * 构建insert sql
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	protected <T> String insertSql(T entity, Long id) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("insert into " + getTableName(entity.getClass()));
		// 字段拼接
		StringBuilder columns = new StringBuilder(" (");
		StringBuilder values = new StringBuilder(" (");
		String primaryKeyName = primaryKeyName(entity.getClass());
		if (null != id) {
			columns.append(primaryKeyName + ",");
			values.append(id + ",");
		}
		Field[] fields = entity.getClass().getDeclaredFields();
		for (Field field : fields) {
			String methodName = "get" + getMethodName(field.getName());
			Column column = field.getAnnotation(Column.class);
			// 获取get 方法
			Method method = entity.getClass().getMethod(methodName);
			// 得到返回值
			Object obj = method.invoke(entity);
			// 判断id的值是否存在
			if (null != column && obj != null) {
				if ("".equals(column.name())) {
					columns.append(underscoreName(field.getName()) + ",");
				} else {
					columns.append(column.name() + ",");
				}
				values.append("'" + obj + "',");
			}
		}
		columns.delete(columns.length() - 1, columns.length());
		values.delete(values.length() - 1, values.length());
		columns.append(")");
		values.append(")");
		sb.append(columns);
		sb.append(" values ");
		sb.append(values);
		return sb.toString();
	}

	/**
	 * 构建查询sql
	 * 
	 * @param entityClass
	 * @param id
	 * @return
	 * @throws Exception
	 */
	protected String querySql(Class<?> entityClass, Long id) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		String primaryKeyName = primaryKeyName(entityClass);
		sb.append(primaryKeyName + ",");
		String tableName = getTableName(entityClass);
		for (Field field : entityClass.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);
			if (null != column) {
				sb.append("".equals(column.name()) ? underscoreName(field.getName()) + "," : column.name() + ",");
			}
		}
		sb.delete(sb.length() - 1, sb.length());
		sb.append(" from " + tableName);
		if (null != id) {
			sb.append(" where " + primaryKeyName + "=" + id);
		}
		return sb.toString();
	}

	/**
	 * 获取最大Id 的sql
	 * 
	 * @param entityClass
	 * @return
	 * @throws Exception
	 */
	protected String maxIdSql(Class<?> entityClass) throws Exception {
		StringBuilder sb = new StringBuilder();
		String primaryKeyName = primaryKeyName(entityClass);
		sb.append("select max(" + primaryKeyName + ") from " + getTableName(entityClass));
		return sb.toString();
	}

	/**
	 * 获取tableName
	 * 
	 * @param entityClass
	 * @return
	 * @throws Exception
	 */
	private String getTableName(Class<?> entityClass) throws Exception {
		Table table = entityClass.getAnnotation(Table.class);
		if (null == table) {
			throw new Exception("get table exception,please ensures that the entity is annotated by @Table");
		}
		return table.name();
	}

	/**
	 * 把字段的第一个小写字母变成大写字母
	 * 
	 * @param fildeName
	 * @return
	 */
	private static String getMethodName(String fildeName) {
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}

	/**
	 * 获取id的自增类型
	 * 
	 * @return
	 */
	protected GenerationType getIdType(Class<?> entityClass) {
		Field[] fields = entityClass.getDeclaredFields();
		for (Field field : fields) {
			GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
			if (null != generatedValue) {
				return generatedValue.strategy();
			}
		}
		// 默认返回使用程序自增
		return GenerationType.AUTO;
	}

	/**
	 * 获取id的值
	 * 
	 * @param entity
	 * @return
	 * @throws Exception
	 */
	protected <T> Long getIdValue(T entity) throws Exception {
		Object obj = null;
		Field[] fields = entity.getClass().getDeclaredFields();
		for (Field field : fields) {
			Id primaryKey = field.getAnnotation(Id.class);
			if (null != primaryKey) {
				String methodName = "get" + getMethodName(field.getName());
				Method method = entity.getClass().getMethod(methodName);
				obj = method.invoke(entity);
				break;
			}
		}
		if (null == obj) {
			return null;
		} else {
			return Long.valueOf(obj.toString());
		}
	}

	/**
	 * 判断记录是否存在的sql
	 * 
	 * @param entityClass
	 * @param id
	 * @return
	 * @throws Exception
	 */
	protected String existSql(Class<?> entityClass, Long id) throws Exception {
		String tableName = getTableName(entityClass);
		String primaryKeyName = primaryKeyName(entityClass);
		String sql = "select 1 from " + tableName + " where " + primaryKeyName + " = '" + id+"'";
		return sql;
	}

	/**
	 * 获得primaryKeyName的名字
	 * 
	 * @param entityClass
	 * @return
	 */
	private String primaryKeyName(Class<?> entityClass) {
		String primaryKeyName = "";
		for (Field field : entityClass.getDeclaredFields()) {
			Id primaryKey = field.getAnnotation(Id.class);
			if (null != primaryKey) {
				primaryKeyName = underscoreName(field.getName());
			}
		}
		return primaryKeyName;
	}

	/**
	 * 对字段名字进行转换
	 * 
	 * @param name
	 * @return
	 */
	protected String underscoreName(String name) {
		if (!StringUtils.hasLength(name)) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		result.append((name.substring(0, 1)).toLowerCase());
		for (int i = 1; i < name.length(); i++) {
			String s = name.substring(i, i + 1);
			String slc = s.toLowerCase();
			if (!s.equals(slc)) {
				result.append("_").append(slc);
			} else {
				result.append(s);
			}
		}
		return result.toString();
	}

	/**
	 * 将map中的值 映射到实体类
	 * 
	 * @param entityClass
	 * @return
	 */
	protected <T> T mapToEntity(Class<T> entityClass, Map<String, Object> entityMap) throws Exception {
		T entity = entityClass.newInstance();
		Field[] fields = entityClass.getDeclaredFields();
		for (Map.Entry<String, Object> entry : entityMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			for (Field field : fields) {
				Column column = field.getAnnotation(Column.class);
				if (null != column) {
					if (column.name().equals(key) || underscoreName(field.getName()).equals(key)) {
						field.setAccessible(true);
						setValue(field, entity, value);
					}
				}
				Id primaryKey = field.getAnnotation(Id.class);
				if (null != primaryKey) {
					if (underscoreName(field.getName()).equals(key)) {
						field.setAccessible(true);
						setValue(field, entity, value);
					}
				}
			}
		}
		return entity;
	}

	/**
	 * 给字段赋值
	 * 
	 * @param field
	 * @param obj
	 * @param value
	 * @throws Exception
	 */
	private void setValue(Field field, Object obj, String value) throws Exception {
		if (field.getGenericType().equals(String.class)) {
			field.set(obj, value);
		}
		// Integer
		if (field.getGenericType().equals(int.class) || field.getGenericType().equals(Integer.class)) {
			field.set(obj, Integer.valueOf(value));
		}
		// Long
		if (field.getGenericType().equals(long.class) || field.getGenericType().equals(Long.class)) {
			field.set(obj, Long.valueOf(value));
		}
		// boolean
		if (field.getGenericType().equals(boolean.class) || field.getGenericType().equals(Boolean.class)) {
			if (Integer.valueOf(value) == 0) {
				field.set(obj, false);
			} else {
				field.set(obj, true);
			}
		}
		// Double
		if (field.getGenericType().equals(double.class) || field.getGenericType().equals(Double.class)) {
			field.set(obj, Double.valueOf(value));
		}
		// Float
		if (field.getGenericType().equals(float.class) || field.getGenericType().equals(Float.class)) {
			field.set(obj, Float.valueOf(value));
		}
		// byte
		if (field.getGenericType().equals(byte.class) || field.getGenericType().equals(Byte.class)) {
			field.set(obj, Byte.valueOf(value));
		}
		// short
		if (field.getGenericType().equals(short.class) || field.getGenericType().equals(Short.class)) {
			field.set(obj, Short.valueOf(value));
		}

	}

}
