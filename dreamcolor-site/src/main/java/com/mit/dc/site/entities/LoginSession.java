/*
 * Copyright 2015 nghiatc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mit.dc.site.entities;

import java.util.*;

/**
 *
 * @author nghiatc
 * @since Nov 10, 2015
 */
public class LoginSession {
    private boolean isLogin = false;
    private boolean isCND = false;
    private int role = -1;
    private String name;
    private long userId = -1;
    private Map<Integer, Integer> bizRoles;

    public LoginSession(boolean isLogin, int role, String name, long userId, boolean isCND) {
        this.isLogin = isLogin;
        this.role = role;
        this.name = name;
        this.userId = userId;
        this.bizRoles = Collections.emptyMap();
        this.isCND = isCND;
    }

    public LoginSession(boolean isLogin, int role, String name, long userId,
			Map<Integer, Integer> bizRoles, boolean isCND) {
		super();
		this.isLogin = isLogin;
		this.role = role;
		this.name = name;
		this.userId = userId;
		this.bizRoles = bizRoles;
		this.isCND = isCND;
	}
    
	public boolean isIsLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Map<Integer, Integer> getBizRoles() {
		return bizRoles;
	}

	public void setBizRoles(Map<Integer, Integer> bizRoles) {
		this.bizRoles = bizRoles;
	}
	
	public boolean getIsCND(){
		return isCND;
	}
	
	public void setIsCND(boolean isCND){
		this.isCND = isCND;
	}

	@Override
    public String toString() {
        return "LoginSession{" + "isLogin=" + isLogin + ", role=" + role + ", name=" + name + ", userId=" + userId + ", isCND=" + isCND + '}';
    }
    
    
}
