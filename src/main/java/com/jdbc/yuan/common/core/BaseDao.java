package com.jdbc.yuan.common.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.GenerationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.jdbc.yuan.common.annotation.QueryBean;

/**
 * create by 袁恩光 2016-8-27
 */
public abstract class BaseDao<T> implements IBaseDao<T> {

	private static Logger logger = LoggerFactory.getLogger(BaseDao.class);

	private Class<T> entityClass;

	@Autowired
	private JdbcTemplate JdbcTemplate;

	private SqlUtil sqlUtil = new SqlUtil();

	private String sql = "";

	/**
	 * 通过构造方法获取entityClass
	 */
	@SuppressWarnings("unchecked")
	public BaseDao() {
		Type genType = getClass().getGenericSuperclass();
		ParameterizedType type = (ParameterizedType) genType;
		Type[] params = type.getActualTypeArguments();
		entityClass = (Class<T>) params[0];
	}

	@Override
	public void deleteById(Long id) {
		delete(id, entityClass);
	}

	@Override
	public Long update(T entity) {
		try {
			sql = sqlUtil.updateSql(entity);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return Long.valueOf(JdbcTemplate.update(sql));
	}

	@Override
	public Long save(T entity) {
		if (generatedIdTypeAuto()) {
			Long id = queryMaxId() + 1;
			return insert(entity, id);
		}
		return insert(entity, null);
	}

	@Override
	public Long saveOrUpdate(T entity) {
		// 获得id的值
		try {
			Long id = sqlUtil.getIdValue(entity);
			if (null == id) {
				return this.save(entity);
			}
			// 进行查询判断是否存在
			if (!existEntity(id)) {
				return this.save(entity);
			}
			return update(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1L;
	}

	@Override
	public Long count() {
		try {
			return count(entityClass);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return 0L;
	}

	@Override
	public T queryOne(Long id) {
		T t = null;
		try {
			sql = sqlUtil.querySql(entityClass, id);
			Map<String, Object> map = JdbcTemplate.queryForMap(sql);
			t = sqlUtil.mapToEntity(entityClass, map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public List<T> queryAll() {
		List<T> listT = new ArrayList<T>();
		try {
			sql = sqlUtil.querySql(entityClass, null);
			List<Map<String, Object>> listMap = JdbcTemplate.queryForList(sql);
			for (Map<String, Object> map : listMap) {
				T t = sqlUtil.mapToEntity(entityClass, map);
				listT.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listT;
	}

	/**
	 * 根据自定义的查询条件查询
	 */
	@Override
	public List<T> queryByBean(QueryBean bean) {
		List<T> listT = new ArrayList<T>();
		try {
			sql = sqlUtil.querySql(entityClass, null);
			sql += bean.getSql();
			List<Map<String, Object>> listMap = JdbcTemplate.queryForList(sql);
			for (Map<String, Object> map : listMap) {
				T t = sqlUtil.mapToEntity(entityClass, map);
				listT.add(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listT;
	}

	/**
	 * 查询最大Id
	 * 
	 * @return
	 */
	private Long queryMaxId() {
		try {
			sql = sqlUtil.maxIdSql(entityClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JdbcTemplate.queryForObject(sql, Long.class);
	}

	/**
	 * 删除方法
	 * 
	 * @param id
	 * @param entityClass
	 */
	private void delete(Long id, Class<T> entityClass) {
		try {
			sql = sqlUtil.deleteSql(id, entityClass);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		JdbcTemplate.execute(sql);
	}

	/**
	 * 获取信息总数
	 * 
	 * @param entityClass
	 * @return
	 * @throws Exception
	 */
	private Long count(Class<T> entityClass) throws Exception {
		sql = sqlUtil.countSql(entityClass);
		return JdbcTemplate.queryForObject(sql, Long.class);
	}

	/**
	 * 判断主键是否设置自增
	 * 
	 * @return
	 */
	private boolean generatedIdTypeAuto() {
		GenerationType type = sqlUtil.getIdType(entityClass);
		if (type.equals(GenerationType.AUTO)) {
			return true;
		}
		return false;
	}

	/**
	 * 通过主键进行自增
	 * 
	 * @param entity
	 * @param id
	 * @return
	 */
	private Long insert(T entity, Long id) {
		try {
			sql = sqlUtil.insertSql(entity, id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		JdbcTemplate.execute(sql);
		return id;
	}

	/**
	 * 判断记录在数据库中是否存在
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private boolean existEntity(Long id) throws Exception {
		String sql = sqlUtil.existSql(entityClass, id);
		try {
			JdbcTemplate.queryForMap(sql);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
