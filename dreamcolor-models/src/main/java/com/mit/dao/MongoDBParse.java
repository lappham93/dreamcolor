package com.mit.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

public abstract class MongoDBParse<T> {

	public List<T> parseList(FindIterable<Document> results) {
		List<T> data = new LinkedList<T>();
		Document doc = null;
		MongoCursor<Document> cursor = results.iterator();
		while(cursor != null && cursor.hasNext() && (doc = cursor.next()) != null) {
			T item = parseObject(doc);
			data.add(item);
		}

		return data;
	}

	public List<T> parseList(List<Document> results) {
		List<T> data = new LinkedList<T>();
		if (results != null) {
			Document doc = null;
			Iterator<Document> cursor = results.iterator();
			while(cursor != null && cursor.hasNext() && (doc = cursor.next()) != null) {
				T item = parseObject(doc);
				data.add(item);
			}
		}

		return data;
	}

	public abstract T parseObject(Document doc);

	public abstract Document toDocument(T obj);

	public Document buildInsertTime(Map<String, Object> map) {
		Document document = new Document(map);
		document.append("createTime", new Date());
		document.append("updateTime", new Date());
		return document;
	}

	public Document buildUpdateTime(Map<String, Object> map) {
		Document document = new Document(map);
		document.append("$currentDate", new Document("updateTime", true));
		return document;
	}
}
