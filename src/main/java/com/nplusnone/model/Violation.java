package com.nplusnone.model;

public record Violation(String fileName, int lineNumber, String message) {}