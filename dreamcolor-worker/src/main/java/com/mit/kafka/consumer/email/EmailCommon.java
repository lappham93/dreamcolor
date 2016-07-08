/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.kafka.consumer.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mit.utils.ConfigUtils;

import hapax.Template;
import hapax.TemplateCache;
import hapax.TemplateDataDictionary;
import hapax.TemplateDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;

/**
 *
 * @author nghiatc
 * @since Jan 27, 2016
 */
public class EmailCommon {
    private static Logger logger = LoggerFactory.getLogger(EmailCommon.class);
    public static final TemplateLoader Loader = TemplateCache.create("./views");
    
    public static String APP_DOMAIN;
	public static String APP_STATIC_DOMAIN;
    
    static {
        APP_DOMAIN = ConfigUtils.getConfig().getString("systeminfo.domain", "");
        APP_STATIC_DOMAIN = ConfigUtils.getConfig().getString("systeminfo.static-domain", "");
    }
    
    public static TemplateDataDictionary getDictionary() {
        TemplateDataDictionary dic = TemplateDictionary.create();
        return dic;
    }
    
    public static String applyTemplate(TemplateDataDictionary dic, String tplName){
        try {
            Template template = Loader.getTemplate(tplName);
            //dic.setVariable("domain", Configuration.APP_DOMAIN);
            //dic.setVariable("static-domain", Configuration.APP_STATIC_DOMAIN);
            
            if (template != null) {
                return template.renderToString(dic);
            }
        } catch (TemplateException ex) {
            logger.error("applyTemplate: " + ex);
        }
        return "";
    }
    
}
