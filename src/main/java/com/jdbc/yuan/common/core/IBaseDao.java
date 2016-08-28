package com.jdbc.yuan.common.core;

import java.util.List;

import com.jdbc.yuan.common.annotation.QueryBean;

/**
 * create by 袁恩光  2016-8-27
 */
public interface IBaseDao<T> {
	
	/**
	 * 通用update 方法
	 * @param t
	 * @return
	 */
	Long update(T entity);
	
	/**
	 * 通用insert 方法
	 * @param t
	 * @return
	 */
	Long save(T entity);
	
	/**
	 * 保存或者更新
	 * @param entity
	 * @return
	 */
	Long saveOrUpdate(T entity);
	
	/**
	 * 根据主键删除
	 * @param t
	 * @return
	 */
	void deleteById(Long id);
	
	
	/**
	 * 查询单条的方法
	 * @param id
	 * @return
	 */
	T queryOne(Long id);
	
	/**
	 * 简单查询所有
	 * @return
	 */
	List<T> queryAll();
	
	/**
	 * 查询信息条数
	 * @return
	 */
	Long count();
	
	/**
	 * 根据条件查询
	 * @param bean
	 * @return
	 */
	List<T> queryByBean(QueryBean bean);
	
	
}
