/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mit.utils;

/**
 *
 * @author truyetnm
 */
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonUtils {
	private final ObjectMapper objMapper = new ObjectMapper();
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

	public JsonUtils() {		
		SimpleModule module = new SimpleModule("SerializerModule");
		module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
		objMapper.registerModule(module);
	}

	public static JsonUtils Instance = new JsonUtils();

	public JsonUtils(Map<SerializationFeature, Boolean> config) {
		for(SerializationFeature feature : config.keySet()) {
			objMapper.configure(feature, config.get(feature));
		}
		objMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		objMapper.setSerializationInclusion(Include.NON_NULL);
	}

	public <T> List<T> getList(String json){
		try {
			return objMapper.readValue(json, new TypeReference<List<T>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public <T> List<T> getList(InputStream inputStream){
		try {
			return objMapper.readValue(inputStream, new TypeReference<List<T>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public Map<String, String> getMap(String json){
		try {
			return objMapper.readValue(json, new TypeReference<Map<String, String>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public Map<String, String> getMap(InputStream inpuStream){
		try {
			return objMapper.readValue(inpuStream, new TypeReference<Map<String, String>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public Map<String, String> getMap(byte[] data){
		try {
			return objMapper.readValue(data, new TypeReference<Map<String, String>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public Map<String, Integer> getMapInteger(String json){
		try {
			return objMapper.readValue(json, new TypeReference<Map<String, Integer>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public Map<String, Object> getMapObject(String json){
		try {
			return objMapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public Map<String, Object> getMapObject(InputStream inpuStream){
		try {
			return objMapper.readValue(inpuStream, new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public Map<String, Object> getMapObject(byte[] data){
		try {
			return objMapper.readValue(data, new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public <T> T getObject(Class<T> type ,String json){
		try {
			return objMapper.readValue(json, type);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public <T> T getObject(Class<T> type ,byte[] data){
		try {
			return objMapper.readValue(data, type);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public <T> T getObject(String json, TypeReference<T> type){
		try {
			return objMapper.readValue(json, type);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	public String toJson(Object obj){
		try {
			return objMapper.writeValueAsString(obj);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public byte[] toByteJson(Object obj){
		try {
			return objMapper.writeValueAsBytes(obj);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static class BigDecimalSerializer extends JsonSerializer<BigDecimal>
	{
	   public void serialize(BigDecimal value, JsonGenerator jgen,SerializerProvider provider)
	       throws IOException, JsonProcessingException
	   {
	       jgen.writeString(BigDecimalUtils.Instance.formatMoney(value));
	   }
	}
}

