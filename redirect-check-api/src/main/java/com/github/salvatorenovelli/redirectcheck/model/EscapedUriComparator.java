package com.github.salvatorenovelli.redirectcheck.model;

import java.net.URI;
import java.net.URISyntaxException;

class EscapedUriComparator {
    static boolean compare(String uri1, String uri2) {
        try {
            String uri1String = new URI(uri1).toASCIIString();
            String uri2String = new URI(uri2).toASCIIString();
            return uri1String.equals(uri2String);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
