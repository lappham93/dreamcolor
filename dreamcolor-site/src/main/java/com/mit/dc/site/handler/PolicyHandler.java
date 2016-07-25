/*
 * Copyright 2016 nghiatc.
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

package com.mit.dc.site.handler;

import hapax.TemplateDataDictionary;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nghiatc
 * @since Jul 25, 2016
 */
public class PolicyHandler extends BaseHandler {
    private static Logger logger = LoggerFactory.getLogger(PolicyHandler.class);
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            TemplateDataDictionary dic = getDictionary();
        	
            String svl = req.getServletPath();
			if ("/web/site/termsofuse".equalsIgnoreCase(svl)) {
				dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "TermsOfUse.xtm", req));
			} else if ("/web/site/privacypolicy".equalsIgnoreCase(svl)) {
				dic.setVariable("MAIN_CONTENT", applyTemplate(dic, "PrivacyPolicy.xtm", req));
			}
        	
            print(applyTemplateLayoutMain(dic, req, resp), resp);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
