package com.lifepharmacy.application.model.pcr.appointmentdetailnew

import com.google.gson.annotations.SerializedName

data class Header(

  @SerializedName("host") val host: List<String>,
  @SerializedName("uuid") val uuid: List<String>,
  @SerializedName("build") val build: List<Int>,
  @SerializedName("accept") val accept: List<String>,
  @SerializedName("cf-ray") val cfray: List<String>,
  @SerializedName("source") val source: List<String>,
  @SerializedName("country") val country: List<String>,
  @SerializedName("version") val version: List<String>,
  @SerializedName("cdn-loop") val cdnloop: List<String>,
  @SerializedName("language") val language: List<String>,
  @SerializedName("latitude") val latitude: List<Double>,
  @SerializedName("longitude") val longitude: List<Double>,
  @SerializedName("campaignid") val campaignid: List<String>,
  @SerializedName("cf-visitor") val cfvisitor: List<String>,
  @SerializedName("user-agent") val useragent: List<String>,
  @SerializedName("cf-ipcountry") val cfipcountry: List<String>,
  @SerializedName("content-type") val contenttype: List<String>,
  @SerializedName("authorization") val authorization: List<String>,
  @SerializedName("content-length") val contentlength: List<Int>,
  @SerializedName("accept-encoding") val acceptencoding: List<String>,
  @SerializedName("x-forwarded-for") val xforwardedfor: List<String>,
  @SerializedName("cf-connecting-ip") val cfconnectingip: List<String>,
  @SerializedName("x-forwarded-proto") val xforwardedproto: List<String>
)