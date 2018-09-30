package com.jt.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.SysResult;
import com.jt.sso.pojo.User;
import com.jt.sso.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	//实现用户的检验 http://sso.jt.com/user/check/{param}/{type}
	//http://sso.jt.com/user/check/dsdada/1?r=0.7055805695937962&callback=jsonp1538205890774&_=1538205895392
	//参考需求文档得知Type的值1表示username的校验,2表示phone,3表示email,而在sql语句查询的时候,我们在业务层要转换为对应的查询属性;
	@RequestMapping("/check/{param}/{type}")
	@ResponseBody
	public MappingJacksonValue checkUser(@PathVariable String param,
			@PathVariable Integer type,String callback){
		//参考需求文档中的1项
		/* 返回值	为{
		status: 200  //200 成功，201 没有查到
		msg: “OK”  //返回信息消息
		data: false  //返回数据true用户已存在，false用户不存在，可以
		}*/

		//查询后台数据 返回SysResult结果信息,其中的data属性为 true,则表示信息已存在
		boolean flag = userService.findCheckUser(param,type);
		//下面需要包装的参数是一个SysResult对象(通过构造方法构建)
		MappingJacksonValue jacksonValue = 
				new MappingJacksonValue(SysResult.oK(flag));
		jacksonValue.setJsonpFunction(callback);
		//包装完成,返回JSONP的数据
		return jacksonValue;
	}
	
	//由前台通过httpClient发起post请求转向后台(jt-sso)
	//根据url定义拦截路径,http://sso.jt.com/user/register
	@RequestMapping("/register")
	@ResponseBody
	//这里的参数需要注意一下,我们在前台发起请求之前好像是将数据封装到了map中,但其实在httpClient调用doPost方法
	//时候,其中将map又封装到了一个表单实体对象formEntity中,因此虽然我们之前写成了map的格式,但是在传输过程中和一个
	//正常的form表单是一样的,和普通的post提交是一样的;既然是post提交的参数,那么我们就可以直接用user对象接收即可
	//跟前台的控制层接收的方式一样;(也可以分开用4个属性来接收,但是麻烦)
	public SysResult saveUser(User user){
		try {
			userService.saveUser(user);
			return SysResult.oK();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201, "用户新增失败");
	}
	
	
	
	
	
}
