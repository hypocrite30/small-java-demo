package com.yzh.shirodemo;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @program: JavaLearning
 * @description: 自定义Realm类
 * @author: Mvbbb
 * @create: 2021-03-21 22:37
 */
public class MyRealm  extends AuthorizingRealm {

	/**
	 * 模拟数据库数据
	 */
	Map<String, String> userMap = new HashMap<>(16);

	{
		userMap.put("daming", "123456");
		super.setName("myRealm"); // 设置自定义Realm的名称，取什么无所谓..
	}

	/**
	 * PrincipalCollection 是中有唯一表示用户的,比如说userName
	 *
	 * @param principalCollection
	 * @return
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		String userName = (String) principalCollection.getPrimaryPrincipal();
		// 从数据库获取角色和权限数据
		Set<String> roles = getRolesByUserName(userName);
		Set<String> permissions = getPermissionsByUserName(userName);

		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.setStringPermissions(permissions);
		simpleAuthorizationInfo.setRoles(roles);
		return simpleAuthorizationInfo;
	}

	/**
	 * 基于userName从数据库中获取用户权限
	 *
	 * @param userName
	 * @return
	 */
	private Set<String> getPermissionsByUserName(String userName) {
		Set<String> permissions = new HashSet<>();
		permissions.add("user:delete");
		permissions.add("user:add");
		return permissions;
	}

	/**
	 * 基于userName从数据库中获取用户角色
	 *
	 * @param userName
	 * @return
	 */
	private Set<String> getRolesByUserName(String userName) {
		Set<String> roles = new HashSet<>();
		roles.add("admin");
		roles.add("user");
		return roles;
	}

	/**
	 * 根据主体传入的认证信息从数据库中获得授权
	 *
	 * @param authenticationToken 主体传过来的认证信息
	 * @return
	 * @throws AuthenticationException
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
		// 1.从主体传过来的认证信息中，获得用户名
		String userName = (String) authenticationToken.getPrincipal();

		// 2.通过用户名到数据库中获取凭证
		String password = getPasswordByUserName(userName);
		if (password == null) {
			return null;
		}
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo("daming", password, "myRealm");
		return authenticationInfo;
	}

	/**
	 * 模拟从数据库取凭证的过程
	 *
	 * @param userName
	 * @return
	 */
	private String getPasswordByUserName(String userName) {
		return userMap.get(userName);
	}
}