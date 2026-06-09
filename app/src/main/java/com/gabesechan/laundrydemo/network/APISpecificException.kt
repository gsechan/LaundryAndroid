package com.gabesechan.laundrydemo.network

class APISpecificException(errors: List<String>): RuntimeException(errors.joinToString("\n")) {
}