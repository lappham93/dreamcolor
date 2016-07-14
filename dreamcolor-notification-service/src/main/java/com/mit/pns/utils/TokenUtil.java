package com.mit.pns.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mit.entities.user.DeviceToken;

public class TokenUtil {
	public static List<DeviceToken> removeDuplicate(List<DeviceToken> tokens) {
		Set<String> tokenSet = new HashSet<String>();
		List<DeviceToken> uniqueTokens = new ArrayList<DeviceToken>(tokens.size());
		for (DeviceToken token: tokens) {
			if (tokenSet.add(token.getDeviceToken())) {
				uniqueTokens.add(token);
			}
		}
		return uniqueTokens;
	}
}
