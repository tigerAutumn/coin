package com.qkwl.admin.layui.shiro;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import at.pollux.thymeleaf.shiro.dialect.ShiroDialect;

@Configuration
public class ShiroConfiguration {
	@Bean(name = "lifecycleBeanPostProcessor")
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean(name = "shiroRealm")
	@DependsOn("lifecycleBeanPostProcessor")
	public ShiroRealm shiroRealm() {
		ShiroRealm realm = new ShiroRealm();
		return realm;
	}

	@Bean(name = "securityManager")
	public SecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(shiroRealm());
		return securityManager;
	}

	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shiroFilterFactoryBean() {
		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager());

		Map<String, Filter> filters = new LinkedHashMap<String, Filter>();
		LogoutFilter logoutFilter = new LogoutFilter();
		logoutFilter.setRedirectUrl("/hotcoin/hot");
		filters.put("logout", logoutFilter);
		filters.put("authc", new ShiroRealmFilter());
		shiroFilterFactoryBean.setFilters(filters);

		Map<String, String> filterChainDefinitionManager = new LinkedHashMap<String, String>();
		//开放调用维护页面的接口
		
		
		
	    // swagger
		filterChainDefinitionManager.put("/swagger-ui.html", "anon");
	    filterChainDefinitionManager.put("/swagger-resources/**", "anon");
	    filterChainDefinitionManager.put("/v2/api-docs/**", "anon");
	    filterChainDefinitionManager.put("/webjars/springfox-swagger-ui/**", "anon");
		
		
		filterChainDefinitionManager.put("/prompt/getPrompt", "anon");
		filterChainDefinitionManager.put("/logout", "logout");
		filterChainDefinitionManager.put("/hotcoin/hot", "anon");
		filterChainDefinitionManager.put("/admin/sendSMS", "anon");
		filterChainDefinitionManager.put("/hotcoin/submithot", "anon");
		filterChainDefinitionManager.put("/admin/startCaptcha", "anon");
		filterChainDefinitionManager.put("/error", "anon");
		filterChainDefinitionManager.put("/servlet/**", "anon");
		filterChainDefinitionManager.put("/css/**", "anon");
		filterChainDefinitionManager.put("/img/**", "anon");
		filterChainDefinitionManager.put("/js/**", "anon");
		filterChainDefinitionManager.put("/font/**", "anon");
		filterChainDefinitionManager.put("/**", "authc,perms");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionManager);

		shiroFilterFactoryBean.setLoginUrl("/hotcoin/hot");
		shiroFilterFactoryBean.setSuccessUrl("/admin/index");
		shiroFilterFactoryBean.setUnauthorizedUrl("/error");

		return shiroFilterFactoryBean;
	}
	
	@Bean  
    @ConditionalOnMissingBean  
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {  
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();  
        daap.setProxyTargetClass(true);  
        return daap;  
    }  
      
    @Bean  
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {  
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();  
        aasa.setSecurityManager(securityManager());  
        return aasa;  
    }  
    
   @Bean
    public ShiroDialect shiroDialect(){ 
    	return new ShiroDialect();
    } 

}
