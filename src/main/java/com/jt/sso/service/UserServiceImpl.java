package com.jt.sso.service;

import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jt.sso.mapper.UserMapper;
import com.jt.sso.pojo.User;

import sun.security.provider.MD5;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper userMapper;

	@Override
	public boolean findCheckUser(String param, Integer type) {
		//将类型转化为具体的字段名称
		String cloumn = null;
		switch (type) {
			case 1:
				cloumn = "username"; break;
			case 2:
				cloumn = "phone";  break;
			case 3:
				cloumn = "email"; break;
		}
		//通过mapper层在数据库中查询,如果返回结果为0 则返回false  如果不为0 返回true
		//由于这里参数为动态的,这里用不了通用mapper,自己写
		int count = userMapper.findCheckUser(cloumn,param);
		//学习思路
		return count == 0 ? false : true;
	}

	
	
	@Override
	public void saveUser(User user) {
		//补全user数据	
		//用户传过来的密码是明文的,需要转为密文,利用DigestUtils工具类(阿帕奇包的),
		//md5Hex表示在md5的基础上再哈希,单纯的md5已经不安全了,还需要加盐值
		String md5Password = DigestUtils.md5Hex(user.getPassword());
		user.setPassword(md5Password);
		user.setEmail(user.getPhone());//为了保证数据库不报错 暂时用电话代替
		user.setCreated(new Date());
		user.setUpdated(user.getCreated());
		userMapper.insert(user);
	}
	
	
	
	
	
}
