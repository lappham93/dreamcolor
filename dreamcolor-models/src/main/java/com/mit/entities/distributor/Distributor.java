package com.mit.entities.distributor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mit.utils.LinkBuilder;

public class Distributor {
	private static final int ACTIVE = 1;
	
	private int id;
	private String name;
	private String contactName;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String country;
	private String countryCode;
	private String zipCode;
	private String phone;
	private long photo;
	private String webLink;
	private int status;
	private long createTime;
	private long updateTime;
	
	public Distributor(int id, String name, String contactName,
			String addressLine1, String addressLine2, String city,
			String state, String country, String zipCode, String phone, long photo,
			String webLink, String countryCode, int status, long createTime, long updateTime) {
		super();
		this.id = id;
		this.name = name;
		this.contactName = contactName;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zipCode = zipCode;
		this.phone = phone;
		this.photo = photo;
		this.webLink = webLink;
		this.countryCode = countryCode;
		this.status = status;
		this.createTime = createTime;
		this.updateTime = updateTime;
	}
	
	public Distributor(int id, String name, String contactName,
			String addressLine1, String addressLine2, String city,
			String state, String country, String zipCode, String phone,
			long photo, String webLink, String countryCode) {
		super();
		this.id = id;
		this.name = name;
		this.contactName = contactName;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zipCode = zipCode;
		this.phone = phone;
		this.photo = photo;
		this.webLink = webLink;
		this.countryCode = countryCode;
		this.status = ACTIVE;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@JsonIgnore
	public long getPhoto() {
		return photo;
	}
	
	public void setPhoto(long photo) {
		this.photo = photo;
	}
	
	public String getPhotoLink() {
		return LinkBuilder.buildDistributorPhotoLink(this.photo);
	}
	
	public String getWebLink() {
		return webLink;
	}
	
	public void setWebLink(String webLink) {
		this.webLink = webLink;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	
}

