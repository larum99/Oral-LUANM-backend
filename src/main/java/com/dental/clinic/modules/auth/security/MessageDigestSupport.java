package com.dental.clinic.modules.auth.security;

import java.security.MessageDigest;

final class MessageDigestSupport {
    private MessageDigestSupport() {}

    static boolean equals(byte[] left, byte[] right) {
        return MessageDigest.isEqual(left, right);
    }
}
