package com.scout24.utils;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class CacheUtils {
    private static Cache ch;

    public final static String urls = "searched-urls";

    private static final Logger log = Logger.getLogger(CacheUtils.class);

    private static CacheUtils utils = new CacheUtils();

    public static CacheUtils getInstance() {
        return utils;
    }

    private CacheUtils() {
        CacheManager cm = CacheManager.newInstance();
        try {
            ch = cm.getCache(urls);
        } catch (Exception e) {
            log.error("Error initializing cache", e);
        }
    }

    public void addElement(String key, Set<String> value) {
        ch.put(new Element(key, value));
    }

    public Set<String> getValue(Object key) {
        if (ch.isKeyInCache(key)) {
            Element elm = ch.get(key);
            return (elm == null) ? new HashSet<>() : (Set<String>) elm.getObjectValue();
        }
        return new HashSet<>();
    }
}
