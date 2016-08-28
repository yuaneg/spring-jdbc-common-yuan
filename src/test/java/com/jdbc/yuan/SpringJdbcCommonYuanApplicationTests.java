package com.jdbc.yuan;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.jdbc.yuan.dao.ISysUserDao;
import com.jdbc.yuan.modle.SysUser;
import com.jdbc.yuan.modle.SysUserQueryBean;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringJdbcCommonYuanApplicationTests {

	@Autowired
	private ISysUserDao sysUserDao;

	@Test
	public void update() {
		SysUser sysUser = new SysUser();
		sysUser.setId(134L);
		sysUser.setRealName("袁恩光");
		sysUserDao.update(sysUser);
	}

	@Test
	public void deleteById() {
		sysUserDao.deleteById(134L);
	}

	@Test
	public void insert() {
		SysUser sysUser = new SysUser();
		sysUser.setRealName("袁恩光");
		sysUserDao.save(sysUser);
	}

	@Test
	public void saveOrUpdate() {
		SysUser sysUser = new SysUser();
		sysUser.setId(155L);
		sysUser.setRealName("222");
		sysUserDao.saveOrUpdate(sysUser);
	}

	@Test
	public void queryOne() {
		SysUser sysUser = sysUserDao.queryOne(154L);
		System.out.println(sysUser.toString());
	}

	@Test
	public void queryAll() {
		List<SysUser> sysUser = sysUserDao.queryAll();
		for (SysUser user : sysUser) {
			System.out.println(user.toString());
		}
	}

	@Test
	public void queryBean() {
		SysUserQueryBean bean = new SysUserQueryBean();
		bean.setRname("2");
		List<SysUser> sysUserList = sysUserDao.queryByBean(bean);
		for (SysUser sysUser : sysUserList) {
			System.out.println(sysUser.toString());
		}
	}
}
