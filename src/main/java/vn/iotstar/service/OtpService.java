package vn.iotstar.service;

public interface OtpService { void send(String email, String purpose); boolean verify(String email, String purpose, String code); }
